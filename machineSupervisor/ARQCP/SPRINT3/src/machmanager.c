#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>    // For error handling

#include "machine.h"
#include "machmanager.h"
#include "operation.h"
#include "utils.h"

void create_machmanager(MachManager *machmanager, Machine *machines, int machine_count, int machine_capacity) {
    machmanager->machines = machines;
    machmanager->machine_count = machine_count;
    machmanager->machine_capacity = machine_capacity;
}

// Free the allocated memory for the machine's buffer
void free_machine(Machine *m) {
    if (m->buffer) {
        free(m->buffer);  // Free the dynamically allocated buffer
        m->buffer = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->operations) {
        free(m->operations); // Free the dynamically allocated operations array
        m->operations = NULL; // Reset pointer to avoid dangling reference
    }
}

int setup_machines_from_file(const char *filename, MachManager *machmanager) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        perror("Error opening file");
        return -1;
    }

    Machine *machines = machmanager->machines;
    int *machine_count = &machmanager->machine_count;
    int *machine_capacity = &machmanager->machine_capacity;

    // Free existing machines if any
    if (*machine_count > 0) {
        for (int i = 0; i < *machine_count; i++) {
            free_machine(&machines[i]); // Free the buffer for each machine
        }
        free(machines);  // Free the array itself
        machines = NULL; // Reset pointer to avoid dangling references
        *machine_count = 0;
        *machine_capacity = 0;
    }

    char header[256];
    if (!fgets(header, sizeof(header), file)) {
        fprintf(stderr, "Error reading file header\n");
        fclose(file);
        return -1;
    }

    char line[256];
    if (!fgets(line, sizeof(line), file)) {
        fprintf(stderr, "No data lines found in the file\n");
        fclose(file);
        return -1;
    }

    do {
        Machine new_machine = {0}; // Ensure it's zero-initialized
        if (sscanf(line, "%9[^,],%19[^,],%f,%f,%f,%f",
               new_machine.id, new_machine.name,
               &new_machine.temperature_min, &new_machine.temperature_max,
               &new_machine.humidity_min, &new_machine.humidity_max) != 6) {
            fprintf(stderr, "Invalid line format: %s", line);
            continue;
        }

        // Definir valores padrão
        new_machine.buffer_size = 100; // Por exemplo
        new_machine.median_window = 10; // Por exemplo

        // Validar os dados
        if (new_machine.temperature_min > new_machine.temperature_max ||
            new_machine.humidity_min > new_machine.humidity_max) {
            fprintf(stderr, "Validation failed for machine %s\n", new_machine.id);
            continue;
        }

        new_machine.head = new_machine.buffer;
        new_machine.tail = new_machine.buffer;

        // Initialize operations array
        new_machine.operation_capacity = 10;
        new_machine.operations = malloc(new_machine.operation_capacity * sizeof(Operation));
        if (!new_machine.operations) {
            perror("Error allocating operations array");
            free(new_machine.buffer);
            fclose(file);
            return -1;
        }
        new_machine.operation_count = 0;

        // Resize machine array if needed
        if (*machine_count >= *machine_capacity) {
            *machine_capacity = (*machine_capacity == 0) ? 2 : (*machine_capacity * 2);
            machines = realloc(machines, *machine_capacity * sizeof(Machine));
            if (!machines) {
                perror("Error resizing machine array");
                free(new_machine.buffer);
                free(new_machine.operations);
                fclose(file);
                return -1;
            }
            machmanager->machines = machines; // Update the MachManager pointer
        }

        // Add the new machine to the array
        machines[*machine_count] = new_machine;
        (*machine_count)++;
    } while (fgets(line, sizeof(line), file));

    fclose(file);
    machmanager->machines = machines;
    return 0;
}

void export_operations_to_csv(Machine *m) {
    if (!m) {
        printf(RED "\nInvalid machine provided.\n" RESET);
        return;
    }
    // Construct the file path using the machine's ID
    char filepath[150];
    snprintf(filepath, sizeof(filepath), "src/data/Machines_OP/%s_Operations.csv", m->id);
    // Open the file for writing
    FILE *file = fopen(filepath, "w");
    if (!file) {
        perror(RED "Error opening file for writing" RESET);
        return;
    }
    // Write CSV header
    fprintf(file, "Number;Designation;Timestamp\n");
    // Write operation details
    for (int i = 0; i < m->operation_count; i++) {
        Operation *op = &m->operations[i];
        char timestamp_str[30];
        strftime(timestamp_str, sizeof(timestamp_str), "%Y-%m-%d %H:%M:%S", localtime(&op->timestamp));
        fprintf(file, "%d;%s;%s\n", op->number, op->designation, timestamp_str);
    }
    fclose(file);
    printf(GREEN "\nOperations exported to %s successfully.\n" RESET, filepath);
}
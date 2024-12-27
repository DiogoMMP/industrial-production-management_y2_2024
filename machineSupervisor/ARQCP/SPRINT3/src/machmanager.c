#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>    // For error handling

#include "machine.h"
#include "machmanager.h"
#include "operation.h"

void create_machmanager(MachManager *machmanager, Machine *machines, int *machine_count, int *machine_capacity){
    machmanager->machines = machines;
    machmanager->machine_count = *machine_count;
    machmanager->machine_capacity = *machine_capacity;
}

int setup_machines_from_file(const char *filename, MachManager *machmanager) {
    FILE *file = fopen(filename, "r");

    Machine *machines = machmanager -> machines;
    int *machine_count = &machmanager -> machine_count;
    int *machine_capacity = &machmanager -> machine_capacity;
    
    if (!file) {
        perror("Error opening file");
        return -1;
    }

    // Free existing machines if any
    if (*machine_count > 0) {
        for (int i = 0; i < *machine_count; i++) {
            free_machine(&machines[i]); // Free the buffer for each machine
        }
        free(machines);  // Free the array itself
        machines = NULL; // Reset pointer to avoid dangling references
        *machine_count = 0;
    }

    char header[256];
    if (!fgets(header, sizeof(header), file)) {
        fprintf(stderr, "Error reading file header\n");
        fclose(file);
        return -1;
    }

    // Check if the file has any lines after the header
    char line[256];
    if (!fgets(line, sizeof(line), file)) {
        fprintf(stderr, "No data lines found in the file\n");
        fclose(file);
        return -1;
    }

    // Process the first line
    do {
        Machine new_machine = {0}; // Ensure it's zero-initialized
        if (sscanf(line, "%9[^,],%19[^,],%f,%f,%f,%f",
                   new_machine.id, new_machine.name,
                   &new_machine.temperature_min, &new_machine.temperature_max,
                   &new_machine.humidity_min, &new_machine.humidity_max) != 6) {
            fprintf(stderr, "Invalid line format: %s", line);
            continue;
        }

        // Validation checks
        if (new_machine.temperature_min > new_machine.temperature_max ||
            new_machine.humidity_min > new_machine.humidity_max) {
            fprintf(stderr, "Validation failed for machine %s\n", new_machine.id);
            continue;
        }

        // Initialize buffer size and median window to 0
        new_machine.buffer_size = 0;
        new_machine.median_window = 0;

        // Initialize operations array
        new_machine.operation_capacity = 31; // Initial capacity for operations
        new_machine.operations = malloc(new_machine.operation_capacity * sizeof(Operation));
        if (!new_machine.operations) {
            perror("Error allocating operations array");
            fclose(file);
            return -1;
        }
        new_machine.operation_count = 0;

        // Add initial operation with default values
        Operation initial_operation = {"none", 0, 0};
        new_machine.operations[new_machine.operation_count++] = initial_operation;

        // Initialize assigned operation with default values
        new_machine.assigned_operation = initial_operation;

        // Check if resizing the machines array is needed
        if (*machine_count >= *machine_capacity) {
            *machine_capacity = (*machine_capacity == 0) ? 2 : *machine_capacity * 2;
            Machine *new_machines = realloc(machines, *machine_capacity * sizeof(Machine));
            if (!new_machines) {
                perror("Error resizing machine array");
                free(new_machine.buffer); // Clean up buffer before returning
                free(new_machine.operations); // Clean up operations array before returning
                fclose(file);
                return -1;
            }
            machines = new_machines;
            free(new_machine.buffer); // Clean up buffer before returning
            free(new_machine.operations); // Clean up operations array before returning
        }

        // Add the new machine to the array
        machines[*machine_count++] = new_machine;
    } while (fgets(line, sizeof(line), file));

    fclose(file);
    return 0;
}
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>    // For error handling

#include "machine.h"
#include "machmanager.h"
#include "operation.h"
#include "utils.h"
#include "assembly_functions.h"
#include "instance.h"

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

void extract_data_machine(const char *str, void *data) {
    int temp_value = 0;
    int hum_value = 0;
    char temp_unit[20] = {0};
    char hum_unit[20] = {0};

    // Define tokens
    char temp_token[] = "TEMP";
    char hum_token[] = "HUM";

    // Extract temperature data
    if (extract_data((char *)str, temp_token, temp_unit, &temp_value) == 1) {
        // Extract humidity data
        if (extract_data((char *)str, hum_token, hum_unit, &hum_value) == 1) {
            snprintf((char *)data, BUFSIZ, "Temperature:%d%s, Humidity:%d%s", temp_value, temp_unit, hum_value, hum_unit);
        } else {
            snprintf((char *)data, BUFSIZ, "Error: Humidity data not found");
        }
    } else {
        snprintf((char *)data, BUFSIZ, "Error: Temperature data not found");
    }
}

void check_for_alerts(Machine *m) {
    //head, aponta para o valor mais recente 
    if (m->head) {
         if (m->head->temperature < m->temperature_min || m->head->temperature > m->temperature_max) {
            printf("ALERT: Machine %s has a temperature outside the range of values! (%.2f°C)\n", m->id, m->head->temperature);
         }
         if (m->head->humidity < m->humidity_min || m->head->humidity > m->humidity_max) {
            printf("ALERT: Machine %s has a humidity outside the range of values! (%.2f%%)\n", m->id, m->head->humidity);
         }
    }
}

Instance* wait_for_instructions_from_ui(void) {
    Instance *instr = malloc(sizeof(Instance));
    if (!instr) {
        perror("Error allocating memory for instruction");
        return NULL;
    }

    return instr;
}

void update_internal_data(Machine *m) {
    // Update the internal data of the machine based on the instruction
    // This function needs to be implemented based on your specific requirements
}

void* get_cmd_from_internal_data() {
    // Generate the command from the internal data
    // This function needs to be implemented based on your specific requirements
    return NULL;
}

void send_cmd_to_machine(char *cmd) {
    // Send the command to the machine
    // This function needs to be implemented based on your specific requirements
}

char* wait_for_data_from_machine() {
    // Wait for data from the machine
    // This function needs to be implemented based on your specific requirements
    return NULL;
}

void update_internal_data_with_new_data(void *data) {
    // Update the internal data with the new data
    // This function needs to be implemented based on your specific requirements
}

void main_loop(MachManager *machmanager) {
    char data[BUFSIZ];
    while (1) {
        // Wait for instructions from UI
        Instance *instr = wait_for_instructions_from_ui();
        if (!instr) {
            perror("Error waiting for instructions from UI");
            continue;
        }

        // Find the machine instance
        Machine *machine = NULL;
        for (int i = 0; i < machmanager->machine_count; i++) {
            if (strcmp(machmanager->machines[i].id, instr->machine_id) == 0) {
                machine = &machmanager->machines[i];
                break;
            }
        }

        if (!machine) {
            fprintf(stderr, "Error: Machine with ID %s not found\n", instr->machine_id);
            free(instr);
            continue;
        }

        // Update internal data based on instruction
        update_internal_data(machine);

        // Get command from internal data
        char cmd[256];
        format_command(instr->state, instr->operation_id, cmd);

        // Send command to machine
        send_cmd_to_machine(cmd);

        // Wait for data from machine
        const char *str = wait_for_data_from_machine();
        if (!str) {
            perror("Error waiting for data from machine");
            free(instr);
            continue;
        }

        // Extract data
        extract_data_machine(str, data);

        // Update internal data based on extracted data
        update_internal_data_with_new_data(data);

        // Check for alerts
        check_for_alerts(machine);

        // Free the instruction structure
        free(instr);
    }
}
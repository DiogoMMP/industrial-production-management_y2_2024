#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
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

void send_cmd_to_machine(char *cmd) {
    if (!cmd) {
        printf(RED "\nError: Invalid command string.\n" RESET);
        return;
    }

    // Send the formatted command to the machine using update_machine
    int update_result = update_machine(cmd);
    if (update_result != 0) {
        printf(RED "\nError: Failed to send the command to the machine." RESET);
    } else {
        printf(GREEN "\nCommand successfully sent to the machine.\n" RESET);
    }
}

Machine* find_machine(MachManager *machmanager, const char* machine_id) {
    trim_trailing_spaces((char*) machine_id);
    // Search the machine by ID
    for (int i = 0; i < machmanager -> machine_count; i++) {
        // Compare the machine ID with the ID provided (using strcmp to compare strings)
        if (strcmp(machmanager -> machines[i].id, machine_id) == 0) {
            return &machmanager -> machines[i];  // returns machine if it exists
        }
    }
    return NULL;  // Returns NULL if the machine is not found
}

void assign_operation_to_machine(Operation *op, Machine *m) {
    // Expand the operation array if necessary
    if (m->operation_count == m->operation_capacity) {
        m->operation_capacity *= 2; // Double the capacity
        m->operations = realloc(m->operations, m->operation_capacity * sizeof(Operation));
        if (!m->operations) {
            fprintf(stderr, RED "Error reallocating memory for operations.\n" RESET);
            exit(EXIT_FAILURE); // Exit on memory allocation failure
        }
    }

    // Add the new operation to the operations array
    m->operations[m->operation_count++] = *op;

    // Update machine's assigned operation
    m->assigned_operation = *op;
    m->state = "OP";
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
        perror(RED "\nError opening file\n" RESET);
        return -1;
    }

    Machine *machines = machmanager->machines;
    int *machine_count = &machmanager->machine_count;
    int *machine_capacity = &machmanager->machine_capacity;

    // Free existing machines if any
    if (*machine_count > 0) {
        for (int i = 0; i < *machine_count; i++) {
            free_machine(&machines[i]); // Free the buffer for each machine
            free(machines[i].id); // Free dynamically allocated id
            free(machines[i].name); // Free dynamically allocated name
        }
        free(machines);  // Free the array itself
        machines = NULL; // Reset pointer to avoid dangling references
        *machine_count = 0;
        *machine_capacity = 0;
    }

    char header[256];
    if (!fgets(header, sizeof(header), file)) {
        fprintf(stderr, RED "\nError reading file header\n" RESET);
        fclose(file);
        return -1;
    }

    char line[256];
    if (!fgets(line, sizeof(line), file)) {
        fprintf(stderr, RED "\nNo data lines found in the file\n" RESET);
        fclose(file);
        return -1;
    }

    do {
        Machine new_machine = {0}; // Ensure it's zero-initialized
        char id_buffer[10], name_buffer[20];

        // Parse the machine data
        if (sscanf(line, "%9[^,],%19[^,],%f,%f,%f,%f",
                   id_buffer, name_buffer,
                   &new_machine.temperature_min, &new_machine.temperature_max,
                   &new_machine.humidity_min, &new_machine.humidity_max) != 6) {
            fprintf(stderr, RED "\nInvalid line format: %s\n" RESET, line);
            continue;
        }

        // Allocate memory for id and name dynamically
        new_machine.id = malloc(strlen(id_buffer) + 1);  // +1 for the null terminator
        new_machine.name = malloc(strlen(name_buffer) + 1);

        if (!new_machine.id || !new_machine.name) {
            fprintf(stderr, RED "\nMemory allocation failed for machine id or name\n" RESET);
            free(new_machine.id);  // Free memory if partially allocated
            free(new_machine.name);
            continue;
        }

        strcpy(new_machine.id, id_buffer);
        strcpy(new_machine.name, name_buffer);

        // Set default values
        new_machine.buffer_size = 100; // For example
        new_machine.median_window = 10; // For example

        // Validate the data
        if (new_machine.temperature_min > new_machine.temperature_max ||
            new_machine.humidity_min > new_machine.humidity_max) {
            fprintf(stderr, RED "\nValidation failed for machine %s\n" RESET, new_machine.id);
            free(new_machine.id);
            free(new_machine.name);
            continue;
        }

        new_machine.head = new_machine.buffer;
        new_machine.tail = new_machine.buffer;

        // Initialize operations array
        new_machine.operation_capacity = 10;
        new_machine.operations = malloc(new_machine.operation_capacity * sizeof(Operation));
        if (!new_machine.operations) {
            perror(RED "\nError allocating operations array\n" RESET);
            free(new_machine.buffer);
            free(new_machine.id);
            free(new_machine.name);
            fclose(file);
            return -1;
        }
        new_machine.operation_count = 0;

        // Resize machine array if needed
        if (*machine_count >= *machine_capacity) {
            *machine_capacity = (*machine_capacity == 0) ? 2 : (*machine_capacity * 2);
            machines = realloc(machines, *machine_capacity * sizeof(Machine));
            if (!machines) {
                perror(RED "\nError resizing machine array\n" RESET);
                free(new_machine.buffer);
                free(new_machine.operations);
                free(new_machine.id);
                free(new_machine.name);
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
        perror(RED "\nError opening file for writing\n" RESET);
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

void feed_system(const char* filename, MachManager* manager) {
    FILE* file = fopen(filename, "r");
    if (!file) {
        perror(RED "\nError opening the file\n" RESET);
        return;
    }

    char line[256];
    int first_line = 1; // Ignore the header line.
    int completed_all_operations = 1; // Flag to track if all operations were completed.

    while (fgets(line, sizeof(line), file)) {
        if (first_line) {
            first_line = 0;
            continue; // Ignore the header line.
        }

        int operation_number = 0, time_duration = 0;
        char *operation_description = NULL, *machine_id = NULL;

        // Allocate dynamic memory for operation_description and machine_id
        operation_description = (char*)malloc(100 * sizeof(char)); // Assuming max 100 chars
        machine_id = (char*)malloc(20 * sizeof(char));             // Assuming max 20 chars

        if (!operation_description || !machine_id) {
            fprintf(stderr, RED "Memory allocation error.\n" RESET);
            free(operation_description);
            free(machine_id);
            continue;
        }

        // Parse the line
        if (sscanf(line, "%d;%*d;%*d - Operation: %99[^-] - Machine: %19[^-] - Item: %*[^-] - Time: %d", 
                   &operation_number, operation_description, machine_id, &time_duration) != 4) {
            fprintf(stderr, RED "\nInvalid line or parsing error: %s\n" RESET, line);
            free(operation_description);
            free(machine_id);
            continue;
        }

        // Check if the machine already exists
        Machine *machine = find_machine(manager, machine_id);
        if (machine == NULL) {
            fprintf(stderr, RED "\nError: Machine '%s' not found. Skipping operation.\n" RESET, machine_id);
            free(operation_description);
            free(machine_id);
            continue; // Skip to the next line
        }

        // Add timestamp to operation
        Operation new_operation;
        new_operation.number = operation_number;
        new_operation.designation = operation_description;
        new_operation.id = machine_id;
        new_operation.time_duration = time_duration;
        new_operation.timestamp = time(NULL);

        // Process the operation
        printf(BOLD BLUE "\nProcessing operation for machine %s:\n" RESET, machine_id);
        printf("  - Operation %d: %s\n", operation_number, operation_description);
        printf("  - Estimated time: %d seconds\n\n", time_duration);

        // Simulate updating the machine

        // Dynamically allocate memory for the command
        char* cmd = (char*)malloc(256 * sizeof(char)); // Allocate space for the formatted command
        if (!cmd) {
            printf(RED "\nError: Memory allocation failed for cmd.\n" RESET);
            return; // Handle the error appropriately, exit the function if necessary
        }

        // Format the command using format_command
        if (format_command("OP", operation_number, cmd) != 1) {
            printf(RED "\nError: Failed to format the command.\n" RESET);
            free(cmd); // Free allocated memory in case of error
            return;
        }

        // Send the command to the machine
        send_cmd_to_machine(cmd);

        // Assign Operation to Machine
        assign_operation_to_machine(&new_operation, machine);

        sleep(2); // Simulate operation execution

        // Format the command using format_command
        if (format_command("ON", operation_number, cmd) != 1) {
            printf(RED "\nError: Failed to format the command.\n" RESET);
            free(cmd); // Free allocated memory in case of error
            return;
        }

        // Send the command to the machine
        send_cmd_to_machine(cmd);
        machine -> state = "ON";

        printf(GREEN "\nOperation %d completed for machine %s.\n" RESET, operation_number, machine_id);

        // Ask user if they want to continue
        char continue_response;
        printf(BOLD "\nDo you want to continue to the next operation? (Y/N): " RESET);
        scanf(" %c", &continue_response); // Adding a space before %c to consume any leftover newline character

        // If the user enters 'n' or 'N', exit the loop
        if (continue_response == 'N' || continue_response == 'n') {
            printf(GREEN "\nExiting operation feed.\n" RESET);
            completed_all_operations = 0; // Mark that not all operations were completed
            break;
        }

        // Free dynamically allocated memory
        free(operation_description);
        free(machine_id);
    }

    fclose(file);

    // Only display the "All operations completed" message if the user didn't exit early
    if (completed_all_operations) {
        printf(GREEN "\nAll operations from the file %s have been processed.\n" RESET, filename);
    }
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
    // head, points to the most recent value 
    if (m->head) {
         if (m->head->temperature < m->temperature_min || m->head->temperature > m->temperature_max) {
            printf(RED "\nALERT: Machine %s has a temperature outside the range of values! (%.2f°C)\n" RESET, m->id, m->head->temperature);
         }
         if (m->head->humidity < m->humidity_min || m->head->humidity > m->humidity_max) {
            printf(RED "\nALERT: Machine %s has a humidity outside the range of values! (%.2f%%)\n" RESET, m->id, m->head->humidity);
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


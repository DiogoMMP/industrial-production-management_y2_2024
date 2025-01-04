#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>    // For error handling
#include <pthread.h>  // Include pthread for threading
#include <time.h>

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

void free_machine(Machine *m) {
    if (m->buffer) {
        free(m->buffer);  // Free the dynamically allocated buffer
        m->buffer = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->operations) {
        free(m->operations); // Free the dynamically allocated operations array
        m->operations = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->exec_operation) {
        free(m->exec_operation); // Free the dynamically allocated executed operations array
        m->exec_operation = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->moving_median) {
        free(m->moving_median); // Free the dynamically allocated moving median array
        m->moving_median = NULL; // Reset pointer to avoid dangling reference
    }
    // Ensure id and name are freed only once
    if (m->id) {
        free(m->id); // Free dynamically allocated id
        m->id = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->name) {
        free(m->name); // Free dynamically allocated name
        m->name = NULL; // Reset pointer to avoid dangling reference
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
        new_machine.state = strdup("OFF"); // Set default state to "OFF"
        new_machine.buffer_size = 100; // For example
        new_machine.buffer_count = 0;  // Initialize buffer count
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

        // Initialize executed operations array
        new_machine.exec_operation_capacity = 10;
        new_machine.exec_operation = malloc(new_machine.exec_operation_capacity * sizeof(Operation));
        if (!new_machine.exec_operation) {
            perror(RED "\nError allocating executed operations array\n" RESET);
            free(new_machine.buffer);
            free(new_machine.operations);
            free(new_machine.id);
            free(new_machine.name);
            fclose(file);
            return -1;
        }
        new_machine.exec_operation_count = 0;

        // Initialize moving median array
        new_machine.moving_median_capacity = new_machine.median_window;
        new_machine.moving_median = malloc(new_machine.moving_median_capacity * sizeof(buffer_data));
        if (!new_machine.moving_median) {
            perror(RED "\nError allocating moving median array\n" RESET);
            free(new_machine.buffer);
            free(new_machine.operations);
            free(new_machine.exec_operation);
            free(new_machine.id);
            free(new_machine.name);
            fclose(file);
            return -1;
        }
        new_machine.moving_median_count = 0;

        // Resize machine array if needed
        if (*machine_count >= *machine_capacity) {
            *machine_capacity = (*machine_capacity == 0) ? 2 : (*machine_capacity * 2);
            Machine *new_machines = realloc(machines, *machine_capacity * sizeof(Machine));
            if (!new_machines) {
                perror(RED "\nError resizing machine array\n" RESET);
                free(new_machine.buffer);
                free(new_machine.operations);
                free(new_machine.exec_operation);
                free(new_machine.moving_median);
                free(new_machine.id);
                free(new_machine.name);
                fclose(file);
                return -1;
            }
            machines = new_machines;
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

    // Write executed operation details
    for (int i = 0; i < m->exec_operation_count; i++) {
        Operation *op = &m->exec_operation[i];
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

    int operation_id_counter = 1; // Counter for operation ID (1 to 31)

    while (fgets(line, sizeof(line), file)) {
        if (first_line) {
            first_line = 0;
            continue; // Ignore the header line.
        }

        int time_duration = 0;
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
        if (sscanf(line, "%*d;%*[^;];%*d - Operation: %99[^-] - Machine: %19[^-] - Item: %*[^-] - Time: %d - Quantity: %*f", 
           operation_description, machine_id, &time_duration) != 3) {
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

        // Check if the operation already exists in the machine's operations array
        int operation_id = -1;
        for (int i = 0; i < machine->operation_count; i++) {
            if (strcmp(machine->operations[i].designation, operation_description) == 0) {
                operation_id = machine->operations[i].number; // Reuse the existing operation ID
                break;
            }
        }

        // If operation does not exist, assign a new sequential ID
        if (operation_id == -1) {
            operation_id = operation_id_counter++;
            if (operation_id_counter > 31) {
                operation_id_counter = 1; // Reset to 1 after 31
            }
        }

        // Process the operation (always show information)
        printf(BOLD BLUE "\nProcessing operation for machine %s:\n" RESET, machine_id);
        printf("  - Operation ID: %d\n", operation_id);
        printf("  - Description: %s\n", operation_description);
        printf("  - Estimated time: %d seconds\n\n", time_duration);

        // Dynamically allocate memory for the command
        char* cmd = (char*)malloc(256 * sizeof(char)); // Allocate space for the formatted command
        if (!cmd) {
            printf(RED "\nError: Memory allocation failed for cmd.\n" RESET);
            free(operation_description);
            free(machine_id);
            return; // Handle the error appropriately, exit the function if necessary
        }

        // Format the command using format_command
        if (format_command("OP", operation_id, cmd) != 1) {
            printf(RED "\nError: Failed to format the command.\n" RESET);
            free(cmd); // Free allocated memory in case of error
            free(operation_description);
            free(machine_id);
            return;
        }

        // Send the command to the machine
        send_cmd_to_machine(cmd);

        // Create a new operation structure
        Operation new_operation;
        new_operation.number = operation_id;  // Use the assigned or reused ID
        new_operation.designation = operation_description;
        new_operation.id = machine_id;
        new_operation.time_duration = time_duration;
        new_operation.timestamp = time(NULL);

        // Assign operation to machine
        assign_operation_to_machine(&new_operation, machine);

        sleep(2); // Simulate operation execution

        // Format the command using format_command
        if (format_command("ON", operation_id, cmd) != 1) {
            printf(RED "\nError: Failed to format the command.\n" RESET);
            free(cmd); // Free allocated memory in case of error
            free(operation_description);
            free(machine_id);
            return;
        }

        // Send the command to the machine
        send_cmd_to_machine(cmd);
        machine->state = "ON";

        printf(GREEN "\nOperation %d completed for machine %s.\n" RESET, operation_id, machine_id);

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
        free(cmd); // Free the command memory
    }

    fclose(file);

    // Only display the "All operations completed" message if the user didn't exit early
    if (completed_all_operations) {
        printf(GREEN "\nAll operations from the file %s have been processed.\n" RESET, filename);
    }
}


void extract_data_machine(const char *str, buffer_data *data) {
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
            data->temperature = temp_value;
            data->humidity = hum_value;
        } else {
            fprintf(stderr, "Error: Humidity data not found\n");
        }
    } else {
        fprintf(stderr, "Error: Temperature data not found\n");
    }
}

void check_for_alerts(Machine *m) {
    // Check the most recent value (head)
    if (m->head) {
        if (m->head->temperature < m->temperature_min || m->head->temperature > m->temperature_max) {
            printf(RED "\nALERT: Machine %s has a temperature outside the range of values! (%.2f°C)\n" RESET, m->id, m->head->temperature);
        }
        if (m->head->humidity < m->humidity_min || m->head->humidity > m->humidity_max) {
            printf(RED "\nALERT: Machine %s has a humidity outside the range of values! (%.2f%%)\n" RESET, m->id, m->head->humidity);
        }
    }

    // Check the moving median values
    if (m->moving_median_count > 0) {
        buffer_data median = m->moving_median[m->moving_median_count - 1];
        if (median.temperature < m->temperature_min || median.temperature > m->temperature_max) {
            printf(RED "\nALERT: Machine %s has a moving median temperature outside the range of values! (%.2f°C)\n" RESET, m->id, median.temperature);
        }
        if (median.humidity < m->humidity_min || median.humidity > m->humidity_max) {
            printf(RED "\nALERT: Machine %s has a moving median humidity outside the range of values! (%.2f%%)\n" RESET, m->id, median.humidity);
        }
    }
}

Instance* wait_for_instructions_from_ui(MachManager *machmanager) {
    Instance *instr = malloc(sizeof(Instance));
    if (!instr) {
        perror("Error allocating memory for instruction");
        return NULL;
    }

    Machine machine = machmanager->machines[0]; // Get the machine from the manager
    instr->machine_id = strdup(machine.id); // Duplicate the machine ID string
    instr->state = strdup(machine.state);   // Duplicate the machine state string
    instr->operation_id = machine.assigned_operation.number; // Use the assigned operation number

    return instr;
}

void update_internal_data(Machine *m, buffer_data *new_data) {
    // Add the new data to the circular buffer
    if (m->buffer_count == 0) {
        // Initialize the buffer
        m->buffer = new_data;
        m->head = new_data;
        m->tail = new_data;
        m->buffer_count = 1;
    } else if (m->buffer_count < m->buffer_capacity) {
        // Add the new data to the buffer
        m->head = (m->head + 1 == m->buffer + m->buffer_capacity) ? m->buffer : m->head + 1;
        *m->head = *new_data;
        m->buffer_count++;
    } else {
        // Buffer is full, overwrite the oldest data
        *m->tail = *new_data;
        m->tail = (m->tail + 1 == m->buffer + m->buffer_capacity) ? m->buffer : m->tail + 1;
        m->head = (m->head + 1 == m->buffer + m->buffer_capacity) ? m->buffer : m->head + 1;
    }

    // Check if the moving median can start being used
    if (m->buffer_count >= m->median_window) {
        // Initialize the moving median array if not already initialized
        if (m->moving_median == NULL) {
            m->moving_median = malloc(m->median_window * sizeof(buffer_data));
            if (!m->moving_median) {
                perror("Error allocating memory for moving median");
                return;
            }
            m->moving_median_capacity = m->median_window;
            m->moving_median_count = 0;
        }

        // Add the new data to the moving median array
        if (m->moving_median_count < m->moving_median_capacity) {
            m->moving_median[m->moving_median_count++] = *new_data;
        } else {
            // Shift the array to make room for the new data
            memmove(m->moving_median, m->moving_median + 1, (m->moving_median_capacity - 1) * sizeof(buffer_data));
            m->moving_median[m->moving_median_capacity - 1] = *new_data;
        }
    }
}

void enqueue_buffer_data(Machine *m, buffer_data *new_data) {
    // Convert buffer_data to an integer array for the assembly function
    int buffer[m->buffer_capacity];
    int head = 0;
    int tail = 0;
    int value;

    buffer[head] = new_data->temperature; // Assuming temperature is the value to enqueue

    // Check if the buffer is full by attempting to enqueue the value
    int result = enqueue_value(buffer, m->buffer_capacity, &tail, &head, new_data->temperature);

    if (result == 1) {
        // Buffer was full, so we need to dequeue the oldest element
        dequeue_value(buffer, m->buffer_capacity, &tail, &head, &value);
        // Try to enqueue the value again
        result = enqueue_value(buffer, m->buffer_capacity, &tail, &head, new_data->temperature);
        if (result != 0) {
            fprintf(stderr, "Error enqueuing value after dequeuing\n");
            return;
        }
    } else if (result != 0) {
        // Print an error message if there was a failure enqueuing the value
        fprintf(stderr, "Error enqueuing value\n");
        return;
    }

    // Update the buffer count
    m->buffer_count = (m->buffer_count < m->buffer_capacity) ? m->buffer_count + 1 : m->buffer_capacity;
}

void* get_cmd_from_internal_data(const char* state, int operation_id) {
    // Allocate memory for the command
    char* cmd = malloc(256 * sizeof(char));
    if (!cmd) {
        perror("Error allocating memory for command");
        return NULL;
    }

    // Format the command using the provided state and operation ID
    if (format_command((char*)state, operation_id, cmd) != 1) {
        fprintf(stderr, "Error formatting command\n");
        free(cmd);
        return NULL;
    }

    return cmd;
}


#include <stdlib.h>
#include <time.h>

char* wait_for_data_from_machine(Instance *instr, Machine *machine) {
    // Allocate memory for the data string
    char *data = malloc(256 * sizeof(char));
    if (!data) {
        perror("Error allocating memory for data");
        return NULL;
    }

    // Simulate reading temperature and humidity values
    srand(time(NULL)); // Seed the random number generator

    // Randomly add or subtract 0.1 to/from the last temperature
    float temp_change = (rand() % 2 == 0) ? 0.1 : -0.1;
    instr->last_temperature += temp_change;

    // Randomly add or subtract 1 to/from the last humidity
    int hum_change = (rand() % 2 == 0) ? 1 : -1;
    instr->last_humidity += hum_change;

    // Format the data string
    snprintf(data, 256, "TEMP&unit:celsius&value:%.1f#HUM&unit:percentage&value:%.1f",
             instr->last_temperature, instr->last_humidity);

    return data;
}


void calculate_moving_median(Machine *machine) {
    int count = 0;
    buffer_data *current = machine->tail;

    // Collect the most recent values up to the median window size
    while (count < machine->median_window && current != machine->head) {
        machine->moving_median[count++] = *current;
        current = (current + 1 == machine->buffer + machine->buffer_size) ? machine->buffer : current + 1;
    }
    machine->moving_median[count++] = *machine->head;

    // Calculate the median for temperature
    int temperature_values[count];
    for (int i = 0; i < count; i++) {
        temperature_values[i] = machine->moving_median[i].temperature;
    }
    int median_temperature;
    median(temperature_values, count, &median_temperature);

    // Calculate the median for humidity
    int humidity_values[count];
    for (int i = 0; i < count; i++) {
        humidity_values[i] = machine->moving_median[i].humidity;
    }
    int median_humidity;
    median(humidity_values, count, &median_humidity);

    // Update the machine's state with the new median values
    snprintf(machine->state, 256, "TEMP: %d, HUM: %d", median_temperature, median_humidity);
}

void update_internal_data_with_new_data(MachManager *machmanager, buffer_data *data) {
    // Get the machine instance from the MachManager
    Machine *machine = &machmanager->machines[0];

    // Update the machine's buffer with the new data
    update_internal_data(machine, data);

    // Update the moving median array
    if (machine->buffer_count >= machine->median_window) {
        calculate_moving_median(machine);
    }
}


void* main_loop_thread(void* arg) {
    Machine *machine = (Machine*)arg;
    MachManager machmanager;
    create_machmanager(&machmanager, machine, 1, 1); // Initialize MachManager with the single machine
    machmanager.running = 1; // Initialize the running flag
    main_loop(&machmanager);
    return NULL;
}

void start_main_loop(Machine *machine) {
    pthread_t thread_id;
    if (pthread_create(&thread_id, NULL, main_loop_thread, (void*)machine) != 0) {
        perror("Error creating thread for main loop");
        return;
    }
    printf(BOLD "\nMachine Starting...\n" RESET);
}

void stop_program(MachManager *machmanager) {
    printf(BOLD "\nStopping the machine...\n" RESET);
    machmanager->running = 0;
}

void main_loop(MachManager *machmanager) {
    buffer_data data;
    while (machmanager->running) {
        // Wait for instructions from UI
        Instance *instr = wait_for_instructions_from_ui(machmanager);
        if (!instr) {
            perror("Error waiting for instructions from UI");
            continue;
        }

        Machine *machine = &machmanager->machines[0]; // Get the machine from the manager
        // Update internal data based on instruction
        buffer_data new_data = {0};
        update_internal_data(machine, &new_data);

        // Get command from internal data
        char* cmd = get_cmd_from_internal_data(instr->state, instr->operation_id);
        if (!cmd) {
            perror("Error getting command from internal data");
            free(instr);
            continue;
        }

        // Send command to machine
        send_cmd_to_machine(cmd);
        free(cmd);

        // Wait for data from machine
        const char *str = wait_for_data_from_machine(instr, machine);
        if (!str) {
            perror("Error waiting for data from machine");
            free(instr);
            continue;
        }

        // Extract data
        extract_data_machine(str, &data);

        // Update internal data based on extracted data
        update_internal_data_with_new_data(machmanager, &data);

        // Check for alerts
        check_for_alerts(machine);

        // Free the instruction structure
        free(instr->machine_id);
        free(instr->state);
        free(instr);
    }
}

void add_machine(MachManager *machManager, const char *id, const char *name, float temp_min, float temp_max, float hum_min, float hum_max) {
    Machine *machines = machManager->machines;
    int machine_count = machManager->machine_count;
    int machine_capacity = machManager->machine_capacity;
    if (machine_count >= machine_capacity) {
        machine_capacity = (machine_capacity == 0) ? 2 : (machine_capacity * 2);
        Machine *new_machines = realloc(machines, machine_capacity * sizeof(Machine));
        if (!new_machines) {
            perror(RED "\nError reallocating memory for machines.\n" RESET);
            return;
        }
        machines = new_machines;
        machManager->machines = machines;
        machManager->machine_capacity = machine_capacity;
    }
    machines[machine_count].id = strdup(id);
    machines[machine_count].name = strdup(name);
    machines[machine_count].state = strdup("OFF"); // Initially not operating
    machines[machine_count].temperature_min = temp_min;
    machines[machine_count].temperature_max = temp_max;
    machines[machine_count].humidity_min = hum_min;
    machines[machine_count].humidity_max = hum_max;
    machines[machine_count].buffer_size = 100; // For example
    machines[machine_count].buffer_count = 0;  // Initialize buffer count
    machines[machine_count].median_window = 10; // For example
    machines[machine_count].operation_capacity = 10;
    machines[machine_count].operations = malloc(machines[machine_count].operation_capacity * sizeof(Operation));
    machines[machine_count].operation_count = 0;
    machines[machine_count].exec_operation_capacity = 10;
    machines[machine_count].exec_operation = malloc(machines[machine_count].exec_operation_capacity * sizeof(Operation));
    machines[machine_count].exec_operation_count = 0;
    machines[machine_count].moving_median_capacity = machines[machine_count].median_window;
    machines[machine_count].moving_median = malloc(machines[machine_count].moving_median_capacity * sizeof(buffer_data));
    machines[machine_count].moving_median_count = 0;
    machines[machine_count].buffer = NULL;
    machines[machine_count].head = NULL;
    machines[machine_count].tail = NULL;
    machManager->machines = machines;
    machManager->machine_count++; // Update the machine count in the MachManager structure
    printf("Machine added: %s, %s\n", id, name);
}

void remove_machine(MachManager *machManager, const char *id) {
    Machine *machines = machManager->machines;
    int machine_count = machManager->machine_count;
    for (int i = 0; i < machine_count; i++) {
        if (strcmp(machines[i].id, id) == 0) {
            if (strcmp(machines[i].state, "OFF") == 0) {
                free(machines[i].state);
                free_machine(&machines[i]);
                free(machines[i].id);
                free(machines[i].name);
                free(machines[i].operations);
                free(machines[i].exec_operation);
                free(machines[i].moving_median);
                free(machines[i].buffer);
                free(machines[i].head);
                free(machines[i].tail);
                free(machines[i].moving_median);
                for (int j = i; j < machine_count - 1; j++) {
                    machines[j] = machines[j + 1];
                }
                machManager->machine_count--; // Update the machine count in the MachManager structure
                printf("Machine removed: %s\n", id);
                return;
            } else {
                printf(RED "\nError: Machine is currently operating.\n" RESET);
                return;
            }
        }
    }
    printf("Machine with id %s not found.\n", id);
}

void read_status_machine(MachManager *machManager, const char *id) {
    Machine *machine = find_machine(machManager, id);
    if (machine) {
        printf(BOLD "\nMachine Status:\n" RESET);
        printf("ID: %s\n", machine->id);
        printf("Name: %s\n", machine->name);
        printf("State: %s\n", machine->state);
        printf("Temperature Range: %.2f - %.2f\n", machine->temperature_min, machine->temperature_max);
        printf("Humidity Range: %.2f - %.2f\n", machine->humidity_min, machine->humidity_max);
    } else {
        printf(RED "\nMachine not found.\n" RESET);
    }
}

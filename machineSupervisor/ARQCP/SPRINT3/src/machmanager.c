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

int last_processed_line = 0; // Global variable to track the last processed line

void create_machmanager(MachManager *machmanager, Machine *machines, int machine_count, int machine_capacity) {
    machmanager->machines = machines;
    machmanager->machine_count = machine_count;
    machmanager->machine_capacity = machine_capacity;
}

void free_operation(Operation *op) {
    if (!op) return;

    if (op->designation) {
        free(op->designation);
        op->designation = NULL;
    }

    if (op->id) {
        free(op->id);
        op->id = NULL;
    }
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

void send_cmd_to_machine_No_UI(char *cmd) {
    if (!cmd) {
        return;
    }
    // Send the formatted command to the machine using update_machine
    update_machine(cmd);
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
    // Check if there is enough space in the operation array to store a new one
    if (m->operation_count >= m->operation_capacity) {
        // Resize the operation array if necessary
        m->operation_capacity *= 2;
        m->operations = realloc(m->operations, m->operation_capacity * sizeof(Operation));
        if (!m->operations) {
            fprintf(stderr, "Error: Failed to allocate memory for the operations array.\n");
            return; // Abort the function if the allocation fails
        }
    }

    // Assign the new operation to the operations array
    m->operations[m->operation_count] = *op;
    m->operation_count++;

    // Now assign the machine state to ‘OP’
    if (m->state) {
        free(m->state); // Release the previously allocated state, if necessary
    }
    
    m->state = strdup("OP"); // Update the state to ‘OP’
    
    if (!m->state) {
        fprintf(stderr, "Error: Failed to allocate memory for the state machine.\n");
        return; // Abort if state allocation fails
    }

    free(m->state);
}


void free_machine(Machine *m) {
    if (!m) return;

    if (m->id) {
        free(m->id);
        m->id = NULL;
    }

    if (m->name) {
        free(m->name);
        m->name = NULL;
    }

    if (m->state) {
        free(m->state);
        m->state = NULL;
    }

    // Free the buffer
    if (m->buffer) {
        free(m->buffer);
        m->buffer = NULL;
    }

    // Free the array of moving medians
    if (m->moving_median) {
        free(m->moving_median);
        m->moving_median = NULL;
    }

    // Free the array of operations
    if (m->operations) {
        for (int i = 0; i < m->operation_count; i++) {
            free_operation(&m->operations[i]);
        }
        free(m->operations);
        m->operations = NULL;
    }

    // Release the assigned operation
    free_operation(&m->assigned_operation);

    // Release the array of executed operations
    if (m->exec_operation) {
        for (int i = 0; i < m->exec_operation_count; i++) {
            free_operation(&m->exec_operation[i]);
        }
        free(m->exec_operation);
        m->exec_operation = NULL;
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
        if (sscanf(line, "%9[^,],%19[^,],%d,%d,%d,%d",
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

int find_operation_id_in_all_machines(MachManager *manager, char *operation_description) {
    if (!manager || !operation_description) {
        fprintf(stderr, "Error: Invalid arguments passed to find_operation_id_in_all_machines.\n");
        return -1;
    }

    for (int j = 0; j < manager->machine_count; j++) {
        Machine *machine = &manager->machines[j];

        for (int i = 0; i < machine->operation_count; i++) {
            // Verifique se a operação tem 'designation' e se a alocação foi bem-sucedida
            if (machine->operations[i].designation) {
                if (strstr(machine->operations[i].designation, operation_description)) {
                    return machine->operations[i].number; // Operation found
                }
            } else {
                // Caso 'designation' não esteja alocada corretamente
                fprintf(stderr, "Warning: Operation designation is NULL in machine %d, operation %d.\n", j, i);
            }
        }
    }

    return -1; // Operation not found
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

    int current_line = 0;
    
    while (fgets(line, sizeof(line), file)) {

        current_line++;

        if (first_line) {
            first_line = 0;
            continue; // Ignore the header line.
        }

        // Ignore lines already processed
        if (current_line <= last_processed_line) {
            continue;
        }

        last_processed_line = current_line; // Update the trace

        int time_duration = 0;
        char *operation_description = (char*)malloc(100 * sizeof(char));
        char *machine_id = (char*)malloc(20 * sizeof(char));
        char *cmd = NULL;

        if (!operation_description || !machine_id) {
            fprintf(stderr, RED "Memory allocation error.\n" RESET);
            free(operation_description);
            free(machine_id);
            continue;
        }

        if (sscanf(line, "%*d;%*[^;];%*d - Operation: %99[^-] - Machine: %19[^-] - Item: %*[^-] - Time: %d - Quantity: %*f", 
                   operation_description, machine_id, &time_duration) != 3) {
            fprintf(stderr, RED "\nInvalid line or parsing error: %s\n" RESET, line);
            free(operation_description);
            free(machine_id);
            continue;
        }

        Machine *machine = find_machine(manager, machine_id);
        if (!machine) {
            fprintf(stderr, RED "\nError: Machine '%s' not found. Skipping operation.\n" RESET, machine_id);
            free(operation_description);
            free(machine_id);
            continue;
        }

        int operation_id = find_operation_id_in_all_machines(manager, operation_description);
        if (operation_id == -1) {
            operation_id = operation_id_counter++;
            if (operation_id_counter > 31) operation_id_counter = 1;
        }

        printf(BOLD BLUE "\nProcessing operation for machine %s:\n" RESET, machine_id);
        printf("  - Operation ID: %d\n", operation_id);
        printf("  - Description: %s\n", operation_description);
        printf("  - Estimated time: %d seconds\n\n", time_duration);

        cmd = (char*)malloc(256 * sizeof(char));
        if (!cmd) {
            printf(RED "\nError: Memory allocation failed for cmd.\n" RESET);
            free(operation_description);
            free(machine_id);
            continue;
        }

        if (format_command("OP", operation_id, cmd) != 1) {
            printf(RED "\nError: Failed to format the command.\n" RESET);
            free(cmd);
            free(operation_description);
            free(machine_id);
            continue;
        }

        send_cmd_to_machine(cmd);

        Operation new_operation;
        new_operation.number = operation_id;
        new_operation.designation = strdup(operation_description);
        new_operation.id = strdup(machine_id);
        new_operation.time_duration = time_duration;
        new_operation.timestamp = time(NULL);

        if (!new_operation.designation || !new_operation.id) {
            fprintf(stderr, RED "\nError: Memory allocation failed for operation fields.\n" RESET);
            free(cmd);
            free(operation_description);
            free(machine_id);
            free(new_operation.designation);
            free(new_operation.id);
            continue;
        }

        assign_operation_to_machine(&new_operation, machine);

        sleep(2);

        if (format_command("ON", operation_id, cmd) != 1) {
            printf(RED "\nError: Failed to format the command.\n" RESET);
            free(cmd);
            free(operation_description);
            free(machine_id);
            continue;
        }

        send_cmd_to_machine(cmd);
        machine->state = strdup("ON");

        printf(GREEN "\nOperation %d completed for machine %s.\n" RESET, operation_id, machine_id);

        char continue_response;
        printf(BOLD "\nDo you want to continue to the next operation? (Y/N): " RESET);
        scanf(" %c", &continue_response);

        if (continue_response == 'N' || continue_response == 'n') {
            printf(GREEN "\nExiting operation feed.\n" RESET);
            free(cmd);
            free(operation_description);
            free(machine_id);
            break;
        }

        free(cmd);
        free(operation_description);
        free(machine_id);
        
        last_processed_line = current_line;
    }

    fclose(file);

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
            printf(RED "\nALERT: Machine %s has a temperature outside the range of values! (%d°C)\n It should be between (%d°C - %d°C)\n" RESET, m->id, m->head->temperature, m->temperature_min, m->temperature_max);
        }
        if (m->head->humidity < m->humidity_min || m->head->humidity > m->humidity_max) {
            printf(RED "\nALERT: Machine %s has a humidity outside the range of values! (%d%%)\n It should be between (%d%% - %d%%)\n" RESET, m->id, m->head->humidity, m->humidity_min, m->humidity_max);
        }
    }

    // Check the moving median values
    if (m->moving_median_count > 0) {
        buffer_data median = m->moving_median[m->moving_median_count - 1];
        if (median.temperature < m->temperature_min || median.temperature > m->temperature_max) {
            printf(RED "\nALERT: Machine %s has a moving median temperature outside the range of values! (%d°C)\n It should be between (%d°C - %d°C)\n" RESET, m->id, median.temperature, m->temperature_min, m->temperature_max);
        }
        if (median.humidity < m->humidity_min || median.humidity > m->humidity_max) {
            printf(RED "\nALERT: Machine %s has a moving median humidity outside the range of values! (%d%%)\n It should be between (%d%% - %d%%)\n" RESET, m->id, median.humidity, m->humidity_min, m->humidity_max);
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
    instr->last_temperature = machine.temperature_min; // Initialize last temperature
    instr->last_humidity = machine.humidity_min; // Initialize last humidity

    return instr;
}

void update_internal_data(Machine *m, buffer_data *new_data) {
    if (!m->buffer) {
        m->buffer = malloc(m->buffer_size * sizeof(buffer_data));
        if (!m->buffer) {
            perror("Error allocating memory for buffer");
            return;
        }
        m->head = m->buffer;
        m->tail = m->buffer;
        m->buffer_count = 0;
    }

    // Add the new data to the circular buffer
    if (m->buffer_count < m->buffer_size) {
        m->head = (m->head + 1 == m->buffer + m->buffer_size) ? m->buffer : m->head + 1;
        *m->head = *new_data;
        m->buffer_count++;
    } else {
        *m->tail = *new_data;
        m->tail = (m->tail + 1 == m->buffer + m->buffer_size) ? m->buffer : m->tail + 1;
        m->head = (m->head + 1 == m->buffer + m->buffer_size) ? m->buffer : m->head + 1;
    }

    // Check if the moving median can start being used
    if (m->buffer_count >= m->median_window) {
        if (!m->moving_median) {
            m->moving_median = malloc(m->median_window * sizeof(buffer_data));
            if (!m->moving_median) {
                perror("Error allocating memory for moving median");
                return;
            }
            m->moving_median_capacity = m->median_window;
            m->moving_median_count = 0;
        }

        if (m->moving_median_count < m->moving_median_capacity) {
            m->moving_median[m->moving_median_count++] = *new_data;
        } else {
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

char* get_cmd_from_internal_data(const char* state, int operation_id) {
    char* cmd = malloc(256 * sizeof(char));
    if (!cmd) {
        perror("Error allocating memory for command");
        return NULL;
    }

    if (format_command((char*)state, operation_id, cmd) != 1) {
        fprintf(stderr, "Error formatting command\n");
        free(cmd);
        return NULL;
    }

    return cmd;
}

char* wait_for_data_from_machine(Instance *instr, Machine *machine) {
    char *data = malloc(256 * sizeof(char));
    if (!data) {
        perror("Error allocating memory for data");
        return NULL;
    }

    srand(time(NULL)); // Seed the random number generator

    // Initialize the temperature and humidity if they are zero
    if (instr->last_temperature == 0) {
        instr->last_temperature = machine->temperature_min;
    }
    if (instr->last_humidity == 0) {
        instr->last_humidity = machine->humidity_min;
    }

    // Generate random changes within the range -1, 0, 1
    int temp_change = (rand() % 3) - 1; // -1, 0, or 1
    instr->last_temperature += temp_change;
    if (instr->last_temperature < machine->temperature_min) {
        instr->last_temperature = machine->temperature_min;
    } else if (instr->last_temperature > machine->temperature_max) {
        instr->last_temperature = machine->temperature_max;
    }

    int hum_change = (rand() % 3) - 1; // -1, 0, or 1
    instr->last_humidity += hum_change;
    if (instr->last_humidity < machine->humidity_min) {
        instr->last_humidity = machine->humidity_min;
    } else if (instr->last_humidity > machine->humidity_max) {
        instr->last_humidity = machine->humidity_max;
    }

    snprintf(data, 256, "TEMP&unit:celsius&value:%d#HUM&unit:percentage&value:%d", instr->last_temperature, instr->last_humidity);
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
            free(instr->machine_id);
            free(instr->state);
            free(instr);
            continue;
        }

        // Send command to machine
        send_cmd_to_machine_No_UI(cmd);
        free(cmd);
        sleep(2); // Simulate the time taken to send the command

        // Wait for data from machine
        char *str = wait_for_data_from_machine(instr, machine);
        if (!str) {
            perror("Error waiting for data from machine");
            free(instr->machine_id);
            free(instr->state);
            free(instr);
            continue;
        }

        // Extract data
        buffer_data data = {0};
        extract_data_machine(str, &data);
        free(str); // Free the data string

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

void add_machine(MachManager *machManager, const char *id, const char *name, int temp_min, int temp_max, int hum_min, int hum_max) {
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

void read_status_machine(MachManager *machmanager, const char *id) {
    Machine *machine = find_machine(machmanager, id);
    if (machine) {
        printf(BOLD "\nMachine Status:\n" RESET);
        printf("ID: %s\n", machine->id);
        printf("Name: %s\n", machine->name);
        printf("State: %s\n", machine->state);
        printf("Temperature Range: %d - %d\n", machine->temperature_min, machine->temperature_max);
        printf("Humidity Range: %d - %d\n", machine->humidity_min, machine->humidity_max);
    } else {
        printf(RED "\nMachine not found.\n" RESET);
    }
}

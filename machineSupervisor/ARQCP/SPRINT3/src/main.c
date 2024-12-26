#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>    // For open()
#include <termios.h>  // For configuring the serial port
#include <errno.h>    // For error handling
#include <time.h>     // For timestamp

#include "machmanager.h"
#include "operation.h"
#include "machine.h"
#include "assembly_functions.h"

Machine *machines = NULL;           // Pointer to the array of machines
int machine_count = 0;              // Current count of machines
int machine_capacity = 0;           // Current capacity of the machines array

void free_machine(Machine *m);
int format_command(char *op, int n, char *cmd);
int run_machine_interface();
int setup_machines_from_file(const char *filename);
void add_to_buffer(Machine *m, float temperature, float humidity);
void print_buffer(Machine *m);
int assign_operation_to_machine(int machine_index, const char *designation, int number, time_t timestamp);
void export_operations_to_csv(Machine *m);
void monitor_machine(Machine *m);

int main() {
    int option = -1; // Initialize option to a default value
    char filename[100];
    do {
        printf("\nMachine Management System\n");
        printf("1 - Setup Machines\n");
        printf("2 - Add Machine\n");
        printf("3 - Remove Machine\n");
        printf("4 - Import List of Instructions\n");
        printf("5 - Monitor Machine\n");
        printf("0 - Exit\n");
        printf("Choose an option: ");

        char input[10];
        if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
            printf("Invalid input. Please enter a number.\n");
            continue;
        }

        switch (option) {
            case 1:
                printf("Want to use the default setup file? (y/n): ");
                char answer;
                getchar(); // Clear the newline character left by the previous input
                scanf("%c", &answer);
                if (answer == 'n' || answer == 'N') {
                    printf("Enter the setup file name: ");
                    scanf("%99s", filename);
                } else {
                    strcpy(filename, "Files/machines.csv"); // Use strcpy to copy the string
                }
                if (setup_machines_from_file(filename) == 0) {
                    printf("Machines setup successfully.\n");
                } else {
                    printf("Error setting up machines.\n");
                }
                break;
            case 2:
                printf("Not implemented yet.\n");
                break;
            case 3:
                printf("Not implemented yet.\n");
                break;
            case 4:
                printf("Not implemented yet.\n");
                break;
            case 5:
                while (1) {
                    if (machine_count == 0) {
                        printf("No machines available.\n");
                        break;
                    }

                    printf("Available Machines:\n");
                    for (int i = 0; i < machine_count; i++) {
                        printf("%d - %s\n", i + 1, machines[i].id);
                    }
                    printf("0 - Back\n");
                    printf("Enter the option you want: ");

                    char input[10];
                    int sub_option;
                    if (scanf("%9s", input) != 1 || sscanf(input, "%d", &sub_option) != 1) {
                        printf("Invalid input. Please enter a number.\n");
                        continue;
                    }

                    if (sub_option == 0) {
                        break;
                    }

                    Machine selected_machine = {0}; // Initialize selected_machine
                    if (sub_option > 0 && sub_option <= machine_count) {
                        selected_machine = machines[sub_option - 1];
                    }

                    // Ask the user for buffer size and median window
                    while (1) {
                        printf("Enter buffer size (or type 'cancel' to go back): ");
                        char buffer_input[10];
                        if (scanf("%9s", buffer_input) != 1) {
                            printf("Invalid input. Please enter a positive number or 'cancel'.\n");
                            continue;
                        }
                        if (strcmp(buffer_input, "cancel") == 0) {
                            break;
                        }
                        int buffer_size;
                        if (sscanf(buffer_input, "%d", &buffer_size) != 1 || buffer_size <= 0) {
                            printf("Invalid buffer size. Please enter a positive number.\n");
                            continue;
                        }

                        printf("Enter median window size (or type 'cancel' to go back): ");
                        char window_input[10];
                        if (scanf("%9s", window_input) != 1) {
                            printf("Invalid input. Please enter a positive number or 'cancel'.\n");
                            continue;
                        }
                        if (strcmp(window_input, "cancel") == 0) {
                            break;
                        }
                        int median_window;
                        if (sscanf(window_input, "%d", &median_window) != 1 || median_window <= 0 || median_window > buffer_size) {
                            printf("Invalid median window size. Please enter a positive number less than or equal to buffer size.\n");
                            continue;
                        }

                        selected_machine.buffer_size = buffer_size;
                        selected_machine.median_window = median_window;

                        // Allocate buffer for the machine
                        selected_machine.buffer = malloc(selected_machine.buffer_size * sizeof(buffer_data));
                        if (!selected_machine.buffer) {
                            perror("Error allocating buffer");
                            continue;
                        }
                        selected_machine.head = selected_machine.buffer;
                        selected_machine.tail = selected_machine.buffer;

                        // Monitor the selected machine
                        monitor_machine(&selected_machine);

                        free(selected_machine.buffer); // Free the buffer to avoid memory leak
                        break;
                    }
                    break;
                }
            case 0:
                printf("Exiting...\n");
                break;
            default:
                printf("Invalid option. Please try again.\n");
        }
    } while (option != 0);

    return 0;
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

int setup_machines_from_file(const char *filename) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        perror("Error opening file");
        return -1;
    }

    // Free existing machines if any
    if (machine_count > 0) {
        for (int i = 0; i < machine_count; i++) {
            free_machine(&machines[i]); // Free the buffer for each machine
        }
        free(machines);  // Free the array itself
        machines = NULL; // Reset pointer to avoid dangling references
        machine_count = 0;
        machine_capacity = 0;
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
        if (machine_count >= machine_capacity) {
            machine_capacity = (machine_capacity == 0) ? 2 : machine_capacity * 2;
            Machine *new_machines = realloc(machines, machine_capacity * sizeof(Machine));
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
        machines[machine_count++] = new_machine;
    } while (fgets(line, sizeof(line), file));

    fclose(file);
    return 0;
}

void export_operations_to_csv(Machine *m) {
    if (!m) {
        printf("Invalid machine provided.\n");
        return;
    }

    // Construct the file path using the machine's ID
    char filepath[150];
    snprintf(filepath, sizeof(filepath), "Files/Machines_OP/%s_Operations.csv", m->id);

    // Open the file for writing
    FILE *file = fopen(filepath, "w");
    if (!file) {
        perror("Error opening file for writing");
        return;
    }

    // Write CSV header
    fprintf(file, "Number;Designation;Timestamp\n");

    // Write operation details
    for (int i = 0; i < m->operation_count; i++) {
        Operation *op = &m->operations[i];
        fprintf(file, "%d;%s;%ld\n", op->number, op->designation, op->timestamp);
    }

    fclose(file);

    printf("Operations exported to %s successfully.\n", filepath);
}

void monitor_machine(Machine *m) {
    int option = -1; // Initialize option to a default value
    do {
        printf("\nManaging Machine: %s\n", m->id);
        printf("1 - Assign Operation to Machine\n");
        printf("2 - Display Machine State\n");
        printf("3 - Export Operations to CSV\n");
        printf("0 - Exit\n");
        printf("Choose an option: ");

        char input[10];
        if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
            printf("Invalid input. Please enter a number.\n");
            continue;
        }

        switch (option) {
            case 1:
                printf("Not implemented yet.\n");
                break;
            case 2:
                printf("Not implemented yet.\n");
                break;
            case 3:
                export_operations_to_csv(m);
                break;
            case 0:
                break;
            default:
                printf("Invalid option. Please try again.\n");
        }
    } while (option != 0);
}




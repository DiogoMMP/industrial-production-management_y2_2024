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
#include "utils.h"
#include "serial_port.h" // Include the new header file

MachManager *machmanager;
Machine *machines = NULL;           // Pointer to the array of machines
int serial_port = -1;               // Define the global variable for the serial port

void monitor_machine(Machine *m);
void disconnect_serial_port();
void cleanup_and_exit();

int main() {
    // Allocates memory for machmanager
    machmanager = malloc(sizeof(MachManager));  // Allocates memory for the MachManager structure
    if (!machmanager) {
        perror("Error allocating memory for MachManager");
        return 1; // Terminate the programme in case of an allocation error
    }

    // Initialize machine_capacity before using it
    machmanager->machine_capacity = 10; // Set a default capacity

    // Allocates memory for the machine array if necessary
    machines = malloc(machmanager->machine_capacity * sizeof(Machine));
    if (!machines) {
        perror("Error allocating memory for machines array");
        free(machmanager);  // Releases memory already allocated to machmanager
        return 1;
    }

    create_machmanager(machmanager, machines, 0, machmanager->machine_capacity);

    int option = -1; // Initialize option to a default value
    char filename[100];
    
    do {
        printf(BOLD CYAN "\n\n--- Machine Monitoring and Control --------------------------\n\n" RESET);
        printf("1 - Setup Machines\n");
        printf("2 - Add Machine\n");
        printf("3 - Remove Machine\n");
        printf("4 - Read status of machine\n");
        printf("5 - Import List of Instructions\n");
        printf("6 - Monitor Machine\n");
        printf("0 - Exit\n");
        printf("\nChoose an option: ");

        char input[10];
        if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
            printf(RED "Invalid input. Please enter a number.\n" RESET);
            continue;
        }

        switch (option) {
            case 1:
                printf(BOLD "\nWant to use the default setup file? (Y/N): " RESET);
                char answer;

                getchar(); // Clear the newline character left by the previous input
                scanf("%c", &answer);

                if (answer == 'n' || answer == 'N') {
                    printf("\nEnter the setup file name: ");
                    scanf("%99s", filename);

                    char temp_filename[200];
                    snprintf(temp_filename, sizeof(temp_filename), "src/data/%s", filename);

                    strcpy(filename, temp_filename);

                } else {
                    strcpy(filename, "src/data/machines.csv"); // Use strcpy to copy the string
                }

                if (setup_machines_from_file(filename, machmanager) == 0) {
                    printf(GREEN "\nMachines setup successfully.\n" RESET);
                    goBackAndWait();
                } else {
                    printf(RED "\nError setting up machines.\n" RESET);
                }

                break;

            case 2:
                printf(BOLD "\nAdd Machine\n" RESET);
                printf("Enter the machine ID: ");
                char id[100];
                scanf("%99s", id);
                printf("Enter the machine name: ");
                char name[100];
                scanf("%99s", name);
                printf("Enter the minimum temperature: ");
                float temp_min;
                scanf("%f", &temp_min);
                printf("Enter the maximum temperature: ");
                float temp_max;
                scanf("%f", &temp_max);
                printf("Enter the minimum humidity: ");
                float hum_min;
                scanf("%f", &hum_min);
                printf("Enter the maximum humidity: ");
                float hum_max;
                scanf("%f", &hum_max);
                add_machine(machmanager, id, name, temp_min, temp_max, hum_min, hum_max);
                break;

            case 3:
                printf(BOLD "\nRemove Machine\n" RESET);
                printf("Enter the machine ID: ");
                char machine_id[100];
                scanf("%99s", machine_id);
                remove_machine(machmanager, machine_id);
                break;

            case 4:
                printf(BOLD "\nRead status of machine\n" RESET);
                printf("Enter the machine ID: ");
                char machine_id_status[100];
                scanf("%99s", machine_id_status);
                read_status_machine(machmanager, machine_id_status);
                break;

            case 5:

                printf(BOLD "\nWant to use the default setup file? (Y/N): " RESET);

                getchar(); // Clear the newline character left by the previous input
                scanf("%c", &answer);

                if (answer == 'n' || answer == 'N') {
                    printf("\nEnter the setup file name: ");
                    scanf("%99s", filename);

                    char temp_filename[200];
                    snprintf(temp_filename, sizeof(temp_filename), "src/data/%s", filename);

                    strcpy(filename, temp_filename);

                } else {
                    strcpy(filename, "src/data/simulation.csv"); // Use strcpy to copy the string
                }

                feed_system(filename, machmanager);
                break;
            case 6:
    while (1) {
        if (machmanager->machine_count == 0) {
            printf(RED "\nNo machines available.\n" RESET);
            break;
        }

        printf(BOLD CYAN "\n\n--- Available Machines --------------------------\n\n" RESET);
        
        for (int i = 0; i < machmanager->machine_count; i++) {
            printf("%d - %s\n", i + 1, machmanager->machines[i].id);
        }
        
        printf("0 - Back\n");
        printf("\nChoose an option: ");

        char input[10];
        int sub_option;
        if (scanf("%9s", input) != 1 || sscanf(input, "%d", &sub_option) != 1) {
            printf(RED "\nInvalid input. Please enter a number.\n" RESET);
            continue;
        }

        if (sub_option == 0) {
            break; // Return to the main menu
        }

        // Get the selected machine directly
        Machine *selected_machine = &machmanager->machines[sub_option - 1];

        // Ask the user for buffer size and median window
        while (1) {
            int buffer_size = 0, median_window = 0;
            
            // Loop to get a valid buffer size
            while (1) {
                printf(BOLD "\nEnter buffer size (or type 'cancel' to go back): " RESET);
                
                char buffer_input[10];
                if (scanf("%9s", buffer_input) != 1) {
                    printf(RED "\nInvalid input. Please enter a positive number or 'cancel'.\n" RESET);
                    continue;
                }
                
                if (strcmp(buffer_input, "cancel") == 0) {
                    break; // Exit the function or handle going back
                }
                
                if (sscanf(buffer_input, "%d", &buffer_size) != 1 || buffer_size <= 0) {
                    printf(RED "\nInvalid buffer size. Please enter a positive number.\n" RESET);
                    continue;
                }
                
                break; // Valid buffer size
            }
            
            // Loop to get a valid median window size
            while (1) {
                printf(BOLD "\nEnter median window size (or type 'cancel' to go back): " RESET);
                
                char window_input[10];
                if (scanf("%9s", window_input) != 1) {
                    printf(RED "\nInvalid input. Please enter a positive number or 'cancel'.\n" RESET);
                    continue;
                }
                
                if (strcmp(window_input, "cancel") == 0) {
                    break; // Go back to asking buffer size
                }
                
                if (sscanf(window_input, "%d", &median_window) != 1 || median_window <= 0 || median_window > buffer_size) {
                    printf(RED "\nInvalid median window size. Please enter a positive number less than or equal to buffer size.\n" RESET);
                    continue;
                }
                
                break; // Valid median window size
            }

            if (median_window > 0) {
                // Both buffer size and median window are valid
                printf(GREEN "\nBuffer size: %d, Median window size: %d\n" RESET, buffer_size, median_window);
                
            }

            // Modify the selected machine's buffer size and median window directly
            selected_machine->buffer_size = buffer_size;
            selected_machine->median_window = median_window;

            // Allocate buffer for the machine
            selected_machine->buffer = malloc(selected_machine->buffer_size * sizeof(buffer_data));
            if (!selected_machine->buffer) {
                perror(RED "\nError allocating buffer.\n" RESET);
                continue;
            }
            selected_machine->head = selected_machine->buffer;
            selected_machine->tail = selected_machine->buffer;

            // Initialize buffer data to zero
            for (int i = 0; i < selected_machine->buffer_size; i++) {
                selected_machine->buffer[i].temperature = 0.0;
                selected_machine->buffer[i].humidity = 0.0;
            }

            // Monitor the selected machine
            monitor_machine(selected_machine);

            // Free the buffer to avoid memory leak after use
            free(selected_machine->buffer);
            break;
        }
        break;
    }
    break;
            case 0:
                // Disconnect the serial port
                disconnect_serial_port();

                // Free all dynamically allocated memory for each machine
                for (int i = 0; i < machmanager->machine_count; i++) {
                    free_machine(&machmanager->machines[i]);
                }

                // Free the machines array
                free(machmanager->machines);

                // Releases memory after use
                free(machmanager);

                printf(GREEN "\nExiting the program.\n\n" RESET);
                cleanup_and_exit();
                return 0;
            default:
                printf(RED "\nInvalid option. Please try again.\n" RESET);
        }
    } while (option != 0);

    // Disconnect the serial port
    disconnect_serial_port();

    // Free all dynamically allocated memory for each machine
    for (int i = 0; i < machmanager->machine_count; i++) {
        free_machine(&machmanager->machines[i]);
    }

    // Free the machines array
    free(machmanager->machines);

    // Releases memory after use
    free(machmanager);

    cleanup_and_exit();
    return 0;
}

void monitor_machine(Machine *m) {
    int option = -1; // Initialize option to a default value
    MachManager machmanager;
    create_machmanager(&machmanager, m, 1, 1); // Initialize MachManager with the single machine

    do {
        printf(BOLD CYAN "\n\n--- Managing Machine: %s --------------------------\n\n" RESET, m->id);
        printf("1 - Assign Operation to Machine\n");
        printf("2 - Display Machine State\n");
        printf("3 - Export Operations to CSV\n");
        printf("0 - Exit\n");
        printf("\nChoose an option: ");

        char input[10];
        if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
            printf(RED "\nInvalid input. Please enter a number.\n" RESET);
            continue;
        }

        switch (option) {
            case 1:
                if (m->operation_count == 0) {
                    printf(RED "\nNo operations available.\n" RESET);
                    break;
                }
                if (m->exec_operation_count == m->operation_count) {
                    printf(GREEN "\nAll operations have been executed.\n" RESET);
                    break;
                }
                assign_operation_to_machine(&m->operations[m->exec_operation_count], m);
                m->exec_operation_count++;
                m->state = "OP"; // Change the state of the machine to "OP"
                printf(GREEN "\nOperation %s assigned to machine %s successfully.\n" RESET, m->operations[m->exec_operation_count - 1].designation, m->id);
                start_main_loop(m);
                sleep(2);
                break;
            case 2: {
                if (m->moving_median_count > 0) {
                    printf(BOLD "\nMachine State:\n" RESET);
                    printf("Temperature (Moving Median): %.2f\n", m->moving_median[m->moving_median_count - 1].temperature);
                    printf("Humidity (Moving Median): %.2f\n", m->moving_median[m->moving_median_count - 1].humidity);
                } else if (m->head) {
                    printf(BOLD "\nMachine State:\n" RESET);
                    printf("Temperature: %.2f\n", m->head->temperature);
                    printf("Humidity: %.2f\n", m->head->humidity);
                } else {
                    printf(RED "\nNo data available.\n" RESET);
                }

                sleep(2);
                break;
            }
            case 3:
                export_operations_to_csv(m);
                break;
            case 0:
                stop_program(&machmanager);
                sleep(2);
                return; // Exit the function to stop monitoring
            default:
                printf(RED "\nInvalid option. Please try again.\n" RESET);
        }
    } while (option != 0);
}

void disconnect_serial_port() {
    if (serial_port != -1) {
        // Reset the serial port variable to disconnect it
        serial_port = -1;
        printf(GREEN "\nSerial port disconnected successfully.\n" RESET);
    }
}

void cleanup_and_exit() {
    // Exit the program
    exit(0);
}
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

MachManager *machmanager;
Machine *machines = NULL;           // Pointer to the array of machines

int format_command(char *op, int n, char *cmd);
int run_machine_interface();
void add_to_buffer(Machine *m, float temperature, float humidity);
void print_buffer(Machine *m);
int assign_operation_to_machine(int machine_index, const char *designation, int number, time_t timestamp);
void export_operations_to_csv(Machine *m);
void monitor_machine(Machine *m);

int main() {
    // Allocates memory for machmanager
    machmanager = malloc(sizeof(MachManager));  // Allocates memory for the MachManager structure
    if (!machmanager) {
        perror("Error allocating memory for MachManager");
        return 1; // Terminate the programme in case of an allocation error
    }

    // Allocates memory for the machine array if necessary
    machines = malloc(machmanager -> machine_capacity * sizeof(Machine));
    if (!machines) {
        perror("Error allocating memory for machines array");
        free(machmanager);  // Releases memory already allocated to machmanager
        return 1;
    }

    create_machmanager(machmanager, machines, 0, 0);

    int option = -1; // Initialize option to a default value
    char filename[100];
    do {
        printf(BOLD CYAN "\n\n--- Machine Management System --------------------------\n\n" RESET);
        printf("1 - Setup Machines\n");
        printf("2 - Add Machine\n");
        printf("3 - Remove Machine\n");
        printf("4 - Import List of Instructions\n");
        printf("5 - Monitor Machine\n");
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
                    if (machmanager -> machine_count == 0) {
                        printf(RED "\nNo machines available.\n" RESET);
                        break;
                    }

                    printf(BOLD CYAN "\n\n--- Available Machines --------------------------\n\n" RESET);
                    
                    for (int i = 0; i < machmanager -> machine_count; i++) {
                        printf("%d - %s\n", i + 1, machmanager -> machines[i].id);
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
                        break;
                    }

                    Machine selected_machine = {0}; // Initialize selected_machine
                    if (sub_option > 0 && sub_option <= machmanager -> machine_count) {
                        selected_machine = machmanager -> machines[sub_option - 1];
                    }

                    // Ask the user for buffer size and median window
                    while (1) {
                        printf(BOLD "\nEnter buffer size (or type 'cancel' to go back): " RESET);
                        char buffer_input[10];
                        if (scanf("%9s", buffer_input) != 1) {
                            printf(RED "\nInvalid input. Please enter a positive number or 'cancel'.\n" RESET);
                            continue;
                        }
                        if (strcmp(buffer_input, "cancel") == 0) {
                            break;
                        }
                        int buffer_size;
                        if (sscanf(buffer_input, "%d", &buffer_size) != 1 || buffer_size <= 0) {
                            printf(RED "\nInvalid buffer size. Please enter a positive number.\n" RESET);
                            continue;
                        }

                        printf(BOLD "\nEnter median window size (or type 'cancel' to go back): " RESET);
                        char window_input[10];
                        if (scanf("%9s", window_input) != 1) {
                            printf(RED "\nInvalid input. Please enter a positive number or 'cancel'.\n" RESET);
                            continue;
                        }
                        if (strcmp(window_input, "cancel") == 0) {
                            break;
                        }
                        int median_window;
                        if (sscanf(window_input, "%d", &median_window) != 1 || median_window <= 0 || median_window > buffer_size) {
                            printf(RED "\nInvalid median window size. Please enter a positive number less than or equal to buffer size.\n" RESET);
                            continue;
                        }

                        selected_machine.buffer_size = buffer_size;
                        selected_machine.median_window = median_window;

                        // Allocate buffer for the machine
                        selected_machine.buffer = malloc(selected_machine.buffer_size * sizeof(buffer_data));
                        if (!selected_machine.buffer) {
                            perror(RED "\nError allocating buffer.\n" RESET);
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
                break;
            default:
                printf(RED "\nInvalid option. Please try again.\n" RESET);
        }
    } while (option != 0);

    // Releases memory after use
    free(machmanager);
    return 0;
}

void monitor_machine(Machine *m) {
    int option = -1; // Initialize option to a default value
    do {
        printf(BOLD CYAN "\n\n--- Managing Machine: %s --------------------------\n\n" RESET, m->id);
        printf("1 - Assign Operation to Machine\n");
        printf("2 - Display Machine State\n");
        printf("3 - Export Operations to CSV\n");
        printf("0 - Exit\n");
        printf("Choose an option: ");

        char input[10];
        if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
            printf(RED "\nInvalid input. Please enter a number.\n" RESET);
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
                printf(RED "\nInvalid option. Please try again.\n" RESET);
        }
    } while (option != 0);
}




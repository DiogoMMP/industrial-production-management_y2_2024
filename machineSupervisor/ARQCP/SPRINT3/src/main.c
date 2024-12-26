#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>    // For open()
#include <termios.h>  // For configuring the serial port
#include <errno.h>    // For error handling
#include <time.h>     // For timestamp

#include "machmanager.h"
#include "operations.h"
#include "machine.h"
#include "assembly_functions.h"

struct machine *machines = NULL; // Pointer to the array of machines
int machine_count = 0;           // Current count of machines
int machine_capacity = 0;        // Current capacity of the machines array

void free_machine(struct machine *m);
int format_command(char *op, int n, char *cmd);
int run_machine_interface();
int setup_machines_from_file(const char *filename);
void add_to_buffer(struct machine *m, float temperature, float humidity);
void print_buffer(struct machine *m);
int assign_operation_to_machine(int machine_index, const char *designation, int number);
void export_operations_to_csv(struct machine *m);
void monitor_machine(struct machine *m);

int main() {
    int option = -1; // Initialize option to a default value
    char filename[100];
    do {
        printf("\nMachine Management System\n");
        printf("1 - Setup Machines\n");
        printf("2 - Export Machines Operations\n");
        printf("3 - Add Machine\n");
        printf("4 - Remove Machine\n");
        printf("5 - Import List of Instructions\n");
        printf("6 - Monitor Machine\n");
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
            case 2: {
                while (1) {
                    if (machine_count == 0) {
                        printf("No machines available.\n");
                        break;
                    }

                    printf("Available Machines:\n");
                    for (int i = 0; i < machine_count; i++) {
                        printf("%d - %s\n", i + 1, machines[i].id);
                    }
                    printf("%d - All\n", machine_count + 1);
                    printf("0 - Back\n");
                    printf("Enter the option you want: ");

                    char input[10];
                    int option;
                    if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
                        printf("Invalid input. Please enter a number.\n");
                        continue;
                    }

                    if (option == 0) {
                        break;
                    }
                    if (option == machine_count + 1) {
                        for (int i = 0; i < machine_count; i++) {
                            export_operations_to_csv(&machines[i]);
                        }
                        break;
                    }

                    struct machine *selected_machine = NULL;
                    if (option > 0 && option <= machine_count) {
                        selected_machine = &machines[option - 1];
                    }

                    if (!selected_machine) {
                        printf("Invalid machine selected. Please try again.\n");
                        continue;
                    }

                    // Export operations to CSV for the selected machine
                    export_operations_to_csv(selected_machine);
                    break;
                }
                break;
            }
            case 3: {
                printf("Not implemented yet.\n");
                break;
            }
            case 4:
                printf("Not implemented yet.\n");
                break;
            case 5:
                printf("Not implemented yet.\n");
                break;
            case 6:
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
                    int option;
                    if (scanf("%9s", input) != 1 || sscanf(input, "%d", &option) != 1) {
                        printf("Invalid input. Please enter a number.\n");
                        continue;
                    }

                    if (option == 0) {
                        break;
                    }

                    struct machine *selected_machine = NULL;
                    if (option > 0 && option <= machine_count) {
                        selected_machine = &machines[option - 1];
                    }

                    if (!selected_machine) {
                        printf("Invalid machine selected. Please try again.\n");
                        continue;
                    }

                    // Export operations to CSV for the selected machine
                    monitor_machine(selected_machine);
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
void free_machine(struct machine *m) {
    if (m->buffer) {
        free(m->buffer);  // Free the dynamically allocated buffer
        m->buffer = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->operations) {
        free(m->operations); // Free the dynamically allocated operations array
        m->operations = NULL; // Reset pointer to avoid dangling reference
    }
}

// This is not correct. It is not an interface.
int run_machine_interface() {
    // Variables for user input
    char state[10];
    int operation_number;
    char command[100];
    char system_command[200];

    // Variables for reading from the serial port
    int serial_port;
    char read_buffer[256];  // Buffer to store the data read from the serial port
    ssize_t bytes_read;

    // Ask the user for the machine state
    printf("Enter the machine status (ON, OFF, or OP): ");
    if (scanf("%9s", state) != 1) {
        printf("Error reading machine status.\n");
        return -1;
    }

    // Ask the user for the operation number
    printf("Enter the operation number (0-31): ");
    if (scanf("%d", &operation_number) != 1) {
        printf("Error reading operation number.\n");
        return -1;
    }

    // Validate the operation number
    if (operation_number < 0 || operation_number > 31) {
        printf("Error: number outside the permitted range (0-31).\n");
        return -1;
    }

    // Call the function to format the command
    if (format_command(state, operation_number, command) != 1) {
        printf("Error formatting the command.\n");
        return -1;
    }

    // Display the formatted command
    printf("Formatted command: %s\n", command);

    // Construct the command for the system()
    snprintf(system_command, sizeof(system_command), "echo \"%s\" > /dev/ttyACM0", command);

    // Execute the command using system()
    int result = system(system_command);
    if (result == -1) {
        printf("Error executing the command with system().\n");
        return -1;
    }

    // Check if the command contains "OP" (not case-sensitive)
    if (strstr(command, "OP") != NULL) {
        // Open the serial port for reading
        serial_port = open("/dev/ttyACM0", O_RDONLY);  // Open the port for reading
        if (serial_port == -1) {
            perror("Error opening the serial port");
            return -1;
        }

        // Configure the serial port
        struct termios tty;
        if (tcgetattr(serial_port, &tty) != 0) {
            perror("Error getting serial port parameters");
            close(serial_port);
            return -1;
        }

        // Set serial port configuration (9600 baud, 8 data bits, no parity, 1 stop bit)
        cfsetispeed(&tty, B9600);      // Set input baud rate
        cfsetospeed(&tty, B9600);      // Set output baud rate
        tty.c_cflag &= ~PARENB;        // Disable parity
        tty.c_cflag &= ~CSTOPB;        // 1 stop bit
        tty.c_cflag &= ~CSIZE;
        tty.c_cflag |= CS8;            // 8 data bits
        tty.c_cflag &= ~CRTSCTS;       // Disable flow control
        tty.c_cflag |= CREAD | CLOCAL; // Enable reading and disable modem control

        // Apply the serial port configuration
        if (tcsetattr(serial_port, TCSANOW, &tty) != 0) {
            perror("Error applying serial port configuration");
            close(serial_port);
            return -1;
        }

        // Read from the serial port
        bytes_read = read(serial_port, read_buffer, sizeof(read_buffer) - 1);
        if (bytes_read == -1) {
            perror("Error reading from the serial port");
            close(serial_port);
            return -1;
        }

        // Add the null terminator to the read string
        read_buffer[bytes_read] = '\0';

        // Display the response from the Arduino
        printf("Response from Arduino: %s\n", read_buffer);

        // Close the serial port
        close(serial_port);
    } else {
        printf("Serial port not found.\n");
        return -1;
    }

    printf("Command executed successfully.\n");
    return 0;
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
        struct machine new_machine = {0}; // Ensure it's zero-initialized
        if (sscanf(line, "%9[^,],%19[^,],%f,%f,%f,%f,%d,%d",
                   new_machine.id, new_machine.name,
                   &new_machine.temperature_min, &new_machine.temperature_max,
                   &new_machine.humidity_min, &new_machine.humidity_max,
                   &new_machine.buffer_size, &new_machine.median_window) != 8) {
            fprintf(stderr, "Invalid line format: %s", line);
            continue;
        }

        // Validation checks
        if (new_machine.temperature_min > new_machine.temperature_max ||
            new_machine.humidity_min > new_machine.humidity_max ||
            new_machine.median_window > new_machine.buffer_size) {
            fprintf(stderr, "Validation failed for machine %s\n", new_machine.id);
            continue;
        }

        // Allocate buffer for the machine
        new_machine.buffer = malloc(new_machine.buffer_size * sizeof(struct buffer_data));
        if (!new_machine.buffer) {
            perror("Error allocating buffer");
            fclose(file);
            return -1;
        }
        new_machine.head = new_machine.buffer;
        new_machine.tail = new_machine.buffer;

        // Initialize operations array
        new_machine.operation_capacity = 10; // Initial capacity for operations
        new_machine.operations = malloc(new_machine.operation_capacity * sizeof(struct operation));
        if (!new_machine.operations) {
            perror("Error allocating operations array");
            free(new_machine.buffer);
            fclose(file);
            return -1;
        }
        new_machine.operation_count = 0;

        // Check if resizing the machines array is needed
        if (machine_count >= machine_capacity) {
            machine_capacity = (machine_capacity == 0) ? 2 : machine_capacity * 2;
            struct machine *new_machines = realloc(machines, machine_capacity * sizeof(struct machine));
            if (!new_machines) {
                perror("Error resizing machine array");
                free(new_machine.buffer); // Clean up buffer before returning
                free(new_machine.operations); // Clean up operations array before returning
                fclose(file);
                return -1;
            }
            machines = new_machines;
            // Free the new machine buffer and operations array before returning
            free(new_machine.buffer);
            free(new_machine.operations);
        }

        // Add the new machine to the array
        machines[machine_count++] = new_machine;
    } while (fgets(line, sizeof(line), file));

    fclose(file);
    return 0;
}

void print_buffer(struct machine *m) {
    struct buffer_data *current = m->tail;
    while (current != m->head) {
        printf("Temperature: %.2f, Humidity: %.2f\n", current->temperature, current->humidity);
        current++;
        if (current >= m->buffer + m->buffer_size) {
            current = m->buffer; // Wrap around to the beginning of the buffer
        }
    }
    printf("Temperature: %.2f, Humidity: %.2f\n", current->temperature, current->humidity); // Print the head element
}


void export_operations_to_csv(struct machine *m) {
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
        struct operation *op = &m->operations[i];
        char timestamp_str[30];
        strftime(timestamp_str, sizeof(timestamp_str), "%Y-%m-%d %H:%M:%S", localtime(&op->timestamp));
        fprintf(file, "%d;%s;%s\n", op->number, op->designation, timestamp_str);
    }

    fclose(file);

    printf("Operations exported to %s successfully.\n", filepath);
}

void monitor_machine(struct machine *m) {
    int option = -1; // Initialize option to a default value
    do {
        printf("\nManaging Machine: %s\n", m->id);
        printf("1 - Assign Operation to Machine\n");
        printf("2 - Display Machine State\n");
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
            case 0:
                break;
            default:
                printf("Invalid option. Please try again.\n");
        }
    } while (option != 0);
}





#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>    // For open()
#include <termios.h>  // For configuring the serial port
#include <errno.h>    // For error handling
#include "usac.h"

struct machine {
    char id[10];
    char name[20];
    float temperature_min;
    float temperature_max;
    float humidity_min;
    float humidity_max;
    int *buffer;         // Pointer to dynamically allocated circular buffer
    int buffer_size;     // Circular buffer length
    int *head;           // Points to the newest element (pointer)
    int *tail;           // Points to the oldest element (pointer)
    int median_window;   // Moving median window length
};

struct machine *machines = NULL; // Pointer to the array of machines
int machine_count = 0;           // Current count of machines
int machine_capacity = 0;        // Current capacity of the machines array

void free_machine(struct machine *m);
int format_command(char *op, int n, char *cmd);
int run_machine_interface();
int setup_machines_from_file(const char *filename);

int main() {
    int option;
    char filename[100];
    do {
        printf("\nMachine Management System\n");
        printf("1 - Setup Machines\n");
        printf("2 - Export Machines Operations\n");
        printf("3 - Add Machine\n");
        printf("4 - Remove Machine\n");
        printf("5 - Check Machine Status\n");
        printf("6 - Assign Operation to Machine\n");
        printf("7 - Display Machine State\n");
        printf("8 - Import List of Instructions\n");
        printf("9 - Notify if a Machine broke one of the limiters\n");
        printf("0 - Exit\n");
        printf("Choose an option: ");
        scanf("%d", &option);

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
                printf("Not implemented yet.\n");
                break;
            case 6:
                printf("Not implemented yet.\n");
                break;
            case 7:
                printf("Not implemented yet.\n");
                break;
            case 8:
                printf("Not implemented yet.\n");
                break;
            case 9:
                printf("Not implemented yet.\n");
                break;
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
    free(m->buffer); // Free the dynamically allocated buffer
}

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

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        struct machine new_machine;
        if (sscanf(line, "%9[^,],%19[^,],%f,%f,%f,%f,%d,%d",
                   new_machine.id, new_machine.name,
                   &new_machine.temperature_min, &new_machine.temperature_max,
                   &new_machine.humidity_min, &new_machine.humidity_max,
                   &new_machine.buffer_size, &new_machine.median_window) != 8) {
            fprintf(stderr, "Invalid line format: %s", line);
            continue;
        }

        // Validation: Temperature ranges
        if (new_machine.temperature_min > new_machine.temperature_max) {
            fprintf(stderr, "Invalid temperature range for machine %s\n", new_machine.id);
            continue;
        }

        // Validation: Humidity ranges
        if (new_machine.humidity_min > new_machine.humidity_max) {
            fprintf(stderr, "Invalid humidity range for machine %s\n", new_machine.id);
            continue;
        }

        // Validation: Moving median window size
        if (new_machine.median_window > new_machine.buffer_size) {
            fprintf(stderr, "Invalid median window size for machine %s\n", new_machine.id);
            continue;
        }

        // Allocate memory for the circular buffer
        new_machine.buffer = malloc(new_machine.buffer_size * sizeof(int));
        if (!new_machine.buffer) {
            perror("Error allocating buffer");
            fclose(file);
            return -1;
        }
        new_machine.head = new_machine.buffer;
        new_machine.tail = new_machine.buffer;

        // Add the machine to the array
        if (machine_count >= machine_capacity) {
            machine_capacity = (machine_capacity == 0) ? 2 : machine_capacity * 2;
            machines = realloc(machines, machine_capacity * sizeof(struct machine));
            if (!machines) {
                perror("Error resizing machine array");
                fclose(file);
                return -1;
            }
        }

        machines[machine_count++] = new_machine;
    }

    fclose(file);
    return 0;
}



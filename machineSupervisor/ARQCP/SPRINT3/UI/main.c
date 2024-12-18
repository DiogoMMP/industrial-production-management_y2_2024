#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>    // For open()
#include <termios.h>  // For configuring the serial port
#include <errno.h>    // For error handling

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
void create_machine();
void modify_machine();
void list_machines();
void cleanup_machines();

int main() {
    int option;
    do {
        printf("\nMachine Management System\n");
        printf("1 - Create a new machine\n");
        printf("2 - Modify an existing machine\n");
        printf("3 - List all machines\n");
        printf("0 - Exit\n");
        printf("Choose an option: ");
        scanf("%d", &option);

        switch (option) {
            case 1:
                create_machine();
                break;
            case 2:
                modify_machine();
                break;
            case 3:
                list_machines();
                break;
            case 0:
                printf("Exiting...\n");
                cleanup_machines(); // Free all allocated memory
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

void create_machine() {
    // Resize the array if needed
    if (machine_count >= machine_capacity) {
        machine_capacity = (machine_capacity == 0) ? 10 : machine_capacity * 2;
        struct machine *new_machines = realloc(machines, machine_capacity * sizeof(struct machine));
        if (new_machines == NULL) {
            printf("Error: Unable to allocate memory for new machines.\n");
            return;
        }
        machines = new_machines;
    }

    struct machine *new_machine = &machines[machine_count];

    printf("Enter machine ID: ");
    scanf("%9s", new_machine->id);

    printf("Enter machine name: ");
    scanf("%19s", new_machine->name);

    printf("Enter minimum temperature: ");
    scanf("%f", &new_machine->temperature_min);

    printf("Enter maximum temperature: ");
    scanf("%f", &new_machine->temperature_max);

    printf("Enter minimum humidity: ");
    scanf("%f", &new_machine->humidity_min);

    printf("Enter maximum humidity: ");
    scanf("%f", &new_machine->humidity_max);

    new_machine->buffer = NULL;
    new_machine->buffer_size = 0;
    new_machine->head = NULL;
    new_machine->tail = NULL;
    new_machine->median_window = 0;

    machine_count++;

    printf("Machine created successfully!\n");
}

void modify_machine() {
    if (machine_count == 0) {
        printf("No machines available to modify.\n");
        return;
    }

    char machine_id[10];
    printf("Enter the ID of the machine to modify: ");
    scanf("%9s", machine_id);

    for (int i = 0; i < machine_count; i++) {
        if (strcmp(machines[i].id, machine_id) == 0) {
            printf("Modifying machine: %s\n", machines[i].name);

            printf("Enter new name (current: %s): ", machines[i].name);
            scanf("%19s", machines[i].name);

            printf("Enter new minimum temperature (current: %.2f): ", machines[i].temperature_min);
            scanf("%f", &machines[i].temperature_min);

            printf("Enter new maximum temperature (current: %.2f): ", machines[i].temperature_max);
            scanf("%f", &machines[i].temperature_max);

            printf("Enter new minimum humidity (current: %.2f): ", machines[i].humidity_min);
            scanf("%f", &machines[i].humidity_min);

            printf("Enter new maximum humidity (current: %.2f): ", machines[i].humidity_max);
            scanf("%f", &machines[i].humidity_max);

            printf("Machine modified successfully!\n");
            return;
        }
    }

    printf("Machine with ID %s not found.\n", machine_id);
}

void list_machines() {
    if (machine_count == 0) {
        printf("No machines available.\n");
        return;
    }

    printf("\nList of Machines:\n");
    for (int i = 0; i < machine_count; i++) {
        printf("ID: %s, Name: %s, Temp Range: %.2f-%.2f, Humidity Range: %.2f-%.2f\n",
               machines[i].id, machines[i].name,
               machines[i].temperature_min, machines[i].temperature_max,
               machines[i].humidity_min, machines[i].humidity_max);
    }
}

// Free all allocated memory for the machines array
void cleanup_machines() {
    for (int i = 0; i < machine_count; i++) {
        free_machine(&machines[i]);
    }
    free(machines);
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


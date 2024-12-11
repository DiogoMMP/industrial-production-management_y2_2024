#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>    // For open()
#include <termios.h>  // For configuring the serial port
#include <errno.h>    // For error handling

int format_command(char* op, int n, char *cmd);

int main() {
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
    }

    printf("Command executed successfully.\n");
    return 0;
}

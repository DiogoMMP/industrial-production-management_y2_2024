#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>

#include "utils.h"

// Function to send the command to the machine
int update_machine(char* cmd) {
    if (!cmd) {
        printf(RED "\nError: Invalid command.\n" RESET);
        return -1;
    }

    // Variables for the system command and serial port reading
    char *system_command = (char*)malloc(200 * sizeof(char));
    char *read_buffer = (char*)malloc(256 * sizeof(char));
    int serial_port;
    ssize_t bytes_read;

    // Validate memory allocation
    if (!system_command || !read_buffer) {
        printf(RED "\nError: Memory allocation failed.\n" RESET);
        free(system_command);
        free(read_buffer);
        return -1;
    }

    // Build the system command
    snprintf(system_command, 200, "echo \"%s\" > /dev/ttyUSB0", cmd);

    // Execute the command using system()
    int result = system(system_command);
    if (result == -1) {
        printf(RED "\nError: Failed to execute the command using system().\n" RESET);
        free(system_command);
        free(read_buffer);
        return -1;
    }

    // Check if the command contains "OP" (case-sensitive)
    if (strstr(cmd, "OP") != NULL) {
        // Open the serial port for reading
        serial_port = open("/dev/ttyUSB0", O_RDONLY);
        if (serial_port == -1) {
            perror(RED "\nError: Failed to open the serial port");
            printf(RESET);
            free(system_command);
            free(read_buffer);
            return -1;
        }

        // Configure the serial port
        struct termios tty;
        if (tcgetattr(serial_port, &tty) != 0) {
            perror(RED "\nError: Failed to get serial port attributes." RESET);
            close(serial_port);
            free(system_command);
            free(read_buffer);
            return -1;
        }

        // Set the serial port configuration (9600 baud, 8 data bits, no parity, 1 stop bit)
        cfsetispeed(&tty, B9600);
        cfsetospeed(&tty, B9600);
        tty.c_cflag &= ~PARENB;  // Disable parity
        tty.c_cflag &= ~CSTOPB;  // 1 stop bit
        tty.c_cflag &= ~CSIZE;
        tty.c_cflag |= CS8;      // 8 data bits
        tty.c_cflag &= ~CRTSCTS; // Disable hardware flow control
        tty.c_cflag |= CREAD | CLOCAL; // Enable reading and disable modem control

        // Apply the serial port configuration
        if (tcsetattr(serial_port, TCSANOW, &tty) != 0) {
            perror(RED "\nError: Failed to apply serial port settings.\n" RESET);
            close(serial_port);
            free(system_command);
            free(read_buffer);
            return -1;
        }

        // Read from the serial port
        bytes_read = read(serial_port, read_buffer, 255);
        if (bytes_read == -1) {
            perror(RED "\nError: Failed to read from the serial port.\n" RESET);
            close(serial_port);
            free(system_command);
            free(read_buffer);
            return -1;
        }

        // Null-terminate the string read from the serial port
        read_buffer[bytes_read] = '\0';

        // Print the response from the Arduino
        printf(BLUE "\nArduino response: %s\n" RESET, read_buffer);

        // Close the serial port
        close(serial_port);
    } else {
        printf(RED "\n\nNo serial port interaction required for this command.\n" RESET);
    }

    // Free dynamically allocated memory
    free(system_command);
    free(read_buffer);

    return 0;
}

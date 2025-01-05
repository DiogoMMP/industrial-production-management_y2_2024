#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/ioctl.h> // Include this header for ioctl

#include "utils.h"
#include "serial_port.h" // Include the new header file

// Function to send the command to the machine
void update_machine(char* cmd) {
    if (!cmd) {
        printf(RED "\nError: Invalid command.\n" RESET);
        return;
    }

    // Variables for the reading buffer
    char *read_buffer = (char*)malloc(256 * sizeof(char));
    ssize_t bytes_read;

    // Validate memory allocation
    if (!read_buffer) {
        printf(RED "\nError: Memory allocation failed.\n" RESET);
        return;
    }

    // Open the serial port for writing if not already open
    if (serial_port == -1) {
        serial_port = open("/dev/ttyUSB0", O_RDWR | O_NOCTTY | O_SYNC);
        if (serial_port == -1) {
            perror(RED "\nError: Failed to open the serial port");
            printf(RESET);
            free(read_buffer);
            return;
        }

        // Configure the serial port
        struct termios tty;
        if (tcgetattr(serial_port, &tty) != 0) {
            perror(RED "\nError: Failed to get serial port attributes." RESET);
            close(serial_port);
            free(read_buffer);
            return;
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
            free(read_buffer);
            return;
        }
    }

    // Write the command to the serial port
    if (write(serial_port, cmd, strlen(cmd)) == -1) {
        perror(RED "\nError: Failed to write to the serial port.\n" RESET);
        close(serial_port);
        free(read_buffer);
        return;
    }

    // Check if the command contains "OP" (case-sensitive)
    if (strstr(cmd, "OP") != NULL) {
        // Read from the serial port
        bytes_read = read(serial_port, read_buffer, 255);
        if (bytes_read == -1) {
            perror(RED "\nError: Failed to read from the serial port.\n" RESET);
            close(serial_port);
            free(read_buffer);
            return;
        }

        // Null-terminate the string read from the serial port
        read_buffer[bytes_read] = '\0';

        // Print the response from the Arduino
        printf(BLUE "\nArduino response: %s\n" RESET, read_buffer);
    } else {
        printf(RED "\n\nNo serial port interaction required for this command.\n" RESET);
    }

    // Flush the serial port
    if (tcflush(serial_port, TCIOFLUSH) == -1) {
        perror(RED "\nError: Failed to flush the serial port.\n" RESET);
    }

    // Drain the serial port
    if (tcdrain(serial_port) == -1) {
        perror(RED "\nError: Failed to drain the serial port.\n" RESET);
    }

    // Free the read buffer
    free(read_buffer);
}
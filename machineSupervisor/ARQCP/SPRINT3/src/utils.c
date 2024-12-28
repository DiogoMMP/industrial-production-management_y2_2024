#include "utils.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Safely allocate memory
void* safe_malloc(size_t size) {
    void* ptr = malloc(size);
    if (!ptr) {
        perror(RED "Error allocating memory" RESET);
        exit(EXIT_FAILURE);
    }
    return ptr;
}

// Reads a line from the console
char *read_line(const char *prompt) {
    printf("%s", prompt);

    size_t buffer_size = 256;
    char *line = malloc(buffer_size);
    if (!line) {
        fprintf(stderr, RED "Memory allocation error.\n" RESET);
        exit(EXIT_FAILURE);
    }

    if (fgets(line, buffer_size, stdin) == NULL) {
        free(line);
        return NULL;
    }

    // Remove trailing newline
    line[strcspn(line, "\n")] = '\0';
    return line;
}

// Reads an integer from the console
int read_int(const char *prompt) {
    int value;
    while (true) {
        char *input = read_line(prompt);
        if (sscanf(input, "%d", &value) == 1) {
            free(input);
            return value;
        }
        printf(RED "Invalid input. Please enter an integer.\n" RESET);
        free(input);
    }
}

// Reads a double from the console
double read_double(const char *prompt) {
    double value;
    while (true) {
        char *input = read_line(prompt);
        if (sscanf(input, "%lf", &value) == 1) {
            free(input);
            return value;
        }
        printf(RED "Invalid input. Please enter a number.\n" RESET);
        free(input);
    }
}

// Asks for confirmation (Y/N)
bool confirm(const char *message) {
    char *input;
    while (true) {
        input = read_line(message);
        if (strcasecmp(input, "y") == 0) {
            free(input);
            return true;
        } else if (strcasecmp(input, "n") == 0) {
            free(input);
            return false;
        }
        printf(YELLOW "Please enter 'Y' or 'N'.\n" RESET);
        free(input);
    }
}

// Displays a list with a header
void show_list(const char *list[], size_t size, const char *header) {
    printf(BOLD "%s\n" RESET, header);
    for (size_t i = 0; i < size; ++i) {
        printf("  %zu - %s\n", i + 1, list[i]);
    }
    printf("  0 - Exit\n");
}

// Allows the user to select an index from a list
int select_index(const char *list[], size_t size, const char *header) {
    show_list(list, size, header);

    int index;
    while (true) {
        index = read_int("Type your option: ");
        if (index >= 0 && index <= (int)size) {
            return index - 1; // Return zero-based index
        }
        printf(RED "Invalid option. Please select a valid index.\n" RESET);
    }
}

void goBackAndWait() {
    char input[10]; // Set a maximum size for the input string
    do {
        // Clear the input buffer
        while (getchar() != '\n'); // Clear any characters in the buffer

        printf("\nPress \"0\" to go back: ");
        fgets(input, sizeof(input), stdin); // Read the line from the console

        // Remove the '\n' that is added by fgets
        input[strcspn(input, "\n")] = '\0';
    } while (strcmp(input, "0") != 0); // Continue until the input is ‘0’
}

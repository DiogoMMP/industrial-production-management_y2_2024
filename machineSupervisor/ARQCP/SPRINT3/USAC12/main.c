#include <stdio.h>
#include <stdlib.h> // For dynamic memory allocation
#include "assembly_funcs.h" // Include the assembly function declarations

// Machine structure
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

// Function prototypes
void initialize_machine(struct machine *m, const char *id, const char *name, int buffer_size, int median_window);
void free_machine(struct machine *m);
void log_value(struct machine *m, int value);
void display_buffer(const struct machine *m);
void calculate_moving_median(const struct machine *m);

// Initialize the machine structure
void initialize_machine(struct machine *m, const char *id, const char *name, int buffer_size, int median_window) {
    snprintf(m->id, sizeof(m->id), "%s", id);
    snprintf(m->name, sizeof(m->name), "%s", name);
    m->temperature_min = -10.0f; // Example values
    m->temperature_max = 50.0f;
    m->humidity_min = 20.0f;
    m->humidity_max = 80.0f;
    m->buffer_size = buffer_size;    // Set buffer size from input
    m->median_window = median_window; // Set median window size

    // Dynamically allocate memory for the buffer
    m->buffer = (int *)malloc(sizeof(int) * buffer_size);
    if (m->buffer == NULL) {
        printf("Error allocating memory for buffer!\n");
        exit(1); // Exit if memory allocation fails
    }

    // Initialize the buffer to zero
    for (int i = 0; i < buffer_size; i++) {
        m->buffer[i] = 0;
    }

    // Initialize the head and tail pointers to the buffer
    m->head = m->buffer;
    m->tail = m->buffer;
}

// Free the allocated memory for the machine's buffer
void free_machine(struct machine *m) {
    free(m->buffer); // Free the dynamically allocated buffer
}

// Function to log a value into the circular buffer
void log_value(struct machine *m, int value) {
    if ((m->head + 1) % m->buffer_size == m->tail) {
        // Buffer is full, overwrite oldest value (tail)
        m->tail = (m->tail + 1) % m->buffer_size;
        printf("Buffer full. Overwriting oldest value.\n");
    }

    // Insert the new value at the head
    *m->head = value;
    m->head = (m->head + 1) % m->buffer_size;
    printf("Logged value: %d\n", value);
}

// Function to display the circular buffer
void display_buffer(const struct machine *m) {
    printf("Circular Buffer: [");
    int *i = m->tail;
    while (i != m->head) {
        printf("%d", *i);
        i = (i + 1) % m->buffer_size;
        if (i != m->head) {
            printf(", ");
        }
    }
    printf("]\n");
}

// Function to calculate the moving median
void calculate_moving_median(const struct machine *m) {
    // Dynamically allocate a temporary array to hold values for median calculation
    int *sorted_array = (int *)malloc(m->buffer_size * sizeof(int)); // Allocate memory for the sorted array
    if (sorted_array == NULL) {
        printf("Memory allocation failed!\n");
        return; // If allocation fails, exit the function
    }

    int median_value;

    // Determine the number of valid elements (it should be the minimum of `median_window` or the available elements)
    int length = 0;
    int *i = m->head;
    while (i != m->tail && length < m->median_window) {
        i = (i - 1 + m->buffer_size) % m->buffer_size; // Move backwards through the buffer
        length++;
    }

    // If there are no elements to calculate the median, return early
    if (length == 0) {
        printf("No data available to calculate moving median.\n");
        free(sorted_array); // Free the allocated memory before returning
        return;
    }

    // Collect the last `length` values
    i = m->head;
    for (int j = 0; j < length; j++) {
        i = (i - 1 + m->buffer_size) % m->buffer_size; // Move backwards through the buffer
        sorted_array[j] = *i;
    }

    // Call the assembly function to calculate the median
    median(sorted_array, length, &median_value);  // Now simply get the median_value directly

    printf("Moving Median: %d\n", median_value);

    free(sorted_array); // Free the allocated memory after use
}



// Main function
int main() {
    // Initialize the machine with a buffer size of 10 and median window size of 5
    struct machine m;
    initialize_machine(&m, "M001", "Machine1", 10, 5);

    // Log values into the circular buffer
    log_value(&m, 10);
    log_value(&m, 20);
    log_value(&m, 30);
    log_value(&m, 40);
    log_value(&m, 50);
    log_value(&m, 60); // Overwrites the oldest value if buffer is full

    // Display the buffer and calculate the moving median
    display_buffer(&m);
    calculate_moving_median(&m);

    // Free dynamically allocated memory
    free_machine(&m);

    return 0;
}

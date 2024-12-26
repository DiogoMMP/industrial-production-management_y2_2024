#ifndef MACHINE_H
#define MACHINE_H

typedef struct {
    char id[10];
    char name[20];
    float temperature_min;
    float temperature_max;
    float humidity_min;
    float humidity_max;
    struct buffer_data *buffer;  // Pointer to dynamically allocated circular buffer
    int buffer_size;            // Circular buffer length
    struct buffer_data *head;   // Points to the newest element
    struct buffer_data *tail;   // Points to the oldest element
    int median_window;          // Moving median window length
    char state[4];              // State: "OP", "ON", or "OFF"
    struct operation *operations; // Pointer to dynamically allocated array of operations
    int operation_count;        // Current count of operations
    int operation_capacity;     // Current capacity of the operations array
} Machine;

#endif // MACHINE_H

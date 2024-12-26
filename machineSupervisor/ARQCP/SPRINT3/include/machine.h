#ifndef MACHINE_H
#define MACHINE_H

#include "buffer_data.h"
#include "operation.h"

typedef struct {
    char id[10];
    char name[20];
    float temperature_min;
    float temperature_max;
    float humidity_min;
    float humidity_max;
    buffer_data *buffer;                    // Pointer to dynamically allocated circular buffer
    int buffer_size;                        // Circular buffer length
    buffer_data *head;                      // Points to the newest element
    buffer_data *tail;                      // Points to the oldest element
    int median_window;                      // Moving median window length
    char state[4];                          // State: "OP", "ON", or "OFF"
    Operation *operations;                  // Pointer to dynamically allocated array of operations
    int operation_count;                    // Current count of operations
    int operation_capacity;                 // Current capacity of the operations array
    Operation assigned_operation;           // The assigned operation
} Machine;

#endif // MACHINE_H

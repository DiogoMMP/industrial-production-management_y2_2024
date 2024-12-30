#ifndef MACHINE_H
#define MACHINE_H

#include "buffer_data.h"
#include "operation.h"

typedef struct {
    char *id;
    char *name;
    float temperature_min;
    float temperature_max;
    float humidity_min;
    float humidity_max;
    buffer_data *buffer;                    // Pointer to dynamically allocated circular buffer
    int buffer_size;                        // Circular buffer length
    buffer_data *head;                      // Points to the newest element
    buffer_data *tail;                      // Points to the oldest element
    int median_window;                      // Moving median window length
    buffer_data *moving_median;             // Pointer to the moving median array
    int moving_median_count;                // Current count of moving median array
    int moving_median_capacity;             // Current capacity of the moving median array
    char *state;                            // State: "OP", "ON", or "OFF"
    Operation *operations;                  // Pointer to dynamically allocated array of operations
    int operation_count;                    // Current count of operations
    int operation_capacity;                 // Current capacity of the operations array
    Operation assigned_operation;           // The assigned operation
    Operation *exec_operation;              // Pointer to the executed operations array
    int exec_operation_count;               // Current count of executed operations
    int exec_operation_capacity;            // Current capacity of the executed operations array
} Machine;

int update_machine(char* cmd);

#endif // MACHINE_H
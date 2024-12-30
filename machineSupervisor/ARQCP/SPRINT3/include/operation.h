#ifndef OPERATION_H
#define OPERATION_H

#include <time.h>     // For timestamp

// Define a structure to hold operation details
typedef struct {
    int number;                 // Operation number (0-31)
    char *designation;          // Dynamic memory for operation designation
    char *id;                   // Associated Machine ID
    int time_duration;          // Time duration of operation
    time_t timestamp;           // Timestamp of the beginning of the operation
    
} Operation;

#endif // OPERATION_H

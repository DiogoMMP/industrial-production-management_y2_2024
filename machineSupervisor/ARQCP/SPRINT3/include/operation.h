#ifndef OPERATION_H
#define OPERATION_H

#include <time.h>     // For timestamp

// Define a structure to hold operation details
typedef struct {
    char designation[20];    // Operation designation
    int number;              // Operation number (0-31)
    time_t timestamp;        // Timestamp of the beginning of the operation

} Operation;

#endif // OPERATION_H

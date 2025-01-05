#ifndef INSTANCE_H
#define INSTANCE_H

typedef struct {
    char *machine_id;
    char *state;
    int operation_id;
    int last_temperature; // Add this line
    int last_humidity;    // Add this line
} Instance;

#endif // INSTANCE_H
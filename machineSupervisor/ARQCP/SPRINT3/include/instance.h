#ifndef INSTANCE_H
#define INSTANCE_H

typedef struct {
    char *machine_id;
    char *state;
    int operation_id;
    float last_temperature; // Add this line
    float last_humidity;    // Add this line
} Instance;

#endif // INSTANCE_H
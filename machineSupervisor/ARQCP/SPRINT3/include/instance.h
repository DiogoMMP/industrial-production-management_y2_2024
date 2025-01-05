#ifndef INSTANCE_H
#define INSTANCE_H

typedef struct {
    char *machine_id;
    char *state;
    int operation_id;
    int last_temperature; 
    int last_humidity;    
} Instance;

#endif // INSTANCE_H
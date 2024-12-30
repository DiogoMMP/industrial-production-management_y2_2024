#ifndef INSTANCE_H
#define INSTANCE_H

typedef struct {
    char machine_id[10];
    char state[10];
    int operation_id;
} Instance;

#endif // INSTANCE_H
#ifndef MACHMANAGER_H
#define MACHMANAGER_H

#include "machine.h"
#include "instance.h"

// Type definitions for machine information and sensor data

// MachManager Struct
typedef struct {
    Machine* machines;       // Pointer to an array of machines
    int machine_count;              // Number of machines in the system
    int machine_capacity;           // Current capacity of the machines array
    void* internal_data;            // Internal data (temporary information for control)
} MachManager;

// Functions for the `MachManager`:

void create_machmanager(MachManager *machmanager, Machine *machines, int machine_count, int machine_capacity);

void send_cmd_to_machine(char* cmd);

Machine* find_machine(MachManager *machmanager, const char* machine_id);

void assign_operation_to_machine(Operation *op, Machine *m);

void free_machine(Machine *m);

int setup_machines_from_file(const char *filename, MachManager *machmanager);

void export_operations_to_csv(Machine *m);

void feed_system(const char* filename, MachManager* manager);

void extract_data_machine(const char* str, void* data);

void check_for_alerts(Machine *m);

Instance* wait_for_instructions_from_ui(void);

void update_internal_data(Machine* m);

void* get_cmd_from_internal_data(void);

char* wait_for_data_from_machine(void);

void update_internal_data_with_new_data(void* data);

void main_loop(MachManager *machmanager);

#endif // MACHMANAGER_H

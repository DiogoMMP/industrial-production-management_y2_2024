#ifndef MACHMANAGER_H
#define MACHMANAGER_H

#include "instance.h"  // Include instance.h to define the Instance type
#include "machine.h"   // Include machine.h to define the Machine type

typedef struct {
    Machine* machines;       // Pointer to an array of machines
    int machine_count;              // Number of machines in the system
    int machine_capacity;           // Current capacity of the machines array
    void* internal_data;            // Internal data (temporary information for control)
    int running;                    // Flag to control the main loop
} MachManager;

void create_machmanager(MachManager *machmanager, Machine *machines, int machine_count, int machine_capacity);
void free_operation(Operation *op);
void send_cmd_to_machine(char *cmd);
void send_cmd_to_machine_No_UI(char *cmd);
Machine* find_machine(MachManager *machmanager, const char* machine_id);
void assign_operation_to_machine(Operation *op, Machine *m);
void free_machine(Machine *m);
int setup_machines_from_file(const char *filename, MachManager *machmanager);
void export_operations_to_csv(Machine *m);
int find_operation_id_in_all_machines(MachManager* manager, char* operation_description);
void feed_system(const char* filename, MachManager* manager);
void extract_data_machine(const char *str, buffer_data *data);
void check_for_alerts(Machine *m);
Instance* wait_for_instructions_from_ui(MachManager *machmanager);
void update_internal_data(Machine *m, buffer_data *new_data);
void enqueue_buffer_data(Machine *m, buffer_data *new_data);
char* get_cmd_from_internal_data(const char* state, int operation_id);
char* wait_for_data_from_machine(Instance *instr, Machine *machine);
void calculate_moving_median(Machine *machine);
void update_internal_data_with_new_data(MachManager *machmanager, buffer_data *data);
void* main_loop_thread(void* arg);
void start_main_loop(Machine *machine);
void stop_program(MachManager *machmanager);
void main_loop(MachManager *machmanager);
void add_machine(MachManager *machmanager, const char *id, const char *name, int temp_min, int temp_max, int hum_min, int hum_max);
void remove_machine(MachManager *machmanager, const char *id);
void read_status_machine(MachManager *machmanager, const char *id);

#endif // MACHMANAGER_H
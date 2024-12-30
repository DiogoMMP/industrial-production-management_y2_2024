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

// Structure for storing alert data
typedef struct {
    int machine_id; // Machine identifier
    char alert_message[256]; // Alert message (e.g. ‘Temperature out of range’)
} Alert;

// Functions for the `MachManager`:

void free_machine(Machine *m);

void create_machmanager(MachManager *machmanager, Machine *machines, int machine_count, int machine_capacity);

int setup_machines_from_file(const char *filename, MachManager *machmanager);

void export_operations_to_csv(Machine *m);

// Function to wait for instructions from the UI
// Returns a pointer to the structure containing the instructions received
Instance* wait_for_instructions_from_ui(void);

// Function to update the `MachManager` internal data based on instructions
// Receives a pointer to the instructions and updates the internal data
void update_internal_data(Machine *m);

// Function to get the command from the updated internal data
// Returns a pointer to the generated command
void* get_cmd_from_internal_data(void);

// Function to send the generated command to the machine
// Receives the pointer to the command to be sent
void send_cmd_to_machine(char *cmd);

// Function to wait for the machine's response
// Returns a string containing the data received from the machine
char* wait_for_data_from_machine(void);

// Function to extract data from the string received from the machine
// The data string is processed and stored in the `data` structure
void extract_data_machine(const char* str, void* data);

// Function to update the internal data with the extracted data
// Receives the extracted data and updates the internal structures
void update_internal_data_with_new_data(void* data);

// Function to check for alerts based on current data (temperature/humidity)
void check_for_alerts(Machine *m);

void feed_system(const char* filename, MachManager* manager);

#endif // MACHMANAGER_H
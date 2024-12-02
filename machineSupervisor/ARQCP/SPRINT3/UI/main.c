#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>  // For sleep function (POSIX)

#include "usac.h"  // Include the assembly functions

// Define a structure for machine data
struct machine {
    char id[10];
    char name[20];
    float temperature_min;
    float temperature_max;
    float humidity_min;
    float humidity_max;
    int *buffer;         // Pointer to dynamically allocated circular buffer
    int buffer_size;     // Circular buffer length
    int *head;           // Points to the newest element
    int *tail;           // Points to the oldest element
    int median_size;     // Moving median length
    pthread_mutex_t lock;  // Mutex for thread safety
};

// Function to read temperature from the sensor (stub function)
float read_temp_from_sensor() {
    return 25.0 + (rand() % 10);  // Example: Random temperature between 25 and 35
}

// Function to read humidity from the sensor (stub function)
float read_hum_from_sensor() {
    return 50.0 + (rand() % 20);  // Example: Random humidity between 50 and 70
}

// Function to send data to MachManager (stub function)
void send_data(const char *str) {
    printf("Sending data: %s\n", str);
}

// Function to turn on LEDs based on the command (stub function)
void turn_on_leds(const char *cmd) {
    printf("Turning on LEDs with command: %s\n", cmd);
}

// Function to turn off LEDs (stub function)
void turn_off_leds() {
    printf("Turning off LEDs.\n");
}

// Function to wait for the command from MachManager (stub function)
void wait_for_command_from_mach_manager(char *cmd) {
    strcpy(cmd, "ON,1,1,0,1,0");  // Simulate receiving a dummy command
}

// Machine component function
void *machine_function(void *arg) {
    struct machine *m = (struct machine *)arg;

    char cmd[50];  // Buffer for the command
    char str[100]; // Buffer for the formatted string
    float temp, hum;  // Temperature and Humidity variables

    while (1) {
        // Wait for command from MachManager
        wait_for_command_from_mach_manager(cmd);

        // Read sensor values
        temp = read_temp_from_sensor();
        hum = read_hum_from_sensor();

        // Example usage of 'm' to check if sensor readings are within range
        if (temp < m->temperature_min || temp > m->temperature_max) {
            printf("Alert: Temperature out of range!\n");
        }
        if (hum < m->humidity_min || hum > m->humidity_max) {
            printf("Alert: Humidity out of range!\n");
        }

        // Format the data
        sprintf(str, "TEMP&unit:celsius&value:%.2f#HUM&unit:percentage&value:%.2f", temp, hum);

        // Send the formatted string to MachManager
        send_data(str);

        // Turn on LEDs based on the received command
        turn_on_leds(cmd);

        // Sleep for 2 seconds
        sleep(2);

        // Turn off LEDs
        turn_off_leds();
    }

    return NULL;
}


// Function to check alerts based on filtered sensor data
void check_for_alerts(struct machine *m) {
    int median_temp, median_hum;
    int values[m->median_size];

    pthread_mutex_lock(&m->lock);  // Lock for thread safety

    // Move the last n sensor readings into the array
    move_n_to_array(m->buffer, m->buffer_size, m->tail, m->head, m->median_size, values);

    pthread_mutex_unlock(&m->lock);  // Unlock after reading

    // Sort the array before calculating the median
    sort_array(values, m->median_size, 'A');  // Sort in ascending order
    median(values, m->median_size, &median_temp);

    sort_array(values, m->median_size, 'D');  // Sort in descending order
    median(values, m->median_size, &median_hum);

    // Check if the median values are outside the acceptable range
    if (median_temp < m->temperature_min || median_temp > m->temperature_max) {
        printf("Alert: Temperature out of range!\n");
    }
    if (median_hum < m->humidity_min || median_hum > m->humidity_max) {
        printf("Alert: Humidity out of range!\n");
    }
}

// Function to manage machine operations based on instructions
void *mach_manager_function(void *arg) {
    struct machine *m = (struct machine *)arg;

    char inst[100];  // Instructions from UI
    char cmd[50];  // Command to machine
    char str[100];  // String for received data

    while (1) {
        // Wait for instructions from UI
        printf("Waiting for instructions...\n");
        strcpy(inst, "start_operation");  // Dummy instruction

        // Update internal data based on instructions
        printf("Updating internal data: %s\n", inst);

        // Get command from internal data
        strcpy(cmd, "ON,1,1,0,1,0");

        // Send the command to the machine
        printf("Sending command to machine: %s\n", cmd);

        // Wait for data from the machine (simulating here)
        sprintf(str, "TEMP&unit:celsius&value:25.0#HUM&unit:percentage&value:50.0");

        // Extract and process the received data
        printf("Received data: %s\n", str);

        // Check for alerts based on the current machine state and sensor data
        check_for_alerts(m);

        // Simulate a short delay
        sleep(2);
    }

    return NULL;
}

int main() {
    setbuf(stdout, NULL);  // Disable stdout buffering for immediate output

    // Initialize machine data
    struct machine data;
    data.temperature_min = 18.0;
    data.temperature_max = 30.0;
    data.humidity_min = 40.0;
    data.humidity_max = 60.0;
    data.median_size = 5;
    data.buffer_size = 10;
    data.buffer = (int *)malloc(data.buffer_size * sizeof(int));
    data.head = data.buffer;
    data.tail = data.buffer;
    pthread_mutex_init(&data.lock, NULL);

    // Create threads for machine manager and machine function
    pthread_t mach_manager_thread, machine_thread;
    pthread_create(&mach_manager_thread, NULL, mach_manager_function, &data);
    pthread_create(&machine_thread, NULL, machine_function, &data);

    // Wait for threads to complete (they won't in this infinite loop)
    pthread_join(mach_manager_thread, NULL);
    pthread_join(machine_thread, NULL);

    // Cleanup
    free(data.buffer);
    pthread_mutex_destroy(&data.lock);

    return 0;
}

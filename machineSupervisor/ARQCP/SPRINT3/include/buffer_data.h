#ifndef BUFFER_DATA_H
#define BUFFER_DATA_H

typedef struct buffer_data {
    float temperature;
    float humidity;
    struct buffer_data *next; // Add this line if it doesn't exist
} buffer_data;

#endif // BUFFER_DATA_H
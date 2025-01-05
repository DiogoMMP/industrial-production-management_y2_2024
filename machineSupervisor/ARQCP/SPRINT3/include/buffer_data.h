#ifndef BUFFER_DATA_H
#define BUFFER_DATA_H

typedef struct buffer_data {
    int temperature;
    int humidity;
    struct buffer_data *next; // Add this line if it doesn't exist
} buffer_data;

#endif // BUFFER_DATA_H
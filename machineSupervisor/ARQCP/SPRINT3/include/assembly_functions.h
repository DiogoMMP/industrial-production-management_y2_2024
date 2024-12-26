#ifndef ASSEBLY_FUNCTIONS_H
#define ASSEBLY_FUNCTIONS_H

int extract_data(char* str, char* token, char* unit, int* value); //USAC01
int get_number_binary(int n, char* bits); //USAC02
int get_number(char* str, int* n); //USAC03
int format_command(char* op, int n, char *cmd); //USAC04
int enqueue_value(int *buffer, int length, int *tail, int *head, int value); //USAC05
int dequeue_value(int *buffer, int length, int *tail, int *head, int *value); //USAC06
int get_n_element(int* buffer, int length, int* tail, int* head); //USAC07
int move_n_to_array(int* buffer, int length, int* tail, int* head, int n, int* array); //USAC08
int sort_array(int* vec, int length, char order); //USAC09
int median(int* vec, int length, int *me); //USAC10



#endif // ASSEBLY_FUNCTIONS_H
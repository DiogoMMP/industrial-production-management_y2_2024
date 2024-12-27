#ifndef UTILS_H
#define UTILS_H

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>

// Console color codes
#define RESET "\033[0m"
#define BOLD "\033[1m"
#define RED "\033[31m"
#define GREEN "\033[32m"
#define YELLOW "\033[33m"
#define BLUE "\033[34m"
#define CYAN "\033[36m"

// Function declarations
void* safe_malloc(size_t size);
char *read_line(const char *prompt);
int read_int(const char *prompt);
double read_double(const char *prompt);
bool confirm(const char *message);
void show_list(const char *list[], size_t size, const char *header);
int select_index(const char *list[], size_t size, const char *header);
void goBackAndWait();

#endif

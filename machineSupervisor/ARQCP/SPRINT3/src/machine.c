#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>    // For error handling

#include "machine.h"


// Free the allocated memory for the machine's buffer
void free_machine(Machine *m) {
    if (m->buffer) {
        free(m->buffer);  // Free the dynamically allocated buffer
        m->buffer = NULL; // Reset pointer to avoid dangling reference
    }
    if (m->operations) {
        free(m->operations); // Free the dynamically allocated operations array
        m->operations = NULL; // Reset pointer to avoid dangling reference
    }
}

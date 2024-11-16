#include <stdio.h>
#include "asm.h"

int main(){
    // Define the array to be tested
    int array[] = {5, 3, 9, 1, 6, 2};

    // Define variables
    int median_value;
    int length = sizeof(array) / sizeof(array[0]);  // Calculate length of the array

    // Call the median function
    int result = median(array, length, &median_value);

    // Check if the median function succeeded
    if (result == 1) {
        printf("Median: %d\n", median_value);  // Print the median value
    } else {
        printf("Error: Invalid array length\n");
    }
    return 0;
}
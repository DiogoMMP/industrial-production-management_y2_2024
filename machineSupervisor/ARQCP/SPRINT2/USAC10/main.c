#include <stdio.h>
#include "asm.h"
#include "sort_array.h"

int main(){
    // Define the array to be tested
    int array[] = {1, 3, 2, 4, 5, 6};
    int array2[] = {0,3000,10,20,0,300};

    // Define variables
    int median_value;
    int * ptr_median_value = &median_value;
    int length = sizeof(array) / sizeof(array[0]);  // Calculate length of the array
    int length2 = sizeof(array2) / sizeof(array2[0]);  // Calculate length of the array



    // Call the median function
    int result = median(array, length, ptr_median_value);
    if (result == 1) {
        printf("Median: %d\n", *ptr_median_value);  // Print the median value
    } else {
        printf("Error: Invalid array length\n");
    }

        // Print the sorted arrays
    printf("Sorted array: ");
    for (int i = 0; i < length; i++) {
        printf("%d ", array[i]);
    }
    printf("\n");

    int result2 = median(array2, length2, ptr_median_value);

        if (result2 == 1) {
        printf("Median: %d\n", *ptr_median_value);  // Print the median value
    } else {
        printf("Error: Invalid array length\n");
    }

        // Print the sorted arrays
    printf("Sorted array: ");
    for (int i = 0; i < length2; i++) {
        printf("%d ", array2[i]);
    }
    printf("\n");

    return 0;

}
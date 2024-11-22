#include <stdio.h>
#include <string.h>
#include "asm.h"
#include "get_n_element.h"
#include "dequeue_value.h"

void print_array(const int *array, int size) {
    for (int i = 0; i < size; i++) {
        printf("%d ", array[i]);
    }
    printf("\n");
}

void run_test(int* buffer, int length, int tail, int head, int n, int expected_res, int* expected_array, int expected_tail, int expected_head) {
    int array[10] = {0};  // Ensure this is large enough for the tests
    int res = move_n_to_array(buffer, length, &tail, &head, n, array);

    printf("Test with buffer={");
    for (int i = 0; i < length; i++) {
        printf("%d", buffer[i]);
        if (i < length - 1) printf(", ");
    }
    printf("}, length=%d, tail=%d, head=%d, n=%d\n", length, tail, head, n);
    printf("Expected result: %d\n", expected_res);
    printf("Result obtained: %d\n", res);

    if (res == expected_res) {
        if (res == 1) {
            printf("Expected array: ");
            print_array(expected_array, n);
            printf("Array obtained: ");
            print_array(array, n);
        }
        printf("Expected tail: %d, Obtained tail: %d\n", expected_tail, tail);
        printf("Expected head: %d, Obtained head: %d\n", expected_head, head);
        printf("Test PASS\n\n");
    } else {
        printf("Test FAIL\n\n");
    }
}

int main() {
    run_test((int[]){0, 0, 0}, 3, 0, 0, 1, 0, (int[]){0, 0, 0}, 0, 0);
    run_test((int[]){1, 0, 0}, 3, 0, 1, 1, 1, (int[]){1}, 1, 1);
    run_test((int[]){1, 2, 3, 4}, 4, 3, 2, 3, 1, (int[]){4, 1, 2}, 2, 2);
    run_test((int[]){1, 2, 3, 4}, 4, 2, 1, 3, 1, (int[]){3, 4, 1}, 1, 1);

    return 0;
}

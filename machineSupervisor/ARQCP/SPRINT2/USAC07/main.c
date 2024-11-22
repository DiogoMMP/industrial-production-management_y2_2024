#include <stdio.h>
#include "asm.h"


int main() {
    int buffer[10];  // A buffer with 10 elements
    int num = 10;    // Number of elements in the buffer
    int tail, head;  // Variables for tail and head positions
    int result;

    // Test 1: Buffer is empty (tail == head)
    tail = 0;
    head = 0;
    result = get_n_element(buffer, num, &tail, &head);
    printf("Test 1 (Empty buffer): Expected: 0, Result: %d\n", result);

    // Test 2: Buffer is full (head one position behind tail in a circular buffer)
    tail = 0;
    head = 9;  // Full buffer example in circular logic
    result = get_n_element(buffer, num, &tail, &head);
    printf("Test 2 (Full buffer): Expected: %d, Result: %d\n", num-1, result);

    // Test 3: Buffer has some elements (head > tail)
    tail = 2;
    head = 6;
    result = get_n_element(buffer, num, &tail, &head);
    printf("Test 3 (Partially filled buffer, head > tail): Expected: 4, Result: %d\n", result);

    // Test 4: Circular buffer (tail > head)
    tail = 8;
    head = 3;  // In a circular buffer, this should calculate the wrap-around difference
    result = get_n_element(buffer, num, &tail, &head);
    printf("Test 4 (Circular buffer): Expected: 5, Result: %d\n", result);

    return 0;
}
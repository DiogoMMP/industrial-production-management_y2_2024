#include <stdio.h>
#include "asm.h"

int main() {
    char str[] = "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
    char token[] = "TEMP";
    char unit[20];
    int value;
    int res = extract_data(str, token, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 1: celsius, 20

    char token2[] = " AAA ";
    res = extract_data(str, token2, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 0: , 0

    char token3[] = "HUM";
    res = extract_data(str, token3, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 1: percentage, 80

    char token4[] = "LEN";
    res = extract_data(str, token4, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 0: , 0

    char token5[] = "EMP";
    res = extract_data(str, token5, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 0: , 0

    char token6[] = "UM";
    res = extract_data(str, token6, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 0: , 0

    char token7[] = "";
    char str2[] = "";
    res = extract_data(str2, token7, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 0: , 0

    return 0;
}
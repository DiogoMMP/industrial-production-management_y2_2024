#include <stdio.h>
#include "asm.h"

int main() {
    char str[] = "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
    char token[] = "TEMP";
    char unit[20];
    int value;
    int res = extract_token(str, token, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 1: celsius, 20

    char token2[] = " AAA ";
    res = extract_token(str, token2, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 0: , 0

    char token3[] = "HUM";
    res = extract_token(str, token3, unit, &value);
    printf("%d: %s, %d\n", res, unit, value); // Expected output: 1: percentage, 80
    return 0;
}
#include <stdio.h>
#include <string.h>
#include "asm.h"
#include "get_number_binary.h"

int compare_strings(const char* str1, const char* str2) {
    while (*str1 && *str2) {
        if (*str1 != *str2) {
            return 0;
        }
        str1++;
        str2++;
    }
    return *str1 == *str2;
}

void run_test(char* str, int value, int expected_res, const char* expected_cmd) {
    char cmd[20];  // Ensure this is large enough for the tests
    int res = format_command(str, value, cmd);
    printf("Test with str=\"%s\", value=%d\n", str, value);
    printf("Expected result: %d, \"%s\"\n", expected_res, expected_cmd);
    printf("Result obtained: %d, \"%s\"\n", res, cmd);
    printf("Test %s\n\n", (res == expected_res && compare_strings(cmd, expected_cmd)) ? "PASS" : "FAIL");
}

void test_Null() { 
    run_test("", 0, 0, ""); 
}

void test_One() { 
    run_test(" op ", 23, 1, "OP,1,0,1,1,1"); 
}

void test_Zero() { 
    run_test(" on ", 8, 1, "ON,0,1,0,0,0"); 
}

void test_Three() { 
    run_test(" ofF ", 17, 1, "OFF,1,0,0,0,1"); 
}

void test_Four() { 
    run_test("Off", 7, 1, "OFF,0,0,1,1,1"); 
}

void test_Five() { 
    run_test("oN      ", 15, 1, "ON,0,1,1,1,1"); 
}

void test_MinusOne() { 
    run_test(" cmD ", -1, 0, ""); 
}

void test_SixtyFour() { 
    run_test(" shdhsdh %444 sdjshd", 64, 0, "");  
}

void test_Forty() { 
    run_test("40  adsads", 40, 0, "");  
}

void test_Offy() { 
    run_test("Offy   ", 0, 0, "");  
}

void test_Offy2() { 
    run_test("Off y  2   ", 0, 0, "");  
}

void test_CMD() { 
    run_test("CmD   ", 0, 0, "");  
}

int main() {
    test_Null();
    test_One();
    test_Zero();
    test_Three();
    test_Four();
    test_Five();
    test_MinusOne();
    test_SixtyFour();
    test_Forty();
    test_Offy();
    test_Offy2();
    test_CMD();

    return 0;
}
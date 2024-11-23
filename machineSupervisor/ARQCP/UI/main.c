#include <stdio.h>
#include <string.h>
#include "usac.h" // Includes the declarations of the functions implemented in the project
#define MAX_LENGTH 8

// Function 1: Extract Data test
void test_extract_data() {
    char str[] = "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
    char token[] = "TEMP";
    char unit[20];
    int value;

    // Asks the user whether they want to use the default values or enter their own
    printf("\n\n--- Test Extract Data ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);
    if (choice == 'N' || choice == 'n') {
        printf("Enter the input string (e.g., TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80): ");
        scanf(" %[^\n]", str);  // Read the whole line
        printf("Enter the token (TEMP or HUM): ");
        scanf("%s", token);
    } else {
        printf("Input String: %s\n", str);
    }

    int result = extract_data(str, token, unit, &value);

    if (result == 1) {
        printf("%d:%s,%d\n", result, unit, value);
    } else {
        printf("%d: \n", result);
    }
    
}

// Function 2: Get Binary Number Test
void test_get_number_binary() {
    int num = 26;
    char bits[5];

    // Asks the user whether they want to use the default values or enter their own
    printf("\n\n--- Test Get Binary Number ---\n");
    printf("Do you want to use default value? (Y/N): ");
    char choice;
    scanf(" %c", &choice);
    if (choice == 'N' || choice == 'n') {
        printf("Enter a number (maximum 32): ");
        scanf("%d", &num);
    } else {
        printf("Input Number: %d\n", num);
    }

    int result = get_number_binary(num, bits);
    if (result == 1){
        printf("%d: %d, %d, %d, %d, %d\n",result, bits[4], bits[3],bits[2],bits[1],bits[0]);
    } else {
        printf("%d: \n", result);
    }
}

// Function 3: Get Number test
void test_get_number() {
    char str[] = " 89 ";
    int value;

    // Asks the user whether they want to use the default values or enter their own
    printf("\n\n--- Test Get Number ---\n");
    printf("Do you want to use default value? (Y/N): ");
    char choice;
    scanf(" %c", &choice);
    if (choice == 'N' || choice == 'n') {
        printf("Enter a string representing a number: ");
        scanf("%s", str);
    } else {
        printf("Input String: %s\n", str);
    }

    int result = get_number(str, &value);

    if (result == 1){
        printf("%d: %d\n",result, value);
    } else {
        printf("%d: \n", result);
    }
    
}

// Function 4: Format Command test
void test_format_command() {
    char op[] = "       oFf     ";
    char cmd[20];
    int n = 26;

    // Asks the user whether they want to use the default values or enter their own
    printf("\n\n--- Test Format Command ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);
    if (choice == 'N' || choice == 'n') {
        printf("Input String (OP, ON or OFF): ");
        scanf("%s", op);
        printf("Enter the number: ");
        scanf("%d", &n);
    } else {
        printf("Input String: '%s'\n", op);
        printf("Input Number: %d\n", n);
    }

    int result = format_command(op, n, cmd);

    if (result == 1){
        printf("%d: %s\n",result, cmd);
    } else {
        printf("%d: \n", result);
    }

}

// Function 5: Enqueue Value Test
void test_enqueue_value() {
    int buffer[MAX_LENGTH] = {0};  // Initialize buffer
    int length = MAX_LENGTH, tail = 1, head = 0;

    printf("\n\n--- Test Enqueue Value ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);

    printf("Buffer before enqueue: {");
    for (int i = 0; i < length; i++) {
        printf("%d", buffer[i]);
        if (i < length - 1) printf(", ");
    }
    printf("}\n");

    if (choice == 'Y' || choice == 'y') {
        // Default values
        int value = 50;
        int result = enqueue_value(buffer, length, &tail, &head, value);
        printf("Result: %d | Enqueued Value=%d | Tail=%d, Head=%d\n", result, value, tail, head);
    } else {
        printf("Enter values to enqueue (maximum %d, type 'stop' to finish):\n", MAX_LENGTH);
        while (1) {
            char input[10];
            printf("Enter value to enqueue: ");
            scanf("%s", input);

            if (strcmp(input, "stop") == 0) {
                break;
            }

            int value;
            if (sscanf(input, "%d", &value) == 1) {
                int result = enqueue_value(buffer, length, &tail, &head, value);
                printf("Result: %d | Enqueued Value=%d | Tail=%d, Head=%d\n", 
                       result, value, tail, head);
                
                if (result != 1) {
                    printf("Queue is full or error occurred. Stopping.\n");
                    break;
                }
            } else {
                printf("Invalid input! Please enter a valid integer or 'stop'.\n");
            }
        }
    }

    printf("Final buffer state: {");
    for (int i = 0; i < length; i++) {
        printf("%d", buffer[i]);
        if (i < length - 1) printf(", ");
    }
    printf("}\n\n");
}

// Function 6: Dequeue Value Test
void test_dequeue_value() {
    int buffer[MAX_LENGTH] = {0};  
    int length = MAX_LENGTH, tail = 0, head = 0, value;

    printf("\n\n--- Test Dequeue Value ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);

    if (choice == 'Y' || choice == 'y') {
        // Valores padrão
        buffer[0] = 50;
        buffer[1] = 60;
        tail = 0;    // O elemento mais antigo está na posição 0
        head = 2;    // Temos 2 elementos no buffer
        
        printf("\nInitial buffer state: {");
        for (int i = 0; i < length; i++) {
            printf("%d", buffer[i]);
            if (i < length - 1) printf(", ");
        }
        printf("}\n");
        printf("Initial positions: Tail=%d, Head=%d\n", tail, head);

        // Fazer dequeue do elemento na tail
        int result = dequeue_value(buffer, length, &tail, &head, &value);
        printf("\nResult: %d | Dequeued Value=%d | New Tail=%d, Head=%d\n", 
               result, value, tail, head);

    } else {
        // Permite ao utilizador definir o buffer inicial
        printf("Enter buffer values (maximum %d, type 'stop' to finish):\n", MAX_LENGTH);
        head = 0;
        while (head < MAX_LENGTH) {
            char input[10];
            printf("Enter value %d: ", head + 1);
            scanf("%s", input);

            if (strcmp(input, "stop") == 0) {
                break;
            }

            if (sscanf(input, "%d", &buffer[head]) == 1) {
                head++;
            } else {
                printf("Invalid input! Please enter a valid integer or 'stop'.\n");
            }
        }

        // Define tail (posição do elemento mais antigo)
        printf("Enter tail position (0-%d): ", head-1);
        scanf("%d", &tail);

        if (tail < 0 || tail >= head) {
            printf("Invalid tail position!\n");
            return;
        }

        printf("\nInitial buffer state: {");
        for (int i = 0; i < length; i++) {
            printf("%d", buffer[i]);
            if (i < length - 1) printf(", ");
        }
        printf("}\n");
        printf("Initial positions: Tail=%d, Head=%d\n", tail, head);

        // Fazer dequeue do elemento na tail
        int result = dequeue_value(buffer, length, &tail, &head, &value);
        printf("\nResult: %d | Dequeued Value=%d | New Tail=%d, Head=%d\n", 
               result, value, tail, head);
    }

    // Mostrar estado final do buffer
    printf("\nFinal buffer state: {");
    for (int i = 0; i < length; i++) {
        printf("%d", buffer[i]);
        if (i < length - 1) printf(", ");
    }
    printf("}\n\n");
}

// Function 7: Get N Element Test
void test_get_n_element() {
    int buffer[MAX_LENGTH] = {0};  // Initialises with zeros
    int length = 0, tail = 0, head = 0;

    // Asks the user if they want to use the default values
    printf("\n\n--- Test Get N Element ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);

    // Configuration of default values
    if (choice == 'Y' || choice == 'y') {
        int default_values[] = {10, 20, 30, 40, 50};
        length = 5;
        tail = 0;
        head = length;
        for (int i = 0; i < length; i++) {
            buffer[i] = default_values[i];
        }
        printf("Buffer: {");
        for (int i = 0; i < length; i++) {
            printf("%d", buffer[i]);
            if (i < length - 1) printf(", ");
        }
        printf("}\n");
    }

    // Configuration of customised values
    if (choice == 'N' || choice == 'n') {
        printf("Enter the buffer values (maximum %d, type 'stop' to finish):\n", MAX_LENGTH);
        while (length < MAX_LENGTH) {
            char input[10];
            printf("Enter value %d: ", length + 1);
            scanf("%s", input);

            if (strcmp(input, "stop") == 0) {
                break;
            }

            if (sscanf(input, "%d", &buffer[length]) == 1) {
                length++;
                head = length; 
            } else {
                printf("Invalid input! Please enter a valid integer or 'stop' to finish.\n");
            }
        }
    }
    
    printf("Buffer content: ");
    for(int i = 0; i < length; i++) {
        printf("%d ", buffer[i]);
    }
    printf("\n");

    int result = get_n_element(buffer, length, &tail, &head);
    printf("Result: %d\n\n", result);
}

// Function 8: Move N to Array Test
void test_move_n_to_array() {
    int buffer[MAX_LENGTH];  // Fixed buffer
    int length = 0, tail = 0, head = 0, n;
    int array[MAX_LENGTH];

    // Asks the user if they want to use the default values
    printf("\n\n--- Test Move N to Array ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);

    // Setting default values
    if (choice == 'Y' || choice == 'y') {
        int default_values[] = {10, 20, 30, 40, 50};
        length = 5;
        tail = 0;
        head = length;
        for (int i = 0; i < length; i++) {
            buffer[i] = default_values[i];
        }
        printf("Initial Buffer: {");
        for (int i = 0; i < length; i++) {
            printf("%d", buffer[i]);
            if (i < length - 1) printf(", ");
        }
        printf("}\n");

        // Display ‘tail’ and ‘head’ values by default
        printf("Default tail: %d, Default head: %d\n", tail, head);

        printf("Enter N (number of elements to move): ");
        scanf("%d", &n);
    }

    // Configuring customised values
    if (choice == 'N' || choice == 'n') {
        printf("Enter the buffer values (up to 8, type 'stop' to finish):\n");
        while (length < MAX_LENGTH) {
            char input[10];  // Buffer for reading the value or ‘stop’
            printf("Enter value %d: ", length + 1);
            scanf("%s", input);

            if (strcmp(input, "stop") == 0) {
                break;  // Ends input by typing ‘stop’
            }

            if (sscanf(input, "%d", &buffer[length]) == 1) {
                length++;
            } else {
                printf("Invalid input! Please enter a valid integer or 'stop' to finish.\n");
            }
        }

        tail = 0;
        head = length;

        // Display ‘tail’ and ‘head’ values by default
        printf("Default tail: %d, Default head: %d\n", tail, head);

        printf("Enter N (number of elements to move): ");
        scanf("%d", &n);
    }

    // Calls the function to move N elements
    int result = move_n_to_array(buffer, length, &tail, &head, n, array);

    if (result == 1){
        // Displays the result of the operation
        printf("Result: %d | Moved Array: {", result);
        for (int i = 0; i < n && i < length; i++) {  // Ensures you don't print over the limit
            printf("%d", array[i]);
            if (i < n - 1) printf(", ");
        }
        printf("}\n\n");
    } else {
        printf("Result: %d | Moved Array: \n\n", result);
    }
    

}

// Function 9: Sort Array Test
void test_sort_array() {
    int vec[MAX_LENGTH] = {5, 3, 9, 1, 7};  // Initial array
    int length = 5;  // Length of the array (can be changed based on user input)
    int order;

    // Asks the user whether they want to use the default values or enter their own
    printf("\n\n--- Test Sort Array ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);

    // Display initial array and parameters only if the user chooses to use the default values
    if (choice == 'Y' || choice == 'y') {
        printf("Initial Array: {");
        for (int i = 0; i < length; i++) {
            printf("%d", vec[i]);
            if (i < length - 1) printf(", ");
        }
        printf("}\n");

        printf("Initial Order: Ascending (1)\n");  // Default order is Ascending
    }

    int i = 0;

    // Handle custom input from user
    if (choice == 'N' || choice == 'n') {
        printf("Enter sorting order ('1' for ascending, '0' for descending): ");
        while (scanf("%d", &order) != 1 || (order != 1 && order != 0)) {
            // Validate that the order entered is either 1 or 0
            printf("Invalid input! Please enter '1' for ascending or '0' for descending: ");
            while (getchar() != '\n'); // Clear the buffer
        }

        printf("Enter the array values (maximum 8). Type 'stop' to finish:\n");
        for (i = 0; i < MAX_LENGTH; i++) {
            char buffer[10];  // Buffer to hold the input for the value or stop command
            printf("Enter value %d: ", i + 1);
            scanf("%s", buffer);  // Read the input as a string

            if (strcmp(buffer, "stop") == 0) {
                break;  // Exit loop if 'stop' is entered
            } else {
                // Convert the string input to an integer and store it in the array
                if (sscanf(buffer, "%d", &vec[i]) == 1) {
                    // Valid integer entered
                } else {
                    printf("Invalid input! Please enter a valid integer or 'stop' to finish.\n");
                    i--; // Decrement index to retry this position
                }
            }
        }
        length = i;  // Update the length based on the number of valid entries
    } else {
        order = 1; // Default order value (ascending)
    }

    // Call the sort_array function
    int result = sort_array(vec, length, order);

    // Show the result
    printf("Result: %d | Sorted Array={", result);
    for (int i = 0; i < length; i++) {
        printf("%d", vec[i]);
        if (i < length - 1) printf(", ");
    }
    printf("}\n\n");
}


// Function 10: Median Test
void test_median() {
    int arr[8] = {0}; 
    int length = 0;    
    int me;           

    printf("\n--- Test Median ---\n");
    printf("Do you want to use default values? (Y/N): ");
    char choice;
    scanf(" %c", &choice);

    if (choice == 'Y' || choice == 'y') {
        // Valores padrão
        int default_values[] = {5, 3, 9, 1, 7};
        length = 5;
        for (int i = 0; i < length; i++) {
            arr[i] = default_values[i];
        }
        
        printf("Input Array: {");
        for (int i = 0; i < length; i++) {
            printf("%d", arr[i]);
            if (i < length - 1) printf(", ");
        }
        printf("}\n");
    }

    if (choice == 'N' || choice == 'n') {
        printf("Enter values (maximum 8, type 'stop' to finish):\n");
        while (length < 8) {
            char input[10];
            printf("Enter value %d: ", length + 1);
            scanf("%s", input);

            if (strcmp(input, "stop") == 0) {
                break;
            }

            if (sscanf(input, "%d", &arr[length]) == 1) {
                length++;
            } else {
                printf("Invalid input! Please enter a valid integer or 'stop' to finish.\n");
            }
        }

        // Check that at least one value has been entered
        if (length == 0) {
            printf("Error: No values entered!\n");
            return;
        }

        // Show final array
        printf("Final Array: {");
        for (int i = 0; i < length; i++) {
            printf("%d", arr[i]);
            if (i < length - 1) printf(", ");
        }

        printf("}\n");
    }

    int result = median(arr, length, &me);
    printf("Result: %d | Median: %d\n", result, me);
}


// Main function with menu
int main() {
    int option;

    do {
        printf("\nMenu:\n");
        printf("1 - Test Extract Data\n");
        printf("2 - Test Get Binary Number\n");
        printf("3 - Test Get Number\n");
        printf("4 - Test Format Command\n");
        printf("5 - Test Enqueue Value\n");
        printf("6 - Test Dequeue Value\n");
        printf("7 - Test Get N Element\n");
        printf("8 - Test Move N to Array\n");
        printf("9 - Test Sort Array\n");
        printf("10 - Test Median\n");
        printf("0 - Exit\n");
        printf("Choose an option: ");
        scanf("%d", &option);

        switch (option) {
            case 1: test_extract_data(); break;
            case 2: test_get_number_binary(); break;
            case 3: test_get_number(); break;
            case 4: test_format_command(); break;
            case 5: test_enqueue_value(); break;
            case 6: test_dequeue_value(); break;
            case 7: test_get_n_element(); break;
            case 8: test_move_n_to_array(); break;
            case 9: test_sort_array(); break;
            case 10: test_median(); break;
            case 0: printf("Exiting...\n\n"); break;
            default: printf("Invalid option! Try again.\n\n");
        }
    } while (option != 0);

    return 0;
}

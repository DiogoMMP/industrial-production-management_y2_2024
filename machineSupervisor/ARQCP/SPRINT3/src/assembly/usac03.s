.section .note.GNU-stack,"",@progbits

.section .text
    .global get_number

get_number:
    # RDI = char* str
    # RSI = int* n

    # Initialize the result as 0
    mov $0, %eax            # Return value (success or failure)
    movl $0, (%rsi)         # Initialize result to 0

skip_whitespace:
    # Skip leading whitespace
    movb (%rdi), %cl
    cmpb $' ', %cl
    je increment_ptr
    cmpb $'\t', %cl         # Handle tabs as whitespace
    je increment_ptr
    cmpb $0, %cl            # End of string (invalid input if reached here)
    je invalid_input
    cmpb $'-', %cl          # Check for negative sign
    je invalid_input        # Invalid input if '-' sign is present
    cmpb $'+', %cl          # Check for positive sign
    je invalid_input        # Invalid input if '+' sign is present
    jmp check_first_digit

increment_ptr:
    inc %rdi                # Increment the pointer
    jmp skip_whitespace

check_first_digit:
    movb (%rdi), %cl
    cmpb $'0', %cl
    jl invalid_input        # Invalid if less than '0'
    cmpb $'9', %cl
    jg invalid_input        # Invalid if greater than '9'
    sub $'0', %cl           # Convert ASCII digit to integer
    movsbl %cl, %ecx
    add %ecx, (%rsi)        # Add to the result
    inc %rdi                # Move to the next character
    jmp digit_loop

digit_loop:
    movb (%rdi), %cl
    cmpb $0, %cl            # End of string
    je store_result
    cmpb $' ', %cl          # Allow trailing spaces
    je check_if_it_ends
    cmpb $'\t', %cl         # Allow trailing tabs
    je check_if_it_ends
    cmpb $'0', %cl
    jl invalid_input
    cmpb $'9', %cl
    jg invalid_input
    sub $'0', %cl           # Convert ASCII digit to integer
    movsbl %cl, %ecx
    movl $10, %r9d          # Multiply current result by 10
    movl (%rsi), %r10d
    imull %r9d, %r10d
    movl %r10d, (%rsi)
    add %ecx, (%rsi)        # Add the new digit
    inc %rdi                # Move to the next character
    jmp digit_loop

check_if_it_ends:
    inc %rdi
    movb (%rdi), %cl
    cmpb $0, %cl
    je store_result
    cmpb $' ', %cl
    je check_if_it_ends
    cmpb $'\t', %cl
    je check_if_it_ends
    jmp invalid_input

store_result:
    movl $1, %eax           # Return 1 (success)
    ret

invalid_input:
    movl $-1, (%rsi)         # Store 0 in the result
    movl $0, %eax           # Return 0 (failure)
    ret

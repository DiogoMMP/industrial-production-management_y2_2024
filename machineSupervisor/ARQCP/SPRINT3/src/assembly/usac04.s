# int get_number_binary(int n, char* bits);
# 1-> %edi 2-> %rsi

# int format_command(char* op, int n, char *cmd);
# 1-> %rdi 2-> %esi 3-> %rdx

.section .note.GNU-stack,"",@progbits

.section .text
    .global format_command
    .extern get_number_binary

format_command:
    # Prologue
    pushq %rbp                 # Save the original value of RBP
    movq %rsp, %rbp            # Copy the current stack pointer to RBP
    movq $0, %rcx              # Index counter for the input string
    movq $0, %r8               # Index counter for the output string
    movb $0, (%rdx)            # Set cmd = empty string

trim_left:                      # Remove leading spaces and convert to uppercase
    movb (%rdi, %rcx, 1), %al  # Load the current character from the input string

    cmpb $0, %al                # If end of string, terminate
    je fail                     

    cmpb $' ', %al              # Check if it is a space
    jne capitalize_loop

    incq %rcx                   # Move to the next character
    jmp trim_left

capitalize_loop:                # Convert to uppercase (if necessary)
    movb (%rdi, %rcx, 1), %al  # Load the current character from the input string

    cmpb $0, %al                # If end of string, terminate
    je check_command

    cmpb $'a', %al              # Check if it is a lowercase letter (ASCII 'a')
    jb copy_char

    cmpb $'z', %al              # Check if it is a lowercase letter (ASCII 'z')
    ja copy_char

    subb $32, %al               # Convert to uppercase

copy_char:
    movb %al, (%rdx, %r8, 1)    # Copy the character to the output string

    incq %rcx                   # Move to the next character
    incq %r8                    # Move to the next position in the output string
    jmp capitalize_loop

check_command:
    # Check if the word is "ON", "OP" or "OFF"
    movq %rdx, %r9              # Pointer to the output string
    movq $2, %r10

    movb (%r9), %al             # Load the first character of the output string
    cmpb $'O', %al              # Check if the first character is 'O'
    jne fail

    movb 1(%r9), %al            # Load the second character of the output string
    cmpb $'N', %al              # Check if the second character is 'N'
    je check_right_2

    cmpb $'P', %al              # Check if the second character is 'P'
    je check_right_2

    cmpb $'F', %al              # Check if the second character is 'F'
    jne fail

    movb 2(%r9), %al            # Load the third character of the output string
    cmpb $'F', %al              # Check if the third character is 'F'
    jne fail

    movq $3, %r10               # Set the index to 3
    jmp check_right_3

check_right_2:
    movb (%r9, %r10, 1), %al    # Load the character at the current index

    cmpq %r8, %r10              # Check if the index is equal to the length of the output string
    je add_comma_2

    cmpb $' ', %al              # Check if the character is a space
    jne fail

    incq %r10                   # Move to the next character
    jmp check_right_2

check_right_3:
    movb (%r9, %r10, 1), %al    # Load the character at the current index

    cmpq %r8, %r10              # Check if the index is equal to the length of the output string
    je add_comma_3

    cmpb $' ', %al              # Check if the character is a space
    jne fail

    incq %r10                   # Move to the next character
    jmp check_right_3

add_comma_2:
    movb $',', 2(%rdx)          # Add a comma at the second position of the output string
    movq $3, %r8                # Set the output string index to 3
    jmp convert_number

add_comma_3:
    movb $',', 3(%rdx)          # Add a comma at the third position of the output string
    movq $4, %r8                # Set the output string index to 4
    jmp convert_number

loop:
    movb (%r9, %r10), %al       # Load the character at the current index
    incq %r10                   # Move to the next character
    cmpb $0, %al                # Check if it is the end of the string
    je convert_number
    
    cmpb $' ', %al              # Check if the character is a space
    je loop

    jmp fail

convert_number:
    decq %r8                    # Decrement the output string index
    # Add the comma after the command
    movb $',', (%rdx, %r8)      # Add a comma at the current position of the output string
    incq %r8                    # Increment the output string index

    # Convert the number to binary and add to the output string
    movl %esi, %edi             # Prepare the number for the get_number_binary function

    # Allocate space for the binary representation
    subq $8, %rsp               # Allocate 8 bytes on the stack
    movq %rsp, %rsi             # Use the allocated space as the buffer

    call get_number_binary      # Call the get_number_binary function

    # Check if the conversion was successful
    cmpb $1, %al                # Check if the conversion was successful
    jne fail

    # Copy the binary representation to the output string, adding commas between the bits
    movq %rsi, %r10             # Use %r10 as a temporary pointer to the buffer (does not overwrite %rdx)
    movq $4, %rcx               # Number of bits to copy (5 bits)
    movb $0, %al                # Initialize %al to zero
copy_bits:
    movb (%r10, %rcx), %al      # Get the bit from the binary representation
    cmpb $0, %al
    jne is_one

    movb $'0', (%rdx, %r8)      # If it is zero
    jmp after_copy

is_one:
    movb $'1', (%rdx, %r8)      # If it is one

after_copy:
    incq %r8

new_iteration:
    # Add a comma after each bit, except the last one
    decq %rcx                   # Decrement the bit counter
    
    cmp $-1, %rcx                # Check if there are still bits to copy
    je copy_bits_done           # If none left, terminate
    movb $',', (%rdx, %r8)      # Add the comma
    incq %r8                    # Move the output pointer

    jmp copy_bits               # Continue the loop

copy_bits_done:
    movb $0, (%rdx, %r8)        # Terminate the string with a null byte (0)
    addq $8, %rsp               # Deallocate the buffer
    movl $1, %eax               # Return 1 to indicate success
    movq %rbp, %rsp             # Retrieve the original RSP value
    popq %rbp                   # Restore the original RBP value
    ret

fail:
    movl $0, %eax               # Return 0 to indicate failure
    movb $0, (%rdx)             # Set cmd as an empty string
    movq %rbp, %rsp             # Retrieve the original RSP value
    popq %rbp                   # Restore the original RBP value
    ret
    
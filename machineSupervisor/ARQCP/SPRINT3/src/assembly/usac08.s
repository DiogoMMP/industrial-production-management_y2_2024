# int move_n_to_array(int* buffer, int length, int *tail, int *head, int n, int* array) (USAC08)
# 1-> %rdi -- buffer
# 2-> %rsi -- length
# 3-> %rdx -- tail
# 4-> %rcx -- head
# 5-> %r8 -- n
# 6-> %r9 -- array

# int dequeue_value(int* buffer, int length, int *tail, int* head, int *value) (USAC06)
# 1-> %rdi -- buffer
# 2-> %rsi -- length
# 3-> %rdx -- tail
# 4-> %rcx -- head
# 5-> %r8 -- value

# int get_n_element(int* buffer, int length, int *tail, int* head) (USAC07)
# 1-> %rdi -- buffer
# 2-> %rsi -- length
# 3-> %rdx -- tail
# 4-> %rcx -- head

.section .note.GNU-stack,"",@progbits

.section .text
    .global move_n_to_array
    .extern get_n_element
    .extern dequeue_value

move_n_to_array:
    # Prologue
    pushq %rbp
    movq %rsp, %rbp

    # Save registers
    pushq %rdi
    pushq %rsi
    pushq %rdx
    pushq %rcx
    pushq %r8
    pushq %r9

    # Call get_n_element to check number of available elements
    call get_n_element

    # Restore registers
    popq %r9
    popq %r8
    popq %rcx
    popq %rdx
    popq %rsi
    popq %rdi

    # Check if there are enough elements
    cmpl %r8d, %eax         # Compare n (%r8d) with available elements (%eax)
    jb fail                 # If n > available elements, fail

    # Initialize loop variables
    movq $0, %r10           # Index for the output array
    movl %r8d, %r11d        # Copy n to %r11d (loop counter)

loop:
    cmpl $0, %r11d          # Check if all elements are moved
    je success              # Exit loop if n == 0

    pushq %r9               # Save %r9 on the stack
    pushq %r10              # Save %r10 on the stack
    pushq %r11              # Save %r11 on the stack
    pushq %rdx              # Save %rdx on the stack

    # Call dequeue_value
    leaq (%r9, %r10, 4), %r8        # Calculate address for the output array element
    call dequeue_value          	# Call dequeue_value

    popq %rdx              # Restore %rdx from the stack
    popq %r11              # Restore %r11 from the stack
    popq %r10              # Restore %r10 from the stack
    popq %r9               # Restore %r9 from the stack

    # Check result of dequeue_value
    cmpl $1, %eax           # Check if dequeue_value was successful
    jne fail                # Fail if dequeue_value returned an error

    # Update indices
    incq %r10               # Increment output array index
    decl %r11d              # Decrement loop counter
    jmp loop                # Repeat loop

success:
    movl $1, %eax           # Return 1 to indicate success
    jmp epilogue            # Jump to epilogue

fail:
    movl $0, %eax           # Return 0 to indicate failure

epilogue:
    # Restore stack frame and return
    movq %rbp, %rsp         # Restore original stack pointer
    popq %rbp               # Restore base pointer
    ret

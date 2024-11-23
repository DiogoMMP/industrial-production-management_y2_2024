.section .text
    .global median
    .extern sort_array

median:
    # %rdi = vec
    # %esi = length
    # %rdx = median

    # Prologue
    pushq %rbp               # Save base pointer
    movq %rsp, %rbp          # Set up stack frame

    movl $0, %eax            # Return 0 (failure) by default

    # Check if length <= 0
    cmpl $0, %esi            # Compare length with 0
    jle exit                 # If length <= 0, jump to exit

    # Sorting the array using the sort function
    pushq %rsi               # Save length
    pushq %rdx               # Save median
    movl $1, %edx            # Sort in ascending order
    call sort_array          # Call sort function
    popq %rdx                # Restore median
    popq %rsi                # Restore length


find_median:
    # Calculate the median
    test $1, %esi            # Check if length is odd or even
    jz even_length           # If length is even, jump to even_length

odd_length:
    # Length is odd
    sarl $1, %esi                  # esi = length / 2
    movl (%rdi, %rsi, 4), %ecx     # edx = vec[length/2]
    movl %ecx, (%rdx)              # vec[length/2] = median
    movl $1, %eax                  # Return 1 (success)
    jmp exit

even_length:
    # Length is even
    subq $4, %rdi                   # Decrement vec pointer
    sarl $1, %esi                   # esi = length / 2
    movl (%rdi, %rsi, 4), %eax      # edx = vec[length/2]
    movl 4(%rdi, %rsi, 4), %ecx     # ecx = vec[length/2 + 1]
    addl %eax, %ecx                 # edx = vec[length/2] + vec[length/2 + 1]
    sarl $1, %ecx                   # edx = (vec[length/2] + vec[length/2 + 1]) / 2
    movl %ecx, (%rdx)               # vec[length/2] = median
    movl $1, %eax                   # Return 1 (success)
    jmp exit

exit:
    # Epilogue
    movq %rbp, %rsp          # Restore stack pointer
    popq %rbp                # Restore base pointer
    ret
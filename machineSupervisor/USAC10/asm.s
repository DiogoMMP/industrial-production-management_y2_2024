.section .text
    .global median

median:
    # %rdi = vec
    # %esi = lenght
    # %rdx = median

    # Prologue
    pushq %rbp               # Save base pointer
    movq %rsp, %rbp          # Set up stack frame

    movl $0, %eax            # Return 0 (failure) by default

    # Check if length <= 0
    cmpl $0, %esi            # Compare length with 0
    jle end                  # If length <= 0, jump to end

    # Sorting the array using Bubble Sort
    movl %esi, %ecx          # Set outer loop counter to length
    decl %ecx                 # ecx = length - 1

outer_loop:
    cmpl $0, %ecx          # Check if outer loop counter is 0
    je find_median         # If ecx == 0, sorting is done

    movl %ecx, %ebx          # Save outer loop counter in ebx (inner loop counter)
    movq %rdi, %r8           # r8 = vec (pointer to array)

inner_loop:
    movl (%r8), %eax         # eax = vec[i] (current element)
    movl 4(%r8), %ebx        # ebx = vec[i+1] (next element)
    cmpl %ebx, %eax          # Compare vec[i] with vec[i+1]
    jge no_swap              # If vec[i] >= vec[i+1], no swap needed

    # Swap vec[i] and vec[i+1]
    movl %ebx, (%r8)         # vec[i] = vec[i+1]
    movl %eax, 4(%r8)        # vec[i+1] = vec[i]

no_swap:
    addq $4, %r8             # Move to next pair of elements (next index)
    decl %ebx                # Decrement inner loop counter
    cmpl $0, %ebx            # Check if inner loop counter is 0
    jne inner_loop           # If inner loop counter != 0, repeat inner loop

    decl %ecx                # Decrement outer loop counter
    jmp outer_loop           # Repeat outer loop

find_median:
    # Now calculate the median
    test $1, %esi            # Check if length is odd or even
    jnz odd_length           # If length is odd, jump to odd_length

    # Length is even
    shr %esi                # esi = length / 2




    movl %esi(%rdi), %edx # rdx = vec[length/2]







    decl %esi                # esi = length / 2 - 1
    movl %esi(%rdi), %eax # rax = vec[length/2 - 1]






    addl %eax, %edx         # rdx = vec[length/2] + vec[length/2 - 1]
    shr %edx                # rdx = (vec[length/2] + vec[length/2 - 1]) / 2
    movl $1, %eax            # Return 1 (success)
    jmp exit


odd_length:
    # Length is odd
    shr %esi
    movq ((%rdi), %esi, 4), %rdx
    movl $1, %eax            # Return 1 (success)
    jmp exit


exit:
    # Epilogue
    mov %rbp, %rsp          # Restore stack pointer
    pop %rbp                # Restore base pointer
    ret
.section .text
    .global get_n_element

get_n_element:
    # %rdi = buffer (not used here)
    # %esi = num (size of the buffer)
    # %rdx = tail (pointer to tail)
    # %rcx = head (pointer to head)

    # Prologue
    pushq %rbp                    # Save the original value of RBP
    movq %rsp, %rbp               # Copy the current stack pointer to RBP

    # Load the values of tail and head
    movl (%rdx), %eax             # Load the value of tail into %eax
    movl (%rcx), %r8d             # Load the value of head into %ebx

    cmpl %eax, %r8d               # Compare head (ebx) and tail (eax)
    je buffer_empty               # If head == tail, the buffer is empty

    movl %r8d, %ecx               # Copy head to ecx
    addl $1, %ecx                 # Increment head by 1 (head + 1)
    cmpl %esi, %ecx               # Check if head + 1 exceeds the buffer size
    jne not_wrapped               # If not, continue
    movl $0, %ecx                 # Wrap head + 1 back to 0 if head + 1 == num (buffer size)

not_wrapped:
    cmpl %eax, %ecx               # Compare (head + 1) with tail
    je buffer_full                # If they are equal, the buffer is full

    # Buffer has elements
    cmpl %eax, %r8d               # Check if head >= tail
    jge head_greater_or_equal_tail

    # Case: tail > head (circular wrap-around)
    movl %esi, %ecx               # ecx = num (size of buffer - 1)
    subl %eax, %ecx               # ecx = num - tail
    addl %r8d, %ecx               # ecx = num - tail + head
    jmp end

head_greater_or_equal_tail:
    subl %eax, %r8d               # ebx = head - tail
    movl %r8d, %ecx               # Store result in ecx

    jmp end

buffer_empty:
    movl $0, %eax                 # Buffer is empty
    jmp epilogue

buffer_full:
    movl %esi, %eax               # Buffer is full
    jmp epilogue

end:
    movl %ecx, %eax               # Move the result into %eax for return

epilogue:
    # Restore the original RBP and return
    movq %rbp, %rsp
    popq %rbp
    ret
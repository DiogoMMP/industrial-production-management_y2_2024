.section .note.GNU-stack,"",@progbits

.section .text
    .global enqueue_value

enqueue_value:
    pushq %rbp                  # save the original value of RBP
    movq %rsp, %rbp             # copy the current stack pointer to RBP

    movq %rdx, %r9              # save tail register in r9

    movl (%rcx), %eax           # save head in %eax -> *head
    addl $1, %eax               # (*head + 1)

    cdq                         # sign-extend double word (long) in %eax to quad word in %edx : %eax to be able to perform division
    divl %esi                   # %edx saves next_head = (*head + 1) % length 

    movl %edx, %r10d            # move next_head to %r10d

    cmpl %r10d, (%r9)           # compare next_head == *tail
    je is_full                  # if it is, the buffer isFull

    movq $0, %rax               # if it isn't, the buffer isn't full, so the return value is 0
    jmp continue

is_full:
    movl (%r9), %eax            # save tail in %eax -> *head
    addl $1, %eax               # (*tail + 1)

    cdq                         # sign-extend double word (long) in %eax to quad word in %edx : %eax to be able to perform division
    divl %esi                   # %edx saves new tail value = (*tail + 1) % length

    movl %edx, (%r9)            # save new tail in tail register
    movq $1, %rax               # since the buffer is full, the return value is 1

    jmp continue

continue:
    movl (%rcx), %esi           # move *head to %esi

    movl %r8d, (%rdi, %rsi, 4)  # move value to buffer[*head]
    movl %r10d, (%rcx)          # *head = *head + 1

    jmp end

end:
    movq %rbp, %rsp   
    popq %rbp                   # restore the original value of RBP and return
    
    ret
    
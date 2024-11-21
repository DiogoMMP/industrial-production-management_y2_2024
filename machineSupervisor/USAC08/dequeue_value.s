.section .text
    .global dequeue_value

dequeue_value:
    pushq %rbp                      # save the original value of RBP
    movq %rsp, %rbp                 # copy the current stack pointer to RBP

    movq %rdx, %r9                  # move tail register to r9

    movl (%r9), %eax                # copy *tail value to %eax

    cmpl %eax, (%rcx)               # compare *tail == *head
    je is_empty                 

    movl (%rdi, %rax, 4), %r10d     # copy buffer[*tail] value to %r10d
    movl %r10d, (%r8)               # *value = buffer[*tail]                       

    addl $1, %eax                   # *tail + 1

    cdq                             # sign-extend double word (long) in %eax to quad word in %edx : %eax to be able to perform division
    divl %esi                       # %edx saves next_tail = (*tail + 1) % length

    movl %edx, (%r9)                # change tail current value to next_tail

    movq $1, %rax                   # operation successful, move 1 to return register
    jmp end

is_empty:
    movq $0, %rax                   # buffer is empty so can't dequeue any value
    jmp end

end:
    movq %rbp, %rsp   
    popq %rbp                       # restore the original value of RBP and return
    
    ret


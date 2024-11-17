.section .text
    .global sort_array

# Parâmetros:
# rdi - array (ponteiro para o array)
# rsi - length (comprimento do array)
# rdx - order (1 para crescente, 0 para decrescente)
# rax - índice externo (i)
# rcx - índice interno (j)
# r8d - valor array[j]
# r9d - valor array[j+1]

sort_array:
    cmpl $0, %esi             
    jle invalid              

    cmpb $1, %dl               
    je ascending   
                
    cmpb $0, %dl             
    je descending            

    jmp invalid

ascending:
    movl $0, %eax

asc_outer_loop:
    cmpl %esi, %eax           
    jge success             

    movl $0, %ecx             

asc_inner_loop:
    cmpl %esi, %ecx          
    jge asc_increment_outer   

    movl (%rdi, %rcx, 4), %r8d 
    movl 4(%rdi, %rcx, 4), %r9d 

    cmpl %r9d, %r8d            
    jle asc_skip_swap         

    # Troca os elementos
    movl %r9d, (%rdi, %rcx, 4) 
    movl %r8d, 4(%rdi, %rcx, 4)

asc_skip_swap:
    incl %ecx                 
    jmp asc_inner_loop         

asc_increment_outer:
    incl %eax                 
    jmp asc_outer_loop         

descending:
    movl $0, %eax

desc_outer_loop:
    cmpl %esi, %eax            
    jge success                

    movl $0, %ecx              

desc_inner_loop:
    cmpl %esi, %ecx            
    jge desc_increment_outer   

    movl (%rdi, %rcx, 4), %r8d 
    movl 4(%rdi, %rcx, 4), %r9d 

    cmpl %r8d, %r9d            
    jle desc_skip_swap         

    movl %r9d, (%rdi, %rcx, 4) 
    movl %r8d, 4(%rdi, %rcx, 4) 

desc_skip_swap:
    incl %ecx                  
    jmp desc_inner_loop       

desc_increment_outer:
    incl %eax                  
    jmp desc_outer_loop       

success:
    movl $1, %eax              
    ret

invalid:
    movl $0, %eax            
    ret

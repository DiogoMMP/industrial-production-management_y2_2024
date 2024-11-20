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
    # Verifica se o comprimento do array é válido
    cmpl $0, %esi
    jle invalid  # Comprimento inválido, retorna 0

    # Verifica a ordem (1 para crescente, 0 para decrescente)
    cmpb $1, %dl
    je ascending

    cmpb $0, %dl
    je descending

    # Ordem inválida
    jmp invalid

# Ordenação Crescente
ascending:
    movl $0, %eax  # Índice externo (i = 0)

asc_outer_loop:
    cmpl %esi, %eax  # Enquanto i < length
    jge success       # Se i >= length, sai com sucesso

    movl $0, %ecx  # Índice interno (j = 0)

asc_inner_loop:
    # Verifica se j+1 < length
    movl %ecx, %r10d
    addl $1, %r10d
    cmpl %esi, %r10d
    jge asc_increment_outer

    # Carrega array[j] e array[j+1]
    movl (%rdi, %rcx, 4), %r8d
    movl 4(%rdi, %rcx, 4), %r9d

    # Compara array[j] com array[j+1]
    cmpl %r9d, %r8d
    jle asc_skip_swap  # Se array[j] <= array[j+1], pula troca

    # Troca array[j] e array[j+1]
    movl %r9d, (%rdi, %rcx, 4)
    movl %r8d, 4(%rdi, %rcx, 4)

asc_skip_swap:
    incl %ecx  # j++
    jmp asc_inner_loop

asc_increment_outer:
    incl %eax  # i++
    jmp asc_outer_loop

# Ordenação Decrescente
descending:
    movl $0, %eax  # Índice externo (i = 0)

desc_outer_loop:
    cmpl %esi, %eax  # Enquanto i < length
    jge success       # Se i >= length, sai com sucesso

    movl $0, %ecx  # Índice interno (j = 0)

desc_inner_loop:
    # Verifica se j+1 < length
    movl %ecx, %r10d
    addl $1, %r10d
    cmpl %esi, %r10d
    jge desc_increment_outer

    # Carrega array[j] e array[j+1]
    movl (%rdi, %rcx, 4), %r8d
    movl 4(%rdi, %rcx, 4), %r9d

    # Compara array[j] com array[j+1]
    cmpl %r8d, %r9d
    jle desc_skip_swap  # Se array[j] >= array[j+1], pula troca

    # Troca array[j] e array[j+1]
    movl %r9d, (%rdi, %rcx, 4)
    movl %r8d, 4(%rdi, %rcx, 4)

desc_skip_swap:
    incl %ecx  # j++
    jmp desc_inner_loop

desc_increment_outer:
    incl %eax  # i++
    jmp desc_outer_loop

# Sucesso
success:
    movl $1, %eax  # Retorna 1 para sucesso
    ret

# Entrada Inválida
invalid:
    movl $0, %eax  # Retorna 0 para erro
    ret

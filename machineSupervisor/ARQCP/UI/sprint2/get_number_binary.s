.section .text
    .global get_number_binary
   
get_number_binary:
	cmpl $0, %edi
	jl out_of_range
	cmpl $31, %edi
	jg out_of_range
	
	movl $4, %ecx

extract:
	cmpl $0, %ecx
	jl success
	
	movl %edi, %eax
	shrl %cl, %eax
	andl $1, %eax
	movb %al, (%rsi, %rcx,1)
	
	decl %ecx
	
	jmp extract

success:	
	movl $1, %eax
	ret

out_of_range:
	movl $0, %eax
	ret




	.file	"essai.c"
	.section	.rodata.str1.1,"aMS",@progbits,1
.LC0:
	.string	"%d\n"
	.text
	.p2align 4,,15
.globl main
	.type	main, @function
main:
	leal	4(%esp), %ecx
	andl	$-16, %esp
	pushl	-4(%ecx)
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%ecx
	subl	$20, %esp
	movl	$4, (%esp)
	call	malloc
	movl	P_ELT, %edx
	movl	%eax, C_ELT
	movl	%edx, (%eax)
	movl	$5, (%eax)
#APP
	jmp .ben
.ben:

#NO_APP
	movl	$5, 4(%esp)
	movl	$.LC0, (%esp)
	call	printf
	addl	$20, %esp
	movl	$1, %eax
	popl	%ecx
	popl	%ebp
	leal	-4(%ecx), %esp
	ret
	.size	main, .-main
	.comm	P_ELT,4,4
	.comm	C_ELT,4,4
	.ident	"GCC: (GNU) 4.2.1 (Debian 4.2.1-3)"
	.section	.note.GNU-stack,"",@progbits

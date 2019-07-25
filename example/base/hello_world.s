	.file	"hello_world.c"
	.comm	arg_count,4,4
	.comm	arg_vector,8,8
	.text
	.globl	print_char
	.type	print_char, @function
print_char:
.LFB2:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$16, %rsp
	movl	%edi, %eax
	movb	%al, -4(%rbp)
	movq	stdout(%rip), %rdx
	movsbl	-4(%rbp), %eax
	movq	%rdx, %rsi
	movl	%eax, %edi
	call	fputc
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE2:
	.size	print_char, .-print_char
	.globl	die_with_code
	.type	die_with_code, @function
die_with_code:
.LFB3:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$16, %rsp
	movl	%edi, -4(%rbp)
	movl	-4(%rbp), %eax
	movl	%eax, %edi
	call	exit
	.cfi_endproc
.LFE3:
	.size	die_with_code, .-die_with_code
	.comm	table_type,0,8
	.section	.rodata
.LC0:
	.string	"Hello world!\n"
	.text
	.globl	main
	.type	main, @function
main:
.LFB4:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$32, %rsp
	movl	%edi, -20(%rbp)
	movq	%rsi, -32(%rbp)
	movl	-20(%rbp), %eax
	movl	%eax, arg_count(%rip)
	movq	-32(%rbp), %rax
	movq	%rax, arg_vector(%rip)
	movl	$1, -4(%rbp)
	jmp	.L4
.L5:
	movq	stdout(%rip), %rdx
	movl	-4(%rbp), %eax
	subl	$1, %eax
	cltq
	movzbl	.LC0(%rax), %eax
	movsbl	%al, %eax
	movq	%rdx, %rsi
	movl	%eax, %edi
	call	fputc
	addl	$1, -4(%rbp)
.L4:
	cmpl	$13, -4(%rbp)
	jle	.L5
	movl	$0, %eax
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE4:
	.size	main, .-main
	.ident	"GCC: (Ubuntu 4.8.4-2ubuntu1~14.04.3) 4.8.4"
	.section	.note.GNU-stack,"",@progbits

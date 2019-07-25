" Vim indent file
" Language: Lisaac
" Maintainer: Xavier Oswald <xoswald@debian.org>
" Contributors: Matthieu Herrmann <matthieu.herrmann@laposte.net>
" $Date: 2009/05/19 21:33:52 
" $Revision: 1.0 
" URL: http://isaacproject.u-strasbg.fr/

" TODO:
" - Improve string indent and if there are {, ( .... inside

" Only load this indent file when no other was loaded.
if exists("b:did_indent")
	finish
endif
let b:did_indent = 1

setlocal indentexpr=GetLisaacIndent()
setlocal indentkeys+==Section,0),0},O]
setlocal nolisp        " no lisp indent
setlocal nosmartindent " no start tab
setlocal nocindent     " no C indent
setlocal nosmarttab    " no start tab
setlocal expandtab     " no tabs, real spaces
setlocal autoindent    " Use indent from the previous line
setlocal tabstop=2     " tab spacing
setlocal softtabstop=2 " 2 spaces when pressing <tab> unify
setlocal shiftwidth=2  " unify


" Only define the function once.
if exists("*GetLisaacIndent")
	finish
endif

function GetLisaacIndent()

	let lnum  = prevnonblank(v:lnum - 1)
	let ind   = indent(lnum)
	let line  = getline(lnum)
	let linec = getline(v:lnum)
	let pline = getline(pnum)

	" At the start of the file use zero indent.
"	if lnum == 0
"		return 0 
"	endif
	
	"""""""""""""""""
	" NO INDENT     "
	"""""""""""""""""

	" Whole line String 
  if linec =~ '^.\s*\\.*\\\s*$' || linec =~ '^\s*\\.*\"'
    return ind
  endif
	
	" && and ||
  if line =~ '^.*&&\s*$' || line =~ '^.*||\s*$' "|| line =~ '^\s*{.*}.*'
    return ind
  endif
	

	"""""""""""""""""
	" INDENT PART   "
	"""""""""""""""""

	if line =~ '^.*($'
		let ind = ind + &sw
		return ind
	endif

	" Add a 'shiftwidth' after lines that start with a Section word
	if line =~ '^\s*Section'
		let ind = ind + &sw
		return ind
	endif

	" Add a 'shiftwidth' after a "(" and no ")" and not in a string
	if line =~ '^.*(' && line !~ '^.*(.*).*'  && line !~ '^.*\".*(.*\".*'
		let ind = ind + &sw
		return ind
	endif

	" Add a 'shiftwidth' after a "{" and no "}" and not in a string
	" .....{ OR ......{ code;
	" No invariant (contract)
	if line =~ '^.*{\s*$' || line =~'^.*{.*' && line !~ '^.*\".*{.*\".*' && line !~ '^.*\\.*{.*\".*'	&& line !~ '^.*?\s*{' && line !~ '^\s*{.*}.*'
		let ind = ind + &sw
		return ind
	endif
	
	" Add a 'shiftwidth' after a "[" and no "]" and not in a string 
	if line =~ '^.\s[' && line !~ '^.*\[ .* \].*'
		let ind = ind + &sw
		return ind
	endif

	"""""""""""""""""
	" UNINDENT PART "
	"""""""""""""""""

	" Unindent end block and end string
  if linec =~ '^\s*};\s*$'
		let ind = ind - &sw 
    return ind
  endif
  if linec =~ '^\s*);\s*$' || linec =~ '^\s*).*'
		let ind = ind - &sw 
    return ind
  endif

	" Unindent Sections :
	if linec =~ '^\s*Section'
		let ind = ind - &sw
		return 0
	endif

	" Unindent for ")" and not in a string
	if linec =~ '^.*)' && linec !~ '^.*(.*).*' && linec !~ '^.*\".*).*\".*'
		let ind = ind - &sw	
		return ind
	endif
	
	" Unindent for "}" and not in a sting
	"if linec =~ '^.*}.*' && linec !~ '^.*{.*}.*'  
	if linec =~ '^.*}.*' && linec !~ '^.*{.*'  && linec !~ '^.*\".*}.*\".*'
		let ind = ind - &sw
		return ind
	endif
	if linec =~ '^\s*}.*' 
		let ind = ind - &sw
		return ind
	endif

	" Unindent for "]" and not in a string
	if linec =~ '^\s*]' && linec !~ '^.*\[ .* \] .*' 
		let ind = ind - &sw
		return ind
	endif
	
return ind

endfunction	
" vim:sw=2

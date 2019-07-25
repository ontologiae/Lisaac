" Vim syntax file
" Language:     Lisaac
" Maintainer:   Xavier Oswald <xoswald@debian.org>
" URL:          http://isaacproject.u-strasbg.fr/
" Last Change:  2008 November 06 
" Filenames:    *.li

" Quit when a syntax file was already loaded
if !exists("main_syntax")
  if version < 600
    syntax clear
  elseif exists("b:current_syntax")
    finish
  endif
  " we define it here so that included files can test for it
  let main_syntax='li'
endif


" keyword definitions
" ===================
syn keyword liFunction            while while_do if else when elseif then self by to do or downto if_true if_false shrink
syn keyword liKey                 Parallel Section Header Insert Inherit Public Private Mapping Interrupt Right Left Self Old Expanded Strict 
syn keyword liSpecial             TODO FIXME DEBUG NOTE not_yet_implemented die_with_code BSBS XOXO JBJB

" Support for String and Char
" ===========================
syn match   liStringSpecial       "^\s*\\" 
syn match   liStringSpecial       "\\$" 
syn match   liStringSpecial       "\\." 
syn region  liString              start=+"+ skip=+\\\\\|\\"+ end=+"+ contains=liStringSpecial

" Operators 
" =========
syn match   liOperatorAffect      "<-\|:=\|?=\|->"
syn match   liOperatorCmp         "<\|>\|*\|/=\|=\|&&\||\|!\|?\|-?\|+?"
syn match   liOperator            "+\|-\|*\|/"

" Quoted expression
" =================
syn match   liExternalExpr        "`[^`\n]*`"
syn region  liQuotedExpr          start=+'+  skip=+\\\\\|\\'+  end=+'+  contains=liStringSpecial

" Others 
" ======
syn match   liPrototype           "[A-Z][A-Z0-9_]*"
syn match   liKey                 "Result\(_[0-9]*\)\="
syn match   liSlot                "^\(\s\|\t\|[(]\)*\(+\|-\)\D"
syn match   liBlock               "{\|}"
syn match   liElement             "\(\[\|\]\)"
syn match   liSymbolDeclaration   "(\|)"
syn match   liContrat             "^\(\s\|\t\)*\[\(\s\|\t\)*\(\.\.\.\)\=\|\]"
syn match   liFunction            "\.\w*"
syn keyword liBoolean             TRUE FALSE BOOLEAN

" Support for decimal, real, binary, Hexadecimal and octal numbers
" ================================================================
" hexa
syn match   liNumberHexa    "\<\(\d\|[ABCDEF]\)\(_\|\d\|[ABCDEF]\)*[hH]\=\>"

" binary
syn match   liNumberBinary  "\<[01]\(\(_\|[01]*\)[01]\)*[bB]\=\>"

" decimal, binary, octal
syn match   liNumberDecimal "\<\d\(\(_\|\d*\)\d\)*[dDbBoO]\=\>"

" real 
syn match   liNumberFloat   "\<\d\(\(_\|\d*\)\d\)*\.\d*\(E-\=\)\=\(\(_\|\d*\)\d\)*[fF]\=\>"

" Comments 
" ========
syn region  liLinesComment        start="/\*" end="\*/" contains=liSpecial
syn match   liQuotedExprInComment contained "`[^']*'" 
syn match   liHiddenComment       "//.*" contains=liQuotedExprInComment,liSpecial 

" The default highlighting Coloration
" ===================================
if version >= 508 || !exists("did_li_syn_inits")
  if version < 508
    let did_li_syn_inits = 1
    command -nargs=+ HiLink hi link <args>
  else
    command -nargs=+ HiLink hi def link <args>
  endif
  HiLink liNumberHexa           Number
  HiLink liNumberDecimal        Number
  HiLink liNumberBinary         Number
  HiLink liNumberFloat          Float
  HiLink liFunction             Function
  HiLink liBoolean              Type 
  HiLink liKey                  Label 
  HiLink liString               String
  HiLink liStringSpecial        SpecialChar
  HiLink liSpecial              Todo
  HiLink liOperatorAffect       Delimiter 
  HiLink liOperatorCmp          Delimiter
  HiLink liOperator             Delimiter
  HiLink liExternalExpr         Define
  HiLink liQuotedExpr           Special 
  HiLink liPrototype            Type
  HiLink liSlot                 Keyword 
  HiLink liBlock                Conditional
  HiLink liElement              keyword
  HiLink liSymbolDeclaration    Keyword
  HiLink liContrat              keyword
  HiLink liNumber               Number 
  HiLink liLinesComment         Comment
  HiLink liHiddenComment        Comment
  HiLink liQuotedExprInComment  SpecialChar 
  delcommand HiLink
endif

let b:current_syntax = "li"

if main_syntax == 'li'
  unlet main_syntax
endif

" vim: ts=8

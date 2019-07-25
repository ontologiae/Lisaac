;; Mode Emacs for LISAAC language by Sonntag Benoit.

;; INSTALLATION
;;   To install, simply copy this file into a directory in your
;;   load-path and add the following two commands in your '.emacs' file:
;;
;;   (add-to-list 'auto-mode-alist '("\\.li\\'" . lisaac-mode))
;;   (autoload 'lisaac-mode "~/lisaac-mode" "Major mode for Lisaac Programs" t)

;; NEW FUNCTION KEY
;;   <tab>          : Indent current line.
;;   <Ctrl>+C <tab> : Indent all lines.
;;   <Ctrl>+C I     : ".if { <Cursor> };".
;;   <Ctrl>+C E     : ".if { <Cursor> } else { };".
;;   <Ctrl>+C U     : ".until_do { <Cursor> };".
;;   <Ctrl>+C W     : ".while_do { <Cursor> };".

;; BUG REPORT
;;   - number: hexa, octal, ...
;;   - (+ to:INTEGER;  truc.put 't' to 10; // to -> black!


;;
;; Table abbrev.
;; 
(defvar li-mode-abbrev-table nil
  "Abbrev table used while in Lisaac mode.")
(define-abbrev-table 'li-mode-abbrev-table ())

;;
;; Expression en couleur.
;;

(defvar li-color 0)
(defvar li-comment 0)
(defvar li-string nil)

(defvar li-test 0)

(defun li-message ()
  ""
  (setq li-string (match-string 0))
  (setq li-color font-lock-function-name-face)
  (setq li-test 0)
  (beginning-of-line)
  (while (= li-test 0)
    ;; Detect Begin file.
    (beginning-of-line)
    (if (= (point) (point-min))
	(setq li-test 1) ;; Stop
      )
    ;; Detect local declaration.
    (if (looking-at (concat "......*[^a-z0-9_]" li-string "\\([ \t\n]*,[ \t\n]*[a-z0-9_]*\\)*[ \t\n]*:[ \t\n]*[A-Z_]+"))
	(progn  
	  (setq li-test 1) ;; Stop
	  (setq li-color 0))
      )
    ;; Detect begin Slot definition.
    (if (looking-at "  \\(\\+\\|-\\|\\*\\)")
    ;(if (looking-at "  [\\+-\\*]")
	;(progn
        ;  (end-of-line)
        ;  (insert "T")
	  (setq li-test 1)
	;  )
      ) ;; Stop
    ;; Ligne suivante.
    (if (= li-test 0)
	(previous-line 1)
      )
    )
  li-color
)

(defun li-declaration ()
  ""
  (setq li-color font-lock-variable-name-face)
  ;; Detect local declaration.
  (if (looking-at "[ \t\n]*[a-z0-9_]*\\([ \t\n]*,[ \t\n]*[a-z0-9_]*\\)*[ \t\n]*:[ \t\n]*[A-Z_]")
      (setq li-color font-lock-warning-face)
    )
  li-color
)

(defun li-number ()
  ""
  (setq li-color font-lock-keyword-face)
  
  li-color
)


(defconst li-font-lock-keywords
  '(
    
    ;; Hidden comments
    ("\\([^/]\\|^\\)/\\*[]!@[#$%^&<|=~/>?\\a-z_A-Z0-9\n\t \\*\\+:\\.;`',(){}-]*\\*/" 0 font-lock-reference-face nil)
    ;("/\\*\\(\\**\\|[^\\*/]+/*\\)*\\*/" 0 font-lock-reference-face nil)
    
    ;; Hidden comments
    ("//.*" 0 font-lock-comment-face nil)

    ;; quoted expr's in comments
    ("`[^'\n]*'" 0 font-lock-builtin-face t)

    ;; Quoted expression
    ("'[^'\n]*'" 0 font-lock-constant-face nil)
   
    ;; External expression
    ("`[^`\n]*`" 0 highlight nil)

    ;; Major keywords :
    ("section.*$" 0 font-lock-keyword-face nil)

    ;; Number Hexa :
    ; ("[0-9][0-9a-fA-F]*[hobd]?" 0 (li-number) nil)
    ("[0-9]+[A-F][A-F0-9]*h" 0 font-lock-keyword-face nil)

    ;; Prototype :
    ("[A-Z][A-Z0-9_]*" 0 font-lock-type-face nil)

    ;; Identifier :
    ("\\.[ \t\n]*[A-Za-z_][a-zA-Z0-9_]*" 0 font-lock-function-name-face nil)
    ("[A-Za-z_][a-zA-Z0-9_]*" 0 (li-message) nil)

    ;; Number :
    ; ("[0-9][0-9a-fA-F]*[hobd]?" 0 (li-number) nil)
    ("[0-9]+" 0 font-lock-keyword-face nil)

    ;; Block :
    ("\{\\|\}" 0 font-lock-comment-face nil)

    ;; Assignment :
    ("<-\\|:=" 0 0 nil) 

    ;; Symbol declaration :
    ("^  \\(\\+\\|-\\|\\*\\)" 0 font-lock-warning-face nil)
    ("\\+\\|-\\|\\*" 0 (li-declaration) nil)

    ;; Operators :
    ("[!@#$%^&<|=~/>?\\]+" 0 font-lock-variable-name-face nil)
    )
  "Additional expressions to highlight in Lisaac mode.")

;;
;; Table de syntax.
;;
(defvar li-mode-syntax-table nil
  "Syntax table used while in Lisaac mode.")


;;
;; Definition de touche sensible.
;;
(defvar li-mode-map (make-sparse-keymap)
  "Keymap used in Lisaac mode.")

(define-key li-mode-map "\t" 'li-indent-command)
(define-key li-mode-map "\r" 'li-newline-command)
(define-key li-mode-map "\C-c\t" 'li-indent-all-command)
(define-key li-mode-map "\C-ci" 'li-cond1-command)
(define-key li-mode-map "\C-ce" 'li-cond2-command)
(define-key li-mode-map "\C-cu" 'li-until-command)
(define-key li-mode-map "\C-cw" 'li-while-command)

;;
;; Insertion loop command. 
;;

(defun li-while-command ()
  "Insert loop while Lisaac."
  (interactive)
  (insert ".while_do {")
  (li-newline-command)
  (li-newline-command)
  (insert "}; // while_do")
  (li-newline-command)
  (previous-line 2)
  (li-indent-command)
)

(defun li-until-command ()
  "Insert loop until Lisaac."
  (interactive)
  (insert ".until_do {")
  (li-newline-command)
  (li-newline-command)
  (insert "}; // until_do")
  (li-newline-command)
  (previous-line 2)
  (li-indent-command)
)

;;
;; Insertion test command.
;;

(defun li-cond1-command ()
  "Insert conditionnal Lisaac."
  (interactive)
  (insert ".if {")
  (li-newline-command)
  (li-newline-command)
  (insert "}; // if")
  (li-newline-command)
  (previous-line 2)
  (li-indent-command)
)

(defun li-cond2-command ()
  "Insert conditionnal Lisaac."
  (interactive)
  (insert ".if {")
  (li-newline-command)
  (li-newline-command)
  (insert "} else {")
  (li-newline-command)
  (li-newline-command)
  (insert "}; // if")
  (li-newline-command)
  (previous-line 4)
  (li-indent-command)
)


(defvar li-count-all 0)

(defun li-indent-all-command ()
  "All indent text"
  (interactive)

  ;; On cherche la premiere ligne.
  (setq li-count-all 1)
  (beginning-of-line)
  (while (/= (point) (point-min))
    (setq li-count-all (+ li-count-all 1))
    (previous-line 1)
    (beginning-of-line))

  ;; Indent chaque ligne.
  (next-line 1)
  (while (/= (point) (point-max)) 
    (li-indent-command)
    (next-line 1))

  ;; Retour a la ligne courante.
  (goto-line li-count-all)
)

;;
;; Indentation.
;;

(defvar li-count-line 0)
(defvar li-point 0)
(defvar li-char ?b)

(defvar li-indent 0)
(defvar li-indent-2 0)
(defvar li-indent-base 0)

(defun li-indent-previous ()
  "Indent celon la ligne precedente."
  ; On cherche la ligne au dessus.
;(insert "lalo")
;(insert "li")
  (setq li-count-line 1)
;(insert "0")
  (previous-line 1)
;(insert "1")
  (beginning-of-line)
;(insert "2")
  (while (looking-at "[ \t\n]*$")
;(insert "L")
    (setq li-count-line (+ li-count-line 1))
    (previous-line 1)
    (beginning-of-line))
;(insert "ici")
  ; On se positionne au debut du texte.
  (forward-to-indentation 0)

  ; Initialisation.
  (setq li-indent-base (current-column)) 
  (setq li-indent 0)
  (setq li-point (point))
  
  ; Cas particulier ...
  (if (looking-at "^section.*$")
     (setq li-indent-base (+ li-indent-base 2)))
  
  ; On compte les parantheses () or {}, de la ligne precedente.
  (setq li-test 0)
  (while (= li-test 0)
    (setq li-char (char-after li-point))
    (if (= li-char ?{) 
	(setq li-indent (+ li-indent 2)))
    (if (= li-char ?() 
	(setq li-indent (+ li-indent 2)))
    (if (= li-char ?})
        (if (/= li-indent 0)
	    (setq li-indent (- li-indent 2))))
    (if (= li-char ?)) 
        (if (/= li-indent 0)
	    (setq li-indent (- li-indent 2))))
;    ;; Virgule and End-of-line.
;    (if (= li-char ?,)
;	(if (= (char-after (+ li-point 1)) ?\n)
;	    (setq li-indent (+ li-indent 2)))) 
    ;; End-of-line.
    (if (= li-char ?\n)
	(setq li-test 1)) ;; Stop
    ;; Comment //
    (if (= li-char ?/)
	(if (= (char-after (+ li-point 1)) ?/)
	    (setq li-test 1)))  ;; Stop
    ;; Next character.
    (setq li-point (+ li-point 1))
  )

  ; On redescend: Ligne courante.
  (while (/= li-count-line 0)
    (setq li-count-line (- li-count-line 1))
    (next-line 1))
  
  ; Fin de la ligne.
  (end-of-line)

  ; Initialisation.
  (setq li-indent-2 0) 
  (setq li-point (point))

  ; On compte les parantheses () or {}, de la ligne courante.
  (setq li-test 0)
  (while (= li-test 0)
    (setq li-point (- li-point 1))
    (setq li-char (char-after li-point))
    (if (= li-char ?}) 
	(setq li-indent-2 (+ li-indent-2 2)))
    (if (= li-char ?)) 
	(setq li-indent-2 (+ li-indent-2 2)))
    (if (= li-char ?{)
        (if (/= li-indent-2 0)
	    (setq li-indent-2 (- li-indent-2 2))))
    (if (= li-char ?() 
        (if (/= li-indent-2 0)
	    (setq li-indent-2 (- li-indent-2 2))))
    (if (= li-char ?\n)
	(setq li-test 1)) ;; Stop
    (if (= li-char ?/)
	(if (= (char-after (+ li-point 1)) ?/)
	    (setq li-indent-2 0)))  
  )

  ; On supprime tous les espaces.
  (beginning-of-line)
  (delete-horizontal-space)
  
  ; On ajoute l'indentation
  (setq li-indent (- li-indent li-indent-2))
  (setq li-indent (+ li-indent li-indent-base))
  (indent-to-column li-indent)

  ; On se met en fin de ligne.
  (end-of-line)
) 


(defun li-indent-command ()
  "indent line for Lisaac mode."
 ; (save-excursion
 ;   (progn
  (interactive)
  (beginning-of-line)
  (if (looking-at "[ \t]*section.*$")
	; then
	(progn
	  (delete-horizontal-space)
	  (end-of-line))
     	;else
      (li-indent-previous))
 ; ))
)

;;
;; Newline command.
;;

(defun li-newline-command ()
  "indent line, append newline, indent line."
  (interactive)
  (if (looking-at "[ \t]*$")
     ; then 
     (progn
	(li-indent-command)
	(newline)
	(li-indent-command))
     ; else
     (newline))
)

;;
;; Autoload.
;;
(defun lisaac-mode ()
  "Major mode for editing typical Lisaac code."
  (interactive)

  ;; compatibility MS-DOS
  (replace-string "" "")

  ;; Light-color On
  (global-font-lock-mode t)

  (kill-all-local-variables)
  (setq mode-name "Lisaac")
  (setq major-mode 'lisaac-mode)
  (setq local-abbrev-table li-mode-abbrev-table)
  (make-local-variable 'font-lock-defaults)
  (setq font-lock-defaults '(li-font-lock-keywords))
  (make-local-variable 'li-mode-syntax-table)
  (setq li-mode-syntax-table (make-syntax-table))
  (set-syntax-table li-mode-syntax-table)
  (use-local-map li-mode-map)

  (run-hooks 'li--mode-hook))


;;
;; Fin du mode Lisaac.
;;
(provide 'lisaac-mode)
















;; 
;;  Mode Emacs for LISAAC language 0.2.2 by Sonntag Benoit.
;;
;;
;;  LICENSE
;;    This program is free software; you can redistribute it and/or modify
;;    it under the terms of the GNU General Public License as published by
;;    the Free Software Foundation; either version 3 of the License, or
;;    (at your option) any later version.
;;
;;    This program is distributed in the hope that it will be useful,
;;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;    GNU General Public License for more details.
;;
;;    You should have received a copy of the GNU General Public License
;;    along with this program.  If not, see <http://www.gnu.org/licenses/>.;
;;
;; INSTALLATION
;;   To install, simply copy this file into a directory in your
;;   load-path and add the following two commands in your '.emacs' file:
;;
;;   (add-to-list 'auto-mode-alist '("\\.li\\'" . lisaac-mode))
;;   (autoload 'lisaac-mode "~/lisaac-mode" "Major mode for Lisaac Programs" t)
;;
;; NEW FUNCTION KEY
;;   <tab>          : Indent current line.
;;   [F2]           : Indent all lines.
;;
;;   [F5]           : Append the Licence Header.
;;   [F6]           : Append the Header section.
;;   [F7]           : Append a constructor.
;;
;;   [F10]          : Display Previous Buffer.
;;   [F11]          : Display the prototype pointed.
;;   [F12]          : Vertical split window and display the prototype pointed.
;;
;;   <Echap>  G     : Goto line.
;;
;; BUG REPORT
;;   - Bug coloration with external `...`
;;   - ( + to:INTEGER;  truc.put 't' to 10; // to -> black!
;;
;; MODIFY FOR YOU :
(defvar li-user-name "Sonntag Benoit")
(defvar li-user-mail "sonntag@icps.u-strasbg.fr")

;;
;; Table abbrev.
;; 
(defvar li-mode-abbrev-table nil
  "Abbrev table used while in Lisaac mode.")
(define-abbrev-table 'li-mode-abbrev-table ())

;;
;; Color Expression.
;;

(defvar li-color 0)
(defvar li-comment 0)
(defvar li-string nil)
(defvar li-string2 nil)


(defvar li-test 0)


(defun li-message ()
  ""  
  ;(insert (char-to-string (char-before (point))))
  (setq li-point2 (point))
  (setq li-string2 (match-string 0))
  (setq li-char (char-before (- (point) (length li-string2))))
  (if (and (>= li-char ?0) (<= li-char ?9))
      (setq li-color font-lock-keyword-face)
    (progn      
      (end-of-line)
      (setq li-point3 (point))
      (setq li-string (concat "....+[^a-z0-9_]" li-string2 "\\([ \t]*,[ \t]*[a-z0-9_]*\\)*[ \t]*:[^=]"))
      
      
      (if (re-search-backward "^  \\(+\\|-\\)" (point-min) t 1)
          (progn
            (setq li-point4 (point))
            (if (and (re-search-forward "<-\\|:=\\|?=\\|;" nil t 1)
                     (< li-point2 (point)))
                (progn
                  (goto-char li-point4)
                  (if (re-search-forward li-string li-point3 t 1)
                      (setq li-color 0)	  
                    (setq li-color li-slot-face)
                    )	      
                  )
              (progn
                (goto-char li-point4)
                (if (re-search-forward li-string li-point3 t 1)
                    (setq li-color 0)	  
                  (setq li-color font-lock-function-name-face)	
                  )
                )
              )
            )
        )
      
                                        ;(setq li-color (get-char-property (point) 'face))
      )
    )      
  (goto-char li-point2)
  li-color
)

(defun li-declaration ()
  ""  
  ;; Detect local declaration.
  (if (looking-at "[ \ta-z0-9_,]*:[^=]") 
      (setq li-color font-lock-warning-face)
    (setq li-color font-lock-variable-name-face)
    )
  li-color
)

(defun li-type-color ()
  ""  
  ;; Detect local declaration.
  (setq li-string2 (concat (downcase (match-string 0)) ".li" ))
  (setq li-test 0)
  (while (and (< li-test (length (mapcar (function buffer-name) (buffer-list))))
              (not (string-equal (nth li-test (mapcar (function buffer-name) (buffer-list))) li-string2))
              )
    (setq li-test (+ li-test 1))
    )
  (if (< li-test (length (mapcar (function buffer-name) (buffer-list))))
      (setq li-color li-type-face)
    (setq li-color font-lock-type-face)
    )
  
  li-color
)

(defconst li-font-lock-keywords
  '(
    ;; External expression
    ("`[^`\n]*`" 0 highlight nil)

    ;; Quoted expression        
    ("'[\\].[^'\n]*'" 0 font-lock-constant-face nil)  
    ("'[^\\ '\n]'" 0 font-lock-constant-face nil)
       
    ;; quoted expr's in comments
    ("`[^'\n]*'" 0 font-lock-builtin-face t)

    ;; Block :
    ("\{\\|\}" 0 font-lock-comment-face nil)

    ;; Assignment :
    ("<-\\|:=\\|?=" 0 0 nil) 

    ;; Float notation :
    ("[0-9_]+\.[0-9]*E[+-]?[0-9]+" 0 font-lock-keyword-face nil)

    ;; Symbol declaration :
    ("^  \\(\\+\\|-\\)" 0 font-lock-warning-face nil)
    ("\\+\\|-" 0 (li-declaration) nil)

    ;; Operators :
    ("[!@#$%^&<|=~/>?\\*\\]+" 0 font-lock-variable-name-face nil)
    
    ;; Major keywords :
    ("^Section[ \t]+[a-zA-Z,\t 0-9_]+\\|Right\\|Left\\|Expanded\\|Strict\\|Old\\|Self\\|Result\\(_[1-9]\\)?" 
     0 font-lock-keyword-face nil)
 
    ;; Hexa-number :
    ("[0-9][0-9_]*[A-F][0-9A-F_]*h" 0 font-lock-keyword-face nil)    

    ;; Prototype :
    ("[A-Z][A-Z0-9_]*" 0 (li-type-color) nil)
    
    ;; Identifier :
    ("\\.[ \t\n]*[a-z][a-z0-9_]*" 0 font-lock-function-name-face nil)
    ("[a-z]+[a-z0-9_]*" 0 (li-message) nil)

    ;; Number :   
    ("[0-9][0-9_]*" 0 font-lock-keyword-face nil)
    ("[0-9]+[0-9A-Fa-f_]*" 0 font-lock-keyword-face nil)    
    )
  "Additional expressions to highlight in Lisaac mode.")

;;
;; Table de syntax.
;;
(defvar li-mode-syntax-table 
  (let ((st (make-syntax-table)))
    ;; Symbol 
    (modify-syntax-entry ?0 "." st)
    (modify-syntax-entry ?2 "." st)
    (modify-syntax-entry ?1 "." st)
    (modify-syntax-entry ?! "." st)
    (modify-syntax-entry ?@ "." st)
    (modify-syntax-entry ?# "." st)
    (modify-syntax-entry ?$  "." st)
    (modify-syntax-entry ?%  "." st)
    (modify-syntax-entry ?^  "." st)
    (modify-syntax-entry ?&  "." st)
    (modify-syntax-entry ?<  "." st)
    (modify-syntax-entry ?|  "." st)
    (modify-syntax-entry ?= "." st)
    (modify-syntax-entry ?/ "." st)
    (modify-syntax-entry ?> "." st)
    (modify-syntax-entry ?\? "." st)  
    (modify-syntax-entry ?* "." st)
    (modify-syntax-entry ?+ "." st)
    (modify-syntax-entry ?- "." st)
    ;; String, character, external.
;    (modify-syntax-entry ?\" "\"" st) 
;    (modify-syntax-entry ?\' "\"" st)
;    (modify-syntax-entry ?` "$$ " st)    
    ;; Identifier
    (modify-syntax-entry ?_  "w" st)
    ;; Comment
    (modify-syntax-entry ?\* ". 23" st)    
    (modify-syntax-entry ?/ ". 124b" st)    
    (modify-syntax-entry ?\n "> b" st)    
    st)
  "Syntax table used while in Lisaac mode.")


;;
;; Definition press key.
;;
(defvar li-mode-map (make-sparse-keymap)
  "Keymap used in Lisaac mode.")
(global-set-key [home] 'beginning-of-line)
(global-set-key [end] 'end-of-line)
(define-key li-mode-map "\t" 'li-indent-command)
(define-key li-mode-map "\r" 'li-newline-command)
(define-key li-mode-map [f2] 'li-indent-all-command)
(define-key li-mode-map [f5] 'li-header1-command)
(define-key li-mode-map [f6] 'li-header2-command)
(define-key li-mode-map [f7] 'li-header3-command)
;(define-key li-mode-map [f8] 'li-header4-command)
;(define-key li-mode-map [f9] 'li-while-command)
(define-key li-mode-map [f10] 'li-previous-buffer-command)
(define-key li-mode-map [f11] 'li-load-direct-prototype-command)
(define-key li-mode-map [f12] 'li-load-prototype-command)

;;
;; Insertion loop command. 
;;

(defun li-previous-buffer-command ()
  "Display prototype."
  (interactive)
  (set-window-buffer (selected-window) (nth 0 (mapcar (function buffer-name) (buffer-list))))  
)

(defun li-load-direct-prototype-command ()
  "Display prototype."
  (interactive)

  (if (face-equal (get-char-property (point) 'face) li-type-face)
      (progn
        (backward-word 1)  
        (looking-at "[A-Z][A-Z0-9_]*")
        (setq li-string2 (concat (downcase (match-string 0)) ".li" ))                 
        (set-window-buffer (selected-window) li-string2)
        )
    )
)

(defun li-load-prototype-command ()
  "Display prototype."
  (interactive)

  (if (face-equal (get-char-property (point) 'face) li-type-face)
      (progn
        (backward-word 1)  
        (looking-at "[A-Z][A-Z0-9_]*")
        (setq li-string2 (concat (downcase (match-string 0)) ".li" ))
         
        (split-window-vertically)
        (set-window-buffer (selected-window) li-string2)
        )
    )
)

(defun li-header1-command ()
  "Insert header standard Lisaac."
  (interactive)
  (insert "///////////////////////////////////////////////////////////////////////////////\n")
  (setq li-string "//                             Application                                   //\n")
  (when (string-match "isaacos" (buffer-file-name))
    (setq li-string "//                         Isaac Opearting System                            //\n")
    )
  (when (string-match "lib" (buffer-file-name))
    (setq li-string "//                             Lisaac Library                                //\n")
    )
  (when (string-match "src" (buffer-file-name))
    (setq li-string "//                             Lisaac Compiler                               //\n")
    )
  (when (string-match "/example" (buffer-file-name))
    (setq li-string "//                             Lisaac Example                                //\n")
    )
  (insert li-string)
  (insert "//                                                                           //\n")
  (insert "//                   LSIIT - ULP - CNRS - INRIA - FRANCE                     //\n")
  (insert "//                                                                           //\n")
  (insert "//   This program is free software: you can redistribute it and/or modify    //\n")
  (insert "//   it under the terms of the GNU General Public License as published by    //\n")
  (insert "//   the Free Software Foundation, either version 3 of the License, or       //\n")
  (insert "//   (at your option) any later version.                                     //\n")
  (insert "//                                                                           //\n")
  (insert "//   This program is distributed in the hope that it will be useful,         //\n")
  (insert "//   but WITHOUT ANY WARRANTY; without even the implied warranty of          //\n")
  (insert "//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           //\n")
  (insert "//   GNU General Public License for more details.                            //\n")
  (insert "//                                                                           //\n")
  (insert "//   You should have received a copy of the GNU General Public License       //\n")
  (insert "//   along with this program.  If not, see <http://www.gnu.org/licenses/>.   //\n")
  (insert "//                                                                           //\n")
  (insert "//                     http://isaacproject.u-strasbg.fr/                     //\n")
  (insert "///////////////////////////////////////////////////////////////////////////////\n")
)

(defun li-header2-command ()
  "Insert header standard Lisaac."
  (interactive)
  (insert "Section Header")
  (li-newline-command)
  (insert "\n")
  (insert "  + name      := ")
  (insert (upcase (truncate-string-to-width (buffer-name) (- (length (buffer-name)) 3) 0)))
  (insert ";\n")
  (insert "\n")
  (insert "  - copyright := \"2003-")
  (insert (format-time-string "%Y"))  
  (insert " ")
  (insert li-user-name)
  (insert "\";\n")
  (insert "\n")
  (insert "  - author    := \"")
  (insert li-user-name)
  (insert " (")
  (insert li-user-mail)
  (insert ")\";\n")
  (insert "  - comment   := \"The main prototype\";\n")
  (insert "\n")
  (insert "Section Inherit\n")
  (insert "\n")
  (insert "  - parent_object:OBJECT := OBJECT;\n")
  (insert "\n")
  (insert "Section Public\n")
)

(defun li-header3-command ()
  "Insert header standard Lisaac."
  (interactive)
  (insert "\n  //\n")
  (insert "  // Creation.\n")
  (insert "  //\n")
  (insert "\n")
  (insert "  - create:SELF <-\n")
  (insert "  ( + result:SELF;\n")
  (insert "    result := clone;\n")
  (insert "    result.make;\n")
  (insert "    result\n")
  (insert "  );\n")
  (insert "\n")
  (insert "  - make <-\n")
  (insert "  ( \n")
  (insert "\n")
  (insert "  );\n")
  (insert "\n")
  (previous-line 3)
  (li-indent-command)
)

;(defun li-while-command ()
;  "Insert loop while Lisaac."
;  (interactive)
;  (insert ".while_do {")
;  (li-newline-command)
;  (li-newline-command)
;  (insert "}; // while_do")
;  (li-newline-command)
;  (previous-line 2)
;  (li-indent-command)
;)

;(defun li-until-command ()
;  "Insert loop until Lisaac."
;  (interactive)
;  (insert ".until_do {")
;  (li-newline-command)
;  (li-newline-command)
;  (insert "}; // until_do")
;  (li-newline-command)
;  (previous-line 2)
;  (li-indent-command)
;)

;;
;; Insertion test command.
;;

;(defun li-cond1-command ()
;  "Insert conditionnal Lisaac."
;  (interactive)
;  (insert ".if {")
;  (li-newline-command)
;  (li-newline-command)
;  (insert "}; // if")
;  (li-newline-command)
;  (previous-line 2)
;  (li-indent-command)
;)

;(defun li-cond2-command ()
;  "Insert conditionnal Lisaac."
;  (interactive)
;  (insert ".if {")
;  (li-newline-command)
;  (li-newline-command)
;  (insert "} else {")
;  (li-newline-command)
;  (li-newline-command)
;  (insert "}; // if")
;  (li-newline-command)
;  (previous-line 4)
;  (li-indent-command)
;)


(defvar li-count-all 0)

(defun li-indent-all-command ()
  "All indent text"
  (interactive)

  ;; Search first line.
  (setq li-count-all 1)
  (beginning-of-line)
  (while (/= (point) (point-min))
    (setq li-count-all (+ li-count-all 1))
    (previous-line 1)
    (beginning-of-line))

  ;; Indent each line.
  (next-line 1)
  (while (/= (point) (point-max)) 
    (li-indent-command)
    (next-line 1))

  ;; Return current line.
  (goto-line li-count-all)
)

;;
;; Indentation.
;;

(defvar li-count-line 0)
(defvar li-point 0)
(defvar li-point2 0)
(defvar li-point3 0)
(defvar li-point4 0)
(defvar li-char ?b)

(defvar li-indent 0)
(defvar li-indent-2 0)
(defvar li-indent-base 0)

(require 'font-lock)
(defvar li-slot-face		'li-slot-face
  "Face to use for slot major.")
(defvar li-type-face		'li-type-face
  "Face to use for type file.")

(defun li-indent-previous ()
  "Indent with previous line."
  ; Search a previous line.
  (setq li-count-line 1)
  (previous-line 1)
  (beginning-of-line)
  (while (looking-at "[ \t\n]*$")
    (setq li-count-line (+ li-count-line 1))
    (previous-line 1)
    (beginning-of-line))
  ; Set position on begin of text.
  (forward-to-indentation 0)

  ; Initialization.
  (setq li-indent-base (current-column)) 
  (setq li-indent 0)
  (setq li-point (point))
  
  ; Particuliar case ...
  (if (looking-at "^Section.*$")
     (setq li-indent-base (+ li-indent-base 2)))
  
  ; Counter of () or {} or [], with previous line.
  (setq li-test 0)
  (while (= li-test 0)
    (setq li-char (char-after li-point))
    (if (= li-char ?[) 
	(setq li-indent (+ li-indent 2)))
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
    (if (= li-char ?]) 
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
    ;; String " "
    (if (= li-char ?")
        (progn        
        (while (= li-test 0)
          (setq li-point (+ li-point 1))
          (setq li-char (char-after li-point))
          (if (= li-char ?\n)
	     (setq li-test 1)) ;; Stop
          (if (= li-char ?")
	     (setq li-test 1)) ;; Stop
        )
        (setq li-test 0))
    )
    ;; String ' '
    (if (= li-char ?')
        (progn        
        (while (= li-test 0)
          (setq li-point (+ li-point 1))
          (setq li-char (char-after li-point))
          (if (= li-char ?\n)
	     (setq li-test 1)) ;; Stop
          (if (= li-char ?')
	     (setq li-test 1)) ;; Stop
        )
        (setq li-test 0))
    )
    ;; Next character.
    (setq li-point (+ li-point 1))
  )

  ; Next line: Current Line.
  (while (/= li-count-line 0)
    (setq li-count-line (- li-count-line 1))
    (next-line 1))
  
  ; Go to End of line.
  (end-of-line)

  ; Initialization.
  (setq li-indent-2 0) 
  (setq li-point (point))

  ; Count () or {} or [], of current line.
  (setq li-test 0)
  (while (= li-test 0)
    (setq li-point (- li-point 1))
    (setq li-char (char-after li-point))
    (if (= li-char ?]) 
	(setq li-indent-2 (+ li-indent-2 2)))
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
    (if (= li-char ?[) 
        (if (/= li-indent-2 0)
	    (setq li-indent-2 (- li-indent-2 2))))
    (if (= li-char ?\n)
	(setq li-test 1)) ;; Stop
    (if (= li-char ?/)
	(if (= (char-after (+ li-point 1)) ?/)
	    (setq li-indent-2 0)))  
    ;; String " "
    (if (= li-char ?")
        (progn        
        (while (= li-test 0)
          (setq li-point (- li-point 1))
          (setq li-char (char-after li-point))
          (if (= li-char ?\n)
	     (setq li-test 1)) ;; Stop
          (if (= li-char ?")
	     (setq li-test 1)) ;; Stop
        )
        (setq li-test 0))
    )
    ;; String ' '
    (if (= li-char ?')
        (progn        
        (while (= li-test 0)
          (setq li-point (- li-point 1))
          (setq li-char (char-after li-point))
          (if (= li-char ?\n)
	     (setq li-test 1)) ;; Stop
          (if (= li-char ?')
	     (setq li-test 1)) ;; Stop
        )
        (setq li-test 0))
    )
  )

  ; Deleted spaces
  (beginning-of-line)
  (delete-horizontal-space)
  
  ; Append indentation.
  (setq li-indent (- li-indent li-indent-2))
  (setq li-indent (+ li-indent li-indent-base))
  (indent-to-column li-indent)

  ; Go to end of line.
  (end-of-line)
) 


(defun li-indent-command ()
  "indent line for Lisaac mode."
  (interactive)
  (beginning-of-line)
  (if (looking-at "[ \t]*Section.*$")
	; then
	(progn
	  (delete-horizontal-space)
	  (end-of-line))
     	;else
      (li-indent-previous))
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
;; autoload
;;
(defun lisaac-mode ()
  "Major mode for editing typical Lisaac code."
  (interactive)

; In LaTeX-mode we want this
;  (add-hook 'LaTeX-mode-hook
;            (function (lambda ()
;                        (paren-toggle-matching-quoted-paren 1)
;                        (paren-toggle-matching-paired-delimiter 1))))
(autoload 'paren-toggle-matching-paired-delimiter "mic-paren" "" t)
;(paren-toggle-matching-quoted-paren 1)
;  (paren-toggle-matching-paired-delimiter 1)
;(auto-overlay-load-definition
;'latex
;'(self ("\\$" (priority . 3) (face . (background-color . "green")))))
;(autoload 'latex-mode "Mode Latex" "jfh" t)
;(require 'font-latex)

  ;; compatibility MS-DOS
  (replace-string "" "")
  ;; Remove space not necessary
  ;(replace-regexp " +$" "")
 ; (global-font-lock-mode t)
  (global-set-key "\M-g" 'goto-line)

  (global-font-lock-mode t)

  (kill-all-local-variables)
  (setq mode-name "Lisaac")
  (setq major-mode 'lisaac-mode)
  (use-local-map li-mode-map)

  (make-local-variable 'li-mode-syntax-table)
  (set-syntax-table li-mode-syntax-table)

  (make-local-variable 'parse-sexp-ignore-comments)
  (setq parse-sexp-ignore-comments nil)
  
  (make-local-variable 'font-lock-string-face)
  (make-local-variable 'font-lock-defaults)

  ; Creation new face.
  (make-face 'li-slot-face)
  (set-face-foreground 'li-slot-face "blue") 
  ;(set-face-bold-p 'li-slot-face t)
  ;(set-face-italic-p 'li-slot-face t)  
  (set-face-underline-p 'li-slot-face t)
  ;(set-face-background 'li-slot-face "black")

  (make-face 'li-type-face)
  (copy-face font-lock-type-face 'li-type-face)
  (set-face-underline-p 'li-type-face t)

  ;(setq font-lock-string-face '(li-font-lock-string))
  (setq font-lock-defaults '(li-font-lock-keywords))
  
  ;; No replace spaces by tabulations
  (setq-default indent-tabs-mode nil)

  ; For use mouse Wheel.
  (require 'mwheel)
  (mwheel-install)

  (run-hooks 'li--mode-hook))

;;
;; End of Lisaac mode.
;;
(provide 'lisaac-mode)



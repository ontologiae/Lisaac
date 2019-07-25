#!/bin/bash

# +====================================================+
# |     Vim plugin installation script for Lisaac      |
# +====================================================+
# |                                                    |
# | This script will:                                  | 
# | -----------------                                  |
# | - add the lisaac.vim syntax file in your           |
# |   ~/.vim/syntax/                                   |   
# | - add the lisaac.vim indent file in your           |
# |   ~/.vim/indent/                                   |   
# | - add the detection if you edit an *.li file in    |
# |   your ~/.vimrc                                    |
# |                                                    |
# | (c) 2007 by Xavier Oswald, all right reserved      |
# |     Licence GPL version 2 or later                 |
# +====================================================+

echo 
echo "+=================================================+"
echo "|    Vim plugin installation script for Lisaac    |"
echo "+=================================================+"
echo 
echo -n "-> Are you sure you want to install this plugin ?(y/n)" 
read result
echo

if [ "$result" = "y" ]; then

	echo "-> Copying lisaac.vim syntax file in your ~/.vim/syntax/ : OK"
	mkdir -p ~/.vim/syntax/
	cp -f syntax/lisaac.vim ~/.vim/syntax/ 
	
	echo "-> Copying lisaac.vim indent file in your ~/.vim/indent/ : OK"
	mkdir -p ~/.vim/indent/
	cp -f indent/lisaac.vim ~/.vim/indent/ 

	echo

	echo -n "-> Do you want to install the default config provided by lisaac installer?(y/n)"
	read result
	if [ "$result" = "y" ]; then
		echo "-> Copying the default vim config file in your ~/.vimrc : OK"
		cp -f vimrc ~/.vimrc
	else
		if [ ! -f ~/.vimrc ]; then
			touch ~/.vimr
		fi
		echo "syntax on" >> ~/.vimrc
		echo "filetype plugin on" >> ~/.vimrc
		echo "filetype indent on" >> ~/.vimrc
		echo "au BufNewFile,BufRead *.li setf lisaac" >> ~/.vimrc
		echo "-> Modification of your ~/.vimrc to support *.li files : OK"
	fi
	echo
	echo "-> Installation finished : OK"
else
	echo "-> Installation of the vim plugin aborted..."
fi

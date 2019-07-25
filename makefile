#                   This file is part of Lisaac compiler.
#                     http://isaacproject.u-strasbg.fr/
#                    LSIIT - ULP - CNRS - INRIA - FRANCE
#
#   This program is free software: you can redistribute it and/or modify    
#   it under the terms of the GNU General Public License as published by    
#   the Free Software Foundation, either version 3 of the License, or       
#   (at your option) any later version.                                    
#                                                                           
#   This program is distributed in the hope that it will be useful,         
#   but WITHOUT ANY WARRANTY; without even the implied warranty of          
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           
#   GNU General Public License for more details.                            
#                                                                           
#   You should have received a copy of the GNU General Public License       
#   along with this program.  If not, see <http://www.gnu.org/licenses/>.   
#
# Available targets:
# ==================
#  - all                   Starts the installer in automatic mode--use it if
#  you know the installer is smart enough to compute default values, and those
#  values suit your needs. This option is used in order to install lisaac in a
#  non-userland way.
#
#  - interactive_userland  Starts the installer in userland interactive mode
#
#  - install               Copy all files in a proper place (non-userland)
#
#  - clean                 Cleans the installation (non-userland)
#
# In short:
# =========
#  Use : make interactive_userland     for a userland installation
#  Use : make & make install(as root)  for a full system installation
#  Use : make clean(as root)           to clean up a non-userland installation
#
# TODO:
# =====
#  - maybe use /etc/ instead of /usr/lib/lisaac four the compilation options
#  - do a /usr/share/menu/lisaac ?
#  - do a /usr/share/doc-base/lisaac ?
#
# Comments:
# =========
#  - use default path.h or bin/path.h if userland or not
#  - move binaries to /usr/bin/
#  - move libraries to /usr/lib/lisaac/
#  - move documentation to /usr/share/doc/lisaac/
#  - move manpages to /usr/share/man/man1/
#  - if you want to generate the documentation
#    shorter -r -f html lib -o $(LIB)$(HTML)
#
# Bug reports:
# ============
#  bug tracker system: https://gna.org/bugs/?func=additem&group=isaac
#  mail to: Xavier Oswald <x.oswald@free.fr>

MAN=/usr/share/man/man1
DOC=/usr/share/doc/lisaac
LIB=/usr/lib/lisaac
BIN=/usr/bin
HTML=/html
DESTDIR=
CC=gcc
CFLAGS=-O2

all: bin/lisaac.c bin/shorter.c
	@echo "#define LISAAC_DIRECTORY \"$(DESTDIR)$(LIB)\"" > bin/path.h
	$(CC) $(CFLAGS) bin/lisaac.c -o bin/lisaac 
	$(CC) $(CFLAGS) bin/shorter.c -o bin/shorter

interactive_userland: install_lisaac.c
	@echo - Lisaac compiler installation For Unix / Linux / Windows -
	@echo Please wait...
	$(CC) $(CFLAGS) install_lisaac.c -o install_lisaac
	@echo - please run ./install_lisaac to finish the installation

install:
	mkdir -p $(DESTDIR)$(LIB) 
	mkdir -p $(DESTDIR)$(BIN)
	mkdir -p $(DESTDIR)$(MAN)
	mkdir -p $(DESTDIR)$(DOC)$(HTML)
	cp bin/lisaac  $(DESTDIR)$(BIN) 
	cp bin/shorter  $(DESTDIR)$(BIN)
	cp path.li  $(DESTDIR)$(LIB)
	cp -rf lib/  $(DESTDIR)$(LIB)
	cp -rf lib_os/  $(DESTDIR)$(LIB)
	cp -rf shorter/  $(DESTDIR)$(LIB)
	cp -rf manpage/*.gz  $(DESTDIR)$(MAN)

	# Temprary since shorter is broken
	# $(DESTDIR)$(BIN)/shorter -r -f html lib -o $(DESTDIR)$(DOC)$(HTML) 
	#
	# previous html documentation:
	cp lib_html/* $(DESTDIR)$(DOC)$(HTML)

clean:
	rm -rf $(DESTDIR)$(BIN)/lisaac
	rm -rf $(DESTDIR)$(BIN)/shorter
	rm -rf $(DESTDIR)$(LIB)
	rm -rf $(DESTDIR)$(DOC)
	rm -rf $(DESTDIR)$(MAN)/lisaac.1.gz
	rm -rf $(DESTDIR)$(MAN)/shorter.1.gz

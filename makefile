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

CC=gcc
CFLAGS=-O2

all: install_lisaac
	./install_lisaac

install_lisaac:
	$(CC) $(CFLAGS) install_lisaac.c -o install_lisaac

clean:
	rm -f install_lisaac
	rm -f bin/shorter.c bin/path.h bin/lisaac bin/shorter
	rm -f src/shorter src/path.h
	rm -rf doc/html

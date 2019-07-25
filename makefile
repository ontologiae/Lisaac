DIST_PRJ=lisaac
DIST_VER=0.4.0
DIST_NAME=$(DIST_PRJ)-$(DIST_VER)
DIST_SRC=\
	Makefile \
	COPYING \
	Changelog \
	Not_yet_implemented \
	README \
	TODO \
	install_lisaac.c \
	install_lisaac.li \
	make.lip \
	bin \
	editor \
	example \
	lib \
	lib_os \
	manpage \
	shorter \
	src \
	tests

PREFIX=/usr/local
MAN=$(PREFIX)/share/man/man1
DOC=$(PREFIX)/share/doc/lisaac
LIB=$(PREFIX)/share/lisaac
BIN=$(PREFIX)/bin
HTML=/html
DESTDIR?=

CC=gcc
CFLAGS=-O3 -U_FORTIFY_SOURCE

default: user
	@echo "###########################################################"
	@echo "##                                                       ##"
	@echo "##  See available targets for this Makefile by running:  ##"
	@echo "##                                                       ##"
	@echo "##      make help                                        ##"
	@echo "##                                                       ##"
	@echo "###########################################################"

help:
	@echo "----------------------------------------------------------------"
	@echo "--               Lisaac IS An Advanced Compiler               --"
	@echo "--            LORIA - LSIIT - ULP - CNRS - FRANCE             --"
	@echo "--         Benoit SONNTAG - sonntag@icps.u-strasbg.fr         --"
	@echo "--                   http://www.lisaac.org/                   --"
	@echo "----------------------------------------------------------------"
	@echo "  make help         - display this help message"
	@echo ""
	@echo ""
	@echo "Quick installation:"
	@echo ""
	@echo "  make user         - compile the installer and run it"
	@echo ""
	@echo "For packagers:"
	@echo ""
	@echo "  make install      - install everything"
	@echo "  make uninstall    - uninstall everything"
	@echo "  make dist         - create a package with the sources only"
	@echo "  make distclean    - clean the source directory"
	@echo ""
	@echo "For developpers:"
	@echo ""
	@echo "  make all          - compile the compiler, the shorter and the"
	@echo "                      documentation"
	@echo "  make doc          - create documentation for the library"
	@echo "  make clean        - clean the source directory"
	@echo "  make clean-spaces - clean whitespaces at end of files"
	@echo ""

.PHONY: default all doc clean clean-spaces help user install uninstall dist distclean

all: bin/lisaac bin/shorter doc

#
# User Install
#

user: install_lisaac
	./install_lisaac

install_lisaac: install_lisaac.c

#
# path.h, make.lip
#

bin/path.h:
	@echo "#define LISAAC_DIRECTORY \"$(LIB)\"" > $@

path.h src/path.h:
	@echo "#define LISAAC_DIRECTORY \"`pwd`\"" > $@

make.lip: make.lip.sample
	ln -s $< $@

#
# Compiler and Shorter
#

bin/lisaac: bin/lisaac.c bin/path.h
	$(CC) $(CFLAGS) $< -o $@ -lm -fomit-frame-pointer

bin/shorter: bin/lisaac make.lip bin/path.h
	cd bin && ./lisaac ../src/make.lip -shorter -boost

#
# Documentation
#

doc: doc/html src/HACKING.html
doc/html: bin/shorter make.lip lib
	mkdir -p doc/html
	cd doc && ../bin/shorter -d -f belinda ../lib -o html 

#
# Installation
#

install: bin/lisaac bin/shorter
	mkdir -p $(DESTDIR)$(LIB) 
	mkdir -p $(DESTDIR)$(BIN)
	mkdir -p $(DESTDIR)$(MAN)
	mkdir -p $(DESTDIR)$(DOC)$(HTML)
	cp bin/lisaac  $(DESTDIR)$(BIN) 
	cp bin/shorter  $(DESTDIR)$(BIN)
	cp make.lip.sample  $(DESTDIR)$(LIB)/make.lip
	cp -rf lib/  $(DESTDIR)$(LIB)
	cp -rf lib_os/  $(DESTDIR)$(LIB)
	cp -rf doc/html/ $(DESTDIR)$(DOC)$(HTML)
	cp -rf shorter/  $(DESTDIR)$(LIB)
	cp -rf manpage/*.gz  $(DESTDIR)$(MAN)

uninstall:
	-rm -rf $(DESTDIR)$(BIN)/lisaac
	-rm -rf $(DESTDIR)$(BIN)/shorter
	-rm -rf $(DESTDIR)$(LIB)
	-rm -rf $(DESTDIR)$(DOC)
	-rm -rf $(DESTDIR)$(MAN)/lisaac.1.gz
	-rm -rf $(DESTDIR)$(MAN)/shorter.1.gz

#
# Distribution
#

dist: clean
	if ! test -d $(DIST_NAME) ; then \
		mkdir $(DIST_NAME) ; \
	fi
	cp -rf $(DIST_SRC) $(DIST_NAME)
	find $(DIST_NAME) \( -name .svn -o -name .git -o -name CVS -o -name .cvsignore -o -name "*.jar" \) -print0 | xargs -0 $(RM) -rf
	tar cjf $(DIST_NAME).tar.bz2 $(DIST_NAME)
	-$(RM) -rf $(DIST_NAME)

distclean: clean

#
# Clean
#

clean:
	-$(RM) -rf bootstrap
	-$(RM) -f make.lip
	-$(RM) -f install_lisaac
	-$(RM) -f bin/path.h bin/shorter.c bin/shorter bin/lisaac
	-$(RM) -f src/path.h src/shorter.c src/shorter src/lisaac src/lisaac.c
	-$(RM) -f path.h shorter shorter.c lisaac lisaac.c
	-$(RM) -rf doc/html
	-find . \( -name "*.orig" -o -name "*.BACKUP.*" -o -name "*.BASE.*" -o -name "*.LOCAL.*" -o -name "*.REMOTE.*" \) -print0 | xargs -0 $(RM) -rf
	# -git clean -fdx

clean-spaces:
	-find . \( -name "*.li" -o -name "*.lip" \) -print0 | xargs -0 sed -i 's/\s*$$//'

src/HACKING.html: src/HACKING Markdown.pl
	$(MARKDOWN_CMDLINE)

### Markdown ###

MARKDOWN_URL=http://daringfireball.net/projects/downloads/Markdown_1.0.1.zip
MARKDOWN_DIR=Markdown_1.0.1
MARKDOWN_CMDLINE=./Markdown.pl <$< >$@

Markdown.zip:
	wget $(MARKDOWN_URL) -O $@

Markdown.pl:
	$(MAKE) Markdown.zip
	unzip -u -j Markdown.zip $(MARKDOWN_DIR)/$@
	chmod +x $@
	-$(RM) Markdown.zip

%.html: %.mdwn Markdown.pl
	$(MARKDOWN_CMDLINE)

%.html: % Markdown.pl
	$(MARKDOWN_CMDLINE)

install:
	@echo - Lisaac compiler installation For Unix / Linux-
	@echo Please wait ...
	@cd src;gcc -O2 *.c -o ../bin/lisaac -lm;cd ..
	@gcc -O2 install_lisaac.c -o install_lisaac
	@echo Run 'install_lisaac' to finish the installation

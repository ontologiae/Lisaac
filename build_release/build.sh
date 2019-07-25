#! /bin/bash

cd ..
find . -name '*~' -exec rm {} \;
find . -name '#*' -exec rm {} \;
find . -name '*.bak' -exec rm {} \;
rm -rf doc/html/*
rm install_lisaac
rm src/lisaac
rm src/build_label
rm src/shorter

# Slide compiler.pdf
#cd doc/slides/compiler
#latex lisaac.tex ; latex lisaac.tex ; dvips lisaac.dvi ; ps2pdf lisaac.ps
#rm *.aux *.dvi *.log *.nav *.out *.ps *.snm *.toc
#mv lisaac.pdf ../compiler.pdf
#cd ../../..

# Slide language.pdf
#cd doc/slides/language
#latex lisaac.tex ; latex lisaac.tex ; dvips lisaac.dvi ; ps2pdf lisaac.ps
#rm *.aux *.dvi *.log *.nav *.out *.ps *.snm *.toc
#mv lisaac.pdf ../language.pdf
#cd ../../..

# Slide manual.pdf
#cd doc/manual
#latex lisaac.tex 
#bibtex lisaac
#bibtex lisaac
#latex lisaac.tex 
#makeindex lisaac.idx
#latex lisaac.tex 
#dvips lisaac.dvi 
#ps2pdf lisaac.ps
#rm *.aux *.dvi *.log *.ps *.toc *.ind *.idx *.ilg *.bbl *.blg
#mv lisaac.pdf ../manual.pdf
#cd ../..

# Build .tgz and .zip
version=`lisaac -version | grep "Version:" | awk -F "0" '{ gsub(" ", "_"); print "_0"$2 }'`
cd ..
rm -f lisaac$version.tar.gz
tar -czvf lisaac$version.tar.gz lisaac
rm -f lisaac$version.zip
zip -r lisaac$version.zip lisaac


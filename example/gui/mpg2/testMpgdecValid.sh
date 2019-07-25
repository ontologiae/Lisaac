#!/bin/sh


./mpeg2dec anim.mpe -o1 rec
for i in `seq 0 186` ; do cat rec$i.Y rec$i.U rec$i.V >> binTest ; done
rm *.Y *.U *.V

./mpeg2dec anim.mpe -o2 rec
for i in `seq 0 186` ; do cat rec$i.SIF >> binTest ; done
rm *.SIF

./mpeg2dec anim.mpe -o3 rec
for i in `seq 0 186` ; do cat rec$i.tga >> binTest ; done
rm *.tga

./mpeg2dec anim.mpe -o4 rec
for i in `seq 0 186` ; do cat rec$i.ppm >> binTest ; done
rm *.ppm


md5sum -c testValide.md5
rm binTest
#if [ $var == "value" ]
#then
#echo is the same
#fi

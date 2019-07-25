#!/bin/bash

for i in `find -name "*~"` ; do rm $i ; done
for i in `find -name "*.c"` ; do rm $i ; done
for i in `find -name "*.exe"` ; do rm $i ; done

for i in `find -name "*.li"`
do
   FILE=`echo $i|sed -e 's/\.li$//;s%^\./%%'`
   if [ -f $FILE ];then
      rm $FILE
   fi
done

exit 0

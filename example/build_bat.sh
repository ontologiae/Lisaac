#!/bin/bash

rm compile.bat
for i in `find -name "*.li"` ; do 
  cat $i | grep -q "\- main <" && echo lisaac $i -q -boost >> compile.bat
done

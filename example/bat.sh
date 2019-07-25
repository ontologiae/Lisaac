#!/bin/bash

rm compile.bat
for i in `find -name "*.li"` ; do 
  if [[ $i == "./gui/desktop/desktop.li" ]]
  then
      echo lisaac gui/make.lip gui/desktop/desktop.li -boost -q >> compile.bat
  else
      cat $i | grep -q "\- main <" && echo lisaac $i -q -boost >> compile.bat
  fi
done

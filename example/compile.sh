#!/bin/bash

for i in `find -name "*.li"` ; do 
  if [[ $i == "./gui/desktop/desktop.li" ]]
  then
      echo Compile $i
      lisaac gui/make.lip gui/desktop/desktop.li -boost -q
  else
      cat $i | grep -q "\- main <" && echo Compile $i && lisaac $i -q -boost
  fi
done

#!/bin/bash

for i in `find -name "*.li"` ; do cat $i | grep -q "main" && echo Compile $i && lisaac $i -O -q ; done

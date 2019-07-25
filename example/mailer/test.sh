#! /bin/bash

echo Launch Program
./mailer ./test
echo Test md5sum
#echo $PWD
md5sum	-c mail.md5
rm test/mail.txt


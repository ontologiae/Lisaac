#!/bin/bash

echo "---------------------------------------"
echo "-- LiA: Lisaac Android - Log viewer  --"
echo "---------------------------------------"
echo "          By Sonntag & Cavallero, 2018."
echo 
echo "Usage: $0 [<filter>]"
echo "Note: by default <filter>=LIA"
echo

if [ -z "$1" ]
then
    android-sdk/platform-tools/adb logcat | grep "LIA"
else
    android-sdk/platform-tools/adb logcat | grep $1
fi

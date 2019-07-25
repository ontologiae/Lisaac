#!/bin/bash

QUIET=1

if [ $QUIET -eq 0 ]; then
    echo "---------------------------------------"
    echo "-- LiA: Lisaac Android - APK Builder --"
    echo "---------------------------------------"
    echo "          By Sonntag & Cavallero, 2018."
    echo
fi

if [ -z "$1" ]; then
    echo
    echo "Usage: $0 <name_project>"
    echo
    exit
fi

set -e
LI=`which lisaac`
LI=`dirname $LI`/..
LIA=$LI/lia
TOOL="$LIA/tool"
OPS="-Wno-parentheses-equality"

AAPT="$LIA/android-sdk/build-tools/debian/aapt"
ZIPA="$LIA/android-sdk/build-tools/debian/zipalign"
ADB="$LIA/android-sdk/platform-tools/adb"

if [ $QUIET -eq 0 ]; then
    echo "I] Create android interface."
fi

if [ ! -d "Lia" ]; then
    if [ $QUIET -eq 0 ]; then
        echo " 1) Copy icones by default in \`Lia/$1/res' ..."
    fi
    mkdir -p Lia/lib/arm64-v8a
    mkdir -p Lia/lib/armeabi-v7a
    cp -r $LIA/tool/res Lia/.

    if [ $QUIET -eq 0 ]; then
        echo " 2) Copy & update \`AndroidManifest.xml' ..."
    fi
    cp $LIA/tool/AndroidManifest.xml Lia/.
    sed -i "s/!MY_PROJECT!/$1/" Lia/AndroidManifest.xml
fi

if [ $QUIET -eq 0 ]; then
    echo
    echo "Done."
    echo 
fi

if [ $QUIET -eq 0 ]; then
    echo "II] APK builder."
    echo " 1) Cleaning..."
fi
rm -rf *.apk
rm -rf Lia/lib/arm64-v8a/*
rm -rf Lia/lib/armeabi-v7a/*

if [ $QUIET -eq 0 ]; then
    echo " 2) Compiling for ARM 32bits..."
fi
CMD="$LIA/android-ndk-r18/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin"

INC="-I $LIA/android-ndk-r18/sysroot/usr/include -I $LIA/android-ndk-r18/sysroot/usr/include/arm-linux-androideabi -I $LIA/tool"

ARCH="$TOOL/armeabi-v7a"
SO="Lia/lib/armeabi-v7a"

if [ $QUIET -eq 0 ]; then
    echo "  --> Compiling \`android_native_glue'..."
fi
$CMD/arm-linux-androideabi-gcc -c $TOOL/android_native_app_glue.c -O2 $INC

if [ $QUIET -eq 0 ]; then
    echo "  --> Compiling \`$1' ..."
fi
$CMD/arm-linux-androideabi-gcc -c $1.c $INC $OPS

if [ $QUIET -eq 0 ]; then
    echo "  --> Linking..."
fi
LIB="-L $LIA/android-ndk-r18/platforms/android-19/arch-arm/usr/lib"

$CMD/arm-linux-androideabi-ld -shared $LIB/crtbegin_so.o $LIB/crtend_so.o $1.o android_native_app_glue.o -o $SO/lib$1.so -landroid -lEGL -lGLESv3  -llog -lm -ldl -lc -L . $LIB -u ANativeActivity_onCreate -s

if [ $QUIET -eq 0 ]; then
    echo " 3) Compiling for ARM 64bits..."
fi
CMD="$LIA/android-ndk-r18/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin"

INC="-I $LIA/android-ndk-r18/sysroot/usr/include -I $LIA/android-ndk-r18/sysroot/usr/include/aarch64-linux-android -I $LIA/tool"

ARCH="$TOOL/arm64-v8a"
SO="Lia/lib/arm64-v8a"

if [ $QUIET -eq 0 ]; then
    echo "  --> Compiling \`android_native_glue'..."
fi
$CMD/aarch64-linux-android-gcc -c $TOOL/android_native_app_glue.c -O2 $INC 

if [ $QUIET -eq 0 ]; then
    echo "  --> Compiling \`$1' ..."
fi
$CMD/aarch64-linux-android-gcc -c $1.c $INC $OPS

if [ $QUIET -eq 0 ]; then
    echo "  --> Linking..."
fi
LIB="-L $LIA/android-ndk-r18/platforms/android-22/arch-arm64/usr/lib"

$CMD/aarch64-linux-android-ld -shared $LIB/crtbegin_so.o $LIB/crtend_so.o $1.o android_native_app_glue.o -o $SO/lib$1.so -landroid -lEGL -lGLESv3  -llog -lm -ldl -lc -L . $LIB -u ANativeActivity_onCreate -s

if [ $QUIET -eq 0 ]; then
    echo " 4) Making APK..."
    echo "  --> Create APK empty..."
fi
$AAPT package -f -F $1.unaligned.apk -M Lia/AndroidManifest.xml -S Lia/res -I $TOOL/android.jar

if [ $QUIET -eq 0 ]; then
    echo "  --> Add \`lib$1.so' (32 & 64bits) in APK ..."
fi
mv $1.unaligned.apk $1.unaligned.zip
cd Lia
zip -q ../$1.unaligned.zip lib/armeabi-v7a/lib$1.so
zip -q ../$1.unaligned.zip lib/arm64-v8a/lib$1.so
cd ..
mv $1.unaligned.zip $1.unaligned.apk

if [ $QUIET -eq 0 ]; then
    echo "  --> Add \`classes.dex' in APK..."
fi
$TOOL/dx --dex --output=classes.dex $TOOL/uiautomator.jar

if [ $QUIET -eq 0 ]; then
    $AAPT add $1.unaligned.apk classes.dex
else
    $AAPT add $1.unaligned.apk classes.dex > /dev/null
fi

if [ $QUIET -eq 0 ]; then
    echo "  --> Aligning APK..."
fi
$ZIPA -f 4 $1.unaligned.apk $1.apk

if [ $QUIET -eq 0 ]; then
    echo "  --> Signing APK..."
    java -jar $TOOL/apksigner.jar sign --min-sdk-version 16 --ks $TOOL/mykey.keystore $1.apk < $TOOL/passwd.txt
else
    java -jar $TOOL/apksigner.jar sign --min-sdk-version 16 --ks $TOOL/mykey.keystore $1.apk < $TOOL/passwd.txt >/dev/null
fi
if [ $QUIET -eq 0 ]; then
    echo " 5) Cleaning..."
fi
rm *.o classes.dex $1.unaligned.apk

if [ $QUIET -eq 0 ]; then
    echo
    echo "Done."
    echo
fi

if [ $QUIET -eq 0 ]; then
    echo "III] Launching on your target..."
fi
$ADB install -r $1.apk
#if [ $QUIET -eq 0 ]; then
    echo
    echo "Note: Use \`lisaac/lia/log.sh' for to read "
    echo "      your \`output' information."
    echo
#fi

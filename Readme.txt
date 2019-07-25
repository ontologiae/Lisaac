
           Welcome to the Lisaac compiler !

                        ~~~~
The first compiler for Object Prototype Language !

Install.
========

* Linux/Unix:
-------------

For the installation in your system, simply to tape : 

    make
    ./install_lisaac

Run isaac' to compile.


* Windows/Dos:
--------------

1- Download from our site:
   * djgpp.zip, for C compiler and binutils (GCC for Windows)
   * rsxntdj.zip, if you want to make Windows application
   * emacs_207.zip, for Lisaac editor facilities
   * last lisaac compiler version (>= 0084)

2- Uncompress djgpp and rsxntdj to c:\ and emacs...

3- Update your C:\AUTOEXEC.BAT:
Append with good path:

REM **** DJGPP ****
set DJGPP=C:\djgpp\djgpp.env
set PATH=C:\djgpp\bin\:%PATH%

REM **** RSXNTDJ **
SET RSXNTDJ=C:\rsxntdj
SET PATH=%PATH%;C:\rsxntdj\bin
SET LIBRARY_PATH=C:\rsxntdj\lib;C:\djgpp\lib

4- Verify and update file : /rsxntdj/lib/specs
In 'cpp' option, update path include with emacs editor only.
Example :

%{Zwin32: -Ic:/rsxntdj/include -Ic:/rsxntdj/include/win32 -D__WIN32__
-D_WIN32 >-D__RSXNT__ %{Zdll:-D__DLL__}}

5- Reboot or reloaded AUTOEXEC.BAT

6- In lisaac directory, to run 'make', and 'install_lisaac'

7- Reboot or reloaded AUTOEXEC.BAT

Directory description.
======================

  ./lib     : Standard Library for Lisaac.
  ./lib_os  : Library for Unix and Windows and Dos (Input/Output,FileSystem)
  ./bin     : Executable file compiler (and C source).
  ./emacs   : Lisaac mode for Emacs editor.
  ./manual  : Manual reference Lisaac language (PS format).
  ./example : Example Lisaac source code. 


Good luck,
Best regard,

     Lisaac team.
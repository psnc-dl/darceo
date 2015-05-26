::-----------------------------------------------------------------
:: Windows script to execute FITS -- install as windows service
@echo off

:: Make FITS_HOME your working directory 
set FITS_DIR=%FITS_HOME:~0,2%
%FITS_DIR%

cd %FITS_HOME%

set JARS=
set CLASSPATH=

for %%i in (lib\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\droid\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\jhove\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\nzmetool\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i
for %%i in (lib\nzmetool\adapters\*.jar) do call "%FITS_HOME%\cpappend.bat" %%i

set CLASSPATH=%JARS%;%FITS_HOME%\xml\nlnz

java -Xmx1024m pl.psnc.synat.fits.rmi.FitsRmi %1

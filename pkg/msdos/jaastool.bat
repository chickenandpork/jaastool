@echo off 

REM A quick script to help people find their JRE and run it with the JAR file
REM NOTE: if you can run a "java -jar jaastool.jar" then you don't need this

REM First, find the JRE
set JAVA="C:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="C:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="D:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="D:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="E:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="E:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="F:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="F:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="G:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="G:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="H:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="H:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="I:\Program Files\Virtual Instruments\VirtualWisdom\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="I:\Program Files (x86)\Virtual Instruments\VirtualWisdom Client\jre\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="C:\Program Files (x86)\Java\jre6\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="C:\Program Files (x86)\Java\jre7\bin\java.exe"
if exist %JAVA% goto :findjar
set JAVA="C:\Program Files\SANscreen\java\bin\java.exe"

:findjar
REM ready-to-run... where is the jaastool.jar file?
set JAR=%~dp0\JAASTOOL.jar
if exist %JAR% goto :run
set JAR=.\JAASTOOL.jar
if exist %JAR% goto :run
set JAR=%JAVA%\..\..\..\UnSupported\JAASTOOL.jar
if exist %JAR% goto :run

REM if we don't find the JAR echo a quick reminder
echo The jaastool.jar file should be in the same directory as the jaastool.bat,
echo or your current working directory, or the VirtualWisdom\UnSupported\ directory
goto :quit


REM OK, hit it: run the JAR file
:run

call %JAVA% -jar %JAR% %1 %2 %3 %4 %5 %6 %7 %8
:quit

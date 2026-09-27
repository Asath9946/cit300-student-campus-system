@echo off
REM ============================================================
REM  CIT300 - University Student Record and Campus Route System
REM  Double-click this file (Windows) to compile and run.
REM  Requires JDK 8 or newer (javac and java on PATH).
REM ============================================================
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
echo Compiling...
javac -encoding UTF-8 -d out -sourcepath src src\app\Main.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Check that the JDK is installed: javac -version
    pause
    exit /b 1
)
echo Starting the system...
echo.
java -cp out app.Main
pause

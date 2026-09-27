@echo off
REM Compiles and runs all automated tests (Windows).
cd /d "%~dp0"
if exist out-test rmdir /s /q out-test
mkdir out-test
javac -encoding UTF-8 -d out-test -sourcepath "src;test" test\TestRunner.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
java -cp out-test TestRunner
pause

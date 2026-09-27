#!/bin/sh
# CIT300 - University Student Record and Campus Route System
# Compile and run on macOS / Linux:   sh run.sh
cd "$(dirname "$0")" || exit 1
rm -rf out && mkdir out
echo "Compiling..."
javac -encoding UTF-8 -d out -sourcepath src src/app/Main.java || { echo "Compilation failed."; exit 1; }
echo "Starting the system..."
echo
java -cp out app.Main

#!/bin/sh
# Compiles and runs all automated tests (macOS / Linux):   sh test.sh
cd "$(dirname "$0")" || exit 1
rm -rf out-test && mkdir out-test
javac -encoding UTF-8 -d out-test -sourcepath "src:test" test/TestRunner.java || { echo "Compilation failed."; exit 1; }
java -cp out-test TestRunner

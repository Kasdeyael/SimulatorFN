@ECHO OFF
TITLE SimulatorFN

REM Repast Simphony model run script for Windows systems 
REM 
REM Please note that the paths given below use a Linux-like notation. 

REM Note the Repast Simphony Directories.
set REPAST_SIMPHONY_ROOT=../repast.simphony/repast.simphony.runtime_2.9.0/

REM Define the Core Repast Simphony Directories and JARs
SET CP=%CP%;%REPAST_SIMPHONY_ROOT%bin
SET CP=%CP%;%REPAST_SIMPHONY_ROOT%lib/*

REM Groovy jar
SET CP=%CP%;../groovylib/groovy-3.0.9-indy.jar

REM User model lib jars
SET CP=%CP%;lib/*

REM Change to the project directory
CD "SimulatorFN"

REM Start the Model
START javaw -XX:+IgnoreUnrecognizedVMOptions --add-modules=ALL-SYSTEM --add-exports=java.base/jdk.internal.ref=ALL-UNNAMED -cp "%CP%" repast.simphony.runtime.RepastMain "./SimulatorFN.rs"

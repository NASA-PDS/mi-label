:: Copyright 2010-2011, by the California Institute of Technology.
:: ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
:: Any commercial use must be negotiated with the Office of Technology Transfer
:: at the California Institute of Technology.
::
:: This software is subject to U. S. export control laws and regulations
:: (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
:: is subject to U.S. export control laws and regulations, the recipient has
:: the responsibility to obtain export licenses or other export authority as
:: may be required before exporting such information to foreign countries or
:: providing access to foreign nationals.
::
:: $Id$

:: Batch file that allows easy execution of the PDS4 Generation Tool
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.pds.pds4.generation.GenerationLauncher ...)

@echo off

:: Expects PDS4 Generation Tool jar file to be located in the ../lib directory.

set SCRIPT_DIR=%~dps0
set PARENT_DIR=%SCRIPT_DIR%..

set LIB_DIR=%PARENT_DIR%\lib

if exist "%LIB_DIR%\generation-tool-*.jar" (
set GENERATION_TOOL_JAR=%LIB_DIR%\generation-tool-*.jar
) else (
echo Cannot find Generation Tool jar file in %LIB_DIR%
goto END
)

:: Finds the jar file in LIB_DIR and sets it to GENERATION_TOOL_JAR

for %%i in ("%LIB_DIR%"\generation-tool-*.jar) do set GENERATION_TOOL_JAR=%%i

:: Executes GENERATION_TOOL via the executable jar file
:: The special variable '%*' allows the arguments
:: to be passed into the executable.

java -jar "%GENERATION_TOOL_JAR%" %*

:END

@REM *****************************************************************
@REM HCL Confidential
@REM OCO Source Materials
@REM
@REM Copyright HCL Technologies Limited 2010, 2020
@REM
@REM The source code for this program is not published or otherwise
@REM divested of its trade secrets, irrespective of what has been
@REM deposited with the U.S. Copyright Office.
@REM
@REM *****************************************************************

@echo off
echo.
SET CLASS_NAME=%1
SET lc_home=%2
SET resource_file=%3
SET PRODUCT=HCL Connections 6.5.0.0_CR1 Toolkit
SET JAVA_HOME_TESTED=""
SET WAS_HOME_TESTED=""
SET JAVA_CMD=""
SET FIXED_CLASS_PATH=.;lib/lc_migration.jar
SET testParameter=true

cd /d %~dp0

IF %testParameter% == true GOTO TestParameter

:TestParameter
if {%CLASS_NAME%}=={} goto Help
if {%lc_home%}=={} goto Help
if NOT EXIST %lc_home% goto INVALID_LC_HOME
if {%resource_file%}=={} goto Help
GOTO TestWAS_HOME

:TestWAS_HOME
SET WAS_HOME_TESTED=true
if "%WAS_HOME%" == "" GOTO TestJava
echo Find WAS_HOME %WAS_HOME%
echo.
SET JAVA_CMD=%WAS_HOME%\java\bin\java.exe
GOTO TestJava

:TestJAVA_HOME
SET JAVA_HOME_TESTED=true
if "%JAVA_HOME%" == "" GOTO TestJava
echo Find JAVA_HOME %JAVA_HOME%
echo.
SET JAVA_CMD=%JAVA_HOME%\bin\java.exe
GOTO TestJava

:Launch
echo Using java.exe at "%JAVA_CMD%"
echo.
SET CLASSPATH=%FIXED_CLASS_PATH%
"%JAVA_CMD%" %CLASS_NAME% %lc_home% %resource_file%
GOTO END

:TestJava
IF NOT "%JAVA_CMD%" == """" echo Locating %JAVA_CMD%
iF EXIST "%JAVA_CMD%" goto Launch
IF NOT %JAVA_HOME_TESTED% == true goto TestJAVA_HOME
IF NOT %WAS_HOME_TESTED% == true goto TestWAS_HOME
GOTO JAVA_FAIL

:Help
echo.
echo "Usage: util <class_name> <lc_home> <resource_path>"
echo.
pause
GOTO END

:JAVA_FAIL
IF "%WAS_HOME%" == "" echo WAS_HOME is not set
IF "%JAVA_HOME%" == "" echo JAVA_HOME is not set
IF NOT "%JAVA_CMD%" == """" echo "%JAVA_CMD%" does not exist
IF "%JAVA_CMD%" == "" echo WAS_HOME or JAVA_HOME must be set.
GOTO FAIL

:INVALID_LC_HOME
echo.
echo %lc_home% is not a valid IBM Connections Install location.
echo.
GOTO FAIL

:FAIL
echo.
echo Failed to launch %PRODUCT%.
echo.
GOTO END

:END

@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                              
@REM                                                                   
@REM Copyright IBM Corp. 2013, 2016                                    
@REM                                                                   
@REM The source code for this program is not published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

@ECHO OFF
SETLOCAL

SET CE_HOME=..\ContentEngine

@REM IF NOT EXIST "%JAVA_HOME%\bin\java.exe" GOTO useCEJavaHome
@REM GOTO Launch

:useCEJavaHome
SET JAVA_HOME=%CE_HOME%\tools\_jvm17\jre
GOTO Launch

:Launch
CALL :log %DATE% %TIME%
CALL :log CE_HOME=%CE_HOME%
CALL :log JAVA_HOME=%JAVA_HOME%

IF NOT EXIST create_gcd.ok (
	CALL :log
	CALL :log ERROR: It appears the last attempt to create the Global Configuration Database - also known as the GCD or domain - may not have succeeded.
	CALL :log If you are just getting started, it is recommended that you drop and recreate the GCD database using the Database Wizard and then rerun the createGCD.bat script before continuing. 
	CALL :log It is possible the current GCD contains errors.
	CALL :log
	
	EXIT /B 1
)

SET CLASSPATH=.;%CE_HOME%\lib\Jace.jar;%CE_HOME%\lib\log4j.jar;.\ccmDomainTool.jar

"%JAVA_HOME%\bin\java" -classpath %CLASSPATH% com.ibm.connections.ccmDomainTool.ccmDomainTool generateSID

ENDLOCAL
GOTO :EOF

REM ====================
REM Function definitions
REM ====================
:log
	ECHO.%*
	ECHO.%* >> "ccmDomainTool.log" 2>&1
GOTO :EOF

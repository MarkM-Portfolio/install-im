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

SET CE_HOME=..\FileNet\ContentEngine
SET FNCS_HOME=..\FNCS

@REM IF NOT EXIST "%JAVA_HOME%\bin\java.exe" GOTO useCEJavaHome
@REM GOTO Launch

:useCEJavaHome
SET JAVA_HOME=%CE_HOME%\tools\_jvm17\jre
GOTO Launch

:Launch
CALL :log %DATE% %TIME%
CALL :log CE_HOME=%CE_HOME%
CALL :log FNCS_HOME=%FNCS_HOME%
CALL :log JAVA_HOME=%JAVA_HOME%

SET CLASSPATH=.;%CE_HOME%\lib\Jace.jar;%CE_HOME%\lib\log4j.jar;.\ccmDomainTool.jar

"%JAVA_HOME%\bin\java" -classpath %CLASSPATH% com.ibm.connections.ccmDomainTool.ccmDomainTool createP8Domain

IF NOT EXIST "gcd_success" GOTO end_gcd

REM Remove previous success run output flag
DEL /F create_gcd.ok 2>NUL
REM Generate new success run output flag
ECHO %DATE% %TIME% >create_gcd.ok

:end_gcd
ENDLOCAL
GOTO:EOF

REM ====================
REM Function definitions
REM ====================
:log
	ECHO.%*
	ECHO.%* >> "%~dp0\ccmDomainTool.log" 2>&1
GOTO :EOF

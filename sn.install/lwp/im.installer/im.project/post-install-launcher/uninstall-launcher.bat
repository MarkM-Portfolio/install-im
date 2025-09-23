@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                             
@REM                                                                   
@REM Copyright IBM Corp. 2010, 2015                                    
@REM                                                                   
@REM The source code for this program is not published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

@REM Build: @BUILD_RELEASE@
@ECHO OFF

ECHO %DATE% %TIME% >> uninstall-launcher.log

SET WAS_HOME=%~1
ECHO Setting WAS_HOME=%~1 >> uninstall-launcher.log

SET WAS_PROFILE_HOME=%~6
ECHO Setting WAS_PROFILE_HOME=%~6 >> uninstall-launcher.log

SET INSTALL_OPTIONS=-uninstall
SET LOG_FILE=%HOME%uninstall.log

ECHO INFO1: CONN_OPTION=%CONN_OPTION% >> uninstall-launcher.log
IF "%CONN_OPTION%"=="" (SET CONN_OPTION=-conntype SOAP)
ECHO INFO2: CONN_OPTION=%CONN_OPTION% >> uninstall-launcher.log

FOR %%i in ("%~dp0.") DO (
SET MY_HOME=%%~dpi
)
SET XKIT=%MY_HOME%xkit
SET CFG_PY=%MY_HOME%cfg.py
SET SCRIPTS_DIR=%~dp0

set CFG_PY=%MY_HOME:\=/%cfg.py
set XKIT=%MY_HOME:\=/%xkit

SET WSADMIN_CLASSPATH=%MY_HOME%lib\lccfg.jar;%MY_HOME%lib\jose4j-0.6.3.jar;%MY_HOME%lib\json4j.jar

IF "%~8"=="ROLLBACK" (
GOTO COMPLETE
)

IF %7 == 1 (
ECHO skip jython installer >> uninstall-launcher.log
) ELSE (
@REM ECHO START "Post install launcher" /D "%~2" bin\lc-install.bat -host localhost -port %~5 -user "%~3" -password %~4 >> post-install-launcher.log
@REM bin\lc-install.bat -host localhost -port %~5 -user %~3 -password %~4

ECHO "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %~5 -user %~3 -password PASSWORD_REMOVED -javaoption "-Dpython.path=%MY_HOME%lib" -wsadmin_classpath "%WSADMIN_CLASSPATH%" -javaoption "-Xmx512m" -f "%SCRIPTS_DIR%install.py" "-uninstall" "%CFG_PY%" "%XKIT%" >> uninstall-launcher.log 

CALL "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %~5 -user %~3 -password %~4 ^
-javaoption "-Dpython.path=%MY_HOME%lib" ^
-wsadmin_classpath "%WSADMIN_CLASSPATH%" ^
-javaoption "-Xmx512m" ^
-javaoption "-Duser.language=en" ^
-javaoption "-Duser.country=US" ^
-f "%SCRIPTS_DIR%install.py" -uninstall "%CFG_PY%" "%XKIT%" > "%MY_HOME%uninstall.log" 2>&1

)

ECHO EXIT CODE IS %ERRORLEVEL% >> post-install-launcher.log
IF %ERRORLEVEL% NEQ 0 (
ECHO Connections Uninstall FAILED! Please check log for details. >> uninstall-launcher.log
EXIT %ERRORLEVEL%
)
ECHO Connections Uninstall finished SUCCESSFULLY! >> uninstall-launcher.log

:COMPLETE
EXIT /B 0

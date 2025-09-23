@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                             
@REM                                                                   
@REM Copyright IBM Corp. 2010, 2018                                    
@REM                                                                   
@REM The source code for this program is NOT published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

@REM Build: @BUILD_RELEASE@
@ECHO OFF

SET INSTALL_DATE=%DATE%
SET INSTALL_TIME=%TIME%
@REM For ease of reading install log
CALL :Append "........"
CALL :Append "........"
CALL :Append "........"
CALL :Append "INFO: ********%INSTALL_DATE% %INSTALL_TIME%********"

:parseArguments
@REM "~" remove quotes around the input parameter, "s" change the path to short pattern to remove any space character. 
@REM "~s" will change "C:\Program Files (x86)\Common Files" to C:\PROGRA~2\COMMON~1
@REM DO NOT put any percentage char % + ~ in the batch comment. It will cause batch script parse error.
SET WAS_HOME=%~s1
CALL :Append "INFO: WAS_HOME=%WAS_HOME%"
SET IC_HOME=%~s2
CALL :Append "INFO: IC_HOME=%IC_HOME%"
SET WAS_USER=%~3
CALL :Append "INFO: WAS_USER=%WAS_USER%"
SET WAS_USER_PW=%~4
SET WAS_PORT=%~5
CALL :Append "INFO: WAS_PORT=%WAS_PORT%"
SET WAS_PROFILE_HOME=%~s6
CALL :Append "INFO: WAS_PROFILE_HOME=%WAS_PROFILE_HOME%"
SET SKIPJYTHON_FLAG=%~7
CALL :Append "INFO: SKIPJYTHON_FLAG=%SKIPJYTHON_FLAG%"
@REM USERJOB has these values:
@REM  1. When fresh install, USERJOB=INSTALL
@REM  2. When update install, USERJOB=UPDATE
@REM  3. When rollback install, USERJOB=ROLLBACK
@REM  4. When modify/add install, USERJOB=MODIFY_ADD
@REM  5. When modify/remove install, USERJOB=MODIFY_REMOVE
SET USERJOB=%~8
CALL :Append "INFO: USERJOB=%USERJOB%"
SET CELL_NAME=%~9
CALL :Append "INFO: CELL_NAME=%CELL_NAME%"

@REM Get Connection install home folder with tailing path char "\" from the batch command itself
@REM Important: Do IC HOME calculation before any shift of command line arguments
FOR %%i in ("%~dp0.") DO (
SET MY_HOME=%%~sdpi
)

SET SCRIPTS_DIR=%~dp0
@REM Replace path char "\" with "/"
SET CFG_PY=%MY_HOME:\=/%cfg.py
SET XKIT=%MY_HOME:\=/%xkit

CALL :Append "INFO: MY_HOME=%MY_HOME%"
CALL :Append "INFO: SCRIPTS_DIR=%SCRIPTS_DIR%"
CALL :Append "INFO: CFG_PY=%CFG_PY%"
CALL :Append "INFO: XKIT=%XKIT%"
SET LCC_HOME=%WAS_PROFILE_HOME%\config\cells\%CELL_NAME%\LotusConnections-config
SET LCC_HOME=%LCC_HOME:"=%
CALL :Append "INFO: LCC_HOME=%LCC_HOME%"

@REM Shift must be behind here
shift
shift
SET IC_VERSION_FROM=%~8
SET IC_VERSION_TO=%~9
CALL :Append "INFO: IC_VERSION_FROM=%IC_VERSION_FROM%"
CALL :Append "INFO: IC_VERSION_TO=%IC_VERSION_TO%"

SET WSADMIN_CLASSPATH=%MY_HOME%lib\lccfg.jar;%MY_HOME%lib\jose4j-0.6.3.jar;%MY_HOME%lib\json4j.jar

IF "%CONN_OPTION%"=="" (SET CONN_OPTION=-conntype SOAP)
CALL :Append "INFO: CONN_OPTION=%CONN_OPTION%"


@REM If #7 argument ${environment:skipjython} is 1, then skip real installation
IF "%SKIPJYTHON_FLAG%" == "1" (
CALL :Append "WARN: Skip the real installation!"
GOTO :COMPLETE
)

@REM update sso domain name begin
SET Connections_HOME=%MY_HOME%
ECHO Setting Connections_HOME=%MY_HOME% >> post-install-launcher.log
ECHO "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password PASSWORD_REMOVED -javaoption "-Duser.language=en" -javaoption "-Duser.country=US" -f %MY_HOME%bin\fetch_sso.py >> post-install-launcher.log
CALL "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password %WAS_USER_PW% -javaoption "-Duser.language=en" -javaoption "-Duser.country=US" -f "%MY_HOME%bin\fetch_sso.py" >> post-install-launcher.log
@REM sso domain name end

:determineInstallMode
SET TARGET=INSTALL_OR_MODIFY
@REM If it's update install, but there is failure during update, IM will auto launch
@REM previous installer to do another "UPDATE" job, so USERJOB is still "UPDATE", but 
@REM actually it's a kind of rollback, this scripts will restore LCC folder and the correct
@REM cfg.py to ensure the "rollback" is OK, thus the system will be still in last good 
@REM state.
IF "%USERJOB%"=="UPDATE" ( 
  IF NOT EXIST %MY_HOME%update_failed (
    GOTO :updateStart
  ) ELSE (
    GOTO :updateFailedAutoRollbackStart
  )
)

IF "%USERJOB%"=="ROLLBACK" GOTO :normalRollbackStart

IF "%TARGET%"=="INSTALL_OR_MODIFY" GOTO :installStart

:updateStart
CALL :Append "INFO: Starting UPDATE deployment from %IC_VERSION_FROM% to %IC_VERSION_TO%..."

:backupLCCFolder
SET LCC_BACKUPFOLDER=%MY_HOME%bak\%IC_VERSION_FROM%\LCC
CALL :Append "INFO: LCC_BACKUPFOLDER=%LCC_BACKUPFOLDER%"
SET "FUNCEXITCODE="
CALL :CopyFolder "Backup LCC" "%LCC_HOME%" "%LCC_BACKUPFOLDER%" FUNCEXITCODE

@REM If LCC backup fails, abandon update install
IF "%FUNCEXITCODE%"=="1" (
    EXIT /B 1
)

@REM This is to make existing technote still valid
:backupFLT
IF EXIST %LCC_HOME%\profiles (
  SET "FUNCEXITCODE="
  CALL :CopyFolder "Backup profiles" "%LCC_HOME%\profiles" "%MY_HOME%FLT_backup\profiles" FUNCEXITCODE
  IF "%FUNCEXITCODE%"=="1" (
    EXIT /B 1
  )
)

IF EXIST %LCC_HOME%\extern (
  SET "FUNCEXITCODE="
  CALL :CopyFolder "Backup extern" "%LCC_HOME%\extern" "%MY_HOME%FLT_backup\extern" FUNCEXITCODE
  IF "%FUNCEXITCODE%"=="1" (
    EXIT /B 1
  )
)

IF EXIST %LCC_HOME%\notifications (
  SET "FUNCEXITCODE="
  CALL :CopyFolder "Backup notifications" "%LCC_HOME%\notifications" "%MY_HOME%FLT_backup\notifications" FUNCEXITCODE
  IF "%FUNCEXITCODE%"=="1" (
    EXIT /B 1
  )
)

IF EXIST %LCC_HOME%\notifications_v2 (
  SET "FUNCEXITCODE="
  CALL :CopyFolder "Backup notifications_v2" "%LCC_HOME%\notifications_v2" "%MY_HOME%FLT_backup\notifications_v2" FUNCEXITCODE
  IF "%FUNCEXITCODE%"=="1" (
    EXIT /B 1
  )
)

:startUpdateInstall_Phase1
SET phaseName=update phase1
CALL :Append "INFO: Starting %phaseName%..."
CALL :Append "CMD: %WAS_PROFILE_HOME%\bin\wsadmin.bat -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password PASSWORD_REMOVED -javaoption -Dpython.path=%MY_HOME%lib -wsadmin_classpath %WSADMIN_CLASSPATH% -javaoption -Xmx512m -javaoption -Duser.language=en -javaoption -Duser.country=US -f %SCRIPTS_DIR%install.py -update %CFG_PY% %XKIT%" 

CALL "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password %WAS_USER_PW% ^
-javaoption "-Dpython.path=%MY_HOME%lib" ^
-wsadmin_classpath "%WSADMIN_CLASSPATH%" ^
-javaoption "-Xmx512m" ^
-javaoption "-Duser.language=en" ^
-javaoption "-Duser.country=US" ^
-f "%SCRIPTS_DIR%install.py" -update "%CFG_PY%" "%XKIT%" >> "%MY_HOME%install.log" 2>&1

SET updateInstallPhase1_ExitCode=%ERRORLEVEL%
IF "%updateInstallPhase1_ExitCode%"=="0" (
  CALL :Append "INFO: Connections %phaseName% deployment finished SUCCESSFULLY!"
) ELSE (
  CALL :Append "ERROR: Connections %phaseName% deployment failed with code %updateInstallPhase1_ExitCode%! Please check log for details."
  ECHO %INSTALL_DATE% %INSTALL_TIME% %phaseName% failed > update_failed
  EXIT /B %updateInstallPhase1_ExitCode%
)

:startUpdateInstall_Phase2
SET phaseName=update phase2
GOTO :sharedInstall

:installStart
IF "%USERJOB%"=="INSTALL" SET phaseName=fresh install
IF "%USERJOB%"=="MODIFY_ADD" SET phaseName=modify_add install
IF "%USERJOB%"=="MODIFY_REMOVE" SET phaseName=modify_remove install


:sharedInstall
CALL :Append "INFO: Starting %phaseName%..."
CALL :Append "CMD: %WAS_PROFILE_HOME%\bin\wsadmin.bat -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password PASSWORD_REMOVED -javaoption -Dpython.path=%MY_HOME%lib -wsadmin_classpath %WSADMIN_CLASSPATH% -javaoption -Xmx512m -javaoption -Duser.language=en -javaoption -Duser.country=US -f %SCRIPTS_DIR%install.py %CFG_PY% %XKIT% "

CALL "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password %WAS_USER_PW% ^
-javaoption "-Dpython.path=%MY_HOME%lib" ^
-wsadmin_classpath "%WSADMIN_CLASSPATH%" ^
-javaoption "-Xmx512m" ^
-javaoption "-Duser.language=en" ^
-javaoption "-Duser.country=US" ^
-f "%SCRIPTS_DIR%install.py" "%CFG_PY%" "%XKIT%" >> "%MY_HOME%install.log" 2>&1

SET install_ExitCode=%ERRORLEVEL%
IF "%install_ExitCode%"=="0" (
  CALL :Append "INFO: Connections %phaseName% deployemnt finished SUCCESSFULLY!"
  IF "%phaseName%"=="update phase2" (
    IF EXIST %MY_HOME%update_failed (
      DEL /Q %MY_HOME%update_failed
    )
  ) ELSE (
    IF EXIST %MY_HOME%install_failed (
      DEL /Q %MY_HOME%install_failed
    )
  )
  GOTO :backupCFGPY
) ELSE (
  CALL :Append "ERROR: Connections %USERJOB% deployemnt failed with code %install_ExitCode% ! Please check log for details."
  IF "%phaseName%"=="update phase2" (
    ECHO %INSTALL_DATE% %INSTALL_TIME% %phaseName% failed > update_failed
  ) ELSE (
    ECHO %INSTALL_DATE% %INSTALL_TIME% %phaseName% failed > install_failed
  )
  EXIT /B %install_ExitCode%
)


:updateFailedAutoRollbackStart
CALL :Append "WARN: Starting update failed auto rollback..."
@REM When update install failed, IM will launch last successful UPDATE, the backup cfg.py is the one to use.
IF EXIST %MY_HOME%bak\%IC_VERSION_FROM%\CFGBAK\cfg.py (
	XCOPY /C/Y %MY_HOME%bak\%IC_VERSION_FROM%\CFGBAK\cfg.py %MY_HOME%
	Call :Append "INFO: Restored cfg.py from %MY_HOME%bak\%IC_VERSION_FROM%\CFGBAK\cfg.py."
) ELSE (
	CALL :Append "WARN: No previous cfg.py, no way to do update fail auto rollback!"
	EXIT /B 3
)

IF EXIST %MY_HOME%bak\%IC_VERSION_FROM%\LCC (
	SET "FUNCEXITCODE="
	CALL :CopyFolder "Restore LCC" "%MY_HOME%bak\%IC_VERSION_FROM%\LCC" "%LCC_HOME%" FUNCEXITCODE
	IF "%FUNCEXITCODE%"=="1" (
    	EXIT /B 1
	)
) ELSE (
	CALL :Append "WARN: No previous LCC backup, no way to do update fail auto rollback!"
	EXIT /B 3
)

:setUpdateFailStatus
SET "UPDATE_FAILED_FLAG="
IF EXIST %MY_HOME%update_failed (
  SET UPDATE_FAILED_FLAG=update failed auto
  GOTO :rollback_Phase1
)

:normalRollbackStart
CALL :Append "INFO: Starting normal rollback..."
IF EXIST %MY_HOME%bak\%IC_VERSION_TO%\CFGBAK\cfg.py (
  XCOPY /C/Y %MY_HOME%bak\%IC_VERSION_TO%\CFGBAK\cfg.py %MY_HOME%
  CALL :Append "INFO: Restored cfg.py from %MY_HOME%bak\%IC_VERSION_TO%\CFGBAK\cfg.py"
  GOTO :restore_LCCBAK
) ELSE (
  CALL :Append "WARN: No previous cfg.py, normal rollback is impossible, please do manual rollback."
  EXIT /B 2
)

:restore_LCCBAK
SET "FUNCEXITCODE="
CALL :CopyFolder "Restore LCC" "%MY_HOME%bak\%IC_VERSION_TO%\LCC" "%LCC_HOME%" FUNCEXITCODE
@REM If LCC restore fails, abandon install
IF "%FUNCEXITCODE%"=="1" (
    EXIT /B 1
)
CALL :Append "INFO: Restored LCC from %MY_HOME%bak\%IC_VERSION_TO%\LCC"

:rollback_Phase1
SET phaseName=%UPDATE_FAILED_FLAG% rollback phase1
CALL :Append "INFO: Starting %phaseName%..."
CALL :Append "CMD: %WAS_PROFILE_HOME%\bin\wsadmin.bat -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password PASSWORD_REMOVED -javaoption -Dpython.path=%MY_HOME%lib -wsadmin_classpath %WSADMIN_CLASSPATH% -javaoption -Xmx512m -javaoption -Duser.language=en -javaoption -Duser.country=US -f %SCRIPTS_DIR%install.py -update %CFG_PY% %XKIT% " 

CALL "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password %WAS_USER_PW% ^
-javaoption "-Dpython.path=%MY_HOME%lib" ^
-wsadmin_classpath "%WSADMIN_CLASSPATH%" ^
-javaoption "-Xmx512m" ^
-javaoption "-Duser.language=en" ^
-javaoption "-Duser.country=US" ^
-f "%SCRIPTS_DIR%install.py" -update "%CFG_PY%" "%XKIT%" >> "%MY_HOME%install.log" 2>&1

SET rollbackInstallPhase1_ExitCode=%ERRORLEVEL%
IF "%rollbackInstallPhase1_ExitCode%"=="0" (
  CALL :Append "INFO: Connections %phaseName% deployemnt finished SUCCESSFULLY!"
  GOTO :rollback_Phase2
) ELSE (
  CALL :Append "ERROR: Connections %phaseName% deployemnt failed with code %rollbackInstallPhase1_ExitCode%! Please check log for details."
  IF "%UPDATE_FAILED_FLAG%"=="update failed auto" (
    ECHO %phaseName% failed > updateautorollback_failed
  ) ELSE (
    ECHO %phaseName% failed > rollback_failed
  )
  EXIT /B %rollbackInstallPhase1_ExitCode%
)

:rollback_Phase2
SET phaseName=%UPDATE_FAILED_FLAG% rollback phase2
CALL :Append "INFO:Starting %phaseName%..."
CALL :Append "CMD: %WAS_PROFILE_HOME%\bin\wsadmin.bat -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password PASSWORD_REMOVED -javaoption -Dpython.path=%MY_HOME%lib -wsadmin_classpath %WSADMIN_CLASSPATH% -javaoption -Xmx512m -javaoption -Duser.language=en -javaoption -Duser.country=US -f %SCRIPTS_DIR%install.py %CFG_PY% %XKIT% " 

CALL "%WAS_PROFILE_HOME%\bin\wsadmin.bat" -lang jython %CONN_OPTION% -host localhost -port %WAS_PORT% -user %WAS_USER% -password %WAS_USER_PW% ^
-javaoption "-Dpython.path=%MY_HOME%lib" ^
-wsadmin_classpath "%WSADMIN_CLASSPATH%" ^
-javaoption "-Xmx512m" ^
-javaoption "-Duser.language=en" ^
-javaoption "-Duser.country=US" ^
-f "%SCRIPTS_DIR%install.py" "%CFG_PY%" "%XKIT%" >> "%MY_HOME%install.log" 2>&1

SET rollbackInstallPhase2_ExitCode=%ERRORLEVEL%
IF "%rollbackInstallPhase2_ExitCode%"=="0" (
  IF "%USERJOB%"=="ROLLBACK" (
  	IF EXIST %MY_HOME%bak\%IC_VERSION_FROM% (
  		RMDIR /S/Q %MY_HOME%bak\%IC_VERSION_FROM%
  		CALL :Append "INFO: Removed folder %MY_HOME%bak\%IC_VERSION_FROM%"
  	)
  ) ELSE (
  	RMDIR /S/Q %MY_HOME%bak\%IC_VERSION_FROM%\LCC
  )
  
  IF EXIST %MY_HOME%rollback_failed (
    DEL /Q %MY_HOME%rollback_failed
  )
  IF EXIST %MY_HOME%updateautorollback_failed (
    DEL /Q %MY_HOME%updateautorollback_failed
  )
  IF EXIST %MY_HOME%update_failed (
  	DEL /Q %MY_HOME%update_failed
  )
  IF "%USERJOB%"=="ROLLBACK" (
  	CALL :Append "INFO: Connections %USERJOB% deployment finished SUCCESSFULLY!"
  ) ELSE (
	CALL :Append "WARN: Connections restored from failed UPDATE deployment, please check install log for the failure reason, try to correct it and retry, or contact HCL Support for help."
  )
  EXIT /B 0
) ELSE (
  CALL :Append "ERROR: Connections %phaseName% deployemnt failed with code %rollbackInstallPhase2_ExitCode%! Please check log for details."
  IF "%UPDATE_FAILED_FLAG%"=="update failed auto" (
    ECHO %phaseName% failed > updateautorollback_failed
  ) ELSE (
    ECHO %phaseName% failed > rollback_failed
  )
  EXIT /B %rollbackInstallPhase2_ExitCode%
)

:backupCFGPY
IF NOT EXIST %MY_HOME%bak\%IC_VERSION_TO%\CFGBAK (
  MKDIR %MY_HOME%bak\%IC_VERSION_TO%\CFGBAK
)
XCOPY /C/Y %MY_HOME%cfg.py %MY_HOME%bak\%IC_VERSION_TO%\CFGBAK
CALL :Append "INFO: Backup cfg.py after %phaseName% is successful."
GOTO :COMPLETE

:COMPLETE
CALL :Append "INFO: Connections %USERJOB% deployment finished SUCCESSFULLY!"
:END
EXIT /B 0

:CopyFolder 
REM Call :CopyFolder taskname src target ret   
REM -- Copy the content of src folder to target folder
REM     -- taskname: the task description
REM     -- src: source folder without tailing path sep
REM     -- target: target folder without tailer path sep
REM     -- ret: success=0, otherwise=1
SETLOCAL
SET taskname=%~1
SET srcfolder=%~2
SET targetfolder=%~3
SET srcfolder=%srcfolder:/=\%
SET targetfolder=%targetfolder:/=\%
CALL :Append "INFO: Doing %taskname% from %srcfolder% to %targetfolder%"
IF NOT EXIST %targetfolder% (
  MKDIR %targetfolder% >> post-install-launcher.log 2>&1 && (CALL :Append "INFO: %taskname% -- Created folder %targetfolder%.") || (CALL :Append "ERROR: %taskname% -- Failed to create folder %targetfolder%." & set rtncode=1)
) 
XCOPY /E/Y/Q %srcfolder% %targetfolder% >> post-install-launcher.log 2>&1 && (CALL :Append "INFO: COPY Success!" & SET rtncode=0) || (CALL :Append "ERROR: COPY FAIL! Check your OS settings, such as permission, disk space, etc." & SET rtncode=1)
ENDLOCAL & IF "%~4" NEQ "" (SET %~4=%rtncode%) ELSE (echo.%rtncode%)
EXIT /B

:Append
SETLOCAL
SET MSG=%~1
ECHO %MSG% >> post-install-launcher.log
ENDLOCAL
EXIT /B

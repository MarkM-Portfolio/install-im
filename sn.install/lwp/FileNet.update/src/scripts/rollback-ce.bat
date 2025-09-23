@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                              
@REM                                                                   
@REM Copyright IBM Corp. 2010, 2016                                    
@REM                                                                   
@REM The source code for this program is not published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

@REM 5724-S68                                                          
@echo off
setlocal ENABLEEXTENSIONS
setlocal ENABLEDELAYEDEXPANSION

set array=(was.dm.path was.admin.user was.admin.password conn.home.location)

REM Cannot recover batch parameters once processed with SHIFT
REM so need to store script path and name for use later if needed
set fnUpdateScriptPath_=%~dp0
set fnUpdateScriptName_=%~nx0

REM Set fn.fncs.ceclient.update.log to (default filename) fn-fncs-ceclient-update.log in the same directory of this script if not specified
if not defined fn.ce.rollback.log (
	set fn.ce.rollback.log=!fnUpdateScriptPath_!fn-ce-rollback.log
)
REM Check if we can write to log file
copy /Y nul "!fn.ce.rollback.log!" > nul 2>&1
if not %errorlevel% == 0 (
	echo Error: Unable to write to log: !fn.ce.rollback.log!
	exit /b 1
)

REM Parse command line params
:parseParamStart
if (%1) == () goto :parseParamEnd
set param=%~1
if "-" == "!param:~0,1!" (
	set paramD=true
	set paramName=!param:~1!
	set existed=false
	if "!paramName!" == "h" (
		call :usage
		exit /b 0
	)
	for %%i in %array% do (	
		if !paramName! == %%i (
			set existed=true
			break
		)
	)
	if "!existed!" == "false" (
		call :usage !paramName!
		exit /b 1
	)
) else if "%paramD%"=="true" (
	call set "!paramName!=!param!"
	set paramD=false
) else set paramD=false

shift
goto :parseParamStart
:parseParamEnd

call :validate
if not %errorlevel% == 0 (
	exit /b 1
)

call :log FileNet CPE Fixpack has been rollbacked successfully.

goto :EOF

REM ====================
REM Function definitions
REM ====================

:usage
	if not "%*" == "" (
		call :log
		call :log Error: "%*" is an unrecognized parameter.
	)
	call :log Usage: %fnUpdateScriptName_% [-param value] ... [-paramN valueN]
	call :log
	call :log Available parameters to set:
	call :log (If there is space in the value of a parameter, please enclose it with a pair of quotation marks, e.g.: "C:\Program Files\IBM\WebSphere\AppServer\profiles\Dmgr01").
	call :log was.dm.path		: Path of WAS Deployment Manager (e.g.: "C:\Program Files\IBM\WebSphere\AppServer\profiles\Dmgr01")
	call :log was.admin.user		: Username of WAS administrator (e.g.: wasadmin)
	call :log was.admin.password	: Password of WAS administrator
	call :log conn.home.location    : Location of Connections Home folder
goto :EOF

:log
	echo.%*
	echo.%* >> "%fn.ce.rollback.log%" 2>&1
goto :EOF

:mutedLog
	echo.%* >> "%fn.ce.rollback.log%" 2>&1
goto :EOF

:doMutedAction
	set temp=%*
	for /F "tokens=1* delims=]" %%A in ('call !temp! 2^>^&1 ^| find /N /V ""') do (
		echo.%%B
		echo.%%B >> "%fn.ce.rollback.log%" 2>&1
	)
GOTO :EOF

:validate
	setlocal
	call :log Performing validation check ...
	call :log
	
	set firstTime=true
	
	:wasDMPathAcquiry
	if not "!was.dm.path!" == "" (
		if exist !was.dm.path!\bin\wsadmin.bat (
			set firstTime=true
			goto :javaHomeCheck
		)
		if not "!firstTime!" == "true" (
			call :log Error: "!was.dm.path!" is not the path of WAS Deployment Manager or there is no bin\wsadmin.bat under the profile folder.
			call :log 
		) else (
			set firstTime=false
		)
	) else (
		if not "!firstTime!" == "true" (
			call :log Error: The path of WAS Deployment Manager is empty.
			call :log 
		) else (
			set firstTime=false
		)
	)
	
	call :log Input the path of WAS Deployment Manager (e.g.: "C:\Program Files\IBM\WebSphere\AppServer\profiles\Dmgr01") [%was.dm.path%]:
	set /P was.dm.path=%~1
	set   was.dm.path=%was.dm.path:"=%
	call :mutedLog %was.dm.path%
	call :log
	goto :wasDMPathAcquiry
	
	:javaHomeCheck
	if "!JAVA_HOME!" == "" (
		set JAVA_HOME=!was.dm.path!\..\..\java
	)
	
	:ADDJAVAHOME
	if not exist "!JAVA_HOME!\bin\java.exe" (
		call :log Error: JAVA_HOME is not a valid path for JDK root folder.
		call :log Please provide JAVA_HOME (e.g.: C:\jre6.0^):
		set /p JAVA_HOME=
		goto :ADDJAVAHOME
	)
	set java="!JAVA_HOME!\bin\java.exe"
	
	
	:wasUserAcquiry
	if not "%was.admin.user%" == "" (
		set firstTime=true
		goto :wasPassAcquiry
	)
	
	if not "!firstTime!" == "true" (
		call :log Error: The username of WAS administrator is empty.
		call :log 
	) else (
		set firstTime=false
	)
	call :log Input the username of WAS administrator (e.g.: wasadmin) [%was.admin.user%]:
	set /P was.admin.user=
	call :mutedLog %was.admin.user%
	call :log
	goto :wasUserAcquiry
	
	:wasPassAcquiry
	if not "%was.admin.password%" == "" (
		set firstTime=true
		goto :wasUserPassValidation
	)
	
	if not "!firstTime!" == "true" (
		call :log Error: The password of WAS administrator is empty.
		call :log 
	) else (
		set firstTime=false
	)
	call :log Input the password of WAS administrator:	
	set /P was.admin.password=
	call :mutedLog "***"
	call :log
	goto :wasPassAcquiry
	
	:wasUserPassValidation
	REM get the SOAP port of Deployment Manager
	pushd "%fnUpdateScriptPath_%..\lib"
	set cp=".;%cd%\*"
	popd
	
	set msgs=
	call :log !java! -classpath !cp! com.ibm.connections.install.FilenetUpdateUtil "!was.dm.path!"
	for /F "tokens=*" %%j in ('"!java! -classpath !cp! com.ibm.connections.install.FilenetUpdateUtil "!was.dm.path!" 2>&1"') do (
		call :mutedLog %%j
		set "msgs=!msgs! %%j"
	)
	for /F "usebackq tokens=*" %%i in (`"echo !msgs! | findstr "ClassNotFoundException""`) do (
		if not "%%i" == "" (
			call :log 
			call :log Solution: Please copy the "lib" folder in ccm-install.jar to "!fnUpdateScriptPath_!..".
			call :log 
		)
	)
	for /F "usebackq tokens=*" %%i in (`"echo !msgs! | findstr "Error Usage""`) do (
		if not "%%i" == "" (
			call :log Error: SOAP port of Deployment Manager is unavailable. The reason and solution are recorded above.
			call :log
			exit /b 1
		)
	)
	
	set dm.soap.port=%msgs%
	
	REM validate the DM connection using soap
	set _="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set _=%_% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set _=%_% -c "sys.exit()"
	set __="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set __=%__% -host localhost -port %dm.soap.port% -username %was.admin.user% -password "***"
	set __=%__% -c "sys.exit()"
	call :log %__%
	for /F "usebackq tokens=*" %%i in (`"!_! | findstr "Exception WASX7008E""`) do (
		if not "%%i" == "" (
			REM if failed to pass validation
			call :log Error: Deployment Manager is inaccessible, please ensure:
			call :log 1. The username and password you provided is correct.
			call :log 2. The Deployment Manager is running.
			call :log 
			exit /b 1
		)
	)
	
	call :log The validation of Deployment Manager is passed.

        set firstTime=true
	:connHomePathAcquiry
	if not "%conn.home.location%" == "" (
		if exist !conn.home.location! (
			goto :validateSuccess
		)
		if not "!firstTime!" == "true" (
			call :log Error: "!conn.home.location!" is not a valid location of Connections home.
			call :log
		) else (
			set firstTime=false
		)
	) else (
		if not "!firstTime!" == "true" (
			call :log Error: The location of Connections Home is empty.
			call :log
		) else (
			set firstTime=false
		)
	)

	:validateconnhome
	call :log Input the location of Connections installation path (e.g.: C:\Program Files\IBM\Connections) [%conn.home.location%]:
	set /P conn.home.location=%~1
	set   conn.home.location=%conn.home.location:"=%
	call :mutedLog %conn.home.location%
	call :log
	goto :connHomePathAcquiry
	
	:validateSuccess
	call :log Validation passed.

        call :log
        call :log rolling back FileNet Engine
        call :log 

        call :log copyinging back Engine-ws.ear
        copy "!conn.home.location!\FileNet_backup\Engine-ws.ear" "!conn.home.location!\FileNet\ContentEngine\tools\configure\profiles\CCM\ear" 

        call :log copyinging back Jace Jar
        copy "!conn.home.location!\FileNet_backup\lib\Jace*.jar" "!conn.home.location!\FileNet\ContentEngine\lib" 

	REM rollback FNCE through the DM connection using soap
        call :log
        call :log starting to re-deploy FileNet Engine ear...
	set fn_deploy="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set fn_deploy=%fn_deploy% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set fn_deploy=%fn_deploy% -wsadmin_classpath "%conn.home.location%\lib\lccfg.jar"
        set fn_deploy=%fn_deploy% -javaoption "-Dpython.path=C:\Program Files\IBM\Connections\lib"
        set fn_deploy=%fn_deploy% -f "%conn.home.location%\FileNet.update\scripts\deploy_fn_apps.py" "%conn.home.location%\cfg.py" "rollbackce"

        set MY_HOME=%conn.home.location%
        set WAS_HOME=%was.dm.path%
        set set_fn_anonymous=n

      REM  call !fn_deploy!
		call %fn_deploy% >> "%fn.ce.rollback.log%" 2>&1
        call :log return code: %errorlevel%
	if not %errorlevel% == 0 (
              call :log
              echo Error: IBM FileNet Engine deploy failed, exit.
	      exit /b 1
	) else (
              call :log
	      call :log IBM FileNet Content Engine have been deployed successfully.
	)


	set fn_config="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set fn_config=%fn_config% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set fn_config=%fn_config% -wsadmin_classpath "%conn.home.location%\lib\lccfg.jar"
        set fn_config=%fn_config% -javaoption "-Dpython.path=C:\Program Files\IBM\Connections\lib"
        set fn_config=%fn_config% -f "%conn.home.location%\FileNet.update\scripts\deploy_fn_apps.py" "%conn.home.location%\cfg.py" "config_ce"

       REM call !fn_config!
		call %fn_config%  >> "%fn.ce.rollback.log%" 2>&1
        call :log return code: %errorlevel%
	if not %errorlevel% == 0 (
              call :log
              echo Error: IBM FileNet Content Engine config failed, exit.
	      exit /b 1
	) else (
              call :log
	      call :log IBM FileNet Content Engine have been configured successfully.
	)

        call :log
        call :log IBM FileNet Content Engine have been upgraded successfully.
	
	endlocal & set was.dm.path=%was.dm.path%& set was.admin.user=%was.admin.user%& set was.admin.password=%was.admin.password%& set conn.home.location=%conn.home.location%
goto :EOF
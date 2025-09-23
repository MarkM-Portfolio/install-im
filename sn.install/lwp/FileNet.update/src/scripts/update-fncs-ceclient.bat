@REM *****************************************************************
@REM
@REM IBM Licensed Material
@REM
@REM Copyright IBM Corp. 2010, 2017
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

set array=(was.dm.path was.admin.user was.admin.password conn.home.location doSetAnonymous anonymous_user anonymous_password fncs.fp.installer.location ceclient.fp.installer.location)

REM Cannot recover batch parameters once processed with SHIFT
REM so need to store script path and name for use later if needed
set fnUpdateScriptPath_=%~dp0
set fnUpdateScriptName_=%~nx0

REM Set fn.fncs.ceclient.update.log to (default filename) fn-fncs-ceclient-update.log in the same directory of this script if not specified
if not defined fn.fncs.ceclient.update.log (
	set fn.fncs.ceclient.update.log=!fnUpdateScriptPath_!fn-fncs-ceclient-update.log
)
REM Check if we can write to log file
copy /Y nul "!fn.fncs.ceclient.update.log!" > nul 2>&1
if not %errorlevel% == 0 (
	echo Error: Unable to write to log: !fn.fncs.ceclient.update.log!
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



::call :log %__%
::call !_! >> "%fn.fncs.ceclient.update.log%" 2>&1
::if not %errorlevel% == 0 (
::	call :log Error: Please read the log for detail of the problem, it is !fn.fncs.ceclient.update.log!.
::	call :log
::	exit /b 1
::)

call :log FileNet FNCS and CE Client have been upgraded successfully.

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
        call :log conn.home.location    : Path of Connections Home (e.g.: "C:\Program Files\IBM\Connections")
        call :log doSetAnonymous        : set anonymous user or not (e.g.: "y" or "n")
        call :log anonymous_user       : anonymous user name if doSetAnonymous is y (e.g.: "Amy Jones")
        call :log anonymous_password   : anonymous user password if doSetAnonymous is y
	call :log fncs.fp.installer.location: Location of IBM CONTENT NAVIGATOR (which now includes FNCS) Fixpack installer (e.g.: C:\filenetFixpack\IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-WIN.exe)
	call :log ceclient.fp.installer.location: Location of FileNet CE Client installer (e.g.: C:\filenetFixpack\5.2.1.7-P8CPE-CLIENT-WIN-FP007.EXE)
goto :EOF

:log
	echo.%*
	echo.%* >> "%fn.fncs.ceclient.update.log%" 2>&1
goto :EOF

:mutedLog
	echo.%* >> "%fn.fncs.ceclient.update.log%" 2>&1
goto :EOF

:doMutedAction
	set temp=%*
	for /F "tokens=1* delims=]" %%A in ('call !temp! 2^>^&1 ^| find /N /V ""') do (
		echo.%%B
		echo.%%B >> "%fn.fncs.ceclient.update.log%" 2>&1
	)
GOTO :EOF

:validate
	setlocal
        call :log
	call :log Performing validation check ...
	call :log

	set firstTime=true

	:wasDMPathAcquiry
	if not "!was.dm.path!" == "" (
		if exist !was.dm.path!\bin\wsadmin.bat (
			set firstTime=true
			goto :diskSpaceCheck
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

	call :log Enter the WAS Deployment Manager path (e.g.: C:\Program Files\IBM\WebSphere\AppServer\profiles\Dmgr01) [%was.dm.path%]:
	set /P was.dm.path=%~1
	set   was.dm.path=%was.dm.path:"=%
	call :mutedLog %was.dm.path%
	call :log
	goto :wasDMPathAcquiry

	:diskSpaceCheck
	if "!IATEMPDIR!" == "" (
		for /F "usebackq tokens=1 delims=\" %%s in (`echo !TEMP!`) do (
			set volume=%%s
		)
	) else (
		for /F "usebackq tokens=1 delims=\" %%s in (`echo !IATEMPDIR!`) do (
			set volume=%%s
		)
	)

	set volume=%volume:"=%
	for /F "usebackq tokens=3" %%a in (`DIR %volume%\ /-C /-O /W`) do (
		set FREE_SPACE=%%a
	)
	set /a FREE_SPACE=%FREE_SPACE:~0,-6%
	if !FREE_SPACE! LEQ 752 (
		REM if the disk space is less than 700MB
		call :log Error: On disk "%volume%", there must be more than 700MB of free space. Please clean up after the program exit.
		call :log
		exit /b 1
	)

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
	call :log Enter the WAS administrator username (e.g.: wasadmin) [%was.admin.user%]:
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
	call :log Enter the WAS administrator password:
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
	REM call :log !java! -classpath !cp! com.ibm.connections.install.FilenetUpdateUtil "!was.dm.path!"
	for /F "tokens=*" %%j in ('"!java! -classpath !cp! com.ibm.connections.install.FilenetUpdateUtil "!was.dm.path!" 2>&1"') do (
		call :mutedLog %%j
		set "msgs=!msgs! %%j"
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
	call :doMutedAction %_%
	for /F "usebackq tokens=*" %%i in (`"!_! | findstr "Exception WASX7008E""`) do (
		if not "%%i" == "" (
			REM if failed to pass validation
			call :log Error: Deployment Manager is inaccessible, please ensure the username and password you provided is correct.
			call :log
			exit /b 1
		)
	)

	call :log The validation of Deployment Manager succeeded.
        call :log


        set firstTime=true
	:connHomePathAcquiry
	if not "%conn.home.location%" == "" (
		if exist !conn.home.location! (
			if exist !conn.home.location!\tmp\FNCS-2.0.0.0-windows_SilentInstall.properties (
				if exist !conn.home.location!\tmp\ceclient_silent_install_windows.txt (
                                        set firstTime=true
					goto :askSetAnonymous
				) else (
					call :log Error: There is no ceclient_silent_install_windows.txt in "!conn.home.location!"\tmp
				)
			) else (
				call :log Error: There is no FNCS-2.0.0.0-windows_SilentInstall.properties in "!conn.home.location!"\tmp
			)
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



	:askSetAnonymous
	if "%doSetAnonymous%" == "" (
		call :log Specify the anonymous username and password for FileNet deployment - [y]es / [n]o:
		set /P doSetAnonymous=
		call :mutedLog %doSetAnonymous%
		set doSetAnonymous=!doSetAnonymous:Y=y!
		set doSetAnonymous=!doSetAnonymous:N=n!
		call :log
		if "!doSetAnonymous!" == "y" (
                     set firstTime=true
                     set set_fn_anonymous=y
                     goto :askAnonymousUser
                )
		if "!doSetAnonymous!" == "n" (
                     set set_fn_anonymous=n
                     goto :fncsFixpackPathAcquiry
                )
	)

	:obtainIfSetAnonymousCommandLine
	if "%doSetAnonymous%" == "y" (
             set set_fn_anonymous=y
             goto :askAnonymousUser
        )
	if "%doSetAnonymous%" == "Y" (
             set set_fn_anonymous=y
             goto :askAnonymousUser
        )
	if "%doSetAnonymous%" == "n" (
             set set_fn_anonymous=n
             goto :fncsFixpackPathAcquiry
        )
	if "%doSetAnonymous%" == "N" (
             set set_fn_anonymous=n
             goto :fncsFixpackPathAcquiry
        )


        :askAnonymousUser
	if not "%anonymous_user%" == "" (
		set firstTime=true
                set fn_anonymous=%anonymous_user%
		goto :askAnonymousPassword
	)

	if not "!firstTime!" == "true" (
		call :log Error: The name of FileNet Anonymous user is empty.
		call :log
	) else (
		set firstTime=false
	)
	call :log Enter the FileNet Anonymous user name (e.g.: wasadmin) [%anonymous_user%]:
	set /P anonymous_user=
	call :mutedLog %anonymous_user%
	call :log
	goto :askAnonymousUser

	:askAnonymousPassword
	if not "%anonymous_password%" == "" (
		set firstTime=true
                set fn_anonymous_password=%anonymous_password
		goto :fncsFixpackPathAcquiry
	)

	if not "!firstTime!" == "true" (
		call :log Error: The password of FileNet Anonymous user is empty.
		call :log
	) else (
		set firstTime=false
	)
	call :log Enter the FileNet Anonymous user password:
	set /P anonymous_password=
	call :mutedLog "***"
	call :log
	goto :askAnonymousPassword


        set firstTime=true
	:fncsFixpackPathAcquiry
	if not "%fncs.fp.installer.location%" == "" (
		REM echo !fncs.fp.installer.location! | findstr /C:"Windows" 1>nul
		if exist !fncs.fp.installer.location! (
			set firstTime=true
		
		REM  To remove this condition, in order to avoid this parameter verification file name leading to an error, 
		REM  and to support the installation of ICN 3.0.x
		REM  Please refer to LC RTC 193213.
		REM	if not "!fncs.fp.installer.location:FP=!" == "!fncs.fp.installer.location!" (
				if not "!fncs.fp.installer.location:IBM_CONTENT_NAVIGATOR=!" == "!fncs.fp.installer.location!" (
					if not "!fncs.fp.installer.location:exe=!" == "!fncs.fp.installer.location!" (
					    goto :ceclientFixpackPathAcquiry
					) else (
					    call :log Error: "!fncs.fp.installer.location!" is not the location of IBM CONTENT NAVIGATOR Fixpack installer or the binary file is wrong.
					)
				) else (
				    call :log Error: "!fncs.fp.installer.location!" is not the location of IBM CONTENT NAVIGATOR Fixpack installer or the binary file is wrong.
				)
				
		REM  To remove this condition, in order to avoid this parameter verification file name leading to an error, 
		REM  and to support the installation of ICN 3.0.x
		REM  Please refer to LC RTC 193213.
		REM	) else (
		REM	    call :log Error: "!fncs.fp.installer.location!" is not the location of IBM CONTENT NAVIGATOR Fixpack installer or the binary is wrong.
		REM	)
		)
		if not "!firstTime!" == "true" (
			call :log Error: "!fncs.fp.installer.location!" is not the location of IBM CONTENT NAVIGATOR Fixpack installer.
			call :log
		) else (
			set firstTime=false
		)
	) else (
		if not "!firstTime!" == "true" (
			call :log Error: The location of IBM CONTENT NAVIGATOR Fixpack installer is empty.
			call :log
		) else (
			set firstTime=false
		)
	)

	call :log Enter the location of IBM CONTENT NAVIGATOR Fixpack installer (e.g.: C:\filenetFixpack\IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-WIN.exe) [%fncs.fp.installer.location%]:
	set /P fncs.fp.installer.location=%~1
	set   fncs.fp.installer.location=%fncs.fp.installer.location:"=%
	call :mutedLog %fncs.fp.installer.location%
	call :log
	goto :fncsFixpackPathAcquiry

        set firstTime=true
	:ceclientFixpackPathAcquiry
	if not "%ceclient.fp.installer.location%" == "" (
		REM echo !ceclient.fp.installer.location! | findstr /C:"Windows" 1>nul
		if exist !ceclient.fp.installer.location! (
			if not "!ceclient.fp.installer.location:P8CPE-CLIENT=!" == "!ceclient.fp.installer.location!" (
				if not "!ceclient.fp.installer.location:EXE=!" == "!ceclient.fp.installer.location!" (
				    goto :validateBackup
				) else (
				    call :log Error: "!ceclient.fp.installer.location!" is not the location of FileNet CE Client installer or the binary file is wrong.
				)
			) else (
			    call :log Error: "!ceclient.fp.installer.location!" is not the location of FileNet CE Client installer or the binary file is wrong.
			)
		)
		if not "!firstTime!" == "true" (
			call :log Error: "!ceclient.fp.installer.location!" is not the location of FileNet CE Client installer.
			call :log
		) else (
			set firstTime=false
		)
	) else (
		if not "!firstTime!" == "true" (
			call :log Error: The location of FileNet CE Client installer is empty.
			call :log
		) else (
			set firstTime=false
		)
	)

	call :log Enter the location of FileNet CE Client installer (e.g.: C:\filenetFixpack\5.2.1.7-P8CPE-CLIENT-WIN-FP007.EXE) [%ceclient.fp.installer.location%]:
	set /P ceclient.fp.installer.location=%~1
	set   ceclient.fp.installer.location=%ceclient.fp.installer.location:"=%
	call :mutedLog %ceclient.fp.installer.location%
	call :log
	goto :ceclientFixpackPathAcquiry


        :validateBackup
	if not "%conn.home.location%" == "" (
		if exist !conn.home.location! (
                       if exist !conn.home.location!\FileNet_backup\Engine-ws.ear (
			      if exist !conn.home.location!\FileNet_backup\navigatorEAR.ear (
                                      goto :validateSuccess
                              ) else (
              		              call :log
                                      echo Error: Please run backup task before upgrade FileNet.
	                              exit /b 1 
                              )
                       ) else (
              		      call :log
                              echo Error: Please run backup task before upgrade FileNet.
	                      exit /b 1 
                       )
		)
	) else (
              call :log
              echo Error: Please run backup task before upgrade FileNet.
	      exit /b 1
	)

	:validateSuccess
	if not "%fncs.fp.installer.location%" == "" (
		if !FREE_SPACE! LEQ 3072 (
			REM if the disk space is less than 3GB
			call :log Error: On disk "%volume%", there must be more than 3GB of free space. Please clean up after the program exit.
			call :log
			exit /b 1
		)
	)
	if not "%fncs.installer.location%" == "" (
		if !FREE_SPACE! LEQ 3072 (
			REM if the disk space is less than 3GB
			call :log Error: On disk "%volume%", there must be more than 3GB of free space. Please clean up after the program exit.
			call :log
			exit /b 1
		)
	)
	call :log
	call :log Validation passed.
	call :log

        set MY_HOME=%conn.home.location%
        set WAS_HOME=%was.dm.path%

        call :log
        call :log anonymous setting: !set_fn_anonymous!
        call :log

        call :log
	echo %ceclient.fp.installer.location% -i silent -f "%conn.home.location%\tmp\ceclient_silent_install_windows.txt" > ceclientupdate.bat

        call :log installing FileNet Content Engine Client %ceclient.fp.installer.location% ...
	REM install FNCS fp through python
	set ceclient_install="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set ceclient_install=%ceclient_install% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set ceclient_install=%ceclient_install% -wsadmin_classpath "%conn.home.location%\lib\lccfg.jar"
        set ceclient_install=%ceclient_install% -f "%conn.home.location%\FileNet.update\scripts\install_fn_fp.py" "ceclient" "%conn.home.location%" "ceclientupdate.bat"

        call %ceclient_install% >> "%fn.fncs.ceclient.update.log%" 2>&1
        call :log return code: %errorlevel%
	if %errorlevel% == 99 (
                call :log
		echo Error: Node agent is not start yet, please make sure all node agents are start up before updating FileNet.
		exit /b 1
	)
	if not %errorlevel% == 0 (
                call :log
		echo Error: FileNet Content Engine CLIENT installation failed.
		exit /b 1
	) else (
                call :log
		call :log FileNet Content Engine CLIENT installation success.
	)
	
	
	
	if "%skip_navigator%" == "yes" goto skipIntall

	:fncsInstall
	echo %fncs.fp.installer.location% -i silent -f "%conn.home.location%\tmp\FNCS-2.0.0.0-windows_SilentInstall.properties" > fncsupdate.bat
	REM call :log running %fncs.fp.installer.location% -i silent -f "%conn.home.location%\tmp\FNCS-2.0.0.0-windows_SilentInstall.properties"
	REM call fncsupdate.bat >> "%fn.fncs.ceclient.update.log%" 2>&1
	call :log installing FileNet Content Navigator %fncs.fp.installer.location% ...
	REM install CE Client fp through python
	set fncs_install="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set fncs_install=%fncs_install% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set fncs_install=%fncs_install% -wsadmin_classpath "%conn.home.location%\lib\lccfg.jar"
        set fncs_install=%fncs_install% -f "%conn.home.location%\FileNet.update\scripts\install_fn_fp.py" "fncs" "%conn.home.location%" "fncsupdate.bat"

        call %fncs_install% >> "%fn.fncs.ceclient.update.log%" 2>&1
        call :log return code: %errorlevel%
	if %errorlevel% == 99 (
                call :log
		echo Error: Node agent is not start yet, please make sure all node agents are start up before updating FileNet.
		exit /b 1
	)
	if not %errorlevel% == 0 (
                call :log
		echo Error: IBM CONTENT NAVIGATOR installation failed.
		exit /b 1
	) else (
                call :log
		call :log IBM CONTENT NAVIGATOR installation success.
	)
	goto resetWasAdminuser

	:skipIntall
	call :log
	call :log  Skip intall IBM CONTENT NAVIGATOR.

	:resetWasAdminuser
    call :log
	REM deploy FNCE and FNCS through the DM connection using soap
	set restore_fncs_config="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set restore_fncs_config=%restore_fncs_config% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
        set restore_fncs_config=%restore_fncs_config% -f "%conn.home.location%\FileNet.update\scripts\restore_fn_config.py" "%conn.home.location%"
	REM call !restore_fncs_config!
	call %restore_fncs_config% >> "%fn.fncs.ceclient.update.log%" 2>&1
        call :log
	if not %errorlevel% == 0 (
                call :log
		echo Error: restore IBM CONTENT NAVIGATOR configuration files failed.
		exit /b 1
	) else (
                call :log
		call :log restore IBM CONTENT NAVIGATOR configuration files success.
	)

        call :log
        call :log updating WAS Admin Username...
        set reset_was_adminuser=%java% -jar "%conn.home.location%\lib\lccfg.jar" "%conn.home.location%\FNCS\configure\profiles\CCM\applicationserver.xml"
        set reset_was_adminuser=%reset_was_adminuser% "/configuration/property[@name='ApplicationServerAdminUsername']/value/text()" %was.admin.user%
	call %reset_was_adminuser%

        call :log
        call :log updating WAS Admin Password...
        set reset_was_adminpwd=%java% -jar "%conn.home.location%\lib\lccfg.jar" "%conn.home.location%\FNCS\configure\profiles\CCM\applicationserver.xml"
        set reset_was_adminpwd=%reset_was_adminpwd% "/configuration/property[@name='ApplicationServerAdminPassword']/value/text()" %was.admin.password%
	call %reset_was_adminpwd%
        call :log

	REM deploy FNCE and FNCS through the DM connection using soap
        call :log starting to build and deploy FileNet ear...
	set fn_deploy="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set fn_deploy=%fn_deploy% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set fn_deploy=%fn_deploy% -wsadmin_classpath "%conn.home.location%\lib\lccfg.jar"
        set fn_deploy=%fn_deploy% -javaoption "-Dpython.path=C:\Program Files\IBM\Connections\lib"
        set fn_deploy=%fn_deploy% -f "%conn.home.location%\FileNet.update\scripts\deploy_fn_apps.py" "%conn.home.location%\cfg.py" "deploy_fncs"

	set fn_config="%was.dm.path%\bin\wsadmin.bat" -lang jython -conntype SOAP
	set fn_config=%fn_config% -host localhost -port %dm.soap.port% -username %was.admin.user% -password %was.admin.password%
	set fn_config=%fn_config% -wsadmin_classpath "%conn.home.location%\lib\lccfg.jar"
        set fn_config=%fn_config% -javaoption "-Dpython.path=C:\Program Files\IBM\Connections\lib"
        set fn_config=%fn_config% -f "%conn.home.location%\FileNet.update\scripts\deploy_fn_apps.py" "%conn.home.location%\cfg.py" "config_fncs"
    REM  call !fn_deploy!
    call %fn_deploy%  >> "%fn.fncs.ceclient.update.log%" 2>&1	
	if not %errorlevel% == 0 (
                call :log
		echo Error: deploy IBM CONTENT NAVIGATOR failed.
		exit /b 1
	) else (
                call :log
		call :log IBM CONTENT NAVIGATOR deployment success.
	)
    REM call !fn_config!
	call %fn_config%  >> "%fn.fncs.ceclient.update.log%" 2>&1
	if not %errorlevel% == 0 (
                call :log
		echo Error: configure IBM CONTENT NAVIGATOR failed.
		exit /b 1
	) else (
                call :log
		call :log IBM CONTENT NAVIGATOR configuration success.
	)
        call :log

	endlocal & set was.dm.path=%was.dm.path%& set was.admin.user=%was.admin.user%& set was.admin.password=%was.admin.password%& set conn.home.location=%conn.home.location%& set ceclient.fp.installer.location=%ceclient.fp.installer.location%& set fncs.fp.installer.location=%fncs.fp.installer.location%
goto :EOF

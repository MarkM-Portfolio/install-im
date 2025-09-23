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

set array=(conn.home.location)

REM Cannot recover batch parameters once processed with SHIFT
REM so need to store script path and name for use later if needed
set fnUpdateScriptPath_=%~dp0
set fnUpdateScriptName_=%~nx0

REM Set fn.backup.log to (default filename) fn-backup.log in the same directory of this script if not specified
if not defined fn.backup.log (
	set fn.backup.log=!fnUpdateScriptPath_!fn-backup.log
)
REM Check if we can write to log file
copy /Y nul "!fn.backup.log!" > nul 2>&1
if not %errorlevel% == 0 (
	echo Error: Unable to write to log: !fn.backup.log!
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


call :backupFileNet >> "%fn.backup.log%" 2>&1
if not %errorlevel% == 0 (
	call :log Error: Please read the log for detail of the problem, it's !fn.backup.log!.
	call :log 
	exit /b 1
)

call :log FileNet backup has been done successfully.

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
	call :log (If there is space in the value of a parameter, please enclose it with a pair of quotation marks, e.g.: "C:\Program Files\IBM\Connections").
	call :log conn.home.location : Path of Connections Home (e.g.: "C:\Program Files\IBM\Connections")
goto :EOF

:log
	echo.%*
	echo.%* >> "%fn.backup.log%" 2>&1
goto :EOF

:mutedLog
	echo.%* >> "%fn.backup.log%" 2>&1
goto :EOF

:doMutedAction
	set temp=%*
	for /F "tokens=1* delims=]" %%A in ('call !temp! 2^>^&1 ^| find /N /V ""') do (
		echo.%%B
		echo.%%B >> "%fn.backup.log%" 2>&1
	)
GOTO :EOF

:validate
	setlocal
	call :log Performing validation check ...
	call :log

        set firstTime=true

	:connHomePathAcquiry
	if not "%conn.home.location%" == "" (
		if exist !conn.home.location! (
                       if exist !conn.home.location!\FileNet\ContentEngine\tools\configure\profiles\CCM\ear\Engine-ws.ear (
				if exist !conn.home.location!\FNCS\configure\deploy\navigatorEAR.ear (
                                        goto :validateSuccess
                                )
                       )
		)
	) else (
		goto :validateconnhome
	)
	
	:validateconnhome
	call :log Input the Path of Connections Home (e.g.: C:\Program Files\IBM\Connections) [%conn.home.location%]:
	set /P conn.home.location=%~1
	set   conn.home.location=%conn.home.location:"=%
	call :mutedLog %conn.home.location%
	call :log
	goto :connHomePathAcquiry

	
	:validateSuccess
        call :log
	call :log Validation passed.
	
	endlocal & set conn.home.location=%conn.home.location%

:backupFileNet
        call :log
        call :log backing up FileNet
        call :log 
	if exist "!conn.home.location!\FileNet_backup" (
              call :log !conn.home.location!\FileNet_backup folder exists, removing it...
              rd /S /Q "!conn.home.location!\FileNet_backup"
	)
        call :log creating backup folder - !conn.home.location!\FileNet_backup
        mkdir "!conn.home.location!\FileNet_backup"

        mkdir "!conn.home.location!\FileNet_backup\lib"
        mkdir "!conn.home.location!\FileNet_backup\fncs"
        mkdir "!conn.home.location!\FileNet_backup\fncs\CCM"
        mkdir "!conn.home.location!\FileNet_backup\CE_API"
        call :log

        call :log backing up Engine-ws.ear
        copy "!conn.home.location!\FileNet\ContentEngine\tools\configure\profiles\CCM\ear\Engine-ws.ear" "!conn.home.location!\FileNet_backup"

        call :log backing up Jace Jar
        copy "!conn.home.location!\FileNet\ContentEngine\lib\Jace*.jar" "!conn.home.location!\FileNet_backup\lib"

        call :log backing up navigator ear
        copy "!conn.home.location!\FNCS\configure\deploy\navigatorEAR.ear" "!conn.home.location!\FileNet_backup"

        call :log backing up fncs-sitePrefs
        copy "!conn.home.location!\FNCS\configure\explodedformat\fncs\WEB-INF\classes\fncs-sitePrefs.properties" "!conn.home.location!\FileNet_backup"

        call :log backing up CE API
        xcopy /s "!conn.home.location!\FNCS\configure\CE_API" "!conn.home.location!\FileNet_backup\CE_API"

        call :log backing up profile CCM
        xcopy /s "!conn.home.location!\FNCS\configure\profiles\CCM" "!conn.home.location!\FileNet_backup\fncs\CCM"
goto :EOF
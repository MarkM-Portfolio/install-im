@REM ************ Begin Standard Header - Do not add comments here ***************
@REM
@REM  File:     prereq_checker.bat
@REM  Version:  1.1.1.7
@REM  Modified: 08/25/11
@REM  Build:    20110825
@REM
@REM ************** Begin Copyright - Do not add comments here ****************
@REM 
@REM  Licensed Materials - Property of IBM
@REM  (C) Copyright IBM Corp. 2009, 2011
@REM  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
@REM  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
@REM
@REM ************************ End Standard Header *************************
@echo off

@REM Change to directory where this script is running
cd /d %~dp0

setlocal

set PRS_VBS=%~dp0preq.vbs

set CMD_LINE_ARGS=
set ERR_LEVEL=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

for /f "delims=" %%i in ('cscript.exe') do ( set ver=%%i & goto sver) 
:sver:
set cscript_ver=%ver:*5.=5.%
if %cscript_ver% leq 5.5 (goto err_cscript_ver)
cscript.exe //nologo "%PRS_VBS%" %CMD_LINE_ARGS%
if "%ERR_LEVEL%"=="" (set ERR_LEVEL=%ERRORLEVEL%)
goto end

:err_cscript_ver
echo The cscript.exe version should be greater than 5.5, but the version found is %cscript_ver%
set ERR_LEVEL=1

:end
if "%ERR_LEVEL%"=="" (set ERR_LEVEL=%ERRORLEVEL%)
rem echo prereq_checker.bat %ERR_LEVEL%
exit /b %ERR_LEVEL%  

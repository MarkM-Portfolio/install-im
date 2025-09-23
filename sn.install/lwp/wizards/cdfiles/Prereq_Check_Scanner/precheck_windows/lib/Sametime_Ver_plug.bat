@REM ************ Begin Standard Header - Do not add comments here ***************
@REM
@REM  File:     Sametime_Ver_plug.bat
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

rem Check if Sametime community server is installed and what the version is by querying from regedit
@echo off
reg query "HKLM\SOFTWARE\Lotus\Sametime\Community Server" /v Version
if not %errorlevel%==0 goto NOSAMETIME
for /f "tokens=3" %%i in ('reg query "HKLM\SOFTWARE\Lotus\Sametime\Community Server" /v Version') do set csver=%%i
echo Sametime Ver=%csver%
goto END

:NOSAMETIME
echo Sametime Ver=0(Notinstalled)

:END

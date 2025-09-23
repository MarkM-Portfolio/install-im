@REM ************ Begin Standard Header - Do not add comments here ***************
@REM
@REM  File:     DB2_version_plug.bat
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

@rem Check DB2 version by running 'db2level' which should be in the PATH if DB2 is installed
db2level 1>nulll 2>null
if not %errorlevel%==0 goto notfind

:setversion
for /f "delims=, skip=2 tokens=1,4" %%i in ('db2level') do ( set ver=%%i & set fp=%%j & goto ver) 
:ver
set lver=%ver:~30,20%

@REM seach '"' through %fp%, if '"' appears, get the fixpack number just after '"'. 
@REM replace '"' with !.
set fp=%fp:"=!%
set chr=!
:next
set comp=%fp:~0,1%
set fp=%fp:~1,20%
if not defined fp goto setfp
if "%chr%"=="%comp%" (goto found) else (goto next)
:found
set dfp=%fp:~0,-3%
goto end
:setfp
for /f "delims=, skip=3" %%i in ('db2level') do ( set fp=%%i & goto fp)
:fp
set fver=%fp:~1,10%
set dfp=%fver:~0,-3%
goto end

:notfind
echo Not found
goto exit
rem echo DB2 Version=v9.5.100.179FP4

:end
echo DB2 Version=%lver:~0,-2% FP %dfp%
:exit
del null nulll

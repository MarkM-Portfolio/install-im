@REM ************ Begin Standard Header - Do not add comments here ***************
@REM
@REM  File:     common.bat
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

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs  
:doneSetArgs

cscript.exe //nologo common.vbs %CMD_LINE_ARGS%




@REM THIS PRODUCT CONTAINS RESTRICTED MATERIALS OF IBM
@REM 5724-E76 and 5724-E77 (C) COPYRIGHT International Business Machines Corp., 2003
@REM All Rights Reserved * Licensed Materials - Property of IBM
@REM US Government Users Restricted Rights - Use, duplication or disclosure
@REM restricted by GSA ADP Schedule Contract with IBM Corp.

@REM wsadmin launcher

@echo off
REM Usage: wsadmin arguments
setlocal

set RUNDIR=%~dp0
call "%WAS_HOME%/bin/setupCmdLine.bat"

@REM CONSOLE_ENCODING controls the output encoding used for stdout/stderr
@REM    console - encoding is correct for a console window
@REM    file    - encoding is the default file encoding for the system
@REM    <other> - the specified encoding is used.  e.g. Cp1252, Cp850, SJIS

SET CONSOLE_ENCODING=-Dws.output.encoding=console
SET PATH=%WAS_PATH%
@REM For debugging the utility itself
@REM set DEBUG=-Djava.compiler=NONE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7777

if NOT %USER_INSTALL_ROOT%.==. goto runcmd
SET USER_INSTALL_ROOT=%WAS_HOME%

:runcmd
java %CONSOLE_ENCODING% %DEBUG% "%CLIENTSOAP%" "%CLIENTSAS%" "-Dcom.ibm.wps.home=%WP_HOME%" "-Dcom.ibm.wps.pui.configprops=%WP_PUI_CONFIG%" "-Dcom.ibm.ws.scripting.wsadminprops=%WSADMIN_PROPERTIES%" -Dcom.ibm.ws.management.standalone=true -Dcom.ibm.websphere.management.filetransfer.downloadPathsNotRestricted=true "-Duser.install.root=%USER_INSTALL_ROOT%" "-Dwas.repository.root=%CONFIG_ROOT%" "-Dlocal.cell=%WAS_CELL%" "-Dlocal.node=%WAS_NODE%" "-Dws.ext.dirs=%WAS_EXT_DIRS%;%RUNDIR%\.." -classpath "%WAS_CLASSPATH%" com.ibm.ws.bootstrap.WSLauncher com.ibm.websphere.update.delta.adminconfig.WPWsAdmin %*

REM Make sure we pass back the return code from Java through the invoker (cmd.exe).  If called from shell (no special env setting ), Only exit the script
set EXEC_CMD_OPT=/b
if NOT %ADMIN_EXIT_CMD%.==. set EXEC_CMD_OPT=
exit %EXEC_CMD_OPT% %ERRORLEVEL%

endlocal



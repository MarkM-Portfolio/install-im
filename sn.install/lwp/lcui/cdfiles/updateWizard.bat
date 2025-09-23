@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                              
@REM                                                                   
@REM Copyright IBM Corp. 2003, 2016                                    
@REM                                                                   
@REM The source code for this program is not published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

@REM THIS PRODUCT CONTAINS RESTRICTED MATERIALS OF IBM
@REM All Rights Reserved * Licensed Materials - Property of IBM
@REM Configuration Based Update Installer

@if defined echo (echo %echo% ) else echo off
setlocal

@REM set DEBUG_UPDATE=yes to turn on debugging statements
set DEBUG_UPDATE=no

set LaunchTitle=%0
Echo Start of [ %LaunchTitle% ] launch script.
Echo Build @BLD_NUMBER@
Echo.

:Wizard
GoTo ParseArgs

:FailWizard
Echo The wizard jar file does not exist.  Exiting.
Echo.
GoTo End

:ParseArgs
set PREREQ_DISABLE_INST=
set PREREQ_DISABLE_UNINST=
set FOR_EFIX=
GoTo ValidateArgs

@REM process any arguments
:ValidateArgs
IF "%1"=="-dpInstall" GoTo SetPrereqInstall
IF "%1"=="-dpUninstall" GoTo SetPrereqUninstall
IF "%1"=="-efixOnly" GoTo SetEFixOnly
IF "%1"=="-configProperties" GoTo SetConfigProps

IF "%1"=="" GoTo CheckWASHome

IF NOT "%1"=="-usage" Echo Usage error: [ "%1" is an unrecognized argument ]

Echo.
Echo Usage: updateWizard ([ -efixOnly ] [ -dpInstall ] [ -dpUninstall ] [-configProperties propFile] [ -usage ])
Echo.
Echo   efixOnly         - Run the installer in EFix Processing mode only. 
Echo   dpInstall        - Disable install prerequisite error locking.	
Echo   dpUninstall      - Disable uninstall prerequisite error locking.
Echo   configProperties - Additional configuration properties.
Echo   usage            - Display syntax help. 
Echo.
GoTo End

:SetEFixOnly
set FOR_EFIX=-W UpdateActionSelect.disablePTFOptions=yes
@REM go see if there are any more args
shift
GoTo ValidateArgs

:SetPrereqInstall
set PREREQ_DISABLE_INST=-W EfixInstallPrereqErrorAction.prereqOverride="yes"
@REM go see if there are any more args
shift
GoTo ValidateArgs  

:SetPrereqUninstall
set PREREQ_DISABLE_UNINST=-W EfixUninstallPrereqError.prereqOverride="yes"
@REM go see if there are any more args
shift
GoTo ValidateArgs

:SetConfigProps
set WP_PUI_CONFIG=%2
@REM go see if there are any more args - Skipping 2 args
shift
shift
GoTo ValidateArgs


:CheckWASHome
IF NOT "%WAS_HOME%" == "" GoTo CheckWasSetup
Echo The WAS_HOME env variable MUST be set.  Exiting.
Echo.
Pause.
GoTo End

:CheckWasSetup
IF "%DEBUG_UPDATE%" == "yes" Echo Attempting to locate setupCmdLine.bat.
IF "%DEBUG_UPDATE%" == "yes" Echo.
IF EXIST "%WAS_HOME%\bin\setupCmdLine.bat" GoTo RunWasSetup
Echo The WAS_HOME env variable MUST be set.  Exiting.
Echo Unable to locate WAS setupCmdLine.bat in %WAS_HOME%.  Exiting.
Echo.
GoTo End



:RunWasSetup
call "%WAS_HOME%\bin\setupCmdLine.bat"
Echo "**** WAS_CELL = %WAS_CELL%"
Echo "**** USER_INSTALL_ROOT = %USER_INSTALL_ROOT%"
GoTo TestJDK

:TestJDK
IF NOT EXIST "%JAVA_HOME%\bin" GoTo FailJavaHome
IF NOT EXIST "%JAVA_HOME%\bin\java.exe" GoTo FailJavaHome
goto LaunchInstaller

:FailJavaHome
Echo The JDK was not found within the set JAVA_HOME:
Echo   JAVA_HOME: [ %JAVA_HOME%\bin\java.exe ]
Echo.
Echo Exiting.
Echo.
goto End

:LaunchInstaller

set DBG_PROP=-Dcom.ibm.lconn.ifix.debug=false -Dcom.ibm.lconn.ifix.ziputil.debug=false -Dcom.ibm.lconn.ifix.fileutil.debug=false
set Log_Level=-Dcom.ibm.websphere.update.ptf.log.level=5
Echo Launching the update installer wizard.
Echo.
set WindowsLAF=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
set PATH="%JAVA_HOME%"\bin;%PATH%
echo %JAVA_HOME%

if "%PROCESSOR_ARCHITECTURE%" == "x86" ( 
	set classpath=.;lib/org.eclipse.swt.win32.win32.x86.jar
)else ( 
	set classpath=.;lib/org.eclipse.swt.win32.win32.x86_64.jar)

set classpath=%classpath%;lib/lcui.jar;lib/org.eclipse.core.commands.jar;lib/org.eclipse.equinox.common.jar;lib/org.eclipse.jface.jar;lib/org.eclipse.ui.forms_3.5.100.v20110425.jar;lib/nativefile.jar;lib/icu4j-68.1.jar;lib/commons-configuration-1.5-plus-node-clone.jar;lib/commons-logging-1.0.4.jar;lib/commons-lang-2.4.jar;lib/commons-collections-3.2.1.jar
start /B javaw.exe %DBG_PROP% -Duser.install.root="%USER_INSTALL_ROOT%" -Dwas.home="%WAS_HOME%" -Dwas.cell="%WAS_CELL%"  com.ibm.lconn.wizard.launcher.UpdateInstallerLauncher

GoTo End

:End
Echo.
Rem Echo End of [ %LaunchTitle% ]
Echo.
endlocal


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

@REM Command Descriptions
@REM 
@REM The '-fix' option specifies an eFix update.
@REM The '-fixpack' option specifies a FixPack update.
@REM The '-install' option specifies an install action.
@REM The '-uninstall' option specifies an uninstall action.
@REM The '-installDir' option specifies the product install root location.
@REM The '-fixDir' option specifies the eFix directory.
@REM The '-fixpackDir' option specifies the FixPack directory.
@REM The '-fixes' specifies an eFix to install or uninstall.
@REM The '-fixpackID' specifies a FixPack to install or uninstall.
@REM The '-wasUserId' specifies a valid WAS administrator ID.
@REM The '-wasPassword' specifies a valid WAS administrator password.
@REM The '-fixDetails' option displays eFix detail information.
@REM The '-fixpackDetails' option displays FixPack detail information.
@REM The '<propertyFile>.properties' option specifies an externally supplied parameters file.

@REM Launch Arguments:
@REM
@REM For EFix Processing:
@REM      updateLC <propertiesFile>
@REM               ( -installDir <product install root>
@REM		         [ -fix ]
@REM                 [ -fixDir <ifix repository root> ]
@REM                 [ -install | -uninstall | -uninstallAll ]
@REM                 [ -fixes <ifix ID> ]
@REM                 [ -wasUserId <userid> ]
@REM                 [ -wasPassword <password> ]
@REM                 [ -fixDetails ]
@REM                 [ -configProperties propertiesFile ] |
@REM               ( -help | -? | /help | /? | -usage )
@REM
@REM For FixPack Processing:
@REM  updateLC <propertiesFile>
@REM	        ( -installDir <product install root>
@REM           	  [ -fixpack ]
@REM              [ ( -install | -uninstall
@REM 		          -fixpackDir <fixpack repository root> ]
@REM 	              -fixpackID <FixPack ID> ]
@REM		      [ -wasUserId <userid> ]
@REM		      [ -wasPassword <password> ]
@REM              [ -configProperties propertiesFile ]
@REM              [ -fixpackDetails ] ) |
@REM            ( -help | -? | /help | /? | -usage )


@if defined echo (echo %echo% ) else echo off
@setlocal

@REM set DEBUG_UPDATE=yes to turn on debugging statements
set DEBUG_UPDATE=no

@REM CONSOLE_ENCODING controls the output encoding used for stdout/stderr
@REM    console - encoding is correct for a console window
@REM    file    - encoding is the default file encoding for the system
@REM    <other> - the specified encoding is used.  e.g. Cp1252, Cp850, SJIS
SET CONSOLE_ENCODING=-Dws.output.encoding=console

@REM Set classpath for eFix commandline installer

set LaunchTitle=%0
Echo Start of [ %LaunchTitle% ]
Echo Build @BLD_NUMBER@
Echo.

:Silent
GoTo CheckWASHome

:FailSilent
Echo The installer jar file does not exist.  Exiting.
Echo.
GoTo End

:CheckWASHome
IF NOT "%WAS_HOME%" == "" GoTo CheckWasSetup
Echo The WAS_HOME env variable MUST be set.  Exiting.
Echo.
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
goto FixPermissions

:FailJavaHome
Echo The JDK was not found within the set JAVA_HOME:
Echo   JAVA_HOME: [ %JAVA_HOME%\bin\java.exe ]
Echo.
Echo Exiting.
Echo.
goto End

:FixPermissions
:: Set read/write permissions on component files

:: No way to unshift parameters, so must use secondary bat file.
IF EXIST %temp%\wpptfcmp.bat del %temp%\wpptfcmp.bat
echo @echo off > %temp%\wpptfcmp.bat
echo :ShiftIterate >> %temp%\wpptfcmp.bat
echo if "%%~1"=="-installDir" goto GetName >> %temp%\wpptfcmp.bat
echo if "%%~1"=="-installdir" goto GetName >> %temp%\wpptfcmp.bat
echo if "%%~1"=="" goto NoArg >> %temp%\wpptfcmp.bat
echo shift >> %temp%\wpptfcmp.bat
echo goto ShiftIterate >> %temp%\wpptfcmp.bat
echo :NoArg >> %temp%\wpptfcmp.bat
echo goto End >> %temp%\wpptfcmp.bat
echo :GetName  >> %temp%\wpptfcmp.bat
echo SET TARGETDIR=%%~s2>> %temp%\wpptfcmp.bat
echo Echo Setting permissions on %%TARGETDIR%%\version >> %temp%\wpptfcmp.bat
echo IF EXIST %%TARGETDIR%%\version\nul attrib -R %%TARGETDIR%%\version >> %temp%\wpptfcmp.bat 
echo IF EXIST %%TARGETDIR%%\version\nul attrib -R %%TARGETDIR%%\version\* /S /D >> %temp%\wpptfcmp.bat 
echo :End >> %temp%\wpptfcmp.bat

call %temp%\wpptfcmp.bat %*
REM del %temp%\wpptfcmp.bat
goto LaunchInstaller

:LaunchInstaller

set DBG_PROP=-Dcom.ibm.lconn.ifix.debug=false -Dcom.ibm.lconn.ifix.ziputil.debug=false -Dcom.ibm.lconn.ifix.fileutil.debug=false
set Log_Level=-Dcom.ibm.websphere.update.ptf.log.level=5

set classpath=.;lib/lcui.jar;lib/nativefile.jar;lib/icu4j-68.1.jar;lib/commons-configuration-1.5-plus-node-clone.jar;lib/commons-logging-1.0.4.jar;lib/commons-lang-2.4.jar;lib/commons-collections-3.2.1.jar
"%JAVA_HOME%\bin\java" %CONSOLE_ENCODING% %DBG_PROP% "-Dcom.ibm.wp.pui.systemroot=%SystemRoot%" -Duser.install.root="%USER_INSTALL_ROOT%" -Dwas.home="%WAS_HOME%" -Dwas.cell="%WAS_CELL%" -Xmx512m com.ibm.websphere.update.launch.Launcher com.ibm.websphere.update.silent.UpdateInstaller %*

:End
Echo.
Echo End of [ %LaunchTitle% ]
Echo.
endlocal


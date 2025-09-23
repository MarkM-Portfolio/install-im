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

@echo off
set setLocale=-Duser.language=en
set PATH=jvm/win/jre/bin;%PATH%
if "%1" == "-silent" GOTO SILENTMODE
GOTO UILAUNNCH

:UILAUNNCH
set CLASSPATH=.;lib/ibmjs.jar;lib/org.eclipse.core.commands_3.6.100.v20140528-1422.jar;lib/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar;lib/org.eclipse.jface_3.10.2.v20141021-1035.jar;lib/org.eclipse.swt.win32.win32.x86_64_3.103.2.v20150203-1351.jar;lib/Wizards.jar
start javaw -Dibm.stream.nio=true -Djava.library.path=lib/linkfile com.ibm.lconn.wizard.launcher.TDIPopulationLauncher %*
GOTO END

:SILENTMODE
set CLASSPATH=.;lib/ibmjs.jar;lib/org.eclipse.core.commands_3.6.100.v20140528-1422.jar;lib/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar;lib/org.eclipse.jface_3.10.2.v20141021-1035.jar;lib/org.eclipse.swt.win32.win32.x86_64_3.103.2.v20150203-1351.jar;lib/Wizards.jar
java -Djava.library.path=lib/linkfile com.ibm.lconn.wizard.launcher.TDIPopulationLauncher %*
GOTO END

:END

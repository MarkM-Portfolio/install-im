@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                              
@REM                                                                   
@REM Copyright IBM Corp. 2013, 2016                                    
@REM                                                                   
@REM The source code for this program is not published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

set CE_HOME=../ContentEngine
set FNCS_HOME=./../FNCS

IF NOT EXIST "%JAVA_HOME%\bin\java.exe" GoTo useCEJavaHome
goto Launch

:useCEJavaHome
set JAVA_HOME=%CE_HOME%/tools/_jvm17/jre
goto Launch

:Launch
set classpath=.;%CE_HOME%/lib/Jace.jar;%CE_HOME%/lib/log4j.jar;./ccmDomainTool.jar
"%JAVA_HOME%\bin\java" -classpath %CLASSPATH% com.ibm.connections.ccmDomainTool.ccmDomainTool ccmUpdate

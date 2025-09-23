@REM ***************************************************************** 
@REM                                                                   
@REM IBM Licensed Material                                             
@REM                                                                   
@REM Copyright IBM Corp. 2010, 2015                                    
@REM                                                                   
@REM The source code for this program is not published or otherwise    
@REM divested of its trade secrets, irrespective of what has been      
@REM deposited with the U.S. Copyright Office.                         
@REM                                                                   
@REM ***************************************************************** 

@REM 5724-S68                                                          
@REM 5724-S68                                                          
@echo off
SETLOCAL ENABLEEXTENSIONS

IF NOT DEFINED WAS_HOME (
ECHO The WAS_HOME environment variable needs to be set to the installation location of WebSphere Application Server
GOTO :END
) ELSE (
ECHO WAS_HOME=%WAS_HOME%
)



SET MIGRATION_JAR=%~dp0..\lib\lc_migration.jar
IF EXIST "%MIGRATION_JAR%" del /F /Q "%MIGRATION_JAR%"
copy /Y lib\lc_migration.jar "%MIGRATION_JAR%"
REM copy /Y scripts\imports\migrate_util.xml "%~dp0..\ConfigEngine\config\includes\migrate_util.xml"

ECHO "%WAS_HOME:"=%\bin\ws_ant.bat" -f migrate_util.xml -propertyfile ..\lcinstall.properties -DWasAppHome=%WAS_HOME% -lib .\lib %*

"%WAS_HOME:"=%\bin\ws_ant.bat" -f migrate_util.xml -propertyfile ..\lcinstall.properties -DWasAppHome=%WAS_HOME% -lib .\lib %*

:END
pause

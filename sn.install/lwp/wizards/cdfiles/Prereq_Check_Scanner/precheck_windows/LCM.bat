@REM ************** Begin Copyright - Do not add comments here ****************
@REM 
@REM  Licensed Materials - Property of IBM
@REM  (C) Copyright IBM Corp. 2009, 2011
@REM  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
@REM  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
@REM
@REM ************************ End Standard Header *************************

prereq_checker.bat "LCM 02300000" detail  PATH=C:\windows\itlm  -p LCM.WASAgent=true SERVER=IP.PIPE://localhost:9988 , LCM.CIT="C:\" , LCM.TCD="C:\", LCM.TEMP="%TEMP%", LCM.WINDIR="%WINDIR%"
echo lcm.bat %ErrorLevel%
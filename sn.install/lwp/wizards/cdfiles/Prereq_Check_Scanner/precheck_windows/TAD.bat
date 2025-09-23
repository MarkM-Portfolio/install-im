@REM ************** Begin Copyright - Do not add comments here ****************
@REM 
@REM  Licensed Materials - Property of IBM
@REM  (C) Copyright IBM Corp. 2009, 2011
@REM  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
@REM  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
@REM
@REM ************************ End Standard Header *************************

prereq_checker.bat "TAD 07220000" detail  PATH=C:\windows\itlm  -p SERVER=IP.PIPE://localhost:9988 , TAD.CIT="C:\" , TAD.TCD="C:\", TAD.TEMP="%TEMP%", TAD.WINDIR="%WINDIR%"
echo tad.bat %ErrorLevel%

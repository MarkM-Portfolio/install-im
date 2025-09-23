'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/mssql.Client.vbs
' Version:  1.1.1.7
' Modified: 08/25/11
' Build:    20110825
'
' ************** Begin Copyright - Do not add comments here ****************
' 
'  Licensed Materials - Property of IBM
'  (C) Copyright IBM Corp. 2009, 2011
'  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
'  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
'
' ************************ End Standard Header *************************
' ---------------------------
' MS SQL Client version check
' ---------------------------
' Information on checking the SQL Native client level is at http://support.microsoft.com/kb/321185
'Registry keys to check
'SQL Server 2008 R2 - HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SQL Server\SQLNCLI10\CurrentVersion
'SQL Server 2008 / SQL Server Native Client 10 - HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SQL Server\SQLNCLI10\CurrentVersion
'SQL Server 2005 / SQL Server Native Client 9 - HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SQL Native Client\CurrentVersion
' Mapping from above URL as of 7/12/2011:
'Version pattern 	SQL Product
'10.5.x.x 	SQL Server 2008 R2
'10.00.x.x 	SQL Server 2008
'9.00.x.x 	SQL Server 2005
'8.00.x.x 	SQL Server 2000 
Set objWMIReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
const HKEY_LOCAL_MACHINE = &H80000002
strKeyPath1 = "SOFTWARE\Microsoft\Microsoft SQL Server\SQLNCLI10\CurrentVersion"
strKeyPath2 = "SOFTWARE\Microsoft\Microsoft SQL Native Client\CurrentVersion"
strVersionName="Version"
sqlfindversion = ""
sqlversion = ""

retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyPath1, strVersionName, sqlfindversion)
If retVal=0 Then
   sqlversion=sqlfindversion
End If
sqlfindversion=""
retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyPath2, strVersionName, sqlfindversion)
If retVal=0 Then
	If sqlversion <> "" Then
	    sqlversion=sqlversion & "," & sqlfindversion
	Else
	    sqlversion=sqlfindversion
	End If
End If


If sqlversion <> "" Then
    WScript.Echo "mssql.Client=" & sqlversion
Else
    WScript.Echo "MS SQL Client not found on this system"
    WScript.Echo "mssql.Client=0.0"
End If

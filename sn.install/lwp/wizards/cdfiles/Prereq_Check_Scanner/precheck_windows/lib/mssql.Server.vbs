'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/mssql.Server.vbs
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
' MS SQL Server version check
' ---------------------------
' Mapping from version number to product name is at http://support.microsoft.com/kb/321185
' Mapping from above URL as of 10/03/2019:
'Version pattern 	SQL Product
'14.0.x.x   SQL Server 2017
'13.0.x.x   SQL Server 2016
'12.0.x.x   SQL Server 2014
'11.0.x.x   SQL Server 2012
'10.5.x.x 	SQL Server 2008 R2
'10.00.x.x 	SQL Server 2008
'9.00.x.x 	SQL Server 2005
'8.00.x.x 	SQL Server 2000 
' HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SQL Server
Set objWMIReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
const HKEY_LOCAL_MACHINE = &H80000002
strKeyPath   = "SOFTWARE\Microsoft\Microsoft SQL Server"
strKeyPath2  = "SOFTWARE\Microsoft"
strKeyInstancePath = ""
strValueName="InstalledInstances"
currentInstanceNames = ""
strVersionName="CurrentVersion"
nextKey = ""
sqlfindversion = ""
sqlversion = ""
on error resume next
retVal = objWMIReg.GetMultiStringValue(HKEY_LOCAL_MACHINE, strKeyPath, strValueName, arrValues)			  				
'wscript.echo "  " & strValueName & " (REG_MULTI_SZ) ="
For Each strInstance in arrValues
   strKeyInstancePath=strKeyPath & "\" & strInstance & "\" & "MSSQLServer\CurrentVersion" 
   retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyInstancePath, strVersionName, sqlfindversion)
	If retVal=0 Then
	    sqlversion=sqlfindversion
	Else
	   strKeyInstancePath=strKeyPath2 & "\" & strInstance & "\" & "MSSQLServer\CurrentVersion" 
	   retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyInstancePath, strVersionName, sqlfindversion)
	   If retVal=0 Then
		    sqlversion=sqlfindversion
	   End If
	End If
Next

If sqlversion <> "" Then
    WScript.Echo "mssql.Server=" & sqlversion
Else
    WScript.Echo "MS SQL Server not found on this system"
    WScript.Echo "mssql.Server=0.0"
End If

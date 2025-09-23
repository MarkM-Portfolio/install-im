'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/mssql.Server.Location.vbs
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
' MS SQL Server location check
' ---------------------------
' Registry key to find instance described at item 12 in http://support.microsoft.com/default.aspx?scid=kb;en-us;257716&Product=sql2k
' Mapping from version number to product described at http://support.microsoft.com/kb/321185
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
'Format of output is instancename1,path1;instancename2,path;
' HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SQL Server
Set objWMIReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
const HKEY_LOCAL_MACHINE = &H80000002
strKeyPath   = "SOFTWARE\Microsoft\Microsoft SQL Server"
strKeyPath2  = "SOFTWARE\Microsoft"
strKeyInstancePath = ""
strValueName="InstalledInstances"
currentInstanceNames = ""
strpathName="SQLPath"
nextKey = ""
sqlfindpath = ""
sqlpath = ""
on error resume next
retVal = objWMIReg.GetMultiStringValue(HKEY_LOCAL_MACHINE, strKeyPath, strValueName, arrValues)			  				
'wscript.echo "  " & strValueName & " (REG_MULTI_SZ) ="
For Each strInstance in arrValues
   'wscript.echo "    " & strInstance
   strKeyInstancePath=strKeyPath & "\" & strInstance & "\" & "Setup" 
   retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyInstancePath, strpathName, sqlfindpath)
   'WScript.Echo "sqlfindpath=" & sqlfindpath
	If retVal=0 Then
		If sqlpath <> "" Then
		    sqlpath=sqlpath & ";" & strInstance & "," & sqlfindpath
		Else
		    sqlpath=strInstance & "," & sqlfindpath	    
		End If
	Else
	   strKeyInstancePath=strKeyPath2 & "\" & strInstance & "\" & "Setup" 
	   retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyInstancePath, strpathName, sqlfindpath)
   	   'WScript.Echo "sqlfindpath=" & sqlfindpath
	   If retVal=0 Then
		  If sqlpath <> "" Then
		      sqlpath=sqlpath & ";" & strInstance & "," & sqlfindpath
	   	  Else
		      sqlpath=strInstance & "," & sqlfindpath	    
		  End If
	   End If
	End If
Next

If sqlpath <> "" Then
    WScript.Echo "mssql.Server.Location=" & sqlpath
Else
    WScript.Echo "MS SQL Server not found on this system"
    WScript.Echo "mssql.Server.Location=Unknown"
End If

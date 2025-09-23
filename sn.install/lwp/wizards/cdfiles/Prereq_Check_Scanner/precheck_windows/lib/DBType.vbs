'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/DBType.vbs
' Version:  1.1.1.7.6
' Modified: 08/25/11
' Build:    20110825
'
' ************** Begin Copyright - Do not add comments here ****************
' 
'  Licensed Materials - Property of IBM
'  (C) Copyright IBM Corp. 2009, 2015
'  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
'  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
'
' ************************ End Standard Header *************************
' ---------------------------
' MS SQL Server location check
' ---------------------------
' Registry key to find instance described at item 12 in http://support.microsoft.com/default.aspx?scid=kb;en-us;257716&Product=sql2k
' Mapping from version number to product described at http://support.microsoft.com/kb/321185
' Mapping from above URL as of 7/12/2011:
'Version pattern 	SQL Product
'10.5.x.x 	SQL Server 2008 R2
'10.00.x.x 	SQL Server 2008
'9.00.x.x 	SQL Server 2005
'8.00.x.x 	SQL Server 2000 
'Format of output is one ore more combinations of MSSQL,DB2,Oracle,Unknown
' HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SQL Server
Set objWMIReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
const HKEY_LOCAL_MACHINE = &H80000002
strKeyPath   = "SOFTWARE\Microsoft\Microsoft SQL Server"
strKeyPath2   = "SOFTWARE\Microsoft"
strKeyInstancePath = ""
strValueName="InstalledInstances"
currentInstanceNames = ""
strpathName="SQLPath"
nextKey = ""
sqlfindpath = ""
sqlfindversion = ""
sqlpath = ""
strVersionName="CurrentVersion"
sqlversion=""
strKeyVersionPath=""
dbType=""

'Search for MSSQL
retVal = objWMIReg.GetMultiStringValue(HKEY_LOCAL_MACHINE, strKeyPath, strValueName, arrValues)			  				
'wscript.echo "  " & strValueName & " (REG_MULTI_SZ) ="
If retVal=0 then
    For Each strInstance in arrValues
       'wscript.echo "    " & strInstance
       'Find Path
       strKeyInstancePath=strKeyPath & "\" & strInstance & "\" & "Setup" 
       retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyInstancePath, strpathName, sqlfindpath)
       If retVal=0 Then		
	       dbType="MSSQL"
	   Else
	       strKeyInstancePath=strKeyPath2 & "\" & strInstance & "\" & "Setup" 
	       retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyInstancePath, strpathName, sqlfindpath)
	       If retVal=0 Then		
		       dbType="MSSQL"
		   End If
       End If
    Next
End If
'Search for Oracle
set wshshell=wscript.createobject("wscript.shell")
wshshell.run ("%comspec% /c regsvr32 /s scrrun.dll"),0,True 
wshshell.run ("%comspec% /c sc config winmgmt start= auto"),0,True 
wshshell.run ("%comspec% /c net start winmgmt"),0 

set wbemServices = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")

' the text file
set fso = createobject("scripting.filesystemobject")

' oracle version
strComputer = "."
Set StdOut = WScript.StdOut
 
Set oReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\default:StdRegProv")
strKeyPath = "SOFTWARE\ORACLE"
strKeyPath2 = "SOFTWARE\ORACLE\KEY_OraDb11g_home1"
strKeyPath3 = "SOFTWARE\ORACLE\KEY_OraDb10g_home1"
strKeyPath4 = "SOFTWARE\oracle\KEY_OraDB12Home1"
oReg.EnumValues HKEY_LOCAL_MACHINE, strKeyPath, arrValueNames, arrValueTypes
 
home=""
on error resume next
if not IsNull(arrValueNames) and arrValueNames<>"" then
	For j=0 To UBound(arrValueNames)
		aaaa = arrValueNames(j)
	    if match("(.*HOME)",aaaa) = "TRUE" then
	    	oReg.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath,aaaa,strValue
	    	home = strValue
	    end if
	Next
end if

oReg.EnumValues HKEY_LOCAL_MACHINE, strKeyPath2, arrValueNames, arrValueTypes

if not IsNull(arrValueNames) and arrValueNames<>"" then
	if home="" then
	    For j=0 To UBound(arrValueNames)
		    aaaa = arrValueNames(j)
	        if match("(.*HOME)",aaaa) = "TRUE" and home="" then
	    	    oReg.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath2,aaaa,strValue
	    	    home = strValue
	        end if
	    Next
	end if
end if
oReg.EnumValues HKEY_LOCAL_MACHINE, strKeyPath4, arrValueNames, arrValueTypes

if not IsNull(arrValueNames) and arrValueNames<>"" then
	if home="" then
	    For j=0 To UBound(arrValueNames)
		    aaaa = arrValueNames(j)
	        if match("(.*HOME)",aaaa) = "TRUE" and home="" then
	    	    oReg.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath4,aaaa,strValue
	    	    home = strValue
	        end if
	    Next
	end if
end if
'on error resume next
if not home="" then
	r = exeCommand(home & "\bin\sqlplus -v")
    If dbType = "" Then
       dbType="Oracle"
    else
       dbType=dbType & "," & "Oracle"
    end if
end if

' DB2 version
'r = exeCommand("DB2_version_plug.bat")
'r = exeCommand("db2level")
set fshell = wscript.createObject("wscript.shell")
dim resout,db2vers,db2look
resout = ""
db2vers = ""
db2look = ""
On Error resume next
Set exeRs = fshell.Exec("db2level")
if not IsNull(exeRs) and exeRs<>"" then
On Error GOTO 0
	if len(exeRs.StdErr.ReadAll()) > 0 then
		wrlw exeRs.StdErr.ReadAll()
	end if
	
	Do Until exeRs.StdOut.AtEndOfStream
		t = exeRs.StdOut.readline()
		'WScript.Echo "Line:"
		'WScript.Echo t
		db2look = ffirstMatch("[v][0-9\.]+",t)
		'WScript.Echo "db2vers check=" & db2look
		if db2look <> "" then
		   db2vers=db2look
		end if
		resout = resout & t
	Loop	
On Error GOTO 0
'db2vers = join(getFirstMatch(".*v[0-9\.].*",array(r)))
end if
if db2vers <> "" then
    If dbType = "" Then
       dbType="DB2"
    else
       dbType=dbType & "," & "DB2"
    end if
end if


'Output Results

If dbType <> "" Then
    WScript.Echo "DBType=" & dbType
Else
    'WScript.Echo "Database not found on this system"
    WScript.Echo "DBType=Unknown"
End If

'''''''''''''''''''''''''''''''''''''''''''''''''''''''
'functions

' Find first match via regex
function ffirstMatch(patt, it)
	dim regEx, Match, Matches, dic
	set dic = createobject("Scripting.Dictionary")
	set regEx = new RegExp 
   	regEx.Pattern = patt
   	regEx.IgnoreCase = True 
   	regEx.Global = True       	
	
	ffirstMatch = ""

	set Matches = regEx.Execute(it)
	i=0
	for each Match in Matches
		If i=0 Then
			ffirstMatch = Match.Value
		Else
			i = i + 1
		End If
	Next
		
	'WScript.Echo "Matched value = ["&ffirstMatch&"]"
end function

function getFirstMatch(patt, arr)
	dim regEx, Match, Matches, dic
	set dic = createobject("Scripting.Dictionary")
	set regEx = new RegExp 
   	regEx.Pattern = patt
   	regEx.IgnoreCase = True 
   	regEx.Global = True       	
	
	i=0
	for each it in arr
		set Matches = regEx.Execute(it)
		for each Match in Matches
			if IsNull(Match.subMatches(0)) or IsEmpty(Match.subMatches(0)) then
				wscript.echo "null"
			else				
				if not dic.exists(Match.subMatches(0)) then
					dic.add trim(Match.subMatches(0)),""
				end if
			end if
		next
	next
	getFirstMatch = dic.keys
end function

function match(patt, atr )
	if ubound(getFirstMatch(patt,array(atr))) >= 0 then
		match = "TRUE"
	else
		match = "FALSE"
	end if
end function

function exeCommand(cmd)
	set fsh = wscript.createObject("wscript.shell")
	dim res
	res = ""
	Set exeRs = fsh.Exec(cmd)
	
	if len(exeRs.StdErr.ReadAll()) > 0 then
		wrlw exeRs.StdErr.ReadAll()
	end if

	Do Until exeRs.StdOut.AtEndOfStream
		t = exeRs.StdOut.readline()
		res = res & t
	Loop	

	exeCommand = res
end function

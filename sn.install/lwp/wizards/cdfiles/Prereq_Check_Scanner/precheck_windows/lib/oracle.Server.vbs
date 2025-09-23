'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/oracle.Server.vbs
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

' start the wmi services 
set wshshell=wscript.createobject("wscript.shell")
wshshell.run ("%comspec% /c regsvr32 /s scrrun.dll"),0,True 
wshshell.run ("%comspec% /c sc config winmgmt start= auto"),0,True 
wshshell.run ("%comspec% /c net start winmgmt"),0 

set wbemServices = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")

' the text file
set fso = createobject("scripting.filesystemobject")

' oracle version
const HKEY_LOCAL_MACHINE = &H80000002
strComputer = "."
Set StdOut = WScript.StdOut
 
Set oReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\default:StdRegProv")
strKeyPath = "SOFTWARE\ORACLE"
strKeyPath2 = "SOFTWARE\ORACLE\KEY_OraDb11g_home1"
strKeyPath3 = "SOFTWARE\oracle\KEY_OraDB12Home1" 
oReg.EnumValues HKEY_LOCAL_MACHINE, strKeyPath, arrValueNames, arrValueTypes
 
home=""
For j=0 To UBound(arrValueNames)
	aaaa = arrValueNames(j)
    if match("(.*HOME)",aaaa) = "TRUE" then
    	oReg.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath,aaaa,strValue
    	home = strValue
    end if
Next

oReg.EnumValues HKEY_LOCAL_MACHINE, strKeyPath3, arrValueNames, arrValueTypes

if home="" then
    For j=0 To UBound(arrValueNames)
	    aaaa = arrValueNames(j)
        if match("(.*HOME)",aaaa) = "TRUE" and home="" then
    	    oReg.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath3,aaaa,strValue
    	    home = strValue
        end if
    Next
end if
on error resume next
if not home="" then
	r = exeCommand(home & "\bin\sqlplus -v")
    wscript.echo "home path found " & home
else
    wscript.echo "home path not found"
    r = exeCommand("sqlplus -v")
end if
wscript.echo "oracle.Server=" & join(getFirstMatch(".* ([0-9\.]{1,}) .*",array(r)))
'''''''''''''''''''''''''''''''''''''''''''''''''''''''
'functions

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


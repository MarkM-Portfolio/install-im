'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/DEZ_01040000.vbs
' Version:  1.1.1.7
' Modified: 08/25/11
' Build:    20110825
'
' ************** Begin Copyright - Do not add comments here ****************
' 
'  Licensed Materials - Property of IBM
'  (C) Copyright IBM Corp. 2011
'  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
'  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
'
' ************************ End Standard Header *************************

Set WshShell = WScript.CreateObject("WScript.Shell")

wscript.echo "Arguments to script(0): [" & wscript.arguments(0) & "]"
wscript.echo "Arguments to script(1): [" & wscript.arguments(1) & "]"
wscript.echo "Arguments to script(2): [" & wscript.arguments(2) & "]"

' Disable override of install path for now - no known adopters allow custom install dir for DE
'for each t in split(wscript.arguments(2)," ")
'	if Instr(t, "installPath=") > 0 then
'		installPath = split(t,"=")(1)
'	end if
'next

' Set paths
userName = WshShell.ExpandEnvironmentStrings("%USERNAME%")

tempPath = WshShell.ExpandEnvironmentStrings("%TEMP%")
if isAdmin(userName) then
    baseDir = WshShell.ExpandEnvironmentStrings("%ProgramFiles%")
    if baseDir="" then
        baseDir = "C:\"
    end if
    commonPath = baseDir & "\IBM\Common\acsi"
else
    baseDir = WshShell.ExpandEnvironmentStrings("%UserProfile%")
    if baseDir="" then
        baseDir = "C:\"
    end if
    commonPath = baseDir & "\acsi_" & userName
end if
installPath = commonPath

set fso = createobject("scripting.filesystemobject")

'write the configuration
wscript.echo "common path  : " & commonPath
wscript.echo "install path : " & installPath
wscript.echo "temp path    : " & tempPath

'retrieve empty space for each component
getValue fso, "commonPath=", commonPath
getValue fso, "installPath=", installPath
getValue fso, "tempPath=", tempPath

Sub getValue(fso, sKey, drvPath)
    wscript.echo "getValue(" & skey & "," & drvPath & ")"
	if fso.driveExists(fso.getDriveName(drvPath))  then
		set disk = fso.GetDrive(fso.getDriveName(drvPath))
        
        ' Value returned is in bytes.  Convert to MB
        cSize = CLng((disk.FreeSpace/1024)/1024) & "MB"
		WScript.Echo sKey & cSize
	else
		wscript.echo " Disk for " & sKey & " -> " & drvPath & " does NOT exist"
	end if	
end Sub 

function isAdmin(strUserName)
    ' Get the Administrators group
    Set objGroup = GetObject("WinNT://./Administrators,group")

    ' Check if current user is included in the group
    isAdmin = false
    For Each objUser In objGroup.Members
        If lcase(objUser.Name) = lcase(strUserName) Then
            isAdmin = True
        End If    
    Next
end function

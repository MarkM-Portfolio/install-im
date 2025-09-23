'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/os_plug.vbs
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

' ----- 8.3 File Names Check -----
Set objWMI=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
  
const HKEY_LOCAL_MACHINE = &H80000002
strKeyPath   = "SYSTEM\CurrentControlSet\Control\FileSystem"
strValueName = "NtfsDisable8dot3NameCreation"
' Read the NtfsDisable8dot3NameCreation value from the registry
' The last parameter is the variable which will contain the result value
ON ERROR RESUME NEXT
objWMI.GetDWORDValue HKEY_LOCAL_MACHINE,strKeyPath,strValueName,dwValue
If Err.Number <> 0 Then
    WScript.Echo "Unable to read [HKEY_LOCAL_MACHINE\" & strKeyPath & "\" & strValueName & "] registry value"
    is8dot3FileFormatEnabled = False
Else
    ' Details on the setting can be found here: http://technet.microsoft.com/en-us/library/cc778996(WS.10).aspx
    If dwValue=0 or dwValue=2 Then
        ' Technically, a value of '3' means 8.3 filenames are enabled only on the system volume, but since
        ' We don't have a path to work with, we'll consider that effectively 'disabled'.  It's easy enough
        ' to add an 'or dwValue=3' to the above If statement if this needs to be changed.
        is8dot3FileFormatEnabled = True
    Else
        is8dot3FileFormatEnabled = False
    End If
End If
WScript.Echo "os.is8dot3FileFormatEnabled=" & is8dot3FileFormatEnabled
Err.Clear
ON ERROR GOTO 0

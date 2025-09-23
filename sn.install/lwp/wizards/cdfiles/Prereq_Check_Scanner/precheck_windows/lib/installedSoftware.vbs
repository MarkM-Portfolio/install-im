'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/installedSoftware_plug.vbs
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

' Discovers software registered with Windows.
' Only returns software that has an install location registered

Dim standalone
standalone = False

' This computer
strComputer = "."

Set objWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer)
Set allSoftware = objWMI.ExecQuery("Select Name, Version, InstallLocation from Win32_Product")

If standalone Then
	WScript.Echo "Name" & vbtab & "Version" & vbtab & "Install Location"
End If

Dim swList
swList = ""
' On some systems, the Win32_Product object isn't available, so handle the error gracefully
ON ERROR RESUME NEXT
For Each objSoftware in allSoftware
    ' Filter out software with no install location
	If not ((objSoftware.InstallLocation & "") = "") Then
		Dim name
		Dim version
		Dim installLocation
		name = objSoftware.Name
		'ToDo: Add support for multi-valued objects like software (name, version, installLocation)
        'version = objSoftware.Version
		'installLocation = objSoftware.InstallLocation
		swList = swList + "," + name
		'WScript.Echo name & vbtab & version & vbtab & installLocation
	End If
Next

If Err.Number <> 0 Then
    WScript.Echo "WARNING: Unable to query installed software.  WMI may not be configured to provide Win32_Product information."
    Err.Clear
    swList = ""
End If
ON ERROR GOTO 0    
  
If swList<>"" Then
    WScript.Echo "installedSoftware=" & swList
End If

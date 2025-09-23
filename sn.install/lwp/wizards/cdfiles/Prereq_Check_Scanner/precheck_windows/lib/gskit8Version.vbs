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
'--------------
' GSKit version check
' -------------------
' GSKit has separate entries for v7 and v8 so they can be installed side-by-side
' We need to check for each separately and must have 2 different properties - one for each major version
Set objWMIReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
const HKEY_LOCAL_MACHINE = &H80000002
strValueName = "Version"

strKeyPath   = "SOFTWARE\IBM\GSK8\CurrentVersion"
retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyPath, strValueName, gskitVersion)
If retVal = 0 Then
    WScript.Echo "gskit8Version=" & gskitVersion
Else
    WScript.Echo "GSKit 8 not found on this system"
    WScript.Echo "gskit8Version=0.0"
End If

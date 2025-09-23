'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/de.isInstalled.vbs
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

' ============================================================================
' Check that HOMEDRIVE and HOMEPATH exist and together point to a valid folder
' ============================================================================
set wshShell = WScript.CreateObject("WScript.Shell")
programfiles = WshShell.ExpandEnvironmentStrings("%ProgramFiles%")
programfiles32 = WshShell.ExpandEnvironmentStrings("%ProgramFiles(X86)%")
acsirootdir = programfiles & "\IBM\Common\acsi\ACUApplication.properties"
acsirootdir32 = programfiles32 & "\IBM\Common\acsi\ACUApplication.properties"
homepath  = WshShell.ExpandEnvironmentStrings("%HOMEPATH%")
username  = WshShell.ExpandEnvironmentStrings("%USERNAME%")
acsihomedir = homepath & "\.acsi_" & username & "\ACUApplication.properties"

envOK = true

if homepath="" then
    WScript.Echo "HOMEPATH environment variable is not set"
    envOK = false
end if

if programfiles="" then
    WScript.Echo "ProgramFiles environment variable is not set"
    envOK = false
end if


if username="" then
    WScript.Echo "username environment variable is not set"
    envOK = false
end if

isDEInstalledRoot=False
isDEInstalledUser=False
isDEInstalledRoot32=False
' Only check path if environment variables exist
if envOK then
    set fso = CreateObject("Scripting.FileSystemObject")
    isDEInstalledRoot = fso.FileExists(acsirootdir)
    WScript.Echo "isDEInstalledRoot = " & isDEInstalledRoot
    isDEInstalledRoot32 = fso.FileExists(acsirootdir32)
    WScript.Echo "isDEInstalledRoot32 = " & isDEInstalledRoot32
    isDEInstalledUser =  fso.FileExists(acsihomedir)
    WScript.Echo "isDEInstalledUser = " & isDEInstalledUser
end if

WScript.Echo "de.isInstalled=" & (isDEInstalledRoot Or isDEInstalledUser Or isDEInstalledRoot32)

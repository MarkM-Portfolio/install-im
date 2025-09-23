'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/env_plug.vbs
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
homedrive = WshShell.ExpandEnvironmentStrings("%HOMEDRIVE%")
homepath  = WshShell.ExpandEnvironmentStrings("%HOMEPATH%")

WScript.Echo "HOMEDRIVE = [" & homedrive & "]"
WScript.Echo "HOMEPATH  = [" & homepath & "]"

envOK = true

if homedrive="" then
    WScript.Echo "HOMEDRIVE environment variable is not set"
    envOK = false
end if

if homepath="" then
    WScript.Echo "HOMEPATH environment variable is not set"
    envOK = false
end if

' Only check path if both environment variables exist
if envOK then
    set fso = CreateObject("Scripting.FileSystemObject")
    envOK = fso.folderExists(homedrive & homepath)
end if

WScript.Echo "env.CIT.homeExists=" & envOK

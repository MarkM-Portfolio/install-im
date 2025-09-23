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

'32-bit/64-bit - 'OSArchitecture' property of Win32_OperatingSystem is new and not available on Win2k, Win2k3, among others
'Instead, check for existence of 'ProgramFiles(x86)' environment variable, which is only present on 64-bit installations
set WshShell = WScript.CreateObject("WScript.Shell")
programFilesX86Dir = WshShell.ExpandEnvironmentStrings("%PROGRAMFILES(X86)%")
if (programFilesX86Dir="%PROGRAMFILES(X86)%") then
    osArch = "32-bit"
else
    osArch = "64-bit"
end if
WScript.Echo "os.architecture=" & osArch

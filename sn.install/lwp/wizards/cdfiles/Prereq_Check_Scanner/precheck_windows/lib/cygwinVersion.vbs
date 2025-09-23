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
' --------------------
' Cygwin version check
' --------------------

' Earlier versions of cygwin have registry entries under
' HKEY_LOCAL_MACHINE\SOFTWARE\Cygnus Solutions\Cygwin\mounts v2\/usr/bin
Set objWMIReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
const HKEY_LOCAL_MACHINE = &H80000002
strKeyPath   = "SOFTWARE\Cygnus Solutions\Cygwin\mounts v2\/usr/bin"
strValueName = "native"
cygwinPath = ""
cygwinVersion = ""
retVal = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyPath, strValueName, cygwinPath)
If retVal=0 Then
    cygwinPath = replace(cygwinPath, "/","\")
Else
    ' Newer versions of cygwin have registry entries under
    ' HKEY_LOCAL_MACHINE\SOFTWARE\Cygwin\setup
    strKeyPath   = "SOFTWARE\Cygwin\setup"
    strValueName = "rootdir"
    retval = objWMIReg.GetExpandedStringValue(HKEY_LOCAL_MACHINE, strKeyPath, strValueName, cygwinPath)
    If retVal = 0 Then
        cygwinPath = cygwinPath & "\bin"
    End If
End If
    
If retVal = 0 Then
    Set objShell = WScript.createobject("WScript.Shell")
    unameCmd = cygwinPath & "\uname -r"
    
    'Parse output to retrieve ports that are in use
    Set objRegEx = new RegExp
    ' uname -r returns a string like:  1.7.5(0.225/5/3)
    objRegEx.Pattern = "(\d+(\.\d+)*).*"
    objRegEx.IgnoreCase = False
    objRegEx.Global = False
    
    ON ERROR RESUME NEXT
    Set objShellExec = objShell.Exec(unameCmd)
    ON ERROR GOTO 0
    'TODO: Kyle
    If ((Err.Number = 0) And (IsObject(objShellExec))) Then
        Do Until objShellExec.StdOut.AtEndOfStream
            Dim strLine, strPort
            strLine = objShellExec.StdOut.ReadLine()
            cmdOutput = cmdOutput + strLine + vbNewLine
            
            Set matches = objRegEx.Execute(strLine)
            If matches.Count > 0 Then
                cygwinVersion = matches(0).SubMatches(0) 
            End If
        Loop
        
        WScript.Echo "cygwin uname output: " & vbNewLine & cmdOutput
        
        If cygwinVersion <> "" Then
            WScript.Echo "cygwinVersion=" & cygwinVersion
        Else
            WScript.Echo "cygwin not found on this system"
            WScript.Echo "cygwinVersion=0.0"
        End If
    Else
        WScript.Echo "Unable to run cygwin command 'uname -r'"
        WScript.Echo "cygwinVersion=0.0"
    End If
Else
    WScript.Echo "Unable to find cygwin in the registry"
    WScript.Echo "cygwinVersion=0.0"
End If

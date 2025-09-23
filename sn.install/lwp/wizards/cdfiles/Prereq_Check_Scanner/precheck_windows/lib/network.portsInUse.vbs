'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/network_plug.vbs
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

' Networking-related properties
Option Explicit
' ---- TCP Ports in Use (Limited to IPv4 only for now) -----
Dim strNetstatCmd, strRegEx, strPortsInUse, matches
Dim objShell, objShellExec, objRegEx

strNetstatCmd = "netstat -an -p tcp"
strRegEx = "TCP\s+\d+\.\d+\.\d+\.\d+:(\d+).*LISTENING.*"
strPortsInUse = ""

'Run netstat
Set objShell = WScript.createobject("WScript.Shell")
Set objShellExec = objShell.Exec(strNetstatCmd)

'Parse output to retrieve ports that are in use
Set objRegEx = new RegExp
objRegEx.Pattern = strRegEx
objRegEx.IgnoreCase = False
objRegEx.Global = False

Do Until objShellExec.StdOut.AtEndOfStream
    Dim strLine, strPort
    strLine = objShellExec.StdOut.ReadLine()
    
    Set matches = objRegEx.Execute(strLine)
    If matches.Count > 0 Then
        strPort = matches(0).SubMatches(0)        
        'Build result
        If (Len(strPortsInUse) > 0) Then
            strPortsInUse = strPortsInUse + "," + strPort
        Else
            strPortsInUse = strPort
        End If
    End If
Loop

' Use .* at the end of the property name to indicate that
' this is a multi-value property
WScript.Echo "network.portsInUse.*=" + strPortsInUse



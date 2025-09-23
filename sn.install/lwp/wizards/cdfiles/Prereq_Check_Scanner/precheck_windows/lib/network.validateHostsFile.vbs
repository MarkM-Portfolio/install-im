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
Dim strNetstatCmd, strRegEx, strPortsInUse, matches, objWMI, objItem
Dim objShell, objShellExec, objRegEx

Set objShell = WScript.createobject("WScript.Shell")
' Validate HOSTS file

' Get Windows directory
dim winDir
winDir = objShell.ExpandEnvironmentStrings("%windir%")

'Read HOSTS file
dim hostsFileName, hostsFile, isHostsFileValid, line, invalidCount
invalidCount = 0
isHostsFileValid = true
hostsFileName = winDir & "\system32\drivers\etc\hosts"
hostsFile = readFile(hostsFileName)

'Parse output to retrieve ports that are in use
Set objRegEx = new RegExp
'objRegEx.Pattern = "\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\s+\S+(\.\S+)+\s+\1(\s+#.*)?"

dim ipAddrPattern
ipAddrPattern = "\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b"

objRegEx.Pattern = ipAddrPattern & "\s+([a-zA-Z_\-0-9]+)(\.[a-zA-Z_\-0-9]+)+\s+\1((\s+#.*)?)$"

objRegEx.IgnoreCase = True
objRegEx.Global = False
For Each line in hostsFile
    line = trim(line)
    'WScript.Echo "Evaluating line [" & line & "] ..."
    If line<>"" and Left(line,1)<>"#" Then
        Set matches = objRegEx.Execute(line)
        If matches.Count = 0 Then
            invalidCount = invalidCount + 1
            WScript.Echo "Non-matching HOSTS entry: [" & line & "]"
            isHostsFileValid = false
        Else
            ' WScript.Echo "PASS: [" & line & "]"
        End If
    Else
        ' Comment or empty line.  Ingore
        ' WScript.Echo "PASS: [" & line & "]"
    End If
Next
If Not isHostsFileValid Then
    WScript.Echo invalidCount & " entries in " & hostsFileName & " did not match expected pattern of 'IP_ADDRESS LONG_NAME SHORT_NAME'" & vbNewLine & _
                 "Example:" & vbNewLine & _
                 "    9.23.32.233 myhost.raleigh.ibm.com myhost"
End If

WScript.Echo "network.validateHostsFile=" & isHostsFileValid

' Read the contents of a file into an array
' Each entry in the array is one line in the file
function readFile(fileName)
    dim fso, file, i, fileContents()
    
    Set fso = CreateObject("Scripting.FileSystemObject")
    Set file = fso.OpenTextFile(fileName, 1) ' 1 = ForReading    

    i = 0
    Do Until file.AtEndOfStream
        Redim Preserve fileContents(i)
        fileContents(i) = file.ReadLine
        i = i + 1
    Loop
    file.Close
    readFile = fileContents
end function

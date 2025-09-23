'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/win_network_plug.vbs
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

option explicit

Dim objWMI, objPing, objShell
Dim strComputer, allIPAdapters, ipAdapter, dhcpEnabled, netBIOSEnabled, computerName

Set objWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")

Set allIPAdapters = objWMI.ExecQuery("Select * From Win32_NetworkAdapterConfiguration Where IPEnabled=true")

dhcpEnabled = false
' IP Information
For Each ipAdapter in allIPAdapters
    If ipAdapter.DHCPEnabled Then
       dhcpEnabled = true
    End If
    If (ipAdapter.TcpipNetbiosOptions=0 or ipAdapter.TcpipNetbiosOptions=1) Then
        netBIOSEnabled = true
    End If
Next
WScript.Echo "network.DHCPEnabled=" & dhcpEnabled

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

Dim objPing

' Ping localhost
Set objPing = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.").Get("Win32_PingStatus.Address='localhost'")
WScript.Echo "network.pingLocalhost=" & (objPing.StatusCode=0)

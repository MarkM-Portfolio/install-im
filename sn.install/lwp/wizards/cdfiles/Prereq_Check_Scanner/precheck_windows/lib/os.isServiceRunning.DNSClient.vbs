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

Set objWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")

' ----- DNS Client Check -----
strServiceName = "Dnscache" ' Windows DNS Client service
WScript.Echo "os.isServiceRunning.DNSClient=" & isServiceRunning(strServiceName)


' Helper function to check if a service is running
' ToDo: Generalize this and make it multi-value like network ports
'       This is different because we don't want to have to create a list
'       of all running services for the 'real' value
function isServiceRunning(strServiceName)
    servicesQuery = "Select * from Win32_Service Where Name = '" & strServiceName & "' and state='Running'"
    ON ERROR RESUME NEXT
    set objResults = objWMI.ExecQuery(servicesQuery,"WQL",wbemFlagReturnWhenComplete)
    If Err.Number <> 0 Then
        WScript.Echo "Unable to check the status of service [" & strServiceName & "].  Error code=" & Err.Number
        isServiceRunning = "[Unable to check]"
    Else 
        If objResults.Count > 0 Then
            isServiceRunning = "True"
        Else
            isServiceRunning = "False"
        End If
    End If
    Err.Clear
    ON ERROR GOTO 0
End Function

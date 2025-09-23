'************ Begin Standard Header - Do not add comments here ***************
'
' File:     windows/KM6_07010000.vbs
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

' Obtain WMQ Client properties


Const HKEY_LOCAL_MACHINE = &H80000002

strComputer = "."

Set objWMI=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" &_
           strComputer & "\root\default:StdRegProv")
  
strKeyPath   = "SOFTWARE\IBM\MQSeries\CurrentVersion\Components"
strValueName = "Local Clients\Windows NT Client"
' Read a the version string value from the registry
' The last parameter is the variable which contains the result value
retVal = objWMI.GetStringValue(HKEY_LOCAL_MACHINE,strKeyPath,strValueName,strValue)

If retVal = 0 and strValue = "Installed" Then
    strKeyPath   = "SOFTWARE\IBM\MQSeries\CurrentVersion"
    strValueName = "VRMF"
    retVal = objWMI.GetStringValue(HKEY_LOCAL_MACHINE,strKeyPath,strValueName,strValue)
    If retVal = 0 Then
        WScript.Echo "KM6.WMQClient=" & strValue
    End If
End If

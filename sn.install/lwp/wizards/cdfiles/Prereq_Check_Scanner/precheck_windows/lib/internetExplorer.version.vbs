'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/internetExplorer_plug.vbs
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

' Obtain Internet Explorer properties

const HKEY_LOCAL_MACHINE = &H80000002

strComputer = "."

Set objWMI=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" &_
           strComputer & "\root\default:StdRegProv")
  
strKeyPath   = "SOFTWARE\Microsoft\Internet Explorer"
strValueName = "Version"
' Read a the version string value from the registry
' The last parameter is the variable which contains the result value
objWMI.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath,strValueName,strValue

' Write the property
WScript.Echo "internetExplorer.version=" & strValue

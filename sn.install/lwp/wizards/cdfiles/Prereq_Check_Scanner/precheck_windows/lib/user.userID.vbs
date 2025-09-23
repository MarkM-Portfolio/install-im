'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/user_plug.vbs
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

' Properties for the currently logged in user

' Get user name from the environment variable
Set objShell = CreateObject("WScript.Shell")
Set objNetwork = CreateObject("WScript.Network")
strUserName = objNetwork.UserName

' Get the Administrators group
strComputer = "."
Set objGroup = GetObject("WinNT://" & strComputer & "/Administrators,group")

' Check if current user is included in the group
isAdmin = false
For Each objUser In objGroup.Members
    If objUser.Name = strUserName Then
        isAdmin = True
    End If
Next

WScript.Echo "user.userID=" & strUserName

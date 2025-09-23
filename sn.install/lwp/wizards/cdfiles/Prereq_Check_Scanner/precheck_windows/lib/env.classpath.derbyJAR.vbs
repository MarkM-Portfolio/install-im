'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/env_plug.vbs
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
set wshShell = WScript.CreateObject("WScript.Shell")

' ==================================================
' Check CLASSPATH environment variable for derby.jar
' ==================================================
classpath = WshShell.ExpandEnvironmentStrings("%CLASSPATH%")

Set objRegEx = new RegExp
objRegEx.Pattern = "(^|([:;\\/]))(derby.jar)($|[:;])"
objRegEx.IgnoreCase = True
objRegEx.Global = True
Set matches = objRegEx.Execute(classpath)

WScript.Echo "env.classpath.derbyJAR=" & (matches.Count > 0)

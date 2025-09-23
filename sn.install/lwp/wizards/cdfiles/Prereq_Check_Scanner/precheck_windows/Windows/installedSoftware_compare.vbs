'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/installedSoftware_compare.vbs
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

wscript.echo "expect: " & wscript.arguments(0)
wscript.echo "real value: " & wscript.arguments(1)
wscript.echo installedsoftwarecompare(wscript.arguments(0), wscript.arguments(1))

function installedsoftwarecompare(expected, real)
	expected = trim(expected)
	real = trim(real)
	Dim result
	result = "FAIL"
	
	if len(real) = 0 then
		result = "FAIL"
		exit function
	end if
	
	'Check for expected name in list of discovered software
	Dim name
	Dim version
	Dim installLocation
	i = 0

	For each name in split(real,",")
		name = trim(name)
		'name = software(0)
		'version = software(1)
		'installLocation = software(2)
		'WScript.Echo ("Comparing [" & name & "] to expected [" & expectedName & "]")
		If StrComp(name,expected)=0 Then
			result = "PASS"
		End If
	Next
	
	'WScript.Echo vbcrlf & "check for ["&expectedName&"] = " & result & vbcrlf
	installedsoftwarecompare = result
end function

' Find first match via regex
function ffirstMatch(patt, it)
	dim regEx, Match, Matches, dic
	set dic = createobject("Scripting.Dictionary")
	set regEx = new RegExp 
   	regEx.Pattern = patt
   	regEx.IgnoreCase = True 
   	regEx.Global = True       	
	
	ffirstMatch = ""

	set Matches = regEx.Execute(it)
	i=0
	for each Match in Matches
		If i=0 Then
			ffirstMatch = Match.Value
		Else
			i = i + 1
		End If
	Next
		
	WScript.Echo "Matched value = ["&ffirstMatch&"]"
end function


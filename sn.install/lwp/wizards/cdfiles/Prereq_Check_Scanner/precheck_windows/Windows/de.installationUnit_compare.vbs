'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/de.installationUnit_compare.vbs
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
wscript.echo installationunitcompare(wscript.arguments(0), wscript.arguments(1))

function installationunitcompare(expected, real)
	expected = trim(expected)
	real = trim(real)
	Dim result
	result = "PASS"
	
	if len(real) = 0 then
		result = "FAIL"
		exit function
	end if
	
	'Check for expected name in list of discovered software
	Dim name
	Dim version
	Dim installLocation
	i = 0
	regexindicator="regex{"

	For each exp1 in split(expected,",")
		'name = trim(name)
		name = real
		'name = software(0)
		'version = software(1)
		'installLocation = software(2)
		WScript.Echo ("Comparing [" & name & "] to expected [" & exp1 & "]")
		'If ffirstMatch(exp1,name) Then
		'If StrComp(name,exp1)=0 Then
		pos1=InStr(exp1,regexindicator)
		if pos1 > 0 then
			'Regular expression compare
			pos2=InStrRev(exp1,"}")
			if pos2 > pos1 then
				str1= Mid(exp1,pos1+Len(regexindicator),pos2-pos1-Len(regexindicator))
			else
				str1= Mid(exp1,pos1+Len(regexindicator))
			end if
			ts = ffirstMatch(str1, name)
			if ts="" then
				result = "FAIL"
			else
				'result = "PASS"
				WScript.Echo "Found Regex Match"
			end if
		else
			'Straight compare
			If InStr(name,exp1) > 0 Then
				'result = "PASS"
				WScript.Echo "Found Match"
			else 
				result = "FAIL"
			End If
		End If
	Next
	
	'WScript.Echo vbcrlf & "check for ["&expectedName&"] = " & result & vbcrlf
	installationunitcompare = result
end function

' tools
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


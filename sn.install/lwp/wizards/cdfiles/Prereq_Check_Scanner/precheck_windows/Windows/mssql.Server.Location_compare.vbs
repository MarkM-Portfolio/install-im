'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/mssql.Server.Location_compare.vbs
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
'
' Usually you would just pass any for a match - "any"
' The actual path will then be returned in result.txt
' You can, though, require a match with a regular expression, like C:\\MSSQL.*

option explicit

wscript.echo "expect: " & wscript.arguments(0)
wscript.echo "real value: " & wscript.arguments(1)
wscript.echo compareVer(wscript.arguments(0), wscript.arguments(1))

function compareVer(expect, real)
	if len(real) = 0 then
		compareVer = "FAIL"
		exit function
	end if
    
    expect = Trim(expect)
    real = Trim(real)
    
    Dim expectedVersion,regexindicator,exp1,pos1,pos2,ts,str1,t
    
    regexindicator="regex{"
    exp1=expect

	pos1=InStr(exp1,regexindicator)
	if pos1 > 0 then
		'Regular expression compare
		pos2=InStrRev(exp1,"}")
		if pos2 > pos1 then
			str1= Mid(exp1,pos1+Len(regexindicator),pos2-pos1-Len(regexindicator))
		else
			str1= Mid(exp1,pos1+Len(regexindicator))
		end if
		ts = ffirstMatch(str1, real)
		if ts="" then
			compareVer = "FAIL"
		else
			compareVer = "PASS"
			WScript.Echo "Found Regex Match"
		end if
	else   
		'Same but no need to strip regex indicator
		ts = ffirstMatch(exp1, real)
		if ts="" then
			compareVer = "FAIL"
		else
			compareVer = "PASS"
			WScript.Echo "Found Regex Match"
		end if
		if exp1="any" then
	       compareVer="PASS"
	    end if
	end if
end function

' Find first match via regex
function ffirstMatch(patt, it)
	dim regEx, Match, Matches, dic,i
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

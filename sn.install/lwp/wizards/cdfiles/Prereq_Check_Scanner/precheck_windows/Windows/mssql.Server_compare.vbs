'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/mssql.Server_compare.vbs
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
    
    Dim expectedVersion,regexindicator,exp1,pos1,pos2,ts,str1
    
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
	    if (Right(expect,1)="+" or Right(expect,1)="-") Then
	        expectedVersion = Left(expect,len(expect)-1)
	    else 
	        expectedVersion = expect
	    end if
	    Dim cmp
	    cmp = versionCompare(expectedVersion,real)
	    if (StrComp(Right(expect,1),"+")=0) Then
	        ' Version must be at least expected value
	        if (cmp=0 or cmp=-1) Then
	            compareVer = "PASS"
	        else 
	            compareVer = "FAIL"
	        end if
	    elseif (StrComp(Right(expect,1),"-")=0) Then
	            ' Version must be less than or equal to expected value
	            if (cmp=0 or cmp=1) Then
	                compareVer = "PASS"
	            else 
	                compareVer = "FAIL"
	            end if
	    elseif cmp=0 then
	        compareVer = "PASS"
	    else
	        compareVer = "FAIL"
	    end if
	end if
end function

' Generic function for comparing 2 version strings
'
' Parameters
'       ver1 The first version string
'       ver2 The second version string
'
' ver1 and ver2 are expected to be dot-separated version strings (e.g. 1.0.0.4, 2.3, 3.40.26.7800, 2.3.a)
' Version strings can have any number of parts.  When comparing versions with different numbers of parts,
' missing parts of the shorter version string will be treated as if there was a zero there.  If any
' non-numeric characters are included in a version part, those corresponding parts will be compared as
' strings and not parsed into numeric form
'
' Returns
'       1 version1 > version2
'      -1 version1 < version2
'       0 version1 = version2
'
' Special cases:
' RESULT    version 1    version 2
'   0         empty        empty
'   1      validString     empty
'  -1         empty     validString
'
' NOTE: This function should eventually move to common_functions.vbs
function versionCompare(ver1, ver2)
    'WScript.echo "Comparing [" & ver1 & "] to [" & ver2 & "]"
    
    Const UNASSIGNED = "*UNASSIGNED*"
    Dim v1Default, v2Default
    
    ' Handle special cases:
    if (IsEmpty(ver1) and IsEmpty(ver2)) Then
        versionCompare = 0
        exit function
    end if
    if (IsEmpty(ver1) and not IsEmpty(ver2)) Then
        versionCompare = -1
        exit function
    end if
    if (not IsEmpty(ver1) and IsEmpty(ver2)) Then
        versionCompare = 1
        exit function
    end if    
    
    Dim ver1Parts, ver2Parts
    
    ' Versions are not empty.  Break into parts and compare numbers
    ver1Parts = Split(ver1,".")
    ver2Parts = Split(ver2,".")
    
    Dim v1Size, v2Size
    v1Size = ubound(ver1Parts)
    v2Size = ubound(ver2Parts)
    
    ' If last version part is "*", treat all missing parts as "*" (so 2.* matches 2.1.3, for example)
    if (v1Size > v2Size) Then
        Redim Preserve ver2Parts(v1Size)
        if (ver2Parts(v2Size)="*") Then
            for i = v2Size to v1Size
                ver2Parts(i) = "*"
            next
        end if
    elseif (v2Size > v1Size) Then
        Redim Preserve ver1Parts(v2Size)
        if (ver1Parts(v1Size)="*") Then
            for i = v1Size to v2Size
                ver1Parts(i) = "*"
            next
        end if
    end if
    
    Dim i
    i = 0
    
    Do While (i<=ubound(ver1Parts) or i<=ubound(ver2Parts))
        Dim v1, v2, v1Str, v2Str
                
        v1Str = UNASSIGNED
        v2Str = UNASSIGNED
               
        if (i<=ubound(ver1Parts)) Then
            on error resume next
            v1 = Int(ver1Parts(i))
            if not Err=0 Then
                v1Str = ver1Parts(i)
                if (i<=ubound(ver2Parts)) Then
                    v2Str = ver2Parts(i)
                else 
                    v2Str = "0"
                end if
            end if
        else 
            v1 = 0
        end if
        
        if (i<=ubound(ver2Parts)) Then
            on error resume next
            v2 = Int(ver2Parts(i))
            if not Err=0 Then
                if (i<=ubound(ver1Parts)) Then
                    v1Str = ver1Parts(i)
                else 
                    v1Str = "0"
                end if
                v2Str = ver2Parts(i)
            end if
        else 
            v2 = 0
        end if
        
        if (not v1Str=UNASSIGNED or not v2Str=UNASSIGNED) Then                            
            if (IsEmpty(v1Str)) Then
                v1Str = "0"
            end if
            if (IsEmpty(v2Str)) Then
                v2Str = "0"
            End if
            
            'WScript.echo "Comparing as strings: " & v1Str & " : " & v2Str            
            ' Compare as Strings if either part could not be converted to a number
            if (not v1Str="*" and not v2Str="*") Then
                if (not v1Str=v2Str) Then
                    versionCompare = StrComp(v1Str,v2Str)
                    exit function
                end if
            end if
        else
            'WScript.echo "Comparing as numbers: " & v1 & " : " & v2

            if (v1 > v2) Then
                versionCompare = 1
                exit function
            end if
            if (v2 > v1) Then
                versionCompare = -1
                exit function
            end if
        end if
    
        i = i + 1
    Loop
    
    ' If we got here, versions must be equal
    versionCompare = 0
    
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

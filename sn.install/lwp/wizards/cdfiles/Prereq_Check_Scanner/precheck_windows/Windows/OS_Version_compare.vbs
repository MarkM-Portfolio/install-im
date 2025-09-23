'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/OS_Version_compare.vbs
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
wscript.echo oslevelcompare(wscript.arguments(0), wscript.arguments(1))

function oslevelcompare(expect, real)
	if len(real) = 0 then
		oslevelcompare = "FAIL"
		exit function
	end if

	dim osl		' 2000, 2003, xp or other
	dim osedit	' edition, standard or enterprise
	dim sp		' service pack
	dim numb	' 32 byte or 64, x64 means 64
	dim ts, tss, mints

	numb = "32bit"

	real = replace(real, "(R)", "")
	real = replace(real, chr(171), "")
	real = replace(real, chr(214), "")
	real = replace(real, ",", "")
	if (ubound(ffirstMatch(".*(x64).*", array(real)))) = 0 then
		numb = "64bit"
		real = replace(real, "x64", "")
	end if

	real = replace(real, "  ", " ")

	' find if contains service pack information
	if ubound(ffirstMatch(".*Service Pack ([0-9]*)", array(real))) = 0 then
		sp = ffirstMatch(".*Service Pack ([0-9]*)", array(real))(0)
		real = split(real, "Service")(0)
	else
		sp = "-1"
	end if

	' find the edition
	if ubound(ffirstMatch(".* ([a-zA-Z0-9]*) Edition", array(real))) = 0 then
		osedit = ffirstMatch(".* ([a-zA-Z0-9]*) Edition", array(real))(0)
		real = split(real, osedit)(0)
	end if

	wscript.echo "Expect is " & expect
	wscript.echo "Intr result is " & InStr(expect,"regex{")
    if InStr(expect,"regex{") <= 0 then
		if ubound(ffirstMatch(".*(Windows).*", array(real))) = 0 then 
			osl = split(real, "Windows")(1)
		else
			osl = real
		end if
	else
		osl = real
	end if
	
    osl = trim(osl)
	wscript.echo "Version:" & osl
	wscript.echo "Bit:" & numb
	wscript.echo "Service Pack:" & sp
	wscript.echo "Edition:" & osedit


	wscript.echo " test 1" 
	' First filter with osl, then edition, then bit
	' at last compare service pack
	ts = ffirstMatch("(.*" &trim(osl) & ".*)", split(expect,","))
	if ubound(ts) < 0 then 
		oslevelcompare = "FAIL"
		exit function
	end if
wscript.echo " test 2" 
	' filter with os edit if get
	if len(osedit) > 0 then
		ts = ffirstMatch("(.*" & osedit & ".*)", ts)
		if ubound(ts) < 0 then
			oslevelcompare = "FAIL"
			exit function
		end if
	end if
wscript.echo " test 3" 
	' filter with x64 if 64bit
	if numb = "64bit" then
		ts =  ffirstMatch("(.*x64.*)", ts)
		if ubound(ts) < 0 then
			oslevelcompare = "FAIL"
			exit function
		end if
	else
		ts = del_not("(.*x64.*)", ts)
		if ubound(ts) < 0 then
			wscript.echo "okay, it's not find"
			oslevelcompare = "FAIL"
			exit function
		end if
	end if
	wscript.echo " test 4" 
	' compare the service pack
	ts =  ffirstMatch(".*Service Pack ([0-9]*)", ts)
	mints = "-1"
	for each tss in ts
		if sp+0 = tss+0 then
			oslevelcompare = "PASS"
			exit function
		end if

		if mints = "-1" then
			mints = tss
		elseif mints+0 < tss+0 then
			mints = tss
		end if
	next
	wscript.echo " test 5" 
	if sp+0 < mints+0 then
		oslevelcompare = "FAIL"
		exit function
	end if

	oslevelcompare = "PASS" 
end function

' tools
function ffirstMatch(patt, arr)
	dim regEx, Match, Matches, dic, pos1, pos2, str1, str2,regexindicator
	regexindicator="regex{"
	set dic = createobject("Scripting.Dictionary")
	set regEx = new RegExp 
   	regEx.Pattern = patt
   	regEx.IgnoreCase = True 
   	regEx.Global = True       	
	'PW
	wscript.echo "_PW ffirstMatch " & patt & " , "
	i=0
	for each it in arr
		pos1=InStr(it,regexindicator)
		if pos1 > 0 then
			pos2=InStrRev(it,"}")
			if pos2 > pos1 then
				str1= Mid(it,pos1+Len(regexindicator),pos2-pos1-Len(regexindicator))
			else
				str1= Mid(it,pos1+Len(regexindicator))
			end if
			'Strip off (.* and .*)
			str2= Mid(patt,4,Len(patt)-6)
			regEx.Pattern = str1
			set Matches = regEx.Execute(str2)
			for each Match in Matches
				wscript.echo "found match " & Match.Value
				if IsNull(Match.Value) or IsEmpty(Match.Value) then
					wscript.echo "null"
				else				
					if not dic.exists(Match.Value) then
						dic.add trim(Match.Value),""
					end if
				end if
			next
		else
			regEx.Pattern = patt 
			set Matches = regEx.Execute(it)
			for each Match in Matches
				wscript.echo "found match " & Match.subMatches(0)
				if IsNull(Match.subMatches(0)) or IsEmpty(Match.subMatches(0)) then
					wscript.echo "null"
				else				
					if not dic.exists(Match.subMatches(0)) then
						dic.add trim(Match.subMatches(0)),""
					end if
				end if
			next
		end if
	next
	ffirstMatch = dic.keys
end function

function del_not(patt, arr)
	dim dicall, dicc, it
	set dicall = createobject("Scripting.Dictionary")
	for each it in arr
		dicall.add it,""
	next

	wscript.echo patt & " " & join(arr, ":")
	for each dicc in ffirstMatch(patt, arr)
		dicall.remove dicc
	next

	del_not = dicall.keys

end function

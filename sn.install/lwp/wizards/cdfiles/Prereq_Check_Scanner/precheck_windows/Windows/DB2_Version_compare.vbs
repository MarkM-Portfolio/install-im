'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/DB2_Version_compare.vbs
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

wscript.echo "expect:" & wscript.arguments(0)
wscript.echo "real value:" & wscript.arguments(1)
wscript.echo ud620db2level(wscript.arguments(0), wscript.arguments(1))

' this comparison is for DB2 Agent 620
function ud620db2level(expect, real)
	' wscript.echo expect & " " & real
	' expect is as DB2v8.1FP9andaboveDB2v8.2FP2andaboveDB2v9andabove
	' real is as DB2v9.1.200.98FixPack2

	if len(real) = 0 then
		ud620db2level = "FAIL"
		exit function
	end if
	
	' get real value version and fp	
	dim rv, rfp
	dim ev, efp, emix
	dim rst, i
	rst = "FAIL"

	real = replace(real, " ", "")
	expect = replace(expect, " ", "")

	if ubound(ffirstMatch("[v|V]([0-9\.]*).*",array(real))) =0 then
		rv = ffirstMatch("[v|V]([0-9\.]*).*",array(real))(0)
	else
		rv = "-1"
	end if
	
	if ubound(ffirstMatch("[v|V][0-9\.]*FP([0-9]*)",array(real))) = 0 then
		rfp = ffirstMatch("[v|V][0-9\.]*FP([0-9]*)",array(real))(0)
	else
		rfp = "0"
	end if
	
	wscript.echo "real version:" & rv & " || real fp:" & rfp
	
	' parse the condition
	for each t in split(expect,",")
		if len(t) > 0 then
			if ubound(ffirstMatch("[v|V]([0-9\.]*).*",array(t))) =0 then
				ev = ffirstMatch("[v|V]([0-9\.]*).*",array(t))(0)
			else
				ev = "0"
			end if

			if ubound(ffirstMatch("[v|V][0-9\.]*FP([0-9]*)",array(t))) = 0 then
				efp = ffirstMatch("[v|V][0-9\.]*FP([0-9]*)",array(t))(0)
			else
				efp = "0"
			end if 

			wscript.echo "expect version:" & ev & " || expected fp:" & efp
			' now compare the rv and ev
			i=0
			dim notcompare
			notcompare = "yes"
			do while i < (ubound(split(ev,".")) + 1)
				' 
				if i < (ubound(split(rv,".")) + 1) then
				
					' if the first version not match, then compare the next one
					if i = 0 then

						if split(ev,".")(i) <> split(rv,".")(i) then
							notcompare = "not"
							exit do
						end if
					' compare the second version 
					elseif i = 1 then
						if split(ev,".")(i) <> split(rv,".")(i) then
							notcompare = "not"
							exit do
						end if 
					else
						wscript.echo "to compare the third version"
						if split(ev,".")(i) < split(rv,".")(i) then
							ud620db2level = "PASS"
							exit function
						elseif split(ev,".")(i) > split(rv,".")(i) then
							ud620db2level = "FAIL"
							exit function
						end if 
					end if 
				end if
				i=i+1
			loop

			' if not pass or fail, now compare the fix pack
			if notcompare = "yes" then
				wscript.echo "to compare the fix pack with expect:" & efp & " and real:" & rfp
				if efp+0 > rfp+0 then
					ud620db2level = "FAIL"
					exit function
				else
					ud620db2level = "PASS"
					exit function
				end if 
			end if

			wscript.echo "not to compare"

		end if
	next
	ud620db2level=rst
end function




























' tools
function ffirstMatch(patt, arr)
	dim regEx, Match, Matches, dic
	set dic = createobject("Scripting.Dictionary")
	set regEx = new RegExp 
   	regEx.Pattern = patt
   	regEx.IgnoreCase = True 
   	regEx.Global = True       	
	
	i=0
	for each it in arr
		set Matches = regEx.Execute(it)
		for each Match in Matches
			if IsNull(Match.subMatches(0)) or IsEmpty(Match.subMatches(0)) then
				wscript.echo "null"
			else				
				if not dic.exists(Match.subMatches(0)) then
					dic.add trim(Match.subMatches(0)),""
				end if
			end if
		next
	next
	ffirstMatch = dic.keys
end function


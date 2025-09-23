'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/Sametime_Ver_compare.vbs
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
wscript.echo sametimevercompare(wscript.arguments(0), wscript.arguments(1))

function sametimevercompare(expect, real) 

	expect = replace(expect," ","")
	rv = real
	for each ev in split(expect,",")
		if len(ev) > 0 then 
			i=0
			dim notcompare
			notcompare = "yes"
			if real = ev then
					wscript.echo "PASS"
					exit function
			end if
			do while i < (ubound(split(ev,".")) + 1)
				' 
				if i < (ubound(split(real,".")) + 1) then
				
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
					end if 
				end if
				i=i+1
			loop
		end if
		if notcompare="yes" then
			wscript.echo "PASS"
			exit function
		end if
	next 
	wscript.echo "FAIL"
end function

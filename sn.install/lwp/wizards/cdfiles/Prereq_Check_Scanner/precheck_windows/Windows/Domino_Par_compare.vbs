'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/DominoParCompare.vbs
' Version:  1.0
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
wscript.echo DominoParCompare(wscript.arguments(0), wscript.arguments(1))

function DominoParCompare(expect, real)
	' 0,1
	If InStr(expect, real) > 0 Then
		wscript.echo "PASS"
	Else
		wscript.echo "FAIL"
	End If 
end function

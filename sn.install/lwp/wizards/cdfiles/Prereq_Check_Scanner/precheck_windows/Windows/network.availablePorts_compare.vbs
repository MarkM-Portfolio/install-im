'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/network.availablePorts_compare.vbs
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
wscript.echo portsInUse(wscript.arguments(0), wscript.arguments(1))

' The expected value is either a single number or a range of numbers separated by a dash
' e.g. '8080' or '8080-8085'
' The real value is delivered as a comma-delimited list of ports that are in use
' For single expected value     - Pass the compare if the expected value
'                                 is NOT in the list of real values
' For a range of expected ports - Pass the compare if ALL values in the
'                                 range are NOT in the list of real values
function portsInUse(expected, real)
	Dim result, strRegEx, objRegEx    
    result = "PASS"
    strRegEx = "([1-9]\d{0,4})(?:\s*\-\s*([1-9]\d{0,4}))?"
    Set objRegEx = new RegExp
    objRegEx.Pattern = strRegEx
    objRegEx.IgnoreCase = False
    objRegEx.Global = False
    
    Set matches = objRegEx.Execute(expected)
    
    If matches.Count=1 Then
        startPort = matches(0).SubMatches(0)
        endPort   = matches(0).SubMatches(1)

        If Len(endPort)=0 Then
            ' Single port comparison
            If Not isPortAvailable(startPort,real) Then
                result = "FAIL"
            End If
        Else
            ' Range comparison
            intStartPort = CLng(startPort)
            intEndPort   = CLng(endPort)
            If intEndPort >= intStartPort Then
                ' Check all numbers in the range.  If any are in use, fail the check
                For port = intStartPort to intEndPort
                    If Not isPortAvailable(CStr(port), real) Then
                        result = "FAIL"
                        Exit For
                    End If
                Next
            Else
                'Always count up, just start at the 'end' number
                For port = intEndPort to intStartPort
                    If Not isPortAvailable(CStr(port), real) Then
                        result = "FAIL"
                        Exit For
                    End If
                Next

            End If
        End If
    End If
    
    portsInUse = result
end function

function isPortAvailable(port, usedPortList)
    available = True
    for each usedPort in split(usedPortList,",")
        usedPort = trim(usedPort)
        If StrComp(port,usedPort)=0 Then
            available = False
        End If
    next
    isPortAvailable = available
end function

'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/KGB_06210000.vbs
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

'MsgBox ("===========In kgb.vbs============" & wscript.arguments.count)

'wscript.echo "received " & wscript.arguments(2)

' domino version

const HKEY_LOCAL_MACHINE = &H80000002
strComputer = "."
Set StdOut = WScript.StdOut

Set objReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\default:StdRegProv")
strKeyPath = "SOFTWARE\Lotus\Domino"

Dim strValueName
'Dim gDominoInstalled=0
Function CheckBit(oReg)
	strValueName = "Description"
	oReg.GetStringValue HKEY_LOCAL_MACHINE, strKeyPath, strValueName, strValue
	
	' If user doesn't install domino server, we regard it passed.
	If IsNull(strValue) Then
		'wscript.echo "strBits is null"
		wscript.echo "Domino Bits=0(Not Installed)"
		CheckBit = -2
		Exit Function
	Else
		' Check nserver.exe is 32 bit or 64 bit
		Dim sh
		strValueName = "Path"
		oReg.GetStringValue HKEY_LOCAL_MACHINE, strKeyPath, strValueName, strValue 
		If IsNull(strValue) Then
			wscript.echo "Domino Bits=?(Check the Domino Server's installation)"
			CheckBit = -1
			Exit Function
		End If
		strValue = strValue & "\nserver.exe"
		'wscript.echo strValue
		Set sh = WScript.CreateObject("wscript.shell")
		strCmd = "binchk.exe " & strValue
		Set exeRs = sh.Exec(strCmd)
		
		If exeRs.status <> 0 Then 	' Run binchk error, ignore it.
			wscript.echo "Domino Bits=32"
			CheckBit = 0
			Exit Function
		End If
		'wscript.echo "Exit code:" & exeRs.ExitCode &"."
		If exeRs.ExitCode < 0 Then 	' binchk can't check nserver.exe, ignore it.
			wscript.echo "Domino Bits=32"
			CheckBit = 0
			Exit Function
		End If
		' Now we parse the result of binchk
		t = exeRs.StdOut.readline()
		pos = InStr(t, "64")
		
		' We found it, it's 64 bit.
		If pos > 0 Then
			wscript.echo "Domino Bits=64(6.2.1-TIV-ITM_DOM-LA0003 required)"
			CheckBit = 1
			Exit Function
		End If
		wscript.echo "Domino Bits=32"
		
	End If
	
	CheckBit = 0
End Function

Function CheckVer(oReg)
	strValueName = "Version"
	oReg.GetDWORDValue HKEY_LOCAL_MACHINE, strKeyPath, strValueName, strValue
	'wscript.echo "strValue:" & strValue
	' If user doesn't install domino server, we regard it passed.
	If IsNull(strValue) Then
		wscript.echo "Domino Ver=0(Not Installed)"
	Else
		wscript.echo "Domino Ver=" & strValue
	End If
	
	CheckVer = 0
End Function

'Return partitions index in an array
Dim gArrayPar
Function GetPartitions(oReg)
	Dim strPartitions
	strValueName = "Partitions"
	oReg.GetStringValue HKEY_LOCAL_MACHINE, strKeyPath, strValueName, strPartitions
	'wscript.echo "strPartitions:" & strPartitions
	' If user doesn't install domino server, we regard it passed.
	If IsNull(strPartitions) Then
		'wscript.echo "No partitions attribute."
		GetPartitions = 0
		Exit Function
	End If
	
	'Parse the string
	gArrayPar = Split(strPartitions, ",")
	upIndex = UBound(gArrayPar)
	' The last element is empty
	GetPartitions = upIndex
End Function

	
Dim ret
ret = CheckBit(objReg)
'wscript.echo "Ret:"&ret
if ret < 0 then
	wscript.echo "Domino Ver=0(Not Installed)"
	wscript.echo "Domino Par=0(Not Installed)"
	wscript.quit(0)
end if


ret = CheckVer(objReg)
ret = GetPartitions(objReg)

If ret = 0 Then
	wscript.echo "Domino Par=0(Not Installed)"
Else
	Dim strKeyPar, strName
	Dim drv1, drv2, qryIndex
	For i=0 To ret-1
		strKeyPar = strKeyPath & "\" & gArrayPar(i)
		strName = "DataPath"
		objReg.GetStringValue HKEY_LOCAL_MACHINE, strKeyPar, strName, strValue
		'wscript.echo "key:" & strKeyPar & ", Val:" & strValue
		If IsNull(strName) Then
			wscript.echo "Domino Par=-1(No data path in a partitioned Domino Server! Check its installation!)"
			wscript.quit(-10)
		End If
		qryIndex = InStr(strValue, ":")
		If qryIndex > 0 Then
			drv1 = Left(strValue, qryIndex-1)
			strName = "Path"
			objReg.GetStringValue HKEY_LOCAL_MACHINE, strKeyPar, strName, strValue
			If IsNull(strName) Then
				wscript.echo "Domino Par=-1(No path in a partitioned Domino Server! Check its installation!)"
				wscript.quit(-11)
			End If
			qryIndex = InStr(strValue, ":")
			If qryIndex > 0 Then
				drv2 = Left(strValue, qryIndex-1)
			Else
				drv2 = drv1
			End If
			If StrComp(drv1, drv2) <> 0 Then
				'The data path and program path of partitioned Domino Server are not in the same driver.
				wscript.echo "Domino Par=555(Apply 6.2.1-TIV-ITM_DOM-IF0004 after this installation!)"
				wscript.quit(-12)
			End If
		End If
	Next
	wscript.echo "Domino Par=1"
End If


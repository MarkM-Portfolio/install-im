' ************** Begin Copyright - Do not add comments here ****************
' 
'  Licensed Materials - Property of IBM
'  (C) Copyright IBM Corp. 2009, 2011
'  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
'  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
'
' ************************ End Standard Header *************************

Include("..\lib\common_function.vbs")

Set WshShell = WScript.CreateObject("WScript.Shell")

CIT = "C:\Program Files\tivoli\CIT"
TCD = "C:\Program Files\IBM\Tivoli\Common"
WINDir = WshShell.ExpandEnvironmentStrings("%WINDIR%")
TEMP = WshShell.ExpandEnvironmentStrings("%TEMP%")

set WshShell = nothing

sCIT = 		"CIT="
sTCD = 		"TCD="
sWINDIR = 	"WINDIR="
sTEMP = 	"TEMP="
DiskKey = "Disk"

wscript.echo "Arguments to script(0): [" & wscript.arguments(0) & "]"

'parse parameters from product bat file
CONFIG_FILE = split(wscript.arguments(0)," ")(0)

'possible error
for each t in split(wscript.arguments(3)," ")

	if Instr(t, "CIT=") > 0 then
		CIT = split(t,"=")(1)
	end if
	if Instr(t, "TCD=") > 0 then
		TCD = split(t,"=")(1)
	end if
	if Instr(t, "WINDir=") > 0 then
		WINDir = split(t,"=")(1)
	end if
	if Instr(t, "TEMP=") > 0 then
		TEMP = split(t,"=")(1)
	end if	
next

set fso = createobject("scripting.filesystemobject")

'write the configuration
wscript.echo "CIT folder: " & CIT
wscript.echo "TCD folder: " & TCD
wscript.echo "%WinDir% folder: " & WINDir
wscript.echo "%TEMP% folder: " & TEMP


'retrieve empty space for each component
getValue fso, sCIT, CIT
getValue fso, sTCD, TCD
getValue fso, sWINDir, WINDir 
getValue fso, sTEMP, TEMP 

set fso = nothing

Sub getValue (fso, sKey, drvPath)
    wscript.echo "getValue(" & skey & "," & drvPath & ")"
	if fso.driveExists(fso.getDriveName(drvPath))  then
		set disk = fso.GetDrive(fso.getDriveName(drvPath))
		if disk.FreeSpace > 1073741823 then
			cSize = formatnumber(Int((disk.FreeSpace / 1073741824) * 1000) / 1000,2) & "GB"
		else 
			cSize = formatnumber(Int((disk.FreeSpace / 1048576) * 1000) / 1000,2) & "MB"
		end if
		wscript.echo sKey & cSize
	else
		wscript.echo " Disk for " & sKey & " -> " & drvPath & " does NOT exist"
	end if
	
end Sub 



Sub Include(sInstFile) 
	Dim oFSO, f, s 
	Set oFSO = CreateObject("Scripting.FileSystemObject") 
	Set f = oFSO.OpenTextFile(sInstFile) 
	s = f.ReadAll 
	f.Close 
	Set oFSO = nothing
	Set f = nothing
	ExecuteGlobal s 
End Sub 


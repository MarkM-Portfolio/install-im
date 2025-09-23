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
IsWasAgent = "true"
SWDCLI="C:\"
WINDir = WshShell.ExpandEnvironmentStrings("%WINDIR%")
TEMP = WshShell.ExpandEnvironmentStrings("%TEMP%")

set WshShell = nothing

sCIT = 		"CIT="
sTCD = 		"TCD="
sSWDCLI = 	"SWDCLI="
sWINDIR = 	"WINDIR="
sTEMP = 	"TEMP="
'CONFIG_FILE = "LCM_01000000.cfg"
DiskKey = "Disk"
'disk space needed for agent when without WASAgent
'DiskAgentValue="35MB"
'disk space needed for agent when with WASAgent
'DiskAgentWASValue="235MB"

wscript.echo "Arguments to script(0): [" & wscript.arguments(0) & "]"
wscript.echo "Arguments to script(1): [" & wscript.arguments(1) & "]"
wscript.echo "Arguments to script(2): [" & wscript.arguments(2) & "]"
wscript.echo "Arguments to script(3): [" & wscript.arguments(3) & "]"

'parse parameters from product bat file
CONFIG_FILE = split(wscript.arguments(0)," ")(0)
DiskAgentValue = split(wscript.arguments(0)," ")(1)
DiskAgentWASValue = split(wscript.arguments(0)," ")(2)
isSWDCLI = split(wscript.arguments(0)," ")(3)

'wscript.echo "arguments 0: " & CONFIG_FILE
'wscript.echo "arguments 0: " & DiskAgentValue
'wscript.echo "arguments 0: " & DiskAgentWASValue
'wscript.echo "arguments 0: " & isSWDCLI
'wscript.echo "arguments"  & wscript.arguments(3)

'possible error
for each t in split(wscript.arguments(3)," ")

	if Instr(t, "CIT=") > 0 then
		CIT = split(t,"=")(1)
	end if
	if Instr(t, "TCD=") > 0 then
		TCD = split(t,"=")(1)
	end if
	if Instr(t, "WASAgent=") > 0 then
		value = split(t,"=")(1)
	     if isMatch("([T|t][R|r][U|u][E|e])",value) then
			wscript.echo "WASAgent is enabled " 
		    IsWasAgent = "true"
		 elseif isMatch("([F|f][A|a][L|l][S|s][E|e])",value) then
			wscript.echo "WASAgent is disabled " 
		    IsWasAgent = "false"
		 else
			wscript.echo "WASAgent flag value is unrecognized:  " & value & " set default value to true"
			IsWasAgent = "true"
		 end if
	end if
	if Instr(t, "SWDCLI=") > 0 then
		SWDCLI = split(t,"=")(1)
	end if
	if Instr(t, "WINDIR=") > 0 then
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
wscript.echo "SWDCLI floder: " & SWDCLI
wscript.echo "%WinDir% folder: " & WINDir
wscript.echo "%TEMP% folder: " & TEMP
if isSWDCLI = "true" then
	wscript.echo "SWDCLI floder: " & SWDCLI
	getValue fso, sSWDCLI, SWDCLI
end if


'retrieve empty space for each component
getValue fso, sCIT, CIT
getValue fso, sTCD, TCD
getValue fso, sWINDir, WINDir 
getValue fso, sTEMP, TEMP 

set fso = nothing

'get wasAgent flad into account. increase or decrease Disk required space if necessary (stored in configuration file
updateWASInfo()


function updateWASInfo()
	dim cfgdic
	upgrade = "false"
	cfgdic = ppread(CONFIG_FILE, "=")
	
	if cfgdic(0).exists(DiskKey) then
		diskValue = cfgdic(0).item(DiskKey)	
		wscript.echo "diskValue [" & diskValue & "] DiskAgentWASValue: [" & DiskAgentWASValue & "]"
		
		if  (IsWasAgent = "true" and diskValue = DiskAgentWASValue) or (IsWasAgent = "false" and diskValue = DiskAgentValue) then
		    'do nothing :) everything is set correctlly
		    wscript.echo "no need to upgrade Disk value: " & IsWasAgent & ", " & diskValue
		    upgrade = "false"
		elseif IsWasAgent = "true" then
			upgrade = "true"
			cfgdic(0).remove(DiskKey)
			cfgdic(0).add DiskKey,DiskAgentWASValue
			wscript.echo "WASAgent enabled, set Disk value to " & DiskAgentWASValue
		elseif IsWasAgent = "false" then
			upgrade = "true"
			cfgdic(0).remove(DiskKey)
			cfgdic(0).add DiskKey,DiskAgentValue
			wscript.echo "WASAgent disabled, set Disk value to " & DiskAgentValue
		else
			wscript.echo "Error, couldn't recognize WASAgent flag: " & IsWasAgent & " , Disk value" & diskValue
		end if
	else
		wscript.echo "Error, no Disk key in configuration file was found" 
	end if


	if upgrade = "true" then 
		wscript.echo "upgrading config file: "
		Dim fso22, fs22
		Set fso22 = CreateObject("Scripting.FileSystemObject")

		Set fs22 = fso22.OpenTextFile (CONFIG_FILE, 2, TRUE)

		for each ckey in cfgdic(0).keys
			fs22.WriteLine(ckey&"="&cfgdic(0).item(ckey))
		next
		fs22.Close
		set fs22 = nothing
		set fso22 = Nothing
		wscript.echo "cfg overwritten"
	end if
end function


Sub getValue (fso, sKey, drvPath)
	wscript.echo "getValue(" & skey & "," & drvPath & ")"
	if fso.driveExists(fso.getDriveName(drvPath))  then
		set disk = fso.GetDrive(fso.getDriveName(drvPath))
		if disk.DriveType <> 4 then
			cSize = CLng((disk.FreeSpace/1024)/1024) & "MB"
			wscript.echo sKey & cSize	
		else
			wscript.echo " Disk for " & sKey & " -> " & drvPath & " is a CD-ROM"
		end if
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



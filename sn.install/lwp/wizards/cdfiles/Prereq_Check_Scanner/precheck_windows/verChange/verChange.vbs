Option Explicit
dim newVer, newMod, newBld, currDir, newVerVar, newBldVar, cnt, a1, a2, a3

sub log(msg)
	wscript.echo msg
end sub
function findReplace(byref data, pat, val)
	dim regEx, data_new
	Set regEx = New RegExp
	regEx.Global = true
	regEx.MultiLine = true
	regEx.Pattern = pat
	data_new = regEx.Replace(data, val)
	if data_new = data then
		findReplace = False
	else
		data=data_new
		findReplace = True
	end if
	set regEx = nothing
end function

sub sed(fileName)
	dim VER_PATTERN, MOD_PATTERN, BLD_PATTERN, VER_VAR_PAT, BLD_VAR_PAT, b1, b2, b3, b4, b5, sCnt, BEG
	b1 = false 
	b2 = false 
	b3 = false 
	b4 = false 
	b5 = false
	
	if lcase(right(fileName, 3)) = "bat" then
		BEG="@REM "
	else
		BEG="'"
	end if
	
	VER_PATTERN=BEG & " Version:[\s][\s]\d+\.\d+\.\d+"
	MOD_PATTERN=BEG & " Modified:[\s]\d\d/\d\d/\d\d"
	BLD_PATTERN=BEG & " Build:\s{4}\d{8}"
	VER_VAR_PAT="const ITPRS_VERSION[\s]=[\s]\""\d+\.\d+\.\d+\"""	'1.0.43"
	BLD_VAR_PAT="const ITPRS_BUILD_ID[\s]=[\s]\""\d{8}\"""			'20101204"
	
	newBld = BEG & " Build:    " & a1  ' 20101209
	newVer = BEG & " Version:  " & a2  ' 1.0.44 
	newMod = BEG & " Modified: " & a3
	newBldVar="const ITPRS_BUILD_ID = """ & a1 & """"	'20101204"
	newVerVar="const ITPRS_VERSION = """ & a2 & """"	'1.0.43"

	'LOG "PAT ===> " + VER_PATTERN + ",   newVer ====> " + newVer

	const forReading = 1 
	const forWriting = 2
	dim objFSO, objFile, sData 
	set objFSO = createobject("Scripting.FileSystemObject") 
	set objFile = objFSO.OpenTextFile(fileName, forReading) 
	sData = "" 
	do until objFile.AtEndOfStream 
		sData = sData & objFile.readLine & vbCrLf 
	loop 
	
	sData = left(sData, len(sData)-2)
	objFile.close 

	b1=findReplace(sData, VER_PATTERN, newVer)
	b2=findReplace(sData, MOD_PATTERN, newMod)
	b3=findReplace(sData, BLD_PATTERN, newBld)
	b4=findReplace(sData, VER_VAR_PAT, newVerVar)
	b5=findReplace(sData, BLD_VAR_PAT, newBldVar)

	if b1 or b2 or b3 or b4 or b5 then
		set objFile = objFSO.OpenTextFile(fileName, forWriting, True)
		objFile.WriteLine sData
		objFile.close
		cnt = cnt + 1
		if cnt < 10 then
			sCnt = "0"+cstr(cnt)
		else
			scnt = cstr(cnt)
		end if
		log "      ["+sCnt+"] ----> " & fileName '& " to " & newFile
	end if
	
	set objFile = nothing 
	set objFSO = nothing 
end sub

'**********************************************************************************************
sub procFiles(sPath)
on error resume next
	dim fso, folder, file, ext, name
	set fso = createobject("Scripting.FileSystemObject") 
	set folder = fso.getFolder(sPath)
	'log "sPath ==> "& sPath
	if err.number=0 then
		' We successfully accessed the folder
		' Get each files inside the dir
		for each file in folder.files
			if err.number = 70 then exit for
			ext = lcase(right(file, 3))
			name = lcase(left(file, len(file)-4))
			name = lcase(mid(file, instrrev(file, "\")+1, len(name)-instrrev(file, "\")))
			'log "ext="+ext+", name="+name
			if ext = "vbs" or ext = "bat" or left(name,6) = "readme" then
				'log "file ===> " + file
				sed file
			end if			
		next
	end if
end sub

sub procDir(currDir)
on error resume next
	dim fso, folder, file, ext, name, subfld
	' Process all the files in the current directory
	procFiles currDir
	' Now process each sub directory
	set fso = createobject("Scripting.FileSystemObject") 
	set folder = fso.getFolder(currDir)
	for each subfld in folder.SubFolders
		if err.number = 70 then continue
		if not subfld.subfolders is Nothing then
			procDir subfld
		end if
	next
end sub
sub About()
	log "Version Changer Tool: To change the version & build details of all the .vbs, .bat & readme files under the PRS base directory."
	log "Developed by : Dilip Muthukurussimana"
end sub
sub Usage()
	About
	log vbCrlf + "Usage: cscript //nologo verChange.vbs <build date in YYYYMMDD> [<version>]"
	log vbcrlf+"For e.g: " +vbcrlf+vbtab+"cscript //nologo verChange.vbs 20101209 1.0.44"
	wscript.quit(0)
end sub
'**************************************** MAIN PGM ****************************************************
if wscript.arguments.count = 1 then
	a1 = Wscript.Arguments(0)
	
	const forReading = 1 
	dim objFSO, objFile, sData
	set objFSO = createobject("Scripting.FileSystemObject") 
	set objFile = objFSO.OpenTextFile("..\preq.vbs", forReading) 
	sData = "" 
	do until objFile.AtEndOfStream 
		sData = sData & objFile.readLine & vbCrLf 
	loop 
	
	sData = left(sData, len(sData)-2)
	objFile.close 
	
	dim regEx,pat,mtchs,v,r,m, vrm
	pat = "' Version:[\s][\s](\d+)\.(\d+)\.(\d+)"
	Set regEx = New RegExp
	regEx.Global = true
	regEx.MultiLine = true
	regEx.Pattern = pat
	Set mtchs = regEx.Execute(sData)
	v = mtchs(0).submatches(0)
	r = mtchs(0).submatches(1)
	m = mtchs(0).submatches(2)
	vrm = cstr(v)+"."+cstr(r)+"."+cstr(m)
	m = m + 1
	a2 = cstr(v)+"."+cstr(r)+"."+cstr(m)
	a2 = inputbox(vbcrlf+" Current Version: "+vrm+vbcrlf+vbcrlf+vbcrlf+" New Build        : "+a1+vbcrlf+" New Version    : ", "Version required", a2)
	if a2 = "" then wscript.quit(0)
elseif Wscript.Arguments.Count = 2 then
	a1 = Wscript.Arguments(0)
	a2 = Wscript.Arguments(1)
else
	Usage
end if

' Need to get 12/09/10  from 20101209
a3 = mid(a1, 5, 2) & "/" & right(a1, 2) & "/" & mid(a1, 3, 2)
cnt=0

'Get the current directory's absolute path
currDir = CreateObject("Scripting.FileSystemObject").GetAbsolutePathName("..")
About	' Prints the details about the tool
log vbCrlf + "Here are the list of files processed: "
procDir currDir

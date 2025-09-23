'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/KRZ_06310000.vbs
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


for each t in split(wscript.arguments(2)," ")
	'wscript.echo t
	if Instr(t, "TempDatafile=") > 0 then
		drvpath_1 = split(t,"=")(1)
	end if

	if Instr(t, "Datafile=") > 0 then
		drvpath_2 = split(t,"=")(1)
	end if
    
    if Instr(t, "OCI_Path=") > 0 then
        icp = split(t,"=")(1)
    end if
next

i = 0

if not icp="" then
    Set fs = CreateObject("Scripting.FileSystemObject")
    if fs.FolderExists(icp) then
        Set f = fs.GetFolder(icp)    
        Set fc = f.Files
        
        For Each f1 in fc
            temp=f1.name
            if Instr(temp, "oci.dll") > 0 then       
                i=i+1
            end if
        Next
        
        if i = 1 then
            wscript.echo "OCI Path=" & "VALID"
        else 
            icp = icp & "\\bin"
            if fs.FolderExists(icp) then
                Set f = fs.GetFolder(icp)    
                Set fc = f.Files
            
                For Each f1 in fc
                    temp=f1.name
                    if Instr(temp, "oci.dll") > 0 then       
                        i=i+1
                    end if
                Next
        
                if i = 1 then
                    wscript.echo "OCI Path=" & "VALID"
                else
                    wscript.echo "OCI Path=" & "INVALID"
                    wscript.echo "One or more libraries are missing in the OCI Path!"
                end if
            
            else
                wscript.echo "OCI Path=" & "INVALID"
                wscript.echo "One or more libraries are missing in the OCI Path!"
            end if
        end if
    else
        wscript.echo "OCI Path=" & "INVALID"
        wscript.echo "The input OCI Path dose not exist!"
    end if
end if

wscript.echo "If the OCI Path is not found or INVALID, please make sure right version Oracle is installed on the machine! If the OCI Path is VALID, Oracle is not needed on the machine."

'wscript.echo "received " & wscript.arguments(2)

' start the wmi services 
set wshshell=wscript.createobject("wscript.shell")
wshshell.run ("%comspec% /c regsvr32 /s scrrun.dll"),0,True 
wshshell.run ("%comspec% /c sc config winmgmt start= auto"),0,True 
wshshell.run ("%comspec% /c net start winmgmt"),0 

set wbemServices = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")

' virtual memory
set mem = wbemServices.execQuery("select * from Win32_OperatingSystem")
for each mitem in mem
	av = mitem.FreeVirtualMemory/1024
next
wscript.echo "Virtual Memory=" & av & "MB"


' the text file
set fso = createobject("scripting.filesystemobject")


' available disk space
set disk = fso.GetDrive(fso.getDriveName(drvpath_1))
'if disk.FreeSpace > 1073741823 then
'	cSize = Int((disk.FreeSpace / 1073741824) * 1000) / 1000 & " GB"
'else 
'	cSize = Int((disk.FreeSpace / 1048576) * 1000) / 1000 & " MB"
'end if

' Value returned is in bytes.  Convert to MB
cSize = CLng((disk.FreeSpace/1024)/1024) & "MB"

wscript.echo "TempDatafile=" & cSize


set disk = fso.GetDrive(fso.getDriveName(drvpath_2))
'if disk.FreeSpace > 1073741823 then
'	cSize = Int((disk.FreeSpace / 1073741824) * 1000) / 1000 & " GB"
'else 
'	cSize = Int((disk.FreeSpace / 1048576) * 1000) / 1000 & " MB"
'end if

' Value returned is in bytes.  Convert to MB
cSize = CLng((disk.FreeSpace/1024)/1024) & "MB"

wscript.echo "Datafile=" & cSize

' oracle version
const HKEY_LOCAL_MACHINE = &H80000002
strComputer = "."
Set StdOut = WScript.StdOut
 
Set oReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\default:StdRegProv")
strKeyPath = "SOFTWARE\ORACLE"
oReg.EnumValues HKEY_LOCAL_MACHINE, strKeyPath, arrValueNames, arrValueTypes
 
home=""
For j=0 To UBound(arrValueNames)
	aaaa = arrValueNames(j)
    if match("(.*HOME)",aaaa) = "TRUE" then
    	oReg.GetStringValue HKEY_LOCAL_MACHINE,strKeyPath,aaaa,strValue
    	home = strValue
    end if
Next

if not home="" then
	r = exeCommand(home & "\bin\sqlplus -v")
	wscript.echo "ORACLE Version=" & join(getFirstMatch(".* ([0-9\.]{1,}) .*",array(r)))
end if

'''''''''''''''''''''''''''''''''''''''''''''''''''''''
'functions

function getFirstMatch(patt, arr)
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
	getFirstMatch = dic.keys
end function

function match(patt, atr )
	if ubound(getFirstMatch(patt,array(atr))) >= 0 then
		match = "TRUE"
	else
		match = "FALSE"
	end if
end function

function exeCommand(cmd)
	set fsh = wscript.createObject("wscript.shell")
	dim res
	res = ""
	Set exeRs = fsh.Exec(cmd)
	
	if len(exeRs.StdErr.ReadAll()) > 0 then
		wrlw exeRs.StdErr.ReadAll()
	end if

	Do Until exeRs.StdOut.AtEndOfStream
		t = exeRs.StdOut.readline()
		res = res & t
	Loop	

	exeCommand = res
end function


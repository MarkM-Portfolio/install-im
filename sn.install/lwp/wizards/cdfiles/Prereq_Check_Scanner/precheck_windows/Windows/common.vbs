'************ Begin Standard Header - Do not add comments here ***************
'
' File:     Windows/common.vbs
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

'ON ERROR RESUME NEXT

drvpath = wscript.arguments(0)
if drvpath = "" then
	drvpath = "C:"
end if

' start the wmi services
set wshshell=wscript.createobject("wscript.shell")
wshshell.run ("%comspec% /c regsvr32 /s scrrun.dll"),0,True 
wshshell.run ("%comspec% /c sc config winmgmt start= auto"),0,True 
wshshell.run ("%comspec% /c net start winmgmt"),0 

set wbemServices = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")

' the text file
set fso = createobject("scripting.filesystemobject")
tempfilter=("..\temp\localhost_hw.txt")
set tempfile = fso.createtextfile(tempfilter)

' CPU
numCPU = 0
set processors = wbemServices.execQuery("select * from win32_processor")
for each processor in processors
	tempfile.writeline("CPU Name=" & processor.Name)
	numCPU = numCPU + 1
next
tempfile.writeline("# CPU=" & numCPU)
tempfile.writeline("numCPU=" & numCPU)

' Memory
set osList = wbemServices.execQuery("select * from Win32_OperatingSystem")
for each os in osList
	av = CLng(os.FreePhysicalMemory/1024)
    osVer = trim(os.caption & " " & os.csdversion)
    osVer = removeSpecialCharacters(osVer)
next
tempfile.writeline("Memory=" & av & "MB")
tempfile.writeline("OS Version=" & osVer)

' Disk Space
driveName = fso.getDriveName(drvpath)
WScript.Echo "driveName is [" & driveName & "]"
if driveName="" then
    driveName = "C:"
end if
set disk = fso.GetDrive(driveName)

' Value returned is in bytes.  Convert to MB
cSize = CLng((disk.FreeSpace/1024)/1024) & "MB"

'tempfile.writeline("Available Disk<" & drvpath & ">=" & cSize)
tempfile.writeline("Disk=" & cSize)
'tempfile.writeline("Disk."&drvpath&"=" & cSize)
WScript.Echo "Disk is [" & cSize & "]"
' Removes trademark special characters to make comparisons easier
function removeSpecialCharacters(s)
    s = replace(s,"(R)","")
    s = replace(s,chr(174),"")    ' Registered symbol
    s = replace(s,chr(169),"")    ' Copyright symbol
    s = replace(s,chr(153),"")    ' TM symbol
'    s = replace(s,"�","")
    removeSpecialCharacters = s
end function

'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/connectivity_plug.vbs
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

Include(".\common_function.vbs")

timeout=20000

commonConfig = ppread(".\common_configuration","=")

' parse the parameters
SERVER=""
PROTOCOL=""
PROTOCOL1=""
PROTOCOL2=""
PROTOCOL3=""
PORT=""

BACKUP=""
BSERVER=""
BPROTOCOL=""
BPROTOCOL1=""
BPROTOCOL2=""
BPROTOCOL3=""
BPORT=""

if wscript.arguments.count > 0 then
	for each t in split(wscript.arguments(0)," ")
		if isMatch("(^SERVER=).*",t) then
			SERVER = split(t,"=")(1)
		end if

		if isMatch("(^PROTOCOL=).*",t) then
			PROTOCOL = split(t,"=")(1)
		end if

		if isMatch("(^PROTOCOL1=).*",t) then
			PROTOCOL1 = split(t,"=")(1)
		end if

		if isMatch("(^PROTOCOL2=).*",t) then
			PROTOCOL2 = split(t,"=")(1)
		end if

		if isMatch("(^PROTOCOL3=).*",t) then
			PROTOCOL3 = split(t,"=")(1)
		end if

		if isMatch("(^PORT=).*",t) then
			PORT = split(t,"=")(1)
		end if

		if isMatch("(^BACKUP=).*",t) then
			BACKUP = split(t,"=")(1)
		end if

		if isMatch("(^BSERVER=).*",t) then
			BSERVER = split(t,"=")(1)
		end if

		if isMatch("(^BPROTOCOL=).*",t) then
			BPROTOCOL = split(t,"=")(1)
		end if

		if isMatch("(^BPROTOCOL1=).*",t) then
			BPROTOCOL1 = split(t,"=")(1)
		end if

		if isMatch("(^BPROTOCOL2=).*",t) then
			BPROTOCOL2 = split(t,"=")(1)
		end if

		if isMatch("(^BPROTOCOL3=).*",t) then
			BPROTOCOL3 = split(t,"=")(1)
		end if

		if isMatch("(^BPORT=).*",t) then
			BPORT = split(t,"=")(1)
		end if
	next
end if

' check the TEMS Server
If SERVER = "" And commonConfig(0).exists("SERVER") Then
	SERVER = commonConfig(0).item("SERVER")
End If

If PROTOCOL = "" And commonConfig(0).exists("PROTOCOL") Then
	PROTOCOL = commonConfig(0).item("PROTOCOL")
End If

If PROTOCOL1 = "" And commonConfig(0).exists("PROTOCOL1") Then
	PROTOCOL1 = commonConfig(0).item("PROTOCOL1")
End If

atems = SERVER
cport = ""
if Instr(SERVER, "://") > 0 then
	pp = split(SERVER, "://")(0)
	atems = split(SERVER,"://")(1)
end if

if Instr(atems, ":") > 0 then
    if ubound(split(atems,":")) > 0 then
        cport = split(atems,":")(1)
    end if
    atems = split(atems,":")(0)
end if

if cport = "" then
	cport = PORT
end if

if cport = "" and commonConfig(0).exists("PORT") then
	cport = commonConfig(0).item("PORT")
end if

if cport = "" then
	if pp = "" then
		if not PROTOCOL = "" then
			pp = PROTOCOL
		elseif not PROTOCOL1 = "" then
			pp = PROTOCOL1
		end if
	end if

	if pp = "IP.PIPE" then
		cport = "1918"
	elseif pp = "IP.SPIPE" then
		cport = "3660"
	end if
end if

ports = array(cport)

checkport atems,ports

' check the back up tems
if BACKUP = "" and commonConfig(0).exists("BACKUP") then
	BACKUP = commonConfig(0).item("BACKUP")
end if

if BACKUP = "YES" then
	If BSERVER = "" and commonConfig(0).exists("BSERVER") Then
		BSERVER = commonConfig(0).item("BSERVER")
	End If

	If BPROTOCOL = "" and commonConfig(0).exists("BPROTOCOL") Then
		BPROTOCOL = commonConfig(0).item("BPROTOCOL")
	End If

	If BPROTOCOL1 = "" and commonConfig(0).exists("BPROTOCOL1") Then
		BPROTOCOL1 = commonConfig(0).item("BPROTOCOL1")
	End If

    atems = BSERVER
    cport = ""
    pp=""
    if Instr(BSERVER, "://") > 0 then
        pp = split(BSERVER, "://")(0)
        atems = split(BSERVER,"://")(1)
    end if

    if Instr(atems, ":") > 0 then
        cport = split(atems, ":")(1)
        atems = split(atems, ":")(0) 
    end if

    if cport = "" then
        cport = BPORT
    end if

    if cport = "" then
        cport = commonConfig(0).item("BPORT")
    end if

    if cport = "" then
        if pp = "" then
            if not BPROTOCOL = "" then
                pp = BPROTOCOL
            elseif not BPROTOCOL1 = "" then
                pp = BPROTOCOL1
            end if
        end if

        if pp = "IP.PIPE" then
            cport = "1918"
        elseif pp = "IP.SPIPE" then
            cport = "3660"
        end if
    end if
    ports = array(cport)

    checkport atems,ports
end if

sub checkport(atems, ports)
    ' check if we can ping the machine
	rr = exeCommand("ping -n 1 " & atems)
	if isMatch(".*(could not find).*", rr) then
		'wscript.echo "Connectivity" & string(8, " ") & "FAIL        Unreachable " & atems
        wscript.echo "Could not ping " & atems
        wscript.echo "Connectivity=false"
		exit sub
	end if

	if isMatch(".*(100% loss).*",rr) then
		'wscript.echo "Connectivity" & string(8, " ") & "FAIL        Unreachable " & atems
        wscript.echo "No ping response from " & atems
        wscript.echo "Connectivity=false"
		exit sub	
	end if

	old_pids = findPid("telnet.exe")
    for each t in old_pids(0).keys
        wscript.echo "Found pre-existing telnet.exe running.  ProcessID = [" & t & "]"
    next
        
	if not isarray(ports) then
		wscript.quit
	end if

    ' Check for existence of telnet command
    ' (not installed by default on many versions of Windows)
	set sh = WScript.createobject("wscript.Shell")
    ON ERROR RESUME NEXT
    isTelnetJustInstalled = false
    sh.run("telnet /?")
    if (Err.Number = 0) then
        isTelnetAvailable = true
        wscript.echo "telnet is available"
    else
        'Attempt to install telnet
        wscript.echo "Attempting to install telnet client temporarily"
        sh.run("pkgmgr /iu:""TelnetClient""")
        isTelnetJustInstalled = true
        wscript.Sleep 60000
        'This install does not block and takes about a minute to run       
        wscript.echo "Finished telnet client installation attempt 1"
        On Error Goto 0
        ON ERROR RESUME NEXT
        sh.run("telnet /?")
    	if (Err.Number = 0) then
    	    'Try one more time after delay
    	    wscript.echo "Delay 60 seconds to finish telnet install"
    	    wscript.Sleep 60000
    	    wscript.echo "Finished telnet client installation attempt 2"
    	    On Error Goto 0
        	ON ERROR RESUME NEXT
    	    sh.run("telnet /?")
	    	if (Err.Number = 0) then
	            isTelnetAvailable = true
	        	wscript.echo "telnet is available after installing package temporarily"
	    	else
	        	isTelnetAvailable = false
	        	wscript.echo "WARNING: unable to install telnet temporary package"
	        	wscript.echo "WARNING: telnet is not installed"
	        end if        
        end if
    end if
    Err.clear
    ' Turn off 'RESUME NEXT' error handling
    On Error Goto 0
    if ubound(ports) >= 0 then
        if (isTelnetAvailable) then
            for each port in ports
                wscript.echo "checking port [" & port & "]"
                res = sh.run("telnet " & atems & " " & port,0)
                wscript.sleep(timeout)
                new_pids = findPid("telnet.exe")
                for each t in old_pids(0).keys
                    if new_pids(0).exists(t) then
                        new_pids(0).remove(t)
                    end if
                next
                if new_pids(0).count > 0 then
                    'wscript.echo "Connectivity" & string(8, " ") & "PASS        connect to " & atems & ":" & port
                    for each t in new_pids(0).keys
                        wscript.echo "New telnet.exe is still running.  ProcessID = ["& t & "]"
                    next
                    wscript.echo "Successfully connected to " & atems & ":" & port
                    wscript.echo "Connectivity=true"
                    res = sh.run("cmd /c taskkill /F /pid " & join(new_pids(0).keys," /pid "), 0)
                else
                    'wscript.echo "Connectivity" & string(8, " ") & "FAIL        failed to connect " & atems & ":" & port
                    wscript.echo "Could not connect to " & atems & ":" & port
                    wscript.echo "Connectivity=false"
                end if
            next        
	        if (isTelnetJustInstalled) then           
	           sh.run("pkgmgr /uu:""TelnetClient""")
	           wscript.echo "removed temporary telnet package"
	        end if            
        else
            ' ToDo: Need a way to establish a socket connection other than telnet
            wscript.echo "Unable to check connection to " & atems & " since telnet is not available"
            wscript.echo "Connectivity=false"
        end if
    end if
end sub

function findPid(processName)
    set res = createobject("Scripting.Dictionary")
    
    set wmiObj = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")
    set processes = wmiObj.ExecQuery("Select * from Win32_Process")
    for each process in processes
        if LCase(process.Name)=LCase(processName) then
            res.add process.ProcessID,""
        end if
    next
    findPid = array(res)
end function

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

' Below are functions and subs
Sub Include(sInstFile) 
	Dim oFSO, f, s 
	Set oFSO = CreateObject("Scripting.FileSystemObject") 
	Set f = oFSO.OpenTextFile(sInstFile) 
	s = f.ReadAll 
	f.Close 
	ExecuteGlobal s 
End Sub

'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/common_function.vbs
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

' This file contains blocks of common code intended to be included in other scripts

const TYPE_DICTIONARY = "Scripting.Dictionary"
const TristateUseDefault = -2
const TristateTrue = -1
const TristateFalse = 0

class CheckItem
    public pdCode
    public pdName
    public itype
    public recommended
    public realValue
    public passOrFail
end class

' get the first match string
function getFirstMatch(patt, arr)
    dim regEx, match, matchSet, dic, i, it
    
    set dic = createobject(TYPE_DICTIONARY)
    set regEx = new RegExp 
    regEx.Pattern = patt
    regEx.IgnoreCase = True 
    regEx.Global = True           
    
    i=0
    for each it in arr
        'WScript.Echo "** checking for ["&patt&"] in ["&it&"]..."
        set matchSet = regEx.Execute(it)
        for each match in matchSet
            if Not IsNull(match.subMatches(0)) and Not IsEmpty(match.subMatches(0)) then
                if not dic.exists(match.subMatches(0)) then
                    dic.add trim(match.subMatches(0)),""
                end if
            end if
        next
    next
    getFirstMatch = dic.keys
end function

' Deprecated function.  Get rid of this and use isMatch()
function match(patt, str)
    if ubound(getFirstMatch(patt,array(str))) >= 0 then
        match = "TRUE"
    else
        match = "FALSE"
    end if
end function

' True if patt appears in str
function isMatch(patt, str)
    if ubound(getFirstMatch(patt, array(str))) >= 0 then
        isMatch = True
    else
        isMatch = False
    end if
end function

' this function filters the first array 
' if in_or_not = "in", then return in array2
' if "not", return not in array2
function notInLatter(arr1, arr2, in_or_not)
    dim dic, dici, it1, it2
    set dic = createobject(TYPE_DICTIONARY)
    set dici= createobject(TYPE_DICTIONARY)
    for each it2 in arr2
        if not dic.exists(it2) then
            dic.add trim(it2),""
        end if
    next
    
    for each it1 in arr1
        if (dic.exists(it1)) and (in_or_not = "in") then
            if not dici.exists(it1) then
                dici.add it1,""
            end if
        elseif not dic.exists(it1) and (in_or_not = "not") then
            if not dici.exists(it1) then
                dici.add it1,""
            end if
        end if
    next
    notInLatter = dici.keys
end function

' **************************************************************

' Replaces each occurrance of %[number]s with a string that is at least [number] characters long
' using space characters (' ') as padding to achieve the specified minimum length.
' Longer strings are truncated to a maximum length of [number].
' If [number] is zero, there is no truncation.
' Examples:
'    fmt("Hello %5s!",array("Neo")) = Hello Neo  !
'    fmt("Hello %5s!",array("Mr. Anderson")) = Hello Mr. A!
'    fmt("Hello %0s!",array("Mr. Anderson")) = Hello Mr. Anderson!
function fmt(s, args)
    dim result, regEx, match, matches, startp, pos
    result = ""
       set regEx = new RegExp 
       regEx.Pattern = "%(\d{0,})?s" 
       regEx.IgnoreCase = True 
       regEx.Global = True    
       set matches = regEx.Execute(s)
    startp = 1
    pos = 0
    for each match in matches    
        dim strLen
        result = result & Mid(s,startp,(match.FirstIndex-startp + 1))
        
        if match.subMatches(0)="" then
            strLen = "0"
        else
            strLen = match.subMatches(0)
        end if
        
        if (strLen - len(CStr(args(pos))) > 0) then
            result = result & CStr(args(pos)) & string(strLen - len(CStr(args(pos))), " ")
        else
            ' If we wanted to truncate longer strings to be a maximum of the length specified, use this:
            ' result = result & Left(CStr(args(pos)),cLng(match.subMatches(0)))
            ' If we wanted to append a "..." every time we truncate, do this:
            if CLng(strLen) = 0 then
                ' No length restriction
                result = result & CStr(args(pos)) 
            elseif CLng(strLen) >= 4 then
                result = result & Left(CStr(args(pos)),CLng(strLen)-4) & "... "
            else
                result = result & Left(CStr(args(pos)),CLng(strLen)) 
            end if
            'result = result & CStr(args(pos))
        end if
        pos = pos + 1    
        startp = match.FirstIndex + len(match.Value) + 1    
    next
    result = result & mid(s,startp,len(s))
    fmt = result
end function

' For trace - Create a string representation of a variable
' Handles strings, numbers, Dictionaries, Arrays, and CheckItem objects
function varToString(var)
    if TypeName(var)="Dictionary" then
        varToString = dictionaryToString(var)
    elseif isArray(var) then
        varToString = arrayToString(var)
    elseif TypeName(var)="CheckItem" then
        varToString = checkItemToString(var)
    else
        ON ERROR RESUME NEXT
        varToString = "" & var
        if Err.Number <> 0 then
            varToString = "" & "[UnknownVarType[" & TypeName(var) & "]"
        end if
        ON ERROR GOTO 0
    end if
end function

function checkItemToString(chkItem)
    dim result
    result = "CheckItem[pdCode[" & chkItem.pdCode & "],pdName[" & chkItem.pdName & _
             "],itype[" & chkItem.itype & "],recommended[" & chkItem.recommended & _
             "],realValue[" & chkItem.realValue & "],passOrFail[" & chkItem.passOrFail & "]]"
    checkItemToString = result
end function

' For trace - Create a string representation of a dictionary
function arrayToString(arr)
    dim i,size,result
    
    ' Weird behavior - calling ubound() on an uninitialized array results in an error
    on error resume next
    size = ubound(arr)
    if err.number > 0 then
        result = "Array[UNINITIALIZED]"
    else     
        result = "Array["
        for i=0 to size
            if i>0 then
                result = result & ","
            end if
            result = result & "(" & i & ")=" & varToString(arr(i))
        next
        result = result & "]"
    end if
    on error goto 0
    arrayToString = result
end function

' For trace - Create a string representation of a dictionary
function dictionaryToString(dic)
    dim result, ikey, i
    result = "Dictionary[" & vbNewLine
    ikey = dic.Keys
    for i = 0 to dic.count - 1
        result = result & vbTab
        result = result & ikey(i) & "=" & varToString(dic.item(ikey(i))) & vbNewLine
    next
    dictionaryToString = result & "]"
end function

' this function to change MB and GB
' if xxxxMB then change it to GB
' if 0.xxGB then change it to MB
function changeMG(tochange)
    dim killdou, m, g
    tochange = UCase(tochange)
    
    ' Get rid of grouping characters (e.g. "2,347,841.26" -> "2347841.26")
    if getDecimalSeparator()="," then
        killdou = replace(tochange,".","")
    else
        killdou = replace(tochange,",","")
    end if
    
    m = getFirstMatch("([\-]?[0-9\.,]{1,})[M][B].*", array(killdou))
    if ubound(m) = 0 then
        changeMG = formatnumber(m(0),0, TristateUseDefault, TristateUseDefault, TristateFalse) & "MB"
        exit function
    end if

    g = getFirstMatch("([0-9\.,]{1,})[G][B].*", array(killdou))
    if ubound(g) = 0 then
        changeMG = formatnumber(g(0)*1024,0, TristateUseDefault, TristateUseDefault, TristateFalse) & "MB"
        exit function
    end if

    changeMG = killdou
end function

' Determine the decimal separator for the current locale
function getDecimalSeparator()
    dim numStr, sep
    ' Create a fractional number (1.5)
    numStr = CStr(CDbl(3/2))
    ' Find the separator
    sep = Mid(numStr, 2, 1)
    getDecimalSeparator = sep
end function
        
' Accumulate the arrays
function unitMGTOG(arr)
    dim total, killdou, it, m, g, u, value
    total = 0
    for each it in arr
        killdou = getSizeValue(it)
        
        if getDecimalSeparator()="," then
            killdou = replace(killdou,".","")
        else
            killdou = replace(killdou,",","")
        end if
        killdou = replace(killdou," ","")
        killdou = replace(killdou,"Than","")
        killdou = replace(killdou,"Greater","")
        killdou = replace(killdou,"Great","") 
        killdou = replace(killdou,"Less","")
        m = getFirstMatch("(.*)[M|m][B|b].*", array(killdou))
        ' Sum up in MB
        if ubound(m) = 0 then
            total = total + m(0)
        end if

        g = getFirstMatch("(.*)[G|g][B|b].*", array(killdou))
        if ubound(g) = 0 then
            total = total + g(0)*1024
        end if
    next
    
    ' Report in MB
    unitMGTOG = total & "MB"
end function

' Accumulate the arrays
function unitMGTOG_original(arr)
    dim total, killdou, it, m, g
    total = 0
    for each it in arr
        if getDecimalSeparator()="," then
            killdou = replace(it,".","")
        else
            killdou = replace(it,",","")
        end if
        killdou = replace(killdou," ","")
        killdou = replace(killdou,"Than","")
        killdou = replace(killdou,"Greater","")
        killdou = replace(killdou,"Great","") 
        killdou = replace(killdou,"Less","")
        m = getFirstMatch("(.*)[M|m][B|b].*", array(killdou))
        ' Sum up in MB
        if ubound(m) = 0 then
            total = total + m(0)
        end if

        g = getFirstMatch("(.*)[G|g][B|b].*", array(killdou))
        if ubound(g) = 0 then
            total = total + g(0)*1024
        end if
    next
    
    ' Report in MB
    unitMGTOG = total & "MB"
end function

function formatForDisplay(val)
    ' Just handle size for now
    formatForDisplay = formatSizeForDisplay(val)
end function

function formatSizeForDisplay(size)
    dim m
    formatSizeForDisplay = size
    
	m = getFirstMatch("([\-]?\d+)MB", array(UCase(size)))
    if ubound(m) = 0 then
        if m(0) >= 1024 then
            formatSizeForDisplay = formatNumber(m(0)/1024,2) & "GB"
        else
            formatSizeForDisplay = m(0) & "MB"
        end if
    end if
    
    m = getFirstMatch("(\d+(\.\d+)*)GB", array(UCase(size)))
    if ubound(m) = 0 then
        if m(0) < 1 then
            formatSizeForDisplay = formatNumber(m(0)*1024,0) & "MB"
        elseif m(0) < 10 then
            formatSizeForDisplay = formatNumber(m(0),2) & "GB"
        else
            formatSizeForDisplay = formatNumber(m(0),0) & "GB"
        end if
    end if
end function

' Default compare logic - Handles numbers, size (MB/GB), speed (MHz/GHz), boolean, and strings (no patterns)
function passOrFail(expect,real)
    expect = changeMG(expect)
    real = changeMG(real)
    dim ke, kr, mee, mrr
    dim regEx 

    ' if there is null, take it as 0
    if strcomp(real,"")=0 then        
        passOrFail = "FAIL"
        exit function
    end if

    if strcomp(expect,"")=0 then        
        passOrFail = "FAIL"
        exit function
    end if

    ' Special case #1: check as boolean if expected value is true/false
    if (UCase(expect)="TRUE" or UCase(expect)="FALSE") then
        logInfo "passOrFail[BOOLEAN] - expected[" & expect & "] real[" & real & "]"
        if (UCase(expect) = UCase(real)) then
            passOrFail = "PASS"
            exit function
        else
            passOrFail = "FAIL"
            exit function
        end if
    end if

    ' Special case #2: check if value is a number (integers only for now due to globalization issues)
    if isNumeric(expect) and isNumeric(real) then
        logInfo "passOrFail[NUMBER] - expected[" & expect & "] real[" & real & "]"
        if (real >= expect) then
            passOrFail = "PASS"
            exit function
        else
            passOrFail = "FAIL"
            exit function
        end if
    end if
    
    ' Special case #3: check if value is a size (MB/GB)
    ' if there contains G,M in the value
    ' then it will unit them and compare
    if ubound(getFirstMatch("(GB|MB|GHz|MHz)$",array(expect)))>=0 then    
        logInfo "passOrFail[SIZE_SPEED] - expected[" & expect & "] real[" & real & "]"
        set regEx = new RegExp
        regEx.Pattern = "[a-zA-Z\s]{1,}"
        
        ke = regEx.replace(expect,"")
        if len(ke) = 0 then
            ke = 0
        end if

        kr = regEx.replace(real,"")
        if len(kr) = 0 then
            kr = 0
        end if

        mee = getFirstMatch("(.*)[G|g].*", array(expect))
        if ubound(mee) = 0 then
            ke = ke * 1024
        else
            ke = ke * 1
        end if

        mrr = getFirstMatch("(.*)[G|g].*", array(real))
        if ubound(mrr) = 0 then
            kr = kr * 1024
        else
            kr = kr * 1
        end if
        
        if ke > kr then
            passOrFail = "FAIL"
        else
            passOrFail = "PASS"
        end if    
    else
        ' Default case: String compare
        logInfo "passOrFail[STRING] - expected[" & expect & "] real[" & real & "]"
        if strComp(trim(expect),trim(real))=0 then
            passOrFail = "PASS"
        else
            passOrFail = "FAIL"
        end if
    end if
end function

' calculate the difference
function bigthan(expect,real)
    expect = changeMG(expect)
    real = changeMG(real)
    dim ke, kr, mee, mrr
    dim regEx 

    ' if there is null, take it as 0
    if strcomp(real,"")=0 then        
        bigthan = "0MB"
        exit function
    end if

    if strcomp(expect,"")=0 then        
        bigthan = "0MB"
        exit function
    end if

    ' if there contains G,M in the value
    ' then it will unit them and compare
    set regEx = new RegExp
    regEx.Pattern = "[a-zA-Z\s]{1,}"
    
    ke = regEx.replace(expect,"")
    if len(ke) = 0 then
        ke = 0
    end if

    kr = regEx.replace(real,"")
    if len(kr) = 0 then
        kr = 0
    end if

    mee = getFirstMatch("(.*)[G|g].*", array(expect))
    if ubound(mee) = 0 then
        ke = ke * 1024
    else
        ke = ke * 1
    end if

    mrr = getFirstMatch("(.*)[G|g].*", array(real))
    if ubound(mrr) = 0 then
        kr = kr * 1024
    else
        kr = kr * 1
    end if
    
    if ke > kr then
        bigthan = (ke-kr) & "MB"
    else
        bigthan = "0MB"
    end if    
end function

' ********************************************************************
' **** Logging utility methods ***************************************
' ********************************************************************

sub log(level, msg)    
    if WRITE_TO_LOG_FILE then
        appendToFile fmt("[%0s %0s] %8s: %0s", Array(date,time,level,msg)), LOG_FILE
    end if
end sub

sub deleteLogFile
    if CreateObject("Scripting.FileSystemObject").fileexists(LOG_FILE) then
        CreateObject("Scripting.FileSystemObject").DeleteFile(LOG_FILE)    
    end if
end sub

' log an INFO message
sub logInfo(msg)    
    log "INFO", msg
end sub

' log a WARNING message
sub logWarning(msg)    
    log "WARNING",msg
end sub

' Output to screen
sub logScreen(msg)
    wscript.echo msg
end sub

'**********************************************************************
'*************************** File Utilities ***************************
'**********************************************************************
sub fileWriting(text, fileName, mode)
    dim fso, textFile
    Set fso = CreateObject("Scripting.FileSystemObject")    
    ON ERROR RESUME NEXT
    Set textFile = fso.OpenTextFile(fileName, mode, True) 
    If Err.Number<>0 Then
        On Error GOTO 0
        Err.Clear
        ' Sleep and try again
        WScript.Sleep(500)
        set textFile = fso.OpenTextFile(fileName, mode, True) 
    End If
    textFile.writeline text
    textFile.close
end sub

' Append text to a file
sub appendToFile(text, fileName)  
	fileWriting text, fileName, 8 ' 8 = ForAppending
end sub

' Write text to a file (overwrite if needed)
sub writeToFile(text, fileName)    
	fileWriting text, fileName, 2 ' 2 = ForWriting
end sub

' Filter file 
function filterFile(fileName, patt)
    dim fso, textFile, line, dic, match
    set dic = createobject(TYPE_DICTIONARY)
    Set fso = CreateObject("Scripting.FileSystemObject")
    Set textFile = fso.OpenTextFile (fileName, 1) ' 1 = ForReading
    Do Until textFile.AtEndOfStream
        line = textFile.Readline
        match = getFirstMatch(patt, array(line))
        if ubound(match)=0 then
            if not dic.exists(match(0)) then
                dic.add match(0),""
            end if
        end if
    Loop    
    filterFile=dic.keys
end function

' Read the contents of a file into an array
' Each entry in the array is one line in the file
function readFile(fileName)
    dim fso, file, i, fileContents()
    
    Set fso = CreateObject("Scripting.FileSystemObject")
    Set file = fso.OpenTextFile(fileName, 1) ' 1 = ForReading    

    i = 0
    Do Until file.AtEndOfStream
        Redim Preserve fileContents(i)
        fileContents(i) = file.ReadLine
        i = i + 1
    Loop
    file.Close
    readFile = fileContents
end function

' Read the contents of a file as a dictionary
' Returns an array, with a Dictionary as the first item
function ppread(fileName, sep)
    dim fso, textFile, line, dic, splitLine
    set dic = createobject(TYPE_DICTIONARY)
    Set fso = CreateObject("Scripting.FileSystemObject")
    Set textFile = fso.OpenTextFile(fileName, 1)
    Do Until textFile.AtEndOfStream
        line = trim(textFile.Readline)
        if Instr(line,sep) > 0 then
            splitLine = split(line,sep)
            if not dic.exists(splitLine(0)) then
                dic.add splitLine(0),splitLine(1)
            end if
        end if
    Loop    
    ppread=array(dic)
end function

' Read all the file names in the given directory into an array
function allFiles(filepath)
    dim dic, fso, folder, files, fileNames(), i, file
    set dic = createobject(TYPE_DICTIONARY)
    Set fso = CreateObject("Scripting.FileSystemObject")
    Set folder = fso.GetFolder(filepath)
    set files = folder.files
    ReDim Preserve fileNames(files.Count)
    i = 0
    For Each file in files
        fileNames(i) = file.name
        i=i+1
    Next
    allFiles = fileNames
end function

' Find the suitable file name for each product codes and version
function findSuitableFile(pd,version,suf,filepath)
    dim afs, pfs, vfs, result    

    result = ""

    afs = getFirstMatch("(.*\." & suf & "$)", allFiles(filepath))
    pfs = getFirstMatch("(^" & pd & "_.*)", afs)
    vfs = getFirstMatch("(.*" & version & ".*)", pfs)
    
    if ubound(vfs) >= 0 then
        findSuitableFile = findNewest(vfs)
        exit function
    end if

    if suf = "bat" then
        findSuitableFile = "common.bat"
        exit function
    elseif suf = "cfg" then
        logScreen "WARNING: Cannot find configuration file for " & pd & " " & version & ". Used " & findNewest(pfs) & " instead"
        logWarning "can NOT find configuration file for " & pd & " " & version & ". Used " & findNewest(pfs) & " instead"
        findSuitableFile = findNewest(pfs)
        exit function
    end if
end function

' find the newest configure file
function findNewest(arr)
    dim result, f
    result = ""
    for each f in arr
        if strcomp(result,f,1) < 0 then
            result = f
        end if
    next
    findNewest = result
end function

' execute commands and return
function exeCommand(cmd)
    dim fsh, result, exeRs, strStdErr
    set fsh = wscript.createObject("wscript.shell")
    result = ""
    Set exeRs = fsh.Exec(cmd)
    
    strStdErr = exeRs.StdErr.ReadAll()
    
    if len(strStdErr) > 0 then
        logWarning "Command [" & cmd & "] failed.  StdErr:" & vbNewLine & strStdErr
    end if

    Do Until exeRs.StdOut.AtEndOfStream
        t = exeRs.StdOut.readline()
        result = result & t
    Loop    

    exeCommand = result
end function

' execute command and find the specified lines
function filterCommand(cmd, line_patt, after_line, info_patt)
    dim fsh, result, t, cou, tocou, exeRs
    set fsh = wscript.createObject("wscript.shell")
    cou = 0
    tocou = "FALSE"
    result = ""
    Set exeRs = fsh.Exec(cmd)
    
    'if len(exeRs.StdErr.ReadAll()) > 0 then
    '    wscript.echo exeRs.StdErr.ReadAll()
    'end if

    Do Until exeRs.StdOut.AtEndOfStream
        t = exeRs.StdOut.readline()
        if tocou = "FALSE" then
            if isMatch(line_patt, t) then 
                tocou = "TRUE" 
                if after_line = 0 then
                    filterCommand = join(getFirstMatch(info_patt, array(t)))
                    exit function
                end if
            end if
        else
            cou = cou + 1
            if cou = after_line then
                filterCommand = join(getFirstMatch(info_patt, array(t))) 
                exit function
            end if

        end if
    Loop 
end function

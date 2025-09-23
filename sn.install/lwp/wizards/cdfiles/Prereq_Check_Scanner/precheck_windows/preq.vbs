'************* Begin Standard Header - Do not add comments here ****************
'
' File:     preq.vbs
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

' This script is the main entry point for the IBM Prerequisite Scanner

' Force all variables to be declared
option explicit

' PRS Version Identification
const ITPRS_VERSION = "1.1.1.7"
const ITPRS_BUILD_ID = "20110825"

' Debug mode
dim debugEnabled
debugEnabled = false ' Can turn this on via the 'debug' command line parameter

' Script Location
dim PRS_BASE_DIR, scriptFullName
scriptFullName = WScript.ScriptFullName
PRS_BASE_DIR =  Left( scriptFullName, InStrRev(scriptFullName, WScript.ScriptName) - 1)

' Constants
dim LOG_FILE, CFG_HOME, TEMP_DIR, TEMPFILE_BAT, CODENAME_CFG_FILE

' Can't declare these as 'const' since they are built from PRS_BASE_DIR
LOG_FILE                = PRS_BASE_DIR & "precheck.log"               ' Trace log file name
CFG_HOME                = PRS_BASE_DIR & "Windows"                    ' Directory containing configuration files
TEMP_DIR                = PRS_BASE_DIR & "temp"                       ' Temporary directory used during PRS execution
TEMPFILE_BAT            = PRS_BASE_DIR & "__tempExecPRS.bat"          ' Temporary batch file for dynamic execution
CODENAME_CFG_FILE       = PRS_BASE_DIR & "codename.cfg"               ' File that maps product codes to descriptions

const COMMON_FILE       = "localhost_hw.txt"               ' Temporary file containing discovered property values
const RESULT_FILE		= "result.txt"
const WRITE_TO_LOG_FILE        = True                             ' Set to true to enable trace log
const PDS_PATTERN              = "([a-zA-Z0-9]{3})_[0-9]{8}\.cfg" ' Pattern to match pd codes
const VER_PATTERN              = "[a-zA-Z0-9]{3}_([0-9]{8})\.cfg" ' Pattern to match pd versions
const DRIVE_PATTERN            = "[A-Za-z]{1}:[\\/]"
const DRIVE_LETTER_PATTERN     = "^([A-Za-z]{1}:)[\\/]?"
const DICTIONARY               = "Scripting.Dictionary"           ' Scripting.Dictionary data type

' Return codes
const RETURN_CODE_PASS  = 0
const RETURN_CODE_FAIL  = 1
const RETURN_CODE_USAGE = -1

const CPU_NAME          = "CPU Name"                     ' CPU Name property

const RESULT_PATTERN    = "%60s%8s%80s%80s"

const MEMORY_NAME         = "Memory"
const DISK_NAME           = "Disk"
const TOTAL_NAME          = "TOTAL"
const ALL_COMPONENTS_NAME = "ALL COMPONENTS"

' Can't do const arrays, so dim and assign
dim CPU_ARRAY, RESULT_HEADER, RESULT_HEADER2

CPU_ARRAY         = array("intel.cpu","risc.cpu")  ' Valid CPU property names
RESULT_HEADER     = array("Property","Result","Found","Expected")
RESULT_HEADER2    = array("========","======","=====","========")

' Parameters Definition
dim info                   ' Dictionary contains pd, cfg and exe
dim exeinfo                ' Dictionary contains exe
dim codename               ' Dictionary contains pd code and pd_name
dim results, failedResults ' Dictionary contains results
dim fso_codename           ' File object
dim overallResult          ' Overall pass/fail result used for the return code

dim t 'Variable/index for iterating over collections
dim arrmem(), arrdisk(), customDisk()

' Results
set results       = createObject(DICTIONARY)
set failedResults = createObject(DICTIONARY)

dim objWMI, objShell
Set objWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
set objShell = CreateObject( "WScript.Shell" )

' *********************************************************
' Step 0 - Deal with input parameters
dim currentArg, argCount, pflag, pathflag, dflag
dim codesAndVersions, spath, flagShowDetail, deliverPara

' Remove any existing trace log
deleteLogFile

' Always write PRS version and machine info to log file
logInfo getVersionInfo()
logInfo getMachineInfo()

argCount = WScript.arguments.count
if argCount = 0 then
    showUsageAndQuit
end if

pflag = 0
pathflag = 0
dflag = 0
flagShowDetail = False
currentArg=0
logInfo "Parsing command-line arguments"
do while currentArg < argCount
    logInfo "arguments(" & currentArg & ") = " & WScript.arguments(currentArg)
    if isMatch("(-[p|P])", WScript.arguments(currentArg)) then
        pflag = currentArg
        currentArg = currentArg + 1
        if (currentArg < argCount) then
            deliverPara = WScript.arguments(currentArg)
        else
            WScript.Echo "-p must be followed by name=value pairs"
            showUsageAndQuit
        end if
    elseif isMatch("^(path|PATH)$", WScript.arguments(currentArg)) then
        pathflag = currentArg
        currentArg = currentArg + 1
        if (currentArg < argCount) then
            spath = WScript.arguments(currentArg)
        else
            WScript.Echo "Missing PATH arguments"
            showUsageAndQuit
        end if
    elseif "detail" = LCase(WScript.arguments(currentArg)) then
        dflag = currentArg
        flagShowDetail = True
    elseif "debug" = LCase(WScript.arguments(currentArg)) then
        debugEnabled = true
    elseif isMatch("(path=).*", LCase(WScript.arguments(currentArg))) then
        pathflag = currentArg
        spath = WScript.arguments(currentArg)
    end if
    currentArg = currentArg + 1
loop

' codesAndVersions is a comma-separated list of 3-letter codes optionally followed
' by a space and a 8-character version
' example 1 - "DMO"
' example 2 - "DMO 01000000"
' example 3 - "DMO,COZ 07020000,COY 07020000"
codesAndVersions = WScript.arguments(0)
logInfo "codesAndVersions = " & codesAndVersions
if pflag <> 1 and pathflag <> 1 and dflag <> 1 then
    if pflag + pathflag + dflag < 1 then 
        if argCount > 1 then
            showUsageAndQuit
        end if
    else 
        showUsageAndQuit
    end if
end if

if not isMatch(".+(=).*", deliverPara) then
    dim pendflag
    pendflag = 0

    if pflag > 0 then
        if pathflag > pflag then
            pendflag = pathflag - 1
        end if

        if dflag > pflag then
            if pendflag = 0 then
                pendflag = dflag - 1
            elseif dflag < pendflag then
                pendflag = dflag - 1
            end if
        end if

        if pendflag > pflag then
            deliverPara = deliverPara & "=" & WScript.arguments(pflag+2)
            for t = pflag+3 to pendflag step 2
                deliverPara = deliverPara & "," & WScript.arguments(t) & "=" & WScript.arguments(t+1)
            next
        else
            pendflag = WScript.arguments.count - 1
            deliverPara = deliverPara & "=" & WScript.arguments(pflag+2)
            
            for t = pflag+3 to pendflag step 2
                deliverPara = deliverPara & "," & WScript.arguments(t) & "=" & WScript.arguments(t+1)
            next
        end if
    end if
end if

if "path=" = LCase(Left(spath,5)) then
    spath = Right(spath,Len(spath)-5)
end if

if flagShowDetail then
    ' Output PRS version and OS info to screen if 'detail' was set
    logScreen getVersionInfo()
    logScreen getMachineInfo()
else
    logInfo "Tip: Use the 'detail' parameter to get verbose output."
end if

if spath="" then
    logInfo "No PATH paramter specified. The default value of C:\ibm\ITM will be used"
    spath = "C:\ibm\ITM"
end if

if not deliverPara="" then
    logInfo "The parameters will be passed to the sub-script: " & deliverPara
end if

' --------------------------------------------------------------------------------------

' Pattern for recognizing and parsing conditional sections in the configuration file
' If left brackets are used in the value section (e.g. for regular expression matching of env variables),
' they should be escaped.  For example, in a config file, the following will process the section if the
' environment variable ZIP_CODE is a 5-digit zip code
' [ZIP_CODE:[\[[1-9\][0-9\]{4}\\]]
'const SECTION_PATTERN = "(\[(!?@?[a-z0-9_\-]+):(.*?([^\\])\]|(\\[^\]])))(?:(?:[\s]+)|$)"
const SECTION_PATTERN = "(\[(!?@?[a-z0-9_\-]+):(.*?(?:[^\\])\]|(?:\\[^\]])))([\s|&]|$)"

' Built-In Variables
dim builtInVars, osTypes, osVer, osSP, osArch

' Populate dictonary of built-in variable names
set builtInVars = createObject("Scripting.Dictionary")

' OS Types
osTypes = "Windows"
dim allOS, os, ProductType

set allOS = objWMI.ExecQuery("Select Version, ProductType, ServicePackMajorVersion, ServicePackMinorVersion, OtherTypeDescription from Win32_OperatingSystem",,48)

if "SWbemObjectSet" <> typename( allOs ) then
	set allOS = objWMI.ExecQuery("Select Version, ServicePackMajorVersion, ServicePackMinorVersion, OtherTypeDescription from Win32_OperatingSystem",,48)
end if


For Each os in allOS
	On Error Resume Next
		ProductType = os.ProductType
		if Err.Number <> 0 then
			ProductType = -1
		end if
	On Error Goto 0

    if ProductType=1 then
        ' Workstation (desktop) OS
        osTypes = osTypes + "|Windows Workstation"
        if versionCompare("5.0.*", os.Version)=0 then
            ' Windows 2000 Workstation
            osTypes = osTypes + "|Windows 2000"
        elseif versionCompare("5.1.*", os.Version)=0 then
            ' Windows XP
            osTypes = osTypes + "|Windows XP"
        elseif versionCompare("5.2.*", os.Version)=0 then
            ' Windows XP 64-bit
            osTypes = osTypes + "|Windows XP"
        elseif versionCompare("6.0.*", os.Version)=0 then
            ' Windows Vista
            osTypes = osTypes + "|Windows Vista"
        elseif versionCompare("6.1.*", os.Version)=0 then
            ' Windows 7
            osTypes = osTypes + "|Windows 7"
        else
            ' Unrecognized OS
            osTypes = osTypes + "|Windows UnknownVersion"
        end if
    else
        ' Server
        osTypes = osTypes + "|Windows Server"
        if versionCompare("5.0.*", os.Version)=0 then
            ' Windows 2000 Server
            osTypes = osTypes + "|Windows 2000"
        elseif versionCompare("5.1.*", os.Version)=0 then
            ' Unknown server OS
        elseif versionCompare("5.2.*", os.Version)=0 then
            ' Windows 2003/2003 R2
            osTypes = osTypes + "|Windows Server 2003"
            ' Check for R2
            if (not isNull(os.OtherTypeDescription)) and os.OtherTypeDescription="R2" then
                osTypes = osTypes + "|Windows Server 2003 R2"
            end if
        elseif versionCompare("6.0.*", os.Version)=0 then
            ' Windows Server 2008
            osTypes = osTypes + "|Windows Server 2008"
        elseif versionCompare("6.1.*", os.Version)=0 then
            ' Windows Server 2008 R2
            osTypes = osTypes + "|Windows Server 2008|Windows Server 2008 R2"
        else
            ' Unrecognized OS
            osTypes = osTypes + "|Windows UnknownVersion"
        end if        
    end if
    
    osVer = os.Version
    osSP = os.ServicePackMajorVersion & "." & os.ServicePackMinorVersion
Next

logDebug "Detected operating system types are [" & osTypes & "]"
logDebug "Detected operating system version [" & osVer & "]"
logDebug "Detected service pack level [" & osSP & "]"
builtInVars.add "OSType", osTypes
builtInVars.add "OSVersion", osVer
builtInVars.add "OSServicePack", osSP

' 32/64 Bit OS
dim programFilesX86Dir
programFilesX86Dir = objShell.ExpandEnvironmentStrings("%PROGRAMFILES(X86)%")
if (programFilesX86Dir="%PROGRAMFILES(X86)%") then
    osArch = "32-bit"
else
    osArch = "64-bit"
end if
builtInVars.add "OSArch", osArch

function readConfigWithSections(fileName)
    dim dic, fso, textFile, sectionApplies, currentSection, lineNum, _
        diskNum, line, splitline, key, value
    
    set dic = Createobject("Scripting.Dictionary")
    Set fso = CreateObject("Scripting.FileSystemObject")
    
    logInfo "Reading configuration file [" & fileName & "]"

    Set textFile = fso.OpenTextFile(fileName, 1)
    sectionApplies = true
    currentSection = "[*Global*]"
    lineNum = 1
    diskNum = 1
    Do Until textFile.AtEndOfStream
        line = trim(textFile.Readline)
        if Len(line) > 0 then
            ' Ignore comment lines
            dim numCPUStr
            numCPUStr = "# CPU="
            if not Left(line,1) = "#" or (Len(line)>=Len(numCPUStr) and Left(line,Len(numCPUStr))=numCPUStr) then
                ' Check if it's a section header and set sectionApplies variable
                if isSection(line) then
                    logInfo "Line #" & lineNum & " is a conditional section: " & line
                    sectionApplies = processSection(line)
                    currentSection = line
                else
                    ' If section applies to current machine, add line to dic
                    if sectionApplies then
                        logDebug "Processing line # " & lineNum
                        logDebug "          line: " & line
                        logDebug " under section: " & currentSection
						'If the directory ends with one or more '\' followed by a ','(comma), replace it with a single ','(comma)
						dim regEx
						Set regEx = New RegExp
						regEx.Pattern = "[\\]+,"
						regEx.IgnoreCase = True
						line = regEx.Replace(line, ",")
						set regEx = nothing
                        if InStr(line,"=") > 0 then
                            ' Break up key & value on '='
                            ' I would like to use a stricter regular expression here, but we're
                            ' using the old 'split()' way to ensure compatibility - for now
                            splitline = split(line,"=")
                            ' Convert deprecated '# CPU' to 'numCPU' since # is used for comments
                            if (not key=numCPUStr) then
                                key = splitline(0)
                            else
                                key = "numCPU"
                            end if
                            value = splitline(1)
                            ' Old code couldn't handle multiple occurrances of the same
                            ' property - 'Disk' in this case.  Ideally Disk would be a '.*'
                            ' property like network.availablePorts.*, but since it's already
                            ' implemented on UNIX, we're just going to do it that way for now
                            if key="Disk" then
                                ' Expand environment variables in Disk property expected value
                                value = objShell.ExpandEnvironmentStrings(value)
                                dim diskLoc
                                ' Uncomment these two lines in place of the ones below to just show the drive letter
                                ' instead of the full path if it gets too long to display
                                'diskLoc = getDriveFromDiskProperty(value, getDriveFromDiskProperty(spath,"C:\"))
                                'key = "Disk#" & diskNum & " (" & diskLoc & ")"
                                diskLoc = getDirFromDiskProperty(value, spath)
                                key = "Disk#" & diskNum & " (" & diskLoc & ")"
                                diskNum = diskNum + 1
                            end if
                            logDebug "Line #" & lineNum & " applies.  Adding " & vbNewLine & _
                                     vbTab & " key=" & key & vbNewLine & _
                                     vbTab & "value=" & value
                            
                            dic.add key,value        
                        else
                            logInfo "Invalid configuration file syntax: line #" & lineNum & " - Equals sign expected."
                        end if
                    else
                        logDebug "Line #" & lineNum & " is in a section that does not apply"
                    end if            
                end if
            else
                logDebug "Line #" & lineNum & " is a comment line.  Ignoring"
            end if
        else
            logDebug "Line #" & lineNum & " is blank.  Ignoring."
        end if
        
        lineNum = lineNum + 1
    Loop
    
    logInfo "Filtered contents for config file [" & fileName & "]: " & varToString(array(dic))
    
    readConfigWithSections = array(dic)
end function

function isSection(line)
    ' Sections start with a left bracket
    isSection = Left(line,1)="["
end function

function processSection(line)
    dim sectionRegEx, matchCount, result, subMatchCount, condition, _
        key, value, operation, testResult, expressionMatch, expressionMatches

    Set sectionRegEx = new RegExp
    sectionRegEx.IgnoreCase = true
    sectionRegEx.Global = true
    sectionRegEx.Pattern = SECTION_PATTERN
    
    Set expressionMatches = sectionRegEx.Execute(line)
    matchCount = 0
    result = true
    operation = "&"
    for each expressionMatch in expressionMatches
        dim nextOperation
        
        matchCount = matchCount + 1
        subMatchCount = 0
        condition = expressionMatch.subMatches(0)
        key = expressionMatch.subMatches(1)
        value = expressionMatch.subMatches(2) & ""
        nextOperation = expressionMatch.subMatches(3) & ""
        value = Left(value,Len(value)-1)
        value = replace(value, "\]", "]")
        logDebug "Condition to process : -->" & condition & "<--"
        logDebug "         Key : -->" & key & "<--"
        logDebug "       Value : -->" & value & "<--"
        logDebug "   Operation : -->" & operation & "<--"
        logDebug "NxtOperation : -->" & nextOperation & "<--"
        testResult = testSectionVariable(key, value)
        logDebug "Condition evaluated to " & testResult
        if operation="|" then
            result = result or testResult
        else
            result = result and testResult
        end if
        operation = nextOperation
    next    
    if matchCount = 0 then
        result = false
        logInfo "Invalid section syntax -->" & line & "<--"
    end if
    
    logInfo "Section -->" & line & "<-- evaluated to " & result
    
    processSection = result
end function

function testSectionVariable(key, value)
    dim result, splitVal, shellObj, envObj, val, realValue, regEx

    result = false
    ' Users can use a '!' to expect a 'false' result
    dim invertResult
    invertResult = false
    
    if Left(key,1)="!" then
        invertResult = true
        key = replace(key,"!","")
    end if
    
    if not isNull(builtInVars.Item(key)) and not builtInVars.Item(key)="" then
        ' Built-in variable
        logDebug "[" & key & "] is a built-in variable with value [" & builtInVars.Item(key) & "]"
        if key="OSServicePack" or key="OSVersion" then
            if versionCompare(value,builtInVars.Item(key)) <= 0 then
                result = true
            end if
        else
            splitVal = split(builtInVars.Item(key),"|")
            for each val in splitVal
                logDebug "Testing [" & value & "] against [" & val & "]"
                if val=value then
                    result = true
                    exit for
                end if
            next
        end if
    else        
        ' Check environment vars
        logDebug "[" & key & "] is not a built-in variable.  Checking environment variables..."
        ' In UNIX PRS, environment variables in sections start with '@', so support that here
        key = replace(key,"@","")
        Set shellObj = WScript.CreateObject("WScript.Shell")
        Set envObj = shellObj.Environment("Process")
        realValue = envObj(key)
        if isNull(realValue) then
            logDebug "Environment variable [" & key & "] not found.  Treating as false."
            result = false
        else
            logDebug "Environment variable [" & key & "] found.  realValue = [" & realValue & "]"
            ' Treat the expected value as a regular expression
            set regEx = new RegExp
            regEx.IgnoreCase = true
            regEx.Global = true
            regEx.Pattern = value
            
            if regEx.Test(realValue) then
                result = true
            else
                result = false
            end if
        end if
    end if
    
    if invertResult then
        result = not result
        logDebug "Result inverted to a final value of " & result
    else
        logDebug "Final result is " & result
    end if
    
    testSectionVariable = result
end function

sub logDebug(msg)
    if debugEnabled then
        log "DEBUG", msg
    end if
end sub

' ----------------------- Version Comparison Common Code -----------------------------
' Generic function for comparing 2 version strings
'
' Parameters
'       ver1 The first version string
'       ver2 The second version string
'
' ver1 and ver2 are expected to be dot-separated version strings (e.g. 1.0.0.4, 2.3, 3.40.26.7800, 2.3.a)
' Version strings can have any number of parts.  When comparing versions with different numbers of parts,
' missing parts of the shorter version string will be treated as if there was a zero there.  If any
' non-numeric characters are included in a version part, those corresponding parts will be compared as
' strings and not parsed into numeric form
'
' Returns
'       1 version1 > version2
'      -1 version1 < version2
'       0 version1 = version2
'
' Special cases:
' RESULT    version 1    version 2
'   0         empty        empty
'   1      validString     empty
'  -1         empty     validString
'
' NOTE: This function should eventually move to common_functions.vbs
function versionCompare(ver1, ver2)
    'WScript.echo "Comparing [" & ver1 & "] to [" & ver2 & "]"
    
    Const UNASSIGNED = "*UNASSIGNED*"
    Dim v1Default, v2Default
    
    ' Handle special cases:
    if (IsEmpty(ver1) and IsEmpty(ver2)) Then
        versionCompare = 0
        exit function
    end if
    if (IsEmpty(ver1) and not IsEmpty(ver2)) Then
        versionCompare = -1
        exit function
    end if
    if (not IsEmpty(ver1) and IsEmpty(ver2)) Then
        versionCompare = 1
        exit function
    end if    
    
    Dim ver1Parts, ver2Parts
    
    ' Versions are not empty.  Break into parts and compare numbers
    ver1Parts = Split(ver1,".")
    ver2Parts = Split(ver2,".")
    
    Dim v1Size, v2Size
    v1Size = ubound(ver1Parts)
    v2Size = ubound(ver2Parts)
    
    ' If last version part is "*", treat all missing parts as "*" (so 2.* matches 2.1.3, for example)
    if (v1Size > v2Size) Then
        Redim Preserve ver2Parts(v1Size)
        if (ver2Parts(v2Size)="*") Then
            for i = v2Size to v1Size
                ver2Parts(i) = "*"
            next
        end if
    elseif (v2Size > v1Size) Then
        Redim Preserve ver1Parts(v2Size)
        if (ver1Parts(v1Size)="*") Then
            for i = v1Size to v2Size
                ver1Parts(i) = "*"
            next
        end if
    end if
    
    Dim i
    i = 0
    
    Do While (i<=ubound(ver1Parts) or i<=ubound(ver2Parts))
        Dim v1, v2, v1Str, v2Str
                
        v1Str = UNASSIGNED
        v2Str = UNASSIGNED
               
        if (i<=ubound(ver1Parts)) Then
            on error resume next
            v1 = Int(ver1Parts(i))
            if not Err=0 Then
                v1Str = ver1Parts(i)
                if (i<=ubound(ver2Parts)) Then
                    v2Str = ver2Parts(i)
                else 
                    v2Str = "0"
                end if
            end if
        else 
            v1 = 0
        end if
        
        if (i<=ubound(ver2Parts)) Then
            on error resume next
            v2 = Int(ver2Parts(i))
            if not Err=0 Then
                if (i<=ubound(ver1Parts)) Then
                    v1Str = ver1Parts(i)
                else 
                    v1Str = "0"
                end if
                v2Str = ver2Parts(i)
            end if
        else 
            v2 = 0
        end if
        
        if (not v1Str=UNASSIGNED or not v2Str=UNASSIGNED) Then                            
            if (IsEmpty(v1Str)) Then
                v1Str = "0"
            end if
            if (IsEmpty(v2Str)) Then
                v2Str = "0"
            End if
            
            'WScript.echo "Comparing as strings: " & v1Str & " : " & v2Str            
            ' Compare as Strings if either part could not be converted to a number
            if (not v1Str="*" and not v2Str="*") Then
                if (not v1Str=v2Str) Then
                    versionCompare = StrComp(v1Str,v2Str)
                    exit function
                end if
            end if
        else
            'WScript.echo "Comparing as numbers: " & v1 & " : " & v2

            if (v1 > v2) Then
                versionCompare = 1
                exit function
            end if
            if (v2 > v1) Then
                versionCompare = -1
                exit function
            end if
        end if
    
        i = i + 1
    Loop
    
    ' If we got here, versions must be equal
    versionCompare = 0
    
end function
' --------------------------------------------------------------------------------------

' Step 1 - Scan for the product codes under Windows folder
logInfo "Step 1 - Scanning product codes"
dim codeVersion, codeVersionArray, code, version
for each codeVersion in split(codesAndVersions,",")
    codeVersionArray = split(trim(codeVersion)," ")
    code = codeVersionArray(0)

    if len(replace(code," ","")) <> 3 then
        logError "Invalid product code [" & code & "].  Product codes must be 3 characters."
        logScreen "Invalid product code [" & code & "].  Product codes must be 3 characters."
        WScript.quit RETURN_CODE_USAGE
    end if
    if ubound(codeVersionArray) > 0 then 
        if ubound(codeVersionArray) > 1 then
            ' Max of 2 parts per code (code + optional version)
            logScreen "Separate the product codes with a comma" & _
                vbnewline & "For example: KNT, KMS 620, KUD"
            WScript.quit RETURN_CODE_USAGE
        end if

        version = codeVersionArray(1)
        if not isNumeric(version) then
            logScreen "Non-numeric version specified: [" & version & "]" & _
                vbnewline & "Example: KNT, KMS 620, KUD"
            WScript.quit RETURN_CODE_USAGE
        end if
    end if
next

info = scanProductCodes(codesAndVersions)

' step 1.5 adding some default parameters to deliverPara for some specific product
' here deal with the oracle product
logInfo "Step 1.5 - Adding default parameters if necessary"
if info(0).exists("KOR") then
    dim kor_para
    kor_para = getFirstMatch(("(^KOR.instance=.*)"), split(deliverPara,","))
    if ubound(kor_para) < 0 then
        deliverPara = deliverPara & ",KOR.instance=1"
    end if
    kor_para = getFirstMatch(("(^KOR.[t|T]emp[d|D]atafile=.*)"), split(deliverPara,","))
    if ubound(kor_para) < 0 then
        deliverPara = deliverPara & ",KOR.TempDatafile=" & spath
    end if
    kor_para = getFirstMatch(("(^KOR.[d|D]atafile=.*)"), split(deliverPara,","))
    if ubound(kor_para) < 0 then
        deliverPara = deliverPara & ",KOR.Datafile=" & spath
    end if 
end if

' Adding Deployment Engine info - Need to set these here so disk space will get summed up
' The real paths will get set in the DEZ_* collection script.
if info(0).exists("DEZ") then
    dim dez_para
    dez_para = getFirstMatch(("(^DEZ.commonPath=.*)"), split(deliverPara,","))
    if ubound(dez_para) < 0 then
        deliverPara = deliverPara & ",DEZ.commonPath=C:\"
    end if
    dez_para = getFirstMatch(("(^DEZ.installPath=.*)"), split(deliverPara,","))
    if ubound(dez_para) < 0 then
        deliverPara = deliverPara & ",DEZ.installPath=C:\"
    end if
    dez_para = getFirstMatch(("(^DEZ.tempPath=.*)"), split(deliverPara,","))
    if ubound(dez_para) < 0 then
        dim tempLoc, de_wshShell
        set de_wshShell = WScript.CreateObject("WScript.Shell")
        tempLoc = de_wshShell.ExpandEnvironmentStrings("%TEMP%")
        if tempLoc="" then
            tempLoc = "C:\"
        end if
        deliverPara = deliverPara & ",DEZ.tempPath=" & tempLoc
    end if
end if

logInfo "deliverPara=[" & deliverPara & "]"

' find the exe files
set exeinfo = createobject(DICTIONARY)

for each t in info(0).keys
    logInfo t & " will read: " & info(0).item(t).item("cfg") & " and will execute script: " & info(0).item(t).item("exe")
    if not exeinfo.exists(info(0).item(t).item("exe")) then
        exeinfo.add info(0).item(t).item("exe"), split(info(0).item(t).item("cfg"),".")(0) & ".txt"
    end if
next

' step 2 - execute 
logInfo "Step 2 - Collect properties"
dim tc
if exeinfo.exists("common.bat") then
    tc = ubound(exeinfo.keys) + 1
else
    tc = ubound(exeinfo.keys) + 2
end if 
logInfo "start commands: [ " & tc & " scripts will be called ]"

collectdata(exeinfo)
logInfo "end of calling commands"

' step 3 - Analysis
logInfo "Step 3 - Analysis"
'feature 145586
Set fso_codename = CreateObject("Scripting.FileSystemObject")
if fso_codename.FileExists(CODENAME_CFG_FILE) then
    logInfo "codename file found"
    codename = ppread(CODENAME_CFG_FILE, "=")
    logInfo "ppread completed"
else
    dim tempDic
    logInfo "no codename file found"
    Set tempDic = createobject(TYPE_DICTIONARY)
    for each t in info(0).keys
        tempDic.add t,t
    next
    codename = array(tempDic)
end if
overallResult = analysis(info(0))

' step 4 - generate reports 
logInfo "Step 4 - Generate reports"
generateReport

' step 5 - clean up temp folder
logInfo "Step 5 - Clean up"
clean_tempfolder

logScreen ""
logScreen "Details also available in " & PRS_BASE_DIR & RESULT_FILE
logInfo "prereq_checker.bat returns : "+cstr(overallResult)

' Set return code
WScript.quit overallResult

' *********************************************************
' ********************* Execution End *********************
' *********************************************************

' *********************************************************
' ******************** Helper Functions ********************
' *********************************************************

function getDiskFreeSpace(path)
    dim fso, disk, size
    
    set fso = CreateObject("Scripting.FileSystemObject")
    
    on error resume next
    set disk = fso.GetDrive(path)
    
    if err.number > 0 then
        logInfo "Unable to query available space for [" & path & "]"
        size = "0MB"
    else
        ' Value returned is in bytes.  Convert to MB
        size = CLng((disk.FreeSpace/1024)/1024) & "MB"
        if err.number > 0 then
            logInfo "Unable to retrieve available space for [" & path & "]"
            size = "0MB"
        end if
    end if
    
    on error goto 0
    
    logInfo "Disk free space for [" & path & "] = " & size
    getDiskFreeSpace = size
end function

' Scan for product codes
function scanProductCodes(pdcodesAndVersionss)
    dim existingCodes   ' all the pd codes available as config files
    dim inputpds        ' pd codes to execute (from the command line - includes versions)
    dim act_pds         ' the pd codes will be checked
    dim config_execu    ' the Dictionary to contain configuration file and execution file
    dim input_cv        ' dictionary contains input code and version
    dim redic
    dim pd, cfg, iexe
    
    pdcodesAndVersionss = UCase(pdcodesAndVersionss)
    logInfo "Input PD codes and versions = [" & pdcodesAndVersionss & "]"
    
    logInfo "Finding available configuration files in [" & CFG_HOME & "]"

    existingCodes = getFirstMatch(PDS_PATTERN, allFiles(CFG_HOME))
    
    inputpds = getFirstMatch("([A-Za-z0-9]{3})", split(pdcodesAndVersionss,","))
    if Ubound(notInLatter(inputpds,existingCodes,"not")) > -1 then
        logWarning "Unable to find the configurations: [ " & join(notInLatter(inputpds,existingCodes,"not"),",") & " ]"
    end if

    if ubound(notInLatter(inputpds,existingCodes,"in")) < 0 then
        logWarning "No valid product codes found."
        WScript.quit RETURN_CODE_USAGE
    end if
    
    ' get the will checked product codes
    act_pds = notInLatter(inputpds,existingCodes,"in")
    input_cv = parsecv(pdcodesAndVersionss)

    set redic = createobject(DICTIONARY)
    for each pd in act_pds
        set config_execu = createobject(DICTIONARY)
        cfg = findSuitableFile(pd,input_cv(0).item(pd),"cfg",CFG_HOME)        
        iexe= findSuitableFile(pd,input_cv(0).item(pd),"bat",CFG_HOME)        
        config_execu.add "cfg",cfg
        config_execu.add "exe",iexe
        redic.add pd,config_execu
    next
    logInfo "config file mapping = " & dictionaryToString(redic)
    scanProductCodes = array(redic)
end function

' collect the data for all the specified params in the cfg file	
sub collectdata(info)	
	'	Algorithm : Dilip Muthukurussimana(19-Jan-2011)
	'	----------------------------------------------------
	'	1. Open the cfg file and read one line
	'	2. If it is not a useful line(eg. comment, blank line, section, Disk check etc), go to the next iteration
	'	3. Split the line by "=" and take the content of the first subset (ie, the left side of "=")
	'	4. Check if there is any vbs or bat file similar to the check
	'	5. If yes, get the ouput of it
	'	6. Else report the issue, and continue with the next line
	dim args,exeRs, exefile, serr, sh, co, i
	dim cfg_file, objfso, sdata, regex, sline, t, arrlines	
	if not CreateObject("Scripting.FileSystemObject").folderexists(TEMP_DIR) then
		CreateObject("Scripting.FileSystemObject").createfolder(TEMP_DIR)
	end if	

	for each t in info.keys	
		cfg_file = CFG_HOME & "\" & split(info.item(t),".")(0)+".cfg"
		logDebug "The config file is: " & cfg_file
	next	

	'Create a File System Object	
	set objfso = createobject("scripting.filesystemobject")

	'Open the corresponding config file - sdata now contains the contents of the config file
	set sdata = objfso.opentextfile(cfg_file, 1)

	set sh = WScript.createobject("WScript.Shell")

	' Here we should first execute the common.bat		
	if info.exists("common.bat") then
		info.remove "common.bat"
	end if		

	writeToFile "@echo off" & vbnewline & "cd " & CFG_HOME & vbnewline & "common.bat" & " " & spath & " " & chr(34) & _ 	
			deliverPara & chr(34), TEMPFILE_BAT

	logInfo string(2, " ") & "calling " & "common.bat" & " [ " & info.count & " scripts left ]"		
	Set exeRs = sh.Exec(TEMPFILE_BAT)		
	sErr = trim(exeRs.StdErr.ReadAll())		
	if len(sErr) > 0 then		
		logWarning "Exec of [" & TEMPFILE_BAT & "] failed.  StdErr:" & vbNewLine & sErr
	end if		
	Do Until exeRs.StdOut.AtEndOfStream		
		logInfo string(4," ") & "[ " & "common.bat" &" ] " & exeRs.StdOut.readline()
	Loop    						

	dim commpara						
	commpara=split(deliverPara,",")				
	commpara = notInLatter(commpara, getFirstMatch("([a-zA-Z]{3,}\..*)",split(deliverPara,",")), "not")					

	' Now iterate through the contents of the config file
	do while not sdata.atendofstream						
		
		set sh = WScript.createobject("WScript.Shell")
	    sline=sdata.readline    									' get one line from the config file contents
		do
			if left(sline, 1) = "#" or len(sline) = 0 or left(sline, 1) = "[" or left(sline, 4) = "Disk" then 	' If this is comment OR a blank line OR a Section OR starts with Disk		
				'logDebug "The read line was a Comment or Blank line or Section or starts with Disk. Skip it and reading the next line."
				exit do												' Then skip it and go with the next iteration
			end if

			args=split(sline, "=")
			logDebug "Processing this line from cfg file: ["&sline&"]"
			logDebug "Take the first part of the line:    ["&args(0)&"]"
			exefile=getExeFileName(args(0))
			if len(exefile) = 0 then
				logWarning "'"&args(0)& "' : No supporting vbs or bat file exists for this check!"
				exit do
			end if
			
			logDebug "OK. Found a file ["&exefile&"] for the check --> "&args(0)
			if lcase(right(exefile, 3)) = "bat" then
                ' chr(34) is the ANSI equivalent of a double quote
				writeToFile "@echo off" & vbnewline & chr(34) & exefile & join(commpara, " ") & chr(34) , TEMPFILE_BAT
			else	
				writeToFile "@echo off" & vbnewline & "cscript.exe //nologo """ & exefile & """ " & chr(34) & join(commpara, " ") & chr(34), TEMPFILE_BAT
			end if	

			Set exeRs = sh.Exec(TEMPFILE_BAT)	
			sErr = ""
			'The StdErr hangs on 64 bit win2008 for stdErr.readAll
			if InStr(exefile,"installationUnit") <= 0 then 
				sErr = trim(exeRs.StdErr.ReadAll())	
			end if	
			if len(sErr) > 0 then		
				logWarning "Exec of [" & TEMPFILE_BAT & "] failed.  StdErr:" & vbNewLine &  sErr
			end if		

			dim trlinee		
			Do Until exeRs.StdOut.AtEndOfStream		
				trlinee = exeRs.StdOut.readline()	
				logInfo string(4," ") & "[ " & t &" ] " & trlinee
				appendToFile trlinee,TEMP_DIR & "\" & COMMON_FILE
			Loop
			exit do
		loop while (0 = 1)
	loop	

	'Cleanup	
	sdata.close	
	set sdata=nothing	
	set objfso = nothing
	
	co = ubound(info.keys)
    i = 0
    for each t in info.keys
        ' deal with deliverPara, with product code to the specify product code
        ' get the product code
        dim epd
        epd = join(getFirstMatch("(.*)_[0-9]{1,}.*",array(t)))

        if epd = "" then
            writeToFile "@echo off" & vbnewline & "cd " & CFG_HOME & vbnewline & t & " " & spath & " " & chr(34) & _ 
                deliverPara & chr(34), TEMPFILE_BAT
        else
            writeToFile "@echo off" & vbnewline & "cd " & CFG_HOME & vbnewline & t & " " & spath & " " & "-p " _ 
                & chr(34) & join(getFirstMatch(epd & "\.(.*)",split(deliverPara,","))," ") & _
                chr(34) , TEMPFILE_BAT            
        end if

        logInfo string(2, " ") & "calling " & t & " [ " & co-i & " scripts left ]"
        Set exeRs = sh.Exec(TEMPFILE_BAT)
        sErr = trim(exeRs.StdErr.ReadAll())
        if len(sErr) > 0 then
            logWarning "Exec of [" & TEMPFILE_BAT & "] failed.  StdErr:" & vbNewLine & sErr
        end if

        createobject("Scripting.FileSystemObject").copyfile TEMP_DIR & "\" & COMMON_FILE, TEMP_DIR & "\" & info.item(t)
        logInfo string(4, " ") & "result will be written into [" & TEMP_DIR & "\" & info.item(t) & "]"
        dim trline
        Do Until exeRs.StdOut.AtEndOfStream
            trline = exeRs.StdOut.readline()
            logInfo string(4," ") & "[ " & t &" ] " & trline
            appendToFile trline,TEMP_DIR & "\" & info.item(t)
        Loop    
        i = i+1
    next
	CreateObject("Scripting.FileSystemObject").DeleteFile(TEMPFILE_BAT)
end sub

' Get the existing file name for a corresponding config param
function getExeFileName(sline)
	dim cmd, exeRs, sh, trline, matches, ln, objFSO, line, fn, line2, fn2
	Set objFSO = CreateObject("Scripting.FileSystemObject")
	line = sline
	ln = len(line)
	do while ln > 0
		logDebug "See if a corresponding vbs or bat file exists for [" & line & "]"
		fn = PRS_BASE_DIR & "lib\"&line
        line2 = replace(line," ","_")				' Replace any spaces with underscore in the name
		fn2 = PRS_BASE_DIR & "lib\"&line2&"_plug"
		If objFSO.FileExists(fn & ".vbs") Then		' Check if such a vbs file exists
			getExeFileName = fn & ".vbs"
			exit do
		elseif objFSO.FileExists(fn & ".bat") Then	' Check if such a bat file exists
			getExeFileName = fn & ".bat"
			exit do
		elseif objFSO.FileExists(fn2 & ".bat") Then	' Check if such a bat file exists
			getExeFileName = fn2 & ".bat"
			exit do
		Else
			getExeFileName=""
		End If
		line = left(line, ln - 1)					' Remove one char from the right and continue
		ln = ln - 1
	loop 
end function

function getFirstExistingDir(path)
	dim result, fso, parent
    result = ""
    if not IsNull(path) and not path="" then
        set fso = CreateObject("Scripting.FileSystemObject")
        ' Get the absolute path name to properly handle relative paths
        path = fso.getAbsolutePathName(path)
        if fso.folderExists(path) then
            result = path
        elseif fso.fileExists(path) then
            ' path is a file, so return the parent folder
            result = fso.getParentFolderName(path)
        else
            ' path doesn't exist.  Move up the directory hierarchy.
            parent = fso.getParentFolderName(path)
            
            if IsNull(parent) or parent="" then
                ' parent is empty if we're at the root of a non-existent or inaccessible drive letter
                result = path
             else
                ' recurse using parent folder
                result = getFirstExistingDir(fso.getParentFolderName(path))
             end if
        end if
    end if
    
    getFirstExistingDir = result
end function

function userCanRead(path)
    dim result, dir, fso, folder, file, fileCount
    
    result = false
    dir = getFirstExistingDir(path)
    if not dir="" then
       set fso = CreateObject("Scripting.FileSystemObject")
       on error resume next
       set folder = fso.getFolder(dir)
       if err.number=0 then
           ' We successfully accessed the folder
           ' Try to read the folder contents too before returning true
		   fileCount = 0
		   for each file in folder.files
		       fileCount = fileCount + 1
           next
		   if err.number=0 then
		       result = true
           end if
       end if
       on error goto 0
    end if       
    
    userCanRead = result
end function

function userCanWrite(path)
	dim result, fileName, dir, fso
    result = false
    dir = getFirstExistingDir(path)
    if not dir="" then
        set fso = CreateObject("Scripting.FileSystemObject")
        fileName = dir & "\__" & cstr(Year(Now))+cstr(Month(Now))+cstr(Day(Now))+"_"+cstr(Hour(Now))+cstr(Minute(Now))+cstr(Second(Now)) & ".tmp"
        on error resume next
        ' Try to create temporary file
        fso.createTextFile fileName, true
        if err.number=0 then
            fso.deleteFile(fileName)
            result = true
        end if
        on error goto 0
    end if
    
    userCanWrite = result
end function
' analysis the files
' it receive an Dictionary contains PD->Dic Dic->cfg,exe
function analysis(allinfo)
    ' load in the common result, localhost_hw.txt
    logInfo "Starting analysis..."
    logInfo "[passOrFail] function will be used as default common comparison"
    dim code, chara, cfgdic, ckey, im, id, cd
    dim arrRst, tRst, tsp
    dim cpuspeed, cctype

    cpuspeed = "0GHz"
    im = 0
    id = 0
    cd = 0

    ' read all files under temp and seperate them
    arrRst = getFirstMatch("(.*)\.txt", allFiles(TEMP_DIR)) 

    for each code in allinfo.keys
        logInfo "Analyzing cfg [" & allinfo.item(code).item("cfg") & "]"
        ' find if there is some result is same name with xx.cfg
        tRst = Filter(arrRst, split(allinfo.item(code).item("cfg"),".")(0))
        cctype = 0
        ' not find then use common result
        if ubound(tRst) < 0 then
            if not createobject("Scripting.FileSystemObject").fileexists(TEMP_DIR & "\" & COMMON_FILE) then
                logInfo "Can not find " & split(allinfo.item(code).item("cfg"),".")(0) & _
                        ".txt under .\temp folder. Please check your script."
                WScript.quit
            end if
            chara =  ppread(TEMP_DIR & "\" & COMMON_FILE, "=")
        else
            chara = ppread(TEMP_DIR & "\" & tRst(0) & ".txt", "=")
        end if

        ' New in 1.0.30 (10/29/2010) - Read config file and process conditional sections
        cfgdic = readConfigWithSections(CFG_HOME & "\" & allinfo.item(code).item("cfg"))

        ' deal cfgdic, to change UPGRADE and FRESH items
        dim ras, rbs, isfresh, isnotRunning, prever, precfg
        isfresh = "yes"
        isnotRunning = "yes"
        prever = ""
        precfg = ""
        if CreateObject("Scripting.FileSystemObject").FolderExists(spath) then
            ras = exeCommand("cmd /c dir /b /s " & spath & " | findstr -i kincinfo.exe")
        else
            logWarning "Agent directory [" & spath & "] does not exist"
            ras = ""
        end if
        if not ras = "" then
            ' if the agent installed, then it should not be FRESH installing
            rbs = filterCommand("cmd /c " & ras & " -i ", "(^" & join(getFirstMatch("K([A-Za-z0-9]{2}).*", array(code))) _
                    & ").*",1, ".*[V|v]ersion: ([0-9\.]{1,}) [B|b]uild.*") 
            if not rbs = "" then
                isfresh = "no"
                prever = replace(rbs,".", "")
            end if

            ' If the agent is running, memory calculation will be different 
            rbs = filterCommand("cmd /c " & ras & " -r ", "(^[A-Za-z0-9\.]{1,}\s{1,}" & join(getFirstMatch("K([A-Za-z0-9]{2}).*", array(code))) _
                    & "\s).*",0, "^[^\s]{1,}[ ]{1,}[^\s]{1,}[ ]{1,}([0-9]{1,}).* ")
            if not rbs = "" then
                if not isMatch("^(0{1,})$", rbs) then
                    isnotRunning = "no"
                end if
            end if

            ' Here find the preversion, if not, then do it as refresh
            if isfresh = "no" then
                if ubound(getFirstMatch("(" & code & "_" & prever & ".cfg)", allFiles(CFG_HOME))) >=0 then
                    precfg = ppread(CFG_HOME & "\" & code & "_" & prever & ".cfg", "=") 
                else
                    ' if not find the configuration of the pre version, treat it as fresh install
                    isfresh = "yes"
                    isnotRunning = "yes"
                end if
            end if
        end if

        logInfo "analysis() - Memory"
        
        dim cal_memory
        if isnotRunning = "no" then
            'memory
            if cfgdic(0).exists(MEMORY_NAME) then
                if precfg(0).exists(MEMORY_NAME) then
                    cal_memory = bigthan(cfgdic(0).item(MEMORY_NAME),precfg(0).item(MEMORY_NAME))        
                    cfgdic(0).remove(MEMORY_NAME)
                    cfgdic(0).add MEMORY_NAME,cal_memory
                end if 
            end if
        end if

        logInfo "analysis() - Oracle"
        
        ' deal with the oracle agents configuration file
        ' it will deal the instance accumulation
        if code = "KOR" then
            dim instn, ttnn, ttdic
            set ttdic = createObject(DICTIONARY)
            instn = getFirstMatch("KOR.instance=(.*)",split(deliverPara,","))(0)

            ' deal the virtual memory
            if cfgdic(0).exists("Virtual Memory") then
                ttdic.add "VM", cfgdic(0).item("Virtual Memory")
                if cfgdic(0).exists("perVertual") then
                    for ttnn =1 to instn
                        ttdic.add ttnn, cfgdic(0).item("perVertual")
                    next
                end if
                cfgdic(0).remove("Virtual Memory")
                cfgdic(0).remove("perVertual")
                cfgdic(0).add "Virtual Memory",changeMG(unitMGTOG(ttdic.items)) 
            end if

            ' deal the disk with installed or non-installed
            if not ras = "" then
                cfgdic(0).add "Disk",cfgdic(0).item("DiskWithOther")
                cfgdic(0).remove("DiskWithOther")
                cfgdic(0).remove("DiskNoOther") 
            else
                cfgdic(0).add "Disk",cfgdic(0).item("DiskNoOther")
                cfgdic(0).remove("DiskWithOther")
                cfgdic(0).remove("DiskNoOther") 
            end if

            ' deal the additional temporary table space datafile
            if cfgdic(0).exists("perTemp_Datafile") then
                ttdic.removeall
                for ttnn =1 to instn
                    ttdic.add ttnn, cfgdic(0).item("perTemp_Datafile")
                next
                cfgdic(0).remove("perTemp_Datafile")
                cfgdic(0).add "TempDatafile",changeMG(unitMGTOG(ttdic.items)) 
            end if

            ' deal the addtional table space datafile
            if cfgdic(0).exists("perDatafile") then
                ttdic.removeall
                for ttnn =1 to instn
                    ttdic.add ttnn, cfgdic(0).item("perDatafile")
                next
                cfgdic(0).remove("perDatafile")
                cfgdic(0).add "Datafile",changeMG(unitMGTOG(ttdic.items)) 
            end if 
        end if

        logInfo "analysis() - CPU"
        
        ' deal with CPU Name, to seperate it to cputype and cpuspeed
        if chara(0).exists(CPU_NAME) then
            if ubound(getFirstMatch(".*([i|I]ntel).*",array(chara(0).item(CPU_NAME)))) < 0 then
                cctype = cctype + 1
            end if
            
            tsp = getFirstMatch(".*\s{1,}(.+[g|G|m|M])[h|H]z.*",array(chara(0).item(CPU_NAME)))            
            if ubound(tsp) = 0 then
                cpuspeed = tsp(0) & "Hz"
            end if

            chara(0).add CPU_ARRAY(cctype) ,cpuspeed

            if cfgdic(0).exists(CPU_ARRAY((cctype+1) mod 2)) then
                cfgdic(0).remove(CPU_ARRAY((cctype+1) mod 2))
            end if
        end if

        logInfo "analysis() - Comparison"
        
        ' start the comparasion
        dim comp
        dim errmap
        set comp = createobject(DICTIONARY)
        set errmap = createobject(DICTIONARY)    

        for each ckey in cfgdic(0).keys
            logInfo "Processing [" & ckey & "]"
            dim ci
            set ci = new CheckItem
            ' set product code
            ci.pdCode = code
            ' set name
            if codename(0).exists(code) then                
                ci.pdName = codename(0).item(code)
            else
                ci.pdName = code & " [not defined]"
            end if
            
            ' set itype
            ci.itype = ckey

            ' Add memory and disk items to arrays
            if ckey = MEMORY_NAME then
                ReDim Preserve arrmem(im)
                arrmem(im) = cfgdic(0).item(ckey)
                im = im + 1
            elseif Len(ckey)>=Len(DISK_NAME) and Left(ckey,Len(DISK_NAME))=DISK_NAME then
                if not Left(cfgdic(0).item(ckey),1)="[" then
                    ReDim Preserve arrdisk(id)
                    arrdisk(id) = cfgdic(0).item(ckey)
                    id = id + 1
                else
                    ' New in 1.0.30 - Fancy disk space properties
                    ReDim Preserve customDisk(cd)
                    customDisk(cd) = cfgdic(0).item(ckey)
                    cd = cd + 1
                end if
            end if
            

            ' set expected
            ci.recommended = cfgdic(0).item(ckey)
            
            ' See if a multi-valued key matches this property
            dim realKeys, realKey
            realKeys = chara(0).keys()
            for each realKey in realKeys
                'logDebug "Found key: " + realKey + " Value = " + chara(0).item(realKey)
                if (Len(realKey) > 2) then
                    if Right(realKey,2) = ".*" then
                        'logDebug "Found multiValue key: " + realKey + " Value = " + chara(0).item(realKey)
                        if (Left(ci.itype,Len(realKey)-2) = Left(realKey,Len(realKey)-2)) then
                            'logDebug "Multivalue key match: " + ci.itype + " matches " + realKey
                            ci.itype=realKey
                        end if
                    end if
                end if
            next
                        
            ' set real value
            dim recommendedVal
            recommendedVal = ci.recommended
            if ci.itype = "Memory" then
                ci.realValue = changeMG(chara(0).item(ci.itype))
            elseif ubound(getFirstMatch("(" & DISK_NAME & ")", array(ci.itype))) = 0 then
                ' Handle Disk#1, Disk#2, etc. as property names - just get the 'Disk' property value
                'ci.realValue = changeMG(chara(0).item(DISK_NAME))

                dim accessVal, mydir
				dim realAccess, realValue
				realAccess="--"
				realValue=""
                accessVal = getQualifierValue(ci.recommended, "access", "")
                mydir = getQualifierValue(ci.recommended, "dir", spath)

                if InStr(LCase(accessVal),"r") then
                    ' check read access
                    if not userCanRead(mydir) then
						logInfo "User does not have read access to [" & mydir & "]."
                    else
						realAccess="r-"
                        logDebug "User has read access to [" & mydir & "]"
                    end if
                end if
                if InStr(LCase(accessVal),"w") then
                    ' check write access
                    if not userCanWrite(mydir) then
						logInfo "User does not have write access to [" & mydir & "]."
                    else
						realAccess=left(realAccess,1)+"w"
                        logDebug "User has write access to [" & mydir & "]"
                    end if
                end if
				if accessVal <> "" then
					realValue = realAccess
					accessVal = lcase(accessVal)
					if len(accessVal) = 1 then
						if accessVal = "r" then
							accessVal = "r-"
						elseif accessVal = "w" then
							accessVal = "-w"
						end if
					end if
					recommendedVal = accessVal
				else
					realValue = getDiskFreeSpace(getDriveFromDiskProperty(ci.recommended, spath))
					recommendedVal = getSizeValue(ci.recommended)
				end if
				ci.realValue = realValue
				ci.recommended = recommendedVal
            else
                ci.realValue = chara(0).item(ci.itype)
            end if            
            
            logDebug "================="
            logDebug "ckey  = " + ckey
            logDebug "itype = " + ci.itype
            logDebug "pcode = " + code
            logDebug "expec = " + ci.recommended
            logDebug "real  = " + ci.realValue
            logDebug "================="            
            
            ' set Pass or fail
            ci.passOrFail = ecompare(code, ci.itype, recommendedVal, ci.realValue)
            
            if CreateObject("Scripting.FileSystemObject").fileexists(TEMPFILE_BAT) then 
                CreateObject("Scripting.FileSystemObject").DeleteFile(TEMPFILE_BAT) 
            end if

            logInfo fmt("[%0s] for %0s of %0s",array(ci.passOrFail,ci.itype,ci.pdName))
            if Instr(ci.passOrFail,"FAIL") > 0 then
                errmap.add ckey, ci
            end if
            comp.add ckey, ci
        next

        if ubound(comp.keys) >= 0 then
            results.add code, comp
        end if

        if ubound(errmap.keys) >= 0 then
            failedResults.add code,errmap
        end if
    next

    ' deal the multi agent with multi disks
    ' for example, they can specify the path to D: or E:
    ' we have to add the account separatelly
    ' arrdisk is an array contains the information of all the configuration files which 
    ' defined Disk= so here we will take it as one unit, and just check the parameters in deliverPara
    ' First parse the deliverPara, find the parameter which contains path
    dim path_para, path_dic, path_orig, path_tt, path_pd, path_nm, path_val, path_old, path_rel, commFile, pathDic

    set path_dic = createObject(DICTIONARY)
    set pathDic = createObject(DICTIONARY)

    ' Read in COMMON_FILE
    set commFile = ppread(TEMP_DIR & "\" & COMMON_FILE, "=")(0)
    logInfo "Read in [" & COMMON_FILE & "]:" & vbNewLine & varToString(commFile)
    
    ' Add the Memory in
    logInfo "Processing Memory"
    
    logInfo "arrmem = " & varToString(arrmem)
    ' Only add memory to the summary if it was included in at least one configuration file
    if (im > 0) then
        path_dic.add "Memory", changeMG(commFile.item(MEMORY_NAME)) & ";" & unitMGTOG(arrmem)
    end if
    
    ' Now begin to deal with disk
    logInfo "Processing Disk.  Default path for all 'Disk=' properties [" & spath & "]"
    logInfo "spath   = " & spath
    logInfo "arrdisk = " & varToString(arrdisk)
    logInfo "customDisk = " & varToString(customDisk)
   
    dim diskNum, diskItem
    
    if not IsEmpty(commFile.item(DISK_NAME)) then
        logInfo "Processing default disk: " & commFile.item(DISK_NAME)
        path_orig = getDriveFromDiskProperty(spath, spath)
        diskItem = changeMG(commFile.item(DISK_NAME)) & ";" & unitMGTOG(arrdisk)
        logInfo "Adding default disk: " & path_orig & " : " & varToString(diskItem)
        path_dic.add path_orig, diskItem
    else
        logInfo "No disk item added"
    end if
    
    ' Add in custom directories from new 'Disk' property syntax
    dim dirExpression    
    for each dirExpression in customDisk
        dim dir, drive, size, freeSpace
        
        logDebug "Adding " & dirExpression & " to disk usage totals"
        dir = getDirFromDiskProperty(dirExpression, spath)
        drive = getDriveFromDiskProperty(dirExpression, spath)
        size = changeMG(getSizeValue(dirExpression))
        freeSpace = getDiskFreeSpace(drive)
        
        logDebug "      dir = " & dir
        logDebug "    drive = " & drive
        logDebug "     size = " & size
        logDebug "freeSpace = " & freeSpace
        
        if path_dic.exists(drive) then
            dim currentSize, totalSize
            
            logDebug "[" & drive & "] already exists in path_dic [" & path_dic.item(drive) & "].  Adding to total."
            ' size is always in MB
            currentSize = Left(size,Len(size)-2)
            totalSize = getFirstMatch(";([0-9][\.,0-9]*)",array(path_dic.item(drive)))(0)
            totalSize = CLng(totalSize) + CLng(currentSize)
            path_dic.remove drive
            path_dic.add drive, freeSpace & ";" & totalSize & "MB"
        else
            logDebug "Adding drive to path_dic: " & drive & "," & freeSpace & ";" & size
            path_dic.add drive, freeSpace & ";" & size
        end if
    next
    
    ' Continue to support the old way of handling multiple paths/disks
    logInfo "deliverPara      =[" & varToString(deliverPara) & "]"
    logInfo "path_dic (before)=[" & varToString(path_dic) & "]"
    path_para = getFirstMatch("(.*=" & DRIVE_PATTERN & ".*)",split(deliverPara,","))
    logInfo "path_para        =[" & varToString(path_para) & "]"
    
    for each path_tt in path_para
        logDebug "  path_tt=[" & path_tt & "]"
        path_pd = join(getFirstMatch("^([a-zA-Z0-9]{3})\..*",array(path_tt)))
        path_nm = replace(split(path_tt,"=")(0), path_pd & ".","")
        path_orig = ucase(join(getFirstMatch(".*=([A-Za-z]{1}:)[\\/].*",array(path_tt))))

        logDebug "  ----------------------------"
        logDebug "  path_pd=[" & path_pd & "]"
        logDebug "  path_nm=[" & path_nm & "]"
        logDebug "  path_orig=[" & path_orig & "]"
        logDebug "  results=[" & varToString(results) & "]"
        logDebug "  path_dic=[" & varToString(path_dic) & "]" & vbNewLine
        logDebug "  ----------------------------"
        
        ' Results structure:
        ' LCM -> AgentDisk -> struct -> value
        path_val = "0GB"
        path_rel = "0GB"
        if results.exists(path_pd) then
            if results.item(path_pd).exists(path_nm) then
                path_val = results.item(path_pd).item(path_nm).recommended
                path_rel = results.item(path_pd).item(path_nm).realValue
            end if
        end if 

        ' calculate the total
        if path_dic.exists(path_orig) then
            path_old = path_dic.item(path_orig)
            path_dic.remove path_orig
            path_dic.add path_orig, split(path_old,";")(0) & ";" & unitMGTOG(array(split(path_old,";")(1),path_val))
        else
            path_dic.add path_orig, path_rel & ";" & path_val
        end if 
    next

    ' Add totals for disk and memory to results
    dim comp2
    dim errmap2
    set comp2 = createobject(DICTIONARY)
    set errmap2 = createobject(DICTIONARY)
    dim fl

    for each fl in path_dic.keys
        dim cim
        set cim = new CheckItem
        cim.pdCode = TOTAL_NAME 
        cim.pdName = ALL_COMPONENTS_NAME 
        cim.itype = fl
        cim.realValue = changeMG(split(path_dic.item(fl),";")(0))
        cim.recommended = changeMG(split(path_dic.item(fl),";")(1))
                
        cim.passOrFail = ecompare(cim.pdCode,cim.itype,split(path_dic.item(fl),";")(1), split(path_dic.item(fl),";")(0))
        logInfo fmt("[%0s] for %0s of %0s",array(cim.passOrFail,cim.itype,cim.pdName))
        if CreateObject("Scripting.FileSystemObject").fileexists(TEMPFILE_BAT) then 
            CreateObject("Scripting.FileSystemObject").DeleteFile(TEMPFILE_BAT) 
        end if

        if Instr(cim.passOrFail,"FAIL") > 0 then
            errmap2.add fl, cim
        end if
        comp2.add fl, cim
    next

    if ubound(comp2.keys) >= 0 then
        results.add TOTAL_NAME, comp2
    end if

    if ubound(errmap2.keys) >= 0 then
        failedResults.add TOTAL_NAME,errmap2
    end if
    
    ' Set return code
    if failedResults.count > 0 then
        analysis = RETURN_CODE_FAIL
    else
        analysis = RETURN_CODE_PASS
    end if
end function

' Input is a property value and the output is a dictionary of qualifier values
function getPropertyQualifiers(extendedValueString)
    dim rEx, name, value, qualifier, expressionMatches, expressionMatch, result, qEx, qMatches, qMatch
    
    set result = createObject(DICTIONARY)

    Set rEx = new RegExp
    rEx.IgnoreCase = true
    rEx.Global = true
    rEx.Pattern = "^\[([^]]+)([\\][\]])*\]"
    const MAX_QUALIFIERS = 100
    Set expressionMatches = rEx.Execute(extendedValueString)
    
    for each expressionMatch in expressionMatches
        logDebug "Processing qualifier expression: " & replace(expressionMatch.submatches(0),"\]","]")
        for each qualifier in split(expressionMatch.submatches(0),",")
            logDebug vbTab & "Found qualifier: " & qualifier
            set qEx = new RegExp
            qEx.IgnoreCase = true
            qEx.Global = true
            qEx.Pattern = "(?:([a-z0-9_\-]+):){1}?(.*)"
            Set qMatches = qEx.Execute(qualifier)
            for each qMatch in qMatches
                name = qMatch.submatches(0)
                value = qMatch.submatches(1)
                value = objShell.ExpandEnvironmentStrings(value)
                result.add name, value
            next
        next    
    next
    
    logDebug "Qualifiers for extendedValueString {" & extendedValueString & "} = " & varToString(result)
    set getPropertyQualifiers = result
end function

function getQualifierValue(extendedValueString, key, defaultValue)
    dim qualifierDic, result
    set qualifierDic = getPropertyQualifiers(extendedValueString)
    
    result = qualifierDic.item(key)
    if isNull(result) or isEmpty(result) or result="" then
        result = defaultValue
    end if
    
    logDebug "getQualifierValue(" & extendedValueString & "," & key & "," & defaultValue & ") = " & result
    getQualifierValue = result
end function

' Input is a property value and the output is the property value minus the qualifiers (if present)
function getPropertyValue(extendedValueString)
    dim rEx, i, name, value, expressionMatches, expressionMatch, max, result
    
    result = extendedValueString
    
    if Len(result)>0 and Left(result,1)="[" then
        ' Skip qualifiers and pick out the value at the end
        Set rEx = new RegExp
        rEx.IgnoreCase = true
        rEx.Global = true
        rEx.Pattern = "^\[(?:[^]]+)(?:[\\][\]])*\](.+)"
        Set expressionMatches = rEx.Execute(extendedValueString)
        for each expressionMatch in expressionMatches
            on error resume next
            value = expressionMatch.submatches(0)
            value = replace(value, "\]", "]")
            value = objShell.ExpandEnvironmentStrings(value)
            if err.number=0 then                            
                result = value
            end if
            on error goto 0
        next
    end if
    
    logDebug "getPropertyValue(" & extendedValueString & ") = " & varToString(result)
    getPropertyValue = result
end function

function getSizeValue(extendedValueString)
        dim u, units, value, result
        
        ' Handle property metadata - units in this case
        if Len(extendedValueString)>0 and Left(extendedValueString,1)="[" then
            units = getQualifierValue(extendedValueString, "unit", "MB")
            value = getPropertyValue(extendedValueString)
            if Len(value)>2 and UCase(Right(value,2))="GB" then
                value = Left(value,Len(value)-2)
                units = "GB"
            elseif UCase(Right(value,2))="MB" then
                value = Left(value,Len(value)-2)
                units="MB"
            end if
            if units="GB" then
                value = CStr(CLng(CDbl(value)) * 1024.0)
            end if
            result = value & "MB"
        else
            result = extendedValueString
        end if
        
        getSizeValue = result
end function

function getDriveFromDiskProperty(extendedValueString, defaultPath)
    dim result, path
    result = defaultPath
    
    path = getDirFromDiskProperty(extendedValueString, defaultPath)
    result = getFirstMatch(DRIVE_LETTER_PATTERN, array(path))(0)
    
    logDebug "getDriveFromDiskProperty(" & extendedValueString & "," & defaultPath & ") = " & result
    
    getDriveFromDiskProperty = UCase(result)
end function

function getDirFromDiskProperty(extendedValueString, defaultPath)
    dim result, dirArr
    result = defaultPath
    
    result = getQualifierValue(extendedValueString, "dir", defaultPath)

    logDebug "getDirFromDiskProperty(" & extendedValueString & "," & defaultPath & ") = " & result
    
    getDirFromDiskProperty = result
end function

function ecompare(pd,tp,rec,rst)
    dim compfs, com_pdtp, com_tp, comm, sErr
    dim toexe, exeRs

    logDebug "ecompare ENTRY"
    logDebug "    pd  = " + pd
    logDebug "    tp  = " + tp
    logDebug "    rec = " + rec
    logDebug "    rst = " + rst
    
    if (Len(tp)>2) then
        if (StrComp(Right(tp,2),".*")=0) then
            tp = Left(tp,Len(tp)-2)
        end if
    end if

    tp = replace(tp," ","_")
    ' All comparison files
    compfs = getFirstMatch("(.*_compare.*)", allFiles(CFG_HOME))
    ' Product-code & version specific comparison files
    com_pdtp = getFirstMatch("("& pd & "_" & tp & ".*)",compfs)
    ' Product-code specific comparison files
    com_tp = getFirstMatch("(.*" & tp & ".*)",compfs)
    
    toexe = ""
    ' Matching both code and version is higher priority than just matching code
    if ubound(com_pdtp) >= 0 then 
        toexe = com_pdtp(0) 
    elseif ubound(com_tp) >=0 then
        toexe = com_tp(0)
    end if
    
    if len(toexe) > 0 then 
        logInfo fmt("[%0s] used as comparison for [%0s] of [%0s]" & vbnewline,array(toexe,tp, pd))
        if ubound(getFirstMatch("(.*\.vbs$)", array(toexe))) = 0 then
            writeToFile "@echo off" & vbnewline & "cd " & CFG_HOME & vbnewline & "cscript.exe //nologo " & chr(34) & toexe & chr(34) _
                & " " & chr(34) & rec & chr(34) & " " & chr(34) & rst & chr(34), TEMPFILE_BAT
        elseif ubound(getFirstMatch("(.*\.bat$)", array(toexe))) = 0 then
            writeToFile "@echo off" & vbnewline & "cd " & CFG_HOME & vbnewline & chr(34) & toexe & chr(34) _
                & " " & chr(34) & rec & chr(34) & " " & chr(34)  & rst & chr(34), TEMPFILE_BAT 
        end if
            
        Set exeRs = WScript.createobject("WScript.Shell").Exec(TEMPFILE_BAT)
	 if tp = "de.installationUnit" then
     		sErr = ""
        else
             sErr = trim(exeRs.StdErr.ReadAll())
        end if
        if len(sErr) > 0 then
            logWarning "Exec of [" & TEMPFILE_BAT & "] failed.  StdErr:" & vbNewLine & sErr
        end if

        dim trline
        Do Until exeRs.StdOut.AtEndOfStream
            trline = exeRs.StdOut.readline()        
            logInfo string(4," ") & trline
            if ubound(getFirstMatch("(.*PASS|FAIL.*)",array(trline))) = 0 then
                ecompare = trline
                exit function
            end if 
        Loop

        ecompare="FAIL"
        exit function
    end if

    ' If no custom comparer, execute the default comparison function
    dim pppf, pppe,comr
    pppe=fmt("comr=%0s(" & chr(34) & "%0s"& chr(34) & "," & chr(34) & "%0s" & chr(34) & ")", array("passOrFail",rec, rst))
    execute pppe
    ecompare=comr
end function

' generate reports from results or failedResults
sub generateReport
    ' Don't output connectivity by default - if it's in a config file, it will get displayed further below
    ' writeToFile join(filterFile(TEMP_DIR & "\" & COMMON_FILE, "(connectivity .*$)"), vbnewline), ".\result.txt"
    dim d, msg, finalMsg
    d = getspecialString(results)
    writeToFile d, PRS_BASE_DIR & RESULT_FILE
    logInfo "Details also available in " & PRS_BASE_DIR & RESULT_FILE
    if flagShowDetail then
        logInfo d
        logScreen getScreenString(results) 
        if ubound(filterFile(PRS_BASE_DIR & RESULT_FILE,".* (FAIL).*")) < 0 then
            logInfo "Congratulations! All the items have PASSED!"
            msg = "PASS"
        else
            logWarning "Attention! Something Failed! " & vbnewline
            msg = "FAIL"
        end if
        finalMsg = "Prereq Scanner Overall Result: " & msg
    else
        if ubound(filterFile(PRS_BASE_DIR & RESULT_FILE,".* (FAIL).*")) >= 0 then
            logWarning "Attention! Something Failed! " & vbnewline & getspecialString(failedResults)
            finalMsg="FAIL"
        else
            logInfo "Congratulations! All the items have PASSED!"
            finalMsg="PASS"
        end if
    end if
    logScreen finalMsg
    appendToFile finalMsg, PRS_BASE_DIR & RESULT_FILE
end sub

' get special string from special object
' Dictionary->Dictionary->checkitem
function getspecialString(dic)
    dim alln
    
    alln = formatresult(dic)

    dim res, headerAdded, key, kty, ci, total
    res = ""    

    res = res & getVersionInfo()
    res = res & getMachineInfo() & vbNewLine
    
    for each key in dic.keys        
        headerAdded = False
        if key = TOTAL_NAME then
            set total = dic.item(key)
        else
            for each kty in dic.item(key).keys
                set ci = dic.item(key).item(kty)        
                if not headerAdded then                
                    res = res & ci.pdName & " [version " _
                        & join(getFirstMatch(VER_PATTERN, array(info(0).item(key).item("cfg")))) _
                        & "]:" & vbnewline & vbNewLine
                    res = res & fmt(RESULT_PATTERN & vbNewLine, RESULT_HEADER)
                    res = res & fmt(RESULT_PATTERN & vbNewLine, RESULT_HEADER2)
                    headerAdded = True
                end if

                if len(ci.realValue) = 0 then
                    ci.realValue = "[Not Found]"
                end if

                if ubound(split(ci.recommended,",")) > 0 then
                    res = res & fmt(RESULT_PATTERN & vbNewLine, array(ci.itype,ci.passOrFail,ci.realValue, split(ci.recommended,",")(0)))
                    dim i, j, fmts, FMT_STR, x, y, x1, x2, x3, l1, l2, l3
                    for i=1 to ubound(split(ci.recommended,",")) step 1
						' suppose RESULT_PATTERN    = "%60s%8s%80s%80s"
						fmts = split(RESULT_PATTERN, "%")		' now fmts contains 60s, 8s, 80s and 80s
						x1=len(fmts(1))
						x2=len(fmts(2))
						x3=len(fmts(3))
						l1=left(fmts(1),x1-1)
						l2=left(fmts(2),x2-1)
						l3=left(fmts(3),x3-1)
						x=CDbl(l1)+CDbl(l2)+CDbl(l3)			' now x = 60 + 8 + 80
						y=left(fmts(4),len(fmts(4))-1)			' and y = 80
						FMT_STR = "%"+cstr(x)+"s%"+cstr(y)+"s" 	' so FMT_STR = "%148s%80s"
                        res = res & fmt(FMT_STR & vbnewline, array(string(alln, " "), trim(split(ci.recommended,",")(i))))
                    next
                else
                    res = res & fmt(RESULT_PATTERN & vbNewLine, array(kty,ci.passOrFail,formatForDisplay(ci.realValue), formatForDisplay(ci.recommended)))
                end if
            
            next     
            res = res & vbnewline
        end if
    next

    ' Totals
    if not isempty(total) then
        headerAdded = False
        for each kty in total.keys
            set ci = total.item(kty)
            if not headerAdded then                
                res = res & ci.pdName & " :" & vbnewline
                res = res & fmt(RESULT_PATTERN & vbNewLine, RESULT_HEADER)
                res = res & fmt(RESULT_PATTERN & vbNewLine, RESULT_HEADER2)
                headerAdded = True
            end if
            if len(ci.realValue) = 0 then
                ci.realValue = "[Not Found]"
            end if
            res = res & fmt(RESULT_PATTERN & vbNewLine, array(kty,ci.passOrFail,ci.realValue, ci.recommended)) 
        next         
    end if
    getspecialString = res
end function

' Determine how many columns are in the command window
function getMaxScreenWidth
    dim MIN_SCREEN_WIDTH, DEFAULT_SCREEN_WIDTH, MAX_SCREEN_WIDTH
    MIN_SCREEN_WIDTH = 66
    DEFAULT_SCREEN_WIDTH = 80
    MAX_SCREEN_WIDTH = 150

    dim objShell, objShellExec, objRegEx, consoleColumns
    consoleColumns = DEFAULT_SCREEN_WIDTH
    
    Set objShell = WScript.createobject("WScript.Shell")
    ON ERROR RESUME NEXT
    Set objShellExec = objShell.Exec("cmd /c mode con")

    if Err.Number=0 then
        ON ERROR GOTO 0
        Set objRegEx = new RegExp
        objRegEx.Pattern = ".*Columns:\s*(\d+)"
        objRegEx.IgnoreCase = True
        objRegEx.Global = True
        
        Do Until objShellExec.StdOut.AtEndOfStream
            Dim strLine, matches
            strLine = objShellExec.StdOut.ReadLine()
            Set matches = objRegEx.Execute(strLine)
            if matches.Count > 0 then
                consoleColumns = CInt(matches(0).submatches(0))-1
                if consoleColumns > MAX_SCREEN_WIDTH then
                    consoleColumns = MAX_SCREEN_WIDTH
                end if
            end if
        Loop
    else
        ON ERROR GOTO 0
        Err.clear
    end if
    
    ' Sanity check
    if consoleColumns < MIN_SCREEN_WIDTH then
        consoleColumns = MIN_SCREEN_WIDTH
    elseif consoleColumns > MAX_SCREEN_WIDTH then
        consoleColumns = MAX_SCREEN_WIDTH
    end if
    
    'WScript.Echo "$$ maxScreenWidth = " & consoleColumns
    getMaxScreenWidth = consoleColumns
end function

' Calculate column widths for screen display
function getScreenPattern(dic)    
    dim key, propName, ci
    dim maxPropName, maxExpected, maxRealValue, maxScreenWidth
    
    maxScreenWidth = getMaxScreenWidth()
    
    maxPropName = 0
    maxExpected = 0
    maxRealValue = 0
    
    for each key in dic.keys
        for each propName in dic.item(key).keys
            set ci = dic.item(key).item(propName)
            if (Len(propName)+1>maxPropName) then
                maxPropName = Len(propName)+1
            end if
            if (Len(formatForDisplay(ci.recommended))+1>maxExpected) then
                maxExpected = Len(formatForDisplay(ci.recommended))+1
            end if
            if (Len(formatForDisplay(ci.realValue))+1>maxRealValue) then
                maxRealValue = Len(formatForDisplay(ci.realValue))+1
            end if
        next
    next
    
    ' Original fixed pattern was "%37s%8s%18s%0s" & vbnewline
    dim nameCol, resultCol, foundCol, expectedCol
    ' nameCol - min 9, max 38
    if maxPropName<=9 then
        nameCol = 9
    elseif maxPropName>37 then
        nameCol = 37
    else
        nameCol = maxPropName
    end if
    
    ' resultCol - Fixed at 8
    resultCol = 8
        
    ' foundCol & expectedCol 
    if maxRealValue<=8 then
        maxRealValue = 8
    end if
    if maxRealValue+maxExpected > (maxScreenWidth - nameCol - resultCol) then
        dim half 
        half = CInt((maxScreenWidth - nameCol - resultCol)/2)
        if maxRealValue < half then
            foundCol    = maxRealValue
            expectedCol = maxScreenWidth - nameCol - resultCol - foundCol
        elseif maxExpected < half then
            expectedCol = maxExpected
            foundCol    = maxScreenWidth - nameCol - resultCol - expectedCol
        else
            foundCol = half
            expectedCol = half
        end if
        ' Split the rest in half between real and expected values
        foundCol    = CInt((maxScreenWidth - nameCol - resultCol)/2)
        expectedCol = foundCol
    else
        foundCol    = maxRealValue
        expectedCol = maxExpected
        if maxPropName + resultCol + foundCol + expectedCol > maxScreenWidth then
            nameCol = maxScreenWidth - expectedCol - foundCol - resultCol
        else
            nameCol = maxPropName
        end if
    end if
            
    'WScript.Echo "$$ screenPattern = " & "%" & nameCol & "s%" & resultCol & "s%" & foundCol & "s%" & expectedCol & "s" & vbNewLine
    getScreenPattern = "%" & nameCol & "s%" & resultCol & "s%" & foundCol & "s%" & expectedCol & "s" & vbNewLine
end function

' Format the output for console display (80 characters)
function getScreenString(dic)
    dim res, headerAdded, productCode, propName, ci, total, screenPattern
    
    res = ""    

    ' Calculate most efficient screen layout based on length of properties & values
    screenPattern = getScreenPattern(dic)

    for each productCode in dic.keys        
        headerAdded = False
        if productCode = TOTAL_NAME then
            set total = dic.item(productCode)
        else
            for each propName in dic.item(productCode).keys
                set ci = dic.item(productCode).item(propName)        
                if not headerAdded then                
                    res = res & ci.pdName & " [version " & _
                        join(getFirstMatch(".+_(.*)\.cfg", array(info(0).item(productCode).item("cfg")))) _
                            & "]:" & vbnewline & vbNewLine
                    res = res & fmt(screenPattern, RESULT_HEADER)
                    res = res & fmt(screenPattern, RESULT_HEADER2)
                    headerAdded = True
                end if

                if len(ci.realValue) = 0 then
                    ci.realValue = "[Not Found]"
                end if
                'res = res & fmt(screenPattern, array(propName,ci.passOrFail,ci.realValue,ci.recommended))
                res = res & fmt(screenPattern, array(propName,ci.passOrFail,formatForDisplay(ci.realValue),formatForDisplay(ci.recommended)))
            next     
            res = res & vbnewline
        end if
    next

    ' Totals
    if not isempty(total) then
        headerAdded = False
        for each propName in total.keys
            set ci = total.item(propName)
            if not headerAdded then                
                res = res & ci.pdName & " :" & vbnewline
                res = res & fmt(screenPattern, RESULT_HEADER)
                res = res & fmt(screenPattern, RESULT_HEADER2)
                headerAdded = True
            end if
            if len(ci.realValue) = 0 then
                ci.realValue = "[Not Found]"
            end if

            res = res & fmt(screenPattern, array(ci.itype,ci.passOrFail,formatForDisplay(ci.realValue), formatForDisplay(ci.recommended))) 
        next         
    end if
    getScreenString = res
end function

function formatresult(dic)
    dim ltype, lpass, lreal
    ltype = 19
    lpass = 8
    lreal = 19

    dim res, phead, key, kty, ci, total    

    for each key in dic.keys    
        for each kty in dic.item(key).keys
            set ci = dic.item(key).item(kty)        
            
            if len(ci.realValue) = 0 then
                ci.realValue = "[Not Found]"
            end if

            if len(ci.itype) > ltype then
                ltype = len(ci.itype)
            end if

            if len(ci.passOrFail) > lpass then
                lpass = len(ci.passOrFail)
            end if

            if len(ci.realValue) > lreal then
                lreal = len(ci.realValue)
            end if 
        next     
    next

    formatresult = ltype + lpass + lreal + 3
end function

' clean temp folder
sub clean_tempfolder
    CreateObject("Scripting.FileSystemObject").DeleteFolder(TEMP_DIR) 
end sub

' parse parameter pdcodesAndVersions
' return a Dictionary, contains in an array
function parsecv(pdcodesAndVersionss)
    Dim als
    Dim al, nn
    dim dic
    set dic = createobject(DICTIONARY)
    als = split(pdcodesAndVersionss, ",")
    for each al in als
        nn = split(replace(trim(al),"  ", " ")," ")
        if Ubound(nn) > 0 then
            dic.add nn(0),nn(1)
        else
            dic.add nn(0),""
        end if
    next
    parsecv = array(dic)
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

' Removes trademark special characters to make comparisons easier
function removeSpecialCharacters(s)
    s = replace(s,"(R)","")
    s = replace(s,chr(174),"")    ' Registered symbol
    s = replace(s,chr(169),"")    ' Copyright symbol
    s = replace(s,chr(153),"")    ' TM symbol	
'   s = replace(s,"","")    
    removeSpecialCharacters = s
end function

function getVersionInfo()
    dim wmiObj, osObj, os, osString, resultStr, wsh
    set wsh = WScript.CreateObject("WScript.Shell")
    set wmiObj = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")
    set osObj = wmiObj.execQuery("Select * from Win32_OperatingSystem")
    for each os in osObj
        osString = trim(os.caption & " " & os.csdversion)
        osString = removeSpecialCharacters(osString)
        osString = trim(replace(osString,"  "," "))
    next
    
    resultStr = vbNewLine & "IBM Prerequisite Scanner" & vbNewLine
    resultStr = resultStr & "    Version  : " & ITPRS_VERSION & vbNewLine
    resultStr = resultStr & "    Build    : " & ITPRS_BUILD_ID & vbNewLine
    resultStr = resultStr & "    OS Name  : " & osString & vbNewLine
    resultStr = resultStr & "    User Name: " & wsh.ExpandEnvironmentStrings("%USERNAME%")
    
    getVersionInfo = resultStr
end function

function getMachineInfo()
    dim wmiObj, wshShell, computerName
    dim allOS, os, osSerial
    dim allBIOS, bios, biosSerial
    dim resultStr
    
    set wmiObj = getobject("winmgmts:{impersonationLevel=impersonate}!\\.")
    
    ' Get computer name
    set wshShell = WScript.CreateObject("WScript.Shell")
    computerName = WshShell.ExpandEnvironmentStrings("%COMPUTERNAME%")
    
    ' Get OS Serial Number
    Set allOS = wmiObj.ExecQuery("Select * from Win32_OperatingSystem")
    for each os in allOS
        osSerial = os.SerialNumber
    next
    
    ' Get BIOS Serial Number
    Set allBIOS = wmiObj.ExecQuery("Select * from Win32_BIOS")
    for each bios in allBIOS
        biosSerial = bios.SerialNumber
    next
    
    resultStr = vbNewLine & "Machine Info" & vbNewLine
    resultStr = resultStr & "   Machine name : " & computerName & vbNewLine
    resultStr = resultStr & "   Serial Number: " & biosSerial & vbNewLine
    resultStr = resultStr & "   OS Serial    : " & osSerial & vbNewLine

    getMachineInfo = resultStr    
end function

Sub showUsageAndQuit
    logInfo "Usage: prereq_checker.bat " & chr(34) & "<Product Code>  [product version],  <Product Code>  [product version]..." _
        & chr(34) & " [PATH=<Agent install path>]  [detail]  [-p <Product Code>.SECTION.NAME=VALUE pairs]"
    logScreen "Usage: prereq_checker.bat " & chr(34) & "<Product Code>  [product version],  <Product Code>  [product version]..." _ 
        & chr(34) & " [PATH=<Agent install path>]  [detail]  [-p <Product Code>.SECTION.NAME=VALUE pairs]"

    logScreen "Example: prereq_checker.bat " & chr(34) & "KNT,KUD 06200000" & chr(34) & " detail PATH=d:\ibm\itm -p SERVER=IP.PIPE://mytems:1918,LCM.ad=d:\"
    WScript.quit
End Sub

' =======================================================
' ============= Include common_function.vbs =============
' =======================================================
' We could load the file through code, but we lose line numbers on some error messages.
' So starting with the 0623 build, we'll try including the text of common_function.vbs
' directly into this file only.

' Include(PRS_BASE_DIR & "lib\common_function.vbs")

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
        changeMG = formatnumber(m(0),0, TristateTrue, TristateUseDefault, TristateFalse) & "MB"
        exit function
    end if

    g = getFirstMatch("([0-9\.,]{1,})[G][B].*", array(killdou))
    if ubound(g) = 0 then
        changeMG = formatnumber(g(0)*1024,0, TristateTrue, TristateUseDefault, TristateFalse) & "MB"
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
	m = getFirstMatch("([\-]*\d+)MB", array(UCase(size)))
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

' log an ERROR message
sub logError(msg)    
	log "ERROR! ",msg
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

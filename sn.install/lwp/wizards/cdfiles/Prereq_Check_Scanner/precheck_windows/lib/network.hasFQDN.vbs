'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/network_plug.vbs
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

' Networking-related properties
Option Explicit
' ---- TCP Ports in Use (Limited to IPv4 only for now) -----
Dim strNetstatCmd, strRegEx, strPortsInUse, matches, objWMI, objItem
Dim objShell, objShellExec, objRegEx

strNetstatCmd = "netstat -an -p tcp"
strRegEx = "TCP\s+\d+\.\d+\.\d+\.\d+:(\d+).*LISTENING.*"
strPortsInUse = ""

'Run netstat
Set objShell = WScript.createobject("WScript.Shell")
Set objShellExec = objShell.Exec(strNetstatCmd)

' Check that hostname has a fully-qualified domain name
dim hostName, adapterCount, fullHostName, allIPAdapters, foundMatchingDomain, objRegistry, strKeyPath, strValueName, strValue
foundMatchingDomain = false

' Current hostname
hostName = LCase(objShell.ExpandEnvironmentStrings("%COMPUTERNAME%"))
WScript.Echo "Local computer name found to be [" & hostName & "]"

set objWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
set allIPAdapters = objWMI.ExecQuery("SELECT * FROM Win32_NetworkAdapterConfiguration where IPEnabled=true")

adapterCount = 0
for each objItem in allIPAdapters
  if Len(objItem.DNSDomain) > 0 and Len(objItem.DNSHostName) > 0 then
      fullHostName = objItem.DNSHostName & "." & objItem.DNSDomain
      if LCase(objItem.DNSHostName)=hostName then
          WScript.Echo "Found fully-qualified domain name: " & hostName & "." & LCase(objItem.DNSDomain) & _
                       " for adapter: " & objItem.Description
          foundMatchingDomain = true
      else
          WScript.Echo "Hostname did not match [" & hostName & "] for " & fullHostName & " for adapter: " & objItem.Description
      end if
  end if
  adapterCount = adapterCount + 1
next
if adapterCount = 0 then
    WScript.Echo "No network adapters with IP enabled were found."
end if

' Check primary DNS suffix (if necessary)
if not foundMatchingDomain then
    const HKEY_LOCAL_MACHINE = &H80000002
    Set objRegistry=GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\default:StdRegProv")
    strKeyPath   = "SYSTEM\CurrentControlSet\services\Tcpip\Parameters"
    strValueName = "Domain"
    strValue = ""
    ' Read the NV Domain (primary DNS suffix) value from the registry
    ' The last parameter is the variable which will contain the result value
    ON ERROR RESUME NEXT
    objRegistry.GetStringValue HKEY_LOCAL_MACHINE, strKeyPath, strValueName, strValue
    if Err.Number = 0 then
        if Len(strValue) > 0 then
            WScript.Echo "Found primary DNS suffix [" & strValue & "]"
            foundMatchingDomain = true
        else
            WScript.Echo "Primary DNS suffix is not set"
        end if
    else
        WScript.Echo "Unable to read [HKEY_LOCAL_MACHINE\" & strKeyPath & "\" & strValueName & "] registry value"
    end if 
end if
    
WScript.Echo "network.hasFQDN=" & foundMatchingDomain  



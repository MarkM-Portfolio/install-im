'************ Begin Standard Header - Do not add comments here ***************
'
' File:     lib/os_plug.vbs
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

Set objWMI = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")

' ----- Basic OS & Memory Properties -----
' os.versionNumber           : numeric, period-separated version string (e.g. '6.1.7600')
' os.architecture            : '32-bit' or '64-bit'
' os.totalMemory             : Total memory (in MB) addressable  to the OS (physical + virtual)
' os.totalPhysicalMemory     : Total physical memory (in MB) addressable by the OS
' os.availablePhysicalMemory : Available physical memory (in MB) - Same as the 'Memory' property from common.vbs
' os.availableMemory         : Amount of free memory (in MB) (includes physical and virtual)

Set allOS = objWMI.ExecQuery("Select FreeVirtualMemory from Win32_OperatingSystem",,48)

For Each os in allOS
	WScript.Echo "os.availableMemory=" & int(os.FreeVirtualMemory/1024) & "MB"
Next

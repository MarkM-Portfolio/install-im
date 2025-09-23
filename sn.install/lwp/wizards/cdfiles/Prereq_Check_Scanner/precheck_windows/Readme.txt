===========================
IBM Prerequisite Scanner - Windows
Version: 1.0.43
Build:   20101204
===========================

This is a script-based tool intended to validate prerequisite requirements for a product or component.
Configuration files, in a simple Java properties file format, specify the properties to check and
the values needed for each check to pass.

By default, the tool will output "PASS" or "FAIL" indicating whether all of the expected
prerequisite conditions were met.  The tool can also generate a human-readable report containing
details on what checks were run as well as the expected and discovered values.


Requirements: Windows 2000 or higher, 32-bit or 64-bit.
              Windows 95, Windows 98, and Windows ME are not currently supported.            

=====
Usage
=====

1. Unzip to any folder, such as to C:\prereq_scanner

2. Open a command line interface and change the working directory to the folder
   (e.g. "cd C:\prereq_scanner")
   
3. Run prereq_checker.bat

    prereq_checker.bat "<Product Code1> [product version1],[ <Product Code2> [product version2]]..."
                       [detail] [debug] [PATH=<Agent install path>]  [-p Product Code.SECTION.NAME=VALUE pairs]

	Example:
        prereq_checker.bat KHD
    Example:
        prereq_checker.bat DMO detail
    Example:  
        prereq_checker.bat "KNT,KUD 06200000" detail PATH=d:\ibm\itm -p SERVER=IP.PIPE://mytems:1234

   Here is a detailed description of the input parameters:
   - <Product Code> [product version]
	- At least one product code is required.  The product code is
	  a 3 letter abbreviation for the component or agent.  The version is
          optional.  If you do not specify a version,  the prereq scanner will check the
          highest version available.
        - You may specify multiple product codes separated by commas. 
        - The list of product codes and versions considered a single input parameter and
          must be enclosed in double quotes on the command line.
	- Example "KNT 06200000, KUD"
              - In this example, we check the KNT Agent version 06200000 
                and the latest version of the KUD Agent.
        - For each component/agent, there will be a config (*.cfg) file.  Config files
          use a naming convention of [product code]_[version].cfg.  [version] is usually
          8 digits used to represent a V.R.M.L version using 2 digits for each part of the version.
          For example, a version of 7.2.23 would be represented as 07022300.
        - Optionally, each component/Agent could have a *.bat file to
          handle product-specific checks.  For example, the 
          KUD_06200000.bat file checks for the required level of DB2 code and only
          runs for the 06200000 version of KUD.
        - If you specify a product code that does not have a corresponding
          *.cfg file, it will be ignored without error, although the log file will
          contain a message that indicates the code could not be found.

   - [detail]
     - The 'detail' parameter is optional.  This flag indicates that you want to
       see detailed results on the screen when you execute the prereq scanner.
     - Do NOT enclose the word detail in quotes.
     - If you don't specify 'detail', then you will only see PASS or FAIL on the
       screen. This will make it easy to identify whether your system meets the
       prereqs.
     - If you do specify detail on the screen, your results will look like this:
      Windows OS monitoring Agent [version 06210000] :
      Evaluation          PASS/FAIL       Expected Result       Result
      CPU Number          PASS            1                         1
      Available Memory    PASS            35MB                      1.02GB
      Available Disk      PASS            70MB                      1.09GB

      ALL COMPONENTS :
      Evaluation          PASS/FAIL       Expected Result           Result
      Available Memory    PASS            35MB                      1.02GB
      Available Disk      PASS            70MB                      1.09GB
     - Regardless of whether you specify the 'detail', the detailed output is
       written to a file called precheck.log.   You can view this file to see
       the results of the prereq check.   In addition, you can view the 
       result.txt file to see the detailed steps taken by the prereq scanner.

   - [debug] Enables more detailed logging in the precheck.log file.  This is useful for
             support, troubleshooting, or for help debugging custom plugins.

   - [PATH=<product install path>]
     The PATH parameter is optional. If you do not specify a PATH parameter, the
     prereq scanner will check the default ITM install paths which are:
     - C:\IBM\ITM on Windows 
     - /opt/IBM/ITM on UNIX and Linux platforms
     Example PATH would be PATH=D:\IBM\ITM
     Note:  Paths must NOT be just drive letters (e.g. "C:").  You must use a
            potentially valid path like "C:\" or "D:\IBM\myProduct\bin"
	
   - Optional Parameters may be specified using a '-p" flag.
     - These optional parameters are passed into the agent/component *.bat
       file for that Agent or component. 
     - The format of the parameters is:
       [-p <Product Code>.<instance>.<parameter>=<value>, 
	    <Product Code>.<instance>.<parameter>=<value>, ...]
     - These parameters are used to pass in optional parameters that will be
       used by the scripts for each agent.   
     - Only the <instance>.<parameter>=<value> will be passed into the scripts.
       the <Product Code> is used to identify which script should receive the
       parameters.
     - If no product code is specified, the parameter will be sent to common
       collector plugins, the _plug.bat/vbs files under ./lib.
     - Example:
       -p KUD.inst1.DB2_INST_OWNER=db2inst1, KUD.inst2.DB2_INST_OWNER=db2inst2
       This will pass in the db2inst1.DB2_INST_OWNER=db2inst1 and 
       db2inst2.DB2_INST_OWNER=db2inst2 to the KUD.<version>.bat script.
     - Example2:
       -p SERVER=IP.PIPE://mymachine:1918 will be used to check the ports. 
       Note: The script accepts the parameters in -p as "tacmd createNode", the user can
       specify SERVER, PROTOCOL, PORT, BACKUP, BSERVER, etc. Also the user can configure
       the parameters in common_configuration in ./lib. Values passed in on the
       command-line is higher take priority over those in the common_configuration file.
       Note: All the parameters are case sensitive.
     - For additional details on the optional parameters used by TAD and LCM, see the
       LCM_TAD_plugin_readme.txt file.
       
3. For detailed results, open the "result.txt" file

4. Debug-level information can be found in "precheck.log"

====================
Available Properties
====================
    
    =======================
    = Original Properties =
    =======================
    intel.cpu - Checks the speed of an Intel CPU
    risc.cpu - Checks the speed of a RISC CPU    
    # CPU  - (DEPRECATED) Number of logical CPUs in the machine (a 2-processor machine with 2 cores per processor will report 4 CPUs.)    
    numCPU - Replacement for the '# CPU' property 
    Memory - Amount of memory (physical + virtual) currently available on the machine    
    Disk - Amount of free disk space.  The amount of space required can be specified directly or can be qualified with a path
           and units.  Examples:
           Disk=340MB
           Disk=3.4GB
           Disk=[dir:D:\my\custom\installDir\;unit:MB]760
           Disk=[dir:D:\some\other\dir\;unit:GB]5
           The 'Disk' property can be specified multiple times in a configuration file.  This is useful for when space is
           consumed in multiple directories or when using sections to conditionally include certain properties
           in a configuration file
    DB2 Version - Version of DB2 currently installed on the machine    
    OS Version - The full name of the OS and service pack on the machine (e.g. "Windows Server 2003 Standard Edition Service Pack 1")

    ===========
    = Network =
    ===========
    network.availablePorts.*  - Checks that the port or port range listed are all available (not being listened to)
    network.portsInUse.*      - Checks that the port or port range listed are in use (actively being listened to)
    network.DHCPEnabled       - True if at least one adapter with a valid IP address obtained that address through DHCP
    network.netBIOSEnabled    - True if at least one adapter with a valid IP address has NetBIOS enabled as a protocol
    network.pingLocalhost     - True if 'localhost' responds to the ping protocol    
    network.pingSelf          - True if the local computer name can be resolved by DNS and pinged
    network.validateHostsFile - True if all entries in the HOSTS file are of the format IP_ADDRESS HOSTNAME.DOMAIN.NAME HOSTNAME
                              - (example: 9.1.2.3 shortname.raleigh.ibm.com shortname)
    
    ====================
    = Operating System =
    ====================
    os.versionNumber - Numeric OS version number (e.g. "6.1.7600" for Windows 7)
        Config File syntax (applies to all properties that are version numbers)
            - Match exactly: os.versionNumber=6.1.7600
            - Match "at least this version" : os.versionNumber=6.0+
            - Match "at most this version" : os.versionNumber=6.1.7600-
            - Match wildcards: os.versionNumber=6.*
        Version matching code is generic and can be applied to additional
        properties (e.g. installed software versions) in the future    
    os.servicePack - Numeric service pack number as majorVersion.minorVersion (usually only majorVersion is used)    
    os.architecture - "32-bit" or "64-bit" - Accepts only 1 value as no check is
                       needed if you support both value anyway    
    os.totalMemory - Total amount of memory the OS has access to
                     (total physical + total virtual)    
    os.totalPhysicalMemory - Total amount of physical memory addressable by the OS
                             Note: Even though you may have say 4GB of memory
                             installed, 32-bit Windows can't address all of it, so
                             this number will be lower    
    os.availableMemory - Current amount of unused physical + virtual memory    
    os.isServiceRunning.remoteRegistry - True if the remote registry service is running    
    os.isServiceRunning.DNSClient - True if the DNSClient service is running    
    os.isServiceRunning.terminalServices - True if the Terminal Services service is installed and running    
    os.is8dot3FileFormatEnabled - True if 8.3 format filenames are being automatically generated    
    os.autoUpdateEnabled - True if Windows Update auto update is enabled

    ======================
    = Installed Software =
    ======================
    installedSoftware - Basic software scanning detects programs in the Windows
                        Registry.  Currently filters out programs without
                        registered install locations, but this is easily
                        changed if needed.                        
    cygwinVersion - Version of cygwin (www.cygwin.com) installed on the system.  Version will be 0.0 if Cygwin is not installed.
    gskit7Version - Version of GSKit 7.x installed on the system.  Version will be 0.0 if GSKit 7 is not installed.
    gskit8Version - Version of GSKit 8.x installed on the system.  Version will be 0.0 if GSKit 8 is not installed.
                        
    ================
    = Current User =
    ================                    
    user.isAdmin - True if the currently logged in user is a member of the Administrators group
    user.userID - The currently logged in user.  Use this if a certain userID must be logged in (e.g. root)
    
    ===============
    = Environment =
    ===============                    
    env.CIT.homeExists - True if the HOMEDRIVE and HOMEPATH environment variables are defined and together point
                         to an existing folder.
    env.classpath.derbyJAR - True if derby.jar is in the system CLASSPATH environment variable.  The main use of this property
                             is to check for a value of 'false'

=====================================
Product Codes and Configuration Files
=====================================
The Prerequisite Scanner uses a 3-letter code to identify products.  The codename.cfg file contains a mapping
of codes to full product/component names.  The current set of codes includes:
    KMS=Tivoli Enterprise Monitoring Server
    KCQ=Tivoli Enterprise Portal Server
    KCJ=Tivoli Enterprise Portal Client
    KSY=Summarization and Prunning Agent
    KHD=Warehouse Proxy Agent
    KUX=Unix Monitoring Agent
    KNT=Windows OS monitoring Agent
    KUD=DB2 Monitoring Agent
    LCM=Tivoli License Compliance Manager
    KOR=Oracle Monitoring Agent
    TAD=Tivoli Asset Discovery for Distributed
    DMO=Prereq Scanner Demo
    DEW=Autonomic Deployment Engine (Windows 2000)
    DEY=Autonomic Deployment Engine (Windows 2003)
    DEZ=Autonomic Deployment Engine (Windows)
    COB=Tivoli Provisioning Manager for AIX
    COC=Tivoli Provisioning Manager for AIX 5.3
    COD=Tivoli Provisioning Manager for AIX 6.1
    COE=Tivoli Provisioning Manager for Linux
    COF=Tivoli Provisioning Manager for Red Hat
    COG=Tivoli Provisioning Manager for Red Hat Enterprise Linux 5 x86 64-bit
    COH=Tivoli Provisioning Manager for Red Hat Enterprise Linux 5 System z 64-bit
    COI=Tivoli Provisioning Manager for SUSE
    COJ=Tivoli Provisioning Manager for Solaris
    COK=Tivoli Provisioning Manager for HP-UX
    COX=Tivoli Provisioning Manager (Windows 2008)
    COY=Tivoli Provisioning Manager (Windows 2003)
    COZ=Tivoli Provisioning Manager (Windows)
    PAE=Tivoli Process Automation Engine
    TOTAL=TOTAL ALL Specified Components

====
Misc
====

There are three optional parameters for Oracle Monitoring Agent:
    KOR.instance=<number>       --- the number of monitoring instances
				    default is 1
    KOR.TempDatafile=<path>     --- the path contains the temperary tablespace datafile
                                    default is the same as PATH
    KOR.Datafile=<path>         --- the path contains the tablespace datafile
                                    default is the same as PATH
	The parameters follow -p and seperated by comma.

Telnet is required for the 'Connectivity' check to function properly.

In order to enable the telnet tool on new windows platforms
(Vista / 2008 / 2008 R2 / 7) you need to do the following:
  1) Click Start button
  2) Open 'Control Panel'
  3) Choose 'Program and Features'
  4) In the 'Program and Features' section choose 'Turn Windows
     features on or off'
  5) In the newly displayed 'Windows Features' windows find and
     select the check box next to 'Telnet Client', and click OK.

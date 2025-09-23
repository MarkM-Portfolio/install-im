# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2012
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


##################################################################################################
# PLUGIN CONFIGURATION
##################################################################################################

Before executing the LCM/TAD scripts, a few parameters must be reviewed and changed as necessary.

In LCM.sh/TAD.sh files, the prereq_checker.sh script input parameters should be verified 
according to the content in the Parameters Overview section below or the prereq_checker topic 
in the IBM Prerequisite Scanner User's Guide.



##################################################################################################
# PARAMETERS OVERVIEW
##################################################################################################

All directory values should be valid Windows or UNIX paths for Prerequisite Scanner to perform 
the scan correctly, for example, use C:\ instead of C:.

PATH:	Product installation path 
		The default is as follows:
		- On UNIX systems: /var/itlm
		- On Windows systems: %WINDIR%/itlm

LCM.WASAgent/	
TAD.WASAgent:	Boolean value (true/false)
				Specifies whether an agent is installed on a machine with IBM WebSphere Application 
				Server, with additional space being required for the installation
				The default value is true 
                           
SERVER:			Server address that points to a valid TLCM/LMT/TAD4D runtime or administration 
				server with a port

LCM.CIT/	
TAD.CIT:		Path to the directory in which CIT is installed
				The default is as follows:
				- On UNIX systems: /opt/tivoli/cit
				- On Windows systems: C:\Program Files\tivoli\cit

LCM.TCD/
TAD.TCD:        Path to the directory in which Tivoli Common Directory is located
				The default is as follows:
				- On UNIX systems: /var/ibm/tivoli/common
				- On Windows systems: C:\Program Files\IBM\tivoli\common 

LCM.TEMP/
TAD.TEMP:       Path to a temporary system directory
				The default is as follows:
				- On UNIX systems: /tmp 
				- On Windows systems: %TEMP%

LCM.HOMEROOT: 	Path to the root home directory; the default is /root

TAD.SWDCLI:		Path to the SWDCLI registry directory
				The default is as follows:
				- On UNIX systems:  /.swdis or /root/.swdis
				- On Windows systems: C:\swdis

LCM.ETC/
TAD.ETC:		Path to the system's /etc directory

LCM.LIB:		Path to the system's /lib directory	

LCM.USRSBIN:	Path to the system's /usr/sbin directory
                           
LCM.WINDIR/
TAD.WINDIR:		Path to the Windows installation directory; the default is %WINDIR%

outputDir:		Path to the Prerequisite Scanner output directory
				The default is as follows:
				- On UNIX systems: /tmp/PRS
				- On Windows systems: %TEMP%\prs



##################################################################################################
# PLUGIN EXECUTION
##################################################################################################

Execution permission rights must be set for the LCM.sh and TAD.sh files on UNIX systems.

For the LCM plugin, the following script must be executed:
		- On UNIX systems: LCM.sh
		- On Windows systems: LCM.bat

For the TAD plugin, the following script must be executed:
		- On UNIX systems: TAD.sh
		- On Windows systems: TAD.bat


##################################################################################################
# TROUBLESHOOTING
##################################################################################################

Information about the Prerequisite Scanner scan results can be found in the precheck.log and 
result.txt files.

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

Before executing the TAD scripts, a few parameters must be reviewed and changed as necessary.

In the TAD.sh files, the prereq_checker.sh script input parameters should be verified according to the content in 
the Parameters Overview section below or the prereq_checker topic in IBM Prerequisite Scanner User's Guide.



##################################################################################################
# PARAMETERS OVERVIEW
##################################################################################################

All directory values should be valid Windows or UNIX paths for Prerequisite Scanner to perform the scan
correctly, for example, use C:\ instead of C:.


PATH:		Product installation path 
		The default is as follows:
		- On UNIX systems: /var/itlm
		- On Windows systems: %WINDIR%/itlm

SERVER:		Server address that points to a valid LMT/TAD4D runtime or administration server with a port

TAD.CIT:		Path to the directory in which CIT is installed
		The default is as follows:
		- On UNIX systems: /opt/tivoli/cit
		- On Windows systems: C:\Program Files\tivoli\cit

TAD.TCD: 	Path to the directory in which Tivoli Common Directory is located
		The default is as follows:
		- On UNIX systems: /var/ibm/tivoli/common
		- On Windows systems: C:\Program Files\IBM\tivoli\common	

TAD.TEMP:	Path to a temporary system directory
		The default is as follows:
		- On UNIX systems: /tmp 
		- On Windows systems: %TEMP%

TAD.ETC:	Path to the system's /etc directory
                           
TAD.WINDIR:	Path to the Windows installation directory; the default is %WINDIR%

outputDir:	Path to the Prerequisite Scanner output directory
		The default is as follows:
		- On UNIX systems: /tmp/PRS
		- On Windows systems: %TEMP%\prs



##################################################################################################
# PLUGIN EXECUTION
##################################################################################################

Execution permission rights must be set for the TAD.sh file on UNIX systems.

For the TAD plugin, the following script must be executed:
		- On UNIX systems: TAD.sh
		- On Windows systems: TAD.bat



##################################################################################################
# TROUBLESHOOTING
##################################################################################################

Information about the Prerequisite Scanner scan results can be found in the precheck.log and result.txt files.

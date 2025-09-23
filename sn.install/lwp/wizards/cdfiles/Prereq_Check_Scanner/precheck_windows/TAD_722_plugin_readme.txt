# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


##################################################################################################
# PLUGIN CONFIGURATION:
##################################################################################################

Before executing TAD scripts, a few parameters should be reviewed and changed if necessary.

In TAD.sh files, the prereq_checker.sh script execution parameters should be verified 
according to the parameters overview below or Readme.txt supplied with the PRC.



##################################################################################################
# PARAMETERS OVERVIEW:
##################################################################################################

All directory values should be valid Windows/UNIX paths for the PRC to do the calculations 
correctly (i.e. use C:\ instead of C:).


PATH:          product installation path (default: /var/itlm or %WINDIR%/itlm),
               (detailed parameter description in Readme.txt),

SERVER:        this address should point to a valid LMT/TAD4D runtime/admin server
               with port (detailed parameter description in Readme.txt),

TAD.CIT:       path to a directory where CIT will be installed (default: /opt/tivoli/cit
               or C:\Program Files\tivoli\cit),

TAD.TCD:       path to a directory where Tivoli Common Directory is located 
               (default: /var/ibm/tivoli/common or C:\Program Files\IBM\tivoli\common)

TAD.TEMP:      path to a temporary system directory (default: /tmp or %TEMP%),

TAD.ETC:       should point to system /etc directory,
                           
TAD.WINDIR:    path to Windows installation directory (default: %WINDIR%)



##################################################################################################
# PLUGIN EXECUTION:
##################################################################################################

Execution permission rights should be set for TAD.sh on UNIX systems.

For TAD plugin, TAD.sh/TAD.bat should be executed.



##################################################################################################
# TROUBLESHOOTING:
##################################################################################################

Information about the PRC execution status can be found in precheck.log and result.txt files.

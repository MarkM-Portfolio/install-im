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

Before executing LCM/TAD scripts, a few parameters should be reviewed and changed if necessary.

In LCM.sh/TAD.sh files, the prereq_checker.sh script execution parameters should be verified 
according to the parameters overview below or Readme.txt supplied with the PRC.



##################################################################################################
# PARAMETERS OVERVIEW:
##################################################################################################

All directory values should be valid Windows/UNIX paths for the PRC to do the calculations 
correctly (i.e. use C:\ instead of C:).


PATH:                     product installation path (default: /var/itlm or %WINDIR%/itlm),
                          (detailed parameter description in Readme.txt),

LCM.WASAgent/TAD.WASAgent (true/false): specifies whether an agent will be installed on a machine
                          with WAS, additional space is then required for the installation
                          (default: true),
                           
SERVER:                   this address should point to a valid TLCM/LMT/TAD4D runtime/admin server
                          with port (detailed parameter description in Readme.txt),

LCM.CIT/TAD.CIT:          path to a directory where CIT will be installed (default: /opt/tivoli/cit
                          or C:\Program Files\tivoli\cit),

LCM.TCD/TAD.TCD:          path to a directory where Tivoli Common Directory is located 
                          (default: /var/ibm/tivoli/common or C:\Program Files\IBM\tivoli\common)

LCM.TEMP/TAD.TEMP:        path to a temporary system directory (default: /tmp or %TEMP%),

LCM.HOMEROOT:             path to a root home directory (default: /root),

TAD.SWDCLI:               path to SWDCLI registry directory (usually /.swdis , /root/.swdis 
                          or C:\swdis),

LCM.ETC/TAD.ETC, LCM.LIB, LCM.USRSBIN: should point to system /etc, /lib, /usr/sbin directories,
                           
LCM.WINDIR/TAD.WINDIR:    path to Windows installation directory (default: %WINDIR%)



##################################################################################################
# PLUGIN EXECUTION:
##################################################################################################

Execution permission rights should be set for LCM.sh, TAD.sh on UNIX systems.

For LCM plugin, LCM.sh/LCM.bat should be executed.

For TAD plugin, TAD.sh/TAD.bat should be executed.



##################################################################################################
# TROUBLESHOOTING:
##################################################################################################

Information about the PRC execution status can be found in precheck.log and result.txt files.
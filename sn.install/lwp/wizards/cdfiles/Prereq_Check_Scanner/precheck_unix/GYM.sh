#!/bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

# Initially set all components to False

TNPM_DL=False; export TNPM_DL
TNPM_DC=False; export TNPM_DC
TNPM_DM=False; export TNPM_DM
TNPM_DB=False; export TNPM_DB
TNPM_DV=False; export TNPM_DV

if [ $# -eq 0 ]
then
   # Set all component env vars to true
   TNPM_DL=True; export TNPM_DL
   TNPM_DC=True; export TNPM_DC
   TNPM_DM=True; export TNPM_DM
   TNPM_DB=True; export TNPM_DB
   TNPM_DV=True; export TNPM_DV
else
   while [ $# -gt 0 ]
   do
      case "$1" in
         "DL")
            TNPM_DL=True; export TNPM_DL
            shift;;
         "DC")
            TNPM_DC=True; export TNPM_DC
            shift;;
         "DM")
            TNPM_DM=True; export TNPM_DM
            shift;;
         "DB")
            TNPM_DB=True; export TNPM_DB
            shift;;
         "DV")
            TNPM_DV=True; export TNPM_DV
            shift;;
         *)
            echo "ERROR: Invalid Component: $1"
            exit 1
      esac
   done

fi

if [ "${TNPM_DV}" = "True" ]
then
   COMPS="GYM,TCR"
else
   COMPS="GYM"
fi

chmod +x ./prereq_checker.sh
./prereq_checker.sh "${COMPS}" detail outputDir=/tmp/prs


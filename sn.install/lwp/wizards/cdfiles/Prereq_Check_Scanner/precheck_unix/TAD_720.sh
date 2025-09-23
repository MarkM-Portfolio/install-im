#!/bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

# RTC Defect 20889
#       Find the working directory for all the supported platforms
#       Working directory "PREREQ_HOME" is available throughout the execution of PRS

if [ "`uname`" = "SunOS" ];then
        PRS_HOME=`echo $0`
        PREREQ_HOME=`dirname $PRS_HOME`
        if [ "$PREREQ_HOME" = "." ]; then
                PREREQ_HOME=`pwd`
        else
                cd $PREREQ_HOME
                PREREQ_HOME=`pwd`
        fi
else
        PRS_HOME=`dirname $0`
        cd $PRS_HOME
        PREREQ_HOME=$PWD
        cd - > /dev/null 2>&1
fi

$PREREQ_HOME/prereq_checker.sh "TAD 07200000" detail PATH=/var/itlm -p TAD.WASAgent=true,SERVER=IP.PIPE://localhost:9988,TAD.CIT=/opt/tivoli/cit,TAD.TCD=/var/ibm/tivoli/common,TAD.TEMP=/tmp,TAD.ETC=/etc,TAD.SWDCLI=/root/.swdis   outputDir=/tmp/prs 

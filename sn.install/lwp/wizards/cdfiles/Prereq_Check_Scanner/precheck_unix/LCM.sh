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


$PREREQ_HOME/prereq_checker.sh "LCM 02300000" detail PATH=/var/itlm -p LCM.WASAgent=true,SERVER=IP.PIPE://localhost:9988,LCM.CIT=/opt/tivoli/cit,LCM.TCD=/var/ibm/tivoli/common,LCM.TEMP=/tmp,LCM.MAIN=/,LCM.HOMEROOT=/root,LCM.ETC=/etc,LCM.LIB=/lib,LCM.USRSBIN=/usr/sbin,  outputDir=/tmp/prs

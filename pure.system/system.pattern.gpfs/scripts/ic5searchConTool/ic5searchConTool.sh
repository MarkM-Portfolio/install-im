#!/bin/bash
# description: 
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2010, 2014                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

basepath=$(pwd -L)
if [ ! -d "${basepath}/logs" ]; then
 mkdir ${basepath}/logs
fi

## Handle output. Send STDOUT to console AND file, STDERR to file
LOGOUT=${basepath}/logs/logOUT-$(date +"%Y-%m-%d_%H-%M").log
LOGERR=${basepath}/logs/logERR-$(date +"%Y-%m-%d_%H-%M").log
exec > >(tee ${LOGOUT})
exec 2>> ${LOGERR}

## load variables from local pure pattern declaration
if [ -f /etc/virtualimage.properties ]; then
 . /etc/virtualimage.properties
fi

## LOAD PROJECT PROPERTIES FILE
NFSSHARE=${MOUNT_POINT}
CONFDIR="${NFSSHARE}/pureshare/config/${PROJECTNAME}"
CONFFILE=ic5pattern.properties
if [ -f ${CONFDIR}/${CONFFILE} ]; then
 echo -e "\n\nLoading project variables - \""${PROJECTNAME}"\""
 . ${CONFDIR}/${CONFFILE}
else
 echo "Error loading properties file for \""${PROJECTNAME}"\" ("${CONFDIR}"/"${CONFFILE}") - aborting Installation."
 exit 1
fi

markup()
{
echo ""
echo "$1 function $2"
echo "---------------------------------------------------------------------"
echo ""
echo ""
}

copySearchTools()
{
markup starting "Copy Search Conversation Tools to local nodes"
if [ -d ${INSTLOC}/data/local/search/stellent/dcs/oiexport ]; then
 su - ${RTUSR} -c "cp -Rfp ${ICSHAREDDIR}/search/stellent/dcs/oiexport/* ${INSTLOC}/data/local/search/stellent/dcs/oiexport/"
 echo "su - ${RTUSR} -c "cp -Rfp ${ICSHAREDDIR}/search/stellent/dcs/oiexport/ ${INSTLOC}/data/local/search/stellent/dcs/oiexport/""
 else
 echo "su - ${RTUSR} -c "mkdir -p ${INSTLOC}/data/local/search/stellent/dcs/oiexport""
 echo "su - ${RTUSR} -c "cp -Rfp ${ICSHAREDDIR}/search/stellent/dcs/oiexport/ ${INSTLOC}/data/local/search/stellent/dcs/oiexport/""
 su - ${RTUSR} -c "mkdir -p ${INSTLOC}/data/local/search/stellent/dcs/oiexport"
 su - ${RTUSR} -c "cp -Rfp ${ICSHAREDDIR}/search/stellent/dcs/oiexport/* ${INSTLOC}/data/local/search/stellent/dcs/oiexport/"
fi
markup stopping "Copy Search Conversation Tools to local nodes"
}

wasSetupCmdSearch()
{
APPSERVERROOT=/opt/IBM/WebSphere/Profiles
echo APPSERVERROOT=${APPSERVERROOT}
cd ${APPSERVERROOT}
find ${APPSERVERROOT} -name DefaultCustom* > custNode.txt
CUSTNODETRUEPATH=`cat custNode.txt|awk '{printf $0}'`
echo CUSTNODETRUEPATH=${CUSTNODETRUEPATH}

SearchBinariesHome=${INSTLOC}/data/local/search/stellent/dcs/oiexport
app_server_root=${CUSTNODETRUEPATH}
markup starting "Setting PATH Variables to SetupCmdLine Tool"
CHECK01=`grep ${SearchBinariesHome} ${app_server_root}/bin/setupCmdLine.sh`
if [ "${CHECK01}" != "" ]; then
echo "VARs exists in ${app_server_root}/bin/setupCmdLine.sh"
else
echo "export PATH=\$PATH:${SearchBinariesHome}" >> ${app_server_root}/bin/setupCmdLine.sh
echo "export LD_LIBRARY_PATH=${SearchBinariesHome}:\$LD_LIBRARY_PATH" >> ${app_server_root}/bin/setupCmdLine.sh
fi
markup stopping "Setting PATH Variables to SetupCmdLine Tool"
}


case $1 in
	*)
	copySearchTools
	wasSetupCmdSearch
	;;
esac

exit 0
#!/bin/bash
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
 exit
fi

markup()
{
echo ""
echo "$1 function $2"
echo "---------------------------------------------------------------------"
echo ""
echo ""
}

execPYscript() {

 ## execute python scripts for wsadmin tasks
 echo "su - ${RTUSR} -c \"cd ${basepath} && /opt/IBM/WebSphere/AppServer/bin/wsadmin.sh ${DMGRURL} -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*\""
 su - ${RTUSR} -c "cd ${basepath} && /opt/IBM/WebSphere/AppServer/bin/wsadmin.sh ${DMGRURL} ${WSOPT} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*"
}

checkPort() {
 ## checkPort HOST PORT
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -n "Checking port status - ${1}:${2} - "
  (echo >/dev/tcp/${1}/${2}) &> /dev/null
  if [ "$?" -eq 0 ]; then
   echo "online"
   return 1
  else
   echo "offline"
   return 0
  fi
 fi
}

wasAdjustMoreSettings()
{
markup starting "Customizing JVM and other Settings"
systemMemory=$(cat /proc/meminfo |grep MemTotal |cut -d: -f2 | sed 's/ //g')
PYSCRIPT=wasJvmAdjust.py
#OPTS="systemMemory=$systemMemory hostName=${HOST_NAME} nodeName=${NODE_NAME}"
OPTS="systemMemory=$systemMemory hostName=${NOD2SRV} nodeName=${NODE_NAME}"
#OPTS="systemMemory=$systemMemory nodeName=${NODE_NAME}"
execPYscript ${OPTS}
markup finished "Customizing JVM and other Settings"
}

case $1 in
	*)
	wasAdjustMoreSettings
	;;
esac

umount ${MOUNT_POINT}

exit 0



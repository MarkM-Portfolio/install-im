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
#
#
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
 echo "su - ${RTUSR} -c \"cd ${basepath} && /opt/IBM/WebSphere/AppServer/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*\""
 su - ${RTUSR} -c "cd ${basepath} && /opt/IBM/WebSphere/AppServer/bin/wsadmin.sh ${WSOPT} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*"
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

statusDMGR()
{
su - ${RTUSR} -c "${DMGRP}/bin/${1}Manager.sh"
}

wasAdjustMoreSettings()
{

markup starting "Customizing JVM and other Settings"
systemMemory=$(cat /proc/meminfo |grep MemTotal |cut -d: -f2 | sed 's/ //g')
PYSCRIPT=wasJvmAdjust.py
OPTS="systemMemory=$systemMemory nodeName=${NODE_NAME}"
execPYscript ${OPTS}

PYSCRIPT=wasPerformanceAdjust.py
OPTS="threadpoolMaxSize=100 nodeName=${NODE_NAME} dbhost=${STANDBYDB2SRV}"
execPYscript ${OPTS}

PYSCRIPT=db2_hadr.py
OPTS="dbhost=${STANDBYDB2SRV} dbhostport=${DBPORTALL}"
execPYscript ${OPTS}
markup finished "Customizing JVM and other Settings"
}

changeownerShip()
{
cd /opt/IBM/WebSphere
chown -R virtuser.admingroup *
}

removePW()
{
RTPWD=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${remROOTpw}" -silent -noSplash"`
APPPWD=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${CONNECTIONSADMINPASSWD}" -silent -noSplash"`
DBPASSWD=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${DBPASSWDdecrypt}" -silent -noSplash"`
BINDPWENCRYT=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${BINDPW}" -silent -noSplash"`

PARSE1="remROOTpw:${RTPWD} CONNECTIONSADMINPASSWD:${APPPWD} DBPASSWDdecrypt:${DBPASSWD} BINDPW:${BINDPWENCRYT}"
DESTFILE=${CONFDIR}/${CONFFILE}
if [ "${PROFILE_TYPE}" == "dmgr" ]; then
 for RES in ${PARSE1}; do
  RES1=`echo ${RES} |cut -d: -f1`
  RES2=`echo ${RES} |cut -d: -f2`
  PARA0=`grep ^${RES1} ${DESTFILE} |cut -d= -f2`
  if [ "${PARA0}" == "${RES2}" ]; then
     echo "Paramter ${RES1} exists and has the correct value"
    else
     echo "Changing paramter ${RES1} in file ${DESTFILE}"
     #sed -i "s/$RES1=${PARA0}/${RES1}=${RES2}/g" ${DESTFILE}
	 ${basepath}/searchAndReplace.py -f ${DESTFILE} -o $RES1=${PARA0} -n ${RES1}=${RES2}
  fi
 done
fi

CONFFILEOLD=ic5pattern.properties_OLD
if [ -f ${CONFDIR}/${CONFFILEOLD} ]; then
 rm ${CONFDIR}/${CONFFILEOLD}
fi
}

case $1 in
	*)
	wasAdjustMoreSettings
	statusDMGR stop			# stop DMGR
	changeownerShip
	statusDMGR start		# start DMGR
	removePW
	;;
esac

umount ${MOUNT_POINT}

exit 0



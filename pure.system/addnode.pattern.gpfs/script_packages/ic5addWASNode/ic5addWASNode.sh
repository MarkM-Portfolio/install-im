#!/bin/bash
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

#password="$(python decodePassword.py wsadmin=/opt/IBM/WebSphere/AppServer/bin/wsadmin.sh password=${passwordDecoded} mode=decode)"

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
 echo "su - ${RTUSR} -c \"cd ${basepath} && /opt/IBM/WebSphere/AppServer/bin/wsadmin.sh ${WSOPT} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*\""
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

wasManageClusterMembers()
{
markup starting "Adding node as Cluster Member to extisting IC Clusters"
PYSCRIPT=wasManageClusterMembers.py
OPTS="nodeName=${NODE_NAME}"
execPYscript ${OPTS}
markup finished "Adding node as Cluster Member to extisting Clusters"
}


case $1 in
	auto)
	wasManageClusterMembers
	;;
	*)
	echo "usage: $0 < auto >"
	;;
esac

exit 0
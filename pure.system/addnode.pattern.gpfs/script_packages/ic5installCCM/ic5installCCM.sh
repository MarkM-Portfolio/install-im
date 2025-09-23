#!/bin/bash
# description: 
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

## include function declaration from external shell scripts located beneath ./bin directory
for file in $(ls ${basepath}/bin/ | grep ".*\.sh$");
do
 source ${basepath}/bin/${file}
done

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

## VARIABLES FOR PROJECT PROPERTIES.. REMOVE IF ADDED TO PROPERTIES FILE
ccmClusterName=CCMCluster
dmgrPath=${DMGRP}
IMPath=/opt/IBM/InstallationManager
ccmPath=${INSTLOC}/addons/ccm

setCcmVariables() {
 cat > ${basepath}/ccm.properties <<EOF
RSFinstallLocation=${INSTLOC}
RSFuserWasAdminuserId=${CONNECTIONSADMIN}
RSFuserCcmAdminuserId=${CONNECTIONSADMIN}
RSFuserWasAdminuserPassword=${CONNECTIONSADMINPASSWD}
RSFuserCcmAdminuserPassword=${CONNECTIONSADMINPASSWD}
RSFuserDeploymentType=${DEPLOY}
RSFuserCcmInstallersPath=${NFSSHARE}/software/Filenet
RSFuserCcmClusterName=${ccmClusterName}
RSFuserCcmFirstNodeName=${NODE_NAME}
RSFuserCcmFirstNodeServerName=${ccmClusterName}_server1
RSFuserCcmSecondaryNodesNames=""
RSFuserCcmSecondaryNodesServerNames=""
RSFuserCcmDbType=db2
RSFuserCcmJdbcLibraryPath=${JDBCPATH}
RSFuserCcmGcdDbHostName=${DB2SRV}
RSFuserCcmGcdDbPort=${DBPORTALL}
RSFuserCcmGcdDbUser=${DBUSRALL}
RSFuserCcmGcdDbUserPassword=${DBPASSWDdecrypt}
RSFuserCcmObjstoreDbHostName=${DB2SRV}
RSFuserCcmObjstoreDbPort=${DBPORTALL}
RSFuserCcmObjstoreDbUser=${DBUSRALL}
RSFuserCcmObjstoreDbUserPassword=${DBPASSWDdecrypt}
EOF
}

initialExecute() {
 ## check how many servers exist in cluster
 clusterDir=${WAS_PROFILE_ROOT}/config/cells/${CELLN}*/clusters/${ccmClusterName}
 if [ ! -d ${clusterDir} ]; then
  setCcmVariables
  countServerInCluster=0
  serverName=${ccmClusterName}_server1
 else
  countServerInCluster=$(cat ${clusterDir}/cluster.xml | grep "<member" | wc -l)
  (( countServerInCluster++ ))
  serverName=${ccmClusterName}_server${countServerInCluster}
 fi
 echo -e "\n\nCopy scripts to DMGR"
 expect -c "
  set timeout 30
  spawn bash -c \"rsync -am -f '+ *.sh' -f '+ *.py' -f '+ *.template' -f '+ *.xml' -f '+ *.properties' -f '-! */' ${basepath}/* ${RTUSR}@${DMGRURL}:${basepath}\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${CONNECTIONSADMINPASSWD}\r\" }
  expect \"denied\" { puts \"Error with remote authentication\"; exit 1 }
  expect timeout { puts \"'expect' timeout reached\"; exit 1 }
  interact"
 if [ "$?" -eq 1 ]; then
  echo -e "\n\nError within expect command - aborting installaton"
  exit
 fi
 echo -e "\n\nChange ownership"
 expect -c "
  set timeout 30
  spawn ssh ${DMGRURL} \"chown -R ${RTUSR}: /ibm\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${remROOTpw}\r\" }
  expect timeout { puts \"'expect' timeout reached\"; exit }
  interact"
 if [ "${countServerInCluster}" == 0 ]; then
  checkPort ${DB2SRV} ${DBPORTALL}
  if [ "$?" -eq 0 ]; then
   echo -e "\n\nError reaching Database - aborting installaton"
   exit
  fi
  echo -e "\n\nInstalling first CCM node into cluster \"${ccmClusterName}\" via remote installation on DMGR"
  expect -c "
   spawn ssh ${RTUSR}@${DMGRURL} \"cd ${basepath}; ${basepath}/$(basename $0) remoteExecute ${NODE_NAME} ${serverName}\"
   expect \"continue connecting\" { send \"yes\r\" }
   expect \"assword:\" { send \"${CONNECTIONSADMINPASSWD}\r\" }
   interact"
 else
  echo -e "\n\nAdding Node ${NODE_NAME} to CCM cluster \"${ccmClusterName}\" via remote installation on DMGR"
  expect -c "
   spawn ssh ${RTUSR}@${DMGRURL} \"cd ${basepath}; ${basepath}/$(basename $0) addNode ${NODE_NAME} ${serverName}\"
   expect \"continue connecting\" { send \"yes\r\" }
   expect \"assword:\" { send \"${CONNECTIONSADMINPASSWD}\r\" }
   interact"
 fi
}

remoteExecute() {
 REnode=$1
 REserver=$2
 ## load temporary CCM properties file
 if [ -f "${basepath}/ccm.properties" ]; then
  echo -e "\n\nLoading CCM properties"
  . ${basepath}/ccm.properties
 else
  echo -e "\n\nError loading CCM properties - aborting installation"
  exit
 fi
 ## Check if IC5 installation completed successfully
 checkIMforIC=$(${IMPath}/eclipse/tools/imcl listInstalledPackages | grep connections)
 if [ -z "${checkIMforIC}" ]; then
  echo -e "\n\nNo Connections installation found in InstallationManager. Aborting CCM installation."
  exit
 fi
 ## Check if directory for installation files exist
 if [ ! -d "${RSFuserCcmInstallersPath}" ]; then
  echo -e "\n\nCannot find directory for CCM installation files (\"${RSFuserCcmInstallersPath}\") - aborting installation."
  exit
 fi
 ## START DMGR
 echo -e "\n\nStarting DMGR"
 ${dmgrPath}/bin/startManager.sh
 ## STOP all AppServer
 echo -e "\n\nStopping all application servers for CCM installation"
 ${dmgrPath}/bin/wsadmin.sh -lang jython -f ${basepath}/bin/wasStopServer.py -javaoption -Dpython.path=${basepath}/bin/
 ## Initiate CCM installation
 prepareResponseFile
 installCCM
 ## Delete ports within LotusConnections-config.xml
 echo -e "\n\nRemoving Ports in LotusConnections-config.xml"
 cd ${basepath}/bin && /usr/bin/python ./icParseICConfig.py ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml
 ## Change URL from DM to IHS in LotusConnections-config.xml
 echo -e "\n\nChanging URL for CCM in LotusConnections-config.xml"
 cd ${basepath}/bin && /usr/bin/python ./searchAndReplace.py -f ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml -o $(hostname -f) -n ${CONNECTIONSURL}
 chown ${RTUSR}: ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml
 ## Do a full resync to all AppServer
 echo -e "\n\nPerforming a full resync with all application servers"
 cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -f bin/wasFullResync.py -javaoption -Dpython.path=${basepath}/bin/
 ## START Connections
 echo -e "\n\nStarting all application servers for CCM configuration"
 ${dmgrPath}/bin/wsadmin.sh -lang jython -f ${basepath}/bin/wasStartServer.py -javaoption -Dpython.path=${basepath}/bin/
 ## Initiate CCM configuration with user and password from temporary CCM properties file
 createP8domain ${CONNECTIONSADMIN} ${CONNECTIONSADMINPASSWD}
 createObjectStore ${CONNECTIONSADMIN} ${CONNECTIONSADMINPASSWD}
 ## Execute CCM post installation tasks
 cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -f ${basepath}/bin/ccmPostInstall.py -javaoption -Dpython.path=${basepath}/bin/ CCMnode=${REnode} CCMserver=${REserver} DMGRpath=${dmgrPath}
 ## Delete temporary CCM properties file
 rm ${basepath}/ccm.properties
}

addNode() {
 ANnode=$1
 ANserver=$2
 ## Add CCM node
 ${dmgrPath}/bin/wsadmin.sh -lang jython -f ${basepath}/bin/ccmAddNode.py -javaoption -Dpython.path=${basepath}/bin/ CCMnode=${ANnode} CCMserver=${ANserver} CCMcluster=${ccmClusterName} DMGRpath=${dmgrPath}
}


case ${1} in
 "initialExecute")
  initialExecute
  ;;
 "remoteExecute")
  remoteExecute ${2} ${3}
  ;;
 "addNode")
  addNode ${2} ${3}
  ;;
 *)
  echo "usage: $(basename $0) {initialExecute|remoteExecute|addNode}"
  ;;
esac


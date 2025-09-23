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

basepath=$(pwd -L)
if [ ! -d "${basepath}/logs" ]; then
 mkdir ${basepath}/logs
 chown -R virtuser:admingroup ${basepath}
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

initiate() {
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
ccmPath=${INSTLOC}/addons/ccm
ccmServer1=${ccmClusterName}_server1
ccmServer2=${ccmClusterName}_server2

CONNECTIONSADMIN=virtuser
CONNECTIONSADMINPASSWD=${Core_VIRTUSER_PW}
}

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
RSFuserCcmFirstNodeServerName=${ccmServer1}
RSFuserCcmSecondaryNodesServerNames=${ccmServer2}
RSFuserCcmDbType=db2
RSFuserCcmJdbcLibraryPath=${JDBCPATH}
RSFuserCcmGcdDbHostName=${DB2SRV}
RSFuserCcmGcdDbPort=${DBPORTALL}
RSFuserCcmGcdDbUser=${DBUSRALL}
RSFuserCcmGcdDbUserPassword=${DB_PASSWORD}
RSFuserCcmObjstoreDbHostName=${DB2SRV}
RSFuserCcmObjstoreDbPort=${DBPORTALL}
RSFuserCcmObjstoreDbUser=${DBUSRALL}
RSFuserCcmObjstoreDbUserPassword=${DB_PASSWORD}
RSFcomIbmLotusConnectionsVersion=5.0.0.0_20141013_1847
dmgrPath=${DMGRP}
DMGRURL=${DMGRURL}
CONNECTIONSURL=${CONNECTIONSURL}
CELLN=${CELLN}
RTUSR=${RTUSR}
ICSHAREDDIR=${ICSHAREDDIR}
CONNECTIONSADMIN=${CONNECTIONSADMIN}
CONNECTIONSADMINPASSWD=${CONNECTIONSADMINPASSWD}
ccmPath=${ccmPath}
CCMNODE1_BASE=${CCMNODE1_BASE}
CCMNODE2_BASE=${CCMNODE2_BASE}
IMPath=${IMPath}
NFS_IP=${NFS_IP}
MOUNT_POINT=${MOUNT_POINT}
REMOTE_EXPORT=${REMOTE_EXPORT}
PROJECTNAME=${PROJECTNAME}
ConnectionInstallLocation=${ConnectionInstallLocation}
RSFuserCcmContentStoreSharedPath=${ICSHAREDDIR}
HTTPHOSTCORE=${HTTPHOSTCORE}
DmgrPort=${DmgrPort}
EOF
}

verifyBinaries()
{
echo "Verify CCM binary folder"
## Verify Filenet binary folder
if [ -d ${MOUNT_POINT}/software/Filenet ]
then
	echo "Filenet binary directory exists"
else
	echo "Filenet binary directory does not exist"
	exit 1
fi

if [ "$(ls -A ${MOUNT_POINT}/software/Filenet)" ]; then
	echo "Filenet binary directory is not empty"
else
	echo "Filenet binary directory is Empty"
	exit 1
fi

}

setDMtoRunAsWasadminUser() {
# #set DM general property to run DM as virtuser
echo "set DM general property to run CCM server as ${CONNECTIONSADMIN}"
su - ${RTUSR} -c "${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${basepath}/bin/updateProcessExecution.py ${CELLN} ${RSFuserCcmFirstNodeName} ${RSFuserCcmFirstNodeServerName} ${CONNECTIONSADMIN} admingroup"
su - ${RTUSR} -c "${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${basepath}/bin/updateProcessExecution.py ${CELLN} ${RSFuserCcmSecondaryNodesNames} ${RSFuserCcmSecondaryNodesServerNames} ${CONNECTIONSADMIN} admingroup"

}

execPYscriptACCE() {
## execute python scripts for wsadmin tasks
echo "su - ${RTUSR} -c \"cd ${basepath} && ${PROFILE_ROOT}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath}/bin -wsadmin_classpath ${basepath}/Jace.jar:${basepath}/log4j-1.2.14.jar $*\""
su - ${RTUSR} -c "cd ${basepath} && ${PROFILE_ROOT}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath}/bin -wsadmin_classpath ${basepath}/Jace.jar:${basepath}/log4j-1.2.14.jar $*"

}

ccmAdjustACCE()
{

PYSCRIPT=${basepath}/bin/ccmACCEAdjust.py
OPTS="CEURI=${uri} WASUsername=${CONNECTIONSADMIN} WASPassword=${CONNECTIONSADMINPASSWD}"
execPYscriptACCE ${OPTS}

}

checkProfileServerAvailibity() {

count=1
while [ "$count" -le 10 ];
do
  echo "loop num : $count"
  count=$(($count+1))
  curl -f -k http://${HTTPHOSTCORE}/profiles/serviceconfigs > /tmp/profileServiceConfigs.xml
  if [ "$?" -eq 0 ]; then
    break    
  else
    echo -e "\n\nProfile server is not up, wait for 60 seconds"
    sleep 60
  fi 
done

count=1
while [ "$count" -le 10 ];
do
  echo "loop num : $count"
  count=$(($count+1))
  curl -f -k http://${HTTPHOSTCORE}/communities/serviceconfigs > /tmp/communitiesServiceConfigs.xml
  if [ "$?" -eq 0 ]; then
    break    
  else
    echo -e "\n\nCommunity server is not up, wait for 60 seconds"
    sleep 60
  fi 
done
}

initialExecute() {
 initiate
 verifyBinaries
 setCcmVariables
 
 echo -e "\n\nCopy scripts to DMGR"
 expect -c "\
  spawn bash -c \"rsync -am -f '+ *.sh' -f '+ *.py' -f '+ *.template' -f '+ *.xml' -f '+ *.properties' -f '-! */' ${basepath}/* root@${DMGRURL}:${basepath}\";\
  expect \"continue connecting\" { send \"yes\r\" };\
  expect -timeout -1 \"password:\";\
  send \"${CORE_RTUSER_PW}\r\";\
  expect -timeout -1 eof"
  
  checkPort ${DB2SRV} ${DBPORTALL}
  if [ "$?" -eq 0 ]; then
   echo -e "\n\nError reaching Database - aborting installaton"
   exit
  fi
  
 echo -e "\n\nInstalling CCM via remote installation on DMGR" 
   expect -c "\
   spawn ssh root@${DMGRURL} \"cd ${basepath}; ${basepath}/$(basename $0) remoteExecute \";\
   expect \"continue connecting\" { send \"yes\r\" };\
   expect -timeout -1 \"password:\";\
   send \"${CORE_RTUSER_PW}\r\";\
   expect -timeout -1 eof"
   
 NFSSHARE=${MOUNT_POINT}
 CONFDIR="${NFSSHARE}/pureshare/config/${PROJECTNAME}"
 CEURIFile=uri.txt
 if [ -f ${CONFDIR}/${CEURIFile} ]; then
  echo -e "\n\nLoading - \""${CEURIFile}"\""
  . ${CONFDIR}/${CEURIFile}
  cp ${CONFDIR}/Jace.jar ${basepath}
  cp ${CONFDIR}/log4j-1.2.14.jar ${basepath}
  ccmAdjustACCE
 else
  echo "Error loading properties file("${CONFDIR}"/"${CEURIFile}")."
 fi
 ## Delete temporary CCM properties fileCEURIFile
 rm ${basepath}/ccm.properties
 sed -i "s/${CONNECTIONSADMINPASSWD}/PASSWORD_REMOVED/g" ${LOGOUT}
}

remoteExecute() {
 ## load temporary CCM properties file
 if [ -f "${basepath}/ccm.properties" ]; then
  echo -e "\n\nLoading CCM properties"
  . ${basepath}/ccm.properties
 else
  echo -e "\n\nError loading CCM properties - aborting installation"
  exit
 fi
 
 #Remount nfs server
 mount ${NFS_IP}:${REMOTE_EXPORT} ${MOUNT_POINT}
 
 #Verify the Was username and password
 su - ${RTUSR} -c "${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -c \"AdminTask.help('-commands')\" -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} > /dev/null"
 if [ "$?" -ne 0 ]; then
  echo -e "\n\nWAS username and password are not correct. Please provide the correct credentials  - aborting installation."
  exit
 fi
 ## Check if IC5 installation completed successfully
 # checkIMforIC=$(${IMPath}/imcl listInstalledPackages | grep connections)
 # if [ -z "${checkIMforIC}" ]; then
  # echo -e "\n\nNo Connections installation found in InstallationManager. Aborting CCM installation."
  # exit
 # fi
 ## Check if directory for installation files exist
 if [ ! -d "${RSFuserCcmInstallersPath}" ]; then
  echo -e "\n\nCannot find directory for CCM installation files (\"${RSFuserCcmInstallersPath}\") - aborting installation."
  exit
 fi
 ## START DMGR
 echo -e "\n\nStarting DMGR"
 su - ${RTUSR} -c "${dmgrPath}/bin/startManager.sh"
 ## STOP all AppServer
 echo -e "\n\nStopping all application servers for CCM installation"
 echo -e "${basepath}"
 su - ${RTUSR} -c "cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${basepath}/bin/wasStopServer.py -javaoption -Dpython.path=${basepath}/bin/"
 
 #Obtain CCM Node1
 DMCELLTRUEPATH=${dmgrPath}/config/cells/${CELLN}
 echo DMCELLTRUEPATH=${DMCELLTRUEPATH}
 find ${DMCELLTRUEPATH}/nodes -name ${CCMNODE1_BASE}* > ccmNode1List.txt
 CUSTNODETRUEPATH=`cat ccmNode1List.txt|awk '{printf $0}'`
 CCMNODE1NAME=${CUSTNODETRUEPATH##*nodes/}
 echo CCMNODE1NAME=${CCMNODE1NAME}
 RSFuserCcmFirstNodeName=${CCMNODE1NAME}
 
 #Obtain CCM Node2
 find ${DMCELLTRUEPATH}/nodes -name ${CCMNODE2_BASE}* > ccmNode2List.txt
 CUSTNODETRUEPATH=`cat ccmNode2List.txt|awk '{printf $0}'`
 CCMNODE2NAME=${CUSTNODETRUEPATH##*nodes/}
 echo CCMNODE2NAME=${CCMNODE1NAME}
 RSFuserCcmSecondaryNodesNames=${CCMNODE2NAME}


 ## Initiate CCM installation
 prepareResponseFile

 # mkdir -p ${ICSHAREDDIR}/ccm
 # chown -R virtuser.admingroup ${ICSHAREDDIR}/ccm
 # chmod -R 775 ${ICSHAREDDIR}/ccm
 #Create CCM Server
 echo -e "\n\nCreate CCM Server"
 echo ${RSFuserCcmFirstNodeName}
 echo ${RSFuserCcmFirstNodeServerName}
 su - ${RTUSR} -c "${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -c \"AdminServerManagement.createApplicationServer('${RSFuserCcmFirstNodeName}', '${RSFuserCcmFirstNodeServerName}', 'default')\""


#Get the port properties file for CCM server1
mkdir -p /tmp/scripts/jacl
chmod -R 777 /tmp/scripts/jacl
 
echo -e "\n\n write to wkplc_GetAppServerPorts_new.jacl 1"
su - ${RTUSR} -c "${dmgrPath}/ConfigEngine/ConfigEngine.sh action-get-first-server-port-info-news -DWasUserid=${CONNECTIONSADMIN} -DWasPassword=${CONNECTIONSADMINPASSWD} -Dnews.FirstNodeName=${RSFuserCcmFirstNodeName} -Dnews.${RSFuserCcmFirstNodeName}.ServerName=${RSFuserCcmFirstNodeServerName} -Dnews.work.dir=/tmp -DnewsPostConfig.jacl=/tmp/scripts/jacl/wkplc_GetAppServerPorts_new.jacl"
#chown ${RTUSR}: /tmp/scripts/jacl/wkplc_GetAppServerPorts_new.jacl
echo -e "create CCMCluster_server1.properties"
su - ${RTUSR} -c "${dmgrPath}/bin/wsadmin.sh -lang jacl -f /tmp/scripts/jacl/wkplc_GetAppServerPorts_new.jacl -user ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD}"


#Install CCM with response file
 installCCM
 
 ## Delete ports within LotusConnections-config.xml
 echo -e "\n\nRemoving Ports in LotusConnections-config.xml"
 su - ${RTUSR} -c "cd ${basepath}/bin && /usr/bin/python ./icParseICConfig.py ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml"
 ## Change URL from DM to IHS in LotusConnections-config.xml
 echo -e "\n\nChanging URL for CCM in LotusConnections-config.xml"
 #cd ${basepath}/bin && /usr/bin/python ./searchAndReplace.py -f ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml -o $(hostname -f) -n ${CONNECTIONSURL}
 su - ${RTUSR} -c "cd ${basepath}/bin && /usr/bin/python ./searchAndReplace.py -f ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml -o ${DMGRURL} -n ${CONNECTIONSURL}"
 
 #chown ${RTUSR}: ${dmgrPath}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml
 
 
 ## Do a full resync to all AppServer
 echo -e "\n\nPerforming a full resync with all application servers"
 su - ${RTUSR} -c "cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f bin/wasFullResync.py -javaoption -Dpython.path=${basepath}/bin/"
 
 setDMtoRunAsWasadminUser
 
 sleep 120
 echo -e "\n\nPerforming a restart of all application nodeagent"
 su - ${RTUSR} -c "cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f bin/wasRestartNodeagent.py -javaoption -Dpython.path=${basepath}/bin/"
 
 sleep 120
 ## START Connections
 echo -e "\n\nStarting all application servers for CCM configuration"
 su - ${RTUSR} -c "cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${basepath}/bin/wasStartServer.py -javaoption -Dpython.path=${basepath}/bin/"
 ## Initiate CCM configuration with user and password from temporary CCM properties file


 checkProfileServerAvailibity

 createP8domain ${CONNECTIONSADMIN} ${CONNECTIONSADMINPASSWD}
 createObjectStore ${CONNECTIONSADMIN} ${CONNECTIONSADMINPASSWD}
 ## Execute CCM post installation tasks
 su - ${RTUSR} -c "cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${basepath}/bin/ccmPostInstall.py -javaoption -Dpython.path=${basepath}/bin/ CCMnode=${RSFuserCcmFirstNodeName} CCMserver=${RSFuserCcmFirstNodeServerName} DMGRpath=${dmgrPath}"
 su - ${RTUSR} -c "cd ${basepath} && ${dmgrPath}/bin/wsadmin.sh -lang jython -port ${DmgrPort} -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD} -f ${basepath}/bin/ccmPostInstall.py -javaoption -Dpython.path=${basepath}/bin/ CCMnode=${RSFuserCcmSecondaryNodesNames} CCMserver=${RSFuserCcmSecondaryNodesServerNames} DMGRpath=${dmgrPath}"

 CEURIFile=${ConnectionInstallLocation}/addons/ccm/ccmDomainTool/uri.txt
 if [ -f ${CEURIFile} ]; then
   
   NFSSHARE=${MOUNT_POINT}
   CONFDIR="${NFSSHARE}/pureshare/config/${PROJECTNAME}"
   echo -e "\n\ncopy CE URI.txt from ccmDomainTool to ${CONFDIR}"

   cp ${CEURIFile} ${CONFDIR}
   cp ${ConnectionInstallLocation}/addons/ccm/CEClient/lib/Jace.jar ${CONFDIR}
   cp ${ConnectionInstallLocation}/addons/ccm/CEClient/lib/log4j-1.2.14.jar ${CONFDIR}
 else
   echo "Error copying ${CEURIFile} file to ${CONFDIR}."
 fi

 ## Delete temporary CCM properties file
 rm ${basepath}/ccm.properties
 umount -f ${MOUNT_POINT}
 
 sed -i "s/${CONNECTIONSADMINPASSWD}/PASSWORD_REMOVED/g" ${LOGOUT}

}

# addNode() {
 # ANnode=$1
 # ANserver=$2
 # ## Add CCM node
 # ${dmgrPath}/bin/wsadmin.sh -lang jython -f ${basepath}/bin/ccmAddNode.py -javaoption -Dpython.path=${basepath}/bin/ CCMnode=${ANnode} CCMserver=${ANserver} CCMcluster=${ccmClusterName} DMGRpath=${dmgrPath}
# }

case ${1} in
 "initialExecute")
  initialExecute
  ;;
 "remoteExecute")
  remoteExecute ${2} ${3}
  ;;
 *)
  echo "usage: $(basename $0) {initialExecute|remoteExecute}"
  ;;
esac


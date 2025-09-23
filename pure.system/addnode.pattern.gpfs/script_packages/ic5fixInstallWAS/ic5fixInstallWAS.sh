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
IMPath=/opt/IBM/InstallationManager
wasFixPath=${basepath}/fixes
wasPath=$(su - ${RTUSR} -c "${IMPath}/eclipse/tools/imcl listInstallationDirectories | grep AppServer")
## var for needed base version of WAS. REGEX format. Here: 8.5.5.1
neededVersion="^8\.5\.5001.*"

getWasServer() {
 echo $(cd ${WAS_PROFILE_ROOT}/servers/; ls -d *)
}

installWasFix() {
 IWFpath=$1
 IWFfix=$2
 echo -e "\nInstalling "${IWFpath}" -> "${IWFfix}
 su - ${RTUSR} -c "${IMPath}/eclipse/tools/imcl install ${IWFfix} -repositories ${IWFpath} -installationDirectory ${wasPath}"
}

stopServer() {
 serverList=$(getWasServer)
 for server in ${serverList}
 do
  echo -e "\n\nStopping ${server}"
  if [ "${server}" == "dmgr" ]; then
   StopCmd="stopManager.sh"
  elif [ "${server}" == "nodeagent" ]; then
   StopCmd="stopNode.sh"
  else
   StopCmd="stopServer.sh ${server}"
  fi
  su - ${RTUSR} -c "${WAS_PROFILE_ROOT}/bin/${StopCmd}"
 done
}

startServer() {
 serverList=$(getWasServer)
 for server in ${serverList}
 do
  echo -e "\n\nStarting ${server}"
  if [ "${server}" == "dmgr" ]; then
   StartCmd="startManager.sh"
  elif [ "${server}" == "nodeagent" ]; then
   StartCmd="startNode.sh"
  else
   StartCmd="startServer.sh ${server}"
  fi
  su - ${RTUSR} -c "${WAS_PROFILE_ROOT}/bin/${StartCmd}"
 done
}

checkWasForFix() {
 ## query InstallationManager for installed version of WAS ND
 installedWas=$(su - ${RTUSR} -c "${IMPath}/eclipse/tools/imcl listInstalledPackages | grep \"com.ibm.websphere.ND.\"")
 wasVersion=$(echo ${installedWas} | cut -d_ -f2)
 if [[ ${wasVersion} =~ ${neededVersion} ]]; then
  ## stop all WAS tasks
  stopServer
  ## check each zip file in directory and install if not allready installed
  for file in $(ls ${wasFixPath} | grep ".*\.zip$");
  do
   availableFix=$(su - ${RTUSR} -c "${IMPath}/eclipse/tools/imcl listAvailableFixes ${installedWas} -repositories ${wasFixPath}/${file}")
   if [ ! -z "${availableFix}" ]; then
    checkIfPackageIsInstalled=$(su - ${RTUSR} -c "${IMPath}/eclipse/tools/imcl listInstalledPackages | grep ${availableFix}")
    if [ -z "${checkIfPackageIsInstalled}" ]; then
     installWasFix ${wasFixPath}/${file} ${availableFix}
    else
     echo -e "\nFile "${file}" is already installed - skipping"
    fi
   fi
  done
  ## start all WAS tasks
  startServer
 else
  echo "WAS not running version 8.5.5.1 - nothing to do"
 fi
}



if [ -d "${wasFixPath}" ]; then
 checkFixFilesExist=$(ls ${wasFixPath} | grep ".*\.zip$")
 if [ -z "${checkFixFilesExist}" ]; then
  echo -e "\n\nNo fix found in repository (\""${wasFixPath}"\") - skipping"
 else
  checkWasForFix
 fi
else
 echo -e "\n\nPath to fix repository does not exist (\""${wasFixPath}"\") - aborting installation."
 exit
fi


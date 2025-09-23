#!/bin/bash

encryptPassword() {
 ## encrypt password for silent installation
 ### encryptPassword PASSWORD
 if [ -z "${1}" ]; then
  echo "no string passed for ${FUNCNAME}"
 else
  echo $(${IMPath}/eclipse/tools/imutilsc encryptString ${1} -silent -noSplash)
 fi
}

prepareResponseFile() {
 echo -e "\n\nPreparing response files for silent installation"
 ## variable for all available variables in response file template. Searched by regex \[a-zA-Z*\]
 RSFlist=$(sed -nr "s/.*\[([a-zA-Z]*)\].*/\1/p" ${basepath}/template/ccm_responseFile.template)
 RSFlistUninstall=$(sed -nr "s/.*\[([a-zA-Z]*)\].*/\1/p" ${basepath}/template/ccm_responseFile_uninstall.template)
 ## prepare XML file with encrypted passwords, paths and usernames from user input
 cp ${basepath}/template/ccm_responseFile.template ${basepath}/ccm_responseFile.xml
 cp ${basepath}/template/ccm_responseFile_uninstall.template ${basepath}/ccm_responseFile_uninstall.xml
 ### get current Connection version
 RSFcomIbmLotusConnectionsVersion=$(${IMPath}/eclipse/tools/imcl listInstalledPackages | grep -Po "(?<=connections_).*\w+") 
 ### encrypt passwords
 RSFuserWasAdminuserPassword=$(encryptPassword ${RSFuserWasAdminuserPassword})
 RSFuserCcmAdminuserPassword=$(encryptPassword ${RSFuserCcmAdminuserPassword})
 RSFuserCcmGcdDbUserPassword=$(encryptPassword ${RSFuserCcmGcdDbUserPassword})
 RSFuserCcmObjstoreDbUserPassword=$(encryptPassword ${RSFuserCcmObjstoreDbUserPassword})
 ### set ServerInfo string
 RSFuserCcmServerInfo="ccm.${RSFuserCcmFirstNodeName}.ServerName=${RSFuserCcmFirstNodeServerName};"
 if [ ! -z "${RSFuserCcmSecondaryNodesNames}" ]; then
  IFSOLD=${IFS}
  IFS=","
  l=1
  for i in ${RSFuserCcmSecondaryNodesNames};
  do
   IFS=$IFSOLD
   cluster=$(echo ${RSFuserCcmSecondaryNodesServerNames} | cut -d"," -f${l})
   RSFuserCcmServerInfo+="ccm.${i}.ServerName=${cluster};"
   (( l++ ))
  done
 fi
 ### replace variables in response files
 for i in ${RSFlist}
 do
  sed -i "s#\[${i}\]#${!i}#g" ${basepath}/ccm_responseFile.xml
 done
 for i in ${RSFlistUninstall}
 do
  sed -i "s#\[${i}\]#${!i}#g" ${basepath}/ccm_responseFile_uninstall.xml
 done
}

installCCM() {
 ## install CCM via response file
 echo -e "\n\nInstalling CCM via response file"
 echo -e "  view /opt/IBM/Connections/logs/ccmInstall.log for installation details"
 if [ ! -d /ibm/IMTMP ]; then
  mkdir /ibm/IMTMP
 fi
 export IATEMPDIR=/ibm/IMTMP
 ${IMPath}/eclipse/tools/imcl -input ${basepath}/ccm_responseFile.xml -log ${basepath}/logs/ccm_silentInstallLog.xml -acceptLicense
 ICCMceClientVersion=$(cat ${ccmPath}/CEClient/ce_version.txt | grep "P8 Content Platform Engine")
 ICCMceVersion=$(cat ${ccmPath}/ContentEngine/ce_version.txt | grep "P8 Content Platform Engine")
 if [ -z ${ICCMceVersion} -o -z ${ICCMceClientVersion} ]; then
  cp /opt/IBM/Connections/logs/ccmInstall.log ${basepath}/logs/
  echo -e "\n\nError during installation. At least one installation failed. Check \"${basepath}/logs/ccmInstall.log\" for details."
  echo -e "Cleaning up InstallationManager - removing CCM"
  ${IMPath}/eclipse/tools/imcl -input ${basepath}/ccm_responseFile_uninstall.xml -log ${basepath}/logs/ccm_silentUninstallLog.xml -acceptLicense
  exit
 else
  echo -e "\n\nInstalled Versions as of ce_version.txt:"
  echo "CEClient: "${ICCMceClientVersion}
  echo "ContentEngine: "${ICCMceVersion}
 fi
}



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

prepareResponseFile() {
 echo -e "\n\nPreparing response files for silent installation"
 ## variable for all available variables in response file template. Searched by regex \[a-zA-Z*\]
 RSFlist=$(sed -nr "s/.*\[([a-zA-Z]*)\].*/\1/p" ${basepath}/template/ccm_responseFile.template)
 RSFlistUninstall=$(sed -nr "s/.*\[([a-zA-Z]*)\].*/\1/p" ${basepath}/template/ccm_responseFile_uninstall.template)
 ## prepare XML file with encrypted passwords, paths and usernames from user input
 cp ${basepath}/template/ccm_responseFile.template ${basepath}/ccm_responseFile.xml
 cp ${basepath}/template/ccm_responseFile_uninstall.template ${basepath}/ccm_responseFile_uninstall.xml
 ### get current Connection version
 #RSFcomIbmLotusConnectionsVersion=$(${IMPath}/imcl listInstalledPackages | grep -Po "(?<=connections_).*\w+") 
 ### encrypt passwords
 RSFuserWasAdminuserPassword=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${RSFuserWasAdminuserPassword}" -silent -noSplash"`
 RSFuserCcmAdminuserPassword=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${RSFuserCcmAdminuserPassword}" -silent -noSplash"`
 RSFuserCcmGcdDbUserPassword=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${RSFuserCcmGcdDbUserPassword}" -silent -noSplash"`
 RSFuserCcmObjstoreDbUserPassword=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${RSFuserCcmObjstoreDbUserPassword}" -silent -noSplash"`
 ### set ServerInfo string
 RSFuserCcmServerInfo="ccm.${RSFuserCcmFirstNodeName}.ServerName=${RSFuserCcmFirstNodeServerName};ccm.${RSFuserCcmSecondaryNodesNames}.ServerName=${RSFuserCcmSecondaryNodesServerNames};"
 
 # if [ ! -z "${RSFuserCcmSecondaryNodesNames}" ]; then
  # IFSOLD=${IFS}
  # IFS=","
  # l=1
  # for i in ${RSFuserCcmSecondaryNodesNames};
  # do
   # IFS=$IFSOLD
   # cluster=$(echo ${RSFuserCcmSecondaryNodesServerNames} | cut -d"," -f${l})
   # RSFuserCcmServerInfo+="ccm.${i}.ServerName=${cluster};"
   # (( l++ ))
  # done
 # fi
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
 # if [ ! -d /ibm/IMTMP ]; then
  # mkdir /ibm/IMTMP
 # fi
 # export IATEMPDIR=/ibm/IMTMP
 su - ${RTUSR} -c "${IMPath}/imcl -input ${basepath}/ccm_responseFile.xml -log ${basepath}/logs/ccm_silentInstallLog.xml -acceptLicense"
 ICCMceClientVersion=$(cat ${ccmPath}/CEClient/ce_version.txt | grep "P8 Content Platform Engine")
 ICCMceVersion=$(cat ${ccmPath}/ContentEngine/ce_version.txt | grep "P8 Content Platform Engine")
 # if [ -z ${ICCMceVersion} -o -z ${ICCMceClientVersion} ]; then
  # cp /opt/IBM/Connections/logs/ccmInstall.log ${basepath}/logs/
  # echo -e "\n\nError during installation. At least one installation failed. Check \"${basepath}/logs/ccmInstall.log\" for details."
  # # echo -e "Cleaning up InstallationManager - removing CCM"
  # # ${IMPath}/imcl -input ${basepath}/ccm_responseFile_uninstall.xml -log ${basepath}/logs/ccm_silentUninstallLog.xml -acceptLicense
  # exit
 # else
  echo -e "\n\nInstalled Versions as of ce_version.txt:"
  echo "CEClient: "${ICCMceClientVersion}
  echo "ContentEngine: "${ICCMceVersion}
 #fi
}



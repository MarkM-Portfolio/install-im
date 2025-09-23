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

# import the global variables property file
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

# define local vars with global ones...
DIRP=${basepath}
DMGRHOST=${DMGR_HOST}
WASTMP=${basepath}/myCMD.py

# if ${DMGR_HOST} is empty, we are definitly on a dmgr...
if [ "${DMGR_HOST}" == "" ]; then
DMGRHOST=${HOSTNAME}
fi

# some functions are used from official IBM helper lib...
execIBMwsadminLIB()
{
if [ -f ${WASTMP} ];then 
rm ${WASTMP}
fi
(
cat <<EOF
execfile('${DIRP}/wsadminlib.py')
enableDebugMessages()

$EXECTMP 

AdminConfig.save()
EOF
) > ${WASTMP}
dos2unix ${WASTMP}
chown ${RTUSR} ${WASTMP}
echo "su - ${RTUSR} -c \"cd ${WASPATH}/bin/ && ${WASPATH}/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${WASTMP} -javaoption -Dpython.path=${basepath} $*\""
su - ${RTUSR} -c "cd ${WASPATH}/bin/ && ${WASPATH}/bin/wsadmin.sh ${WSOPT} -f ${WASTMP} -javaoption -Dpython.path=${basepath} $*"
#rm ${WASTMP}
}

execPYscript()
{
## execute python scripts for wsadmin tasks
echo "su - ${RTUSR} -c \"cd ${basepath} && ${DMGRP}/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*\""
su - ${RTUSR} -c "cd ${DIRP} && ${WASPATH}/bin/wsadmin.sh ${WSOPT} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*"
}

markup()
{
echo ""
echo "$1 function $2"
echo "---------------------------------------------------------------------"
echo ""
echo ""
}

setMailConfiguration()
{
markup starting "setting mail configuration"
 echo -e "\n\nRunning mail configuration"
 ## configure mail settings in two steps: 1. via XML file manipulation, 2. via wsadmin task
 ### Command for wsadmin:
 EXECTMP="getMailSessionId=AdminConfig.list('MailSession')"$'\n'"AdminConfig.modify(getMailSessionId, '[[mailTransportHost ${MAILSRV}]]')"
 execIBMwsadminLIB
 ### Command for notification-config.xml:
 xmlFile=${WASPATH}/config/cells/${CELLN}/LotusConnections-config/notification-config.xml
 if [ -f "${xmlFile}" ]; then
  captureSpaces=$(sed -nr "/<properties>/{n;s/^(\s*)<.*/\1/p}" ${xmlFile})
  if [ "$(grep "alwaysUseGlobalSender" ${xmlFile})" == "" ]; then
   echo "Property \"alwaysUseGlobalSender\" not found - adding it to XML file"
   sed -i "/<properties>/a\\${captureSpaces}<property name=\"alwaysUseGlobalSender\">true<\/property>" ${xmlFile}
  else
   echo "Property \"alwaysUseGlobalSender\" found - updating"
   sed -ri "s/(.*name=\"alwaysUseGlobalSender\">).*(<.*)/\1true\2/g" ${xmlFile}
  fi
  if [ "$(grep "globalSenderName" ${xmlFile})" == "" ]; then
   echo "Property \"globalSenderName\" not found - adding it to XML file"
   sed -i "/<properties>/a\\${captureSpaces}<property name=\"globalSenderName\">IBM Connections Administrator<\/property>" ${xmlFile}
  else
   echo "Property \"globalSenderName\" found - updating"
   sed -ri "s/(.*name=\"globalSenderName\">).*(<.*)/\1IBM Connections Administrator\2/g" ${xmlFile}
  fi
  if [ "$(grep "globalSenderEmailAddress" ${xmlFile})" == "" ]; then
   echo "Property \"globalSenderEmailAddress\" not found - adding it to XML file"
   sed -i "/<properties>/a\\${captureSpaces}<property name=\"globalSenderEmailAddress\">${MailAddress}<\/property>" ${xmlFile}
  else
   echo "Property \"globalSenderEmailAddress\" found - updating"
   sed -ri "s/(.*name=\"globalSenderEmailAddress\">).*(<.*)/\1${MailAddress}\2/g" ${xmlFile}
  fi
 else
  echo -e "\n\nXML file not found: "${tmpXmlFile}
  exit
 fi
markup stopping "setting mail configuration"
}

activateFileDownload()
{
markup starting "activating file download configuration"
 echo -e "\n\nActivating file download configuration"
 xmlPath=${WASPATH}/config/cells/${CELLN}/LotusConnections-config
 ## step 1: modify "files-config.xml", "wikis-config.xml", "mobile_config.xml", "oa-config.xml" on DMGR
 modifyFiles="files-config.xml:/files_content wikis-config.xml:/wikis_content mobile-config.xml:/mobile_content oa-config.xml:/activities_content"
 for fileSet in ${modifyFiles}
 do
  xmlFile=$(echo ${fileSet} | cut -d: -f1)
  hrefPrefix=$(echo ${fileSet} | cut -d: -f2)
  tmpXmlFile=${xmlPath}/${xmlFile}
  if [ -f "${tmpXmlFile}" ]; then
   dos2unix ${tmpXmlFile}
   grepVar=$(grep "modIBMLocalRedirect" ${tmpXmlFile})
   if [ "${grepVar}" == "" ]; then
    echo "Property \"modIBMLocalRedirect\" not found in ${tmpXmlFile} - adding it"
    ## only for oa-config.xml: insert above "</store>"
    case ${xmlFile} in
     "oa-config.xml")
      captureSpaces=$(sed -nr "/<store/{n;s/^(\s*)<.*/\1/p}" ${tmpXmlFile}) 
      sed -i "/<\/store>/i\\${captureSpaces}<download><modIBMLocalRedirect enabled=\"true\" hrefPathPrefix=\"${hrefPrefix}\" /></download>" ${tmpXmlFile}
      ;;
     *)
      echo -e "\n\nWARNING - unsupported file to insert XML tag (\"${tmpXmlFile}\") - skipping"
      ;;
    esac
   else
    echo "Property \"modIBMLocalRedirect\" found in ${tmpXmlFile} - updating"
    ## delete line breaks within <modIBMLocalRedirect ....> tag - so return would be a oneliner
    sed -ri '/modIBMLocalRedirect/{:a;/>\s*$/!{N;s/\n//g;ba}}' ${tmpXmlFile}
    ## replace "modIBMLocalRedirect" tag with template settings
    sed -ri "s/(.*)<modIBMLocalRedirect[^<]*>(.*)$/\1<modIBMLocalRedirect enabled=\"true\" hrefPathPrefix=\"\\${hrefPrefix}\" \/>\2/" ${tmpXmlFile}
   fi
  else
   echo -e "\n\nWARNING - XML file not found (\"${tmpXmlFile}\") - skipping"
  fi
 done 
 ## step 2: full resync
 echo -e "\n\nPerforming a full resync with all application servers"
 su - ${RTUSR} -c "cd ${basepath}; ${WASPATH}/bin/wsadmin.sh -lang jython -f ${basepath}/wasFullResync.py -javaoption -Dpython.path=${basepath}/"
markup stopping "activating file download configuration"
}

statusCluster()
{
markup starting "${1}ing all Clusters"
EXECTMP="${1}AllServerClusters()"
execIBMwsadminLIB  
markup stopped "${1}ing all Clusters"
}

statusDMGR()
{
su - ${RTUSR} -c "${WASPATH}/bin/${1}Manager.sh"
}

wasMap2Host()
{
markup starting "Mapping WebServer to applications"
#(un)Mapping of WebServerNode to all application(s)		
PYSCRIPT=wasMap2Host.py
OPTS=""
execPYscript ${OPTS}
markup stopping "Mapping WebServer to applications"
}

wasVAR()
{
markup starting "Setting WAS Variable"
EXECTMP="setWebSphereVariable ( 'FILE_CONTENT_CONVERSION', '${INSTLOC}/data/local/search/stellent/dcs/oiexport/exporter' )"
execIBMwsadminLIB
markup stopped "Setting WAS Variable"
}

wasPlugin()
{
markup starting "Generate Plugin"
PYSCRIPT=wasPlugin.py
OPTS="dmgrProfile=${DMGRP}"
execPYscript ${OPTS}
markup stopping "Generate Plugin"
}

replaceWrongServerNames()
{
markup starting "replaceWrongServerNames"
python ${basepath}/searchAndReplace.py -f ${DMGRP}/config/cells/${DMCELLNAME}/nodes/${IHSNODE}/servers/webserver1/plugin-cfg.xml -o "${INSTNODE1}_" -n ""
python ${basepath}/searchAndReplace.py -f ${DMGRP}/config/cells/${DMCELLNAME}/nodes/${IHSNODE}/servers/webserver1/plugin-cfg.xml -o "${INSTNODE2}_" -n ""

python ${basepath}/searchAndReplace.py -f ${DMGRP}/config/cells/${DMCELLNAME}/nodes/${IHS2NODE}/servers/webserver1/plugin-cfg.xml -o "${INSTNODE1}_" -n ""
python ${basepath}/searchAndReplace.py -f ${DMGRP}/config/cells/${DMCELLNAME}/nodes/${IHS2NODE}/servers/webserver1/plugin-cfg.xml -o "${INSTNODE2}_" -n ""
markup finished "replaceWrongServerNames"
}

wasPluginPropagate()
{
markup starting "Propagate Plugin"
PYSCRIPT=wasPlugin_propagate.py
OPTS="dmgrProfile=${DMGRP}"
execPYscript ${OPTS}
markup stopping "Propagate Plugin"
}

wasRetrieveSigner()
{
markup starting "Retrieving SSL Certs from Web Server"
PYSCRIPT=wasRetrieveSigner.py
OPTS="hostName=${HTTPHOSTCORE} alias=http_ssl"
execPYscript ${OPTS}

OPTS="hostName=${HTTPHOST2CORE} alias=http_ssl"
execPYscript ${OPTS}

markup stopped "Finished Retrieving SSL Certs from Web Server"
}

wasSearchWorkmanagers()
{
markup starting "Setting Workmanager for SEARCH Application"
PYSCRIPT=wasSearchWorkmanagers.py
OPTS="node=${NODE_NAME}"
execPYscript ${OPTS}
markup finished "Setting Workmanager for SEARCH Application"
}

wasRetrieveLdapSigner()
{
markup starting "Retrieving SSL Certs from LDAP Server"
PYSCRIPT=wasRetrieveSigner.py
OPTS="hostName=${LDAPSRV} alias=ldap_ssl port=${LPORT}"
if [ "${LPORT}" != "389" ]; then
execPYscript ${OPTS}
else
echo "No LDAPS configured in property file - skipping task wasRetrieveLdapSigner"
fi
markup stopped "Finished Retrieving SSL Certs from Web Server"
}

wasInstallCerts()
{
markup starting "Installing SSL Certs from ${CONFDIR}/CERTS/"
CERTS=`find ${CONFDIR}/CERTS/ -name *cer -exec basename {} \;`
PYSCRIPT=wasRetrieveSigner.py
if [ "${CERTS}" != "" ]; then
  for CERT in ${CERTS}; do
  OPTS="certPath=${CONFDIR}/CERTS/${CERT}"
  execPYscript ${OPTS}
  done
else
  echo "No CERTS found for installation!"
fi
markup finished "Installing SSL Certs from ${CONFDIR}/CERTS/"
}

icParseICConfig()
{
markup starting "Remove Ports from Connections Config"
PYSCRIPT=icParseICConfig.py
cd ${DIRP} && /usr/bin/python ./$PYSCRIPT ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml
echo "Removing Ports in ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml"
markup stopping "Remove Ports from Connections Config"
}

replaceICURL()
{
markup starting "Replacing IC URLs in LotusConnections-config.xml"
PYSCRIPT=searchAndReplace.py
TMPORGURL=`hostname -f`
CHK1=`grep ${TMPORGURL} ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml |wc -l`
if [ "${CHK1}" != "0" ]; then
cd ${DIRP} && /usr/bin/python ./$PYSCRIPT -f ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml -o ${TMPORGURL} -n ${CONNECTIONSURL}
echo "Replace original URL ${TMPORGURL} with ${CONNECTIONSURL}"
else
echo "Replace original URL ${HOSTNAME} with ${CONNECTIONSURL}"
cd ${DIRP} && /usr/bin/python ./$PYSCRIPT -f ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml -o ${HOSTNAME} -n ${CONNECTIONSURL}
fi
echo "Changing permissons: chown ${RTUSR} ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml"
chown ${RTUSR} ${WASPATH}/config/cells/${CELLN}/LotusConnections-config/LotusConnections-config.xml
markup stopping "FINISHED Replacing IC URLs in LotusConnections-config.xml"
}

wasSyncNodes()
{
markup starting "Start syncronizing Nodes"
PYSCRIPT=wasSyncNodes.py
OPTS=""
execPYscript ${OPTS}
markup stopping "Finished syncronizing Nodes"
}

case $1 in
	custom)
	WASPATH=/opt/IBM/WebSphere/Profiles/DefaultCustom01
	statusCluster stop
	markup starting "AddNode settings"
	wasMap2Host
	wasPlugin
	wasRetrieveSigner
	wasSearchWorkmanagers
	wasSyncNodes
	statusCluster start
	markup stopping "AddNode settings"
	;;
	dmgr)
	WASPATH=${DMGRP}
	## automatically start the post-install-steps in a defined order.
	markup starting auto
	statusCluster stop      # stop Cluster Server
	wasMap2Host		 		# Map Web Server to all Applications
	sleep 120
	wasPlugin				# generate new Plugin
	sleep 120
	replaceWrongServerNames # replace wrong server name in plguin-cfg.xml
	sleep 120
	wasPluginPropagate      # propagate new plguin
	sleep 120
	## uncommented due to move to ic5installic5 package. we have to install certs, before connecting to ldaps
	#wasRetrieveSigner		# Retrieve SSL Cert from WebServer
	#wasInstallCerts		# Install ext. Certs, if there are any...
	icParseICConfig   		# Remove App Ports in Lotus Connections Config
	replaceICURL			# Replace DMGR URL with the HTTP Server URL
	statusDMGR stop			# Stop Dmgr
	statusDMGR start		# Start Dmgr
	## setMailConfiguration 	# set mail configuration in XML file and via wsadmin command
	activateFileDownload 	# activating file download via IHS configuration in XML files
	wasVAR					# Set Search Conversation tool DMGR PATH
	wasSyncNodes			# Synchronizing Nodes with Dmgr
	statusCluster start		# Start Cluster Servers 
	markup stopped auto
	;;
	*)
	echo "usage $0 < custom | dmgr >"
	;;
esac

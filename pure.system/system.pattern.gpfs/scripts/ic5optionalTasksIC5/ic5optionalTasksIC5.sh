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

WASTMP=${basepath}/myCMD.py

# some functions are used from official IBM helper lib...
execIBMwsadminLIB()
{
if [ -f ${WASTMP} ];then 
rm ${WASTMP}
fi
(
cat <<EOF
execfile('${basepath}/wsadminlib.py')
enableDebugMessages()

$EXECTMP 

AdminConfig.save()
EOF
) > ${WASTMP}
dos2unix ${WASTMP}
chown ${RTUSR} ${WASTMP}
echo "su - ${RTUSR} -c \"${DMGRP}/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${WASTMP}\""
su - ${RTUSR} -c "${DMGRP}/bin/wsadmin.sh ${WSOPT} -f ${WASTMP} -javaoption -Dpython.path=${basepath} $*"
#rm ${WASTMP}
}

execPYscript()
{
## execute python scripts for wsadmin tasks
echo "su - ${RTUSR} -c \"cd ${basepath} && ${DMGRP}/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*\""
su - ${RTUSR} -c "cd ${basepath} && ${DMGRP}/bin/wsadmin.sh ${WSOPT} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*"
}

statusCluster()
{
markup starting "${1}ing all Clusters"
EXECTMP="${1}AllServerClusters()"
execIBMwsadminLIB  
markup stopped "${1}ing all Clusters"
}

wasSetApplicationRoles()
{
markup starting "Mapping WebServer to applications"
#(un)Mapping of WebServerNode to all application(s)
PYSCRIPT=wasSetApplicationRoles.py
OPTS="adminUser=\"${CONNADMINUSERS}\""
execPYscript ${OPTS}
markup stopping "Mapping WebServer to applications"
}

wasEnableDictionary()
{
markup starting "Enablement of German dictionary"	
xmlFile=${WAS_PROFILE_ROOT}/config/cells/${DMCELLNAME}/LotusConnections-config/search-config.xml
## set default dictionary to "de"
echo -e "\n\nSet default dictionary to \"de\""
sed -i "s/defaultLocale=.*[^>]/defaultLocale=\"de\"/g" ${xmlFile}
## activate dictionary "de"
echo -e "\n\nActivate dictionary \"de\""
sed -i "/<languageSupport/a<dictionary locale=\"de\" path=\"\${SEARCH_DICTIONARY_DIR}\"\/>" ${xmlFile}
## full resync
echo -e "\n\nPerforming a full resync with all nodes"
PYSCRIPT=wasFullResync.py
execPYscript
markup stopping "Enablement of German dictionary"
}

case $1 in
		*)
		wasSetApplicationRoles
		wasEnableDictionary
		statusCluster stop
		statusCluster start
		;;
esac

exit 0

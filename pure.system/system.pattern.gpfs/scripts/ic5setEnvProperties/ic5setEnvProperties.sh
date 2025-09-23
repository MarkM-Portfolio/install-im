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

#! /bin/bash
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

## load variables from pure pattern declaration
if [ -f /etc/virtualimage.properties ]; then
 . /etc/virtualimage.properties
fi

PROJNAME=$PROJECTNAME	 	# Projectname definiert Ordner

####
NFSSHARE=${MOUNT_POINT}
CONFDIR="${NFSSHARE}/pureshare/config/$PROJNAME"
CONFIGFILE=ic5pattern.properties

##if [ -d ${CONFDIR} ]; then
if [ -f ${CONFDIR}/${CONFIGFILE} ]; then
echo "Using Config File from ${CONFDIR}/${CONFIGFILE}"
else
mkdir -p ${CONFDIR}
mkdir -p ${CONFDIR}/CERTS
cp ${basepath}/${CONFIGFILE} ${CONFDIR}/${CONFIGFILE}
fi
dos2unix ${CONFDIR}/${CONFIGFILE}

service iptables stop
/sbin/chkconfig iptables off

ulimit -n 65536
echo "virtuser hard nofile 65536" >> /etc/security/limits.conf
echo "root soft nproc 2047" >> /etc/security/limits.conf
echo "root hard nproc 16384" >> /etc/security/limits.conf
echo "root soft nofile 1024" >> /etc/security/limits.conf
echo "root hard nofile 65536" >> /etc/security/limits.conf
echo "root soft stack 10240" >> /etc/security/limits.conf
echo "session required pam_limits.so" >> /etc/pam.d/login

# description: Use for transfer VARs from one to another property file
# usage transferAttrValue <search-item> <search-value> <parse-item> <dest-item(maybe NEW)>
# example: transferAttrValue PROFILE_TYPE dmgr HOSTNAME DMGRURL
transferAttrValue()
{
SOURCEFILE=/etc/virtualimage.properties
DESTFILE=${CONFDIR}/${CONFIGFILE}
#
if [ ! -f ${SOURCEFILE} ]; then
echo "${SOURCEFILE} does not exists"
exit 1
fi
if [ ! -f ${DESTFILE} ]; then
echo "${DESTFILE} does not exists"
exit 1
fi

PROFTYPE=`grep $1 ${SOURCEFILE} |cut -d= -f2`
if [ "${PROFTYPE}" == "${2}" ]; then
 VAREX=`grep ^$4 ${DESTFILE}`
 PARA0=`grep $3 ${SOURCEFILE} |cut -d= -f2`
 PARA1=`grep ^$4 ${DESTFILE} |cut -d= -f2`
 if [ "${VAREX}" != "" ]; then
    if [ "${PARA0}" == "${PARA1}" ]; then
     echo "Paramter $4=$PARA1 exists and has the correct value"
    else
     echo "Changing paramter in file ${DESTFILE} to $4=${PARA0}"
     #sed -i "s/$4=${PARA1}/$4=${PARA0}/g" ${DESTFILE}
	 ${basepath}/searchAndReplace.py -f ${DESTFILE} -o $4=${PARA1} -n $4=${PARA0}
    fi
 else
  echo "Adding parameter $4=${PARA0} to file ${DESTFILE}"
  echo "$4=${PARA0}" >> ${DESTFILE}
 fi
else
 echo "The paramter $1 is not available...."
fi
}

editAttrValue()
{
DMCELLTRUEPATH=${PROFILE_ROOT}/config/cells/${CELL_NAME}
echo DMCELLTRUEPATH=${DMCELLTRUEPATH}
find ${DMCELLTRUEPATH}/nodes -name ${INSTNODE1_BASE}_* > custNodeList.txt
CUSTNODETRUEPATH=`cat custNodeList.txt|awk '{printf $0}'`
CUSTNODENAME=${CUSTNODETRUEPATH##*nodes/}
echo CUSTNODENAME=${CUSTNODENAME}

find ${DMCELLTRUEPATH}/nodes -name ${INSTNODE2_BASE}* > custNode2List.txt
CUSTNODE2TRUEPATH=`cat custNode2List.txt|awk '{printf $0}'`
CUSTNODE2NAME=${CUSTNODE2TRUEPATH##*nodes/}
echo CUSTNODE2NAME=${CUSTNODE2NAME}

find ${DMCELLTRUEPATH}/nodes -name ${IHSNODE_BASE}_* > ihsNodeList.txt
IHSNODETRUEPATH=`cat ihsNodeList.txt|awk '{printf $0}'`
IHSNODENAME=${IHSNODETRUEPATH##*nodes/}
echo IHSNODENAME=${IHSNODENAME}

find ${DMCELLTRUEPATH}/nodes -name ${IHSNODE2_BASE}* > ihsNode2List.txt
IHSNODE2TRUEPATH=`cat ihsNode2List.txt|awk '{printf $0}'`
IHSNODE2NAME=${IHSNODE2TRUEPATH##*nodes/}
echo IHSNODE2NAME=${IHSNODE2NAME}

# echo "bind user: ${BIND_USER}"
# echo "search base: ${LDAP_Search_Base}"
# echo "mail ssl enabled: ${MAILSSLENABLED}"

PARSE1="HTTPHOSTCORE:${HTTPHOST}.${PROJDOMAIN} HTTPHOST2CORE:${HTTPHOST2}.${PROJDOMAIN} DMGRURL:${DMSYSHST}.${PROJDOMAIN} COREDOM:${PROJDOMAIN} DB2SRV:${DBSYSHST}.${PROJDOMAIN} STANDBYDB2SRV:${STANDBYDBSYSHST}.${PROJDOMAIN} DBPASSWDdecrypt:${LCUSER_PW} HADRDBNAME:${HADR_DBName} HADRDBUSER:${HADR_DBUser} NODSRV:${NDSYSHST}.${PROJDOMAIN} NOD2SRV:${ND2SYSHST}.${PROJDOMAIN} CELLN:${CELL_NAME} DMCELLNAME:${CELL_NAME} INSTNODE1:${CUSTNODENAME} INSTNODE2:${CUSTNODE2NAME} IHSNODE:${IHSNODENAME} IHS2NODE:${IHSNODE2NAME} CONNECTIONSADMIN:virtuser CONNECTIONSADMINPASSWD:${VIRTUSERPW} RTUSR:virtuser remROOTpw:${VIRTUSERPW} LDAPSRV:${LDAP_SRV} MAILSRV:${MAIL_SRV} BINDPW:${BIND_USER_PW} LDAPSSLENABLED:${ENABLE_SSL_LDAP} LPORT:${LDAP_PORT} LTYPE:${LDAP_TYPE} MAILSRVPORT:${MAIL_PORT} MAILSSLENABLED:${ENABLE_SSL_MAIL} MAILUSER:${Mail_User} MAILPWD:${Mail_Password} GPFILESYSTEM:${fileSystemName} ICSHAREDDIR:${contentStoreLink} ICSHAREDFOLDER:${contentStoreFolder} DBPORTALL:${DB2Port} DMCONNECTORPORT:${DM_Connector_Port} CUSTNODE1NFSHOST:${CustNode1_NFS_Host} CUSTNODE1NFSEXPORT:${CustNode1_NFS_Export} CUSTNODE2NFSHOST:${CustNode2_NFS_Host} CUSTNODE2NFSEXPORT:${CustNode2_NFS_Export} IHSNODE1NFSHOST:${IHSNode1_NFS_Host} IHSNODE1NFSEXPORT:${IHSNode1_NFS_Export} IHSNODE2NFSHOST:${IHSNode2_NFS_Host} IHSNODE2NFSEXPORT:${IHSNode2_NFS_Export} PRIMARYDBNFSHOST:${PrimaryDB_NFS_Host} PRIMARYDBNFSEXPORT:${PrimaryDB_NFS_Export} STANDBYDBNFSHOST:${StandbyDB_NFS_Host} STANDBYDBNFSEXPORT:${StandbyDB_NFS_Export} TDINODENFSHOST:${TDINODE_NFS_Host} TDINODENFSEXPORT:${TDINODE_NFS_Export} DMNODENFSHOST:${DMNODE_NFS_Host} DMNODENFSEXPORT:${DMNODE_NFS_Export}"
DESTFILE=${CONFDIR}/${CONFIGFILE}
if [ "${PROFILE_TYPE}" == "dmgr" ]; then
 for RES in ${PARSE1}; do
  RES1=`echo ${RES} |cut -d: -f1`
  RES2=`echo ${RES} |cut -d: -f2`
  PARA0=`grep ^${RES1}= ${DESTFILE} |cut -d= -f2`
  if [ "${PARA0}" == "${RES2}" ]; then
     echo "Paramter ${RES1} exists and has the correct value"
    else
     #echo "Changing paramter ${RES1} in file ${DESTFILE} to ${RES1}=${RES2}"
	 echo "Changing paramter ${RES1} in file ${DESTFILE}"
     #sed -i "s/$RES1=${PARA0}/${RES1}=${RES2}/g" ${DESTFILE}
	 ${basepath}/searchAndReplace.py -f ${DESTFILE} -o $RES1=${PARA0} -n ${RES1}=${RES2}
  fi
 done
 
  #Change the bind user; Since it has space, we are separating it from other parameters.  
  RES1=BINDUSR
  RES2=${BIND_USER}
  PARA0=`grep ^${RES1} ${DESTFILE} |cut -d= -f2`
  if [ "${PARA0}" == "${RES2}" ]; then
     echo "Paramter ${RES1}=${RES2} exists and has the correct value"
    else
     echo "Changing paramter ${RES1} in file ${DESTFILE}"
	 ${basepath}/searchAndReplace.py -f ${DESTFILE} -o ${RES1}="${PARA0}" -n ${RES1}="\"${RES2}\""
  fi
  
  #Change the LDAP search base; Since it has space, we are separating it from other parameters. 
  if [ -z "${LDAP_Search_Base}" ]
  then
	  echo "LDAP search base is not set"
  else  
	  RES1=SEARCHBASE
	  RES2=${LDAP_Search_Base}
	  PARA0=`grep ^${RES1} ${DESTFILE} |cut -d= -f2`
	  if [ "${PARA0}" == "${RES2}" ]; then
		 echo "Paramter ${RES1}=${RES2} exists and has the correct value"
		else
		 echo "Changing paramter ${RES1} in file ${DESTFILE} to ${RES1}=${RES2}"
		 ${basepath}/searchAndReplace.py -f ${DESTFILE} -o ${RES1}="${PARA0}" -n ${RES1}="\"${RES2}\""
	  fi
  fi
  
fi
}

transferAttrValue PROFILE_TYPE dmgr WAS_PROFILE_ROOT DMGRP
transferAttrValue PROFILE_TYPE dmgr DMSYSHST DMGRURL

editAttrValue

sed -i -e "s/icfenc1:50001/icfenc1:${DB2Port}/" ${CONFDIR}/${CONFIGFILE}

exit 0



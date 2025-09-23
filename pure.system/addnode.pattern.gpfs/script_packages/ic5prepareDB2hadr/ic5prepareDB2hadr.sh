#!/bin/bash
#
# Prepare environments for GCC on SLES or RedHat Systems
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
 exit 5
fi

INFODIR="${basepath}/dbinfos"
if [ -d $INFODIR ]; then
#echo "$INFODIR exists..."
find $INFODIR/*.info -exec rm {} \; > /dev/null 2>&1
find $INFODIR/*.txt -exec rm {} \; > /dev/null 2>&1
else 
mkdir -p $INFODIR
chmod -R 777 $INFODIR
fi

# DB2 Backup Directory
if [ -d "$BACKUPHOME" ]; then
echo "$BACKUPHOME exists..."
else
mkdir -p $BACKUPHOME
chmod -R 777 $BACKUPHOME
fi

# DB2 Archive Log Directory
if [ -d "$LOGARCH" ]; then
echo "$LOGARCH exists..."
else
mkdir -p $LOGARCH
chmod -R 777 $LOGARCH
fi

markup()
{
echo ""
echo "$1 function $2"
echo "---------------------------------------------------------------------"
echo ""
echo ""
}

####### Start section: For later use with one db in a separate instance ########
## User and group management for DB2 environment. Please make sure, that each instance created needs its users/groups config 
# Groups needed for DB2 environment
# usage: (<groupname>:<GID> <groupname>:<GID>)
#DB2GROUPSALL="dasadm1:101 lxadmins:1000 icinst1:1100 icadm1:1101 icinst2:1102 icadm2:1103 dsrdbm01:1104 icinst3:1105 icadm3:1106 \
#icinst4:1107 icadm4:1108 icinst5:1109 icadm5:1110 icinst6:1111 icadm6:1112 icinst7:1113 icadm7:1114 icinst8:1115 \
#icadm8:1116 icinst9:1117 icadm9:1118 icinst10:1119 icadm10:1120 icinst11:1121 icadm11:1122 icinst12:1123 icadm12:1124 \
#icadm13:1125 icinst13:1126 icadm14:1127 icinst14:1129"

# Membership used for DB2 access rights
# usage: ( <user>:<group,group,group...> <user>:<group,group,group...>)
#GRPMEMBER="lcuser:lxadmins,dasadm1,icadm1,icadm2,icadm3,icadm4,icadm5,icadm6,icadm7,icadm8,icadm9,icadm10,icadm11,icadm12 \
#icinst1:icadm1,dasadm1 icinst2:icadm2,dasadm1 dsrdbm01:dsrdbm01,dasadm1 icinst3:icadm3,dasadm1 icinst4:icadm4,dasadm1 icinst5:icadm5,dasadm1 \
#icinst6:icadm6,dasadm1 icinst7:icadm7,dasadm1 icinst8:icadm8,dasadm1 icinst9:icadm9,dasadm1 icinst10:icadm10,dasadm1 icinst11:icadm11,dasadm1 \
#icinst12:icadm12,dasadm1 icinst13:icadm13,dasadm1 icinst14:icadm14,dasadm1"

# DB2 Users 
# usage: ( USERNAME:UID:GROUPID:HOME_DIR:BASH:PASSWD )
#DB2USRALL="icinst1:1100:1100:${HME}/icinst1:${SLN}:aZ0I3ow9 icfenc1:1101:1101:${HME}/icfenc1:${SLN}:Wij8yU2W \
#icinst2:1102:1102:${HME}/icinst2:${SLN}:tEw8Ak7o icfenc2:1103:1103:${HME}/icfenc2:${SLN}:Wu7hoIc4 dsrdbm01:1104:1104:${HME}/dsrdbm01:/bin/ksh:GIS5db \
#icinst3:1105:1105:${HME}/icinst3:${SLN}:yieRsh9E icfenc3:1106:1106:${HME}/icfenc3:${SLN}:rI6gAj1e icinst4:1107:1107:${HME}/icinst4:${SLN}:es6fic9M \
#icfenc4:1108:1108:${HME}/icfenc4:${SLN}:iC5ci3Qu icinst5:1109:1109:${HME}/icinst5:${SLN}:sarp2oF6 icfenc5:1110:1110:${HME}/icfenc5:${SLN}:fAg8nAic \
#icinst6:1111:1111:${HME}/icinst6:${SLN}:mO3kEc4Y icfenc6:1112:1112:${HME}/icfenc6:${SLN}:feG2co3S icinst7:1113:1113:${HME}/icinst7:${SLN}:cak5fot4 \
#icfenc7:1114:1114:${HME}/icfenc7:${SLN}:Drov1oc8 icinst8:1115:1115:${HME}/icinst8:${SLN}:iK0Ij4fO icfenc8:1116:1116:${HME}/icfenc8:${SLN}: \
#icinst9:1117:1117:${HME}/icinst9:${SLN}:eN4uRck7 icfenc9:1118:1118:${HME}/icfenc9:${SLN}:If4deiRs icinst10:1119:1119:${HME}/icinst10:${SLN}:voB9deAr \
#icfenc10:1120:1120:${HME}/icfenc10:${SLN}:Ran6qUoi icinst11:1121:1121:${HME}/icinst11:${SLN}:noOl9orT icfenc11:1122:1122:${HME}/icfenc11:${SLN}:ayn5cas2 \
#icinst12:1123:1123:${HME}/icinst12:${SLN}:curP0yeC icfenc12:1124:1124:${HME}/icfenc12:${SLN}:Doyd5iV5 lcuser:1125:1000:${HME}/lcuser:${SLN}:n27wXBfq \
#icinst13:1126:1126:${HME}/icinst13:${SLN}:knap5oWm icfenc13:1127:1127:${HME}/icfenc13:${SLN}:com7zEf3 icinst14:1128:1128:${HME}/icinst14:${SLN}:Jaid9Kev \
#icfenc14:1129:1129:${HME}/icfenc14:${SLN}:vist3Uk3"
####### End section: For later use with one db in a separate instance ########

# Different Status Queries and if $2 is set change the status
# usage: status_rh < ulimits | checkyum | db2groups | db2users
status_rh()
{
case $1 in
	# Group Config. Use different VARS for different usage
	# OSgroups <DB2 | STD> <installgroups> 
	OSgroups)
	TMPGROUPSALL=""
	if [ "${2}" = "DB2" ]; then
	TMPGROUPSALL=${DB2GROUPSALL}
	fi
	if [ "${2}" = "STD" ]; then
	TMPGROUPSALL=${STDGROUPS}
	fi
	echo "------------------------------${1} GROUP CONFIG----------------------------"
	for exgroups in ${TMPGROUPSALL}; do
	tmpgrp01=`echo ${exgroups} |cut -d: -f1 | sed 's/ //g'`
	tmpgrp02=`echo ${exgroups} |cut -d: -f2 | sed 's/ //g'`
	tmpgrp03=`echo ${exgroups} |cut -d: -f3 | sed 's/ //g'`
	chk01=`cat /etc/group |grep ${tmpgrp01} |grep ${tmpgrp02} |wc -l`
	groupmod ${tmpgrp01} > /dev/null 2>&1 && grpStatus=1 || grpStatus=0
	if [ "${grpStatus}" = "0" ]; then
	echo -e "\e[00;31m Group ${tmpgrp01} does not exists! \e[00m"
	if [ "$3" = "installgroups" ]; then
	groupadd -g ${tmpgrp02} ${tmpgrp01} 
	fi
	fi 
	if [ "${grpStatus}" = "1" ]; then
	if [ "${chk01}" = "" ]; then
	chk01=0
	fi
		if [ "${chk01}" = "0" ]; then
		echo -e "\e[00;31m Group ${tmpgrp01} exists, but has a wrong GID! \e[00m"
		echo -e "\e[00;31m Please correct manually!!!\e[00m"
		else
		echo -e "\e[00;32m Group ${tmpgrp01} exists and has the correct GID ${tmpgrp02}! \e[00m"
		fi
	fi
	done
	;;
	# User Config. Use different VARS for different usage
	# OSusers <DB2 | STD> <installusers> 
	OSusers)
	if [ "${2}" = "DB2" ]; then
	TMPGROUPSALL=${DB2USRALL}
	fi
	if [ "${2}" = "STD" ]; then
	TMPGROUPSALL=${STDUSR}
	fi
	echo "-----------------------------${1} USER  CONFIG----------------------------"
	for exusers in ${TMPGROUPSALL}; do
	usrname=`echo ${exusers} |cut -d: -f1 | sed 's/ //g'`
	usrid=`echo ${exusers} |cut -d: -f2 | sed 's/ //g'`
	usrgrp=`echo ${exusers} |cut -d: -f3 | sed 's/ //g'`
	usrhme=`echo ${exusers} |cut -d: -f4 | sed 's/ //g'`
	usrlgn=`echo ${exusers} |cut -d: -f5 | sed 's/ //g'`
	usrpwd=`echo ${exusers} |cut -d: -f6 | sed 's/ //g'`
	chkid=`cat /etc/passwd |grep ${usrname} |grep ${usrid} |wc -l`
	chkgrp=`cat /etc/passwd |grep ${usrname} |grep ${usrgrp} |wc -l`
	chkhme=`cat /etc/passwd |grep ${usrname} |grep ${usrhme} |wc -l`
	chklgn=`cat /etc/passwd |grep ${usrname} |grep ${usrlgn} |wc -l`
	#
	id ${usrname} > /dev/null 2>&1 && usrStatus=1 || usrStatus=0
	if [ "${usrStatus}" = "0" ]; then
	echo -e "\e[00;31m User ${usrname} does not exists! \e[00m"
			if [ "$3" = "installusers" ]; then
			if [ -d ${HME} ]; then
			touch ${HME}
			else
			mkdir -p ${HME}
			fi
	  		useradd -m -s ${usrlgn} -u ${usrid}  -g ${usrgrp} -d ${usrhme} ${usrname}
				if [ "${usrpwd}" = "" ]; then
				usrpwd=0
				fi
				if [ "${usrpwd}" = "0" ]; then
				echo "No password defined for user ${usrname}..."
				else
				echo ${usrpwd} |passwd --stdin ${usrname}
				fi
			fi
	fi 
	if [ "${usrStatus}" = "1" ]; then
	if [ "${chkid}" = "" ]; then
	chkid=0
	fi
	if [ "${chkgrp}" = "" ]; then
	chkgrp=0
	fi
	if [ "${chkhme}" = "" ]; then
	chkhme=0
	fi
	if [ "${chklgn}" = "" ]; then
	chklgn=0
	fi
		if [ "${chkid}" = "0" ] || [ "${chgrp}" = "0" ] || [ "${chhme}" = "0" ] || [ "${chklgn}" = "0" ]; then
		echo -e "\e[00;31m User ${usrname} exists, but has wrong Config Paramter. Should be --> ID: ${usrid} Group: ${usrgrp} Home: ${usrhme} Login: ${usrlgn}! \e[00m"
		echo -e "\e[00;31m `cat /etc/passwd |grep ${usrname}` \e[00m"
		echo -e "\e[00;31m Please correct manually!!!\e[00m"
		else
		echo -e "\e[00;32m User ${usrname} exists and has the correct Config Paramter --> ID: ${usrid} Group: ${usrgrp} Home: ${usrhme} Login: ${usrlgn} \e[00m"
		fi
	fi
	done
	# Add users to specific groups
	if [ "$3" = "installusers" ]; then
	TMPGROUPSALL=${GRPMEMBER}
	for exgroups in ${TMPGROUPSALL}; do
	username=`echo ${exgroups} |cut -d: -f1 | sed 's/ //g'`
	groups1=`echo ${exgroups} |cut -d: -f2 | sed 's/ //g'`
	echo "Configure User $username with the following groups:"
		echo ${groups1}
		usermod -G ${groups1} ${username}
	done
	fi
	;;
esac
}

xml_template_exec_prim()
{
markup starting "Creating DB2 HAICU on primary Node"
DB2SRVSHORT=`echo ${DB2SRV} |cut -d. -f1`
ADDDB2SHORT=`echo ${ADDDB2SRV} |cut -d. -f1`
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
(
cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!--
****************************************************************************
** Licensed Materials - Property of IBM
**
** Governed under the terms of the International
** License Agreement for Non-Warranted Sample Code.
**
** (C) COPYRIGHT International Business Machines Corp. 2007
** All Rights Reserved.
**
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*****************************************************************************
-->
<DB2Cluster xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="db2ha.xsd" clusterManagerName="TSA" version="1.0">
   <ClusterDomain domainName="db2HAdomain">
      <Quorum quorumDeviceProtocol="network" quorumDeviceName="${QDEVNAME}"/>
      <ClusterNode clusterNodeName="${DB2SRVSHORT}"/>
      <ClusterNode clusterNodeName="${ADDDB2SHORT}"/>
   </ClusterDomain>
   <FailoverPolicy>
      <HADRFailover></HADRFailover>
   </FailoverPolicy>
   <DB2PartitionSet>
      <DB2Partition dbpartitionnum="0" instanceName="$usr">
      </DB2Partition>
   </DB2PartitionSet>
EOF
) > ${INFODIR}/primaryxmlfile_$usr.xml
  for DBCOMP in $dbs; do
  DB=`echo ${DBCOMP} |cut -d+ -f1 |tr '[:lower:]' '[:upper:]'`
(
cat <<EOF
    <HADRDBSet>
      <HADRDB databaseName="${DB}" localInstance="$usr" remoteInstance="$usr" localHost="${DB2SRVSHORT}" remoteHost="${ADDDB2SHORT}" />
    </HADRDBSet>
EOF
) >> ${INFODIR}/primaryxmlfile_$usr.xml
  done
echo "</DB2Cluster>" >> ${INFODIR}/primaryxmlfile_$usr.xml
echo "su - $usr -c "db2haicu -f ${INFODIR}/primaryxmlfile_$usr.xml""
su - $usr -c "db2haicu -f ${INFODIR}/primaryxmlfile_$usr.xml"
#rm ${INFODIR}/primaryxmlfile_$usr.xml
done
markup finished "Creating DB2 HAICU on primary Node"
}


xml_template_exec_stdby()
{
markup starting "Creating DB2 HAICU on standby Node"
DB2SRVSHORT=`echo ${DB2SRV} |cut -d. -f1`
ADDDB2SHORT=`echo ${ADDDB2SRV} |cut -d. -f1`
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
(
cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!--
****************************************************************************
** Licensed Materials - Property of IBM
**
** Governed under the terms of the International
** License Agreement for Non-Warranted Sample Code.
**
** (C) COPYRIGHT International Business Machines Corp. 2007
** All Rights Reserved.
**
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*****************************************************************************
-->
<DB2Cluster xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="db2ha.xsd" clusterManagerName="TSA" version="1.0">
   <ClusterDomain domainName="db2HAdomain">
      <Quorum quorumDeviceProtocol="network" quorumDeviceName="${QDEVNAME}"/>
      <ClusterNode clusterNodeName="${DB2SRVSHORT}"/>
      <ClusterNode clusterNodeName="${ADDDB2SHORT}"/>
   </ClusterDomain>
   <FailoverPolicy>
      <HADRFailover></HADRFailover>
   </FailoverPolicy>
   <DB2PartitionSet>
      <DB2Partition dbpartitionnum="0" instanceName="$usr">
      </DB2Partition>
   </DB2PartitionSet>
</DB2Cluster>
EOF
)  > ${INFODIR}/standbyxmlfile_$usr.xml
echo "su - $usr -c "db2haicu -f ${INFODIR}/standbyxmlfile_$usr.xml""
su - $usr -c "db2haicu -f ${INFODIR}/standbyxmlfile_$usr.xml"
#rm ${INFODIR}/standbyxmlfile_$usr.xml
done
markup finished "Creating DB2 HAICU on standby Node"
}



createDBs()
{
case $1 in
		opnact)
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/activities/db2/ && db2 -td@ -vf createDb.sql"
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/activities/db2/ && db2 -td@ -vf appGrants.sql"
		;;
		sncomm)
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/communities/db2/ && db2 -td@ -vf createDb.sql"
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/communities/db2/ && db2 -td@ -vf appGrants.sql"
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/communities/db2/ && db2 -td@ -vf calendar-createDb.sql"
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/communities/db2/ && db2 -td@ -vf calendar-appGrants.sql"
		;;
		peopledb)
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/profiles/db2/ && db2 -td@ -vf createDb.sql"
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/profiles/db2/ && db2 -td@ -vf appGrants.sql"
		;;
		homepage)
	   	su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/homepage/db2/ && db2 -td@ -vf createDb.sql"
	   	su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/homepage/db2/ && db2 -td@ -vf initData.sql"
	   	su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/homepage/db2/ && db2 -td@ -vf appGrants.sql"
	   	su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/homepage/db2/ && db2 -td@ -vf reorg.sql"	
		;;
		fnos)
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/libraries.os/db2/ && db2 -td@ -vf createDb.sql"
                su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/libraries.os/db2/ && db2 -td@ -vf appGrants.sql"
		;;
		fngcd)
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/libraries.gcd/db2/ && db2 -td@ -vf createDb.sql"
                su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/libraries.gcd/db2/ && db2 -td@ -vf appGrants.sql"
		;;	
		*)
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/$1/db2/ && db2 -td@ -vf createDb.sql"
		su - $2 -c "cd ${MOUNT_POINT}/software/Wizards/connections.sql/$1/db2/ && db2 -td@ -vf appGrants.sql" 
		;;
esac
}

db2backup()
{
markup starting "Creating DB2 Backup"
if [ -d ${BACKUPHOME} ]; then
rm -rf ${BACKUPHOME}/*
fi
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
for DBCOMP in $dbs; do
DB=`echo ${DBCOMP} |cut -d+ -f1`
(
cat <<EOF
BACKUP DATABASE $DB TO $BACKUPHOME WITH 2 BUFFERS BUFFER 1024 PARALLELISM 1 WITHOUT PROMPTING;
EOF
) > $INFODIR/$DB-$usr.info
echo "task for database: $DB"
echo "backup database ( offline ) $DB TO $BACKUPHOME"
su - $usr -c "db2 -tvf $INFODIR/$DB-$usr.info"
#rm $INFODIR/$DB-$usr.info
done
done
markup finished "Creating DB2 Backup"
}

db2restore()
{
markup starting "Restore DB2 Databases"
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
for DBCOMP in $dbs; do
DB=`echo ${DBCOMP} |cut -d+ -f1`
(
cat <<EOF
RESTORE DATABASE $DB FROM $BACKUPHOME;
EOF
) > $INFODIR/$DB-$usr.info
echo "task for database: $DB"
echo "restore database $DB FROM $BACKUPHOME to $usr"
su - $usr -c "db2 -tvf $INFODIR/$DB-$usr.info"
#rm $INFODIR/$DB-$usr.info
done
done
markup finished "Restore DB2 Databases"
}

createInstance()
{
markup starting "Creating DB2 Instances"
#Instances="<username>:<db,db,db>:<fencuser>:<port>
for INST in $Instances; do
 usr=`echo $INST |cut -d: -f1`
 dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
 fenc=`echo $INST |cut -d: -f3`
 prt=`echo $INST |cut -d: -f4`
 /opt/ibm/db2/V10.1/instance/db2icrt -a SERVER -p $prt -u $fenc $usr
 su - $usr -c "db2set DB2COMM=TCPIP"
 su - $usr -c "db2set DB2CODEPAGE=1208"
 su - $usr -c "db2 update dbm cfg using SVCENAME $prt"	
 su - $usr -c "db2start"
done
markup finished "Creating DB2 Instances"
}

restartInstance()
{
markup starting "Restart Instances"
for INST in $Instances; do
 usr=`echo $INST |cut -d: -f1`
 su - $usr -c "db2stop"
 sleep 5
 su - $usr -c "db2stop force"
 su - $usr -c "db2start"
done
markup finished "Restart Instances"
}

extractDBs()
{
markup starting "Create DBs in the defined instance"
for INST in $Instances; do
 usr=`echo $INST |cut -d: -f1`
 dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
 fenc=`echo $INST |cut -d: -f3`
 prt=`echo $INST |cut -d: -f4`
  for DBCOMP in $dbs; do
  DB=`echo ${DBCOMP} |cut -d+ -f1`
  echo $DB
  createDBs $DB $usr
  done
done
markup finished "Create DBs in the defined instance"
}

hadrConfig()
{
markup starting "Configure HADR service/ports in /etc/services"
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
   for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
    HAPRT=`echo ${DBCOMP} |cut -d+ -f2` 
    echo $DB
    CHECK1=`grep db2hadr_${DB}_p /etc/services`
    if [ "$CHECK1" == "" ]; then
    echo "db2hadr_${DB}_p $HAPRT/tcp # DB2 HADR $DB" >> /etc/services
    fi	
   done
done
markup finished "Configure HADR service/ports in /etc/services"
}

confDBs_Stdby()
{
markup starting "Configure HADR and other parameters in DBs on stanby host"
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
prt=`echo $INST |cut -d: -f4`
   for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
    HAPRT=`echo ${DBCOMP} |cut -d+ -f2`
    echo "Working on DB $DB"
    su - $usr -c "db2 update db cfg for $DB using LOGINDEXBUILD ON"
    su - $usr -c "db2 update db cfg for $DB using INDEXREC RESTART"
    su - $usr -c "db2 update db cfg for $DB using LOGARCHMETH1 DISK:${LOGARCH}/$usr AUTO_DEL_REC_OBJ ON num_db_backups 7 rec_his_retentn 7"
    su - $usr -c "db2 update db cfg for $DB using HADR_LOCAL_HOST ${ADDDB2SRV}"
    su - $usr -c "db2 update db cfg for $DB using HADR_REMOTE_HOST ${DB2SRV}"
    su - $usr -c "db2 update db cfg for $DB using HADR_LOCAL_SVC ${HAPRT}"
    su - $usr -c "db2 update db cfg for $DB using HADR_REMOTE_SVC ${HAPRT}"
    su - $usr -c "db2 update db cfg for $DB using HADR_REMOTE_INST $usr"
    su - $usr -c "db2 update db cfg for $DB using HADR_TIMEOUT 120"
    su - $usr -c "db2 update db cfg for $DB using HADR_SYNCMODE SYNC"
    su - $usr -c "db2 update db cfg for $DB using HADR_PEER_WINDOW 120"
    su - $usr -c "db2 update alternate server for database $DB using hostname ${DB2SRV} port ${prt}"
    done
su - $usr -c "db2stop"
sleep 5
su - $usr -c "db2stop force"
su - $usr -c "db2start"
## we expierenced inconsistence in dbs ( maybe after a snapshot ), so we have to sure...
 for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
    HAPRT=`echo ${DBCOMP} |cut -d+ -f2`
    echo "Backup DB $DB"
    su - $usr -c "db2 deactivate db ${DB}"
	sleep 2
	su - $usr -c  "db2 activate db ${DB}"
	su - $usr -c "db2 backup db ${DB} to /dev/null"
 done
su - $usr -c "db2stop"
sleep 5
su - $usr -c "db2stop force"
su - $usr -c "db2start"
done
markup finished "Configure HADR and other parameters in DBs on stanby host"
}

confDBs_Primary()
{
markup starting "Configure HADR and other parameters in DBs on primary host"
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
prt=`echo $INST |cut -d: -f4`
if [ ! -d ${LOGARCH}/$usr ]; then
su - $usr -c "mkdir ${LOGARCH}/$usr"
fi
 for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
    HAPRT=`echo ${DBCOMP} |cut -d+ -f2`
    echo "Working on DB $DB"
    su - $usr -c "db2 update db cfg for $DB using LOGINDEXBUILD ON"
    su - $usr -c "db2 update db cfg for $DB using INDEXREC RESTART"
    su - $usr -c "db2 update db cfg for $DB using LOGARCHMETH1 DISK:${LOGARCH}/$usr AUTO_DEL_REC_OBJ ON num_db_backups 7 rec_his_retentn 7"
    su - $usr -c "db2 update db cfg for $DB using HADR_LOCAL_HOST ${DB2SRV}"
    su - $usr -c "db2 update db cfg for $DB using HADR_REMOTE_HOST ${ADDDB2SRV}"
    su - $usr -c "db2 update db cfg for $DB using HADR_LOCAL_SVC ${HAPRT}"
    su - $usr -c "db2 update db cfg for $DB using HADR_REMOTE_SVC ${HAPRT}"
    su - $usr -c "db2 update db cfg for $DB using HADR_REMOTE_INST $usr"
    su - $usr -c "db2 update db cfg for $DB using HADR_TIMEOUT 120"
    su - $usr -c "db2 update db cfg for $DB using HADR_SYNCMODE SYNC"
    su - $usr -c "db2 update db cfg for $DB using HADR_PEER_WINDOW 120"
    su - $usr -c "db2 update alternate server for database $DB using hostname ${ADDDB2SRV} port ${prt}"
 done
su - $usr -c "db2stop"
sleep 5
su - $usr -c "db2stop force"
su - $usr -c "db2start"
## we expierenced inconsistence in dbs ( maybe after a snapshot ), so we have to sure...
 for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
    HAPRT=`echo ${DBCOMP} |cut -d+ -f2`
    echo "Backup DB $DB"
    su - $usr -c "db2 deactivate db ${DB}"
	sleep 2
	su - $usr -c  "db2 activate db ${DB}"
	su - $usr -c "db2 backup db ${DB} to /dev/null"
 done
su - $usr -c "db2stop"
sleep 5
su - $usr -c "db2stop force"
su - $usr -c "db2start"
done
markup finished "Configure HADR and other parameters in DBs on stanby host"
}

statusHadr_Stdby()
{
markup starting "Starting HADR on stanby host"
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
   for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
	 if [ "${1}" == "start" ]; then	 
	 su - $usr -c "db2 start hadr on db $DB as standby"
    fi
    if [ "${1}" == "stop" ]; then
    su - $usr -c "db2 stop hadr on db $DB as standby"
    fi
   done
done
markup finished "Starting HADR on stanby host"
}

statusHadr_Primary()
{
markup starting "Starting HADR on primary host"
for INST in $Instances; do
usr=`echo $INST |cut -d: -f1`
dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
   for DBCOMP in $dbs; do
    DB=`echo ${DBCOMP} |cut -d+ -f1`
	 if [ "${1}" == "start" ]; then	 
	 su - $usr -c "db2 start hadr on db $DB as primary"
    fi
    if [ "${1}" == "stop" ]; then
    su - $usr -c "db2 stop hadr on db $DB as primary"
    fi
   done
done
markup finished "Starting HADR on primary host"
}

stdby_task1()
{
## The first task on STANDBY Host, create the groups, users and instance(s)
CHKBASE=`hostname -f`
if [ "${CHKBASE}" == "${ADDDB2SRV}" ]; then
 echo "We are on the DB2 HADR StandBy Host"
 status_rh OSgroups DB2 installgroups
 status_rh OSusers DB2 installusers
 createInstance
else
 echo "This is not the Standby Hosts -- ERROR"
 exit 5	
fi
}

stdby_task2()
{
## After environment is ready (stdby_task1) and the task prim_task1 on 
## PRIM Host ( backup DBs, etc. ) has finished, we are going on with restore DBs etc.
CHKBASE=`hostname -f`
if [ "${CHKBASE}" == "${ADDDB2SRV}" ]; then
 echo "We are on the DB2 HADR StandBy Host"
 hadrConfig
 db2restore
 confDBs_Stdby
 statusHadr_Stdby start
 markup starting "Resetting Node IDs from Hypervisior Images"
 echo "/usr/sbin/rsct/install/bin/recfgct"
 /usr/sbin/rsct/install/bin/recfgct
 markup finished "Resetting Node IDs from Hypervisior Images"
 markup starting "Executing PREPRPNODE on standby host"
 echo "preprpnode ${ADDDB2SRV} ${DB2SRV}"
 preprpnode ${ADDDB2SRV} ${DB2SRV}
 markup finished "Executing PREPRPNODE on standby host"
else
 echo "This is not the Standby Hosts -- ERROR"
 exit 5	
fi
}

stdby_task3()
{
## The last task creates the DB2 HAICU Domain....
CHKBASE=`hostname -f`
if [ "${CHKBASE}" == "${ADDDB2SRV}" ]; then
 echo "We are on the DB2 HADR StandBy Host"
  xml_template_exec_stdby
else
 echo "This is not the Standby Hosts -- ERROR"
 exit 5	
fi
}

prim_task1()
{
CHKBASE=`hostname -f`
## After Cluster Servers are offline, this is the first task for DB2 primary node
### Configuration for the PRIMARY HOST
if [ "${CHKBASE}" == "${DB2SRV}" ]; then
 echo "We are on the DB2 HADR Primary Host"
 hadrConfig
 confDBs_Primary
 db2backup
else
 echo "This is not the Primary Hosts -- ERROR"
 exit 5
fi
}

prim_task2()
{
CHKBASE=`hostname -f`
## After the DB2 StandBy Host is configured and the DBs are restored, we have some tasks left...
### Configuration for the PRIMARY HOST
if [ "${CHKBASE}" == "${DB2SRV}" ]; then
 echo "We are on the DB2 HADR Primary Host"
 statusHadr_Primary start
 markup starting "Resetting Node IDs from Hypervisior Images"
 echo "/usr/sbin/rsct/install/bin/recfgct"
 /usr/sbin/rsct/install/bin/recfgct
 markup finished "Resetting Node IDs from Hypervisior Images"
 markup starting "Executing PREPRPNODE on primary host"
 echo "preprpnode ${DB2SRV} ${ADDDB2SRV}"
 preprpnode ${DB2SRV} ${ADDDB2SRV}
 markup finished "Executing PREPRPNODE on primary host"
else
 echo "This is not the Primary Hosts -- ERROR"
 exit 5
fi
}

prim_task3()
{
CHKBASE=`hostname -f`
## Last task on primary host creates the DB2 HAICU Domain
### Configuration for the PRIMARY HOST
if [ "${CHKBASE}" == "${DB2SRV}" ]; then
 echo "We are on the DB2 HADR Primary Host"
 xml_template_exec_prim
else
 echo "This is not the Primary Hosts -- ERROR"
 exit 5
fi
}

addSecondaryNode() {
 rootPassword=${remROOTpw}
 ## step 1: prepare local system for HADR
 ## - run command on local system
 echo -e "\n\nPreparing secondary node for HADR"
 stdby_task1

 ## step 2: stop all AppServer
 ## - run command on DMGR
 ### step 2.1: copy wsadminlib to DMGR
 echo -e "\n\nCopy scripts to DMGR"
 expect -c "
  set timeout 30
  spawn bash -c \"rsync -am -f '+ *.py' -f '-! */' ${basepath}/* ${DMGRURL}:${basepath}\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${rootPassword}\r\" }
  expect \"denied\" { puts \"Error with remote authentication\"; exit 1 }
  expect timeout { puts \"'expect' timeout reached\"; exit 1 }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi
 ### step 2.2: change ownership
 echo -e "\n\nChange ownership"
 expect -c "
  set timeout 30
  spawn ssh ${DMGRURL} \"chown -R ${CONNECTIONSADMIN}: ${basepath}\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${rootPassword}\r\" }
  expect timeout { puts \"'expect' timeout reached\"; exit }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi
 ### step 2.3: stop all AppServer
 echo -e "\n\nStopping all application servers via wsadmin on DMGR"
 expect -c "
  spawn ssh ${CONNECTIONSADMIN}@${DMGRURL} \"cd ${basepath}; ${DMGRP}/bin/wsadmin.sh -lang jython -javaoption -Dpython.path=${basepath} -f ${basepath}/wasStopServer.py\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${CONNECTIONSADMINPASSWD}\r\" }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi

 ## step 3: prepare primary node and backup databases
 ## - run command on primary node
 ### step 3.1: copy pattern script to primary node
 echo -e "\n\nCopy scripts to primary node"
 expect -c "
  set timeout 30
  spawn bash -c \"rsync -am -f '+ *.sh' -f '-! */' ${basepath}/* ${DB2SRV}:${basepath}\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${rootPassword}\r\" }
  expect \"denied\" { puts \"Error with remote authentication\"; exit 1 }
  expect timeout { puts \"'expect' timeout reached\"; exit 1 }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi
 ### step 3.2: prepare primary node and backup databases
 echo -e "\n\nPreparing primary node for HADR"
 expect -c "
  spawn ssh ${DB2SRV} \"cd ${basepath}; ${basepath}/$(basename $0) prim_task1\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${rootPassword}\r\" }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi

 ## step 4: restore databases on secondary node
 ## - run command on local system
 echo -e "\n\nRestoring databases on secondary node"
 stdby_task2

 ## step 5: start HADR on primary node
 ## - run command on primary node
 echo -e "\n\nStarting HADR on primary node"
 expect -c "
  spawn ssh ${DB2SRV} \"cd ${basepath}; ${basepath}/$(basename $0) prim_task2\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${rootPassword}\r\" }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi

 ## step 6: Create DB2 HAICU Domain on secondary node
 ## - run command on local system
 echo -e "\n\nCreating DB2 HAICU Domain on secondary node"
 stdby_task3
 
 ## step 7: create DB2 HAICU Domain on primary node
 ## - run command on primary node
 echo -e "\n\nCreating DB2 HAICU Domain on primary node"
 expect -c "
  spawn ssh ${DB2SRV} \"cd ${basepath}; ${basepath}/$(basename $0) prim_task3\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${rootPassword}\r\" }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi
  
 ## step 8: start all AppServer
 ## - run command on DMGR
 echo -e "\n\nStarting all application servers via wsadmin on DMGR"
 expect -c "
  spawn ssh ${CONNECTIONSADMIN}@${DMGRURL} \"cd ${basepath}; ${DMGRP}/bin/wsadmin.sh -lang jython -javaoption -Dpython.path=${basepath} -f ${basepath}/wasStartServer.py\"
  expect \"continue connecting\" { send \"yes\r\" }
  expect \"assword:\" { send \"${CONNECTIONSADMINPASSWD}\r\" }
  interact"
 if [ "$?" -eq 1 ]; then echo -e "\n\nError within expect command - aborting script"; exit; fi
}


case $1 in
	auto)
	status_rh OSgroups DB2 installgroups
	status_rh OSusers DB2 installusers
	createInstance
	extractDBs
	;;
	stdby_task1)
	stdby_task1
	;;
	stdby_task2)
	stdby_task2
	;;
	prim_task1)
	prim_task1
	;;
	prim_task2)
	prim_task2
	;;
	addSecondaryNode)
	addSecondaryNode
	;;
	*)
	echo "usage: $0 < auto | stdby_task1 | stdby_task2 | prim_task1 | prim_task2 | addSecondaryNode >"
	;;
esac

exit 0

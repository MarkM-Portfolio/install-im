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

createInstance()
{
#Instances="<username>:<db,db,db>:<fencuser>:<port>
for INST in $Instances; do
 usr=`echo $INST |cut -d: -f1`
 dbs=`echo $INST |cut -d: -f2 |sed 's/,/\n/g'`
 fenc=`echo $INST |cut -d: -f3`
 prt=`echo $INST |cut -d: -f4`
 	${DBToolPath}/instance/db2icrt -a SERVER -p $prt -u $fenc $usr
	su - $usr -c "db2set DB2COMM=TCPIP"
	su - $usr -c "db2set DB2CODEPAGE=1208"
	su - $usr -c "db2 update dbm cfg using SVCENAME $prt"	
 	su - $usr -c "db2start"
	 	for DBCOMP in $dbs; do
		DB=`echo ${DBCOMP} |cut -d+ -f1`
 		echo $DB
 		#call function createDBs with param <application> <instance>
 		createDBs $DB $usr
 		done
done
}

case $1 in
	auto)
	status_rh OSgroups DB2 installgroups
	status_rh OSusers DB2 installusers
	createInstance
	;;
	*)
	echo "usage: $0 < auto >"
	;;
esac

exit 0

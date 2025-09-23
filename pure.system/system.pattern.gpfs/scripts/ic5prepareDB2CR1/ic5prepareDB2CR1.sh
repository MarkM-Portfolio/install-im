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

cp -r ${MOUNT_POINT}/software/Wizards/50cr1-database-updates ${basepath}
chmod -R 777  ${basepath}/50cr1-database-updates

updateDBs()
{
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-activities-db2.sql"
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-blogs-db2.sql"
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-calendar-db2.sql"
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-files-db2.sql"
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-forums-db2.sql"
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-homepage-db2.sql"
	su - icinst1 -c "cd ${basepath}/50cr1-database-updates/From-50/db2/ && db2 -td@ -vf 50-CR1-homepage-appGrants-db2.sql"
	su - icinst1 -c "cd ${basepath}/sql/db2/ && db2 -td@ -vf 50-CR1-updatecfg-db2.sql"
}

case $1 in
	auto)
	updateDBs
	;;
	*)
	echo "usage: $0 < auto >"
	;;
esac

umount ${MOUNT_POINT}

exit 0

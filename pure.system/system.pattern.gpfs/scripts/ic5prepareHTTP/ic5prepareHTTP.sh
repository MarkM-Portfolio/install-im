#! /bin/bash
#
#
# set HTTP Server settings
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
KEYSPW=ghwpadm
#HTPATH=/opt/IBM/HTTPServer
HTPATH=${IHS_INSTALL_ROOT}
#JAVAEXEC=/opt/IBM/WebSphere/AppServer/java/bin/java
JAVAEXEC="${IHS_INSTALL_ROOT}/java/jre/bin/java"

NEWHSTNAME=`hostname -f`
NEWCONURL=${CONNECTIONSURL}

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

prepHTTPconfig()
{
## copy template files to the correct path
cp -r ${basepath}/conf ${basepath}/keys ${basepath}/modules ${HTPATH}/

## replace the template URLs with the runtime URLs
cat ${basepath}/conf/httpd.conf |sed s/httpsvr.collab.local/${NEWHSTNAME}/g > ${basepath}/httpd_mod.conf
cat ${basepath}/httpd_mod.conf |sed s/social.collab.local/${NEWCONURL}/g > ${HTPATH}/conf/httpd.conf

## replace "[ICSHARE]" in httpd.conf with used variable for Connections share directory
sed -i "s#\[ICSHARE\]#${ICSHAREDDIR}#g" ${HTPATH}/conf/httpd.conf
}

createSelfSignCert()
{
${JAVAEXEC} com.ibm.gsk.ikeyman.ikeycmd -cert -create -dB ${HTPATH}/keys/key.kdb -pw ${KEYSPW} -label PureIC5Certificate -dn "CN=${NEWHSTNAME}" -size 2048 -default_cert yes
}

restartHTTP()
{
${HTPATH}/bin/apachectl stop
sleep 5
${HTPATH}/bin/apachectl stop
sleep 3
${HTPATH}/bin/apachectl start
}


case $1 in
	*)
	prepHTTPconfig
	KDB=`find ${CONFDIR}/CERTS/ -type f -name "*.kdb" -exec basename {} \;`
	STH=`find ${CONFDIR}/CERTS/ -type f -name "*.sth" -exec basename {} \;`
	if [ "${KDB}" != "" ] && [ "${STH}" != "" ]; then
	 echo "Using KeyStore and StashFile from ${CONFDIR}/CERTS/"
	 echo "--> cp "${CONFDIR}/CERTS/${KDB}" ${HTPATH}/keys/key.kdb"
	 cp "${CONFDIR}/CERTS/${KDB}" ${HTPATH}/keys/key.kdb
	 echo "--> cp "${CONFDIR}/CERTS/${STH}" ${HTPATH}/keys/key.sth"
	 cp "${CONFDIR}/CERTS/${STH}" ${HTPATH}/keys/key.sth
	else
	echo "No KeyStore and StashFile in ${CONFDIR}/CERTS/ found - Creating self signed certificates"
	createSelfSignCert
	fi
	restartHTTP
	;;
esac

umount ${MOUNT_POINT}

exit 0
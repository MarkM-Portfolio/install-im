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
    perl -pe 's/(.+?)=(.*)/\1=\"\2\"/' /etc/virtualimage.properties > virtualimage.rc
    . virtualimage.rc
fi
##if [ -f /etc/virtualimage.properties ]; then
## . /etc/virtualimage.properties
##fi

## LOAD PROJECT PROPERTIES FILE
NFSSHARE=${MOUNT_POINT}
CONFDIR="${NFSSHARE}/pureshare/config/${PROJECTNAME}"
CONFFILE=ic5pattern.properties
if [ -f ${CONFDIR}/${CONFFILE} ]; then
 echo -e "\n\nLoading project variables - \""${PROJECTNAME}"\""
 . ${CONFDIR}/${CONFFILE}
else
 echo "Error loading properties file for \""${PROJECTNAME}"\" ("${CONFDIR}"/"${CONFFILE}") - aborting Installation."
 exit 1
fi

# Local VARS
TDISOL=${TDISOLDIR}
TDIPATH=${TDIINSTDIR}
DBIP=${DB2SRV}
LCERT=${CONFDIR}/CERTS/ldap_ssl.cer
KEYSTORES="${TDIPATH}/testserver.jks:server ${TDIPATH}/serverapi/testadmin.jks:administrator"

service iptables stop
/sbin/chkconfig iptables off

ulimit -n 65536

sed -i '/virtuser hard nofile/d' /etc/security/limits.conf
sed -i '/root soft nproc/d' /etc/security/limits.conf
sed -i '/root hard nproc/d' /etc/security/limits.conf
sed -i '/root soft nofile/d' /etc/security/limits.conf
sed -i '/root hard nofile/d' /etc/security/limits.conf
sed -i '/root soft stack/d' /etc/security/limits.conf
sed -i '/session required pam_limits.so/d' /etc/pam.d/login

echo "virtuser hard nofile 65536" >> /etc/security/limits.conf
echo "root soft nproc 2047" >> /etc/security/limits.conf
echo "root hard nproc 16384" >> /etc/security/limits.conf
echo "root soft nofile 1024" >> /etc/security/limits.conf
echo "root hard nofile 65536" >> /etc/security/limits.conf
echo "root soft stack 10240" >> /etc/security/limits.conf
echo "session required pam_limits.so" >> /etc/pam.d/login

## needed for disconnect from openssl session
imapscript () {
while sleep 1; do
  echo "01 logout"
done
}

retrLCert()
{
if [ ! -d ${CONFDIR}/CERTS ]; then
mkdir -p ${CONFDIR}/CERTS
fi
imapscript | openssl s_client -crlf -connect ${LDAPSRV}:${LPORT} 2>&1 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > ${LCERT}
}

installCerts()
{
CERTS=`find ${CONFDIR}/CERTS/ -regex .*cer -exec basename {} \;`
if [ "${CERTS}" != "" ]; then
  for CERT in ${CERTS}; do
  	DTYPE=${CERTS/*./}
	for KEYS in ${KEYSTORES}; do
 	KEYSTORE=`echo ${KEYS} | cut -d: -f1`
 	KEYPWD=`echo ${KEYS} | cut -d: -f2` 
 	echo "Installing ${CERT} as alias ${CERT%%.$DTYPE} to keystore ${KEYSTORE}"
	${TDIPATH}/jvm/jre/bin/keytool -import -noprompt -trustcacerts -alias ${CERT%%.$DTYPE} -file ${CONFDIR}/CERTS/${CERT} -keystore ${KEYSTORE} -storepass ${KEYPWD}
	done
  done
echo "....done!"
else
echo "No CERTs found for installation!"
fi
}


## Change only the dyn. DB2 URL. The rest should be hard set in the config files....
customizePROPS()
{
    echo "customizePROPS begins:"
    echo "Set dbrepos_jdbc_url=jdbc:db2://${DB2SRV}:${DBPORTALL}/peopledb in ${TDISOL}/profiles_tdi.properties"
    sed -i -e "s|dbrepos_jdbc_url=.*|dbrepos_jdbc_url=jdbc:db2://${DB2SRV}:${DBPORTALL}/peopledb|" ${TDISOL}/profiles_tdi.properties

    local ldap_protocol="ldap"
    local ldap_url=""
    echo "ssl: ${ENABLE_SSL_LDAP}"
    if [ "${ENABLE_SSL_LDAP}" = "true" ]; then
        ldap_protocol=ldaps
    fi
    ldap_url="${ldap_protocol}://${LDAPSRV}:${LPORT}"
    echo "Set source_ldap_url=${ldap_url} in ${TDISOL}/profiles_tdi.properties"
    sed -i -e "s|source_ldap_url=.*|source_ldap_url=${ldap_url}|" ${TDISOL}/profiles_tdi.properties

    echo "Set source_ldap_search_base=${LDAP_SearchBase} in ${TDISOL}/profiles_tdi.properties"
    sed -i -e "s/source_ldap_search_base=.*/source_ldap_search_base=${LDAP_Search_Base}/" ${TDISOL}/profiles_tdi.properties

    echo "Set source_ldap_search_filter=${LDAP_Search_Filter} in ${TDISOL}/profiles_tdi.properties"
	filter="${LDAP_Search_Filter}"
	filter_escaped=`echo $filter | sed -e "s/&/\\\\\&/"`
    sed -i -e "s|source_ldap_search_filter=.*|source_ldap_search_filter=${filter_escaped}|" ${TDISOL}/profiles_tdi.properties
	##sed -i -e "s|source_ldap_search_filter=.*|source ...|"
    ## python ${basepath}/searchAndReplace.py -f ${TDISOL}/profiles_tdi.properties -o source_ldap_search_filter= -n source_ldap_search_filter="${LDAP_Search_Filter}"

    echo "Set source_ldap_user_login=${BIND_USER} in ${TDISOL}/profiles_tdi.properties"
    sed -i -e "s/source_ldap_user_login=.*/source_ldap_user_login=${BIND_USER}/" ${TDISOL}/profiles_tdi.properties

    echo "Set source_ldap_user_password=Password_Removed in ${TDISOL}/profiles_tdi.properties"
    sed -i -e "s/source_ldap_user_password=.*/{protect}-source_ldap_user_password=${BIND_USER_PW}/" ${TDISOL}/profiles_tdi.properties
	
    echo "Set dbrepos_password=Password_Removed in ${TDISOL}/profiles_tdi.properties"
    sed -i -e "s/dbrepos_password=.*/{protect}-dbrepos_password=${LC_USER_PW}/" ${TDISOL}/profiles_tdi.properties	

    chmod 755 ${TDISOL}/*
    echo "customizePROPS finished"
}

populateUsers()
{
echo "populateUsers"
${TDISOL}/collect_dns.sh
${TDISOL}/populate_from_dn_file.sh
echo "finished population"
}

cron()
{
echo "cron begins:"
check1=`crontab -l |grep sync_all_dns.sh |wc -l`
echo "check1: ${check1}"
if [ "$check1" != "0" ]; then
echo "Crontab entry exists:"
echo "---------------------"
crontab -l
else
	if ! ${SETCRONTAB} ; then
	(crontab -l 2>/dev/null; echo "#*/15 * * * * ${TDISOL}/sync_all_dns.sh") | crontab -
	else
	(crontab -l 2>/dev/null; echo "*/15 * * * * ${TDISOL}/sync_all_dns.sh") | crontab -
	fi
fi
echo "cron finished"
}

if [ -d ${CONFDIR}/TDI ]; then
    echo "Using Config Files from ${CONFDIR}/TDI/...."
    cp -v -R ${CONFDIR}/TDI/* ${TDISOL}/
else
    echo "Using Config Files from ${basepath}/TDI/...."
    cp -v -R ${basepath}/TDI/* ${TDISOL}/
fi

case $1 in
	man)
	man_vars
	customizePROPS
	cron
	;;
	*)
	if [ "${LPORT}" != "389" ]; then
   retrLCert
   installCerts
   else
   echo "No LDAPS configured in property file - skipping tasks retrLCert & installCerts"
   fi
	customizePROPS
	populateUsers
	cron
	;;	
esac

umount ${MOUNT_POINT}

exit 0 

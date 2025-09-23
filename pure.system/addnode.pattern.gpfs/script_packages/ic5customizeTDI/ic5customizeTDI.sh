#!/bin/bash
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

# Local VARS
TDISOL=${TDISOLDIR}
TDIPATH=${TDIINSTDIR}
DBIP=${DB2SRV}
LCERT=${CONFDIR}/CERTS/ldap_ssl.cer
KEYSTORES="${TDIPATH}/testserver.jks:server ${TDIPATH}/serverapi/testadmin.jks:administrator"

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
ARGORGDB=`grep dbrepos_jdbc_url ${TDISOL}/profiles_tdi.properties |cut -d= -f2`
python ${basepath}/searchAndReplace.py -f ${TDISOL}/profiles_tdi.properties -o dbrepos_jdbc_url=${ARGORGDB} -n dbrepos_jdbc_url=jdbc:db2://${DB2SRV}:50001/peopledb
ARGORGLDAP=`grep source_ldap_url ${TDISOL}/profiles_tdi.properties |cut -d= -f2`
python ${basepath}/searchAndReplace.py -f ${TDISOL}/profiles_tdi.properties -o source_ldap_url=${ARGORGLDAP} -n source_ldap_url=ldap://${LDAPSRV}:${LPORT}
chmod 755 ${TDISOL}/*
}

cron()
{
check1=`crontab -l |grep sync_all_dns.sh |wc -l`
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
}

if [ -d ${CONFDIR}/TDI ]; then
echo "Using Config Files from ${CONFDIR}/TDI/...."
cp -R ${CONFDIR}/TDI/* ${TDISOL}/
else
echo "Using Config Files from ${WORKDIR}/...."
cp -R ${basepath}/TDI/* ${TDISOL}/
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
	cron
	;;	
esac

exit 0 

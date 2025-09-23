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

if [ -f ${CONFDIR}/${CONFIGFILE} ]; then
echo "Using Config File from ${CONFDIR}/${CONFIGFILE}"
. ${CONFDIR}/${CONFIGFILE}
else
echo "No Config File found -- ERROR"
exit 5
fi

set > ${basepath}/EnvVARs.txt

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
     sed -i "s/$4=${PARA1}/$4=${PARA0}/g" ${DESTFILE}
    fi
 else
  echo "Adding parameter $4=${PARA0} to file ${DESTFILE}"
  echo "$4=${PARA0}" >> ${DESTFILE}
 fi
else
 echo "The paramter $1 is not available...."
fi
}

# description: Use for set local vars depend of deployment time
# usage editAttrValue 
# EDIT PARSE1="<LOCALVAR:GLOBALVAR LOCALVAR:GLOBALVAR>"
# PARSE1="HTTPHOSTCORE:${HTTPHOST} COREDOM:${DOMAIN}"
editAttrValue()
{
PARSE1="ADDDB2SRV:${DBSYSHST}.${COREDOM} remROOTpw:${ROOT_PASSWORD}"
DESTFILE=${CONFDIR}/${CONFIGFILE}
if [ -d /db2home ]; then
 for RES in ${PARSE1}; do
  RES1=`echo ${RES} |cut -d: -f1`
  RES2=`echo ${RES} |cut -d: -f2`
  PARA0=`grep ^${RES1} ${DESTFILE} |cut -d= -f2`
  if [ "${PARA0}" == "${RES2}" ]; then
     echo "Paramter ${RES1}=${RES2} exists and has the correct value"
    else
     echo "Changing paramter ${RES1} in file ${DESTFILE} to ${RES1}=${RES2}"
     sed -i "s/$RES1=${PARA0}/${RES1}=${RES2}/g" ${DESTFILE}
  fi
 done
fi
}
 
#transferAttrValue PROFILE_TYPE dmgr HOSTNAME DMGRURL
editAttrValue

exit 0
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

#!/bin/bash
# description: 
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
 exit 1
fi


echo "ICSHAREDDIR: ${ICSHAREDDIR}"
echo "link: ${contentStoreLink}"

PARSE1="ICSHAREDDIR:${contentStoreLink}"
DESTFILE=${CONFDIR}/${CONFFILE}

 for RES in ${PARSE1}; do
  echo "RES: ${RES}"
  RES1=`echo ${RES} |cut -d: -f1`
  RES2=`echo ${RES} |cut -d: -f2`
  PARA0=`grep ^${RES1} ${DESTFILE} |cut -d= -f2`
  echo "res1: ${RES1} res2: ${RES2} PARA0: ${PARA0}"
  if [ "${PARA0}" == "${RES2}" ]; then
     echo "Paramter ${RES1}=${RES2} exists and has the correct value"
    else
     echo "Changing paramter ${RES1} in file ${DESTFILE} to ${RES1}=${RES2}"
     #sed -i "s/$RES1=${PARA0}/${RES1}=${RES2}/g" ${DESTFILE}
	 ${basepath}/searchAndReplace.py -f ${DESTFILE} -o $RES1=${PARA0} -n ${RES1}=${RES2}
  fi
 done



exit 0
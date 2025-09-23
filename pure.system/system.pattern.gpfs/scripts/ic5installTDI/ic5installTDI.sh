#!/bin/bash
#
# Silent Installation TDI Server
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

# installs TDI 7.1.1 silently and unpack IC5 TDISOL to the install dir
instTDI()
{
${LOCSHARE}/TDI/linux_x86_64/install_tdiv711_linux_x86_64.bin -f ${basepath}/tdi_install.rsp -i silent
tar xvf ${INSTDIR}/TDISOL/tdisol.tar -C ${TDIINSTDIR}
chmod 755 ${TDISOLDIR}/*
}

# installs TDI 7.1.1 FP3 silently
instFP3()
{
cp ${LOCSHARE}/TDI_FP3/7.1.1-TIV-TDI-FP0003/UpdateInstaller.jar ${TDIINSTDIR}/maintenance/
${TDIINSTDIR}/bin/applyUpdates.sh -update ${LOCSHARE}/TDI_FP3/7.1.1-TIV-TDI-FP0003/TDI-7.1.1-FP0003.zip -clean -silent
${TDIINSTDIR}/bin/applyUpdates.sh -queryreg
}

case $1 in
	*)
	instTDI
	instFP3
	;;
	fp3)
	instFP3
	;;
	tdi)
	instTDI
	;;
esac

exit 0
#!/bin/sh
# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2013, 2016                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

export CE_HOME=../ContentEngine

log() {
	echo "$*" 2>&1 | tee -a ccmDomainTool.log
}

if [ ! `uname -s` = "OS400" ]; then
	export JAVA_HOME=${CE_HOME}/_cejvm/jre
fi

if [ ! -f "$JAVA_HOME"/bin/java ]
then
	export JAVA_HOME=${CE_HOME}/_cejvm/jre
fi

log `date "+%Y-%m-%d %H:%M:%S"`
log "CE_HOME=${CE_HOME}"
log "JAVA_HOME=${JAVA_HOME}"

if [ ! `uname -s` = "OS400" ]; then
	if [ ! -f create_gcd.ok ]
	then
		log ""
		log "ERROR: It appears the last attempt to create the Global Configuration Database - also known as the GCD or domain - may not have succeeded."
		log "If you are just getting started, it is recommended that you drop and recreate the GCD database using the Database Wizard and then rerun the createGCD.sh script before continuing."
		log "It is possible the current GCD contains errors."
		log ""

		exit 1
	fi
fi

export CLASSPATH=.:${CE_HOME}/lib/Jace.jar:${CE_HOME}/lib/log4j.jar:./ccmDomainTool.jar

"${JAVA_HOME}/bin/java" -classpath ${CLASSPATH} com.ibm.connections.ccmDomainTool.ccmDomainTool generateSID

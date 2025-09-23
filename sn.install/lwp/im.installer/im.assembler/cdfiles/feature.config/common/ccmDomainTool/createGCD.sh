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

export CE_HOME=../FileNet/ContentEngine
export FNCS_HOME=../FNCS

log() {
	echo "$*" 2>&1 | tee -a $(pwd -L)/ccmDomainTool.log
}

#if [ ! -f "$JAVA_HOME"/bin/java ]
#then
	export JAVA_HOME=${CE_HOME}/_cejvm/jre
#fi

log `date "+%Y-%m-%d %H:%M:%S"`
log "CE_HOME=${CE_HOME}"
log "FNCS_HOME=${FNCS_HOME}"
log "JAVA_HOME=${JAVA_HOME}"

export CLASSPATH=.:${CE_HOME}/lib/Jace.jar:${CE_HOME}/lib/log4j.jar:./ccmDomainTool.jar

"${JAVA_HOME}/bin/java" -classpath ${CLASSPATH} com.ibm.connections.ccmDomainTool.ccmDomainTool createP8Domain

if [ -f gcd_success ]
then
	# Remove previous success run output flag
	rm -f create_gcd.ok > /dev/null 2>&1
	# Generate new success run output flag
	echo `date "+%Y-%m-%d %H:%M:%S"` >create_gcd.ok
fi

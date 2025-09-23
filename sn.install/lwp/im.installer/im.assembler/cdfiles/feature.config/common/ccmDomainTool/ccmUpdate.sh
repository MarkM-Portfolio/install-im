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

export CE_HOME=./../ContentEngine
export FNCS_HOME=./../FNCS

if [ -f "$JAVA_HOME"/bin/java ]
   then
        launchInstaller=1
   else
        export JAVA_HOME=${CE_HOME}/_cejvm/jre
fi

export CLASSPATH=.:${CE_HOME}/lib/Jace.jar:${CE_HOME}/lib/log4j.jar:./ccmDomainTool.jar
"${JAVA_HOME}/bin/java" -classpath ${CLASSPATH} com.ibm.connections.ccmDomainTool.ccmDomainTool ccmUpdate

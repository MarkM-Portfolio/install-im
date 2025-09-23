#!/bin/sh
# ***************************************************************** 
#                                                                   
#                                                                   
# IBM Licensed Material                                             
#                                                                   
# Copyright IBM Corp. 2010, 2015                                          
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

# 5724-S68                                                          
# 5724-S68                                                         
if [ "${WAS_HOME}" == "" ]
then
	echo "The WAS_HOME environment variable needs to be set to the installation location of WebSphere Application Server"
	exit 1
else
	echo "WAS_HOME=${WAS_HOME}"
fi

MIGRATION_JAR=../lib/lc_migration.jar
if [ -f ../lib/lc_migration.jar ]
	then rm -rf $MIGRATION_JAR
fi
cp -rf lib/lc_migration.jar $MIGRATION_JAR
#cp -rf scripts/imports/migrate_util.xml ../ConfigEngine/config/includes/migrate_util.xml

echo "${WAS_HOME}/bin/ws_ant.sh -f migrate_util.xml -propertyfile ../lcinstall.properties -DWasAppHome=${WAS_HOME} -lib ./lib $@"
${WAS_HOME}/bin/ws_ant.sh -f migrate_util.xml -propertyfile ../lcinstall.properties -DWasAppHome=${WAS_HOME} -lib ./lib $@

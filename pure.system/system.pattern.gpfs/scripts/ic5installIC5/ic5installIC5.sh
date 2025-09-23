#!/bin/bash
# Create Silent Install Script, configure WAS/LDAP and starts the IC5 install
#
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

#ConnectionInstallLocation="/opt/IBM/Connections"

APPPWD=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${CONNECTIONSADMINPASSWD}" -silent -noSplash"`
DBPASSWD=`su - ${RTUSR} -c "${IMPath}/imutilsc encryptString "${DBPASSWDdecrypt}" -silent -noSplash"`

cd /opt/IBM/WebSphere
chown -R virtuser.admingroup *

#INFO=<dbuser>:<dbpasswd>:<port>:<appadmin>:<appadminpwd>:<Clustername>:<is activated? true/false>
APPS="homepage news search activities blogs dogear communities files forum metrics mobile moderation profiles wikis ccm"
INFOhompepage=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOnews=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOsearch=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOactivities=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOblogs=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOdogear=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOcommunities=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOfiles=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOforum=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOmetrics=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOmobile=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOmoderation=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOprofiles=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOwikis=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}
INFOccm=${DBPASSWD}:${DBUSRALL}:${DBPORTALL}:${CONNECTIONSADMIN}:${APPPWD}

set > ${basepath}/EnvVars_dmgr.txt


setDMtoRunAsVirtuser() {
#set DM general property to run DM as virtuser
echo "set DM general property to run DM as virtuser"
su - ${RTUSR} -c "${DMGRP}/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -c \"AdminConfig.modify('(cells/${CELL_NAME}/nodes/${NODE_NAME}/servers/dmgr|server.xml#ProcessExecution_1)', '[[runAsUser "virtuser"] [runAsGroup "admingroup"] [runInProcessGroup "0"] [processPriority "20"] [umask "022"]]')\" -username ${CONNECTIONSADMIN} -password ${CONNECTIONSADMINPASSWD}"
}

execPYscript() {
 ## execute python scripts for wsadmin tasks
 echo "su - ${RTUSR} -c \"cd ${basepath} && ${DMGRP}/bin/wsadmin.sh -lang jython -port ${DMCONNECTORPORT} -username ${CONNECTIONSADMIN} -password Password_Removed -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*\""
 su - ${RTUSR} -c "cd ${basepath} && ${DMGRP}/bin/wsadmin.sh ${WSOPT} -f ${PYSCRIPT} -javaoption -Dpython.path=${basepath} $*"
}

checkPort() {
 ## checkPort HOST PORT
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -n "Checking port status - ${1}:${2} - "
  (echo >/dev/tcp/${1}/${2}) &> /dev/null
  if [ "$?" -eq 0 ]; then
   echo "online"
   return 1
  else
   echo "offline"
   return 0
  fi
 fi
}

# deploy-models. 
case $DEPLOY in
		large)
		homepageCLNA=HomepageCluster
		newsCLNA=NewsCluster
		searchCLNA=SearchCluster
		activitiesCLNA=ActivitiesCluster
		blogsCLNA=BlogsCluster
		dogearCLNA=DogearCluster
		communitiesCLNA=CommunitiesCluster
		filesCLNA=FilesCluster
		forumCLNA=ForumCluster
		metricsCLNA=MetricsCluster
		mobileCLNA=MobileCluster
		moderationCLNA=ModerationCluster
		profilesCLNA=ProfilesCluster
		wikisCLNA=WikisCluster
		filenetCLNA=FilenetCluster
		;;
		medium)
		CLMED1=Cluster1
		CLMED2=Cluster2
		CLMED3=InfraCluster
		CLMED4=CCMCluster
		activitiesCLNA=${CLMED1}
		communitiesCLNA=${CLMED1}
		forumCLNA=${CLMED1}
		metricsCLNA=${CLMED1}
		profilesCLNA=${CLMED1}
		blogsCLNA=${CLMED2}
		dogearCLNA=${CLMED2}
		filesCLNA=${CLMED2}
		wikisCLNA=${CLMED2}
		homepageCLNA=${CLMED3}
		mobileCLNA=${CLMED3}
		moderationCLNA=${CLMED3}
		newsCLNA=${CLMED3}
		searchCLNA=${CLMED3}
		;;
		small)
		CLSMALL=ConnectionsCluster
		homepageCLNA=${CLSMALL}
		newsCLNA=${CLSMALL}
		searchCLNA=${CLSMALL}
		activitiesCLNA=${CLSMALL}
		blogsCLNA=${CLSMALL}
		dogearCLNA=${CLSMALL}
		communitiesCLNA=${CLSMALL}
		filesCLNA=${CLSMALL}
		forumCLNA=${CLSMALL}
		metricsCLNA=${CLSMALL}
		mobileCLNA=${CLSMALL}
		moderationCLNA=${CLSMALL}
		profilesCLNA=${CLSMALL}
		wikisCLNA=${CLSMALL}
		filenetCLNA=${CLSMALL}
		;;
esac

for VAR in ${APPS}; do
case ${VAR} in
	homepage)
	VAR=$INFOhompepage
	homepageDBPWD=`echo ${VAR} |cut -d: -f1`
	homepageDBUSR=`echo ${VAR} |cut -d: -f2`
	homepagePORT=`echo ${VAR} |cut -d: -f3`
	homepageWSAD=`echo ${VAR} |cut -d: -f4`
	homepageWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	news)
	VAR=$INFOnews
	newsDBPWD=`echo ${VAR} |cut -d: -f1`
	newsDBUSR=`echo ${VAR} |cut -d: -f2`
	newsPORT=`echo ${VAR} |cut -d: -f3`
	newsWSAD=`echo ${VAR} |cut -d: -f4`
	newsWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	search)
	VAR=$INFOsearch
	searchDBPWD=`echo ${VAR} |cut -d: -f1`
	searchDBUSR=`echo ${VAR} |cut -d: -f2`
	searchPORT=`echo ${VAR} |cut -d: -f3`
	searchWSAD=`echo ${VAR} |cut -d: -f4`
	searchWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	activities)
	VAR=$INFOactivities
	activitiesDBPWD=`echo ${VAR} |cut -d: -f1`
	activitiesDBUSR=`echo ${VAR} |cut -d: -f2`
	activitiesPORT=`echo ${VAR} |cut -d: -f3`
	activitiesWSAD=`echo ${VAR} |cut -d: -f4`
	activitiesWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	blogs)
	VAR=$INFOblogs
	blogsDBPWD=`echo ${VAR} |cut -d: -f1`
	blogsDBUSR=`echo ${VAR} |cut -d: -f2`
	blogsPORT=`echo ${VAR} |cut -d: -f3`
	blogsWSAD=`echo ${VAR} |cut -d: -f4`
	blogsWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	dogear)
	VAR=$INFOdogear
	dogearDBPWD=`echo ${VAR} |cut -d: -f1`
	dogearDBUSR=`echo ${VAR} |cut -d: -f2`
	dogearPORT=`echo ${VAR} |cut -d: -f3`
	dogearWSAD=`echo ${VAR} |cut -d: -f4`
	dogearWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	communities)
	VAR=$INFOcommunities
	communitiesDBPWD=`echo ${VAR} |cut -d: -f1`
	communitiesDBUSR=`echo ${VAR} |cut -d: -f2`
	communitiesPORT=`echo ${VAR} |cut -d: -f3`
	communitiesWSAD=`echo ${VAR} |cut -d: -f4`
	communitiesWSPW=`echo ${VAR} |cut -d: -f5`	
	;;
	files)
	VAR=$INFOfiles
	filesDBPWD=`echo ${VAR} |cut -d: -f1`
	filesDBUSR=`echo ${VAR} |cut -d: -f2`
	filesPORT=`echo ${VAR} |cut -d: -f3`
	filesWSAD=`echo ${VAR} |cut -d: -f4`
	filesWSPW=`echo ${VAR} |cut -d: -f5`	
	;;	
	forum)
	VAR=$INFOforum
	forumDBPWD=`echo ${VAR} |cut -d: -f1`
	forumDBUSR=`echo ${VAR} |cut -d: -f2`
	forumPORT=`echo ${VAR} |cut -d: -f3`
	forumWSAD=`echo ${VAR} |cut -d: -f4`
	forumWSPW=`echo ${VAR} |cut -d: -f5`	
	;;
	metrics)
	VAR=$INFOmetrics
	metricsDBPWD=`echo ${VAR} |cut -d: -f1`
	metricsDBUSR=`echo ${VAR} |cut -d: -f2`
	metricsPORT=`echo ${VAR} |cut -d: -f3`
	metricsWSAD=`echo ${VAR} |cut -d: -f4`
	metricsWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	mobile)
	VAR=$INFOmobile
	mobileDBPWD=`echo ${VAR} |cut -d: -f1`
	mobileDBUSR=`echo ${VAR} |cut -d: -f2`
	mobilePORT=`echo ${VAR} |cut -d: -f3`
	mobileWSAD=`echo ${VAR} |cut -d: -f4`
	mobileWSPW=`echo ${VAR} |cut -d: -f5`	
	;;
	moderation)
	VAR=$INFOmoderation
	moderationDBPWD=`echo ${VAR} |cut -d: -f1`
	moderationDBUSR=`echo ${VAR} |cut -d: -f2`
	moderationPORT=`echo ${VAR} |cut -d: -f3`
	moderationWSAD=`echo ${VAR} |cut -d: -f4`
	moderationWSPW=`echo ${VAR} |cut -d: -f5`	
	;;
	profiles)
	VAR=$INFOprofiles
	profilesDBPWD=`echo ${VAR} |cut -d: -f1`
	profilesDBUSR=`echo ${VAR} |cut -d: -f2`
	profilesPORT=`echo ${VAR} |cut -d: -f3`
	profilesWSAD=`echo ${VAR} |cut -d: -f4`
	profilesWSPW=`echo ${VAR} |cut -d: -f5`	
	;;
	wikis)
	VAR=$INFOwikis
	wikisDBPWD=`echo ${VAR} |cut -d: -f1`
	wikisDBUSR=`echo ${VAR} |cut -d: -f2`
	wikisPORT=`echo ${VAR} |cut -d: -f3`
	wikisWSAD=`echo ${VAR} |cut -d: -f4`
	wikisWSPW=`echo ${VAR} |cut -d: -f5`	
	;;
	ccm)
	VAR=$INFOwikis
	ccmDBPWD=`echo ${VAR} |cut -d: -f1`
	ccmDBUSR=`echo ${VAR} |cut -d: -f2`
	ccmPORT=`echo ${VAR} |cut -d: -f3`
	ccmWSAD=`echo ${VAR} |cut -d: -f4`
	ccmWSPW=`echo ${VAR} |cut -d: -f5`
	;;
	*)
	echo "Error setting VARs...!"
esac
done

markup()
{
echo ""
echo "$1 function $2"
echo "---------------------------------------------------------------------"
echo ""
echo ""
}

create_rsp()
{
(
cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<agent-input acceptLicense='true'>
<server>
<repository location='${INSTDIR}'/>
</server>
<profile id='IBM Connections' installLocation='${INSTLOC}'>
<data key='eclipseLocation' value='${INSTLOC}'/>
<data key='user.import.profile' value='false'/>
<data key='cic.selector.os' value='linux'/>
<data key='cic.selector.arch' value='x86'/>
<data key='cic.selector.ws' value='gtk'/>
<data key='user.job' value='INSTALL'/>
<data key='cic.selector.nl' value='en'/>
<data key='user.lcinstallscript.path' value='/opt/IBM/WebSphere/Profiles/AppServer_Shared/../LCInstallScript'/>
<data key='user.clusterlist' value=''/>
<data key='user.nodeslist' value='${INSTNODE1},${INSTNODE2}'/>
<data key='user.clusterfullinfo' value=''/>
<data key='user.nodesHostnamelist' value='${NODSRV},${NOD2SRV}'/>
<data key='user.nodesServerlist' value=''/>
<data key='user.was.installlocation' value='/opt/IBM/WebSphere/AppServer'/>
<data key='user.was.userhome' value='${DMGRP}'/>
<data key='user.was.wasSoapPort' value='${DMCONNECTORPORT}'/>
<data key='user.was.profileName' value='DefaultDmgr01'/>
<data key='user.was.dmCellName' value='${CELLN}'/>
<data key='user.was.dmHostname' value='${DMGRURL}'/>
<data key='user.was.adminuser.id' value='${CONNECTIONSADMIN}'/>
<data key='user.was.adminuser.password' value='${APPPWD}'/>
<data key='user.activities.adminuser.id' value='${activitiesWSAD}'/>
<data key='user.activities.adminuser.password' value='${activitiesWSPW}'/>
<data key='user.blogs.adminuser.id' value='${blogsWSAD}'/>
<data key='user.blogs.adminuser.password' value='${blogsWSPW}'/>
<data key='user.dogear.adminuser.id' value='${dogearWSAD}'/>
<data key='user.dogear.adminuser.password' value='${dogearWSPW}'/>
<data key='user.communities.adminuser.id' value='${communitiesWSAD}'/>
<data key='user.communities.adminuser.password' value='${communitiesWSPW}'/>
<data key='user.files.adminuser.id' value='${filesWSAD}'/>
<data key='user.files.adminuser.password' value='${filesWSPW}'/>
<data key='user.forum.adminuser.id' value='${forumWSAD}'/>
<data key='user.forum.adminuser.password' value='${forumWSPW}'/>
<data key='user.homepage.adminuser.id' value='${homepageWSAD}'/>
<data key='user.homepage.adminuser.password' value='${homepageWSPW}'/>
<data key='user.ccm.adminuser.id' value='${ccmWSAD}'/>
<data key='user.ccm.adminuser.password' value='${ccmWSPW}'/>
<data key='user.metrics.adminuser.id' value='${metricsWSAD}'/>
<data key='user.metrics.adminuser.password' value='${metricsWSPW}'/>
<data key='user.mobile.adminuser.id' value='${mobileWSAD}'/>
<data key='user.mobile.adminuser.password' value='${mobileWSPW}'/>
<data key='user.moderation.adminuser.id' value='${moderationWSAD}'/>
<data key='user.moderation.adminuser.password' value='${moderationWSPW}'/>
<data key='user.news.adminuser.id' value='${newsWSAD}'/>
<data key='user.news.adminuser.password' value='${newsWSPW}'/>
<data key='user.profiles.adminuser.id' value='${profilesWSAD}'/>
<data key='user.profiles.adminuser.password' value='${profilesWSPW}'/>
<data key='user.search.adminuser.id' value='${searchWSAD}'/>
<data key='user.search.adminuser.password' value='${searchWSPW}'/>
<data key='user.wikis.adminuser.id' value='${wikisWSAD}'/>
<data key='user.wikis.adminuser.password' value='${wikisWSPW}'/>
<data key='user.deployment.type' value='${DEPLOY}'/>
<data key='user.activities.clusterExist' value='false'/>
<data key='user.activities.clusterName' value='${activitiesCLNA}'/>
<data key='user.activities.firstNodeName' value='${INSTNODE1}'/>
<data key='user.activities.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.activities.serverInfo' value='activities.${INSTNODE1}.ServerName=ActivitiesCluster_server1;activities.${INSTNODE2}.ServerName=ActivitiesCluster_server2;'/>
<data key='user.blogs.clusterExist' value='false'/>
<data key='user.blogs.clusterName' value='${blogsCLNA}'/>
<data key='user.blogs.firstNodeName' value='${INSTNODE1}'/>
<data key='user.blogs.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.blogs.serverInfo' value='blogs.${INSTNODE1}.ServerName=BlogsCluster_server1;blogs.${INSTNODE2}.ServerName=BlogsCluster_server2;'/>
<data key='user.dogear.clusterExist' value='false'/>
<data key='user.dogear.clusterName' value='${dogearCLNA}'/>
<data key='user.dogear.firstNodeName' value='${INSTNODE1}'/>
<data key='user.dogear.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.dogear.serverInfo' value='dogear.${INSTNODE1}.ServerName=DogearCluster_server1;dogear.${INSTNODE2}.ServerName=DogearCluster_server2;'/>
<data key='user.communities.clusterExist' value='false'/>
<data key='user.communities.clusterName' value='${communitiesCLNA}'/>
<data key='user.communities.firstNodeName' value='${INSTNODE1}'/>
<data key='user.communities.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.communities.serverInfo' value='communities.${INSTNODE1}.ServerName=CommunitiesCluster_server1;communities.${INSTNODE2}.ServerName=CommunitiesCluster_server2;'/>
<data key='user.files.clusterExist' value='false'/>
<data key='user.files.clusterName' value='${filesCLNA}'/>
<data key='user.files.firstNodeName' value='${INSTNODE1}'/>
<data key='user.files.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.files.serverInfo' value='files.${INSTNODE1}.ServerName=FilesCluster_server1;files.${INSTNODE2}.ServerName=FilesCluster_server2;'/>
<data key='user.forum.clusterExist' value='false'/>
<data key='user.forum.clusterName' value='${forumCLNA}'/>
<data key='user.forum.firstNodeName' value='${INSTNODE1}'/>
<data key='user.forum.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.forum.serverInfo' value='forum.${INSTNODE1}.ServerName=ForumCluster_server1;forum.${INSTNODE2}.ServerName=ForumCluster_server2;'/>
<data key='user.homepage.clusterExist' value='false'/>
<data key='user.homepage.clusterName' value='${homepageCLNA}'/>
<data key='user.homepage.firstNodeName' value='${INSTNODE1}'/>
<data key='user.homepage.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.homepage.serverInfo' value='homepage.${INSTNODE1}.ServerName=HomepageCluster_server1;homepage.${INSTNODE2}.ServerName=HomepageCluster_server2;'/>
<data key='user.metrics.clusterExist' value='false'/>
<data key='user.metrics.clusterName' value='${metricsCLNA}'/>
<data key='user.metrics.firstNodeName' value='${INSTNODE1}'/>
<data key='user.metrics.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.metrics.serverInfo' value='metrics.${INSTNODE1}.ServerName=MetricsCluster_server1;metrics.${INSTNODE2}.ServerName=MetricsCluster_server2;'/>
<data key='user.mobile.clusterExist' value='false'/>
<data key='user.mobile.clusterName' value='${mobileCLNA}'/>
<data key='user.mobile.firstNodeName' value='${INSTNODE1}'/>
<data key='user.mobile.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.mobile.serverInfo' value='mobile.${INSTNODE1}.ServerName=MobileCluster_server1;mobile.${INSTNODE2}.ServerName=MobileCluster_server2;'/>
<data key='user.moderation.clusterExist' value='false'/>
<data key='user.moderation.clusterName' value='${moderationCLNA}'/>
<data key='user.moderation.firstNodeName' value='${INSTNODE1}'/>
<data key='user.moderation.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.moderation.serverInfo' value='moderation.${INSTNODE1}.ServerName=ModerationCluster_server1;moderation.${INSTNODE2}.ServerName=ModerationCluster_server2;'/>
<data key='user.news.clusterExist' value='false'/>
<data key='user.news.clusterName' value='${newsCLNA}'/>
<data key='user.news.firstNodeName' value='${INSTNODE1}'/>
<data key='user.news.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.news.serverInfo' value='news.${INSTNODE1}.ServerName=NewsCluster_server1;news.${INSTNODE2}.ServerName=NewsCluster_server2;'/>
<data key='user.profiles.clusterExist' value='false'/>
<data key='user.profiles.clusterName' value='${profilesCLNA}'/>
<data key='user.profiles.firstNodeName' value='${INSTNODE1}'/>
<data key='user.profiles.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.profiles.serverInfo' value='profiles.${INSTNODE1}.ServerName=ProfilesCluster_server1;profiles.${INSTNODE2}.ServerName=ProfilesCluster_server2;'/>
<data key='user.search.clusterExist' value='false'/>
<data key='user.search.clusterName' value='${searchCLNA}'/>
<data key='user.search.firstNodeName' value='${INSTNODE1}'/>
<data key='user.search.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.search.serverInfo' value='search.${INSTNODE1}.ServerName=SearchCluster_server1;search.${INSTNODE2}.ServerName=SearchCluster_server2;'/>
<data key='user.wikis.clusterExist' value='false'/>
<data key='user.wikis.clusterName' value='${wikisCLNA}'/>
<data key='user.wikis.firstNodeName' value='${INSTNODE1}'/>
<data key='user.wikis.secondaryNodesNames' value='${INSTNODE2}'/>
<data key='user.wikis.serverInfo' value='wikis.${INSTNODE1}.ServerName=WikisCluster_server1;wikis.${INSTNODE2}.ServerName=WikisCluster_server2;'/>
<data key='user.activities.dbHostName' value='${DB2SRV}'/>
<data key='user.activities.dbPort' value='${activitiesPORT}'/>
<data key='user.activities.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.activities.dbName' value='OPNACT'/>
<data key='user.activities.dbUser' value='${activitiesDBUSR}'/>
<data key='user.activities.dbUserPassword' value='${activitiesDBPWD}'/>
<data key='user.activities.dbType' value='db2'/>
<data key='user.activities.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.activities.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.activities.selected' value='true'/>
<data key='user.blogs.dbHostName' value='${DB2SRV}'/>
<data key='user.blogs.dbPort' value='${blogsPORT}'/>
<data key='user.blogs.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.blogs.dbName' value='BLOGS'/>
<data key='user.blogs.dbUser' value='${blogsDBUSR}'/>
<data key='user.blogs.dbUserPassword' value='${blogsDBPWD}'/>
<data key='user.blogs.dbType' value='db2'/>
<data key='user.blogs.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.blogs.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.blogs.selected' value='true'/>
<data key='user.communities.dbHostName' value='${DB2SRV}'/>
<data key='user.communities.dbPort' value='${communitiesPORT}'/>
<data key='user.communities.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.communities.dbName' value='SNCOMM'/>
<data key='user.communities.dbUser' value='${communitiesDBUSR}'/>
<data key='user.communities.dbUserPassword' value='${communitiesDBPWD}'/>
<data key='user.communities.dbType' value='db2'/>
<data key='user.communities.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.communities.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.communities.selected' value='true'/>
<data key='user.dogear.dbHostName' value='${DB2SRV}'/>
<data key='user.dogear.dbPort' value='${dogearPORT}'/>
<data key='user.dogear.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.dogear.dbName' value='DOGEAR'/>
<data key='user.dogear.dbUser' value='${dogearDBUSR}'/>
<data key='user.dogear.dbUserPassword' value='${dogearDBPWD}'/>
<data key='user.dogear.dbType' value='db2'/>
<data key='user.dogear.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.dogear.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.dogear.selected' value='true'/>
<data key='user.profiles.dbHostName' value='${DB2SRV}'/>
<data key='user.profiles.dbPort' value='${profilesPORT}'/>
<data key='user.profiles.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.profiles.dbName' value='PEOPLEDB'/>
<data key='user.profiles.dbUser' value='${profilesDBUSR}'/>
<data key='user.profiles.dbUserPassword' value='${profilesDBPWD}'/>
<data key='user.profiles.dbType' value='db2'/>
<data key='user.profiles.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.profiles.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.profiles.selected' value='true'/>
<data key='user.wikis.dbHostName' value='${DB2SRV}'/>
<data key='user.wikis.dbPort' value='${wikisPORT}'/>
<data key='user.wikis.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.wikis.dbName' value='WIKIS'/>
<data key='user.wikis.dbUser' value='${wikisDBUSR}'/>
<data key='user.wikis.dbUserPassword' value='${wikisDBPWD}'/>
<data key='user.wikis.dbType' value='db2'/>
<data key='user.wikis.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.wikis.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.wikis.selected' value='true'/>
<data key='user.files.dbHostName' value='${DB2SRV}'/>
<data key='user.files.dbPort' value='${filesPORT}'/>
<data key='user.files.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.files.dbName' value='FILES'/>
<data key='user.files.dbUser' value='${filesDBUSR}'/>
<data key='user.files.dbUserPassword' value='${filesDBPWD}'/>
<data key='user.files.dbType' value='db2'/>
<data key='user.files.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.files.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.files.selected' value='true'/>
<data key='user.forum.dbHostName' value='${DB2SRV}'/>
<data key='user.forum.dbPort' value='${forumPORT}'/>
<data key='user.forum.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.forum.dbName' value='FORUM'/>
<data key='user.forum.dbUser' value='${forumDBUSR}'/>
<data key='user.forum.dbUserPassword' value='${forumDBPWD}'/>
<data key='user.forum.dbType' value='db2'/>
<data key='user.forum.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.forum.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.forum.selected' value='true'/>
<data key='user.news.dbHostName' value='${DB2SRV}'/>
<data key='user.news.dbPort' value='${newsPORT}'/>
<data key='user.news.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.news.dbName' value='HOMEPAGE'/>
<data key='user.news.dbUser' value='${newsDBUSR}'/>
<data key='user.news.dbUserPassword' value='${newsDBPWD}'/>
<data key='user.news.dbType' value='db2'/>
<data key='user.news.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.news.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.news.selected' value='true'/>
<data key='user.search.dbHostName' value='${DB2SRV}'/>
<data key='user.search.dbPort' value='${searchPORT}'/>
<data key='user.search.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.search.dbName' value='HOMEPAGE'/>
<data key='user.search.dbUser' value='${searchDBUSR}'/>
<data key='user.search.dbUserPassword' value='${searchDBPWD}'/>
<data key='user.search.dbType' value='db2'/>
<data key='user.search.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.search.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.search.selected' value='true'/>
<data key='user.homepage.dbHostName' value='${DB2SRV}'/>
<data key='user.homepage.dbPort' value='${homepagePORT}'/>
<data key='user.homepage.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.homepage.dbName' value='HOMEPAGE'/>
<data key='user.homepage.dbUser' value='${homepageDBUSR}'/>
<data key='user.homepage.dbUserPassword' value='${homepageDBPWD}'/>
<data key='user.homepage.dbType' value='db2'/>
<data key='user.homepage.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.homepage.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.homepage.selected' value='true'/>
<data key='user.metrics.dbHostName' value='${DB2SRV}'/>
<data key='user.metrics.dbPort' value='${metricsPORT}'/>
<data key='user.metrics.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.metrics.dbName' value='METRICS'/>
<data key='user.metrics.dbUser' value='${metricsDBUSR}'/>
<data key='user.metrics.dbUserPassword' value='${metricsDBPWD}'/>
<data key='user.metrics.dbType' value='db2'/>
<data key='user.metrics.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.metrics.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.metrics.selected' value='true'/>
<data key='user.mobile.selected' value='true'/>
<data key='user.mobile.dbHostName' value='${DB2SRV}'/>
<data key='user.mobile.dbPort' value='${mobilePORT}'/>
<data key='user.mobile.jdbcLibraryPath' value='${JDBCPATH}'/>
<data key='user.mobile.dbName' value='MOBILE'/>
<data key='user.mobile.dbUser' value='${mobileDBUSR}'/>
<data key='user.mobile.dbUserPassword' value='${mobileDBPWD}'/>
<data key='user.mobile.dbType' value='db2'/>
<data key='user.mobile.dataSourceTemplateName' value='DB2 Universal JDBC Driver DataSource'/>
<data key='user.mobile.jdbcProviderTemplateName' value='DB2 Universal JDBC Driver Provider'/>
<data key='user.moderation.selected' value='true'/>
<data key='user.contentStore.local.path' value='${ConnectionInstallLocation}/data/local'/>
<data key='user.contentStore.shared.path' value='${ICSHAREDDIR}'/>
<data key='user.messageStore.shared.path' value='${ICSHAREDDIR}'/>
<data key='user.connections.installlocation' value='${ConnectionInstallLocation}'/>
<data key='user.notification.replyto.enabled' value='false'/>
<data key='user.notification.replyto.domain' value=''/>
<data key='user.notification.replyto.localPart.type' value=''/>
<data key='user.notification.replyto.localPart' value=''/>
<data key='user.notification.replyto.mailFile.hostName' value=''/>
<data key='user.notification.replyto.mailFile.userID' value=''/>
<data key='user.notification.replyto.mailFile.password' value=''/>
<data key='user.notification.enabled' value='true'/>
<data key='user.notification.useJavaMailProvider' value='true'/>
<data key='user.notification.host' value='${MAILSRV}'/>
<data key='user.notification.port' value='${MAILSRVPORT}'/>
<data key='user.notification.user' value='${MAILUSER}'/>
<data key='user.notification.password' value='${MAILPWD}'/>
<data key='user.notification.ssl.enabled' value='${MAILSSLENABLED}'/>
<data key='user.notification.dnshost' value=''/>
<data key='user.notification.dnsport' value=''/>
<data key='user.notification.domain' value=''/>
</profile>
<install modify='false'>
<offering id='com.ibm.lotus.connections' version='5.0.0.0_20140613_0855' profile='IBM Connections' features='homepage,news,search,activities,blogs,dogear,communities,files,forum,metrics,mobile,moderation,profiles,wikis' installFixes='none'/>
</install>
<preference name='com.ibm.cic.common.core.preferences.eclipseCache' value='${CONN_SHARED_LOCATION}'/>
<preference name='com.ibm.cic.common.core.preferences.connectTimeout' value='30'/>
<preference name='com.ibm.cic.common.core.preferences.readTimeout' value='45'/>
<preference name='com.ibm.cic.common.core.preferences.downloadAutoRetryCount' value='0'/>
<preference name='offering.service.repositories.areUsed' value='true'/>
<preference name='com.ibm.cic.common.core.preferences.ssl.nonsecureMode' value='false'/>
<preference name='com.ibm.cic.common.core.preferences.http.disablePreemptiveAuthentication' value='false'/>
<preference name='http.ntlm.auth.kind' value='NTLM'/>
<preference name='http.ntlm.auth.enableIntegrated.win32' value='true'/>
<preference name='com.ibm.cic.common.core.preferences.preserveDownloadedArtifacts' value='true'/>
<preference name='com.ibm.cic.common.core.preferences.keepFetchedFiles' value='false'/>
<preference name='PassportAdvantageIsEnabled' value='false'/>
<preference name='com.ibm.cic.common.core.preferences.searchForUpdates' value='false'/>
<preference name='com.ibm.cic.agent.ui.displayInternalVersion' value='false'/>
<preference name='com.ibm.cic.common.sharedUI.showErrorLog' value='true'/>
<preference name='com.ibm.cic.common.sharedUI.showWarningLog' value='true'/>
<preference name='com.ibm.cic.common.sharedUI.showNoteLog' value='true'/>
</agent-input>
EOF
) > ${basepath}/ic5_install.rsp
echo "make ConnectionsInstallLocation"
mkdir -p ${ConnectionInstallLocation}
chmod -R 777 ${ConnectionInstallLocation}
echo "make ConnectionsInstallLocation/data/local"
mkdir -p ${ConnectionInstallLocation}/data/local
chmod -R 777 ${ConnectionInstallLocation}/data/local
mkdir -p ${CONN_SHARED_LOCATION}
chmod -R 777 ${CONN_SHARED_LOCATION}
chown -R ${RTUSR} ${CONN_SHARED_LOCATION} 
}

setPermSharedDir()
{
markup starting "Setting permissions of IC Shared directory"
chown -R ${RTUSR} ${ICSHAREDDIR}
chmod -R 777 ${ICSHAREDDIR}
markup finished "Setting permissions of IC Shared directory"
}

verifyBinaries()
{
echo "Verify required binary folders"
## Verify Connections binary folder
if [ "${INSTDIR}" == "" ]; then
	echo "VAR ${INSTDIR} is empty - exiting...."
	exit 1
fi

if [ -d ${INSTDIR} ]; then
	echo "${INSTDIR} directory exists"
else
	echo "${INSTDIR} directory does not exist"
	exit 1
fi

if [ "$(ls -A ${INSTDIR})" ]; then
	echo "Connections binary directory is not empty"
else
	echo "Connections binary directory is empty"
	exit 1
fi

## Verify Connections CR1 binary folder
if [ "${INSTCR1DIR}" == "" ]; then
  echo "VAR ${INSTCR1DIR} is empty - exiting...."
  exit 1
fi

if [ -d ${INSTCR1DIR} ]; then
	echo "${INSTCR1DIR} directory exists"
else
	echo "${INSTCR1DIR} directory does not exist"
	exit 1
fi

if [ "$(ls -A ${INSTCR1DIR})" ]; then
	echo "Connections CR1 binary directory is not empty"
else
	echo "Connections CR1 binary directory is empty"
	exit 1
fi
	
## Verify DB Wizard binary folder
if [ -d ${MOUNT_POINT}/software/Wizards ]
then
	echo "DB Wizard binary directory exists"
else
	echo "DB Wizard binary directory does not exist"
	exit 1
fi

if [ "$(ls -A ${MOUNT_POINT}/software/Wizards)" ]; then
	echo "DB Wizard binary directory is not empty"	
else
	echo "DB Wizard CR1 binary directory is empty"
	exit 1
fi
	
if [ -d ${MOUNT_POINT}/software/Wizards/50cr1-database-updates ]
then
	echo "DB Wizard CR1 binary directory exists"
else
	echo "DB Wizard CR1 binary directory does not exist"
	exit 1
fi

if [ "$(ls -A ${MOUNT_POINT}/software/Wizards/50cr1-database-updates)" ]; then
	echo "DB Wizard CR1 binary directory is not empty"
else
	echo "DB Wizard CR1 binary directory is empty"
	exit 1
fi
			
## Verify TDI binary folder
if [ -d "${MOUNT_POINT}/software/TDI" ]
then
	echo "TDI binary directory exists"
else
	echo "TDI binary directory does not exist"
	exit 1
fi

if [ "$(ls -A ${MOUNT_POINT}/software/TDI)" ]; then
	echo "TDI binary directory is not empty"
else
	echo "TDI binary directory is Empty"
	exit 1
fi
	
## Verify TDI_FP3 binary folder
if [ -d ${MOUNT_POINT}/software/TDI_FP3 ]
then
	echo "TDI_FP3 binary directory exists"
else
	echo "TDI_FP3 binary directory does not exist"
	exit 1
fi

if [ "$(ls -A ${MOUNT_POINT}/software/TDI_FP3)" ]; then
	echo "TDI_FP3 binary directory is not empty"
else
	echo "TDI_FP3 binary directory is Empty"
	exit 1
fi

}

inst()
{
#chown -R nobody.admingroup ${ICSHAREDDIR}
markup starting "Silent Installation of IBM Connections"
## Check if the IC Install folder is empty and install Connections
if [ "${INSTLOC}" == "" ]; then
echo "VAR ${INSTLOC} is empty - exiting...."
exit 1
else
echo "Installation folder is not empty... deleting first..."
echo "rm -rf ${INSTLOC}/*"
rm -rf ${INSTLOC}/*
fi
# echo "Changing ownership an start IC Installation!"
# echo "chown -R ${RTUSR} ${INSTLOC}"
# chown -R ${RTUSR} ${INSTLOC}
echo "su - ${RTUSR} -c \"cd ${IMPath} && ./imcl -acceptLicense -input ${basepath}/ic5_install.rsp -log /tmp/lc_silent_install.log\""
su - ${RTUSR} -c "cd ${IMPath} && ./imcl -acceptLicense -input ${basepath}/ic5_install.rsp -log /tmp/lc_silent_install.log"
markup finished "Silent Installation of IBM Connections"
}

linkProfiles()
{
## Connections Installer does not find the DMGR, if we don not set this link....
markup starting "Link Profiles Directory to the Default path"
echo "Linking Profiles Directory to the Default path"
echo "ln -s /opt/IBM/WebSphere/Profiles /opt/IBM/WebSphere/AppServer/profiles"
echo "ln -s /opt/IBM/WebSphere/Profiles/properties/* /opt/IBM/WebSphere/AppServer/properties/" 
ln -s /opt/IBM/WebSphere/Profiles /opt/IBM/WebSphere/AppServer/profiles
ln -s /opt/IBM/WebSphere/Profiles/properties/* /opt/IBM/WebSphere/AppServer/properties/
markup finished "Link Profiles Directory to the Default path"
}

activateAPPsecurity()
{
## Connections Installer requires activated WAS App Security!
markup starting "Activating WAS Application Security, needed for IC Install"
SECBKP=${basepath}/bkp_security.xml
echo "Activating WAS Application Security - an requirement for IC5 installer"
echo "cp ${DMGRP}/config/cells/${DMCELLNAME}/security.xml ${SECBKP}"
echo "cat ${SECBKP} | sed 's/appEnabled="false"/appEnabled="true"/g' > ${DMGRP}/config/cells/${DMCELLNAME}/security.xml"
echo "chown ${RTUSR} ${DMGRP}/config/cells/${DMCELLNAME}/security.xml"
cp ${DMGRP}/config/cells/${DMCELLNAME}/security.xml ${SECBKP}
cat ${SECBKP} | sed 's/appEnabled="false"/appEnabled="true"/g' > ${DMGRP}/config/cells/${DMCELLNAME}/security.xml
chown ${RTUSR} ${DMGRP}/config/cells/${DMCELLNAME}/security.xml
markup finished "Activating WAS Application Security, needed for IC Install"
}

dbfiles()
{
## Creating folder and copy DB2 driver files from script package
markup starting "Creating DB2 driver DIR and copy files"
echo "Copy Db2 driver files to the defined Folder"
echo "mkdir -p ${JDBCPATH}"
echo "mv ${basepath}/*.jar ${JDBCPATH}/"
mkdir -p ${JDBCPATH}
cp ${basepath}/*.jar ${JDBCPATH}/
chown -R ${RTUSR}:admingroup ${JDBCPATH}/
markup finished "Creating DB2 driver DIR and copy files"
}

setSSOdomain()
{
## Setting SSO Domain for Login 
markup starting "Configuring SSO domain"
PYSCRIPT=wasLDAPconfig.py
OPTS="domain="${SSODOMAIN}""
execPYscript ${OPTS}
markup stopping "Configure SSO domain"
}

addLDAPRepository()
{
## Adding LDAP ferderated repository
markup starting "Adding LDAP Repository"
PYSCRIPT=wasLDAPconfig.py
if [ "${LPORT}" == "636" ]; then
OPTS="ldapHost='"${LDAPSRV}"' ldapPort='"${LPORT}"' ldapUser='"${BINDUSR}"' ldapPassword='"${BINDPW}"' ldapType='"${LTYPE}"' ldapSearchBase='"${SEARCHBASE}"' ldapSSL='true' loginAttributes=''"
else
OPTS="ldapHost='"${LDAPSRV}"' ldapPort='"${LPORT}"' ldapUser='"${BINDUSR}"' ldapPassword='"${BINDPW}"' ldapType='"${LTYPE}"' ldapSearchBase='"${SEARCHBASE}"' ldapSSL='false' loginAttributes=''"
fi
echo "OPTS is ${OPTS}"
checkPort ${LDAPSRV} ${LPORT}
 if [ "$?" -eq 0 ]; then
  echo -e "\n\nError reaching ${LDAPSRV} - abort addLDAPRepository"
  else
  execPYscript ${OPTS}
 fi
markup stopping "Adding LDAP Repository"
}

addWASAdminRole()
{
## Adding WAS administrative ROLES to LDAP User 
markup starting "Adding WAS Admin Role to LDAP User"
PYSCRIPT=wasLDAPconfig.py
OPTS="userLdapName="${LDAPADM}""
checkPort ${LDAPSRV} ${LPORT}
if [ "$?" -eq 0 ]; then
	echo -e "\n\nError reaching ${LDAPSRV} - abort addWASAdminRole"
  	else
  	execPYscript ${OPTS}
fi	
markup stopping "Adding WAS Admin Role to LDAP User"
}

setLTPACookieNames()
{
## Name LtpaToken to the correct values, for SSO reasons
markup starting "setLTPACookieNames"
python ${basepath}/searchAndReplace.py -f ${DMGRP}/config/cells/${DMCELLNAME}/security.xml -o "</security:Security>" -n "<properties xmi:id= \"Property_1405354124846\" name=\"com.ibm.websphere.security.customSSOCookieName\" value=\"LtpaToken2\"/><properties xmi:id=\"Property_1405354124847\" name=\"com.ibm.websphere.security.customLTPACookieName\" value=\"LtpaToken\"/></security:Security>"
python ${basepath}/searchAndReplace.py -f ${DMGRP}/config/cells/${DMCELLNAME}/security.xml -o "name=\"com.ibm.ws.security.ssoInteropModeEnabled\" value=\"false\"" -n "name=\"com.ibm.ws.security.ssoInteropModeEnabled\" value=\"true\""
echo "Changing permissons: chown ${RTUSR} ${DMGRP}/config/cells/${DMCELLNAME}/security.xml"
chown ${RTUSR} ${DMGRP}/config/cells/${DMCELLNAME}/security.xml
markup finished "setLTPACookieNames"
}

statusDMGR()
{
su - ${RTUSR} -c "${DMGRP}/bin/${1}Manager.sh"
}

wasRetrieveSigner()
{
## Retrieve HTTP certificate and add it to WAS-Cell trustStore
markup starting "Retrieving SSL Certs from Web Server"

PYSCRIPT=wasRetrieveSigner.py
OPTS="hostName=${HTTPHOSTCORE} alias=http_ssl"
execPYscript ${OPTS}

OPTS="hostName=${HTTPHOST2CORE} alias=http_ssl2"
execPYscript ${OPTS}

markup stopped "Finished Retrieving SSL Certs from Web Server"
}

wasRetrieveLdapSigner()
{
## Retrieve LDAP certificate and add it to WAS-Cell trustStore, if using LDAPS
markup starting "Retrieving SSL Certs from LDAP Server"
PYSCRIPT=wasRetrieveSigner.py
OPTS="hostName=${LDAPSRV} alias=ldap_ssl port=${LPORT}"
if [ "${LDAPSSLENABLED}" != "false" ]; then
execPYscript ${OPTS}
else
echo "No LDAPS configured in property file - skipping task wasRetrieveLdapSigner"
fi
markup stopped "Finished Retrieving SSL Certs from Web Server"
}

wasInstallCerts()
{
## if placing certificates to ${CONFDIR}/CERTS/ install them....
markup starting "Installing SSL Certs from ${CONFDIR}/CERTS/"
CERTS=`find ${CONFDIR}/CERTS/ -regex .*cer -exec basename {} \;`
PYSCRIPT=wasRetrieveSigner.py
if [ "${CERTS}" != "" ]; then
  for CERT in ${CERTS}; do
  OPTS="certPath=${CONFDIR}/CERTS/${CERT}"
  execPYscript ${OPTS}
  ## seems to be (sometimes) a problem, if we call wsadmin in a short frequence. 
  sleep 5
  done
else
  echo "No CERTS found for installation!"
fi
markup finished "Installing SSL Certs from ${CONFDIR}/CERTS/"
}

case $1 in
	auto)	
	linkProfiles			# link Profiles DIR 
	activateAPPsecurity		# activate APP Security in WAS
	dbfiles					# create folder and copy DB2 driver
	wasInstallCerts			# install ext. Certs, if there any
	wasInstallCerts      	# Expirienced problems with wsadmin errors, so run it twice, to be sure
	wasRetrieveSigner		# retrieve HTTP cert
	wasRetrieveLdapSigner	# retrive LDAP certs, if use SSL Port 636
	addLDAPRepository		# adding LDAP repository
	#statusDMGR stop			# stop DMGR
	#statusDMGR start		# start DMGR
	#addWASAdminRole			# adding Admin from LDAP Repo as ISC Admin.
	setSSOdomain			# setting SSO domain
	setLTPACookieNames		# name LTPA Tokens to the correct values
	statusDMGR stop			# stop DMGR
	statusDMGR start		# start DMGR
	setPermSharedDir		# check/set permissions on the shared directory
	verifyBinaries          # verify required install binary directories exist
	create_rsp				# create response file
	inst					# install connections silently
	setDMtoRunAsVirtuser
	#statusDMGR stop			# stop DMGR
	#statusDMGR start		# start DMGR
	;;
	*)
	echo "usage $0 <auto>"
	;;
esac

exit 0

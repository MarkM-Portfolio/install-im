#!/usr/bin/python
#
# 
# GIS-AG
# created 2014-08-01
# version 1.0.0
#
# history:
# 2014-08-01: initial version
#
#

import sys
import wsadminlib as lb
import wasAdminConfig as adminConfig
import baseArguments as myArgs
import baseLogHandler as log


######################################################
#
# public methods
#
######################################################


def createServerInCluster(clustername, nodename, servername):
	"""Create a new server in a cluster, return its id.
	Turn on session replication if sessionReplication is True"""
	m = "createServerInCluster:"

	AdminTask.createClusterMember('[-clusterName %s -memberConfig[-memberNode %s -memberName %s -memberWeight 2 -genUniquePorts true -replicatorEntry false]]' % (clustername,nodename,servername))

def listServerClusters():
    """Return list of names of server clusters"""
    cluster_ids = AdminConfig.list( 'ServerCluster' ).splitlines()
    result = []
    for cluster_id in cluster_ids:
        result.append(AdminConfig.showAttribute(cluster_id,"name"))
    return result
    
def deleteServerFromNode(server, node):
	log.info("   deleteServerFromNode " + server + " " + node)
	ret = AdminTask.deleteServer(["-serverName", server, "-nodeName", node])
	log.info(ret)
	return ret


######################################################
#
# main
#
######################################################
def main(argv):
	
	log.info("\n*** manageApplicationClusters ***\n")
	
	"""
	hostName = myArgs.parseArgument(argv,"hostName")
	log.info("hostName = " + hostName)
	
	if(hostName==""):
		log.info("ERROR: hostName argument is missing")

	# hostName=vmConfig.get("HOSTNAME")
	#shortHostName = bs.shortHostName(hostName)
	#nodeName= shortHostName + "Node"
	
	log.info("try to find node for hostname " + hostName)
	nodeName = lb.findNodeOnHostname(hostName)
	log.info("node  (calculated from hostName) = ")
	log.info(nodeName)
	"""

	nodeName = myArgs.parseArgument(argv,"nodeName")
	log.info("nodeName = " + nodeName)
	
	if(nodeName==""):
		log.error("ERROR: nodeName argument is missing")
		sys.exit(1)
	
	
	clusters = listServerClusters()
	
	for clusterName in clusters:
		try: 	
			serverName= clusterName + "_" + nodeName
			if(clusterName!="CCMCluster"):
				log.info("Add Server to cluster : " + clusterName +  ", server: " + serverName)
				createServerInCluster(clusterName, nodeName, serverName)
				log.info ("OK")
			else:
				log.info ("CCMCluster ignored.....!")
		except:
			log.error("\nProblem adding server to cluster " + clusterName) 

			
	adminConfig.saveConfig()
	log.info ("\nfinished\n")  
	
	
if __name__ == "__main__":
	main(sys.argv)
   
   

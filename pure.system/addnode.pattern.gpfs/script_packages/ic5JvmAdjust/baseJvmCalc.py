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
import math

import baseLogHandler as log


######################################################
#
# public methods
#
######################################################
	


######################################################
#
#server methods
#
######################################################
"""
extract only these servers, which are installed on this node.
Because only for the node, we are on, we can read the system memory 
"""
def getServersForCurrentNode():
	servers = ["Activities", "WikisXX", "News"]
	return servers	




######################################################
#
# heap size calculation methods
#
######################################################


allServerRatios = [
["ActivitiesCluster",2.0],
["BlogsCluster",2.0],
["BookmarksCluster",1.5],
["DogearCluster",1.5],
["CCMCluster",2.5],
["CommunitiesCluster",2.5],
["FilesCluster",2.0],
["ForumsCluster",2.0],
["ForumCluster",2.0],
["HomepageCluster",1.5],
["MetricsCluster",2.5],
["MobileCluster",2.0],
["ModerationCluster",0.5],
["NewsCluster",2.5],
["ProfilesCluster",2.0],
["SearchCluster",2.5],
["WikisCluster",2.0], 
["Cluster1",2.0], 
["Cluster2",2.0], 
["InfraCluster",2.0]
]

"""
extract server ratios only for these servers, which are installed on this node
"""
def extractServerRatioList(serverList):
	
	allServerNames=[row[0] for row in allServerRatios]
	allRatios=[row[1] for row in allServerRatios]
	log.info(allServerNames)
	
	serverRatios = []
	for server in serverList:
		#
		# Servername is <Clustername>_<Node> per convention
		# We extract the cluster name to match against performance Tuning Guide
		log.info (server)
		temp = server.split("_")
		# log.info("L %d" %len(temp))
		clusterName = temp[0]
		# log.info(clusterName) 
		# log.info(server.find("_"))
		i=-1
		try: 
			i = allServerNames.index(clusterName)
		except:
			i = 1
			log.error("	 server '%s' not in list, use 1 as factor" %clusterName)

		serverRatios.append([server,allRatios[i]])

	return(serverRatios)
	

"""
expects system memory in bytes from
returns available system memory in MB
"""
def availableSystemMemory(rawSystemMemory):
	log.info(rawSystemMemory)
	systemMemory = rawSystemMemory/1024
	
	if(systemMemory > 40000.0):
		systemMemory = systemMemory - 8000.0
		availableMemory = round(systemMemory,-2)
	elif(systemMemory > 20000.0):
		systemMemory = systemMemory - 4000.0
		availableMemory = round(systemMemory,-2)
	elif(systemMemory > 10000.0):
		systemMemory = systemMemory - 2000.0
		availableMemory = round(systemMemory,-2)
	elif(systemMemory > 4000.0):
		systemMemory = systemMemory - 1000.0
		availableMemory = round(systemMemory,-2)
	elif(systemMemory > 2000.0):
		systemMemory = systemMemory - 500.0
		availableMemory = round(systemMemory,-2)
	else: 
		log.error ("Not enough system memory to adjust jvm heap sizes %f, %f" %(rawSystemMemory, systemMemory))
		availableMemory = 0
		
	availableMemory = round(systemMemory,-2)
	log.info("raw system memory (kB):  %f -> systemMemory (MB): %f   -> availableMemory (MB)  %s" %(rawSystemMemory, systemMemory, availableMemory))
	return availableMemory

def sumOfRatios(ratios): 
	
	sumRatio = 0.0
	# this loop adds all ratios of available servers
	# a simple sum command would do the same, but raises an error  in wsadmin jython
	for ratio in ratios:
		sumRatio = sumRatio + ratio
	log.info ("     sum of all ratios %f" %sumRatio)
	return sumRatio	
	
"""
"""
def calculateServerMemory(availableMemory,serverRatios): 
	
	#
	#
	log.info("calculate a memory factor, which fraction of memory a server heap will get") 
	log.info("the memory fraction for each server then is the memory factor mutiply by the server ratio")
	
	ratios=[row[1] for row in serverRatios]
	ratioSum = sumOfRatios(ratios)

	
	if (ratioSum > 0.0):
		factorRaw = availableMemory/ratioSum
		log.info("      availableMemory/ratioSum")
		factor = round(factorRaw, 1)
		log.info("  %f   --->>  %f " %(factorRaw, factor))
		
	else: 
		factor=1.0
		log.error ("Cannot access the list of available app servers. Use the default serverFactor = 1.0")


	servers=[row[0] for row in serverRatios]
	serverMemorySizes = []
	for i in range(len(ratios)):
		serverMemorySizes.append([servers[i],int(ratios[i]*factor)])
	
	log.info(serverMemorySizes)
	return(serverMemorySizes)
	
######################################################
#
# main
#
######################################################
def main():
	
	
	log.info("---------------------------") 
	log.info("            ****           ") 
	log.info("---------------------------")
	log.info("List all serverNames, available on this node: MOCK DATA")
	servers = getServersForCurrentNode() 
	log.info(servers)
	log.info("---------------------------") 
	log.info("Add memory ratio from IC tuning guide to serverlist")
	serverRatios = extractServerRatioList(servers)
	log.info(serverRatios)
	log.info("---------------------------") 
	log.info("Calculate available system memory")
	availableMemory = availableSystemMemory(avMem)
	log.info("---------------------------")
	log.info("Calculate memory for all available Servers")  
	serverMemoryList = calculateServerMemory(availableMemory,serverRatios)
	log.info(serverMemoryList)
		
if __name__ == "__main__":
   main()
   
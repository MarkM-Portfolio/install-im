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
import re
import os

import wsadminlib as lb
import baseJvmCalc as jvmCalc
import baseArguments as myArgs
import wasAdminConfig as adminConfig
import baseLogHandler as log

######################################################
#
# public methods
#
######################################################
def listHeapSizesForCluster(cluster):
	l = []
	log.info(cluster)
	serverIDs=lb.listServersInCluster(cluster)
	for serverID in serverIDs: 
			log.debug(serverID)
			jvm = AdminConfig.list('JavaVirtualMachine', serverID)
			log.debug(jvm)
			s = []
			s.append(lb.getNameFromId(serverID))
			# s = s.extend(_getJvmHeapSize(jvm))
			log.info(_getJvmHeapSize(jvm))
			l.append(s)
			log.info(" ".join(s))
	return l		


def listHeapSizesForNode(node):
	l = []
	heaps = []
	summe = 0
	#

	for srv in lb.listAllAppServers():
		# log.info(srv)
		nodeName=srv[0]
		server=srv[1]

		if(node == nodeName or node == "all" ):
			serverID = lb.getServerId(nodeName,server)
			# log.info("ID=" + serverID)
			log.info ("get heap for node " + nodeName)
			#log.info (server)
			jvm = AdminConfig.list('JavaVirtualMachine', serverID)
			s = []
			s.append(nodeName)
			s.append(server)
			s.extend(_getJvmHeapSize(jvm))
			l.append(s)
			
			heap = _getJvmMaxHeapSize(jvm)
			# log.info(heap)
			heaps.append(_extractInt(heap))
			summe = summe + _extractInt(heap)
			# log.info(summe)
			log.info(" ".join(s))
			
	log.info("HEAPS")
	log.info(heaps)
	# s = sum(heaps)
	#log.info(s)
	return l	



def listServersForNode(node):
	l = []
	#
	for srv in lb.listAllAppServers():
		# log.info(srv)
		nodeName=srv[0]
		server=srv[1]
		if(node == nodeName):
			serverID = lb.getServerId(nodeName,server)
			l.append(server)
			log.info(" ".join(l))
	return l	


def listHeapSizesForAllNodes():
	return listHeapSizesForNode("all")

	

def getServersForCurrentHost(hostName):
	
	log.info("HOSTNAME = " + hostName)
	node = lb.findNodeOnHostname(hostName)
	log.info("NODE = " + node)
	log.info("HEAPS")
	return(listHeapSizesForNode(node))

def setJvmHeapSizes(node, serverMemoryList):
	
	serverList=[row[0] for row in serverMemoryList]
	memoryList=[row[1] for row in serverMemoryList]
	
	for i in range(len(serverList)):
		server = serverList[i]
		memory = memoryList[i]
		log.info("Setting heapsize for server %s: %d" %(server, memory))
		
		serverID = lb.getServerId(node,server)
		jvm = AdminConfig.list('JavaVirtualMachine', serverID)
		# log.info("Server " + server + " %d" %_getJvmHeapSize(jvm))
		ret = _setJvmHeapSize(jvm,memory)
		log.info (" -> ok")



	
def removeLocalEndpointSecurity(node): 


	# change endpoint security for CCM node to cell
	# delete node specific settings, so the cell wide setting will be used
        log.info ("Changing endpoint security from node to cell")
	
	try:         
	        cell=AdminControl.getCell()
		log.info("using cell name " + cell)
		adminConfig.deleteSSLConfigGroup(node, cell, "inbound")
		adminConfig.deleteSSLConfigGroup(node, cell, "outbound")
		
		## save changes
		adminConfig.saveConfig()
	except: 
		log.handleException(sys.exc_info()[:2])
		pass
	

def configureLogging(node, server): 

	## change log settings for SystemOut and SystemErr - commands from wsadminlib
	log.info ("Changing JVM log settings for new server")
	
	try: 
		lb.setServerSysout(node, server, '${SERVER_LOG_ROOT}/SystemOut.log', '10', '5')
		lb.setServerSyserr(node, server, '${SERVER_LOG_ROOT}/SystemErr.log', '10', '5')
	
		## activate verbose garbage collection
		log.info ("Activating verbose garbage collection")
		AdminTask.setJVMProperties([
			"-nodeName", node, 
			"-serverName", server, 
			"-verboseModeGarbageCollection", "true"])

		## save changes
		adminConfig.saveConfig()
	except: 
		log.handleException(sys.exc_info()[:2])
		pass

	
def setAutostartOption(node, server): 
	log.info ("Changing auto start options for server")
	
	try:
		#
		serverId = lb.getServerId(node, server)
		# print (AdminConfig.types())
		monitoringPolicy = AdminConfig.list('MonitoringPolicy',serverId)
		log.info(monitoringPolicy)
		log.info(AdminConfig.showall(monitoringPolicy))

		AdminConfig.modify(monitoringPolicy, '[[autoRestart "true"] [nodeRestartState "RUNNING"]]')
		log.info(AdminConfig.showall(monitoringPolicy))

		## save changes
		adminConfig.saveConfig()
		log.info("Done")
		
	except: 
		log.handleException(sys.exc_info()[:2])
		pass
	



def test(node, server):
	try:
			#
			serverId = lb.getServerId(node, server)
			nodeId = lb.getNodeId(node)
			
			cellname = lb.getCellName()
			cellId = lb.getCellId(cellname)
			 
			types = lb._splitlines(AdminConfig.types())
			print("-----------server ----------------")
			for type in types:
				try:
					typeObject = AdminConfig.list(type,serverId)
					if(typeObject != ""):
						print("***" +  type  + "***")
						print(AdminConfig.showall(typeObject))
				except:
					pass
			
			print("------------node -----------------")
			for type in types:
				try:
					typeObject = AdminConfig.list(type,nodeId)
					if(typeObject != ""):
						print("***" +  type  + "***")
						print(AdminConfig.showall(typeObject))
				except:
					pass

			print("------------cell -----------------")
			for type in types:
				try:
					typeObject = AdminConfig.list(type,cellId)
					if(typeObject != ""):
						print("***" +  type  + "***")
						print(AdminConfig.showall(typeObject))
				except:
					pass
			
			
			log.info("Done")
	except: 
		log.handleException(sys.exc_info()[:2])
		pass
		
	
######################################################
#
# private methods
#
######################################################

def _extractInt(s):
    try:
        return int(s)
    except ValueError:
        return int(float(s))

def _getJvmMaxHeapSize(jvm):
	ret = AdminConfig.showAttribute(jvm, "maximumHeapSize")
	return ret 



def _setJvmHeapSize(jvm, heapSize):
	heapSize = str(heapSize)
	ret = AdminConfig.modify(jvm, [['initialHeapSize', '0'], ['maximumHeapSize', heapSize]])

def _getJvmHeapSize(jvm):
	#ret = "HeapSize: -initial " + AdminConfig.showAttribute(jvm, "initialHeapSize") 
	#ret = ret  + " -max " + AdminConfig.showAttribute(jvm, "maximumHeapSize")
	heap = []
	
	heap.append(AdminConfig.showAttribute(jvm, "initialHeapSize"))
	heap.append(AdminConfig.showAttribute(jvm, "maximumHeapSize"))
	# log.info (heap)
	return heap 
	


def _usage():
	
	log.info ("this program sets basic jvm process related settings for the current node.") 
	log.info ("allowed parameters: ")
	log.info ("     parameter: hostName")
	log.info ("     parameter: systemMemory")
	log.info ("")
	log.info ("---------------------------------------")



######################################################
#
# main
#
######################################################
def main(argv):
	
	log.info("\n*** jvmAdjust ***\n")
	log.info("Available system memory passed as parameter:")
	log.info("Parse arguments from calling program")
	systemMemory = myArgs.parseArgument(argv,"systemMemory")
	log.info("systemMemory = " + systemMemory)
	
	hostName = myArgs.parseArgument(argv,"hostName")
	log.info("hostName: " + hostName)
	
	nodeName = myArgs.parseArgument(argv,"nodeName")
	log.info("nodeName: " + nodeName)
	

	if(nodeName == "" and hostName==""):
		log.error("ERROR: nodeName and hostName are missing")
		_usage()
		sys.exit(-1)
	
	if(nodeName == ""):
		nodeName = lb.findNodeOnHostname(hostName)
		log.info("node  (calculated from hostName) = " + nodeName)
	
	if(hostName== ""):
		hostName = lb.getNodeHostname(nodeName)
		log.info("host  (calculated from nodeName) = " + hostName)

	log.info("\n\n-------------------------")
	
	#
	# adjust logging and endpoint security
	
	removeLocalEndpointSecurity(nodeName)

	servers = listServersForNode(nodeName)
	for server in servers:
		configureLogging(nodeName, server)

	log.info("\n\n-------------------------")
	
	#
	# set autostart options for all servers of node
	servers = listServersForNode(nodeName)
	for server in servers:
		setAutostartOption(nodeName, server)
	log.info("\n\n-------------------------")

	
	#
	# calculate and adjust heap size
	
	if systemMemory == "": 
		log.info("ERROR: no system memory passed to program")
		_usage()
		sys.exit(-1)


	log.info("extract int value from passed systemMemory information") 
	sysMemStripped = re.match(r'\d+', systemMemory).group()
	sysMem = int(sysMemStripped)
	log.info (" read a system memory of %d (in kB)" %sysMem)	
	
	log.info("\n---------------------------")
	log.info("List all serverNames, available on this node")
	
	servers = getServersForCurrentHost(hostName)
	log.info(servers)
	serverNames=[row[1] for row in servers]
	log.info(serverNames)
	
	log.info("\n---------------------------") 
	log.info("Add memory ratio from IC tuning guide to serverlist")
	serverRatios = jvmCalc.extractServerRatioList(serverNames)
	log.info(serverRatios)

	log.info("\n---------------------------") 
	log.info("Calculate available system memory")
	availableMemory = jvmCalc.availableSystemMemory(sysMem)

	log.info("\n---------------------------")
	log.info("Calculate memory for all available Servers")  
	serverMemoryList = jvmCalc.calculateServerMemory(availableMemory,serverRatios)
	log.info(serverMemoryList)
	
	log.info("\n---------------------------") 
	log.info("Adjust JVM maxHeapSize...")
	setJvmHeapSizes(nodeName, serverMemoryList)
	adminConfig.saveConfig()
	
	log.info("\n-----------------------")
	log.info("Double check the newly set JVM maxHeapSizes ...")
	for nodeHeap in listHeapSizesForNode(nodeName):
		log.info(nodeHeap)
	
	log.info("\n-----------------------")

	log.info ("finished\n")  
	
	
	
if __name__ == "__main__":
	
	main(sys.argv)
   
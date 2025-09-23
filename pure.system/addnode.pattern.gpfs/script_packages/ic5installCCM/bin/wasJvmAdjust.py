#!/usr/bin/python
#
#
# 
# author VoDo
# GIS-AG
# created 2014-07-05

import sys
import re
import os

# sys.path.append('/opt/icinstall')
import wsadminlib as lb
import baseJvmCalc as jvmCalc
import baseArguments as myArgs
import wasAdminConfig as adminConfig

######################################################
#
# public methods
#
######################################################
def listHeapSizesForCluster(cluster):
	l = []
	print cluster
	serverIDs=lb.listServersInCluster(cluster)
	for serverID in serverIDs: 
			print serverID
			jvm = AdminConfig.list('JavaVirtualMachine', serverID)
			print (jvm)
			s = []
			s.append(lb.getNameFromId(serverID))
			print ("B")
			# s = s.extend(_getJvmHeapSize(jvm))
			print(_getJvmHeapSize(jvm))
			print ("C")
			l.append(s)
			print(" ".join(s))
	return l		

def listHeapSizesForNode(node):
	l = []
	heaps = []
	summe = 0
	#

	for srv in lb.listAllAppServers():
		# print(srv)
		nodeName=srv[0]
		server=srv[1]

		if(node == nodeName or node == "all" ):
			serverID = lb.getServerId(nodeName,server)
			# print("ID=" + serverID)
			print "get heap for node " + nodeName
			#print server
			jvm = AdminConfig.list('JavaVirtualMachine', serverID)
			s = []
			s.append(nodeName)
			s.append(server)
			s.extend(_getJvmHeapSize(jvm))
			l.append(s)
			
			heap = _getJvmMaxHeapSize(jvm)
			# print(heap)
			heaps.append(_extractInt(heap))
			summe = summe + _extractInt(heap)
			# print(summe)
			print(" ".join(s))
			
	print("HEAPS")
	print(heaps)
	# s = sum(heaps)
	#print(s)
	return l	



def listHeapSizesForAllNodes():
	return listHeapSizesForNode("all")

	

def getServersForCurrentHost(hostName):
	
	print("HOSTNAME = " + hostName)
	node = lb.findNodeOnHostname(hostName)
	print("NODE = " + node)
	print("HEAPS")
	return(listHeapSizesForNode(node))

def setJvmHeapSizes(node, serverMemoryList):
	
	serverList=[row[0] for row in serverMemoryList]
	memoryList=[row[1] for row in serverMemoryList]
	
	for i in range(len(serverList)):
		server = serverList[i]
		memory = memoryList[i]
		print("Setting heapsize for server %s: %d" %(server, memory))
		
		serverID = lb.getServerId(node,server)
		jvm = AdminConfig.list('JavaVirtualMachine', serverID)
		# print("Server " + server + " %d" %_getJvmHeapSize(jvm))
		ret = _setJvmHeapSize(jvm,memory)
		print " -> ok"

	
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
	# print heap
	return heap 
	


######################################################
#
# main
#
######################################################
def main(argv):
	
	print("\n*** jvmAdjust ***\n")
	print("Available system memory passed as parameter:")
	print("Parse arguments from calling program")
	systemMemory = myArgs.parseArgument(argv,"systemMemory")
	print("systemMemory = " + systemMemory)
	
	hostName = myArgs.parseArgument(argv,"hostName")
	print("hostName: " + hostName)
	
	nodeName = myArgs.parseArgument(argv,"nodeName")
	print("nodeName: " + nodeName)
	
	if systemMemory == "": 
		print("ERROR: no system memory passed to program")
		return


	print("extract int value from passed systemMemory information") 
	sysMemStripped = re.match(r'\d+', systemMemory).group()
	sysMem = int(sysMemStripped)
	print (" read a system memory of %d (in kB)" %sysMem)	
	
	print("\n---------------------------")
	print("List all serverNames, available on this node")
	
	"""
	# TODO: hostName comes from system properties 
	try: 
		hostName = lb.getNodeHostname(nodeName)
		print("found hostName " + hostName " from provided nodeName " + nodeName) 
	except:
		print "ERROR: no hostName found for node " + nodeName
	
		#print("!!! Using Hard Coded Hostname !!! ") 
		#nodeName = "socialNode01"
		#hostName = lb.getNodeHostname(nodeName)
	"""
	if(nodeName == "" and hostName==""):
		print("ERROR: nodeName and hostName are missing")
		return
	
	if(nodeName == ""):
		nodeName = lb.findNodeOnHostname(hostName)
		print("node  (calculated from hostName) = " + nodeName)
	
	if(hostName== ""):
		hostName = lb.getNodeHostname(nodeName)
		print("host  (calculated from nodeName) = " + hostName)

	print("\n\n-------------------------")
	
			
	servers = getServersForCurrentHost(hostName)
	print(servers)
	serverNames=[row[1] for row in servers]
	print(serverNames)
	
	print("\n---------------------------") 
	print("Add memory ratio from IC tuning guide to serverlist")
	serverRatios = jvmCalc.extractServerRatioList(serverNames)
	print(serverRatios)

	print("\n---------------------------") 
	print("Calculate available system memory")
	availableMemory = jvmCalc.availableSystemMemory(sysMem)

	print("\n---------------------------")
	print("Calculate memory for all available Servers")  
	serverMemoryList = jvmCalc.calculateServerMemory(availableMemory,serverRatios)
	print(serverMemoryList)
	
	print("\n---------------------------") 
	print("Adjust JVM maxHeapSize...")
	setJvmHeapSizes(nodeName, serverMemoryList)
	adminConfig.saveConfig()
	
	print("\n-----------------------")
	print("Double check the newly set JVM maxHeapSizes ...")
	for nodeHeap in listHeapSizesForNode(nodeName):
		print(nodeHeap)
	
	print("\n-----------------------")

	print ("\nfinished\n")  
	
	
	
if __name__ == "__main__":
	
	main(sys.argv)
   
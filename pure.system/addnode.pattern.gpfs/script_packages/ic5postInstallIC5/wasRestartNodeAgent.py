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
# restart selected  nodeagent

import sys

import wsadminlib as lb
import baseArguments as myArgs
import baseLogHandler as log


######################################################
#
# public methods
#
######################################################
def restartNodeAgent(node):

	log.info("restart node agent on  node " + node + " ... ")
	try: 
		#sync = AdminControl.completeObjectName('cell='+ cell +',node='+ node +',type=NodeSync,*')
		# AdminControl.invoke(sync , 'sync')
		na = AdminControl.queryNames("type=NodeAgent,node=" + node+ ",*")
		log.info(na)
		
		AdminControl.invoke(na,'restart','true true')
				
		# First true for the syncFirst parameter to synchronize your configuration 
		# before the command restarts the node. 
		# Second true for the restartServers parameter 
		# to restart all running servers while the command restarts the node. 
		log.info ("  ok")
	except: 
		log.handleException(sys.exc_info()[:2])
   		log.info("(ERR) not able to restart node agent on " + node)
		
	
######################################################
#
# main
#
######################################################
def main(argv):
	log.info("\n*** restartNodeAgent ***\n")
	nodeName = myArgs.parseArgument(argv,"node")
	
	if(nodeName==""):
		nodeName="all"
		log.info ("no parameter 'node'. So try to restart all node agents  ...")
	else: 
		log.info ("try to restart node agent on node  " + nodeName)
	
	# nodes = AdminTask.listNodes().splitlines()
	# exclude http and dmgr nodes
	nodes = lb.listAppServerNodes()
	for node in nodes:
		log.info("node = " + node)
		if(nodeName== node or nodeName=="all"):
			restartNodeAgent(node)
	
	log.info ("\nfinished\n")  

   
if __name__ == "__main__":
   main(sys.argv)
   
   

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
import baseArguments as myArgs
import baseLogHandler as log



######################################################
#
# public methods
#
######################################################
	
def syncNode(cell,node):
	log.info("syncing node " + node + " ... ")
	try: 
		sync = AdminControl.completeObjectName('cell='+ cell +',node='+ node +',type=NodeSync,*')
		AdminControl.invoke(sync , 'sync')	
		log.info ("  ok")
	except: 
		log.handleException(sys.exc_info()[:2])
		
		
	
######################################################
#
# main
#
######################################################
def main(argv):
	
	log.info("\n*** syncNodes ***\n")
	
	cell = AdminControl.getCell()
	log.info("cell = " + cell)
	nodeName = myArgs.parseArgument(argv,"node")
	
	if(nodeName==""):
		nodeName="all"
		log.info ("no parameter 'node'. So try to sync all WebSphere nodes ...")
	else: 
		log.info ("try to sync node " + nodeName)
	
	# nodes = AdminTask.listNodes().splitlines()
	# exclude http and dmgr nodes
	nodes = lb.listAppServerNodes()
	for node in nodes:
		log.info("node = " + node)
		if(nodeName== node or nodeName=="all"):
			syncNode(cell, node)
	
	log.info ("\nfinished\n")  

   
if __name__ == "__main__":
   main(sys.argv)
   
   

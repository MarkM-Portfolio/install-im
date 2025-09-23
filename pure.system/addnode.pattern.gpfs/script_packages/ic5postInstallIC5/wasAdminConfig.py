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
import baseLogHandler as log

######################################################
#
# public methods
#
######################################################
"""
remove endpoint security for node
"""
def deleteSSLConfigGroup(node, cell, direction):

	list=AdminTask.listSSLConfigGroups([
		"-scopeName", "(cell):" + cell + ":(node):" + node,
		"-direction", direction])
	if (list.find(node) >= 0):
		AdminTask.deleteSSLConfigGroup([
			"-name", node, 
			"-scopeName", "(cell):" + cell + ":(node):" + node, 
			"-direction", direction])

""" 
list all servers of type WEB_SERVER
"""
def listAllWebServers():

	serverType="WEB_SERVER"
	servers = []
	node_ids = AdminConfig.list( 'Node' ).splitlines()
	cellName = lb.getCellName()
	for node_id in node_ids:
		nodename = lb.getNodeName(node_id)
		serverEntries = AdminConfig.list( 'ServerEntry', node_id ).splitlines()
		for serverEntry in serverEntries:
			sName = AdminConfig.showAttribute( serverEntry, "serverName" )
			sType = AdminConfig.showAttribute( serverEntry, "serverType" )
			if serverType == sType:
				fullServerName="cell=" + cellName + ",node=" + nodename + ",server=" + sName			
				servers.append(fullServerName)
	return servers

"""
Save changes
"""
def saveConfig():
	if (AdminConfig.hasChanges()):
		log.info("")
		log.info("Save configuration in master console")
		log.info(".")
		AdminConfig.save()
	else: 
		log.info("")
		log.info("No changes to save in master console")
		log.info(".")

######################################################
#
# main
#
######################################################
def main():

  log.info(listAllWebServers())


   
if __name__ == "__main__":
   main()
   

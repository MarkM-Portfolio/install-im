#!/usr/bin/python
#
# helper library for admin config
#
# 
# author VoDo
# GIS-AG
# created 2014-06-25


execfile('/tmp/wsadminlib.py')


######################################################
#
# public methods
#
######################################################


""" 
list all servers of type WEB_SERVER
"""
def listAllWebServers():

	serverType="WEB_SERVER"
	servers = []
	node_ids = _splitlines(AdminConfig.list( 'Node' ))
	cellName = getCellName()
	for node_id in node_ids:
		nodename = getNodeName(node_id)
		serverEntries = _splitlines(AdminConfig.list( 'ServerEntry', node_id ))
		for serverEntry in serverEntries:
			sName = AdminConfig.showAttribute( serverEntry, "serverName" )
			sType = AdminConfig.showAttribute( serverEntry, "serverType" )
			if serverType == sType:
				fullServerName="cell=" + cellName + ",node=" + nodename + ",server=" + sName			
				servers.append(fullServerName)
	return servers



######################################################
#
# main
#
######################################################
def main():


  print(listAllWebServers())


   
if __name__ == "__main__":
   main()
   

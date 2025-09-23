#!/usr/bin/python
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
   

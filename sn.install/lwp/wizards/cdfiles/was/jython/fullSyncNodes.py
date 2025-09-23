# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2008, 2016                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

######################################################################
# Sync all the nodes.
######################################################################
nodes = AdminConfig.list('Node')
nodeList = nodes.splitlines()
for node in nodeList:
	nodeName = AdminConfig.showAttribute(node, 'name')
	######################################################################
	# Set the variable for node synchronization.
	######################################################################
	sync = AdminControl.completeObjectName('type=NodeSync,node='+nodeName+',*')
	
	if sync == '' :
		continue
	
	######################################################################
	# Synchronize the node by issuing the following command:
	######################################################################
	AdminControl.invoke(sync,'sync')

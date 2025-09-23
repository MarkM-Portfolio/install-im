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

cellName = AdminControl.getCell()

for clusterName in sys.argv :

	##############################################################################
	# Identify the ClusterMgr MBean and assign it to the clusterMgr variable.
	##############################################################################
	clusterMgr = AdminControl.completeObjectName('cell='+cellName+',type=ClusterMgr,*')
	
	##############################################################################
	# Refresh the list of clusters.
	##############################################################################
	AdminControl.invoke(clusterMgr, 'retrieveClusters')
	
	##############################################################################
	# Identify the Cluster MBean and assign it to the cluster variable.
	#############################################################################
	cluster = AdminControl.completeObjectName('cell='+cellName+',type=Cluster,name='+clusterName+',*')
	
	if cluster == '':
		continue
		
	##############################################################################
	# Stop cluster.
	#############################################################################
	print "stopping cluster" + cluster
	AdminControl.invoke(cluster, 'stop')
	print "finish."

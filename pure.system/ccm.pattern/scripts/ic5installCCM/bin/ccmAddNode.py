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
#
#

import sys
import time

import wasAdminConfig as adminConfig
import baseArguments as myArgs
import baseLogHandler as log
import wasPlugin as wp

execfile('bin/wsadminlib.py')

######################################################
#
# public methods
#
######################################################

def usage():
	
	log.info ("Provide the following parameters. Otherwise this job is skipped") 
	log.info ("allowed parameters: ")
	log.info ("---------------------------------------")
	log.info ("add ccm node to existing cell:")
	log.info ("     parameter: CCMnode")
	log.info ("     parameter: CCMserver")
	log.info ("     parameter: CCMcluster")
	log.info ("     parameter: DMGRpath")
	log.info ("")


######################################################
#
# main
#
######################################################
def main(argv):
        CCMnode = myArgs.parseArgument(argv,"CCMnode")
        CCMserver = myArgs.parseArgument(argv,"CCMserver")
        CCMcluster = myArgs.parseArgument(argv,"CCMcluster")
        DMGRpath = myArgs.parseArgument(argv,"DMGRpath")

        if(CCMnode== "" or CCMserver== "" or CCMcluster== "" or DMGRpath== ""):
                usage()
                sys.exit(1)

        else:
		## create new server on additional node
		log.info ("\n\nCreating additional node for CCM cluster")
		AdminTask.createClusterMember('[-clusterName ' + CCMcluster + ' -memberConfig [-memberNode ' + CCMnode + ' -memberName ' + CCMserver + ' -memberWeight 2 -genUniquePorts true -replicatorEntry false]]')
		
		## set WAS configuration as described in IC5 documentation for CCM
		log.info ("\n\nSet WAS configuration for CCM")
		configureTransactionService(CCMnode, CCMserver, '30', '60', '0', '180')
		serverId=getServerByNodeAndName(CCMnode,CCMserver)
		AdminConfig.modify(serverId, '[[provisionComponents "true"]]')

		## save changes
		log.info ("\n\nSaving WAS configuration")
		adminConfig.saveConfig()

		## do a full resync
		log.info ("\n\nPerforming a full resync")
		FRnodescope=AdminControl.queryNames('process=nodeagent,platform=common,node=' + CCMnode + ',*,type=ConfigRepository')
		AdminControl.invoke(FRnodescope, 'refreshRepositoryEpoch')
		FRdmgrscope=AdminControl.queryNames('type=CellSync,*')
		AdminControl.invoke(FRdmgrscope, 'syncNode', '[' + CCMnode + ']', '[java.lang.String]') 
		time.sleep(20)

		## Update HTTP server plugin with changes
		log.info ("\n\nUpdating WAS HTTP server plugin")
		wp.generateWebserverPlugins(DMGRpath)

		## start server on new node
		log.info ("\n\nStarting server " + CCMserver)
		SNscope=AdminControl.queryNames('process=nodeagent,platform=common,node=' + CCMnode + ',*,type=NodeAgent,mbeanIdentifier=NodeAgent')
		AdminControl.invoke(SNscope, 'launchProcess', '[' + CCMserver + ']', '[java.lang.String]') 

        log.info ("\nfinished\n")


if __name__ == "__main__":
   main(sys.argv)




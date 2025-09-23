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
import re
import os

import wsadminlib as lb
import baseJvmCalc as jvmCalc
import baseArguments as myArgs
import wasAdminConfig as adminConfig
import baseLogHandler as log

def _usage():
	
	log.info ("this program sets basic performance tuning on the dm node.") 
	log.info ("allowed parameters: ")
	log.info ("     parameter: node")
	log.info ("     parameter: settings")
	log.info ("")
	log.info ("---------------------------------------")


def modify_threadpool(node, settings):
    for srv in lb.listAllAppServers():
        # log.info(srv)
        nodeName=srv[0]
        server=srv[1]
        # log.info(server)
        if server.startswith("CCM"):
            log.info(server)	
            pool_id = AdminConfig.getid("/Server:%s/ThreadPoolManager:/ThreadPool:WebContainer/" % server)
            if pool_id == "":
                log.info("failed to locate ccm thread pool")
            attrs = []
            for k,v in settings.items():
                attrs.append([k,v])
            print "Update WebContainer Thread Pool settings: ", attrs
            AdminConfig.modify(pool_id, attrs)


def modify_datasource():
    ds_id = AdminConfig.getid('/DataSource:FNGCDDS/')
    AdminConfig.modify(ds_id, '[[statementCacheSize "100"]]')

    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find fngcdds data source")
    #log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "200"] [minConnections "75"]]')
    AdminConfig.save()
	
    ds_id = AdminConfig.getid('/DataSource:FNGCDDSXA/')
    AdminConfig.modify(ds_id, '[[statementCacheSize "100"]]')

    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find fngdcdsxa data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "75"] [minConnections "25"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:FNOSDS/')
    AdminConfig.modify(ds_id, '[[statementCacheSize "100"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:FNOSDSXA/')
    AdminConfig.modify(ds_id, '[[statementCacheSize "100"]]')
    AdminConfig.save()


######################################################
#
# main
#
######################################################
def main(argv):
	
	log.info("\n*** PerformanceTuning ***\n")
	
	threadpool = myArgs.parseArgument(argv,"threadpoolMaxSize")
	log.info("threadpool: " + threadpool)
	
	nodeName = myArgs.parseArgument(argv,"nodeName")
	log.info("nodeName: " + nodeName)
	

	if(nodeName == "" and threadpool == ""):
		log.error("ERROR: nodeName and threadpool are missing")
		_usage()
		sys.exit(-1)


	log.info("\n\n-------------------------")
	
	modify_threadpool(nodeName, {"maximumSize": threadpool})

        log.info("setting data source")

        modify_datasource()

	log.info("Performance Tuning...")
	adminConfig.saveConfig()
	
	log.info("\n-----------------------")

	log.info ("finished\n")  
	
	
	
if __name__ == "__main__":
	
	main(sys.argv)
   

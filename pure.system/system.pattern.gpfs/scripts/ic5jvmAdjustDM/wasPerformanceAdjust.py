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
        pool_id = AdminConfig.getid("/Server:%s/ThreadPoolManager:/ThreadPool:WebContainer/" % server)
        if pool_id == "":
            raise LcError, "failed to locate thread pool %s" % pool_id
        attrs = []
        for k,v in settings.items():
            attrs.append([k,v])
        print "Update WebContainer Thread Pool settings: ", attrs
        AdminConfig.modify(pool_id, attrs)
		
def modify_MessageStore():
    bus_name = 'ConnectionsBus'

    bus = AdminConfig.getid("/SIBus:%s/" % bus_name)
    members = AdminConfig.showAttribute(bus, 'busMembers')
    for member in members[1:-1].split():
        cluster_name = AdminConfig.showAttribute(member, 'cluster')
        engine = AdminTask.listSIBEngines('[-bus %s -cluster %s ]' % (bus_name, cluster_name))
        store = AdminConfig.showAttribute(engine, 'fileStore')
        attrs = [ ['minPermanentStoreSize', 500], ['maxPermanentStoreSize', 3000], ['minTemporaryStoreSize', 500], ['maxTemporaryStoreSize', 1000] ]
        AdminConfig.modify(store, attrs)
    AdminConfig.save()

def modify_datasource(db2host):
    log.info(db2host)
    ds_id = AdminConfig.getid('/DataSource:activities/')
    log.info(ds_id)
    propSet = AdminConfig.showAttribute(ds_id, 'propertySet')
    log.info(propSet)
    # AdminConfig.modify(ds_id, '[[name "clientRerouteAlternateServerName"] [type "java.lang.String"] [value db2host] [description "desc"] [required "false"]]')
    # AdminConfig.modify(ds_id, '[[name "clientRerouteAlternateServerName"] [type "java.lang.String"] [value db2host] [required "false"]]')

    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    #activity_id = AdminConfig.getid('/JDBCProvider:activitiesJDBC/')
    if pool_id == "":
        log.info("failed to find activity data source")
    #log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "50"] [minConnections "1"]]')
    AdminConfig.save()
	
    ds_id = AdminConfig.getid('/DataSource:blogs/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find blogs data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "250"] [minConnections "1"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:dogear/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find dogear data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "150"] [minConnections "1"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:communities/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find communities data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "200"] [minConnections "10"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:files/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find files data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "100"] [minConnections "10"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:forum/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find forum data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "100"] [minConnections "50"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:homepage/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find homepage data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "100"] [minConnections "20"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:metrics/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find metrics data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "75"] [minConnections "1"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:mobile/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find mobile data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "100"] [minConnections "1"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:news/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find news data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "75"] [minConnections "50"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:profiles/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find profiles data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "100"] [minConnections "1"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:search/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find search data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "75"] [minConnections "50"]]')
    AdminConfig.save()

    ds_id = AdminConfig.getid('/DataSource:wikis/')
    pool_id = AdminConfig.showAttribute(ds_id, 'connectionPool')
    if pool_id == "":
        log.info("failed to find wikis data source")
    log.info(pool_id)
    AdminConfig.modify(pool_id, '[[maxConnections "100"] [minConnections "1"]]')
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

        db2host = myArgs.parseArgument(argv,"dbhost")
	log.info("dbhost: " + db2host)

	if(nodeName == "" and threadpool==""):
		log.error("ERROR: nodeName and threadpool are missing")
		_usage()
		sys.exit(-1)


	log.info("\n\n-------------------------")
	
	modify_threadpool(nodeName,{"maximumSize": threadpool})
	
	log.info("setting message store")

        modify_MessageStore()

        log.info("setting data source")

        modify_datasource(db2host)

	log.info("Performance Tuning...")
	adminConfig.saveConfig()
	
	log.info("\n-----------------------")

	log.info ("finished\n")  
	
	
	
if __name__ == "__main__":
	
	main(sys.argv)
   

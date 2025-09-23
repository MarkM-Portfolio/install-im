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
# helper library for handling of WebServer PlugIns

import sys

import wsadminlib as lb
import wasAdminConfig as adminConfig
import baseArguments as myArgs
import baseLogHandler as log

######################################################
#
# public methods
#
######################################################

""" 
works on all managed web servers 
-generate and propagate websphere plugin to http server
-propagates keyring file to webserver
-restarts webserver

"""
def generateWebserverPlugins(dmgrPath):
	
	dmgrConfigPath= dmgrPath + "/config"
	
	cell = AdminControl.getCell()
	log.info("...cell=" + cell)
	nodes = AdminTask.listNodes().splitlines()

	for node in nodes:
		log.info ("...current node=" + node)
    		webservers = AdminTask.listServers('[-serverType WEB_SERVER -nodeName ' + node + ']').splitlines()
    		for webserver in webservers:
        		webserverName = AdminConfig.showAttribute(webserver, 'name')
		        generator = AdminControl.completeObjectName('type=PluginCfgGenerator,*')

    			log.info("Working on " + node + " " + webserverName) 
			#
			#
		        try:
				log.info ("Generating plugin-cfg.xml for " + webserverName + " on " + node)
		        	AdminControl.invoke(generator, 'generate', dmgrConfigPath + ' ' + cell + ' ' + node + ' ' + webserverName + ' false')
				log.info ("  ok")
		        except:
				log.error ("error on generating plugin-cfg.xml")
			
			#
			#
			try:
				log.info ("Propagating plugin-cfg.xml for " + webserverName + " on " + node)
			        AdminControl.invoke(generator, 'propagate', dmgrConfigPath + ' ' + cell + ' ' + node + ' ' + webserverName)
				log.info ("  ok")
		        except:
				log.error ("error on propagating plugin-cfg.xml")

			#
			# additional save is needed here
        		adminConfig.saveConfig()

			#
			#
			try:
				log.info ("Propagating keyring for " + webserverName + " on " + node)
				AdminControl.invoke(generator, 'propagateKeyring', dmgrConfigPath + ' ' + cell + ' ' + node + ' ' +webserverName)
				log.info ("  ok")
			except:
				log.error ("error on propagateKerying")
			
			#
			#
			try:
				log.info ("Stopping " + webserverName + " on " + node)
				lb.stopWebServer( node, webserverName)
				log.info ("  ok")
			except:
				log.error ("error on stop " + e)
		
			#
			#
			try:
				log.info ("Starting " + webserverName + " on " + node)
				lb.startWebServer( node, webserverName)
				log.info ("  ok")
			except:
				log.error ("error on start" + e)


	
######################################################
#
# main
#
######################################################
def main(argv):
	dmgrProfile = myArgs.parseArgument(argv,"dmgrProfile")

	if(dmgrProfile== ""):
		log.info ("missing parameter 'dmgrProfile'. This variable must point to the dmgr profile path")
		
	else: 
		generateWebserverPlugins(dmgrProfile)
	
	
	log.info ("\nfinished\n")  




if __name__ == "__main__":
   main(sys.argv)
   
   

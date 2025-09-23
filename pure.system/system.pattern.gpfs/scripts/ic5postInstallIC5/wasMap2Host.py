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

import sys
import traceback
import wsadminlib as lb
import wasAdminConfig as adminConfig
import baseLogHandler as log
import baseArguments as myArgs

######################################################
#
# public methods
#
######################################################

""" 
maps an application to any servers of type WEB_SERVER
"""

def mapAppToAllWebServers(appName):
        fullServerName=adminConfig.listAllWebServers()
        action='+'
        print(fullServerName)
        print("Mapping application '" + appName + "' to server ")
        _mapAppToServer(appName, fullServerName, action)

        adminConfig.saveConfig()



""" 
maps all avaiable applications to any servers of type WEB_SERVER
"""
def mapAllAppsToAllWebServers():
	
	servers=adminConfig.listAllWebServers()
	mapAllAppsToServer(servers)
	
""" 
unmaps all avaiable applications to any servers of type WEB_SERVER
"""
def unmapAllAppsToAllWebServers():
	
	servers=adminConfig.listAllWebServers()
	unmapAllAppsToServer(servers)


"""
"""
def mapAllAppsToServer(fullServerName):
	


	allApps=lb.listApplications()
	action='+'
	log.info(fullServerName)
	for appName in allApps:
		if (appName != "ivtApp" and appName != "query"):
			log.info("Mapping application '" + appName + "' to server ")
		  	_mapAppToServer(appName, fullServerName, action)
	
	adminConfig.saveConfig()

"""
"""
def unmapAllAppsToServer(fullServerName):
	

	allApps=lb.listApplications()
	action='-'
	log.info(fullServerName)
	for appName in allApps:
		if appName != "ivtApp" and appName != "query":
			log.info("UnMapping application '" + appName + "' to server ")
		  	_mapAppToServer(appName, fullServerName, action)
	adminConfig.saveConfig()
		
"""
"""
def mapAppToServer(appName, fullServerName):
	log.info(fullServerName)
	log.info("Mapping application '" + appName + "' to server ")
	action='+'
  	_mapAppToServer(appName, fullServerName, action)

	adminConfig.saveConfig()

	

"""
"""
def unmapAppToServer(appName, fullServerName):
	log.info("Unmapping application '" + appName + "' to server " + fullServerName)
	action='-'
	_mapAppToServer(appName, fullServerName, action)

	adminConfig.saveConfig()

######################################################
#
# private methods
#
######################################################

"""
maps or unmaps all submodules of an application to a server or cluster
the final save to masterconsole has to be done in the calling function to avoid multiple saves to the master console


action = "+" adds app to server, 
action = "-" removes module from server, 

"""
def _mapAppToServer(appName, fullServerName, action):

	try:
		# concatenate multiple servers, if fullServerName is a list
		# todo: check, if + is ok, when removing:
		serverNames = "+WebSphere:".join(fullServerName)

		app_options = ['-MapModulesToServers', [['.*', '.*', action + "WebSphere:" + serverNames]]]
		AdminApp.edit(appName, app_options)
   	except:
   		log.handleException(sys.exc_info()[:2])
		traceback.print_exc()
		pass
	else: 
		log.info("server mapping finished")


######################################################
#
# main
#
######################################################
def main(argv):


	appName = myArgs.parseArgument(argv,"appName")
	if(appName != ""): 
		mapAppToAllWebServers(appName)
	
	
	else:
		mapAllAppsToAllWebServers()

	#
	# Example of assigning an app to one server or cluster
	#
	# appName="Communities"	
	# cluster="cell=socialCell01,cluster=Cluster2"
	# httpServer="cell=socialCell01,node=webserver1,server=webserver1"
	# mapAppToServer(appName, httpServer)
	# mapAppToServer(appName, cluster)


   
if __name__ == "__main__":
   main(sys.argv)
   
   

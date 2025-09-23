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

import wsadminlib as lb
import wasAdminConfig as adminConfig
import baseLogHandler as log
import baseArguments as myArgs

######################################################
#
# public methods
#
######################################################

def setSecurityRoles(adminUser):
	
	log.info("setSecurityRoles ...")
	
	# every role entry has the format: 
	# role name, all users flag Yes or No , all authenticated flag Yes or No, user in this role, group in this role
	 
	admin = ["admin", "No", "No", adminUser, ""]
	administrator = ["administrator", "No", "No", adminUser, ""]
	allAuthenticated = ["allAuthenticated", "No", "Yes", "", ""]
	authenticated = ["authenticated", "No", "Yes", "", ""]
	bssProvisioningAdmin = ["bss-provisioning-admin", "No", "No", "", ""]

	clientManager = ["client manager", "No", "No", "", ""]
	communityCreator = ["community-creator", "No", "Yes", adminUser, ""]
	communityMetricsRun = ["community-metrics-run", "No", "Yes", adminUser, ""]
	discussThisUser = ["discussThis-user", "Yes", "No", "", ""]
	dsxAdmin = ["dsx-admin", "No", "No", adminUser, ""]	

	everyone = ["everyone","Yes","No","",""]
	everyoneNY = ["everyone", "No", "Yes", "", ""]
	
	everyoneAuthenticated = ["everyone-authenticated", "No", "Yes", "", ""]
	filesOwner = ["files-owner", "No", "Yes", "", ""]
	globalModerator = ["global-moderator", "No", "No", adminUser, ""]

	mailUser = ["mail-user", "No", "No", "", ""]
	metricsReader = ["metrics-reader", "No", "No", adminUser, ""]
	metricsReportRun = ["metrics-report-run", "No", "No", adminUser, ""]
	orgAdmin = ["org-admin", "No", "No", "", ""]
	person = ["person", "No", "Yes", "", ""]
	reader = ["reader", "No", "Yes", "", ""]
	searchAdmin = ["search-admin", "No", "No", adminUser, ""]
	searchPublicAdmin = ["search-public-admin", "No", "No", "", ""]
	shareboxReader = ["sharebox-reader", "Yes", "No", "", ""]
	trustedExternalApplication = ["trustedExternalApplication", "No", "No", "", ""]    
	widgetAdmin = ["widget-admin", "No", "No", adminUser, ""]
	wikiCreator = ["wiki-creator", "No", "Yes", "", ""] 


	# fncs 
	#OAuthClient =  ["OAuthClient", "No", "Yes", "", ""]
	#Anonymous = ["Anonymous", "No", "Yes", "", ""]
	#Authenticated =  ["Authenticated", "No", "Yes", "", ""]
	
	
	
	log.info("setting application roles")	
	try:
	
		AdminApp.edit("Activities", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, searchAdmin, widgetAdmin, admin]])
		log.debug("Activities done")
		
		AdminApp.edit("Blogs", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, admin, globalModerator, searchAdmin, widgetAdmin]])
		AdminApp.edit("Common", ["-MapRolesToUsers", [person, everyone, reader, allAuthenticated, admin, metricsReportRun, globalModerator, mailUser]])
		AdminApp.edit("Communities", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, communityCreator, communityMetricsRun, searchAdmin, globalModerator, admin, dsxAdmin, widgetAdmin]])

		AdminApp.edit("ConnectionsProxy", ["-MapRolesToUsers", [person, everyone, reader, allAuthenticated]])
		AdminApp.edit("Dogear", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, searchAdmin]])
		log.debug("Dogear done")
		
		# Extensions = no security roles
		# FileNetEngine = no security roles
		
		AdminApp.edit("Files", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, everyoneAuthenticated, filesOwner, admin, searchAdmin, widgetAdmin, globalModerator, orgAdmin ]])
		AdminApp.edit("Forums", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, discussThisUser, searchAdmin, widgetAdmin, admin, globalModerator, searchPublicAdmin ]])
		log.debug("Forums done")

		# Help = no security roles
		
		AdminApp.edit("Homepage", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, admin]])
		AdminApp.edit("Metrics", ["-MapRolesToUsers", [person, everyone, reader, everyoneAuthenticated, communityMetricsRun, admin, metricsReportRun]])
		AdminApp.edit("Mobile", ["-MapRolesToUsers", [person, everyone, reader ]])
		log.debug("Mobile done")
		
		AdminApp.edit("Mobile Administration", ["-MapRolesToUsers", [administrator, everyoneNY ]])
		AdminApp.edit("Moderation", ["-MapRolesToUsers", [reader, person, everyoneAuthenticated, globalModerator ]])
		AdminApp.edit("News", ["-MapRolesToUsers", [person, everyone, reader, shareboxReader, metricsReader, allAuthenticated, admin, searchAdmin, widgetAdmin ]])
		AdminApp.edit("Profiles", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, allAuthenticated, admin, searchAdmin, dsxAdmin]])
		log.debug("Profiles done")
		
		AdminApp.edit("PushNotification", ["-MapRolesToUsers", [person, everyone, reader, admin, everyoneAuthenticated, bssProvisioningAdmin ]])
		
		 
		AdminApp.edit("Search", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, admin, searchAdmin, everyoneAuthenticated ]])
		AdminApp.edit("WebSphereOauth20SP", ["-MapRolesToUsers", [authenticated, clientManager ]])
		AdminApp.edit("WidgetContainer", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, allAuthenticated, admin, globalModerator, mailUser, trustedExternalApplication ]])
		AdminApp.edit("Wikis", ["-MapRolesToUsers", [person, everyone, reader, metricsReader, everyoneAuthenticated, wikiCreator, admin, searchAdmin, widgetAdmin ]])
		log.debug("Wikis done")
		
		#AdminApp.edit("fncs", ["-MapRolesToUsers", [Anonymous, Authenticated, OAuthClient]])
		AdminApp.edit("oEmbed", ["-MapRolesToUsers", [person, everyone, reader, searchAdmin, widgetAdmin, everyoneAuthenticated, metricsReader, bssProvisioningAdmin ]])
		log.debug("oEmbed done")
		 	
		 
		adminConfig.saveConfig()
	except: 
   		log.error("an uxepected error occured")
   		print(sys.exc_info())
		log.handleException(sys.exc_info()[:2])
		raise

def usage():
	
	log.info ("Sets application security roles.") 
	log.info ("allowed parameters: ")
	log.info ("---------------------------------------")
	log.info ("    adminUser")
	log.info ("---------------------------------------")


######################################################
#
# private methods
#
######################################################

######################################################
#
# main
#
######################################################
def main(argv):
	
	adminUser = myArgs.parseArgument(argv,"adminUser")
	log.info("adminUser: " + adminUser)
	
	if(adminUser == ""): 
		usage()
		sys.exit(1)
	try:	
		log.info("started")
		setSecurityRoles(adminUser)
		log.info(" finished")
   	except:
   		log.error("an uxepected error occured, exit program")
   		sys.exit(1)
   	
   	
if __name__ == "__main__":
   main(sys.argv)
   
   

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
import baseArguments as myArgs
import baseLogHandler as log
from com.ibm.ws.scripting import ScriptingException

######################################################
#
# public methods
#
######################################################
"""
check if a LDAP repository exists
"""
def isLDAPRepositoryExists(id):
    try:
        AdminTask.getIdMgrRepository(['-id', id])
        is_exists = 1
    except ScriptingException, msg:
        is_exists = 0
    return is_exists

"""
add a LDAP repository, 
Can handle different types of ldaps ("AD", "DOMINO", "IDS").

 
"""
def addLDAPRepository(ldapType, ldapHost, ldapPort, ldapUser, ldapPassword, ldapSearchBase, ldapSSL, loginAttributes):
	
	log.info("* addLDAPRepository entered *")

	#
	# identify ldap type
	#
	type = ldapType.upper()
	id = type.lower()
	log.info("do configuration for LDAP type " + type)
	if(type not in ("AD", "DOMINO", "IDS")):
		return log.fatal ("ERROR:  Only LDAP type AD, DOMINO, IDS are supported, got type " + type) 

	#
	# set/identify ldap login attributes
	# 
	if (loginAttributes == ""): 
		loginProps = "uid;mail;cn"
	else: 
		loginProps = loginAttributes
	log.info("allowing the following properties as login arguments: '%s'" %loginProps)

	
	#
	#
	#		
	if (ldapSSL not in ("true", "false")):
		log.info("no SSL option, use without")
		ldapSSL = "false"		

	try: 
		#
		# first do the general config stuff which is the same for the different types of ldap, 
		# then set the repository individual settings in the second part 
		#
		if isLDAPRepositoryExists(id):
			AdminTask.deleteIdMgrRepository(['-id', id])
		print "AdminTask.createIdMgrLDAPRepository()"
		AdminTask.createIdMgrLDAPRepository([
			"-default", "true", 
			"-id", id, 
			"-adapterClassName", "com.ibm.ws.wim.adapter.ldap.LdapAdapter", 
			"-ldapServerType", type, 
			"-sslConfiguration", 
			"-certificateMapMode", "exactdn", 
			"-supportChangeLog", "none", 
			"-certificateFilter", 
			"-loginProperties", loginProps
		])
		
		#
		# when handled via admin console, the config has to be saved, before we can proceed. 
		# so we save here, too
		#
		#adminConfig.saveConfig()
			
		#
		print "AdminTask.addIdMgrLDAPServer(...)"
		AdminTask.addIdMgrLDAPServer([
			"-id", id, 
			"-host", ldapHost, 
			"-bindDN", ldapUser,
			"-bindPassword", ldapPassword,
			"-referal", "ignore",
			"-sslEnabled", ldapSSL, 
			"-ldapServerType", type,
			"-sslConfiguration",
			"-certificateMapMode", "exactdn",
			"-certificateFilter",
			"-authentication", "simple", 
			"-port", ldapPort
		])
		#adminConfig.saveConfig()
		#
		print "AdminTask.addIdMgrRepositoryBaseEntry()"
		AdminTask.addIdMgrRepositoryBaseEntry([
			"-id", id, 
			"-name", ldapSearchBase,
			"-nameInRepository", ldapSearchBase
		])
		
		#
		print "AdminTask.addIdMgrRealmBaseEntry()"
		AdminTask.addIdMgrRealmBaseEntry([
			"-name", "defaultWIMFileBasedRealm", 
			"-baseEntry", ldapSearchBase])
			
		#	
		print "AdminTask.updateIdMgrRealm()"
		AdminTask.updateIdMgrRealm([
			"-name", "defaultWIMFileBasedRealm", 
			"-allowOperationIfReposDown", "true"
		])
	
	
		# 
		# 
		# 
		if(type=="DOMINO"):
			configureDominoSettings(id)
		elif(type=="IDS"):
			configureIDSSettings(id)
		elif(type=="AD"):
			configureADSettings(id)
		else:	
			log.error("This LDAP type is not supported '" + type + "'")
			return(-1)

		
		log.info(" finished")
		adminConfig.saveConfig()
		return (0)

	except: 
		log.handleException(sys.exc_info()[:2])
		return(-1)

""" 
"""
def configureDominoSettings(id): 
	AdminTask.updateIdMgrLDAPEntityType([
		"-id", id, 
		"-name", "PersonAccount", 
		"-objectClasses", "dominoPerson", 
		"-searchBases", 
		"-searchFilter"
	])
	AdminTask.updateIdMgrLDAPEntityType([
		"-id", id, 
		"-name", "Group", 
		"-objectClasses", "dominoGroup", 
		"-searchBases", 
		"-searchFilter"
	]) 
	
def configureIDSSettings(id): 
	AdminTask.updateIdMgrLDAPEntityType([
		"-id", id, 
		"-name", "PersonAccount", 
		"-objectClasses", "inetOrgPerson", 
		"-searchBases", 
		"-searchFilter"
	])
	AdminTask.updateIdMgrLDAPEntityType([
		"-id", id, 
		"-name", "Group", 
		"-objectClasses", "groupOfNames", 
		"-searchBases", 
		"-searchFilter"
	]) 
		

def configureADSettings(id): 
	log.fatal("not implemented")
	raise("ERROR: function not implemented")


"""
"""
def findAndAddLDAPAdminUser(userLdapName, role):

	log.info("* findAndAddLDAPAdminUser entered *")
	
	try: 
		users = AdminTask.listRegistryUsers([
			"-displayAccessIds", "true", 
			"-userFilter", userLdapName
			#,"-numberOfUsers","23"
		])
		
		log.debug(users)
		userList = users.splitlines()
		log.debug (userList)
		
		log.info("found %s users in ldap for search string %s." %(len(userList), userLdapName))
		 
		if(len(userList)==0):
			log.error("no user found to add as admin user, cancel")
			return(-1)
		elif(len(userList)==1):
			log.info("do it")
			user = _extractUserFromUserSearchResult(userList[0])
			
			if(len(user)!=2):
				log.error("Found not a valid user from search: %s" %user) 
				return (-1)
			else: 	
				currentAdminUsers = listAdminUsers()
				if (user[1] in currentAdminUsers): 
					log.warn("User %s already in list of admin users, cancel" %user[1])
					return(-2)
				else: 
					if(role == ""):
						addLDAPAdminUserFromSearch(user)
					else:
						addLDAPAdminUserFromSearch(user, role)
				return(0)		
		else: # i.e. (len(userList)>1):
			log.error("found more than one user to add as admin user, cancel")
			return(-1)
			
	except: 
		log.handleException(sys.exc_info()[:2])
		return(-1)
		

def listAdminUsers():

	log.info("* listAdminUsers entered *")

	try: 
		groups = AdminTask.listUserIDsOfAuthorizationGroup()
		groups = groups[1:].split("], ")
		log.debug(groups)
		for group in groups:
			# log.debug(group)
			(key,value) = group.split("=[")
			log.debug("         --> %s=%s" %(key, value))
			if(key == "administrator"):
				log.debug(value.split(", "))
				return value.split(", ")

		"""
		all of this does not work
		users = AdminTask.listAuditUserIDsOfAuthorizationGroup()
		users2 = AdminTask.listAuditUserIDsOfAuthorizationGroup("administrator") 
		print AdminTask.listUsersForNamingRoles("administrator")
		"""
	
		log.info("   finished")
	except: 
		pass
"""
20140819, VoDo: add admin user to all wasadmin groups and not only the admin role
"""
def addLDAPAdminUserFromSearch(user):
		
	log.info("* addLDAPAdminUserFromSearch entered *")

	# role="administrator"
	roles = ["adminsecuritymanager", "administrator", "auditor", "configurator", "deployer", "iscadmins", "monitor", "operator"]

	try: 
		userFullName = user[0]
		userLdapName = user[1]
		
		for role in roles: 
			log.debug("role = '%s'" %role)
			if(role=="auditor"):
				AdminTask.mapUsersToAuditRole([
					"-accessids", userFullName, 
					"-userids", "[" + userLdapName + " ]", 
					"-roleName", role
					
				])
			else: 
				AdminTask.mapUsersToAdminRole([
					"-accessids", userFullName, 
					"-userids", "[" + userLdapName + " ]", 
					"-roleName", role
				])

		log.info("   finished")
		
		adminConfig.saveConfig()
		return (0)
	
	except: 
		log.handleException(sys.exc_info()[:2])
		return(-1)

"""
"""
def addLDAPUserToRoleFromSearch(user, role):
		
	log.info("* addLDAPUserToRoleFromSearch entered *")

	roles = ["adminsecuritymanager", "administrator", "auditor", "configurator", "deployer", "iscadmins", "monitor", "operator"]
	if (role in roles):
		try: 
			userFullName = user[0]
			userLdapName = user[1]
			if(role=="auditor"):
				AdminTask.mapUsersToAuditRole([
					"-accessids", userFullName, 
					"-userids", "[" + userLdapName + " ]", 
					"-roleName", role
					
				])
			else: 
				AdminTask.mapUsersToAdminRole([
					"-accessids", userFullName, 
					"-userids", "[" + userLdapName + " ]", 
					"-roleName", role
				])
			
			log.info("   finished")

			adminConfig.saveConfig()
			return (0)
		
		except: 
			log.handleException(sys.exc_info()[:2])
			return(-1)
	else: 
		log.error("selected admin role %s is not available." %role)
		return(-1)
	
"""
deprecated: 
this may cause inconsistent user entries, because the fullname may be incorrect, 
this is not checked by the admintask. Use "findAndAddLdapAdminUser" instead
"""
		
def addLDAPAdminUser(userFullName, userLdapName):

	log.info("* addLDAPAdminUser entered *")
	
	# role="administrator"
	roles = ["adminsecuritymanager", "administrator", "auditor", "configurator", "deployer", "iscadmins", "monitor", "operator"]

	try: 
		user = "defaultWIMFileBasedRealm/" + userFullName 
	
		# AdminTask.mapUsersToAdminRole('[-accessids [user:defaultWIMFileBasedRealm/CN=vdorna,O=GISADM ] -userids [volker.dorna@gis-ag.com ] -roleName administrator]')
		# AdminTask.listRegistryUsers('[-displayAccessIds true -userFilter *admin -numberOfUsers 23]') 
		
		for role in roles: 
			if(role=="auditor"):
				AdminTask.mapUsersToAuditRole([
					"-accessids", userFullName, 
					"-userids", "[" + userLdapName + " ]", 
					"-roleName", role
				])
			else: 
				AdminTask.mapUsersToAdminRole([
					"-accessids", userFullName, 
					"-userids", "[" + userLdapName + " ]", 
					"-roleName", role
				])
			
			log.info(" finished")
		
		adminConfig.saveConfig()
		return (0)
	
	except: 
		log.handleException(sys.exc_info()[:2])
		raise
	
		

def setSSODomain(domainName):
	
	log.info("* setSSODomain entered *")
	
	log.info("set domain to '%s'" %domainName)
	
	try: 
		ltpa = AdminConfig.list("LTPA")
		
		AdminConfig.modify(ltpa, [["singleSignon", [["requiresSSL", "false"], ["domainName", domainName], ["enabled", "true"]]]])
		
		log.debug("verify settings")	
		AdminTask.configureAdminWIMUserRegistry(["-verifyRegistry", "true"])
			
		log.info(" finished")
		
		adminConfig.saveConfig()
		return (0)
	except: 
		log.handleException(sys.exc_info()[:2])
		return(-1)
	
def usage():
	
	log.info ("this program can execute several tasks. Provide all the parameters for a task, to execute it. Otherwise this task is skipped") 
	log.info ("allowed parameters: ")
	log.info ("---------------------------------------")
	log.info ("add a new LDAPuser to the WAS admin role:")
	log.info ("     parameter: userLdapName")
	log.info ("     parameter: userRole (optional, leave blank to set all roles for user")
	log.info ("     parameter: userFullName (optional)")
	log.info ("")
	log.info ("set SSO domain:")
	log.info ("     parameter: domain \n")
	log.info ("")
	log.info ("add a new LDAP repository:")
	log.info ("     parameter: type (AD, IDS, DOMINO)")
	log.info ("     parameter: ldapHost")
	log.info ("     parameter: ldapPort")
	log.info ("     parameter: ldapUser")
	log.info ("     parameter: ldapPassword")
	log.info ("     parameter: ldapSearchBase")
	log.info ("     parameter: ldapSSL (true, false), default = 'false'")
	log.info ("     parameter: loginAttributes (default = 'uid;mail;cn'")
	log.info ("")
	log.info ("---------------------------------------")
	
######################################################
#
# private methods
#
######################################################

def _extractUserFromUserSearchResult(userFromSearch):
	
	log.info("* _extractUserFromUserSearchResult entered *")
	
	
	"""
	very ugly code ...
	sub sequent strip name and accessId for a user as returned by search
	example code:
	[[name VKahn] [accessId [user:defaultWIMFileBasedRealm/CN=Vanessa Kahn,O=GIS GmbH,C=DE]] ]
	"""
	
	name = None
	accessId = None
	
	#
	# extract starting and ending "[" "]"
	
	tempLine = userFromSearch
	if tempLine[0] != '[' or tempLine[-1] != ']':
		raise ("Invalid string: %s" %tempLine)
	
	# ... we are here: 
	# [name VKahn] [accessId [user:defaultWIMFileBasedRealm/CN=Vanessa Kahn,O=GIS GmbH,C=DE]] 
	#
	
	#
	# split line
	 
	tempList = tempLine[1:-1].split('] ')
	log.info(tempList)
	
	# ... we are here: 
	# ['[name VKahn', '[accessId [user:defaultWIMFileBasedRealm/CN=Vanessa Kahn,O=GISGmbH,C=DE]', '']
	
	
	for entry in tempList:
		if (len(entry) == 0):
			log.info ("skip line")
		else:
	    		log.info("   " + entry)
	    		if(entry.find(" ") > 0):
	    			#
	    			#
				# ... we are here: 
	    			# [name VKahn or [accessId [...]
			    	(key,val) = entry.split(" ",1)

				# ... we are here: 
	    			# name VKahn or accessId [...]
		    		(key,val) = ((key[1:],val))
				if(key == "name"):
					name = val
				if(key == "accessId"):
					#accessId = val
					# changed due to space in accessId
					accessId = "[\"" + val[1:-1] + "\"]"
				# wow, 
				# finally we have extracted name and accessId

	if(name	== None or accessId == None):
		raise ("Invalid name: %s" %userFromSearch)

	return (accessId, name)


def _testExtractUserFromUserSearchResult():
	
	a = "[[name volker.friese@jumo.net] [accessId [user:defaultWIMFileBasedRealm/CN=Volker Friese,O=GIS-Kunden]] ]"
	a = "[[name VKahn] [accessId [user:defaultWIMFileBasedRealm/CN=Vanessa Kahn,O=GIS GmbH,C=DE]] ]"
	log.info(a)

	b = _extractUserFromUserSearchResult(a)
	log.info(b)

	

def _unitTest(argv):
	
	log.info("START")
	
	_testExtractUserFromUserSearchResult()
	
	log.info("FINISHED")


######################################################
#
# main
#
######################################################
def main(argv):

	log.info("\n*** wasLDAPconfig ***\n")
	
	if (myArgs.parseArgument(argv,"test")!= ""):
		return _unitTest(argv)
		
		
	# userFullName = myArgs.parseArgument(argv,"userFullName")
	userLdapName = myArgs.parseArgument(argv,"userLdapName")
	userRole =  myArgs.parseArgument(argv,"userRole")
	
	domain = myArgs.parseArgument(argv,"domain")
	
	ldapType = myArgs.parseArgument(argv,"ldapType")
	ldapHost = myArgs.parseArgument(argv,"ldapHost")
	ldapPort = myArgs.parseArgument(argv,"ldapPort")
	ldapUser = myArgs.parseArgument(argv,"ldapUser")
	ldapPassword = myArgs.parseArgument(argv,"ldapPassword")
	ldapSSL = myArgs.parseArgument(argv,"ldapSSL")
	loginAttributes = myArgs.parseArgument(argv, "loginAttributes")
	ldapSearchBase = myArgs.parseArgument(argv, "ldapSearchBase")
	print "* " * 40
	print "*                 START RUNNING"
	print "* " * 40
	
		
	"""
	userFullName="CN=vdorna,O=GISADM"
	userLdapName="volker.dorna@gis-ag.com"
	
	domain="gish.de"
	
	type="DOMINO"
	ldapHost="ldap.gish.de"
	ldapPort="389"
	ldapUser="cn=wpgbind,o=gisadm"
	ldapPassword="xxx"
	"""
	
	isActionDone = 0
	isSuccess = 0
	
	if(userLdapName != ""):
		"""
		if(userFullName== ""):
		""" 
		# listAdminUsers()
		
		ret = findAndAddLDAPAdminUser(userLdapName, userRole)
		if(ret !=0):
			isSuccess=ret
		
		isActionDone = 1
		
		"""
		else:
		ret = addLDAPAdminUser(userFullName, userLdapName)
		if(ret !=0):
			isSuccess=ret
		isActionDone = 1
		"""	

	if(domain!= ""):
		ret = setSSODomain(domain)
		if(ret !=0):
			isSuccess=ret
		isActionDone = 2
	
	if(ldapType!="" and ldapHost!="" and ldapPort!="" and ldapUser!="" and ldapPassword!="" and ldapSearchBase!=""):
		if(ldapSSL==""):
			ldapSSL="false"
		
		ret = addLDAPRepository(ldapType, ldapHost, ldapPort, ldapUser, ldapPassword, ldapSearchBase, ldapSSL, loginAttributes)
		if(ret !=0):
			isSuccess=ret
		isActionDone = 3


	log.info ("\nfinished\n")  
	
	if(isActionDone < 1):
		usage()
	if(isSuccess != 0): 
		log.info("WARN: finished with return code %s" %isSuccess)
		sys.exit(isSuccess)
						
	
if (__name__ == "__main__"):
	main(sys.argv)
	
	


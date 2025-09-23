#!/usr/bin/python
#
# script to install Connections on an additional node 
#
# 
# author VoDo
# GIS-AG
# created 2014-07-01

#
# TODOS: 
# logging
# handle exceptions of os commands 


import basePropertiesHandler as bp
import baseStrings as bs
import os
import logging
import baseLogConfig as logConf

"""
delete existing WAS profiles
and remove files under Profiles folder
"""
def deleteLocalWASProfiles(profileName, profileRoot, wasInstallRoot, wasUsername):
	logging.info("deleteLocalWASProfiles")

	baseCommand=wasInstallRoot + "/bin/manageprofiles.sh"
	
	#
	#
	logging.info("delete all existing profiles ...")
	
	delAll=baseCommand + " -deleteAll"
	command = "su - " + wasUsername + " -c '" + delAll + "'"
	logging.info("will execute os command: " + command)
	ret = os.system(command)

	if(ret == 0):
		# TODO: VALIDATION !!!
		logging.info("deleting default profile folders... ") 
		if ("" != profileRoot):
			command2 = "rm -r " + profileRoot + "/DefaultCustom01"
			logging.info("will execute os command: " + command2)
			ret = os.system(command2)
			if(ret == 0): 
				if ("" != profileName):
					logging.info("deleting existing node profile folders... ") 
					command3 = "rm -r " + profileRoot + "/" + profileName
					logging.info("will execute os command: " + command3)
					ret = os.system(command3)
	
	logging.info("return value = %d" %(ret))
	return ret
	
	
"""
create a new WAS profile
"""
def createLocalWASProfile(profileName, nodeName, localUser, localPassword, profileRoot, wasInstallRoot, hostName, cellName, wasUsername):
	
	logging.info("createLocalWASProfile")

	baseCommand=wasInstallRoot + "/bin/manageprofiles.sh"
	# baseCommand="echo"
	
	#
	#
	logging.info("create new profile '" + profileName + "' ...")

	create= baseCommand + " -create"
	createParams=""
	createParams = createParams + " -profileName " + profileName
	
	createParams = createParams + " -profilePath " + profileRoot + "/"  + profileName
	createParams = createParams + " -templatePath " + wasInstallRoot + "/profileTemplates/default"
	createParams = createParams + " -hostName " + hostName
	createParams = createParams + " -nodeName " + nodeName
	createParams = createParams + " -cellName " + cellName
	createParams = createParams + " -enableAdminSecurity true"
	createParams = createParams + " -adminUserName " + localUser
	createParams = createParams + " -adminPassword " + localPassword

	command = create + createParams
	command = "su - " + wasUsername + " -c '" + command + "'"
	logging.info("will execute os command: " + command)
	ret = os.system(command)
	
	logging.info("return value = %d" %(ret))
	return ret

"""
add node into dmgr:
"""
def addNodeToDmgr(profileName, dmgrHostName, dmgrSOAPPort, dmgrUser, dmgrPassword, localUser, localPassword, profileRoot, wasInstallRoot, wasUsername):
	
	logging.info("add node to dmgr ...")
	
	addNode= profileRoot + "/"  + profileName + "/bin/addNode.sh"

	addNodeParams=[]
	addNodeParams.extend([dmgrHostName,dmgrSOAPPort])
	addNodeParams.extend(["-username",dmgrUser])
	addNodeParams.extend(["-password",dmgrPassword])
	addNodeParams.extend(["-localusername",localUser])
	addNodeParams.extend(["-localpassword",localPassword])
	
	command = addNode + " " + " ".join(addNodeParams)
	command = "su - " + wasUsername + " -c '"+ command + "'"
	logging.info("will execute os command: " + command)
	ret = os.system(command)
	logging.info("return value = %d" %(ret))
	return ret
	


"""
execute wsadmin script
"""
def executeWsadminSciptOnRemoteHost(fileName, hostName, soapPort, user, password, params, wasUsername):
	
	logging.info("execute wsadmin script " + fileName)
	command = "/opt/IBM/WebSphere/AppServer/bin/wsadmin.sh -host "+ hostName + " -port " + soapPort +" -lang jython -username " + user + " -password " + password + " -f " + fileName + " " + params
	command = "su - " + wasUsername + " -c '"+ command + "'"
	logging.info("will execute os command: " + command)
	ret = os.system(command)
	logging.info("return value = %d" %(ret))
	return ret 


######################################################
#
# main
#
######################################################

def main():
	
	dmgrHostName=os.environ.get("dmgrHost", "")
	dmgrSOAPPort=os.environ.get("dmgrSOAPPort", "8879")	
	dmgrUser=os.environ.get("WAS_USERNAME", "")	
	localUser=os.environ.get("WAS_USERNAME", "")
	dmgrPassword=os.environ.get("WAS_PASSWORD", "")	
	localPassword=os.environ.get("WAS_PASSWORD", "")	
	
	
	profileRoot=os.environ.get("PROFILE_ROOT", "")
	wasInstallRoot=os.environ.get("WAS_INSTALL_ROOT", "")

	hostName=os.environ.get("HOSTNAME", "")
	cellName=os.environ.get("CELL_NAME", "")
	

	#
	shortHostName= bs.shortHostName(hostName)
	profileName="ICprofile_" + shortHostName
	nodeName= shortHostName + "Node"
	
	#
	ret = deleteLocalWASProfiles(profileName, profileRoot, wasInstallRoot, localUser)
	
	#
	ret = createLocalWASProfile(profileName, nodeName, localUser, localPassword, profileRoot, wasInstallRoot, hostName, cellName, localUser)
	
	#
	ret = addNodeToDmgr(profileName, dmgrHostName, dmgrSOAPPort, dmgrUser, dmgrPassword, localUser, localPassword,  profileRoot, wasInstallRoot, localUser)
	
	#
	fileName = "/opt/icinstall/wasManageClusterMembers.py"
	params = "hostName=" + hostName
	ret = executeWsadminSciptOnRemoteHost(fileName, dmgrHostName, dmgrSOAPPort, dmgrUser, dmgrPassword, params, localUser)


if __name__ == "__main__":
   main()
   
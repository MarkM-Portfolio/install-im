# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2010, 2016                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

# 5724-S68                                                          
# Check the WebSphere Application's  security 

# waltzConfig.py
#
#  About:
#      This script will try to check whether VMM and Waltz are enabled on existing FileNet Server,
#      If exists, will try to create the J2C alias for FileNet
#
#  Usage:
#     wsadmin.sh -username system \
#                -password password \
#                -lang jython \
#                -f /wkplc_GetDMInfo.py c:/temp/result.log
#
#  Parameters:
#      arg1 -the file pato to store security result
#
#
#-------------------------------------------------------------------
import sys, java, string
from types import *
from java import lang
from java import util
from java import io
lineSeparator = java.lang.System.getProperty('line.separator')

CEAppName = "@CEAppName@"
tempFile = "@tempFile@"
serverName = "@ServerName@"
clusterNameStrings = "@ClusterName@"

def checkJVMPropStandalone():
	#--------------------------------------------------------------
	# set up globals
	#--------------------------------------------------------------
	global AdminConfig
	
	nodeNameStrings = AdminApplication.getAppDeployedNodes(CEAppName)
	if(len(nodeNameStrings) != 0):
		for nodeName in nodeNameStrings:
			print "nodeName: "+ nodeName
			print "serverName:"+ serverName
			if(len(serverName) == 0):
				print "FileNet is using Cluster!"
				return -2
			else:
				jvmArgs = AdminTask.showJVMProperties(["-nodeName", nodeName, "-serverName", serverName, "-propertyName", "genericJvmArguments"])
				print "JVM Properties for "+serverName+" is "+jvmArgs
				if(string.find(jvmArgs, "-Dibm.filenet.security.vmmProvider.waltzImpl=true") == -1 or string.find(jvmArgs, "-Dcom.ibm.connections.directory.services.j2ee.security.principal") == -1 or string.find(jvmArgs, "-DenableWaltzIdConversion=true") == -1 or string.find(jvmArgs, "-Dibm.filenet.security.connectionsProvider.disableRecursiveParentCall=true") == -1):
					print "Please make sure you have these JVM Properties set for "+serverName+" is not set"
					print "-Dibm.filenet.security.vmmProvider.waltzImpl=true"
					print "-Dcom.ibm.connections.directory.services.j2ee.security.principal=<login property>"
					print "-DenableWaltzIdConversion=true"
					print "-Dibm.filenet.security.connectionsProvider.disableRecursiveParentCall=true"
					print "Waltz is not enabled for "+serverName
					return -1
	

def checkJVMPropCluster():
	#--------------------------------------------------------------
	# set up globals
	#--------------------------------------------------------------
	global AdminConfig
	
	nodeNameStrings = AdminApplication.getAppDeployedNodes(CEAppName)
	if(len(clusterNameStrings) == 0):
		print "If FileNet is using Cluster, please input the cluster name in the build.properties file!"
	else:
		clusterNameList = clusterNameStrings.split(",")
		if(len(nodeNameStrings) != 0):
			for nodeName in nodeNameStrings:
				print "nodeName = "+nodeName
				nodeServersList1 = AdminServerManagement.listServers("APPLICATION_SERVER", nodeName)
				if(len(nodeServersList1) != 0):
					for clusterName in clusterNameList:
						print "clusterName = "+clusterName
						nodeServersList2 = AdminClusterManagement.listClusterMembers(clusterName)
						if(len(nodeServersList2) != 0):
							for server1 in nodeServersList1:
								nodeServer=server1.split('(')
								serverName1=nodeServer[0]
								for server2 in nodeServersList2:
									clusterServer=server2.split('(')
									serverName2=clusterServer[0]
									if(serverName1 == serverName2):
										print "serverName:"+ serverName
										jvmArgs = AdminTask.showJVMProperties(["-nodeName", nodeName, "-serverName", serverName2, "-propertyName", "genericJvmArguments"])
										print "JVM Properties for "+serverName+" is "+jvmArgs
										if(string.find(jvmArgs, "-Dibm.filenet.security.vmmProvider.waltzImpl=true") == -1 or string.find(jvmArgs, "-Dcom.ibm.connections.directory.services.j2ee.security.principal") == -1 or string.find(jvmArgs, "-DenableWaltzIdConversion=true") == -1 or string.find(jvmArgs, "-Dibm.filenet.security.connectionsProvider.disableRecursiveParentCall=true") == -1):
											print "Please make sure you have these JVM Properties set for "+serverName+" is not set"
											print "-Dibm.filenet.security.vmmProvider.waltzImpl=true"
											print "-Dcom.ibm.connections.directory.services.j2ee.security.principal=<login propeerty>"
											print "-DenableWaltzIdConversion=true"
											print "-Dibm.filenet.security.connectionsProvider.disableRecursiveParentCall=true"
											print "Waltz is not enabled for "+serverName
											return -1

def output(tempFile):
	try:
		fo = java.io.FileOutputStream(tempFile)
		pw = java.io.PrintWriter(fo)
		pw.write("Waltz Enabled= -1"+"\n")
		pw.close()
	except:
		print "GetDMInfo: Error -- IO error"
	return
							
def loadProperties (source):
	""" Load a Java properties file into a Dictionary. """
	result = {}
	try:
		if type(source) == type(''): # name provided, use file
			source = io.FileInputStream(source)
			bis = io.BufferedInputStream(source)
			props = util.Properties()
			props.load(bis)
			bis.close()
			for key in props.keySet().iterator():
				result[key] = props.get(key)
	except java.io.FileNotFoundException, e:
		print "File not found: " + source
		print ""
	return result

def getProperty(props, key):
	val = props.get(key)
	if val == None:
		print "Error, the property '" + key + "' can not be null"
		print ""
	return val

def createWaltzJAASAuth():
	print ""
	print "----------------------------------------------------------"
	print " Install Script for Waltz JAAS Authentication Entry       "
	print " (C) Copyright IBM Corporation, 2006, 2012                "
	print "----------------------------------------------------------"
	argc = len(sys.argv)
	global AdminConfig
	global AdminApp

	cell = AdminControl.getCell()
   	node = AdminControl.getNode()

	propFile = "build.properties"
	installProps = loadProperties(propFile)

	aliasName = getProperty(installProps, "alias")
	userid = getProperty(installProps, "userid")
	password = getProperty(installProps, "password")
   
	description = "JAAS Alias for Inter-Service Communication"
   
	authEntriesString=AdminConfig.list("JAASAuthData")
	entries=authEntriesString[0:len(authEntriesString)].split(lineSeparator)
	for entry in entries:
		if len(entry) > 0:
			alias = AdminConfig.showAttribute(entry, "alias")
			if alias == aliasName:
				print ""
				print "Removing old auth alias..."
   				AdminConfig.remove(entry)

	print ""
	print "Creating JAASAuthData..."
	security = AdminConfig.getid("/Cell:"+cell+"/Security:/")
	aliasAttr = ["alias", aliasName]
	useridAttr = ["userId", userid]
	passwordAttr = ["password", password]
	descAttr = ["description", description]
	jaasAttrs = [aliasAttr, useridAttr, passwordAttr, descAttr]
	authdata = AdminConfig.create("JAASAuthData", security, jaasAttrs)
	AdminConfig.save()

	print ""
	print "JAASAuthData created successfully"
	print ""	

#-------------------------------------------
# Main  - just execute the creation function
#-------------------------------------------
if(checkJVMPropStandalone() == -2):
	if(checkJVMPropCluster() == -1):
		output(tempFile)
		print "Please enable Waltz and using VMM for FileNet first!"
	else:
		createWaltzJAASAuth()
elif(checkJVMPropStandalone() == -1):
	output(tempFile)
	print "Please enable Waltz and using VMM for FileNet first!"
else:
	createWaltzJAASAuth()
print ""

	
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
# Update Generic JVM Arguments for CCM Servers

# updateJVMArgs.py
#
#  About:
#      This script will remove JVM args "-ibm.filenet.security.vmmProvider.waltzImpl=true" from the CCM servers
#
#  Usage:
#     wsadmin.sh -username system \
#                -password password \
#                -lang jython \
#                -f /updateJVMArgs.py -servers "server1,server2,server3"
#
#
#
#-------------------------------------------------------------------

import sys, java, string
lineSeparator = java.lang.System.getProperty('line.separator')

servers = ""

#######################################################################################
# Define General Functions			                                                  #
#######################################################################################

def readArgs(argList):
    args = {}
    for arg in argList:
        argument = arg.strip()
        if argument[0] == '-':
            # arg starts with '-' so its a property and next arg should be a value
            property = arg[1:]
            getValue = 1
            args[property] = property
        else:
            args[property] = argument

    return args
#endDef

# Parse command line
args = readArgs(sys.argv)

if args.has_key("servers"):
	servers = args["servers"]
	print "Servers passed in are "+servers
else:
	print "No servers specified"

global AdminConfig

serverNamesList = servers.split(",")
cellname = AdminControl.getCell()
cellId = AdminConfig.getid('/Cell:'+cellname+'/')
nodeNameStrings = AdminConfig.list('Node', cellId)
if(len(nodeNameStrings) != 0):
	nodeList = nodeNameStrings.split(lineSeparator)
	for node in nodeList:
		nodeName = AdminConfig.showAttribute(node, "name")
		nodeServersString = AdminConfig.list('Server', node)
		for serverName in serverNamesList:   
			if(len(nodeServersString) != 0):
				nodeServerList = nodeServersString.split(lineSeparator)
				for nodeServer in nodeServerList:
					nodeServerName = AdminConfig.showAttribute(nodeServer, "name")
					if(nodeServerName==serverName):
						jvmArgs = AdminTask.showJVMProperties(["-nodeName", nodeName, "-serverName", serverName, "-propertyName", "genericJvmArguments"])
						print "Original generic JVM arguments: "+ jvmArgs
						print ""
						
						targetArg = "-Dibm.filenet.security.vmmProvider.waltzImpl=true"
						args = string.split(jvmArgs)
						newJvmArgs=""
						for arg in args:
							if(arg != targetArg):
								newJvmArgs +=arg + " "								
						AdminTask.setGenericJVMArguments("[-nodeName "+ nodeName +" -serverName "+ serverName +" -genericJvmArguments '"+ newJvmArgs +"']")
						print "Saving generic JVM arguments on node ["+ nodeName +"], server ["+ serverName +"]"
						AdminConfig.save()
						print "Generic JVM arguments saved"
print "updateJVMArgs.py - end"

 



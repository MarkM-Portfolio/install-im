# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2008, 2016                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

varNode = sys.argv[0]
varServer = sys.argv[1]

serverInfo = "/Node:" + varNode + "/Server:" + varServer + "/"

print serverInfo

serverId = AdminConfig.getid(serverInfo)

print serverId

AdminConfig.remove(serverId)

AdminConfig.save()

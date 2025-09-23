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

# wkplc_GetDMInfo.py
#
#  About:
#      This script will try to connect to Websphere Applicaiton Deployment Manager Server to get cluster list information
#      And store the result to a specified file.
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
import sys, java
lineSeparator = java.lang.System.getProperty('line.separator')

def outPutDMInfo(filePath):
   """out put the app security value to a file
   
      Return none."""
   # use AdminControl but not AdminConfig to get cell info
   cellname = AdminControl.getCell()
   # get the cluster info
   clusterNameStrs = getClusterInfo(cellname)
   nodeNameStrs = getNodesInfo(cellname)
   nodeAgents = detectNodeAgents(cellname)
   nodeServersStrs = getNodeServers(cellname);
   nodeHostNameStrs = getNodeHostName(cellname)
   # get the heap size info
   nodename = AdminControl.getNode()
   dmcfgid = AdminControl.getConfigId('node='+nodename+',type=Server,processType=DeploymentManager,*')
   jvm = AdminConfig.list("JavaVirtualMachine", dmcfgid).split(lineSeparator)[0]
   heapSize = AdminConfig.showAttribute(jvm,"maximumHeapSize")
   heapSizeTooLow='false'
   if(int(heapSize)<512):
      heapSizeTooLow='true'
   print "Get DM Info: Info - WebSphere DM cell name:"+cellname
   print "Get DM Info: Info - WebSphere DM heap size:"+heapSize
   print "Get DM Info: Info - Cluster list from DM:"+clusterNameStrs
   print "Get DM Info: Info - DM node:"+nodename
   print "Get DM Info: Info - Nodes list from DM:"+nodeNameStrs
   print "Get DM Info: Info - Node Agent list from DM:"+nodeAgents
   print "Get DM Info: Info - Nodes Host Name list from DM:"+nodeHostNameStrs
   print "Get DM Info: Info - Nodes Server Name list from DM:"+nodeServersStrs
   print "Get DM Info: Info - Writing result to file:"+filePath

   roleEnough = "true"
   try:
     AdminTask.listUserIDsOfAuthorizationGroup()     
   except:
     roleEnough = "false"

   try:
       fo = java.io.FileOutputStream(filePath)
       pw = java.io.PrintWriter(fo)
       pw.write("dm.cellanme="+cellname+"\n")
       pw.write("dm.heapsize="+heapSize+"\n")
       pw.write("dm.heapSizeTooLow="+heapSizeTooLow+"\n")
       pw.write("dm.nodename="+nodename+"\n")
       pw.write("dm.nodes="+nodeNameStrs+"\n")
       pw.write("dm.node.agents="+nodeAgents+"\n")
       pw.write("dm.nodes.hostname="+nodeHostNameStrs+"\n")
       pw.write("dm.nodes.servername="+nodeServersStrs+"\n")
       pw.write("dm.clusterlist="+clusterNameStrs+"\n")
       pw.write("dm.userRoleEnough="+roleEnough)
       pw.close()
   except:
      print "GetDMInfo: Error -- IO error"
   return



def getClusterInfo(cellname):
   """get the cluster information from dm
   
      Return cluster information."""
   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   
   global AdminConfig
   cellId = AdminConfig.getid('/Cell:'+cellname+'/')
   clusterNameStrings = AdminConfig.list('ServerCluster', cellId)
   outputStr = ''
   if(len(clusterNameStrings) != 0):
       clusterList = clusterNameStrings.split(lineSeparator)
       for cluster in clusterList:
           clusterName = AdminConfig.showAttribute(cluster, "name")
           clusterMemberString = AdminConfig.list('ClusterMember', cluster)
           # get the cluster members from the cluster
           members = ''
           if(len(clusterMemberString) != 0):
              clusterMemberList = clusterMemberString.split(lineSeparator)
              for clustermember in clusterMemberList:
                 clusterNodeName = AdminConfig.showAttribute(clustermember, "nodeName")
                 clusterMemberName = AdminConfig.showAttribute(clustermember, "memberName")
		 nodeServer = clusterNodeName + "#" + clusterMemberName;
                 members += nodeServer + ","
           outputStr += clusterName +":" + members +";"
   return outputStr

def getNodesInfo(cellname):
   """get the nodes information from cell
   
      Return nodes information."""
   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   
   global AdminConfig
   cellId = AdminConfig.getid('/Cell:'+cellname+'/')
   nodename = AdminControl.getNode();
   nodeNameStrings = AdminConfig.list('Node', cellId)
   managedNodeStrings = AdminTask.listManagedNodes()
   managedNodeList = managedNodeStrings.split(lineSeparator)
   outputStr = ''
   if(len(nodeNameStrings) != 0):
       nodeList = nodeNameStrings.split(lineSeparator)
       for node in nodeList:
           nodeName = AdminConfig.showAttribute(node, "name")
           for managedNode in managedNodeList:
               if(nodeName==managedNode):
                   outputStr += nodeName +","
   return outputStr

def detectNodeAgents(cellname):
   """detect if nodeagent is started
   
      Return nodeagent information."""
   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   
   global AdminConfig
   cellId = AdminConfig.getid('/Cell:'+cellname+'/')
   nodename = AdminControl.getNode();
   nodeNameStrings = AdminConfig.list('Node', cellId)
   managedNodeStrings = AdminTask.listManagedNodes()
   managedNodeList = managedNodeStrings.split(lineSeparator)
   outputStr = ''
   if(len(managedNodeList) != 0):
       for node in managedNodeList:
           started = 'false'
           servs = AdminControl.queryNames('type=Server,cell=' + cellname + ',node=' + node + ',*').split()
           for server in servs:
               sname = AdminControl.getAttribute(server, 'name')
               ptype = AdminControl.getAttribute(server, 'processType')
               pid   = AdminControl.getAttribute(server, 'pid')
               state = AdminControl.getAttribute(server, 'state')
               jvm = AdminControl.queryNames('type=JVM,cell=' + cellname +',node=' + node + ',process=' + sname + ',*')
               osname = AdminControl.invoke(jvm, 'getProperty', 'os.name')
               print " " + sname + " " +  ptype + " has pid " + pid + ";state: " + state + "; on " + osname + "\n"
               if(sname == 'nodeagent' and ptype == 'NodeAgent' and state == 'STARTED'):
                   started = 'true'
           outputStr += node + ':' + started + ';'
   return outputStr

def getNodeHostName(cellname):
   """get the Node Host Name from dm
   
      Return Node Host Name."""

   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   
   global AdminConfig
   cellId = AdminConfig.getid('/Cell:'+cellname+'/')
   nodename = AdminControl.getNode();
   nodeNameStrings = AdminConfig.list('Node', cellId)
   managedNodeStrings = AdminTask.listManagedNodes()
   managedNodeList = managedNodeStrings.split(lineSeparator)
   outputStr = ''
   if(len(nodeNameStrings) != 0):
       nodeList = nodeNameStrings.split(lineSeparator)
       for node in nodeList:
           nodeName = AdminConfig.showAttribute(node, "name")
           nodeHost = AdminConfig.showAttribute(node, "hostName")
           for managedNode in managedNodeList:
               if(nodeName==managedNode):
                    outputStr += nodeName + ":" + nodeHost +";"
          
   return outputStr
   
def getNodeServers(cellname):
   """get the Node Host Name from dm
   
      Return Node Host Name."""

   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   
   global AdminConfig
   cellId = AdminConfig.getid('/Cell:'+cellname+'/')
   nodename = AdminControl.getNode();
   nodeNameStrings = AdminConfig.list('Node', cellId)
   managedNodeStrings = AdminTask.listManagedNodes()
   managedNodeList = managedNodeStrings.split(lineSeparator)
   outputStr = ''
   if(len(nodeNameStrings) != 0):
       nodeList = nodeNameStrings.split(lineSeparator)
       for node in nodeList:
           nodeName = AdminConfig.showAttribute(node, "name")
           nodeServersString = AdminConfig.list('Server', node);
           for managedNode in managedNodeList:
               outputStr += nodeName + ":"   
               if(len(nodeServersString) != 0):
                       nodeServerList = nodeServersString.split(lineSeparator)
                       for nodeServer in nodeServerList:
                           if(nodeName==managedNode):
                               outputStr += nodeServer + ","
               
               outputStr += ";"                         
   return outputStr   

#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
print "Check DM Info: Info - Starting"
if (len(sys.argv) != 1):
   print "Check DM Info: Error - This script requires a parameter: output file path"
   print "e.g.:   wkplc_GetDMInfo.py c:/temp/result.log"
else:
   filePath = sys.argv[0]
   outPutDMInfo(filePath)
print "Check DM Info: Info - Finished"

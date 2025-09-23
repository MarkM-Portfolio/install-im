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

# wkplc_GetNodeInfo.py
#
#  About:
#      This script will try to connect to Websphere Applicaiton Server to get Node information
#      And store the result to a specified file.
#
#  Usage:
#     wsadmin.sh -username system \
#                -password password \
#                -lang jython \
#                -f /wkplc_GetNodeInfo.py c:/temp/result.log
#
#  Parameters:
#      arg1 -the file pato to store security result
#
#
#-------------------------------------------------------------------
import sys, java
lineSeparator = java.lang.System.getProperty('line.separator')

def outPutNodeInfo(filePath):
   
   # use AdminControl but not AdminConfig to get cell info
   cellname = AdminControl.getCell()
   nodename = AdminControl.getNode()
   print "Get Node Info: Info - WebSphere cell name:"+cellname
   print "Get Node Info: Info - node:"+nodename
   print "Get Node Info: Info - Writing result to file:"+filePath
   
   try:
       fo = java.io.FileOutputStream(filePath)
       pw = java.io.PrintWriter(fo)
       pw.write("local.node.cellname="+cellname+"\n")
       pw.write("local.node.nodename="+nodename+"\n")
       pw.close()
   except:
      print "GetNodeInfo: Error -- IO error"
   return

#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
print "Check DM Info: Info - Starting"
if (len(sys.argv) != 1):
   print "Check Node Info: Error - This script requires a parameter: output file path"
   print "e.g.:   wkplc_GetNodeInfo.py c:/temp/result.log"
else:
   filePath = sys.argv[0]
   outPutNodeInfo(filePath)
print "Check Node Info: Info - Finished"

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
   print "Get DM Info: Info - WebSphere DM cell name:"+cellname
   
   try:
       fo = java.io.FileOutputStream(filePath)
       pw = java.io.PrintWriter(fo)
       pw.write("dm.cellanme="+cellname+"\n")
       pw.close()
   except:
      print "GetDMInfo: Error -- IO error"
   return


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

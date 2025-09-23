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

# wkplc_CheckAppSecurity.py
#
#  About:
#      This script will check Websphere Applicaiton Server's Application Security
#      And store the result to a specified file.
#
#  Usage:
#     wsadmin.sh -username system \
#                -password password \
#                -lang jython \
#                -f /wkplc_CheckAppSecurity.py c:/temp/result.log
#
#  Parameters:
#      arg1 -the file pato to store security result
#
#
#-------------------------------------------------------------------
import sys, java

def outPutAppSecurity(filePath):
   """out put the app security value to a file
   
      Return none."""
   appSecurity = getAppSecurity()
   adminSecurity = getAdminSecurity()
   java2Security = getJava2Security()
   print "Check Application Security: Info - WebSphere Application Security is "+appSecurity
   print "Check Application Security: Info - Writing result to file "+filePath
   try:
       fo = java.io.FileOutputStream(filePath);
       pw = java.io.PrintWriter(fo);
       pw.write("was.appsecurity="+appSecurity+"\n");
       pw.write("was.adminsecurity="+adminSecurity+"\n");
       pw.write("was.java2security="+java2Security);
       pw.close();
   except:
      print "CheckAppSecurity: Error -- IO error"
   return

def getAppSecurity():
   """get the application security value from was admin config
   
      Return application security."""
   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   global AdminConfig
   appSecurity = AdminConfig.showAttribute(AdminConfig.list("Security"),"appEnabled")
   return appSecurity

def getAdminSecurity():
   """get the administrator security value from was admin config
   
      Return application security."""
   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   global AdminConfig
   adminSecurity = AdminConfig.showAttribute(AdminConfig.list("Security"),"enabled")
   return adminSecurity

def getJava2Security():
   """get the Java2 security value from was admin config
   
      Return Java2 security."""
   #--------------------------------------------------------------
   # set up globals
   #--------------------------------------------------------------
   global AdminConfig
   java2Security = AdminConfig.showAttribute(AdminConfig.list("Security"), "enforceJava2Security")
   return java2Security

#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
print "Check Application Security: Info - Starting"
if (len(sys.argv) != 1):
   print "Check Application Security: Error - This script requires a parameter: output file path"
   print "e.g.:   wkplc_CheckAppSecurity.py c:/temp/result.log"
else:
   filePath = sys.argv[0]
   outPutAppSecurity(filePath)
print "Check Application Security: Info - Finished"

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

# Check the WebSphere Application's  installed features list 

# lc_GetinstalledFeatures.py
#
#  About:
#      This script will try to connect to Websphere Applicaiton Server to get installed features list information
#      And store the result to a specified file.
#
#  Usage:
#     wsadmin.sh -username system \
#                -password password \
#                -lang jython \
#                -f /lc_GetinstalledFeatures.py c:/temp/result.log
#
#  Parameters:
#      arg1 -the file pato to store security result
#
#
#-------------------------------------------------------------------
import sys, java
lineSeparator = java.lang.System.getProperty('line.separator')

def outPutInstalledFeatures(filePath):
   """out put the installed features to a file
   
      Return none."""
   # try to locate the entry first
   entries = AdminApp.list().split(lineSeparator)
   installedFeatures = 'installedFeatures='
   for entry in entries :
       if(entry == 'Activities'):
          installedFeatures = installedFeatures + 'activities,'
       elif(entry == 'Blogs'):
          installedFeatures = installedFeatures + 'blogs,'
       elif(entry == 'Communities'):
          installedFeatures = installedFeatures + 'communities,'
       elif(entry == 'Dogear'):
          installedFeatures = installedFeatures + 'dogear,'
       elif(entry == 'Profiles'):
          installedFeatures = installedFeatures + 'profiles,'
       elif(entry == 'Homepage'):
          installedFeatures = installedFeatures + 'homepage,'
       elif(entry == 'News'):
          installedFeatures = installedFeatures + 'news,'
       elif(entry == 'Search'):
          installedFeatures = installedFeatures + 'search,'
       elif(entry == 'Files'):
          installedFeatures = installedFeatures + 'files,'
       elif(entry == 'Wikis'):
          installedFeatures = installedFeatures + 'wikis,'
       elif(entry == 'Mobile'):
          installedFeatures = installedFeatures + 'mobile,'
       elif(entry == 'Forum'):
          installedFeatures = installedFeatures + 'forum,'
       elif(entry == 'Moderation'):
          installedFeatures = installedFeatures + 'moderation,'
       elif(entry == 'Metrics'):
          installedFeatures = installedFeatures + 'metrics,'		  
	   
   installedFeatures = installedFeatures[0:len(installedFeatures)-1]
   print "Get App Info: Info - "+installedFeatures
   print "Get App Info: Info - Writing result to file:"+filePath
   try:
       fo = java.io.FileOutputStream(filePath)
       pw = java.io.PrintWriter(fo)
       pw.write(installedFeatures)
       pw.close()
   except:
      print "GetDMInfo: Error -- IO error"
   return

#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
print "Get App Info: Info - Starting"
if (len(sys.argv) != 1):
   print "Get App Info: Error - This script requires a parameter: output file path"
   print "e.g.:   wkplc_GetinstalledFeatures.py c:/temp/result.log"
else:
   filePath = sys.argv[0]
   outPutInstalledFeatures(filePath)
print "Get App Info: Info - Finished"

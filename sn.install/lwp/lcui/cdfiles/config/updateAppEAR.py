# ***************************************************************** 
#                                                                   
# HCL Confidential                                                 
#                                                                  
# OCO Source Materials                                            
#                                            
# Copyright HCL Technologies Limited 2021                    
#                                                                   
# US Government Users Restricted Rights - Use, duplication or       
# disclosure restricted by GSA ADP Schedule Contract with           
# IBM Corp.                                                         
#                                                                   
# ***************************************************************** 
# command-line syntax: earName=sys.argv[0] , earFile=sys.argv[1]

import os, sys, jarray

earName = sys.argv[0]
earFile = sys.argv[1]
print "updateAppEAR earName=" + earName
print "updateAppEAR earFile=" + earFile

updateOptions = '[ -operation update -contents %s -update.ignore.new ]' % earFile
print "updateAppEAR updateOptions=" + updateOptions

print "Running: AdminApp.update(%s, 'app', %s)" % (earName, updateOptions)
AdminApp.update(earName, 'app', updateOptions)

AdminConfig.save()

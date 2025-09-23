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
# command-line syntax: appName=sys.argv[0] , exportFileName=sys.argv[1]

import os, sys

appName = sys.argv[0]
exportFileName = sys.argv[1]
print "exportAppEAR appName=" + appName
print "exportAppEAR exportFileName=" + exportFileName

AdminApp.export(appName, exportFileName)


# ***************************************************************** 
#                                                                   
# Licensed Materials - Property of IBM                              
#                                                                   
# 5724-S68                                                          
#                                                                   
# Copyright IBM Corp. 2010, 2013  All Rights Reserved.            
#                                                                   
# US Government Users Restricted Rights - Use, duplication or       
# disclosure restricted by GSA ADP Schedule Contract with           
# IBM Corp.                                                         
#                                                                   
# ***************************************************************** 
#TODO Parse commandline, e.g. earName=sys.argv[0] , earFile=sys.argv[1], 

import os, sys, jarray

earName = sys.argv[0]
earFile = '\'' + sys.argv[1] +'\''
print "earName=" + earName
print "earFile=" + earFile

AdminApplication.updatePartialAppToAnAppWithUpdateCommand(earName , earFile, 'app_patch')

AdminConfig.save()

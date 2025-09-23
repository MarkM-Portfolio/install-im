#!/usr/bin/python
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2010, 2014                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 
#
#


import sys
import re
import os

import wsadminlib as lb
import baseArguments as myArgs
import baseLogHandler as log
from com.filenet.api.admin import ServerCacheConfiguration
from com.filenet.api.core import Connection
from com.filenet.api.core import Factory
from com.filenet.api.core import Domain
from javax.security.auth import Subject
from com.filenet.api.util import UserContext
from com.filenet.api.collection import SubsystemConfigurationList
from com.filenet.api.constants import RefreshMode

def _usage():
	
	log.info ("this program sets CE maximal cache.") 
	log.info ("allowed parameters: ")
	log.info ("     parameter: CEURI")
	log.info ("     parameter: WASUsername")
	log.info ("     parameter: WASPassword")
	log.info ("")



######################################################
#
# main
#
######################################################
def main(argv):

    log.info("\n*** PerformanceTuning CE Cache ***\n")
    CEURI = myArgs.parseArgument(argv,"CEURI")
    WASUsername = myArgs.parseArgument(argv,"WASUsername")
    WASPassword = myArgs.parseArgument(argv,"WASPassword")

    if(CEURI=="" or WASUsername=="" or WASPassword==""):
        usage()
        sys.exit(1)

    else:
        print "CEURI=", CEURI
        print "WASUsername=", WASUsername
        print "WASPassword", WASPassword
        conn = Factory.Connection.getConnection(CEURI)
        subj = UserContext.createSubject(conn, WASUsername, WASPassword, "FileNetP8WSI")
        uc = UserContext.get()
        uc.pushSubject(subj)
        domain = Factory.Domain.fetchInstance(conn, "ICDomain", None)
        scList = domain.get_SubsystemConfigurations()
        for i in range(len(scList)):
            if(isinstance(scList.get(i), ServerCacheConfiguration)):
                scc = scList.get(i)
                print i
                print scc
                scc.set_PrincipalCacheMaxEntries(98304)
        domain.save(RefreshMode.REFRESH)

    log.info("\n-----------------------")

    log.info ("finished\n")  
	
	
	
if __name__ == "__main__":
	
	main(sys.argv)
   

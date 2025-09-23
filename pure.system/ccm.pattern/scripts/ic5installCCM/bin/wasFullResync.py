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
import time

import baseLogHandler as log

execfile('bin/wsadminlib.py')

######################################################
#
# main
#
######################################################
def main(argv):
	## do a full resync
	FRnodescopes=AdminControl.queryNames('*:*,type=ConfigRepository,process=nodeagent').splitlines()
	for FRnodescope in FRnodescopes:
		AdminControl.invoke(FRnodescope, 'refreshRepositoryEpoch')
	FRdmgrscope=AdminControl.queryNames('type=CellSync,*')
	FRnodeList=listAppServerNodes()
	for FRnode in FRnodeList:
		res = AdminControl.invoke(FRdmgrscope, 'syncNode', '[' + FRnode + ']', '[java.lang.String]') 
		log.info("syncNode called ["+FRnode +"]: "+ res)
	time.sleep(900)

        log.info ("\nfinished\n")


if __name__ == "__main__":
   main(sys.argv)




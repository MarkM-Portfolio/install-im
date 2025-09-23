#!/usr/bin/python
#
# 
# GIS-AG
# created 2014-08-01
# version 1.0.0
#
# history:
# 2014-08-01: initial version
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
		AdminControl.invoke(FRdmgrscope, 'syncNode', '[' + FRnode + ']', '[java.lang.String]') 
	time.sleep(20)

        log.info ("\nfinished\n")


if __name__ == "__main__":
   main(sys.argv)




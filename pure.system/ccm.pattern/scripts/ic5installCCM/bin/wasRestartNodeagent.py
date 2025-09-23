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

#import baseLogHandler as log

#execfile('bin/wsadminlib.py')

######################################################
#
# main
#
######################################################
def main(argv):
	## do restart node agent
	nodes = AdminConfig.list('Node')
	nodeList = nodes.split(lineSeparator)
	for nodeName in nodeList:
       	    if nodeName.find('CustNode') != -1:
	        print 'Restart nodeAgent ', nodeName
	        node_name = AdminConfig.showAttribute(nodeName, 'name')
	        na = AdminControl.queryNames('type=NodeAgent,node=' + node_name + ',*') 
	        AdminControl.invoke(na,'restart','true true')
               	print 'Restart nodeAgent finished', nodeName
	    if nodeName.find('CCMNode') != -1:
	        print 'Restart nodeAgent ', nodeName
	        node_name = AdminConfig.showAttribute(nodeName, 'name')
	        na = AdminControl.queryNames('type=NodeAgent,node=' + node_name + ',*') 
	        AdminControl.invoke(na,'restart','true true')
               	print 'Restart nodeAgent finished', nodeName


if __name__ == "__main__":
   main(sys.argv)


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

cellName = sys.argv[0]
nodeName = sys.argv[1]
serverName = sys.argv[2]
runAsUserName = sys.argv[3]
runAsGroupName = sys.argv[4]

s1 = AdminConfig.getid('/Cell:'+cellName+'/Node:'+nodeName+'/Server:'+serverName+'/')
print 'Server id: '+s1
processDef = AdminConfig.list('JavaProcessDef', s1)
print 'Process def: '+processDef
#processDef = AdminConfig.showAttribute(s1, 'processDefinition')
print 'Modifying process def:'
print '  runAsUser: '+runAsUserName
print '  runAsGroup: '+runAsGroupName
AdminConfig.modify(processDef, [['execution', [['runAsUser', runAsUserName],['runAsGroup', runAsGroupName]]]])
print 'Saving configuration...'
AdminConfig.save()
print '... saved; synching...'
Sync1 = AdminControl.completeObjectName('type=NodeSync,node='+nodeName+',*')
AdminControl.invoke(Sync1, 'sync')
print '... synching complete'



'''
Created on 2013-08-16

@author: psp
'''

import os, sys, jarray
from com.ibm.websphere.management.application.commands import EARExpander

#TODO Parse commandline, e.g. earFile=sys.argv[0], operationDir=sys.argv[1] ...
earFile = sys.argv[0]
operationDir = sys.argv[1]

operation = 'expand'
expansionFlags = 'all'

# Not sure if java.lang.System would be sufficient here
#os.environ['org.eclipse.jst.j2ee.commonarchivecore.openAllArchives'] = 'true'
java.lang.System.setProperty('org.eclipse.jst.j2ee.commonarchivecore.openAllArchives', 'true')

# Create Java String Array with args
args=jarray.array(['-ear', earFile, '-operation', operation, '-operationDir', operationDir, '-expansionFlags', expansionFlags], java.lang.String)

# Invoke
EARExpander.main(args)

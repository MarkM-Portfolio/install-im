#
#*===================================================================
#*
# Licensed Materials - Property of IBM  
# "Restricted Materials of IBM"
# 5725-G32, 5725-F46  Copyright IBM Corp., 2013, 2013
# All Rights Reserved * Licensed Materials - Property of IBM
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
#*
#*===================================================================
#

import subprocess
import sys
import os
import platform
def runShellQuick(cmd):
    
    
        p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (out,err) = p.communicate()

        rc = p.returncode

        if rc > 0:
            raise Exception("The command: %s, had a return code of %d, not 0" % (str(cmd), rc))
            
        return out  #This is the stdout from the shell command

cmd="hostname"
    
if platform.system()=="Linux":
    cmd="hostname -f "+sys.argv[1]

out=runShellQuick (cmd)
out=str(out).replace('b', '').replace('\\n', '').replace("'", "")
print(out)

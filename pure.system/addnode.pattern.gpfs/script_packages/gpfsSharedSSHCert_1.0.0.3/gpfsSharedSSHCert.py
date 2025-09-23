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

import os
import subprocess
import shutil
import platform

print("Set Platform .ssh directory")
sshDir=""
if platform.system()=="AIX":
    sshDir="/.ssh/"
if platform.system()=="Linux":
    sshDir="/root/.ssh/"


print("Verify file permissions")
os.chmod('/tmp/gpfs/gpfsSharedSSHCert/id_rsa', 0o600)

print("Copy Public / Private Certificates")
shutil.copy2('/tmp/gpfs/gpfsSharedSSHCert/id_rsa', sshDir + 'id_rsa')
shutil.copy2('/tmp/gpfs/gpfsSharedSSHCert/id_rsa.pub', sshDir + 'id_rsa.pub')

print("Merge system and pre-defined authorized_keys file")
templateFile = open('/tmp/gpfs/gpfsSharedSSHCert/authorized_keys.template', 'r')
templateContents = templateFile.read()
templateFile.close()
masterfile = open(sshDir + 'authorized_keys', 'a+')
masterfile.write(templateContents)
masterfile.close()

print("Disable strict host key checking in ssh")
shutil.copy2('/tmp/gpfs/gpfsSharedSSHCert/config', sshDir + 'config')



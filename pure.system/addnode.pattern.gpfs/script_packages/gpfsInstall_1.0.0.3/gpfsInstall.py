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
import rpm
import sys

####################################################
print "System GFPS Prereqs"
print "Update Firewall Rules"

print "Open ssh port and ping icmp ports for mmgetstate"
try:
	subprocess.call(["python", "/tmp/gpfs/gpfsInstall/gpfs.py", "-operationName", "openSSHPort"])
except:
	print "Failed to open ssh port."
####################################################

####################################################
print "Install GFPS Prereqs"

def checkRPM(rpm):
	return subprocess.call(["rpm", "-q", rpm])

if checkRPM("gcc-4.4.6-4.el6.x86_64")==0:
	subprocess.call(["rpm", "-ivh", "libstdc++-devel-4.4.6-4.el6.x86_64.rpm", "gcc-c++-4.4.6-4.el6.x86_64.rpm"])
elif checkRPM("gcc-4.4.7-3.el6.x86_64")==0:
	subprocess.call(["rpm", "-ivh", "gcc-c++-4.4.7-3.el6.x86_64.rpm", "libstdc++-devel-4.4.7-3.el6.x86_64.rpm"])
elif checkRPM("gcc-4.4.6-3.el6.x86_64")==0:
	subprocess.call(["rpm", "-ivh", "gcc-c++-4.4.6-3.el6.x86_64.rpm", "libstdc++-devel-4.4.6-3.el6.x86_64.rpm"])
elif checkRPM("gcc-4.4.5-6.el6.x86_64")==0:
	subprocess.call(["rpm", "-ivh", "gcc-c++-4.4.5-6.el6.x86_64.rpm", "libstdc++-devel-4.4.5-6.el6.x86_64.rpm"])
else:
	print "Unknown Version"
	sys.exit(1)
####################################################

####################################################
print "Install GFPS"
try:
	subprocess.call(["rpm", "-ivh", "gpfs.msg.en_US-3.5.0-0.noarch.rpm", "gpfs.base-3.5.0-0.x86_64.rpm", "gpfs.docs-3.5.0-0.noarch.rpm", "gpfs.gpl-3.5.0-0.noarch.rpm"])
except:
	print "Install GPFS FAILED"
####################################################

####################################################
print "Update GFPS"	
try:
	subprocess.call(["rpm", "-Uvh", "gpfs.msg.en_US-3.5.0-17.noarch.rpm", "gpfs.base-3.5.0-17.x86_64.update.rpm", "gpfs.docs-3.5.0-17.noarch.rpm", "gpfs.gpl-3.5.0-17.noarch.rpm"])
except:
	print "UpdateGPFS FAILED"
####################################################

####################################################
print "Build GFPS portability"	
try:
	os.chdir("/usr/lpp/mmfs/src")
	subprocess.call(["make", "Autoconfig"])
	subprocess.call(["make", "World"])
	subprocess.call(["make", "InstallImages"])
except:
	print "Build GFPS portability FAILED"  
####################################################

####################################################
print "Create GPFS dump directory"

dump = os.path.dirname("/tmp/mmfs/")

if not os.path.exists(dump):
	os.makedirs(dump)
####################################################
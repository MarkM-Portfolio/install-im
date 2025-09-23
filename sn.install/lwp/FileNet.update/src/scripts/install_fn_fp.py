# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2010, 2016
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

#
# run this from wsadmin
#   sudo /opt/IBM/WebSphere/bin/wsadmin.py -lang jython \
#        -user xxx -password yyy -f install_fn.py options_file binaries_dir
#
import java
import sys, traceback
import re

# to workaround the issue that the os module in WAS 6.1 doesn't know
# Windows Server 2003 is a NT type OS
windows_os = ["Windows .*"]
for os_pattern in windows_os:
    regex_obj = re.compile(os_pattern)
    os_name = java.lang.System.getProperty("os.name")
    if regex_obj.match(os_name):
        sys.registry.setProperty('python.os', 'nt')
        break

import os
import java.lang.System as javasys

lineSeparator = javasys.getProperty('line.separator')
cells = AdminConfig.list('Cell').split()
for cell in cells:
    nodes = AdminTask.listManagedNodes().split()
    for node in nodes:
        print node
        servs = AdminControl.completeObjectName('type=Server,processType=NodeAgent,node=' + node + ',*').split()
        if len(servs) != 1:
            print "Node [%s] is not started yet." % node
            print "Please ensure all Node Agents startup before deploy IBM Content Navigator."
            sys.exit(99)

def usage():
    print "Usage:"
    print "  wsadmin -javaoption \"-Dpython.path=lib\" -wsadmin_classpath lib/lccfg.jar \\"
    print "          -f bin/configure_fn.py options_file was_home was_profile_name was_profile_path"
    print ""
    print "  options_file -  is a piece of Python/Jython script contains the install"
    print "                  options, such as server names, passwords, etc."
    print "                  see examples in samples directory"
    print "  was_home -  directory where WAS is installed"
    print "  was_profile_name - profile name where Connections is installed (AppSrv01)"
    print "  was_profile_path - WAS profile path"
    print ""
    sys.exit(1)

filenet_binaries = {
    'ce': {
        'exits': [0],
        'failureMsg': 'Failed to install FileNet Content Plantform Engine FixPack, Please check the log for detail of the problem.',
        'successMsg': 'FileNet Content Platform Engine has been installed successfully.'},
    'ceclient': {
        'exits': [0],
        'failureMsg': 'Failed to install FileNet Content Platform Engine Client FixPack, Please check the log for detail of the problem.',
        'successMsg': 'FileNet Content Platform Engine Client has been installed successfully.'},
    'fncs': {
        'exits': {'aix': [0,208], 'windows': [0,2000], 'zlinux': [0], 'linux': [0]},
        'failureMsg': 'Failed to install FileNet Content Navigator FixPack, Please check the log for detail of the problem.',
        'successMsg': 'FileNet Content Navigator has been installed successfully.'},
    }

def setup_names():
    osname = sys.registry['os.name'].lower()
    if osname.find("windows") == 0: osname = "windows"
    if osname == "linux" and sys.registry['os.arch'] == "s390x": osname = "zlinux"
    ostype = osname
    if osname in ['linux', 'zlinux', 'aix']:
        ostype = "unix"
    return osname

def fn_fp_install(component,cmdline,osname,batfilename):
    if osname == "windows":
        cmdline = os.path.join(cmdline, 'FileNet.update', 'scripts', batfilename)
    print "running command: ", cmdline
    result = os.system(cmdline)
    print "Exit code:", result
    return result

fp_result = 0

try:
    comp = sys.argv[0]
    cmd = sys.argv[1]
    osname = setup_names()
    # for winodws platform, update-ce|fncs-ceclient.bat only path Connections home path, not a full file path
    # because path is broken, have to get a full path by join
    if osname == "windows":
        batname = sys.argv[2]
    else:
        batname = ""
    fp_result = fn_fp_install(comp,cmd,osname,batname)
except:
    print "ERROR occurred while installing FileNet fix pack."
    print "-" * 60
    traceback.print_exc()
    print "-" * 60
    sys.exit(fp_result)

if comp == "fncs":
    accepted_exit_codes = filenet_binaries[comp]['exits'][osname]
else:
    accepted_exit_codes = filenet_binaries[comp]['exits']

print "accepted_exit_codes: ", accepted_exit_codes
if fp_result in accepted_exit_codes:
    print filenet_binaries[comp]['successMsg']
    sys.exit(0)
else:
    print filenet_binaries[comp]['failureMsg']
    sys.exit(-1)

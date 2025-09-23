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
import os
import shutil

#sys.path.append(os.path.join(os.environ['MY_HOME'],"lib"))

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

def copy_fn_configfiles(conn_home):
    config_files = [ "CCM.cfgp",
        "applicationserver.xml",
        "configurefncstask.xml",
        "configureldap.xml",
        "deployapplication.xml",
        "downloadcejarstask.xml",
        "importltpakey.xml",
        "rebuildear.xml" ]

    #Copy backup fncsConfigFiles to profile directory
    for cfg_file in config_files:
        src_file = os.path.join(conn_home, "FileNet_backup", "fncs", "CCM", cfg_file)
        dst_file = os.path.join(conn_home, "FNCS", "configure", "profiles", "CCM", cfg_file)
        shutil.copyfile(src_file, dst_file)

try:
    conn_home = sys.argv[0]
    copy_fn_configfiles(conn_home)
except:
    print "ERROR occurred while copying FNCS configuration files."
    print "-" * 60
    traceback.print_exc()
    print "-" * 60

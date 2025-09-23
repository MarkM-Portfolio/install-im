# *****************************************************************
#
# IBM Confidential
#
# OCO Source Materials
#
# Copyright IBM Corp. 2016
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

import java
import sys, traceback
import re
import os
import shutil
from java.util import Properties

# to workaround the issue that the os module in WAS 6.1 doesn't know
# Windows Server 2003 is a NT type OS
windows_os = ["Windows .*"]
for os_pattern in windows_os:
    regex_obj = re.compile(os_pattern)
    os_name = java.lang.System.getProperty("os.name")
    if regex_obj.match(os_name):
        sys.registry.setProperty('python.os', 'nt')
        break


def fetch_sso():
    dir= os.environ['Connections_HOME']
    print dir
    cfg_file="cfg.py"
    cfg_update="cfg_update.py"
    cfg_path=os.path.join(dir,cfg_file)
    cfgupdate_path=os.path.join(dir,cfg_update)
    sso_attrs = AdminTask.getSingleSignon()
    #print sso_attrs
    domain_start = sso_attrs.index('domainName')
    domain_end = sso_attrs.index(']', domain_start)
    domain_space = sso_attrs.index(' ', domain_start)
    domain_name = sso_attrs[domain_space+1:domain_end]
    #print domain_name

    try:
        fin = open(cfg_path, 'rt')
        fout = open(cfgupdate_path, "wt")

        for line in fin.readlines():
            pos_at = line.find("ssoDomain")
            if pos_at >= 0:
                print "fetch_sso found: %s" % line
                if domain_name:
                    newline = "\"ssoDomain\": \"%s\"," % domain_name
                else:
                    newline = line
                print "fetch_sso replace with: %s" % newline
                fout.write(newline)
                fout.write("\n")
            else:
                fout.write(line)

        fin.close()
        fout.close()
        shutil.copyfile(cfgupdate_path, cfg_path)
    except IOError:
        print "IOError when read cfg file "


try:
    fetch_sso()
except:
    print "ERROR occurred while fetch the configuration files."
    print "-" * 60
    traceback.print_exc()
    print "-" * 60

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

sys.path.append(os.path.join(os.environ['MY_HOME'],"lib"))

import ccm
import lcutils

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


def dump_install_options(opts):
    for key,value in opts.items():
        if key.endswith("Password"):
            value = "*" * 8
        #print "%25s : %s" % (key, value)

def validate_apps_selection(app_options):
    errors = []
    warnings = []
    required_apps = ['ccm']
    for app_name in required_apps:
        if not apps_options.has_key(app_name):
            warnings.append("[%s] is required but not specified to install, force added" % app_name)
            apps_options[app_name] = {}
    return (errors, warnings)


# note that the wsadmin Jython environment is different with the normal
# Jython, it does not include the script itself, so the sys.argv[0] is
# the first argument rather the name of our script itself.
print sys.argv
if (len(sys.argv) != 2):
    usage()

print "Loading options from file: ", sys.argv[0]
common_options = {}
apps_options = {}
if os.path.isfile(sys.argv[0]):
    execfile(sys.argv[0])
else:
    print "Error - %s not exist or is not a file." % sys.argv[0]
    usage()

print "options loaded"

action = sys.argv[1]

validate_apps_selection(apps_options)
options = common_options.copy()
options.update(apps_options["ccm"])
dump_install_options(options)

was_profile_path = java.lang.System.getProperty("user.install.root")
was_profile = os.path.basename(os.path.abspath(was_profile_path))
was_home = os.environ['WAS_HOME']
home_dir = os.environ['MY_HOME']
print "Using: was_profile_path =", was_profile_path
print "Using: was_profile =", was_profile
print "Using: was_home =", was_home
print "Using: home_dir =", home_dir

set_anonymous = os.environ['set_fn_anonymous']
if set_anonymous == "y":
    anonymous_user = os.environ['fn_anonymous']
    anonymous_password = os.environ['fn_anonymous_password']
    options['filenetAnonymousUser'] = anonymous_user
    options['filenetAnonymousPassword'] = anonymous_password
else:
    options['filenetAnonymousUser'] = ""
    options['filenetAnonymousPassword'] = ""

for d in [was_home, was_profile_path, home_dir]:
    if not os.path.isdir(d):
        print "Error - %s is not a directory or does not exist." % d
        usage()

import java.lang.System as javasys

try:
    ccm_app = ccm.CCM("xkit", None)
    ccm_app.apply_install_options(options)
    ccm_app.kit_dir = os.path.join(home_dir, 'xkit')

    if action == "deploy_ce":
        ccm_app.uninstall_ce_app()
        AdminConfig.save()
        print "deploying filenet ear in WAS ..."
        ccm_app.upgrade_ce("update")
        AdminConfig.save()
    if action == "deploy_fncs":
        ccm_app.uninstall_fncs_app()
        AdminConfig.save()
        print "deploying filenet ear in WAS ..."
        ccm_app.upgrade_fncs("update")
        AdminConfig.save()
    if action == "config_ce":
        ccm_app.patch_ce_with_new_waltz()
        ccm_app.map_to_web_servers()
    if action == "config_fncs":
        ccm_app.apply_fncs_filter()
        ccm_app.map_fncs_roles()
        ccm_app.map_to_web_servers()
    if action == "rollbackce":
        print "uninstalling filenet ear in WAS ..."
        ccm_app.uninstall_ce_app()
        AdminConfig.save()
        print "deploying filenet ear in WAS ..."
        ccm_app.upgrade_ce("rollback")
        AdminConfig.save()
    if action == "rollbackfncs":
        lineSeparator = javasys.getProperty('line.separator')
        cells = AdminConfig.list('Cell').split()
        for cell in cells:
            nodes = AdminTask.listManagedNodes().split()
            for node in nodes:
                print node
                servs = AdminControl.completeObjectName('type=Server,processType=NodeAgent,node=' + node + ',*').split()
                if len(servs) != 1:
                    print ""
                    print "Please ensure all Node Agents startup before deploy IBM Content Navigator."
                    print "IBM Content Navigator aborted."
                    sys.exit(99)

        ccm_app.uninstall_fncs_app()
        AdminConfig.save()
        print "deploying filenet ear in WAS ..."
        ccm_app.upgrade_fncs("rollback")
        AdminConfig.save()

    AdminConfig.save()
except:
    print "ERROR occurred while installing FileNet applications"
    print "-" * 60
    traceback.print_exc()
    print "-" * 60
    sys.exit(1)

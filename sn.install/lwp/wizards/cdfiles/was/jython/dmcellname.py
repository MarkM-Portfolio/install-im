# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2008, 2016                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************
import re
from java.lang import System
lineSeparator = System.getProperty("line.separator")
p = re.compile("^(\\w+)\\(cells/(.+)/clusters/(\\w+)\\|.*")

print "[[" + AdminControl.getCell() + "]]"
members = AdminConfig.list("ClusterMember").split(lineSeparator)
for member in members :
    print member

members = AdminConfig.list("ClusterMember").split(lineSeparator)
for member in members :
    m = p.match(member)
    if (m) :
        clusterName = m.group(3)
        nodeName = AdminConfig.showAttribute(member, "nodeName")
        print "[" + clusterName + "] = [" + nodeName + "]"

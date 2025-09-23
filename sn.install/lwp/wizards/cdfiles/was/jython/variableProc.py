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

from java.lang import System
lineSeparator = System.getProperty("line.separator")
availableAction = ["list", "creat", "modify", "remove"]

def printUsage() :
    print
    print "Usage: cellName[,NodeName[,ServerName]] list|create|modify|remove [varName[,varValue[,varDescription]]] [DMcellName]"
    print
    sys.exit(-1)

if (len(sys.argv) <2) :
    printUsage()

scope = sys.argv[0].split(",")
action = sys.argv[1]

if action != "create" and action != "modify" and action != "remove" and action != "list" :
    printUsage()

if (action == "create" or action=="modify" or action=="remove") and len(sys.argv)<3 :
    printUsage()

varName = None
varValue = ""
varDesc = ""
dmCellName = None    
if action != "list" :
    # create or modify
    params = sys.argv[2].split(",")

    for i in range(0, len(params)) :
        if i==0 :
            varName =  params[0]
        if i==1 :
            varValue = params[1]
        if i==2 :
            varDesc = params[2]
    if len(sys.argv)>3 :
        dmCellName = sys.argv[3]
else :
    if len(sys.argv)>2 :
        dmCellName = sys.argv[2]
    

varValue = varValue.replace("\\", "/")

scopeStr = None
for i in range(0, len(scope)) :
    if i==0 :
        scopeStr =  "/Cell:" + scope[i] + "/"
    if i==1 :
        scopeStr = scopeStr + "Node:" + scope[i] + "/"
    if i==2 :
        scopeStr = scopeStr + "Server:" + scope[i] + "/"
parentId = AdminConfig.getid(scopeStr)

# if local cell name fails, try dmCellName
if ((parentId == "" or not parentId) and dmCellName) :
    scopeStr = None
    for i in range(0, len(scope)) :
        if i==0 :
            scopeStr =  "/Cell:" + dmCellName + "/"
        if i==1 :
            scopeStr = scopeStr + "Node:" + scope[i] + "/"
        if i==2 :
            scopeStr = scopeStr + "Server:" + scope[i] + "/"  
    parentId = AdminConfig.getid(scopeStr)     

if (parentId == "" or not parentId) :
    print "ERROR: Cannot locate the specified scope!"
    sys.exit(-1)

entryParentId = AdminConfig.getid(scopeStr + "VariableMap:/")

# try to locate the entry first
varExists = None
entries = AdminConfig.list("VariableSubstitutionEntry", entryParentId).split(lineSeparator)
for entry in entries :
    vName = AdminConfig.showAttribute(entry, "symbolicName")
    vValue = AdminConfig.showAttribute(entry, "value")
    if not vValue :
        vValue = ""
    print "[" + vName + "] = [" + vValue + "]"
    if(AdminConfig.showAttribute(entry,"symbolicName") == varName) :
        varExists = entry

attributes = [["symbolicName", varName], ["value", varValue], ["description", varDesc]]
if action == "modify" :
    if ( not varExists) :
        print "ERROR: WebSphere Variable doesn't exist!"
        sys.exit(-1)
    else :
        vName = AdminConfig.showAttribute(varExists, "symbolicName")
        vValue = AdminConfig.showAttribute(varExists, "value")
        if not vValue :
            vValue = ""
        print "INFO: Changing \"" + vName + "\" from \"" + vValue + "\" to \"" + varValue + "\""
        AdminConfig.modify(varExists, attributes)    
        AdminConfig.save()
        print "INFO: Variable \"" + varName + "\" is modified."
elif action == "remove" :
    if ( not varExists) :
        print "ERROR: WebSphere Variable doesn't exist!"
        sys.exit(-1)
    else :
        AdminConfig.remove(varExists)
        AdminConfig.save()
        print "INFO: Variable \"" + varName + "\" is removed."
elif action == "create" :
    if (varExists) :
        print "ERROR: WebSphere Variable already exists!"
        sys.exit(-1)
    elif (not entryParentId) :
        print "ERROR: " + scopeStr + "VariableMap:/" + " doesn't exist!"
        sys.exit(-1)
    else :
        AdminConfig.create("VariableSubstitutionEntry", entryParentId, attributes)
        AdminConfig.save()
        print "INFO: Variable \"" + varName + "\" is created."

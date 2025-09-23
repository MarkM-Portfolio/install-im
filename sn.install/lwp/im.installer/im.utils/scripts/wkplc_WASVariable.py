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

# 5724-S68                                                          
# Check the WebSphere Application's  variable 

# wkplc_WASVariable.py
#
#  About:
#      This script will try to connect to Websphere Applicaiton Deployment Manager Server to get was variable information
#      And store the result to a specified file.
#
#  Usage:
#     wsadmin.sh -username system \
#                -password password \
#                -lang jython \
#                -f /wkplc_WASVariable.py c:/temp/result.log
#  
#  Functions:
#        get_was_variable
#        arg1 -the file pato to store security result
#        arg2 -variable name
#  
#        create_was_variable
#        arg1 -variable name
#        arg2 -variable value        
#      
#
#
#-------------------------------------------------------------------
import sys, java
def get_was_variable(filePath, var_name):
    server = AdminConfig.getid('/Cell:'+AdminControl.getCell()+'/')
    varSubstitutions = AdminConfig.list("VariableSubstitutionEntry",server).split(java.lang.System.getProperty("line.separator"))
        
    for varSubst in varSubstitutions:
        getVarName = AdminConfig.showAttribute(varSubst, "symbolicName")
        if getVarName == var_name:
            var_value = AdminConfig.showAttribute(varSubst, "value")
    
    if (var_value != None):    
        try:
           fo = java.io.FileOutputStream(filePath)
           pw = java.io.PrintWriter(fo)
           pw.write("was.variable.name="+var_name+"\n")
           pw.write("was.variable.value="+var_value+"\n")
           pw.close()
        except:
          print "GetNodeInfo: Error -- IO error"
    
    return

def create_was_variable(var_name, var_value):
    AdminConfig.create('VariableSubstitutionEntry', '(cells/'+AdminControl.getCell()+'|variables.xml#VariableMap_1)', '[[symbolicName \"'+var_name+'\"] [value \"'+var_value+'\"]]')
    AdminConfig.save() 

def check_was_app(app_name):
    app_list = AdminApp.list();
    print app_list
    for elem in app_name:
        print elem;
        if (app_list.find(elem) == -1):
            print "Application:" + elem + " does not exist in WAS"
            raise "Application:" + elem + " does not exist in WAS"
            return 1;
    print "All application exists in WAS"
    return 0;
#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
print "Get WAS Variable: - Starting"
if (sys.argv[0] == 'create_variable'):
    var_name = sys.argv[1]
    var_value = sys.argv[2]
    create_was_variable(var_name,var_value)
elif (sys.argv[0] == 'get_variable'):
    output_path = sys.argv[1]
    var_name = sys.argv[2]
    get_was_variable(output_path,var_name);
elif (sys.argv[0] == 'check_was_app'):
    print "check was app: - Starting"
    var_name = [];
    for arg in sys.argv[1:]:
        var_name.append(arg);    
    check_was_app(var_name);
print "Get WAS Variable: - Finished"

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

def printUsage() :
    print 'Usage: '
    print '1. getConfig feature'
    print '2. updateConfig feature configName configValue'
    sys.exit(-1)
    

# check arguments 
if len(sys.argv) <2 :
    printUsage()

operation = sys.argv[0]

if operation != 'getConfig' and operation != 'updateConfig':
    printUsage()

feature = sys.argv[1]

if operation == 'updateConfig' and len(sys.argv) < 4 :
    printUsage()


from java.lang import System
from java.io import File
fileSeparator = System.getProperty('file.separator')
userHome = System.getProperty('user.home')
lineSeparator = System.getProperty('line.separator')
configPath = userHome + fileSeparator + 'lcWizard' + fileSeparator + 'config'
configPath = 'D:/'
# prepare config folder
File(configPath).mkdirs()
# prepare cellName and nodeName
cellName = AdminControl.getCell()
nodeName = AdminControl.getNode()

activitiesParamMap = {
                      'activities.contentPath':'ACTIVITIES_CONTENT_DIR',
                      'activities.statisticsPath':'ACTIVITIES_STATS_DIR'
                      }
blogsParamMap = {
                 'blogs.contentPaht':'FileUploadDirectory',
                 'blogs.indexPath':'LocalSearchIndexDirectory'
                 }
communitiesParamMap = {
                       'communities.indexPath':'/comm:config/comm:indexingTask/comm:indexPath'
                       }
profilesParamMap = {
                    'profiles.indexPath':'/tns:config/tns:IndexingTask/tns:indexpath',
                    'profiles.statisticsPath':'/tns:config/tns:statistics/tns:statisticsFilePath'
                    }

# get configuration
if operation == 'getConfig' :
    if feature == 'activities' :
        node = AdminConfig.getid('/Node:' + nodeName)
        varSubstitutions = AdminConfig.list('VariableSubstitutionEntry', node).split(lineSeparator)
        for varSubst in varSubstitutions:
            getVarName = AdminConfig.showAttribute(varSubst, 'symbolicName')
            for key in activitiesParamMap.keys() :
                if getVarName == activitiesParamMap.get(key) :
                    getVarValue = AdminConfig.showAttribute(varSubst, 'value')
                    print key + ' = ' + getVarValue


    if feature == 'communities' :
        execfile('communitiesAdmin.py')
        CommunitiesConfigService.checkOutConfig(configPath, cellName)
        commConfig = CommConfigFileReaderUpdater(configPath + fileSeparator + 'communities-config.xml')
        for key in communitiesParamMap.keys() :
            print key + ' = '  + commConfig.getNodeProperty(communitiesParamMap.get(key), 'value') 
        
    
    if feature == 'profiles' :
        execfile('profilesAdmin.py')
        ProfilesConfigService.checkOutConfig(configPath, cellName)
        profConfig = ProfConfigFileReaderUpdater(configPath + fileSeparator + 'profiles-config.xml')
        for key in profilesParamMap.keys() :
            print key + ' = ' + profConfig.getNodeValue(profilesParamMap.get(key)) 
        
    
    if feature == 'blogs' :
        execfile('blogsAdmin.py')
        BlogsConfigService.showConfig()
   
# update configuration
if operation == 'updateConfig' :
    configName = sys.argv[2]
    configValue = sys.argv[3]
    if feature == 'activities' :
        varName = activitiesParamMap.get(configName)
        if (not varName) : 
            print 'ERROR: no such configuration: ' + configName
            sys.exit(-1)
        node = AdminConfig.getid('/Node:' + nodeName)
        varSubstitutions = AdminConfig.list('VariableSubstitutionEntry', node).split(lineSeparator)
        for varSubst in varSubstitutions:
            getVarName = AdminConfig.showAttribute(varSubst, 'symbolicName')
            if (varName == getVarName) :
                AdminConfig.modify(varSubst,[["value", configValue]])
                AdminConfig.save()
                print 'value changed' 
                break
        
    if feature == 'communities' :
        execfile('communitiesAdmin.py')
        CommunitiesConfigService.checkOutConfig(configPath, cellName)
        commConfig = CommConfigFileReaderUpdater(configPath + fileSeparator + 'communities-config.xml')
        xpath = communitiesParamMap.get(configName)
        if(xpath) :
            commConfig.updateNodeProperty(xpath, 'value', configValue)
        else :
            print 'ERROR: no such configuration: ' + configName
            sys.exit(-1)
        
        commConfig.saveDoc()
        CommunitiesConfigService.checkInConfig()
        
    
    if feature == 'profiles' :
        execfile('profilesAdmin.py')
        ProfilesConfigService.checkOutConfig(configPath, cellName)
        profConfig = ProfConfigFileReaderUpdater(configPath + fileSeparator + 'profiles-config.xml')
        xpath = profilesParamMap.get(configName)
        if (xpath) :
            profConfig.updateNodeValue(xpath, configValue)
        else :
            print 'ERROR: no such configuration: ' + configName
            sys.exit(-1)
        
        profConfig.saveDoc()
        ProfilesConfigService.checkInConfig()
    
    if feature == 'blogs' :
        execfile('blogsAdmin.py')
        _configName = blogsParamMap.get(configName)
        if (_configName) :
            BlogsConfigService.updateConfig(_configName, configValue)
        else :
            print 'ERROR: no such configuration: ' + configName
            sys.exit(-1)
        
    
        
    
    




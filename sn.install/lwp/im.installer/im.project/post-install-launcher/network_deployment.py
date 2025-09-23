# ***************************************************************** 
#                                                                   
# Licensed Materials - Property of IBM                              
#                                                                   
# 5724-S31                                                          
#                                                                   
# Copyright IBM Corp. 2011, 2014  All Rights Reserved.              
#                                                                   
# US Government Users Restricted Rights - Use, duplication or       
# disclosure restricted by GSA ADP Schedule Contract with           
# IBM Corp.                                                         
#                                                                   
# ***************************************************************** 

"""
This is a sample of a network deployment clustered installation,
where applications grouped and installed into different clusters
on different WAS nodes.
"""

# common settings of your deployment
common_options = {
    "installType": "nd",
    "cell": "yguo02Cell01",
    "cluster": "cluster1",
    #"node": "yguo02aNode01",
    #"server": "server1",
    "webserverNode": "",
    "webserver": "",
    "connectionsAdminUser": "lcadmin",
    "connectionsAdminPassword": "secret",
    "dbType": "DB2",
    "dbServer": "yguo02.notesdev.ibm.com",
    "dbPort": 50003,
    "dbPassword": "secret",
    "dbDriverPath": "/opt/ibm/db2/V9.5/java",
    "dataDirectory": "/opt/IBM/LotusConnections/Data",
    "dataDirectoryLocal": "/opt/IBM/LotusConnections/DataLocal",
    "msgStoreHome": "/opt/IBM/LotusConnections/msgstore",
    "msgStore": {
        "logSize": 100,
        "minPermanentStoreSize": 200,
        "minTemporaryStoreSize": 200,
        "maxPermanentStoreSize": 500,
        "maxTemporaryStoreSize": 500
    },
    "lcHome": "/opt/IBM/LotusConnections",
    "enableWPI": "true",
    "enableWCI": "true",
    "replaceEAR": "true",
    "mailSession":  {
                        "host":"d01av03.pok.ibm.com"
                        #"properties": {
                        # "mail.smtp.connectiontimeout":12000
                        #}
                    },
    "ssoDomain": ".swg.usma.ibm.com",
    "admin": "yguo",
    "global-moderator": "ajones2",
    "customize-roles-maps": {
	"admin": [ "Yes", "No", "vijay0", "ics_admin_group" ],
	"search-admin": [ "Yes", "No", "vijay", "ics_admin_group" ],
	"widgets-admin": [ "Yes", "No", "vijay", "ics_admin_group" ],
	}
    "notificationEmailDomain": "skipper.swg.usma.ibm.com",
    "security":{"com.ibm.websphere.security.disableGetTokenFromMBean":"true"},
    "log_options":{
        "maxNumLogFiles":30,
        "rolloverType":"TIME",
        "rolloverPeriod":24,
        "baseHour":24
    },
    "webContainer":{"HttpSessionCloneId":"someValue","Description":"put_a_description_here"},
    "trace_options":{"startupTraceSpecification":"com.ibm.ws.security.ltpa.LTPAServerObject=severe"},
    "jvm":{"verboseModeGarbageCollection":"true"}
    #"sessionCookieName": "ICSESSIONID",
    #"sessionCookieDomain": "ibm.com",
    #"multi-tenent": "false",
    #"filenetAdmin": "ajones1",
    #"filenetAdminPassword": "jones1",
    #"mediaGalleryAdmin": "ajones1",
    #"mediaGalleryAdminPassword": "jones1",
    # Advanced tuning options
    #'contextRootPrefix': '/ic4',
    #'customizationDir': '/iccs/customization_4.0',
    #'datasourceConnectionPoolMax': 49,
    #'datasourceStatementCacheSize': 99,
    #'logFileSize': 9,
    #'webcontainerThreadPool': {'minimumSize':49, 'maximumSize':99},
    #'jvm': {"initialHeapSize":256, 'maximumHeapSize': 1024,
    #    'genericJvmArguments': '-Xgcpolicy:gencon'}
    # user provided post install script will be called with tmp
    # LotusConnetions-config diretory as argument
    #"post-install-script": "post-install.sh"
}

#
# When install, put component specific settings here, any settings find in
# this section will override the same settings from the common_options.
#
# application specific settings:
#   "disableModule": ['calendar.war', 'catalog.war', 'qkr.search.engine.jar'],
#   "deleteModule": ['calendar.war', 'catalog.war', 'qkr.search.engine.jar'],
#
apps_options = {
    "news":     {
                    "cluster":"clusterN",
                    "dbPort": 50000,
                    "activation_specs":[{"name":"News Deleted Event Consumer AS","maxConcurrency":"3"}] 
                },
    "search":   {
                 "cluster":"clusterS",
                 "dbPort": 50000
                 # "was_variables": { "SEARCH_INDEX_DIR" : "/opt/IBM/index" }
                },
    "profiles": {"cluster":"clusterP", "dbPort": 50001 },
    "homepage": {"cluster":"clusterH" },
    "activities": {"cluster":"clusterA" },
    "communities": {"cluster":"clusterC" },
    "blogs":    {"cluster":"clusterB"},
    "dogear":   {"cluster":"clusterD" },
    "files":    {
                    "cluster":"clusterF" 
                    "custom_roles":{
                        "search-admin":['No','No','ajones1|ajones2|ajones3','Catalina'],
                        "widget-admin":['No','No','wasadmin','']
                    }
                },
    "wikis":    {"cluster":"clusterW" },
    "mobile":   {"cluster":"clusterM" },
    "common":   {"cluster":"clusterCO"
                 #"contextRoot": "common"
                },
    "proxy":    {},
    "urlpreview":{},
    "widgetcontainer":{},
    "pushnotification":{"cluster":"clusterF"},
    "forums":   {"cluster":"clusterDF" }
}

#!/usr/bin/python
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2010, 2014                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 
#
#

import sys

import wsadminlib as lb
import baseArguments as myArgs
import wasAdminConfig as adminConfig
import baseLogHandler as log

data_sources = [ 'activities', 'blogs', 'communities', 'dogear', 'files', 'forum', 'homepage', 'metrics', 'mobile', 'news', 'oauth provider', 'oembed', 'profiles', 'pushnotification', 'search', 'wikis' ]

def existing_properties(pset_id):
    """ list existing properties in given J2EEResourcePropertiesSet,
        result is a list of tuple, like: {name:property_id...} """
    prop_ids = AdminConfig.showAttribute(pset_id, "resourceProperties")
    prop_ids = prop_ids[1:len(prop_ids)-1].split()
    prop_dict = {}
    for id in prop_ids:
        name = AdminConfig.showAttribute(id, "name")
        prop_dict[name] = id
    return prop_dict

def set_rerounting(hadr_properties):
    for ds_name in data_sources:
        print "Update JDBC Datasource [%s]" % ds_name
        ds_id = AdminConfig.getid("/DataSource:%s/" % ds_name)
        print "   data source id: %s" % ds_id
        prop_set = AdminConfig.showAttribute(ds_id, 'propertySet')
        print "   propertySet id: %s" % prop_set
        props = existing_properties(prop_set)
        for n,v in hadr_properties.items():
            t = "java.lang.String"
            if type(v) == type(1):
                t = "java.lang.Integer"
            elif v == "true" or v == "false":
                t = "java.lang.Boolean"
            print "   Set property [%s] -> [%s]" % (n, v)
            if props.has_key(n):
                AdminConfig.modify(props[n], [['value',  v],['type', t]])
            else:
                AdminConfig.create('J2EEResourceProperty', pset, [['name', n], ['value', v], ['type', t]])



######################################################
#
# main
#
######################################################
def main(argv):
	
	log.info("\n*** Set data source re-routing ***\n")
	
	standbydbhost = myArgs.parseArgument(argv,"dbhost")
	log.info("threadpool: " + standbydbhost)
	
	dbport = myArgs.parseArgument(argv,"dbhostport")
	log.info("nodeName: " + dbport)


        reroutingproperties = {"clientRerouteAlternateServerName": standbydbhost, "clientRerouteAlternatePortNumber": dbport, "retryIntervalForClientReroute": 6, "maxRetriesForClientReroute": 10}

	if(standbydbhost == "" or dbport == ""):
		log.error("ERROR: standbydbhost or dbport are missing")
		_usage()
		sys.exit(-1)


	log.info("\n\n-------------------------")
	
	set_rerounting(reroutingproperties)

	log.info("Set data source re-routing...")
	adminConfig.saveConfig()
	
	log.info("\n-----------------------")

	log.info ("finished\n")  
	
	
	
if __name__ == "__main__":
	
	main(sys.argv)

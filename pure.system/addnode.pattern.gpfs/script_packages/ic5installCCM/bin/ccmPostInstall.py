#!/usr/bin/python
#
# 
# GIS-AG
# created 2014-08-01
# version 1.0.0
#
# history:
# 2014-08-01: initial version
#
#

import sys
import wasAdminConfig as adminConfig
import baseArguments as myArgs
import baseLogHandler as log

import wasMap2Host as mth
import wasPlugin as wp

execfile('bin/wsadminlib.py')

######################################################
#
# public methods
#
######################################################
def usage():
	
	log.info ("Provide the following parameters. Otherwise this job is skipped") 
	log.info ("allowed parameters: ")
	log.info ("---------------------------------------")
	log.info ("add ccm node to existing cell:")
	log.info ("     parameter: CCMnode")
	log.info ("     parameter: CCMserver")
	log.info ("     parameter: CCMcluster")
	log.info ("     parameter: DMGRpath")
	log.info ("")


######################################################
#
# main
#
######################################################

def main(argv):

        CCMnode = myArgs.parseArgument(argv,"CCMnode")
        CCMserver = myArgs.parseArgument(argv,"CCMserver")
        DMGRpath = myArgs.parseArgument(argv,"DMGRpath")

        if(CCMnode=="" or CCMserver=="" or DMGRpath==""):
                usage()
                sys.exit(1)

        else:
			## map CCM application to all HTTP Servers
			log.info ("\n\nMapping CCM to HTTP Server")
			mth.mapAppToAllWebServers("Extensions")
			mth.mapAppToAllWebServers("FileNetEngine")
			mth.mapAppToAllWebServers("fncs")

			## set WAS configuration as described in IC5 documentation for CCM
			log.info ("\n\nSet WAS configuration for CCM")
			configureTransactionService(CCMnode, CCMserver, '30', '60', '0', '180')
			serverId=getServerByNodeAndName(CCMnode,CCMserver)
			AdminConfig.modify(serverId, '[[provisionComponents "true"]]')

			## save changes
			adminConfig.saveConfig()

			## Update HTTP server plugin with changes
			log.info ("\n\nUpdating WAS HTTP server plugin")
			wp.generateWebserverPlugins(DMGRpath)

			log.info ("\nfinished\n")




if __name__ == "__main__":
   main(sys.argv)


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
import os

import wsadminlib as lb
import wasAdminConfig as adminConfig
import baseArguments as myArgs
import baseLogHandler as log


######################################################
#
# public methods
#
######################################################
def getKeyStores(): 
	ret = AdminTask.listKeyStores()
	return ret
	
def getAllSignerCertificates(keyStoreName):
	ret = AdminTask.listSignerCertificates (["-keyStoreName" ,keyStoreName])
	return ret

def getAllPersonalCertificates(keyStoreName):
	ret = AdminTask.listPersonalCertificates (["-keyStoreName", keyStoreName]) 
	return ret
	

def addSignerCertificate(keyStoreName, certFilePath):
	
	log.debug("addSignerCertificate from File %s" %certFilePath)
	certAlias = "ALIAS"
	
	try: 
		if (os.path.exists(certFilePath)): 
			(certPath,certFileName)=os.path.split(certFilePath)
			log.info("found certFile name %s" %certFileName)
			(certFile,certExt) = os.path.splitext(certFileName)
			if(certFile != ""): 
				certAlias = certFile
		else: 
			log.error("Certificate File does not exist: %s" %certFilePath)
			return (-1)
	except:
		pass
	
	log.info("using alias '%s' for this certificate " %certAlias )
	
	try: 
		ret = AdminTask.addSignerCertificate([
									"-keyStoreName", keyStoreName, 
									"-certificateFilePath", certFilePath,
									"-base64Encoded", "true", 
									"-certificateAlias", certAlias
									 ]) 
		adminConfig.saveConfig()
	
		return ret
	
	except:
		log.handleException(sys.exc_info()[:2])
		return (-1)
	

""" 
"""
def retrieveSigner(hostName, port, alias, keyStoreName):
	log.info("retrieving SSL Certs from Web Server")

   	try:
	   	ret = AdminTask.retrieveSignerFromPort(['-host', hostName,
	   	'-port', port,
	   	'-keyStoreName', keyStoreName,
	   	'-certificateAlias', alias])
	   	
	   	
	   	
   	except:
		log.handleException(sys.exc_info()[:2])
   		log.info("(ERR) not able to store certificate into keystore " + keyStoreName)

   	log.info("finished retrieving SSL Certs from Web Server")

	adminConfig.saveConfig()
	
	return ret
	
def usage():
	
	log.info ("Provide the following parameters. Otherwise this job is skipped") 
	log.info ("allowed parameters: ")
	log.info ("---------------------------------------")
	log.info ("retrieve signer certificate from host:")
	log.info ("     parameter: hostname")
	log.info ("     parameter: port (optional, default 443)" )
	log.info ("")
	log.info ("import root certificate into cell trust store:")
	log.info ("     parameter: certPath")
	
	
######################################################
#
# main
#
######################################################
def main(argv):
	
	keyStoreName = "CellDefaultTrustStore"
	
	hostName = myArgs.parseArgument(argv,"hostName")
	port = myArgs.parseArgument(argv,"port")
	certPath = myArgs.parseArgument(argv,"certPath")
	alias = myArgs.parseArgument(argv,"alias")
	
	if(certPath == "" and hostName == ""):
		usage()
		sys.exit(-1)
	
	log.info("reading all keystores ")
	ret = getKeyStores()
	log.info(ret)
	log.info("\n")
	
	log.info("reading all signer certificates for keystore " + keyStoreName)
	signerCerts = getAllSignerCertificates(keyStoreName).splitlines()
	for cert in signerCerts:
		log.info(cert)
	log.info("\n")


	log.info("reading all personal certificates for keystore " + keyStoreName)
	persCerts = getAllPersonalCertificates(keyStoreName).splitlines()
	for cert in persCerts:
		log.info(cert)
	log.info("\n-----------------------")

	ok = 0
	if(hostName != ""):
		ok = 1
		if(port== ""):
			log.info("no parameter 'port'. Use default=443")
			port="443"
		
		ret = retrieveSigner(hostName, port, alias, keyStoreName)
		log.info (ret)
		if(ret == -1): 
			sys.exit(ret)
		
		log.info("\n-------------------------")
		log.info("reading all certificates for keystore " + keyStoreName)
		ret = getAllSignerCertificates(keyStoreName)
		log.info(ret)
	
	
	if(certPath != ""):
		ok = 1
		ret = addSignerCertificate(keyStoreName, certPath)
		log.info (ret)
		if(ret == -1): 
			sys.exit(ret)


	if(ok==0): 
		usage()
				
	log.info ("\nfinished\n")  



   
if __name__ == "__main__":
   main(sys.argv)
   
   

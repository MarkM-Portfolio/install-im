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


import sys, getopt
import baseLogHandler as log


def parseArgument(argv, argument):

	log.debug("list of program arguments: " + ",".join(argv))
	arglen = len(argv)
	log.debug("number of arguments passed: %d" %arglen)	
	
	for arg in argv:
		keyAndValue = arg.split("=",1) # only the first "=" is seperator
		if(len(keyAndValue) == 2):
			key=keyAndValue[0]
			value=keyAndValue[1]
			log.debug(" %s  =   %s" %(key, value))
			if(key.lower() == argument.lower()): 
				return value		
	log.debug ("argument '%s' not found in list" %argument)
	return("")
######################################################
#
# main
#
######################################################


def main(argv):
	log.info("ok")	

if __name__ == "__main__":
   main(sys.argv)
   
   


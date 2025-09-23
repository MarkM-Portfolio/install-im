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


import sys
# import datetime
import time
import baseLogConfig as logConfig
######################################################
#
# public methods
#
######################################################

def debug(message):
	if(logConfig.level>= logConfig.logLevels.get("DEBUG")):
		_print ("DEBUG", message)
	return 0

def info(message):
	if(logConfig.level>= logConfig.logLevels.get("INFO")):
		_print ("INFO", message)
	return 0

def warn(message):
	if(logConfig.level>= logConfig.logLevels.get("WARN")):
		_print ("WARN", message)
	return 2

def error(message):
	if(logConfig.level>= logConfig.logLevels.get("ERROR")):
		_print ("ERROR", message)
	return 3

def fatal(message):
	if(logConfig.level>= logConfig.logLevels.get("FATAL")):
		_print ("FATAL", message)
	return 4

"""
use as 
log.handleException(sys.exc_info()[:2])
"""
def handleException(exception): 
	# (kind, value) = sys.exc_info()[:2]
	(kind, value) = exception
	(kind, value) = str(kind), str(value)
	_print("EXCEPTION", (kind,value))
	# print ("Exception type: " + kind)
	# print ("Exception value: " + value)
	return 5 


######################################################
#
# private methods
#
######################################################


def _print(level, message):
	# now = str(datetime.datetime.now().isoformat())
	
	# nowX = time.strptime("1 Dec 2008 06:43:00 +0100", "%d %b %Y %H:%M:%S %Z")
	now = time.strftime("%m/%d/%Y %I:%M:%S%p")

	try:
		# print ("Current date & time = %s" % now)
		# print ("Date and time in ISO format = %s" % now.isoformat() )
		print("(%s) - %s : %s" %(level,now,message))
	except: 
		print("(LOGGER ERROR) - %s: Error printing message" %now)
		print(message)
		pass  

def _test():

	print ("LOG LEVEL = %s" %logConfig.level)
	
	info(["Test","Test"])
	
	warn("Test")
	
	info(5)
	
	print(error("HALLO"))
	
	try: 
		print("a" + 5)
	except:
		handleException(sys.exc_info()[:2])
	
	


######################################################
#
# main
#
######################################################

def main(argv):
	_test()
	
if __name__ == "__main__":
   main(sys.argv)
   
   


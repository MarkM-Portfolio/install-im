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


import re
import sys
import getopt
import os
import subprocess


def parseArgument(argv, argument):

	# print("list of program arguments: " + ",".join(argv))
	arglen = len(argv)
	# print("number of arguments passed: %d" %arglen)	
	
	for arg in argv:
		keyAndValue = arg.split("=",1) # only the first "=" is seperator
		if(len(keyAndValue) == 2):
			key=keyAndValue[0]
			value=keyAndValue[1]
			# print(" %s  =   %s" %(key, value))
			if(key.lower() == argument.lower()): 
				return value		
	# print ("argument '%s' not found in list" %argument)
	return("")
######################################################
#
# main
#
######################################################

def main(argv):


	wsadminCommand = parseArgument(argv, "wsadmin") #d:/IBM/WP85/AppServer/bin/wsadmin.bat")
	password= parseArgument(argv, "password") 
	# password = "{xor}OTAw"
	mode = parseArgument(argv, "mode")
	
	if(wsadminCommand=="" or password == "" or mode == ""): 
		print("usage decodePassword wsadmin=<path to wsadmin script> password=<password to encode or decode> mode=<encode|decode>")
		exit(0)
		
	if(mode=="encode"):
		coder = "import com.ibm.ws.security.util.PasswordEncoder as pe; x='" + password + "'; pe.main([x]);"
	elif(mode=="decode"):
		coder = "import com.ibm.ws.security.util.PasswordDecoder as pd; pd.main(['" + password + "']);"
	else:
		exit(1)
	
	# print(coder)
	
	#
	#
	# call wsadmin script in subprocess and retrieve the return values	
	p = subprocess.Popen([wsadminCommand, "-lang", "jython", "-conntype", "none", "-c", coder], stdout=subprocess.PIPE)
	output = p.communicate()[0]
	
	encoded = ""
	decoded = ""

	#
	# strip the password and encoded password from this line
	#
	# encoded password == "{xor}OTAw", decoded password == "foo"
	outputLines = output.splitlines()
	try:
		for i in range(len(outputLines)):
			if(i>0):
				text = outputLines[i].decode("utf-8")
				# print(text)
				lineparts = text.split(",")
				for entry in lineparts:
					keyvalue = entry.split("==")
					# print(keyvalue)
					if(keyvalue[0].find("decoded")>-1):
						#decoded = re.sub(r'"(.*)"', r'\1', keyvalue[1])
						decoded = keyvalue[1].strip((" \""))
						# print ("decoded = " + decoded)
					elif(keyvalue[0].find("encoded")>-1):
						#encoded = re.sub(r'"(.*)"', r'\1', keyvalue[1])
						encoded = keyvalue[1].strip((" \""))
						
						# print ("encoded = " + encoded)
	
		if(mode=="encode"):
			ret = encoded
		elif(mode=="decode"):
			ret = decoded
		else:
			ret = ""
		print(ret)
		
	except:
		print("cannot decode/encode")
		pass									
	# os.environ['UUU2']="KKKK"
	# os.putenv('UUU','csj2')
		
if __name__ == "__main__":
   main(sys.argv)
	   
   



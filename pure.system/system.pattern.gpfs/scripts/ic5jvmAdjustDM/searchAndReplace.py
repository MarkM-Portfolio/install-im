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
import getopt
import shutil
    

######################################################
#
# main
#
######################################################

def main(argv):

	# print(argv)
	usage = "usage: searchAndReplace.py -f <file> -o <oldstring> -n <newstring>"
	try:
      		opts, args = getopt.getopt(argv,"hf:o:n:",["file=","oldText=","newText"])
	except getopt.GetoptError:
      		print(usage)
      		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print (usage)
			sys.exit()
		elif opt in ("-f", "--file"):
			inputFile = arg
		elif opt in ("-o", "--oldText"):
			oldText = arg
		elif opt in ("-n", "--newText"):
			newText = arg
	
	if(not ('inputFile' in locals() and 'oldText' in locals() and 'newText' in locals())):
		print(usage)
		sys.exit()
	
	print("Input file is " + inputFile)
	#print("Text convert from '" + oldText + "' to '" + newText + "'")
	
	

	try: 
		backupFile = inputFile + "_OLD"
		print ("move backup to file " + backupFile)
		shutil.move(inputFile,backupFile)
	except: 
		print("Error writing backup file - ignored")        


	try:
		with open(backupFile, 'r') as fOld:
			old = fOld.readlines() # Pull the file contents to a list
	finally: 
		fOld.close()

	try:
		with open(inputFile, 'w') as f:
			for line in old:
				# line = line.rstrip()
				line = line.replace(oldText, newText)
				f.write(line)
	finally: 
		f.close()





if __name__ == "__main__":
   main(sys.argv[1:])

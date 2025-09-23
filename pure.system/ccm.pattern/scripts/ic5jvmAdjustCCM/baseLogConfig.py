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

logLevels = {}

# Add three key-value tuples to the dictionary
logLevels["DEBUG"] = 40
logLevels["INFO"] = 30
logLevels["WARN"] = 20
logLevels["ERROR"] = 10
logLevels["FATAL"] = 0

level=logLevels.get("DEBUG")
#level=logLevels.get("INFO")

######################################################
#
# main
#
######################################################


print("current log level %s" %level)


def main():
	print("OK")


if __name__ == "__main__":
   main()
   
   


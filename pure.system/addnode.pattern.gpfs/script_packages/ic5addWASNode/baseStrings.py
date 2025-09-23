#!/usr/bin/python
#
#
# 
# author VoDo
# GIS-AG
# created 2014-07-01

import string

"""
"""
def shortHostName(hostName):
	start = 0
	end = hostName.find('.', start)
	if(end != -1):
		ret = hostName[start:end]
	else:
		ret = hostName
		
	print ("extraction short hostname from hostname " + hostName + " -> " + ret)
	return ret

######################################################
#
# main
#
######################################################
def main():
	
	print(shortHostName("12123"))
	print(shortHostName("121.23"))
	print(shortHostName("121.2.3"))
	print(shortHostName("1ldlsakjd lksaj dlksaj dljsa dl jsa21.2.3"))
	
	
if __name__ == "__main__":
   main()
   
   


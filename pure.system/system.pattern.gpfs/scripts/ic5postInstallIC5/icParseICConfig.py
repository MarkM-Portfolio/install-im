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

import fileinput
import re
import sys

    

######################################################
#
# main
#
######################################################

def main(argv):
  
   
  for line in fileinput.input(inplace=1, backup='.bak'):
    line = re.sub(r' ssl_href="(.*):\d+"', r' ssl_href="\1"', line.rstrip())
    line = re.sub(r' href="(.*):\d+"', r' href="\1"', line.rstrip())
    print(line)
   
   
if __name__ == "__main__":
   main(sys.argv[1:])
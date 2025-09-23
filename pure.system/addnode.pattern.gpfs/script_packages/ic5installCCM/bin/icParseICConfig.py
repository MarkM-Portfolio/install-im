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
# strip port numbers from LotusConnections.config file 
# usage
# python icParseICConfig.py <fileName>
# 
# creates backup file with extension .bak  
#

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
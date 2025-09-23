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
   
   


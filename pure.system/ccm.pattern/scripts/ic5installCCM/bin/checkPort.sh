#!/bin/bash
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

checkPort() {
 ## checkPort HOST PORT
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -en "\n\nChecking port status - ${1}:${2} - "
  (echo >/dev/tcp/${1}/${2}) &> /dev/null
  if [ "$?" -eq 0 ]; then
   echo "online"
   return 1
  else
   echo "offline"
   return 0
  fi
 fi
}

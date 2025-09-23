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

createP8domain() {
 ## create P8 domain for CCM
 ### createP8domain USERNAME USERPASSWORD
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -e "\n\nCreating P8 domain"
  CPadminUser=$1
  CPadminPassword=$2
  cd ${ccmPath}/ccmDomainTool; expect -c "\
   spawn ./createGCD.sh;\
   expect \"Enter the Deployment Manager administrator user ID*\" { send \"${CPadminUser}\r\" };\
   expect -timeout -1 \"password:\";\
   send \"${CPadminPassword}\r\";\
   expect \"*correct information?*\" { send \"Y\r\" };\
   expect -timeout -1 \"Enter group name*\";\
   send \"\r\" ;\
   expect -timeout -1 eof"
 fi
}

createObjectStore() {
 ## create ObjectStore
 ### createObjectStore USERNAME USERPASSWORD
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -e "\n\nCreating ObjectStore"
  COadminUser=$1
  COadminPassword=$2
  COendpointUrl=$(awk "/>\/communities</{getline; print}" $(find ${dmgrPath}/config/cells/ -name LotusConnections-config.xml) | grep -Po "(?<=ssl_href=\").*\w+")
  cd ${ccmPath}/ccmDomainTool; expect -c "\
   spawn ./createObjectStore.sh;\
   expect \"Enter the Deployment Manager administrator user ID*\" { send \"${COadminUser}\r\" };\
   expect -timeout -1 \"password:\";\
   send \"${COadminPassword}\r\";\
   expect \"*correct information?*\" { send \"Y\r\" };\
   expect -timeout -1 \"Enter group name*\" ;\
   send \"\r\" ;\
   expect -timeout -1 \"endpoint URL\" ;\
   send \"${COendpointUrl}\r\" ;\
   expect -timeout -1 eof"
 fi

}



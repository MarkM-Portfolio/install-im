#!/bin/bash

createP8domain() {
 ## create P8 domain for CCM
 ### createP8domain USERNAME USERPASSWORD
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -e "\n\nCreating P8 domain"
  CPadminUser=$1
  CPadminPassword=$2
  cd ${ccmPath}/ccmDomainTool; expect -c "
   set timeout 180
   spawn ./createGCD.sh
   expect \"Enter the Deployment Manager administrator user ID*\" { send \"${CPadminUser}\r\" }
   expect \"Enter the Deployment Manager administrator password:\" { send \"${CPadminPassword}\r\" }
   expect \"*correct information?*\" { send \"Y\r\" }
   expect \"Enter group name*\" { send -- \"\r\" }
   expect timeout { puts \"'expect' timeout reached\"; exit }
   interact"
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
  cd ${ccmPath}/ccmDomainTool; expect -c "
   set timeout 180
   spawn ./createObjectStore.sh
   expect \"Enter the Deployment Manager administrator user ID*\" { send \"${COadminUser}\r\" }
   expect \"Enter the Deployment Manager administrator password:\" { send \"${COadminPassword}\r\" }
   expect \"*correct information?*\" { send \"Y\r\" }
   expect \"Enter group name*\" { send -- \"\r\" }
   expect \"endpoint URL\" { send \"${COendpointUrl}\r\" }
   expect timeout { puts \"'expect' timeout reached\"; exit }
   interact"
 fi

}



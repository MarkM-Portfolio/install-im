#!/bin/bash
ccmPathToDbScripts=${NFSSHARE}/software/Filenet/db2
dbInstanceUser="icinst1"
dbInstanceFencUser="icfenc15"
dbInstanceFencGroup="icadm15"
dbInstancePort=50001

#createCcmDbUsers() {
# ## create local users and groups for CCM instance
# groupadd -g 1130 ${dbInstanceFencGroup}
# groupadd -g 1131 ${dbInstanceUser}
# useradd -m -s /bin/bash -u 1130 -g 1130 -d /ibm/db2home/${dbInstanceFencUser} ${dbInstanceFencUser}
# useradd -m -s /bin/bash -u 1131 -g 1131 -d /ibm/db2home/${dbInstanceUser} ${dbInstanceUser}
# usermod -G ${dbInstanceFencGroup},dasadm1 ${dbInstanceUser}
#}

createCcmDatabases() {
 ## create CCM databases
 if [ ! -d "${ccmPathToDbScripts}/libraries.gcd" ]; then
  echo "Directory for DB2 scripts not found"
 else
  su - ${dbInstanceUser} -c "db2 -td@ -vf ${ccmPathToDbScripts}/libraries.gcd/createDb.sql"
  su - ${dbInstanceUser} -c "db2 -td@ -vf ${ccmPathToDbScripts}/libraries.gcd/appGrants.sql"
  su - ${dbInstanceUser} -c "db2 -td@ -vf ${ccmPathToDbScripts}/libraries.os/createDb.sql"
  su - ${dbInstanceUser} -c "db2 -td@ -vf ${ccmPathToDbScripts}/libraries.os/appGrants.sql"
 fi
}

#createCcmInstance() {
# ## create DB2 instance
# /opt/ibm/db2/V10.1/instance/db2icrt -a SERVER -p ${dbInstancePort} -u ${dbInstanceFencUser} ${dbInstanceUser}
# su - ${dbInstanceUser} -c "db2set DB2COMM=TCPIP"
# su - ${dbInstanceUser} -c "db2set DB2CODEPAGE=1208"
# su - ${dbInstanceUser} -c "db2 update dbm cfg using SVCENAME ${dbInstancePort}"
# su - ${dbInstanceUser} -c "db2start"
#}

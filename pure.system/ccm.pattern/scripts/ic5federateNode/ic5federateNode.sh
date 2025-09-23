#!/bin/bash
# description: 
# 

basepath=$(pwd -L)
if [ ! -d "${basepath}/logs" ]; then
 mkdir ${basepath}/logs
fi

## Handle output. Send STDOUT to console AND file, STDERR to file
LOGOUT=${basepath}/logs/logOUT-$(date +"%Y-%m-%d_%H-%M").log
LOGERR=${basepath}/logs/logERR-$(date +"%Y-%m-%d_%H-%M").log
exec > >(tee ${LOGOUT})
exec 2>> ${LOGERR}

## load variables from local pure pattern declaration
if [ -f /etc/virtualimage.properties ]; then
 . /etc/virtualimage.properties
fi

dmgrPath=${PROFILE_ROOT}

service iptables stop
/sbin/chkconfig iptables off

ulimit -n 65536
echo "virtuser hard nofile 65536" >> /etc/security/limits.conf
echo "root soft nproc 2047" >> /etc/security/limits.conf
echo "root hard nproc 16384" >> /etc/security/limits.conf
echo "root soft nofile 1024" >> /etc/security/limits.conf
echo "root hard nofile 65536" >> /etc/security/limits.conf
echo "root soft stack 10240" >> /etc/security/limits.conf
echo "session required pam_limits.so" >> /etc/pam.d/login

chown -R virtuser:admingroup /opt/IBM/WebSphere
RTUSR=virtuser

auto() {
  echo "su - ${RTUSR} -c \"${dmgrPath}/bin/addNode.sh ${DmgrHostName} ${DmgrPort} -conntype SOAP -username virtuser -password PASSWORD_REMOVED\""
  su - ${RTUSR} -c "${dmgrPath}/bin/addNode.sh ${DmgrHostName} ${DmgrPort} -conntype SOAP -username virtuser -password ${WAS_Password}"
 
}

case ${1} in
 "auto")
  auto
  ;;
 *)
  echo "usage: $(basename $0) {auto}"
  ;;
esac


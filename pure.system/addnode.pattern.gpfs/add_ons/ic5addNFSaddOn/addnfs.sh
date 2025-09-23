#!/bin/bash
#
# Default add-on script to configure an NFS mount point.  This script must be run as root!
# HOST_NAME - Host name of the NFS server.
# REMOTE_EXPORT - Remote mount point that will be mounted on the local Virtual Machine
# MOUNT - Mount point for the NFS file system.  The mount point directory is
#   created if it does not already exist.  An entry specifying the device and
#   the mount point is added to /etc/fstab and the NFS filesystem is mounted.  If not
#   supplied, the disk is not mounted.
# NFS_TYPE - NFS type of the target server, can be nfs for NFSv2 and NFSv3 file 
#            server and nfs4 for NFSv4 server.
# OPTIONS - comma separate list of options used in the NFS command
#  LOCKD_TCPPORT=32803
#  LOCKD_UDPPORT=32769
#  MOUNTD_PORT=892
#  RQUOTAD_PORT=875
#  PORTMAPPER=111
#  STATD_PORT=662
#  NFS_PORT=2049
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

if [ -f /etc/virtualimage.properties ]; then
. /etc/virtualimage.properties
fi

markup()
{
echo ""
echo "$1 function $2"
echo "---------------------------------------------------------------------"
echo ""
echo ""
}

statusIPtables()
{
## for testing purposes, switch off iptables
markup starting "$1 iptables services"
case $1 in
	start)
	echo "Starting iptables and add to autostart!"
	/etc/init.d/iptables start
	/etc/init.d/ip6tables start
	chkconfig iptables on
	chkconfig ip6tables on
	;;
	stop)
	echo "Stopping iptables and remove autostart!"
	/etc/init.d/iptables stop
	/etc/init.d/ip6tables stop
	chkconfig iptables off
	chkconfig ip6tables off
	;;
esac
markup finished "$1 iptables services"
}

checkPort() {
 ## checkPort HOST PORT
 if [ "$#" -ne 2 ]; then
  echo "${FUNCNAME} - wrong number of variables passed ("$@")"
 else
  echo -n "Checking port status - ${1}:${2} - "
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

createMount()
{
## Do the job
markup starting "Checking NFS Server Status and mounting EXPORT to local Mountpoint"
checkPort ${HOST_NAME} 2049
if [ "$?" -eq 0 ]; then
   echo -e "\n\nError reaching NFS Server ${HOST_NAME} - abort createMount"
   exit 5
  else
   if [ -n "$MOUNT_POINT" ]; then
     echo "*** mounting ${HOST_NAME}:${REMOTE_EXPORT} at ${MOUNT_POINT} with ${OPTIONS}"
     mkdir -p "${MOUNT_POINT}"
	 CHECKENTR=`grep ${MOUNT_POINT} /etc/fstab`
	 if [ "${CHECKENTR}" == "" ]; then
	  echo "${HOST_NAME}:${REMOTE_EXPORT}	${MOUNT_POINT}	${NFS_TYPE} ${OPTIONS}	0 0" >>/etc/fstab
	  else
	  echo "Entry with ${MOUNT_POINT} already exists in /etc/fstab"
	 fi
	 mount "$MOUNT_POINT"
   else
    echo "$MOUNT_POINT is not set - abort!"
    exit 5
   fi
fi
markup finished "Checking NFS Server Status and mounting EXPORT to local Mountpoint"
}

case $1 in
	*)
	statusIPtables stop
	createMount
	;;
esac

exit 0

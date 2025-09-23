#!/bin/bash
# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2010, 2016
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

# 5724-S68

log() {
	echo "$*" 2>&1 | tee -a ${fn_backup_log}
}

mutedLog() {
	echo "$*" 2>&1 >> ${fn_backup_log}
}

backupFileNet() {
	if [ -d "${conn_home_path}"/FileNet_backup ]
	then
		log "FileNet_backup folder exists, removing it..."
		rm -rf ${conn_home_path}/FileNet_backup
		create_log=$?
		if [ 0 -ne $create_log ]
		then
		  log ""
			log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
			exit 1
		fi
	fi
	mkdir ${conn_home_path}/FileNet_backup
	mkdir ${conn_home_path}/FileNet_backup/lib
	mkdir ${conn_home_path}/FileNet_backup/fncs

  log "backing up Engine-ws.ear"
	cp ${conn_home_path}/FileNet/ContentEngine/tools/configure/profiles/CCM/ear/Engine-ws.ear ${conn_home_path}/FileNet_backup

	log "backing up Jace jar"
	cp ${conn_home_path}/FileNet/ContentEngine/lib/Jace*.jar ${conn_home_path}/FileNet_backup/lib

	log "backing up navigatorEAR"
	cp ${conn_home_path}/FNCS/configure/deploy/navigatorEAR.ear ${conn_home_path}/FileNet_backup

	log "backing up fncs-sitePrefs"
	cp ${conn_home_path}/FNCS/configure/explodedformat/fncs/WEB-INF/classes/fncs-sitePrefs.properties ${conn_home_path}/FileNet_backup

	log "backing up CE API"
	cp -r ${conn_home_path}/FNCS/configure/CE_API ${conn_home_path}/FileNet_backup

	log "backing up profile CCM"
	cp -r ${conn_home_path}/FNCS/configure/profiles/CCM ${conn_home_path}/FileNet_backup/fncs
}

usage() {
	if [ ! "$*" == "" ]
	then
		log ""
		log "Error: $* is an unrecognized parameter."
	fi
	log "Usage: $fnUpdateScriptName_ [-param=value] ... [-paramN=valueN]"
	log ""
	log "Available parameters to set:"
	log "conn_home_path		: Path of Connections Home (e.g.: /opt/IBM/Connections)"
}

validate() {

	# config Connections home folder
		firstTime_="true"
		while [ ! -f "$conn_home_path/FileNet/ContentEngine/tools/configure/profiles/CCM/ear/Engine-ws.ear" ] || [ ! -f "${conn_home_path}/FNCS/configure/deploy/navigatorEAR.ear" ]
		do
			if [ -z "$conn_home_path" ]
			then
				if [ ! "$firstTime_" == "true" ]
				then
					log "Error: The path of Connections Home is empty."
					log ""
				else
					firstTime_="false"
				fi
			else
				if [ ! "$firstTime_" == "true" ]
				then
					log "Error: $conn_home_path is not the path of Connections Home or there is no Engine-ws.ear under ContentEngine folder."
					log ""
				else
					firstTime_="false"
				fi
			fi

      log ""
			log "Input the path of Connections Home (e.g.: /opt/IBM/Connections) [$conn_home_path]:"
			read temp
			if [ ! "$temp" == "" ]
			then
				conn_home_path=$temp
			fi
			echo "$temp" >> ${fn_backup_log} 2>&1
			log ""
		done

}

array=(
	conn_home_path
)

# Cannot recover batch parameters once processed with SHIFT
# so need to store script path and name for use later if needed
pushd $(dirname "${0}") > /dev/null
fnUpdateScriptPath_=$(pwd -L)
popd > /dev/null
fnUpdateScriptName_=`basename $0`

if [ -z "${fn_backup_log}" ]
then
	fn_backup_log=${fnUpdateScriptPath_}/fn-backup.log
	rm -rf $fn_backup_log
	create_log=$?
	if [ 0 -ne $create_log ]
	then
	  log ""
		log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
		exit 1
	fi
fi
# Check if we can write to log file
touch ${fn_backup_log}
create_log=$?
if [ 0 -ne $create_log ]
then
  log ""
	log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
	exit 1
fi

# Parse command line params
while [ $# -gt 0 ]
do
	if [ "${1:0:1}" = "-" ]
	then
		if [ "$1" == "-h" ]
		then
			usage
			exit 0
		else
			idx=`expr index "$1" "\="`
			if [ $idx -le 0 ]
			then
				log "Error: The input is malformed, please follow the usage."
				log ""
				usage
				exit 1
			fi
			paramName="${1:1:$idx-2}"
			paramName=${paramName//\./_}
			existed="false"
			for i in "${array[@]}"
			do
				if [ "$paramName" == "$i" ]
				then
					existed="true"
					break
				fi
			done
			if [ "$existed" == "true" ]
			then
				eval "${paramName}='${1:$idx}'"
			else
				usage $paramName
				exit 1
			fi
		fi
	fi
	shift
done

# get the platform information
platform=`uname`
linux_hardware=`uname -m`
if [ "${linux_hardware}" == "s390" ]
then
	platform="zLinux"
fi
if [ "${linux_hardware}" == "s390x" ]
then
	platform="zLinux"
fi
os=`echo $platform|tr A-Z a-z`

validate
if [ ! "0" == $? ]
then
	exit 1
fi


backupFileNet
if [ ! "0" == $? ]
then
	log "Error: Please read the log for detail of the problem, it is ${fn_backup_log}."
	log ""
	exit 1
fi

log "FileNet backup have been done successfully."

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
	echo "$*" 2>&1 | tee -a ${fn_ce_rollback_log}
}

mutedLog() {
	echo "$*" 2>&1 >> ${fn_ce_rollback_log}
}

doActionWithRC() {
	eval $* >> ${fn_ce_rollback_log} 2>&1
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
	log "was_dm_path		: Path of WAS Deployment Manager used for FileNet (e.g.: /opt/IBM/WebSphere/AppServer/profiles/Dmgr01)"
	log "was_admin_user		: Username of WAS administrator (e.g.: wasadmin)"
	log "was_admin_password	: Password of WAS administrator"
	log "conn_home_path: path of Connections home"
}

rollbackFNCE() {
	log "copying Engine-ws.ear back to ${conn_home_path}/FileNet/ContentEngine/tools/configure/profiles/CCM/ear"
	cp ${conn_home_path}/FileNet_backup/Engine-ws.ear ${conn_home_path}/FileNet/ContentEngine/tools/configure/profiles/CCM/ear

	log "copying Jace jar back to ${conn_home_path}/FileNet/ContentEngine/lib"
	cp ${conn_home_path}/FileNet_backup/lib/Jace*.jar ${conn_home_path}/FileNet/ContentEngine/lib
}

validate() {

	firstTime_="true"
	while [ ! -f "$was_dm_path/bin/wsadmin.sh" ]
	do
		if [ -z "$was_dm_path" ]
		then
			if [ ! "$firstTime_" == "true" ]
			then
				log "Error: The path of WAS Deployment Manager is empty."
				log ""
			else
				firstTime_="false"
			fi
		else
			if [ ! "$firstTime_" == "true" ]
			then
				log "Error: $was_dm_path is not the path of WAS Deployment Manager or there is no Cbin/wsadmin.sh under the profile folder."
				log ""
			else
				firstTime_="false"
			fi
		fi

		log "Input the path of WAS Deployment Manager (e.g.: /opt/IBM/WebSphere/AppServer/profiles/Dmgr01) [$was_dm_path]:"
		read temp
		if [ ! "$temp" == "" ]
		then
			was_dm_path=$temp
		fi
		echo "$temp" >> ${fn_ce_rollback_log} 2>&1
		log ""
	done

	# config java environment
	export JAVA_HOME=${was_dm_path}/../../java
	while [ ! -f "$JAVA_HOME/bin/java" ]
	do
		log "Error: JAVA_HOME is not a valid path for JDK root folder."
		log "Please input the valid JAVA_HOME, e.g: /opt/IBM/WebSphere/AppServer/java :"
		read JAVA_HOME
	done
	log ""
	java="$JAVA_HOME/bin/java"

	firstTime_="true"
	while [ -z "$was_admin_user" ]
	do
		if [ ! "$firstTime_" == "true" ]
		then
			log "Error: The username of WAS administrator is empty."
			log ""
		else
			firstTime_="false"
		fi

		log "Input the username of WAS administrator (e.g.: wasadmin) [$was_admin_user]:"
		read temp
		if [ ! "$temp" == "" ]
		then
			was_admin_user=$temp
		fi
		echo "$temp" >> ${fn_ce_rollback_log} 2>&1
		log ""
	done

	firstTime_="true"
	while [ -z "$was_admin_password" ]
	do
		if [ ! "$firstTime_" == "true" ]
		then
			log "Error: The password of WAS administrator is empty."
			log ""
		else
			firstTime_="false"
		fi

		log "Input the password of WAS administrator:"
		read temp
		was_admin_password=$temp
		echo "***" >> ${fn_ce_rollback_log} 2>&1
		log ""
	done

	pushd "$fnUpdateScriptPath_/../lib"
	cp=".:`pwd`/*"
	popd

	log "${java} -classpath ${cp} com.ibm.connections.install.FilenetUpdateUtil \"${was_dm_path}\""
	msgs="`${java} -classpath ${cp} com.ibm.connections.install.FilenetUpdateUtil \"${was_dm_path}\" 2>&1`"
	mutedLog "$msgs"
	msgs_error="`echo ${msgs} | grep -E \"Error|Usage\"`"
	msgs_exception="`echo ${msgs} | grep -E \"ClassNotFoundException\"`"
	if [ -n "${msgs_exception}" ]
	then
		log ""
		log "Solution: Please copy the \"lib\" folder in ccm-install.jar to \"$fnUpdateScriptPath_/..\"."
		log "If you do not understand the above solution well, please turn to the documentation for what you should do to solve the problem."
		log ""
	fi
	if [ -n "${msgs_error}" ]
	then
		log "Error: Deployment Manager is inaccessible, please ensure:"
		log "1. The username and password you provided is correct."
		log "2. The Deployment Manager is running."
		log ""
		exit 1
	fi

	dm_host_name="localhost"
	dm_soap_port="${msgs}"

	# start validate the username and password
	cmd_="${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP"
	cmd_="$cmd_ -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password}"
	cmd_="$cmd_ -c \"sys.exit()\""
	cmd__="${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP"
	cmd__="$cmd__ -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password \"***\""
	cmd__="$cmd__ -c \"sys.exit()\""
	log "$cmd__"
	msgs="`$cmd_ | grep -E \"Exception|WASX7008E\"`"
	if [ ! "${msgs}" == "" ]
	then
		log "Error: Deployment Manager is inaccessible, please ensure the username and password you provided is correct."
		log ""
		exit 1
	fi

	log "The validation of Deployment Manager is passed."

	# config Connections home folder
	firstTime_="true"
	while [ ! -f "$conn_home_path/FileNet_backup/Engine-ws.ear" ] || [ ! -f "$conn_home_path/FileNet_backup/lib/Jace.jar" ] || [ ! -f "$conn_home_path/FileNet_backup/lib/Jace_t.jar" ]
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
				log "Error: $conn_home_path is not the path of Connections Home or there is no Engine-ws.ear or Jace.jar or Jace_t.jar under filenet backup folder of the Connections home."
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
		echo "$temp" >> ${fn_ce_rollback_log} 2>&1
		log ""
	done

}

array=(
	was_dm_path
	was_admin_user
	was_admin_password
	conn_home_path
)

# Cannot recover batch parameters once processed with SHIFT
# so need to store script path and name for use later if needed
pushd $(dirname "${0}") > /dev/null
fnUpdateScriptPath_=$(pwd -L)
popd > /dev/null
fnUpdateScriptName_=`basename $0`

if [ -z "${fn_ce_rollback_log}" ]
then
	fn_ce_rollback_log=${fnUpdateScriptPath_}/fn-ce-rollback.log
	rm -rf $fn_ce_rollback_log
	create_log=$?
	if [ 0 -ne $create_log ]
	then
	  log ""
		log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
		exit 1
	fi
fi
# Check if we can write to log file
touch ${fn_ce_rollback_log}
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


rollbackFNCE

export WAS_HOME=${was_dm_path}
export MY_HOME=${conn_home_path}
export set_fn_anonymous=n

log ""
log "Rolling back FileNet Content Engine..."
log ""

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${conn_home_path}/lib/lccfg.jar" \
		-javaoption "-Dpython.path=${conn_home_path}/lib" \
    -f "${conn_home_path}/FileNet.update/scripts/deploy_fn_apps.py" "${conn_home_path}/cfg.py" "rollbackce" >> ${fn_ce_rollback_log} 2>&1
ce_deploy_code=$?
log "return code: $ce_deploy_code"
if [ 0 -ne $ce_deploy_code ]
then
	log ""
	log "Error: Please read the log for detail of the problem, it is ${fn_ce_rollback_log}."
	log ""
	exit 1
fi

log ""
log "Updating FileNet Content Engine configuration ..."
log ""

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${conn_home_path}/lib/lccfg.jar" \
		-javaoption "-Dpython.path=${conn_home_path}/lib" \
    -f "${conn_home_path}/FileNet.update/scripts/deploy_fn_apps.py" "${conn_home_path}/cfg.py" "config_ce" >> ${fn_ce_rollback_log} 2>&1
ce_config_code=$?
if [ 0 -ne $ce_config_code ]
then
    log ""
		log "Error: Please read the log for detail of the problem, it is ${fn_ce_rollback_log}."
		exit 1
else
	  log "IBM FileNet Content Engine have been configured successfully."
fi

log "FileNet CE Fixpacks has been rollbacked successfully."

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
	echo "$*" 2>&1 | tee -a ${fn_fncs_ceclient_rollback_log}
}

mutedLog() {
	echo "$*" 2>&1 >> ${fn_fncs_ceclient_rollback_log}
}

doActionWithRC() {
	eval $* >> ${fn_fncs_ceclient_rollback_log} 2>&1
}

usage() {
	if [ ! "$*" == "" ]
	then
		log ""
		log "Error: $* is an unrecognized parameter."
	fi
	log "Usage: $fnrollbackScriptName_ [-param=value] ... [-paramN=valueN]"
	log ""
	log "Available parameters to set:"
	log "was_dm_path		: Path of WAS Deployment Manager used for FileNet (e.g.: /opt/IBM/WebSphere/AppServer/profiles/Dmgr01)"
	log "was_admin_user		: Username of WAS administrator (e.g.: wasadmin)"
	log "was_admin_password	: Password of WAS administrator"
	log "conn_home_path  : path of Connections home"
	log "doSetAnonymous  : y or n"
	log "anonymous_user  : anonymous user name if doSetAnonymous is y"
	log "anonymous_password  : anonymous user password if doSetAnonymous is y"
}

rollbackFNCS() {
	log "copying navigatorEAR back to ${conn_home_path}/FNCS/configure/deploy"
	cp ${conn_home_path}/FileNet_backup/navigatorEAR.ear ${conn_home_path}/FNCS/configure/deploy

	log "copying fncs-sitePrefs ${conn_home_path}/FNCS/configure/explodedformat/fncs/WEB-INF/classes"
	cp ${conn_home_path}/FileNet_backup/fncs-sitePrefs.properties ${conn_home_path}/FNCS/configure/explodedformat/fncs/WEB-INF/classes

	log "copyinging CE_API back to ${conn_home_path}/FNCS/configure/CE_API"
	cp -r ${conn_home_path}/FileNet_backup/CE_API ${conn_home_path}/FNCS/configure

	log "copying profile CCM back to ${conn_home_path}/FNCS/configure/profiles/CCM"
	cp -r ${conn_home_path}/FileNet_backup/fncs/CCM  ${conn_home_path}/FNCS/configure/profiles
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
				log "Error: $was_dm_path is not the path of WAS Deployment Manager or there is no bin/wsadmin.sh under the profile folder."
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
		echo "$temp" >> ${fn_fncs_ceclient_rollback_log} 2>&1
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
		echo "$temp" >> ${fn_fncs_ceclient_rollback_log} 2>&1
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
		echo "***" >> ${fn_fncs_ceclient_rollback_log} 2>&1
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
		log "Error: SOAP port of Deployment Manager is unavailable. The reason and solution are recorded above."
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
		log "Error: Deployment Manager is inaccessible, please ensure:"
		log "1. The username and password you provided is correct."
		log "2. The Deployment Manager is running."
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
		echo "$temp" >> ${fn_fncs_ceclient_rollback_log} 2>&1
		log ""
	done

# add anonymous user part for rollback
  if [ ! -z "$doSetAnonymous" ] || [ "$doSetAnonymous" != "y" ]  || [ "$doSetAnonymous" != "Y" ] || [ "$doSetAnonymous" != "n" ] || [ "$doSetAnonymous" != "N" ]
  then
	  until [ "$doSetAnonymous" == "y" ] || [ "$doSetAnonymous" == "Y" ] || [ "$doSetAnonymous" == "n" ] || [ "$doSetAnonymous" == "N" ]
	  do
		  log ""
		  log "Specify the anonymous username and password for FileNet deployment - [y]es / [n]o:"
		  read temp
		  doSetAnonymous=`echo "$temp" | tr '[:upper:]' '[:lower:]'`
	  done
  fi

  if [ "$doSetAnonymous" == "y" ] || [ "$doSetAnonymous" == "Y" ]
  then
	  firstTime_="true"
	  while [ -z "$anonymous_user" ]
	  do
		  if [ ! "$firstTime_" == "true" ]
		  then
			  log "Error: FileNet Anonymouse user is empty."
			  log ""
		  else
			  firstTime_="false"
		  fi

		  log "Enter the FileNet Anonymouse user (e.g.: wasadmin) [$anonymous_user]:"
		  read temp
		  if [ ! "$temp" == "" ]
		  then
			  anonymous_user=$temp
		  fi
		  echo "$temp" >> ${fn_fncs_ceclient_rollback_log} 2>&1
		  log ""
	  done

  	firstTime_="true"
	  while [ -z "$anonymous_password" ]
	  do
		  if [ ! "$firstTime_" == "true" ]
		  then
			  log "Error: The password of FileNet Anonymouse user is empty."
			  log ""
		  else
			  firstTime_="false"
		  fi

		  log "Enter the FileNet Anonymouse user password:"
		  read temp
		  anonymous_password=$temp
		  echo "***" >> ${fn_fncs_ceclient_rollback_log} 2>&1
		  log ""
	  done

	  export set_fn_anonymous=y
	  export fn_anonymous=${anonymous_user}
	  export fn_anonymous_password=${anonymous_password}
  else
	  export set_fn_anonymous=n
  fi

}

array=(
	was_dm_path
	was_admin_user
	was_admin_password
	conn_home_path
	doSetAnonymous
	anonymous_user
	anonymous_password
)

# Cannot recover batch parameters once processed with SHIFT
# so need to store script path and name for use later if needed
pushd $(dirname "${0}") > /dev/null
fnUpdateScriptPath_=$(pwd -L)
popd > /dev/null
fnUpdateScriptName_=`basename $0`

if [ -z "${fn_fncs_ceclient_rollback_log}" ]
then
	fn_fncs_ceclient_rollback_log=${fnUpdateScriptPath_}/fn-fncs-ceclient-rollback.log
	rm -rf $fn_fncs_ceclient_rollback_log
	create_log=$?
	if [ 0 -ne $create_log ]
	then
	  log ""
		log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
		exit 1
	fi
fi
# Check if we can write to log file
touch ${fn_fncs_ceclient_rollback_log}
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

rollbackFNCS

export WAS_HOME=${was_dm_path}
export MY_HOME=${conn_home_path}

log ""
log "Starting to rollback FileNet Content Engine Client and Navigator ..."
log ""

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${conn_home_path}/lib/lccfg.jar" \
		-javaoption "-Dpython.path=${conn_home_path}/lib" \
    -f "${conn_home_path}/FileNet.update/scripts/deploy_fn_apps.py" "${conn_home_path}/cfg.py" "rollbackfncs" >> ${fn_fncs_ceclient_rollback_log} 2>&1
fncs_return_code=$?
if [ 0 -ne $fncs_return_code  ]
then
  log ""
	log "IBM CONTENT NAVIGATOR rollback failed, exit."
	log "Error: Please read the log for detail of the problem, it is ${fn_fncs_ceclient_rollback_log}."
	log ""
	exit 1
fi


${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${conn_home_path}/lib/lccfg.jar" \
		-javaoption "-Dpython.path=${conn_home_path}/lib" \
    -f "${conn_home_path}/FileNet.update/scripts/deploy_fn_apps.py" "${conn_home_path}/cfg.py" "config_fncs" >> ${fn_fncs_ceclient_rollback_log} 2>&1
config_fncs_code=$?
if [ 0 -ne $config_fncs_code ]
then
    log ""
		log "IBM CONTENT NAVIGATOR config failed, exit."
		log "Error: Please read the log for detail of the problem, it is ${fn_fncs_ceclient_rollback_log}"
		exit 1
else
	  log "IBM CONTENT NAVIGATOR have been configured successfully."
fi

log "FileNet FNCS & CEClient Fixpacks have been rollbacked successfully."

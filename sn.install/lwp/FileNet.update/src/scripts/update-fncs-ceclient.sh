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
	echo "$*" 2>&1 | tee -a ${fn_fncs_ceclient_update_log}
}

mutedLog() {
	echo "$*" 2>&1 >> ${fn_fncs_ceclient_update_log}
}

doActionWithRC() {
	eval $* >> ${fn_fncs_ceclient_update_log} 2>&1
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
	log "was_dm_path  : Path of WAS Deployment Manager used for FileNet (e.g.: /opt/IBM/WebSphere/AppServer/profiles/Dmgr01)"
	log "was_admin_user  : Username of WAS administrator (e.g.: wasadmin)"
	log "was_admin_password	 : Password of WAS administrator"
	log "conn_home_path  : path of Connections home"
	log "doSetAnonymous  : y or n"
	log "anonymous_user  : anonymous user name if doSetAnonymous is y"
	log "anonymous_password  : anonymous user password if doSetAnonymous is y"
	log "fncs_fp_installer_location  : Location of IBM CONTENT NAVIGATOR (which now includes FNCS) Fixpack installer (e.g.: /opt/filenet-fixpack/IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-${platform}.bin)"
	log "ceclient_fp_installer_location  : Location of FileNet CE Client Fixpack installer (e.g.: /opt/filenet-fixpack/5.2.1.7-P8CPE-CLIENT-${os_c}-FP007.BIN)"
}

validate() {
	if [ -z "$IATEMPDIR" ]
	then
		tmp_size=$(df /tmp | tail -1 | awk '{print $3}')
	else
		tmp_size=$(df /$IATEMPDIR | tail -1 | awk '{print $3}')
	fi
	tmp_a_size=$((tmp_size-220997*3))
	if [ $tmp_a_size -lt 0 ]
	then
		log "Error: To install IBM CONTENT NAVIGATOR (which now includes FNCS) & CE Client Fixpacks, there should be at least 700MB under directory /tmp. Please try either of the following methods:"
		log "1. Clean up directory /tmp and ensure it meets the free space requirement."
		log "2. Run the instruction: export IATEMPDIR=/your/free/space/directory ,while /your/free/space/directory is the directory you assign to place the temporary files during installation."
		log ""
		exit 1
	fi

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

		log "Enter the WAS Deployment Manager path (e.g.: /opt/IBM/WebSphere/AppServer/profiles/Dmgr01) [$was_dm_path]:"
		read temp
		if [ ! "$temp" == "" ]
		then
			was_dm_path=$temp
		fi
		echo "$temp" >> ${fn_fncs_ceclient_update_log} 2>&1
		log ""
	done

	# config java environment
	export JAVA_HOME=${was_dm_path}/../../java
	while [ ! -f "$JAVA_HOME/bin/java" ]
	do
		log "Error: JAVA_HOME is not a valid path for JDK root folder."
		log "Enter a valid JAVA_HOME, e.g: /opt/IBM/WebSphere/AppServer/java :"
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

		log "Enter the WAS administrator username (e.g.: wasadmin) [$was_admin_user]:"
		read temp
		if [ ! "$temp" == "" ]
		then
			was_admin_user=$temp
		fi
		echo "$temp" >> ${fn_fncs_ceclient_update_log} 2>&1
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

		log "Enter the WAS administrator password:"
		read temp
		was_admin_password=$temp
		echo "***" >> ${fn_fncs_ceclient_update_log} 2>&1
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
	log ""
	echo "DM SOAP port: ${dm_soap_port}"

	# start validate the username and password
	log ""
	log "Validating Deployment Manager ..."
	log ""
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
  log ""
	log "The validation of Deployment Manager succeeded."

	# config Connections home folder
    log ""
		firstTime_="true"
		while [ ! -f "$conn_home_path/tmp/ceclient_silent_install_unix.txt" -o ! -f "$conn_home_path/tmp/FNCS-2.0.0.0-unix_SilentInstall.properties" ]
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
					log "Error: $conn_home_path is not the path of Connections Home or there is no ceclient_silent_install_unix.txt or FNCS-2.0.0.0-unix_SilentInstall.properties under tmp of the Connections home folder."
					log ""
				else
					firstTime_="false"
				fi
			fi

			log "Input the path of Connections Home (e.g.: /opt/IBM/Connections) [$conn_home_path]:"
			read temp
			if [ ! "$temp" == "" ]
			then
				conn_home_path=$temp
			fi
			echo "$temp" >> ${fn_fncs_ceclient_update_log} 2>&1
			log ""
		done

		if [ ! -d "${conn_home_path}"/FileNet_backup ] || [ ! -f "${conn_home_path}"/FileNet_backup/Engine-ws.ear ] ||  [ ! -f "${conn_home_path}"/FileNet_backup/navigatorEAR.ear ]
		then
			log ""
			log "please run backup task before upgrade FileNet."
			log ""
			exit 1
		fi

		if [ ! -z "$doSetAnonymous" ] || [ "$doSetAnonymous" != "y" ] || [ "$doSetAnonymous" != "Y" ] || [ "$doSetAnonymous" != "n" ] || [ "$doSetAnonymous" != "N" ]
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
				echo "$temp" >> ${fn_fncs_ceclient_update_log} 2>&1
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
				echo "***" >> ${fn_fncs_ceclient_update_log} 2>&1
				log ""
			done
			export set_fn_anonymous=y
			export fn_anonymous=${anonymous_user}
			export fn_anonymous_password=${anonymous_password}
    else
			export set_fn_anonymous=n
		fi

	# fncs_os_validator=`echo $fncs_fp_installer_location | grep $platform`
		firstTime_="true"
		while [ ! -f "$fncs_fp_installer_location" ]
		do
			if [ -z "$fncs_fp_installer_location" ]
			then
				if [ ! "$firstTime_" == "true" ]
				then
					log "Error: The location of IBM CONTENT NAVIGATOR Fixpack installer is empty."
					log ""
				else
					firstTime_="false"
				fi
			else
				if [ ! "$firstTime_" == "true" ]
				then
					log "Error: $fncs_fp_installer_location is not the location of IBM CONTENT NAVIGATOR Fixpack installer."
					log ""
				else
					firstTime_="false"
				fi
			fi

      log ""
			log "Enter the location of IBM CONTENT NAVIGATOR (which now includes FNCS) Fixpack installer (e.g.: /opt/filenet-fixpack/IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-${platform}.bin):"
			read temp
			if [ ! "$temp" == "" ]
			then
				if [[ $temp == *IBM_CONTENT_NAVIGATOR* ]] && [[ $temp == *FP* ]] && [[ $temp == *bin ]]
				then
				    fncs_fp_installer_location=$temp
				else
					log "Error: $temp is not the location of FileNet FNCS Fixpack installer or the binary is wrong."
					firstTime_="true"
				fi
			fi
			echo "$temp" >> ${fn_fncs_ceclient_update_log} 2>&1
			log ""
		done

	if [ -z "$IATEMPDIR" ]
	then
		tmp_size=$(df /tmp | tail -1 | awk '{print $3}')
	else
		tmp_size=$(df /$IATEMPDIR | tail -1 | awk '{print $3}')
	fi
	tmp_a_size=$((tmp_size-3145728))
	if [ $tmp_a_size -lt 0 ]
	then
		log "Error: To install IBM CONTENT NAVIGATOR & CE Client Fixpacks, there should be at least 3GB under directory /tmp. Please try either of the following methods:"
		log "1. Clean up directory /tmp and ensure it meets the free space requirement."
		log "2. Run the instruction: export IATEMPDIR=/your/free/space/directory ,while /your/free/space/directory is the directory you assign to place the temporary files during installation."
		log ""
		exit 1
	fi


	firstTime_="true"
	# ceclient_os_validator=`echo $ceclient_fp_installer_location | grep $platform`
	while [ ! -f "$ceclient_fp_installer_location" ]
	do
		if [ -z "$ceclient_fp_installer_location" ]
		then
			if [ ! "$firstTime_" == "true" ]
			then
				log "Error: The location of FileNet CE Client Fixpack installer is empty."
				log ""
			else
				firstTime_="false"
			fi
		else
			if [ ! "$firstTime_" == "true" ]
			then
				log "Error: $ceclient_fp_installer_location is not the location of FileNet CE Client installer."
				log ""
			else
				firstTime_="false"
			fi
		fi

    log ""
		log "Enter the location of the FileNet CE Client Fixpack installer (e.g.: /opt/filenet-fixpack/5.2.1.7-P8CPE-CLIENT-${os_c}-FP007.BIN) [$ceclient_fp_installer_location]:"
		read temp
		if [ ! "$temp" == "" ]
		then
			if [[ $temp == *P8CPE-CLIENT* ]] && [[ $temp == *BIN ]]
			then
				  ceclient_fp_installer_location=$temp
			else
					log "Error: $temp is not the location of FileNet CE Client installer or the binary is wrong."
					firstTime_="true"
			fi
			# ceclient_os_validator=`echo $ceclient_fp_installer_location | grep $platform`
		fi
		echo "$temp" >> ${fn_fncs_ceclient_update_log} 2>&1
		log ""
	done
}

array=(
	was_dm_path
	was_admin_user
	was_admin_password
	conn_home_path
	doSetAnonymous
	anonymous_user
	anonymous_password
	fncs_fp_installer_location
	ceclient_fp_installer_location
)

# Cannot recover batch parameters once processed with SHIFT
# so need to store script path and name for use later if needed
pushd $(dirname "${0}") > /dev/null
fnUpdateScriptPath_=$(pwd -L)
popd > /dev/null
fnUpdateScriptName_=`basename $0`

if [ -z "${fn_fncs_ceclient_update_log}" ]
then
	fn_fncs_ceclient_update_log=${fnUpdateScriptPath_}/fn-fncs-ceclient-update.log
	rm -rf $fn_fncs_ceclient_update_log
	create_log=$?
	if [ 0 -ne $create_log ]
	then
	  log ""
		log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
		exit 1
	fi
fi
# Check if we can write to log file
touch ${fn_fncs_ceclient_update_log}
create_log=$?
if [ 0 -ne $create_log ]
then
  log ""
	log "User does NOT have permission to write log file in current folder, please grant privilege, and try again."
	exit 1
fi

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
os_c=`echo $platform|tr a-z A-Z`

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

validate
if [ ! "0" == $? ]
then
	exit 1
fi

if [ ! "$fncs_fp_installer_location" == "" ]
then
	cmd_fncs="$fncs_fp_installer_location -i silent -f $conn_home_path/tmp/FNCS-2.0.0.0-unix_SilentInstall.properties"
	log "$cmd_fncs"
	#doActionWithRC $cmd_fncs
	#log "return code: $?"
fi
if [ ! "$ceclient_fp_installer_location" == "" ]
then
	cmd_ceclient="$ceclient_fp_installer_location -i silent -f $conn_home_path/tmp/ceclient_silent_install_unix.txt"
	log "$cmd_ceclient"
	#doActionWithRC $cmd_ceclient
	#log "return code: $?"
fi


if [[ ! -x "$fncs_fp_installer_location" ]]
then
	  log ""
		log "user does NOT have permission to run $fncs_fp_installer_location."
		log ""
		exit 1
fi

if [ $platform == "AIX" ]; then
	 chmod u+x $fncs_fp_installer_location
	 if [ 0 -ne $? ]
	 then
		 log ""
		 log "user does NOT have permission to run $fncs_fp_installer_location."
	 	 exit 1
	 fi
fi

if [[ ! -x "$ceclient_fp_installer_location" ]]
then
	  log ""
		log "user does NOT have permission to run $ceclient_fp_installer_location."
		log ""
		exit 1
fi

if [ $platform == "AIX" ]; then
	 chmod u+x $ceclient_fp_installer_location
	 if [ 0 -ne $? ]
	 then
		 log ""
		 log "user does NOT have permission to run $ceclient_fp_installer_location."
	 	 exit 1
	 fi
fi

export WAS_HOME=${was_dm_path}
export MY_HOME=${conn_home_path}

log ""
log "installing FileNet Content Engine Client $ceclient_fp_installer_location and Navigator $fncs_fp_installer_location ..."
log ""

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -f "${conn_home_path}/FileNet.update/scripts/install_fn_fp.py" "ceclient" "${cmd_ceclient}" >> ${fn_fncs_ceclient_update_log} 2>&1
ceclient_return_code=$?
if [ 99 -eq $ceclient_return_code ]
then
	log "Node agent is not start yet, please make sure all node agents are start up before updating FileNet"
	exit 1
fi
if [ 0 -ne $ceclient_return_code ]
then
	  log "return code: $ceclient_return_code"
    log "Error: ce client installation failed, Please read the log for detail of the problem, it is ${fn_fncs_ceclient_update_log}."
		exit 1
else
	  log "IBM Content Engine Client have been installed successfully."
fi

if [ ! "${skip_navigator}" == "yes" ]
then
	${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
		-host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
		-javaoption "-Xmx512m" \
		-f "${conn_home_path}/FileNet.update/scripts/install_fn_fp.py" "fncs" "${cmd_fncs}" >> ${fn_fncs_ceclient_update_log} 2>&1
	fncs_return_code=$?
	log "fncs return code: $fncs_return_code"
	if [ 0 -ne $fncs_return_code ]
	then
		log "return code: $fncs_return_code"
		log "IBM CONTENT NAVIGATOR installation failed, exit."
		exit 1
	else
		log "IBM CONTENT NAVIGATOR have been installed successfully."
	fi
else
	log "Skip Install IBM CONTENT NAVIGATOR."
fi

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -f "${conn_home_path}/FileNet.update/scripts/restore_fn_config.py" "${conn_home_path}" >> ${fn_fncs_ceclient_update_log} 2>&1
restore_fn_config=$?
if [ 0 -ne $restore_fn_config ]
then
    log "IBM CONTENT NAVIGATOR profile restore failed, exit."
		exit 1
else
	  log "IBM CONTENT NAVIGATOR profile have been restored successfully."
fi

log "updating WAS Admin Username..."
$JAVA_HOME/bin/java -jar ${conn_home_path}/lib/lccfg.jar ${conn_home_path}/FNCS/configure/profiles/CCM/applicationserver.xml \
		 "/configuration/property[@name='ApplicationServerAdminUsername']/value/text()" ${was_admin_user}

log "updating WAS Admin Password..."
$JAVA_HOME/bin/java -jar ${conn_home_path}/lib/lccfg.jar ${conn_home_path}/FNCS/configure/profiles/CCM/applicationserver.xml \
		 "/configuration/property[@name='ApplicationServerAdminPassword']/value/text()" ${was_admin_password}

log ""
log "Deploying FileNet navigator ear ..."
log ""

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${conn_home_path}/lib/lccfg.jar" \
		-javaoption "-Dpython.path=${conn_home_path}/lib" \
    -f "${conn_home_path}/FileNet.update/scripts/deploy_fn_apps.py" "${conn_home_path}/cfg.py" "deploy_fncs"  >> ${fn_fncs_ceclient_update_log} 2>&1
deploy_fncs_code=$?
if [ 0 -ne $deploy_fncs_code ]
then
	  log "deploy taks return code: $deploy_fncs_code"
    log "IBM CONTENT NAVIGATOR deploy failed, exit."
		exit 1
else
	  log "IBM CONTENT NAVIGATOR have been deployed successfully."
fi

${was_dm_path}/bin/wsadmin.sh -lang jython -conntype SOAP \
    -host ${dm_host_name} -port ${dm_soap_port} -username ${was_admin_user} -password ${was_admin_password} \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${conn_home_path}/lib/lccfg.jar" \
		-javaoption "-Dpython.path=${conn_home_path}/lib" \
    -f "${conn_home_path}/FileNet.update/scripts/deploy_fn_apps.py" "${conn_home_path}/cfg.py" "config_fncs"  >> ${fn_fncs_ceclient_update_log} 2>&1
config_fncs_code=$?
if [ 0 -ne $config_fncs_code ]
then
    log "IBM CONTENT NAVIGATOR config failed, exit."
		exit 1
else
	  log "IBM CONTENT NAVIGATOR have been configured successfully."
fi

log "IBM CONTENT NAVIGATOR and CE Client have been upgraded successfully."

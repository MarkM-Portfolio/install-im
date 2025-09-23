#!/bin/sh
# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2010, 2018                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************
# Build: @BUILD_RELEASE@

WAS_HOME=$1
IC_HOME=$2
WAS_USER=$3
WAS_USER_PW=$4
WAS_PORT=$5
WAS_PROFILE_HOME=$6
USERJOB=$8
WAS_CELL_NAME=$9
IC_VERSION_FROM=${10}
IC_VERSION_TO=${11}

append() { 
    echo $* >> "${IC_HOME}/post-install-launcher.log"
}

do_lccbak(){ 
    # Back up LCC folder before UPDATE install
    local taskname="Backup LCC"
    local srcfolder=${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/*
    local targetfolder=${IC_HOME}/bak/${IC_VERSION_FROM}/LCC
    append "INFO: Do LCC backup before normal UPDATE install"
    append "INFO: Doing ${taskname} from ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config to ${targetfolder}"
    if [ ! -d "${targetfolder}" ]; then
        mkdir -p ${targetfolder} >> ${IC_HOME}/post-install-launcher.log 2>&1
        if [ $? -ne 0 ]; then
            append "WARN: ${taskname} - failed to create folder ${targetfolder}!"
            return 1
        else
            append "INFO: ${taskname} - created folder ${targetfolder}."
        fi
    fi
    
    cp -rf ${srcfolder} ${targetfolder} 2>&1
    if [ $? -ne 0 ]; then
        append "ERROR: ${taskname} - failed to copy folder ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config to folder ${targetfolder}! Check your OS settings, such as permission, disk space, etc."
        return 1
    else
        append "INFO: ${taskname} - successfully copied folder ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config to folder ${targetfolder}."
        return 0
    fi
}

do_fltbak(){ 
    append "INFO: Do FLT backup"
    # This is to make existing technote still valid
    if [ ! -d "${IC_HOME}/FLT_backup" ]; then
        mkdir -p ${IC_HOME}/FLT_backup >> ${IC_HOME}/post-install-launcher.log 2>&1
    fi
    if [ -d "${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/profiles" ]; then
        cp -rf ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/profiles ${IC_HOME}/FLT_backup >> ${IC_HOME}/post-install-launcher.log 2>&1
    fi
    if [ -d "${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/extern" ]; then
        cp -rf ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/extern ${IC_HOME}/FLT_backup >> ${IC_HOME}/post-install-launcher.log 2>&1
    fi
    if [ -d "${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/notifications" ]; then
        cp -rf ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/notifications ${IC_HOME}/FLT_backup >> ${IC_HOME}/post-install-launcher.log 2>&1
    fi
    if [ -d "${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/notifications_v2" ]; then
        cp -rf ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config/notifications_v2 ${IC_HOME}/FLT_backup >> ${IC_HOME}/post-install-launcher.log 2>&1
    fi
}

do_update(){ 
    append "INFO: Starting update deployment..."
    append "${IC_HOME}/bin/lc-install.sh -update -host localhost -port ${WAS_PORT} -user ${WAS_USER} -password PASSWORD_REMOVED"
    ${IC_HOME}/bin/lc-install.sh -update -host localhost -port ${WAS_PORT} -user ${WAS_USER} -password ${WAS_USER_PW}
    return $?
    
}

do_install(){ 
    append "INFO: Starting install deployment..."
    append "${IC_HOME}/bin/lc-install.sh -host localhost -port ${WAS_PORT} -user ${WAS_USER} -password PASSWORD_REMOVED"
    ${IC_HOME}/bin/lc-install.sh -host localhost -port ${WAS_PORT} -user ${WAS_USER} -password ${WAS_USER_PW}
    return $?
}

do_normalupdate(){ 
    append "INFO: Do normal UPDATE Install..."
    # back up LCC folder, if failed, set flag and exit
    do_lccbak
    if [ $? -ne 0 ]; then
        touch ${IC_HOME}/update_failed
        append "ERROR: Connections ${USERJOB} deployment failed at LCC backup phase! Check the OS settings, such as permission, disk space, etc."
        exit 1
    else
        # back up FLT files
        do_fltbak
        do_update
        exitcode=$?
        if [ ${exitcode} -ne 0 ]; then
            touch ${IC_HOME}/update_failed
            append "ERROR: Connections ${USERJOB} deployment failed with code ${exitcode} at phase1! Check the logs."
            exit ${exitcode}
        else
            do_install
            exitcode=$?
            if [ ${exitcode} -ne 0 ]; then
                touch ${IC_HOME}/update_failed
                append "ERROR: Connections ${USERJOB} deployment failed with code ${exitcode} at phase2! Check the logs."
                exit ${exitcode}
            else
                do_cfgpybak
                append "INFO: Connections ${USERJOB} deployment finished SUCCESSFULLY!"
                exit 0
            fi
        fi
    fi
}

do_normalrollback() {
    append "INFO: Do normal ROLLBACK Install..."
    if [ -f ${IC_HOME}/bak/${IC_VERSION_TO}/CFGBAK/cfg.py ]; then
        append "INFO: Restored cfg.py."      
        rm -f ${IC_HOME}/cfg.py
        cp -f ${IC_HOME}/bak/${IC_VERSION_TO}/CFGBAK/cfg.py ${IC_HOME}/
        append "INFO: Restored LCC."
        cp -rf ${IC_HOME}/bak/${IC_VERSION_TO}/LCC/* ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config
        
        do_update
        exitcode=$?
        if [ ${exitcode} -ne 0 ]; then
            touch ${IC_HOME}/rollback_failed
            append "ERROR: Connections ${USERJOB} deployment failed with code ${exitcode} at phase1! Check the logs."
            exit ${exitcode}
        else
            do_install
            exitcode=$?
            if [ ${exitcode} -ne 0 ]; then
                touch ${IC_HOME}/rollback_failed
                append "ERROR: Connections ${USERJOB} deployment failed with code ${exitcode} at phase2! Check the logs."
                exit ${exitcode}
            else
                if [ -d ${IC_HOME}/bak/${IC_VERSION_FROM} ]; then 
                	rm -rf ${IC_HOME}/bak/${IC_VERSION_FROM}
                	append "INFO: Removed backup folder ${IC_HOME}/bak/${IC_VERSION_FROM}."
                fi
                append "INFO: Connections ${USERJOB} deployment finished SUCCESSFULLY!"
                exit 0
            fi
        fi
    else
        append "WARN: No previous cfg.py, normal rollback is impossible, please do manual rollback."
        exit 2
    fi
}


do_updatefailautorollback(){
    append "WARN: Restoring from failed UPDATE install..." 
    
    cp -f ${IC_HOME}/bak/${IC_VERSION_FROM}/CFGBAK/cfg.py ${IC_HOME}/ && append "INFO: Restored cfg.py."
    
    cp -rf ${IC_HOME}/bak/${IC_VERSION_FROM}/LCC/* ${WAS_PROFILE_HOME}/config/cells/${WAS_CELL_NAME}/LotusConnections-config && append "INFO: Restored LCC."
    
    do_update
    exitcode=$?
    if [ ${exitcode} -ne 0 ]; then
        touch ${IC_HOME}/updateautorollback_failed
        append "ERROR: update auto rollback failed with code ${exitcode} at phase1! Check the logs."
        exit ${exitcode}
    else
        do_install
        exitcode=$?
        if [ ${exitcode} -ne 0 ]; then
            touch ${IC_HOME}/updateautorollback_failed
            append "ERROR: update auto rollback failed with code ${exitcode} at phase2! Check the logs."
            exit ${exitcode}
        else
            if [ -f ${IC_HOME}/updateautorollback_failed ]; then
                rm -rf ${IC_HOME}/updateautorollback_failed
            fi
            if [ -f ${IC_HOME}/update_failed ]; then
                rm -rf ${IC_HOME}/update_failed
                append "INFO: Removed update_failed flag file"
            fi
            if [ -f ${IC_HOME}/rollback_failed ]; then
                rm -rf ${IC_HOME}/rollback_failed
            fi
            if [ -d ${IC_HOME}/bak/${IC_VERSION_FROM}/LCC ]; then
            	rm -rf ${IC_HOME}/bak/${IC_VERSION_FROM}/LCC
            fi
            append "WARN: Connections restored from failed update deployment, please check install log for the failure reason, try to correct it and retry, or contact HCL Support for help."
            exit 0
        fi
    fi
}

do_cfgpybak(){ 
    append "INFO: Do cfg.py backup after install is successful."
    if [ ! -d "${IC_HOME}/bak/${IC_VERSION_TO}/CFGBAK" ]; then
        mkdir -p ${IC_HOME}/bak/${IC_VERSION_TO}/CFGBAK
    fi

    cp -f ${IC_HOME}/cfg.py ${IC_HOME}/bak/${IC_VERSION_TO}/CFGBAK && append "INFO: Backup cfg.py successfully."
}

add_sep(){ 
    append "........"
    append "........"
    append "........"
}

do_normalinstall(){ 
    append "INFO: doing ${USERJOB} install..."
    do_install
    exitcode=$?
    if [ ${exitcode} -ne 0 ]; then
        touch ${IC_HOME}/install_failed
        append "ERROR: Connections ${USERJOB} deployment failed with code ${exitcode}!"
        exit ${exitcode}
    else
        do_cfgpybak
        append "INFO: Connections ${USERJOB} deployment finished SUCCESSFULLY!"
        exit 0
    fi
}

# main
UNAME_OS=`uname`
if [ "$UNAME_OS" = "OS400" ]; then
    touch -C 819 ${IC_HOME}/post-install-launcher.log
fi

add_sep
append "********`date`********"
append "INFO: WAS_HOME=${WAS_HOME}"
append "INFO: IC_HOME=${IC_HOME}"
append "INFO: WAS_USER=${WAS_USER}"
append "INFO: WAS_PORT=${WAS_PORT}"
append "INFO: WAS_PROFILE_HOME=${WAS_PROFILE_HOME}"
append "INFO: USERJOB=${USERJOB}"
append "INFO: WAS_CELL_NAME=${WAS_CELL_NAME}"
append "INFO: IC_VERSION_FROM=${IC_VERSION_FROM}"
append "INFO: IC_VERSION_TO=${IC_VERSION_TO}"

export WAS_PROFILE_HOME=${WAS_PROFILE_HOME}

if [ $7 = 1 ]; then
    append "INFO: Skip jython installer."
    exit 0
fi

#update sso domain name begin
export Connections_HOME=${IC_HOME}
${WAS_PROFILE_HOME}/bin/wsadmin.sh -lang jython -conntype SOAP -host localhost -port ${WAS_PORT} -user ${WAS_USER} -password ${WAS_USER_PW} -javaoption "-Duser.language=en" -javaoption "-Duser.country=US" -f ${IC_HOME}/bin/fetch_sso.py >> ${IC_HOME}/post-install-launcher.log
#update sso domain name end
	
if [ "${USERJOB}" == "UPDATE" ]; then
    if [ -f "${IC_HOME}/update_failed" ]; then
        do_updatefailautorollback
    else
        do_normalupdate
    fi
else
    if [ "${USERJOB}" == "ROLLBACK" ]; then
        do_normalrollback
    else
        do_normalinstall
    fi
fi

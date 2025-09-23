#!/bin/sh
# ***************************************************************** 
#                                                                   
# IBM Licensed Material                                              
#                                                                   
# Copyright IBM Corp. 2004, 2016                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

# THIS PRODUCT CONTAINS RESTRICTED MATERIALS OF IBM
# All Rights Reserved * Licensed Materials - Property of IBM
# Configuration Based Update Installer
#----------------------------------------------------------------------------------
# Command Descriptions
# 
# The '-fix' option specifies an eFix update.
# The '-fixpack' option specifies a FixPack update.
# The '-install' option specifies an install action.
# The '-uninstall' option specifies an uninstall action.
# The '-installDir' option specifies the product install root location.
# The '-fixDir' option specifies the eFix directory.
# The '-fixpackDir' option specifies the FixPack directory.
# The '-fixes' specifies an eFix to install or uninstall.
# The '-fixpackID' specifies a FixPack to install or uninstall.
# The '-fixDetails' option displays eFix detail information.
# The '-fixpackDetails' option displays FixPack detail information.
# The '-wasUserId' specifies a valid WAS administrator ID.
# The '-wasPassword' specifies a valid WAS administrator password.
# The '<propertyFile>.properties' option specifies an externally supplied parameters file.
#----------------------------------------------------------------------------------
# Launch Arguments:
#
# For EFix Processing:
#      updatePortal.sh <propertiesFile>
#               ( -installDir <product install root>
#		  [ -fix ]
#                 [ -fixDir <fix repository root> ]
#                 [ -install | -uninstall | -uninstallAll ]
#                 [ -fixes <ifix ID> ]
#                 [ -wasUserId <userid> ]
#                 [ -wasPassword <password> ]
#                 [ -fixDetails ]
#                 [ -configProperties propertiesFile ] |
#               ( -help | -? | /help | /? | -usage )
#
# For FixPack Processing:
#  updatePortal.sh <propertiesFile>
#	        ( -installDir <product install root>
#		  		[ -fixpack ]
#               [ ( -install | -uninstall
# 		      		-fixpackDir <fixpack repository root> ]
# 	              	-fixpackID <FixPack ID> ]
#	          	[ -wasUserId <userid> ]
#	          	[ -wasPassword <password> ]
#             	[ -configProperties propertiesFile ]
#             	[ -fixpackDetails ] ) |
#            ( -help | -? | /help | /? | -usage )
#----------------------------------------------------------------------------------

# set DEBUG_UPDATE=yes to turn on debugging statements
DEBUG_UPDATE=

success_rc=0
fail_rc=$?

LaunchTitle=$0

launchInstaller=
useLocalJDK=
prepareJDK=
useJavaHome=
checkSetup=
callSetup=
checkClientSetup=
callClientSetup=
failSetup=
testJDK=
copyJDK=
failedCopyJDK=
okCopyJDK=
fixversionfiles=1

currentDir=`dirname $0`        
PLATFORM=`uname`

NOW=`date '+%y_%m_%d_%H_%M_%S'`
#----------------------------------------------------------------------------------
#  Initialization and setup
#----------------------------------------------------------------------------------
echo Start of [ "$LaunchTitle" ]
echo Build @BLD_NUMBER@
echo       

    if [ "$PLATFORM" = "OS400" ]
    then
        if [ "$WAS_PROD_HOME" ] 
        then 
            JAVA_HOME="/QIBM/ProdData/Java400/jdk14"
            WP_CP_EXTRA="/QIBM/ProdData/java400/jt400ntv.jar:/QIBM/ProdData/http/public/jt400/lib/jt400.jar"
            WP_JAVA_PARAMS="$WP_JAVA_PARAMS -Djava.version=1.4 -DpermissionHelper=com.ibm.websphere.update.delta.OS400PermissionHelper"
            INSTALLDIR=null
            NEXT=FALSE
            #cp "$WAS_PROD_BASE"/java/extlib/log.jar "$currentDir"/log.jar
            
            for VAR in $@
            do
                    if [ TRUE = "$NEXT" ]
                    then
                             INSTALLDIR=$VAR
                             NEXT=FALSE
                             break
                    fi

                    if [ -INSTALLDIR = "`echo $VAR | tr '[a-z]' '[A-Z]'`" ]
                    then
                             NEXT=TRUE
                    fi
            done

            #echo $INSTALLDIR
            
            # Not needed for PUI6 
            #if [ ! -f "$WAS_PROD_HOME"/lib/xerces.jar ] 
            #then
            #   cp $INSTALLDIR/jcr/rm/WEB-INF/lib/xerces.jar $WAS_PROD_HOME/lib/xerces.jar
            #fi        
        
            # Keep EXPRESS.product for wp50x but not for WCS/WSE/WP6.X
            if [ -f "$INSTALLDIR"/version/EXPRESS.product ] 
            then
                if [ -f "$INSTALLDIR"/version/MP.product ] 
                then
                    echo EXPRESS and MP product files found, renaming EXPRESS.product
                    mv "$INSTALLDIR"/version/EXPRESS.product "$INSTALLDIR"/version/EXPRESS.product."${NOW}"
                fi 
            fi 

            callSetup=1
            
        else
            echo The WAS_PROD_HOME env variable MUST be set.  
            echo Exiting.
            echo
            echo Set WAS_PROD_HOME=/QIBM/ProdData/WebSphere/AppServer/V6/Base for WebSphere version 6
            echo Set WAS_PROD_HOME=/QIBM/ProdData/WebSphere/AppServer/V6/ND for WebSphere version 6 Network Deployment
            echo
            echo example: export WAS_PROD_HOME=/QIBM/ProdData/WebSphere/AppServer/V6/Base
            exit $fail_rc
        fi
    else
	
		PLATFORM=`uname`
		LINUX_HARDWARE=`uname -m`
		if [ "${LINUX_HARDWARE}" = "s390" ]
		then
			PLATFORM="s390"
		fi
		if [ "${LINUX_HARDWARE}" = "s390x" ]
		then
			PLATFORM="s390x"
		fi
		case ${PLATFORM} in
			Linux)
				needSetup=1
				;;
			AIX)
			    needSetup=1
				launchInstaller=1
				;;
			s390)
				needSetup=1
				;;
			s390x)
				needSetup=1
				;;
		esac
		if [ "$needSetup" ]
		then
			if [ "$WAS_HOME" ] 
			then 
				if [ -f "$WAS_HOME"/bin/setupCmdLine.sh ]
				then
					echo Attempting to locate setupClient.sh
					echo
					callSetup=1
				else
					failSetup=1
				fi
			else
				echo The WAS_HOME env variable MUST be set.  
				echo Exiting.
				echo
				exit $fail_rc
			fi
		fi
   fi
#else
#    echo The installer jar file does not exist.  
#    echo Exiting.
#    echo
#    exit $fail_rc
#fi     

# Make read-only component file writable.
if [ "$fixversionfiles" ]
    then
    NEXT=FALSE
    for VAR in $@
    do
        if [ TRUE = "$NEXT" ]
        then
            TARGETDIR=$VAR
            NEXT=FALSE
            break
        fi

        if [ -INSTALLDIR = "`echo $VAR | tr '[a-z]' '[A-Z]'`" ]
        then
            NEXT=TRUE
        fi
    done

    #echo $TARGETDIR

    if [ "$PLATFORM" != "OS400" ]
    then
        if [ -d "$TARGETDIR"/version ]
        then
            #make certain the version directory and all the files in it are writable
            chmod -fR a+w "$TARGETDIR"/version
            chmod_rc=$?
            if [ 0 -ne "${chmod_rc}" ]
            then
                echo Could not change access mode of $TARGETDIR/version files
            fi 
        else
		     echo
            #echo $TARGETDIR/version directory not found.

        fi
    fi
fi

if [ "$failSetup" ]
   then        
	echo Unable to locate setupCmdLine.sh, this is usually located beneath your
	echo WebSphere AppServer installation, in the 'bin' directory
	echo
	echo Exiting.	
	echo
	exit $fail_rc 
fi

if [ "$callSetup" ]
   then 
                         
        if [ "$PLATFORM" = "OS400" ]
           then
               launchInstaller=1
           else
               CURR_TEMP_DIR=`pwd`
               cd $WAS_HOME/bin
               . $WAS_HOME/bin/setupCmdLine.sh
               cd $CURR_TEMP_DIR
               testJDK=1
			   echo "**** WAS_CELL = $WAS_CELL"
               echo "**** USER_INSTALL_ROOT = $USER_INSTALL_ROOT"
        fi
fi

if [ "$testJDK" ]
   then
        if [ -f "$JAVA_HOME"/bin/java ]
           then
                launchInstaller=1
           else
                echo The JDK was not found within the set JAVA_HOME:
        	echo     JAVA_HOME: [ "$JAVA_HOME"/bin/java ]
        	echo
        	echo Exiting.
        	echo
        	exit $fail_rc
        fi           
fi  
      
#PLATFORM=`uname`
export PLATFORM
                 
	                   
#----------------------------------------------------------------------------------
# Set up library loading for native extfile libs
#----------------------------------------------------------------------------------

# Cases for platform specific goop.
case ${PLATFORM} in
        Linux)
                # Select the right arch library for the utils!
                OS_ARCH=`uname -m`
                case ${OS_ARCH} in
                    i386|i486|i586|i686|i786|i886|athlon)
                        LD_LIBRARY_PATH=${currentDir}/lib/linux/bin/ia32:${LD_LIBRARY_PATH}
                        export LD_LIBRARY_PATH
                        ;;
                    ppc|ppc64)
                        LD_LIBRARY_PATH=${currentDir}/lib/linux/bin/ppc32:${LD_LIBRARY_PATH}
                        export LD_LIBRARY_PATH
                        ;;
                    s390|s390x)
                        LD_LIBRARY_PATH=${currentDir}/lib/linux/bin/s390:${LD_LIBRARY_PATH}
                        export LD_LIBRARY_PATH
                        ;;
                    *)    
                        echo "Unknown Linux Architechture: ${OS_ARCH}. Native file libraries will not be loaded."
                        ;;
                esac
                ;;
        SunOS|Solaris)
                LD_LIBRARY_PATH=${currentDir}/lib/solaris/bin/sparc:${LD_LIBRARY_PATH}
                export LD_LIBRARY_PATH
                ;;
        HP-UX)
                SHLIB_PATH=${currentDir}/lib/hpux/bin/pa-risc:${SHLIB_PATH}
                export SHLIB_PATH
                
                # Make sure this is executable or it won't load!
                if [ ! -x ${currentDir}/lib/hpux/bin/pa-risc/libNativeFile.sl ] ; then
                    chmod 755 ${currentDir}/lib/hpux/bin/pa-risc/libNativeFile.sl
                    chmod_rc=$?
                       if [ 0 -ne ${chmod_rc} ]
                          then
                          echo Could not set execute permissions on ${currentDir}/lib/hpux/bin/pa-risc/libNativeFile.sl
                          echo Make sure that this file has execute permission and run again
                          exit ${chmod_rc}
                       fi
                fi                 
                ;;
        AIX)    
                # Discern between 32 and 64 bit processor
                AIX_PROCESSOR=`bootinfo -K`
                case ${AIX_PROCESSOR} in
                    32)
                        LIBPATH=${currentDir}/lib/aix/bin/ppc32:${LIBPATH}
                        export LIBPATH
                        ;;
                    64)
                        LIBPATH=${currentDir}/lib/aix/bin/ppc64:${LIBPATH}
                        export LIBPATH
                        ;;
                esac
                export LIBPATH
                ;;
        OS400)
        	;;
        *)
                echo "Unknown platform: ${PLATFORM}.  Native file libraries will not be loaded."
                ;;
esac


#----------------------------------------------------------------------------------
# Installer application execution
#----------------------------------------------------------------------------------

DBG_PROP="-Dcom.ibm.websphere.update.ptf.log.level=5 -Dcom.ibm.lconn.ifix.debug=false -Dcom.ibm.lconn.ifix.ziputil.debug=false -Dcom.ibm.lconn.ifix.fileutil.debug=false"
export DBG_PROP

if [ "$PLATFORM" = "OS400" ]
then
  # d170005
  if [ -d "/QIBM/ProdData/PortalServer/V6" ]; then
    chmod 755 "/QIBM/ProdData/PortalServer/V6"
    if [ -d "/QIBM/ProdData/PortalServer/V6/update" ]; then
        chmod -R 755 "/QIBM/ProdData/PortalServer/V6/update"
    fi
  fi
  
  # d177296
  if [ -d "/QIBM/ProdData/PortalExpress/V6" ]; then
    chmod 755 "/QIBM/ProdData/PortalExpress/V6"
    if [ -d "/QIBM/ProdData/PortalExpress/V6/update" ]; then
        chmod -R 755 "/QIBM/ProdData/PortalExpress/V6/update"
    fi
  fi
  
  # get 3rd string in the path to see if it is userdata
  INST_TYPE=`echo $INSTALLDIR | cut -f "3" -d "/" | tr 'a-z' 'A-Z'`
#  echo INST_TYPE is $INST_TYPE
  if [ $INST_TYPE = "USERDATA" ]; then
        # get first 12 chars of last object in pathname
        INST_PROD=`echo $(basename $INSTALLDIR) | tr 'a-z' 'A-Z' | cut -c1-12`
#        echo INST_PROD is $INST_PROD
        if [ $INST_PROD = "PORTALSERVER" ]; then
                setccsid -R -P 819 "$INSTALLDIR/config"
                chown -R QEJBSVR "$INSTALLDIR/config"
                chown -R QEJBSVR "$INSTALLDIR/doc"
                chown -R QEJBSVR "$INSTALLDIR/installableApps"
                chown -R QEJBSVR "$INSTALLDIR/log"
          	if [ -d "$INSTALLDIR"/pzn ]; then
                   	chown -R QEJBSVR "$INSTALLDIR/pzn"
                        chown QEJBSVR "$INSTALLDIR/version/pzn.component"
        	fi
 	fi
  fi
fi   

if [ "$launchInstaller" ] 
then           
	PLATFORM=`uname`
		export PLATFORM

		PLATFORM=`uname`
		LINUX_HARDWARE=`uname -m`
		if [ "${LINUX_HARDWARE}" = "s390" ]
		then
			PLATFORM="s390"
		fi
		if [ "${LINUX_HARDWARE}" = "s390x" ]
		then
			PLATFORM="s390"
		fi
		setLocale=-Duser.language=en
		CLASS_PATH=
		BIN_PATH=
		JAVA_CMD=
		LAUNCH_MODE=$1
		case ${PLATFORM} in
			Linux)
		
				;;
			AIX)
				BIN_PATH=jvm/aix/jre/bin
				;;
			s390)

				;;
		esac

		export PATH=${BIN_PATH}:$PATH
		export CLASSPATH=.:${CLASS_PATH}:lib/lcui.jar:lib/nativefile.jar:lib/icu4j-68.1.jar:lib/commons-configuration-1.5-plus-node-clone.jar:lib/commons-logging-1.0.4.jar:lib/commons-lang-2.4.jar:lib/commons-collections-3.2.1.jar
		

       
    java \
    -Xmx512m \
    -DCURRENT_UPDATE_DIR="${currentDir}" \
    -DWAS_PROD_BASE_PATH="$WAS_PROD_HOME" \
    -Dwas.home="$WAS_HOME" \
	-Dwas.cell="$WAS_CELL" \
    -Dcom.ibm.wp.pui.bld.level=@BLD_NUMBER@ \
    -Duser.install.root="$USER_INSTALL_ROOT" \
    $WP_JAVA_PARAMS \
    ${DBG_PROP} \
    com.ibm.websphere.update.launch.Launcher \
    com.ibm.websphere.update.silent.UpdateInstaller "$@"
    success_rc=$?
    #echo $success_rc
fi

#ensure appropriate file system ownership on iSeries
#if [ "$PLATFORM" = "OS400" ]
#then
#   chown -R QEJBSVR "$INSTALLDIR"
#fi   
if [ "$PLATFORM" = "OS400" ]
then
  # get 3rd string in the path to see if it is proddata
  INST_TYPE=`echo $INSTALLDIR | cut -f "3" -d "/" | tr 'a-z' 'A-Z'`
  if [ $INST_TYPE = "PRODDATA" ]; then
	if [ -d "$INSTALLDIR"/fixes ]; then
        	chown -R QSYS "$INSTALLDIR/fixes"			
	fi
	if [ -d "$INSTALLDIR"/version ]; then
        	chown -R QSYS "$INSTALLDIR/version"
	fi
  fi
fi   

echo End of [ "$LaunchTitle" ]
echo 
exit $success_rc

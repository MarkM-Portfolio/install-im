#!/bin/sh
# ***************************************************************** 
#                                                                   
# IBM Licensed Material                                              
#                                                                   
# Copyright IBM Corp. 2003, 2016                                    
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

# set DEBUG_UPDATE=yes to turn on debugging statements
DEBUG_UPDATE=

MotifLAF=com.sun.java.swing.plaf.motif.MotifLookAndFeel

success_rc=0
fail_rc=$?

LaunchTitle=$0

runInstaller=
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

currentDir=`dirname $0`        

PREREQ_DISABLE_INST=
PREREQ_DISABLE_UNINST=
FOR_EFIX=
RUN_JAVA_HOME=
WP_PUI_CONFIG=


#----------------------------------------------------------------------------------
#  Initialization and setup
#----------------------------------------------------------------------------------
echo Start of [ "$LaunchTitle" ]
echo Build @BLD_NUMBER@
echo
     
   
   
      while [ "$1" ]
      	   do 	   		
      	      if [ "$1" = "-dpInstall" ]
      	 	 then 
      		      PREREQ_DISABLE_INST="-W EfixInstallPrereqErrorAction.prereqOverride=yes"
      	      elif [ "$1" = "-dpUninstall" ] 
      		 then
      		      PREREQ_DISABLE_UNINST="-W EfixUninstallPrereqError.prereqOverride=yes"
      	      elif [ "$1" = "-efixOnly" ] 
      	         then
      	              FOR_EFIX="-W UpdateActionSelect.disablePTFOptions=yes"
      	      elif [ "$1" = "-configProperties" ] 
      	         then
                      WP_PUI_CONFIG=$2
      	              shift 
      	      elif [ "$1" = "-usage" ] 
      	         then
      	              echo
      	              echo Usage: updateWizard [ -efixOnly ] [ -dpInstall ] [ -dpUninstall ] [ -configProperties cfgFile ] [ -usage ]
      		      echo
      		      echo   efixOnly         - Run the installer in EFix Processing mode only. 
      		      echo   dpInstall        - Disable install prerequisite error locking.	
      		      echo   dpUninstall      - Disable uninstall prerequisite error locking.
      		      echo   configProperties - Additional configuration properties to be passed to FixPak/fix processing.
      		      echo   usage            - Display syntax help. 
      		      echo       	
      		      exit $fail_rc            	           	  		  
      		 else 
      		      echo Usage error: [ "$1" is an unrecognized argument ]	          	          	          
      		      exit $fail_rc
      	      fi	     	       	     
      	      
      	      shift	      
      done

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
                echo Attempting to locate setupCmdLine.sh
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
        CURR_TEMP_DIR=`pwd`
		cd $WAS_HOME/bin
        . $WAS_HOME/bin/setupCmdLine.sh
        cd $CURR_TEMP_DIR
        testJDK=1
	    echo "**** WAS_CELL = $WAS_CELL"
        echo "**** USER_INSTALL_ROOT = $USER_INSTALL_ROOT"
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

DBG_PROP="-Dcom.ibm.websphere.update.ptf.log.level=5 -Dcom.ibm.lconn.ifix.debug=false -Dcom.ibm.lconn.ifix.ziputil.debug=false -Dcom.ibm.lconn.ifix.fileutil.debug=false"
export DBG_PROP
if [ "$launchInstaller" ] 
  then 

		CLASS_PATH=
		BIN_PATH=
		JAVA_CMD=
		LAUNCH_MODE=$1
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
				CLASS_PATH=lib/org.eclipse.swt.gtk.linux.x86_64.jar
				;;
			AIX)
				CLASS_PATH=lib/org.eclipse.swt.gtk.aix.ppc64.jar
				BIN_PATH=jvm/aix/jre/bin
				;;
			s390x)
				CLASS_PATH=lib/org.eclipse.swt.gtk.linux.s390x_3.7.0.v3735b.jar
				;;
		esac

		export PATH=${BIN_PATH}:$PATH
		export CLASSPATH=.:${CLASS_PATH}:lib/lcui.jar:lib/org.eclipse.core.commands.jar:lib/org.eclipse.equinox.common.jar:lib/org.eclipse.jface.jar:lib/org.eclipse.ui.forms_3.5.100.v20110425.jar:lib/nativefile.jar:lib/icu4j-68.1.jar:lib/commons-configuration-1.5-plus-node-clone.jar:lib/commons-logging-1.0.4.jar:lib/commons-lang-2.4.jar:lib/commons-collections-3.2.1.jar
		javaw -Dibm.stream.nio=true -Dwas.home="$WAS_HOME" -Duser.install.root="$USER_INSTALL_ROOT" -Dwas.cell="$WAS_CELL" com.ibm.lconn.wizard.launcher.UpdateInstallerLauncher
 
fi  

echo End of [ "$LaunchTitle" ]
echo 
exit $success_rc
      


 

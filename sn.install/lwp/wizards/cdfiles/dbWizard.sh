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

#################LogLevel################
#LogLevel	Value
#SEVERE 	1000
#WARNING 	900
#INFO 		800
#CONFIG 	700
#FINE 		500
#FINER 		400
#FINEST		300
#################LogLevel################
#LOGLEVEL=-Dloglevel=300
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
    	CLASS_PATH=lib/org.eclipse.swt.gtk.linux.x86_64-3.105.3.jar
        BIN_PATH=jvm/linux/jre/bin
        ;;
    AIX)
        CLASS_PATH=lib/org.eclipse.swt.gtk.aix.ppc64_3.103.2.v20150203-1351.jar
        BIN_PATH=jvm/aix/jre/bin
        ;;
    s390)
        CLASS_PATH=lib/org.eclipse.swt.gtk.linux.s390x_3.103.2.v20150203-1351.jar
        BIN_PATH=jvm/s390/jre/bin
        ;;
esac

export PATH=${BIN_PATH}:$PATH
export CLASSPATH=.:lib/Wizards.jar:${CLASS_PATH}:lib/ibmjs.jar:lib/org.eclipse.core.commands_3.6.100.v20140528-1422.jar:lib/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar:lib/org.eclipse.jface_3.10.2.v20141021-1035.jar:lib/itkdepcheck.jar:$CLASSPATH

if [[ "$LAUNCH_MODE" = "-silent" ]]; then
        ${BIN_PATH}/java ${LOGLEVEL} com.ibm.lconn.wizard.launcher.DBSilentLauncher $2
else
        ${BIN_PATH}/javaw ${LOGLEVEL} -Djava.library.path=lib/linkfile com.ibm.lconn.wizard.launcher.DBWizardLauncher $1
fi

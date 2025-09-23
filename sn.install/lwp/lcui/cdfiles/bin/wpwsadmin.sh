#!/bin/sh

# THIS PRODUCT CONTAINS RESTRICTED MATERIALS OF IBM
# 5724-E76 and 5724-E77 (C) COPYRIGHT International Business Machines Corp., 2003
# All Rights Reserved * Licensed Materials - Property of IBM
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.

# wpwsadmin launcher

PLATFORM=`uname`

runDir=`dirname "$0"`

#. "$WAS_HOME/bin/setupCmdLine.sh"

if [ "$PLATFORM" = "OS400" ]
   then
       WP_CP_EXTRA="/QIBM/ProdData/java400/jt400ntv.jar:/qibm/proddata/http/public/jt400/lib/jt400.jar"
       . $WAS_HOME/bin/setupCmdLine -instance $WAS_INSTANCE
       WP_JAVA_PARAMS="$WP_JAVA_PARAMS -Djava.version=1.4 -Djava.ext.dirs=$JAVA_EXT_DIRS"
       WAS_CLASSPATH=`dirname $0`/../bin/jt400Native.jar:${WAS_INSTALL_ROOT}/lib/jython.jar:${WAS_CLASSPATH}
   else
       CURR_TEMP_DIR=`pwd`
       cd $WAS_HOME/bin
       . $WAS_HOME/bin/setupCmdLine.sh
       cd $CURR_TEMP_DIR
fi

CONSOLE_ENCODING=-Dws.output.encoding=console
SHELL=com.ibm.websphere.update.delta.adminconfig.WPWsAdmin

if [ ! "$USER_INSTALL_ROOT" ] ; then
   USER_INSTALL_ROOT=$WAS_HOME
fi

# For debugging the utility itself
# DEBUG="-Djava.compiler=NONE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7777"

"$JAVA_HOME/bin/java" \
  -Xbootclasspath/p:"$WAS_BOOTCLASSPATH" \
  $CONSOLE_ENCODING \
  $DEBUG \
  "$CLIENTSAS" \
  "$CLIENTSOAP" \
  $WP_JAVA_PARAMS \
  -Dcom.ibm.wps.home="$WP_HOME" \
  -Dcom.ibm.wps.pui.configprops="$WP_PUI_CONFIG" \
  -Dcom.ibm.ws.scripting.wsadminprops="$WSADMIN_PROPERTIES" \
  -Dcom.ibm.websphere.management.filetransfer.downloadPathsNotRestricted=true \
  -Dwas.install.root="$WAS_HOME" \
  -Duser.install.root="$USER_INSTALL_ROOT" \
  -Dwas.repository.root="$CONFIG_ROOT" \
  -Dserver.root="$WAS_HOME" \
  -Dlocal.cell="$WAS_CELL" \
  -Dlocal.node="$WAS_NODE" \
  -Dws.ext.dirs="$WAS_EXT_DIRS:$runDir/.." \
  -classpath "$WP_CP_EXTRA":"$WAS_CLASSPATH" com.ibm.ws.bootstrap.WSLauncher \
  $SHELL "$@"

exit $?

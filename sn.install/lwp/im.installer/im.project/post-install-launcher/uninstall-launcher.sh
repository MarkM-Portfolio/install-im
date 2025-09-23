#!/bin/sh
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
# Build: @BUILD_RELEASE@

UNAME_OS=`uname`
if [ "$UNAME_OS" = "OS400" ]; then
    touch -C 819 $2/uninstall-launcher.log
fi

if [ $8 == 'ROLLBACK' ]; then
exit 0
fi

echo "starting here" > $2/uninstall-launcher.log
export WAS_PROFILE_HOME=$6
if [ $7 = 1 ]; then
echo 'skip jython installer' >> $2/uninstall-launcher.log
exit 0
fi

echo "$2/bin/lc-install.sh -uninstall -host localhost -port $5 -user $3 -password PASSWORD_REMOVED" >> $2/uninstall-launcher.log
$2/bin/lc-install.sh -uninstall -host localhost -port $5 -user $3 -password $4

EXIT_CODE=$?
echo "Exit Code is ${EXIT_CODE}" >> $2/uninstall-launcher.log
if [ "${EXIT_CODE}" != 0 ]; then
echo "Connections Uninstall FAILED! Please check log for details." >> $2/uninstall-launcher.log
exit ${EXIT_CODE}
fi

echo "Connections Uninstall finished SUCCESSFULLY!" >> $2/uninstall-launcher.log
exit 0

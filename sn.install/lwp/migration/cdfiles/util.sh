# *****************************************************************
# HCL Confidential
# OCO Source Materials
#
# Copyright HCL Technologies Limited 2010, 2020
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

lchome=$2
resource_file=$3
status=
CLASS_NAME=$1
PRODUCT="HCL Connections 6.5.0.0_CR1 Toolkit"
JAVA_HOME_TESTED=""
WAS_HOME_TESTED=""
JAVA_CMD=""
FIXED_CLASS_PATH=".:./lib/lc_migration.jar"


exit=999
status=checkLib

cd `dirname $0`

while [ "$exit" = "999" ]
do
	case $status in
		checkLib )
			if [ ! -f "./lib/lc_migration.jar" ]
			then
				echo lib/lc_migration.jar does not exist
				rc=3
				exit=3
			else
				status=checkLCHome
			fi
			;;
    		checkLCHome )
			if [ ! -d "$lchome" ]
			then
				echo $lchome is not a valid directory, specify a location that has IBM Connections installed.
				exit=1
			else
				status=testwashome
				#echo $status
			fi
			;;
		help )
			echo "Usage: util <class_name> <lchome> <resource_path>"
			rc=2
			exit=2
			;;
		testwashome )
			WAS_HOME_TESTED=1
			if [ -n "$WAS_HOME" ]
			then
				echo "Find WAS_HOME $WAS_HOME"
				JAVA_CMD="$WAS_HOME/java/bin/java"
				status=testjavacmd
				#echo $status
			else
				echo "WAS_HOME not set. "
				JAVA_CMD=""
				status=testjavahome
				#echo $status
			fi
			;;
		testjavacmd )
			if [ -f "$JAVA_CMD" ]
			then
				echo "Using java: $JAVA_CMD"
				status=launch
				#echo $status
			else
				if [ ! "$JAVA_CMD"="" ]
				then
					echo $JAVA_CMD does not exist
				fi
				if [ -z "$WAS_HOME_TESTED" ]
				then
					status=testwashome
				#echo $status
				else
					if [ -z "$JAVA_HOME_TESTED" ]
					then
						status=testjavahome
				#echo $status
					else
						status=javafail
				#echo $status
					fi
				fi
			fi
			;;
		testjavahome )
			JAVA_HOME_TESTED=1
			if [ -n "$JAVA_HOME" ]
			then
				echo Find JAVA_HOME $JAVA_HOME
				JAVA_CMD=$JAVA_HOME/bin/java
				status=testjavacmd
				#echo $status
			else
				echo JAVA_HOME not set
				JAVA_CMD=""
				status=testjavacmd
				#echo $status
			fi
			;;
		javafail )
			echo WAS_HOME/java/bin/java and JAVA_HOME/bin/java are not found.
			status=fail
				#echo $status
			;;
		fail )
			echo Failed to launch $PRODUCT
			exit=2
			rc=1
			;;
		launch )
			export CLASSPATH=$FIXED_CLASS_PATH
			exit=0
			${JAVA_CMD} ${CLASS_NAME} ${lchome} ${resource_file}
			;;
		* )
			#echo $status
			;;
	esac
done
exit $exit

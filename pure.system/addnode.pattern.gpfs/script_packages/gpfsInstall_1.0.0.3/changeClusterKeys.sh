#
#*===================================================================
#*
# Licensed Materials - Property of IBM  
# "Restricted Materials of IBM"
# 5725-G32, 5725-F46  Copyright IBM Corp., 2013, 2013
# All Rights Reserved * Licensed Materials - Property of IBM
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
#*
#*===================================================================
#

HOST=""
OSType=$(uname)
#echo $OSType
existKeyDir=""
if [ "$OSType" == "AIX" ]
then
#echo "Its AIX"
STR_HOST=$(hostname)
#echo $STR_HOST
HOST=$(host $STR_HOST | cut -d" " -f3 | cut -d"," -f1)
else
HOST=$(hostname -i)
fi
echo $HOST

if [ "$1" != "-keySubDir" ]
then
echo "The keySubDir parameter is missing"
echo "Script Failed"
echo "The command is changeClusterKeys.sh -keySubDir subdir [-existingKeyDir rsakeydir]"
echo "Parameters must be entered in the order above"
exit 1

else
if [ "$2" != "" ]
then
echo "Value for keySubDir is: "$2
else
echo "No value for keySubdir"
echo "The command is changeClusterKeys.sh -keySubDir subdir [-existingKeyDir rsa
key dir"
echo "Parameters must be entered in the order above"
exit 1
fi

fi

if [ "$3" == "" ]
then
echo "No -existingKeyDir given"
else

if [ "$3" != "-existingKeyDir" ]

then
echo "The existingKeyDir option is misspelled"
echo "Script Failed"
echo "The command is changeClusterKeys.sh -keySubDir subdir [-existingKeyDir rsakeydir]"
echo "Parameters must be entered in the order above"
exit 1
else

if [ "$4" != "" ]
then
echo "Value for -existinKeyDir is: "$4
existKeyDir=" -existingKeyDir "$4
else
echo "No value for existingKeyDir"
echo "The command is changeClusterKeys.sh -keySubDir subdir [-existingKeyDir rsa
key dir"
echo "Parameters must be entered in the order above"
exit 1
fi
fi
fi

echo "keySubDir:  "$2
echo "existingKeyDir:  "$existKeyDir
python /tmp/gpfs/gpfsInstall/gpfs.py -operationName changeClusterKeys -keySubDir $2 $existKeyDir -managerHost $HOST
echo "Success"
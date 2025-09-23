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

if [ "$OSType" == "AIX" ]
then
#echo "its AIX"
STR_HOST=$(hostname)
#echo $STR_HOST
HOST=$(host $STR_HOST | cut -d" " -f3 | cut -d"," -f1)
else
HOST=$(hostname -i)
fi
echo $HOST
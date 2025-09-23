#!/bin/sh

# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

# first parameter is real architecture, like: "i386, i686"
# second parameter is expect one, like: "32 bit, 64 bit"

# filter the type

type=`uname`


if [ "$type" = "AIX" ]; then
    arch = `bootinfo -y`
fi

if [ "$type" = "Linux" ]; then
       arch=`getconf LONG_BIT`
fi


if [ "$type" = "SunOS" ]; then
       arch=`isainfo -kv | cut -f1 -d -`
fi


# get the architecture, such as "32 bit" or "64 bit"
Earch=`echo $1 | sed 's/\([s/a-zA-Z0-9.]\{1,\}\).*/\1/g'`
# get the options, such as "+", "-"
option=`echo $1 | sed 's/.* [a-zA-Z0-9.]\{1,\}\(.\).*/\1/g'`

if [ "$option" = "+" ]; then

	if [ $arch -ge $Earch ]; then
		echo "$Msg_TRUE"
	else
		echo "$Msg_FALSE"
	fi
else
       if [ "$option" = "-" ]; then
	
		if [ $arch -lt $Earch ]; then
			echo "$Msg_TRUE"
		else
			echo "$Msg_FALSE"
		fi
	else
		if [ "$arch" = "$Earch" ]; then
			echo "$Msg_TRUE"
		else
			echo "$Msg_FALSE"
		fi
	fi
fi



 

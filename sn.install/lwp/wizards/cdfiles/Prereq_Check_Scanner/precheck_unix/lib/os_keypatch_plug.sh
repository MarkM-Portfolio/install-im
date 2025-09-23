#!/bin/sh

# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


# this script is for checking key patch
# for solaris V8, V9, V10
# for HP-UX 11i
# for AIX V5.x
# for Red Hat 2.1
# for Red Hat 4 Intel

# now just do Red Hat 4 Intel

# get OS Version
case `uname` in 
	Linux)
		# to see if Red Hat 4
		isred=`cat /etc/*release 2>/dev/null | grep "Linux" | awk '{print $1}' | sed -n '/[Rr]ed/p'`
		if [ -z "$isred" ]; then
			exit
		fi

		# get the version
		ver4=`cat /etc/*release 2>/dev/null | grep "Linux" | grep "Red" | sed 's/.* release \([0-9]\{1,\}\).*/\1/g'`
		if [ "$ver4" != "4" ]; then
			exit		
		fi

		# check if the Intel architecture
		isIntel=`uname -a | grep "i386"`
		if [ -z "$isIntel" ]; then
			exit
		fi

		# check if the three patches is there, we just check 3.2.3
		# "compat-gcc-32-c++-3.2.3-46.1.i386.rpm, compat-gcc-32-3.2.3-46.1.i386.rpm, compat-libstdc++-33-3.2.3-46.1.i386.rpm"
		ngcc=`rpm -aq | grep "compat-gcc" | wc -l | awk '{print $1}'`
		nlibc=`rpm -aq | grep "compat-libstdc++" | wc -l | awk '{print $1}'`

		if [ "$ngcc" -ge "2" -a "$nlibc" -ge "1" ]; then
			echo "OS Patch: yes"
		else
			echo "OS Patch: no, require compat-gcc-32-c++-3.2.3-46.1.i386.rpm, compat-gcc-32-3.2.3-46.1.i386.rpm, compat-libstdc++-33-3.2.3-46.1.i386.rpm"
		fi


		
	;;
	AIX)
	;;
	HP-UX)
	;;
	Sun-OS)
	;;
esac

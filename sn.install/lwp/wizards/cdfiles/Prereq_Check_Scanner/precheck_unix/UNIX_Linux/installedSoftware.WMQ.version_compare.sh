#!/bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
# first parameter like: v9.1.0.0FP0
# second like: v8.1 FP9+, v8.2 FP2+, v9.1FP1+, V9.5+
# filter the type
type=`uname`
# parse the version
version=`echo "$1" | sed 's/v\([0-9.]\{1,\}\).*/\1/g'`
option=`echo "$1" | sed 's/v[0-9.]\{1,\}\(.\).*/\1/g'`
echo $version
echo $option
# parse the fix pack
fp=`echo "$1" | sed 's/.*FP\([0-9.]\{1,\}\).*/\1/g'`
fpopt=`echo "$1" | sed 's/.*FP[0-9.]\{1,\}\(.\).*/\1/g'`
if [ "$fp" = "" ]; then
	fp="-1"
fi
#echo "Logi"
echo $option"+"$fpopt
for tt in `echo "$2" | sed 's/[ ]*//g' | tr "," "\n"`
do 
	eversion=`echo "$tt" | sed 's/[vV]\([0-9.]\{1,\}\).*/\1/g'` 
	matchver=`echo "$version" | grep "$eversion"`
	if [ "$matchver" = "" ]; then
		continue
	else
		# match the version, then compare the fix pack
		efp=`echo "$tt" | sed 's/.*FP\([0-9.]\{1,\}\).*/\1/g'`
		if [ "$efp" = "$tt" ]; then
			efp="-1"
		fi
		if [ "$efp" = "" ]; then
			efp="-1"
		fi
		if [[ "$fp" < "$efp" ]]; then
			echo "$Msg_FAIL"
		else
			echo "$Msg_PASS"
		fi
		exit
	fi
done


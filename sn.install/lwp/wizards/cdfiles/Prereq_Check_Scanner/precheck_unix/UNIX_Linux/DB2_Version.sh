#./bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

verdb=`db2level 2>/dev/null`
if [ "$verdb" = "" ]; then
	#for toe in `find /opt -name db2level 2>/dev/null`
	#do
	#	verdb=`$toe`	
	#	break
	#done 

	# find the db2profile from /home
	prof=`find /home -name db2profile | sed -n '1p'`	
	if [ -z "$prof" ]; then
		echo "$Msg_UNAVAILABLE"
		exit
	fi 

	. $prof
	verdb=`db2level 2>/dev/null`
fi

if [ "$verdb" = "" ]; then
	echo "$Msg_UNAVAILABLE"
	exit
fi

# some shell not recognize awk -F"xxxxx", so use sed instead
#vv=`echo $verdb | awk -F"\"DB2 " '{print $2;}' | awk -F"\"" '{print $1;}'` 
vv=`echo "$verdb" | sed 's/.*\"DB2 \([v|V][0-9\.]\{1,\}\)\".*/\1/' | sed -n '/^[v|V][0-9\.]\{1,\}/p'`

#fp=`echo $verdb | awk -F"Pack \"" '{print $2;}' | awk -F"\"" '{print $1;}'`
fp=`echo "$verdb" | sed 's/^\"\([0-9\.]\{1\}\)\".*/\1/g' | sed -n '/^[0-9\.]\{1,\}/p'`
if [ -z "$fp" ]; then
	fp=`echo $verdb | awk -F"FixPak \"" '{print $2;}' | awk -F"\"" '{print $1;}'`
fi

if [ -z "$fp" ]; then
	fp="0"
fi
echo $vv"FP"$fp

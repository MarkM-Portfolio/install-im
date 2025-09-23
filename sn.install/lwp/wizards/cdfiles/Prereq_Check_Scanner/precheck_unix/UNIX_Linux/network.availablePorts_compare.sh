#!/bin/bash
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

range=$2
check_range=`echo $range | grep -`
if [ $check_range ]; then 
range_start=`echo $2 | cut -d '-' -f1`
range_end=`echo $2 | cut -d '-' -f2`
#echo $range_start
#echo $range_end

rm -f /$TMP_DIR/prs.res
type=`uname`
if [ "$type" = "SunOS" ]; then
 netstat | grep LISTEN > /$TMP_DIR/prs.net
else

netstat -ant | grep LISTEN > /$TMP_DIR/prs.net
fi
while [ $range_start -le $range_end ]
do
#echo $range_start
res=`grep -w $range_start /$TMP_DIR/prs.net`
res=`echo $res | cut -d' ' -f1`

if [ $res ]; then
 echo  "False" >> /$TMP_DIR/prs.res
else
 echo "True" >> /$TMP_DIR/prs.res
fi


((range_start++))
done
check=`grep -w False /$TMP_DIR/prs.res`
#echo $check
check1=`echo $check | cut -d ' ' -f1`

if [ $check1 ]; then
 echo "$Msg_FAIL"
else
 echo "$Msg_PASS"
fi

else
type=`uname`
if [ "$type" = "SunOS" ]; then
 netstat | grep LISTEN > /$TMP_DIR/prs.net
else
 netstat -ant | grep LISTEN > /$TMP_DIR/prs.net
fi
res=`grep -w $2 /$TMP_DIR/prs.net`
res=`echo $res | cut -d' ' -f1`
if [ $res ]; then
 echo  "$Msg_FAIL"
else
 echo "$Msg_PASS"
fi
fi

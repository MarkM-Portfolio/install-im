# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

. ../lib/common_function.sh
res=0

if [ "$1" = "Unavailable" ]; then
  echo "$Msg_FAIL"
  exit
fi


len=`echo $2 | wc -c`
len1=`expr $len - 1`
option=`echo $2 | cut -c $len1`
version=$1
len2=`expr $len - 2`
eversion=`echo $2 | cut -c 1-$len2`
#echo $eversion
#echo $option
if [ "$option" = "+" ]; then
   tmpArg=`versionCompare $eversion $version`
                                  if [ $tmpArg -eq 0 -o $tmpArg -eq -1 ]; then
                                                res=1
                                        fi
fi

if [ $res -eq 1 ]; then
  echo "$Msg_PASS"
else
  echo "$Msg_FAIL"
fi

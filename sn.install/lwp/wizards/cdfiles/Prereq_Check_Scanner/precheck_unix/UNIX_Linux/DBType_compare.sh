# Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

if [ "$1" = "Unknown" ]; then
  echo "$Msg_FAIL"
  exit
else
   if [ "$2" = "Any"  -o "$2" = "any" ]; then
   echo "$Msg_PASS"
   exit
   fi
fi

regexFound=`echo $2 | grep "regex{"`
if [ "$regexFound" != "" ]; then
  toSearch=`echo $2 | sed 's/regex{//g' | sed 's/}//g' | sed 's/|/\\\|/g'`
  isFound=`echo $1 | grep  $toSearch`
  if [ "$isFound" != "" ]; then
    echo "$Msg_PASS"
    exit
  fi
else
   toSearch=$2
   isFound=`echo $1 | grep  $toSearch`
  if [ "$isFound" != "" ]; then
    echo "$Msg_PASS"
    exit
  fi
fi  
echo "$Msg_FAIL"

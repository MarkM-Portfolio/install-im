#!/bin/bash

# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

#find / -name tar > /$TMP_DIR/ostar
type=`uname`

#res=`cat which tar > /$TMP_DIR/ostar --usage`
if [ "$type" = "SunOS" ]; then
res=`which $1 | cut -c 1-2`

if [ "$res" = "no" ]; then
	echo "$Msg_UNAVAILABLE"
else
	echo "$Msg_AVAILABLE"
fi

else
 res=`which $1 2>/dev/null`
if [ $res ]; then
  echo "$Msg_AVAILABLE" 
else
  echo "$Msg_UNAVAILABLE"
fi
fi

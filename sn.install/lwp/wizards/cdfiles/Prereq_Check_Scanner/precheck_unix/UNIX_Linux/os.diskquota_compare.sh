#!/bin/bash
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

if [ $1="Unlimited" ]; then
echo "$Msg_PASS"
else if [ $1 -gt $2 ]; then
echo "$Msg_PASS"
else 
 echo "$Msg_FAIL"
fi
fi
     

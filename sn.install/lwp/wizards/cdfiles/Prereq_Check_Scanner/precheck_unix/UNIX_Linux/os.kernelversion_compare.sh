# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


check=`echo $1 | cut -c 1-3`
if [ $check = "2.6" ]; then
   echo "$Msg_PASS"
else
   echo "$Msg_FAIL"
fi

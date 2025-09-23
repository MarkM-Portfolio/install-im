# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

if [ "$1" = "Not Required" ]; then
  echo "$Msg_PASS"
  exit
fi

if [ "$1" = "Unavailable" ]; then
  echo "$Msg_FAIL"
else
  echo "$Msg_PASS"
fi

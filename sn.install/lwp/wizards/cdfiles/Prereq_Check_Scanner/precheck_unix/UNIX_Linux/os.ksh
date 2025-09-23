#!/bin/bash

# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

IsKshFound=`which ksh 2>/dev/null` 
if [ $IsKshFound ]; then
	echo "$Msg_AVAILABLE"
else
	echo "$Msg_UNAVAILABLE"
fi


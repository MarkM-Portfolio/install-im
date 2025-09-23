#!/bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2010
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

chmod +x TAD722_impl.sh
./TAD722_impl.sh "TAD_07220000.cfg" ${1+"$@"}

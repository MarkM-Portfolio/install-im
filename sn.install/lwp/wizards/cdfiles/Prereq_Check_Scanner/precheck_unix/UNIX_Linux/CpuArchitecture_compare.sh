# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

ExpectedArch=$2
ActualArch=$1

for EachArch in `echo $ExpectedArch | sed 's/,/ /g'`
do
        if [ "$ActualArch" = "$EachArch" ]; then
                MatchFound=True
                break
        fi
done
if [ "$MatchFound" = "True" ]; then
        echo "Msg_PASS"
else
        echo "Msg_FAIL"
fi


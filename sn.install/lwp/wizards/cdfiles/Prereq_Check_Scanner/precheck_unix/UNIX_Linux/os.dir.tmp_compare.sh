# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

#ExpValue=`echo $2 | cut -d "]" -f2 | sed 's/.$//g'`
###Refining for + #####
Plusfound=`echo $2 | grep "+"`
if [ $Plusfound ]; then
        ExpValue=`echo $2 | cut -d "]" -f2 | sed 's/.$//g'`
        operator=ge
else
        ExpValue=`echo $2 | cut -d "]" -f2`
        operator=eq
fi
###Counting and making the Value readable for Comparison####
ValCount=`echo $1 | wc -c`
if [ $ValCount -eq 5 ]; then
        ActualVal=`echo $1 | cut -c 2-`
else
        ActualVal=$1
fi

FirstActnum=`echo $ActualVal | cut -c 1`
FirstExpnum=`echo $ExpValue | cut -c 1`
if [ $FirstActnum -$operator $FirstExpnum ]; then
        SecondActnum=`echo $ActualVal | cut -c 2`
        SecondExpnum=`echo $ExpValue | cut -c 2`
        if [ $SecondActnum -$operator $SecondExpnum ]; then
                ThirdActnum=`echo $ActualVal | cut -c 3`
                ThirdExpnum=`echo $ExpValue | cut -c 3`
                if [ $ThirdActnum -$operator $ThirdExpnum ]; then
                        echo "$Msg_PASS"
                else
                        echo "$Msg_FAIL"
                fi
        else
                echo "$Msg_FAIL"
        fi
else
        echo "$Msg_FAIL"
fi


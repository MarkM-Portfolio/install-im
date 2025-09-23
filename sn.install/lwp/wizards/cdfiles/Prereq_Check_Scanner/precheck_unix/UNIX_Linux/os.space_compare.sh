#!/bin/bash
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
compare(){
        if [ $# -lt 2 ]; then
                echo "it should contains two parameters"
                return 1;
        fi

		if [ "$1" = "NOT_REQ_CHECK_ID" ]; then
            echo "$Msg_PASS"
            return 0;
        fi
		
        if [ "$1" = "True" -a "$2" = "False" ]; then
            echo "$Msg_Fail"
            exit
        fi


        aa=`echo $1 | sed 's/,//g' | tr "g" "G" | tr "m" "M"`
        bb=`echo $2 | sed 's/,//g' | tr "g" "G" | tr "m" "M"`

        echo $aa $bb | awk '{

                if($1 !~ /G|g|M|m/ && $2 !~ /G|g|M|m/){
                        if($1 < $2){
                                print "$Msg_FAIL";
                        }else{
                                print "$Msg_PASS";
                        }
                }else{
                        if($1 ~ /G|g/){
                                split($1,mx,"G");
                                #gsub(",","",mx[1]);
                                mx[1]=mx[1]*1024;
                        }

                        if($1 ~ /M|m/){
                                split($1,mx,"M");
                                #gsub(",","",mx[1]);
                                mx[1]=mx[1]*1;
                        }

                        if($2 ~ /G|g/){
                                split($2,my,"G");
                                #gsub(",","",my[1]);
                                my[1]=my[1]*1024;
                        }
                        if($2 ~ /M|m/){
                                split($2,my,"M");
                                #gsub(",","",my[1]);
                                my[1]=my[1]*1;
                        }

                        if(mx[1]<my[1]){
                                print "$Msg_FAIL";
                        }else{
                                print "$Msg_PASS";
                        }
                }
        }'
}

res=-1
if [ "$1" = "Unavailable" ]; then
	echo "$Msg_FAIL"
	exit
fi
item=$2
value=""
                #For Quality Specifier
                Val=`echo $item | grep "]"`
                if [ $Val ]; then
                        UnitCheck=`echo $item | grep -i unit`
                        if [ $UnitCheck ]; then
                                Size=`echo $item | cut -d "]" -f2`
                                Unit=`echo $item | cut -d "]" -f1 | cut -d "," -f2 | cut -d ":" -f2`
                                value=$Size$Unit
                        else
                                value=`echo $item | cut -d "]" -f2`
                        fi
                else
                        value=$item
                fi

OutPutValue=`compare $1 $value`


if [ "$OutPutValue" = "$Msg_FAIL" ]; then
	res=-1
else
	res=0
fi
if [ $res -eq -1 ]; then
	echo "$Msg_FAIL"
else
  	echo "$Msg_PASS"
fi


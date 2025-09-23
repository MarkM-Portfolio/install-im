#!/bin/sh
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
                echo "$Msg_2PARAMETER"
                return 1;
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
                                print "FAIL";
                        }else{
                                print "PASS";
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
                                print "FAIL";
                        }else{
                                print "PASS";
                        }
                }
        }'
}
   
type=`uname`
var1=`echo $1 | sed 's/G/ G/g' | cut -d " " -f1`
###Checking for Qualifier###
if [ "$2" = "RAMSize+" ]; then

IsQualFound=`echo "$2" | grep "]" | cut -d "=" -f1`
if [ $IsQualFound ]; then

	ActualValue=$1
	ExpectedValue=`echo "$2" | cut -d "]" -f2`
	OutPutValue=`compare $ActualValue $ExpectedValue`
	echo $OutPutValue
	exit
fi	
if [ "$type" = "AIX" ]; then
 
	cal=`vmstat | grep mem= | awk '{print $4}' | cut -d '=' -f2 | sed 's/MB/ MB/g' | awk '{print $1}'`
	var=`echo $cal/1024 | bc -l`

elif [ "$type" = "SunOS" ]; then

	cal=`prtconf | grep "Memory" | awk '{print $3}'`
	var=`echo $cal/1024 | bc -l`

else 

	var=`cat /proc/meminfo | grep MemTotal | awk {'print $2'}`
	var1=`echo $var1*1024*1024 | bc -l | awk -F "." '{print $1}'`
	#var=`echo $cal/1048576  | bc -l | cut -c 1-5`
fi
#tmp=`compare $var1 $var`
if [ $var1 -ge $var ]; then
	echo "$Msg_PASS"
else
	echo "$Msg_FAIL"
fi

else
        ActualValue=$1
        ExpectedValue=$2
        OutPutValue=`compare $ActualValue $ExpectedValue`
        echo $OutPutValue
        exit


fi

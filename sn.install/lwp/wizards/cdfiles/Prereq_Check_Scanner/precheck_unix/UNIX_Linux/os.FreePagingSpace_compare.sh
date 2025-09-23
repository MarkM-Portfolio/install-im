# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
#set -n
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

ActualPS=`echo $1 | sed 's/GB/ GB/g' | cut -d " " -f1`
####Refining for Plus####
IsPlusFound=`echo $2 | grep "+"`
if [ $IsPlusFound ]; then
        ExpectedPS=`echo $2 | sed 's/.$//g' | sed 's/GB/ GB/g' | cut -d " " -f1`
	Result=`compare $ActualPS $ExpectedPS`
else
        ExpectedPS=`echo $2 | sed 's/GB/ GB/g' | cut -d " " -f1`
        #####Divide Mantisa and Decimal part####
        ActVal1=`echo $ActualPS | cut -d "." -f1`
        ####Checking the Expected value for decimal part####
        IsDecimal=`echo $ExpectedPS | grep "."`
        if [ $IsDecimal ]; then
                DecimalExpPart1=`echo $ExpectedPS | cut -d "." -f1`
                DecimalExpPart2=`echo $ExpectedPS | cut -d "." -f2`

                if [ $ActVal1 -eq $DecimalExpPart1 ]; then

                        ActVal2=`echo $ActualPS | cut -d "." -f2 | cut -c 1`
                        if [ $ActVal2 -eq $DecimalExpPart2 ]; then
                                Result=$Msg_PASS
                        else
                                Result=$Msg_FAIL
                        fi
                else
                        Result=$Msg_FAIL
                fi
        else
                if [ $ActVal1 -eq $ExpectedPS ]; then
                        Result=$Msg_PASS
                else
                        Result=$Msg_FAIL
                fi
        fi

fi


echo $Result



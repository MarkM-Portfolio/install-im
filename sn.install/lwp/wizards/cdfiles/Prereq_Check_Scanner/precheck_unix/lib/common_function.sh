# ************** Begin Copyright - Do not add comments here ****************
#
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

ptrace="$TMP_DIR/prs.trc"
pdbg="$TMP_DIR/prs.debug"
wrlTrace(){
     if [ "$PREREQ_TRACE" = "True" ]; then   
     printf "[%-20s] %-8s: %-s\n" "`date '+%Y.%m.%d %H.%M.%S'`" "$1" "$2" >> $ptrace
     fi
}
wrlDebug(){
       if [ "$PREREQ_DEBUG" = "True" ]; then
#	printf "[%-20s] %-8s: %-s\n" "`date '+%Y.%m.%d %H.%M.%S'`" "$1" $2 >> $pdbg
        `wrlDebugGeneric "" "$1" "$2"` 
      fi
}

wrlDebugGeneric(){
         if [ "$PREREQ_DEBUG" = "True" ]; then
        #$1 - formatSpec
        #$2,$3 - logString
        printf "[%-20s] $1%-s\n" "`date '+%Y.%m.%d %H.%M.%S'`" "$2 $3" >> $pdbg
       fi
}

wrlTraceFuncStart(){
        # $1 = Name of function
        `wrlTrace "[$1] - Entered"`
}

wrlTraceFuncExit(){
        # $1 = Name of function
        `wrlTrace "[$1] - Exit"`
}

wrlDebugFuncStart(){
        # $1 = Name of function
        `wrlDebug "[$1] - Entered"`
}

wrlDebugFuncExit(){
        # $1 = Name of function
        `wrlDebug "[$1] - Exit"`
}

wrlDebugFunc(){
        `wrlDebugGeneric "\t" "$1"`
}

wrlDebugFuncParam(){
        #$1- Param Name
        #$2 - Param Value
        `wrlDebugFunc "[Param] $1:$2"`
}

wrlLogFuncStart(){
        `wrlDebugFuncStart "$1"`
        `wrlTraceFuncStart "$1"`
}

wrlLogFuncExit(){
        `wrlDebugFuncExit "$1"`
        `wrlTraceFuncExit "$1"`
}

wrlDebugFuncReturn(){
        `wrlDebugFunc "Returning - $1"`
}


changeMG(){
	`wrlLogFuncStart "Entering" "changeMG()"`
	`wrlDebugFuncParam "InputValue" "$1"`
	if [ $# -lt 1 ]; then
		echo "it should contains parameter"
		return 1;
	fi

	aa=`echo $1 | sed 's/,//g' | tr "g" "G" | tr "m" "M"`
	echo $aa | awk '{
		if($1 !~ /G|g|M|m/){
			print $1;
		}else{
		if($1 ~ /G|g/){
			split($1,mm,"G");
			#gsub(",","",mm[1]);
			if(mm[1]*1 < 1){
				printf "%.0fM%s",mm[1]*1024,mm[2];
			}else{
				printf "%.2fG%s",mm[1],mm[2];
			}

		}
		if($1 ~ /M|m/){
			split($1,mm,"M");
			#gsub(",","",mm[1]);
			if(mm[1]*1 > 1023){
				printf "%.2fG%s",mm[1]/1024,mm[2];
			}else{
				printf "%.0fM%s",mm[1],mm[2];
			}
		}
		}
	}'
	`wrlDebugFuncReturn "$mm[2]"`
 	`wrlLogFuncExit "ChangeMG()"`
}

AddMG(){
	
        `wrlLogFuncStart "AddMG()"`
        `wrlDebugFuncParam "Inputvalue1" "$1"`
        `wrlDebugFuncParam "Inputvalue2" "$2"`

        if [ $# -lt 2 ]; then
		echo "it should contains two parameters"
		return 1
	fi

	aa=`echo $1 | sed 's/,//g' | tr "g" "G" | tr "m" "M"`
        bb=`echo $2 | sed 's/,//g' | tr "g" "G" | tr "m" "M"`

        echo $aa $bb | awk '{
		
		if($1 !~ /G|g|M|m/){
			print $1;
		}else{
		if($1 ~ /G|g/){
			split($1,mm,"G");
			#gsub(",","",mm[1]);
			printf "%f",mm[1]+$2;

		}
		if($1 ~ /M|m/){
			split($1,mm,"M");
			#gsub(",","",mm[1]);
			printf "%f",mm[1]/1024+$2;
		}
		}
	}'
       `wrlLogFuncExit "AddMG()"`
}

compare(){
        `wrlLogFuncStart "compare()"`

        `wrlDebugFuncParam "compareThis" "$1"`
        `wrlDebugFuncParam "compareWith" "$2"`

	if [ $# -lt 2 ]; then
		echo "it should contains two parameters"
		return 1;
	fi

         if [ "$1" = "True" -a "$2" = "False" ]; then
            echo "Fail"
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
`wrlLogFuncExit "compare()"`
   
}



##141808

cutdown(){
         `wrlLogFuncStart  "cutdown()"`

        `wrlDebugFuncParam "InputValue1" "$1"`
        `wrlDebugFuncParam "InputValue2" "$2"`


         if [ $# -lt 2 ]; then
                echo "it should contains two parameters"
                return 1;
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
                                print "0MB";
                        }else{
                                print (mx[1]-my[1])"MB";
                        }
                }
        }'
     `wrlLogFuncExit "cutdown()"`
}

# find the message for one path
mes4Path(){
	 `wrlLogFuncStart "mes4Path()"`

        `wrlDebugFuncStart "Path" "$1"`


        if [ $# -lt 1 ]; then
		echo "it should have one parameter contains path"
		return 1;
	fi
	path=$1
	# check if it's a path
	#path=`echo "$1" | sed -n '/^\//p'`
	#if [ -z "$path" ];then
		#return 2;
	#else 
          nfs_check_status=`NFScheck $path`
          if [ "$nfs_check_status" = "TRUE" ]; then 	
        	case `uname` in
		        SunOS)
		                # disk
		        	df -k "$path" 1>/dev/null 2>/dev/null
		        	while [ "$?" != "0" ]
		        	do
		                	path=`echo "$path" | sed 's/^\(.*\)\/[^ ]\{1,\}/\1/g'`
		                	if [ -z "$path" ]; then
		                        	path="/"
		                        	break
		                	fi
		                	df -k "$path" 1>/dev/null 2>/dev/null
		        	done
		
		                df -k "$path" | sed -n '$p' | awk '{print $NF}'
		        ;;
			AIX)
		                #disk
				df -m "$path" 1>/dev/null 2>/dev/null
		                while [ "$?" != "0" ]
		                do
		                        path=`echo "$path" | sed 's/^\(.*\)\/[^ ]\{1,\}/\1/g'`
		                        if [ -z "$path" ]; then
		                                path="/"
		                                break
		                        fi
					df -m "$path" 1>/dev/null 2>/dev/null
		                done
				
				df -m "$path" | sed -n '$p' | awk '{print $NF}'
		        ;;
			HP-UX)                                                                                                                                                                   
		                # disk
				df -k "$path" 1>/dev/null 2>/dev/null
		                while [ "$?" != "0" ]
		                do
		                        path=`echo "$path" | sed 's/^\(.*\)\/[^ ]\{1,\}/\1/g'`
		                        if [ -z "$path" ]; then
		                                path="/"
		                                break
		                        fi
					df -k "$path" 1>/dev/null 2>/dev/null
		                done
		
				df -k "$path" | sed -n '1p' | awk '{print $1}'
		        ;;
		
		        Linux)
		                # get disk info
		                df -h "$path" 1>/dev/null 2>/dev/null
		                while [ "$?" != "0" ]
		                do
		                        path=`echo "$path" | sed 's/^\(.*\)\/[^ ]\{1,\}/\1/g'`
		                        if [ -z "$path" ]; then
		                                path="/"
		                                break
		                        fi
		                        df -h "$path" 1>/dev/null 2>/dev/null
		                done
		
		                df -h "$path" | sed -n '$p' | awk '{print $NF}' 
		esac
              else
              echo "$path Server NotAvailable Responding for $path"
               exit
            fi
	#fi
     `wrlLogFuncExit  "mes4Path()"`
}
#This is For Zfs Fix In Solaris System
mes4Path1(){
        
        `wrlLogFuncStart "mes4Path1()"`
        `wrlDebugFuncParam "Path" "$1"`
 
        if [ $# -lt 1 ]; then
                echo "it should have one parameter contains path"
                return 1;
        fi

        # check if it's a path
        path=`echo "$1" | sed -n '/^\//p'`
        if [ -z "$path" ];then
                return 2;
        else
                case `uname` in
                        SunOS)
                                # disk
                                df -k "$path" 1>/dev/null 2>/dev/null
                                while [ "$?" != "0" ]
                                do
                                        path=`echo "$path" | sed 's/^\(.*\)\/[^ ]\{1,\}/\1/g'`
                                        if [ -z "$path" ]; then
                                                path="/"
                                                break
                                        fi
                                        df -k "$path" 1>/dev/null 2>/dev/null
                                done

                                df -k "$path" | sed -n '$p' | awk '{print $1}'
                        ;;
                esac
        fi
      `wrlLogFuncExit  "mes4Path1()"`
}

# find the os version, release and version, hardware structure information
findOSInfo(){
Osname=`uname`
        `wrlLogFuncStart"findOSInfo()"`
        `wrlDebugFuncParam "OSName" "$Osname"`
	

       oo=`uname | cut -c0-1`		
	hh=""
	rr=""
	vv=""
	kk=""
	case "$oo" in
	L)
		case `uname -i` in
		i386)
			hh="I"
		;;
		s390*)
			hh="Z"
		;;
		esac

		rr=`cat /etc/*release 2>/dev/null | sed -n '1p' | cut -c0-1`
		vv=`cat /etc/*release 2>/dev/null | sed -n '1p' | tr " " "\n" | sed -n '/^\([0-9]\{1,\}\)$/p'`
		
		kk=`uname -r | cut -c0-3` 
	;;
	*)
	;;
	esac

	echo $oo$kk$hh$rr$vv
   `wrlDebugFuncReturn  "$oo$kk$hh$rr$vv"`
        `wrlLogFuncExit  "findOSInfo()"`
}

telnetNFS(){
    	`wrlLogFuncStart "telnetNFS()"`
        `wrlDebugFuncParam "IP" "$1"`
        ip=$1
	telnet_out_log=/$TMP_DIR/prs.prc_telnet_out.log
    	default_nfs_port=2049
	max_tries=10 #telnet timeout in seconds
    
	telnet $ip $default_nfs_port >$telnet_out_log 2>/dev/null &
	telnet_pid="$!"
	
	result="FALSE"
	try_nr=0
	while [ $try_nr -lt $max_tries ]; do
		grep "Connected" $telnet_out_log >/dev/null 2>&1
		if [ $? -eq 0 ]; then
			result="TRUE"
			break
		fi
		sleep 1
		try_nr=`expr $try_nr + 1`
	done
	
	kill -9 $telnet_pid >/dev/null 2>&1	
	rm -rf $telnet_out_log 2>/dev/null
	
	echo $result
      `wrlDebugFuncReturn  "$result"`
        `wrlLogFuncExit  "telnetNFS()"`



}

## function for NFS check
NFScheck(){
	 `wrlLogFuncStart "NFScheck()"`
        `wrlDebugFuncParam "Dir" "$1"`

        checked_dir=$1
	mount_result_log=/$TMP_DIR/prs.prc_mount_result.log
	nfs_list_log=/$TMP_DIR/prs.prc_nfs_list.log
	
	mount | sed 's/\( \{1,\}\)/-/g' > $mount_result_log
	if [ "`uname`" = "SunOS" ]; then
		grep -i remote $mount_result_log > $nfs_list_log
	else
		grep -i nfs- $mount_result_log > $nfs_list_log
		grep -i nfs[0-9]- $mount_result_log >> $nfs_list_log
		grep -i nfsv[0-9]- $mount_result_log >> $nfs_list_log
	fi
	
	nfs_check_status="TRUE"
	
	if [ -s $nfs_list_log ]; then
		ping_count=2
		
		for mount_entry in `cat $nfs_list_log | sort | uniq`; do
			case `uname` in
	        SunOS)
				ip_addr=`echo $mount_entry | cut -d ":" -f1 | nawk -F '-' '{print $3}'`
				nfs_path=`echo $mount_entry | nawk -F '-' '{print $1}'`
				check=`echo $checked_dir | grep -w $nfs_path`
				if [ $check ]; then
					responses_count=`ping -s $ip_addr 64 $ping_count | grep "packets transmitted" | nawk -F ',' '{print $2}' | nawk -F ' ' '{print $1}'`
					if [ $responses_count -eq 0 ]; then
						nfs_check_status="FALSE"
					else
						if [ `telnetNFS $ip_addr` = "FALSE" ]; then
							nfs_check_status="FALSE"
						fi
					fi
				fi
			;;
	        HP-UX)
				ip_addr=`echo $mount_entry | cut -d ":" -f1 | awk -F '-' '{print $3}'`
				nfs_path=`echo $mount_entry | awk -F '-' '{print $1}'`
				check=`echo $checked_dir | grep -w $nfs_path` 
				if [ $check ]; then
					responses_count=`ping $ip_addr -n $ping_count | grep "packets transmitted" | awk -F ',' '{print $2}' | awk -F ' ' '{print $1}'`
					if [ $responses_count -eq 0 ]; then
						nfs_check_status="FALSE"
					else
						if [ `telnetNFS $ip_addr` = "FALSE" ]; then
							nfs_check_status="FALSE"
						fi
					fi
				fi
			;;
	        AIX)
				ip_addr=`echo $mount_entry | awk -F '-' '{print $1}'`
				nfs_path=`echo $mount_entry | awk -F '-' '{print $3}'`
				check=`echo $checked_dir | grep -w $nfs_path`
				if [ $check ]; then
					responses_count=`ping -c $ping_count $ip_addr | grep "packets transmitted" | awk -F ',' '{print $2}' | awk -F ' ' '{print $1}'`
					if [ $responses_count -eq 0 ]; then
						nfs_check_status="FALSE"
					else
						if [ `telnetNFS $ip_addr` = "FALSE" ]; then
							nfs_check_status="FALSE"
						fi
					fi
				fi
			;;
			Linux)
				ip_addr=`echo $mount_entry | cut -d ":" -f1`
				nfs_path=`echo $mount_entry | awk -F '-' '{print $3}'`
				check=`echo $checked_dir | grep -w $nfs_path`
				if [ $check ]; then
					responses_count=`ping -c $ping_count $ip_addr | grep "packets transmitted" | awk -F ',' '{print $2}' | awk -F ' ' '{print $1}'`
					if [ $responses_count -eq 0 ]; then
						nfs_check_status="FALSE"
					else
						if [ `telnetNFS $ip_addr` = "FALSE" ]; then
							nfs_check_status="FALSE"
						fi
					fi
				fi
			;;
			esac
		done #for
	fi

	rm -rf $mount_result_log 2>/dev/null
	rm -rf $nfs_list_log 2>/dev/null
	
	echo $nfs_check_status

       `wrlDebugFuncReturn "$nfs_check_status"`
        `wrlLogFuncExit  "NFScheck()"`

}
versionCompare() {
#Handle special cases
`wrlLogFuncStart "versionCompare()"`
`wrlDebugFuncParam "comapreThis" "$1"`
`wrlDebugFuncParam "compareWith" "$1"`



check1=`echo $1 | sed 's/*/#/g'`
check2=`echo $2 | sed 's/*/#/g'`


ver1Parts=`echo $check1 | awk -F"." '{print $1,$2,$3,$4,$5,$6}'`
ver2Parts=`echo $check2 | awk -F"." '{print $1,$2,$3,$4,$5,$6}'`
set -- $ver1Parts
set -- $ver2Parts

lenver1Parts=`echo $ver1Parts | wc -w`
lenver2Parts=`echo $ver2Parts | wc -w`

#echo "INSIDE "$ver1Parts
#echo "INSIDE "$ver2Parts

#echo "lenver1Parts=$lenver1Parts"
#echo "lenver2Parts=$lenver2Parts"

# if last version part is "*" treat all missing part as *

if [ $lenver1Parts -ge $lenver2Parts ]; then
     len=`echo $ver2Parts | wc -m`
#    echo "Len=$len"
#echo "***************************"$ver2Parts
     len2=`expr $len - 1`
     last=`echo $ver2Parts | cut -c $len2`
#echo "ZZZZZZZcheck1:-$last"
      if [ "$last" = "#" ]; then
        	type=`uname`
#        	echo $type
      		if [ "$type" = "HP-UX" ]; then
#      			echo $type 
        		i=$lenver2Parts
       		while [ $i -le  $lenver1Parts - 1 ]; do
        		ver2Parts=$ver2Parts" #"
       		((i++))
       		done
      		else 
			i=0;
                        lenver1PartsMinusOne=`expr $lenver1Parts - 1`
			while [ $i -lt $lenver1PartsMinusOne  ]; do
			ver2Parts=$ver2Parts" #"
			i=`expr $i + 1`	
			done
        	fi
      fi
elif [ $lenver2Parts -ge $lenver1Parts ]; then

      	len1=`echo $ver1Parts | wc -m`
#   echo "Len1=$len1"
	len3=`expr $len1 - 1`
      	last=`echo $ver1Parts | cut -c $len3`
#   echo "check2:-$last"
      	if [ "$last" = "#" ]; then
         	type=`uname`
      		if [ "$type" = "HP-UX" ]; then
      			i=$lenver1Parts
      			while [ $i -le  $lenver2Parts-1 ]; do
      			ver1Parts=$ver1Parts" #"
      			((i++))
      			done
      		else
			i=0;
			while [ $i -lt $lenver2Parts-1 ]; do
			ver2Parts=$ver2Parts" #"
			((i++))
			done
        	fi
      	fi
fi

#echo "YYYYYYYY"$ver2Parts
#echo "YYYYYYYY"$ver1Parts
j=1
while [ $j -le $lenver2Parts -o $j -le $lenver1Parts ]
do 
#echo "IN SIDE WHILE--"$lenver1Parts
	v1Str="UNASSIGNED"
	v2Str="UNASSIGNED"

   	if [ $j -le $lenver1Parts ]; then
   		v1=`echo $ver1Parts | cut -d ' ' -f$j`
  		res=`echo $v1 | tr -d '[0-9]' | tr -d '.'`
#		res=`echo $l | grep "]"`
#		echo "Value=" $res
		if [ $res ]; then
#	echo "char"
       		v1Str=$v1
       		if [ $j -le $lenver2Parts ]; then
       			v2Str=`echo $ver2Parts | cut -d ' ' -f$j`
       		else
       			v2Str="0"
       		fi
		fi
   	else
     		v1=0
   	fi

   if [ $j -le $lenver2Parts ]; then
   	v2=`echo $ver2Parts | cut -d ' ' -f$j` 
       res=`echo $v2 | tr -d '[0-9]' | tr -d '.'`
      #  res=`echo $l | grep "]"`
       if [ $res ]; then
 # echo "char"
           if [ $j -le $lenver1Parts ]; then
           	v1Str=`echo $ver1Parts | cut -d ' ' -f$j`
           else
           	v1Str="0"
           fi
           v2Str=$v2
        fi
   else
     	v2=0
   fi
#  echo "v1=$v1"
#  echo "v2=$v2"
#  echo "v1Str=$v1Str"
#  echo "v2Str=$v2Str"

   if [ "$v1Str" != "UNASSIGNED" -o "$v2Str" != "UNASSIGNED" ]; then
       if [ "$v1Str" = "" ]; then
           v1Str="0"
       fi
       if [ "$v2Str" = "" ]; then
            v2Str="0"
       fi
 #     echo "Inside String check"   

	#Comparing as string
     if [ "$v1Str" != "#" -a "$v2Str" != "#" ]; then
  #      echo "Inside String check1"
            if [ "$v1Str" != "$v2Str" ]; then 
  #            echo "Inside String check2"
	      if [ "$v1Str" > "$v2Str" ]; then
  #             echo "Inside String check3"       
                  versionCompare=1
              else
                  versionCompare=-1
              fi
              echo $versionCompare
              `wrlDebugFuncReturn  "$versionCompare"`
                 `wrlLogFuncExit "versionCompare()"`

              exit
          fi
     fi

   else
	#Comparing as number
#  echo "compared number"
  if [ "$v1" -gt "$v2" ]; then
        versionCompare=1
        echo $versionCompare
       `wrlDebugFuncReturn  "$versionCompare"`
                 `wrlLogFuncExit "versionCompare()"`
 
       exit
  fi
 # echo "compared number2"
  if [ "$v2" -gt "$v1" ]; then
        versionCompare=-1
        echo $versionCompare
         `wrlDebugFuncReturn  "$versionCompare"`
         `wrlLogFuncExit "versionCompare()"`
         exit
  fi
#    echo "compared number3"
   fi 
   j=`expr $j + 1` 
#   echo "j=$j"
done
# if we reach here,versions must be equal
versionCompare=0
echo $versionCompare
`wrlDebugFuncReturn  "$versionCompare"`
         `wrlLogFuncExit "versionCompare()"`

}
DirAdd()
{
PREREQ_HOME=$1
for EachPrdctCode in `echo "$product_version" | tr "," "\n" | sed '/^$/d'`
do
 		EachPrdctCode=`echo $EachPrdctCode | cut -d "+" -f1`
        cfile=`ls $tempd | grep -i $EachPrdctCode | grep -i .cfg`
        if [ $cfile ]; then
                IsOsSpaceFound=`cat $tempd/$cfile | grep -c "os.space" | grep -v "#os.space"`
        else
                IsOsSpaceFound=0
        fi
        if [ $IsOsSpaceFound -gt 0 ]; then

                InitValue=1
                while  [ $InitValue -le $IsOsSpaceFound ]
                do
                        Line=`cat $tempd/$cfile | grep "os.space" | grep -v "#os.space" | sed -n "$InitValue"p | sed 's/#/;/g'`
                        ###Refining for Qualifier####
                        IsQualFound=`echo "$Line" | grep "]"`
                        if [ $IsQualFound ]; then
                                ####Handling the Unit Check####
                                Unitfound=`echo "$Line" | grep -i unit`
                                if [ $Unitfound ]; then
                                        Unit=`echo "$Line" | sed 's/.*\,\(.*\)/\1/' | cut -d "]" -f1 | cut -d ":" -f2`
                                        ExpValue=`echo "$Line" | cut -d "]" -f2`
                                     ExpValue=$ExpValue$Unit
                                else
                                     ExpValue=`echo "$Line" | cut -d "]" -f2`
                                fi
                        else
                                ExpValue=`echo "$Line" | cut -d "=" -f2`
                        fi
                        resultfile=`ls $tempd | grep -i $EachPrdctCode | grep -i .txt`
                        # removed the "NOT_REQ_CHECK_ID" from the below line because of the RTC defect 26196
                        #ActLine=`cat $tempd/$resultfile | grep "os.space" | grep -v "NOT_REQ_CHECK_ID" | sed -n "$InitValue"p`
                        ActLine=`cat $tempd/$resultfile | grep "os.space" | sed -n "$InitValue"p`
                        ActValue=`echo "$ActLine" | cut -d "=" -f2`
                        NoofArg=`echo $Line | grep "," | wc -l`

                        if [ $IsQualFound ]; then

                        ###Finding the proper value According to root and non-root####
                        ###This will be handling all the cases Qualifier like only root,only non_root,both root and no_root and Old Os.space Notation#####
                        
                        UserHomeFound=`echo "$Line" | grep -i USERHOME`
                        if [ $UserHomeFound ]; then
                                UsrHome=`echo $HOME`
                                Line=`echo $Line | sed 's:USERHOME:'$UsrHome':g' | sed 's:HOST:'$HostName':g'`
                        fi
                        

                                Validate_ID=`id | awk '{print $1}'| cut -d "(" -f1 | cut -d "=" -f2`
                                Found=`echo "$Line" | grep ";"`
                                if [ $NoofArg -gt 0 ]; then
                                        if [ $Validate_ID -eq 0 ]; then
                                                ###Validating for root and non-root####
                                                if [ $Found ]; then
                                                        DirtoCheck=`echo "$Line" | cut -d"=" -f3 | cut -d ";" -f1`
                                                else
                                                        DirtoCheck=`echo  "$Line" | cut -d"=" -f3 | cut -d "," -f1`

                                                fi
                                        else
                                                ###Validating for root and non-root####
                                                 if [ $Found ]; then
                                                        Dir=`echo "$Line" | cut -d "=" -f4 | cut -d "," -f1`
                                                else
                                                          Dir=`echo "$Line" | cut -d "=" -f3 | cut -d "," -f1`
                                                fi
                                                UsrHome=`echo $HOME`
                                                HostName=`hostname`
                                                DirtoCheck=`echo $Dir | sed 's:USERHOME:'$UsrHome':g' | sed 's:HOST:'$HostName':g'`
                                        fi
                                else
                                        if [ $Validate_ID -eq 0 ]; then
                                                ###Validating for root and non-root####
                                                if [ $Found ]; then
                                                        DirtoCheck=`echo "$Line" | cut -d "=" -f3 | cut -d ';' -f1`
                                                else
                                                        DirtoCheck=`echo "$Line" | cut -d "=" -f3 | cut -d "]" -f1`
                                                fi
                                        else
                                                ###Validating for root and non-root####
                                                if [ $Found ]; then
                                                        Dir=`echo "$Line" | cut -d"=" -f4 | cut -d "]" -f1`
                                                else
                                                        Dir=`echo "$Line" | cut -d "=" -f3 | cut -d "]" -f1`
                                                fi
                                                UsrHome=`echo $HOME`
                                                HostName=`hostname`
                                                DirtoCheck=`echo $Dir | sed 's:USERHOME:'$UsrHome':g' | sed 's:HOST:'$HostName':g'`
                                        fi
                                fi
                        else
                                DirtoCheck=`echo "$Line" | grep "os.space" | cut -d "=" -f1 | cut -d "." -f3`
                                if [ $DirtoCheck ]; then
                                        DirtoCheck=/$DirtoCheck
                                fi
                        fi

                        if [ $DirtoCheck ]; then
                                if [ "$ExpValue" != "" -a "$ActValue" != "" ]; then
                                        echo `mes4Path "$DirtoCheck"` $ExpValue $ActValue >> $totaltf
                                fi
                        fi
                        InitValue=`expr $InitValue + 1`
                done

        fi
done
}

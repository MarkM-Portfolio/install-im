#!/bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

. $PREREQ_HOME/lib/common_function.sh
#appends or trims mantissa length to 2, 123MB -> 123.00MB, 123.123MB -> 123.12MB
#tested on Linux|AIX|HPUX
formatSizeDisplay() {
        valueLength=`echo $1| wc -m`
    splitId=`expr $valueLength - 3`
    valuePart=`echo $1 | cut -c1-$splitId`
    unitId=`expr $splitId + 1`
    unitPart=`echo $1 | cut -c${unitId}-`

    echo $valuePart | grep \\. > /dev/null
    if [ $? = 0 ]; then
        mantissaTmp=`echo $valuePart | cut -d"." -f2 | wc -m`
        mantissaLength=`expr $mantissaTmp - 1`
        if [ $mantissaLength -eq 0 ]; then
            echo "${valuePart}00${unitPart}"
        elif [ $mantissaLength -eq 1 ]; then
            echo "${valuePart}0${unitPart}"
        elif [ $mantissaLength -eq 2 ]; then
            echo "$1"
        else
                correctPosition=`expr $splitId - $mantissaLength + 2`
            echo "`echo $valuePart | cut -c-$correctPosition`${unitPart}"
        fi
    else
            echo "${valuePart}.00${unitPart}"
    fi
}

ff="$TMP_DIR/localhost_hw.txt"

case `uname` in
        SunOS)
                # get CPU Info
                echo "No of CPUs="`psrinfo -v | sed -n '/Status of/p' | wc -l | sed 's/[ ]*//g'` > $ff
                itype=`psrinfo -v | grep -i "intel"`
                if [ "$itype" = "" ]; then
                        echo "risc.cpu="`psrinfo -v | sed -n '/operates at/p' | uniq | sed 's/.* operates at \(.*\),/\1/g' | sed 's/[ ]*//g'` >> $ff
                else
                        echo "intel.cpu="`psrinfo -v | sed -n '/operates at/p' | uniq | sed 's/.* operates at \(.*\),/\1/g' | sed 's/[ ]*//g'` >> $ff
                fi

                #Calculating the disk
                path=$1
                #Validating the Path
                IsPath=`echo "$path" | cut -c1`
                if [ "$IsPath" = "/" ]; then

	                nfs_check_status=`NFScheck $path`
        	        if [ "$nfs_check_status" = "TRUE" ]; then

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

                	dsize=`df -k "$path" | awk '{ if(NF<7 && NF>1){ print $(4)/1024"MB" } }'`
                	echo "Disk=`formatSizeDisplay $dsize`" >> $ff
                        else
                                echo "Disk=NFS_NOT_AVAILABLE" >> $ff
                        fi
	
                else
                        echo "Disk=NOT_A_VALID_PATH" >> $ff
                fi
			nfs_check_status1=`NFScheck /tmp`
                        if [ "$nfs_check_status1" = "TRUE" ]; then
        	                df -k /tmp | awk '{if(NF<7){if(NF>1){print "Temp="$(NF-2)/1024"MB";} }}' >> $ff 
                        else
                	         echo "Temp=NFS_NOT_AVAILABLE" >> $ff
                        fi

                #memory
                echo "Memory="`vmstat | sed -n '3p' | awk '{print $5/1024,"MB"}' | sed 's/[ ]*//g'` >> $ff
        ;;
	 AIX)
                #get CPU Info
                tt="./temp_aix_hw"
                prtconf > $tt
                echo "No of CPUs="`cat $tt | sed -n '/Number Of Processors/p' | awk -F: '{printf $2}' | sed 's/[ ]*//g'` > $ff
                itype=`cat $tt | grep -i "intel"`
                if [ "$itype" = "" ]; then
                        echo "risc.cpu="`cat $tt | sed -n '/Processor Clock Speed/p' | awk -F: '{print $2}' | sed 's/[ ]*//g'` >> $ff
                else
                        echo "intel.cpu="`cat $tt | sed -n '/Processor Clock Speed/p' | awk -F: '{print $2}' | sed 's/[ ]*//g'` >> $ff
                fi
                #Calculating the disk
                path=$1
                #Validating the Path
                IsPath=`echo "$path" | cut -c1`
                if [ "$IsPath" = "/" ]; then

			nfs_check_status=`NFScheck $path`
        	        if [ "$nfs_check_status" = "TRUE" ]; then

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
		
			df -m "$path" | awk '{if(NF<=7){if(NF>1){print "Disk="$(NF-4)"MB";} }}' >> $ff
                        else
                                echo "Disk=NFS_NOT_AVAILABLE" >> $ff
                        fi

                else
                        echo "Disk=NOT_A_VALID_PATH" >> $ff
                fi

                	nfs_check_status1=`NFScheck /tmp`
                        if [ "$nfs_check_status1" = "TRUE" ]; then
	                        df -m /tmp | awk '{if(NF<=7){if(NF>1){print "Temp="$(NF-4)"MB";} }}' >> $ff
                        else
        	                 echo "Temp=NFS_NOT_AVAILABLE" >> $ff
                        fi
    
                #memory
                vmstat | tail -n 1 | awk '{print "Memory="($4)*4/1024"MB"}' >> $ff 
                    rm -f $tt 2>/dev/null

        ;;
	HP-UX)                                                                                                                                                                   
                # get CPU info
		var=`uname -r`
		if [ "$var" = "B.11.11" -o  "$var" = "B.11.23" ]; then
			echo "No of CPUs="`/usr/sbin/ioscan -kf | grep processor | wc -l` > $ff
                        MHZ=`echo itick_per_usec/D | adb /stand/vmunix /dev/mem 2>/dev/null | tail -1 | awk '{print $2}'` 
			echo "risc.cpu="$MHZ"MHz" >> $ff

		else
 
                	echo "No of CPUs="`machinfo | grep "processors"  | sed -n '1p' | awk '{print $1}' ` > $ff
                	itype=`machinfo | grep "processor" | grep -v "processors" | grep -i "intel"`
                
 
		if [ "$itype" = "" ]; then
                        echo "risc.cpu="`machinfo | grep "processor" | head -1 | awk -F"(" '{print $3}' | awk -F, '{print $1}' | sed 's/[ ]*//g'` >> $ff            
                else
                        echo "intel.cpu="`machinfo | grep "processor" | grep -i Intel | awk -F"(" '{print $3}' | awk -F, '{print $1}' | sed 's/[ ]*//g'` >> $ff           
                fi
		
		fi	

                #Calculating the disk
                path=$1
                #Validating the Path
                IsPath=`echo "$path" | cut -c1`
                if [ "$IsPath" = "/" ]; then

        	        nfs_check_status=`NFScheck $path`
	                if [ "$nfs_check_status" = "TRUE" ]; then

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

			df -k "$path" | grep -i "free" | awk '{print "Disk="$1/1024"MB"}' >> $ff
                        else
                                echo "Disk=NFS_NOT_AVAILABLE" >> $ff
                        fi
                else
                        echo "Disk=NOT_A_VALID_PATH" >> $ff
                fi

               		nfs_check_status1=`NFScheck /tmp`
                        if [ "$nfs_check_status1" = "TRUE" ]; then
	                	df -k /tmp | grep -i "free" | awk '{print "Temp="$1/1024"MB"}' >> $ff         
                        else
        	                 echo "Temp=NFS_NOT_AVAILABLE" >> $ff
                        fi
                 # memory

                swapinfo | grep "memory" | awk '{print "Memory="$2/1024"MB"}' >> $ff

        ;;

        Linux)
                # get CPU info
                cat /proc/cpuinfo | grep "model name" | awk -F: '{print "CPU Name="$2}' > $ff
                cat /proc/cpuinfo | grep "cores" | awk -F: '{print "No of CPUs="$2}' >> $ff

                #Calculating the disk
                path=$1
                #Validating the Path
                IsPath=`echo "$path" | cut -c1`
                if [ "$IsPath" = "/" ]; then
 
			nfs_check_status=`NFScheck $path`
        	        if [ "$nfs_check_status" = "TRUE" ]; then
	
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

                	df -h "$path" | awk '{if(NF<7){if(NF>1){print "Disk="$(NF-2)"B";} }}' >> $ff
                                       else
                                echo "Disk=NFS_NOT_AVAILABLE" >> $ff
                        fi
                else
                        echo "Disk=NOT_A_VALID_PATH" >> $ff
                fi
                nfs_check_status1=`NFScheck /tmp`
                if [ "$nfs_check_status1" = "TRUE" ]; then
 
			df -k /tmp >/dev/null 2>&1
			if [ $? = 0 ]; then
				tmpfs_var=/tmp
			else
				tmpfs_var=`mount | grep tmpfs | awk '{print $3}'`
			fi
		else
                        echo "Temp=NFS_NOT_AVAILABLE" >> $ff
                fi
	
                	nfs_check_status1=`NFScheck $tmpfs_var`
                	if [ "$nfs_check_status1" = "TRUE" ]; then
       		         	df -h "$tmpfs_var" | awk '{if(NF<7){if(NF>1){print "Temp="$(NF-2)"B";} }}' >> $ff
                        else
                	         echo "Temp=NFS_NOT_AVAILABLE" >> $ff
	                fi

                # get memory info, free + buffers + cached
                free -m | grep Mem | awk '{print "Memory="$4+$6+$7"MB";}' >> $ff

esac
#echo $ff
#cat $ff

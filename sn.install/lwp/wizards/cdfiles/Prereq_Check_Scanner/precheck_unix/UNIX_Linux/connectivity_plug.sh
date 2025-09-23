#!/bin/sh

# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


# test if the host is parseable
Gentelnet(){
    ip=$1
    port=$2
        telnet_out_log=/tmp/prs.gen_telnet_out.log
        max_tries=10 #telnet timeout in seconds

        telnet $ip $port >$telnet_out_log 2>/dev/null &
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
}
checkport(){
        case `uname` in
                HP-UX)
	responses_count=`ping $1 -n 1 | grep "packets transmitted" | awk -F ',' '{print $2}' | awk -F ' ' '{print $1}'`
	if [ $responses_count -ne 0 ]; then
                
		status=`Gentelnet $1 $2` 
                if [ "$status" = "TRUE" ]; then
                        echo "Connectivity        PASS      connect to $1:$2"
                else
                        echo "Connectivity        FAIL      fail to connect $1:$2"
                fi
                
        else
                echo "Connectivity        FAIL      $1 Unreachable"
        fi
                ;;
               SunOS)
	resp_count=`ping -s $1 64 2 | grep "packets transmitted" | nawk -F ',' '{print $2}' | nawk -F ' ' '{print $1}'`
		if [ $resp_count -ne 0 ];then
			status=`Gentelnet $1 $2`
		                if [ "$status" = "TRUE" ]; then
                        echo "Connectivity        PASS      connect to $1:$2"
                else
                        echo "Connectivity        FAIL      fail to connect $1:$2"
                fi

	        else
       		         echo "Connectivity        FAIL      $1 Unreachable"
        	fi
                ;;
 
		*)
        if ping -c 1 "$1" 1>/dev/null 2>/dev/null; then                                                                                                                                            
                case `uname` in
                AIX)
			status=`Gentelnet $1 $2`
                ;;
                HP-UX)
			status=`Gentelnet $1 $2`
                ;;
                *)
			status=`Gentelnet $1 $2`	
                ;;
                esac
                if [ "$status" = "TRUE" ]; then
                        echo "Connectivity        PASS      connect to $1:$2"
                else
                        echo "Connectivity        FAIL      fail to connect $1:$2"
                fi
                rm -f $tf
        else
                echo "Connectivity        FAIL      $1 Unreachable"
        fi
                ;;
        esac
}


# parse the parameters
# to keep close to createNode's parameters

SERVER=""
PROTOCOL=""
PROTOCOL1=""
PROTOCOL2=""
PROTOCOL3="" 
PORT=""

BACKUP=""
BSERVER=""
BPROTOCOL=""
BPROTOCOL1=""
BPROTOCOL2=""
BPROTOCOL3="" 
BPORT=""

cf="./../lib/common_configuration"

if [ $# -ge 2 ]; then
	for tt in `echo "$2" | tr "," "\n"`
	do
		if [ "$SERVER" = "" ]; then
			SERVER=`echo $tt | awk -F= '{if($1 ~ /^SERVER/){print $2}}'`
		fi
		if [ "$PROTOCOL" = "" ]; then
			PROTOCOL=`echo $tt | awk -F= '{if($1 ~ /^PROTOCOL/ ){print $2}}'`
		fi
		if [ "$PROTOCAL1" = "" ]; then
			PROTOCAL1=`echo $tt | awk -F= '{if($1 ~ /^PROTOCAL1/){print $2}}'`
		fi
		if [ "$PROTOCOL2" = "" ]; then
			PROTOCOL2=`echo $tt | awk -F= '{if($1 ~ /^PROTOCOL2/ ){print $2}}'`
		fi
		if [ "$PROTOCOL3" = "" ]; then
			PROTOCOL3=`echo $tt | awk -F= '{if($1 ~ /^PROTOCOL3/ ){print $2}}'`
		fi
		if [ "$PORT" = "" ]; then
			PORT=`echo $tt | awk -F= '{if($1 ~ /^PORT/ ){print $2}}'`
		fi
		if [ "$BACKUP" = "" ]; then
			BACKUP=`echo $tt | awk -F= '{if($1 ~ /^BACKUP/ ){print $2}}'`
		fi
		if [ "$BSERVER" = "" ]; then
			BSERVER=`echo $tt | awk -F= '{if($1 ~ /^BSERVER/ ){print $2}}'`
		fi
		if [ "$BPROTOCOL" = "" ]; then
			BPROTOCOL=`echo $tt | awk -F= '{if($1 ~ /^BPROTOCOL/ ){print $2}}'`
		fi
		if [ "$BPROTOCOL1" = "" ]; then
			BPROTOCOL1=`echo $tt | awk -F= '{if($1 ~ /^BPROTOCOL1/ ){print $2}}'`
		fi
		if [ "$BPROTOCOL2" = "" ]; then
			BPROTOCOL2=`echo $tt | awk -F= '{if($1 ~ /^BPROTOCOL2/ ){print $2}}'`
		fi
		if [ "$BPROTOCOL3" = "" ]; then
			BPROTOCOL3=`echo $tt | awk -F= '{if($1 ~ /^BPROTOCOL3/ ){print $2}}'`
		fi
		if [ "$BPORT" = "" ]; then
			BPORT=`echo $tt | awk -F= '{if($1 ~ /^BPORT/ ){print $2}}'`
		fi
	done
fi

# parse the SERVER parameter, to see if contains protocol

if [ -z "$SERVER" ]; then
	SERVER=`cat $cf | sed -n '/^SERVER=/p' | awk -F= '{if($1 ~ /^SERVER/ ){print $2}}'`
fi
if [ -z "$PROTOCOL" ]; then
	PROTOCOL=`cat $cf | sed -n '/^PROTOCOL=/p' | awk -F= '{if($1 ~ /^PROTOCOL/ ){print $2}}'`
fi
if [ -z "$PROTOCOL1" ]; then
	PROTOCOL1=`cat $cf | sed -n '/^PROTOCOL1=/p' | awk -F= '{if($1 ~ /^PROTOCOL1/){print $2}}'`
fi

cpro=`echo $SERVER | grep "//"`
pp=""
tems_server=""
if [ -z "$cpro" ]; then
	tems_server=`echo "$SERVER" | awk -F: '{print $1}'`
	
	if [ -z "$PROTOCOL" ]; then
		if [ -n "$PROTOCOL1" ]; then
			pp="$PROTOCOL1"
		fi
	else
		pp="$PROTOCOL"
	fi	
else
	#pp=`echo "$SERVER" | awk -F"//" '{print $1}'`
	pp=`echo "$SERVER" |  sed 's/^\(.*\)\/\/\(.*\)$/\1/g'`
	#tems_server=`echo "$SERVER" | awk -F"//" '{print $2}' | awk -F: '{print $1}'`
	tems_server=`echo "$SERVER" |  sed 's/^\(.*\)\/\/\(.*\)$/\2/g' | awk -F: '{print $1}'`
fi

pp=`echo $pp | sed 's/://g'`
# to see if there contains port
cport=`echo "$SERVER" | sed 's/.*:\([0-9]\{1,\}\)$/\1/' | sed -n '/^[0-9]\{1,\}$/p'`

if [ -z "$cport" ]; then
	cport="$PORT"
fi

if [ -z "$cport" ]; then
	cport=`cat $cf | sed -n '/^PORT=/p' | awk -F= '{if($1 ~ /PORT/ && $1 !~ /B/){print $2}}'`
fi 

if [ -z "$cport" ]; then
	if [ "$pp" = "IP.PIPE" ]; then
		cport="1918"
	elif [ "$pp" = "IP.SPIPE" ]; then
		cport="3660"
	fi
fi

# don't check PROTOCOL2 and PROTOCOL3 now

# check the port
checkport "$tems_server" "$cport" 


# check the backup tems server
if [ -z "$BACKUP" ]; then
	BACKUP=`cat $cf | sed -n '/BACKUP=/p' | awk -F= '{if($1 ~ /BACKUP/ ){print $2}}'`
fi
	if [ "$BACKUP" = "YES" ]; then
	####### back up one
if [ -z "$BSERVER" ]; then
	BSERVER=`cat $cf | sed -n '/BSERVER=/p' | awk -F= '{if($1 ~ /BSERVER/ ){print $2}}'`
fi
if [ -z "$BPROTOCOL" ]; then
	BPROTOCOL=`cat $cf | sed -n '/BPROTOCOL=/p' | awk -F= '{if($1 ~ /BPROTOCOL/ ){print $2}}'`
fi
if [ -z "$BPROTOCOL1" ]; then
	BPROTOCOL1=`cat $cf | sed -n '/BPROTOCOL1=/p' | awk -F= '{if($1 ~ /BPROTOCOL1/ ){print $2}}'`
fi

cpro=`echo $BSERVER | grep "//"`
pp=""
tems_server=""
if [ -z "$cpro" ]; then
	tems_server=`echo "$BSERVER" | awk -F: '{print $1}'`
	
	if [ -z "$BPROTOCOL" ]; then
		if [ -n "$BPROTOCOL1" ]; then
			pp="$BPROTOCOL1"
		fi
	else
		pp="$BPROTOCOL"
	fi	
else
	#pp=`echo "$BSERVER" | awk -F"//" '{print $1}'`
	pp=`echo "$BSERVER" |  sed 's/^\(.*\)\/\/\(.*\)$/\1/g'`
	#tems_server=`echo "$BSERVER" | awk -F"//" '{print $2}' | awk -F: '{print $1}'`
	tems_server=`echo "$BSERVER" |  sed 's/^\(.*\)\/\/\(.*\)$/\2/g' | awk -F: '{print $1}'`
fi

pp=`echo $pp | sed 's/://g'`
# to see if there contains port
cport=`echo "$BSERVER" | sed 's/.*:\([0-9]\{1,\}\)$/\1/' | sed -n '/^[0-9]\{1,\}$/p'`

if [ -z "$cport" ]; then
	cport="$BPORT"
fi

if [ -z "$cport" ]; then
	cport=`cat $cf | sed -n '/BPORT=/p' | awk -F= '{if($1 ~ /BPORT/){print $2}}'`
fi 

if [ -z "$cport" ]; then
	if [ "$pp" = "IP.PIPE" ]; then
		cport="1918"
	elif [ "$pp" = "IP.SPIPE" ]; then
		cport="3660"
	fi
fi

# don't check PROTOCOL2 and PROTOCOL3 now

# check the port
checkport "$tems_server" "$cport" 

#### end of back up
	fi


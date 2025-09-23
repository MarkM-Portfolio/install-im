#!/bin/sh
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


. ../lib/common_function.sh

checkSunOS() {
	if [ "`uname -p`" = "sparc" ]; then
		echo "SOLARISSPARC"
	else
		echo "SOLARISX86"
	fi
}
checkHpux() {
	if [ "`uname -m`" = "ia64" ]; then
		echo "HPUXIA64"
	else
		echo "HPUXPARISC"
	fi
}
checkLinux() {
	type=`uname -m`
	if [ "$type" = "ppc64" -o "$type" = "ppc" ]; then
		echo "LINUXPSERIES"
	elif [ "$type" = "s390x" -o "$type" = "s390" ]; then
		echo "LINUXZSERIES"
	else
		echo "LINUXX86"
	fi
}

getSystemId() {
	case $SYSTEM in
		Linux)
			checkLinux
		;;
		SunOS)
			checkSunOS
		;;
		AIX)
			echo "AIX"
		;;
		HP-UX)
			checkHpux
		;;
	esac
}


#set value for the key in a given file (if key exists), syntax: setValue KEY VALUE FILENAME
setValue() {
  	KEY=$1 ; VALUE=$2 ; FILE=$3
  	echo "DEBUG: setValue: KEY="$KEY " VALUE="$VALUE >>${DEBUG_FILE}
  	sed -e "s/^$KEY=.*$/$KEY=$VALUE/g" $FILE > .putValueTmp
  	cat .putValueTmp > $FILE
  	rm .putValueTmp
}

#read value for the key in a given file (if key exists), syntax: getValue KEY FILENAME
getValue() {
  	KEY=$1 ; FILE=$2
  	value=`cat $FILE | grep $KEY | cut -d"=" -f2`
  	echo "DEBUG: getValue: KEY=$KEY FILE=$FILE value=$value" >>${DEBUG_FILE}
  	echo $value
}

#put value from TAD config to PREREQ_CHECKER config for the current SYSTEM
copyValue() {
	VAL_TO=$1 ; VAL_FROM=$2
	VALUE=`getValue ${SYSTEMID}_${VAL_FROM} ${TAD_CFG}`
	setValue $VAL_TO $VALUE $CFG
}

#parse parameter from -p param-list and put its value in the list
parseDirParameter() {
	INDEX=$1
	val=`eval echo \\\$key${INDEX}=*`
	case $ARG in
		$val) 
		pathValue=`echo $ARG | awk -F"=" '{print $2}'`
		echo "DEBUG: parseDirParameter: pathValue="$pathValue >>${DEBUG_FILE}
		eval path${INDEX}=$pathValue
	esac
}

#appends or trims mantissa length to 2, 123MB -> 123.00MB, 123.123MB -> 123.12MB
#tested on Linux|AIX|HPUX
formatSizeDisplay() {
	echo "DEBUG: formatSizeDisplay: before="$1 >>${DEBUG_FILE}
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

#given the directory parameter returns self, if directory exists, 
#or the closest existing parent directory in tree hierarchy
getClosestExistingParentDir() {
	awkcmd='awk'
	
	#Solaris awk does not support system() function, so use nawk instead
	case `uname` in
		SunOS)
			awkcmd='nawk'
		;;
	esac
	
	echo $1 | $awkcmd -F"/" '{
		#build full path parameter
		for (i=2; i<=NF; i++) path = path"/"$i;
		
		subpath = path
		#for each parent directory
		for (i=NF; i>1; i--) {
			#if directory exists return it
			if (! system("test -d "subpath)) { print subpath; exit }
			#else move to the grandparent  
			else subpath = substr(subpath, 1, length(subpath)-length($i)-1)
		}
		print "/" #where no parent exists return root
	}'
}


#print the available disk space for a given directory
printDirSize() {
	INDEX=$1
	KEY=`eval echo \\\$key${INDEX}`
	DIRPATH=`eval echo \\\$path${INDEX}`
	echo "DEBUG: printDirSize: key=$KEY path=$DIRPATH" >>${DEBUG_FILE}
	nfs_check_status=`NFScheck $DIRPATH`
	if [ "$nfs_check_status" = "TRUE" ]; then
		dirToCheck=`getClosestExistingParentDir $DIRPATH`
		case `uname` in
	        Linux)			
				dsize=`df -h "$dirToCheck" | awk '{ if(NF<7 && NF>1){ print $(NF-2)"B" } }'`
				;;
			SunOS)
				dsize=`df -k "$dirToCheck" | awk '{ if(NF<7 && NF>1){ print $(4)/1024"MB" } }'`
				;;
	        AIX)
				dsize=`df -m "$dirToCheck" | awk '{ if(NF<8 && NF>1){ print $(NF-4)"MB" } }'`
	    		;;
	        HP-UX)
				dsize=`df -k "$dirToCheck" | grep -i "free" | awk '{ print $(1)/1024"MB" }'`
	        	;;
		esac
		echo "$KEY=`formatSizeDisplay $dsize`"
	else
		echo "$KEY=NFS_NOT_AVAILABLE"
	fi
}


DEBUG_FILE="TAD722_debug.log"

if [ -f ${DEBUG_FILE} ]; then
	rm -f ${DEBUG_FILE}
fi

TAD_CFG="TAD722_impl.cfg"

key1="Disk"
key2="TEMP"
key3="CIT"
key4="TCD"
key5="ETC"

keys="1 2 3 4 5"

CFG=$1

#set Disk path
path1=$2

SYSTEM=`uname`
SYSTEMID=`getSystemId`

#handle -p arguments
for ARG in `echo $3 | tr "," "\n"`; do
  	for i in $keys; do
		parseDirParameter $i
	done
done

#write disk value
copyValue $key1 DISK

#write other values to the main config
for i in $keys; do
	DIR=`eval echo \\\$key${i}`
	copyValue $DIR $DIR
done

#print key=value available space for every directory
for i in $keys; do
	echo `printDirSize $i`
done


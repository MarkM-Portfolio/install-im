#!/bin/bash
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************


versionCompare() {

#Handle special cases
if [ "$1" = "" ]; then
   if [ "$2" = "" ]; then
        echo 0
        exit
  fi
fi

if [ "$1" = "" ]; then
   if [ "$2" != "" ]; then
        echo 2
        exit
   fi
fi

if [ "$1" != "" ]; then
    if [ "$2" = "" ]; then
        echo 0
        exit
 fi
fi
#if both are equal
if [ "$1" = "$2" ]; then
        echo 0
        exit
fi

expectedResult=$1
actualResult=$2

ExpCount=1
ActCount=1

while [ true ]; do

        FirstVal=`echo $expectedResult | awk -v count=$ExpCount, -F'.' '{ print $(count) }'`
        SecondVal=`echo $actualResult | awk -v count=$ExpCount, -F'.' '{ print $(count) }'`
        ((ExpCount++))
        ((ActCount++))

        if [ "${FirstVal}" = "" -o "${SecondVal}" = "" ]; then
		if [ "${FirstVal}" = "" ]; then
		    if [ "$SecondVal" != "" ]; then
			echo 2 
			break ;
		    fi
		fi
		if [ "$FirstVal" != "" ]; then
		    if [ "$SecondVal" = "" ]; then
			echo 0
			break ;
		    fi
		fi
		echo 1
                break ;
        fi
        if [ "${FirstVal}" = "${SecondVal}" ]; then
                continue ;
        fi
        #which one is bigger ?
        echo ${FirstVal} >/$TMP_DIR/versioncompare.txt
        echo ${SecondVal}>>/$TMP_DIR/versioncompare.txt

        LowerValue=`cat /$TMP_DIR/versioncompare.txt | uniq | sort -n| head -1 2>/dev/null`
		rm -rf /$TMP_DIR/versioncompare.txt
        if [ "$LowerValue" = "$FirstVal" ]; then
                echo "2"
                break ;
        else
                echo "1"
				break ;
        fi
done
}

rpmCompare()
{

#for 1st rpm

res=`echo $1 | sed 's/-/ /g'`
j=`echo $res | wc -w`
if [ "$j" -eq 1 ]; then
	rpm1ver2=""
	rpm1ver1=""
fi

if [ "$j" -eq 2 ]; then
	rpm1ver1=`echo $res | cut -d ' ' -f$j`
	rpm1ver2=""
fi

if [ "$j" -gt 2 ]; then
	rpm1ver2=`echo $res | cut -d ' ' -f$j`
	k=`expr $j - 1`
	rpm1ver1=`echo $res | cut -d ' ' -f$k`
fi

#for 2nd rpm
res=`echo $2 | sed 's/-/ /g'`
j=`echo $res | wc -w`

if [ "$j" -eq 1 ]; then
	rpm2ver1=""
	rpm2ver2=""
fi
if [ "$j" -eq 2 ]; then
rpm2ver1=`echo $res | cut -d ' ' -f$j`
rpm2ver2=""
fi
if [ "$j" -gt 2 ]; then
rpm2ver2=`echo $res | cut -d ' ' -f$j`
k=`expr $j - 1`
rpm2ver1=`echo $res | cut -d ' ' -f$k`
fi

if [ $(versionCompare $rpm1ver1 $rpm2ver1) -eq 0 ]; then
     if [ $(versionCompare $rpm1ver2 $rpm2ver2) -eq 0 ]; then
           echo 0
           exit
     else
           echo $(versionCompare $rpm1ver2 $rpm2ver2)
          exit
     fi
else
   echo $(versionCompare $rpm1ver1 $rpm2ver1)
   exit
fi




}

type=`uname`
PROD_NAME=`echo $2 | cut -d'-' -f1`
#echo $PROD_NAME
if [ "$type" = "AIX" ]; then
   rpm -q $PROD_NAME > /$TMP_DIR/prs.package.txt 2>/dev/null
   Package=`cat /$TMP_DIR/prs.package.txt | sed -n '$p' | cut -d' ' -f1 | grep $PROD_NAME`
fi

if [ "$type" = "Linux" ]; then
   rpm -q $PROD_NAME > /$TMP_DIR/prs.package.txt 2>/dev/null
   Package=`cat /$TMP_DIR/prs.package.txt | sed -n '1{p;q}' | cut -d' ' -f1 | grep $PROD_NAME`
fi

if [ "$type" = "SunOS" ]; then
     pkginfo | grep $PROD_NAME > /$TMP_DIR/prs.package.txt 2>/dev/null
     Package=`cat /$TMP_DIR/prs.package.txt | sed -n '$p' | awk '{print$2}' | grep $PROD_NAME`
fi
if [ "$type" = "HP-UX" ]; then
	swlist  -l product $PROD_NAME > /$TMP_DIR/prs.package.txt 2>/dev/null
	   Package=`cat /$TMP_DIR/prs.package.txt | sed '/^$/d'  | sed -n '$p' | grep -v "^#" | tr -s "   " " " | tr -s "        " " " | sed 's/^[ \t]*//' |  cut -d' ' -f1`
fi

if [ "$1" != "$Msg_UNAVAILABLE" ]; then
RPM_VERSION=`echo $1 | sed "s/$3/9999/g"`
STR_BASE_ACCEPTABLE_VER=`echo $2 | sed "s/$3/9999/g"`

isDot1=`echo "$RPM_VERSION" | grep "9999\."`
if [ "$isDot1" != "" ]; then
	RPM_VERSION=`echo $RPM_VERSION | sed "s/9999\./9999-/"`
fi
isDot2=`echo "$STR_BASE_ACCEPTABLE_VER" | grep "9999\."`
if [ "$isDot2" != "" ]; then
        STR_BASE_ACCEPTABLE_VER=`echo $STR_BASE_ACCEPTABLE_VER | sed "s/9999\./9999-/"`
fi
isHyphen1=`echo "$RPM_VERSION" | grep "9999_"`
if [ "$isHyphen1" != "" ]; then
        RPM_VERSION=`echo $RPM_VERSION | sed "s/9999_/9999-/"`
fi
isHyphen2=`echo "$STR_BASE_ACCEPTABLE_VER" | grep "9999_"`
if [ "$isHyphen2" != "" ]; then
        STR_BASE_ACCEPTABLE_VER=`echo $STR_BASE_ACCEPTABLE_VER | sed "s/9999_/9999-/"`
fi

len=`echo $STR_BASE_ACCEPTABLE_VER | wc -m`
len=$(($len-1))
#echo $len
STR_VERSION_TYPE=`echo $STR_BASE_ACCEPTABLE_VER | cut -c $len`
#echo $STR_VERSION_TYPE
#echo $STR_BASE_ACCEPTABLE_VER
if [[ "$STR_VERSION_TYPE" == "+" || "$STR_VERSION_TYPE" == "-" ]]; then
  STR_ABSOLUTE=`echo $STR_BASE_ACCEPTABLE_VER | cut -c -$(($len-1))`
  #echo $STR_ABSOLUTE

	result=`rpmCompare $RPM_VERSION $STR_ABSOLUTE`
  if [ $result -eq 0 ]; then
	echo "$Msg_PASS"
  else 
	result=`rpmCompare $RPM_VERSION $STR_ABSOLUTE`
	if [ $result -eq 2 ]; then
		if [ "$STR_VERSION_TYPE" == "+" ]; then
			echo "$Msg_FAIL"
		else
			echo "$Msg_PASS"
		fi
	else if [ "$STR_VERSION_TYPE" = "-" ]; then
                 echo "$Msg_FAIL"
        else
                 echo "$Msg_PASS"
        fi
  fi
fi
else 
	result=`rpmCompare $RPM_VERSION $STR_BASE_ACCEPTABLE_VER`
	if [ $result -eq 0 -o $result -eq 1 ]; then
		echo "$Msg_PASS"
	else
		echo "$Msg_FAIL"
	fi
fi
else
 echo "$Msg_FAIL"
fi
rm -f /$TMP_DIR/prs.package.txt

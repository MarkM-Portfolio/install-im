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
        echo -1
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

        FirstVal=$expectedResult
        SecondVal=$actualResult 

        if [ "${FirstVal}" = "" -o "${SecondVal}" = "" ]; then
                if [ "${FirstVal}" = "" ]; then
                    if [ "$SecondVal" != "" ]; then
                        echo -1
                    fi
                fi
                if [ "$FirstVal" != "" ]; then
                    if [ "$SecondVal" = "" ]; then
                        echo 0
                    fi
                fi
                echo 1
        fi
        #which one is bigger ?
        echo ${FirstVal} >/$TMP_DIR/versioncompare.txt
        echo ${SecondVal}>>/$TMP_DIR/versioncompare.txt

        LowerValue=`cat /$TMP_DIR/versioncompare.txt | uniq | sort -n| head -1 2>/dev/null`
                rm -rf /$TMP_DIR/versioncompare.txt
        if [ "$LowerValue" = "$FirstVal" ]; then
                echo "-1"
        else
                echo "1"
        fi
}

version=`echo $1 | sed 's/-/./g'`
eversion=`echo $2 | sed 's/-/./g'`

option=`echo "$2" | sed -e "s/^.*\(.\)$/\1/"`
res=0
if [ "+" = "$option" ]; then
	eversion=`echo $2 | sed 's/+//g'`
	tmpArg=`versionCompare $eversion $version`
	#echo "tmpArg=$tmpArg"
	if [ $tmpArg -eq -1 -o $tmpArg -eq 0 ]; then
		res=1
	fi


	if [ $res -eq  1 ]; then
		echo "$Msg_PASS"
		exit
	fi
else
	if [ "-" = "$option" ]; then
		eversion=`echo $2 | sed 's/-//g'`
		tmpArg=`versionCompare $eversion $version`
		if [ $tmpArg -eq 1 -o $tmpArg -eq 0 ]; then
			res=1
		fi

		if [ $res -eq 1 ]; then
			echo "$Msg_PASS"
			exit
		fi
	else
		if [ "*" = "$option" ]; then
			eversion=`echo $2 | sed 's/*//g'`
			tmpArg=`versionCompare $eversion $version`
			if [ $tmpArg -eq 0 ]; then
				res=1
			fi
			if [ $res -eq 1 ]; then
				echo "$Msg_PASS"
				exit
			fi
		else
			tmpArg=`versionCompare $eversion $version`
			if [ $tmpArg -eq 0 ]; then
				res=1
			fi
			if [ $res -eq 1 ]; then
				echo "$Msg_PASS"
				exit
			fi
		fi
	fi
fi
echo "$Msg_FAIL"

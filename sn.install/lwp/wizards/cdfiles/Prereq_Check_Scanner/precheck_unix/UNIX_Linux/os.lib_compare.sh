# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
. ../lib/common_function.sh
if [ "$1" = "Unavailable" ]; then
	echo "$Msg_FAIL"
else
     regexFound=`echo $2 | grep "regex{"`
     if [ "$regexFound" != "" ]; then
          compareValue=`echo $2 | sed 's/regex{/+/g' | cut -d'+' -f2 | cut -d'}' -f1`
          compare=`echo $1 | grep $compareValue`
          if [ "$compare" != "" ]; then
            echo "$Msg_PASS"
            exit
          fi
      fi   
	#####Finding for "/" for Supporting Old os.lib LOGIC####
	IsOldTypeFound=`echo "$1" | grep "/" | cut -d ' ' -f1`
	if [ $IsOldTypeFound ]; then
		echo "$Msg_PASS"
	else
		#####Finding the "(+) sign for Supporting Greater Than or Equal to Versions#####
		IsPlusFound=`echo "$2" | sed -e "s/^.*\(.\)$/\1/"`
		if [ "$IsPlusFound" = "+" ]; then
			PackageName=`cat /$TMP_DIR/prs.lib.txt | cut -d "=" -f2` 
			ExpectedVersion=`echo "$2" | sed 's/.$//g' | sed 's/'$PackageName'/'$PackageName' /g' | cut -d " " -f2`  	
			VersionFound=`echo "$1" | sed 's/'$PackageName'/'$PackageName' /g' | cut -d " " -f2`
			
			####Checking for Dot####
			#FirstChar=`echo $	
			ExpectedVersion=`echo $ExpectedVersion | cut -c 2-`
			VersionFound=`echo $VersionFound | cut -c 2-`	
			Result=`versionCompare  $ExpectedVersion $VersionFound`
			#echo $Result
			if [ $Result -eq 0 -o $Result -eq -1 ]; then
				echo "$Msg_PASS"
			else
				echo "$Msg_FAIL"
			fi
		else
			echo "$Msg_PASS"
		fi

	fi
fi

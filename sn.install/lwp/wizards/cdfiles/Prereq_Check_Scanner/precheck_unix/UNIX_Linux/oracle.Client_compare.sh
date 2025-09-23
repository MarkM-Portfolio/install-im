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
echo -1
exit
   fi
fi

if [ "$1" != "" ]; then
    if [ "$2" = "" ]; then
echo 1
exit   
 fi
fi
#echo $1
#echo $2

check1=`echo $1 | sed 's/*/#/g'`
check2=`echo $2 | sed 's/*/#/g'`


ver1Parts=$(echo $check1 | awk -F"." '{print $1,$2,$3,$4,$5,$6}')
ver2Parts=$(echo $check2 | awk -F"." '{print $1,$2,$3,$4,$5,$6}')
set -- $ver1Parts
set -- $ver2Parts
lenver1Parts=`echo $ver1Parts | wc -w`
lenver2Parts=`echo $ver2Parts | wc -w`


#echo $ver1Parts
#echo $ver2Parts

#echo "lenver1Parts=$lenver1Parts"
#echo "lenver2Parts=$lenver2Parts"

# if last version part is "*" treat all missing part as *

if [ $lenver1Parts -ge $lenver2Parts ]; then
     len=`echo $ver2Parts | wc -m`
 #    echo "Len=$len"
      last=`echo $ver2Parts | cut -c $(($len-1))`
  #    echo "check1:-$last"
      if [ $last = "#" ]; then
         for (( i = $lenver2Parts; i <= $lenver1Parts-1; i++))
         do
         ver2Parts=$ver2Parts" #"
         done
      fi
elif [ $lenver2Parts -ge $lenver1Parts ]; then
      len1=`echo $ver1Parts | wc -m`
   #   echo "Len1=$len1"
       last=`echo $ver1Parts | cut -c $(($len1-1))`
    #   echo "check2:-$last"
      if [ $last = "#" ]; then
      for (( i=$lenver1Parts; i <=  $lenver2Parts-1; i++))
         do
         ver1Parts=$ver1Parts" #"
         done

      fi

fi



#echo $ver2Parts
#echo $ver1Parts
j=1
while [ $j -le $lenver2Parts -o $j -le $lenver1Parts ]
do 
  v1Str="UNASSIGNED"
  v2Str="UNASSIGNED"

   if [ $j -le $lenver1Parts ]; then
   v1=`echo $ver1Parts | cut -d ' ' -f$j`
   
  	 res="$(echo $v1 | tr -d '[0-9]' | tr -d '.' )"
#	res=`echo $l | grep "]"`
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
       res="$(echo $v2 | tr -d '[0-9]' | tr -d '.')"
      #  res=`echo $l | grep "]"`
        if [ $res ]; then
 #       echo "char"
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
	      if [[ "$v1Str" > "$v2Str" ]]; then
  #             echo "Inside String check3"       
                  versionCompare=1
              else
                  versionCompare=-1
              fi
              echo $versionCompare
              exit
          fi
     fi

   else
	#Comparing as number
#  echo "compared number"
  if [ "$v1" -gt "$v2" ]; then
        versionCompare=1
        echo $versionCompare
        exit
  fi
 # echo "compared number2"
  if [ "$v2" -gt "$v1" ]; then
        versionCompare=-1
        echo $versionCompare
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
}


if [ "$1" = "Unavailable" ]; then 
 echo "$Msg_FAIL"
 exit
fi

STR_BASE_ACCEPTABLE_VER=$2
res=-1
version=$1
len=`echo $STR_BASE_ACCEPTABLE_VER | wc -m`
len=$(($len-1))
#echo $len
STR_VERSION_TYPE=`echo $STR_BASE_ACCEPTABLE_VER | cut -c $len`
#echo $STR_VERSION_TYPE
#echo $STR_BASE_ACCEPTABLE_VER
if [[ "$STR_VERSION_TYPE" == "+" || "$STR_VERSION_TYPE" == "-" ]]; then
  STR_ABSOLUTE=`echo $STR_BASE_ACCEPTABLE_VER | cut -c -$(($len-1))`
#echo $STR_ABSOLUTE
  if [ "$STR_VERSION_TYPE" == "+" ]; then
 res=$(versionCompare $version  $STR_ABSOLUTE)  
fi
  if [ "$STR_VERSION_TYPE" == "-" ]; then
 res=$(versionCompare $STR_ABSOLUTE  $version)  
 fi
else
   if [ $(versionCompare $STR_ABSOLUTE  $version) -eq 0 ]; then
      res=1
      fi
fi
if [ "$res" -eq 1 ]; then
 echo "$Msg_PASS"
else
echo "$Msg_FAIL"
fi

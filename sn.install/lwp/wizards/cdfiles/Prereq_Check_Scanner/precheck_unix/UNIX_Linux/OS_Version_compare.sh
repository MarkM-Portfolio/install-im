#!/bin/sh 
# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
#set -x
# first parameter is real os version, like: "Red Hat Enterprise Linux Server release 5 (Tikanga)" 
# second parameter is expect one, like: "AIX V5.2 (32 bit), AIX V5.2 (64 bit), AIX V5.3 (32 bit)"
#set -x


. ../lib/common_function.sh

# filter the type
type=`uname`
res=-1
if [ "$type" = "SunOS" ];then
        type="Solaris"
fi

# special treat Linux and Unix
if [ "$2" = "Linux" ]; then
	if [ "$type" = "Linux" ]; then
		echo "$Msg_PASS"
		exit
	fi
fi

if [ "$2" = "Unix" ]; then
	if [ "$type" = "Solaris" -o "$type" = "HP-UX" -o "$type" = "AIX" ]; then
		echo "$Msg_PASS"
		exit
	fi
fi

if [ "`uname`" = "SunOS" ]; then
        IsOracleSun=`cat /etc/*release 2>/dev/null | grep "Solaris" | grep "Oracle"`
        if [ "$IsOracleSun" != "" ]; then
                OSbit=`isainfo -kv | awk '{print "("$1")"}' | sed 's/-/ /g'`
                tmpVersion=`cat /etc/release | sed -n '1p' | sed 's/^[ ]*//g' | awk '{print $2,"V"$3,"("$6")";}'`
                OSVersion=`echo $tmpVersion $OSbit`
        fi
fi


# get the version, such as V5.3.2
if [ "`uname`" = "AIX" ]; then
        version=`echo $1 | sed 's/[R|r]elease /V/g' | sed 's/.* V\([a-zA-Z0-9.]\{1,\}\).*/\1/g' | awk '{print substr($0,0,3)}'`
else
        version=`echo $1 | sed 's/[R|r]elease /V/g' | sed 's/.* V\([a-zA-Z0-9.]\{1,\}\).*/\1/g'`
fi

option=`echo $2 | sed -e "s/^.*\(.\)$/\1/"`


if [ "$version" = "$1" ]; then
	# get the version, such as "SUSE Linux Enterprise Server 10"
	version=`echo $1 | sed 's/.* \([a-zA-Z0-9.]\{1,\}\).*/\1/g'`
	option=`echo $1 | sed 's/.* [a-zA-Z0-9.]\{1,\}\(.\).*/\1/g'`
fi



# get the first word as release name
release=`echo $1 | awk '{print $1}'`
# don't know if to filter with architecture

filtered=`echo $2 | sed 's/ /@/g'`
# filter the expect ones
regexFound=`echo "$2" | grep "regex{"`
  if [ "$regexFound" = "" ]; then

filtered=`echo $2 | sed 's/[ ]*//g' | tr "," "\n" | grep -i "$type" | grep -i "$release" | tr "\n" ","`
echo "check=$filtered"
fi
for tt in `echo "$filtered" | tr "," "\n"`
do
echo $tt     
regexFound=`echo "$tt" | grep "regex{"`
  if [ "$regexFound" != "" ]; then
    valueToCheck=`echo "$tt" | sed 's/regex{//g' | sed 's/}//g' | sed 's/@/ /g'`
     compare=""
    type=`uname`
    if [ "$type" = "SunOS" ];then
      compare=`echo $1 | grep "$valueToCheck"`
    else
     compare=`echo $1 | grep -x -e "$valueToCheck"`
    fi
    
     if [ "$compare" != "" ]; then
     echo "$Msg_PASS"
     exit
     fi

  fi
  
 eversion=`echo "$tt" | sed 's/Linux/LinuxV/g' | sed 's/Server/ServerV/g' | sed 's/release/realeaseV/g' | sed 's/.*[vV]\([0-9.*]\{1,\}\).*/\1/g'`
        if [ "$type" = "HP-UX" ]; then
            eversion=`echo $tt| cut -c 6-10`
      	 fi
      # option=`echo "$tt" | sed 's/Linux/LinuxV/g' | sed 's/Server/ServerV/g' | sed 's/.*[vV][0-9.]\{1,\}\(.\).*/\1/g'`
     option=`echo "$tt" | sed -e "s/^.*\(.\)$/\1/"`
#echo "@@@@@@ "$option"@@@@@@ "$eversion"@@@@@@ "$version
#option="{"
RangeCheck=`echo $2 | grep ".." | cut -d " " -f1`
n=1
if [ $RangeCheck ]; then
        Range1=`echo "$eversion" | sed 's/\.\./+/g' | cut -d "+" -f1`
        Range2=`echo "$eversion" | sed 's/\.\./+/g' | cut -d "+" -f2`
        Range1check=`versionCompare $version $Range1`
        Range2check=`versionCompare $version $Range2`
     
         if [ $Range1check -eq 0 -o $Range2check -eq 0 ]; then
                echo "$Msg_PASS"
                exit
         fi


        if [ $Range1check -eq 1 -a $Range2check -eq -1 ]; then
        	echo "$Msg_PASS"
		exit
        fi
else



echo "option=$option"



	if [ "+" = "$option" ]; then
echo "@@@@@jif@@@@@@@"
	             tmpArg=`versionCompare $eversion $version`
                        echo "tmpArg=$tmpArg"
                                  if [ $tmpArg -eq -1 -o $tmpArg -eq 0 ]; then
                                                res=1
                                        fi
	

		if [ $res -eq 1 ]; then
			echo "$Msg_PASS"
			exit
		fi
	else
#echo "@@@@@@else@@@@@@"
		if [ "-" = "$option" ]; then
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
fi
done
echo "$Msg_FAIL"
#tmp=`versionCompare 5.3 5.4`
#echo $tmp

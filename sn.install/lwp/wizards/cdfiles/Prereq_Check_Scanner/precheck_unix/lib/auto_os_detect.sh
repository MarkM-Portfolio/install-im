#!/bin/sh

# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
#set -n

# step 1: judge the release version
# here we handle Linux, Unix, Solaris, AIX

case `uname` in
	Linux) 
		release=`ls /etc/*release 2>/dev/null`
		if [ ! "$release" = "" ]; then
			for rr in $release
			do   
                                Architecture=`arch`
                                #if [ "$Architecture" = "x86_64" ]; then
                                #  check_arch="64 bit"
                               	#else
                                #   check_arch="32 bit" 
				#fi
                                # if [ "$Architecture" = "ppc64" ]; then
                                #   check_arch="ppc64"
                                # fi 
                                
                                #if [ "$Architecture" = "s390x" ]; then
                                #   check_arch="s390x"
                                # fi
                                #if [ "$Architecture" = "s390" ]; then
                                #   check_arch="s390"
                                # fi


				check_arch=`getconf LONG_BIT`"-bit"
				OS=`cat $rr | sed -n '1p' | sed 's/^[ ]*//g'| cut -d "(" -f1`
                                echo "OS Version="$OS"("$check_arch")"  
				echo "Kernel"=$Architecture
				exit
			done
		fi

		issue=`ls /etc/*issue 2>/dev/null`
		if [ ! "$issue" = "" ]; then
			for rr in $issue
			do
				echo "OS Version="`cat $rr | sed -n '1p' | sed 's/^[ ]*//g'`
				exit
			done
		fi
	;;
	HP-UX) 
# Added for Bug 141762
#		echo "OS Version="`cat /etc/issue 2>/dev/null | sed 's/.*\[\(.*\)\].*/\1/g'`" ("`getconf KERNEL_BITS`" bit)"	
 #       echo "OS Version="`uname` `uname -r`" ("`getconf KERNEL_BITS`"-bit)"
    check=`uname -r`
            if [ "$check" = "B.11.0" ]; then
                ver="11.0"
            fi
             if [ "$check" = "B.11.11" ]; then
                ver="11iv1"
            fi

             if [ "$check" = "B.11.23" ]; then
                ver="11iv2"
            fi

            if [ "$check" = "B.11.31" ]; then
                ver="11iv3"
            fi
             echo "OS Version="`uname`" "$ver


	;;
	AIX)
		echo "OS Version="`uname`" "V`oslevel`" ("`bootinfo -K 2>/dev/null`"-bit)"
		echo "Kernel"=`lsattr -E -l proc0 | grep "Processor type" | awk '{print $2}' | cut -d "_" -f2`
	;;
        SunOS)
                release=`ls /etc/*release 2>/dev/null`
                if [ ! "$release" = "" ]; then
                        for rr in $release
                        do
				echo "OS Version="`cat $rr | sed -n '1p' | sed 's/^[ ]*//g' | awk '{print $1,"V"$2,"("$5")";}'`
                                exit
                        done
                fi

        ;;

        *) echo "can not handle"
esac

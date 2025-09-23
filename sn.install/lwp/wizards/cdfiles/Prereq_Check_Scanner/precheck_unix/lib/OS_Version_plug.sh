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
# step 1: judge the release version
# here we handle Linux, Unix, Solaris, AIX

case `uname` in
	Linux) 
		release=`ls /etc/*release 2>/dev/null`
		if [ ! "$release" = "" ]; then
			for rr in $release
			do
				 updateFound=""
                                updateFound=`cat $rr | grep -i Update | sed 's/[Uu]pdate/[Uu]pdate+/g' | cut -d "+" -f2 | sed 's/ //g' | cut -c 1`
                                if [ "$updateFound" != "" ]; then
                                result=`cat $rr | sed -n '1p' | sed 's/^[ ]*//g' | cut -d '(' -f1  | sed 's/.$//g'`
                                
                                echo "OS Version="$result".$updateFound"
                                exit
                                else
                                echo "OS Version="`cat $rr | sed -n '1p' | sed 's/^[ ]*//g'`
                                exit
                                fi

                                echo "Architecture="`arch`
				#mem=`df -h | sed -n '2p'`
				#if [ ! "$mem" = "" ]; then
				#	totSize=`echo $mem | awk '{print $2}'`
				#	echo "Total Memory="$totSize
				#fi
				exit
			done
		fi

		issue=`ls /etc/*issue 2>/dev/null`
		if [ ! "$issue" = "" ]; then
			for rr in $issue
			do      
                                updateFound=""
                                updateFound=`cat $rr | grep -i Update | sed 's/[Uu]pdate/[Uu]pdate+/g' | cut -d "+" -f2 | sed 's/ //g' | cut -c 1`
                                if [ "$updateFound" != "" ]; then
                                result=`cat $rr | sed -n '1p' | sed 's/^[ ]*//g' | cut -d '(' -f1 `".$updateFound"
                                echo "OS Version="$result 
                                exit
                                else
				echo "OS Version="`cat $rr | sed -n '1p' | sed 's/^[ ]*//g'`
				exit
                                fi
			done
		fi
	;;
	HP-UX) 
# Added for Bug 141762
#		echo "OS Version="`cat /etc/issue 2>/dev/null | sed 's/.*\[\(.*\)\].*/\1/g'`" ("`getconf KERNEL_BITS`" bit)"	
 #       echo "OS Version="`uname` `uname -r`" ("`getconf KERNEL_BITS`" bit)"
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
		echo "OS Version="`uname`" "V`oslevel`" ("`bootinfo -K`" bit)"
	;;
	SunOS)
		release=`ls /etc/*release 2>/dev/null`
                IsOracleSun=`cat $release 2>/dev/null | grep "Solaris" | grep "Oracle"`
                if [ "$IsOracleSun" != "" ]; then
                        echo "OS Version="`cat $release | sed -n '1p' | sed 's/^[ ]*//g' | awk '{print $2,"V"$3,"("$6")";}'`" ("`isainfo -b`" bit)"
                        exit
                fi
                if [ ! "$release" = "" ]; then
                        for rr in $release
                        do
                                echo "OS Version="`cat $rr | sed -n '1p' | sed 's/^[ ]*//g' | awk '{print $1,"V"$2,"("$5")";}'`" ("`isainfo -b`" bit)"
                                exit
                        done
                fi		
	;;
	*) echo "can not handle"
esac


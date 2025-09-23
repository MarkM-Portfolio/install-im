# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************
. ../lib/common_function.sh
version=`echo $1 | sed 's/-/./g'`
#eversion=`echo $2 | sed 's/-/./g'`

option=`echo "$2" | sed -e "s/^.*\(.\)$/\1/"`

if [ "+" = "$option" ]; then
#echo "@@@@@jif@@@@@@@"
  eversion=`echo $2 | sed -e 's/[+ ]*$//g' | sed 's/-/./g'`
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
		eversion=`echo $2 | sed -e 's/[- ]*$//g' | sed 's/-/./g'`
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
		      eversion=`echo $2 | sed -e 's/[* ]*$//g' | sed 's/-/./g'`
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
	


echo "$Msg_FAIL"
#tmp=`versionCompare 5.3 5.4`
#echo $tmp

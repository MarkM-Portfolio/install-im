# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

. $PREREQ_HOME/lib/common_function.sh
`wrlLogFuncStart "packageTest.sh"`

isQualifierExists(){
        `wrlLogFuncStart "isQualifierExists()"`
        `wrlDebugFuncParam "line" "$1"`
        line=$1
        isQualifierExists=`echo $line| grep "]"`
        if [ $isQualifierExists ]; then
                `wrlDebugFuncReturn "1"`
                rc="1"
        else
                `wrlDebugFuncReturn "0"`
                rc="0"
        fi
        `wrlLogFuncExit "isQualifierExists()"`
        echo "$rc" 
       
        
}

getPreReqScanParamValue(){
         `wrlLogFuncStart "getPreReqScanParamValue()"`
        `wrlDebugFuncParam "cfgLine"  "$1"`
        cfgLine=$1
        isQualFound=`isQualifierExists "$cfgLine"`
         if [ "$isQualFound" -eq "1" ]; then
                scanParamValue=`echo "$cfgLine" | cut -d "]" -f2`
	else
                scanParamValue=`echo "$cfgLine" | cut -d "=" -f2`
        fi
         `wrlDebugFuncReturn "$scanParamValue"`
        `wrlLogFuncExit "getPreReqScanParamValue()"`
        echo "$scanParamValue"
}
generateScanCmd(){
        `wrlLogFuncStart "generateScanCmd()"`
          `wrlDebugFuncParam "scriptCmd" "$1"`
         `wrlDebugFuncParam "scriptParams" "$2"`
         `wrlDebugFuncParam "checkName" "$3"`
         `wrlDebugFuncParam "checkSpec" "$4"`
         `wrlDebugFuncParam "generic" "$5"`

        scriptCmd=$1
        scriptParams=$2
        checkName=$3
        checkSpec=$4
        generic=$5
        
        if [ $generic="True" ]; then
                key=`echo $checkSpec |cut -d"=" -f1 |cut -d"." -f3`
                checkName="$checkName.$key"
        fi
        echo "\`wrlDebugFunc \"Processing $checkName\"\`" >> /$TMP_DIR/prs.check
         
      # IsQualFound=`echo $checkSpec | grep "]"`
        #isQualFound=`isQualifierExists "$checkSpec"`
      
        ExpValue=`getPreReqScanParamValue "$checkSpec"`
        echo "\`wrlDebugFunc \"Calling $scriptCmd\"\`" >> /$TMP_DIR/prs.check
        echo "ss=\`$PREREQ_HOME/UNIX_Linux/$scriptCmd $scriptParams\`" >> /$TMP_DIR/prs.check
        echo "\`wrlDebugFunc \"$checkName value obtained - \$ss; Expected - $ExpValue\"\`" >> /$TMP_DIR/prs.check
        #echo "ss=\`./os.level\`" >>/$TMP_DIR/prs.check
	
	#echo "\`wrlTrace "Finished" "os.level"\`"  >>/$TMP_DIR/prs.check
	
	echo "echo \"$checkName=\$ss\"" >> /$TMP_DIR/prs.check
        `wrlLogFuncExit "generateScanCmd()"`
}
rm -f /$TMP_DIR/prs.check
# fix for Bug 142865
if [ "$2" = "LCM_02300000.cfg" -o "$2" = "TAD_07200000.cfg" -o "$2" = "TAD_07220000.cfg" -o "$2" = "LCM_01000000.cfg" ]; then
echo "Using existing sh " >>/$TMP_DIR/prs1.check
else    
echo "#!/bin/sh" >>/$TMP_DIR/prs.check
type=`uname`
if [ "$type" != "SunOS" ]; then
echo cd $1>>/$TMP_DIR/prs.check
fi
echo "set -x" >>/$TMP_DIR/prs.check
#echo "ptrace=\"../prs.trc\"" >>/$TMP_DIR/prs.check
#echo "pdbg=\"../prs.debug\"" >>/$TMP_DIR/prs.check
if [ "$3" = "debugFlag" ]; then
echo "debugFlag=\"True\"" >>/$TMP_DIR/prs.check
fi
if [ "$4" = "traceFlag" ]; then
echo "traceFlag=\"True\"" >>/$TMP_DIR/prs.check
fi

#echo ". ../lib/common_function.sh" >>/$TMP_DIR/prs.check
echo ". $PREREQ_HOME/lib/common_function.sh" >>/$TMP_DIR/prs.check
#echo "wrlTrace(){" >>/$TMP_DIR/prs.check
#echo " if [ \"$traceFlag\" = \"True\" ]; then" >>/$TMP_DIR/prs.check
#echo " printf "[%-20s] %-8s: %-s\n" "`date '+%Y.%m.%d %H.%M.%S'`" "$1" \"$2\""
#echo ". ./lib/common_function.sh" >>/$TMP_DIR/prs.check

for line in `cat $1/$2 | sed '/^$/d'`
do
  file1=`echo $line | grep package | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/os.package.perl_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/os.package.perl_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi
echo $line

file1=`echo $line | grep availablePorts | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/network.availablePorts.db2_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/network.availablePorts.db2_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi


file1=`echo $line | grep portsInUse | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/network.portsInUse_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/network.portsInUse_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi

file1=`echo $line | grep os.lib | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/os.lib_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/os.lib_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi

file1=`echo $line | grep os.LibPatch | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/os.LibPatch_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/os.LibPatch_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi

file1=`echo $line | grep os.space | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/os.space_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/os.space_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi

file1=`echo $line | grep os.dir | cut -d'=' -f1`
  if [ "$type" = "SunOS" ]; then
  cp $1/os.dir_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/os.dir_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi

file=`echo $line | grep os.ulimit | cut -d'=' -f1`
key=`echo $line | grep os.ulimit | cut -d "[" -f2 | cut -d "]" -f1 | cut -d ":" -f2`
file1=$file.$key
  if [ "$type" = "SunOS" ]; then
  cp $1/os.ulimit_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  else
  cp $1/os.ulimit_compare.sh $1/"$file1"_compare.sh 2>/dev/null
  fi


res=`echo $line | grep network.availablePorts`
if [ $res ]; then
   ports=`echo $line | cut -d'.' -f3 |cut -d'=' -f1`
##TRACE AND DEBUG##
	echo "\`wrlTrace "Starting" "network.availablePorts.$ports"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "network.availablePorts $ports"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "network.availablePorts.$ports"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ports" \`" >>/$TMP_DIR/prs.check

 	echo "ss=\`./network.port $ports\`" >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Finished" "network.availablePorts $ports"\`"  >>/$TMP_DIR/prs.check
 	
	echo "echo \"network.availablePorts.$ports=\$ss\"" >>/$TMP_DIR/prs.check
	
	echo "\`wrlDebug "Finished" "network.availablePorts.$ports"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValue" \$ss\`"  >>/$TMP_DIR/prs.check
  	echo "\`wrlTrace "Done" "network.availablePorts.$ports"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep network.portsInUse`
if [ $res ]; then
   	ports=`echo $line | cut -d'.' -f3 |cut -d'=' -f1`
##TRACE AND DEBUG##
	echo "\`wrlTrace "Starting" "network.portsInUse.$ports"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "network.portsInUse $ports"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "network.portsInUse.$ports"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ports" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./network.port $ports\`" >>/$TMP_DIR/prs.check
	
	echo "\`wrlTrace "Finished" "network.portsInUse $ports"\`"  >>/$TMP_DIR/prs.check 
	
	echo "echo \"network.portsInUse.$ports=\$ss\"" >>/$TMP_DIR/prs.check

	echo "\`wrlDebug "Finished" "network.portsInUse.$ports"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Done" "network.portsInUse.$ports"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep os.ulimit`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
	
	echo "\`wrlTrace "Starting" "os.ulimit"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.ulimit"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.ulimit"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        ####CHECKING FOR QUALIFIER####
        IsQualFound=`echo "$res" | grep "]" | cut -d "=" -f1`
        prod=`echo "$res" | cut -d "=" -f2-`
        if [ $IsQualFound ]; then
                echo "ss=\`./os.ulimit "$prod"\`" >>/$TMP_DIR/prs.check
        else
                echo "ss=\`./os.ulimit\`" >>/$TMP_DIR/prs.check
        fi
	
	echo "\`wrlTrace "Finished" "os.ulimit"\`"  >>/$TMP_DIR/prs.check
        key=`echo "$res" | cut -d "[" -f2 | cut -d "]" -f1 | cut -d ":" -f2`
        if [ $IsQualFound ]; then
                echo "echo \"os.ulimit.$key=\$ss\"" >>/$TMP_DIR/prs.check
        else
                echo "echo \"os.ulimit=\$ss\"" >>/$TMP_DIR/prs.check
        fi

	echo "\`wrlDebug "Finished" "os.ulimit"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.ulimit"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.level`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
	
	echo "\`wrlTrace "Starting" "os.level"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.level"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.level"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check


	echo "ss=\`./os.level\`" >>/$TMP_DIR/prs.check
	
	echo "\`wrlTrace "Finished" "os.level"\`"  >>/$TMP_DIR/prs.check
	
	echo "echo \"os.level=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.level"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Done" "os.level"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.sshdConfig`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.sshdConfig"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.sshdConfig"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.sshdConfig"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.sshdConfig\`" >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Finished" "os.sshdConfig"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.sshdConfig=\$ss\"" >>/$TMP_DIR/prs.check
	
	echo "\`wrlDebug "Finished" "os.sshdConfig"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Done" "os.sshdConfig"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep os.userLimits`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.userLimits"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.userLimits"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.userLimits"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "ExpValue" \`" >>/$TMP_DIR/prs.check


	echo "ss=\`./os.userLimits\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.userLimits"\`"  >>/$TMP_DIR/prs.check
	echo "echo \"os.userLimits=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.userLimits"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Done" "os.userLimits"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep env.classpath.derbyJAR`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "env.classpath.derbyJar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "env.classpath.derbyJar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "env.classpath.derbyJAR"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./env.classpath.derbyJAR\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "env.classpath.derbyJar"\`"  >>/$TMP_DIR/prs.check
	echo "echo \"env.classpath.derbyJAR=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "env.classpath.derbyJAR"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "env.classpath.derbyJar"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.ftpusers`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.ftpusers"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.ftpusers"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.ftpusers"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.ftpusers\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.ftpusers"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.ftpusers=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.ftpusers"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.ftpusers"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.loginVariable`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.loginVariable"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.loginVariable"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.loginVariable"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.loginVariable\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.loginVariable"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.loginVariable=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.loginVariable"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.loginVariable"\`"  >>/$TMP_DIR/prs.check


fi


res=`echo $line | grep os.kernelMode`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.kernelMode"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.kernelMode"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.kernelMode"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.kernelMode\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.kernelMode"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.kernelMode=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.kernelMode"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.kernelMode"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.maximumProcesses`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.maximumProcesses"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.maximumProcesses"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.maximumProcesses"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.maximumProcesses\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.maximumProcesses"\`"  >>/$TMP_DIR/prs.check
	echo "echo \"os.maximumProcesses=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.maximumProcesses"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.maximumProcesses"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.MozillaVersion`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.MozillaVersion"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.MozillaVersion"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.MozillaVersion"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.MozillaVersion\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.MozillaVersion"\`"  >>/$TMP_DIR/prs.check
	echo "echo \"os.MozillaVersion=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.MozillaVersion"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.MozillaVersion"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep DB2`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "DB2_Version"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "DB2_Version.sh"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "DB2_Version"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./DB2_Version.sh \`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "DB2_Version.sh"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"DB2 Version=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "DB2_Version"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "DB2_Version"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.largeFile`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.largeFile"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.largeFile"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.largeFile"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.largeFile\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.largeFile"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.largeFile=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.largeFile"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.largeFile"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.windowManager`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.windowManager"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.windowManager"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.windowManager"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.windowManager\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.windowManager"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.windowManager=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.windowManager"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.windowManager"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.RAMSize`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.RAMSize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.RAMSize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.RAMSize"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.RAMSize\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.RAMSize"\`"  >>/$TMP_DIR/prs.check


	echo "echo \"os.RAMSize=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.RAMSize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.RAMSize"\`"  >>/$TMP_DIR/prs.check


fi

res=`echo $line | grep os.automaticCleanup`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.automaticCleanup"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.automaticCleanup"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.automaticCleanup"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.automaticCleanup\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.automaticCleanup"\`"  >>/$TMP_DIR/prs.check
	echo "echo \"os.automaticCleanup=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.automaticCleanup"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.automaticCleanup"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep os.swapSize`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
 prod=`echo $line |cut -d"=" -f2`
	echo "\`wrlTrace "Starting" "os.swapSize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.swapSize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.swapSize"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check


	echo "ss=\`./os.swapSize "$prod"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.swapSize"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.swapSize=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.swapSize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.swapSize"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.CompizVar`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.CompizVar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.CompizVar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.CompizVar"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.CompizVar\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.CompizVar"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.CompizVar=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.CompizVar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.CompizVar"\`"  >>/$TMP_DIR/prs.check

fi



res=`echo $line | grep os.umask`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.umask"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.umask"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.umask"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.umask\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.umask"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.umask=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.umask"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.umask"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.locale`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.locale"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.locale"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.locale"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.locale\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.locale"\`"  >>/$TMP_DIR/prs.check


	echo "echo \"os.locale=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.locale"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.locale"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep db2.home.space`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "db2.home.space"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "db2.home.space"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "db2.home.space"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./db2.home.space\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "db2.home.space"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"db2.home.space=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "db2.home.space"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "db2.home.space"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.expectLink`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.expectLink"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.expectLink"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.expectLink"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.expectLink\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.expectLink"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.expectLink=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.expectLink"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.expectLink"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.maximoDirectory`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.maximoDirectory"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.maximoDirectory"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.maximoDirectory"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.maximoDirectory\`" >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Finished" "os.maximoDirectory"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.maximoDirectory=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.maximoDirectory"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.maximoDirectory"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.maximoDirOwner`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.maximoDirOwner"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.maximoDirOwner"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.maximoDirOwner"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.maximoDirOwner\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.maximoDirOwner"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.maximoDirOwner=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.maximoDirOwner"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.maximoDirOwner"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.kernelversion`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.kernelversion"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.kernelversion"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.kernelversion"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.kernelversion\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.kernelversion"\`"  >>/$TMP_DIR/prs.check


	echo "echo \"os.kernelversion=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.kernelversion"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.kernelversion"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.kernelParameters`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.kernelParameters"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.kernelParameters"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.kernelParameters"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.kernelParameters\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.kernelParameters"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.kernelParameters=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.kernelParameters"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.kernelParameters"\`"  >>/$TMP_DIR/prs.check


fi



res=`echo $line | grep oracle.Server`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "oracle.Server"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "oracle.Server"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "oracle.Server"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./oracle.Server\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "oracle.Server"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"oracle.Server=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "oracle.Server"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "oracle.Server"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep oracle.Server.Location`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "oracle.Server.Location"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "oracle.Server.Location"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "oracle.Server.Location"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./oracle.Server.Location\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "oracle.Server.Location"\`"  >>/$TMP_DIR/prs.check


	echo "echo \"oracle.Server.Location=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "oracle.Server.Location"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "oracle.Server.Location"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep oracle.Client`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "oracle.Client"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "oracle.Client"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "oracle.Client"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./oracle.Client\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "oracle.Client"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"oracle.Client=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "oracle.Client"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "oracle.Client"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep oracle.Client.Location`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "oracle.Client.Location"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "oracle.Client.Location"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "oracle.Client.Location"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./oracle.Client.Location\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "oracle.Client.Location"\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlDebug "Finished" "oracle.Client.Location"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "echo \"oracle.Client.Location=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Done" "oracle.Client.Location"\`"  >>/$TMP_DIR/prs.check

fi




res=`echo $line | grep network.pingLocalhost`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "network.pingLocalhost"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "network.pingLocalhost"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "network.pingLocalhost"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./network.ping localhost\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "network.pingLocalhost"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"network.pingLocalhost=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "network.pingLocalhost"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "network.pingLocalhost"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.gnu.tar`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.gnu.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.gnu.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.gnu.tar"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.cmd gtar\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.gnu.tar"\`"  >>/$TMP_DIR/prs.check
	
	echo "echo \"os.gnu.tar=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.gnu.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.gnu.tar"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.tar`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.tar"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.cmd tar\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.tar"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.tar=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.tar"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.nslookup`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.nslookup"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.nslookup"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.nslookup"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.cmd nslookup\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.nslookup"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.nslookup=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.nslookup"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.nslookup"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.architecture`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.architecture"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.architecture"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.architecture"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.architecture 32 bit\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.architecture"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.architecture=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.architecture"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.architecture"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep network.DHCPEnabled`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "network.DHCPEnabled"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "network.DHCPEnabled"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "network.DHCPEnabled"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./network.DHCPEnabled\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "network.DHCPEnabled"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"network.DHCPEnabled=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "network.DHCPEnabled"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "network.DHCPEnabled"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep network.dns`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "network.dns"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "network.dns"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "network.dns"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./network.dns\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "network.dns"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"network.dns=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "network.dns"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "network.dns"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.tmpdir`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.tmpdir"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.tmpdir"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.tmpdir"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.tmpdir\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.tmpdir"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.tmpdir=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.tmpdir"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.tmpdir"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.diskquota`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.diskquota"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.diskquota"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.diskquota"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.diskquota\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.diskquota"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.diskquota=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.diskquota"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.diskquota"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.hostformat`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.hostformat"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.hostformat"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.hostformat"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.hostformat\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.hostformat"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.hostformat=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.hostformat"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.hostformat"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.SELinux`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.SELinux"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.SELinux"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.SELinux"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check
	###Checking for Qualifier####
	IsQualFound=`echo "$res" | grep "]" | cut -d "=" -f1`
	prod=`echo "$res" | cut -d "=" -f2-8`
	if [ $IsQualFound ]; then
		echo "ss=\`./os.SELinux "$prod"\`" >>/$TMP_DIR/prs.check
	else
		echo "ss=\`./os.SELinux\`" >>/$TMP_DIR/prs.check
	fi
	echo "\`wrlTrace "Finished" "os.SELinux"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.SELinux=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.SELinux"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.SELinux"\`"  >>/$TMP_DIR/prs.check

fi



res=`echo $line | grep os.pagesize`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.pagesize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.pagesize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.pagesize"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.pagesize\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.pagesize"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.pagesize=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.pagesize"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.pagesize"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.commandPrompt`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.commandPrompt"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.commandPrompt"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.commandPrompt"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.commandPrompt\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.commandPrompt"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.commandPrompt=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.commandPrompt"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.commandPrompt"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep user.isAdmin`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "user.isAdmin"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "user.isAdmin"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "user.isAdmin"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.user\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "user.isAdmin"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"user.isAdmin=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "user.isAdmin"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "user.isAdmin"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.file.expect`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.file.expect"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.file.expect"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.file.expect"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.filepath /usr/bin/expect\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.file.expect"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.file.expect=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.file.expect"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.file.expect"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.file.tar`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.file.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.file.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.file.tar"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.filepath /usr/bin/tar\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.file.tar"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.file.tar=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.file.tar"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.file.tar"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.file.gzip`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.file.gzip"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.file.gzip"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.file.gzip"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.filepath /usr/bin/gzip\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.file.gzip"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.file.gzip=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.file.gzip"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.file.gzip"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.file.bash`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.file.bash"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.file.bash"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.file.bash"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.filepath /bin/bash\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.file.bash"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.file.bash=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.file.bash"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.file.bash"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.automount`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.automount"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.automount"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.automount"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.automount\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.automount"\`"  >>/$TMP_DIR/prs.check
	
	echo "echo \"os.automount=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.automount"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.automount"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.Firefox`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.Firefox"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.Firefox"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.Firefox"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	echo "ss=\`./os.Firefox\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.Firefox"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.Firefox=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.Firefox"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.Firefox"\`"  >>/$TMP_DIR/prs.check

fi


res=`echo $line | grep os.package`

if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.package"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.package"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.package"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	package=`echo $line | cut -d"=" -f2`
	prod=`echo $line |cut -d"=" -f1 |cut -d"." -f3-7`
	echo "ss=\`./os.package $prod\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.package"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.package.$prod=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.package"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.package"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.lib`

if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

	echo "\`wrlTrace "Starting" "os.lib"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Executing" "os.lib"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Starting" "os.lib"\`" >>/$TMP_DIR/prs.check
	#echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

	prod=`echo $line |cut -d"=" -f2`
	key=`echo $line |cut -d"=" -f1 |cut -d"." -f3-8`
	
	 echo "\`wrlDebug "Running os.lib"  \`" >>/$TMP_DIR/prs.check
     #   echo "\`wrlDebug "prod=$prod" "$prod" \`" >>/$TMP_DIR/prs.check
     #   echo "\`wrlDebug "key=$key" "$key" \`" >>/$TMP_DIR/prs.check
	echo "ss=\`./os.lib "\"$prod\"" "$key"\`" >>/$TMP_DIR/prs.check
	echo "\`wrlTrace "Finished" "os.lib"\`"  >>/$TMP_DIR/prs.check

	echo "echo \"os.lib.$key=\$ss\"" >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "Finished" "os.lib"\`"  >>/$TMP_DIR/prs.check
	echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

	echo "\`wrlTrace "Done" "os.lib"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.space`
Qual=`echo $line | grep "]" | cut -d "=" -f1`
if [ $Qual ]; then
        Valid=FALSE
        Validate_ID=`id | awk '{print $1}'| cut -d "(" -f1 | cut -d "=" -f2`
        if [ $Validate_ID -eq 0 ]; then
                IsRootValue=`echo $line | grep -w root`
                if [ $IsRootValue ]; then
                        Valid=TRUE
                fi
        else
                IsNon_RootValue=`echo $line | grep non_root`
                if [ $IsNon_RootValue ]; then
                        Valid=TRUE
                fi
        fi
        ###Handling both Root And NonRoot case###
        BothFound=`echo "$line" | grep "non_root" | grep -w "root" | cut -d "=" -f1`
        if [ $BothFound ]; then
                Valid=TRUE
        fi
else
        Valid=TRUE
fi
if [ $res ]; then
        if [ "$Valid" = "TRUE" ]; then
                ExpValue=`echo $res | cut -d "=" -f2`

                echo "\`wrlTrace "Starting" "os.space"\`"  >>/$TMP_DIR/prs.check
                echo "\`wrlTrace "Executing" "os.space"\`"  >>/$TMP_DIR/prs.check
                echo "\`wrlDebug "Starting" "os.space"\`" >>/$TMP_DIR/prs.check
                echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check
                if [ $Qual ]; then
                        prod=`echo $line | cut -d "=" -f2-8 | sed 's/;/#/g'`
                else
                        prod=`echo $line | cut -d "=" -f1 | cut -d "." -f3`
                fi
                key=`echo $line |cut -d"=" -f1 |cut -d"." -f3`
                echo "ss=\`./os.space "$prod"\`" >>/$TMP_DIR/prs.check
                echo "\`wrlTrace "Finished" "os.space"\`"  >>/$TMP_DIR/prs.check

                echo "echo \"os.space.$key=\$ss\"" >>/$TMP_DIR/prs.check
                echo "\`wrlDebug "Finished" "os.space"\`"  >>/$TMP_DIR/prs.check
                echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

                echo "\`wrlTrace "Done" "os.space"\`"  >>/$TMP_DIR/prs.check

        else
                CheckValue=`echo "$res" | cut -d "=" -f1`
                echo "echo \"$CheckValue=NOT_REQ_CHECK_ID\"" >>/$TMP_DIR/prs.check
        fi
fi


res=`echo $line | grep network.pingSelf`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2` 
        echo "\`wrlTrace "Starting" "network.pingSelf"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "network.pingSelf"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "network.pingSelf"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check
 

 echo "ss=\`./network.pingSelf\`" >>/$TMP_DIR/prs.check
 echo "\`wrlTrace "Finished" "network.pingSelf"\`"  >>/$TMP_DIR/prs.check
 echo "echo \"network.pingSelf=\$ss\"" >>/$TMP_DIR/prs.check
 echo "\`wrlDebug "Finished" "network.pingSelf"\`"  >>/$TMP_DIR/prs.check
 echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

 echo "\`wrlTrace "Done" "network.pingSelf"\`"  >>/$TMP_DIR/prs.check


fi

res=`echo $line | grep os.dir`
if [ $res ]; then
scriptCmd="os.dir"
scriptParams=`echo $res | cut -d "=" -f2`
checkName="os.dir"
checkSpec="$res"
generic="True"

`generateScanCmd "$scriptCmd" "$scriptParams" "$checkName" "$checkSpec" "$generic"`
#ExpValue=`echo $res | cut -d "=" -f2-8`

#        echo "\`wrlTrace "Starting" "os.dir"\`"  >>/$TMP_DIR/prs.check
#        echo "\`wrlTrace "Executing" "os.dir"\`"  >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "Starting" "os.dir"\`" >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

 #       prod=`echo $res | cut -d "=" -f2-8`
#        echo "ss=\`./os.dir "$prod"\`" >>/$TMP_DIR/prs.check
#        echo "\`wrlTrace "Finished" "os.dir"\`"  >>/$TMP_DIR/prs.check
#	key=`echo $line |cut -d"=" -f1 |cut -d"." -f3` 
#        echo "echo \"os.dir.$key=\$ss\"" >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "Finished" "os.dir"\`"  >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

#        echo "\`wrlTrace "Done" "os.dir"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.ksh`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "os.ksh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.ksh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.ksh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.ksh"\`"  >>/$TMP_DIR/prs.check

        echo "ss=\`./os.ksh\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.ksh"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"os.ksh=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.ksh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.ksh"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep os.iodevicestatus`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check

        echo "ss=\`./os.iodevicestatus\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"os.iodevicestatus=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.iodevicestatus"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.mountcheck`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check

        prod=`echo $res | cut -d "=" -f2-8`
        echo "ss=\`./os.mountcheck "$prod"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"os.mountcheck=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.mountcheck"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep os.shell.default`
if [ $res ]; then
scriptName="os.shell.default"
checkName="os.shell.default"
checkSpec="$res"
scriptParams=`echo $res | cut -d "=" -f2`

`generateScanCmd "$scriptName"  "$scriptParams" "$checkName" "$checkSpec"`
#ExpValue=`echo $res | cut -d "=" -f2`
#        echo "\`wrlTrace "Starting" "os.shell.default"\`"  >>/$TMP_DIR/prs.check
#        echo "\`wrlTrace "Executing" "os.shell.default"\`"  >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "Starting" "os.shell.default"\`"  >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "Starting" "os.shell.default"\`"  >>/$TMP_DIR/prs.check
#        echo "Sarath"
#        prod=`echo $res | cut -d "=" -f2-8`
 #       echo "ss=\`./os.shell.default "$prod"\`" >>/$TMP_DIR/prs.check
 #       echo "\`wrlTrace "Finished" "os.shell.default"\`"  >>/$TMP_DIR/prs.check
#        echo "echo \"os.shell.default=\$ss\"" >>/$TMP_DIR/prs.check
#        echo "\`wrlDebug "Finished" "os.shell.default"\`"  >>/$TMP_DIR/prs.check
 #       echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

#        echo "\`wrlTrace "Done" "os.shell.default"\`"  >>/$TMP_DIR/prs.check

fi

res=`echo $line | grep os.ldLibPath`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check

        prod=`echo $res | cut -d "=" -f2-8`
        echo "ss=\`./os.ldLibPath "$prod"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"os.ldLibPath=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.ldLibPath"\`"  >>/$TMP_DIR/prs.check
fi


res=`echo $line | grep network.fqdn`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "network.fqdn"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "network.fqdn"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "network.fqdn"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check


        echo "ss=\`./network.fqdn\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "network.fqdn"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"network.fqdn=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "network.fqdn"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "network.fqdn"\`"  >>/$TMP_DIR/prs.check


fi

res=`echo $line | grep Key_Library`
#res=""
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check

        prod=`echo $res | cut -d "=" -f2-8`
        echo "ss=\`./Key_Library.sh "$prod"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"Key_Library=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "Key_Library.sh"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.ServicePack`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check

        prod=`echo $res | cut -d "=" -f2-8`
        echo "ss=\`./os.ServicePack "$prod"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"os.ServicePack=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.ServicePack"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.FreePagingSpace`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "os.FreePagingSpace"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.FreePagingSpace"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.FreePagingSpace"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check


        echo "ss=\`./os.FreePagingSpace\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.FreePagingSpace"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"os.FreePagingSpace=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.FreePagingSpace"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.FreePagingSpace"\`"  >>/$TMP_DIR/prs.check


fi
res=`echo $line | grep CpuArchitecture`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`
        echo "\`wrlTrace "Starting" "CpuArchitecture"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "CpuArchitecture"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "CpuArchitecture"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check


        echo "ss=\`./CpuArchitecture\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "CpuArchitecture"\`"  >>/$TMP_DIR/prs.check
        echo "echo \"CpuArchitecture=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "CpuArchitecture"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "CpuArchitecture"\`"  >>/$TMP_DIR/prs.check


fi

res=`echo $line | grep de.isInstalled`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "de.isInstalled"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "de.isInstalled"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "de.isInstalled"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        echo "ss=\`./de.isInstalled\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "de.isInstalled"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"de.isInstalled=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "de.isInstalled"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "de.isInstalled"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep de.installationUnit`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "de.installationUnit"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "de.installationUnit"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "de.installationUnit"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        echo "ss=\`./de.installationUnit\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "de.installationUnit"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"de.installationUnit=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "de.installationUnit"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "de.installationUnit"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep DBType`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "DBType"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "DBType"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "DBType"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        echo "ss=\`./DBType\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "DBType"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"DBType=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "DBType"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "DBType"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep DBTypeDetails`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "DBTypeDetails"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "DBTypeDetails"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "DBTypeDetails"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        echo "ss=\`./DBTypeDetails\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "DBTypeDetails"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"DBTypeDetails=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "DBTypeDetails"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "DBTypeDetails"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.patch`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "os.patch"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.patch"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.patch"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        echo "ss=\`./os.patch\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.patch"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"os.patch=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.patch"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.patch"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.LibPatch`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "os.LibPatch"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.LibPatch"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.LibPatch"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check
        key=`echo $line |cut -d"=" -f1 |cut -d"." -f3-8`
        echo "ss=\`./os.LibPatch $key\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.LibPatch"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"os.LibPatch=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.LibPatch"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.LibPatch"\`"  >>/$TMP_DIR/prs.check
fi

res=`echo $line | grep os.itmInstalled`
if [ $res ]; then
ExpValue=`echo $res | cut -d "=" -f2`

        echo "\`wrlTrace "Starting" "os.itmInstalled"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Executing" "os.itmInstalled"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Starting" "os.itmInstalled"\`" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Expected" "$ExpValue" \`" >>/$TMP_DIR/prs.check

        echo "ss=\`./os.itmInstalled\`" >>/$TMP_DIR/prs.check
        echo "\`wrlTrace "Finished" "os.itmInstalled"\`"  >>/$TMP_DIR/prs.check

        echo "echo \"os.itmInstalled=\$ss\"" >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "Finished" "os.itmInstalled"\`"  >>/$TMP_DIR/prs.check
        echo "\`wrlDebug "OutPutValueIs" \$ss\`"  >>/$TMP_DIR/prs.check

        echo "\`wrlTrace "Done" "os.itmInstalled"\`"  >>/$TMP_DIR/prs.check
fi


  done

exefile=`echo $2 | sed 's/\.cfg/\.sh/g'`
cp -f /$TMP_DIR/prs.check $1/$exefile
fi
rm -f /$TMP_DIR/prs.check

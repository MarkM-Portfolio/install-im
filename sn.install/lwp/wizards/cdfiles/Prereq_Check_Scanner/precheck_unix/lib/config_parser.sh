# ************** Begin Copyright - Do not add comments here ****************
#
#  Licensed Materials . Property of IBM
#  (C) Copyright IBM Corp. 2009, 2011
#  All Rights Reserved. US Government Users Restricted Rights - Use, duplication or
#  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# ************************ End Standard Header *************************

#set -n
. $PREREQ_HOME/lib/common_function.sh

`wrlLogFuncStart "config_parser.sh"`
`wrlDebugFuncParam "OSInfo" "$1"`
        `wrlDebugFuncParam "ProductCode" "$2"`
        `wrlDebugFuncParam "OSArch" "$3"`
        `wrlDebugFuncParam "CPUArch" "$4"`
        `wrlDebugFuncParam "Version" "$5"`
        `wrlDebugFuncParam "XXX" "$6"`

###Validating and Handling the mutliple Configuration files Case####
version=`echo "$5" | cut -d "=" -f2`
check=`echo $version | grep  "[0-9]"`
if [ $check ]; then
        mv $PREREQ_HOME/UNIX_Linux/$2_$version.cfg $PREREQ_HOME/UNIX_Linux/$2_$version.cfg-Master 2>/dev/null
        CONFIG="$PREREQ_HOME/UNIX_Linux/$2_$version.cfg-Master"
else
        Master_File="`ls $PREREQ_HOME/UNIX_Linux/$2*.cfg 2>/dev/null | sort -ir | sed -n "1p"`"
        mv $Master_File $Master_File"-Master" 2>/dev/null
        CONFIG=$Master_File"-Master"
        echo $CONFIG >/$TMP_DIR/check5
fi

FALSE=1
TRUE=0
GetIn=$FALSE
First_String=""
ParseCount=0
ParseArray=""

# Function name : Form_Parse_String
# Description   : Forms the Array to compare with the section in the config file
# inputs        : $1 -> Section name to parse
#
Form_Parse_String() {

        `wrlLogFuncStart "Form_Parse_String"`
        `wrlDebugFuncParam "OSInfo" "$1"`
        `wrlDebugFuncParam "ProductCode" "$2"`
        `wrlDebugFuncParam "OSArch" "$3"`
        `wrlDebugFuncParam "CPU" "$4"`
        `wrlDebugFuncParam "CPUArch" "$5"`

        OSNamepre=`echo $1 | cut -d '{' -f1`
        ## REmoving release from OSNAME for RedHatEnterpriseLinuxServer release
        ## Removing V from OSName for Solaris V10 and removing spaces   
        OSName=`echo $OSNamepre | sed 's/release//g' |sed  's/V//g' | sed  's/ //g'`
        #Detect the OS version if it is Oracle Solaris System
        if [ "`uname`" = "SunOS" ]; then
                IsOracleSun=`cat /etc/*release 2>/dev/null | grep "Solaris" | grep "Oracle"`
                if [ "$IsOracleSun" != "" ]; then
                        OSName=`echo "$IsOracleSun" | awk '{print $2$3}'`
                fi
        fi
        ###Check for Update in RHEL 4Versions anc change it to specified format###
        isred=`cat /etc/*release 2>/dev/null | grep "Linux" | awk '{print $1}' | sed -n '/[Rr]ed/p'| sed -n 1p`
        if [ $isred ]; then
                # get the version
                ver4=`cat /etc/*release 2>/dev/null | grep "Linux" | grep "Red" | sed 's/.* release \([0-9]\{1,\}\).*/\1/g'| sed -n 1p`
                if [ $ver4 ]; then
                        UpdateFound=`cat /etc/*release 2>/dev/null | grep -i update | sed 's/[Uu]pdate/[Uu]pdate+/g' | cut -d "+" -f2 | sed 's/ //g' | cut -c 1 | sed -n 1p`
                        if [ $UpdateFound ]; then
                                OSName=$OSName"."$UpdateFound
                        fi
                fi
        fi

	cputest=`echo $4 | cut -d '=' -f2`
	Archtest=`echo $3 | cut -d "=" -f2`
        CPUArchtest=`echo "$5" | cut -d "=" -f2`
        if [ "$cputest" = "" ]; then
      		if [ "$Archtest" = "" ]; then
       			Section="[OSType:$OSName]" 
       	 	else
       			Section="[OSType:$OSName][OSArch:$Archtest]" 
           	fi
       else
      	        Section="[OSType:$OSName][OSArch:$3][CPU:$cputest]" 
       fi

       if [ "$CPUArchtest" = "" ]; then
       		echo "$Section[OSKernel:$CPUArchtest]" >/$TMP_DIR/prs.test3
       else
       		Section="$Section[CPUArch:$CPUArchtest]"
       fi

        ParseArray=""
        IsRedhatFound=""
        IsEnterpriseFound=""
        ISAdvanceFound="" 
			#####Checking for OS NAME and adding the Sections to the CONFIG Fiel####
			IsAIXFound=`echo "$OSName" | grep AIX | cut -d " " -f1`
			IsRedhatFound=`echo "$OSName" | grep -i "RedHat" | cut -d " " -f1`
                        IsEnterpriseFound=`echo "$OSName" | grep -i "Enterprise" | cut -d " " -f1`
                        ISAdvanceFound=`echo "$OSName" | grep -i "Advance" | cut -d " " -f1`
			ISAdvanceASFound=`echo "$OSName" | grep  "AS" | cut -d " " -f1`
			ISAdvanceESFound=`echo "$OSName" | grep  "ES" | cut -d " " -f1`
                        IsRedhatEnterpriseLinuxServerFound=`echo "$OSName" | grep -i "RedHatEnterpriseLinux" | cut -d " " -f1`
			IsSolarisFound=`echo "$OSName" | grep -i Solaris | cut -d " " -f1`
			IsSUSEFound=`echo "$OSName" | grep -i "SUSE" | cut -d " " -f1`

			IsHP_UXFound=`echo "$OSName" | grep -i HP-UX | cut -d " " -f1`
			IsHP_UXv1Found=`echo "$OSName" | grep -i HP-UX | grep -i 11iv1 | cut -d " " -f1`
			IsHP_UXv2Found=`echo "$OSName" | grep -i HP-UX | grep -i 11iv2 |cut -d " " -f1`
			IsHP_UXv3Found=`echo "$OSName" | grep -i HP-UX | grep -i 11iv3 |cut -d " " -f1`
			
                        if [ $IsHP_UXFound ]; then
                                ParseArray="[OSType:UNIX][OSType:HP-UX]$Section"
			# Hp-UX version 1
                        elif [ $IsHP_UXv1Found ]; then
                                ParseArray="[OSType:UNIX][OSType:HP-UX][OSType:HP-UX 11iv1]$Section"
			# Hp-UX version 2
                        elif [ $IsHP_UXv2Found ]; then
                                ParseArray="[OSType:UNIX][OSType:HP-UX][OSType:HP-UX 11iv2]$Section"
			# Hp-UX version 3
                        elif [ $IsHP_UXv3Found ]; then
                                ParseArray="[OSType:UNIX][OSType:HP-UX][OSType:HP-UX 11iv3]$Section"

                        elif [ $IsSolarisFound ]; then
                                ParseArray="[OSType:UNIX][OSType:Solaris]$Section"
                        elif [ $IsSUSEFound ]; then
                                ParseArray="[OSType:UNIX][OSType:LINUX][OSType:SUSE]$Section"

                        elif [ $IsAIXFound ]; then
				VersionString=`echo $OSName | cut -d '.' -f1`".*"
				ParseArray="[OSType:UNIX][OSType:AIX][OSType:$VersionString]$Section"

                        elif [ $IsRedhatEnterpriseLinuxServerFound ]; then
                                 VersionString=`echo $OSName | cut -d '.' -f1`".*"
                                 ParseArray="[OSType:UNIX][OSType:LINUX][OSType:RedHat][OSType:RedHatEnterpriseLinuxServer][OSType:$VersionString]$Section"
                                 if [ "$ISAdvanceFound" != "" ]; then
                                 	ParseArray="[OSType:UNIX][OSType:LINUX][OSType:RedHat][OSType:RedHatEnterpriseLinuxServer][OSType:RedHatEnterpriseLinuxAdvance][OSType:$VersionString]$Section"
                                 fi
                                 if [ "$ISAdvanceASFound" != "" ]; then
                                   	OSName1=`echo $OSName | sed 's/AS/Server/g'`
                                   
                                   	VersionString1=`echo $OSName1 | cut -d '.' -f1`".*"
                                   	ParseArray="[OSType:UNIX][OSType:LINUX][OSType:RedHat][OSType:$VersionString1][OSType:RedHatEnterpriseLinuxAS][OSType:$VersionString]$Section"
                                 fi
                                 if [ "$ISAdvanceESFound" != "" ]; then
                                       OSName1=`echo $OSName | sed 's/ES/Server/g'`

                                        VersionString1=`echo $OSName1 | cut -d '.' -f1`".*"
                                   	ParseArray="[OSType:UNIX][OSType:LINUX][OSType:RedHat][OSType:RedHatEnterpriseLinuxServer][OSType:RedHatEnterpriseLinuxES][OSType:$VersionString]$Section"
                                 fi

			elif [ "$IsRedhatFound" != "" -a "$ISAdvanceFound" != ""  -o "$ISAdvanceESFound" != "" -o "$ISAdvanceASFound" != ""  ]; then
                                 VersionString=`echo $OSName | cut -d '.' -f1`".*" 
			         ParseArray="[OSType:UNIX][OSType:LINUX][OSType:RedHat][OSType:$VersionString]$Section"

                        else
				ParseArray="[OSType:UNIX]"
                                #echo "WARNING: Product doesn't support this OS"
			fi
               
                	ParseArray=$ParseArray
#	echo "** $ParseArray ** " >/$TMP_DIR/sachin1
	`wrlDebugFunc "Form_Parse_String - ParseArray: $ParseArray"`
        `wrlLogFuncExit "Form_Parse_String"`

}

# Function name : Read_conigFile
# Description   : Reads the config file with sectionwise and the result will be store in file
# inputs        : $1 -> config File name
#
Read_conigFile() {
	
        `wrlLogFuncStart "Read_configFile()"`
	`wrlDebugFuncParam "ConfigFile" "$1"`
        `wrlDebugFuncParam "Product" "$2"`
	CONFIG_FILE=$1
        if [ -f $CONFIG_FILE ]; then

        	cat $CONFIG_FILE | while read LINE
        	do
        		TRIM_LINE=`echo $LINE | tr -s "        " " "`
               
              		#Skip the commented lines
              		FIRSTCHAR_TRIM_LINE=`echo $TRIM_LINE | cut -c 1`
                	if [ "$FIRSTCHAR_TRIM_LINE" = "#" ]
                	then
                        	continue
                	fi
                	if [ -z "$TRIM_LINE" ]
                	then
                        	continue
                	fi

                	FIRSTCHAR_LINE=`echo $LINE | cut -c 1`
                	LenLINE=`echo $LINE | tr -s "        " " " |  wc -m`
                	LLINE=`expr $LenLINE - 1`
                	LASTCHAR=`echo $LINE | cut -c $LLINE` 
	                if [ "$FIRSTCHAR_LINE" = "[" -a "$LASTCHAR" = "]" ]
        	        then
                		if [ $GetIn -eq $TRUE ]
                        	then
                                	GetIn=$FALSE
                        	fi
                      
                        	###Checking for ENV variables###
	                        EnvFound=`echo $TRIM_LINE | grep "@" | cut -d ":" -f1`
        	                if [ $EnvFound ]; then
                	                OrigLine=`echo $TRIM_LINE | sed 's/\]\[/\] \[/g' | sed 's/|/\] \[/g'`
                        	        EnvLine=""
	                                for EvryField in $OrigLine
        	                        do
                	                        ####Check for EnvSymbol####
                        	                IsFound=`echo "$EvryField" | cut -c 2`
     		                                if [ "$IsFound" = "@" ]; then
	                                                EnvLine=$EvryField" $EnvLine"
        	                                fi
                	                done
                                
					####ENV Variables Line Created,Now Checking the EnvValues Set in the Environment####
                                
					for EnvField in $EnvLine
                                	do
                                        	EnvVar=`echo "$EnvField" | cut -c 3- | cut -d ":" -f1 `
                                        	`wrlDebugFunc "Found Env Var - $EnvVar"`
                                        	ConfigEnvVal=`echo "$EnvField" | cut -d ":" -f2 | cut -d "]" -f1`
                                         	`wrlDebugFunc "$EnvVar has '$ConfigEnvVal' in config file"`
					
        	                                ####Finding the EnvVar Value from Environment####

                	                        ExistingEnvVal=`env | grep "$EnvVar" | cut -d "=" -f2`
                                        	`wrlDebugFunc "$EnvVar env var value - '$ExistingEnvVal'"`
                    		                if [ "$ConfigEnvVal" = "$ExistingEnvVal" ]; then
                                                	`wrlDebugFunc "Adding $EnvVar to ParseArray..."`  
                         	                       	ParseArray=$ParseArray$EnvField
                                        	fi
                             		 done
                        	fi
				#echo "** $ParseArray ** "
				#replace the [ and ] char to ~ before the grep
                	        Trim_ParseArray=`echo $ParseArray | sed 's/\]/~/g' | sed 's/\[/~/g'`
                        	Trim_Line=`echo $TRIM_LINE | sed 's/\]/~/g' | sed 's/\[/~/g' | sed 's/|/+/g'`
          		        Trim_Line1=`echo $Trim_Line |  sed 's/~~/~ ~/g'`       

	                       	GetInFlag=TRUE
        	         	if [ `echo "$Trim_ParseArray" | grep -i "$Trim_Line" | cut -d' ' -f1` ]
                	        then
                        	        GetIn=$TRUE
                                	continue
                        	else
                         		for eachSection in $Trim_Line1
                         		do 
                          			eachSectionCheck=`echo $eachSection | grep "+"`
               		          		if [ $eachSectionCheck ]; then 
                                                	`wrlDebugFunc "Found '|' in section..."`
                        	        		orList=`echo $eachSection | sed 's/+/~ ~/g'`
                                			orString="" 

                                        	        ####Getting the Type of Field Like (OSType,OSArch,CPUNAME,)####
                                                	Type=`echo "$eachSection" | cut -c 2- | cut -d ":" -f1`

                                			for eachor in $orList
                                			do 
                                                        	####Differentiating Between EnvFields and Normal Fields####
                                                        	IsEnv=`echo "$eachor" | cut -c 2`
                                                        	if [ "$IsEnv" = "@" ]; then
                                                                	eachor=$eachor
                                                        	else
                                                                	####Checking Whether Type of Field  Present or not (OSType,OSArch,CPUNAME,)####
                                                                	IsFound=`echo "$eachor" | grep "$Type"`
                                                                	if [ $IsFound ]; then
                                                                        	eachor=$eachor
                                                                	else
                                                                        	eachor=`echo "$eachor" | cut -c 2- | sed 's/^/~'$Type':/g'`
									fi
								fi
                                 				if [ `echo "$Trim_ParseArray" | grep -i "$eachor" | cut -d' ' -f1` ]; then
                                 					orString=$orString" True"
                                  				else
                                  					orString=$orString" False" 
                                 				fi
                                			done

		                              		orStringCheck=`echo $orString | grep True | cut -d ' ' -f1`
               			              		if [ $orStringCheck ]; then
								GetInFlag=TRUE	
                               				else
                                				GetInFlag=FALSE
                                                        	break
                               				fi                                 
 
                         			elif [ `echo "$Trim_ParseArray" | grep -i  "$eachSection" | cut -d' ' -f1` ]
                        			then
                                			GetInFlag=TRUE
                               			else
                                			GetInFlag=FALSE
                                			break 
        					fi

                          		done
                        	fi
                	fi
                	if [ "$GetInFlag" = "FALSE" ]; then
                		GetIn=$FALSE
                	else
                		FIRSTCHAR_LINE=`echo $LINE | cut -c 1`
                        	if [ "$FIRSTCHAR_LINE" = "[" ]; then
    					GetIn=$FALSE 
                        	else
                          		GetIn=$TRUE
                        	fi
                	fi
                	if [ $GetIn -eq $TRUE ]
                	then
                        	check=`echo $version | grep  "[0-9]"`
        
       			        if [ $check ]; then
                                	echo $TRIM_LINE >>$2_$version.cfg
                        	else
                                	version=`echo "$CONFIG_FILE" | sed 's/.*\/\(.*\)/\1/' | cut -d "_" -f2 | cut -d "." -f1`
                                	`wrlDebugFunc "Writing $TRIM_LINE to $2_$version.cfg"`
                                	echo $TRIM_LINE >>$2_$version.cfg
                        	fi
                	fi
        	done
	fi
	`wrlLogFuncExit "Read_configFile()"`
}
`wrlDebugFunc "Forming parse array..."`
Form_Parse_String "$1" $2 $3 $4 $6
`wrlDebugFunc "Reading config file and parsing using parse array..." `                              
Read_conigFile $CONFIG $2 $version
cp $2_*.cfg ../UNIX_Linux/ 2>/dev/null
#cat $2_$version.cfg  | sed '/^$/d' > ../UNIX_Linux/$2_$version.cfg
rm -f $2_*.cfg 2>/dev/null
`wrlLogFuncExit "config_parser.sh"`

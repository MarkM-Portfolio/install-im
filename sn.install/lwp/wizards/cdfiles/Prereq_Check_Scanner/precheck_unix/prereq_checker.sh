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

#START TRANSLATABLE

Msg_USAGE='Usage: ';export Msg_USAGE
Msg_EXAMPLE='Example:  ';export Msg_EXAMPLE
Msg_DETAIL_STR='Details also available in';export Msg_DETAIL_STR
Msg_INVALID_PRODUCT='The tool does not support product code: $var1';export Msg_INVALID_PRODUCT
Msg_NOSPACE='Not enough space left in "/tmp" directory. Minimum 10 MB space required to run the tool. Exiting...';export Msg_NOSPACE
Msg_FIELD_EMPTY='ERROR: Value for "outputDir" input parameter cannot be empty';export Msg_FIELD_EMPTY
Msg_OUTPUTDIR='Setting Prerequisite Scanner output directory to user defined directory: $var1';export Msg_OUTPUTDIR
Msg_CREATEFILE='ERROR: Cannot create files in Prerequisite Scanner output directory: "$var1"  Exiting...';export Msg_CREATEFILE
Msg_CREATETMPFILE='ERROR: Cannot create files in Prerequisite Scanner temporary directory: "/tmp/$var1"  Exiting...';export Msg_CREATETMPFILE
Msg_NOTVALIDPRD='ERROR: Product Code [$var1] is not valid. Check the product code and try again.';export Msg_NOTVALIDPRD
Msg_NOTVALIDPRDTRYAGAIN='ERROR: No valid product codes found. Check the product codes and try again.';export Msg_NOTVALIDPRDTRYAGAIN
Msg_VERNOTFOUND='ERROR: Cannot find configuration file $var1 for $var2.  Exiting...';export Msg_VERNOTFOUND
Msg_PASS='PASS';export Msg_PASS
Msg_FAIL='FAIL';export Msg_FAIL
Msg_Fail='Fail';export Msg_Fail
Msg_AVAILABLE='Available';export Msg_AVAILABLE
Msg_UNAVAILABLE='Unavailable';export Msg_UNAVAILABLE
Msg_NOTFOUND='Not Found';export Msg_NOTFOUND
Msg_TRUE='True';export Msg_TRUE
Msg_FALSE='False';export Msg_FALSE
Msg_2PARAMETER='You must specify 2 input parameters';export Msg_2PARAMETER
Msg_Enabled='Enabled';export Msg_Enabled
Msg_Disabled='Disabled';export Msg_Disabled
Msg_NOTREQUIRED='Not Required';export Msg_NOTREQUIRED
Msg_UNLIMITED='Unlimited';export Msg_UNLIMITED
Msg_CONTAINPARAMETER='You must specify an input parameter';export Msg_CONTAINPARAMETER
Msg_CONTAINPATH='You must specify input parameter with path';export Msg_CONTAINPATH
Msg_Version='Version';export Msg_Version
Msg_Build='Build';export Msg_Build
Msg_OSName='OS name';export Msg_OSName
Msg_UserName='User Name';export Msg_UserName
Msg_MachineInfo='Machine Information';export Msg_MachineInfo
Msg_MachineName='Machine Name';export Msg_MachineName
Msg_MachineSNo='Serial number';export Msg_MachineSNo
Msg_Property='Property';export Msg_Property
Msg_RESULT_STR='Result';export Msg_RESULT_STR
Msg_Found='Found';export Msg_Found
Msg_Expected='Expected';export Msg_Expected
Msg_AllSpecifiedComponent='ALL SPECIFIED COMPONENTS';export Msg_AllSpecifiedComponent
Msg_OverallResult='Prerequisite Scanner Overall Result';export Msg_OverallResult


#END TRANSLATABLE

#       Function name : printmessage
#       Description : Print the messages on the console
#       input : it takes maximum 2 parameters 
#       output : None

printmessage() {
        var1=$2
        var2=$3
        var3=$4
                #replacing characters '/' with '~' as the sed does not work with '/'
        NewVar1=`echo $var1 | tr '/' '~'`
        NewVar2=`echo $var2 | tr '/' '~'`
        NewVar3=`echo $var3 | tr '/' '~'`
        echo "$1" | sed -e "s/\$var1/$NewVar1/g" | sed -e "s/\$var2/$NewVar2/g" | sed -e "s/\$var3/$NewVar3/g" | tr '~' '/'
}



# Define the XML results flags
XML_Header=1
PRS_Info=2
Machine_Info=3
User_Info=4
Product_Info=5
Detailed_Result=6
Overall_Result=7
XML_Footer=8
XML_Flag=$XML_Header

True=0
False=1
TotalComp=$True
LoopCount=0
PREREQ_BUILD="20120423"
PRS_Version="1.1.1.10"
Msg_DENOTAVAILABLE='DENotAvailable';export Msg_DENOTAVAILABLE
Indent='    '
PRS_Str="IBM Prerequisite Scanner"
os_name=`uname`
if [ "`uname`" = "SunOS" ];then
        HostID=`hostid`
fi

# RTC Defect 20889
#	Find the working directory for all the supported platforms
#	Workign directory "PREREQ_HOME" is available thoughout the execution of PRS

if [ "`uname`" = "SunOS" ];then
        PRS_HOME=`echo $0`
        PREREQ_HOME=`dirname $PRS_HOME`
        if [ "$PREREQ_HOME" = "." ]; then
                PREREQ_HOME=`pwd`
        else
                cd $PREREQ_HOME
                PREREQ_HOME=`pwd`
        fi
else
        PRS_HOME=`dirname $0`
        cd $PRS_HOME
        PREREQ_HOME=$PWD
        cd - > /dev/null 2>&1
fi

export PREREQ_HOME
PREREQ_LOGDIR="$PREREQ_HOME"
export PREREQ_LOGDIR




PREREQ_TRACE="True"
export PREREQ_TRACE
PREREQ_DEBUG="True"
export PREREQ_DEBUG
####################################################
#	Loging function it puts data in to log file
wrl(){
        printf "[%-20s] %-8s: %-s\n" "`date '+%Y-%m-%d %H:%M:%S'`" "$1" "$2" >> $TMP_DIR/$plog
}

#	Calls wrl with the type of log info and the message

wrll(){
        wrl "INFO" "$1"
}

#	Calls wrl() with type of log warning and message
wrlw(){
        wrl "WARNING" "$1"
}

#	Cleans tmp directory
clean_temp(){
	rm -f $TMP_DIR/$tft $TMP_DIR/$tfi $TMP_DIR/$tfe
}
clean_temp_Directory() {
	# RTC Defect 20889
	#       Moving "results.txt", "precheck.log" and prs.trc files to "outputDir" dir
	#       "outputDir" dir is deleted if Prereqchecker is running in non-debug mode
	#       Dir "outputDir" is not deleted if running in debug mode


	outputdir=`dirname $TMP_DIR`
	if [ "$traceFlag" = "True" ]; then
		if [ ! "$PRS_OUTPUT_DIR" = "" ]; then
			mv $TMP_DIR/result.txt $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/result.xml $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/precheck.log $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/prs.trc $outputdir/ > /dev/null 2>&1
		else
			mv $TMP_DIR/result.txt $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/result.xml $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/precheck.log $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/prs.trc $Default_outputDir/ > /dev/null 2>&1
		fi
	elif [ "$debugFlag" = "True" ]; then
		if [ ! "$PRS_OUTPUT_DIR" = "" ]; then
			mv $TMP_DIR/result.txt $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/result.xml $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/precheck.log $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/prs.debug $outputdir/ > /dev/null 2>&1
		else
			mv $TMP_DIR/result.txt $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/result.xml $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/precheck.log $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/prs.debug $Default_outputDir/ > /dev/null 2>&1
		fi
	else
		if [ ! "$PRS_OUTPUT_DIR" = "" ]; then
			mv $TMP_DIR/result.txt $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/result.xml $outputdir/ > /dev/null 2>&1
			mv $TMP_DIR/precheck.log $outputdir/ > /dev/null 2>&1
			rm -rf $TMP_DIR > /dev/null 2>&1
		else
			mv $TMP_DIR/result.txt $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/result.xml $Default_outputDir/ > /dev/null 2>&1
			mv $TMP_DIR/precheck.log $Default_outputDir/ > /dev/null 2>&1
			rm -rf $TMP_DIR > /dev/null 2>&1
		fi
	fi
	if [ "$xmlResult" = "True" ]; then
		mv $PREREQ_HOME/PRSResults.xsd $outputdir/ > /dev/null 2>&1
	fi

	echo "$Msg_DETAIL_STR $outputdir/result.txt"
	cd $outputdir
	rm -rf $PREREQ_HOME > /dev/null 2>&1

}
#	Description : Detects OS 
#	input : it takes 3 parameters , 1 : product info 2: OS info 3 : CPU architecture
#	output : product code (TPM)and version (07200000)
AutoOsDetection(){

	`wrlLogFuncStart "AutoOsDetection()"`
        `wrlDebugFuncParam "ProductInfo" "$1"`
         `wrlDebugFuncParam "OSInfo" "$2"`
        `wrlDebugFuncParam "CPU Arch" "$3"`
 	product_version=$1           
        
        cd $PREREQ_HOME/lib
	
	#####Executing the Script for detecting the OS#####
	Os_detected="$2"
        product=`echo $product_version | cut -d '+' -f1`

	####Checking if the Version is specified with the Product or not ####
        NoofCodes=`echo $product_version | sed 's/+/ /g' | wc -w`
        if [ $NoofCodes -gt 1 ]; then

                version=`echo $product_version | cut -d '+' -f2`
        else
                version=""
        fi
	####Ending the Validation#####
	prod_check=""
         numProdname=`ls $PREREQ_HOME/*codename.cfg | wc -l` 
	if [ $numProdname -eq 1 ]; then
        `wrlDebugFunc "Finding product code in product.cfg"`	
	prod_check=`grep $product $PREREQ_HOME/*codename.cfg | grep " for "  | cut -d'=' -f1`
         `wrlDebugFunc "product code found : $prod_check"`
        fi
         if [ $numProdname -gt 1 ]; then
        `wrlDebugFunc "Finding product code in multiple product.cfg"`
         code_check=`grep $product $PREREQ_HOME/*codename.cfg | cut -d ":" -f2 | cut -d "=" -f1 | sed -n 1p`
	  `wrlDebugFunc "product code found : $code_check"`
         fi

        if [ "$prod_check" = "" ]; then
                `wrlDebugFunc "Found $product code in product.cfg"`
		if [ "$code_check" = "" ]; then
			code_check=`grep $product $PREREQ_HOME/*codename.cfg | cut -d'=' -f1`
		fi
                `wrlDebugFunc "Finding OS Arch and CPU Type"`
		####Checking for The Architecture of the Server####
        	arch64_test=`echo "$Os_detected" | grep -i "64-bit" | cut -d ' ' -f1`
        	arch32_test=`echo "$Os_detected" | grep -i  "32-bit" | cut -d ' ' -f1`
                #archppc64_test=`echo "$Os_detected" | grep -i  "ppc64" | cut -d ' ' -f1`
                #archs390x_test=`echo "$Os_detected" | grep -iw  "s390x" | cut -d ' ' -f1`
                #archs390_test=`echo "$Os_detected" | grep -iw  "s390" | cut -d ' ' -f1`
		####Checking for the CPU of the Server####
       		cpu_itanium=`echo "$Os_detected" | grep -i Itanium | cut -d '=' -f1`

		####Validating#####
       		if [ $plat_test ]; then
        		Os_detected="UNIX"
           		code_check=$prod_check
        	fi
      		if [ $plat_test_linux ]; then
           		Os_detected="LINUX"
           		code_check=$prod_check
       		fi

        	os_arch=""
        	if [ $arch64_test ]; then
        		os_arch="64-bit"
        	fi
        	if [ $arch32_test ]; then
        		os_arch="32-bit"
        	fi
                #if [ $archppc64_test ]; then
                #        os_arch="ppc64"
                #fi
                #if [ $archs390x_test ]; then
                #        os_arch="s390x"
                #fi
                #if [ $archs390_test ]; then
                #        os_arch="s390"
                #fi


       		cpu_test=""
        	if [ $cpu_itanium ]; then
         		cpu_test="Itanium"
        	fi 
#	This function is called during the OS detection
#	it creates the Master Config file parses it and creates the new config file, without the sections.
                   `wrlDebugFunc "Found OS Arch = $os_arch, CPU Type=$cpu_test"`
                        `wrlDebugFunc "Calling config_parser.sh... "`
			`wrlDebugFunc "****** Found OS Arch = $os_arch, CPU Type=$cpu_test "`
			$PREREQ_HOME/lib/config_parser.sh "$Os_detected" $code_check "Arch=$os_arch" "CPU=$cpu_test" "version=$version" $3 
                #cd ..
	else
		`wrlDebugFunc "Finding product code in codename.cfg"`
                Arg_code=$product
		#Line=`grep $product ../*codename.cfg | cut -d ":" -f2`
		#check=`grep $product ../*codename.cfg | cut -d ":" -f2 | cut -d "=" -f1`
                 Line=`grep $product $PREREQ_HOME/*codename.cfg 2>/dev/null | cut -d ":" -f2`
                check=`grep $product $PREREQ_HOME/*codename.cfg 2>/dev/null | cut -d ":" -f2 | cut -d "=" -f1`

		`wrlDebugFunc "Line=[$Line]"`
		`wrlDebugFunc "check=[$check]"`
		`wrlDebugFunc "Os_detected=[$Os_detected]"`

		IsFound=`echo $Line | grep "$Os_detected" | cut -d "=" -f1`
		OsValue=`echo $Line | cut -d "=" -f2`
		####Checking for Special Cases common Config Files for All Platforms####
		IsUnixFound=""
		IsLinuxFound=""
		IsUnixFound=`echo $Line | grep "UNIX" | cut -d "=" -f1` 
		IsLinuxFound=`echo $Line | grep "LINUX"| cut -d "=" -f1`

		`wrlDebugFunc "IsFound=[$IsFound]"`
		`wrlDebugFunc "IsUnixFound=[$IsUnixFound]"`
		`wrlDebugFunc "IsLinuxFound=[$IsLinuxFound]"`

		if [ "$check" != "" ];then
                       `wrlDebugFunc "Found product code in codename.cfg"`
			if [ $IsFound ]; then
				code_check=$IsFound
			elif [ $IsUnixFound ]; then
				code_check=$IsUnixFound
			elif [ $IsLinuxFound ]; then
				code_check=$IsLinuxFound	
			else
				code_check=$check
				#echo "TPS does not support $Arg_code for $OsValue"
			fi
		else
                       `wrlDebugFunc "Could not find product code in product.cfg or codename.cfg"`  
			printmessage "$Msg_INVALID_PRODUCT" $product
		fi
	fi
	
	echo $code_check $version 
	`wrlDebugFuncReturn "Code = $code_check, version= $version"`
        `wrlLogFuncExit "AutoOsDetection()"`

}

#	Description : Display it to std output
#	input : it takes 5 parameters , 1 : pattern to display 2: eveluation pass/fail 3 : Result in the output 4: Actual Result collected from the system 5: Expected Result
#	output : None
myprintf(){
	# it should contains five parameters, as below:
	# printf "$patt" "Property" "Result"  "Found" 'Expected'
	if [ "$xmlResult" = "True" ]; then
		if [ "$2" != "$Msg_Property" -a "$2" != "========" ]; then
			xmlStr=`echo "$2 ~ $3 ~ $4 ~ $5"`
			generateXmlReport "$xmlStr"
		fi
	fi
	echo "$5" | tr "," "\n" > $TMP_DIR/pppppp_temp
	ww=`wc -l $TMP_DIR/pppppp_temp | awk '{print $1}'`
	if [ "$ww" = "1" ]; then
		f="not"
	else
		f="true"
	fi
	while read tt
	do
		if [ "$f" = "true" ]; then
			printf "$1" "$2" "$3" "\"$4\"" " \"$tt\""
			f="false"
		elif [ "$f" = "false" ]; then
			printf "$1" " " " " " " "$tt"
		else
			ates=`echo "$4" | sed -n '/[ ]\{1,\}/p'`
			if [ -z "$ates" ]; then
				printf "$1" "$2" "$3" "$4" "$tt" 
			else
				printf "$1" "$2" "$3" "\"$4\"" "\"$tt\"" 
			fi
		fi
	done < $TMP_DIR/pppppp_temp
	rm -f $TMP_DIR/pppppp_temp
}

#	Description : Display it to std output
#	input : it takes 5 parameters , 1 : pattern to display 2: eveluation pass/fail 3 : Result in the output 4: Actual Result collected from the system 5: Expected Result
#	output : None
smyprintf(){

        if [ "$2" = "Connectivity" ]; then
           return
        fi 

        echo "$5" | tr "," "\n" > $TMP_DIR/pppppp_temp
        ww=`wc -l $TMP_DIR/pppppp_temp | awk '{print $1}'`
        if [ "$ww" = "1" ]; then
                f="not"
        else
                f="true"
        fi
        while read tt
        do
              check=`echo $2 |grep "#" | cut -d' ' -f1`
              if [ $check ]; then
    
              echo $check > $TMP_DIR/prs.abc
             else

                if [ "$f" = "true" ]; then
                        printf "$spatt" "$2" "$3" "\"$4\"" " \" $tt\""
                        f="false"
                elif [ "$f" = "false" ]; then
                        printf "$spatt" " " " " " " " $tt"
                else
                        printf "$spatt" "$2" "$3" "$4" " $tt"
                fi
             fi 


        done < $TMP_DIR/pppppp_temp
        rm -f $TMP_DIR/pppppp_temp
}


#	Description : clean tmp files
#	input : none
#	output : None

clean_log(){
	rm -f $TMP_DIR/result.txt 2>/dev/null
	rm -f $TMP_DIR/$plog 2>/dev/null
        rm -f $TMP_DIR/$ptrace 2>/dev/null
	rm -f $TMP_DIR/$pdbg 2>/dev/null
	rm -rf $TMP_DIR 2>/dev/null
}

#       Description : Generate XML Header 
#       input : none
#       output : None
Create_XML_Header() {

	`wrlLogFuncStart "Create_XML_Header()"`

	echo "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> " > $TMP_DIR/result.xml

	echo "<!--  " >> $TMP_DIR/result.xml
	echo "$Indent Begin Standard Header  " >> $TMP_DIR/result.xml
	echo "  " >> $TMP_DIR/result.xml
	echo "$Indent Licensed Materials - Property of IBM  " >> $TMP_DIR/result.xml
	echo "$Indent (C) Copyright IBM Corp. 2009, 2011  " >> $TMP_DIR/result.xml
	echo "$Indent All Rights Reserved. US Government Users Restricted Rights - Use, duplication or  " >> $TMP_DIR/result.xml
	echo "$Indent disclosure restricted by GSA ADP Schedule Contract with IBM Corp.  " >> $TMP_DIR/result.xml
	echo "$Indent   " >> $TMP_DIR/result.xml
	echo "$Indent End Standard Header  " >> $TMP_DIR/result.xml
	echo "-->  " >> $TMP_DIR/result.xml


	echo "<Results schemaVersion=\"1.0\"" >> $TMP_DIR/result.xml
	echo "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" >> $TMP_DIR/result.xml
	echo "xsi:noNamespaceSchemaLocation=\"PRSResults.xsd\"" >> $TMP_DIR/result.xml
	echo ">" >> $TMP_DIR/result.xml
	`wrlLogFuncExit "Create_XML_Header()"`
}

#       Description : Display system information on console
#       input : String
#       output : None
ElementAsItIs() {
	`wrlLogFuncStart "ElementAsItIs()"`
	`wrlDebugFuncParam "String" "$1"`
	
	bufStr="$1"
	if [ "$xmlResult" = "True" ]; then
		echo "$1" >> $TMP_DIR/result.xml
	fi
	`wrlLogFuncExit "ElementAsItIs()"`
}
#	Description : Display system information on console
#	input : none
#	output : None
Display_PRS_Info() {

	`wrlLogFuncStart "Display_PRS_Info()"`
		
	ElementAsItIs "$Indent <PRSInfo>"

	echo "$PRS_Str"
	generateXmlReport "$Indent $Indent PRSName : $PRS_Str"
	#printmessage "$PRS_Version"
	echo "     $Msg_Version: $PRS_Version"
	#generateXmlReport "$Indent PRS$PRS_Version"
	generateXmlReport "             PRSVersion: $PRS_Version"
	echo "     $Msg_Build  : $PREREQ_BUILD"
	generateXmlReport "$Indent $Indent PRSBuild  : $PREREQ_BUILD"
	echo "     $Msg_OSName: $os_name"
	outputdir=`dirname $TMP_DIR`
	generateXmlReport "$Indent $Indent PRSOutputDir : $outputdir"
	generateXmlReport "$Indent $Indent PRSResultXmlFile : $outputdir/result.xml"

	ElementAsItIs "$Indent </PRSInfo>"
	`wrlLogFuncExit "Display_PRS_Info()"`
}

#       Description : generates the XML results for PRS inforamtion
#       input : String
#       output : None
Create_PRS_info_Section() {
	`wrlLogFuncStart "Create_PRS_info_Section()"`
	`wrlDebugFuncParam "String" "$1"`
	
	bufStr="$1"
	FirstStr=`echo $bufStr | sed 's/ //g' | cut -d":" -f1 | sed -e 's/^ *//g;s/ *$//g'`
	SecStr=`echo $bufStr | cut -d":" -f2 | sed -e 's/^ *//g;s/ *$//g'`

	echo "$Indent $Indent <$FirstStr>$SecStr</$FirstStr>" >> $TMP_DIR/result.xml
	`wrlLogFuncExit "Create_PRS_info_Section()"`
}

#       Description : generates the XML results for User inforamtion
#       input : String
#       output : None
Create_User_info_Section() {
	`wrlLogFuncStart "Create_User_info_Section()"`
	`wrlDebugFuncParam "String" "$1"`
	
	bufStr="$1"
	FirstStr=`echo $bufStr | sed 's/ //g' | cut -d":" -f1 | sed -e 's/^ *//g;s/ *$//g'`
	SecStr=`echo $bufStr | cut -d":" -f2 | sed -e 's/^ *//g;s/ *$//g'`

	echo "$Indent $Indent <$FirstStr>$SecStr</$FirstStr>" >> $TMP_DIR/result.xml
	`wrlLogFuncExit "Create_User_info_Section()"`
}

#       Description : generates the XML results for machine inforamtion
#       input : String
#       output : None
Create_machine_info_Section() {

	`wrlLogFuncStart "Create_machine_info_Section()"`
	`wrlDebugFuncParam "String" "$1"`
	
    bufStr="$1"
	FirstStr=`echo $bufStr | sed 's/ //g' | cut -d":" -f1 | sed -e 's/^ *//g;s/ *$//g'`
	SecStr=`echo $bufStr | cut -d":" -f2 | sed -e 's/^ *//g;s/ *$//g'`

	echo "$Indent $Indent <$FirstStr>$SecStr</$FirstStr>" >> $TMP_DIR/result.xml
	`wrlLogFuncExit "Create_machine_info_Section()"`
}

#       Description : generates the XML results for Product inforamtion
#       input : String
#       output : None
Create_Product_info_Section() {

	`wrlLogFuncStart "Create_Product_info_Section()"`
	`wrlDebugFuncParam "String" "$1"`
	
        bufStr="$1"
        FirstStr=`echo $bufStr | cut -d":" -f1 | sed -e 's/^ *//g;s/ *$//g'`
        SecStr=`echo $bufStr   | cut -d":" -f2 | sed -e 's/^ *//g;s/ *$//g'`
	ThirdStr=`echo $bufStr | cut -d":" -f3 | sed -e 's/^ *//g;s/ *$//g'`

	echo "$Indent <ProductElement> " >> $TMP_DIR/result.xml
	echo "$Indent $Indent <ProductCode>$FirstStr</ProductCode> " >> $TMP_DIR/result.xml
	echo "$Indent $Indent <ProductName>$SecStr</ProductName> " >> $TMP_DIR/result.xml
	echo "$Indent $Indent <ProductVersion>$ThirdStr</ProductVersion> " >> $TMP_DIR/result.xml
	echo "$Indent </ProductElement> " >> $TMP_DIR/result.xml
	`wrlLogFuncExit "Create_Product_info_Section()"`
}

#       Description : generates the XML results for detailed results inforamtion
#       input : String
#       output : None
Create_detailed_result_Section() {

	`wrlLogFuncStart "Create_detailed_result_Section()"`
	`wrlDebugFuncParam "String" "$1"`
	
        bufStr="$1"
        FirstStr=`echo $bufStr | sed 's/&/ and /g' | cut -d"~" -f1 | sed -e 's/^ *//g;s/ *$//g'`
        SecStr=`echo $bufStr   | cut -d"~" -f2 | sed -e 's/^ *//g;s/ *$//g'`
        ThirdStr=`echo $bufStr |  cut -d"~" -f3 | sed -e 's/^ *//g;s/ *$//g'`
        FourthStr=`echo $bufStr | cut -d"~" -f4 | sed -e 's/^ *//g;s/ *$//g'`

        echo "$Indent $Indent <ResultElement> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <PropertyName>$FirstStr</PropertyName> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <Result>$SecStr</Result> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <Found>$ThirdStr</Found> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <Expected>$FourthStr</Expected> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent </ResultElement> " >> $TMP_DIR/result.xml
        `wrlLogFuncExit "Create_detailed_result_Section()"`
}

#       Description : generates the XML results for Overall results inforamtion
#       input : String
#       output : None
Create_Overall_Result_Section() {

	`wrlLogFuncStart "Create_Overall_Result_Section()"`
	`wrlDebugFuncParam "String" "$1"`

        bufStr="$1"
        FirstStr=`echo $bufStr | cut -d"~" -f1 | sed -e 's/^ *//g;s/ *$//g'`
        SecStr=`echo $bufStr   | cut -d"~" -f2 | sed -e 's/^ *//g;s/ *$//g'`
        ThirdStr=`echo $bufStr | cut -d"~" -f3 | sed -e 's/^ *//g;s/ *$//g'`
        FourthStr=`echo $bufStr | cut -d"~" -f4 | sed -e 's/^ *//g;s/ *$//g'`

        echo "$Indent $Indent <ResultElement> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <PropertyName>$FirstStr</PropertyName> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <Result>$SecStr</Result> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <Found>$ThirdStr</Found> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent $Indent <Expected>$FourthStr</Expected> " >> $TMP_DIR/result.xml
        echo "$Indent $Indent </ResultElement> " >> $TMP_DIR/result.xml
        `wrlLogFuncExit "Create_Overall_Result_Section()"`
}


#	Description : generates the XML results
#	input : String
#	output : None
generateXmlReport() {

	`wrlLogFuncStart "generateXmlReport()"`
	`wrlDebugFuncParam "String" "$1"`
	
	str=$1
	if [ "$xmlResult" = "True" ]; then
		if [ "$xmlFlag" = "$XML_Header" ]; then
			Create_XML_Header
		fi
		if [ "$xmlFlag" = "$PRS_Info" ]; then
			Create_PRS_info_Section "$str"
		fi
		if [ "$xmlFlag" = "$Machine_Info" ]; then
			Create_machine_info_Section "$str"
		fi
		if [ "$xmlFlag" = "$User_Info" ]; then
			Create_User_info_Section "$str"
		fi
		if [ "$xmlFlag" = "$Product_Info" ]; then
			Create_Product_info_Section "$str"
		fi
		if [ "$xmlFlag" = "$Detailed_Result" ]; then
			Create_detailed_result_Section "$str"
		fi
		if [ "$xmlFlag" = "$Overall_Result" ]; then
			Create_Overall_Result_Section "$str"
		fi
		if [ "$xmlFlag" = "$XML_Footer" ]; then
			echo "XML_Footer"
		fi
	fi
	
	`wrlLogFuncExit "generateXmlReport()"`
}
#	Description : generates the code version and codename 
#	input : file name 
#	output : None
GetCodenameAndVersion() {

	`wrlLogFuncStart "GetCodenameAndVersion()"`
	`wrlDebugFuncParam "String" "$1"`
		
	if [ "$xmlResult" = "True" ]; then
		#Display Product info only for "XML" report generations
		xmlFlag=$Product_Info
		ElementAsItIs "    <ProductInfo>"
		file=$1
		while read cf
		do 
			baserst=`basename $cf`
			code=`echo $baserst | sed "s/\(.*\)_.*/\1/g"`
			version=`echo $baserst | sed "s/.*_\(.*\)/\1/g"`
			codename=$code

			 numCodename=`ls $PREREQ_HOME/*codename.cfg 2>/dev/null | wc -l`
			if [ $numCodename -ge 1 ]; then
				codename=`cat *codename.cfg | grep "$code=" | sed "s/$code=//g" | tr -d "\015"`
			else
			 codename=$code
			fi

			generateXmlReport "$code : $codename : $version"
		done < $file
		ElementAsItIs "$Indent </ProductInfo>"
	fi
	`wrlLogFuncExit "GetCodenameAndVersion()"`
}
#       Description : Check the /tmp disk space before executing PRS
#       input :  None
#       output : None
Check_tmp_DiskSpace() {
        type=`uname`
        if [ "$type" = "Linux" ]; then
                res=` df -k /tmp | tail -n 1`
                len=`echo $res | wc -w`
                len1=`expr $len - 2`
                avail=`echo $res | cut -d ' ' -f$len1`
        fi
        if [ "$type" = "SunOS" ]; then
                avail=`df -k /tmp | awk '{ if(NF<7 && NF>1){ print $(4) } }'`
        fi
        if [ "$type" = "AIX" ]; then
                avail=`df -k /tmp | sed -n 2p | awk '{print $3}'`
        fi
        if [ "$type" = "HP-UX" ]; then
                avail=`df -k /tmp | sed -n 2p | awk '{print $1}'`
        fi
        tmpSize=`expr $avail / 1024`
        if [ $tmpSize -le 10 ]; then
                echo ""
                sleep 1
		printmessage "$Msg_NOSPACE" $product
                echo ""
                exit 2
        fi
}
PRS_Usage() {
        echo ""
        echo "$Msg_USAGE ./prereq_checker.sh \"<Product Code>  [product version],  <Product Code>  [product version]...\" [PATH=<Agent install path>]  [detail]  [-p <Product Code>.SECTION.NAME=VALUE pairs] [outputDir=\"<PRS output dir path>\"] [xmlResult]"
        echo ""
        echo "$Msg_EXAMPLE ./prereq_checker.sh \"KLZ,KUD 06200000\" detail PATH=/opt/ibm/itm -p SERVER=IP.PIPE://mytems:1918,LCM.ad=/opt outputDir=\"/tmp/PRS/\" xmlResult"
	clean_log
	exit 2
}
####################################################
# Start of main logic
# If no parameter, show usage



if [ $# -eq 0 ]; then
	PRS_Usage
fi

rep="result1.txt"
if [ "`uname`" = "AIX" ];then
	if [[ "$TERM" = "" ]]; then
		TERM=vt100
		export TERM
	fi
fi
LC_ALL=C
export LC_ALL
#pd=$1
product_version=`echo $1 | sed 's/, /,/g'`
shift

if [ $# -ne 0 ]; then
        CMD_LINE_ARGS=$1
        shift
        while [ $# -ne 0 ]
        do
                CMD_LINE_ARGS="$CMD_LINE_ARGS $1"
                shift
        done
fi



spath=""
flag=""
deliverPara=""
xmlResult="False"
xmlFlag=0
debugFlag="False"
traceFlag="False"

Default_outputDir=`pwd`
PRS_OUTPUT_DIR=""

# RTC story 20889
#   Defined new parameters PRS_OUTPUT_DIR and TMP_DIR to handle the "outputDir" directory as command line input
#   If "outputDir" is not passed as a command line input then 
#   output dir will be default to PRS installation dir
#   Supports "outputDir" in both capital and non capital letters


for ARGS in $CMD_LINE_ARGS
do
        if [ ! "`echo $ARGS | sed -n '/[o|O][u|U][t|T][p|P][u|U][t|T][d|D][i|I][r|R].*/p'`" = ""  ]; then
		PRS_OUTPUT_DIR=`echo $ARGS | awk -F= '{print $1}'`
		outputdir=`echo $ARGS | awk -F= '{print $2}'`
        fi
        if [ "$ARGS" = "xmlResult" ]; then
		xmlResult="True"
        fi
        if [ "$ARGS" = "debug" ]; then
		debugFlag="True"
        fi
        if [ "$ARGS" = "trace" ]; then
		traceFlag="True"
	fi

        if [ "$pflag" = "TRUE" ]; then
                deliverPara=$ARGS
                pflag="FALSE"
        fi
        if [ ! "`echo $ARGS | sed -n '/-[p|P]/p'`" = "" ]; then
                pflag="TRUE"
        elif [ ! "`echo $ARGS | sed -n '/[p|P][A|a][t|T][h|H]=.*/p'`" = "" ]; then
                spath=`echo $ARGS | sed 's/[p|P][A|a][t|T][h|H]=//'`
        elif [ ! "`echo $ARGS | sed -n '/[D|d]etail/p'`" = "" ]; then
                flag=$ARGS
        fi
done

if [ ! "$PRS_OUTPUT_DIR" = ""  -a  "$outputdir" = "" ]; then
	printmessage "$Msg_FIELD_EMPTY" $product
	PRS_Usage
fi

# Create the temorary directory "/temp" within the "outputDir" 
# New "/temp" location is updated in "TMP_DIR" and this "TMP_DIR" variable is available thoughout the execution of the PRS

if [ ! "$PRS_OUTPUT_DIR" = "" ]; then
        FirstChar=`echo $outputdir | cut -c 1`
        if [ "$FirstChar" = "." ]; then
                TMP_DIR=$PREREQ_HOME/$outputdir/temp
        elif [ "$FirstChar" != "." -a "$FirstChar" != "/" ]; then
                TMP_DIR=$PREREQ_HOME/$outputdir/temp
        else
                TMP_DIR=$outputdir/temp
        fi
        outputDir=`dirname $TMP_DIR`
	printmessage "$Msg_OUTPUTDIR" $outputDir
        echo ""
else
        TMP_DIR=$Default_outputDir/temp
fi


export TMP_DIR
ptrace="/prs.trc"
pdbg="/prs.debug"
ptrace="/prs.trc"
. $PREREQ_HOME/lib/common_function.sh

if [ ! -d $TMP_DIR ]; then
        mkdir -p $TMP_DIR > /dev/null 2>&1
        if [ ! -d $TMP_DIR  ]; then
                outputDir=`dirname $TMP_DIR`
		printmessage "$Msg_CREATEFILE" $outputDir
		PRS_Usage
        fi
fi

# Copy all the source code to $TMP_DIR

DatenTime=`date +%Y%m%d_%H%M%S`
tempDir="prs_$DatenTime"

mkdir /tmp/$tempDir > /dev/null 2>&1
if [  ! -d /tmp/$tempDir ]; then
	printmessage "$Msg_CREATETMPFILE" $tempDir
	PRS_Usage
fi

# Check the "/tmp" disk space before copying PRS into "/tmp" Directory 
Check_tmp_DiskSpace

cp -r $PREREQ_HOME/prereq_checker.sh /tmp/$tempDir 2>/dev/null
cp -r $PREREQ_HOME/PRSResults.xsd /tmp/$tempDir 2>/dev/null
cp -r $PREREQ_HOME/lib /tmp/$tempDir 2>/dev/null
cp -r $PREREQ_HOME/UNIX_Linux /tmp/$tempDir 2>/dev/null
cp -r $PREREQ_HOME/*codename.cfg /tmp/$tempDir 2>/dev/null
#remove the existing result.txt and result.xml files
outputDir=`dirname $TMP_DIR`
rm $outputDir/result.txt $outputDir/result.xml 2>/dev/null

PREREQ_HOME=/tmp/$tempDir
export PREREQ_HOME
chmod -R 777  $PREREQ_HOME/*
xmlFlag=$XML_Header
generateXmlReport 
xmlFlag=$PRS_Info
Display_PRS_Info


echo "$PRS_Str" >> $TMP_DIR/$rep
	echo "     $Msg_Version: $PRS_Version" >> $TMP_DIR/$rep
	echo "     $Msg_Build  : $PREREQ_BUILD" >> $TMP_DIR/$rep
	echo "     $Msg_OSName: $os_name" >> $TMP_DIR/$rep

if [ "$os_name" = "SunOS" ]; then
echo "   $Msg_UserName: `who am i | cut -d ' ' -f1`" >> $TMP_DIR/$rep
else
echo "   $Msg_UserName: `whoami`" >> $TMP_DIR/$rep
fi

echo " "
echo " " >> $TMP_DIR/$rep

xmlFlag=$Machine_Info
ElementAsItIs "    <MachineInfo>"
echo " $Msg_MachineInfo"
echo " $Msg_MachineInfo" >> $TMP_DIR/$rep
echo " $Msg_MachineName: `hostname`"
generateXmlReport "$Indent Machine Name: `hostname`"
echo " $Msg_MachineName: `hostname`" >> $TMP_DIR/$rep

if [ "$os_name" = "SunOS" ]; then
	echo " $Msg_MachineSNo: $HostID"
	generateXmlReport "$Indent Machine Serial Number: $HostID"
	echo " $Msg_MachineSNo: $HostID" >> $TMP_DIR/$rep
elif [ "$os_name" = "AIX" ]; then
	ser=`lscfg -vp | grep "Machine/Cabinet Serial No" | sed -n 1p | cut -d'.' -f4`
	echo " $Msg_MachineSNo: $ser"
	generateXmlReport "$Indent Machine Serial Number: $ser"
	echo " $Msg_MachineSNo: $ser" >> $TMP_DIR/$rep
else
	dmi_check=`which dmidecode 2>/dev/null`
	if [ "$dmi_check" != "" ]; then
	   dmi_check_first=`which dmidecode | cut -d " " -f1`  
	   if [ "$dmi_check_first" = "no" ]; then
		if [ "$os_name" = "HP-UX" ]; then
			echo " $Msg_MachineSNo: `getconf MACHINE_SERIAL`"
			generateXmlReport "$Indent Machine Serial Number: `getconf MACHINE_SERIAL`"
			echo " $Msg_MachineSNo: `getconf MACHINE_SERIAL`" >> $TMP_DIR/$rep
		fi
	    else
		echo " $Msg_MachineSNo: `dmidecode 2>/dev/null | grep "Serial Number" | sed -n 1p | cut -d ':' -f2`"
		generateXmlReport "$Indent Machine Serial Number: `dmidecode 2>/dev/null | grep "Serial Number" | sed -n 1p | cut -d ':' -f2`"
		echo " $Msg_MachineSNo: `dmidecode 2>/dev/null | grep "Serial Number" | sed -n 1p | cut -d ':' -f2`" >> $TMP_DIR/$rep	
	    fi
	fi
fi
echo " "
echo " " >> $TMP_DIR/$rep
generateXmlReport "$Indent $Indent MachineOSName: $os_name"
ElementAsItIs "$Indent </MachineInfo>"

#Display user info only for "XML" report generations
xmlFlag=$User_Info
ElementAsItIs "    <UserInfo>"
userid=`id | cut -d " " -f1 | cut -d "(" -f2 | cut -d ")" -f1`
generateXmlReport "User Name: $userid"
ElementAsItIs "    </UserInfo>"



PREREQ_TRACE=$traceFlag
export PREREQ_TRACE
PREREQ_DEBUG=$debugFlag
export PREREQ_DEBUG

if [ "$traceFlag" = "True" ]; then
	echo "IBM Tivoli Prerequisite Scanner" >> $ptrace
	echo "     $Msg_Version: $PRS_Version" >> $ptrace
	echo "     $Msg_Build  : $PREREQ_BUILD" >> $ptrace
	echo "     $Msg_OSName: $os_name" >> $ptrace
fi

if [ "$debugFlag" = "True" ]; then
	echo "IBM Tivoli Prerequisite Scanner" >> $pdbg
	echo "     $Msg_Version: $PRS_Version" >> $pdbg
	echo "     $Msg_Build  : $PREREQ_BUILD" >> $pdbg
	echo "     $Msg_OSName: $os_name" >> $pdbg

fi

`wrlLogFuncStart "main()"`

##########STARTING OF AUTO OS DETECTION CODE ##################
`wrlDebugFunc "==== Step 1: Detecting OS..."`
Os_detected_output=`$PREREQ_HOME/lib/auto_os_detect.sh | wc -l`
kernel_bit="Kernel="
Os_detected=""
if [ $Os_detected_output -eq 2 ]; then
        kernel_bit=`$PREREQ_HOME/lib/auto_os_detect.sh | sed -n 2p`
        Os_detected=`$PREREQ_HOME/lib/auto_os_detect.sh | sed -n 1p | cut -d "=" -f2 | sed 's/(/{/g' | sed 's/)/}/g'`
else
        Os_detected=`$PREREQ_HOME/lib/auto_os_detect.sh | cut -d "=" -f2 | sed 's/(/{/g' | sed 's/)/}/g'`
fi
 `wrlDebugFunc "OS Detected: $Os_detected"`
pd=""
warn="True"
productCount=0
productCode=""
product_version=`echo $product_version | sed 's/ /+/g'`
 `wrlDebugFunc "product_version: $product_version"`
for Arg in `echo $product_version | tr "," "\n"`
do
        product_iversion=`echo $Arg | sed 's/+/ /g' | cut -d" " -f1`
        pd1=`AutoOsDetection "$Arg" "$Os_detected" "$kernel_bit"`
        pd2=`AutoOsDetection "$product_iversion" "$Os_detected" "$kernel_bit"`
        #####Checking for the Correct Code ######
        IstheCodeWrong=`echo $pd1 | grep -i "not" | cut -d " " -f1`
        IstheOsMatched=`echo $pd1 | grep -i "doesn't" | cut -d " " -f1`
        if [ $IstheOsMatched ]; then
                echo $pd1
                warn="False"
                exit
        else
                #echo "TPS detected : $Os_detected "
                echo ""
        fi

        if [ "$pd1" = "" -o "$pd2" = "" ]; then
                warn="False"
                productCode=$Arg
                ProdCode=$ProdCode,$product_iversion
        fi

        if [ $IstheCodeWrong ]; then
                echo $pd1
                warn="False"
        else
                #echo "Using the $pd1 config file"
                pd=$pd1","$pd
        fi
        productCount=`expr $productCount + 1`
done
##########ENDING OF AUTO OS DETECTION CODE ##################


`wrlDebugFunc "====== Step 2: Handle Params"`



plog="precheck.log"

if [ "$spath" = "" ]; then
	wrlDebugFunc "You have not specify path paramter. It will use default value /opt/IBM/ITM"
	spath="/opt/IBM/ITM"
fi

if [ "$flag" = "" ]; then
	wrlDebugFunc "hint: You can specify detail parameter to get detail information on screen."
fi


if [ ! "$deliverPara" = "" ]; then
	wrlDebugFunc "You will pass the parameters to the sub-script: "$deliverPara
fi

# step 1. specifiedan for the products codes
CFGHOME=$PREREQ_HOME/UNIX_Linux
execommon="common.sh"
rescommon="localhost_hw.txt"
rep=result2.txt
patt="%-40s%-16s%-43s%-20s\n"
spatt="%-36s%-10s%-39s%-20s\n"
tfe=tf_epds
tfi=tf_ipds
tft=tf_tipds
exei=exeinfo
tempd=$TMP_DIR
TMEMO="Memory"
TDISK="Disk"

ls -1 "$CFGHOME"/*.cfg | sed "s;${CFGHOME};\.;" | tr -d "\.\/" | awk -F"_" '{if(length($1)>=3){print $1}}' | sort | uniq > $TMP_DIR/$tfe
echo $pd | tr "[a-z]" "[A-Z]" |  sed 's/\s{2,}/ /g' | sed 's/, /,/g' |  tr "," "\n"  | sort | uniq  > $TMP_DIR/$tfi
cat $TMP_DIR/$tfi | awk '{print $1}' > $TMP_DIR/$tft

#	variable aaaa is for detail mode
aaaa=`sort $TMP_DIR/$tfe $TMP_DIR/$tft $TMP_DIR/$tfe | uniq -u | tr "\n" " "`
if [ ! "$aaaa" = "" ]; then
	wrlw "some check will be ignored by no configuration files found: [ "$aaaa"]"
fi

join $TMP_DIR/$tfe $TMP_DIR/$tfi > $TMP_DIR/$tft 

aaaa=`wc -l $TMP_DIR/$tft | awk '{print $1;}'`

if [ "$warn" = "False" ]; then
	wrlw "ERROR: One of the product Code is not valid. Please check the product codes and try again."

	ProdCode=`echo $ProdCode | sed 's/,//' | cut -d" " -f1`
	printmessage "$Msg_NOTVALIDPRD" $ProdCode
	PRS_Usage
	exit 2
fi
if [ "$aaaa" = "0" ]; then
        if [ "$warn" = "True" ]; then
		wrlw "ERROR: No Valid Product Code found. Please check the product codes and try again."
		printmessage "$Msg_NOTVALIDPRDTRYAGAIN" 
		PRS_Usage
		exit 2
        else
		exit
        fi
fi
cora=">"
`wrlDebugFunc "====== Step 3: Reading product config file and creating product shell script"`

#	packagetest.sh -> takes the config file and path of the config home
#		for each line os.space and create a sh file with the same name of the config file except .cfg is replace with .sh 
#		and this .sh file invokes the script for each script 
#		ex: os.space is called with corrousponng poaramters and logged into ".sh" file for further running it. 
#			user will be able to open the ".sh" file to see the enteries
while read pp vv
do
	cfgfile=`ls -1 "$CFGHOME"/"$pp"_*$vv.cfg 2>/dev/null | sort -ir | sed -n "1p" `
	if [ -z "$cfgfile" ]; then
		cfgfile=`ls -1 "$CFGHOME"/"$pp"_*.cfg 2>/dev/null | sort -ir | sed -n "1p" `
		wrlw "WARNING: Can not Find the specified version: $vv for $pp. Use $cfgfile instead"
		printmessage "$Msg_VERNOTFOUND" $vv $pp
		PRS_Usage
	fi
          
         #echo "Using config file - $cfgfile for $pp"
         `wrlDebugFunc "Calling packageTest.sh"`

       chmod +x $CFGHOME/packageTest.sh
	tmpCfgfile=`basename $cfgfile`
       $CFGHOME/packageTest.sh $CFGHOME $tmpCfgfile 1>/dev/null

       
         
      #  ./$CFGHOME/packageTest.sh $CFGHOME $cfgfile 1>/dev/null
	exefile=`echo $tmpCfgfile | sed 's/\.cfg/\.sh/g'`
	if [ ! -f "$CFGHOME/$exefile" ]; then
	        exefile=$execommon
	fi

	cfgfile=`echo $cfgfile | tr "\n" " "`
	eval "echo $pp $cfgfile $exefile $cora $TMP_DIR/$exei" 
	cora=">>"

	wrlDebugFunc $pp" will read: "$cfgfile" and will execute: "$exefile
done < $TMP_DIR/$tft
	# remove the temp files
#clean_temp

# step 2. execute the commonds

`wrlDebugFunc "====== Step 4: Executing product shell scripts that collect information about checks"`
cat $TMP_DIR/$exei | awk '{print $3}' | sort | uniq > $TMP_DIR/$tfe
echo $execommon > $TMP_DIR/$tft
sort $TMP_DIR/$tfe $TMP_DIR/$tft $TMP_DIR/$tft | uniq -u >> $TMP_DIR/$tft

ttsc=`wc -l $TMP_DIR/$tft | awk '{print $1}'`

wrlDebugFunc "Calling Scripts: [$ttsc scripts will be called]"
i=1
while read execommand
do
	wrlDebugFunc "    Calling: $execommand [$i of $ttsc]"
	if [ "$execommand" = "$execommon" ]; then
		eval "cd $CFGHOME;chmod +x $execommand;./$execommand $spath $deliverPara"
		cd $tempd
		crc=commonchange
		cat $TMP_DIR/$rescommon | sort | uniq > $TMP_DIR/$crc	2>/dev/null
		
		# if there should parse cpu Name
                iscpuname=`cat $TMP_DIR/$crc | grep "CPU Name"`
                if [ "$iscpuname" != "" ]; then
                        eh=`cat $TMP_DIR/$crc | grep "CPU Name" | grep -i "intel"`
                        cu=`cat $TMP_DIR/$crc | grep "CPU Name" | sed 's/.*\s\{1,\}\([0-9\.,]\{1,\}\s*[g|G|m|M]\)[h|H][z|Z].*/\1/g'`
                        if [ -n "$eh" ]; then
                                echo intel.cpu=$cu"Hz" >> $TMP_DIR/$crc
                        else
                                echo risc.cpu=$cu"Hz" >> $TMP_DIR/$crc
                        fi
                fi

		# filter the xlC
		case `uname` in 
			AIX)
				echo "xlC lib version="`lslpp -L xlC.rte | grep xlC.rte | awk '{print $2}'` >> $TMP_DIR/$crc
			;;
		esac
		

		mv $TMP_DIR/$crc $TMP_DIR/$rescommon

		# execute the common plugins
		# parse out the parameters
		fdeliver=""
		for fde in `echo $deliverPara | tr "," "\n"`
		do
			fones="$fde"
			for exipd in `cat $TMP_DIR/$exei | awk '{print $1}'`
			do 
				if [ -n "$fones" ]; then
					fones=`echo $fones | grep -v "$exipd"`
				else
					break
				fi
			done

			if [ -n "$fones" ]; then
				if [ -n "$fdeliver" ]; then
					fdeliver="$fdeliver,$fones"
				else
					fdeliver="$fones"
				fi
			fi
		done

		# execute plugs
		#Connectivity Check,Parsing the Input Variables and getting the Config file for check
                #Parsing the Input Paratameters
		if [ "`uname`" = "SunOS" ];then
			
			IP1=`echo $pd | awk '{print $1}'`
			IP2=`echo $pd | awk '{print $2}'` 
			CheckVal1=`echo $IP1 | grep ","`
			if [ $CheckVal1 ]; then
				IP1=`echo $IP1 | sed 's/.$//g'`
			fi
			Checkval2=`echo $IP2 | grep ","`
			if [ $Checkval2 ]; then
				IP2=`echo $IP2 | sed 's/.$//g'`
			fi
			cfgfile=`ls "$CFGHOME"/"$IP1"_"$IP2"*.cfg 2>/dev/null | sed "s/"$CFGHOME"\///g" 2>/dev/null | sort -ir | sed -n "1p" 2>/dev/null | cut -d "/" -f2`
			if [ -f "$CFGHOME/$cfgfile" ]; then
				check=`cat $CFGHOME/$cfgfile | grep "Connectivity" | cut -d '=' -f2` 2>/dev/null
			fi
                        if [ "$check" = "True" ]; then
                                $CFGHOME/connectivity_plug.sh $spath $fdeliver >> $TMP_DIR/$rescommon 2>> $TMP_DIR/$plog
                        fi

		else
 
			if [ -f "$cfgfile" ]; then
                	check=`cat $cfgfile | grep "Connectivity" | cut -d '=' -f2`
                	fi
 
			if [ "$check" = "True" ]; then
				$CFGHOME/connectivity_plug.sh $spath $fdeliver >> $TMP_DIR/$rescommon 2>>$TMP_DIR/$plog
                	fi	
		fi

                cd $PREREQ_HOME/lib
		for exeplug in `ls *plug*.sh 2>/dev/null`
		do
			eval "chmod +x $exeplug; ./$exeplug $spath $fdeliver >> $TMP_DIR/$rescommon  2>>$TMP_DIR/$plog"
		done
		
	else
		orstfile=`echo $execommand | sed 's/\.sh/\.txt/g'`
		cp -f $TMP_DIR/$rescommon $TMP_DIR/$orstfile
		# filter the parameters
		fdeliver=""
		fpd=`echo $execommand | awk -F_ '{print $1;}'`
		for fde in `echo $deliverPara | tr "," "\n"`
		do
			jjj=`echo $fde | grep $fpd`
			if [ "$fdeliver" = "" ]; then
				fdeliver="`echo $fde | grep $fpd | awk -F. '{print $2;}'`"
			elif [ "$jjj" != "" ]; then
				fdeliver="$fdeliver,""`echo $fde | grep $fpd | awk -F. '{print $2;}'`"
			fi
		done
		eval "cd $CFGHOME;chmod +x $execommand;$CFGHOME/$execommand $spath $fdeliver >> $TMP_DIR/$orstfile 2>>/dev/null" 
	fi

	cd ..
	i=`expr $i + 1`
done < $TMP_DIR/$tft

wrlDebugFunc "End call commands"

	# remove the temp files
rm -f $TMP_DIR/$tfe $TMP_DIR/$tft 2>/dev/null

# write the common plugs information to result.txt
# here is the port connection
cat $TMP_DIR/$rescommon | sed -n '/[C|c]onnectivity/p' > $TMP_DIR/$rep
cat $TMP_DIR/$rescommon | sed -n '/OS Patch/p' >> $TMP_DIR/$rep

# step 3. analysis and generate reports
`wrlDebugFunc "====== Step 5: Comparing/evaluating the information collected for each check with the values in config file and generating reports"`
cat $TMP_DIR/$exei | awk '{print $2}' | cut -d. -f1 > $TMP_DIR/$tfi
aaaa=`echo $flag | grep -i "detail" | wc -l | sed 's/[ ]*//g'` 
if [ "$aaaa" != "0" ]; then
	cat $TMP_DIR/$rescommon | sed -n '/[C|c]onnectivity/p' | sed 's/[ ]\{8,8\}/    /g'
	cat $TMP_DIR/$rescommon | sed -n '/OS Patch/p'
fi


GetCodenameAndVersion "$TMP_DIR/$tfi"

xmlFlag=$Detailed_Result
ElementAsItIs "    <DetailedResults>"

head=">>"
	total_memory=0
	total_disk=0
exec 4<&0 0< $TMP_DIR/$tfi
while read cf
do
	baserst=`basename $cf`
	rstfile=`ls "$tempd"/"$baserst"*.txt 2>/dev/null | sort -ir | sed -n "1p"`
	if [ -z "$rstfile" ]; then
		rstfile=$TMP_DIR/$rescommon
	fi

	# deal with the rstfile

	# begin to compare
	
	wrlDebugFunc "Deal with $cf"

	# write head of report 
	#code=`echo $cf | sed "s/\(.*\)_.*/\1/g"`
	#version=`echo $cf | sed "s/.*_\(.*\)/\1/g"`

	code=`echo $baserst | sed "s/\(.*\)_.*/\1/g"`
	version=`echo $baserst | sed "s/.*_\(.*\)/\1/g"`
        codename=$code

         numCodename=`ls $PREREQ_HOME/*codename.cfg 2>/dev/null | wc -l`
	if [ $numCodename -ge 1 ]; then 
		codename=`cat *codename.cfg | grep "$code=" | sed "s/$code=//g" | tr -d "\015"`
        else
       		 codename=$code
	fi

if [ "$codename" = "" ]; then
codename=`cat product.cfg | grep "$code=" | sed "s/$code=//g" | sed 's/^M//g'`
fi

	# # deal with the upgrate or fresh
	# copy and filter the items from $CFGHOME to $tempd
	nfsstatus_check=`NFScheck $spath`
        if [ "$nfsstatus_check" = "TRUE" ]; then
        cinfopath=`find "$spath" -name cinfo 2>/dev/null`
	fresh="yes"
	lcode=`echo $code | sed 's/K//g'`
	if [ -n "$cinfopath" ]; then
		isfre=`$cinfopath -i | grep -i "^$lcode"`
		if [ -n "$isfre" ]; then
			fresh="no"
		fi
	fi
         #else
         #       echo "NFS_Server problem for $spath"
         #       exit
        fi

	tmpcfg=`basename $cf`
	if [ "$fresh" = "yes" ]; then
		cat $cf.cfg | sed '/FRESH/d' | sed 's/UPGRADE\.//g' > $tempd/$tmpcfg.cfg
	else
		cat $cf.cfg | sed '/UPGRADE/d' | sed 's/FRESH\.//g' > $tempd/$tmpcfg.cfg
	fi

	ElementAsItIs "	<DetailedProductResultsElement>"
	ElementAsItIs "		<ProductCode>$code</ProductCode>"

	eval "echo "$code - $codename" "[$version]:" $head $TMP_DIR/$rep"
			if [ ! "$aaaa" = "0" ]; then
				echo "$code - $codename [version $version]:"
			fi
	head=">>"

	echo ""
        ####This is for Displaying Valid Checks,Based on the,Based on the Id's#####
        NofValues=`cat $rstfile | grep -c "NOT_REQ_CHECK_ID"`
        LineNo=1
        if [ $NofValues -gt 0 ]; then
                while [ $LineNo -le $NofValues ]
                do
                        CommentValue=`cat $rstfile | grep NOT_REQ_CHECK_ID | cut -d "=" -f1 | sed -n "$LineNo"p`
                        if [ $CommentValue ]; then
                                        sed "s/"$CommentValue"/#"$CommentValue"/g" $cf.cfg >> $TMP_DIR/test
                                        mv $TMP_DIR/test $cf.cfg
                        fi
                        LineNo=`expr $LineNo + 1`
                done
        fi
	###End of this Section###	

	subhead="TRUE"

	OLDIFS=$IFS
	IFS==
	exec 4<&0 0< $tempd/$tmpcfg.cfg
	while read item value
	do
                
                # `wrlDebugFunc "Evaluating $item..."`
                r="$Msg_FAIL"
                xlCFlag="False"
		IFS=$OLDIFS
		value=`echo $value | tr -d "\015"`
                #This is to Make the result.txt More Readable for Connectivity test
               	Val1=`echo $item | grep "Connectivity"`
                if [ $Val1 ]; then
                        break
                fi

		# skip to compare xlC lib version if not AIX
		if [ "$item" = "xlC lib version" ]; then
			case `uname` in
				AIX)
                                   res=0
                                        rstvalue=`cat $rstfile | grep  "xlC" | cut -d= -f2`
                                        tmpArg=`versionCompare $value $rstvalue`
                                         if [ $tmpArg -eq -1 -o $tmpArg -eq 0 ]; then
                                                res=1
                                        fi
                                         if [ $res -eq 1 ]; then
                                                 r="$Msg_PASS"
                                                 else
                                                 r="$Msg_FAIL"
                                         fi
                                        xlCFlag="True"
				;;
				*)
					IFS==
					continue
				;;
			esac
		fi

		if [ "$item" = "$TMEMO" ]; then
			total_memory=`AddMG $value $total_memory`
		elif [ "$item" = "$TDISK" ]; then
			total_disk=`AddMG $value $total_disk`
		fi

                ###Checking for Os.ulimit####
                IsUlimitFound=`echo $item | grep "ulimit"`
                if [ $IsUlimitFound ]; then
                      if [ "$value" != "Available" ]; then
                        limit=`echo $value | cut -d "[" -f2 | cut -d "]" -f1 | cut -d ":" -f2`
                        item=$item.$limit
                        fi
                fi

		rstvalue=`cat $rstfile | grep -w  "$item" | cut -d= -f2`
	        #echo "rstvalue=$rstvalue"
               #lenrstvalue=`echo $rstvalue | wc -w`
                  lenrstvalue=`cat $rstfile | grep -c "$item"`
                   if [ $lenrstvalue -gt 1 ]; then
         	      rstvalue=`echo $rstvalue | awk '{print $1}'`
                   fi

                      if [ -n "$rstvalue" ]; then
			# compare, it's the compare between $value and $rstvalue
			# $value is from configuration file
			# $rstvalue is from result file
			# if there exists user defined comparison file, use it
			# else use the compare function
			
			ridcode=`echo $code | sed "s/ /_/g"`
			riditem=`echo $item | sed "s/ /_/g"`

			#comparefile=`ls "$CFGHOME"/"$ridcode"_*$riditem*_compare* 2>/dev/null  | sed "s/"$CFGHOME"\///g" | sort -ir | sed -n "1p"` 
			comparefile=`ls "$CFGHOME"/"$ridcode"_*$riditem*_compare* 2>/dev/null | sed "s;${CFGHOME};\.;" | sed 's/^..//' | sort -ir | sed -n "1p"` 
			if [ -z "$comparefile" ]; then
				comparefile=`ls "$CFGHOME"/$riditem*_compare* 2>/dev/null  | sed "s;${CFGHOME};\.;" | sed 's/^..//' | sort -ir | sed -n "1p"` 
			fi
                        #echo $comparefile		
	
			if [ -n "$comparefile" ]; then
				wrlDebugFunc "    [$comparefile] used as comparison for [$item] of [$codename]"
				eval "cd $CFGHOME;chmod +x \"$comparefile\";"
				#./$comparefile "$rstvalue" "$value"
			
                                PackageName=""
                                ISPackageFound=`echo $item | grep "os.package"`
                                if [ "$ISPackageFound" != "" ]; then
                                    PackageName=`echo $item | cut -d'.' -f3-`
                                    #echo $PackageName
                                 fi
                                

                          	r=`$PREREQ_HOME/UNIX_Linux/$comparefile "$rstvalue" "$value" "$PackageName" | grep "$Msg_PASS"`	
				if [ -z "$r" ]; then
					r="$Msg_FAIL"
				fi
				cd ..
			else
			         if [ "$rstvalue" = "NFS_NOT_AVAILABLE" -o "$rstvalue" = "NOT_A_VALID_PATH" ]; then
                                 r="$Msg_FAIL"
                                else
                                  if [ $xlCFlag = "False" ]; then
                                  `wrlDebugFunc "    [compare()] used as comparison for [$item] of [$codename]"`
                        	r=`compare "$rstvalue" "$value"`
                                   fi 
		                fi
                	fi

			wrlDebugFunc "    [$r] $item expect $value while get $rstvalue"
			if [ "$subhead" = "TRUE" ]; then
				myprintf "$patt" "$Msg_Property" "$Msg_RESULT_STR"  "$Msg_Found" "$Msg_Expected " >> $TMP_DIR/$rep
				myprintf "$patt" "========" "======"  "=====" '========= ' >> $TMP_DIR/$rep
				if [ ! "$aaaa" = "0" ]; then
					smyprintf "$patt" "$Msg_Property" "$Msg_RESULT_STR"  "$Msg_Found" "$Msg_Expected"
					smyprintf "$patt" "========" "======"  "=====" '========'
				fi
				subhead="FALSE"
			fi
				mord=`echo "$item" | awk '{if($1 ~  /Memory/){print $1}if($1 ~ /Disk/){print $1}}'`
				if [ "$mord" = "" ]; then
                                        ###Checking for Ulimit values####
                                         IsUlimitFound=`echo $item | grep "ulimit"`
                                         if [ $IsUlimitFound ]; then
                                                item=`echo "$item" | cut -d "." -f1-2`
                                         fi
                                   	myprintf "$patt" "$item" "$r" "$rstvalue" "$value" >> $TMP_DIR/$rep
				else
					myprintf "$patt" "$item" "$r" `changeMG "$rstvalue"` `changeMG "$value"` >> $TMP_DIR/$rep
				fi
			if [ ! "$aaaa" = "0" ]; then
				mord=`echo "$item" | awk '{if($1 ~  /Memory/){print $1}if($1 ~ /Disk/){print $1}}'`
				if [ "$mord" = "" ]; then
					smyprintf "$patt" "$item" "$r" "$rstvalue" "$value" 
				else
					smyprintf "$patt" "$item" "$r" `changeMG "$rstvalue"` `changeMG "$value"`
				fi
			fi
		else
			ss=`echo $item | grep -i "cpu"`
			if [ -z "$ss" ];then
				wrlw "it's empty for "$item
				mord=`echo "$item" | awk '{if($1 ~  /Memory/){print $1}if($1 ~ /Disk/){print $1}}'`
				if [ "$mord" = "" ]; then
				 check=`echo $item | cut -c 1`     
                               #if [  "$check" != "#" ]; then
                               #         myprintf "$patt" "$item" "$Msg_FAIL" "[Not Found]" "$value" >> $TMP_DIR/$rep
                               # fi
  
                                if [ "$item" != "Connectivity" ]; then
                                     if [  "$check" != "#" ]; then
                                      	myprintf "$patt" "$item" "$Msg_FAIL" "[Not Found]" "$value" >> $TMP_DIR/$rep
	                            fi
                                fi	
         		else
					myprintf "$patt" "$item" "$Msg_FAIL" "[Not Found]" `changeMG "$value"` >> $TMP_DIR/$rep
				fi
				if [ ! "$aaaa" = "0" ]; then
					mord=`echo "$item" | awk '{if($1 ~  /Memory/){print $1}if($1 ~ /Disk/){print $1}}'`
					if [ "$mord" = "" ]; then
						smyprintf "$patt" "$item" "$Msg_FAIL" "[Not Found]" "$value"
					else
						smyprintf "$patt" "$item" "$Msg_FAIL" "[Not Found]" `changeMG "$value"`
					fi
				fi 
			fi
		fi
		IFS== 
	done
	exec 0<&4
	
	IFS=$OLDIFS 
	printf "\n" >> $TMP_DIR/$rep
			if [ ! "$aaaa" = "0" ]; then
				printf "\n"
			fi
	ElementAsItIs "	</DetailedProductResultsElement>"
done
exec 0<&4
#For BUG 141808

ElementAsItIs "    </DetailedResults>"

# to sort the path
# if the agent want to collect space on another path, it does contains in his product code .txt in temp
totaltf="$tempd/disk_total_temp"
totalsw="$tempd/disk_total_swap"
totalre="$tempd/disk_total_rest"
IP_Fs_Mounts="$tempd/Filesystems_list"
###This is for Adding the Disk value to total according to the Configuration value###
ref_product_version=`echo $product_version | sed 's/ /+/g'`
for Arg in `echo "$ref_product_version" | tr "," " "`
do
        cfgval=`echo $Arg | cut -d "+" -f1`
   #     echo $tempd/$cfgval*.cfg
        #Found=`cat $tempd/$cfgval*.cfg | grep "Disk"`
        #echo $Found
        #if [ $Found ]; then
                echo `mes4Path $spath`" $total_disk""GB" `cat $TMP_DIR/$rescommon | grep $TDISK | cut -d= -f2` > $totaltf
        #fi
done

for ftotal in `echo $deliverPara | tr "," "\n"`
do
	# parse the deliver parameters, it should contains value, key and product code
	pval=`echo $ftotal | awk -F= '{print $2}' | sed -n '/^\//p'`
	if [ -n "$pval" ]; then
		pkey=`echo $ftotal | awk -F= '{print $1}'`
		#pd_pkey=`echo "$pkey" | sed 's/\(^[a-zA-Z0-9]\{3,3\}\)\..*/\1/g'`
                pd_pkey=`echo "$pkey" | tr "." "\n" | sed -n '1p'`
                #nm_pkey=`echo "$pkey" | sed "s/$pd_pkey\.//g"`
                nm_pkey=`echo "$pkey" |  tr "." "\n" | sed -n '2p'`


		# find the result value and total value
		ff_want=`ls $tempd | grep -i "$pd_pkey" | grep -i .cfg`
		pp_want=`cat $tempd/$ff_want | grep "^$nm_pkey" | cut -d= -f2`


		ff_result=`ls $tempd | grep -i "$pd_pkey" | grep -i .txt`
		pp_result=`cat $tempd/$ff_result | grep "^$nm_pkey" | cut -d= -f2`
		echo `mes4Path "$pval"`" $pp_want $pp_result" >> $totaltf
		echo `mes4Path1 "$pval"`" $pp_want $pp_result" >> $IP_Fs_Mounts
	fi 
done
#####This is for Adding the Disk Value for TOTAL SPECIFIED COMPONENTS#######
if [ "`uname`" = "SunOS" ]; then
	if [ -f $tempd/$ff_want ]; then
		pp_want_disk=`cat $tempd/$ff_want | grep "Disk" | cut -d= -f2` 2>/dev/null
		pp_result_disk=`cat $tempd/$ff_result | grep "Disk" | cut -d= -f2` 2>/dev/null
		echo `mes4Path1 "$spath"`" $pp_want_disk $pp_result_disk" >>$IP_Fs_Mounts
	fi
fi
#####For Adding the Os.space values to temp/disk_total_temp file for getting consolidated calculations ####
DirAdd $PREREQ_HOME
####END OF THE SECTION######
sort $totaltf > $totalsw
mv $totalsw $totaltf

# NOT REQ entries are been removed from the totaltf file as the total component shuld not display results RTC Defect 26220
cat  $totaltf | grep -v NOT_REQ_CHECK_ID > $totalsw
mv $totalsw $totaltf


# calculate the disks
if [ -f "$totalre" ]; then
	rm -f $totalre
fi
touch $totalre
for naa in `cat $totaltf | awk '{print $1}' | sort | uniq`
do
	echo $naa > $totalsw
	tttttt="0G"
	for nbb in `join $totaltf $totalsw | awk '{print $2}'`
	do
		tttttt=`AddMG $nbb $tttttt`
	done

	echo "$naa $tttttt""GB "`join $totaltf $totalsw | sed -n '1p' | awk '{print $3}'` >> $totalre
done

# For Zfs Fix 
#======================
istep="1"
numbertotal=`wc -l $totalre | awk '{print $1}'`

while [ $istep -le $numbertotal ]
do
	rstdisk=`cat $totalre | sed -n "$istep""p" | awk '{print $3}'`
	total_disk=`cat $totalre | sed -n "$istep""p" | awk '{print $2}'`
	echo "$rstdisk $total_disk" >> $TMP_DIR/disk_sac1 
	istep=`expr $istep + 1`
done

istep="1"
numbertotal=`wc -l $totalre | awk '{print $1}'`
check_zpool=False

xmlFlag=$Overall_Result

while [ $istep -le $numbertotal ]
do
        rstdisk=`cat $totalre | sed -n "$istep""p" | awk '{print $3}'`
        total_disk=`cat $totalre | sed -n "$istep""p" | awk '{print $2}'`
        totalpat=`cat $totalre | sed -n "$istep""p" | awk '{print $1}'`

if [ "`uname`" = "SunOS" ]; then
	zfslist="$tempd/Zfs_filesystems"

	#For fixing the issue with "zpool"command in Solaris 10 and above
	solaris_version=`cat /etc/release |grep Solaris | nawk -F " " '{print $2}'`
        
	if [ $solaris_version -ge 10 ]; then
                #zpool status >/dev/null
		ZpoolcmdFound=`which zpool 2>/dev/null | cut -d " " -f1`
		if [ $ZpoolcmdFound != "no" ]; then
                        check_zpool=`zpool status | grep state | cut -d':' -f2 | uniq`
        
			#Getting the Zpool Variable & Getting the Zfs filesystems

        		Zpools=`zpool list | awk '{print $1}'| sed '1d'`
        		Zfs_mounts=`zfs list | awk '{print $5}' | sed '1d' | sed '/-/d'`
        		for line in $Zfs_mounts 
        		do
                		FirstChar=`echo $line | cut -c 1`
                		if [ "$FirstChar" = "/" ]; then
                        		zfs list $line | sed '1d' | nawk '{print $1}' >> $zfslist 2>/dev/null
                		fi
        		done
		fi
	fi
fi
 
 if [ "$check_zpool" = " ONLINE" ]; then

        check_istep=1
        fs=`df -k "$totalpat" | sed '1d' | awk '{print $1}'`
        Validate_zfs=`grep "$fs" $zfslist | sed -n '1p' | cut -c 1-3`
        if [ $Validate_zfs ]; then
		fs1=`echo $fs | nawk -F "/" '{print $1}'`
		Total="0G"
			touch $TMP_DIR/Sorted_IP_Fs_Mounts
			for Zpool in $Zpools 
			do
				if [ -s $IP_Fs_Mounts ]; then	
					grep -i "$Zpool" $IP_Fs_Mounts | sed 's/'$Zpool'\//'$Zpool' \//g' | sort  > $TMP_DIR/Sorted_IP_Fs_Mounts
				fi
				echo $fs1 > $TMP_DIR/temp3
                               	for Fsize in `join $TMP_DIR/Sorted_IP_Fs_Mounts $TMP_DIR/temp3 | sed 's/'$Zpool' \//'$Zpool'\//g' | awk '{print $2}'`
                               	do
                                	Total=`AddMG $Fsize $Total`
                               	done
			done
                                total_disk=$Total"GB"
				check_istep=`expr $check_istep + 1`

        else
                	for Fsystem in $fs 
                	do
                        	if [ -s $IP_Fs_Mounts ]; then	
					cat $IP_Fs_Mounts | sort > $TMP_DIR/Sorted_IP_Fs_Mounts
				fi
                        	Total="0G"
                        	echo $Fsystem > $TMP_DIR/temp5
                        	for Fsize in `join $TMP_DIR/Sorted_IP_Fs_Mounts $TMP_DIR/temp5 | awk '{print $2}'`
                        	do
                                	Total=`AddMG $Fsize $Total`
                        	done
                                	total_disk=$Total"GB"
                	done

        fi

 fi
        ifTotalDiskZero=`changeMG $total_disk`
        if [ "$ifTotalDiskZero" = "0MB" -o "$ifTotalDiskZero" = "0M" ]; then
                ifTotalDiskZero="N\/A"
		TotalComp=$True
	else
		TotalComp=$False
        fi
	if [ $TotalComp -eq $False ]; then
		# Print Only Once 
		if [ $LoopCount -eq 0 ]; then 
			# deal the total memory and disk
			ElementAsItIs "    <AggregatedResults>"
			printf "$Msg_AllSpecifiedComponent\n" >> $TMP_DIR/$rep
			if [ ! "$aaaa" = "0" ]; then
				printf "$Msg_AllSpecifiedComponent\n"
			fi
			myprintf "$patt" "$Msg_Property" "$Msg_RESULT_STR"  "$Msg_Found" "$Msg_Expected">> $TMP_DIR/$rep
			myprintf "$patt" "========" "======"  "=====" '========'>> $TMP_DIR/$rep
			if [ ! "$aaaa" = "0" ]; then
				smyprintf "$patt" "$Msg_Property" "$Msg_RESULT_STR"  "$Msg_Found" "$Msg_Expected"
				smyprintf "$patt" "========" "======"  "=====" '========'
			fi
		fi
		if [ ! "$aaaa" = "0" ]; then
		    if [ "$rstdisk" = "NotAvailable" ]; then
			smyprintf "$patt" "$totalpat" "$Msg_FAIL" `changeMG $rstdisk` $ifTotalDiskZero
		    else
			smyprintf "$patt" "$totalpat" "`compare $rstdisk $total_disk`"  `changeMG $rstdisk` $ifTotalDiskZero
		    fi
		fi
		LoopCount=1
		myprintf "$patt" "$totalpat" "`compare $rstdisk $total_disk`"  `changeMG $rstdisk` $ifTotalDiskZero >> $TMP_DIR/$rep
	fi
        istep=`expr $istep + 1`
        rm -rf $tempd/Zfs_filesystems
done
# For 141808
####For Bug 159072 ###
###Displaying the results based on the Memory Values###
IsZero=`changeMG $total_memory`
if [ "$IsZero" != "0" ]; then
	rstmemory=`cat $TMP_DIR/$rescommon | grep $TMEMO | cut -d= -f2`
	rstmemory1=`echo $rstmemory | cut -d ' ' -f1`

	if [ $LoopCount -eq 0 ]; then
         # deal the total memory and disk
         ElementAsItIs "    <AggregatedResults>"
         printf "$Msg_AllSpecifiedComponent\n" >> $TMP_DIR/$rep
         if [ ! "$aaaa" = "0" ]; then
                printf "$Msg_AllSpecifiedComponent\n"
         fi
         myprintf "$patt" "$Msg_Property" "$Msg_RESULT_STR"  "$Msg_Found" "$Msg_Expected">> $TMP_DIR/$rep
         myprintf "$patt" "========" "======"  "=====" '========'>> $TMP_DIR/$rep
         if [ ! "$aaaa" = "0" ]; then
                smyprintf "$patt" "$Msg_Property" "$Msg_RESULT_STR"  "$Msg_Found" "$Msg_Expected"
                smyprintf "$patt" "========" "======"  "=====" '========'
         fi
                LoopCount=1
    fi

	myprintf "$patt" "$TMEMO" "`compare $rstmemory1 $total_memory"GB"`" `changeMG $rstmemory` `changeMG $total_memory"GB"` >> $TMP_DIR/$rep
			if [ ! "$aaaa" = "0" ]; then
				smyprintf "$patt" "$TMEMO" "`compare $rstmemory1 $total_memory"GB"`"  `changeMG $rstmemory` `changeMG $total_memory"GB"`
			fi
fi
if [ $LoopCount -eq 1 ]; then
	ElementAsItIs "    </AggregatedResults>"
fi
return_code=0
if [ "`cat $TMP_DIR/$rep | grep "$Msg_FAIL" | grep -v "$Msg_PASS"`" = "" ]; then
	if [ ! "$aaaa" = "0" ]; then
		echo
		ElementAsItIs "    <OverallResult>$Msg_PASS</OverallResult>"
		echo "$Msg_OverallResult:   $Msg_PASS"
		echo "$Msg_OverallResult:   $Msg_PASS" >> $TMP_DIR/$rep
	else
		ElementAsItIs "    <OverallResult>$Msg_PASS</OverallResult>"
		echo "$Msg_OverallResult:   $Msg_PASS" >> $TMP_DIR/$rep
		echo "$Msg_PASS"
	fi
else
	if [ ! "$aaaa" = "0" ]; then
		echo
		ElementAsItIs "    <OverallResult>$Msg_FAIL</OverallResult>"
		echo "$Msg_OverallResult:   $Msg_FAIL"
		echo "$Msg_OverallResult:   $Msg_FAIL" >> $TMP_DIR/$rep
	else
		echo "$Msg_OverallResult:   $Msg_FAIL" >> $TMP_DIR/$rep
		ElementAsItIs "    <OverallResult>$Msg_FAIL</OverallResult>"
		echo "$Msg_FAIL"
	fi
    return_code=1
fi

ElementAsItIs "</Results>"
`wrlDebugFunc "====== Step 6: Cleanup"`

# clean the temp files and folder
ls $PREREQ_HOME/UNIX_Linux/*-Master > $TMP_DIR/master 2>/dev/null

for masterFile in `cat $TMP_DIR/master`
do
  origFile=`echo $masterFile | sed 's/-Master//g'`
  rm -f $origFile
  mv $masterFile $origFile
done 
cat $TMP_DIR/result1.txt $TMP_DIR/result2.txt >> $TMP_DIR/result.txt

clean_temp_Directory

`wrlLogFuncExit "main()"`
if [ $return_code -eq 0 ]; then
exit 0
else
exit 1
fi

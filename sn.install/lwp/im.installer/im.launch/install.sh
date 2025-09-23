# *****************************************************************
#
# IBM Licensed Material
#
# Copyright IBM Corp. 2010, 2016                                    
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#
# *****************************************************************

os="$(uname)"
platform=""
if [ "$os" == "AIX" ]; then
	platform="aix"
fi
if [ "$os" == "Linux" ]; then
	if [ "$(uname -i)" == "s390x" ]; then
		platform="zlinux"
	else
		platform="linux"
	fi
fi

if [ "" == $platform ]; then
	echo "Could not determine operating system"
else
	cd $platform
	./install
fi

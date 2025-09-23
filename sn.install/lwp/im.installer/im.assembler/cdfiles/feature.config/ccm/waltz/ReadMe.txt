Please follow this instruction to setup Waltz for existing deployment of FileNet
This tool will do three things:
1. Check on FileNet server whether VMM and Waltz have been enabled;
2. If enabled will create the J2C alias;
3. Copy directory.services.xml, directory.services.xsd, sonata.services.xml, sonata.services.xsd into FileNet Server.


Windows
1. Unzip apache-ant-1.8.2-bin.zip into your local folder, e.g. C:\waltz.
2. Put all the rest of the files into the same folder.
3. Input the properties in build.properties.
4. In command line please setup below environment variables, see example:
	set JAVA_HOME=C:\Program Files (x86)\IBM\WebSphere\AppServer\java
	set ANT_HOME=C:\apache-ant-1.8.2
	set PATH=%PATH%;C:\apache-ant-1.8.2\bin
5. Use this command to verify whether your ANT has been setup correctly.
	ant -version
	The correct output would be: Apache Ant(TM) version 1.8.2 compiled on December 20 2010
6. In the same command line, go into the folder that hosts the files, e.g. C:\waltz, please run "ant waltz-config".


Unix
1. Unzip apache-ant-1.8.2-bin.tar.gz into your local folder, e.g. /opt/waltz.
2. Put all the rest of the files into the same folder.
3. Input the properties in build.properties.
4. In command line please setup below environment variables, see example:
	export JAVA_HOME=/opt/IBM/WebSphere/AppServer/java
	export ANT_HOME=/opt/apache-ant-1.8.2
	export PATH=$PATH:$ANT_HOME/bin
5. Use this command to verify whether your ANT has been setup correctly.
	ant -version
	The correct output would be: Apache Ant(TM) version 1.8.2 compiled on December 20 2010
6. In the same command line, go into the folder that hosts the files, e.g. /opt/waltz, please run "ant waltz-config".
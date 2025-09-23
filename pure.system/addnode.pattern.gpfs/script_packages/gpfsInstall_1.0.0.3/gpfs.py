#
#*===================================================================
#*
# Licensed Materials - Property of IBM  
# "Restricted Materials of IBM"
# 5725-G32, 5725-F46  Copyright IBM Corp., 2013, 2013
# All Rights Reserved * Licensed Materials - Property of IBM
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
#*
#*===================================================================
#

import sys
import os
import time
import subprocess
import string
import re
import logging
import datetime
import platform
import fcntl
from os import path

clientHostName=""
clientIP=""
existingKeyDir=""
sshPath=""
removeKey=""
removeKeyString="this is ok to remove"
defaultKeyString="root@ipas-lpar-037-100"
keySubDir=""
managerHost=""
backupDir=""
newDir=""
initialDevice="none"
linuxInitialDevice="/dev/sdb"
aixInitialDevice="hdisk1"
enableIPV6="false"
operationName=None
blockSize="1024K"
tscTcpPort="1191"
mmsdrservPort="1191"
tscCmdPortRangeLowNumber="30000"
tscCmdPortRangeHighNumber="30020"
baseServer1=""
baseServer2=""
baseServer3=""
repServer1=""
repServer2=""
repServer3=""
tieServer=""
device=""
nsdName=""
linkDir=""
fileSetName=""
clientHost=""
remoteServerHost=""
minQuotaSize=""
maxQuotaSize=""
mountPoint=""
driveLetter=""
configDir="/tmp/gpfs/"
defaultFileSystem="gpfsdev"
# need to consider drive letter
logger=logging.getLogger('gpfs.py')


class GPFSLock:

    def __init__(self, filename):
        self.filename = filename
        # This will create it if it does not exist already
        self.handle = open(filename, 'w')

    # Bitwise OR fcntl.LOCK_NB if you need a non-blocking lock
    def acquire(self):
        fcntl.flock(self.handle, fcntl.LOCK_EX)

    def release(self):
        fcntl.flock(self.handle, fcntl.LOCK_UN)

    def __del__(self):
        self.handle.close()




def verifyRacksSVM(baseServer1,repServer1,tieServer):
    if baseServer1 !="":
        ipCheck("baseServer1",baseServer1,initialDevice)
    if repServer1 !="":
        ipCheck("repServer1",repServer1,initialDevice)
    if tieServer !="":
        ipCheck("tieServer",tieServer,initialDevice)        


#checks initial disk is attached and verifies connection
def verifyRacks(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer):
    if baseServer1 !="":
        ipCheck("baseServer1",baseServer1,initialDevice)
    if baseServer2 !="":
        ipCheck("baseServer2",baseServer2,initialDevice)
    if baseServer3 !="":
        ipCheck("baseServer3",baseServer3,initialDevice)
    if repServer1 !="":
        ipCheck("repServer1",repServer1,initialDevice)
    if repServer2 !="":
        ipCheck("repServer2",repServer2,initialDevice)
    if repServer3 !="":
        ipCheck("repServer3",repServer3,initialDevice)        
    if tieServer !="":
        ipCheck("tieServer",tieServer,initialDevice)        


def silentCheckClientNodeIsUp(clientIP):
    checkCount=0
    maxCount=5
    clientNodeActive=True
    
    while checkCount !=maxCount:
    
            try:
                answer=checkNodeStatus(clientIP)
            except:
                print(("Caught expection checking node "+clientIP))
                clientNodeActive=False

            if answer !=0:
                 print(("Status not active for node: "+clientIP))
                 clientNodeActive=False
            else:
                return 0
            if clientNodeActive==False:
                time.sleep(120)
                
            else:
                return 0
            checkCount=checkCount+1
    # final check             
    if allNodesActive==False:
       raise Exception("The client node:  "+clientIP+ " is not in active state and work can not continue")    
       
       
       
       
def silentCheckAllClusterNodesAreUp():
    nodeList =constructClusterMemberList()
    print(str(nodeList))
    nodeListLen=len(nodeList)
    maxCount=5
    checkCount=0
    allNodesActive=True

    while checkCount !=maxCount:
        allNodesActive=True
        start=0
        while start !=nodeListLen:
            answer=""
            node=""
            try:
                node=nodeList[start]
                print(("node is: "+node))
                answer=checkNodeStatus(node)
                print("past check node status")
            except Exception as e:
                print(("exception is:  "+str(e)))
                print(("Caught expection checking node "+node))
                allNodesActive=False
                print ("try again")
            if answer !=0:
                 print(("Status not active for node: "+nodeList[start]))
                 allNodesActive=False
            start=start+1


        if allNodesActive==False:
            time.sleep(120)
            
        else:
             return 0
        checkCount=checkCount+1
    # final check
    if allNodesActive==False:
       print ("The nodes of the cluster are not in active state and work can not continue")
       raise Exception("The nodes of the cluster are not in active state and work can not continue")

       

#checks initial disk is attached and verifies connection

def lastQuotaCharCheck(input):
   if re.match('^[gkmtGMKT]+$',input):
       return True
   else:
       return False

def lastBlockSizeCharCheck(input):
   if re.match('^[MK]+$',input):
       return True
   else:
       return False


#. Supported block sizes are 16 KiB, 64 KiB, 128 KiB, 256 KiB, 512 KiB, 1 MiB, 2 MiB, 4 MiB, 8 MiB, and
#16 MiB.

def checkBlockSize(value):
    print(("checking block size value:  "+value)) 
    qvLen=len(value)
    last=value[qvLen-1]
    num=value [0:qvLen-1]
    resNum=checkNumeric(num)
    if resNum ==False:
        raise Exception("Block Size Value: "+value+" is not numeric")
    resLast=lastBlockSizeCharCheck(value[qvLen-1])
    if resLast ==False:
        raise Exception("Block Size Value: "+value+" last character must be K or M")
    else:
        if value[qvLen-1]=="K":
            print ('its a K')
        
            if  num in ["16","64","128","256","512","1024","2048","4096","8192","16384"]:
                print ("Valid block size")
            else:
                raise Exception("Wrong block size, must be 16,64,128,256,512,1024,2048,4096,8192,16384 for K values")    
        if value[qvLen-1] =="M":
            if  num in ["1","2","4","8","16"]:
                print ("Valid block size")
            else:
                raise Exception("Wrong block size, must be 1,2,4,8,16 for M values")            
       
    


def checkQuotaValue(quotaValue):
    qvLen=len(quotaValue)
    if qvLen==1:
        raise Exception("Quota value: "+quotaValue+" is invalid, must have numeric followed by kKgGmMtT")
    last=quotaValue[qvLen-1]
    num=quotaValue [0:-1]
    resNum=checkNumeric(num)
    if resNum ==False:
        raise Exception("Quota Value: "+quotaValue+" is not numeric")
    resLast=lastQuotaCharCheck(quotaValue[qvLen-1])
    if resLast ==False:
        raise Exception("Quota Value: "+quotaValue+" last character must be gkmtGMKT")
    
def checkAlphaNum(input):
   if " " in input:
      raise Exception("No spaces allowed in value:  "+input)
   if checkNumeric(input[0]):
      raise Exception ("The first character of value "+input+" must begin a-z,A-Z")
   if re.match('^[a-zA-Z0-9]+$',input):
       return True
   else:
       return False


def checkAlphaNumPlus(input):
   if " " in input:
      raise Exception("No spaces allowed in value:  "+input)
   if checkNumeric(input[0]):
      raise Exception ("The first character of value "+input+" must begin a-z,A-Z")
   if re.match('^[a-zA-Z0-9-/_\.]+$',input):
       return True
   else:
       return False
   
   
def checkNumeric(input):
   if " " in input:
       raise Exception("No spaces allowed for numeric values, value given is:  "+input)
   if re.match('^[0-9]+$',input):
       return True
   else:
       return False
def checkPortValue(input):
    if checkNumeric(input) == False:
        raise Exception ("Port value:  "+input+ "  must be a numeric value")

    
    
    
def checkAlpha(input):
   if " " in input:
      raise Exception("No spaces allowed in value:  "+input) 
   if re.match('^[a-zA-Z]+$',input):
       return True
   else:
       return False

def ipCheck(serverName,ip):
    try:
        out=runShellQuick("ssh root@"+ip+" ls")
    except:
        print(("ip value for server:  "+serverName+ " ip value: "+ " does not respond."))
        raise Exception("ip value: "+ip+"  for server:  "+serverName+ " ip value: "+ " does not respond.")










def createInitialLinkDir(linkpath):
    
    if linkpath[0]!="/":
        raise Exception("The first character of the link dir must be '/'")    
    
    lt=len(linkpath)
    

    if linkpath[lt-1]=="/":
        raise Exception("The last character of the link dir can not be '/'")


#else:
    lm= linkpath.rfind("/")
    if lm==0:
        return
    basedir=linkpath[0:lm]
    if path.exists(basedir):
         print("subdir exists")
    else:
        
        runShellQuick("mkdir -p "+basedir)



def createKeyBackupDirs():
    if path.exists("/tmp/gpfs/backup"):
        print ("backup dir exists")
    else:
        runShellQuick("mkdir /tmp/gpfs/backup")
        runShellQuick("chmod 600 /tmp/gpfs/backup")
    
    if path.exists("/tmp/gpfs/new"):
        print ("new dir exists")
    else:
        runShellQuick("mkdir /tmp/gpfs/new")
        runShellQuick("chmod 600 /tmp/gpfs/new")

#this should only be run once
# there is a command option to make it run without human input
def sshkeygen(directoryLocation,existingKeyDir):
    #runShellQuick("mkdir "+directoryLocation)
    if existingKeyDir=="":
        runShellQuick("ssh-keygen -t rsa -f "+directoryLocation+"/id_rsa" +" -N ''")
    else:
        runShellQuick("cp "+existingKeyDir+"/id_rsa " +directoryLocation+"/id_rsa")
        runShellQuick("cp "+existingKeyDir+"/id_rsa.pub " +directoryLocation+"/id_rsa.pub")


def cleanAuthorizedKeys(removeKey):
    keyToReplace=getOldKey()
       
    keyToReplaceLen=len(keyToReplace)
    keyToReplace=keyToReplace[0:keyToReplaceLen]
    if removeKey in keyToReplace:
        return 0
    print("removing:  "+keyToReplace)
    f=open(sshPath+"/.ssh/authorized_keys","r")
    lines=f.readlines()
    f.close()
    f1=open(sshPath+"/.ssh/authorized_keys","w")
    for line in lines:
        print (line)
        print((str(len(keyToReplace))))
        if not keyToReplace in line:
            f1.write(line)
        else:
            print("could not find: "+keyToReplace)
    f1.close()   

def copyKeysToRemote(nodeHost,newDir,backupDir):
   runShellQuick("ssh root@"+nodeHost+" mkdir "+backupDir)
   runShellQuick("ssh root@"+nodeHost+" mkdir "+newDir)
   runShellQuick("scp "+newDir+"/id_rsa root@"+nodeHost+":"+newDir+"/id_rsa")
   runShellQuick("scp "+newDir+"/id_rsa.pub root@"+nodeHost+":"+newDir+"/id_rsa.pub")

def backupOldKeys(backupDir):
    runShellQuick("cp "+sshPath+"/.ssh/id_rsa "+backupDir+"/id_rsa")
    runShellQuick("cp "+sshPath+"/.ssh/id_rsa.pub "+backupDir+"/id_rsa.pub")
    runShellQuick("cp "+sshPath+"/.ssh/authorized_keys "+backupDir+"/authorized_keys")



def replaceServerKeys(newKeyDir,backupDir,removeKey):
    
    backupOldKeys(backupDir)
    cleanAuthorizedKeys(removeKey)
    runShellQuick("cp "+ newKeyDir+"/id_rsa "+sshPath+"/.ssh/id_rsa")
    runShellQuick("cp "+ newKeyDir+"/id_rsa.pub "+sshPath+"/.ssh/id_rsa.pub")
    runShellQuick("cat "+sshPath+"/.ssh/id_rsa.pub >>"+sshPath+"/.ssh/authorized_keys")

def getOldKey():
    keyToReplace=""
    f=open(sshPath+"/.ssh/id_rsa.pub","r")
    lines=f.readlines()
    #should only be one line element 2 should be key
    for line in lines:
        keyToReplace=line.split(' ')[2]
    return keyToReplace


def constructClusterMemberList():
        resultsArray=[]
        reachedData="false"
        dataStart=0
        
        p = subprocess.Popen("/usr/lpp/mmfs/bin/mmlscluster", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (out,err) = p.communicate()
        print((str(out)))
        rc = p.returncode
        myout= str(out).split("\n")
        mylen=len(myout)
        print(str(mylen))
   
        for x in range(0,mylen-2):
            if "Node  Daemon node name" in myout[x]:
                print ("reached data")
                reachedData="true"
                dataStart=x+2
            print(("x is: "+str(x)))
            if reachedData=="true" and x >=dataStart:
                print(str(nonEmptyElements(myout[x])[2]))
                result=nonEmptyElements(myout[x])[2]
                resultsArray.append(result)
        print(resultsArray)
        return resultsArray

def runClearKeysOnRemote(nodeHost,newKeyDir,backupDir):
    runShellQuick("ssh root@"+nodeHost+ " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName createKeyBackupDirs")
    copyKeysToRemote(nodeHost,newKeyDir,backupDir)
    runShellQuick("ssh root@"+nodeHost+ " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName replaceServerKeys -newDir "+newKeyDir+" -backupDir "+backupDir+" -removeKey remote")

def changeClusterKeys(keySubDirName,managerHost):
    backupDir="/tmp/gpfs/backup/"+keySubDirName
    newDir="/tmp/gpfs/new/"+keySubDirName
    runShellQuick("mkdir "+newDir)
    checkAllClusterNodesAreUp()
    runShellQuick("mkdir "+backupDir)
    sshkeygen(newDir,existingKeyDir)
    nodeList =constructClusterMemberList()
    print(str(nodeList))
    nodeListLen=len(nodeList)
    start=0
    while start !=nodeListLen:
       if nodeList[start]!=managerHost:
           print(("Changing keys on: "+nodeList[start]))
           runClearKeysOnRemote(nodeList[start],newDir,backupDir)
       start=start+1
    replaceServerKeys(newDir,backupDir,defaultKeyString)
    print ("Replace Server Keys Exit")

def backupOrigKeys():
    if path.exists(sshPath+"/.ssh/orig"):
        print("orig dir exists")
    else:
        runShellQuick("mkdir "+sshPath+"/.ssh/orig")
    runShellQuick("cp "+sshPath+"/.ssh/id_rsa* "+sshPath+"/.ssh/orig/.")
    f=open(sshPath+"/.ssh/config","a")
    f.write ("\n")
    f.write("    IdentityFile "+sshPath+"/.ssh/id_rsa\n")
    f.write("    IdentityFile "+sshPath+"/.ssh/orig/id_rsa\n")

def checkNodeStatus(nodeHost):
    output=runShellQuick("/usr/lpp/mmfs/bin/mmgetstate -N "+nodeHost)
    print (output)
    if "active" in output:
        return 0
    else: return 1

def getActiveNode():
    f=open("/tmp/gpfs/clusterHosts","r")
    lines=f.readlines()
    foundGoodHost="false"
    for line in lines:
        host=line.split(" ")[0]
        try:
            if checkNodeStatus(host)==0:
                foundGoodHost="true"
                return host
        except:
            print(("Received exception on host:  "+str(host)+ " continuing to next host."))    
    if foundGoodHost=="false":
        raise Exception("No quorum nodes in cluster are active")
        

def retrieveCurrentKeysAndSyncClient(mgmtHost):

    runShellQuick ("scp root@"+mgmtHost+":"+sshPath+"/.ssh/id_rsa /tmp/id_rsa")
    runShellQuick ("scp root@"+mgmtHost+":"+sshPath+"/.ssh/id_rsa.pub /tmp/id_rsa.pub")
    cleanAuthorizedKeys("Its a client")
    runShellQuick ("cp /tmp/id_rsa "+sshPath+"/.ssh/id_rsa")
    runShellQuick ("cp /tmp/id_rsa.pub "+sshPath+"/.ssh/id_rsa.pub")   
    runShellQuick("cat "+sshPath+"/.ssh/id_rsa.pub >>"+sshPath+"/.ssh/authorized_keys")
    runShellQuick ("rm /tmp/id_rsa")
    runShellQuick ("rm /tmp/id_rsa.pub")
    
def checkAllClusterNodesAreUp():
    nodeList =constructClusterMemberList()
    print(str(nodeList))
    nodeListLen=len(nodeList)
    start=0
    while start !=nodeListLen:
        answer="none"
        try:
            answer=checkNodeStatus(nodeList[start])
        except:
             raise Exception("Node:  "+nodeList[start]+" is not in active state or may not exist in the cluster, we can not exchange rsa keys at this time")

        if answer !=0:
            raise Exception("Node:  "+nodeList[start]+" is not in active state, we can not exchange rsa keys at this time")
        start=start+1       


def createFileSetRemote(filesetName,minQuotaSize,maxQuotaSize):
   print("createFileSetRemote")
   remoteServerHost = str(getActiveNode())
   createFileSetRemoteCMD="ssh root@" + remoteServerHost + " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName createFileSet -remoteServerHost "+remoteServerHost+ " -fileSetName "+filesetName+ " -minQuotaSize "+minQuotaSize+" -maxQuotaSize "+maxQuotaSize
   runShell(createFileSetRemoteCMD)



def stripIPV6Brackets(ipv6Addr):
    newipv6Addr=ipv6Addr
    if ipv6Addr[0]=="[":
        print ("modify IPV6")
        lenIPV6=len(ipv6Addr) -1
        print (lenIPV6)
        newipv6Addr=ipv6Addr[1:lenIPV6]
    return newipv6Addr

def runIPV6Check(cmd,ipv6AddrInput):
    print(cmd)
    ipv6Addr=stripIPV6Brackets(ipv6AddrInput)
    test=True
    retVal=1
    iter=1
    while test==True:
        
        p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (out,err) = p.communicate()
        rc = p.returncode
        myout= str(out).split("\n")
        mylen=len(myout)
        print(str(mylen))
   
        for x in range(0,mylen-1):
            currentLine=nonEmptyElements(myout[x])
            if len(currentLine) >=3:
                print(str(nonEmptyElements(myout[x])[2]))
                result=areIPV6Equal(nonEmptyElements(myout[x])[2],ipv6Addr)
                if result=="worked":
                    retVal=0
                    return retVal
                else:
                    time.sleep(10)
            
    
        iter=iter+1
        if iter==4:
             test=False
    return retVal

def convert4(inputVal):
   print((inputVal[0]))
   print((inputVal[1]))
   print((inputVal[2]))
   print((inputVal[3]))
   print ("====")
   start=0
   end=4
   if inputVal[0]=="0":
      start=start+1
      if inputVal[1]=="0":
         start=start+1
         if inputVal[2]=="0":
            start=start+1
            if inputVal[3]=="0":
               start=start+1
               return "0"

   return inputVal[start:end]

def findNonZero(ipList,start):
    print(("start is:  "+str(start)))
    ipListLength=len(ipList)
    print(("ipListLength: "+str(ipListLength)))
    y=start

    while y != ipListLength:
        print(("y is:  "+str(y)))
        if ipList[y] !="0000":
           print((ipList[y] +" at position "+str(y)+" is not 0000"))           
           return y
        else:
           print(("no match "+str(ipList[y])))
        y=y+1


def areIPV6Equal(ip1,ip2):
   ip1List=ip1.split(":")
   print(("ip1: "+str(ip1List)))

   ip2List=ip2.split(":")
   print(("ip2: "+str(ip2List)))
   ip1ListLength=len(ip1List)
   print(("1 list len:  "+str(ip1ListLength)))
   ip2count=0
   x=0
   listEnd=ip1ListLength
   while x != listEnd and ip2count !=None :
     print(("x is:  "+str(x)))
     print(("ip2count is: "+str(ip2count)))
     if ip1List[x]==ip2List[ip2count].lower():
        print(("comparing:  "+str(ip1List[x])+" "+str(ip2List[ip2count])))
        print((ip1List[x]+" is equal "+ip2List[ip2count]))
        ip2count=ip2count+1
        x=x+1
     else:
          if len(ip1List[x])!=4:
             if ip1List[x]=="":
                ip2count=findNonZero(ip2List,x+2)
                x=x+1
                print(("ip2count is:  "+str(ip2count)))
                       
             else:
                
                tempString=convert4(ip2List[x])
                print(("tempstring:  "+tempString))
                if tempString.lower()==ip1List[x]:
                   print((str(ip1List[x])+" equal after removing 0s "+str(ip2List[x])))
                   ip2count=ip2count+1
                   x=x+1
                else:
                   return "failed"
          else:
             return "failed"

   return "worked"


def convertIPV6(ipString):
    return "["+ipString.split("/")[0]+"]"

def configClientFirewall(remoteServerHost):
    remoteSCPPortsFileCMD="scp root@"+remoteServerHost+":/tmp/gpfs/gpfsPorts /tmp/gpfs/gpfsPorts"
    runShellQuick(remoteSCPPortsFileCMD)

    with open('/tmp/gpfs/gpfsPorts', 'r') as myfile:
        ports = myfile.read()
    myfile.close()
    #print(ports)
    tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6 = ports.split(",", 4)
    #print tscTcpPort+mmsdrservPort+tscCmdPortRangeLowNumber+tscCmdPortRangeHighNumber+enableIPV6
    openPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6.rstrip())
    time.sleep(20)

def createPortsFile(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):
    print("createPortsFile")
    with open('/tmp/gpfs/gpfsPorts', 'w') as myfile:
        myfile.write(tscTcpPort + ',' + mmsdrservPort + ',' + tscCmdPortRangeLowNumber + ',' + tscCmdPortRangeHighNumber + ',' + enableIPV6)

def firewallRules(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):
    createPortsFile(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
    openPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
    
    remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+baseServer2+":/tmp/gpfs/gpfsPorts"
    runShellQuick(remotePortsFileCMD)
    openPortsRemote(baseServer2,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
    
    remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+baseServer3+":/tmp/gpfs/gpfsPorts"
    runShellQuick(remotePortsFileCMD)
    openPortsRemote(baseServer3,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)

    if repServer1!="":
        remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+repServer1+":/tmp/gpfs/gpfsPorts"
        runShellQuick(remotePortsFileCMD)
        openPortsRemote(repServer1,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
        
        remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+repServer2+":/tmp/gpfs/gpfsPorts"
        runShellQuick(remotePortsFileCMD)
        openPortsRemote(repServer2,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
        
        remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+repServer3+":/tmp/gpfs/gpfsPorts"
        runShellQuick(remotePortsFileCMD)
        openPortsRemote(repServer3,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
        
    if tieServer!="":
        remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+tieServer+":/tmp/gpfs/gpfsPorts"
        runShellQuick(remotePortsFileCMD)
        openPortsRemote(tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)

def firewallRulesSVM(baseServer1,repServer1,tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):
    createPortsFile(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
    openPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)

    if repServer1!="":
        remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+repServer1+":/tmp/gpfs/gpfsPorts"
        runShellQuick(remotePortsFileCMD)
        openPortsRemote(repServer1,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
        
    if tieServer!="":
        remotePortsFileCMD="scp /tmp/gpfs/gpfsPorts root@"+tieServer+":/tmp/gpfs/gpfsPorts"
        runShellQuick(remotePortsFileCMD)     
        openPortsRemote(tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)

def openPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):
    sshPort="22"
    allowedPorts = [sshPort,tscTcpPort,mmsdrservPort]
    numPorts = int(tscCmdPortRangeHighNumber) - int(tscCmdPortRangeLowNumber) + 1
    for i in range(numPorts):
        allowedPorts.append(tscCmdPortRangeLowNumber)
        tscCmdPortRangeLowNumber = str(int(tscCmdPortRangeLowNumber) + 1)
    print(allowedPorts)
    
    totalPortRange = numPorts + 3
    
    if platform.system()=="AIX":
        if enableIPV6 ==  'false':
            for port in range(totalPortRange):
                templateRule1CMD = 'genfilt -v 4 -n 3 -a P -s 0.0.0.0 -m 0.0.0.0 -d 0.0.0.0 -M 0.0.0.0 -g Y -c all -o any -p 0 -O eq -P ' + allowedPorts[port] + ' -r B -w B -l N -f Y -i all'
                runShellQuick(templateRule1CMD)
                templateRule2CMD = 'genfilt -v 4 -n 3 -a P -s 0.0.0.0 -m 0.0.0.0 -d 0.0.0.0 -M 0.0.0.0 -g Y -c all -o eq -p ' + allowedPorts[port] + ' -O any -P 0 -r B -w B -l N -f Y -i all'
                runShellQuick(templateRule2CMD)
        if enableIPV6 == 'true':
            for port in range(totalPortRange):
                templateRule3CMD = 'genfilt -v 6 -n 2 -a P -s :: -m 0 -d :: -M 0 -g Y -c all -o any -p 0 -O eq -P ' + allowedPorts[port] + ' -r B -w I -l N -f Y -i all'
                runShellQuick(templateRule3CMD)
                templateRule4CMD = 'genfilt -v 6 -n 2 -a P -s :: -m 0 -d :: -M 0 -g Y -c all -o eq -p ' + allowedPorts[port] + ' -O any -P 0 -r B -w O -l N -f Y -i all'
                runShellQuick(templateRule4CMD)
        runShellQuick("mkfilt -u")
    
    if platform.system()=="Linux":
        if enableIPV6 ==  'false':
            for port in range(totalPortRange):
                templateRule1CMD = '/sbin/iptables -I INPUT -p tcp -m tcp --dport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule1CMD)
                templateRule2CMD = '/sbin/iptables -I INPUT -p tcp -m tcp --sport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule2CMD)
                templateRule3CMD = '/sbin/iptables -I OUTPUT -p tcp -m tcp --sport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule3CMD)
                templateRule4CMD = '/sbin/iptables -I OUTPUT -p tcp -m tcp --dport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule4CMD)
                
            runShellQuick("/sbin/service iptables save")
            runShellQuick("/sbin/service iptables status")
            runShellQuick("/sbin/service iptables restart")         
                
        if enableIPV6 == 'true':
            for port in range(totalPortRange):
                templateRule5CMD = '/sbin/ip6tables -I INPUT -p tcp -m tcp --dport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule5CMD)
                templateRule6CMD = '/sbin/ip6tables -I INPUT -p tcp -m tcp --sport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule6CMD)
                templateRule7CMD = '/sbin/ip6tables -I OUTPUT -p tcp -m tcp --sport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule7CMD)
                templateRule8CMD = '/sbin/ip6tables -I OUTPUT -p tcp -m tcp --dport ' + allowedPorts[port] + ' -j ACCEPT'
                runShellQuick(templateRule8CMD)
        
            runShellQuick("/sbin/service ip6tables save")
            runShellQuick("/sbin/service ip6tables status")
            runShellQuick("/sbin/service ip6tables restart")
        
def openPortsRemote(remoteServerHost,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):
    print("openPortsRemoteRemote")
    openPortsRemoteCMD="ssh root@" + remoteServerHost + " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName openPorts -remoteServerHost " + remoteServerHost + " -tscTcpPort " + tscTcpPort + " -mmsdrservPort " + mmsdrservPort + " -tscCmdPortRangeLowNumber " + tscCmdPortRangeLowNumber + " -tscCmdPortRangeHighNumber " + tscCmdPortRangeHighNumber + " -enableIPV6 " + enableIPV6
    runShellQuick(openPortsRemoteCMD)
    
def openSSHPort():   
    if platform.system()=="AIX":
        #if enableIPV6 ==  'false':
            runShellQuick('genfilt -v 4 -n 2 -a P -s 0.0.0.0 -m 0.0.0.0 -d 0.0.0.0 -M 0.0.0.0 -g Y -c all -o any -p 0 -O eq -P 22 -r B -w B -l N -f Y -i all')
            runShellQuick('genfilt -v 4 -n 2 -a P -s 0.0.0.0 -m 0.0.0.0 -d 0.0.0.0 -M 0.0.0.0 -g Y -c all -o eq -p 22 -O any -P 0 -r B -w B -l N -f Y -i all')
            runShellQuick('genfilt -v 4 -n 3 -a P -s 0.0.0.0 -m 0.0.0.0 -d 0.0.0.0 -M 0.0.0.0 -g Y -c icmp -o any -p 0 -O any -P 0 -r B -w B -l N -f Y -i all')
            #runShellQuick('genfilt -v 4 -n 3 -a P -s 0.0.0.0 -m 0.0.0.0 -d 0.0.0.0 -M 0.0.0.0 -g Y -c icmp -o any -p 22 -O any -P 0 -r B -w B -l N -f Y -i all')
        #if enableIPV6 == 'true':
        #    runShellQuick('genfilt -v 6 -n 4 -a P -s :: -m 0 -d :: -M 0 -g Y -c all -o any -p 0 -O eq -P 22 -r B -w I -l N -f Y -i all')
        #    runShellQuick('genfilt -v 6 -n 4 -a P -s :: -m 0 -d :: -M 0 -g Y -c all -o eq -p 22 -O any -P 0 -r B -w O -l N -f Y -i all')
        #    runShellQuick('genfilt -v 4 -n 5 -a P -s :: -m 0 -d :: -M 0 -g Y -c icmpv6 -o any -p 0 -O any -P 0 -r B -w B -l N -f Y -i all')
        #    runShellQuick('genfilt -v 4 -n 5 -a P -s :: -m 0 -d :: -M 0 -g Y -c icmpv6 -o any -p 22 -O any -P 0 -r B -w B -l N -f Y -i all')
            
            runShellQuick("mkfilt -u")
    
    if platform.system()=="Linux":
        #if enableIPV6 ==  'false':
            runShellQuick('/sbin/iptables -I INPUT -p tcp -m tcp --dport 22 -j ACCEPT')
            runShellQuick('/sbin/iptables -I INPUT -p tcp -m tcp --sport 22 -j ACCEPT')
            runShellQuick('/sbin/iptables -I OUTPUT -p tcp -m tcp --sport 22 -j ACCEPT')
            runShellQuick('/sbin/iptables -I OUTPUT -p tcp -m tcp --dport 22 -j ACCEPT')
            runShellQuick('/sbin/iptables -I OUTPUT -p icmp -m icmp --icmp-type 8 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT')
            runShellQuick('/sbin/iptables -I INPUT -p icmp -m icmp --icmp-type 0 -m state --state ESTABLISHED,RELATED -j ACCEPT')
            runShellQuick("/sbin/service iptables save")
            runShellQuick("/sbin/service iptables status")
            runShellQuick("/sbin/service iptables restart")
        #if enableIPV6 == 'true':
        #    runShellQuick('/sbin/ip6tables -I INPUT -p tcp -m tcp --dport 22 -j ACCEPT')
        #    runShellQuick('/sbin/ip6tables -I INPUT -p tcp -m tcp --sport 22 -j ACCEPT')
        #    runShellQuick('/sbin/ip6tables -I OUTPUT -p tcp -m tcp --sport 22 -j ACCEPT')
        #    runShellQuick('/sbin/ip6tables -I OUTPUT -p tcp -m tcp --dport 22 -j ACCEPT')
        #    runShellQuick('/sbin/ip6tables -I OUTPUT -p icmp -m icmp --icmp-type 8 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT')
        #    runShellQuick('/sbin/ip6tables -I INPUT -p icmp -m icmp --icmp-type 0 -m state --state ESTABLISHED,RELATED -j ACCEPT')
        #    runShellQuick("/sbin/service ip6tables save")
        #    runShellQuick("/sbin/service ip6tables status")
        #    runShellQuick("/sbin/service ip6tables restart")
     
def verifyDisk(diskString):
    if platform.system()=="AIX":
        diskString = "/dev/" + diskString
        if os.path.exists(diskString):
             print(("Testing for device ... " + diskString))
             return 0
        else:
             print(("Device " + diskString + " not found.  Scanning AIX..."))
             discoverCMD="cfgmgr"
    elif platform.system()=="Linux":
        if os.path.exists(diskString):
             print(("Testing for device ... " + diskString))
             return 0
        else:
             print(("Device " + diskString + " not found.  Scanning Linux..."))
             discoverCMD='find /sys/class/scsi_host/host* -exec sh -c \'echo "- - -" > {}/scan\' \;'
    else:
        print("Unknown OS")
        return 1

    runShellQuick(discoverCMD)

    try:
        with open(diskString) as f: print(("Testing for new device ... " + diskString))
        return 0
    except IOError as e:
        print(("Error: %s not found." % diskString))
        raise


def verifyDiskRemote(diskString,remoteServerHost):
    print("verifyDiskRemote")
    verifyDiskRemoteCMD="ssh root@" + remoteServerHost + " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName verifyDisk -device " + diskString
    runShellQuick(verifyDiskRemoteCMD)

def setGPFSPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber):
    tscTcpPortCmd="/usr/lpp/mmfs/bin/mmchconfig tscTcpPort="+tscTcpPort
    mmsdrservPortCmd="/usr/lpp/mmfs/bin/mmchconfig mmsdrservPort="+mmsdrservPort
    rangeCmd="/usr/lpp/mmfs/bin/mmchconfig tscCmdPortRange="+tscCmdPortRangeLowNumber+"-"+tscCmdPortRangeHighNumber
    runShell(tscTcpPortCmd)
    runShell(mmsdrservPortCmd)
    runShell(rangeCmd)
    
    
def nonEmptyElements(fullString):
    origArray=fullString.split(' ')
    newArray=[]
    endPt=len(origArray)
    for ctr in range(0,endPt):
        if str(origArray[ctr])!='':
             newArray.append(origArray[ctr])
    return newArray

def appendClientToetcHosts(clientIP,clientHostName):
    try:
       lock = GPFSLock("/tmp/gpfs/etchostslock.log")
       print(("try to get lock for /etc/hosts:  "+fileSetName))
       lock.acquire()
       print ("have lock")
       f=open ("/etc/hosts","a")
       f.write(clientIP +" "+clientHostName+"\n")
    finally:
       lock.release()
    
#need remove client from etcHosts    

def removeClientFrometcHosts(removeIP):
    try:
       lock = GPFSLock("/tmp/gpfs/etchostslock.log")
       print(("try to get lock for /etc/hosts:  "+fileSetName))
       print("removing:  "+removeIP)
       removeIP=removeIP.split('\n')[0]
       f=open("/etc/hosts","r")
       lines=f.readlines()
       f.close()
       # backup etc/hosts?
       f1=open("/etc/hosts","w")
       for line in lines:
           print (line)
           if not removeIP in line:
               f1.write(line)
           else:
               print("could not find: "+removeIP)
       f1.close()
    finally:
        lock.release()   
          


def removeClientToQuorumMemberetcHosts(clientIP):
    clientHostName=retrieveClientHost(clientIP)
    hostsFile=open("/tmp/gpfs/clusterHosts","r")
    lines=hostsFile.readlines()
    for line in lines:
        remoteServer=line.split(' ')[0]
        try:
            if checkNodeStatus(remoteServer) !=1:
                runShellQuick("ssh "+remoteServer+" python /tmp/gpfs/gpfsInstall/gpfs.py -operationName removeClientFrometcHosts -clientIP "+clientIP)
        except:
            print(("WARNING the remote host: "+remoteServer+" was not available and the /etc/hosts value for "+clientIP+ " was not added"))


def writeClientToQuorumMemberetcHosts(clientIP):
    clientHostName=retrieveClientHost(clientIP)
    hostsFile=open("/tmp/gpfs/clusterHosts","r")
    lines=hostsFile.readlines()
    for line in lines:
        remoteServer=line.split(' ')[0]
        try:
            if checkNodeStatus(remoteServer) !=1:
                runShellQuick("ssh "+remoteServer+" python /tmp/gpfs/gpfsInstall/gpfs.py -operationName appendClientToetcHosts -clientIP "+clientIP +" -clientHostName "+clientHostName)
        except:
            print(("WARNING the remote host: "+remoteServer+" was not available and the /etc/hosts value for "+clientIP+ " was not removed"))




def retrieveClientHost(clientIP):
    
    out= runShellQuick("ssh "+clientIP+" python /tmp/gpfs/gpfsInstall/getHostName.py "+clientIP)
    return out
       

def createQuorumHostsFile(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer):
    hostsFile=open("/tmp/gpfs/clusterHosts","a")
    if baseServer1 !="":
       out= runShellQuick("ssh root@"+baseServer1+" python /tmp/gpfs/gpfsInstall/getHostName.py "+baseServer1)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(baseServer1+' '+str(out.split('\n')[0])+"\n")
    if baseServer2 !="":
       out= runShellQuick("ssh root@"+baseServer2+" python /tmp/gpfs/gpfsInstall/getHostName.py "+baseServer2)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(baseServer2+' '+str(out.split('\n')[0])+"\n")
    if baseServer3 !="":
       out= runShellQuick("ssh root@"+baseServer3+" python /tmp/gpfs/gpfsInstall/getHostName.py "+baseServer3)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(baseServer3+' '+str(out.split('\n')[0])+"\n")
    
    if repServer1 !="":
       out= runShellQuick("ssh root@"+repServer1+" python /tmp/gpfs/gpfsInstall/getHostName.py "+repServer1)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(repServer1+' '+str(out.split('\n')[0])+"\n")
    if repServer2 !="":
       out= runShellQuick("ssh root@"+repServer2+" python /tmp/gpfs/gpfsInstall/getHostName.py "+repServer2)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(repServer2+' '+str(out.split('\n')[0])+"\n")
    if repServer3 !="":
       out= runShellQuick("ssh root@"+repServer3+" python /tmp/gpfs/gpfsInstall/getHostName.py "+repServer3)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(repServer3+' '+str(out.split('\n')[0])+"\n")

    if tieServer !="":
       out= runShellQuick("ssh root@"+tieServer+" python /tmp/gpfs/gpfsInstall/getHostName.py "+tieServer)
       print(("out is:  "+out.split('\n')[0]))
       hostsFile.write(tieServer+' '+str(out.split('\n')[0])+"\n")



def createHostsfile():    
    runShell("/usr/lpp/mmfs/bin/mmlscluster >>/tmp/gpfs/cluster.txt")
    f =open("/tmp/gpfs/cluster.txt","r")
    for line in f:
        if "quorum" in line:
            appendHost(line)
    
def appendHost(nodeInfo):
    nodeData=nonEmptyElements(nodeInfo)
    if "quorum" in nodeData[4]:
        hostsFile=open("/tmp/gpfs/clusterHosts","a")
        hostsFile.write(str(nodeData[2])+' '+str(nodeData[3])+"\n")

def runShellWithCheck(cmd,check):
    print(cmd)
    test=True
    iter=1
    while test==True:
        print(iter)
        result=0
        logger.info ("runShell->%s" % cmd)
        p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (out,err) = p.communicate()
        rc = p.returncode
        if not check in str(out):
            print("did't find string")
            time.sleep(10)
            result=1
        else:
            print("found check string")
            result=0
            test=False
        iter=iter+1
        # now checks 20 times
        if iter==20:
            test=False


    return result


def runShell(cmd):
        print(cmd)
        logger.info ("runShell->%s" % cmd)
        p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        time.sleep(20)
        (out,err) = p.communicate()
        rc = p.returncode
        print("RC is: "+str(rc))
        print("Message(1) is:  "+str(out))
        print("Message(2) is: "+str(err))
        logger.debug ("\n out: %s \n err: %s \n rc: %s" % (out, err, rc))

        if rc > 0:
            raise Exception("The command: %s, had a return code of %d, not 0" % (str(cmd), rc))

        return out  #This is the stdout from the shell command
    
def runShellQuick(cmd):
        print(cmd)
        logger.info ("runShell->%s" % cmd)
        p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (out,err) = p.communicate()
        rc = p.returncode
        print("RC is: "+str(rc))
        print("Message(1) is:  "+str(out))
        print("Message(2) is: "+str(err))
        logger.debug ("\n out: %s \n err: %s \n rc: %s" % (out, err, rc))

        if rc > 0:
            raise Exception("The command: %s, had a return code of %d, not 0" % (str(cmd), rc))

        return out  #This is the stdout from the shell command


def createNSDFile(fileName,failureGroup,usage,servers,device,nsdName):
    with open(configDir+fileName, "w") as nsdfile:
        nsdfile.write("%nsd:   nsd="+nsdName+"\n")
        nsdfile.write("   usage="+usage+"\n")
        nsdfile.write("   failureGroup="+failureGroup+"\n")
        nsdfile.write("   servers="+servers+"\n")
        nsdfile.write("   device="+device+"\n")


def createNodesFile(fileName,server1,server2,server3):
    print("createNodesFile")
    with open(configDir+fileName, "w") as myfile:
        myfile.write(server1+":quorum-manager\n")
        if server2 != "None":
            myfile.write(server2+":quorum-manager\n")
        if server3 !="None":
            myfile.write(server3+":quorum\n")


def createNodesFileSVM(fileName,server1):
    print("createNodesFile")
    with open(configDir+fileName, "w") as myfile:
        myfile.write(server1+":quorum-manager\n")




def configureBaseCluster(server1,server2,server3):
#
# create a rack1.nodes file insert three node defs
        
    createNodesFile('rack1.nodes',server1,server2,server3)
    clusterCmd="/usr/lpp/mmfs/bin/mmcrcluster -N "+configDir+"rack1.nodes -p "+server1+" -s "+server2+" -r /usr/bin/ssh -R /usr/bin/scp -A"
    runShell(clusterCmd)
    licenseCmd="/usr/lpp/mmfs/bin/mmchlicense server --accept -N "+server1+","+server2+","+server3
    runShell(licenseCmd)
    server1Check=0
    server2Check=0
    server3Check=0
    if enableIPV6=="true":
        server1Check=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server1)
        server2Check=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server2)
        server3Check=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server3)
    else:    
        server1Check=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server1)
        server2Check=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server2)
        server3Check=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server3)

    if (server1Check==1) or (server2Check==1) or (server3Check==1):
        raise Exception("ClusterCreation failed, inputed server ips were:  "+server1+","+server2+","+server3)


def configureBaseClusterSVM(server1):
#
# create a rack1.nodes file insert three node defs
    
    createNodesFileSVM('rack1.nodes',server1)
    clusterCmd="/usr/lpp/mmfs/bin/mmcrcluster -N "+configDir+"rack1.nodes -p "+server1+" -r /usr/bin/ssh -R /usr/bin/scp -A"
    runShell(clusterCmd)
    licenseCmd="/usr/lpp/mmfs/bin/mmchlicense server --accept -N "+server1
    runShell(licenseCmd)
    server1Check=0
    
    if enableIPV6=="true":
        server1Check=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server1)
    else:
        server1Check=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server1)    
    if (server1Check==1):
            raise Exception("ClusterCreation failed, inputed server ips were:  "+server1)


def configureReplicationRack(server1,server2,server3):
# add nodes
# change license to server
# startup nodes

    print("configureRackB")
    createNodesFile('rack2.nodes',server1,server2,server3)
    #need to do mmchcluster to change the secondary
    clusterCmd="/usr/lpp/mmfs/bin/mmaddnode -N "+configDir+"rack2.nodes" 
    runShell(clusterCmd)
    licenseCmd="/usr/lpp/mmfs/bin/mmchlicense server --accept -N "+server1+","+server2+","+server3
    runShell(licenseCmd)
    chgCmd="/usr/lpp/mmfs/bin/mmchcluster -s "+server1
    runShell(chgCmd)
    repCheck1=0
    repCheck2=0
    repCheck3=0
    
    if enableIPV6=="true":
        repCheck1=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server1)
        repCheck2=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server2)
        repCheck3=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server3)
    else:
        repCheck1=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server1)
        repCheck2=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server2)
        repCheck3=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server3)
    if (repCheck1==1) or (repCheck2==1) or (repCheck3==1):
        raise Exception("Replication Rack config failed with servers:  "+server1+","+server2+","+server3)


def configureReplicationRackSVM(server1):
# add nodes
# change license to server
# startup nodes

    print("configureRackB")
    createNodesFileSVM('rack2.nodes',server1)
    #need to do mmchcluster to change the secondary
    clusterCmd="/usr/lpp/mmfs/bin/mmaddnode -N "+configDir+"rack2.nodes" 
    runShell(clusterCmd)
    licenseCmd="/usr/lpp/mmfs/bin/mmchlicense server --accept -N "+server1
    runShell(licenseCmd)
    chgCmd="/usr/lpp/mmfs/bin/mmchcluster -s "+server1
    runShell(chgCmd)
    repCheck1=0
    
    if enableIPV6=="true":
        repCheck1=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',server1)
    else:
        repCheck1=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',server1)
    if (repCheck1==1):
        raise Exception("Replication Rack config failed with servers:  "+server1)


def createNSD(fileName,nsdName,device,server1,server2,server3,failureGroup,usage):
    print("createNSD")
    nsdCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsnsd -d '+ nsdName,nsdName)
    if nsdCheck==0:
        raise Exception("NSD: "+"already exists")

    serverList=server1
    if server2 !="None":
        serverList=serverList+","+server2
    if server3 !="None":
        serverList=serverList+","+server3

    createNSDFile(fileName,failureGroup,usage,serverList,device,nsdName)
    try:
        nsdCmd="/usr/lpp/mmfs/bin/mmcrnsd -F "+configDir+fileName+ ' -v yes'
        runShell(nsdCmd)
    except:
        runShell("rm /tmp/gpfs/"+fileName)
        raise
    nsdCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsnsd -d '+ nsdName,nsdName)
    if nsdCheck==1:
        runShell("rm /tmp/gpfs/"+fileName)
        raise Exception("NSD: "+"does not exist")


def createNSDSVM(fileName,nsdName,device,server1,failureGroup,usage):
    print("createNSDSVM")
    nsdCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsnsd -d '+ nsdName,nsdName)
    if nsdCheck==0:
        raise Exception("NSD: "+"already exists")

    serverList=server1

    createNSDFile(fileName,failureGroup,usage,serverList,device,nsdName)
    try:
        nsdCmd="/usr/lpp/mmfs/bin/mmcrnsd -F "+configDir+fileName+ ' -v yes'
        runShell(nsdCmd)
    except:
        runShell("rm /tmp/gpfs/"+fileName)
        raise

    nsdCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsnsd -d '+ nsdName,nsdName)
    if nsdCheck==1:
        raise Exception("NSD: "+"does not exist")


def addDisk(fileSystem,nsdFile,server1,server2,server3):
     print("inside add disk")
     nsdName=nsdFile.split('.nsd')[0]
     diskCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsdisk gpfsdev -d '+nsdName,nsdName)
     if diskCheck==0:
         raise Exception("Disk "+nsdName+" already exists")
     nodeList=server1
     if server2 !="None":
         nodeList=nodeList+","+server2
     if server3 !="None":
         nodeList=nodeList+","+server3
     print("callAddDisk")
     addDiskCmd="/usr/lpp/mmfs/bin/mmadddisk "+defaultFileSystem+" -F "+configDir+nsdFile+" -v yes -N "+nodeList
     runShell(addDiskCmd)
     diskCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsdisk gpfsdev -d '+nsdName,nsdName)
     if diskCheck==1:
         raise Exception("Disk "+nsdName+" was not created")
     print("complete add disk")


def addDiskSVM(fileSystem,nsdFile,server1):
     print("inside add disk SVM")
     nsdName=nsdFile.split('.nsd')[0]
     diskCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsdisk gpfsdev -d '+nsdName,nsdName)
     if diskCheck==0:
         raise Exception("Disk "+nsdName+" already exists")
     nodeList=server1
     print("callAddDiskSVM")
     addDiskCmd="/usr/lpp/mmfs/bin/mmadddisk "+defaultFileSystem+" -F "+configDir+nsdFile+" -v yes -N "+nodeList
     runShell(addDiskCmd)
     diskCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsdisk gpfsdev -d '+nsdName,nsdName)
     if diskCheck==1:
         raise Exception("Disk "+nsdName+" was not created")
     print("complete add disk SVM")

def setIPV6():
    ipv6CMD="/usr/lpp/mmfs/bin/mmchconfig enableIPv6=yes"
    runShell(ipv6CMD)
    # may need to restart the cluster
    ipv6CMD2="mmchconfig enableIPv6=commit"
    runShell(ipv6CMD2)
    
def createBaseFileSystem(fsname,replication,blockSize):
    print("createBaseFileSystem")
    startupCmd="/usr/lpp/mmfs/bin/mmstartup -a"
    runShell(startupCmd)
#create nsd
#create base FS with nsd
    runShell("cat /tmp/gpfs/rack1.nsd >> /tmp/gpfs/all.nsd")
    if replication==2:
    	runShell("cat /tmp/gpfs/rack2.nsd >> /tmp/gpfs/all.nsd")
    	runShell("cat /tmp/gpfs/tie.nsd >> /tmp/gpfs/all.nsd")
    silentCheckAllClusterNodesAreUp()
    fileSystemCmd="/usr/lpp/mmfs/bin/mmcrfs "+defaultFileSystem+" -F "+configDir+"all.nsd -A yes -B "+blockSize+" -K always -m "+str(replication) +" -M 2 -Q yes -r "+str(replication)+" -R 2 -v yes"
    runShell(fileSystemCmd)
    
    fsCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsfs gpfsdev','/gpfs/gpfsdev')
    if fsCheck ==1:
       raise Exception("file system gpfsdev is not configured")
    mountCmd="/usr/lpp/mmfs/bin/mmmount gpfsdev -a"
    runShell(mountCmd)


#note the tie cluster is currently last and starts the cluster
def configureTie(tieHost):

    print("configureTie")
    createNodesFile('tie.nodes',tieHost,"None","None")

    clusterCmd="/usr/lpp/mmfs/bin/mmaddnode -N "+configDir+"tie.nodes"
    runShell(clusterCmd)
    licenseCmd="/usr/lpp/mmfs/bin/mmchlicense server --accept -N "+ tieHost
    runShell(licenseCmd)
    tieCheck=0
    
    if enableIPV6=="true":
        tieCheck=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',tieHost)
    else:
        tieCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',tieHost)
    if (tieCheck==1):
        raise Exception("Tiebreaker config failed with server:  "+tieHost)

    print("configureTie end")


def configureTieSVM(tieHost):

    print("configureTieSVM")
    createNodesFileSVM('tie.nodes',tieHost)

    clusterCmd="/usr/lpp/mmfs/bin/mmaddnode -N "+configDir+"tie.nodes"
    runShell(clusterCmd)
    licenseCmd="/usr/lpp/mmfs/bin/mmchlicense server --accept -N "+ tieHost
    runShell(licenseCmd)
    tieCheck=0
    if enableIPV6=="true":
        tieCheck=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',tieHost)
    else:
        tieCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',tieHost)
    if (tieCheck==1):
        raise Exception("Tiebreaker config failed with server:  "+tieHost)

    print("configureTieSVM end")


#add 1 to n Nodes, all of type nodeTypes with qualifiers
def addClientNode(nodeHost):
   print("addNode")
   writeClientToQuorumMemberetcHosts(nodeHost)
   addClientNodeCMD="/usr/lpp/mmfs/bin/mmaddnode -N "+nodeHost
   runShell(addClientNodeCMD)

   licenseCmd="/usr/lpp/mmfs/bin/mmchlicense client --accept -N "+nodeHost
   runShell(licenseCmd)
   startupCmd="/usr/lpp/mmfs/bin/mmstartup -N "+nodeHost
   runShell(startupCmd)
   clientCheck=0
   if enableIPV6=="true":
       clientCheck=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',nodeHost)
   else:
       clientCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',nodeHost)
   if (clientCheck==1):
        raise Exception("add client config failed with client:  "+nodeHost)
   
   silentCheckClientNodeIsUp(nodeHost)
   mountFileSystemCmd="/usr/lpp/mmfs/bin/mmmount gpfsdev -N "+nodeHost
   runShell(mountFileSystemCmd)



def deleteClientNode(nodeHost):
   
   
   print("deleteNode")
   unmountCMD="/usr/lpp/mmfs/bin/mmunmount gpfsdev -N "+nodeHost
   runShell(unmountCMD)
   shutdownCMD="/usr/lpp/mmfs/bin/mmshutdown -N "+nodeHost
   runShell(shutdownCMD)
   delClientNodeCMD="/usr/lpp/mmfs/bin/mmdelnode -N "+nodeHost
   runShell(delClientNodeCMD)
   clientCheck=0
   if enableIPV6=="true":
       clientCheck=runIPV6Check('/usr/lpp/mmfs/bin/mmlscluster',nodeHost)

   else:
       clientCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlscluster',nodeHost)
   if (clientCheck==0):
        raise Exception("delete client failed with client:  "+nodeHost)
   removeClientToQuorumMemberetcHosts(nodeHost)
   
   
   
   
def addClientNodeRemote(remoteServerHost):
   print("addNodeRemote")
   runShellQuick("chmod +rwx /tmp/gpfs/gpfsInstall/getClientHostName.sh")
   out= runShellQuick("/tmp/gpfs/gpfsInstall/getClientHostName.sh")
   clientHost=out.split("\n")[0]
   print(("got client host:  "+clientHost))
   createKeyBackupDirs()
   configClientFirewall(remoteServerHost)
   
   retrieveCurrentKeysAndSyncClient(remoteServerHost)
   remoteSCPHostsFileCMD="scp root@"+remoteServerHost+":/tmp/gpfs/clusterHosts /tmp/gpfs/clusterHosts"
   runShell(remoteSCPHostsFileCMD)
   runShell('cat /tmp/gpfs/clusterHosts >>/etc/hosts')    

   addClientNodeRemoteCMD="ssh root@" + remoteServerHost + " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName addClientNode -clientHost " + clientHost
   runShell(addClientNodeRemoteCMD)


def deleteClientNodeRemote():
   #print "deleteNodeRemote"
   #addClientNodeRemoteCMD="ssh root@" + remoteServerHost + " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName deleteClientNode -clientHost " + clientHost
   #runShell(addClientNodeRemoteCMD)
   remoteServerHost = str(getActiveNode())
   out= runShellQuick("/tmp/gpfs/gpfsInstall/getClientHostName.sh")
   clientHost=out.split("\n")[0]
   addClientNodeRemoteCMD="ssh root@" + remoteServerHost + " python /tmp/gpfs/gpfsInstall/gpfs.py -operationName deleteClientNode -clientHost " + clientHost
   runShell(addClientNodeRemoteCMD)



def createFileSet(filesetName,minQuotaSize,maxQuotaSize):
    
   try:
       lock = GPFSLock("/tmp/gpfs/gpfsInstall/"+fileSetName+".log")
       print(("try to get lock for fileset:  "+fileSetName))
       lock.acquire()
       print ("have lock")
    
       print("createFileSet")

       fsCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsfileset gpfsdev '+filesetName,filesetName)
       if fsCheck==0:
           print("WARNING fileset "+filesetName+" already exists")
           
       else:
           fileSetCmd="/usr/lpp/mmfs/bin/mmcrfileset "+defaultFileSystem+" "+filesetName
           runShell(fileSetCmd)
           linkFileSetCmd="/usr/lpp/mmfs/bin/mmlinkfileset "+defaultFileSystem+" "+ filesetName+" -J /gpfs/gpfsdev/"+filesetName
           runShell(linkFileSetCmd)
           #create fileset on fs
           cmdQuota="/usr/lpp/mmfs/bin/mmsetquota -j "+filesetName+" -s "+minQuotaSize+" -h "+maxQuotaSize+" gpfsdev"
           runShell(cmdQuota)
           fsCheck=runShellWithCheck('/usr/lpp/mmfs/bin/mmlsfileset gpfsdev '+filesetName,filesetName)
           if fsCheck==1:
               raise Exception("Fileset "+filesetName+" was not created")
   except IOError:
       raise Exception("Fileset "+filesetName+" already exists")
   finally:
       lock.release()





def linkFileSet(filesetName,linkdir):
   if os.path.exists("/gpfs/gpfsdev/"+filesetName):
       print("fileSet "+filesetName+" exists")
   else:
       print("fileSet:  "+filesetName+"does not exist. Can not link")
       raise Exception("fileSet: "+filesetName+" to link to does not exist")
   if os.access(linkdir,os.F_OK):
       raise Exception ("ln to "+linkdir+" already exists")
   createInitialLinkDir(linkdir)
   cmdLN="ln -s /gpfs/gpfsdev/"+filesetName+" "+linkdir
   runShell(cmdLN)
   if os.access(linkdir,os.F_OK):
       print("ln created to: "+linkdir)
   else:
      raise Exception ("ln not created to "+linkdir)    
    


#mount fs on hosts
def mountFileSystem(nodeList,fsname,mountPoint,driveLetter):
   print("mountFileSystem")
   #mmmount node/nodes?
   spacer1=""
   spacer2=""
   mountValue=mountPoint
   driveValue=driveLetter
   if mountPoint!=None:
       spacer1=" "
   if driveLetter!=None:
       spacer2=" "
   if mountPoint==None:
       mountValue=""
   if driveLetter==None:
       driveValue=""
   mountCmd="/usr/lpp/mmfs/bin/mmmount "+fsname+spacer1+mountValue+spacer2+driveValue+" -N "+NodeList
   runShell(mountCmd)


def status():
   current = datetime.datetime.now()
   print("Status collected at: " + str(current))
   runShell("/usr/lpp/mmfs/bin/mmlscluster")
   runShell("/usr/lpp/mmfs/bin/mmlsnsd")
   runShell("/usr/lpp/mmfs/bin/mmlsdisk gpfsdev")
   runShell("/usr/lpp/mmfs/bin/mmlsfs gpfsdev")
   runShell("/usr/lpp/mmfs/bin/mmlsfileset gpfsdev")


###### main ######


parmsLength=len(sys.argv)
print(str(len(sys.argv)))
for x in range (0,parmsLength):
    if sys.argv[x]=="-operationName":
        operationName=sys.argv[x+1]
        if checkAlphaNum(operationName) ==False:
            raise Exception ("operationName value must be alphabetic or numeric a-z,A-Z,0-9")
    if sys.argv[x]=="-baseServer1":     
        baseServer1=sys.argv[x+1]
    if sys.argv[x]=="-baseServer2":
        baseServer2=sys.argv[x+1]
    if sys.argv[x]=="-baseServer3":
        baseServer3=sys.argv[x+1]
    if sys.argv[x]=="-repServer1":
        repServer1=sys.argv[x+1]
    if sys.argv[x]=="-repServer2":
        repServer2=sys.argv[x+1]
    if sys.argv[x]=="-repServer3":
        repServer3=sys.argv[x+1]
    if sys.argv[x]=="-tieServer":
        tieServer=sys.argv[x+1]
    if sys.argv[x]=="-device":
        device=sys.argv[x+1]
    if sys.argv[x]=="-nsdName":
        nsdName=sys.argv[x+1]
        if checkAlphaNum(nsdName) !=True:
            raise Exception("nsdName muse be alpha numeric value using characters a-z,A-Z,0-9")        
    if sys.argv[x]=="-linkDir":
        linkDir=sys.argv[x+1]
    if sys.argv[x]=="-fileSetName":
        fileSetName=sys.argv[x+1]
        if checkAlphaNumPlus(fileSetName) !=True:
             raise Exception("fileSetName muse be alpha numeric value using characters a-z,A-Z,0-9 or -/_.")
    if sys.argv[x]=="-clientHost":
        clientHost=sys.argv[x+1]
    if sys.argv[x]=="-remoteServerHost":
        remoteServerHost=sys.argv[x+1]
    if sys.argv[x]=="-minQuotaSize":
        minQuotaSize=sys.argv[x+1]
        checkQuotaValue(minQuotaSize)
    if sys.argv[x]=="-maxQuotaSize":
        maxQuotaSize=sys.argv[x+1]
        checkQuotaValue(maxQuotaSize)
    if sys.argv[x]=="-mountPoint":
        mountPoint=sys.argv[x+1]
    if sys.argv[x]=="-driveLetter":
        driveLetter=sys.argv[x+1]
    if sys.argv[x]=="-blockSize":
       blockSize=sys.argv[x+1]
       checkBlockSize(blockSize)
       
    if sys.argv[x]=="-tscTcpPort":
       tscTcpPort=sys.argv[x+1]
       checkPortValue(tscTcpPort)
    if sys.argv[x]=="-mmsdrservPort":
       mmsdrservPort=sys.argv[x+1]
       checkPortValue(mmsdrservPort)
    if sys.argv[x]=="-tscCmdPortRangeLowNumber":          
       tscCmdPortRangeLowNumber=sys.argv[x+1]
       checkPortValue(tscCmdPortRangeLowNumber)
    if sys.argv[x]=="-tscCmdPortRangeHighNumber":
        
       tscCmdPortRangeHighNumber=sys.argv[x+1]
       checkPortValue(tscCmdPortRangeHighNumber)
#    if sys.argv[x]=="-enableIPV6":
#       enableIPV6=sys.argv[x+1]
    if sys.argv[x]=="-keySubDir":
        keySubDir=sys.argv[x+1]
        if checkAlphaNum(keySubDir) ==False:
            raise Exception ("keySubDir must be alpha numeric a-z,A-Z,0-9 characters")
    if sys.argv[x]=="-newDir":
        newDir=sys.argv[x+1]
    if sys.argv[x]=="-backupDir":
        backupDir=sys.argv[x+1]
        
    if sys.argv[x]=="-managerHost":
        managerHost=sys.argv[x+1]
    if sys.argv[x]=="-removeKey":
        removeKey=sys.argv[x+1]
    if sys.argv[x]=="-existingKeyDir":
        existingKeyDir=sys.argv[x+1]
        if path.exists(existingKeyDir):
            print(("Found existingKeyDir:  "+existingKeyDir))
        else:
            raise Exception("existingKeyDir path:  "+existingKeyDir+" not found")
        
    if sys.argv[x]=="-clientIP":
        clientIP=sys.argv[x+1]
    if sys.argv[x]=="-clientHostName":
        clientHostName=sys.argv[x+1]        




############################## end INPUT        
if enableIPV6=="true":
    baseServer1=convertIPV6(baseServer1)
    if baseServer2 !="":
        baseServer2=convertIPV6(baseServer2)
    if baseServer3 !="":
        baseServer3=convertIPV6(baseServer3)
    if repServer1 !="":
        repServer1=convertIPV6(repServer1)
    if repServer2 !="":
        repServer2=convertIPV6(repServer2)
    if repServer3 !="":
        repServer3=convertIPV6(repServer3)
    if tieServer !="":
        tieServer=convertIPV6(tieServer)
    try:
            chtest=clientHost.index(":")
            enableIPV6="true"
            clientHost=convertIPV6(clientHost)
    except:
            print("Client Host not IPV6 continuing") 
       
    try:
            rshtest=remoteServerHost.index(":")
            enableIPV6="true"
            remoteServerHost=convertIPV6(remoteServerHost)
    except:
            print("Remote Server Host not IPV6 continuing") 
       
       
print("-operationName: "+operationName)
print("-baseServer1: "+baseServer1)
print("-baseServer2: "+baseServer2)
print("-baseServer3: "+baseServer3)
print("-repServer1: "+repServer1)
print("-repServer2: "+repServer2)
print("-repServer3: "+repServer3)
print("-tieServer: "+tieServer)
print("-device: "+device)
print("-nsdName: "+nsdName)
print("-linkDir: "+linkDir)
print("-fileSetName: "+fileSetName)
print("-clientHost: "+clientHost)
print("-remoteServerHost: "+remoteServerHost)
print("-minQuotaSize: "+minQuotaSize)
print("-maxQuotaSize: "+maxQuotaSize)
print("-mountPoint: "+mountPoint)
print("-DriveLetter: "+driveLetter)
print("-blockSize:  "+blockSize)
print("-tscTcpPort: "+tscTcpPort)
print("-mmsdrservPort:  "+mmsdrservPort)
print("-tscCmdPortRangeLowNumber:  "+tscCmdPortRangeLowNumber)
print("-tscCmdPortRangeHighNumber:  "+tscCmdPortRangeHighNumber)
print("-enableIPV6:  "+enableIPV6)
print(("-operationName:  "+operationName))
print(("-managerHost: "+managerHost))
print(("-removeKey: "+removeKey))
print(("-backupDir: "+backupDir))
print(("-newDir: "+newDir))
print(("-clientIP: "+clientIP))
print(("-clientHostName: "+clientHostName))
#SYS.Exit(0)
#

if platform.system()=="AIX":
    initialDevice=aixInitialDevice
    sshPath=""
if platform.system()=="Linux":
    initialDevice=linuxInitialDevice
    sshPath="/root"
    


############ mountFileSystem ###################################
if operationName=="-mountFileSystem":
    serverList=baseServer1
    if baseServer2 !="":
       serverList=serverList+","+baseServer
    if baseServer3 !="":
       serverList=serverList+","+baseServer3
    if repServer1 !="":
       if serverList=="":
           serverList=repServer1
       else:
           serverList=serverList+","+repServer1

    if repServer2 !="":
       serverList=serverList+","+repServer2
    if repServer3 !="":
       serverList=serverList+","+repServer3

    if tieServer !="":
       if serverList=="":
           serverList=tieServer
       else:
           serverList=serverList+","+tieServer

    mountFileSystem(serverList,'gpfsdev',mountPoint,driveLetter)


############ mountFileSystem (SVM)##############################
if operationName=="-mountFileSystemSVM":
    serverList=baseServer1
    if repServer1 !="":
       if serverList=="":
           serverList=repServer1
       else:
           serverList=serverList+","+repServer1
    if tieServer !="":
       if serverList=="":
           serverList=tieServer
       else:
           serverList=serverList+","+tieServer

    mountFileSystem(serverList,'gpfsdev',mountPoint,driveLetter)





    
def modifyetcHosts():
    if baseServer1 !="":
        remoteSCPHostsFileCMD="scp /tmp/gpfs/clusterHosts root@"+baseServer1+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD)
        runShell("ssh root@"+baseServer1+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")   
    if baseServer2 !="":
        remoteSCPHostsFileCMD="scp /tmp/gpfs/clusterHosts root@"+baseServer2+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD)
        runShell("ssh root@"+baseServer2+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")   
    if baseServer3 !="":
        remoteSCPHostsFileCMD="scp /tmp/gpfs/clusterHosts root@"+baseServer3+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD)
        
        runShell("ssh root@"+baseServer3+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")
    if repServer1 !="":
        remoteSCPHostsFileCMD="scp  /tmp/gpfs/clusterHosts root@"+repServer1+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD) 
          
        runShell("ssh root@"+repServer1+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")     
    if repServer2 !="":
        remoteSCPHostsFileCMD="scp /tmp/gpfs/clusterHosts  root@"+repServer2+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD)
        runShell("ssh root@"+repServer2+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")             
    if repServer3 !="":
        remoteSCPHostsFileCMD="scp  /tmp/gpfs/clusterHosts root@"+repServer3+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD)
        runShell("ssh root@"+repServer3+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")             
    if tieServer !="":
        remoteSCPHostsFileCMD="scp  /tmp/gpfs/clusterHosts root@"+tieServer+":/tmp/gpfs/clusterHosts"
        runShell(remoteSCPHostsFileCMD)
        runShell("ssh root@"+tieServer+" ' cat /tmp/gpfs/clusterHosts >>/etc/hosts'")
                            
        
        
################### configureRacks ##############################        
def configureRacks(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer,blockSize,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):  
    firewallRules(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
    runShellQuick("chmod +rwx /tmp/gpfs/gpfsInstall/changeClusterKeys.sh")
    replication=1
    backupOrigKeys()
    createKeyBackupDirs()
    
    createQuorumHostsFile(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer)
    modifyetcHosts()
    configureBaseCluster(baseServer1,baseServer2,baseServer3)
    
    if repServer1!="":
        replication=2
        configureReplicationRack(repServer1,repServer2,repServer3)
    if tieServer!="":
        configureTie(tieServer)
#    if enableIPV6=="true":
#        setIPV6()
    #createHostsFile()
    
    createNSD("rack1.nsd","rack1",initialDevice,baseServer1,baseServer2,baseServer3,"1","dataAndMetadata")
    if repServer1 != "":
         createNSD("rack2.nsd","rack2",initialDevice,repServer1,repServer2,repServer3,"2","dataAndMetadata")

    if tieServer != "":
         createNSD("tie.nsd","tie",initialDevice,tieServer,"None","None","3","descOnly")
    setGPFSPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber)         
    
    createBaseFileSystem(defaultFileSystem,replication,blockSize)


################### configureRacks (SVM)#########################

def configureRacksSVM(baseServer1,repServer1,tieServer,blockSize,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6):
    firewallRulesSVM(baseServer1,repServer1,tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)   
    replication=1
    runShellQuick("chmod +rwx /tmp/gpfs/gpfsInstall/changeClusterKeys.sh")
    backupOrigKeys()
    
    createKeyBackupDirs()
    
    createQuorumHostsFile(baseServer1,"","",repServer1,"","",tieServer)
    modifyetcHosts()
    configureBaseClusterSVM(baseServer1)
#    if enableIPV6=="true":
#        setIPV6()
    verifyDisk(initialDevice)   
    createNSDSVM("rack1.nsd","rack1",initialDevice,baseServer1,"1","dataAndMetadata")
    if repServer1!="":
        replication=2
        configureReplicationRackSVM(repServer1)
    if tieServer!="":
        configureTieSVM(tieServer)
#    createHostsFile()

    if repServer1 != "":
         verifyDiskRemote(initialDevice, repServer1) 
         createNSDSVM("rack2.nsd","rack2",initialDevice,repServer1,"2","dataAndMetadata")

    if tieServer != "":
         verifyDiskRemote(initialDevice, tieServer)
         createNSDSVM("tie.nsd","tie",initialDevice,tieServer,"3","descOnly")
    setGPFSPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber)
    
    createBaseFileSystem(defaultFileSystem,replication,blockSize)


############################### addDisk #######################

def addDiskRack1(nsdName,device,server1,server2,server3):
    verifyDisk(device)
    verifyDiskRemote(device,server2)
    verifyDiskRemote(device,server3)
    createNSD(nsdName+"rack1.nsd",nsdName+"rack1",device,server1,server2,server3,"1","dataAndMetadata")
    addDisk(defaultFileSystem,nsdName+"rack1.nsd",server1,server2,server3)

def addDiskRack2(nsdName,device,server1,server2,server3):
    verifyDisk(device)
    verifyDiskRemote(device,server2)
    verifyDiskRemote(device,server3)
    createNSD(nsdName+"rack2.nsd",nsdName+"rack2",device,server1,server2,server3,"2","dataAndMetadata")
    addDisk(defaultFileSystem,nsdName+"rack2.nsd",server1,server2,server3)

def addDiskTie(nsdName,device,server1):
    verifyDisk(device)
    createNSD(nsdName+"tie.nsd",nsdName+"tie",device,server1,"None","None","3","descOnly")
    addDisk(defaultFileSystem,nsdName+"tie.nsd",server1,"None","None")


############################### addDisk (SVM) #################

def addDiskRack1SVM(nsdName,device,server1):
    verifyDisk(device)
    createNSDSVM(nsdName+"rack1.nsd",nsdName+"rack1",device,server1,"1","dataAndMetadata")
    addDiskSVM(defaultFileSystem,nsdName+"rack1.nsd",server1)

def addDiskRack2SVM(nsdName,device,server1):
    verifyDisk(device)
    createNSDSVM(nsdName+"rack2.nsd",nsdName+"rack2",device,server1,"2","dataAndMetadata")
    addDiskSVM(defaultFileSystem,nsdName+"rack2.nsd",server1)

def addDiskTieSVM(nsdName,device,server1):
    verifyDisk(device)
    createNSDSVM(nsdName+"tie.nsd",nsdName+"tie",device,server1,"3","descOnly")
    addDiskSVM(defaultFileSystem,nsdName+"tie.nsd",server1)


###################################### calls by input from sys.argv #################

if operationName=="configureRacks":
    configureRacks(baseServer1,baseServer2,baseServer3,repServer1,repServer2,repServer3,tieServer,blockSize,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)

if operationName=="configureRacksSVM":
    configureRacksSVM(baseServer1,repServer1,tieServer,blockSize,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)


###################### add Disks #########################################
if operationName=="addDiskRack1":
    addDiskRack1(nsdName,device,baseServer1,baseServer2,baseServer3)

if operationName=="addDiskRack2":
    addDiskRack2(nsdName,device,repServer1,repServer2,repServer3)

if operationName=="addDiskTie":
    addDiskTie(nsdName,device,tieServer)

###################### add Disks (SVM) Single VM #########################
if operationName=="addDiskRack1SVM":
    addDiskRack1SVM(nsdName,device,baseServer1)

if operationName=="addDiskRack2SVM":
    addDiskRack2SVM(nsdName,device,repServer1)

if operationName=="addDiskTieSVM":
    addDiskTieSVM(nsdName,device,tieServer)


if operationName=="kim":
   createBaseFileSystem(defaultFileSystem,2)

####################### createFileSet ########################

if operationName == "createFileSet":
    createFileSet(fileSetName,minQuotaSize,maxQuotaSize)

if operationName == "createFileSetRemote":
    createFileSetRemote(fileSetName,minQuotaSize,maxQuotaSize)

################### linkFileSet #########################

if operationName == "linkFileSet":
    try:
        linkFileSet(fileSetName,linkDir)
    except:
        print("link error with fileSetName "+fileSetName+" and linkDir "+linkDir+" error is: "+ str(sys.exc_info()))
        sys.exit(1)


###################### addClientNode #########################

if operationName == "addClientNode":
    addClientNode(clientHost)


###################### deleteClientNode #########################

if operationName == "deleteClientNode":
    deleteClientNode(clientHost)


#########################deleteClientNodeRemote #########################

if operationName == "deleteClientNodeRemote":
    deleteClientNodeRemote()

###################### addClientNodeRemote #########################

if operationName == "addClientNodeRemote":
    addClientNodeRemote(remoteServerHost)

###################### Status #########################

if operationName == "status":
    status()
    
###################### verifyDisk #########################

if operationName == "verifyDisk":
    verifyDisk(device)

###################### verifyDiskRemote #########################

if operationName == "verifyDiskRemote":
    verifyDiskRemote(device,remoteServerHost)
  
###################### verifyDiskRemote #########################

if operationName == "firewallRules":
    firewallRules(baseServer1,repServer1,tieServer,tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)
    
###################### verifyDiskRemote #########################

if operationName == "openPorts":
    openPorts(tscTcpPort,mmsdrservPort,tscCmdPortRangeLowNumber,tscCmdPortRangeHighNumber,enableIPV6)

###################### verifyDiskRemote #########################

if operationName == "openSSHPort":
    openSSHPort()
###################### changeClusterKeys #########################    
if operationName=="changeClusterKeys":
    changeClusterKeys(keySubDir,managerHost)
###################### replaceServerKeys #########################    
if operationName=="replaceServerKeys":
    replaceServerKeys(newDir,backupDir,removeKey)

    
if operationName=="createKeyBackupDirs":
    createKeyBackupDirs()
if operationName=="appendClientToetcHosts":    
    appendClientToetcHosts(clientIP,clientHostName)    
    
if operationName=="removeClientFrometcHosts":    
    removeClientFrometcHosts(clientIP)    
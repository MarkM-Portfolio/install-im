/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.delta;

//  DeltaByte Reconstructor

import java.io.*;


/**
 *  
 */
class DeltaByteReconstructor {
   public static final String pgmVersion = "1.3" ;
   public static final String pgmUpdate = "4/29/04" ;

 static Logger  Log;
 static int     verbosity;
 static boolean fDebug;
 static int     bufferSize;
 static File    tempDir;

 static HelperList HL;
 static Helper1    HC1;


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                          Constructor                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 DeltaByteReconstructor(Logger Log, int verbosity, boolean fDebug, int bufferSize, File tempDir) {

	DeltaByteReconstructor.Log        = Log;
	DeltaByteReconstructor.verbosity  = verbosity;
	DeltaByteReconstructor.fDebug     = fDebug;
	DeltaByteReconstructor.bufferSize = bufferSize;
	DeltaByteReconstructor.tempDir    = tempDir;

   HL  = new HelperList();
   HC1 = new Helper1(Log, verbosity);

 }

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ           Reconstruct a File from the changeList Stream            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean Reconstruct(String theOldFile, InputStream changeList) {

 int workBufSize = bufferSize;     // this can increase in size
 byte[] workBuf  = new byte[workBufSize*2];  // buffer needs to twice the size of the construction buffer
 int blockOutCount = 0;
 File tempFile = null;

 ChangeItem ci = new ChangeItem(Log);


          // make the changes and write to New File
 long adjustment  = 0;  //  absolute position within the new file
 long totRead     = 0;  //  track the total bytes read



 try {
   tempFile = File.createTempFile(theOldFile, "DBR", tempDir);

   BufferedInputStream  bf1 = new BufferedInputStream( new FileInputStream(theOldFile), workBufSize);
   BufferedOutputStream bof = new BufferedOutputStream(new FileOutputStream(tempFile), (workBufSize*2));

   long available = new File(theOldFile).length();
   int  numRead   = bf1.read(workBuf, 0, bufferSize); // first read
   int  dataLen   = numRead;  // count of data in workBuf
                             // Note: when we have an insert in the last position of the buffer
                             //       it will go into the first position of the next buffer. of
                             //       course there are no more buffers, then we need to place it
                             //       in the current buffer.
   if (fDebug)
     Log.Both("numRead="+numRead +" "+ XlateCRLF(workBuf, dataLen));


   DataInputStream dis = new DataInputStream(changeList);

   if (ci.fromStream(dis) == 0) {  // do initial read
     ci = null;
   }


   boolean eof      = false;
   boolean moreData = true;

   while ( moreData ) {

     if (ci != null) {

       while ((ci.index < (numRead + totRead)) || (eof) || ci.action == HelperList.k_FileSize) {
         int tPos;  // target position within the buffer

         if (fDebug) {
           Log.Both("Top totRead="+totRead);
           Log.Both("    datalen="+dataLen+" (" + XlateCRLF(workBuf, dataLen) + ")");
           Log.Both(ci.ciRead + " Action=" + ci.toString());
         }


         if (ci.action == HelperList.k_Replace) {
           tPos = new Long((ci.index+adjustment) - totRead).intValue();

           for (int i=0; i < ci.length; i++, tPos++) {
             workBuf[tPos] = ci.data[i];
           }


         } else if (ci.action == HelperList.k_Delete) {

           int target = new Long((ci.index+adjustment) - totRead).intValue();
           int source = target + ci.length;

           for (; source < dataLen; source++, target++) {
             workBuf[target] = workBuf[source];
           }

           dataLen     =  (dataLen > ci.length) ?  dataLen - ci.length : 0;
           available  -= ci.length;
           adjustment -= new Integer(ci.length).longValue();

         } else if (ci.action == HelperList.k_Insert) {

           if ((dataLen + ci.length) > workBufSize ) { // make the buffer Bigger
             int newSize =  dataLen + ci.length;
             Log.Both(4, " Expanding Buffer from "+workBufSize+" to "+ newSize);
             byte[] newBuf = new byte[dataLen + ci.length];

             for (int i=0; i < dataLen ; i++) {
               newBuf[i] = workBuf[i];
             }
             workBuf = newBuf;
             workBufSize = newSize;
           }

           tPos = new Long((ci.index - totRead) + adjustment).intValue();  // target position in the buffer
           int endPos = (dataLen-1) + ci.length;
           int srcByte = dataLen-1;

           if (fDebug) {
             Log.Both(" tPos="+tPos +"  endPos="+endPos +" srcByte="+srcByte);
           }


           for (; (srcByte >= tPos) && (srcByte < endPos) ; endPos--, srcByte--) {
             workBuf[endPos] = workBuf[srcByte];
           }


           for (int i=0; i < ci.length; i++, tPos++) {
             workBuf[tPos] = ci.data[i];
           }

           dataLen = dataLen += ci.length;
           adjustment += ci.length;


         } else if (ci.action == HelperList.k_FileSize) {    /// check the file size


           if (ci.index == available) {
             if (fDebug)
               Log.Both("FileSize matches at " +  HC1.FmtNum(available,0,0,0));

           } else {
             Log.Err(802, "Invalid FileSize, expected " +  HC1.FmtNum(ci.index,0,0,0) + " actual is " + HC1.FmtNum(available,0,0,0) );
             return false;
           }

         } else
           Log.Err(801, "invalid action "+ci.action);


         if (fDebug)
           Log.Both("result dataLen="+dataLen+" (" + XlateCRLF(workBuf, dataLen)+")");


         if (ci.fromStream(dis) == 0 ) {   // fetch the next change element
           ci = null;
           break;
         }

       }

     } else {
     //  moreData = false;
     }


     if ((totRead < available) || (dataLen > 0 )) {

       if (dataLen > 0) {
         if (numRead > 0)
           totRead += numRead;

         if (fDebug)
           Log.Both("Writing len=" + dataLen+ " ("+ XlateCRLF(workBuf, dataLen)+")");

         bof.write(workBuf, 0, dataLen);
         blockOutCount++;

         if (verbosity == 9) {
           String modiName = "modi\\Block."+ blockOutCount;
           BufferedOutputStream modi = new BufferedOutputStream(new FileOutputStream(modiName), bufferSize);
           modi.write(workBuf, 0, dataLen);
           modi.close();
         }

         dataLen = 0;
       } else {
         if (numRead > 0)
           totRead += numRead;
       }

       if (numRead != -1) {
         adjustment = 0;
         numRead = bf1.read(workBuf, 0, bufferSize);
         if (fDebug) {
           if (numRead == -1)
             Log.Both("numRead=" + numRead);
           else
             Log.Both("numRead=" + numRead +" ("+ XlateCRLF(workBuf, numRead)+")" );
         }

         if (numRead > 0)
           dataLen = numRead;
       }

     } else {

       if (ci == null) {
         moreData = false;
       } else {
         eof = true;
       }

     }

   } //  moreData


   dis.close();
   bf1.close();
   bof.close();

 } catch (Exception ex) {
   Log.Err(01, "  ", ex);
   ex.printStackTrace();
   System.exit(8);
 }

 CopyFile(tempFile, new File(theOldFile));
 tempFile.delete();


 return true;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Copying a File                                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public int CopyFile(File InFile, File OutFile) {

 int numRead = 0;
 int totRead = 0;

 try {
   BufferedInputStream  BufInFile  = new BufferedInputStream( new FileInputStream(InFile),   bufferSize);
   BufferedOutputStream BufOutFile = new BufferedOutputStream(new FileOutputStream(OutFile), bufferSize);

   byte[] buf = new byte[bufferSize];

   while ((numRead = BufInFile.read(buf)) != -1 ) {
      totRead += numRead;
      BufOutFile.write(buf, 0, numRead);
   }

   BufInFile.close();
   BufOutFile.close();


 } catch (FileNotFoundException e) {
    System.out.println("Error -- FileNotFound " + e.getMessage());

 } catch (IOException e ) {
    System.out.println("Error -- IOException " + e.getMessage());
 }

 return totRead;
}




//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ              Xlate CRLF to BangBang for Debug Display              บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
static String XlateCRLF(byte[] byteArray, int length) {

 String temp = new String(byteArray, 0, length);

 temp = temp.replace('\n', '!');  // newLine
 temp = temp.replace('\r', '!');  // carrage Return

 return temp;
}



}

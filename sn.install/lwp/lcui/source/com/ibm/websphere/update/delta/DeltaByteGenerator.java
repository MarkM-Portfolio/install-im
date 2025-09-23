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
//    Delta Byte Generator

import java.io.*;

/**
 *  
 */
class DeltaByteGenerator {
   public static final String pgmVersion = "1.3" ;
   public static final String pgmUpdate = "4/29/04" ;

 static HelperList HL;
 static Logger     Log;

 static int     verbosity;
 static boolean debug;
 static int     bufferSize;
 static int     reSyncLen;
 static int     reSyncScan;
 static int     maxSizePct;

 static boolean fTest = false;

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                   Constructor                                      บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 DeltaByteGenerator (Logger Log, int verbosity, boolean debug, int bufferSize, int reSyncLen, int reSyncScan, int maxSizePct) {

	DeltaByteGenerator.Log        = Log;
	DeltaByteGenerator.verbosity  = verbosity;
	DeltaByteGenerator.debug      = debug;
	DeltaByteGenerator.bufferSize = bufferSize;
	DeltaByteGenerator.reSyncLen  = reSyncLen;
	DeltaByteGenerator.reSyncScan = reSyncScan;
	DeltaByteGenerator.maxSizePct = maxSizePct;

   HL  = new HelperList();
 }


 DeltaByteGenerator () {
   // this is used for getting version information only
 }


//***************************************************************************
//             Build the Delta Vector
//***************************************************************************
 class ScanState {  // this class contains the current state of scanning and buffer information

  int     ciCount;                   // count of how many Change Items we have written
  int bufCountOld;
  int bufCountNew;
  int numReadOld;
  int numReadNew;
  long totReadOld;
  long totReadNew;
  boolean oldEOF;
  boolean newEOF;
  long    deltaSize;                 // the runing size of the delta data

  int p1;
  int r1;
  int p2;
  int r2;

  byte[] oldBuf;          // I/O buffers
  byte[] newBuf;

  DataOutputStream dos;

  ScanState (int bufferSz, OutputStream outStream ) {    // object constructor

    bufCountOld = 0;
    bufCountNew = 0;
    numReadOld  = 0;
    numReadNew  = 0;
    totReadOld  = 0;
    totReadNew  = 0;
    oldEOF      = false;         // to indicate if we have reached EOF
    newEOF      = false;

    oldBuf = new byte[bufferSz];
    newBuf = new byte[bufferSz];
    p1 = r1 = p2 = r2 =0;

    dos = new DataOutputStream(outStream);
  }

  //
  //    Display the content of the ScanState for Debugging and errors
  //
  public void Display(String msg) {
    if (msg == null)
      Log.Both("  **** Content of ScanState Object ****" );
    else
      Log.Both("  **** " + msg + " ****");

    Log.Both("bufCountOld="+   bufCountOld);
    Log.Both("bufCountNew="+   bufCountNew);
    Log.Both("numReadOld ="+   numReadOld);
    Log.Both("numReadNew ="+   numReadNew);
    Log.Both("totReadOld ="+   totReadOld);
    Log.Both("totReadNew ="+   totReadNew);
    Log.Both("oldEOF     ="+   oldEOF);
    Log.Both("newEOF     ="+   newEOF);
    Log.Both("p1="+p1 + " r1="+r1 + "  len=" + (r1-p1));
    Log.Both("p2="+p2 + " r2="+r2 + "  len=" + (r2-p2));


    // if ((numReadOld > 0 ) && (numReadOld < maxDisplayLength))
    //   Log.Both("oldBuf (" + XlateCRLF(oldBuf, numReadOld) +")");

    // if ((numReadNew > 0 ) && (numReadNew < maxDisplayLength))
    //   Log.Both("newBuf (" + XlateCRLF(newBuf, numReadNew) + ")");

  }

}




//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Generate Byte Delta Stream                         บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int Generate(BufferedInputStream oldOne, long oldOneSize, BufferedInputStream newOne, long newOneSize, OutputStream deltaOne) {
 // return values are:
 //  -1  we had an error
 //   0  the size of changeIems exceeds the new file size, deltaOne should be deleted
 //   1  there was no difference in the file, deltaOne should be deleted
 // > 2  count of how many changeItems were written to deltaOne stream

 boolean moreData = true;
 long nextTime = System.currentTimeMillis() + 10000;
 long reads2do = newOneSize/bufferSize;  // number of reads we need  to do
 long maxSizePctAdj =  (newOneSize * maxSizePct) / 100;


 ScanState ss = new ScanState(bufferSize, deltaOne);   // gime a new ScanState Object

 ChangeItem ci = new ChangeItem(Log, HelperList.k_FileSize,  oldOneSize, 0);
 ss.ciCount++;
 ss.deltaSize += (ci.k_OverHead + 8);
 ci.toStream(ss.dos);   // create the fileSize Check entry


 while (moreData) {

   if (!ss.oldEOF) {    // read a block from the old file
     ss.totReadOld += ss.numReadOld;
     try {
       ss.numReadOld = oldOne.read(ss.oldBuf);
     } catch (IOException ex) {
       Log.Err(201, "Read failure for "+ oldOne.toString(), ex);
       return -1;
     }

     if (ss.numReadOld == -1) {
       ss.oldEOF = true;
       ss.numReadOld = 0;
     } else
       ss.bufCountOld++;


     ss.p1 = 0;
     ss.r1 = 0;

     if (debug) {
       if (ss.oldEOF)
         Log.Both("oldOne EOF");
       else
         Log.Both("oldOne " + ss.numReadOld +" (" + XlateCRLF(ss.oldBuf, ss.numReadOld)+")");
     }

     if ((fTest) && (!ss.oldEOF)) {   // write out a copy of this block
       String fName = "tOld\\Block."+ ss.bufCountOld;
       try {
         BufferedOutputStream orig = new BufferedOutputStream(new FileOutputStream(fName), bufferSize);
         orig.write(ss.oldBuf, 0, ss.numReadOld);
         orig.close();
       } catch (Exception ex) {
         Log.Err(202, "Write failure for "+ fName, ex);
         Log.Close();
         System.exit(8);
       }
     }
   }



   if (!ss.newEOF) {    // Read a block from the new file
     ss.totReadNew += ss.numReadNew;
     try {
       ss.numReadNew = newOne.read(ss.newBuf);
     } catch (IOException ex) {
       Log.Err(201, "Read failure for "+ newOne.toString(), ex);
       return -1;
     }

     if (ss.numReadNew == -1) {
       ss.newEOF = true;
       ss.numReadNew = 0;
     } else
       ss.bufCountNew++;

     ss.p2 = 0;
     ss.r2 = 0;

     if (debug) {
       if (ss.newEOF)
         Log.Both("newOne EOF");
       else
         Log.Both("newOne " + ss.numReadNew +" (" + XlateCRLF(ss.newBuf, ss.numReadNew)+")");
     }

     if ((fTest) && (!ss.newEOF)) {   // write out a copy of this block
       String fName = "tNew\\Block."+ ss.bufCountNew;
       try {
         BufferedOutputStream orig = new BufferedOutputStream(new FileOutputStream(fName), bufferSize);
         orig.write(ss.newBuf, 0, ss.numReadNew);
         orig.close();
       } catch (Exception ex) {
         Log.Err(203, "Write failure for "+ fName, ex);
         Log.Close();
         System.exit(8);
       }
     }
   }


   if (ss.oldEOF && ss.newEOF) {   // are we at the end of both streams
     moreData = false;
     continue;
   }


             // do a byte compare on the buffers
   while ((ss.p1 < ss.numReadOld) && (ss.p2 < ss.numReadNew)) {

     if (ss.oldBuf[ss.p1] == ss.newBuf[ss.p2]) {
       ss.p1++;
       ss.p2++;

     } else {  // here we have a delta in the buffer

       if (ReSync(ss)) {  // go Find the resync chars
         MidBufDelta(ss);

       } else {   // we did not find a re-sync
         EndBufDelta(ss);
       }

     }

   }

           // now handle if the new file ends before the old and we still have data
   if (ss.numReadOld > ss.p1) {
     ss.p1 = Delete("Eof2", ss, ss.p1, ss.numReadOld - ss.p1); // delete all the old Stuff

   } else if (ss.numReadNew > ss.p2) {
     Insert("Eof1", ss, ss.p1, ss.numReadNew - ss.p2, ss.p2);
   }


   if (ss.deltaSize > maxSizePctAdj) {
     moreData = false;
     ss.ciCount = 0;     // to indicate the ciSize has exceeded the newFileSize
   }


   if (System.currentTimeMillis() > nextTime ) {  // update the screen every 10 seconds
     long pct = 0;

     if (reads2do > 0)
       pct = (ss.bufCountNew * 100) / reads2do;

     System.out.println(Log.CurrentTimeStamp() + "\t" + ss.bufCountNew + " blocks read  " +
                       Long.toString(pct) + "% complete" );

     nextTime = System.currentTimeMillis() + 10000;
   }

 }  // end of data

 try {
   ss.dos.close();
 } catch (IOException ex) {
   Log.Err(208, "failure closing DataOutStream", ex);
   return -1;
 }

 return ss.ciCount;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ       Process Changes to the mid Buffer                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void MidBufDelta(ScanState ss) {
 // here we have a re-sync so r1 and r2 have good values

 int oldLen = ss.r1 - ss.p1;
 int newLen = ss.r2 - ss.p2;

        // Simple replace of the same length into the same positions
 if (oldLen == newLen) {
   ss.p1 = Replace("MBD1", ss, ss.p1, newLen, ss.p2);
   ss.p2 += newLen;

        // replace a string with a shorter one
 } else if (oldLen > newLen ) {
   ss.p1 = Replace("MBD2", ss, ss.p1, newLen, ss.p2);
   ss.p2 += newLen;
   ss.p1 = Delete("MBD2", ss, ss.p1, oldLen-newLen);

        // re-sync futher down in old Buffer so we need to delete Some
 } else if ((ss.p1 == ss.p2) && (ss.r1 > ss.r2)) {
   ss.p1 = Delete("MBD3", ss, ss.p1, ss.r1 - ss.p1);

        // replace a string with a longer one
 } else if (oldLen < newLen) {
   ss.p1 = Replace("MBD4", ss, ss.p1, oldLen, ss.p2);
   ss.p2 += oldLen;
   Insert("MBD4", ss, ss.p1, newLen-oldLen, ss.p2);
   ss.p2 = ss.r2;

        // replace a string with a shorter one
 } else if ((ss.r1 - ss.p1) < (ss.r2 - ss.p2)) {
   ss.p1 = Replace("MBD5", ss, ss.p1, newLen, ss.p2);
   ss.p2 += newLen;
   ss.p1 = Delete("MBD5", ss, ss.p1, oldLen-newLen);


 } else {
   Log.Err(203, "MidBufDelta unresolved condition");
   ss.Display("Error 203");
   Log.Close();
   System.exit(8);
 }

}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ       Process Changes to the End-of-Buffer                         บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void EndBufDelta(ScanState ss) {
 // here we do not have a re-sync so the r1 and r2 values are void

 int lenOld = ss.numReadOld - ss.p1;
 int lenNew = ss.numReadNew - ss.p2;

     // if to replace to the end of the buffer - same Length
 if ( lenOld == lenNew ) {
   ss.p1  = Replace("EBD1", ss, ss.p1, lenOld, ss.p2);
   ss.p2 += lenOld;

     // if to replace to the end of the buffer with a shorter length
 } else if (lenOld > lenNew) {
   ss.p1 = Replace("EBD2", ss, ss.p1, lenNew, ss.p2);
   ss.p1 = Delete("EBD2",  ss, ss.p1, lenOld - lenNew);
   ss.p2 = ss.numReadNew;

     // if to replace to the end of the buffer with a longer length
 } else if (lenOld < lenNew) {
   ss.p1 = Replace("EBD3", ss, ss.p1, lenOld, ss.p2);
   ss.p2 += lenOld;
   Insert("EBD3", ss, ss.p1, lenNew - lenOld, ss.p2);
   ss.p1 = ss.numReadOld;
   ss.p2 = ss.numReadNew;

 } else {
   Log.Err(204, "EndBufDelta unresolved condition");
   ss.Display("  ");
   Log.Close();
   System.exit(8);
 }

}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Replace some number of bytes                         บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int Replace(String msg, ScanState ss, int relTargetPos, int length, int relSrcPos) {

 if (debug)
   Log.Both(msg + " Replace  Abs/Pos="+(ss.totReadOld+relTargetPos)+"/"+relTargetPos+ "  Length="+length +"  srcPos="+relSrcPos);

 byte[] ba = new byte[length];

 for (int i=0; i < length; i++)
   ba[i] = ss.newBuf[relSrcPos + i];

                           // action index length data
 ChangeItem ci = new ChangeItem(Log, HelperList.k_Replace, ss.totReadOld + relTargetPos, length, ba);
 ss.ciCount++;
 ss.deltaSize += (ci.k_OverHead + length);
 ci.toStream(ss.dos);

 return relTargetPos + length;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Delete some number of bytes                          บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int Delete(String msg, ScanState ss, int relTargetPos, int length) {

 if (debug)
   Log.Both(msg +" Delete   Abs/Pos="+(ss.totReadOld+relTargetPos)+"/"+relTargetPos+ "  Length="+length);

 ChangeItem ci = new ChangeItem(Log, HelperList.k_Delete, ss.totReadOld + relTargetPos, length);
 ss.ciCount++;
 ss.deltaSize += (ci.k_OverHead + 1);
 ci.toStream(ss.dos);

 return relTargetPos + length;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Insert some number of bytes                          บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void Insert(String msg, ScanState ss, int relTargetPos, int length, int relSrcPos) {

 if (debug)
   Log.Both(msg+" Insert   Abs/Pos="+(ss.totReadOld+relTargetPos)+"/"+relTargetPos+ "  Length="+length +"  srcPos="+relSrcPos);

 byte[] ba = new byte[length];

 for (int i=0; i < length; i++)
   ba[i] = ss.newBuf[relSrcPos + i];

 ChangeItem ci = new ChangeItem(Log, HelperList.k_Insert, ss.totReadOld + relTargetPos, length, ba);
 ss.ciCount++;
 ss.deltaSize += (ci.k_OverHead + length);
 ci.toStream(ss.dos);

 return;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ              Xlate CRLF to BangBang for Debug Display              บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String XlateCRLF(byte[] byteArray, int length) {

 String temp = new String(byteArray, 0, length);

 temp = temp.replace('\n', '!');  // newLine
 temp = temp.replace('\r', '!');  // carrage Return

 return temp;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ             Find the next re-synchronizing character               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean ReSync(ScanState ss) {

 // ss.p1 and ss.p2  are the current scan position in the buffer

                   // determine scan length for this buffer

 //  are the number of bytes remaining in the buffer greater than the re-sync length
 int z1 = ( (ss.numReadOld - ss.p1) > reSyncLen) ? ((ss.numReadOld - ss.p1) - reSyncLen) : 0;
 int max = reSyncScan - reSyncLen;      // this would be the maximum scan length      TBD make it final
 int endScan = (z1 > max ) ? max : z1;  // take the lesser of the two


 if (debug)
   Log.Both("Re-sync start p1="+ ss.p1+" p2=" + ss.p2 +" scanEnd=" + (endScan+ss.p1)) ;

 endScan += ss.p1;

 for (int p1=ss.p1; p1 < endScan; p1++) {    // bump through the old buffer

   int scanEnd2 = ((ss.numReadNew - ss.p2) > reSyncLen) ? ((ss.numReadNew - ss.p2) - reSyncLen) : 0;
   scanEnd2 += ss.p2;

   for (int p2=ss.p2; p2 < scanEnd2; p2++) {

     if (ss.oldBuf[p1] == ss.newBuf[p2]) {  // if the first char match then check

       if (checkReSyncLen(ss, p1, p2)) {
         ss.r2 = p2;
         ss.r1 = p1;
         if (debug)
           Log.Both("        exit found at r1="+ss.r1+"  r2="+ss.r2);
         return true;
       }
     }

   }

 }

 if (debug)
   Log.Both("        exiting false");

 return false;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ       Check if this string matches for the specified length        บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 boolean checkReSyncLen(ScanState ss, int p1, int p2) {

 // here p1 points to the resync char and p2 points to a matching
 // char, we need to check if they are the same for the reSyncLen

 if (reSyncLen > (ss.numReadOld - p1) )
   return false;   // not enough characters remaining in the old buffer

 if (reSyncLen > (ss.numReadNew - p2) )
   return false;   // not enough characters remaining in the new buffer

 for (int i=0; i < reSyncLen; i++, p1++, p2++) {

   if (ss.oldBuf[p1] != ss.newBuf[p2])
     return false;

 }

 return true;
}

}

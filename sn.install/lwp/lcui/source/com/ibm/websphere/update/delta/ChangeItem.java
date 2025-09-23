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
//  ChangeItem
//
// This class describes and has supporting methods for the DeltaByte
// ChangeItem. Which is used to send only the bytes that change within
// a file or jar entry


import java.io.*;

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Change Item Class                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
/**
 *  
 */
class ChangeItem {
   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;

 final int k_FileSize = 0 ;
 // these are the action types for the ChangeItems
 final int k_CRC = 1 ;
 // these values are duplicated in HelperList.java
 final int k_Replace = 2 ;
 final int k_Insert = 3 ;
 final int k_Delete = 4 ;
 final int k_OverHead = 16 ;
                              // length(action) + length(index) + length(length)
                              //         4                8                4

// The ChangeItem class is used for the ByteDelta, both the Delta.class and
// Extractor.class use this class, as well as TestBD.class


 // Errors generated within this class are in the 700-750 range

   int    action;     // type of action
   long   index;      // index value into the original file
   int    length;     // length of the change
   byte[] data;       // the data we are dealing with

   // Note: The index variable is overloaded
   //       When Action = k_FileSize    Index contains the byte count of the file
   //       When Action = k_CRC         Index contains the CRC values for the file
   //       When Action = k_Replace, k_Insert, k_Delete
   //                                   Index contains the index into the file

   int ciRead;         // Count of how many ChangeItems we have read
   Logger Log;

              // this constructor is used for replace and insert
   public ChangeItem(Logger Log, int action, long index, int length, byte[] data) {
     this.Log    = Log;
     this.action = action;
     this.index  = index;
     this.length = length;
     this.data   = data;
   }
              // this constructor is used for delete and fileSize  -- no data
   public ChangeItem(Logger Log, int action, long index, int length) {
     this.Log    = Log;
     this.action = action;
     this.index  = index;
     this.length = length;
     this.data   = null;
   }
              // this constructor is for recreation
   public ChangeItem(Logger Log) {
     this.Log    = Log;
     this.action = 999;
     this.index  = 0;
     this.length = 0;
     this.data   = null;
     ciRead      = 0;
   }



   //********************************************************
   //  Display the content of this ci for Error and Debug
   //********************************************************
   public String toString() {

     StringBuffer stgB = new StringBuffer();

     switch (action) {
       case k_FileSize: stgB.append("FileSize len="); break;
       case k_CRC:      stgB.append("CRC      crc="); break;
       case k_Replace:  stgB.append("Replace  pos="); break;
       case k_Delete:   stgB.append("Delete   pos="); break;
       case k_Insert:   stgB.append("Insert   pos="); break;
       default:         stgB.append("dunknow  pos=");
     }

     stgB.append(index);
     stgB.append("  len=");
     stgB.append(length);
     stgB.append(" (");

     // if (data != null)
     // stgB.append( XlateCRLF(data, length));
     // else
     // stgB.append(" null ");
     //
     // stgB.append(")");

     return stgB.toString();
   }


   //********************************************************
   //  Output the content of this ci to a Stream
   //********************************************************
   public boolean toStream(DataOutputStream dos) {

     try {
       dos.writeInt(action);
       dos.writeLong(index);
       dos.writeInt(length);

       if ((length > 0) && (data != null))
         dos.write(data, 0, length);

     } catch (IOException ex) {
       Log.Err(700, "failure to write to DataOutputStream:", ex);
       return false;
     }

     return true;
   }


   //********************************************************
   //  Read a dataStream and populate the variables
   //********************************************************
   public int fromStream(DataInputStream dis) {
     // return 1 = alls well - we have data
     //        0 = EOF
     //       -1 = Error - message has been printed

     int bytesRead = 0;

     try {
       action = dis.readInt();
       index  = dis.readLong();
       length = dis.readInt();

       boolean dataExpected = false;
       if ((action == k_Replace) || (action == k_Insert ))
         dataExpected = true;

       if ((dataExpected) && (length > 0)) {
         data = new byte[length];
         bytesRead = dis.read(data, 0, length);

         if (bytesRead != length) {
           Log.Err(702, "Read failed, expected "+ length + " received " + bytesRead);
           return -1;
         }
       }

     } catch (EOFException ex) {
       // System.out.println("Eof Exception " + ex.getMessage());
       return 0;

     } catch (IOException ex) {
       Log.Err(701, "failure to read from DataInputStream:", ex);
       return -1;   // to indicate we had an error
     }

     ciRead++;
     return 1;
   }

}  // end of ChangeItem class



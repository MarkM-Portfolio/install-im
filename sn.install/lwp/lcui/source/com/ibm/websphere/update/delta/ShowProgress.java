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

import java.util.*;
import java.text.*;

/** Show Progress in a command line.
 * Passed in is a sentence with NLS Resource Bundle notation.

 <h2> String sentence = "{0} processing item {1} of {2}   {3}%" </h2>

 Where {0} is a time stamp.
       {1} is the incrementing value
       {2} is the maximum value

    long[] values = {4,0,100}
        first  value is the dispaly increment in seconds.
        second value is the starting value.
        third  value is the max value.

    ShowProgress sp = new ShowProgress(sentence, values);
    sp.setDaemon(true);  // this will let the thread die if the parent dies
    sp.start();


*/

public class ShowProgress extends Thread {

   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;

 String sentence;
 long[] values;

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                           Constructor                              บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ

    ShowProgress(String sentence, long[] values) {
      this.sentence = sentence;
      this.values   = values;

      if (this.values.length < 3)
        System.out.println("Error in ShowProgress constructor : values needs minimum of 3 elements.");

    }


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                           Run                                      บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void run() {

 long pct       = 0;
 long val2      = 0;

 int  padLength = 0;
 int  maxIdle   = 100;  // this is just to keep from looping forever
 int  idle      = 1;
 long lastValue = -1;


 do {

   if (idle <= 0) {
     System.out.println("ShowProgress.run() v"+ pgmVersion +" - thread terminating: value "+ values[1] +" has not changed in "+ (maxIdle * values[0]) +" secs.");
     return;
   }


   try {
     sleep(values[0] * 1000);
   } catch (InterruptedException ex) {
     System.out.println("Error in ShowProgress.run() v"+ pgmVersion +"- insomnia: " + ex.getMessage());
     return;
   }


   if (values[2] != val2) {
     val2 = values[2];
     String temp = FmtNum(val2,0,0,0);
     padLength = temp.length();
   }

   pct = (values[1] == 0) ? 0 : (values[1] * 100) / values[2];

   Date now = new Date();
   SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

   Object[] vals = {new String(df.format(now)),
                    new String(FmtNum(values[1], 0, padLength, 0)),
                    new String(FmtNum(values[2], 0, padLength, 0)),
                    new String(FmtNum(pct,0,3,0))};

   System.out.println(MessageFormat.format(sentence, (Object[]) vals));

   if (values[1] == lastValue)  // no change sence the the last time we were here
     idle--;
   else {
     lastValue = values[1];
     idle = maxIdle;
   }

   if ((values[1] == 0) && ( values[2] == 0)) // if the from and to values are zero, we bail out
     values[0] = 0;


 } while ((values[0] > 0) && (pct < 100 ));

}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         Format a number                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String FmtNum(long InValue, int DecPts, int Width, int Special) {

// Name:            FormatNum
//
// Description:     Put commas into a numeric value
//
// Parms:     #1  Value
//            #2  The number of decimal points
//            #3  The length ,in bytes, of the output field. If zero is specified
//                the the output length will be truncated to the shortest possible
//                length.
//            #4  Special Functions
//                0 = Nothing special
//                1 = Pad to min width with zeros


 int OutPutChars = 0;             // Count how many bytes we put into the output string
 int cnt = 0;
 int i;
 int j = 79;                      // Maximum length of the output string
 char  Wk1a[];
 String Result;

 char[] Wk2 = new char[80];

 char fillChar = (Special == 1) ? '0' : ' ';

 for (i=0; i < j; i++) {
   Wk2[i] = fillChar;
 }


 String Wk1 = String.valueOf(InValue);
 i          =  Wk1.length() - 1;
 Wk1a       = Wk1.toCharArray();


 if (DecPts != 0) {               // put in the decimal places, if any

   while (i < DecPts) {
     Result = "0";
     Result = Result.concat(Wk1);
     Wk1 = Result;
     i++;
   }

   Wk1a = Wk1.toCharArray();

   for (int k=0; k < DecPts; k++, i--, j-- ) {

     if (i > 0)
       Wk2[j] = Wk1a[i];
     else
       Wk2[j] = '0';

     OutPutChars++;
   }

   Wk2[j--] = '.';
   OutPutChars++;
 }


 while (i > -1) {
   if (cnt > 2) {
     Wk2[j--] = ',';
     OutPutChars++;
     cnt = 0;
   }

   Wk2[j--] = Wk1a[i--];
   OutPutChars++;
   cnt++;
 }


 if (Width == 0)
    i = OutPutChars;          // default to actual width
 else
    i = Math.max(Width, OutPutChars);

 return  String.valueOf(Wk2, 80-i, i);

}

}

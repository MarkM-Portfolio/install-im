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
// A class to contain Helper methods

// Note:
//    The DataOutputStream dos used in several methods is in support of Logit to support proxy operation
//    this can be null.

import java.io.*;
import java.lang.Integer;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/*
 * Helper1
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/Helper1.java, wps.base.fix, wps6.fix
 *
 * @author: Steve Pritko
 * @version 1.4
 * Date: 4/29/04
 */

/**
 * This is a collection of un-associated small helper classes. <Table CELLSPACING="25"> <TR> <TH> Method <Br> Name </TR> <TH> Description </TR> <TR> <TD> FmtNum          </TD> <TD> Will edit commas into a value. </TD> <TR> <TD> UnZip           </TD> <TD> Unzip a file. </TD> <TR> <TD> UnJar           </TD> <TD> UnJar a file. </TD> <TR> <TD> FindFiles       </TD> <TD> Will return hashtables of files and directories. </TD> <TR> <TD> CalcET          </TD> <TD> Calculate elapsed time. </TD> <TR> <TD> Padit           </TD> <TD> Pad a string. </TD> <TR> <TD> ParseFileSpec   </TD> <TD> Dissamble a FileSpec. </TD> <TR> <TD> Hex             </TD> <TD> Convert a string to Hex, for debugging. </TD> <TR> <TD> JavaVersion     </TD> <TD> Return the java Version  as an int. </TD> <TR> <TD> File2Vector     </TD> <TD> Read a file into a vector. </TD> <TR> <TD> Vector2File     </TD> <TD> Write a file from a vector. </TD> <TR> <TD> isCaseSensitive </TD> <TD> To determine if a platform is case sensitive. </TD> <TR> <TD> resovleMacro    </TD> <TD> Will replace $() strings in a line. </TD> <TR> <TD> getNextLogicalLine </TD> <TD> Will read and concatenate continuations. </TD> <TR> <TD> test4numerics   </TD> <TD> Will read and concatenate continuations. </TD> </Table> <Pre> Note: The DataOutputStream dos used in several methods is in support ofthe Logger to support proxy operation this can be null. </Pre>
 */
public class Helper1 extends Thread {
   /** program version.     depreciated */
   public static final String       version = "1.4";

   /** Program version.     */
   public static final String pgmVersion = "1.4" ;
   /** Program version.     */
   public static final String pgmUpdate = "4/29/04" ;

   protected static final String errPrefix = "Error in Helper1.class v"+ pgmVersion +" : ";

   static int    verbosity;
   static Logger Logit;
   static String FileSep;




//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                   Helper1  Constructors                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ

   Helper1(Logger Logit, int Verbosity ) {

      Helper1.Logit     = Logit;
      Helper1.verbosity = Verbosity;
      FileSep        = System.getProperty("file.separator"); // a slash
   }


   Helper1() {

      Logit         = null;
      verbosity     = 3;
      FileSep       = System.getProperty("file.separator"); // a slash
   }


/** Read a help file and display it.

*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ             Read a on-line help file and display it                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public void showHelpFile(String helpFileName, String topLine) {

      BufferedReader reader = null;

      if (helpFileName.startsWith("jar:")) {

         String inputJarFileName = determineOurSource();

         if (inputJarFileName == null) {
            System.out.println("Sorry, on-Line help is not currently available, it normally resides in "+ helpFileName );
            return;
         }

         JarFile jf = null;
         try {
            jf = new JarFile(inputJarFileName);
         } catch (IOException ex) {
            System.err.println(errPrefix +" Unable to open "+ inputJarFileName +", "+ ex.getMessage());
            return;
         }

         ZipEntry ze = jf.getEntry(helpFileName.substring(4));

         if (ze == null) {
            System.err.println( errPrefix +"The documentation file ("+ helpFileName +") could not be located.");
            return;
         }

         try {
            InputStream is = jf.getInputStream(ze);
            reader = new BufferedReader(new InputStreamReader(is));
         } catch (IOException ex) {
            System.out.println(errPrefix + "The documentation file ("+ helpFileName +") can not be opened : "+ ex.getMessage());
            return;
         }


      } else {  // it must be a file somewhere


         try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(helpFileName)));
         } catch (FileNotFoundException ex) {
            System.err.println(errPrefix +"The documentation file ("+ helpFileName +") can not be opened : "+ ex.getMessage());
            return;
         }
      }


      String aLine;
      System.out.println(topLine) ;

      try {
         while ((aLine = reader.readLine())  != null)
            System.out.println(aLine);

      } catch (IOException ex) {
         System.err.println(errPrefix +"IO reading help file ("+ helpFileName +") :"+  ex.getMessage());
      }

      return;
   }


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ        Determine the name of the jar file we are comming from      บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public String determineOurSource() {

      String inputJarFileName = null;

      Class theClass = new Helper1().getClass();
      String theClassName = theClass.getName();

      ClassLoader theClassLoader = theClass.getClassLoader();
      URL url = theClassLoader.getResource(theClassName + ".class");

      if ( url == null ) {
         System.err.println(errPrefix +"Cannot find my own URL!  (I am Helper1)");
         return theClassName + ".class";

      } else {

         String theURLString = url.toString();

         if ( theURLString.startsWith("jar:") ) {
            int fileEnd = theURLString.indexOf("!");

            if ( fileEnd > 0 ) {
               int start = 9; // point to start of path

               if (!isCaseSensitive())
                  start = 10;  // this is to bypass the leading slash for NT

               inputJarFileName = theURLString.substring(start, fileEnd);
            }
         }
      }

      return inputJarFileName;
   }


/** Unzip a file. dos may be null
  * This method does this.

*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         UnJar a File                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public int UnJar(String JarFileName, String Target, DataOutputStream dos) {

      int rc = 0;

      if (Target.endsWith(FileSep) == false)   // ensure target path ends with a pathSeperator
         Target = Target.concat(FileSep);

      Logit.Both("UnJaring " + JarFileName + " to " + Target, dos);

      int BufferSize = 2048;
      byte[] IOBuf = new byte[BufferSize];

      try {
         JarFile JF = new JarFile(JarFileName, false);
         Logit.Both("   Number of entries is " + JF.size(), dos);

         JarInputStream JIS = new JarInputStream(new FileInputStream(JarFileName), false);
         int Count = 0;
         JarEntry  JE;


         while ((JE = JIS.getNextJarEntry()) != null  ) {
            if (verbosity > 3)
               Logit.Both(++Count +" of "+ JF.size() + " JarEntry=" + JE, dos);


            if (JE.isDirectory() == true ) {
               String NewPath = Target+JE;
               Logit.Both("     Directory = " + NewPath, dos);
               File aFile = new File(NewPath);
               aFile.mkdirs();


            } else {        // it must be a file

               File WorkFile = new File(Target+JE);           // ensure that the directory exists
               String ABSPath = WorkFile.getAbsolutePath();
               int lastSlash = ABSPath.lastIndexOf(FileSep);
               String PathOnly = ABSPath.substring(0, lastSlash);
               File NewPath = new File(PathOnly);

               if (NewPath.exists() == false) {
                  Logit.Both("     Directory = " + ABSPath, dos);
                  NewPath.mkdirs();
               }


               BufferedOutputStream OutFile = new BufferedOutputStream(new FileOutputStream(Target+JE), BufferSize);

               boolean fNotDone = true;
               long TotRead = 0;


               while (fNotDone) {
                  int BytesRead = JIS.read(IOBuf, 0, BufferSize);

                  if (BytesRead > 0) {
                     TotRead += BytesRead;
                     OutFile.write(IOBuf, 0, BytesRead);

                  } else
                     fNotDone = false;
               }
               if (verbosity > 3)
                  Logit.Both("   " + TotRead +  " bytes read", dos);
               OutFile.close();

            }

         }

         JIS.close();

      } catch (IOException e) {
         Logit.Both("Error -- IOException " + e + "\n", dos);
         rc = 8;
      }

      return rc;
   }



/** UnZip a file. dos may be null. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         UnZip a File                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public int UnZip(String ZipFileName, String Target, DataOutputStream dos) {

      int rc = 0;

      if (Target.endsWith(FileSep) == false)   // ensure target path ends with a pathSeperator
         Target = Target.concat(FileSep);

      Logit.Both("UnZipping " + ZipFileName + " to " + Target, dos);

      int BufferSize = 2048;
      byte[] IOBuf = new byte[BufferSize];

      try {
         ZipFile ZF = new ZipFile(ZipFileName);
         Logit.Both("   Number of entries is " + ZF.size(), dos);

         ZipInputStream ZIS = new ZipInputStream(new FileInputStream(ZipFileName));
         int Count = 0;
         ZipEntry  ZE;


         while ((ZE = ZIS.getNextEntry()) != null  ) {
            if (verbosity > 3)
               Logit.Both(++Count +" of "+ ZF.size() + " ZipEntry=" + ZE, dos);

            if (ZE.isDirectory() == true ) {
               String NewPath = Target+ZE;
               Logit.Both("     Directory = " + NewPath, dos);
               File aFile = new File(NewPath);
               aFile.mkdirs();


            } else {        // it must be a file

               File WorkFile = new File(Target+ZE);           // ensure that the directory exists
               String ABSPath = WorkFile.getAbsolutePath();
               int lastSlash = ABSPath.lastIndexOf(FileSep);
               String PathOnly = ABSPath.substring(0, lastSlash);
               File NewPath = new File(PathOnly);

               if (NewPath.exists() == false) {
                  Logit.Both("     Directory = " + ABSPath, dos);
                  NewPath.mkdirs();
               }

               BufferedOutputStream OutFile = new BufferedOutputStream(new FileOutputStream(Target+ZE), BufferSize);

               boolean fNotDone = true;
               long TotRead = 0;


               while (fNotDone) {
                  int BytesRead = ZIS.read(IOBuf, 0, BufferSize);

                  if (BytesRead > 0) {
                     TotRead += BytesRead;
                     OutFile.write(IOBuf, 0, BytesRead);

                  } else
                     fNotDone = false;
               }
               if (verbosity > 3)
                  Logit.Both("   " + TotRead +  " bytes read", dos);

               OutFile.close();

            }

         }

         ZIS.close();

      } catch (IOException e) {
         Logit.Both("Error -- IOException " + e + "\n", dos);
         rc = 8;
      }

      return rc;
   }


/** Pad a string.
<code>
  Parm 1 is the input string.
  Parm 2 is the output length.
  Parm 3 if the left justify the value.

  The fill character will be a blank
</Code>
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                            Pad a string                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public String Padit(String input, int length,  boolean leftJustify) {

      return Padit(input, length,leftJustify, ' ');
   }

/** Pad a string.
<pre>
  Parm 1 is the input string.
  Parm 2 is the output length.
  Parm 3 if the left justify the value.
  Parm 4 is the fill character.

</Pre>
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                            Pad a string                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public String Padit(String input, int length,  boolean leftJustify, char ch) {

      if (input == null)
         input = "";

      if (input.length() >= length)
         return input;

      int delta = length - input.length();
      StringBuffer sb = new StringBuffer(delta);

      for (int i=0; i<delta; i++)  sb.append(ch);

      if (leftJustify)
         return input.concat(sb.toString());
      else
         return sb.toString().concat(input);

   }


/** The FmtNum method will put commas into a number for display.
<pre>
 Parm 1  The value to process.

 Parm 2  The number of decimal places.

 Parm 3  The width of the resulting field. If zero is provided then
         the width will be the minimum width to contain the value.

</Pre>
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Format a number                              บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public String FmtNum(long InValue, int DecPts, int Width) {

      return FmtNum(InValue, DecPts, Width, 0);

   }

/** The FmtNum method will put commas into a number for display.
<pre>
 Parm 1  The value to process.

 Parm 2  The number of decimal places.

 Parm 3  The width of the resulting field. If zero is provided then
         the width will be the minimum width to contain the value.

 Parm 4 Special Processing - none implemented at this time.

</Pre>
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Format a number                              บ
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

/** Calculate an elapsed time.
 Passed in is a long startTime = System.currentTimeMillis()

 Returned is a String in the form of HH:MM:SS.000.
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                  Calculate elapsed time                       บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public String CalcET(long t0) {

      // Sample of start time
      //      long startTime = System.currentTimeMillis();
      //
      // returns string of hh:mm:ss.sss

      long msecPerHour = 3600000; // milliseonds per hour
      long msecPerMin  = 60000;   // milliseconds per minute
      long msecPerSec  = 1000;    // milliseconds per second

      String hours, minutes, seconds;
      long work, delta = System.currentTimeMillis() - t0;


      if ( delta > msecPerHour) {
         work = delta / msecPerHour;
         delta = delta - (work * msecPerHour);
         hours = (work < 10 ) ? "0" + Integer.toString((int) work) : Integer.toString((int)work);
      } else
         hours = "00";


      if ( delta > msecPerMin ) {
         work = delta / msecPerMin;
         delta = delta - (work * msecPerMin);
         minutes = (work < 10 ) ? "0" + Integer.toString((int) work) : Integer.toString((int)work);
      } else
         minutes = "00";


      if (delta > msecPerSec) {
         work = delta / msecPerSec;
         delta = delta - (work * msecPerSec);
         seconds = (work < 10 ) ? "0" + Integer.toString((int) work) : Integer.toString((int)work);
      } else
         seconds = "00";


      return hours +":"+ minutes +":"+ seconds + "."+ delta;

   }

/**  Find files and directories.

 <Pre>
  The pattern consists of Drive/path/filespec, If the pattern ends in a slash then
  all files will be assumed.

  The two hashtables passed in will receive the found data. The key is the path with
  all slashes translated to astericks, and the value is the actual path.

  If case sensitivity is false, then all keys will be translated to lower case.

 returned is a count of items inspected.

 </Pre>
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Find Files                                   บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public int FindFiles(String pattern, boolean fRecurse, HashMap hFiles, HashMap hDirectories, boolean fCaseSensitive, boolean fDebug) {

      String pathSep = System.getProperty("file.separator");

      Vector pathPattern = new Vector(); // holds tokens in the path
      Vector filePattern = new Vector(); // holds tokens of the file name

      int lastSlash    = pattern.lastIndexOf(pathSep);
      String pathPart  = null;
      String filePart  = null;
      File startingDir = null;    // where we start looking

      if (fCaseSensitive == false) {
         pattern = pattern.toLowerCase();
      }

      if (fDebug)
         System.out.println("Debug  Starting pattern ("+pattern+") caseSensitivity "+ fCaseSensitive);


      // first we need to seperate the pattern into pathPart and filePart

      if (pattern.endsWith(pathSep) == true ) { // if it ends with a slash its a directory, so find everything

         pathPart = pattern.substring(0,  (pattern.length() -1)  );
         filePart = "*";    // we'll take all files

      } else {

         if (lastSlash == -1) { // here we do not have a path, just a filespec

            pathPart = "*";  // take all paths

            if ((pattern.length() > 1) && (pattern.substring(1,2).equals(":") == true)) { // could be c:SomeFile.txt, no slash after the drive
               filePart = pattern.substring(2);
               pathPart = pattern.substring(0, 2);

            } else {
               filePart = pattern;
               pathPart = ".";        // no drive or path was specified so the current dir is assumed
            }

         } else {  // here we have a path and a filespec
            pathPart = pattern.substring(0, lastSlash);
            filePart = pattern.substring(lastSlash+1);
         }
      }


      MakeTokens(pathPart, pathPattern, fDebug);
      MakeTokens(filePart, filePattern, fDebug);


      String sd;
      if (pathPattern.size() > 0)
         sd = (String) pathPattern.elementAt(0);
      else
         sd = "";

      if ((!isCaseSensitive()) && (!sd.endsWith("\\")) )
         sd = sd.concat("\\");

      startingDir = new File(sd).getAbsoluteFile();

      if (fDebug)
         System.out.println("Debug absolute startingDir=("+startingDir.toString()+")");


      int sdLen = startingDir.toString().length(); // this is the length of the starting Directory

      int searchCount = FFProcessDir(startingDir, sdLen, fRecurse,
                                     pathPattern, filePattern, hFiles,
                                     hDirectories, fCaseSensitive, fDebug);

      return searchCount;
   }


//ฺฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฟ
//ณ             Parse the provided string into tokens                  ณ
//ภฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤู
   protected int MakeTokens(String pattern, Vector vec, boolean fDebug) {

      if (pattern != null) {
         // here we wish to break up the pattern into elements and put each element into
         // the xxxPattern vector. Siginificent elements are asterick, question mark,
         // and everything else.

         if (fDebug)
            System.out.println("Debug tokenizing ("+pattern+")" );

         String temp = "";
         boolean tempHasData = false;

         for (int i = 0; i < pattern.length(); i++) {
            char theChar  = pattern.charAt(i);

            if (theChar == '*') {
               if (tempHasData) {
                  vec.addElement(temp);
                  tempHasData = false;
                  temp = "";
               }
               vec.addElement("*");


            } else if (theChar == '?') {
               if (tempHasData) {
                  vec.addElement(temp);
                  tempHasData = false;
                  temp = "";
               }
               vec.addElement("?");


            } else {
               temp = temp.concat(String.valueOf(theChar));
               tempHasData = true;
            }

         }

         if (tempHasData) {
            vec.addElement(temp);
            tempHasData = false;
         }

      }


      if (fDebug) {
         for (int loop = 0; loop < vec.size(); loop++) {
            System.out.println("Debug  token " + loop +"  "+ (String) vec.elementAt(loop));
         }
      }


      return vec.size();
   }


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                   Scan the Driectory recursively                   บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   protected static int FFProcessDir(File SomeDirectory, int sdLen, boolean fRecurse, Vector pathToks, Vector fileToks, HashMap hFiles, HashMap hDirectories, boolean fCaseSenisitivy, boolean fDebug) {

      String pathSep = System.getProperties().getProperty("file.separator");
      char cPathSep  = pathSep.charAt(0);
      String [] files = null;
      String StartingDir, Key;
      File fileObject;
      int searchCount = 0;


      // files = SomeDirectory.list(filterObj); // we are not using the filter as we are unable to tell if it a directory
      files = SomeDirectory.list();

      if (files != null) {
         StartingDir = SomeDirectory.toString();

         if (StartingDir.endsWith(pathSep)) {    // Strip any trailing slash
            StartingDir = StartingDir.substring(0, StartingDir.length() -1);
         }


         for (int q = 0; q < files.length; q++) {

            fileObject = new File(StartingDir + pathSep + files[q]);
            Key =  fileObject.toString().replace(cPathSep , '*').substring(sdLen);

            if (fileObject.isDirectory()) {
               hDirectories.put(Key, fileObject);
               if (fRecurse) {
                  // if (acceptFile(files[q], pathToksCnt, pathToks, fDebug)) {
                  int sc = FFProcessDir(fileObject, sdLen, fRecurse, pathToks, fileToks,
                                        hFiles, hDirectories, fCaseSenisitivy, fDebug);
                  searchCount +=sc;
                  // }
               }
            }


            if (fileObject.isFile()) {
               searchCount++;
               if (FFacceptFile(files[q], fileToks, fCaseSenisitivy, fDebug)) {
                  if (fCaseSenisitivy)
                     hFiles.put(Key, fileObject);
                  else
                     hFiles.put(Key.toLowerCase(), fileObject);
               }
            }

            // long Size = fileObject.length();
            // long LastTime = fileObject.lastModified();
            // Date DateO = new Date(LastTime);
            // int Year  = DateO.getYear();
            // int Month = DateO.getMonth();
            // int DofM  = DateO.getDate();
            // int HH    = DateO.getHours();
            // int MM    = DateO.getMinutes();
            // int SS    = DateO.getSeconds();
            // System.out.println(FmtNum.FormatNum(q+1,0,6,0) + ObjectType + FmtNum.FormatNum(Size,0,12,0) +" "+FmtNum.FormatNum(Year,0,2,1)+"/"+FmtNum.FormatNum(Month+1,0,2,1)+"/"+FmtNum.FormatNum(DofM,0,2,1)+ " " + FmtNum.FormatNum(HH+3,0,2,1)+":"+FmtNum.FormatNum(MM,0,2,1)+":"+FmtNum.FormatNum(SS,0,2,1)+ " " + fileObject);

         }
      }

      return searchCount;
   }



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ           Determine if we want to accept the file                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   protected static boolean FFacceptFile(String name, Vector toks, boolean fCaseSenisitivy, boolean fDebug) {

      boolean isNotTranslated = true; // to indicate if the name has been translated to lower case
      boolean status = true;
      int nameLen = name.length();

      if (fDebug)
         System.out.println(" Examining ("+ name + ")");

      boolean mustScan= false;  // if we need to scan for the first occurance
      int ndx = 0;              // index of where we are looking into name



      for (int loop=0; loop < toks.size(); loop++) {

         if (status == false) // get out as soon as we have a failure
            break;

         String tok = (String) toks.elementAt(loop);  // this would be the token we are looking for

         if (tok.equals("*") == true) {
            mustScan = true;


         } else if (tok.equals("?") == true) {
            ndx++;   // bump to the next char

            if (ndx > name.length())
               status = false;

            mustScan = false;   // reset scan request

         } else {

            int tokLength = tok.length();

            if (tokLength > (nameLen - ndx) ) {  // ensure we have enough characters
               status = false;
               if (fDebug)
                  System.out.println("  end of text  searching for ("+tok+") @ position ("+ ndx+")" );

            } else {

               if ((fCaseSenisitivy == false) && (isNotTranslated))
                  name = name.toLowerCase();


               if (mustScan) {  // here we need to scan for what we are looking for

                  int pos = name.indexOf(tok, ndx);
                  if (pos == -1 ) {
                     status = false;

                     if (fDebug)
                        System.out.println("  scan failed searching for ("+tok+") starting @ "+ ndx );

                     break;

                  } else {
                     ndx = pos + tokLength;
                  }

                  mustScan = false;   // reset scan request

               } else {

                  if (tok.equals(name.substring(ndx, (ndx+tokLength))) == true ) {
                     if (fDebug)
                        System.out.println("  token " + tok + " found @ "+ ndx  );

                     ndx += tokLength;

                  } else {
                     status = false;
                     if (fDebug)
                        System.out.println("  failed expecting " + tok +" @ "+ ndx );

                  }
               }

            }
         }

      }


      if (ndx != nameLen) {
         if (mustScan == false) {
            status = false;

            if (fDebug)
               System.out.println("  Length failed nameLen = "+ nameLen + " ndx = " + ndx );
         }
      }


      if (fDebug)
         System.out.println("               " + status );

      return status;
   }

/** To access the fileNameParts array */
   public final int k_fullname = 0 ;

/** To access the fileNameParts array */ /** To access the fileNameParts array */
   public final int k_drive = 1 ;

/** To access the fileNameParts array */ /** To access the fileNameParts array */
   public final int k_path = 2 ;

/** To access the fileNameParts array */ /** To access the fileNameParts array */
   public final int k_name = 3 ;

/** To access the fileNameParts array */ /** To access the fileNameParts array */
   public final int k_ext = 4 ;

/** To access the fileNameParts array */ /** To access the fileNameParts array */
   public final int k_elCount = 5 ;

/** Disassemble a filespec into drive path filename and extension.
  Input is the string filespec and a string array of k_elCount elements to
  receive the pieces parts. The elements of the array should be refercenced
  using the k_ variables.

  <pre>
  Inputs are:
     String  filespec
     String  partArray[k_elCount]  to receive the parts
     boolean debug option

     partArray[k_fullname]  is the filename plus extension seperated with a dot.
     partArray[k_drive]     is the drive letter followed by a colon, for Unix platforms
                            this element is set to no length.
     partArray[k_path]      is the path part with leading and trailing slash
     partArray[k_name]
     partAray[k_ext]

  </pre>
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ          Parse a file specification into Drive Path name           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public boolean ParseFileSpec(String filespecOrig, String array[] ,boolean fDebug) {
      // Notes:
      //        The path contains all slashes.
      //        UNC names are acceptable.

      boolean returnCode = true;
      int startOfPath = 0;
      int endOfPath   = 0;
      int startOfName = 0;
      int startOfExt  = 0;


      for (int i=0; i < 5; i++) {   // ensure array is cleared
         array[i] = "";
      }

      String localSlash   = System.getProperty("file.separator");
      String forigenSlash = (localSlash.equals("\\")) ? "/" : "\\";
      String filespec     = filespecOrig.replace(forigenSlash.charAt(0), localSlash.charAt(0));


      if (filespec.startsWith("\\\\" ) ) {   // if its a UNC Name  \\serverName\resource\

         startOfPath = filespec.indexOf("\\", 2);  // find end of ServerName
         if (startOfPath == -1 ) {
            startOfPath = 0;  //  its a bad name

         } else {
            startOfPath = filespec.indexOf("\\", startOfPath+1);  // find end of ServerName

            if (startOfPath == -1 )
               startOfPath = 0;  //  its a bad name
            else
               array[k_drive] = filespec.substring(0, startOfPath);  // UNC information
         }

      } else if ((filespec.length() > 1) && (filespec.substring(1,2).equals(":"))) {
         array[k_drive] = filespec.substring(0,2);
         startOfPath = 2;

      } else {
         array[k_drive] = "";
         startOfPath = 0;
      }


      endOfPath = filespec.lastIndexOf("\\");  // find the start of the name
      if (endOfPath == -1)
         endOfPath = filespec.lastIndexOf("/");

      if (endOfPath == -1) {            // no slash found - no path
         endOfPath = startOfPath;
         startOfName = startOfPath;

      } else {                          // slash found
         if (endOfPath < startOfPath) {  // found a slash before the colon
            endOfPath   = startOfPath;
            startOfName = startOfPath;
            returnCode  = false;

         } else {
            endOfPath++;  // to adjust for substring
            startOfName = endOfPath;
         }
      }

      array[k_path] = filespec.substring(startOfPath, endOfPath);

      array[k_fullname] = filespec.substring(startOfName);

      startOfExt = array[k_fullname].lastIndexOf(".");

      if (startOfExt == -1) {
         array[k_name] = array[k_fullname];
         array[k_ext] = "";
      } else {
         array[k_name] = array[k_fullname].substring(0, startOfExt);
         array[k_ext] = array[k_fullname].substring(startOfExt+1);
      }

      if (array[k_path].length() == 2 ) {  // to change "\\" to "\"
         if (array[k_path].substring(0,1).equals(array[k_path].substring(1,2))) {
            array[k_path] = array[k_path].substring(1);
         }
      }


      if (fDebug) {
         System.out.println("ParseFileSpec debug input - (" + filespec+")");
         System.out.println("     1(" + array[k_drive]    +
                            ") 2(" + array[k_path]     +
                            ") 3(" + array[k_name]     +
                            ") 4(" + array[k_ext]      +
                            ") 0(" + array[k_fullname] + ")" );
      }


      return returnCode;
   }


/** Convert a string to  displayable Hex strings for debug.
 <pre>
   Parm 1  The string value to be displayed as hex.

   Parm 2  If to return the values as decimal values rather than hex values.

   Parm 3  Mimimum width of each byte.

   Returned is a string of the translation.

</pre>
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Convert a string to Hex                         บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public static String Hex(String string, boolean fDecimal, int width) {

      //  Parm 1 A string to convert to Hex representation
      //  Parm 2 if ya want decimal repesentation returned
      //  Parm 3 the minimum width for each element  typically 2 - 4
      //
      //   returned is a string

      String[] hex = {"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
         "10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
         "20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
         "30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
         "40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",
         "50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
         "60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",
         "70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
         "80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",
         "90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
         "A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",
         "B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
         "C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",
         "D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
         "E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",
         "F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"};

      byte[] strBytes = string.getBytes();
      StringBuffer hexString = new StringBuffer((strBytes.length * width) + 5);

      if (fDecimal)
         hexString.append("dec ");
      else
         hexString.append("  x'");

      for (int i=0; i < strBytes.length; i++) {
         Byte nextByte = new Byte(strBytes[i]);

         int value = nextByte.shortValue();
         String work;

         if (fDecimal)
            work = nextByte.toString();
         else
            work = hex[value];

         while (work.length() < width) {
            // work = work.concat(" ");
            work = new String(" ").concat(work);
         }

         hexString.append(work);
      }

      if (!fDecimal)
         hexString.append("'");

      return hexString.toString();
   }


/** A utility method to get the current time in SQL Timestamp form.  YYYY-mm-dd-HH:mm:ss.ttt
   If zero is passed in then the current time will be returned.
*/
//ฺฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฟ
//ณ        Get the current time and Format it to SQL Format            ณ
//ภฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤฤู
   public String getSQLTimeStamp(long userTime) {

      Date theTime;

      if (userTime == 0)
         theTime = new Date();
      else
         theTime = new Date(userTime);

      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.S");
      return df.format(theTime);

   }


/** This method will return the java version as an int so we can to a compare on it. */
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ           Get the Java version and return as an Int           บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public int GetJavaVersion() {

      // We are assuming three sets of digits seperated by periods
      //   ie   1.2.2

      int Pos=0, iVers=0;
      int nDx[] = {100, 10, 1, 0, 0, 0};
      int count = 3;  // the number of positions we get

      String jVersion = System.getProperty("java.version");

      if (jVersion.startsWith("JavaVM-")) {   // this string is returned by HPUX
         jVersion = jVersion.substring(7);
      } else if (jVersion.startsWith("JPSE_")) { // this string is returned by Solaris
         jVersion = jVersion.substring(5);
      }

      StringTokenizer Tok = new StringTokenizer(jVersion, ".");

      while ((Tok.hasMoreTokens()) && (count > 0)) {

         String sWork = Tok.nextToken();
         char[] ca = sWork.toCharArray();

         for (int i=0; i<ca.length; i++ ) {
            if (Character.isDigit(ca[i]) == false ) {
               sWork = "0";
            }
         }

         iVers += (Integer.parseInt(sWork.trim()) * nDx[Pos++]);
      }

      return(iVers);

   }


/** This method will convert a Version.release.modification.submod to an int array.

  * returned boolean true  = all went well
  *                  false = we had problems
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ           Converat a string Version to an int array           บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public boolean convertVRM(String strVersion, int[] VRM ) {

      boolean returnValue = true;

      int index = 0;

      StringTokenizer toks = new StringTokenizer(strVersion, ".");

      while ((toks.hasMoreTokens() && index < VRM.length)) {
         String strNum = toks.nextToken();
         try {
            VRM[index] = Integer.parseInt(strNum);
         } catch (NumberFormatException ex) {
            VRM[index] = 0;
            returnValue = false;
         }
         index++;
      }

      if (index != VRM.length )
         returnValue = false;


      return returnValue;
   }



/** This method will numerically compare two string versions.
  * version are assumed to be Version.Release.Modification

  * returned 0 = Failure
  *          1 = first version is bad
  *          2 = second version is bad
  *          3 = first Version is less than the second Version
  *          4 = first Version is equal to second Version
  *          5 = first Version is greater than the second Version
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Compare two versions                       บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public int compareVRM(String v1, String v2) {

      int returnCode = 0;
      int[] iV1 = {0,0,0};
      int[] iV2 = {0,0,0};

      if (!convertVRM(v1, iV1))
         returnCode = 1;

      if (!convertVRM(v2, iV2))
         returnCode = 2;

      if (returnCode == 0) {

         returnCode = 4;  // assume equal until we determine different

         for (int i=0; i < iV1.length; i++) {

            if (iV1[i] != iV2[i]) {
               if (iV1[i] < iV2[i]) {
                  returnCode = 3;
               } else
                  returnCode = 5;
            }

            if (returnCode != 4) // if we have an inequality then exit
               break;

         }

      }

      return returnCode;
   }


/** This a convenience class to see if the first Version is Greater than the Second.
  * version are assumed to be Version.Release.Modification
  * false is returned if any errors have occured.

  * returned false = Nop
  *          true  = Yes
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ         Is the first Version greater than the Second          บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public boolean v1GTv2(String v1, String v2) {

      if (compareVRM(v1, v2) == 5)
         return true;
      else
         return false;

   }



/** Determine if this platform is case sensitive. */
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            Is this platform case sensitive                    บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public boolean isCaseSensitive() {

      //                 Arch     Name
      // OE or USS       390      os/390
      // Linux on 390    s390     Linux


      String osName = System.getProperty("os.name");

      if ( osName.startsWith( "Windows" ) ) {
         return false;
      }
      /*
      if ( osName.equals("Windows NT"))
         return false;

      if ( osName.equals("Windows XP"))
         return false;

      else if ( osName.equals("Windows 2000"))
         return false;
      */
      else if ( osName.equals("Linux"))
         return true;

      else if ( osName.equals("Solaris"))
         return true;

      else if ( osName.equals("SunOS"))
         return true;

      else if ( osName.equals("AIX"))
         return true;

      else if ( osName.equals("HP-UX"))
         return true;

      else if ( osName.equals("OS/2"))
         return false;

      else if ( osName.equals("os/390"))
         return true;

      else
         System.err.println("Error -- Un-recognized os.name in Helper1 v"+ pgmVersion +" ("+ osName +")");

      return true;   // assume true
   }


/** Get unix style file permissions
The returned value is the 3 diget octal value to be used with chmod.
if the returned value is greater than 777 then this is a symbolic link
and to make the value usable, subtrack 1000 from the value.
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ  Get the permissions of a File and return as 3 octal chars    บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public int getPermissions(File aFile, boolean debug) {

      int v0 = 0,   // to indicate if this is a Symbolic link value is either a one or a zero
      v1 = 0,   // User Part
      v2 = 0,   // Group part
      v3 = 0;   // World Part

      if (debug)
         System.out.println("Debug - getPermissions(File "+ aFile.getAbsolutePath() +")");


      if (isCaseSensitive()) {   // this would be for all unix platforms

         boolean adjust4Platforms = true;
         boolean showLaunchMsg    = (debug) ? true : false;
         boolean displayStdOut    = (debug) ? true : false;
         boolean displayStdErr    = (debug) ? true : false;

         Vector msgBuffer = new Vector();
         Vector logBuffer = new Vector();

         String[] cmd = { "ls", "-l", aFile.getAbsolutePath()};

         ExecCmd exCmd = new ExecCmd(adjust4Platforms, showLaunchMsg);
         int rc = exCmd.Execute(cmd, displayStdOut, displayStdErr, msgBuffer, logBuffer);

         if ((rc == 0) && (msgBuffer.size() > 0 )) {
            String line = (String) msgBuffer.elementAt(0);
            if (line.length() > 11) {

               char[] chars = line.toCharArray();
               // the line should look like  drwxrwxrwx

               v0 = (chars[0] == 'l') ?    1 :  0;    // to indicate if this is a link

               v1 = (chars[1] == 'r') ?    4 :  0;    // User Part
               v1 = (chars[2] == 'w') ? v1+2 : v1;
               v1 = (chars[3] == 'x') ? v1+1 : v1;

               v2 = (chars[4] == 'r') ?    4 :  0;    // Group Part
               v2 = (chars[5] == 'w') ? v2+2 : v2;
               v2 = (chars[6] == 'x') ? v2+1 : v2;

               v3 = (chars[7] == 'r') ?    4 :  0;    // World Part
               v3 = (chars[8] == 'w') ? v3+2 : v3;
               v3 = (chars[9] == 'x') ? v3+1 : v3;
            }
         }

      } else { // for Windows and OS/2 all three sets are the same

         if (aFile.isHidden()) {  // we can not do anything
            v1 = 0;
            v2 = 0;
            v3 = 0;

         } else if (aFile.canWrite()) {  // if we can write we can do everything
            v1 = 7;
            v2 = 7;
            v3 = 7;

         } else {  // this would be read and Execute
            v1 = 5;
            v2 = 5;
            v3 = 5;
         }
      }

      int permissions = (v0 * 1000) + (v1 * 100) + (v2 * 10) + v3;

      if (debug)
         System.out.println("Debug - getPermissions() - returning "+ permissions);

      return permissions;
   }


/** This method will resolve a macro from a hash table or System environment.
 The passed in hash table may be null. returned is the string with macros replaced.
 The macros may be of the $() form or of the $MACRONAME form
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            Resolve Macro in find and replace Strings          บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   static public String resolveMacro(String line, Hashtable ht) {

      int dollarPos = line.indexOf("$");
      if (dollarPos == -1)    // no macros
         return line;

      do {
         int resume = 0;
         int endPos = -1;
         String macroName = null;

         if (line.charAt(dollarPos+1) == '(') {  // do we have a $() form
            endPos    = line.indexOf(")", dollarPos+2);
            macroName = line.substring(dollarPos + 2, endPos);
            resume    = endPos+1;

         } else {    // it must be of the $MacroName form
            int i = 0;
            for (i=dollarPos+1; i < line.length(); i++) {

               if (!Character.isJavaIdentifierPart(line.charAt(i)))
                  break;

               if (line.charAt(i) == '$')
                  break;

            }
            endPos = i;

            if (endPos > dollarPos) {
               macroName = line.substring(dollarPos + 1, endPos);
               resume = endPos;
            }
         }


         if ( macroName != null ) {  // we Have identified a macro
            String newValue = null;

            if (ht != null)          // try to get it from the Hashtable first
               newValue = (String) ht.get(macroName);

            if (newValue == null )  // try to get it from the System environment Second
               newValue = System.getProperty(macroName);

            if ( newValue != null ) {
               line =  line.substring(0, dollarPos) + newValue + line.substring(resume);
            }
         }

         dollarPos = line.indexOf("$", dollarPos+1);
      } while (dollarPos >= 0 );


      return line;
   }




/** Read a file into a vector.
<pre>
    Parm 1 is a Vector to receive error messages, if null is passed in
           error messages will be sent to StdErr

    Parm 2 is the file to read.

    Returned is the vector or null.
</pre>
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                  read a file into a vector                    บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public Vector file2Vector(Vector msgVector, String sourceFileName) {

      String methodName = "file2Vector";

      if (sourceFileName == null) {
         vError(msgVector, methodName, "File2Vector -- source File is null.");
         return null;
      }


      Vector sourceLines = new Vector();
      BufferedReader reader;

      File sourceFile = new File(sourceFileName).getAbsoluteFile();

      try {
         reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
      } catch ( FileNotFoundException ex ) {
         vError(msgVector, methodName, "FileNotFoundException for (" + sourceFile.toString() +"); "+ ex.getMessage());
         return null;
      }

      try {
         String workLine;
         while ( (workLine = reader.readLine()) != null )
            sourceLines.addElement(workLine);

      } catch ( IOException ex ) {
         vError(msgVector, methodName, "IOException reading (" + sourceFile.toString() +"); "+ ex.getMessage());
         return null;

      } finally {
         try {
            reader.close();
         } catch ( IOException ex ) {
            vError(msgVector, methodName, "IOException closing (" + sourceFile.toString() +"); "+ ex.getMessage());
            return null;
         }
         reader = null;
      }

      return sourceLines;
   }


/** Write a file from a vector.
<pre>
  Parm 1 is a Vector to receive error messages, if null is passed in
         error messages will be sent to StdErr.

    Parm 2 is a vector which contains lines to write.

    Parm 3 is the output file.

    Parm 4 is a bool which signifies if to append to the file.

    Returned is boolean success.
 </pre>
*/
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            Write a file from a vector                         บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   public boolean vector2File(Vector msgVector, Vector sourceLines, String destFileName, boolean appendOption) {

      String methodName = "vector2File";

      if (destFileName == null) {
         vError(msgVector, methodName, "Vector2File -- destination fileName is null.");
         return false;
      }


      if (sourceLines == null) {
         vError(msgVector, methodName, "Vector2File -- input Vector is null.");
         return false;
      }


      File destFile = new File(destFileName).getAbsoluteFile();
      PrintWriter destWriter = null;

      try {
         destWriter = new PrintWriter(new FileWriter(destFileName, appendOption));
      } catch ( IOException ex ) {
         vError(msgVector, methodName, "Unable to open (" + destFile.toString() +"); "+ ex.getMessage());
         return false;
      }

      int lineNo = 0;
      int totalLines = sourceLines.size();
      String crlf    = System.getProperty("line.separator");

      for ( lineNo = 0; lineNo < totalLines; lineNo++ )
         destWriter.write(sourceLines.elementAt(lineNo) + crlf);

      destWriter.close();
      destWriter = null;

      return true;
   }


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                Log a Vector Error                             บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   protected void vError(Vector msgVector, String methodName, String msg) {

      String tempMsg =  errPrefix +" "+ methodName +": "+ msg;

      if (msgVector == null)
         System.err.println(tempMsg);
      else
         msgVector.add(tempMsg);

      return;
   }


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                read a line, append continuations                       บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   static String         gnll_LastFileName = "";
   static BufferedReader gnll_br           = null;
   static int            gnllLineCount;
   static String         gnllComment       = null;

/** Read a text file with optional continuation.
 * A line ending with a comma will have the next physical line appended
 * starting with the first non white space character, the comma will remain.
 * A Line ending with a hyphen (-) will have the next physical line appended
 * starting with the first non-whitespace and the hyphen will be replaced
 * by the first non-white space of the next line. The comment indicator will be
 * taken from the first non-white space of the first line, Recognized comment
 * indicators include * # ! and //. Comments may then appear any where in the
 * file. All data starting with the comment indicator to the end of the line
 * will be ignored, all other data will be processed.
 *
*/
   String getNextLogicalLine(int[] physicalLine, String fileName) {

      return gnll(physicalLine, fileName, null);
   }


   String getNextLogicalLine(int[] physicalLine, String fileName, InputStream stream) {

      return gnll(physicalLine, fileName,  stream);
   }



   String gnll(int[] physicalLine, String fileName, InputStream stream) {

      // if we have a stream we'll use it and we'll use the filename just to keep track
      // when it changes. if the stream is null then we'll use the filename

      if ((fileName == null) &&(stream == null))
         return null;


      StringBuffer theNextLine = new StringBuffer();
      String absPath;

      if (stream == null)
         absPath = new File(fileName).getAbsolutePath();
      else
         absPath = fileName;

      if (!gnll_LastFileName.equals(absPath)) {   // if this is a new name

         if (gnll_br != null ) {
            try {
               gnll_br.close();
               gnll_br = null;
            } catch (IOException ex) {
               Logit.Err(1, " in getNextLogicalLine() -- Failure to close "+ gnll_LastFileName, ex);
               return null;
            }
         }

         if (stream == null) {   // we don't make these checks for streams

            File temp = new File(absPath);
            if (!temp.exists()) {
               Logit.Err(5,"in getNextLogicalLine() -- The file ("+ absPath +") does not exist.");
               return null;
            }

            if (!temp.isFile()) {
               Logit.Err(6, "in getNextLogicalLine() -- The specification ("+ absPath +") is not a file.");
               return null;
            }

            if (!temp.canRead()) {
               Logit.Err(7, "in getNextLogicalLine() -- The file ("+ absPath +") can not be read.");
               return null;
            }

         }


         try {
            if (stream == null)
               gnll_br = new BufferedReader(new InputStreamReader(new FileInputStream(absPath)));
            else
               gnll_br = new BufferedReader(new InputStreamReader(stream));

            gnllLineCount   = 0;
            gnll_LastFileName = absPath;

         } catch (IOException ex) {
            Logit.Err(2, "in getNextLogicalLine() -- Failure to open "+ fileName, ex);
            return null;
         }

      }

      String  workLine     = null;
      boolean continuation = true;

      while (continuation) {

         try {
            workLine = gnll_br.readLine();
         } catch (Exception ex) {
            Logit.Err(3, "in getNextLogicalLine() -- Unable to read "+ fileName, ex);
            return null;
         }


         if (workLine != null) {
            gnllLineCount++;
            physicalLine[0] = gnllLineCount;
            workLine = workLine.trim();

            if (workLine.length() == 0)      // always skip lines of zero length
               continue;

            if (gnllLineCount == 1) {       // see if we can find a comment on the first line

               if (workLine.startsWith("//"))
                  gnllComment = "//";

               if (workLine.startsWith("#"))
                  gnllComment = "#";

               if (workLine.startsWith("!"))
                  gnllComment = "!";

               if (workLine.startsWith("*"))
                  gnllComment = "*";

            }

            if (workLine.trim().toLowerCase().startsWith("commenttag ")) {
               gnllComment = workLine.trim().substring(10).trim();
               continue;
            }

            if (gnllComment != null) {
               if (workLine.startsWith(gnllComment))  // if the entire line is a comment
                  continue;

               int pos = workLine.indexOf(" "+ gnllComment);  // if we have a comment some where in the line
               if (pos > -1) {
                  workLine = workLine.substring(0, pos);
                  workLine = workLine.trim();
               }
            }


            if ((workLine.endsWith(",")) || (workLine.endsWith("-"))) {  // we have a continuation here

               if (workLine.endsWith("-")) {         // remove the dash
                  theNextLine.append(workLine.substring(0, workLine.length() -1));

               } else if (workLine.endsWith(",")) {  // leave the comma
                  theNextLine.append(workLine.substring(0, workLine.length() ));
               }


            } else {
               theNextLine.append(workLine);
               continuation = false;
            }


         } else {    // EOF has been reached
            try {
               gnll_br.close();
               continuation = false;
               theNextLine = null;
            } catch (IOException ex) {
               Logit.Err(4, "in getNextLogicalLine() -- Failed to close "+ fileName, ex);
               return null;
            }
            gnll_LastFileName = "";
         }

      }

      if (theNextLine == null)
         return null;
      else
         return theNextLine.toString();

   }



   public static final String[][] numericSuffix = {{"K", "000"},
      {"M", "000000"},
      {"G", "000000000"},
      {"T", "000000000000"}};

//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ      Test a String value for numerics  Long                   บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   long test4numerics(String number, long defaultValue, StringBuffer errSB) {

      long longValue = defaultValue;

      for (int i=0; i < numericSuffix.length; i++ ) {
         if (number.endsWith( numericSuffix[i][0] )) {
            number = number.substring(0, number.length()-2) + numericSuffix[i][1];
            break;
         }
      }

      try {
         longValue = Long.parseLong(removeCommas(number.trim()));

      } catch (NumberFormatException ex ) {
         longValue = defaultValue;
         errSB.append(" the value ").append(number).append(" is not numeric, or has an invalid extension.");
      }

      return longValue;
   }


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ      Test a String value for numerics  int                    บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   int test4numerics(String number, int defaultValue, StringBuffer errSB) {

      long longValue = test4numerics(number, (long) defaultValue, errSB);
      int intValue   = -1;

      if (longValue > Integer.MAX_VALUE ) {
         errSB.append("The value ").append(number).append(" is greater than the maximum allowed for an integer, max is ").append(Integer.MAX_VALUE);
         intValue = defaultValue;
      } else
         intValue = new Long(longValue).intValue();

      return intValue;
   }





//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Remove commas from numeric Strings                 บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   String removeCommas(String rawValue) {

      StringBuffer value = new StringBuffer();
      int pos= rawValue.indexOf(',');

      if (pos == -1)
         return rawValue;

      int lastComma = -1;

      do {
         if (pos != 0)
            value.append(rawValue.substring(lastComma+1, pos));

         lastComma = pos;
         pos = rawValue.indexOf(',', pos+1);
      } while (pos != -1 );

      value.append(rawValue.substring(lastComma+1));

      return value.toString();
   }




//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ             Take a small nap                                  บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
   void siesta(long milliSeconds, boolean debug) {

      if (debug) {
         Date now = new Date();
         SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         System.out.println(df.format(now) +" Entering siesta for "+ FmtNum(milliSeconds,0,0) +" milli Seconds");
      }

      try {
         sleep(milliSeconds);
      } catch (InterruptedException ex) {
         System.err.println("Error in Helper1.siesta() --  Suffering insomina:"+ ex.getMessage());
      }

      if (debug) {
         Date now = new Date();
         SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         System.out.println(df.format(now) +" exiting siesta");
      }

      return;
   }


}

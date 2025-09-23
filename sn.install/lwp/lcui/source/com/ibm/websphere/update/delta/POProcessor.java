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

import java.io.*;
import java.util.*;


// History 1.2, 9/26/03
//               Added setUserQuestionArray() methos to allow the modification of user questions
//               after the PODefs have been initialized.


/**
 * Property Option Processor. <br> This processor is designed to collect information from four places, <pre> 1) command Line 2) Property File 3) Defaults 4) User Input </pre> and dispense the information upon request. Any defined keyword may be specified on either the command line or in the property file. Entries on the command line take presidents over entries in a property file. Keywords are defined using the PODef class. As the values are held in a property table, any value may be retrieved as a string, regardless of the reciprocal type defined. All keywords, regardless of how they are defined, can be reterieved as a string, as all values are stored in property tables. Note: To specify continuation of a list within a property file, a comma delimited list may be specified on a single line. Or if you wish to span multiple lines end the line with a backslash and place the delimiting comma as the first character of the next line. <code> ie: FarmAnimals = cow,Horse,Pig,Sheep or FarmAnimals = cow,\ Horse,\ Pig,\ Sheep </code>
 */
public class POProcessor {
 /** Version of this program */

   public static final String pgmVersion = "1.2" ;
 /** Version of this program */

   public static final String pgmUpdate = "9/26/03" ;

         // Note:  this list must match the "validTypes" array
 protected static final int t_Error = 0 ;
         // Type of reciprocal to expect as stored in the types vector
 // Note:  this list must match the "validTypes" array
 protected static final int t_Boolean = 1 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_Int = 2 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_IntList = 3 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_Long = 4 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_LongList = 5 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_LongSfx = 6 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_LongSfxList = 7 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_String = 8 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_StringList = 9 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_InFile = 10 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_InFileList = 11 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_OutFileNew = 12 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_OutFileNewList = 13 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_OutFileOld = 14 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_OutFileOldList = 15 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_OutFileAny = 16 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_OutFileAnyList = 17 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_Directory = 18 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_DirectoryList = 19 ;
         // Note:  this list must match the "validTypes" array
 protected static final int t_BuiltInHelp = 20 ;
         // translate ? ,-?, -Help, help to  -help
 // Note:  this list must match the "validTypes" array
 protected static final int t_Validating = 21 ;
         // valid only for Property Files
 // Note:  this list must match the "validTypes" array
 protected static final int t_NonValidating = 22 ;

      // Note: If you update this list don't forget to update isList()
 static String[] validTypes = {"Error",
                               "boolean",
                               "int",
                               "intList",
                               "Long",
                               "LongList",
                               "LongSfx",
                               "LongSfxList",
                               "String",
                               "StringList",
                               "InFile",
                               "InFileList",
                               "OutFileNew",
                               "OutFileNewList",
                               "OutFileOld",
                               "OutFileOldList",
                               "OutFileAny",
                               "OutFileAnyList",
                               "Directory",
                               "DirectoryList",
                               "BuiltInHelp",
                               "Validating",
                               "NonValidating"
                              };

 /** This array represents the source of the last reterieved value.
  * It is intended that this array be indexed with the value held in
  * <B>valueSource</B> field.
 */
 public static final String srcDescriptions[] = { "Command Line",
                                                  "Property File",
                                                  "Default",
                                                  "Console",
                                                  "Unknown" };
 /** Value used to set and inspect the <b>valueSource</B> field. */
 public static final int k_CmdLine = 0 ;
 // index values into props[]
 /** Value used to set and inspect the <b>valueSource</B> field. */
 public static final int k_PropFile = 1 ;
 /** Value used to set and inspect the <b>valueSource</B> field. */
 public static final int k_Default = 2 ;
 /** Value used to set and inspect the <b>valueSource</B> field. */
 public static final int k_UserResp = 3 ;
 /** Value used to set and inspect the <b>valueSource</B> field. */
 public static final int k_Unknown = 4 ;

 /** Acceptable numeric suffixes, case sensitive. */
 public static final String[][] numericSuffix = {{"kB", "000"},
                                                 {"mB", "000000"},
                                                 {"gB", "000000000"},
                                                 {"tB", "000000000000"}};



 /**
 * @uml.property  name="po"
 * @uml.associationEnd  multiplicity="(0 -1)"
 */
protected PODef[] po;

 /** Indicates the source of the retrieved value.
  * This public value may be interrogated after each retrieval and represents from
  * where the value has been reterieved. It is intended that this value be used in
  * conjunction with the <B>srcDescriptions</B> array.
 */
 public int valueSource = 4;      // Source of the last value retrieved

 Properties[] props = { new Properties(),   // Command Line  Properties
                        new Properties(),   // Property File Properties
                        new Properties(),   // Default       Properties
                        new Properties() }; // User responce Properties



 /** Delimiters used when parsing a list.
  * Default delimiter is a comma. */
 public String delimiters = ",";

 protected static boolean builtInHelpMode = false;  // if we have specified the help translation

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                          Constructor                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
  /** Process the command line arguments.
   * The errVector, if provided, will collect processing error messages and
   * it is upto the calling program to display them. If the errVector is null
   * than the error messages will be displayed when they occur.
  */
  public POProcessor(PODef[] defs, String[] args , Vector errVector ) {   // main constructor

    this.errVector = errVector;

    if (defs == null)
      LogError("Incomming PODefs are Null", ABEND);
    else
      po = defs;

    if (args == null)
      LogError("Incomming arguments are Null", ABEND);


                // set the Defaults
    for (int pod=0; pod < po.length; pod++) {

      if (po[pod].defaultValue != null) {

        if (isList(po[pod].type, po[pod].key)) {
          props[k_Default].setProperty( po[pod].key+"_num1", po[pod].defaultValue);
          props[k_Default].setProperty( po[pod].key+"_count", "1");

        } else {   // not a list
          props[k_Default].setProperty( po[pod].key, po[pod].defaultValue);
        }

      }

    }

    ProcessArgs(args);
  }


 /** Process the command line arguments. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Process Command Line Arguments                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void ProcessArgs (String[] args) {

 String key = "";
 String strValue;
 int count, numParms = args.length -1; // start counting at zero


 for (int parmNum=0; parmNum < args.length; parmNum++ ) {

   String cmdVar = args[parmNum];

   if (builtInHelpMode) {
     if ((cmdVar.equals("-?")) ||
         (cmdVar.equals("?"))  ||
         (cmdVar.equalsIgnoreCase("-help")) ||
         (cmdVar.equalsIgnoreCase("help")))  {
       cmdVar = "-?";
     }
   }

   if (cmdVar.startsWith("-"))
     key = cmdVar.substring(1).toLowerCase(); // strip the dash from the front
   else
     LogError("Command line parameter #"+(parmNum+1)+", "+args[parmNum]+", is invalid, was expecting an option with a leading dash.", ABEND);

   int poIndex = FindKey(key);

   if (poIndex == -1)
     LogError("Command line parameter #"+(parmNum+1)+", "+args[parmNum]+", is not defined.", ABEND);

   po[poIndex].used = true;
   String reciprocal;

   if ((parmNum + 2) <= args.length )
     reciprocal = args[parmNum + 1].trim();
   else
     reciprocal = null;

   //               target_Prop     KeyWord       reciprocal     Type
   if (ProcessKeyword(k_CmdLine, po[poIndex].key, reciprocal, po[poIndex].type))
     parmNum++;

 }

 return;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Process a KeyWord                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean ProcessKeyword(int propx, String keyword, String reciprocal, int type) {

 boolean validating = false;
 boolean reciprocalConsumed = false;  // if we used up the reciprocal
 String strValue;
 int count;

 switch (type) {

   case t_Error:
     LogError("programming error 1, Type not set for  keyword " + keyword, ABEND);
     break;


   case t_Boolean:
     boolean state = true;   //  default to true just because it exists

     if (reciprocal != null ) {
       if (!reciprocal.startsWith("-")) {
         state = test4Boolean(reciprocal, "The parameter following " + keyword +", should be a boolean value, found was ("+ reciprocal.trim() +")");
         reciprocalConsumed = true;
       }
     }
     props[propx].setProperty(keyword, (state) ? "true" : "false");

     String s = props[propx].getProperty(keyword);
     break;


   case t_Int:
     if (reciprocal == null ) {
       LogError("The parameter following " + keyword +", requires a numeric value to follow.", NoABEND);
     } else {
       strValue = test4Int(reciprocal, "The parameter following " + keyword + ", " + reciprocal +", is not a numeric value.");
       props[propx].setProperty(keyword, strValue);
       reciprocalConsumed = true;
     }
     break;


   case t_IntList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires one or more numeric values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         strValue = test4Int(val, "In the list following " + keyword +", item #" + count + " ("+ val +") is not a numeric value.");
         props[propx].setProperty(keyword+"_num"+count, strValue);
       }
       reciprocalConsumed = true;
     }
     break;


   case t_Long:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword +", requires a numeric value to follow.", NoABEND);
     } else {
       strValue = test4Long(reciprocal, "The parameter following " + keyword + ", is not a numeric value. (" + reciprocal + ")");
       props[propx].setProperty(keyword, strValue);
       reciprocalConsumed = true;
     }
     break;


   case t_LongList:
     if (reciprocal == null ) {
       LogError("The parameter following " + keyword +", requires one or more numeric values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         strValue = test4Long(val, "In the list following " + keyword +", item #" + count + " ("+ val +") is not a numeric value.");
         props[propx].setProperty(keyword+"_num"+count, strValue);
       }
       reciprocalConsumed = true;
     }
     break;


   case t_LongSfx:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword +", requires a numeric value to follow.", NoABEND);

     } else {
       strValue = test4LongSfx(reciprocal, "The parameter following " + keyword + ", is not a numeric value. (" + reciprocal + ")");
       props[propx].setProperty(keyword, strValue);
       reciprocalConsumed = true;
     }
     break;


   case t_LongSfxList:
     if (reciprocal == null ) {
       LogError("The parameter following " + keyword +", requires one or more numeric values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         strValue = test4LongSfx(val, "In the list following " + keyword +", item #" + count + " ("+ val +") is not a numeric value.");
         props[propx].setProperty(keyword+"_num"+count, strValue);
       }
       reciprocalConsumed = true;
     }
     break;



   case t_String:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires a parameter to follow.", NoABEND);
     } else {
       props[propx].setProperty(keyword, reciprocal);
       reciprocalConsumed = true;
     }
     break;

   case t_StringList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires one or more entries values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken().trim();
         count++;
         props[propx].setProperty(keyword+"_num"+count, val);
       }
       reciprocalConsumed = true;
     }
     break;


   case t_InFile:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires an input file specification to follow.", NoABEND);
     } else {
       props[propx].setProperty(keyword, canRead(reciprocal, keyword));
       reciprocalConsumed = true;
     }
     break;


   case t_InFileList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires one or more input file entries values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         props[propx].setProperty(keyword+"_num"+count, canRead(val, keyword));
       }
       reciprocalConsumed = true;
     }
     break;


   case t_OutFileOld:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires an output file specification to follow.", NoABEND);
     } else {
       props[propx].setProperty(keyword, canWrite(reciprocal, keyword));
       reciprocalConsumed = true;
     }
     break;

   case t_OutFileOldList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires one or more output file entries values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         props[propx].setProperty(keyword+"_num"+count, canWrite(val, keyword));
       }
       reciprocalConsumed = true;
     }
     break;


   case t_OutFileNew:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires an output file specification to follow.", NoABEND);
     } else {
       props[propx].setProperty(keyword, newFile(reciprocal, keyword));
       reciprocalConsumed = true;
     }
     break;


   case t_OutFileNewList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword + ", requires one or more output file entries values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         props[propx].setProperty(keyword+"_num"+count, newFile(val, keyword));
       }
       reciprocalConsumed = true;
     }
     break;


   case t_OutFileAny:       // just an output file that may or may not exist
     if (reciprocal == null) {
       LogError("The parameter following " + keyword +", requires an output file specification to follow.", NoABEND);
     } else {
       props[propx].setProperty(keyword, outFile(reciprocal, keyword));
       reciprocalConsumed = true;
     }
     break;


   case t_OutFileAnyList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword +", requires one or more output file entries values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         props[propx].setProperty(keyword+"_num"+count, outFile(val, keyword));
       }
       reciprocalConsumed = true;
     }
     break;


   case t_Directory:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword +", requires a directory specification to follow.", NoABEND);
     } else {
       props[propx].setProperty(keyword, isDirectory(reciprocal, keyword));
       reciprocalConsumed = true;
     }
     break;


   case t_DirectoryList:
     if (reciprocal == null) {
       LogError("The parameter following " + keyword +", requires one or more directory entries values to follow.", NoABEND);
     } else {
       StringTokenizer toks = new StringTokenizer(reciprocal, delimiters);
       props[propx].setProperty(keyword+"_count", Integer.toString(toks.countTokens() ));

       count = 0;
       while (toks.hasMoreTokens()) {
         String val = toks.nextToken();
         count++;
         props[propx].setProperty(keyword+"_num"+count, isDirectory(val, keyword));
       }
       reciprocalConsumed = true;
     }
     break;


   case t_BuiltInHelp:
     props[propx].setProperty(keyword, "true");
     break;


   case t_Validating:
     validating = true;


   case t_NonValidating:
     if (reciprocal == null) {
       LogError("A property file specification is expected to follow keyword " + keyword +".", NoABEND);

     } else {
       File propFile = new File(reciprocal).getAbsoluteFile();
       props[propx].setProperty(keyword, propFile.toString());

       if (!propFile.isFile()) {
         LogError("The property file specification, "+keyword + " " + reciprocal + ", is not an existing file.", NoABEND);

       } else {
         try {
           props[k_PropFile].load(new FileInputStream(propFile));

         } catch (IOException ex) {
           LogError("The property file,"+ reciprocal +", can not be processed:"+ ex.getMessage(), NoABEND);
         } finally {
           reciprocalConsumed = true;
         }
       }
       GoValidate(reciprocal, validating);
     }
     validating = false;
     break;


   default:
     LogError("Programming error 2, invalid value in switch, " + type, ABEND);

 }

 return reciprocalConsumed;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ         Tests if a File exists and can be read                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String canRead(String fileName, String key) {

 File tempFile = new File(fileName).getAbsoluteFile();

 if (!tempFile.exists()) {
   LogError("The input file specification, -" + key + " " + tempFile + ", the file must exist.", NoABEND);

 } else if (!tempFile.canRead()) {
     LogError("The input file specification, -" + key + " " + tempFile + ", can not be open for input.", NoABEND);
 }

 return tempFile.toString();
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ         Tests if a File exists and can be writen to                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String canWrite(String fileName, String key) {

 File tempFile = new File(fileName).getAbsoluteFile();

 if (!tempFile.exists()) {
   LogError("The output file specification, -" + key + " " + tempFile + ", the file must exist.", NoABEND);

 } else if (!tempFile.canWrite()) {
     LogError("The output file specification, -" + key + " " + tempFile + ", can not be open for output.", NoABEND);
 }

 return tempFile.toString();
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               ensures that a file does not exist                   บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String newFile(String fileName, String key) {

 File tempFile = new File(fileName).getAbsoluteFile();

 if (tempFile.exists()) {
   LogError("The output file specification, -" + key + " " + tempFile + ", can not pre-exist.", NoABEND);

 } else if (tempFile.isDirectory()) {
     LogError("The output file specification, -" + key + " " + tempFile + ", seems to be a directory.", NoABEND);
 }

 return tempFile.toString();
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ     an Output File if it exists ensure we can write to it          บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String outFile(String fileName, String key) {

 File tempFile = new File(fileName).getAbsoluteFile();

 if (tempFile.isDirectory()) {
   LogError("The output file specification, -" + key + " " + tempFile + ", seems to be a directory.", NoABEND);

 } else if (tempFile.exists()) {

   if (!tempFile.canWrite()) {
     LogError("The output file specification, -" + key + " " + tempFile + ", can not be open for output.", NoABEND);
   }

 }

 return tempFile.toString();
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ      Ensure specification is a Directory                           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String isDirectory(String fileName, String key) {

 File tempFile = new File(fileName).getAbsoluteFile();

 if (!tempFile.isDirectory()) {
   LogError("The directory specification, -" + key + " " + tempFile + ", seems not to be a directory.", NoABEND);
 }

 return tempFile.toString();
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Test a value for integer value                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String test4Int(String value, String errorMessage) {

 String returnValue = "";

 try {

   returnValue = Long.toString(Long.parseLong(removeCommas(value)));
   Long l1 = new Long(returnValue);

   if (l1.longValue() > Integer.MAX_VALUE)
     LogError("The value "+ value +", is greater than an integer can contain.", NoABEND);

 } catch (NumberFormatException ex) {
   LogError(errorMessage, NoABEND);
 }


 return returnValue;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Test a value for Long value                        บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String test4Long(String value, String errorMessage) {

 String returnValue = "";

 try {
   returnValue = Long.toString(Long.parseLong(removeCommas(value)));
 } catch (NumberFormatException ex) {

   LogError(errorMessage, NoABEND);
 }


 return returnValue;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ         Test a value for Long value with optional suffix           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String test4LongSfx(String value, String errorMessage) {


 for (int i=0; i < numericSuffix.length; i++ ) {
   if (value.endsWith( numericSuffix[i][0] )) {
     value = value.substring(0, value.length()-2) + numericSuffix[i][1];
     break;
   }
 }

 return test4Long(value, errorMessage);
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Test for a valid boolean value                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean test4Boolean(String value, String errorMessage) {

 boolean state = false;
 value= value.trim();

 if (value.equalsIgnoreCase("true") ||
     value.equalsIgnoreCase("yes")  ||
     value.equals("1")              ||
     value.equals("on")) {
   state = true;

 } else if (value.equalsIgnoreCase("false") ||
            value.equalsIgnoreCase("no") ||
            value.equals("0") ||
            value.equals("off") ) {
   state = false;

 } else
   LogError(errorMessage, NoABEND);

 return state;
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


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Find a key within the po Vector                    บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int FindKey(String key) {

 int keyIndex  = -1;
 boolean foundit = false;

 for (int workIndex=0; workIndex < po.length; workIndex++) {

   if (po[workIndex].key.toLowerCase().startsWith(key.toLowerCase())) {

     if (foundit) {
       LogError("option -"+ key +" is ambiguous between -"+
              po[keyIndex].key  +" and -"+
              po[workIndex].key +
              ", please specify more characters to make the option unique.", ABEND);
     } else {
       keyIndex = workIndex;
       foundit = true;
     }

   }
 }

 return keyIndex;  // return the index to the keyWord
}


/** Ensure keys in the property File are all valid.
 * and translate the keys to lower case. Also trim() the value.
 */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ         Ensure keys in the Property File are all valid             บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void GoValidate(String propFileName, boolean validating) {

 Enumeration peNum = props[k_PropFile].propertyNames();

 while (peNum.hasMoreElements()) {

   String propKey = (String) peNum.nextElement();
   String value   =  props[k_PropFile].getProperty(propKey).trim();
   props[k_PropFile].remove(propKey); // remove the key and reinsert it in lower case
   propKey = propKey.toLowerCase();
   boolean found = false;

   for (int i=0; i < po.length; i++) {
     PODef pod = po[i];

     if (pod.key.equals(propKey)) {

       if (found)
         LogError("A duplicate key ,"+ propKey +", was found in the property file" + propFileName, NoABEND);
       else {
         found=true;
         pod.used = true;
         //             target_Prop  KeyWord      reciprocal     Type
         ProcessKeyword(k_PropFile,  pod.key,        value,    pod.type);
       }
     }
   }

   if ((!found) && (validating)) {
     LogError("The key ,"+ propKey +", within "+ propFileName +" is undefined", NoABEND);
   }

 }
 return;
}




/** Tests if a keyword is has been used.
 * Returns true if Yes.
 * Returns false if no or not defined.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                  Test if keyword has been specified                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public boolean keywordUsed(String keyword) {

 int index = FindKey(keyword);

 if ((index > -1) && (po[index].used))
   return true;

 return false;
}


/** Set default values after keyword definitions. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                          Set Defaults                              บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void setDefault(String key, String value) {        // String  String

 if (value == null) {
   props[k_Default].remove(key.toLowerCase());

 } else
   props[k_Default].setProperty(key.toLowerCase(), value);

}

/** Set default values after keyword definitions.
    for list objects, the keyWord needs appended "_num1".
*/

public void setDefault (String key, boolean value) {     // String  Bool

 props[k_Default].setProperty(key.toLowerCase(),  (value) ? "true" : "false");

}

/** Set default values after keyword definitions.  */
public void setDefault (String key, int value) {         // String  Int

 props[k_Default].setProperty(key.toLowerCase(), Integer.toString(value));

}


/** Set User-Question values after keyword definitions have been initialized.
  * This only works if the original definitions have a user responce array.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                Set User Question Array Default                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void setUserQuestionArray(String key, String[] newArray) {

 int index = FindKey(key);
 po[index].questionArray = newArray;

}


/** Returns the count of entries from a specified list. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ          Return the count of elements of a list                    บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public int getCount(String keyword) {

 String value = getString(keyword+"_count");

 if (value == null)
   return 0;
 else
   return Integer.parseInt(value);
}



//  All other getXXX methods use this getString method
/** Returns the string value associated with a keyword. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Return a String Value                        บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getString(String keyword) {

 String value;
 String key = keyword.toLowerCase();
                         // from the command line first
 if ((value = props[k_CmdLine].getProperty(key)) != null) {
   valueSource = k_CmdLine;
   return value;
 }
                         // from the Property File second
 if ((value = props[k_PropFile].getProperty(key)) != null) {
   valueSource = k_PropFile;
   return value;
 }
                         // from the defaults third
 if ((value = props[k_Default].getProperty(key)) != null) {
   valueSource = k_Default;
   return value;
 }

                         // from the console forth
 if ((value = props[k_UserResp].getProperty(key)) != null) {
   valueSource = k_UserResp;
   return value;
 }

 // Here we need to check if we have a user question to ask
 // as we only ask if we need to
 int index = FindKey(key);

 if ((index > -1) && (po[index].alreadyAsked == false )) {
   if (po[index].haveQuestion) {
     value = AskQuestion(index);
     po[index].alreadyAsked = true;
     if (value != null) {
       valueSource = k_UserResp;
       props[k_UserResp].setProperty(key.toLowerCase(), value);
       return value;
     }
   }
 }

 // at this point we have failed to find the the keyword
 // check and see if maybe it is a list

 if (FindKey(key) > -1)  {

   if (isList(po[index].type, key) ) {
     LogError("The keyword \""+ key +"\" is defined as a list and must be accessed by specifing a index value", ABEND);
   }
 }

 valueSource = k_Unknown;
 return value;
}

/** Returns the string value associated with a keyword from a list.
 <p>
  Note: The first reciprocal is #1.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Return an String Value from a list                   บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getString(String keyword, int reciprocalNumber) {

  return getString(keyword +"_num"+ reciprocalNumber);
}


/** Returns the bool value associated with a keyword.
 <p>
 Note: To use this method the keyword must have been defined as boolean.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Return Bool Value                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public boolean getBool(String keyword) {

 if (keyword == null)
   return false;

 String strBool = getString(keyword);

 if (strBool == null)
   return false;
 else
   strBool = strBool.trim();

 if (strBool.equalsIgnoreCase("true") ||
     strBool.equals("1")              ||
     strBool.equalsIgnoreCase("yes")  ||
     strBool.equalsIgnoreCase("on"))
   return true;

 if (strBool.equalsIgnoreCase("false") ||
    strBool.equals("0")                ||
    strBool.equalsIgnoreCase("no")     ||
    strBool.equalsIgnoreCase("off"))
   return false;

 System.err.println("Error in POProcessor -- value for key " + keyword + " is not boolean ("+ strBool +")");
 System.exit(8);

 return false;
}


/** Returns the integer value associated with a keyword.
 <p>
 Note: To use this method the keyword must have been defined as integer.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                      Return an Integer Value                       บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public int getInt(String keyword) {

 String strValue = getString(keyword);

 if (strValue == null) {
   LogError("No default value was provided for "+ keyword + ", -1 was returned.", NoABEND);
   return -1;
 }

 return Integer.parseInt(strValue);
}


/** Returns the integer value associated with a keyword from a list.
 <p>
 Note: To use this method the keyword must have been defined as integer.
 <P>
 Note: The first value is reciprocalNumber # 1.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Return an Integer Value from a list                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public int getInt(String keyword, int reciprocalNumber) {

  return getInt(keyword +"_num"+ reciprocalNumber);
}


/** Returns the long value associated with a keyword from a list.
 <p>
 Note: To use this method the keyword must have been defined as long.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                      Return a Long Value                           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public long getLong(String keyword) {

 String strValue = getString(keyword);

 if (strValue == null) {
   LogError("No default value was provided for "+ keyword + ", -1 was returned.", NoABEND);
   return -1;
 }

 return Long.parseLong(strValue);
}


/** Returns the long value associated with a keyword from a list.
 <p>
 Note: To use this method the keyword must have been defined as long.
 <P>
 Note: The first value is reciprocalNumber # 1.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Return an Integer Value from a list                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public long getLong(String keyword, int reciprocalNumber) {

  return getLong(keyword +"_num"+ reciprocalNumber);
}



/** Returns the string value of a directory associated with keyword.
 <p>
 Note: To use this method the keyword must have been defined as Directory.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Return a Directory                              บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getDirectory(String keyword) {

 String strValue = getString(keyword);

 //TBD  Need to check to ensure this entry was defined as a Directory

 return strValue;
}


/** Returns the string value of a directory associated with keyword.
 <p>
 Note: To use this method the keyword must have been defined as Directory.
 <P>
 Note: The first value is reciprocalNumber # 1.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Return a Directory Value from a list                 บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getDirectory(String keyword, int reciprocalNumber) {

  return getDirectory(keyword+"_num"+reciprocalNumber);
}

/** Returns the file associated with keyword.
 <p>
 Note: To use this method the keyword must have been defined as file.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Return a File                                   บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public File getFile(String keyword) {

 File file = null;

 String value = getString(keyword);

 if (value != null)
   file = new File(getString(keyword));

 //TBD  Need to check to ensure this entry was defined as a File

 return file;
}


/** Returns the file associated with keyword from a list.
 <p>
 Note: To use this method the keyword must have been defined as file.
 <P>
 Note: The first value is reciprocalNumber # 1.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Return a File  Value from a list                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public File getFile(String keyword, int reciprocalNumber) {

  return getFile(keyword+"_num"+reciprocalNumber);
}





protected final static boolean ABEND = true ;

protected final static boolean NoABEND = false ;


/** Error count encountered during processing. */
public int errorCount  = 0;      // caller accessable error count

Vector errVector = null;

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     Log an Eror Message                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void LogError(String errorMsg, boolean if2Abend) {

 errorCount++;
 String errorPrefix = " Error in POProcessor : ";

 if (errVector == null) {
   System.err.println(errorPrefix + errorMsg );
   System.exit(8);

 } else {
   errVector.add(errorPrefix + errorMsg);
 }

 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Ask the user what to do                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 /** Ask the user what to do.
  * Will either read an external file or output a string array.
 */
String AskQuestion(int index) {

 if (!po[index].haveQuestion)  // no question to ask
   return null;

 int bufferSize = 4096;
 String IOLine;

 System.out.println("");

 if (po[index].questionFile != null) {  // we have a file that contains the question
   try {
     BufferedReader BufInFile  = new BufferedReader(new FileReader(po[index].questionFile), bufferSize);

     while ((IOLine = BufInFile.readLine()) != null) {
       System.out.println(IOLine);
     }

     BufInFile.close();

   } catch (FileNotFoundException ex) {
     LogError("Question file, (" + new File(po[index].questionFile).getAbsolutePath() + ") for keyword " + po[index].key + " was not found.", NoABEND);

   } catch (IOException ex ) {
     LogError(" IOException on Question file, ("+po[index].questionFile+") for keyword " + po[index].key + " " + ex.getMessage() , ABEND);
   }

 } else if ((po[index].questionArray != null) && (po[index].questionArray.length > 0)) {

   for (int i=0; i < po[index].questionArray.length; i++) {
     System.out.println( po[index].questionArray[i]);
   }

 }

 String  reply   = null;
 boolean goodReply = false;

 while (!goodReply) {
   reply = getKeyBoard();

   switch (po[index].type) {
     case t_Error:
       LogError("56, Invalid type zero for keyword " + po[index].key, ABEND);
       break;

     case t_Boolean:
       if (reply.equalsIgnoreCase("true") || reply.equalsIgnoreCase("yes") || reply.equals("1") || reply.equals("on")) {
         goodReply = true;

       } else if (reply.equalsIgnoreCase("false") || reply.equalsIgnoreCase("no") || reply.equals("0") || reply.equals("off") ) {
         goodReply = true;

       } else {
         System.out.println("Unacceptable reply, Please reply with \"true\", \"yes\", \"1\" or \"on\" to answer in the affirmative.");
         System.out.println("or reply with \"false\", \"no\", \"0\" or \"off\" to answer in the negative.");
       }
       break;

     case t_Int:
       try {
         int temp = Integer.parseInt(reply);
         goodReply = true;
       } catch (NumberFormatException ex) {
         System.out.println("Your reply (" + reply + ") is not a numeric value, or is to large.");
       }
       break;

     case t_Long:
       try {
         long temp = Long.parseLong(reply);
         goodReply = true;
       } catch (NumberFormatException ex) {
         System.out.println("Your reply (" + reply + ") is not a numeric value, or is to large.");
       }
       break;

     case t_String:
       goodReply = true;   // no checking is done here
       break;

     case t_InFile:
       File tempFile = new File(reply);
       if (!tempFile.exists()) {
         System.out.println("The specified input file, " + tempFile.getAbsolutePath() + ", does not exist.");

       } else if (!tempFile.canRead()) {
         System.out.println("The specified input file, " + tempFile.getAbsolutePath() + ", can not be opened for input.");
       } else
         goodReply = true;
       break;

     case t_OutFileNew:
       tempFile = new File(reply);
       if (tempFile.exists()) {
         System.out.println("The specified output file, " + tempFile.getAbsolutePath() + ", already exists.");
       } else
         goodReply = true;
       break;

     case t_OutFileOld:
       tempFile = new File(reply);
       if (!tempFile.exists()) {
         System.out.println("The specified output file, " + tempFile.getAbsolutePath() + ", does not exist.");

       } else if (!tempFile.canWrite()) {
         System.out.println("The specified output file, " + tempFile.getAbsolutePath() + ", can not be opened for output.");
       } else
         goodReply = true;
       break;

     case t_OutFileAny:
       goodReply = true;
       break;

     case t_Directory:
       tempFile = new File(reply);
       if (!tempFile.isDirectory()) {
         System.out.println("The specified directory, " + tempFile.getAbsolutePath() + ", seems not to be a directory.");
       } else
         goodReply = true;
       break;

     default:
       LogError("57, Invalid type " + po[index].type + " for keyword " + po[index].key, ABEND);
   }

   if (!goodReply)
     System.out.println("  Please re-enter your response.");

 }

 return reply;
}

/** Beep and receive keyboard input. */
//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ              Get a reply from the keyboard                    บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getKeyBoard() {

 String keyBoardData;

 InputStreamReader inputReader = new InputStreamReader(System.in);
 BufferedReader keyboard = new BufferedReader(inputReader);

 System.out.println("\07\00\00\00\00\00\00\00\00\00\00\00\00\00\00\00\00\07");  // beep beep
 System.out.println();
 System.out.println("   You may enter \"exit\" to terminate this operation.");

 try {
   keyBoardData = keyboard.readLine();
 } catch ( IOException ex ) {
   LogError("Keyboard IOException : " + ex.getMessage(), ABEND);
   keyBoardData = null;
 }

 keyBoardData = keyBoardData.trim();

 if (keyBoardData.equalsIgnoreCase("exit"))
   System.exit(8);

 return keyBoardData;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ              Test if a specific type is a List                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 /** Determine if a speified type is a List item. */
boolean  isList(int type, String key) {

 boolean rc = false;

  switch (type) {
    case t_Boolean:
    case t_Int:
    case t_Long:
    case t_LongSfx:
    case t_String:
    case t_InFile:
    case t_OutFileNew:
    case t_OutFileOld:
    case t_OutFileAny:
    case t_Directory:
    case t_BuiltInHelp:
    case t_Validating:
    case t_NonValidating:
      rc = false;
      break;

    case t_IntList:
    case t_LongList:
    case t_LongSfxList:
    case t_StringList:
    case t_InFileList:
    case t_OutFileNewList:
    case t_OutFileOldList:
    case t_OutFileAnyList:
    case t_DirectoryList:
      rc = true;
      break;

    default:
      LogError("Programming error 3 in POProcessor: Un-Caught type in isList() :  Keyword=" + key +", type=" + type, ABEND);
  }

 return rc;
}

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ  A Debug option to list the content of the Properties Arrays       บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 /** A debug method to display the content of all the Property tables and
   the PODef values.
 */
void showProps() {

 System.out.println("Debug -- Listing content of Property Tables");

 System.out.println("Debug --   Command Line Entries");
 DisplayProps(props[k_CmdLine]);

 System.out.println("Debug --   Property File Entries");
 DisplayProps(props[k_PropFile]);

 System.out.println("Debug --   User Responce Entries");
 DisplayProps(props[k_UserResp]);

 System.out.println("Debug --   Default Entries");
 DisplayProps(props[k_Default]);

 System.out.println("Debug --   KeyWord Definitions");
 for (int i=0; i < po.length; i++) {
   PODef pod = po[i];
   System.out.println("  KeyWord="+pod.key + " type=" + validTypes[pod.type]);
 }

 return;
}

/** An internal method to display the content of a property table. */
private void DisplayProps(Properties propTable) {

 Enumeration eNum = propTable.propertyNames();

 if (!eNum.hasMoreElements()) {
   System.out.println("           No entries");
   return;
 }

 while (eNum.hasMoreElements()) {
   String key = (String) eNum.nextElement();
   System.out.println("   " + key + " = " + propTable.getProperty(key));
 }

 return;
}

}



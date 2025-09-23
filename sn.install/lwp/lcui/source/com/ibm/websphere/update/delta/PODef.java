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


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ             Property Options Definitions Class                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ

/**
 * The PODef (Proprety Options Defination) class is used to create an array of keyword definition objects.
 * The calling program creates one PODef object for each keyword. In the following example the keywords
 * are assigned to a constant to reduce spelling errors during use.
 * <br>
 *
 * <br> example:
 * <pre>
 *  final String k_Version       =  "version",    // to display the current version of this program
 *               k_Debug         =  "debug",
 *               k_Help          =  "?",          // must be "?" for built-in Function
 *               k_LogFile       =  "logfile",    // a log file to write to
 *               k_Verbosity     =  "verbosity",  // degree of output to produce
 *               k_Target        =  "target",     // An input directory
 *               k_Recurse       =  "recurse",
 *               k_MaxDays       =  "maxdays",
 *               k_PropertyFile  =  "PropertyFile";
 *
 *
 *  //          KeyWord           Type       Default_Value
 *  PODef[] defs = {
 *    new PODef(k_Version,      "Boolean",      "false" ),
 *    new PODef(k_Debug,        "Boolean",      "false" ),
 *    new PODef(k_Help,         "BuiltInHelp",      null  ),
 *    new PODef(k_LogFile,      "OutFileAny",     null  ),
 *    new PODef(k_Verbosity,    "Int",             "3"  ),
 *    new PODef(k_PropertyFile, "Validating",     null  ),  // Validating will ensure all keys are valid
 *
 *    new PODef(k_Target,       "Directory",      null  ),
 *    new PODef(k_Recurse,      "Bool",         "false" ),
 *    new PODef(k_MaxDays,      "Numeric",     "999999" )
 * };
 *
 * POProcessor po = new POProcessor(defs, argv, null) ;
 *
 * </pre>
 */
// public    class PODef     {    //  we need the public for javadoc to work
 class PODef {

    public static final String pgmVersion = "1.2" ;
    public static final String pgmUpdate = "9/26/03" ;

  /** The keyword, translated to lower case. */
  protected String  key;

  /** The type of recoprocial to expect.
   <pre>
   Valid types are: (case insensitive):

   bool        A value of true, on, yes, 1 or the existance on the the option will return a true.
               A value of false, off, no or 0 will return false.
   boolean     Same as bool.

   int         Numeric values.
   Numeric     Same as Int.

   intList     A comma delimited list of numeric values.
   NumericList Same as intList.

   Long        A numeric value  Maximun value is 9,223,372,036,854,775,807.
   LongList    A comma delimited list of numeric values.

   LongSfx     A numeric value that may have a sufix appended, like kB, mB, and gb.
   LongSfxList Same as LongSfx, but a list.

   String      A string value.
   StringList  A list of string values.

   InFile      An input file, a check is made for the files existance and readability.
   InFileList  A comman delimited list of input files.

   OutFileNew  An output file, a check is made to ensure the file does not already exist.
   OutFileNewList A comma delimited list of OutFileNew.

   OutFileOld  An output file, a check is made to ensure the file already exists.
   OutFileOldList A comma delimited list of OutFileOlds

   OutFileAny  An output file, no validating is done at this time.
   OutFileAnyList A comma delimited list of OutFileAny.

   Directory   A directory, a check is made to ensure the directory exists.
   DirectoryList A comma delimited list of Directories.

   Validating  For use with Property files only. Will validate thta the property file exists and that every keyword is defined.
   NonValidating For use with Property files only. Will validate the the file exists.

   BuiltInHelp This type is for the help functions only. It is intended to be used when the defining keyword is ?. The values Help, -Help, ?, -? will all be translated to ?.
 </pre>
  */
  public int     type;

  /** The default value for the keyword, or null. */
  protected String  defaultValue;

  /** The question to as the user, if any */
  boolean  haveQuestion  = false;  // If we have a question to ask
  String[] questionArray = null;   // The question may be a string array
  String   questionFile  = null;   // The question may be in a text file
  boolean  alreadyAsked  = false;  // if we have already asked the user
  boolean  used          = false;  // to indicate if this keywork has been used

                      // Constructors
  /** Minimum requirements.   */
  public PODef(String key, String type, String defaultValue) {
    this.key          = key.toLowerCase();
    this.type         = xlateType(type, key);
    this.defaultValue = defaultValue;
  }


  /** Minimum requirements plus user Question Array. */
  public PODef(String key, String type, String defaultValue, String[] question) {
    this.key           = key.toLowerCase();
    this.type          = xlateType(type, key);
    this.defaultValue  = defaultValue;
    this.questionArray = question;
    haveQuestion      = true;
  }


  /** Minimum requirements plus user Question File. */
  public PODef(String key, String type, String defaultValue, String question) {
    this.key          = key.toLowerCase();
    this.type         = xlateType(type, key);
    this.defaultValue = defaultValue;
    this.questionFile = question;
    haveQuestion      = true;
  }

/** This internal method validates and translates the specified type. <br>
 *<Code>
 * Valid types are:
 *  boolean    This option will accept true, on, 1, yes to be true and
 *             false, off, 0, no to be false. When specified on the command
 *             line the existance of the keyword implies true.
 *
 *  Int        Will recieve a numeric value and can be reterieved as an int.
 *  IntList    Like Int except the reciprocal may be a comma delimited list.
 *
 *  Long       Will recieve a numeric value and can be reterieved as an Long.
 *  LongList   Like Long except the reciprocal may be a comma delimited list.
 *
 *  String     Any type of data.
 *  StringList Like String except the reciprocal may be a comma delimited list.
 *
 *  InFileSpec Expecting an input file name, that will be validated for readability.
 *  InFileSpecList  Like InFileSpec except the reciprocal may be a comma delimited list.
 *
 *  OutFileNew An Output file that may not pre exist.
 *  OutFileNewList Like outrFileNew except the reciprocal may be a comma delimited list.
 *
 *  OutFileOld An output file that mst pre-exist.
 *  OutFileOldList Like OutFileOld except the reciprocal may be a comma delimited list.
 *
 *  OutFileAny An output file that may or may not pre-exist.
 *  OutFileAnyList  Like OutFileAny except the reciprocal may be a comma delimited list.
 *
 *  Directory  Expecting a pre existing directory name.
 *  DirectoryList  Like Directory except the reciprocal may be a comma delimited list.
 *
 *  Validating Expecting a proper property file and every key will be validated.
 *
 *  NonValidating Expecting a proper property file.
 *
 *  BuildInHelp This option will intrepret ?, -?, help, -help (case insensitive)
 *              to the help heyword.
 *
 </Code>
*/

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            Translate string type to Int for storage                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int xlateType(String type, String key) {

               // Special processing for Built-In-Help
 if (type.equalsIgnoreCase(POProcessor.validTypes[POProcessor.t_BuiltInHelp])) {
   POProcessor.builtInHelpMode = true;
   this.key = "?";
   return POProcessor.t_BuiltInHelp;
 }

 for (int i=0; i < POProcessor.validTypes.length; i++) {
   if (type.equalsIgnoreCase(POProcessor.validTypes[i]) ) {
     return i;
   }
 }

 System.out.println(" Error in PODef - Type definition ("+ type +") for keyword "+ key + " is invalid.");

 return POProcessor.t_Error;
}

}

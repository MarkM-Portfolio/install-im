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

//      Find a class in a given delimited path

// import java.util.StringTokenizer;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

class findClassInPath  implements FilenameFilter  {

   public static final String pgmVersion = "1.3" ;
   public static final String pgmUpdate = "4/29/04" ;

 static         boolean debug      = false;


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                            Mainliner                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public static void main(String argv[]) {


 findClassInPath ficp = new findClassInPath();
 StringBuffer errMsg  = new StringBuffer();
 Vector items = new Vector();

 int numberFound = ficp.locate(errMsg, argv[0], argv[1], items);

 System.out.println("Number Found = "+ numberFound);
 for (int i=0; i < items.size(); i++) {
   System.out.println(i +" "+ items.elementAt(i));
 }

 System.out.println(errMsg.toString());

 return;
}



/**  Find a class in a delimited list.

 *  class2Find may include a partial path.

 * Returns the count of how many were found
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ        Find a class in the provided delimited path                 บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public int locate(StringBuffer errMsg, String path, String class2Find, Vector items) {

 final String errKonstant  = "Error in findClassInPath.locate() -- ";
 final String warnKonstant = "Warning from findClassInPath.locate() -- ";

 StringTokenizer toks = new StringTokenizer(path, System.getProperty("path.separator"));
 int inspected = 0;
 int count = 0;

 class2Find = class2Find.replace('\\','/').trim();  // normalize all slashes to foward

 Hashtable ht = new Hashtable();

 while (toks.hasMoreElements()) {
   String listEntry = toks.nextToken();

   int type = typeOfEntry(listEntry);
   switch (type) {
     case k_Unknown:
       errMsg.append(warnKonstant + "Unaccessible : "+ listEntry + System.getProperty("line.separator"));
       break;

     case k_DirCanRead:
       File file = new File(listEntry);
       String[] fl = new File(listEntry).list(new findClassInPath());

       for (int i=0; i < fl.length; i++) {
         inspected++;

         if (checkFile(class2Find, fl[i] )) {
           count++;
           items.add(listEntry + File.separator + fl[i]);
         }
       }
       break;

     case k_DirCanNotRead:
       errMsg.append(errKonstant + "CanNotRead : "+ listEntry);
       break;

     case k_JarFileCanRead:
       count += processJar(errMsg, items, class2Find, listEntry);
       break;

     case k_JarFileCanNotRead:
       errMsg.append(errKonstant + "CanNotRead : "+ listEntry);
       break;

     case k_ZipFileCanRead:
       count += processJar(errMsg, items, class2Find, listEntry);
       break;

     case k_ZipFileCanNotRead:
       errMsg.append(errKonstant + "CanNotRead : "+ listEntry);
       break;


     case k_FileCanRead:       // Here we handle regular Files
     case k_FileCanNotRead:   
     case k_ClassFileCanRead:
     case k_ClassFileCanNotRead:
       inspected++;

       if (checkFile(class2Find, listEntry )) {
         count++;
         items.add(listEntry);
       }
       break;

     default:
       errMsg.append(errKonstant + "Program Error -- unHandled case "+ type);
   }

 }

 return count;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ          check if this is the file                                 บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean checkFile(String class2find, String suspect) {

  if (suspect.indexOf("\\") != -1)
    suspect = suspect.replace('\\', '/');

  if (suspect.endsWith(class2find))
    return true;
  else
    return false;

}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ          Search the Jar file for the the class2Find                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int processJar(StringBuffer errMsg, Vector items, String class2find, String suspectJar) {

 int count = 0;

 try {
   JarInputStream jis = new JarInputStream(new FileInputStream(suspectJar), false);
   JarEntry  je;

   while ((je = jis.getNextJarEntry()) != null  ) {

     if (!je.isDirectory()) {
       String jarEntryName = je.getName();

       if (checkFile(class2find, jarEntryName)) {
         count++;
         items.add(suspectJar +"->"+ jarEntryName);
       }

     }

   }

   jis.close();

  } catch (IOException ex) {
    errMsg.append("Error in findClassInPath.processJar() -- IOException : "+  ex.getMessage());
  }

 return count;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ          Search the Zip file for the the class2Find                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int processZip(Vector errMsg, Vector items, String class2find, String suspectJar) {

 int count = 0;

 try {
   ZipInputStream zis = new JarInputStream(new FileInputStream(suspectJar), false);
   ZipEntry  ze;

   while ((ze = zis.getNextEntry()) != null  ) {

     if (!ze.isDirectory()) {
       String zipEntryName = ze.getName();

       if (checkFile(class2find, zipEntryName)) {
         count++;
         items.add(suspectJar +"->"+ zipEntryName);
       }

     }

   }

   zis.close();

  } catch (IOException ex) {
    errMsg.add("Error in findClassInPath.processZip() -- IOException : "+  ex.getMessage());
  }

 return count;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               File filter to accept only files                     บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public boolean accept(File file, String filename) {

 File test = new File(file.toString() + File.separator + filename);

 if (test.isFile())
   return true;
 else {
   // System.out.println("Diag #102 dropping name= "+ test.toString());
   return false;
 }

}



static final int k_Unknown = 0 ;

static final int k_DirCanRead = 1 ;

static final int k_DirCanNotRead = 2 ;

static final int k_FileCanRead = 3 ;

static final int k_FileCanNotRead = 4 ;

static final int k_JarFileCanRead = 5 ;

static final int k_JarFileCanNotRead = 6 ;

static final int k_ZipFileCanRead = 7 ;

static final int k_ZipFileCanNotRead = 8 ;

static final int k_ClassFileCanRead = 9 ;

static final int k_ClassFileCanNotRead = 10 ;

/**  Determine what kind of file object this is.

 Return values  0 = not identified
                1 = is a Directory that can be read
                2 = is a directory that can not be read

*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Determine type of entry                         บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
int typeOfEntry(String filespec) {

// if (System.getProperty("file.separator").equals("\\"))
//   filespec = filespec.replace('/','\\');
// else
//   filespec = filespec.replace('\\','/');

 File someFile = new File(filespec);

 if (someFile.isDirectory()) {

   if (someFile.canRead())
     return k_DirCanRead;
   else
     return k_DirCanNotRead;

 } else if (someFile.isFile()) {

   String lcFile = filespec.toLowerCase().trim();

   if (lcFile.endsWith(".jar") ) {

     if (someFile.canRead())
       return k_JarFileCanRead;
     else
       return k_JarFileCanNotRead;

   } else if (lcFile.endsWith(".zip") ) {

       if (someFile.canRead())
         return k_ZipFileCanRead;
       else
         return k_ZipFileCanNotRead;

   } else if (lcFile.endsWith(".class") ) {

     if (someFile.canRead())
       return k_ClassFileCanRead;
     else
       return k_ClassFileCanNotRead;

   } else

     if (someFile.canRead())
       return k_FileCanRead;
     else
       return k_FileCanNotRead;

 }

 return k_Unknown;
}

}

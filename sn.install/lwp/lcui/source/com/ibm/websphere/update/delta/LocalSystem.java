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
import java.lang.System;
import java.util.*;

/*
 * LocalSystem
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/LocalSystem.java, wps.base.fix, wps6.fix
 *
 * @author: Steve Pritko
 * History 1.5, 4/29/04
 *
 * 04-Dec-2003 cdchoi iSeries enablement
 */

/**
 *  
 */
public class LocalSystem {
   public static final String pgmVersion = "1.5" ;
   public static final String pgmUpdate = "4/29/04" ;

   protected static boolean debug;

   protected static String osName;
   protected static boolean isUnix;
   protected static boolean isiSeries;

   public LocalSystem(boolean debug)
   {
		LocalSystem.debug = debug;
	
		LocalSystem.osName = System.getProperty("os.name");
		LocalSystem.isUnix = computeIsUnix(LocalSystem.osName);
		LocalSystem.isiSeries =getIsiSeries (LocalSystem.osName);
   }

   /**
 * @return  the debug
 * @uml.property  name="debug"
 */
public boolean getDebug()
   {
      return LocalSystem.debug;
   }

   public String getOSName()
   {
      return LocalSystem.osName;
   }

   /**
 * @return  the isUnix
 * @uml.property  name="isUnix"
 */
public boolean getIsUnix()
   {
      return LocalSystem.isUnix;
   }

   public boolean getIsWindows()
   {
      return !(LocalSystem.isUnix && LocalSystem.isiSeries);
   }

   public boolean getIsiSeries(String useOSName){
     if ( useOSName.equals("OS/400") )           // iSeries
         return true;
     else return false;
   }
   

   public boolean computeIsUnix(String useOSName)
   {
      if ( useOSName.startsWith("Windows") )  // Windows NT, Windows XP, Windows 2003, Windows 2000
         return false;
      if ( useOSName.equals("OS/2") )
         return false;

      if ( useOSName.equals("Linux") )
         return true;
      if ( useOSName.equals("Solaris") )
         return true;
      if ( useOSName.equals("SunOS") )
         return true;
      if ( useOSName.equals("AIX") )
         return true;
      if ( useOSName.equals("HP-UX") )
         return true;
      if ( useOSName.equals("OS/390") )
         return true;
      if ( useOSName.equals("OS/400") )      // iSeries
         return false;
      if ( useOSName.equalsIgnoreCase("Z/OS") )
         return true;

      System.err.println("Error -- LocalSystem v" + pgmVersion +
                         " -- Unrecognized os.name (" + useOSName + ")");
      return true;
   }

   protected boolean makeDirectory(String dirName)
   {
      File childDir = new File(dirName);

      if ( !childDir.exists() )
         return childDir.mkdirs();
      else
         return true;
   }

   protected ISystemFile newFile(String fileName)
   {
      if ( getIsUnix() )
         return new UnixFile(fileName);
      else
         return new WindowsFile(fileName);
   }
}

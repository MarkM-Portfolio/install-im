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

/*
 * PlatformUtils -- Platform utilities for EAR processing.
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/util/PlatformUtils.java, wps.base.fix, wps5.fix
 *
 * History 1.5, 1/29/04
 *
 * 02-Dec-2002 Initial Version
 */

package com.ibm.websphere.update.util;

import java.util.*;
import java.io.*;

public class PlatformUtils
{
   public final static String pgmVersion = "1.5" ;
   public final static String pgmUpdate = "1/29/04" ;
   private static File srcFile;

    public PlatformUtils()
    {
        super();
    }

   

     public static boolean isLinux390()
    {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        //just want to make sure that if OLD PUI was run on this machine before, then it will delete the old wp.linux file.
         srcFile  = new File( WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "version" + File.separator + "wp.linux.component" );

        if ( osName.equalsIgnoreCase("Linux") && ( osArch.equalsIgnoreCase("s390") || osArch.equalsIgnoreCase("s390x") ) ) 
        {
           if ( srcFile.exists() )
           {
                srcFile.delete();
            }
        }

        return ( osName.equalsIgnoreCase("Linux") &&
                 ( osArch.equalsIgnoreCase("s390") || osArch.equalsIgnoreCase("s390x")) );
    }


    public static boolean isLinux()
    {
        String osName = System.getProperty("os.name");

        return ( osName.equalsIgnoreCase("Linux") );
    }

    public static boolean isSolaris()
    {
        String osName = System.getProperty("os.name");

        return ( osName.equalsIgnoreCase("SunOS") ||
                 osName.equalsIgnoreCase("Solaris") );
    }

    public static boolean isHpux()
    {
        String osName = System.getProperty("os.name");

        return ( osName.equalsIgnoreCase("HPUX") ||
                 osName.equalsIgnoreCase("HP-UX") );
    }

    public static boolean isAIX()
    {
        String osName = System.getProperty("os.name");
        
        return ( osName.equalsIgnoreCase("AIX") );
    }
    
    public static boolean isWindows()
    {
        String osName = System.getProperty("os.name");
        
        return ( osName.startsWith("Windows") );
        
        // return (osName.equalsIgnoreCase("Windows 2000") ||
        //         osName.equalsIgnoreCase("Windows NT") ||
        //         osName.equalsIgnoreCase("Windows XP"));
    }
    
    public static boolean isWinNT()
    {
        String osName = System.getProperty("os.name");
        
        return ( osName.equalsIgnoreCase("Windows NT") );
    }

    public static boolean isWin2000()
    {
        String osName = System.getProperty("os.name");
        
        return ( osName.equalsIgnoreCase("Windows 2000") );
    }

    public static boolean isWin2003()
    {
        String osName = System.getProperty("os.name");
        String osVer  = System.getProperty("os.version");
        
        return (  osName.equalsIgnoreCase("Windows 2003") || 
                 (osName.toLowerCase().startsWith("windows 2000") && osVer.equals( "5.2" )) );  // current 1.3.1 JDKs report this
    }

    public static boolean isWinXP()
    {
        String osName = System.getProperty("os.name");
        
        return ( osName.equalsIgnoreCase("Windows XP") );
    }
    
    public static boolean isZOs() {
       // TODO: what does zOS return
       String osName = System.getProperty("os.name");
       return ( osName.indexOf( "OS/390" ) != -1 ) ;
    }

    public static boolean isISeries() {
       // TODO: what does iSeries return
       String osName = System.getProperty("os.name");
       return ( osName.indexOf( "OS/400" ) != -1 ) ;
    }

    public static boolean isChineseLocale()
    {
        String locale = Locale.getDefault().toString();
        
        return ( locale.startsWith("zh") );
    }
    
    //  Taiwan HongKong Macau

    public static boolean isTraditionalChineseLocale()
    {
        String locale = Locale.getDefault().toString();
        
        return ( locale.startsWith("Zh_TW") );
    }
    
    //  Mainland China and Singapore

    public static boolean isSimplifiedChineseLocale()
    {
        String locale = Locale.getDefault().toString();

        return ( locale.startsWith("zh_CN") );
    }
    
    public static boolean isKoreanLocale()
    {
        String locale = Locale.getDefault().toString();

        return ( locale.startsWith("ko") );
    }

    public static boolean isJapaneseLocale()
    {
        String locale = Locale.getDefault().toString();

        return ( locale.startsWith("ja") );
    }
}

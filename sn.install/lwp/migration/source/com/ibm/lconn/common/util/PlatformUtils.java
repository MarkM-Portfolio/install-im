/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.util;


import java.util.Locale;

import com.ibm.lconn.common.config.CommonConstants;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class PlatformUtils
{
   public final static String pgmVersion = "1.5" ;
   public final static String pgmUpdate = "1/29/04" ;

    public PlatformUtils()
    {
        super();
    }

   

     public static boolean isLinux390()
    {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
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



	public static String getSimplePlatformType() {
			String os = System.getProperty("os.name");
			os = os.toLowerCase();
			if (os.indexOf("win") != -1)
				return CommonConstants.OS_WINDOWS;
			if (os.indexOf("aix") != -1)
				return CommonConstants.OS_AIX;
			if (os.indexOf("redhat") != -1)
				return CommonConstants.OS_LINUX_REDHAT;
			if (os.indexOf("suse") != -1)
				return CommonConstants.OS_LINUX_SUSE;
			return CommonConstants.OS_LINUX_REDHAT;
		}
}

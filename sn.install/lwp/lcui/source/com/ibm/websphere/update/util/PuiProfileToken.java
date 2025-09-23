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
package com.ibm.websphere.update.util;

/*
 * PuiProfileToken
 *
 * History 1.1, 6/1/07
 *
 */

import com.ibm.as400.access.*;
import com.ibm.as400.security.auth.*;

public class PuiProfileToken
{
   public static final String pgmVersion = "1.1" ;
   public static final String pgmUpdate = "6/1/07" ;

    protected static ProfileTokenCredential pt = new ProfileTokenCredential();
    
    protected static AS400Credential originalCredentials = null;

    public PuiProfileToken()
    {
      try 
      {
        pt.setSystem(new AS400("localhost", "*CURRENT", "*CURRENT"));
        pt.setTokenType(ProfileTokenCredential.TYPE_SINGLE_USE);
        pt.setTimeoutInterval(3600);

        pt.setToken("QEJBSVR", ProfileTokenCredential.PW_NOPWDCHK);
        
        originalCredentials = pt.swap(true);
        //pt.destroy();

      } catch (Exception ex) {
          System.out.println("PuiProfileToken() failed with exception: " + ex);
      }
    }


    public static ProfileTokenCredential getToken()
    {
        return pt;
    }


    public static void initToken()
    {
      try 
      {
        originalCredentials = pt.swap(true);

      } catch (Exception ex) {
          System.out.println("PuiProfileToken.initToken failed with exception: " + ex);
      }
    }


    public static void swapToken()
    {
      try 
      {
        if (originalCredentials != null)
            originalCredentials.swap();

      } catch (Exception ex) {
          System.out.println("PuiProfileToken.swapToken failed with exception: " + ex);
      }
    }


}

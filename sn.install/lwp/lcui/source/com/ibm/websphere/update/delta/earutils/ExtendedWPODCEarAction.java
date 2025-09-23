/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
*/
package com.ibm.websphere.update.delta.earutils;

import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

/*
 *  @ (#) ExtendedWebuiAction.java
 *
 *  Overload superclass 'ExtendedEARAction' to redefine
 *  'createActor' to return a WpsEarEARActor.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.*;

public abstract class ExtendedWPODCEarAction extends ExtendedWPEarAction {
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "10/7/03" ;

   public ExtendedWPODCEarAction() {
      super();
   }


   protected ExtendedEARActor createActor(String installPath,
                                          StringBuffer messages,
                                          StringBuffer errors,
                                          String earName,
                                          String appName,
                                          boolean doDeploy,
                                          boolean doPluginDeploy,
                                          boolean installableOnly,
                                          boolean applicationOnly) {

      return new WPOdcEARActor( getWasHomeDir(),
                                messages, errors,
                                earName, appName,
                                doDeploy, doPluginDeploy,
                                installableOnly, applicationOnly);

   }

   public ExtendedEARActor createActor(String installPath,
                                       StringBuffer messages,
                                       StringBuffer errors,
                                       Vector args)
   {
      if ( args.size() == 0 ) {
         errors.append("No EAR name argument is present.");
         return null;
      }

      Object thisArgument = args.elementAt(0);

      String earName;

      try {
         earName = (String) thisArgument;
      } catch ( ClassCastException e ) {
         errors.append("EAR actor argument one is not an EAR name: " + thisArgument + ".\n");
         return null;
      }

      String appName;
      if ( args.size() == 1 ) {
         errors.append("No Application name argument is present.");
         return null;
      }
      thisArgument = args.elementAt(1);
      try {
         appName = (String) thisArgument;
      } catch ( ClassCastException e ) {
         errors.append("EAR actor argument two is not an Application name: " + thisArgument + ".\n");
         return null;
      }
         
      boolean doDeploy = true;

      if ( args.size() > 2 ) {
         thisArgument = args.elementAt(2);

         String deployText;

         try {
            deployText = (String) thisArgument;
         } catch ( ClassCastException e ) {
            errors.append("EAR actor argument two is not text for a boolean value: " + thisArgument + ".\n");
            return null;
         }

         if ( ExtendedEARActor.isTrue(deployText) ) {
            doDeploy = true;
         } else if ( ExtendedEARActor.isFalse(deployText) ) {
            doDeploy = false;
         } else {
            errors.append("EAR actor argument two is not a valid boolean value: " + thisArgument + ".\n");
            return null;
         }

      }


      boolean doPluginDeploy = false;

      if ( args.size() > 3 ) {
         thisArgument = args.elementAt(3);

         String pluginDeployText;

         try {
            pluginDeployText = (String)thisArgument;
         } catch ( ClassCastException e ) {
            errors.append("EAR actor argument three is not text for a boolean value: " + thisArgument + ".\n");
            return null;
         }

         if ( ExtendedEARActor.isTrue(pluginDeployText) ) {
            doPluginDeploy = true;
         } else if ( ExtendedEARActor.isFalse(pluginDeployText) ) {
            doPluginDeploy = false;
         } else {
            errors.append("EAR actor argument three is not a valid boolean value: " + thisArgument + ".\n");
            return null;
         }
      }

      boolean installableOnly;
      boolean applicationOnly;

      if ( args.size() > 4 ) {
         String limitTag = (String) args.elementAt(4);

         if ( ExtendedEARActor.isInstallableOnly(limitTag) ) {
            installableOnly = true;
            applicationOnly = false;
         } else if ( ExtendedEARActor.isApplicationOnly(limitTag) ) {
            installableOnly = false;
            applicationOnly = true;
         } else {
            installableOnly = false;
            applicationOnly = false;

            errors.append("EAR actor argument four is not a valid limit;" +
                          " one of " + ExtendedEARActor.INSTALLABLE_ONLY_TAG +
                          " or " + ExtendedEARActor.APPLICATION_ONLY_TAG +
                          " is required.");
         }
      } else {
         installableOnly = false;
         applicationOnly = false;
      }


      return createActor(installPath, messages, errors,
                         earName, appName,
                         doDeploy, doPluginDeploy,
                         installableOnly, applicationOnly);
   }


}

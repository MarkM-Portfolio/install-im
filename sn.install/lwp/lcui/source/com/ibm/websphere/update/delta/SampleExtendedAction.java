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

/*
 * Script custom class supertype.
 *
 * History 1.2, 9/26/03
 */

import java.util.Vector;

public class SampleExtendedAction extends ExtendedUpdateAction
{
   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;

    public SampleExtendedAction()
    {
        super();
    }

    // Similar to the superclass defined method, but with
    // an added arguments vector.

    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errorMessage,
                       boolean debug,
                       Vector args)
    {
        System.out.println("Sample Extended Action; Processing ...");

        System.out.println("Target Directory: " + root);

        for ( int argNo = 0; argNo < args.size(); argNo++ )
            System.out.println("  >> [" + argNo + "]: " + args.elementAt(argNo));

        int rc = 0;

        System.out.println("Returning: " + rc);

        return rc;
    }
}

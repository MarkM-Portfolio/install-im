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
 * History 1.3, 3/19/04
 */

import java.util.Vector;

/**
 *  
 */
public class ExtendedUpdateAction extends UpdateAction
{
    public static final String pgmVersion = "1.3" ;
    public static final String pgmUpdate = "3/19/04" ;

    protected Extractor extractor = null;

    public ExtendedUpdateAction()
    {
        super();
    }

    // Added methods for the extended update action;
    // Don't modify 'UpdateAction', as this introduces
    // an incompatibility with other extenders.

    // Similar to the superclass defined method, but with
    // an added arguments vector.

    public Vector file2Backup(String root,
                              String[] components,
                              POProcessor po,
                              StringBuffer messages,
                              StringBuffer errorMessage,
                              boolean debug,
                              Vector args)
    {
        return null;
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
        return 0;
    }

    // make Extractor methods available to user class
    /**
	 * @param extractor  the extractor to set
	 * @uml.property  name="extractor"
	 */
    public void setExtractor( Extractor e )
    {
        this.extractor = e;
    }

    /**
	 * @return  the extractor
	 * @uml.property  name="extractor"
	 */
    public Extractor getExtractor()
    {
        return this.extractor;
    }

}

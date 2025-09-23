/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 1998, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.xml;

/*
 * Base Error Handler
 *
 * History 1.2, 9/26/03
 *
 * 22-Jul-2002 Initial Version
 */

import org.xml.sax.*;

/**
 *  
 */
public class BaseErrorHandler
    implements ErrorHandler
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public BaseErrorHandler(BaseFactory factory)
    {
        super();

        this.factory = factory;
    }

    // BaseFactory access ...

    protected BaseFactory factory;

    /**
	 * @return  the factory
	 * @uml.property  name="factory"
	 */
    protected BaseFactory getFactory()
    {
        return factory;
    }

    // Superclass API ...

    public void fatalError(SAXParseException ex)
        throws SAXParseException
    {
        throw ex; // Allow it to be thrown.
    }

    public void error(SAXParseException ex)
        throws SAXParseException
    {
        getFactory().addRecoverableError(ex);
    }

    public void warning(SAXParseException ex)
        throws SAXParseException
    {
        getFactory().addWarning(ex);
    }
}

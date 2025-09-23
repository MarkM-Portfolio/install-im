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
 * Base Entity Resolver
 *
 * History 1.2, 9/26/03
 *
 * 22-Jul-2002 Initial Version
 */

import java.io.*;
import org.xml.sax.*;

/**
 *  
 */
public class BaseEntityResolver
    implements EntityResolver
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    // Don't care about the public id.

    // The system ID is a DTD file name relative to the DTD directory.

    public BaseEntityResolver(BaseFactory factory, String dtdDir)
    {
        super();

        this.factory = factory;
        this.dtdDir = dtdDir;

        // System.out.println(">>>>> DTD Directory: " + dtdDir + " <<<<");
    }

    // Factory access ...

    protected BaseFactory factory;

    /**
	 * @return  the factory
	 * @uml.property  name="factory"
	 */
    protected BaseFactory getFactory()
    {
        return factory;
    }

    protected void setException(String publicId, String systemId, String systemName,
                                Exception ex)
    {
        getFactory().setException("WVER0108E",
                                  new String[] { publicId, systemId, systemName },
                                  ex);
    }

    // DTD access ...

    protected String dtdDir;

    public String getDTDDir()
    {
        return dtdDir;
    }

    public String resolveSystemId(String systemId)
    {
        int slashLoc = systemId.lastIndexOf("/");

        String dtdFileName;

        if ( slashLoc == -1 )
            dtdFileName = systemId;
        else
            dtdFileName = systemId.substring(slashLoc + 1);

        return getDTDDir() + File.separator + dtdFileName;
    }

    // Superclass API ...

    public InputSource resolveEntity(String publicId, String systemId)
    {
        // System.out.println(">>>>> Resolving System Name: " + systemId + " <<<<");

        final String systemName = resolveSystemId(systemId);

        // System.out.println(">>>>> Resolved System Name: " + systemName + " <<<<");

        try {
            FileInputStream inputStream = new FileInputStream(systemName);
            // throws FileNotFoundException

            return new InputSource(inputStream);

        } catch ( FileNotFoundException e ) {
            setException(publicId, systemId, systemName, e);

            return null;
        }
    }
}

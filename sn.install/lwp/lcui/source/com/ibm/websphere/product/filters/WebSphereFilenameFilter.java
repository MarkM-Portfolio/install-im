/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.filters;

/*
 * WebSphere File Filter
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.io.File;
import java.io.FilenameFilter;

import com.ibm.websphere.product.xml.websphere.WebSphereHandler;

public class WebSphereFilenameFilter implements FilenameFilter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Filtering ...

    /**
     * @see FilenameFilter#accept(File, String)
     */

    public boolean accept(File dir, String name) 
    {
        return WebSphereHandler.accepts(name);
    }
}

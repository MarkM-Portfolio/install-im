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

package com.ibm.websphere.product.history.filters;

/*
 * PTF Applied File Filter
 *
 * History 1.2, 9/26/03
 *
 * 20-Aug-2002 Initial Version
 */

import java.io.File;
import java.io.FilenameFilter;

import com.ibm.websphere.product.history.xml.AppliedHandler;

public class PTFAppliedFilenameFilter implements FilenameFilter
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
        return AppliedHandler.acceptsPTFAppliedFileName(name);
    }
}

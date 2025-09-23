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

package com.ibm.websphere.product.history.xml;

/*
 * Include EFix Bean
 *
 * History 1.2, 9/26/03
 *
 * 18-Aug-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class includedEFix extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Field tags ...

    public static final String
        EFIX_ID_FIELD_TAG = "efix-id";

    // efix-id     (String) [required] (The id of an efix fixed by a PTF.)

    // Instantor ...

    public includedEFix()
    {
        super();
    }
    
    // Basic field access ...

    // See the field tags, above ...

    protected String efixId;

    public void setEFixId(String efixId)
    {
        this.efixId = efixId;
    }
    
    public String getEFixId()
    {
        return efixId;
    }
}

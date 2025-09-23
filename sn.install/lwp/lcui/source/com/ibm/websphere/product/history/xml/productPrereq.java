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
 * Product Prerequisite Bean
 *
 * History 1.2, 9/26/03
 *
 * 01-Jul-2002 Initial Version
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class productPrereq extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public productPrereq()
    {
        super();
    }
    
    // Basic field access ...

    // See the field tags, above ...

    protected String productId;

    /**
	 * @param productId  the productId to set
	 * @uml.property  name="productId"
	 */
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
    
    /**
	 * @return  the productId
	 * @uml.property  name="productId"
	 */
    public String getProductId()
    {
        return productId;
    }

    protected String buildVersion;

    /**
	 * @param buildVersion  the buildVersion to set
	 * @uml.property  name="buildVersion"
	 */
    public void setBuildVersion(String buildVersion)
    {
        this.buildVersion = buildVersion;
    }
    
    /**
	 * @return  the buildVersion
	 * @uml.property  name="buildVersion"
	 */
    public String getBuildVersion()
    {
        return buildVersion;
    }
    
    protected String buildDate;

    /**
	 * @param buildDate  the buildDate to set
	 * @uml.property  name="buildDate"
	 */
    public void setBuildDate(String buildDate)
    {
        this.buildDate = buildDate;
    }
    
    /**
	 * @return  the buildDate
	 * @uml.property  name="buildDate"
	 */
    public String getBuildDate()
    {
        return buildDate;
    }

    protected String buildLevel;
    
    /**
	 * @param buildLevel  the buildLevel to set
	 * @uml.property  name="buildLevel"
	 */
    public void setBuildLevel(String buildLevel)
    {
        this.buildLevel = buildLevel;
    }
    
    /**
	 * @return  the buildLevel
	 * @uml.property  name="buildLevel"
	 */
    public String getBuildLevel()
    {
        return buildLevel;
    }
    
    public String toString(){
    	return "productId:" + productId + " buildVersion:" + buildVersion + " buildDate:" + buildDate + " buildLevel: " + buildLevel;
    }
}

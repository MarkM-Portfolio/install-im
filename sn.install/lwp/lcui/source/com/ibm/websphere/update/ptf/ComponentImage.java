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
package com.ibm.websphere.update.ptf;


/*
 * Component Image
 *
 * History 1.2, 1/15/03
 *
 * 22-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

import com.ibm.websphere.product.history.xml.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public abstract class ComponentImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "1/15/03" ;

    // Instantor ...

    protected ComponentImage(UpdateImage parentImage,
                             String componentName)
    {
        this.parentImage = parentImage;

        this.componentName = componentName;

        this.retrievedUpdate = false;
        this.update = null;
    }

    // Parent Image access ...

    protected UpdateImage parentImage;

    /**
	 * @return  the parentImage
	 * @uml.property  name="parentImage"
	 */
    public UpdateImage getParentImage()
    {
        return parentImage;
    }

    public String getId()
    {
        return getParentImage().getUpdateId();
    }

    public Object getDriver()
    {
        return getParentImage().getDriver();
    }

    public String getJarName()
    {
        return getParentImage().getJarName();
    }

    // Component access ...

    protected String componentName;

    protected boolean retrievedUpdate;
    protected componentUpdate update;

    /**
	 * @return  the componentName
	 * @uml.property  name="componentName"
	 */
    public String getComponentName()
    {
        return componentName;
    }

    public boolean retrievedUpdate()
    {
        return retrievedUpdate;
    }

    /**
	 * @return  the update
	 * @uml.property  name="update"
	 */
    public componentUpdate getUpdate()
    {
        if ( !retrievedUpdate ) {
            retrievedUpdate = true;
            update = retrieveUpdate();
        }

        return update;
    }

    public boolean getIsRequired()
    {
        componentUpdate useUpdate = getUpdate();

        if ( useUpdate == null )
            return false;
        else
            return useUpdate.getIsRequiredAsBoolean();
    }

    protected abstract componentUpdate retrieveUpdate();

    public String getComponentEntryName()
    {
        return getParentImage().getComponentsEntryName() + "/" + getComponentName();
    }


	//Change note: returns a list of primary Contents
	//necessary for mq updates
    public List getPrimaryContentEntryName()
    {
    	List primaryContentList = new ArrayList();
		String primaryContent = getUpdate().getPrimaryContent();
		
		if(primaryContent.indexOf("!") >= 0){
			StringTokenizer st = new StringTokenizer(primaryContent, "!");
			while(st.hasMoreTokens()){
				String aPrimaryContentEntry	= st.nextToken();
				primaryContentList.add(getComponentEntryName() + "/" + aPrimaryContentEntry);
			}
		}else{						
			primaryContentList.add(getComponentEntryName() + "/" + primaryContent);
		}
    	
		return primaryContentList;
    }
    
    public String toString(){
    	return " parentImage:" + parentImage + " componentName:" + componentName + " retrievedUpdate:" + retrievedUpdate + 
    	       " update:" + update; 
    }
}

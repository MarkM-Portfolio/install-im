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
 * EFix Install Image
 *
 * This eFixImage object represents a single eFix as it
 * is shipped to a customer.
 *
 * History 1.1, 12/20/02
 *
 * 09-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

/**
 * <p>Expected efix jar contents.  One or more efix may be
 * present, as follows:</p>
 * <pre>
 *     efixes\\
 *
 *     efixes\\efix1\\
 *     efixes\\efix1\\efix1.efix
 *     efixes\\efix1\\components\\
 *     efixes\\efix1\\components\\component1\\
 *     efixes\\efix1\\components\\component1\\update.jar
 *     efixes\\efix1\\components\\component2\\
 *     efixes\\efix1\\components\\component2\\update.jar
 *
 *     efixes\\efix2\\
 *     efixes\\efix2\\efix2.efix
 *     efixes\\efix2\\components\\
 *     efixes\\efix2\\components\\component1\\
 *     efixes\\efix2\\components\\component1\\update.jar
 *     efixes\\efix2\\components\\component2\\
 *     efixes\\efix2\\components\\component2\\update.jar
 *
 */

import java.io.*;
import java.util.*;

import org.xml.sax.InputSource;

import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.history.xml.*;

import com.ibm.websphere.update.*;
import com.ibm.websphere.update.ioservices.*;

public class EFixImage extends UpdateImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "12/20/02" ;

    // Instantor ...

    public EFixImage(IOService ioService, String dtdDirName, String jarName, String efixId)
    {
        super(ioService, dtdDirName, jarName, efixId);
    }

    public String getEFixId()
    {
        return getUpdateId();
    }

    // EFix Entry Identification ...

    public static final String EFIXES_DIR = "efixes";

    public String getUpdatesDir()
    {
        return EFIXES_DIR;
    }

    public String getDriverExtension()
    {
        return AppliedHandler.EFIX_DRIVER_FILE_EXTENSION;
    }

    // EFix Access ...

    public efixDriver getEFixDriver()
    {
        return (efixDriver) getDriver();
    }

    protected ComponentImage
        createComponentImage(UpdateImage parentImage, String componentName)
    {
        return new EFixComponentImage((EFixImage) parentImage, componentName);
    }
    
    public String toString(){
    	return "IOService:" + getIOService() + " JarName:" + getJarName() + 
    	       " UpdateId:" + getUpdateId() + " dtdDirName:" + getDtdDirName() + " EFixDriver:" + getEFixDriver(); 
    }
}

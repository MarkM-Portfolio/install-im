/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */


package com.ibm.prs.common.reports.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ibm.prs.common.exception.PRSApiException;

/**
 * PRSXmlResultValidator class provides methods to validate the results XML file 
 * that Prerequisite Scanner generates.
 * 
 * @author jichen
 * @version 1.0
 * 
 */

public class PRSXmlResultValidator {
    
    String xmlFileName = null;
    String schemaFileName = null;
	
    /**
     * Constructor for the <code>PRSXmlResultValidator</code> class.
     * 
     * @param xmlFileName the fully qualified path and file name for the result XML file
     * @param schemaFileName  the fully qualified path and file name for the result XML schema definition
     */
	public PRSXmlResultValidator (String xmlFileName, String schemaFileName)
	{
	    this.xmlFileName = xmlFileName;
	    this.schemaFileName = schemaFileName;
	}
 
	/**
	 * Validates the results XML file against the XML schema definition file.
	 * 
	 * @return a boolean value to indicate whether the XML is valid based on its schema definition
	 * @throws PRSApiException
	 */
	public boolean isXmlValidatedAgainstSchema () throws PRSApiException
	{
	    boolean isValidated = true;
	    
	    Schema schema = loadSchema();
	    isValidated = isXmlValidatedWithSchema(schema);
	    
	    return isValidated;
	}
	
	/**
	 * Checks whether the result XML file is well formatted.
	 * 
	 * @return a boolean value to indicate whether the XML is well formatted
	 * @throws PRSApiException 
	 */
    public boolean isXMLWellFormatted() throws PRSApiException{
        boolean isWellFormatted = false;
        try{
            File file = new File(xmlFileName);
            if (file.exists()){
                XMLReader reader = XMLReaderFactory.createXMLReader();
                reader.parse(xmlFileName);
                isWellFormatted = true;
            }
            else
            {
                PRSApiException pe = new PRSApiException(xmlFileName+" not found exception.");
                throw pe;
            }
         }
         catch (SAXException sax){
             //System.out.println(xmlFileName + " isn't well-formed");
             System.out.println("(PRSXmlResultValidator.isXMLWellFormatted)SAXException: "+sax.getLocalizedMessage());
             sax.printStackTrace();
             return isWellFormatted;
         }
         catch (IOException io){
             System.out.println("PRSXmlResultValidator.isXMLWellFormatted)IOException: "+io.getLocalizedMessage());
             io.printStackTrace();
             PRSApiException pe = new PRSApiException(io.getLocalizedMessage(), io.getCause());
             throw pe;
         }
         
         return isWellFormatted;
    }
    
    
    //----------------------------- private methods -----------------------/
    
    /**
     *  Loads the Prerequisite Scanner XML schema definition file.
     *  
     *  @return the loaded schema
     */
    private Schema loadSchema() throws PRSApiException{
        Schema schema = null;
        File schemaFile = new File(schemaFileName);
        try 
        {
          String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
          SchemaFactory factory = SchemaFactory.newInstance(language);
          schema = factory.newSchema(schemaFile);
        } catch (SAXException e) {
            e.printStackTrace();
            System.out.println("(PRSXmlResultValidator.loadSchema)SAXException: "+e.getLocalizedMessage());
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        }
        return schema;
    }
    
    /**
     *  Validates the result XML file against the XML schema definition.
     *  
     *  @return a boolean value to indicate whether the XML is valid based on its schema
     */
    private boolean isXmlValidatedWithSchema(Schema schema) throws PRSApiException{
        boolean isXmlValidatedWithSchema = false;
        Validator validator = schema.newValidator();
        //preparing the XML file as a SAX source
        SAXSource xmlSource;
        try {
            xmlSource = new SAXSource(
                    new InputSource(new FileInputStream(xmlFileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        }

        // validating the xml against the schema
        try {
            validator.validate(xmlSource);
        } catch (SAXException e) 
        {
            isXmlValidatedWithSchema = false;
            System.err.println("(PRSXmlResultValidator.isXmlValidatedWithSchema)SAXException: " + e.getLocalizedMessage());
            e.printStackTrace();
            return isXmlValidatedWithSchema;
        } catch (IOException e) 
        {
            isXmlValidatedWithSchema = false;
            System.err.println("(PRSXmlResultValidator.isXmlValidatedWithSchema)IOException: " + e.getLocalizedMessage());
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        } 

        isXmlValidatedWithSchema = true;
        return isXmlValidatedWithSchema;
    }
    
    //====================Main Method =====================//

    /**
     * Runs the <code>PRSXmlResultValidator</code> utility, requiring 2 arguments to be passed to it.
     * 
     * @param args two required arguments, as follows:
     * <ul>
     * <li><code>args[0]</code> - the fully qualified path and file name for the result XML file</li>
     * <li><code>args[1]</code> - the fully qualified path and file name for the result XML schema definition</li>
     * </ul>      
     */
    public static void main(String[] args) {
        if (args.length != 2)
        {
            System.out.println("Usage: java PRSXmlResultValidator <xmlFileName> <schemaFileName>");
            System.exit(-1);
        } 
        
        PRSXmlResultValidator thisValidator = new PRSXmlResultValidator(args[0], args[1]);
        
        //1) validating XML file format
        boolean isXmlWellFormatted = false;
        try {
            isXmlWellFormatted = thisValidator.isXMLWellFormatted();
        
            if (isXmlWellFormatted)
                System.out.println("Format validation successful - " + thisValidator.xmlFileName + " is well formatted.");
            else
                System.out.println("Format validation ERROR: "+ thisValidator.xmlFileName + " is well formatted.");
        } catch (PRSApiException e1) {
            System.err.println("ERROR: PRSApiException - " + e1.getLocalizedMessage());
            e1.printStackTrace();
            System.exit(-1);
        }

        //2) validating XML file against xsd schema
        boolean isXmlValidatedWithSchema = true;
        try
        {
            isXmlValidatedWithSchema = thisValidator.isXmlValidatedAgainstSchema();
          if (isXmlValidatedWithSchema)
              System.out.println("Schema validation successful - " + thisValidator.xmlFileName + " is successfully validated against schema.");
          else
              System.out.println("Schema validation ERROR: "+ thisValidator.xmlFileName + " is failed to validate against schema.");
        } catch (PRSApiException e)
        {
            System.err.println("Exception while validating " + thisValidator.xmlFileName + " with schema " + thisValidator.schemaFileName);
            System.exit(-1);
        }
        
        if (!isXmlWellFormatted || !isXmlValidatedWithSchema)
        {
            System.out.println("XML format or schema validation failed.  Exiting.");
            System.exit(-1);
        }
    }

}

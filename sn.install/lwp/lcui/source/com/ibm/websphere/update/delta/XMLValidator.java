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
 *  XML handler using SAX parser
 *
 *  also see XMLHandler.java
 *
 * History 1.1, 3/23/06
 *
 */
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;


class XMLValidator extends DefaultHandler
{
    // Default parser name
    protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    // Validation feature id
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
    // Default validation support (false), enable this for dtd/xsd validation
    protected static final boolean DEFAULT_VALIDATION = true;

    // Schema validation feature id
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";
    // Default Schema validation support (false), enable this for xsd validation
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = true;

    // Schema full checking feature id
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";
    // Default Schema full checking support (false), enable this for xsd full check
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = true;

    // use this property to specify external schema location
    protected static final String EXTERNAL_SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    // use this property to specify external nonamespace schema location
    protected static final String EXTERNAL_NONAMESPACE_SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

    protected final String slash = System.getProperty("file.separator");
    private String xmlFile;
    private String dtdFile;
    private File file = null;           // temp file for the schema
    private String fileLoc;             // path location of the xml file

    /**
     * Constructor
     *
     * @param xmlFile       name of the xml file
     * @param dtdFile    full path or relative path if in the jar of the dtd file
     */
    public XMLValidator(String xmlFile, String dtdFile)
    {
        this.xmlFile = xmlFile;
        this.dtdFile = dtdFile;
    }

    /**
     * validate the xml file
     *
     * @return  true or false
     */
    public boolean validate()
    {
        boolean bValid = false;

        if(!verifyFile())
            return false;
        else
        {
            writeOutFile(dtdFile);  // write dtd file out from the jar

            XMLReader parser = null;
            try
            {
                parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);

            } catch (SAXException e) {
                try
                {
                    parser = XMLReaderFactory.createXMLReader();
                } catch (SAXException ex) {
                    System.err.println("error: Unable to instantiate parser.");
                    System.err.println(e.getMessage());
                }
            }
            // if you want to enable dtd validation, the dtd file must be in the same folder
            // of where the xml file is, or it is in the path specified in the xml file's doctype
            try
            {
                if(parser != null)
                {
                    parser.setFeature(VALIDATION_FEATURE_ID, DEFAULT_VALIDATION);

                    // parse file
                    XMLHandler xmlHandler = new XMLHandler();
                    parser.setContentHandler(xmlHandler);
                    parser.setErrorHandler(xmlHandler);
                    parser.parse(xmlFile);
                    bValid = xmlHandler.isbValid();
                }
            }catch (SAXException e)
            {
                System.err.println(e.getMessage());
                System.err.println(e.getMessage());
            }catch (IOException e)
            {
                System.err.println(e.getMessage());
                 e.printStackTrace();
            }
        }
        return bValid;
    }

    /**
     * Verify the xml (efixDriver/ptfDriver) file's existence
     *
     * @return  true or false
     */
    public boolean verifyFile()
    {
        boolean b;

        // is xmlFile a directory or a file?
        File tempFile = new File(xmlFile).getAbsoluteFile();
        if (tempFile.isDirectory())  // is a directory, search for the driver file in it
        {
            String[] children = tempFile.list();
            for (int i=0; i<children.length; i++)
            {
                // Get filename of file or directory
                String filename = children[i];
                // check to see if xmlFile ends with "efixdriver" or "ptfdriver"
                if(filename.matches("(?i).*efixdriver")||filename.matches("(?i).*ptfdriver"))
                {
                    tempFile = new File(tempFile.toString()+slash+filename).getAbsoluteFile();
                    xmlFile = tempFile.toString();
                    break;
                }
            }
        }

        fileLoc = tempFile.getParent();     // location for dtdFile
        if (!tempFile.exists())
        {
            System.out.println("The input file " + tempFile.toString() + " does not exist.");
            b = false;
        }
        else
            b = true;

        return b;
    }

    // same location as the efixDriver is
    /**
     * Write the file out from the jar
     *
     * @param source    source of the content file
     */
    private void writeOutFile(String source)
    {
        InputStream in = XMLValidator.class.getResourceAsStream(source);
        String fileName = constructFile(source);
        //System.out.println(fileName);
        try
        {
            file = new File(fileName);
            if(file.exists())
                file.delete();
            file.createNewFile();
            String line;
            StringBuffer strBuf = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null)
            {
                strBuf.append(line).append('\n');
            }
            br.close();
            in.close();

            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(strBuf.toString());
            out.close();
         } catch (IOException e)
         {
            System.out.println(e.getMessage());
         }
    }

    private String constructFile(String source)
    {
        File fsource = new File(source);
        String fname = fsource.getName();
        //System.out.println(fname);

        return fileLoc+slash+fname;
    }

 }

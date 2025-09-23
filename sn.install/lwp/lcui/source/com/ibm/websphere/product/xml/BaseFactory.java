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

package com.ibm.websphere.product.xml;

/*
 * Base Factory
 *
 * History 1.2, 9/26/03
 *
 * 24-Aug-2002 Initial Version
 * 07-Oct-2002 Defect 145593; Force parser class
 */

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.*;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *  
 */
public class BaseFactory
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...
    //
    // public BaseFactory(BaseHandler, BaseWriter);

    public BaseFactory(BaseHandler handler, BaseWriter writer)
    {
        this.handler = handler;
        this.writer = writer;

        this.boundException = null;
        this.recoverableErrors = new Vector();
        this.warnings = new Vector();
    }

    // Handler access ...
    //
    // public BaseHandler getHandler();
    // public BaseWriter getWriter();

    protected BaseHandler handler;

    /**
	 * @return  the handler
	 * @uml.property  name="handler"
	 */
    public BaseHandler getHandler()
    {
        // if ( handler == null )
        //     System.out.println(">>> NULL HANDLER!! <<<");

        return handler;
    }

    protected BaseWriter writer;

    /**
	 * @return  the writer
	 * @uml.property  name="writer"
	 */
    public BaseWriter getWriter()
    {
        return writer;
    }

    // Saving ...
    //
    // public static BaseHandlerException saveSingleton(Object, BaseWriter, String);
    // public boolean save(String filename);

    public static BaseHandlerException saveSingleton(Object rootElement,
                                                     BaseWriter writer,
                                                     String filename)
    {
        // System.out.println("Emitting single element of type " + rootElement.getClass().getName());

        ArrayList roots = new ArrayList();
        roots.add(rootElement);

        BaseFactory factory = new BaseFactory(null, writer);

        if ( !factory.save(filename, roots) )
            return factory.getException();
        else
            return null;
    }

    public boolean save(String filename, List rootElements)
    {
        getWriter().emit(rootElements, filename);

        Exception writerException = writer.getBoundException();

        if ( writerException != null ) {
            setException("WVER0107E", new Object[] { filename }, writerException);
            return false;
        } else {
            return true;
        }
    }

    // Parse support ...
    //
    // public List load(String, String, String);
    // public List load(String, InputSource);

    public static Object loadSingleton(BaseHandler handler,
                                       String dtdDirName,
                                       String dirName,
                                       String fileName)
        throws BaseHandlerException
    {
        // Don't need a writer
        BaseFactory factory = new BaseFactory(handler, null);

        List rootElements = factory.load(dtdDirName, dirName, fileName);

        BaseHandlerException handlerException = factory.getException();
        if ( handlerException != null )
            throw handlerException;

        Iterator roots = rootElements.iterator();

        if ( roots.hasNext() )
            return roots.next();
        else
            return null;
    }

    public static Object loadSingleton(BaseHandler handler,
                                       String dtdDirName,
                                       InputSource inputSource,
                                       String sourceName)
        throws BaseHandlerException
    {
        // Don't need a writer
        BaseFactory factory = new BaseFactory(handler, null);

        List rootElements = factory.load(dtdDirName, inputSource, sourceName);

        BaseHandlerException handlerException = factory.getException();
        if ( handlerException != null )
            throw handlerException;

        Iterator roots = rootElements.iterator();

        if ( roots.hasNext() )
            return roots.next();
        else
            return null;
    }

    public List load(String dtdDirName, String dirName, String fileName)
    {
        InputSource inputSource = openSource(dirName, fileName);

        return load(dtdDirName, inputSource, fileName);
    }

    public List load(String dtdDirName, InputSource inputSource, String sourceName)
    {
        XMLReader reader;

        try {
            reader = createReader(dtdDirName); // throws SAXException

        } catch (SAXException e) {
            setException("WVER0101E",
                         new Object[] { dtdDirName, sourceName  },
                         e);
            return null;
        }

        try {
            reader.parse(inputSource); // throws SAXParseException, SAXException, IOException

        } catch ( SAXParseException e ) {
            e.printStackTrace(System.out);

            setException("WVER0102E",
                         new Object[] { dtdDirName, sourceName,
                                        e.getSystemId(),
                                        e.getPublicId(),
                                        new Integer(e.getLineNumber()),
                                        new Integer(e.getColumnNumber()) },
                         e);
            return null;

        } catch ( Exception e ) {
            e.printStackTrace(System.out);

            setException("WVER0101E",
                         new Object[] { dtdDirName, sourceName  },
                         e);

            return null;
        }

        return (List) (getHandler().getObject());
    }

    // Parse support ...
    //
    // XMLReader createReader(String);
    // EntityResolver createEntityResolver(String);
    // ErrorHandler createErrorHandler();

    public static final String NAMESPACES_FEATURE_NAME = "http://xml.org/sax/features/namespaces" ;
    // Parse support ...
    //
    // XMLReader createReader(String);
    // EntityResolver createEntityResolver(String);
    // ErrorHandler createErrorHandler();

    public static final String VALIDATE_FEATURE_NAME = "http://xml.org/sax/features/validation" ;
    // Parse support ...
    //
    // XMLReader createReader(String);
    // EntityResolver createEntityResolver(String);
    // ErrorHandler createErrorHandler();

    public static final String LOAD_EXTERNAL_DTD_FEATURE_NAME = "http://apache.org/xml/features/nonvalidating/load-external-dtd" ;

    // Test output of the next two println's:
    //   Reader Property: null
    //   Reader Class: org.apache.xerces.parsers.SAXParser

    protected XMLReader createReader(String dtdDirName)
        throws SAXException, SAXNotRecognizedException
    {
        boolean validating = isValidating();

	// System.out.println("Reader Property: " + System.getProperty("org.xml.sax.driver"));

	// Start: Defect 145593
	//
	// Would prefer to not do this, but there is no workaround.
	// No guarentee is made that the driver property name is null at this point.

	System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");

        XMLReader reader = XMLReaderFactory.createXMLReader(); // throws SAXException

	// System.out.println("Reader Class: " + reader.getClass().getName());

	// End: Defect 145593

        reader.setFeature(NAMESPACES_FEATURE_NAME,        true);
        reader.setFeature(VALIDATE_FEATURE_NAME,          validating);
        reader.setFeature(LOAD_EXTERNAL_DTD_FEATURE_NAME, validating);
        // 'setFeature' throws SAXNotRecognizedException

        reader.setEntityResolver( createEntityResolver(dtdDirName) );
        reader.setContentHandler( getHandler() );
        reader.setErrorHandler  ( createErrorHandler() );

        return reader;
    }

    protected EntityResolver createEntityResolver(String dtdDir)
    {
        return new BaseEntityResolver(this, dtdDir);
    }

    protected ErrorHandler createErrorHandler()
    {
        return new BaseErrorHandler(this);
    }

    // Input source utility ...
    //
    //  InputSource openSource(String, String);

    protected InputSource openSource(String dirName, String fileName)
    {
        File inputFile = new File(dirName, fileName);
        String inputFilePath = inputFile.getAbsolutePath();

        return new InputSource(inputFilePath);
    }

    // System property access ... 
    //
    // boolean isValidating();

    public static final String VALIDATING_PROPERTY_NAME = "was.version.validate" ;
    // System property access ... 
    //
    // boolean isValidating();

    public static final String VALIDATING_PROPERTY_TRUE_VALUE = "true" ;
    // System property access ... 
    //
    // boolean isValidating();

    public static final String VALIDATING_PROPERTY_FALSE_VALUE = "false" ;

    protected boolean isValidating()
    {
        String validatingValue = System.getProperty(VALIDATING_PROPERTY_NAME);

        return ( (validatingValue == null) ||
                 !(validatingValue.equalsIgnoreCase(VALIDATING_PROPERTY_FALSE_VALUE)) );
    }

    // Exception access ...

    protected BaseHandlerException boundException;

    protected void setException(BaseHandlerException e)
    {
        boundException = e;
    }

    public BaseHandlerException getException()
    {
        return boundException;
    }

    protected void clearException()
    {
        boundException = null;
    }

    protected void setException(String msgKey)
    {
        setException(createException(msgKey, null, null));
    }

    protected void setException(String msgKey, Exception e)
    {
        setException(createException(msgKey, null, e));
    }

    protected void setException(String msgKey, Object[] msgArgs)
    {
        setException(createException(msgKey, msgArgs, null));
    }

    protected void setException(String msgKey, Object[] msgArgs, Exception e)
    {
        setException(createException(msgKey, msgArgs, e));
    }

    protected BaseHandlerException createException(String msgKey, Object[] msgArgs, Exception e)
    {
        return new BaseHandlerException(msgKey, msgArgs, e);
    }

    // Exception lists ...

    protected Vector recoverableErrors;

    /**
	 * @return  the recoverableErrors
	 * @uml.property  name="recoverableErrors"
	 */
    public Iterator getRecoverableErrors()
    {
        return recoverableErrors.iterator();
    }

    protected void addRecoverableError(SAXException e)
    {
        recoverableErrors.add(e);
    }

    public void clearRecoverableErrors()
    {
        recoverableErrors = new Vector();
    }

    protected Vector warnings;

    /**
	 * @return  the warnings
	 * @uml.property  name="warnings"
	 */
    public Iterator getWarnings()
    {
        return warnings.iterator();
    }

    protected void addWarning(SAXException e)
    {
        warnings.add(e);
    }

    public void clearWarnings()
    {
        warnings = new Vector();
    }
}

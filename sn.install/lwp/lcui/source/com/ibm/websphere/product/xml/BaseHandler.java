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
 * Base Parse Handler ...
 *
 * History 1.2, 9/26/03
 *
 * 24-Aug-2002 Initial Version
 */

import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 *  
 */
public abstract class BaseHandler
    extends DefaultHandler
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public BaseHandler()
    {
    }

    // Object access ...
    //
    // public Object getObject();

    public Object getObject()
    {
        return rootElements;
    }

    // Parse API ...
    //
    // public void startDocument() throws SAXException;
    // public void endDocument() throws SAXException;
    // public void startElement(String, String, String, Attributes) throws SAXException;
    // public void endElement() throws SAXException;

    public void startDocument()
        throws SAXException
    {
        initializeElements();
    }

    public void endDocument()
        throws SAXException
    {
        if ( elementNameTrain.size() != 0 )
            throw new SAXException(getString("WVER0203E", new Object[] {} ));
    }

    public void startElement(String documentUri,
                             String elementName,
                             String qualifiedName,
                             Attributes attributes)
        throws SAXException
    {
        // System.out.println("Starting element: " + elementName);
        // System.out.println("Document URI: " + documentUri);
        // System.out.println("Handler: " + getClass().getName());

        Object element;

        try {
            element = createElement(elementName,
                                    currentElementName, currentElement,
                                    attributes); // throws SAXException
        } catch (Throwable th) {
            System.out.println("Throwable: " + th);
            th.printStackTrace(System.out);

            element = null;
        }

        pushElement(elementName, element);
    }

    public void endElement(String documentUri, String elementName, String qualifiedName)
        throws SAXException
    {
        popElement(elementName); // throws SAXParseException
    }

    // Element Text Handling ...

    protected void startCapturing()
    {
        currentCaptureFlag = true;
    }

    protected String stopCapturing()
    {
        String result;

        if ( !currentCaptureFlag ) {
            result = "";
        } else {
            currentCaptureFlag = false;

            if ( currentCaptureBuffer == null ) {
                result = "";
            } else {
                result = currentCaptureBuffer.toString();
                currentCaptureBuffer.setLength(0);
            }
        }

        return result;
    }

    public void characters(char[] buffer, int offset, int length)
        throws SAXException
    {
        if ( !currentCaptureFlag )
            return;

        if ( currentCaptureBuffer == null ) {
            currentCaptureBuffer = new StringBuffer();
            captureTrain.set(captureTrain.size() - 1, currentCaptureBuffer);
        }

        currentCaptureBuffer.append(buffer, offset, length);
    }

    // Element Utility ...
    //
    // abstract Object createElement(String, String, Object, attributes)
    //     throws SAXParseException;
    // String getAttribute(Attributes, String)
    //     throws SAXParseException;

    protected abstract Object createElement(String childElementName,
                                            String parentElementName, Object parentElement,
                                            Attributes attributes)
        throws SAXParseException;

    protected String getAttribute(Attributes attributes, String attributeName,
                                  String elementName, String defaultValue)
        throws SAXParseException
    {
        String attributeValue = attributes.getValue(attributeName);

        if ( attributeValue == null ) {
            if ( defaultValue == null )
                throw newException("WVER0201E", new Object[] { attributeName, elementName } );
            else
                return defaultValue;
        } else {
            return attributeValue;
        }
    }

    // Element handling
    //    protected String getCurrentElementName()
    //    protected Object getCurrentElement()
    //    protected void initializeElements()
    //    protected void pushElement(String elementName, Object element)
    //    protected Object popElement(String)

    protected ArrayList    rootElements;

    protected ArrayList    elementNameTrain;
    protected ArrayList    elementTrain;
    protected ArrayList    captureFlagTrain;
    protected ArrayList    captureTrain;

    protected String       currentElementName;
    protected Object       currentElement;
    protected boolean      currentCaptureFlag;
    protected StringBuffer currentCaptureBuffer;

    /**
	 * @return  the currentElementName
	 * @uml.property  name="currentElementName"
	 */
    protected String getCurrentElementName()
    {
        return currentElementName;
    }

    /**
	 * @return  the currentElement
	 * @uml.property  name="currentElement"
	 */
    protected Object getCurrentElement()
    {
        return currentElement;
    }

    protected void initializeElements()
    {
        rootElements = new ArrayList();

        elementNameTrain = new ArrayList();
        elementTrain     = new ArrayList();
        captureFlagTrain = new ArrayList();
        captureTrain     = new ArrayList();

        currentElementName   = null;
        currentElement       = null;
        currentCaptureFlag   = false;
        currentCaptureBuffer = null;
    }

    protected void pushElement(String elementName, Object element)
    {
        if ( elementTrain.size() == 0 )
            rootElements.add(element);

        elementNameTrain.add(elementName);
        elementTrain.add(element);
        captureFlagTrain.add(new Boolean(false));
        captureTrain.add(currentCaptureBuffer);

        currentElementName = elementName;
        currentElement = element;
        currentCaptureFlag = false;
        currentCaptureBuffer = null;
    }

    protected Object popElement(String elementName)
        throws SAXParseException
    {
        int trainLength = elementTrain.size();

        if ( trainLength == 0 )
            throw newException("WVER0202E", new Object[] {} );

        int lastElementOffset = trainLength - 1;

        Object lastElement = elementTrain.get(lastElementOffset);
        String lastElementName = (String) elementNameTrain.get(lastElementOffset);

        if ( !lastElementName.equals(elementName) )
            throw newException("WVER0206E", new Object[] { elementName, lastElementName } );

        elementNameTrain.remove(lastElementOffset);
        elementTrain.remove(lastElementOffset);

        if ( lastElementOffset == 0 ) {
            currentElementName = null;
            currentElement = null;
            currentCaptureFlag = false;
            currentCaptureBuffer = null;

        } else {
            lastElementOffset--;

            currentElementName = (String) elementNameTrain.get(lastElementOffset);
            currentElement = elementTrain.get(lastElementOffset);
            currentCaptureFlag = ((Boolean) captureFlagTrain.get(lastElementOffset)).booleanValue();
            currentCaptureBuffer = (StringBuffer) captureTrain.get(lastElementOffset);
        }

        return lastElement;
    }

    // Exception helper ...
    //
    // SAXParseException newInvalidElementException(String, String);
    // SAXParseException newException(String, Object[]);

    protected SAXParseException newInvalidElementException(String parentElementName,
                                                           String childElementName)
    {
        if ( parentElementName == null ) {
            return newException("WVER0204E",
                                new Object[] { childElementName } );
        } else {
            return newException("WVER0205E",
                                new Object[] { childElementName, parentElementName } );
        }
    }

    protected SAXParseException newException(String exceptionId, Object[] args)
    {
        return new SAXParseException(getString(exceptionId, args),
                                     getDocumentLocator());
    }

    // Locator access ...
    //
    // public void setDocumentLocator(Locator);
    // public Locator getDocumentLocator();

    protected Locator locator;
        
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }

    public Locator getDocumentLocator()
    {
        return locator;
    }

    // Message access ...
    //
    // static String getString(String);
    // static String getString(String, Object[]);

    protected static String getString(String msgCode)
    {
        return BaseHandlerException.getString(msgCode);
    }

    protected static String getString(String msgCode, Object[] msgArgs)
    {
        return BaseHandlerException.getString(msgCode, msgArgs);
    }
}

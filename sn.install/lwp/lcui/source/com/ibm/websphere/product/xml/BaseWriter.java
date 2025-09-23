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
 * Simple DOM writer.
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 *
 */

import java.io.*;
import java.util.*;

/**
 *  
 */
public abstract class BaseWriter
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    public BaseWriter()
    {
    }

    // Exception handling ...

    protected Exception boundException;

    /**
	 * @return  the boundException
	 * @uml.property  name="boundException"
	 */
    public Exception getBoundException()
    {
        return boundException;
    }

    // Encoding details ...

    public static final String DEFAULT_ENCODING = "UTF8";

    protected String encoding;
    protected String encodingTag;
    protected String docTypeString;

    // Output Support ... 

    protected String outputFileName;
    protected FileOutputStream outputStream;
    protected PrintWriter outputWriter;

    /**
	 * @return  the outputFileName
	 * @uml.property  name="outputFileName"
	 */
    public String getOutputFileName()
    {
        return outputFileName;
    }

    public void print(String text)
    {
        outputWriter.print(text);
    }

    public void println()
    {
        outputWriter.println();
    }

    public void println(String text)
    {
        outputWriter.println(text);
    }

    public void flush()
    {
        outputWriter.flush();
    }

    

    public void openWriter(String encoding, String encodingTag, String docTypeString,
                           String outputFileName)
        throws FileNotFoundException, UnsupportedEncodingException
    {
        this.encoding = encoding;
        this.encodingTag = encodingTag;
        this.docTypeString = docTypeString;

        this.outputFileName = outputFileName;

        this.outputStream = new FileOutputStream(outputFileName); // throws FileNotFoundException

        String useEncoding = ( (encoding == null) ? DEFAULT_ENCODING : encoding );
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, useEncoding);
        // throws UnsupportedEncodingException
        this.outputWriter = new PrintWriter(new BufferedWriter(writer));

        this.indent = 0;
    }

    public void closeWriter()
        throws IOException
    {
        outputWriter.flush();
        outputWriter = null;

        OutputStream useOutputStream = outputStream;
        outputStream = null;

        useOutputStream.close(); // throws IOException
    }

    // Indented output ...

    protected static final int INDENT_WIDTH = 80;
    protected static final String indentLine = "                                                                                ";

    public static final int INDENT_GAP = 2;

    protected int indent;

    public void indentIn()
    {
        indent += INDENT_GAP;
    }

    public void indentOut()
    {
        indent -= INDENT_GAP;
    }

    public void printIndent()
    {
        int remainingIndent = indent;

        while ( remainingIndent >= INDENT_WIDTH ) {
            print(indentLine);
            remainingIndent -= INDENT_WIDTH;
        }

        if ( remainingIndent > 0 )
            print(indentLine.substring(0, remainingIndent));
    }

    // Usage:
    //
    //   Open the writer
    //   Begin a document
    //   Emit the tree
    //   Close the writer
    //
    //  The writer must be closed to ensure that the
    //  output is flushed.

    public void beginDocument()
    {
        if ( (encodingTag != null) && !encodingTag.equals("") ) 
            println("<?xml version=\"1.0\" encoding=\"" + encodingTag + "\"?>");

        if ( (docTypeString != null) && !docTypeString.equals("") )
            println(docTypeString);
    }

    // <elementName aName="aValue" bName="bValue"/>  (isComplete == true)
    // -- or --
    // <elementName aName="aValue" bName="bValue">   (isComplete == false)

    public static final boolean IS_COMPLETE = true ;
    // <elementName aName="aValue" bName="bValue"/>  (isComplete == true)
    // -- or --
    // <elementName aName="aValue" bName="bValue">   (isComplete == false)

    public static final boolean IS_INCOMPLETE = false ;

    public void emitElement(String elementName, Iterator attributes, boolean isComplete)
    {
        printIndent();
        beginElementOpening(elementName);

        while ( attributes.hasNext() ) {
            String[] attributeInfo = (String[]) attributes.next();

            String
                attributeName = attributeInfo[0],
                attributeValue = attributeInfo[1];

            if ( attributeValue != null ) {
                print(" ");
                emitAttribute(attributeName, attributeValue);
            }
        }

        endElementOpening(isComplete);
        println();
    }

    // <elementName aName="aValue" bName="bValue">
    //     elementText
    // </elementName>

    public void emitElement(String elementName, Iterator attributes, String elementText)
    {
        printIndent();
        beginElementOpening(elementName);

        while ( attributes.hasNext() ) {
            String[] attributeInfo = (String[]) attributes.next();

            String
                attributeName = attributeInfo[0],
                attributeValue = attributeInfo[1];

            if ( attributeValue != null ) {
                print(" ");
                emitAttribute(attributeName, attributeValue);
            }
        }

        endElementOpening(CLOSE_PARTIALLY);
        println();

        indentIn();

        println(elementText);

        indentOut();

        printIndent();
        emitElementClosure(elementText);
        println();
    }

    public void emitAttributeOnLine(String attributeName, String attributeValue)
    {
        if ( attributeValue != null ) {
            printIndent();
            emitAttribute(attributeName, attributeValue);
            println();
        }
    }

    // <elementName>
    //     elementText
    // </elementName>
    // - or -
    // <elementName>elementText</elementName>

    public static final int MAX_TEXT_WITH_ELEMENT = 40;

    public void emitElement(String elementName, String elementText)
    {
        printIndent();
        beginElementOpening(elementName);
        endElementOpening(CLOSE_PARTIALLY);

        elementText = normalize(elementText);

        if ( elementText.length() > MAX_TEXT_WITH_ELEMENT ) {
            println();

            indentIn();
            printIndent();
            println(elementText);

            indentOut();

            printIndent();

        } else {
            print(elementText);
        }

        emitElementClosure(elementName);
        println();
    }

    public void beginElementOpening(String elementName)
    {
        print("<" + elementName);
    }

    protected static final boolean CLOSE_WHOLLY = true ;
    protected static final boolean CLOSE_PARTIALLY = false ;

    public void endElementOpening(boolean completeClosure)
    {
        if ( completeClosure )
            print("/>");
        else
            print(">");
    }

    public void emitElementClosure(String elementName)
    {
        print("</" + elementName + ">");
    }

    public void emitAttribute(String attributeName, String attributeValue)
    {
        if ( attributeValue == null )
            attributeValue = "";

        print(attributeName + "=\"" + attributeValue + "\"");
    }

    /**
     * Normalize the argument string.
     */

    public String normalize(String text)
    {
        if ( text == null )
            return "";

        text = text.trim();

        int numChars = text.length();

        StringBuffer normalizedText = new StringBuffer();

        for ( int charNo = 0; charNo < numChars; charNo++ ) {
            char nextChar = text.charAt(charNo);
            
            if ( nextChar == '<' ) {
                normalizedText.append("&lt;");
            } else if ( nextChar == '>' ) {
                normalizedText.append("&gt;");
            } else if ( nextChar == '&' ) {
                normalizedText.append("&amp;");
            } else if ( nextChar == '"' ) {
                normalizedText.append("&quot;");
            } else if ( (nextChar == '\r') || (nextChar == '\n') ) {
                // Ignore!
            } else {
                normalizedText.append(nextChar);
            }
        }
        
        return normalizedText.toString();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        return null;
    }

    public void emit(List rootElements, String outputFileName)
    {
        emit(rootElements, null, null, null, outputFileName);
    }

    public void emit(List rootElements,
                     String encoding, String encodingTag, String docTypeString,
                     String outputFileName)
    {
        if ( docTypeString == null )
            docTypeString = getDefaultDocTypeString(rootElements);

        boundException = null;

        try {
            openWriter(encoding, encodingTag, docTypeString, outputFileName);
            // throws FileNotFoundException, UnsupportedEncodingException

        } catch (FileNotFoundException e) {
            boundException = e;
        } catch (UnsupportedEncodingException e) {
            boundException = e;
        }

        if ( boundException != null )
            return;

        try {
            baseEmit(rootElements);

        } finally {
            try {
                closeWriter(); // throws IOException
            } catch (IOException e) {
                boundException = e;
            }
        }
    }

    public abstract void baseEmit(List rootElements);
}

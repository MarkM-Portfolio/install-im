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

package com.ibm.websphere.product.formatters;

/*
 * HTML Reporter Implementation
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.io.PrintWriter;

import java.util.Vector;
import java.util.Iterator;

public class HTMLPrintFormatter extends TextPrintFormatter
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    public static final int BASE_TEXT_SIZE = 3 ;
    public static final int INDENT_PERCENTAGE = 10 ;

    protected String fontPrefix(int indent)
    {
        int size = BASE_TEXT_SIZE - indent;
        if ( size < 0 )
            size = 0;
    
        return "<FONT SIZE=\"+" + size + "\">";
    }

    protected String fontPostfix(int indent)
    {
        return "</FONT>";
    }

    protected void writeTablePrefix(int indent)
    {
        indent *= INDENT_PERCENTAGE;

        out.println("<TABLE WIDTH=\"100%\">");
        out.println("<TR><TD WIDTH=\"" + indent + "%\"></TD>");
        out.print  ("    <TD WIDTH=\"" + (100 - indent) + "%\">");
    }

    protected void writeTablePostfix(int indent)
    {
        out.println("</TD>");
        out.println("</TR>");
        out.println("</TABLE>");
    }

    public String embolden(String text)
    {
        return "<B>" + text + "</B>";
    }

    public void println(String text, int indent)
    {
        // text = fontPrefix(indent) + text + fontPostfix(indent);

        writeTablePrefix(indent);

        out.println(text);

        writeTablePostfix(indent);
    }

    public void blankLine()
    {
        out.println("<BR>");
    }

    // Reuse the text separator methods.

    public void printHeader(String text)
    {
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<TITLE>" + text + "</TITLE>");
        out.println("</HEAD>");
        out.println("");
        out.println("<BODY>");
        out.println("<H1>" + text + "</H1>");
        out.println("<HR>");
    }

    public void printFooter(String text)
    {
        out.println("");
        out.println("<HR>");
        out.println("<H1>" + text + "</H1>");
        out.println("</BODY>");
        out.println("</HTML>");
    }

    public void printList(Iterator iterator, int indent)
    {
        // out.println(fontPrefix(indent));

        writeTablePrefix(indent);
        out.println("");

        out.println    ("        <UL>");
        while ( iterator.hasNext() )
            out.println("            <LI>" + iterator.next() + "</LI>");
        out.println    ("        </UL>");

        out.print      ("    ");
        writeTablePostfix(indent);

        // out.println(fontPostfix(indent));
    }

    public void printTable(Vector allocations, Vector table, int indent)
    {
        // out.println(fontPrefix(indent));

        writeTablePrefix(indent);
        out.println(          "");

        out.println(          "        <TABLE WIDTH=\"100%\">");

        Iterator tableWeights = allocations.iterator();
        Iterator tableRows = table.iterator();

        int[] nextWeights = null;

        while ( tableRows.hasNext() ) {
            if ( tableWeights.hasNext() )
                nextWeights = (int[]) tableWeights.next();

            Object[] nextRow = (Object[]) tableRows.next();

            out.println(      "            <TR>");

            for ( int columnNo = 0; columnNo < nextRow.length; columnNo++ ) {
                Object nextColumnValue = nextRow[columnNo];
                int nextWeight = nextWeights[columnNo];

                out.println(  "                <TD WIDTH=\"" + nextWeight + "%\">" + nextColumnValue + "</TD>");
            }

            out.println(      "            </TR>");
        }

        out.println(          "        </TABLE>");
        
        out.print(            "    ");
        writeTablePostfix(indent);

        // out.println(fontPostfix(indent));
    }
}

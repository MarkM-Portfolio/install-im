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
 * Text Reporter Implementation
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.io.PrintWriter;

import java.util.Vector;
import java.util.Iterator;

public class TextPrintFormatter implements PrintFormatter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    protected PrintWriter out = new PrintWriter(System.out);

    public void setPrintWriter(PrintWriter out)
    {
        this.out = out;
    }

    // 'MAX_PAD_WIDTH' must be at lest as big as 'REPORT_WIDTH', below.

    public static final String
        PAD_TEXT       = "                                                                                ";
    public static final int
        MAX_PAD_WIDTH  = PAD_TEXT.length();

    public static final String
        SEPARATOR_LINE = "--------------------------------------------------------------------------------";
    public static final int
        REPORT_WIDTH   = SEPARATOR_LINE.length();

    public static final int
        INDENT_GAP     = 3;

    public String paddingFor(String partialText, int desiredWidth)
    {
        int partialWidth = partialText.length();

        int completingWidth;

        if ( partialWidth > desiredWidth )
            completingWidth = 0;
        else
            completingWidth = desiredWidth - partialWidth;

        if ( completingWidth > MAX_PAD_WIDTH )
            completingWidth = MAX_PAD_WIDTH;

        return PAD_TEXT.substring(0, completingWidth);
    }

    public String embolden(String text)
    {
        return text;
    }

    public void println(String text, boolean isBold)
    {
        println(text, 0, isBold);
    }

    public void println(String text, int indent, boolean isBold)
    {
        println((isBold ? embolden(text) : text), indent);
    }

    public void println(String text)
    {
        println(text, 0);
    }

    public void println(String text, int indent)
    {
        if ( indent < 0 ) {
            indent = 0;
        } else {
            indent *= INDENT_GAP;
            if ( indent > REPORT_WIDTH )
                indent = REPORT_WIDTH;
        }

        out.print(PAD_TEXT.substring(0, indent));
        out.println(text);
    }

    public void blankLine()
    {
        out.println("");
    }

    public void separator()
    {
        separator(0, false);
    }

    public void separator(boolean isBold)
    {
        separator(0, isBold);
    }

    public void separator(int indent)
    {
        separator(indent, false);
    }

    public void separator(int indent, boolean isBold)
    {
        if ( indent < 0 )
            indent = 0;

        int remaining = REPORT_WIDTH - (indent * INDENT_GAP);

        String separatorText;

        if ( remaining > 0 )
            separatorText = SEPARATOR_LINE.substring(0, remaining);
        else
            separatorText = "";

        println(separatorText, indent, isBold);
    }

    public void printHeader(String text)
    {
        separator(true);
        println(text, true);
        separator(true);
    }

    public void printFooter(String text)
    {
        separator(true);
        println(text, true);
        separator(true);
    }

    public void printList(Iterator iterator, int indent)
    {
        while ( iterator.hasNext() ) {
            Object nextItem = iterator.next();
            String nextText = (nextItem == null) ? "" : nextItem.toString();

            println(nextText, indent);
        }
    }

    public void printTable(Vector table)
    {
        printTable(table, 0);
    }

    public void printTable(Vector table, int indent)
    {
        printTable(STANDARD_WIDTHS, table, indent);
    }

    public void printTable(int[] allocation, Vector table, int indent)
    {
        Vector allocations = new Vector();
        allocations.add(allocation);

        printTable(allocations, table, indent);
    }

    public void printTable(Vector allocations, Vector table, int indent)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator tableWeights = allocations.iterator();
        Iterator tableRows = table.iterator();

        int[] nextWeights = null;

        while ( tableRows.hasNext() ) {
            if ( tableWeights.hasNext() )
                nextWeights = (int[]) tableWeights.next();
            Object[] nextRow = (Object[]) tableRows.next();

            int supColumnNo = nextRow.length,
                lastColumnNo = supColumnNo - 1;

            for ( int columnNo = 0; columnNo < supColumnNo; columnNo++ ) {
                int nextWeight = nextWeights[columnNo];
                Object nextColumnValue = nextRow[columnNo];

                String nextColumnText =
                    (nextColumnValue == null) ? "" : nextColumnValue.toString();

                buffer.append(nextColumnText);

                if ( columnNo != lastColumnNo ) {
                    String padText = paddingFor(nextColumnText, nextWeight);

                    if ( padText.length() == 0 )
                        padText = " ";

                    buffer.append(padText);
                }
            }

            println(buffer.toString(), indent);
            buffer.setLength(0);
        }
    }
}

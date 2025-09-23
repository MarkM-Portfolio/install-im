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
 * Reporting Interface for Product Information
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.io.PrintWriter;

import java.util.Vector;
import java.util.Iterator;

public interface PrintFormatter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public void setPrintWriter(PrintWriter out);

    public String paddingFor(String partialText, int desiredWidth);

    public void println(String text);
    public void println(String text, boolean isBold);
    public void println(String text, int indent);
    public void println(String text, int indent, boolean isBold);

    public void blankLine();

    public void separator();
    public void separator(boolean isBold);
    public void separator(int indent);
    public void separator(int indent, boolean isBold);

    public void printHeader(String text);
    public void printFooter(String text);

    public void printList(Iterator iterator, int indent);

    public static final int KEY_ALLOCATION = 15 ;
    public static final int VALUE_ALLOCATION = 85 ;

    public static final int[]
        STANDARD_WIDTHS = { KEY_ALLOCATION, VALUE_ALLOCATION };

    // Expected Vector type:
    //     [ int --> String[2] ]

    public void printTable(Vector table);
    public void printTable(Vector table, int indent);

    // Expected Vector type:
    //     [ int --> String[allocation.length] ]

    public void printTable(int[] allocation, Vector table, int indent);

    // Expected allocation Vector type:
    //    [ int --> int[] ]
    // Expected table Vector type:
    //    [ int --> Object[] ]
    // Where:
    //    table[offset].length == allocation[offset].length
    //
    // Note:
    //    allocation.length is not required to be the same as table.length.
    //    The last available allocation is extended to handle the suplus
    //    tail of the table.

    public void printTable(Vector allocation, Vector table, int indent);
}

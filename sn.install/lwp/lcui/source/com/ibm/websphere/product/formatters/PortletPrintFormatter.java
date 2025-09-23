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
 * Portlet HTML Reporter Implementation
 *
 * History 1.1, 10/2/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.io.PrintWriter;

import java.util.Vector;
import java.util.Iterator;

public class PortletPrintFormatter extends HTMLPrintFormatter
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "10/2/03" ;


    // Reuse the text separator methods.

    public void printHeader(String text)
    {
        //out.println("<HTML>");
        //out.println("<HEAD>");
        //out.println("<TITLE>" + text + "</TITLE>");
        //out.println("</HEAD>");
        //out.println("");
        //out.println("<BODY>");
        out.println("<H1>" + text + "</H1>");
        out.println("<HR>");
    }

    public void printFooter(String text)
    {
        out.println("");
        out.println("<HR>");
        out.println("<H1>" + text + "</H1>");
        //out.println("</BODY>");
        //out.println("</HTML>");
    }

}

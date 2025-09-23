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

package com.ibm.websphere.update.launch;

import java.io.*;

public class NlvPrintStream
    extends PrintStream
{
    private String encoding = null;

    public NlvPrintStream(OutputStream out, boolean autoFlush, String encoding)
        throws UnsupportedEncodingException
    {
        super(out, autoFlush);

        this.encoding = encoding;

        // test that the encoding is valid now.
        "1".getBytes(encoding);
    }

    public void write(byte[] b)
        throws IOException
    {
        if (out == null) // The receiver is closed.
            return;

        String s = new String(b);

        try {
            b = s.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
        }

        out.write(b);
    }

    public void write(byte[] b,int off, int len)
    {
        if (out == null) // The receiver is closed.
            return;

        String s = new String(b, off, len);

        try {
            try {
                b = s.getBytes(encoding);
                out.write(b);
            } catch (UnsupportedEncodingException ex) {
                out.write(b, off, len);
            }
        } catch (IOException ioe) {
        }
    }

    public void write(int b)
    {
        if (out == null) // The receiver is closed.
            return;

        byte ba[] = new byte[1];
        ba[0]=(byte) b;

        try {
            write(ba);
        } catch (IOException ex) {
        }
    }

    /*
      static public void main(String args[])
      {
          System.out.println("before codepage change u00e9=\u00e9");

          try {
              System.setOut(new NlvPrintStream(System.out,true, "Cp437"));
              System.setErr(new NlvPrintStream(System.err,true, "Cp437"));
          } catch (UnsupportedEncodingException e) {
              System.out.println("unable to set encoding, not supported");
          }
          System.out.println("after codepage change u00e9=\u00e9");
      }
    */
}

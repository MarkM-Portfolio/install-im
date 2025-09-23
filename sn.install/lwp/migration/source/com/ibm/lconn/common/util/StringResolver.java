/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.util;

import java.util.Properties;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public abstract class StringResolver {
	public static String resolveMacro(String line, Properties props, String prefix, String postfix){
		String newValue = null;

        int startPos = line.indexOf(prefix);

        while ( startPos > -1 ) {
            int endPos = line.indexOf(postfix, startPos );

            if ( endPos > -1 ) {
                String macroName = line.substring(startPos + 2, endPos);
                newValue = props.getProperty(macroName);
                // search for knownMacros first
                // Its not a knownMacro, see if its an environment variable.

                if ( newValue == null )
                    newValue = System.getProperty(macroName);

                // If we have a new value replace it with the macro value.

                if ( newValue != null ) {
                    line =  line.substring(0, startPos) + newValue + line.substring(endPos + 1);
                    newValue = null;
                }
            }

            if ( endPos > 0 )
                startPos = line.indexOf(prefix, startPos+1);
            else
                startPos = -2;  // to show we are done
        }
        return line;
	}
	public static String resolveMacro(String line, Properties props)
    {
        // the string $<> denotes a macro. where we will replace the string
        // with either an known string or from a real environment variable
        line = resolveMacro(line, props, "$<", ">");
        line = resolveMacro(line, props, "$(", ")");
        line = resolveMacro(line, props, "${", "}");
		return line;
    }
	
	public static void main(String[] args) {
		Properties p = new Properties();
		p.setProperty("a", "AAAA");
		p.setProperty("b", "BBBB");
		p.setProperty("c", "CCCC");
		p.setProperty("d", "DDDD");
		
		System.out.println(StringResolver.resolveMacro("$<a>,$<b>$<c>$<d>$<a",  p));
	}
}

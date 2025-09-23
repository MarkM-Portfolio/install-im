/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2007, 2015                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.update.delta;

public class PasswordRemover 
{

    public final static String PWD_REMOVED = "PASSWORD_REMOVED";
    
    // replace all password with "PASSWORD_REPLACED"
    /**
     * @param input         The input string the search will be preformed on
     * @param searchStr     The string to search for
     * @return              the original or modified string
     */
    public static String removePassword(String input, String searchStr) 
    {
    	
    	// add a space to the end of the input, this makes it easier
    	// to look for the password when it is at the end of the line
    	// then instead of lookin for System.getProperty("line.separator")
    	// we can just search for the SPACE.
    	String tmp = input + " ";
    	
		int pre_index = tmp.indexOf(searchStr);
		
		if(pre_index != -1)
		{
			String pre_sub = tmp.substring(0, pre_index+searchStr.length());
			
			// find the SPACE
			int post_index = tmp.indexOf(" ", pre_index+searchStr.length());
			String post_sub = tmp.substring(post_index, tmp.length());
			
			return pre_sub + PWD_REMOVED + post_sub;
		}
		return input;
	}

}

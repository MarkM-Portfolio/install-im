/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class XMLHelper {

	   public static Configuration loadConfigFile(File xml) throws IOException, ConfigurationException {
		    FileConfiguration _config = new XMLConfiguration();
	        InputStream is = new FileInputStream(xml);
	        PushbackInputStream pbis = new PushbackInputStream(is);
	        int b = pbis.read();
	        if (b == 0xEF) // x'fe'
	        {
	            // BOM is there, read the 2 remaining BOM bytes
	            pbis.skip(2);
	        } else {
	            // there is no BOM, reset to the beginning of the file
	            pbis.unread(b);
	        }
	       
	        _config.load(pbis, "UTF-8");
	        pbis.close();
	        is.close();

	        return _config;
	    }
	    public static void main(String[] args)throws Exception{   
	    	File variables = new File("variables.xml");
			Configuration config = XMLHelper.loadConfigFile(variables);
			if (config != null) {
				System.out.println("variables.xml file [" + variables.getAbsolutePath() + "] can be loaded.");
			} else {
				System.out.println("variables.xml file can not be loaded.");
				return;
			}
	        int i = 0;
	        while (config != null) {
	            String symbolicName = config.getString("entries(" + i + ")[@symbolicName]");

	            if (symbolicName == null)
	                break;       
               if("CONNECTIONS_PROVISION_PATH".equals(symbolicName))
               {
   	    		String value = config.getString("entries(" + i + ")[@value]");
		        System.out.println("symbolicName=" + symbolicName);
		        System.out.println("value=" + value);      
		        break;
               }
	            i++;
	        }			    
	        
	    }
}


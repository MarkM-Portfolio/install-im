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
import com.ibm.websphere.update.delta.Logger;

public abstract class BaseInstaller {

	public static final int INSTALL = 0;
	public static final int UNINSTALL = 1;
	

	protected static final String[] componentIds = { };
	
	protected boolean didInitialize = false;
	
	protected static boolean removeWorkFolder = true;

	
	protected static String user_install_root = null;
	protected static String was_home = null;
	protected static String lcc_home = null;
	protected static String was_cell = null;
	protected static String cell_Path = null;
	protected static String provision_path = null;
	protected static String webresource_path = null;
    protected ZipUtil zipUtil = new ZipUtil();

    public static boolean debugEnabled = false;
    public static final String debugPropertyName = "com.ibm.lconn.ifix.debug" ;
    
    protected Logger logStream;


    public Logger getLogStream() {
		return logStream;
	}


	public void setLogStream(Logger logStream) {
		this.logStream = logStream;
	}


	static {
    	user_install_root = System.getProperty(Contants.USER_INSTALL_ROOT);
    	was_home = System.getProperty(Contants.WAS_HOME);    	
    	was_cell = System.getProperty(Contants.WAS_CELL);
    	
		cell_Path = user_install_root + File.separator + "config" + File.separator + "cells" + File.separator +  was_cell;
		lcc_home = cell_Path + File.separator + Contants.CONNECTIONS_CONFIG_FOLDER;
		File variables = new File(cell_Path + File.separator + Contants.VARIABLES_FILE);
		provision_path = Contants.loadWASVariable("CONNECTIONS_PROVISION_PATH", variables);
		webresource_path = provision_path + File.separator + Contants.WEBRESOURCES;
		
		String clean = System.getProperty(Contants.ClEAN_TEMP);
		if(clean !=null)
			removeWorkFolder = Boolean.parseBoolean(clean);

	    String debugValue = System.getProperty(debugPropertyName);
	    debugEnabled = ( (debugValue != null) && debugValue.equals("true") );
	 
		
		System.out.println("\n");
		System.out.println(" user_install_root =" + user_install_root);
		System.out.println(" was_home =" + was_home);
		System.out.println(" was_cell=" + was_cell);
		System.out.println(" cell_Path=" + cell_Path);
		System.out.println(" lcc_home=" + lcc_home);
		System.out.println(" provision_path=" + provision_path);
		System.out.println(" removeWorkFolder=" + removeWorkFolder);	
		System.out.println("\n");
		
    }

    

 }

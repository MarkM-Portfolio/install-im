/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.io.PrintWriter;

public class CreateFile {
//	private static final ILogger log = IMLogger.getLogger(com.ibm.connections.install.CreateFile.class);
	
	public static void main(String[] args) {
		
	}
	
	public void run(String[] args, PrintWriter writer) {
		writer.write("create file jar initiated.\n");
		try{
			if(args.length >= 1){
				for(String arg : args){
					File file = new File(arg);
					boolean flag = false;
					if((!file.exists()) || (file.exists() && file.delete())){
						 flag = file.mkdirs();
					}
			    	if(flag == false){
	//		    		log.error("Error: Cannot create directory: "+arg);
			    	}
				}
			}
		}catch(Exception e){
			
		}
	}


}

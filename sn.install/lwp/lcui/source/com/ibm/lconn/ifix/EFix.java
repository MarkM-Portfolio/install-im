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
import java.util.Vector;

import com.ibm.websphere.update.delta.Logger;

public class EFix {
	
	String apar = null;

	String feature = null;
    protected Logger logStream;


    public Logger getLogStream() {
		return logStream;
	}


	public void setLogStream(Logger logStream) {
		this.logStream = logStream;
	}

	public String getFeature() {
		return feature;
	}
	File installedDir = null;

	String appName = null;

	Vector<Component> components = new Vector<Component>();

	File workDir  = null;

	File postUpdatePy = null;
	File updateResultPattern = null;

	public EFix(String feature, String apar, Logger logStream){
		this.feature = feature;
		this.apar = apar;
		this.logStream = logStream;
		
	}	

	public EFix(String feature, String apar, File workDir, Logger logStream){
		this.feature = feature;
		this.apar = apar;	
		this.workDir = workDir;
		this.logStream = logStream;
	}
	
	public String getAPAR(){
		return this.apar;
	}
	
	public Vector<Component> getComponents(){
		return this.components;
	}

	public void processFromPackage(File workDir){
		logStream.Both("EFix.processFromPackage workDir="+workDir);
		this.workDir = workDir;
		
        String srcFolderPath =  this.workDir.getAbsolutePath() + File.separator + "fixes" + File.separator + apar;
        
        this.postUpdatePy = new File (this.workDir.getAbsolutePath() + File.separator + ".." + File.separator+ "config" + File.separator + this.apar +"_" + this.feature +"_post_update.py");
        this.updateResultPattern = new File (this.workDir.getAbsolutePath() + File.separator + ".." + File.separator+ "config" + File.separator + this.apar +"_" + this.feature +"_updateResultPattern.txt"); 
        
        if(new File(srcFolderPath).exists()) {
	        Component component = new Component(feature, srcFolderPath , this.logStream);
	        component.processFromPackage();
	        //handle sub-component
	        Vector<Component> subComponents = component.getSubComponents();
	        if(subComponents!= null && subComponents.size() > 0){
	        	for(int j=0; j< subComponents.size(); j++){
	        	  components.add(subComponents.get(j));
	        	}
	        }
	        //add self as one component, it's used to handle web provision
	        components.add(component);
        }

	}
    public void processFromMetadata(File installedDir){
		logStream.Both("EFix.processFromMetadata installedDir="+installedDir);

    	this.installedDir = installedDir;
 
        this.postUpdatePy = new File (this.installedDir.getAbsolutePath() + File.separator + ".." + File.separator+ "config" + File.separator + this.apar +"_" + this.feature +"_post_update.py");
        this.updateResultPattern = new File (this.installedDir.getAbsolutePath() + File.separator + ".." + File.separator+ "config" + File.separator + this.apar +"_" + this.feature +"_updateResultPattern.txt"); 

    	String path = this.installedDir.getAbsolutePath() + File.separator + this.apar ;
    	File ifixDir = new File(path);
    	if(ifixDir.exists()){
    		File[] subfolders = ifixDir.listFiles();
    		if(subfolders !=null && subfolders.length > 0){
    			for ( int i=0; i< subfolders.length; i++){
    				File subfolder = subfolders[i];
    				if(subfolder.isDirectory()){
    		            Component component = new Component(subfolder.getName(), subfolder.getName(), path +File.separator+ subfolder.getName(), this.logStream);
    		            component.processFromMetadata();
    		            components.add(component);   					
    				}   				
    			}
    		}
    	}
    }
}

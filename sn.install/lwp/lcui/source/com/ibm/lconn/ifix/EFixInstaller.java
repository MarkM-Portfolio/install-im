/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2015, 2021                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.ibm.websphere.update.delta.Logger;


public class EFixInstaller extends BaseInstaller {

	private String ic_home = null;
	private String feature = null;
	private String apar    = null;
	private String wasUserId = null;
	private String wasUserPwd = null;
	
	private String efix_home = null;
	private String efixWorkFolder = null;
	private String efixBackupFolder = null;	
	private String efixConfigFolder = null;	

	private String earExpandFolder = null;
	private String earUpdateFolder = null;
	private EARCmd earCmd = null;

	public String errorArg = null;
	public String errorCode = null;
	public boolean isComplete = false;	
		
	public EFixInstaller(Logger logStream) {
		this.logStream = logStream;
	    this.earCmd = new EARCmd(user_install_root);
	    earCmd.setLogStream(logStream);
	    zipUtil.setLogStream(logStream);
	}
		
	public void setEFixMetaDat(String icHome, String feature, String apar, String wasUserId, String wasUserPwd){
		this.ic_home = icHome;
        this.feature = feature;
        this.apar = apar;
        this.wasUserId = wasUserId;
        this.wasUserPwd = wasUserPwd;
        
		this.efix_home = ic_home + File.separator + "efix";	
        
		this.efixBackupFolder = efix_home + File.separator + this.feature + File.separator + "backup" ;
		this.efixConfigFolder = efix_home + File.separator + this.feature + File.separator + "config";	
		this.efixWorkFolder = efix_home + File.separator + this.feature + File.separator + "work";
		
		this.earExpandFolder = efixWorkFolder + File.separator + "extract";
		this.earUpdateFolder = efixWorkFolder + File.separator + "update";
	}


	public boolean doInstall() {
		
		logStream.Both("EFixInstaller.doInstall feature="+feature+", apar="+apar);
		logStream.Both("efix_home = " + efix_home);
		logStream.Both("efixWorkFolder = " + efixWorkFolder);
		logStream.Both("efixBackupFolder = " + efixBackupFolder);

		 File workFolder = new File(efixWorkFolder);

		//prepare working env, clean temp folder
		File updateFolder = new File(workFolder,"update");
	    if(updateFolder.exists()){
	    	FileUtil.deleteDirectory(updateFolder);
	    }			
		File extractFolder = new File(workFolder,"extract");
		if(extractFolder.exists()){
		    FileUtil.deleteDirectory(extractFolder);
		}	

		 
		boolean executeResult = true;

		EFix eFix = new EFix(feature, apar, logStream);
        eFix.processFromPackage(workFolder);

		//generate backup zip package
		 executeResult = backupEFix(eFix);
		 if(!executeResult){
			 logStream.Both("fail to create backup file" );
			 return false;
		 }
		 executeResult = copyInstallablePackage(eFix);		
		//execute update package command
		 executeResult = executeUpdate(eFix);
		 if(!executeResult){
			 logStream.Both("fail to update applcation" );
			 return false;
		 }
		 if(executeResult)
		   removeWorkFolder(eFix);
		return true;
	}
	
	public boolean doUninstall() {
		logStream.Both("EFixInstaller.doUninstall feature="+feature+", apar="+apar);
		logStream.Both("efix_home = " + efix_home);
		logStream.Both("efixBackupFolder = " + efixBackupFolder);
		
		boolean executeResult = true;
		File installedDir = new File(efixBackupFolder);
		EFix eFix =  new EFix(feature, apar, this.logStream);
		eFix.processFromMetadata(installedDir);
		  //rollback
		executeResult = executeRollback(eFix);
		  if(!executeResult){
				 logStream.Both("fail to rollback iFix" );
				 return false;
		  }else{
			  cleanEFixFile(eFix);
		  }
		  //
		return true;
	}
    
	private void removeWorkFolder(EFix efix){
		if (removeWorkFolder){
	    	
	    	if(efix.workDir.exists()){
	    		FileUtil.deleteDirectory(efix.workDir);
	    	}			
		}
	}

    private boolean copyInstallablePackage(EFix eFix){
    	logStream.Both("start to copy installable package for iFix :: " + eFix.getAPAR());
    	String containerPath = this.efixBackupFolder + File.separator + eFix.getAPAR() ;

   	
        Vector<Component> components = eFix.getComponents();
        if(components != null){
        	Component component = null;
        	try {
        	for(int i=0; i<components.size(); i++){
        		component = components.get(i);
        		String path = containerPath;
        		path += File.separator + component.getAppName();
        		File container = new File(path);
        		container.mkdirs();
        		File updateZip = component.getUpdateZip();
        		if(updateZip!=null && updateZip.exists()){ 
        		  logStream.Both("Copy updateZip: " + updateZip.getAbsolutePath() + " To: " + path );
				  FileUtil.copyFile(updateZip, path);	
        		}
        		File deleteZip = component.getDeleteZip();
        		if(deleteZip!=null && deleteZip.exists()){  
        		  logStream.Both("Copy deleteZip: " + deleteZip.getAbsolutePath() + " To: " + path );
				  FileUtil.copyFile(deleteZip, path);	
        		}
        		File backupZip = component.getBackupZip();
        		if(backupZip!=null && backupZip.exists()){ 
        		  logStream.Both("Copy backupZip: " + backupZip.getAbsolutePath() + " To: " + path );
				  FileUtil.copyFile(backupZip, path);	
        		}       		
        		File backupEAR = component.getBackupEAR();
        		if(backupEAR!=null && backupEAR.exists()){ 
        		  logStream.Both("Copy backupEAR: " + backupEAR.getAbsolutePath() + " To: " + path );
				  FileUtil.copyFile(backupEAR, path);	
        		}       		
        	}
        	} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
        }
        logStream.Both("finish to copy installable package for iFix :: " + eFix.getAPAR());
        return true;
    }
    private boolean backupEFix(EFix eFix) {
    	logStream.Both("start to backup for iFix :: " + eFix.getAPAR());
    	boolean executeResult = true;
    	
        String compressedPath = cell_Path + File.separator  + "applications" + File.separator + "Activities.ear" + File.separator +"Activities.ear";
        String uncompressedPath = earExpandFolder + File.separator + eFix.getAPAR() + File.separator  + "Activities" ;
        
        Vector<Component> components = eFix.getComponents();
        if(components != null){
        	Component component = null;
        	String backupZipName = Contants.PARTIALAPP_BACKUP_FILENAME_SUFFIX;
        	for(int i=0; i<components.size(); i++){
        		component = components.get(i);
        		logStream.Both("component.getAppEARName()=" + component.getAppEARName());
        		logStream.Both("component.getAppName()=" + component.getAppName());

        		String updateEARFilesList = component.getUpdatedEARFileList();
        		String deleteEARFilesList = component.getDeleteEARFileList();
				boolean isFullEarUpdate = component.isFullEarUpdate();
        		
        		logStream.Both("updateEARFilesList=" + updateEARFilesList);
        		logStream.Both("deleteEARFilesList=" + deleteEARFilesList);
        		logStream.Both("isFullEarUpdate=" + isFullEarUpdate);
        		
        		String type = "war";
        		boolean osgiUpdate = false;
        		String regexp = ".*\\.jar/.*\\..*";
        		String regexp2 =".*/WEB-INF/eclipse/plugins/.*\\.jar/.*/.*\\..*";

        		if(isFullEarUpdate || (updateEARFilesList !=null && !updateEARFilesList.equals(""))){
                		
	        		compressedPath = cell_Path + File.separator  + "applications" + File.separator + component.getAppEARName() + File.separator +component.getAppEARName();
	        		uncompressedPath = earExpandFolder + File.separator + eFix.getAPAR() + File.separator  + component.getAppName() ;
	        		
	        		if(new File (compressedPath).exists()){
	        			
	        			component.isInstalled = true;

						if (isFullEarUpdate) {
							logStream.Both("backupEFix - processing full ear update ");
							String backupEarName = earExpandFolder + File.separator + eFix.getAPAR() + File.separator + component.getAppName().toLowerCase() + Contants.FULLAPP_BACKUP_FILENAME_SUFFIX;
							String backupAppName = component.getAppEARNameNoExtension();
							logStream.Both("backupEarName = " + backupEarName);
							logStream.Both("backupAppName = " + backupAppName);

							// call the EARCmd to backup the file
							executeResult = earCmd.exportApp(backupEarName, backupAppName, "./config/exportAppEAR.py", this.wasUserId, this.wasUserPwd)	;	
							if(!executeResult) {
								return executeResult;
							}
							logStream.Both(backupEarName + " file is created.");
							component.setBackupEAR(new File(backupEarName));  
						} else {
							if (updateEARFilesList != null && updateEARFilesList.length()>0){
								if (ZipUtil.match(regexp, updateEARFilesList.replace(File.separatorChar, '/'))){
									logStream.Both(" Partial update::  expand all" );
									type = "all";
								}else{
									logStream.Both(" Partial update::  expand war" );
								}
								if(type.equalsIgnoreCase("all") && 
									eFix.getFeature()!=null && 
									eFix.getFeature().equalsIgnoreCase(Contants.NEWS_NAME) && 
									ZipUtil.match(regexp2, updateEARFilesList.replace(File.separatorChar, '/'))){
									logStream.Both("update files contain OSGi bundles files ::" );
									osgiUpdate = true;
								}
							}
							if(osgiUpdate){
								executeResult = earCmd.uncompress(compressedPath, uncompressedPath,  "./config/earFullExpander.py", this.wasUserId, this.wasUserPwd); 
								if(!executeResult)
								return executeResult;
							}else{
								executeResult = earCmd.uncompress(compressedPath, uncompressedPath, type); 
								if(!executeResult)
									return executeResult;	        			
							}
						
							backupZipName = component.getAppName().toLowerCase() + Contants.PARTIALAPP_BACKUP_FILENAME_SUFFIX;
							//handle OSGi bundle files: change timestamp in getUpdatedEARFileList according to timestamp from customer env
							//also update -partialapp-update.zip if OSGi files was included.
							if(osgiUpdate){
								String newEARContainer = this.earUpdateFolder + File.separator + eFix.getAPAR() + File.separator + component.getAppName() + File.separator  + Contants.EAR_FOLDER;
								component.changeTimeStamp(uncompressedPath, newEARContainer);
							}
						
							boolean append = false;
							if (updateEARFilesList != null && updateEARFilesList.length()>0){
								logStream.Both("component.getUpdatedEARFileList() = " + component.getUpdatedEARFileList());
								String includeFileList = component.getUpdatedEARFileList();
						
								append = zipUtil.doZip(uncompressedPath, backupZipName, includeFileList, null, false, false);
							}
							if (deleteEARFilesList != null && deleteEARFilesList.length()>0){
								logStream.Both("deleteEARFilesList = " + deleteEARFilesList);
								append = zipUtil.doZip(uncompressedPath, backupZipName, deleteEARFilesList, null, append, true);
							}
							logStream.Both("return value for doZip()=" + append);
							if(!append){
							
								logStream.Both("Warning::NO file is need to backup, so " + backupZipName + " no created.");
								//return append;
							}else{
								logStream.Both(backupZipName + " file is created.");
								component.setBackupZip(new File(earExpandFolder + File.separator + eFix.getAPAR() + File.separator + backupZipName));  
							}	 
						}       			
	        		}else{
	        			logStream.Both("Customer env doesn't install this EAR application: " + compressedPath);
	        			//if EAR app was not installed, we need remove update-package and delete-package if exist.
                        File tmp = component.getUpdateZip();
                        if(tmp != null && tmp.exists()){
                        	logStream.Both("remove update package: " + tmp.getAbsolutePath());
                        	FileUtil.deleteFile(tmp.getAbsolutePath());
                        }
                        tmp = component.getDeleteZip();
                        if(tmp !=null && tmp.exists()){
                        	logStream.Both("remove delete package: " + tmp.getAbsolutePath());
                        	FileUtil.deleteFile(tmp.getAbsolutePath());
                        }
	        		}
                }else{
                	logStream.Both("No Need To BackUp EAR application");
                }
        		//start: web provision resource backup
        		String provisionFileList = component.getProvisionFileList();
        		if (provisionFileList !=null && !provisionFileList.equals("") ){
        			logStream.Both("provisionFileList= " + provisionFileList);
        			// need to get web provision path via WAS variable
            		File oldProvisionFolder = new File (webresource_path);
            		File tgtFolder = new File( this.efixBackupFolder + File.separator + eFix.getAPAR()+ File.separator + component.getAppName() + File.separator + Contants.PROVISION_FOLDER);
            		if(ZipUtil.match(regexp, provisionFileList.replace(File.separatorChar, '/'))){
            			type ="all";
            		}
            		logStream.Both(" update provision JAR type = " + type);
            		File newProvisionFolder = component.getProvisionFolder();
        			boolean result = backupProvisionByType(oldProvisionFolder, newProvisionFolder, tgtFolder, provisionFileList, type);
        			if(result){
        				component.setOldProvisionFolder(new File(tgtFolder, "old"));
        				component.setNewProvisionFolder(new File(tgtFolder, "new"));
        			}

        		}else {
        			logStream.Both("No Need To BackUp Provision JAR");
        		}
        		//finish: web provision resource backup
        	}
        }
        logStream.Both("finish to backup for iFix :: " + eFix.getAPAR());
        return true;
    }
    private boolean executeUpdate(EFix eFix){
    	logStream.Both("start to update for iFix :: " + eFix.getAPAR());
    	boolean executeResult = true;
        Vector<Component> components = eFix.getComponents();
        if(components != null){
        	Component component = null;
        	try {
        	String appEARName ="";
        	for(int i=0; i<components.size(); i++){
        		component = components.get(i);
        		 if (component.getAppEARName() != null){
        		    appEARName = component.getAppEARNameNoExtension();
        		 }
        		logStream.Both("component.isInstalled= " + component.isInstalled);
        		//if no backupZip file, it means the EAR application may not be installed on customer's env, it will lead install/uninstall fail 
        		File deleteZip = component.getDeleteZip();
        		if(deleteZip != null && deleteZip.exists() && component.isInstalled){  
        			executeResult = earCmd.updateApp(deleteZip, appEARName, "./config/updateApp.py", this.wasUserId, this.wasUserPwd)	;		
        		}else{
        			logStream.Both("No need to delete file from EAR " + appEARName);
        		}

        		File updateZip = component.getUpdateZip();
        		if(updateZip!=null && updateZip.exists() && component.isInstalled){        			
        			executeResult = earCmd.updateApp(updateZip, appEARName, "./config/updateApp.py", this.wasUserId, this.wasUserPwd)	;	
        		}else{
        			logStream.Both("No need to update file from EAR " + appEARName);
        		}

        		File earFile = component.getEarFile();
        		if(component.isFullEarUpdate() && earFile!=null && earFile.exists() && component.isInstalled){        			
        			executeResult = earCmd.updateApp(earFile, appEARName, "./config/updateAppEAR.py", this.wasUserId, this.wasUserPwd)	;	
        		}else{
        			logStream.Both("No need for a full EAR update for " + appEARName);
        		}
        		//String provisionPath = "C:\\Program Files\\IBM\\Connections\\data\\shared\\provision\\webresources";

        		File oldProvisionFolder = component.getOldProvisionFolder();
        		if(oldProvisionFolder!=null && oldProvisionFolder.exists()){
        			logStream.Both("remove old provision web resources :: " );
        			File[] list = oldProvisionFolder.listFiles();
        			for(int j=0; j< list.length ; j++){
        				File file = list[j];
        				logStream.Both("remove [" + j +"]= " + file.getName() + " from location: " +  webresource_path);
        				boolean b = FileUtil.deleteFile(webresource_path + File.separator + file.getName());
        				logStream.Both("remove action return = " + b );
        			}
        		}

        		File newProvisionFolder = component.getNewProvisionFolder();
        		if(newProvisionFolder!=null && newProvisionFolder.exists()){
        			logStream.Both("copy new provision web resources :: " );
        			File[] list = newProvisionFolder.listFiles();
        			for(int j=0; j< list.length ; j++){
        				File file = list[j];
        				logStream.Both("copy [" + j +"]= " + file.getAbsolutePath() + " To: " + webresource_path );
        				FileUtil.copyFile(file, new File(webresource_path + File.separator + file.getName()));
        			}
        		}      		
        	}
        	} catch (Exception e) {
				e.printStackTrace();
			}
        }
    	File postPy = eFix.postUpdatePy;
    	if(postPy != null && postPy.exists()){
    		logStream.Both("postUpdatePy= " + postPy.getAbsolutePath() );
			executeResult = earCmd.postUpdate("install", this.ic_home, postPy.getAbsolutePath(), this.wasUserId, this.wasUserPwd)	;		       		
    	}
    	File resultCheck = eFix.updateResultPattern;
    	if(resultCheck!=null && resultCheck.exists()){
    		logStream.Both("Copy file= " + resultCheck.getAbsolutePath() +" To: " + ic_home + "/version");
    		try{
    		 FileUtil.copyFile(resultCheck, new File(ic_home + File.separator + "version" + File.separator + "updateResultPattern.txt"));
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
        logStream.Both("finish to update for iFix :: " + eFix.getAPAR());
        return executeResult;
    }
	private void cleanEFixFile(EFix efix){
	    File iFixBackup = new File(this.efixBackupFolder, efix.getAPAR());	
	    if(iFixBackup.exists()){
	    		FileUtil.deleteDirectory(iFixBackup);
	    }
	    File postPy = efix.postUpdatePy;
	    if(postPy.exists()){
	    	FileUtil.deleteFile(postPy.getAbsolutePath());
	    }
	    File resultCheck = efix.updateResultPattern;
	    if(resultCheck.exists()){
	    	FileUtil.deleteFile(resultCheck.getAbsolutePath());
	    }
	}
    private boolean executeRollback(EFix eFix){
    	logStream.Both("start to rollback for iFix :: " + eFix.getAPAR());
    	boolean executeResult = true;
        Vector<Component> components = eFix.getComponents();
        if(components != null){
        	Component component = null;
        	try {
        	String appEARName ="";
        	for(int i=0; i<components.size(); i++){
        		component = components.get(i);
        		appEARName = component.getAppEARName().substring(0,component.getAppEARName().indexOf("."));
        		File deleteZip = component.getDeleteZip();
        		if(deleteZip != null && deleteZip.exists()){  
        			executeResult = earCmd.updateApp(deleteZip, appEARName, "./config/updateApp.py", this.wasUserId, this.wasUserPwd)	;		
        		}
        		File backupZip = component.getBackupZip();
        		if(backupZip!=null && backupZip.exists()){        			
        			executeResult = earCmd.updateApp(backupZip, appEARName, "./config/updateApp.py",  this.wasUserId, this.wasUserPwd)	;	
        		}
        		File backupEAR = component.getBackupEAR();
        		if(backupEAR!=null && backupEAR.exists()){        			
        			executeResult = earCmd.updateApp(backupEAR, appEARName, "./config/updateAppEAR.py",  this.wasUserId, this.wasUserPwd)	;	
        		}
        		
        		//String provisionPath = "C:\\Program Files\\IBM\\Connections\\data\\shared\\provision\\webresources";
        		File newProvisionFolder = component.getNewProvisionFolder();
        		if(newProvisionFolder!=null && newProvisionFolder.exists()){
        			logStream.Both("rollback new provision web resources :: " );
        			File[] list = newProvisionFolder.listFiles();
        			for(int j=0; j< list.length ; j++){
        				File file = list[j];
        				logStream.Both("remove [" + j +"]= " + file.getName() );
        				boolean b= FileUtil.deleteFile(webresource_path + File.separator + file.getName());
        				logStream.Both("remove action return = " + b );
        			}
        		}
        		File oldProvisionFolder = component.getOldProvisionFolder();
        		if(oldProvisionFolder!=null && oldProvisionFolder.exists()){
        			logStream.Both("copy original provision web resources :: " );
        			File[] list = oldProvisionFolder.listFiles();
        			for(int j=0; j< list.length ; j++){
        				File file = list[j];
        				logStream.Both("copy [" + j +"]= " + file.getName() );
        				FileUtil.copyFile(file, new File(webresource_path + File.separator + file.getName()));
        			}
       			
        		}
   		
        	}
        	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	File postPy = eFix.postUpdatePy;
    	if(postPy != null && postPy.exists()){
    		logStream.Both("postUpdatePy= " + postPy.getAbsolutePath() );
			executeResult = earCmd.postUpdate("uninstall", this.ic_home, postPy.getAbsolutePath(), this.wasUserId, this.wasUserPwd)	;		       		
    	}
    	File resultCheck = eFix.updateResultPattern;
    	if(resultCheck!=null && resultCheck.exists()){
    		logStream.Both("Copy file= " + resultCheck.getAbsolutePath() +" To: " + ic_home + "/version");
    		try{
    		 FileUtil.copyFile(resultCheck, new File(ic_home + File.separator + "version" + File.separator + "updateResultPattern.txt"));
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}

        logStream.Both("finish to rollback for iFix :: " + eFix.getAPAR());
        return executeResult;
    }

    private boolean backupProvisionByType(File srcFolder, File updateFolder, File targetFolder, String provisionFileList, String type){
       	logStream.Both("start backup provision web resource for iFix :: ");

    	File backOldJarFolder = new File(targetFolder, "old");
    	File newJarFolder = new File(targetFolder, "new");
    	
    	try {
    		//clean target folder first
        	if(targetFolder.exists()){
        		//FileUtil.deleteDirectory(targetFolder);
        	}   		
    		
    	   	backOldJarFolder.mkdirs();
        	newJarFolder.mkdirs();
     
    		
    		String[] jarnames = provisionFileList.split(",");
    		File[] files = srcFolder.listFiles();
    		ArrayList<String> bundleNames = new ArrayList<String>();
    		logStream.Both("jarnames length= " + jarnames.length);
    		if(type.equals("war")){//update entire JAR
    			for(int j=0; j< jarnames.length; j++){
    				String jarname = jarnames[j];
    				if(jarname != null && !jarname.equals("")){
    					if(jarname.indexOf("_") > 0){
    				  	 bundleNames.add(jarname.substring(0, jarname.indexOf("_")));
    					}else if(jarname.indexOf("-") > 0){
    						 bundleNames.add(jarname.substring(0, jarname.indexOf("-")));
    					}else{
    						bundleNames.add(jarname.substring(0, jarname.lastIndexOf(".")));
    					}
    				}
    			}
				FileUtil.copyDirectory(updateFolder, newJarFolder );			
				
				for (int i=0; i< files.length; i++){
					File file = files[i];
					if(file !=null && file.exists() && file.isFile()){
						String name =file.getName();
						if(file.getName().indexOf("_")>0){
						  name = file.getName().substring(0, file.getName().indexOf("_"));
						}else if(file.getName().indexOf("-")>0){
							name = file.getName().substring(0, file.getName().indexOf("-"));
						}else{
							name = file.getName().substring(0, file.getName().lastIndexOf("."));
						}
						 
						if(bundleNames.contains(name)){
							FileUtil.copyFile(file, backOldJarFolder.getAbsolutePath());
						}
						
					}
				}   			
            }else{// update partial files in JAR.
            	File extractJarFolder = new File(targetFolder, "extract");
            	extractJarFolder.mkdirs();
        	   HashMap<String, String> bundlesMap = new HashMap<String, String>();
              //get bundles name list
    		   for(int j=0; j< jarnames.length; j++){
    				String jarname = jarnames[j];
    				logStream.Both("jarname= " + jarname);
    				if(jarname != null && !jarname.equals("") ){

    					String bundleName = "";
    					if(jarname.indexOf("_")>0){
    						bundleName = jarname.substring(0, jarname.indexOf("_"));
    					}else if(jarname.indexOf("-")>0){
    						bundleName = jarname.substring(0, jarname.indexOf("-"));
    					}else{
    						bundleName = jarname.substring(0, jarname.lastIndexOf("."));
    					}
    					
    					logStream.Both("bundleName = " + bundleName);
    					if(!bundleNames.contains(bundleName)){
    			    		logStream.Both("add bundles name= " + bundleName);
    						bundleNames.add(bundleName);                                						
    					}    				  	
    				}   				
    		  } 
    		  // unzip bundles file to bak folder
     		  for(int j=0; j< files.length; j++ ){
    			  File file = files[j];
					if(file !=null && file.exists() && file.isFile() ){
						String name = "";
						if(file.getName().contains("_")){
							name = file.getName().substring(0, file.getName().indexOf("_"));
						}else if(file.getName().contains("-")){
							name = file.getName().substring(0, file.getName().indexOf("-"));
						}else{
							name = file.getName().substring(0, file.getName().lastIndexOf("."));
						}
						
						
						String name2 = file.getName().substring(0, file.getName().lastIndexOf("."));
						String path = extractJarFolder.getAbsolutePath() + File.separator + name2;
						if(bundleNames.contains(name)){
							logStream.Both("provision file name= " + name);
							logStream.Both("add provision file path into map= " + path);
							bundlesMap.put(name, path);
							FileUtil.copyFile(file, backOldJarFolder.getAbsolutePath());
							boolean b = zipUtil.unZip(file, path);
							if(b){
								logStream.Both(" unZip success " );
							}else{
								logStream.Both(" unZip fail " );
							}
						}
					}    			  
    		  }
    		  // copy update files into bak folder
    		  File[] updateFiles = updateFolder.listFiles();
    		  if(updateFiles !=null){
    			  for (int j=0; j<updateFiles.length; j++){
    				  File file = updateFiles[j];
    				  if(file !=null && file.exists() && file.isDirectory()){
    					 
  						String name = "";
  						if(file.getName().contains("_")){
  							name = file.getName().substring(0, file.getName().indexOf("_"));
  						}else if(file.getName().contains("-")){
  							name = file.getName().substring(0, file.getName().indexOf("-"));
  						}else{
  							name = file.getName().substring(0, file.getName().lastIndexOf("."));
  						}
   					  
    					  logStream.Both("update provision file name= " + name);
    					  if (bundlesMap.containsKey(name)){
    						  logStream.Both("copy provision folder to= " + bundlesMap.get(name));
    						  FileUtil.copyDirectory(file, new File(bundlesMap.get(name)));
    					  }    					  
    				  }
    			  }
    		  }
    		  File[] extractJars = extractJarFolder.listFiles();
    		  if(extractJars !=null){
    			  for (int j=0; j< extractJars.length; j++){
    				  File file = extractJars[j];
    				  if(file !=null && file.exists() && file.isDirectory()){
    					  logStream.Both("zip new provision folder= " + file.getName() + ".jar");
                          boolean b = zipUtil.doZip(file.getAbsolutePath(), file.getName() + ".jar", null, null, false, false)  ;
                          if(b){
                            FileUtil.copyFile(new File(extractJarFolder, file.getName() + ".jar"), newJarFolder.getAbsolutePath());
                          }else{
                        	  logStream.Both(" doZip fail !");  
                          }
    				  }
    			  }    			  
    		  }
    		  
           }

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	   	logStream.Both("finish backup provision web resource for iFix :: " );

    	return true;
    }

}
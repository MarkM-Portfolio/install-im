/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */

package com.ibm.lconn.ant.task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
/**
 * @author Zheng Jun Song (songzj@cn.ibm.com)
 * 
 */
public class TimeStampConvert extends BaseTask {
	private String updateFilesPath, baseFilesPath, backupFilesPath, osgiBundlesList, updateFilesList;

	public String getUpdateFilesList() {
		return updateFilesList;
	}

	public void setUpdateFilesList(String updateFilesList) {
		this.updateFilesList = updateFilesList;
	}

	public String getUpdateFilesPath() {
		return updateFilesPath;
	}

	public void setUpdateFilesPath(String updateFilesPath) {
		this.updateFilesPath = updateFilesPath;
	}

	public String getBackupFilesPath() {
		return backupFilesPath;
	}

	public void setBackupFilesPath(String backupFilesPath) {
		this.backupFilesPath = backupFilesPath;
	}

	public String getBaseFilesPath() {
		return baseFilesPath;
	}

	public void setBaseFilesPath(String baseFilesPath) {
		this.baseFilesPath = baseFilesPath;
	}

	public String getOsgiBundlesList() {
		return osgiBundlesList;
	}

	public void setOsgiBundlesList(String osgiBundles) {
		this.osgiBundlesList = osgiBundles;
	}

	 public static void copyFile(File sourceFile, File targetFile) throws IOException {
	        BufferedInputStream inBuff = null;
	        BufferedOutputStream outBuff = null;
	        try {

	            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
	            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
	            byte[] b = new byte[1024 * 5];
	            int len;
	            while ((len = inBuff.read(b)) != -1) {
	                outBuff.write(b, 0, len);
	            }
	            outBuff.flush();
	        } finally {
	            if (inBuff != null)
	                inBuff.close();
	            if (outBuff != null)
	                outBuff.close();
	        }
	  }
	 public static ArrayList<String> copyDirectiory(File srcDir, File tgtDir) throws IOException {
		    ArrayList<String> relPathList = new ArrayList<String>();
         if(srcDir == null || tgtDir == null){
         	return relPathList;
         }
	        tgtDir.mkdirs();
	        File[] file = srcDir.listFiles();
	        for (int i = 0; i < file.length; i++) {
	            if (file[i].isFile()) {
	                File sourceFile = file[i];
	                File targetFile = new File(tgtDir.getAbsolutePath() + File.separator  + file[i].getName());
	                copyFile(sourceFile, targetFile);
	                //System.out.println("Copy File: " + sourceFile.getAbsolutePath());
	                //System.out.println("To File: " + sourceFile.getAbsolutePath());
	                relPathList.add(targetFile.getAbsolutePath());
	            }
	            if (file[i].isDirectory()) {
	                String dir1 = srcDir.getAbsolutePath() + File.separator + file[i].getName();
	                String dir2 = tgtDir.getAbsolutePath() + File.separator + file[i].getName();
	                relPathList.addAll(copyDirectiory(new File(dir1), new File(dir2)));
	            }
	        }
	        return relPathList;
	    }
	  
	 public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	 public static boolean deleteDirectory(File dirFile) {
		
		if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i]);
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	 public static boolean moveDirectory(File srcDir, File tgtDir) throws IOException {
		
		if (!srcDir.exists() || !srcDir.isDirectory()) {
			return false;
		}
		tgtDir.mkdirs();
		boolean flag = true;
     String sPath = srcDir.getAbsolutePath();
     String tPath = tgtDir.getAbsolutePath();
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String aPath = files[i].getAbsolutePath();
			String rPath = aPath.substring(sPath.length());
			String updatePath = tPath + rPath;
			if (files[i].isFile()) {
				flag = moveFile(files[i], new File(updatePath));
				if (!flag)
					break;
			} else {
				flag = moveDirectory(files[i], new File(updatePath));
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (srcDir.delete()) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean moveFile(File srcFile, File tgtFile) throws IOException {

			boolean flag = false;
			if(srcFile == null){
				return flag;
			}

			if (srcFile.isFile() && srcFile.exists()) {
				copyFile(srcFile, tgtFile);
				srcFile.delete();
				flag = true;
			}
			return flag;
		}
     public static ArrayList<String> getOSGiBundles(String osgiBundles){
    	 ArrayList<String> namesList = new ArrayList<String>();
    	 
    	 if(osgiBundles ==null || osgiBundles.equals(""))
    		 return namesList;
   	     osgiBundles = osgiBundles.replace('\\', '/');
		 String[] names = osgiBundles.split(",");
		 
		  
		 for(int i=0; i<names.length; i++){
			  String s1 = names[i].trim();
			  String s2 = s1.substring(0, s1.lastIndexOf("*"));
			  String name =s2 ;
			  if (s2.contains("/")){
				  name = s2.substring(s2.lastIndexOf("/")+1);
			  }
			  System.out.println("OSGi bundles prefix name=" + name);
			  namesList.add(name);
		  }
		  		  
		  return namesList;
	  }
	  
	  
	 public static ArrayList<String> refreshFileList(String strRoot, String strPath, String baseRoot, ArrayList<String> osgiBundlesNames, String tgtRoot) throws IOException 
	   { 
		   ArrayList<String> relPathList = new ArrayList<String>();
		   strRoot = strRoot.replace('\\', File.separatorChar);
		   strPath = strPath.replace('\\', File.separatorChar);
		   baseRoot = baseRoot.replace('\\', File.separatorChar);
		   tgtRoot = tgtRoot.replace('\\', File.separatorChar);
		   
			if (!strRoot.endsWith(File.separator)) {
				strRoot = strRoot + File.separator;
			}
			if (!baseRoot.endsWith(File.separator)) {
				baseRoot = baseRoot + File.separator;
			}
			if (!tgtRoot.endsWith(File.separator)) {
				tgtRoot = tgtRoot + File.separator;
			}
		   
		   File dir = new File(strPath); 
	       File[] files = dir.listFiles(); 
	        
	        if (files == null) 
	            return relPathList; 
	        
	        for (int i = 0; i < files.length; i++) {
	        	
           	String fileName = files[i].getName();
           	String aPath = files[i].getAbsolutePath();
           	String rPath = aPath.substring(strRoot.length()-1);
           	String rPath2= rPath;
           	String tgtFilePath = tgtRoot + rPath;
           	
	            if (files[i].isDirectory()) { 


	            	String name = fileName;
	            	String versionTimeStamp ="";
	            	
	            	if(fileName.contains("_")){
	            		name = fileName.substring(0,fileName.indexOf("_"));
	            		versionTimeStamp = fileName.substring(fileName.indexOf("_")+1);
	            	}
	            		            	
	            	
	            	
	            	if (osgiBundlesNames.contains(name)){
	            		
	            		System.out.println("aPath=" + aPath);
	            		System.out.println("rPath=" + rPath);
	            		System.out.println("fullName=" + fileName);
	            		System.out.println("name=" + name);
	            		System.out.println("versionTimeStamp=" + versionTimeStamp);
	            		
	            		
	            		File baseFile = new File(baseRoot + rPath );
	            		if (baseFile.exists()){
	            			System.out.println("File version is same in Update folder and Customer env");
	            		}else {
	            			File baseParent = baseFile.getParentFile();
	            			File[] tmpList = baseParent.listFiles();
	            			File tmpFile;
	            			String tmpFileName;
	            			String baseVersionTimeStamp;
	            			for ( int j=0; j<tmpList.length; j++){
	            				tmpFile = tmpList[j];
	            				tmpFileName = tmpFile.getName();
	            				if (tmpFileName.contains(name)){
	            					System.out.println("baseFileName="+tmpFileName);
	        	            		baseVersionTimeStamp = tmpFileName.substring(tmpFileName.indexOf("_")+1);
	        	            		System.out.println("baseVersionTimeStamp="+baseVersionTimeStamp);
	            					String updateFileName = name + "_" + baseVersionTimeStamp;
	            					
	            					rPath2 = rPath.substring(0, rPath.lastIndexOf(fileName));
	            					System.out.println("relPath="+rPath2);
	            					
	            					tgtFilePath = strRoot + rPath2 + updateFileName;	            					
	            					System.out.println("tgtFilePath1="+tgtFilePath);
	            					relPathList.addAll(copyDirectiory(files[i], new File(tgtFilePath)));
									
									tgtFilePath = tgtRoot + rPath2 + fileName;
									System.out.println("tgtFilePath2="+tgtFilePath);
									moveDirectory(files[i], new File(tgtFilePath));            				
	            					break;
	            				}
	            			}
	            		}
	            		
	            	}else{
	            		//relPathList.add(files[i].getAbsolutePath());  
	            	}
	            	
	            	relPathList.addAll(refreshFileList(strRoot, files[i].getAbsolutePath(), baseRoot, osgiBundlesNames,tgtRoot)); 
	            } else {
	            	relPathList.add(files[i].getAbsolutePath());                    
	            } 
	        } 
	        return relPathList;
	    }

	@Override
	public void execute() throws BuildException {
        String updateFilesPath = this.getUpdateFilesPath();
        String baseFilesPath   = this.getBaseFilesPath();
        String backupFilesPath  = this.getBackupFilesPath();
        String osgiBundlesList = this.getOsgiBundlesList();
        String updateFilesList = this.getUpdateFilesList();
        
		Project p = getProject();
		String val_updateFilesPath = p.getProperty(updateFilesPath);
		String val_baseFilesPath   = p.getProperty(baseFilesPath);
		String val_backupFilesPath = p.getProperty(backupFilesPath);
		String val_osgiBundlesList     = p.getProperty(osgiBundlesList);
		

		
		System.out.println("val_updateFilesPath=" + val_updateFilesPath);
		System.out.println("val_backupFilesPath=" + val_backupFilesPath);
		System.out.println("val_baseFilesPath=" + val_baseFilesPath);
		System.out.println("val_osgiBundlesList=" + val_osgiBundlesList);
		
		try {
			ArrayList<String> relPathList = refreshFileList(val_updateFilesPath,val_updateFilesPath, val_baseFilesPath, getOSGiBundles(val_osgiBundlesList), val_backupFilesPath);
			StringBuffer sb = new StringBuffer();
			for(int i=0; i< relPathList.size(); i++){
				String s = relPathList.get(i);
				if(i!=0){
				 sb.append(",");
				}
				sb.append(s.substring(val_updateFilesPath.length()));
			}
			setProperty(updateFilesList,sb.toString());
			System.out.println("update files list=" + sb);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}

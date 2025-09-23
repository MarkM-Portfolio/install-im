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
import java.io.BufferedOutputStream;  
import java.io.BufferedInputStream;  
import java.io.FileOutputStream;  
import java.io.FileInputStream;  
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;  
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;  
import java.util.zip.ZipOutputStream;  
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ibm.websphere.update.delta.Logger;
/** 
 * @author songzj@cn.ibm.com 
 * 
 */  
public class ZipUtil{   
    private ZipInputStream  zipIn;       
    private ZipOutputStream zipOut;     
    private ZipEntry        zipEntry;   
    private int      bufSize;    //size of bytes   
    private byte[]          buf;   
    private int             readedBytes;   

    public static boolean debugEnabled = false;
    public static final String debugPropertyName = "com.ibm.lconn.ifix.ziputil.debug" ;
	static {

	    String debugValue = System.getProperty(debugPropertyName);
	    debugEnabled = ( (debugValue != null) && debugValue.equals("true") );	 
	}  
	
    private Logger logStream;


    public Logger getLogStream() {
		return logStream;
	}


	public void setLogStream(Logger logStream) {
		this.logStream = logStream;
	}
	
    public ZipUtil(){   
        this(1024);   
    }   
  
    public ZipUtil(int bufSize){   
        this.bufSize = bufSize;   
        this.buf = new byte[this.bufSize * 5];   
    }   
     
    public boolean doZip(String zipDirectory, String zipName, String includesList, String excludesList, boolean append, boolean isRegexp){  
 
    	File zipDir = null;  
    	File zipFile = null;
    	File newZip = null ;
    	String zipFilePath ="";
        boolean b = true;
        try{   
            zipDir = new File(zipDirectory);   
            zipFilePath = zipDir.getParent() + File.separator + zipName;  
            zipFile = new File(zipFilePath);
            newZip = new File(zipFilePath + ".new");
            logStream.Both("Begin to zip file: " + zipFilePath);
            this.zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(newZip)));
        	if(append && zipFile.exists()){
        		// append new content into current zip package.
        		ZipFile currentZip = new ZipFile(zipFilePath);      		
        		Enumeration<? extends ZipEntry> entries = currentZip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    //logStream.Both("copy: " + e.getName());
                    this.zipOut.putNextEntry(e);
                    if (!e.isDirectory()) {
                        copy(currentZip.getInputStream(e), this.zipOut);                     
                    }
                    this.zipOut.flush();
                    this.zipOut.closeEntry();
                }
                currentZip.close();
        		
        	}else{
        		//create new zip  
        	}
           
            b = handleDir(zipDir , this.zipOut, zipDirectory, includesList, excludesList, isRegexp); 
            this.zipOut.flush();
            this.zipOut.close();   
            if (zipFile.exists()){
            	zipFile.delete();
            }
            newZip.renameTo(zipFile);
        }catch(Exception ioe){  
        	if(debugEnabled){
        	  ioe.printStackTrace();
        	}
        	logStream.Both("ERROR1 in doZip(): " + ioe.getMessage());
        } finally{
        	try {
				//this.zipOut.close();
				if(!b && !append ){
		            if (zipFile != null && zipFile.exists()){
		            	zipFile.delete();
		            }				
				}
				File temp = new File(zipFilePath + ".new");
				temp.deleteOnExit();
				b = new File(zipFilePath).exists();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logStream.Both("ERROR2 in doZip(): " + e.getMessage());
				if(debugEnabled)
				  e.printStackTrace();
			}   
        }
        return b;
    }   
    private void copy(InputStream input, OutputStream output) throws IOException {

        while((this.readedBytes=input.read(this.buf))>0){  
        	output.write(this.buf , 0 , this.readedBytes); 
        }
    }
 
    private boolean handleDir(File dir , ZipOutputStream zipOut, String rootPath, String includesList, String excludesList, boolean isRegexp)throws Exception{   
        FileInputStream fileIn;   
        File[] files;   
        boolean b = true;
        if (!rootPath.endsWith(File.separator)){
        	rootPath += File.separator;
        }
        String patternList = includesList;
        //logStream.Both("dir=" + dir.getAbsolutePath());
        files = dir.listFiles();   
        if(files.length == 0){ 
        	//logStream.Both("rootPath=" + rootPath);
        	//logStream.Both("dir.getAbsolutePath()=" + dir.getAbsolutePath());
        	
        	if (rootPath.startsWith(dir.getAbsolutePath())){
        		logStream.Both("The directory does Not contain any files");
	            this.zipOut.putNextEntry(new ZipEntry(dir.getName()+ "/"));   
	            this.zipOut.closeEntry();  
	            return false;
        	}else{
               //continus, don't put blank folder into zip
        	}
        }   
        else{ 
            for(File file : files){     
                if(file.isDirectory()){   
                	//add directory path
                	/*
                    String path= file.getAbsolutePath(); 
                    path = path.substring(rootPath.length()) ;
                    path = path.replace('\\', File.separatorChar);
                    path = path.replace('/', File.separatorChar);
                    path = path.replace(File.separatorChar, '/');
                    path = path + "/";
                    //logStream.Both("path=" + path );
                    this.zipOut.putNextEntry(new ZipEntry(path));     
                    */
                    handleDir(file , this.zipOut, rootPath, patternList, excludesList , isRegexp);   
                }   
                else{                      
                    String path= file.getAbsolutePath(); 
                    path = path.substring(rootPath.length());
                    if (includesList != null){
	                    includesList = includesList.replace('\\', File.separatorChar);
	                    includesList = includesList.replace('/', File.separatorChar);
	                    //logStream.Both("includesList=" + includesList);
                    }
                    if (excludesList != null){
	                    excludesList = excludesList.replace('\\', File.separatorChar);
	                    excludesList = excludesList.replace('/', File.separatorChar);
	                    //logStream.Both("excludesList=" + excludesList);
                    }
                    path = path.replace('\\', File.separatorChar);
                    path = path.replace('/', File.separatorChar);
                    
                    //logStream.Both("path=" + path);
                     boolean add = false;
                    if (includesList == null && excludesList == null){
                    	add = true;                       
                    }else if (includesList != null && excludesList == null){
                        
                    	if (!isRegexp && includesList.contains(path)) {
                    		logStream.Both("path=" + path);
                    		add = true;
                    	}else{
                    		if(isRegexp && ZipUtil.matchAll(patternList, path)){
                    			logStream.Both("patternList=" + patternList);
                    			logStream.Both("path=" + path);
                    			add = true;
                    		}
                    	}
                    }else if (includesList != null && excludesList != null){
                    	if (((!isRegexp && includesList.contains(path)) || (isRegexp && ZipUtil.matchAll(patternList, path))) && !excludesList.contains(file.getName()))
                    		add = true;
                    }else if (includesList == null && excludesList != null){
                    	if (!excludesList.contains(file.getName()))
                    			add = true;
                    }
                    //logStream.Both("canAdd=" + add);
                    if(add)	{
                        path = path.replace(File.separatorChar, '/');
                    	//logStream.Both("path=" + path);
                    	try{
		                    this.zipOut.putNextEntry(new ZipEntry(path));  
		                    fileIn = new FileInputStream(file);  
		                    copy(fileIn, this.zipOut);
		                    this.zipOut.flush();
		                    this.zipOut.closeEntry();
		                    b=true;
                    	}catch(Exception e){
                    		if(debugEnabled)
                    		  e.printStackTrace();
                    		logStream.Both("ERROR in handleDir() : " + e.getMessage());                   		
                    	}
                    }
                }   
            }   
        } 
        return b;
    }   
    public boolean unZip(File srcFile, File targetFolder){
    	if (srcFile != null && targetFolder != null)
    		 return unZip(srcFile.getAbsolutePath(), targetFolder.getAbsolutePath() );
    	
        return false;
    }
    public boolean unZip(File srcFile, String targetFolderPath){
    	if (srcFile != null && targetFolderPath != null)
    		 return unZip(srcFile.getAbsolutePath(), targetFolderPath );
    	
        return false;
    }
    
    public boolean unZip(String unZipfileName, String targetFolderPath){
        FileOutputStream fileOut;   
        File file;   
        logStream.Both("unZipfileName=" + unZipfileName);
        logStream.Both("targetFolderPath=" + targetFolderPath);
        
        File tgtFolder=new File(targetFolderPath);  
        try{   
            if(!tgtFolder.exists()){  
            	tgtFolder.mkdir();  
            }
            
            this.zipIn = new ZipInputStream (new   
                    BufferedInputStream(new FileInputStream(unZipfileName)));  
            
            while((this.zipEntry = this.zipIn.getNextEntry()) != null){ 
            	//logStream.Both("this.zipEntry.getName()=" + this.zipEntry.getName());
                file = new File(targetFolderPath + File.separator + this.zipEntry.getName());  
                //logStream.Both("file.path=" + file.getAbsolutePath());
                if(this.zipEntry.isDirectory()){             
                    file.mkdirs();   
                }
                else{   
                    File parent = file.getParentFile();   
                    if(!parent.exists()){   
                        parent.mkdirs();   
                    }   
 
                    if(!file.exists())
                    	file.createNewFile();
                    
                    fileOut = new FileOutputStream(file);   
                      
                    while(( this.readedBytes = this.zipIn.read(this.buf) ) > 0){   
                        fileOut.write(this.buf , 0 , this.readedBytes );   
                    }  
                    fileOut.flush();
                    fileOut.close();  
                }
                  
                this.zipIn.closeEntry(); 
            }  
        }catch(Exception ioe){  
            ioe.printStackTrace();   
        }
        return true;
    }   
  
    public void setBufSize(int bufSize){   
        this.bufSize = bufSize;   
    }   
  
    public String getSubFolderName(File parent){ 
    	String name ="";
    	File[] files = null;
    	File   file = null;
    	if (parent !=null && parent.isDirectory()){
    		files = parent.listFiles();
    		if (files !=null){
    			file = files[0];
    			name = file.getName();
    		}
    	}
        return name ; 
    }
    
    public Vector<String> getSubFolderNameList(File parent){ 
    	Vector<String> list = new Vector<String>();
    	File[] files = null;
    	File   file = null;
    	if (parent !=null && parent.isDirectory()){
    		files = parent.listFiles();
    		if (files !=null){
    			for( int i=0;i<files.length; i++){
    			  file = files[i];
    			  list.add(file.getName());
    			}
    		}
    	}
        return list ; 
    }
    
    public static boolean matchAll(String patternList, String input){
    	boolean b =false;
    	//patternList = patternList.replace(File.separatorChar, '/');
    	input = input.replace(File.separatorChar, '/');
        //System.out.println("patternList= "+patternList);	
    	//System.out.println("input=" + input);
    	String[] patterns = patternList.split(",");
    	if(patterns == null)
    		return b;
    	for(int i=0; i<patterns.length; i++){
    		
    		b = match(patterns[i], input);
    		if(b)
    		  break;
    	}
    	
    	return b;
    }
    public static boolean match(String pattern, String input){
    	boolean b = false;
    	Pattern r = Pattern.compile(pattern);
    	Matcher m = r.matcher(input);
    	int count =0;
        while(m.find()) {
            count++;
            //System.out.println("Match number "+count);
            b = true;
            break;
         }
    	return b;
    }
    
    public static void main(String[] args)throws Exception{   
    
     ZipUtil zipUtil = new ZipUtil();
     File logFile = new File("e:/temp/zipLog.txt");
     String logFileFullName = logFile.getAbsolutePath();
     zipUtil.setLogStream(new Logger(logFileFullName, true, 5, false));
     String srcDir = "E:/temp/new/com.ibm.lconn.files.web.resources_3.0.0.20151214-1241";
     
     String zipName = "com.ibm.lconn.files.web.resources_3.0.0.20151214-1241.jar";
     boolean a = zipUtil.doZip(srcDir, zipName, null, null, false, false) ;
     System.out.println("a="+a);
     //zipUtil.doZip(srcDir, "news-partialapp-update.zip", "news\\news\\news\\work/fixes/LO82343/EAR/lc.shindig.serverapi.war/WEB-INF/eclipse/plugins/com.ibm.cre.proxy.config-1.4.0-SNAPSHOT.jar", "ibm-partialapp-delete.props,a/b\\c/d.jsp", true);
      
     //String pattern1 ="WEB-INF/ibm-web-b.*\\.xmi,WEB-INF/ibm-web-a.*\\.xmi";
     //pattern1 = pattern1.replace('\\', File.separatorChar);
     //pattern1 = pattern1.replaceAll("\\.", ".");
     //pattern1 = pattern1.replace('/', File.separatorChar);
     //System.out.println("pattern1= "+pattern1);		 
     		
    // String pattern2 ="oawebui.war/WEB-INF/lib/lucene-core-1.9.1.jar/com/ibm/a.class";
    // String input = ".*\\.jar/.*/.*\\..*";
 
     //boolean a = zipUtil.matchAll(pattern1, "a/WEB-INF/ibm-web-bnd.xmi");
     //System.out.println("a="+a);
     //zipUtil.match( input,pattern1);
     //zipUtil.match( input,pattern2);
    }   
}   
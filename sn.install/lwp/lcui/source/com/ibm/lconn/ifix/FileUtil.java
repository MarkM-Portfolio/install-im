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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;  
import java.util.zip.ZipFile;

public class FileUtil {

    public static boolean debugEnabled = false;
    public static final String debugPropertyName = "com.ibm.lconn.ifix.fileutil.debug" ;
	static {

	    String debugValue = System.getProperty(debugPropertyName);
	    debugEnabled = ( (debugValue != null) && debugValue.equals("true") );	 
	} 
	
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
            if(!targetFile.getParentFile().exists()){
            	targetFile.getParentFile().mkdirs();
            }
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

	public static ArrayList<String> copyDirectory(File srcDir, File tgtDir)
			throws IOException {
		ArrayList<String> relPathList = new ArrayList<String>();
		if (srcDir == null || tgtDir == null) {
			return relPathList;
		}
		tgtDir.mkdirs();
		File[] file = srcDir.listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				File sourceFile = file[i];
				File targetFile = new File(tgtDir.getAbsolutePath()
						+ File.separator + file[i].getName());
				//System.out.println("Copy file = " + sourceFile.getAbsolutePath());
				//System.out.println("Copy to file = " + targetFile.getAbsolutePath());
				copyFile(sourceFile, targetFile);
				// System.out.println("Copy File: " +
				// sourceFile.getAbsolutePath());
				// System.out.println("To File: " +
				// sourceFile.getAbsolutePath());
				relPathList.add(targetFile.getAbsolutePath());
			}
			if (file[i].isDirectory()) {
				String dir1 = srcDir.getAbsolutePath() + File.separator
						+ file[i].getName();
				String dir2 = tgtDir.getAbsolutePath() + File.separator
						+ file[i].getName();
				relPathList.addAll(copyDirectory(new File(dir1),
						new File(dir2)));
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

	public static boolean moveDirectory(File srcDir, File tgtDir)
			throws IOException {

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
		if (!flag){
			System.out.println("delete file fail");
			return false;
		}
		if (srcDir.delete()) {
			System.out.println("delete file success");
			return true;
		} else {
			System.out.println("delete file fail");
			return false;
		}
	}

	public static boolean moveFile(File srcFile, File tgtFile)
			throws IOException {

		boolean flag = false;
		if (srcFile == null) {
			return flag;
		}

		if (srcFile.isFile() && srcFile.exists()) {
			copyFile(srcFile, tgtFile);
			if(srcFile.delete()){
			 System.out.println("delete file success !");
			 flag = true;
			}else{
				 srcFile.deleteOnExit();
				 System.out.println("delete file fail !");
				 flag = false;				
			}
			
		}
		return flag;
	}

	public static boolean copyFile(File srcFile, String containerPath)
			throws IOException {

		boolean flag = false;
		if (srcFile == null) {
			return flag;
		}

		if (srcFile.isFile() && srcFile.exists()) {
			File tgtFile = new File(containerPath + File.separator + srcFile.getName());
			copyFile(srcFile, tgtFile);
			//srcFile.delete();
			flag = true;
		}
		return flag;
	}

	public static String getAllFilesRelPath(File srcDir){
		String path = srcDir.getAbsolutePath();
		if (!path.endsWith(File.separator)){
			path += File.separator;
		}
		StringBuffer sb = getAllFilesPath(srcDir);

		String aPath = sb.toString();

		path = path.replace('\\', File.separatorChar);
		aPath = aPath.replace('\\', File.separatorChar);
		path = path.replace('/', File.separatorChar);
		aPath = aPath.replace('/', File.separatorChar);
		
		//System.out.println("path=" + path);
		//System.out.println("aPath=" + aPath);		
		
		String rPath = "";
		String[] sGroup = aPath.split(",");
		for (int i=0;i<sGroup.length;i++){
			String s = sGroup[i];
			s = s.substring(path.length());
			//System.out.println("sGroup[i]=" + s);	
			rPath = rPath + s + ",";
		}
		return rPath;
	}
	public static ArrayList<String> getDeleteFilesPattern(File deleteZip){
		ArrayList<String> deleteFiles = new ArrayList<String>();
		
		try {
			ZipFile zipFile = new ZipFile(deleteZip.getAbsolutePath());
    		Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                if (!e.isDirectory() && e.getName().contains(Contants.PARTIALAPP_DELETE_PROPS)) {
                	deleteFiles.addAll( getContentsByLine(zipFile.getInputStream(e)));                     
                }
            }
            zipFile.close();
    					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("deleteFiles =" + deleteFiles.toString());
		return deleteFiles;
	}
	private static ArrayList<String> getContentsByLine (InputStream input){
		ArrayList<String> deleteFiles = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
     
		System.out.println("Reading File line by line using BufferedReader");
     
        String line;
		try {
			line = reader.readLine();
	        while(line != null){
	        	System.out.println(line);
	            deleteFiles.add(line);
	            line = reader.readLine();
	        }
	     } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
		return deleteFiles;
	}
	public static StringBuffer getAllFilesPath(File srcDir){
		StringBuffer sb = new StringBuffer();
		if(srcDir ==null || !srcDir.exists())
			return sb;
		File[] subFiles = srcDir.listFiles();
		File temp;
		for(int i=0;i<subFiles.length;i++){
			temp = subFiles[i];
			if(temp.isDirectory()){
				sb.append(getAllFilesPath(temp));
			}else{
				sb.append(temp.getAbsolutePath()).append(",");
			}
		}				
		return sb;		
	}
	public static ArrayList<String> getOSGiBundles(String osgiBundles) {
		ArrayList<String> namesList = new ArrayList<String>();

		if (osgiBundles == null || osgiBundles.equals(""))
			return namesList;
		osgiBundles = osgiBundles.replace('\\', '/');
		String[] names = osgiBundles.split(",");

		for (int i = 0; i < names.length; i++) {
			String s1 = names[i].trim();
			String s2 = s1.substring(0, s1.lastIndexOf("*"));
			String name = s2;
			if (s2.contains("/")) {
				name = s2.substring(s2.lastIndexOf("/") + 1);
			}
			System.out.println("OSGi bundles prefix name=" + name);
			namesList.add(name);
		}

		return namesList;
	}

	public static ArrayList<String> refreshFileList(String strRoot,
			String strPath, String baseRoot,
			ArrayList<String> osgiBundlesNames, String tgtRoot)
			throws IOException {
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
			String rPath = aPath.substring(strRoot.length() - 1);
			String rPath2 = rPath;
			String tgtFilePath = tgtRoot + rPath;

			if (files[i].isDirectory()) {

				String name = fileName;
				String versionTimeStamp = "";

				if (fileName.contains("_")) {
					name = fileName.substring(0, fileName.indexOf("_"));
					versionTimeStamp = fileName
							.substring(fileName.indexOf("_") + 1);
				}
				System.out.println("versionTimeStamp = " + versionTimeStamp);

				if (osgiBundlesNames.contains(name)) {

					//System.out.println("aPath=" + aPath);
					//System.out.println("rPath=" + rPath);
					//System.out.println("fullName=" + fileName);
					//System.out.println("name=" + name);
					//System.out.println("versionTimeStamp=" + versionTimeStamp);

					File baseFile = new File(baseRoot + rPath);
					if (baseFile.exists()) {
						System.out
								.println("File version is same in Update folder and Customer env");
					} else {
						File baseParent = baseFile.getParentFile();
						File[] tmpList = baseParent.listFiles();
						File tmpFile;
						String tmpFileName;
						String baseVersionTimeStamp;
						for (int j = 0; j < tmpList.length; j++) {
							tmpFile = tmpList[j];
							tmpFileName = tmpFile.getName();
							if (tmpFileName.contains(name)) {
								System.out.println("baseFileName="
										+ tmpFileName);
								baseVersionTimeStamp = tmpFileName
										.substring(tmpFileName.indexOf("_") + 1);
								System.out.println("baseVersionTimeStamp="
										+ baseVersionTimeStamp);
								String updateFileName = name + "_"
										+ baseVersionTimeStamp;

								rPath2 = rPath.substring(0,
										rPath.lastIndexOf(fileName));
								System.out.println("relPath=" + rPath2);

								tgtFilePath = strRoot + rPath2 + updateFileName;
								System.out.println("tgtFilePath1="
										+ tgtFilePath);
								relPathList.addAll(copyDirectory(files[i],
										new File(tgtFilePath)));

								tgtFilePath = tgtRoot + rPath2 + fileName;
								System.out.println("tgtFilePath2="
										+ tgtFilePath);
								moveDirectory(files[i], new File(tgtFilePath));
								break;
							}
						}
					}

				} else {
					// relPathList.add(files[i].getAbsolutePath());
				}

				relPathList.addAll(refreshFileList(strRoot,
						files[i].getAbsolutePath(), baseRoot, osgiBundlesNames,
						tgtRoot));
			} else {
				relPathList.add(files[i].getAbsolutePath());
			}
		}
		return relPathList;
	}
    public static boolean match(String pattern, String input){
    	boolean b = false;
    	Pattern r = Pattern.compile(pattern);
    	Matcher m = r.matcher(input);
    	int count =0;
        while(m.find()) {
            count++;
            System.out.println("Match number "+count);
           // System.out.println("start(): "+m.start());
            //System.out.println("end(): "+m.end());
         }
    	return b;
    }

    public static void main(String[] args)throws Exception{   
        
    	ArrayList<String> al = FileUtil.getDeleteFilesPattern(new File("E:\\temp\\GAD\\news-partialapp-delete.zip")); 
        String input ="oawebui.war/WEB-INF/web.xml,oawebui.war/WEB-INF/struts-config.xml,oawebui.war/WEB-INF/eclipse/plugins/net.jazz.ajax_1.4.0.jar";

    	for(int i =0; i< al.size(); i++){
    		System.out.println("al.get(i): "+al.get(i));
    		FileUtil.match(al.get(i), input);
    	}
     }   
}

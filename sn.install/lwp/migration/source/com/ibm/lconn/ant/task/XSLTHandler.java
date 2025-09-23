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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import com.ibm.lconn.common.util.Util;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class XSLTHandler extends BaseTask{
	private String todir;
	private String defaultVersion="201";
	private List<FileSet> filesets;
	private List<File> xslts, xmls;
	private HashMap<String, List<File>> xsltPool, sourcePool;
	public class XSLTFileset extends FileSet{
		
	}
	public class XMLFileset extends FileSet{
		
	}
	
	@Override
	public void execute() throws BuildException {
		try{
		initSetFiles();
		initXSLTs();
		initSourceFiles();
		String[] versions = sortVersion();
		String curVersion = null;
		copySourceToDest();
		for (int i = 0; i < versions.length; i++) {
			curVersion = versions[i];
			List<File> sourceFileList = getSourceFileList(curVersion);
			List<File> xsltFileList = getXsltFileList(curVersion);
			Iterator<File> iter = sourceFileList.iterator();
			while (iter.hasNext()) {
				File sourceFile = (File) iter.next();
				String name = Util.removeExt(sourceFile.getName());
				for (Iterator<File> iterator = xsltFileList.iterator(); iterator
						.hasNext();) {
					File xsltFile = iterator.next();
					if(xsltFile.getName().startsWith(name+"-update-")){
						deal(sourceFile, xsltFile);
					}
				}
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copySourceToDest() {
		Copy cp = new Copy();
		cp.setProject(getProject());
		cp.setTaskName(getTaskName());
		cp.setTodir(new File(getTodir()));
		Iterator<FileSet> iter = getFilesets().iterator();
		while (iter.hasNext()) {
			FileSet fs = (FileSet) iter.next();
			if(fs instanceof XMLFileset)
				cp.addFileset(fs);
		}
		cp.setOverwrite(true);
		cp.setFailOnError(true);
		cp.execute();
	}
	
	private void deal(File sourceFile, File xsltFile) {
		String targetVersion = getTargetVersion(xsltFile.getName());
		File destFile;
		try {
			destFile = File.createTempFile("parsed-", "-"+xsltFile.getName()+".xml");
//			destFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Can not create temp file for XSLT handling. ");
		}
		deal(sourceFile, xsltFile, destFile);
		copyFile(destFile, new File(getTodir(), sourceFile.getName()), true);
		copyFile(xsltFile, new File(getTodir(), xsltFile.getName()), true);
		getSourceFileList(targetVersion).add(destFile);
	}

	private void copyFile(File sourceFile, File toFile, boolean overwrite) {
		Copy copy = new Copy();
		copy.setProject(getProject());
		copy.setTaskName(getTaskName());
		copy.setTofile(toFile);
		copy.setFile(sourceFile);
		copy.setOverwrite(overwrite);
		copy.setVerbose(true);
		copy.execute();
	}

	private void deal(File sourceFile, File xsltFile, File destFile) {
		XSLTTransform transform = new XSLTTransform();
		transform.setTaskName(getTaskName());
		transform.setOutput(getOutput());
		transform.setProject(getProject());
		transform.setSource(sourceFile.getAbsolutePath());
		transform.setDest(destFile.getAbsolutePath());
		transform.setXslt(xsltFile.getAbsolutePath());
		transform.setOverwrite(true);
		transform.execute();
	}

	private String[] sortVersion() {
		List<String> version = new ArrayList<String>();
		version.addAll(getXsltPool().keySet());
		Collections.sort(version, new Comparator<String>(){
			public int compare(String o1, String o2) {
				int result = Util.compareVersion(o1, o2);
				return result;
			}
		});
		String[] result = new String[version.size()];
		return version.toArray(result);
	}

	private void initSourceFiles() {
		Iterator<File> iter = xmls.iterator();
		while (iter.hasNext()) {
			File f = (File) iter.next();
			if(!f.getName().endsWith(".xml")) continue;
			String version = getVersionFromXML(f);
			getSourceFileList(version).add(f);
		}
	}

	private String getVersionFromXML(File f) {
		return formatVersion(getDefaultVersion());
	}

	private void initXSLTs() {
		Iterator<File> iter = getXslts().iterator();
		while (iter.hasNext()) {
			File f = (File) iter.next();
			if(!f.getName().endsWith(".xsl")) continue;
			String startVersion = getStartVersion(f.getName());
			List<File> xsltFileList = getXsltFileList(startVersion);
			if(fileExist(xsltFileList, f)) continue;
			xsltFileList.add(f);
		}
	}
	
	/**
	 * @param xsltFileList
	 * @param f
	 * @return
	 */
	private boolean fileExist(List<File> xsltFileList, File f) {
		for (Iterator<File> iterator = xsltFileList.iterator(); iterator.hasNext();) {
			File existFile = (File) iterator.next();
			if(existFile.getName().equals(f.getName())){
				return true;
			}
		}
		return false;
	}

	public static String getStartVersion(String fileName){
		int start = fileName.indexOf("update-") + "update-".length();
		int end = fileName.lastIndexOf('-');
		return formatVersion(fileName.substring(start, end));
	}
	
	public static String getTargetVersion(String fileName){
		int start = fileName.lastIndexOf('-')+1;
		int end = fileName.lastIndexOf('.');
		return formatVersion(fileName.substring(start, end));
	}
	
	public static String formatVersion(String version){
		return version.replace(".", "").trim();
	}

	private void initSetFiles() {
		Iterator<FileSet> iter = getFilesets().iterator();
		while (iter.hasNext()) {
			FileSet fs = (FileSet) iter.next();
			List<File> file = (fs instanceof XSLTFileset? getXslts(): getXmls());
			addFilesTo(fs, file);
		}
	}

	private void addFilesTo(FileSet fs, List<File> file) {
		DirectoryScanner ds = fs.getDirectoryScanner(getProject());
		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++) {
			File pFile = new File(fs.getDir(getProject()), files[i]);
			file.add(pFile);
		}
	}

	public XSLTFileset createXSLTFileSet(){
		XSLTFileset set = new XSLTFileset();
		getFilesets().add(set);
		return set;
	}
	
	public XMLFileset createXMLFileset(){
		XMLFileset set = new XMLFileset();
		getFilesets().add(set);
		return set;
	}

	public String getTodir() {
		return todir;
	}

	public void setTodir(String todir) {
		this.todir = todir;
	}

	public List<FileSet> getFilesets() {
		if(filesets==null) filesets = new ArrayList<FileSet>();
		return filesets;
	}

	public List<File> getXslts() {
		if(xslts==null) xslts = new ArrayList<File>();
		return xslts;
	}

	public List<File> getXmls() {
		if(xmls==null) xmls = new ArrayList<File>();
		return xmls;
	}

	public HashMap<String, List<File>> getXsltPool() {
		if(xsltPool==null) xsltPool = new HashMap<String, List<File>>();
		return xsltPool;
	}
	
	public List<File> getSourceFileList(String startVersion){
		List<File> list = getSourcePool().get(startVersion);
		if(list==null){
			list = new ArrayList<File>();
			getSourcePool().put(startVersion, list);
		}
		return list;
	}
	
	public List<File> getXsltFileList(String startVersion){
		List<File> list = getXsltPool().get(startVersion);
		if(list==null){
			list = new ArrayList<File>();
			getXsltPool().put(startVersion, list);
		}
		return list;
		
	}

	public HashMap<String, List<File>> getSourcePool() {
		if(sourcePool==null) sourcePool = new HashMap<String, List<File>>();
		return sourcePool;
	}

	public String getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(String defaultVersion) {
		this.defaultVersion = defaultVersion;
	}
}

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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class XMLReplaceHandler extends BaseTask {
	private String todir;
	private List<FileSet> filesets;
	private String version = "25";
	
	private class ReplaceItem {
		/**
		 * @param file
		 */
		public ReplaceItem(File file) {
			this.dest = file;
		}

		File source, dest, templete;
		
		public String toString(){
			return MessageFormat.format("Source: {0}, dest: {1}, template: {2}", source, dest, templete);
		}
	}
	
	public static void main(String[] args) {
		XMLReplaceHandler handler = new XMLReplaceHandler();
		handler.setOutput(System.out);
		handler.setProject(new Project());
		handler.setTodir("d:/share/CCRC/00000/XMLReplace/dest");
		
		ListFileset lfs = handler.createListFileset();
		lfs.setDir(new File("d:/share/CCRC/00000/XMLReplace/tem"));
		
		SourceFileset sfs = handler.createSourceFileset();
		sfs.setDir(new File("d:/share/CCRC/00000/XMLReplace/source"));
		
		handler.execute();
	}

	private HashMap<String, ReplaceItem> replaceItemList;

	private void initSetFiles() {
		Iterator<FileSet> iter = getFilesets().iterator();
		while (iter.hasNext()) {
			FileSet fs = (FileSet) iter.next();
			boolean isSource = fs instanceof SourceFileset;
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++) {
				File pFile = new File(fs.getDir(getProject()), files[i]);
				log("Loading {0}", pFile);
				String name = pFile.getName();
				String ext = (isSource ? ".xml" : "-" + getVersion() + ".txt");
				if (name.toLowerCase().endsWith(ext)) {
					String nameWithoutExt = name.substring(0, name.length()
							- ext.length());
					ReplaceItem item = getReplaceItemList().get(nameWithoutExt);
					if (item != null) {
						if (isSource){
							item.source = pFile;
						}
						else{
							item.templete = pFile;
						}
					}
				}
			}
		}
	}

	public SourceFileset createSourceFileset() {
		SourceFileset set = new SourceFileset();
		getFilesets().add(set);
		return set;
	}

	public ListFileset createListFileset() {
		ListFileset set = new ListFileset();
		getFilesets().add(set);
		return set;
	}

	public class SourceFileset extends FileSet {

	}

	public class ListFileset extends FileSet {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		initDest();
		initSetFiles();
		Iterator<ReplaceItem> iter = getReplaceItemList().values().iterator();
		while (iter.hasNext()) {
			XMLReplaceHandler.ReplaceItem item = (XMLReplaceHandler.ReplaceItem) iter
					.next();
			log("Replacing {0}", item.dest.getAbsoluteFile());
			if (item.source == null)
				log("Source of {0} can not be found. ", item.dest.getName());
			else if (item.templete == null)
				log(
						"Replacing element list of {0}, for version {1} can not be found. ",
						item.dest.getName(), getVersion());
			else {
				execute(item.source, item.dest, item.templete);
			}
		}
	}

	/**
	 * @param source
	 * @param dest
	 * @param templete
	 */
	private void execute(File source, File dest, File templete) {
		XMLReplace t = new XMLReplace();
		t.setProject(getProject());
		t.setTaskName(getTaskName());
		t.setSource(source.getAbsolutePath());
		t.setDest(dest.getAbsolutePath());
		t.setListfile(templete.getAbsolutePath());
		t.execute();
	}


	/**
	 * 
	 */
	private void initDest() {
		File destDir = new File(getTodir());
		List<File> destFiles = new ArrayList<File>();
		addXMLFiles(destDir, destFiles);
		
		for (Iterator<File> iter = destFiles.iterator(); iter.hasNext();) {
			File destFile = (File) iter.next();
			String name = destFile.getName();
			String nameWithoutExt = name.substring(0, name.length()
					- ".xml".length());
			getReplaceItemList().put(nameWithoutExt,
					new ReplaceItem(destFile));
		}

	}

	private void addXMLFiles(File destDir, List<File> destFiles) {
		File[] listFiles2 = destDir.listFiles();
		for (int j = 0; j < listFiles2.length; j++) {
			File f = listFiles2[j];
			if(f.isDirectory()){
				addXMLFiles(f, destFiles);
			}else if( f.getName().toLowerCase().endsWith(".xml")){
				destFiles.add(f);
			}
		}
	}

	public List<FileSet> getFilesets() {
		if (filesets == null)
			filesets = new ArrayList<FileSet>();
		return filesets;
	}

	public String getTodir() {
		return todir;
	}

	public void setTodir(String todir) {
		this.todir = todir;
	}

	public HashMap<String, ReplaceItem> getReplaceItemList() {
		if (replaceItemList == null)
			replaceItemList = new HashMap<String, ReplaceItem>();
		return replaceItemList;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

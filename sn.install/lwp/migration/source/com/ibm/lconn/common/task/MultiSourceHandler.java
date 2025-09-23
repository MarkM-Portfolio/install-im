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
package com.ibm.lconn.common.task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Copyfile;

import com.ibm.lconn.ant.task.BaseTask;
import com.ibm.lconn.common.util.Util;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class MultiSourceHandler extends BaseTask {
	private File sourceFolder, buildFolder, toDir;
	private String startVersion, toVersion, versionPath;

	// private List<File> sourceFileList;
	//	
	// public List<File> getSourceFileList() {
	// if(sourceFileList==null) sourceFileList = new ArrayList<File>();
	// return sourceFileList;
	// }

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}

	public String getVersionPath() {
		return versionPath;
	}

	public void setVersionPath(String versionPath) {
		this.versionPath = versionPath;
	}

	public String getStartVersion() {
		return startVersion;
	}

	public void setStartVersion(String startVersion) {
		this.startVersion = startVersion;
	}

	@Override
	public void execute() throws BuildException {
		execute(initSourceFileList());
	}

	public void execute(List<File> sourceFileList) {
		for (File xmlFile : sourceFileList) {
			xmlFile = Util.getFile(xmlFile.getAbsolutePath());
			System.out.println("==============================================================================================================================================");
			log("Handling {0} ... ... ", xmlFile.getAbsolutePath());
			try {
				String name = xmlFile.getName();
				String xmlSymbol = name.substring(0, name.length()
						- getSourceSymbol().length());
//				if ("LotusConnections-config".equals(xmlSymbol)){
//					xmlSymbol = "global-config";
//				}
				LocalImportHandler lih = createLocalImportHandler();
				lih.setVersionPath(getVersionPath());
				log("MultiSourceHandler version path {0}. ", getVersionPath());
				lih.setXmlSymbol(xmlSymbol);
				lih.setSourceFile(xmlFile);
				lih.execute();
				log("Finished handling {0}. ", xmlFile);
				System.out.println("==============================================================================================================================================");
			} catch (Exception e) {
				log("Handling {0} failed. ", xmlFile);
				e.printStackTrace();
				System.out.println("==============================================================================================================================================");
			}
		}
	}

	private String getSourceSymbol() {
//		if(System.getProperty("isLocalMigration")=="true")
//			return "-source.xml";
		return ".xml";
	}

	private LocalImportHandler createLocalImportHandler() {
		LocalImportHandler lih = new LocalImportHandler();
		lih.setProject(getProject());
		lih.setOutput(getOutput());
		lih.setTaskName(getTaskName());
		lih.setFromDir(getBuildFolder());
		lih.setToDir(getToDir());
		lih.setStartVersion(getStartVersion());
		lih.setToVerstion(getToVersion());
		return lih;
	}

	private List<File> initSourceFileList() {
		File sourceFolder = getSourceFolder();
		File[] listFiles = sourceFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(getSourceSymbol());
			}
		});
		ArrayList<File> list = new ArrayList<File>();
		for (File file : listFiles) {
			list.add(file);
		}
		return list;
	}

	public File getSourceFolder() {
		return sourceFolder;
	}

	public void setSourceFolder(File sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public File getBuildFolder() {
		return buildFolder;
	}

	public void setBuildFolder(File buildFolder) {
		this.buildFolder = buildFolder;
	}

	public File getToDir() {
		return toDir;
	}

	public void setToDir(File toDir) {
		this.toDir = toDir;
	}

	public static void main(String[] args) {
//		String[] args = {"25","30","C:/Documents and Settings/Administrator/Desktop/localmigration251/INPUT","C:/Documents and Settings/Administrator/Desktop/localmigration251/UTIL","C:/Documents and Settings/Administrator/Desktop/localmigration251/OUTPUT","true"};
//		String[] args = {"25","30","true","./INPUT","./UTIL","./OUTPUT"};
		try {
			String startVersion = args[0];
			String toVersion = args[1];
			File sourceFolder = Util.getFile(args[2]);
			File buildFolder = Util.getFile(args[3]);
			File toDir = Util.getFile(args[4]);
			String versionPath = args[5];
			System.setProperty("isLocalMigration", "false");
			if(args.length == 7){
				System.setProperty("isLocalMigration", args[6]);
			}
			if (sourceFolder.exists() && buildFolder.exists()) {
				if (!toDir.delete()) {
					System.out.println("Warning: " + toDir
							+ " is not cleared. ");
				}
				toDir.mkdirs();
				MultiSourceHandler msh = new MultiSourceHandler();
				Project p = new Project();
				p.setName("MultiSourceHandler");
				msh.setStartVersion(startVersion);
				msh.setProject(p);
				msh.setOutput(System.out);
				msh.setSourceFolder(sourceFolder);
				msh.setToDir(toDir);
				msh.setBuildFolder(buildFolder);
				msh.setToVersion(toVersion);
				msh.setVersionPath(versionPath);
				msh.execute();
			} else {
				help();
			}
		} catch (Exception e) {
			help();
		}
	}

	private static void help() {
		System.out
				.println("- Help -\nArgs: <startVersion> <toVersion> <sourceFolder> <buildFolder> <toDir>");
		System.out
				.println("This tool define every xml file with an xmlSymbol, that is,  LotusConnections-config is the xmlSymbol for LotusConnections-config.xml, search-config is the xmlSymbol for search-config.xml. ");
		System.out
				.println("\t<startVersion>: This is usually 201 or 201b1 or 201b2. ");
		System.out
		.println("\t<toVersion>: This is usually 201b1 or 201b2 or 25 or 30. ");
		System.out
				.println("\t<sourceFolder>: The folder contain the source XML. E.g. The xml files from 2.0.1 deployed environment. Please make sure the file name is in the format of <LotusConnections-config-source.xml>. ");
		System.out
				.println("\t<buildFoder>: The folder contain the fresh installed XML. Containing: <xmlSymbol>.xml, <xmlSymbol>-30.txt, all the xslt files and <xmlSymbol>.xsd optionally. ");
		System.out.println("\t<toDir>: The folder to hold the output. ");
	}

}

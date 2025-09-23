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
import java.text.MessageFormat;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;

import com.ibm.lconn.ant.task.BaseTask;
import com.ibm.lconn.ant.task.XMLReplace;
import com.ibm.lconn.ant.task.XSLTTransform;
import com.ibm.lconn.common.util.Util;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class LocalImportHandler extends BaseTask {
	private File xsltDir, sourceFile, targetFile, toDir, fromDir, dtdFile;

	public File getDtdFile() {
		if (dtdFile == null && getFromDir() != null) {
			File file = new File(getFromDir(), getXmlSymbol() + ".xsd");
			if (file.exists() && file.isFile())
				return file;
		}
		return dtdFile;
	}

	public void setDtdFile(File dtdFile) {
		this.dtdFile = dtdFile;
	}

	public File getFromDir() {
		return fromDir;
	}

	public void setFromDir(File fromDir) {
		this.fromDir = fromDir;
	}

	private String[] versions = "20,201,25b1,25b2,25,30I1,30I2,30I3,30"
			.split(",");
	private String versionPath = null;

	public String getVersionPath() {
		return versionPath;
	}

	public void setVersionPath(String versionPath) {
		this.versionPath = versionPath;
	}

	public String[] getVersions() {
		String versionPath = getVersionPath();
		log("LocalImportHandler version path {0}. ", versionPath);
		if (versionPath != null && versionPath.trim().length() != 0)
			return versionPath.split(",");
		return versions;
	}

	private String startVersion, toVerstion;

	public String getToVerstion() {
		return toVerstion;
	}

	public void setToVerstion(String toVerstion) {
		this.toVerstion = toVerstion;
	}

	public File getXsltDir() {
		return xsltDir;
	}

	public void setXsltDir(File xsltDir) {
		this.xsltDir = xsltDir;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public File getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(File targetFile) {
		this.targetFile = targetFile;
	}

	public File getToDir() {
		return toDir;
	}

	public void setToDir(File toDir) {
		this.toDir = toDir;
	}

	public String getStartVersion() {
		return startVersion;
	}

	public void setStartVersion(String startVersion) {
		this.startVersion = startVersion;
	}

	public String getXmlSymbol() {
		return xmlSymbol;
	}

	public void setXmlSymbol(String xmlSymbol) {
		this.xmlSymbol = xmlSymbol;
	}

	public File getTemplete() {
		return templete;
	}

	public void setTemplete(File templete) {
		this.templete = templete;
	}

	public int getVersionPosition(String version) {
		int versionPosition = 0;
		versions = getVersions();
		for (int i = 0; i < versions.length; i++) {
			if (versions[i].equalsIgnoreCase(version)) {
				versionPosition = i + 1;
				break;
			}
		}
		return versionPosition;
	}

	private String xmlSymbol;
	private File templete;
	private XSLTPatch xsltPatcher;

	public void handleXSLT(File sourceFile, File xsltFile, File destFile) {
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

	public boolean handleReplace(File sourceFile, File destFile, File templete) {
		if (!templete.exists() || !templete.isFile()) {
			log("The replacement is skipped as {0} does not exist. ",
					templete.getName());
			return false;
		}
		boolean result = validate(sourceFile);
		if (!result) {
			log("WARNING: The schema validation on {0} does not pass. ",
					sourceFile);
		}

		XMLReplace t = new XMLReplace();
		t.setProject(getProject());
		t.setTaskName(getTaskName());
		t.setOutput(getOutput());
		t.setSource(sourceFile.getAbsolutePath());
		t.setDest(destFile.getAbsolutePath());
		t.setListfile(templete.getAbsolutePath());
		t.execute();
		validate(destFile);
		return true;
	}

	private boolean validate(File destFile) {
		File dtdFile = getDtdFile();
		if (dtdFile == null || !getDtdFile().exists()) {
			log("DTD file {0} does not exist. DTD validation is skipped. ",
					getDtdFile());
			return true;
		} else {
			return validate(destFile, getDtdFile());
		}
	}

	private boolean validate(File source, File schemaLocation) {
		try {
			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = factory.newSchema(schemaLocation);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(source));
			log("{0} is valid.", source.getAbsolutePath());
			return true;
		} catch (Exception ex) {
			log("WARNING: {0} is invalid because {1}. ",
					source.getAbsolutePath(), ex.getMessage(), getDtdFile());
			return false;
		}

	}

	public void handleXML(String xmlSymbol, String startVersion,
			File sourceFile, File xsltDir, File toDir, File targetFile,
			File templete) {
		if (targetFile == null || !targetFile.exists()) {
			log("The operation on {0} is skipped, for there is no {1} as the target XML.",
					xmlSymbol, targetFile.getAbsolutePath());
			return;
		}

		File currentVersionFile = handleXSLTs(xmlSymbol, startVersion,
				sourceFile, xsltDir, toDir);

		int before25Flag = getVersionPosition(getToVerstion())
				- getVersionPosition("25");
		log("before25Flag = " + before25Flag);
		File destFile = new File(toDir, xmlSymbol + getTargetExt());
		destFile = Util.getFile(destFile.getAbsolutePath());
		if (before25Flag <= 0) {
			copyFile(targetFile, destFile);
		}

		// System.out.println("===================================================");
		// System.out.println("xmlSymbol: " + xmlSymbol);
		// System.out.println("startVersion: " + startVersion + " " + " " );
		// System.out.println("sourceFile: " + sourceFile.getAbsolutePath());
		// System.out.println("targetFile: " + targetFile.getAbsolutePath());
		// System.out.println("templete: " + templete.getAbsolutePath());
		// System.out.println("xsltDir: " + xsltDir.getAbsolutePath());
		// System.out.println("toDir: " + toDir.getAbsolutePath());
		// System.out.println("currentVersionFile: " +
		// currentVersionFile.getAbsolutePath());
		// System.out.println("before25Flag: " + before25Flag);
		// System.out.println("ToVersionPosition: " +
		// getVersionPosition(getToVerstion()));
		// System.out.println("25VersionPosition: " + getVersionPosition("25"));
		// System.out.println("VersionPath: " + getVersionPath());
		// System.out.println("===================================================");

		if (currentVersionFile.getName().endsWith("-transformed.xml")) {

			// if after Version 25, create the copy of currentVersionFile
			File currentVersionFile_temp = null;
			String currentVersionFilename_temp = "";
			if (before25Flag > 0) {
				currentVersionFile_temp = new File(
						currentVersionFile.getAbsolutePath() + "_temp");
				copyFile(currentVersionFile, currentVersionFile_temp);
				currentVersionFilename_temp = currentVersionFile.getName();
			}

			boolean replacedFlag = false;
			if (before25Flag <= 0) {
				replacedFlag = handleReplace(currentVersionFile, destFile,
						templete);
			} else {
				replacedFlag = handleReplace(targetFile, currentVersionFile,
						templete);
			}

			if (replacedFlag) {
				// rename
				File replacedFile = new File(toDir, xmlSymbol + "-replaced.xml");
				if (!replacedFile.exists()
						|| (replacedFile.exists() && replacedFile.delete())) {
					if (before25Flag <= 0) {
						destFile.renameTo(replacedFile);
					} else {
						currentVersionFile.renameTo(replacedFile);
					}
				} else {
					if (before25Flag <= 0) {
						log("Rename the file {0} to {1} failed!",
								destFile.getAbsoluteFile(),
								replacedFile.getAbsoluteFile());
					} else {
						log("Rename the file {0} to {1} failed!",
								currentVersionFile.getAbsoluteFile(),
								replacedFile.getAbsoluteFile());
					}
				}

				// if after Version 25, create the last transformed file
				File tempFile = new File(toDir, currentVersionFilename_temp);
				if (before25Flag > 0) {
					if (!tempFile.exists()
							|| (tempFile.exists() && tempFile.delete())) {
						currentVersionFile_temp.renameTo(tempFile);
					} else {
						log("Rename the file {0} to {1} failed!",
								currentVersionFile_temp.getAbsoluteFile(),
								tempFile.getAbsoluteFile());
					}
				}

				copyToDeployFile(replacedFile, destFile, toDir, xmlSymbol);

			} else {
				// if replace failed, restore the last transformed file.
				File tempFile = new File(toDir, currentVersionFilename_temp);
				if (before25Flag > 0) {
					if (!tempFile.exists()
							|| (tempFile.exists() && tempFile.delete())) {
						currentVersionFile_temp.renameTo(tempFile);
					} else {
						log("Rename the file {0} to {1} failed!",
								currentVersionFile_temp.getAbsoluteFile(),
								tempFile.getAbsoluteFile());
					}
				}

				copyToDeployFile(currentVersionFile, destFile, toDir, xmlSymbol);
			}

		} else {
			File sourFile = new File(toDir, xmlSymbol + "-sour.xml");
			sourFile = Util.getFile(sourFile.getAbsolutePath());
			if (before25Flag > 0) {
				copyFile(currentVersionFile, sourFile);
			}

			boolean replacedFlag = false;
			if (before25Flag <= 0) {
				replacedFlag = handleReplace(currentVersionFile, destFile,
						templete);
			} else {
				replacedFlag = handleReplace(targetFile, sourFile, templete);
			}

			if (replacedFlag) {
				// rename
				File replacedFile = new File(toDir, xmlSymbol + "-replaced.xml");
				if (!replacedFile.exists()
						|| (replacedFile.exists() && replacedFile.delete())) {
					if (before25Flag <= 0) {
						destFile.renameTo(replacedFile);
					} else {
						sourFile.renameTo(replacedFile);
					}
				} else {
					if (before25Flag <= 0) {
						log("Rename the file {0} to {1} failed!",
								destFile.getAbsoluteFile(),
								replacedFile.getAbsoluteFile());
					} else {
						log("Rename the file {0} to {1} failed!",
								sourFile.getAbsoluteFile(),
								replacedFile.getAbsoluteFile());
					}
				}

				copyToDeployFile(replacedFile, destFile, toDir, xmlSymbol);

			} else {
				copyToDeployFile(targetFile, destFile, toDir, xmlSymbol);

				if (sourFile.exists()) {
					sourFile.delete();
				}
			}

		}

		// if(destFile.delete()){
		// replacedFile = new File(toDir, xmlSymbol + "-replaced.xml");
		// currentVersionFile.renameTo(replacedFile);
		// }else{
		// log("Rename the file {0} to {1} failed!",
		// currentVersionFile.getAbsoluteFile(), replacedFile);
		// }

	}

	protected void copyToDeployFile(File replacedFile, File destFile,
			File toDir, String xmlSymbol) {
		if (isLocal()) {
			if (destFile.exists()) {
				destFile.delete();
			}
			File deployFile = new File(toDir, xmlSymbol + "-deploy.xml");
			if (!deployFile.exists()
					|| (deployFile.exists() && deployFile.delete())) {
				copyFile(replacedFile, deployFile);
			} else {
				log("Copy the file {0} to {1} failed!",
						replacedFile.getAbsoluteFile(),
						deployFile.getAbsoluteFile());
			}
		} else {
			if (!destFile.exists() || (destFile.exists() && destFile.delete())) {
				copyFile(replacedFile, destFile);
			} else {
				log("Copy the file {0} to {1} failed!",
						replacedFile.getAbsoluteFile(),
						destFile.getAbsoluteFile());
			}
		}
	}

	protected String getTargetExt() {
		// if(isLocal())
		// return "-target.xml";
		return ".xml";
	}

	private boolean isLocal() {
		return "true".equals(System.getProperty("isLocalMigration"));
	}

	private void copyFile(File source, File dest) {
		Copy cp = new Copy();
		cp.setProject(getProject());
		cp.setTaskName(getTaskName());
		cp.setFile(source);
		cp.setTofile(dest);
		cp.execute();
	}

	public File handleXSLTs(String xmlSymbol, String startVersion,
			File sourceFile, File xsltDir, File toDir) {
		File currentVersionFile = sourceFile;
		boolean started = false;
		String[] versions = getVersions();
		for (int i = 0; i < versions.length; i++) {
			log("LocalImporthandler versions {0}. ", versions[i]);
		}
		String startVersionRemovedCR = null;
		if(startVersion.toLowerCase().contains("cr")) {
			for (int i = 0; i < versions.length; i++) {
				if(startVersion.equalsIgnoreCase(versions[i])) {
					startVersionRemovedCR = startVersion.toLowerCase();
					break;
				}
			}
			// startVersion contains 'cr' and not in the path
			if(startVersionRemovedCR == null) {
				startVersionRemovedCR = startVersion.substring(0, 2);
			}
		} else {
			startVersionRemovedCR = startVersion;
		}
			
		String currentVersion = startVersionRemovedCR;
		
		
		for (int i = 0; i < versions.length - 1; i++) {
			String fromVersion = versions[i];
			System.out.println("fromVersion: " + fromVersion);
			System.out.println("startVersion: " + startVersion);
			System.out.println("startVersionRemovedCR: " + startVersionRemovedCR);
			
			if (!started) {
				started = fromVersion.equals(startVersionRemovedCR);
				if (!started)
					continue;
			}
			if (fromVersion.equals(getToVerstion())) {
				break;
			}

			String xsltName = "";
			File xslt = null;
			File nextVersionFile = null;

			String toVersion = "";

			//int indexTmp = getVersionPosition(getToVerstion()) - 1;
			//if (indexTmp != -1) {
			//	i = indexTmp - 1;
			//}

			// for (int j = versions.length - 1; j >= i; j--) {
			toVersion = versions[i + 1];;
			System.out.println("fromVersion: " + fromVersion + " toVersion : " + toVersion);
			xsltName = getXSLTName(xmlSymbol, fromVersion, toVersion);
			nextVersionFile = new File(toDir, getVersionedXMLName(xmlSymbol,
					toVersion));
			if(!nextVersionFile.exists()) {
				xsltName = getXSLTName(xmlSymbol, fromVersion.toUpperCase(), toVersion.toUpperCase());
				nextVersionFile = new File(toDir, getVersionedXMLName(xmlSymbol,
						toVersion));
			}
			nextVersionFile = Util.getFile(nextVersionFile.getAbsolutePath());
			
			xsltName = getXSLTName(xmlSymbol, fromVersion, toVersion);
			xslt = new File(xsltDir, xsltName);
			if(!xslt.exists()) {
				xsltName = getXSLTName(xmlSymbol, fromVersion.toUpperCase(), toVersion.toUpperCase());
				xslt = new File(xsltDir, xsltName);
			}
			if (xslt.exists() && xslt.isFile()) {
				log("Transforming from {0} to {1} for {2}", fromVersion,
						toVersion, currentVersionFile);
				getXSLTPatcher().patchBefore(xmlSymbol, currentVersionFile,
						fromVersion, toVersion);
				handleXSLT(currentVersionFile, xslt, nextVersionFile);
				getXSLTPatcher().patchPost(xmlSymbol, nextVersionFile,
						fromVersion, toVersion);
				log("Finished transformation from {0} to {1} for {2}",
						fromVersion, toVersion, currentVersionFile);
				currentVersionFile = nextVersionFile;
				currentVersion = toVersion;
				//i = j - 1; // will be added by one in for loop
				
			} else {
				log("Transformation from version {0} to version {1} is skipped as there''s no {2}. ",
						fromVersion, toVersion, xslt.getName());
			}
			// }

		}
		// if (currentVersion.equals(startVersion)) {
		// log(
		// "No transforming is performed. XSLT for {0} of version {1} is not found. ",
		// xmlSymbol, startVersion);
		// }
		// if (!currentVersion.equals(getFinalVersion())) {
		// log(
		// "The current version XML {0} is not of the top version {1}, the replace will be no sense. ",
		// currentVersionFile, getFinalVersion());
		// }
		return currentVersionFile;
	}

	protected XSLTPatch getXSLTPatcher() {
		if (this.xsltPatcher == null) {
			this.xsltPatcher = new XSLTPatch();
			setTask(this.xsltPatcher);
		}
		return this.xsltPatcher;
	}

	// private String getFinalVersion() {
	// return getToVerstion();
	// }

	private String getVersionedXMLName(String xmlSymbol, String version) {
		return MessageFormat.format("{0}-{1}-transformed.xml", xmlSymbol,
				version);
	}

	private String getTxtFileName(String xmlSymbol) {
		return getTxtFileName(xmlSymbol, "30");
	}

	private String getTxtFileName(String xmlSymbol, String finalVersion) {
		return MessageFormat.format("{0}-{1}.txt", xmlSymbol, finalVersion);
	}

	private String getXSLTName(String xmlSymbol, String startVersion,
			String toVersion) {
		return MessageFormat.format("{0}-update-{1}-{2}.xsl", xmlSymbol,
				startVersion, toVersion);
	}

	@Override
	public void execute() throws BuildException {

		if (getFromDir() != null) {
			String xmlSymbol = getXmlSymbol();
			handleXML(xmlSymbol, startVersion, getDefaultSourceFile(),
					getFromDir(), toDir, getDefaultTargetFile(),
					getDefaultTemplateFile());
		} else {
			handleXML(xmlSymbol, startVersion, sourceFile, xsltDir, toDir,
					targetFile, templete);
		}
	}

	private File getDefaultTemplateFile() {
		return new File(getFromDir(),
				getTxtFileName(xmlSymbol, getToVerstion()));
	}

	private File getDefaultTargetFile() {
		return new File(getFromDir(), xmlSymbol + ".xml");
	}

	private File getDefaultSourceFile() {
		if (getSourceFile() != null)
			return getSourceFile();
		return new File(getFromDir(), xmlSymbol + "-source.xml");
	}

	public static void mainSample(String[] args) {
		LocalImportHandler handler = new LocalImportHandler();
		handler.setOutput(System.out);
		Project project = new Project();
		project.setName("LocalImport");
		handler.setProject(project);
		handler.setTaskName("LocalImportTask");
		// handler.handleXML(xmlSymbol, startVersion, sourceFile, xsltDir,
		// toDir, targetFile, templete);
		handler.setXmlSymbol("LotusConnections-config");
		handler.setStartVersion("20");
		handler.setSourceFile(new File(
				"D:/Charley/MyWork/Dev/LC2.5_Migration_Local/config/source/LotusConnections-config-source.xml"));
		handler.setXsltDir(new File(
				"D:/Charley/MyWork/Dev/LC2.5_Migration_Local/config/source"));
		handler.setToDir(new File(
				"D:/Charley/MyWork/Dev/LC2.5_Migration_Local/config/dest"));
		handler.setTargetFile(new File(
				"D:/Charley/MyWork/Dev/LC2.5_Migration_Local/config/source/LotusConnections-config-25.xml"));
		handler.setTemplete(new File(
				"D:/Charley/MyWork/Dev/LC2.5_Migration_Local/config/source/LotusConnections-config-25.txt"));
		handler.setDtdFile(new File(
				"D:/Charley/MyWork/Dev/LC2.5_Migration_Local/config/source/LotusConnections-config.xsd"));
		handler.execute();
	}

	/**
	 * Args [ LotusConnections-config, 20, LotusConnections-config-source.xml,
	 * xsltFolder, outputFolder, targetFreshInstallFile, txtFile, XSD
	 * file(optional)]
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LocalImportHandler handler = new LocalImportHandler();
		handler.setOutput(System.out);
		Project project = new Project();
		project.setName("LocalImport");
		handler.setProject(project);
		handler.setTaskName("LocalImportTask");
		// handler.handleXML(xmlSymbol, startVersion, sourceFile, xsltDir,
		// toDir, targetFile, templete);
		handler.setXmlSymbol(args[0]);
		handler.setStartVersion(args[1]);
		handler.setSourceFile(new File(args[2]));
		handler.setXsltDir(new File(args[3]));
		handler.setToDir(new File(args[4]));
		handler.setTargetFile(new File(args[5]));
		handler.setTemplete(new File(args[6]));
		if (args.length >= 8) {
			handler.setDtdFile(new File(args[7]));
		}
		handler.execute();
	}

}

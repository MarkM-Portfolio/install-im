/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.migration.blogs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.ibm.lconn.common.file.FileOperator;
import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.util.Spliter;

public class FilesExtractor extends LogOperator {

//	private static Log log = LogFactory.getLog(FilesExtractor.class);

	public static void main(String[] args) throws Exception {
		
		// getProperties();
		String sourceStr, targetStr;
		sourceStr = "e:/temp2/";
		targetStr = "e:/temp2/";
		FilesExtractor fe = new FilesExtractor();
		fe.setSource(sourceStr);
		fe.setTarget(targetStr);
		fe.setOutput(System.out);
		fe.execute();
		
	}

	private String source;
	private String target;
	
	@Override
	public boolean execute(String para) {
		Spliter sp = new Spliter(para);
		String source = sp.getHeader();
		String target = sp.getTail();
		source = resolve(source);
		target = resolve(target);
		setSource(source);
		setTarget(target);
		try {
			execute();
			return true;
		} catch (IOException e) {
			log("Extracting Blogs upload directory fail", e);
			return false;
		}
	}

	private void execute() throws IOException {
		log("Extracting files...");
		File source = new File(getSource());
		File target = new File(getTarget());
		log("From: " + source);
		log("To: " + target);
		if (!source.exists())
			throw new IllegalArgumentException("Source does not exist");
		if (!target.exists())
			throw new IllegalArgumentException("Target does not exist");

		File[] dirs = source.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (int n = 0; n < dirs.length; n++) {
			File from = dirs[n];
			File[] test2 = from.listFiles();
			File to = new File(target, getPath(from.getName()));
			to.mkdirs();
			log(from + " -> " + to + " :: ");
			String dirName = from.getName();
			if (dirName.length() == 1) {
				System.out.println("handling one character folder -- ");
				copySingleCharFolderFiles(test2, to, 2);
			} else
				copyFiles(from, to);
		}
		log("Complete");
	}
	private void copySingleCharFolderFiles(File[] files, File to, int depth)
			throws IOException {
		if(depth <= 0) return;
		for (int n = 0; n < files.length; n++) {
			File file = files[n];
			log("\t" + file.getName());
			if (file.isDirectory()) {
				if(depth < 2)
					continue;
				File[] contents = file.listFiles();
				File newdir = new File(to, file.getName());
				newdir.mkdir();
				copySingleCharFolderFiles(contents, newdir, depth-1);
			} else {
				System.out.println("copying file " + file.getName());
				File newfile = new File(to, file.getName());
				FileChannel fromChannel = new FileInputStream(file)
						.getChannel();
				FileChannel toChannel = new FileOutputStream(newfile)
						.getChannel();
				fromChannel.transferTo(0, fromChannel.size(), toChannel);
				fromChannel.close();
				toChannel.close();
			}
		}
	}

	private void copyFiles(File from, File to) throws IOException {
		new FileOperator().copy(from, to, true);
	}

	private String getPath(String handle) {
		try {
			String h = "";
			int c = 0, n = 0;
			while (c < 3) {
				if (n < handle.length()) {
					char ch = handle.charAt(n++);
					if (Character.isLetterOrDigit(ch)) {
						c++;
						h += ch + File.separator;
					}
				} else {
					c++;
					h += h.charAt(h.length() - 2) + File.separator;
				}
			}
			handle = h + handle;
		} catch (Exception e) {
		}
		return handle;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}

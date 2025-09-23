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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ibm.lconn.common.util.Util;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class ZipExtract extends BaseTask {
	private String dest, src, URI;
	private String key = ".zip,.jar,.war,.ear";
	private String[] keys = key.split(",");
	private Hashtable<File, File> extractedPool;
	private String extractionList;

	public String getExtractionList() {
		return extractionList;
	}

	public void setExtractionList(String extractionList) {
		this.extractionList = extractionList;
	}

	public void init() {
		extractedPool = new Hashtable<File, File>();
	}

	public static void main(String[] args) {
//		testMain(args);
		testLocal();
	}

	public static void testLocal(){
		ZipExtract ze = new ZipExtract();
//		ze.setSrc("d:/temp/xiaofeng/lc.util.web-2.0.jar");
//		ze.setDest("d:/temp/xiaofeng/extract");
//		ze
//				.setURI("com/ibm/lconn/core/web/auth/wasimpl/WasLCLoginServiceImpl.class");
//		ze.execute();

		ze.setDest("d:/temp/xiaofeng/extract");
		ze
				.setExtractionList("D:/work/JAVA_Project/Java_Other/other/ant.zip/extractList");
		ze.execute();
	}

	public void execute() {
		log("Extracting files to {0}", Util.getFile(getDest()).getAbsolutePath());
		init();

		try {
			String listStr = getExtractionList();
			File listFile = Util.getFile(listStr);
			if (!listFile.exists()) {
				deal();
			} else {
				log("Using settings in file: {0}", listFile.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(listFile)));
				String line = br.readLine();
				String dest = getDest();
				String tempDest = null;
				String itemDest = null;
				while (line != null) {
					if (!line.startsWith("#")) {
						if (line.startsWith("\t")) {
							setURI(line.trim().replace('\\', '/'));
							if(tempDest!=null){
								setDest(new File(Util.getFile(getDest()), tempDest).getAbsolutePath());
							}else if(itemDest!=null){
								setDest(new File(Util.getFile(getDest()), itemDest).getAbsolutePath());
							}
							deal();
							setDest(dest);
							tempDest = null;
						} else if(line.startsWith("\tDEST:")){
							tempDest = line.substring("\tDEST-".length()).trim();
						} else if(line.startsWith("DEST:")){
							itemDest = line.substring("DEST-".length()).trim();
						} else{
							setSrc(line.trim());
							tempDest = null;
							itemDest = null;
						} 
					}
					line = br.readLine();
				}
				br.close();
			}
			log("Finished extracting files to {0}", Util.getFile(getDest()));
			
		} catch (Exception e) {
			log("ERROR: Bad format of extraction list file");
			e.printStackTrace();
			log("Failed to extract files to {0}", Util.getFile(getDest()));
		}

		clear();
	}

	private void deal() {
		String dest = getDest();
		String src = getSrc();
		File destFile = Util.getFile(dest);
		File srcFile = Util.getFile(src);
		String uri = getURI();
		deal(destFile, srcFile, uri);
	}

	private void deal(File destFile, File srcFile, String uri) {
		int index = getIndex(uri);
		File currentSrc = srcFile;
		while (index != uri.length() - 1) {
			String nextZip = uri.substring(0, index);
			uri = uri.substring(index + 1);
			index = getIndex(uri);
			try {
				destFile = new File(destFile, nextZip);
				currentSrc = prepareNextZip(destFile, currentSrc, nextZip);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			destFile = new File(destFile, uri);
			destFile.getParentFile().mkdirs();
			extract(currentSrc, uri, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File prepareNextZip(File destFile, File currentSrc, String nextZip)
			throws IOException {
		File pooled = query(destFile);
		if (pooled != null && pooled.exists() && pooled.isFile())
			currentSrc = pooled;
		else {
			File f = File.createTempFile("extracted", "_"
					+ new File(nextZip).getName());
			currentSrc = extract(currentSrc, nextZip, f);
			store(destFile, currentSrc);
		}
		return currentSrc;
	}

	private File query(File destFile) {
		return extractedPool.get(destFile);
	}

	private void store(File destFile, File currentSrc) {
		extractedPool.put(destFile, currentSrc);
	}

	private void clear() {
		Collection<File> values = extractedPool.values();
		for (Iterator<File> iterator = values.iterator(); iterator.hasNext();) {
			File f = iterator.next();
			try {
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		extractedPool.clear();
	}

	private File extract(File zip, String src, File dest) throws IOException {
		log("Source: {0}",  zip.getAbsolutePath());
		log("uri: {0}", src);
		log("Dest: {0}", dest.getAbsolutePath());
		ZipFile zf = new ZipFile(zip);
		ZipEntry ze = new ZipEntry(src);
		InputStream is = zf.getInputStream(ze);
		OutputStream os = new FileOutputStream(dest);
		byte[] b = new byte[4096];
		int read = is.read(b);
		while (-1 != read) {
			os.write(b, 0, read);
			read = is.read(b);
		}
		os.flush();
		os.close();
		is.close();
		zip = dest;
		return zip;
	}

	private int getIndex(String uri) {
		int index = uri.length() - 1;
		for (int i = 0; i < keys.length; i++) {
			int iKey = uri.indexOf(keys[i]);
			if (iKey != -1)
				iKey += keys[i].length();
			if (iKey != -1 && iKey < index) {
				index = iKey;
			}
		}
		return index;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uri) {
		URI = uri;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}

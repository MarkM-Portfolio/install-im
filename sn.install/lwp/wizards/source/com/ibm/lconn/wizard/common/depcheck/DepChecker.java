/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common.depcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.commerce.depchecker.engine.CheckResults;
import com.ibm.commerce.depchecker.engine.DepCheck;
import com.ibm.commerce.depchecker.engine.DepCheckException;
import com.ibm.commerce.depchecker.trace.FileTracer;
import com.ibm.commerce.depchecker.trace.PayManagerFormatter;
import com.ibm.commerce.depchecker.trace.TraceController;
import com.ibm.commerce.depchecker.trace.Tracer;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class DepChecker {
	public static final Logger logger = LogUtil.getLogger(DepChecker.class);
	
	public static final String FS = System.getProperty("file.separator");
	public static final String PRODUCT_DB = "DB"; //$NON-NLS-1$
	public static final String PRODUCT_TDI = "TDI"; //$NON-NLS-1$
	private static final String NAME = "depcheck"; //$NON-NLS-1$
	private static final String TOOLKIT = "com.ibm.commerce.depchecker.engine.toolkit.ITJToolkitInterface"; //$NON-NLS-1$
	private static final String RULEFILE_PREFIX = "depCheck"; //$NON-NLS-1$
	private static TraceController d_traceController = null;

	static {
		initializeTracing();
	}

	private String type, platform;

	private static void initializeTracing() {
		if (d_traceController == null) {
			d_traceController = new TraceController();
		}
		int iter = 0;
		String traceFileBase = System.getProperty("java.io.tmpdir");
		String traceFileName = NAME + "_" + iter + ".trace";
		File traceFile;
		for (traceFile = new File(traceFileBase, traceFileName); traceFile
				.exists(); traceFile = new File(traceFileName)) {
			iter++;
			traceFileName = traceFileBase + "_" + iter + ".trace";
		}

		String traceFilePath = traceFile.getAbsolutePath();
		d_traceController.registerListener(new FileTracer(traceFilePath,
				new PayManagerFormatter()));
	}

	public DepChecker(String type, String platform) {
		if (!PRODUCT_DB.equals(type)) {
			throw new IllegalArgumentException("Unsupported product type");
		}
		this.type = type;
		this.platform = platform;
	}

	private String getRuleFileName() {
		StringBuffer sb = new StringBuffer(RULEFILE_PREFIX);
		sb.append(type);
		if (Constants.OS_WINDOWS.equals(platform)) {
			sb.append("WIN");
		} else if (Constants.OS_LINUX_REDHAT.equals(platform)
				|| Constants.OS_LINUX_SUSE.equals(platform)) {
			sb.append("LNX");
		} else if (Constants.OS_AIX.equals(platform)) {
			sb.append("AIX");
		}
		sb.append(".ini");
		
		logger.log(Level.FINER, "common.finer.rulefile_name", new String[]{sb.toString()});
		return sb.toString();
	}

	public Map<String, ProductInfo> check()  {
		/*
		Map<String, ProductInfo> map = new HashMap<String, ProductInfo>();
		Tracer tracer = new Tracer(d_traceController);
		DepCheck dc = null;
		CheckResults cr = null;
		try {
			dc = new DepCheck(new FileInputStream(NAME + FS
					+ getRuleFileName()), tracer, TOOLKIT);		
			cr = dc.check();
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "common.severe.rulefile_not_found", e);
		} catch (DepCheckException e) {
			logger.log(Level.SEVERE, "common.severe.depcheck_error", e);
		}
		if(cr != null) {
			String[] products = cr.getProducts();
			for (int i = 0; i < products.length; i++) {
				Properties p = cr.getProductProperties(products[i]);
				if(Boolean.valueOf(p.getProperty("installed"))) {
					ProductInfo pi = new ProductInfo();
					pi.setVersion(p.getProperty("version"));
					if (products[i].startsWith("SQL")){
						pi.setName(Constants.DB_SQLSERVER);
						pi.setInstallLoc(process64(p.getProperty("location")));
						map.put(Constants.DB_SQLSERVER, pi);
					}else{
						pi.setName(products[i]);
						pi.setInstallLoc(p.getProperty("location"));
						map.put(products[i].toLowerCase(), pi);
					}					
					logger.log(Level.INFO, "common.info.detectd_product", 
							new String[] {products[i], p.getProperty("location")});
					map.put(products[i].toLowerCase(), pi);
				}
			}
		}	
		logger.log(Level.SEVERE, "Database Info: " + map);
		return map;
		*/
		Map<String, ProductInfo> map = null;
		try {
			map = DepCheckByPrereqScanner.check();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "code file for Prereq Scanner not Found", e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "the Prereq Scanner can't be launched", e);
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			logger.log(Level.SEVERE, "probably has 32bit DB2 detected, please install the 64bit version", e);
			e.printStackTrace();
		}
		logger.log(Level.SEVERE, "Database Info: " + map);
		return map;
	}
	private String process64(String location){
		String str = "(x86)";
		if (location.contains(str)){
			int loc = location.indexOf(str);
			return location.substring(0, loc - 1).concat(location.substring(loc+5, location.length()));	 
		}
		return location;
	
	}
	
	
}

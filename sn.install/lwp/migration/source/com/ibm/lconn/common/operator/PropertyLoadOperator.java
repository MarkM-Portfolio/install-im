/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.operator;

import java.io.FileInputStream;
import java.util.Properties;

import com.ibm.lconn.common.file.FileUtil;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.Spliter;
import com.ibm.lconn.common.util.StringResolver;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class PropertyLoadOperator extends LogOperator {
	private String prefix;
	private String postfix;
	private String fileLocation;

	@Override
	public boolean execute(String para) {
		Spliter spliter = new Spliter(para);
		String header = spliter.getHeader();
		if(ObjectUtil.isDigit(header)){
			int paraCount = Integer.parseInt(header);
			spliter.takeTail();
			if(paraCount-->0) prefix = spliter.takeTail().getHeader();
			if(paraCount-->0) postfix = spliter.takeTail().getHeader();
			spliter.takeTail();
		}
		fileLocation = spliter.getSource(); 
		
		if(prefix!=null) prefix = StringResolver.resolveMacro(prefix, getMacroProperties());
		if(postfix!=null) postfix = StringResolver.resolveMacro(postfix, getMacroProperties());
		fileLocation = StringResolver.resolveMacro(fileLocation, getMacroProperties());
		
		return execute();
	}

	public boolean execute() {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(fileLocation));
		} catch (Exception e) {
			try {
				p.load(getClass().getResourceAsStream(fileLocation));
			} catch (Exception e2) {
				log("Load property file {0} failed.", fileLocation);
				return false;
			}
		}
		ObjectUtil.copyProperties(p, getMacroProperties(), prefix, postfix);
		log("Load property file {0} successfully.", FileUtil.getAbsoluteFile(
				fileLocation).getAbsolutePath());
		
		return true;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
}

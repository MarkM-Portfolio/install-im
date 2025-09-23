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
import java.io.IOException;
import java.io.InputStream;

import com.ibm.lconn.common.util.Spliter;
import com.ibm.lconn.common.util.StringResolver;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class FilterExecOperator extends LogOperator {

	private String filterPath;

	@Override
	public boolean execute(String para) {
		Spliter spliter = new Spliter(para);
		String path = spliter.getSource();
		path = StringResolver.resolveMacro(path, getMacroProperties());
		this.filterPath = path;
		return execute();
	}

	public boolean execute() {
		InputStream resourceAsStream = getClass().getResourceAsStream(filterPath);
		if(resourceAsStream==null){
			try{
				resourceAsStream = new FileInputStream(filterPath);
			}catch (Exception e) {
			}
		}
		if(resourceAsStream!=null){
			OperatorFactory of = new OperatorFactory();
			of.setOutput(getOutput());
			of.setMacroProperties(getMacroProperties());
			try {
				of.execute(resourceAsStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public String getFilterPath() {
		return filterPath;
	}

	public void setFilterPath(String filterPath) {
		this.filterPath = filterPath;
	}

//	public static void main(String[] args) {
//		FilterExecOperator feo = new FilterExecOperator();
//		feo.execute("/co");
//	}
}

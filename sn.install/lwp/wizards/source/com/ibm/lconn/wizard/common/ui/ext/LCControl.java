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
package com.ibm.lconn.wizard.common.ui.ext;

import org.eclipse.swt.SWT;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class LCControl {
	public static final int VIEW_INFORCENTER = 1;
	public static final int VIEW_LOG = 2;
	private int type;
	private String label;
	private String executable;
	private int alignment = SWT.LEFT;
	
	public LCControl(int type, String label, String exec, int alignment) {
		this.type = type;
		this.label = label;
		this.executable = exec;
		this.alignment = alignment;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the executable
	 */
	public String getExecutable() {
		return executable;
	}

	/**
	 * @return the alignment
	 */
	public int getAlignment() {
		return alignment;
	}

}

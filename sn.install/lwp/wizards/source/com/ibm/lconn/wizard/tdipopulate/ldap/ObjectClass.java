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
package com.ibm.lconn.wizard.tdipopulate.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectClass {

	private String name;

	private String desc;

	private List<String> attrbutes = new ArrayList<String>();

	private boolean sorted = true;
	
	public ObjectClass(String name) {
		this.name = name;
	}
	
	public ObjectClass() { }

	public List<String> getAttrbutes() {
		if(!sorted) {
			Collections.sort(attrbutes);
			sorted = true;
		}
		return attrbutes;
	}

	public void addAttrbute(String att) {
		attrbutes.add(att);
		sorted = false;
	}

	public void removeAttrbute(String att) {
		attrbutes.remove(att);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

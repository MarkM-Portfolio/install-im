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
package com.ibm.lconn.wizard.common.ui.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;

import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author Jun Jing Zhang
 *
 */
public class WizardTreeData {
	private String id;
	private HashMap<String, DataPair> treeItemMap;
	private List<String> selectedValues;
	private DataPair root = new DataPair(null, null, null);
	int style;
	
	/**
	 * @return the style
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(int style) {
		this.style = style;
	}

	public WizardTreeData(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	public void addTreeItem(String parentValue, String label, String value){
		DataPair parent = getItem(parentValue);
		DataPair child = new DataPair(parent, label, value);
		this.treeItemMap.put(value, child);
		if(parent==null) root.addChild(child);
	}
	
	public void select(String value){
		if(selectedValues==null) selectedValues = new ArrayList<String>();
		if(CommonHelper.isStyle(getStyle(), SWT.MULTI)){
			selectedValues.clear();
		}
		selectedValues.remove(value);
		selectedValues.add(value);
	}
	
	public List<String> getSelectedValues(){
		return this.selectedValues;
	}
	
	private DataPair getItem(String itemValue) {
		if(treeItemMap==null) return null;
		return treeItemMap.get(itemValue);
	}
	
	public String getLabel(String value){
		DataPair item = getItem(value);
		if(item!=null) return item.label;
		return null;
	}
	
	public List<String> getRoots(){
		return output(root.children);
	}
	
	public List<String> getChildren(String itemValue){
		DataPair item = getItem(itemValue);
		if(item==null) return new ArrayList<String>();
		return output(item.children);
	}
	
	private List<String> output(List<DataPair> children) {
		ArrayList<String> out = new ArrayList<String>();
		if(children==null) {
			return out;
		}
		for (DataPair dataPair : children) {
			out.add(dataPair.value);
		}
		return out;
	}
	public boolean remove(String value){
		if(treeItemMap==null) return false;
		DataPair item = getItem(value);
		treeItemMap.remove(value);
		if(item!=null) item.removeFromParent();
		return item!=null;
	}

	private class DataPair{
		String label,value;
		DataPair parent;
		List<DataPair> children;
		DataPair(DataPair parent, String label, String value){
			this.parent = parent;
			this.label = label;
			this.value = value;
			if(parent!=null) parent.addChild(this);
		}
		
		public void removeFromParent() {
			if(parent!=null)
				parent.children.remove(this);
		}

		void addChild(DataPair pair){
			if(children==null){
				children = new ArrayList<DataPair>();
			}
			children.add(pair);
		}
		
		boolean hasChild(){
			return children != null;
		}
		
		public boolean equals(Object o){
			if( o instanceof DataPair ){
				DataPair ot = (DataPair) o;
				return CommonHelper.equals(ot.value, this.value);
			}
			return false;
		}
	}
}


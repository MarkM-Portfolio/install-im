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

import java.util.Properties;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class WizardPageInputData {
	private String type, label, id, wizardId, defaultValue, className, name, tooltip;
	private Properties props = new Properties();
	String[] options;
	private int style;
	private String bgColor;
	public WizardPageInputData(String wizardId, String id){
		this.wizardId = wizardId;
		this.id = id;
		DefaultWizardDataLoader.initWizardPageInput(this);
	}
	
	public boolean isLabel(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_LABEL);
	}
	
	public boolean isRadio(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_RADIO);
	}
	public boolean isText(){
		return isStyledText() || isPassword() || CommonHelper.equals(this.type, Constants.WIDGET_TYPE_TEXT);
	}
	public boolean isCheck(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_CHECK);
	}
	public boolean isCheckGroup(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_CHECKGROUP);
	}
	
	public boolean isDirBrowser(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_DIR_BROWSER)
		|| isFileBrowser();
	}
	
	public boolean isFileBrowser(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_FILE_BROWSER);
	}
	
	public boolean isPassword(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_PASSWORD);
	}
	
	public void setValue(String key, String value){
		props.setProperty(key, value);
	}
	
	public String getValue(String key){
		return props.getProperty(key);
	}
	
	
	public boolean isGroup(){
		return isRadio()||isCheckGroup();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public String getWizardId() {
		return wizardId;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isTable() {
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_TABLE);
	}

	public boolean isTextArea() {
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_TEXTAREA);
	}
	
	public boolean isStyledText(){
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_STYLEDTEXT);
	}

	public boolean isDropDown() {
		return CommonHelper.equals(this.type, Constants.WIDGET_TYPE_DROPDOWN);
	}

	public void setStyle(int parseStyle) {
		this.style = parseStyle;
	}

	public int getStyle() {
		return style;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getBgColor() {
		return bgColor;
	}
}

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.common.ui.ext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.LCGroupController;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCFileBrowserCheckGroup extends LCGroup {
	private  Map<String, LCFileBrowser> map;
	
	@SuppressWarnings("unchecked")
	public LCFileBrowserCheckGroup(Composite parent, WizardPageInputData data) {
		super(parent, data, SWT.CHECK);
		map = (Map<String, LCFileBrowser>)DataPool.getComplexData(data.getWizardId(), data.getId() + ".browserMap");
	}

	
	@Override
	public void createControl(Composite parent) {
		if(map==null) {
			map = new HashMap<String, LCFileBrowser>();
		}
		
		WizardPageInputData data = getData();
		Assert.isTrue(data.isGroup());
		group = new Group(parent, getGroupStyle());
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		group.setLayout(new GridLayout(2, false));
		String[] options = data.getOptions();
		for (String optionId : options) {
			final Button option = new Button(group, getButtonStyle());
			final String id  = optionId;
			String optionText = Messages.getString("LABEL." + optionId);
			option.setText(optionText);
			GridData gd = new GridData();
			gd.horizontalSpan=2;
			option.setLayoutData(gd);
			
			option.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					boolean selected = ((Button)e.widget).getSelection();
					map.get(id).setEnable(selected);
				}
				public void widgetSelected(SelectionEvent e) {
					boolean selected = ((Button)e.widget).getSelection();
					map.get(id).setEnable(selected);
				}				
			});
			
			String newId = data.getId() + "." + optionId +".path";

			WizardPageInputData fbData = new WizardPageInputData(data.getWizardId(), newId);
			fbData.setValue(Constants.WIDGET_PROP_FILE_BROWSER_EXT_FILTER, "*.csv");
			LCFileBrowser lcfb = new LCFileBrowser(group, fbData);
			lcfb.setEnable(false);
			
			String defaultValue = DataPool.getValue(fbData.getWizardId(), newId);
			if(defaultValue == null || Constants.TEXT_EMPTY_STRING.equals(defaultValue)) {
				File csvFile = new File(Constants.TDI_WORK_DIR + Constants.FS + Constants.optionalTaskFileMap.get(optionId));
				DataPool.setValue(fbData.getWizardId(), 
					newId, 
					csvFile.getAbsolutePath());
			}
			lcfb.updateData();
			map.put(optionId, lcfb);			
		}
		
		DataPool.setComplexData(data.getWizardId(), data.getId() + ".browserMap", map);
		
	}
	
	public void updateData() {
		Control[] children = group.getChildren();
		WizardPageInputData data = getData();
		String value = DataPool.getValue(data.getWizardId(), data.getId());
		String[] options = data.getOptions();
		boolean selected = false;
		for (int i = 0, j=0; i < children.length; i++) {			
			if(! (children[i] instanceof Button)) {
				continue;
			}
			String optionId = options[j];
			Button button = (Button) children[i];
			boolean selectionFound = selectionFound(data, value, optionId);
			button.setSelection(selectionFound);
			map.get(optionId).setEnable(selectionFound);
			if (selectionFound) {
				selected = true;
			}
			LCGroupController controller = getController();
			if(controller!=null) button.setEnabled(controller.enable(optionId));
			
			map.get(optionId).updateData();
			
			j++;
		}
		boolean isCheck = CommonHelper.isStyle(getStyle(), SWT.CHECK);
		if(!selected && !isCheck) {
			Button firstButton= (Button) group.getChildren()[0];
			firstButton.setSelection(true);
		}
	}
	
	@Override
	public String getValue() {
		Control[] children = group.getChildren();
		boolean isCheck = CommonHelper.isStyle(getStyle(), SWT.CHECK);
		String value = null;
		for (int i = 0, j=0; i < children.length; i++) {
			if(! (children[i] instanceof Button)) {
				continue;
			}
			Button child = (Button) children[i];
			if (child.getSelection()) {
				String val = getData().getOptions()[j];
				if (isCheck) {
					if (value == null)
						value = val;
					else
						value += "," + val;
				} else {
					value = val;
					break;
				}
			}
			
			// the LCFileBrowser value
			String optionId = getData().getOptions()[j];
			String filepath = map.get(optionId).getValue();
			map.get(optionId).setDataPoolValue(filepath);
			
			j++;
		}
		if (value == null)
			value = Constants.TEXT_EMPTY_STRING;
		return value;
	}

}

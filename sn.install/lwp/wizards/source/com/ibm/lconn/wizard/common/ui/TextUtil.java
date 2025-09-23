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
package com.ibm.lconn.wizard.common.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.msg.Messages;

public class TextUtil {
	private static final String ID_TEXT = "id_text";
	private static final Object FALSE = "false";

	public static Composite createTextInput(Composite parent, String[]id, DataPool valuePool, Listener listener){
		Composite com = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		com.setLayout(gridLayout);
		
		com.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		for (int i = 0; i < id.length; i++) {
			createTextInput(id[i], com, valuePool);
		}		
		return com;
	}

	private static void createTextInput(String id, Composite parent,
			DataPool valuePool) {
		final Label dbNameLabel = new Label(parent, SWT.NONE);
		dbNameLabel.setText(Messages.getString(id+".label")); 

		String defaultText = Messages.getString(id+".defaultText");
		
		Text dbNameText = TextHelper.newAutoValidationText(parent, SWT.BORDER);
		dbNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		dbNameText.setEditable(FALSE.equals(Messages.getString(id+".enable")));
		dbNameText.setData(ID_TEXT, id);
		if(defaultText!=null) dbNameText.setText(defaultText);
	}
}

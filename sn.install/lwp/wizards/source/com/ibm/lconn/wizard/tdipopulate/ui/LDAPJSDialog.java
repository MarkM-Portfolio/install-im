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
package com.ibm.lconn.wizard.tdipopulate.ui;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.msg.MessagePopup;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.LCDialog;
import com.ibm.lconn.wizard.common.validator.IBMJSValidator;
import com.ibm.lconn.wizard.tdipopulate.js.Function;
import com.ibm.lconn.wizard.tdipopulate.js.FunctionOperator;

public class LDAPJSDialog extends LCDialog {

	private Text text;
	private Tree tree;
	private TreeItem editingItem;
	private TreeEditor editor;
	private Text editingItemNameText;
	private String selectedJS;

	// private boolean editing;

	/**
	 * Create the dialog
	 * 
	 * @param parentShell
	 */
	public LDAPJSDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Label descLabel = new Label(composite, SWT.NONE);
		descLabel.setText(MessageUtil.getMsg("LDAPJSDialog.description"));
		descLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final SashForm sashForm = new SashForm(composite, SWT.NONE);

		final Composite composite_2 = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		composite_2.setLayout(gridLayout);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;

		Label label = new Label(composite_2, SWT.NONE);
		label.setText(Messages.getString("LDAPJSDialog.functionListLabel")); //$NON-NLS-1$

		tree = new Tree(composite_2, SWT.FOCUSED | SWT.BORDER | SWT.H_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		editor = new TreeEditor(tree);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		Composite buttonCom = new Composite(composite_2, SWT.NONE);
		CommonHelper.setColumn(buttonCom, 2);
		final Button addFunctionButton = new Button(buttonCom, SWT.NONE);
		addFunctionButton.setText(Messages
				.getString("LDAPJSDialog.addFunctionButtonText")); //$NON-NLS-1$
		final Button removeFunctionButton = new Button(buttonCom, SWT.NONE);
		removeFunctionButton.setText(Messages
				.getString("LDAPJSDialog.removeFunctionButtonText")); //$NON-NLS-1$
		GridData gridData = new GridData();
		gridData.horizontalIndent = 10;
		removeFunctionButton.setLayoutData(gridData);
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData2.verticalIndent = 10;
		sashForm.setLayoutData(gridData2);

		final Composite composite_1 = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout();
		composite_1.setLayout(gridLayout2);
		gridLayout2.marginHeight = 0;
		gridLayout2.marginWidth = 0;

		final Label functionBodyLabel = new Label(composite_1, SWT.NONE);
		functionBodyLabel.setText(Messages
				.getString("LDAPJSDialog.functionBodyLabel")); //$NON-NLS-1$
		text = new Text(composite_1, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Button applyButton = new Button(composite_1, SWT.RIGHT);
		applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		applyButton.setAlignment(SWT.RIGHT);
		applyButton.setText(Messages.getString("LDAPJSDialog.applyButtonText")); //$NON-NLS-1$

		sashForm.setWeights(new int[] { 1, 2 });

		addData(tree);
		setDefault();
		applyButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				applyEdit();
			}

		});

		tree.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (event.item instanceof TreeItem) {
					TreeItem ti = (TreeItem) event.item;
					Object function = ti.getData();
					if (function == null) {
					} else {
						text.setText(((Function) function).getBody());
					}
				}
			}

		});

		addFunctionButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				addFunction();
			}
		});
		removeFunctionButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				removeSelectedFunction();
			}
		});
		return composite;
	}

	private void addFunction() {
		final TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setData(new Function("", ""));
		editItemName(item);
	}

	private void editItemName(TreeItem item) {
		setSelectedItem(item);
		if (editingItemNameText != null && !editingItemNameText.isDisposed()) {
			item.setText(editingItemNameText.getText());
			editingItemNameText.dispose();
		}
		editingItemNameText = new Text(tree, SWT.BORDER);
		editingItem = item;
		editingItemNameText.setText(item.getText());
		editingItemNameText.selectAll();
		editingItemNameText.setFocus();
		editor.setEditor(editingItemNameText, editingItem);
		editingItemNameText.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				focusOut();
			}
		});

		editingItemNameText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {

				switch (event.keyCode) {
				case SWT.CR:
					focusOut();
					break;
				case SWT.ESC:
					cancelAddingFunction();
					break;
				// default:
				// editing = true;
				}

			}
		});

	}

	private void setDefault() {
		String value = DataPool.getValue(Constants.WIZARD_ID_TDIPOPULATE,
				LDAPMappingTable.SELECTED_JS);
		value = selectedJS;
		TreeItem[] items = tree.getItems();
		for (TreeItem ti : items) {
			if (CommonHelper.equals(ti.getText(), value)) {
				setSelectedItem(ti);
				return;
			}
		}
		if (tree.getSelectionCount() == 0) {
			setSelectedItem(tree.getItem(0));
		}
	}

	private void setSelectedItem(TreeItem ti) {
		tree.setSelection(ti);
		Function func = (Function) ti.getData();
		if (func != null)
			text.setText(func.getBody());
		else
			text.setText("");
	}

	protected void okPressed() {
		this.selectedJS = tree.getSelection()[0].getText();
		super.okPressed();
	}

	private void addData(Tree list2) {
		for (String s : FunctionOperator.getNameList()) {
			TreeItem ti = new TreeItem(tree, SWT.NONE);
			ti.setText(s);
			ti.setData(FunctionOperator.getFunction(s));
			ti.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					TreeItem ti = (TreeItem) event.data;
					text.setText(((Function) ti.getData()).getBody());
				}
			});
		}
	}

	public String getSelectedFunction() {
		return this.selectedJS;
	}

	/**
	 * Create contents of the button bar
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("button.OK.text"),
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Messages.getString("button.cancel.text"), false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(640, 480);
	}

	public static void main(String[] args) {
		new Display();
		LDAPJSDialog d = new LDAPJSDialog(null);
		d.setSelectedJS("function_map_from_objectGUID"); //$NON-NLS-1$
		d.open();

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("LDAPJSDialog.title")); //$NON-NLS-1$
	}

	private boolean cancelAddingFunction() {
		boolean result = editingItemNameText != null || editingItem != null;
		if (editingItemNameText != null)
			editingItemNameText.dispose();
		if (editingItem != null) {
			editingItem.dispose();
			editingItem = null;
		}
//		if (this.text != null)
//			this.text.setText("");
		return result;
	}

	public void setSelectedJS(String text2) {
		this.selectedJS = text2;
	}

	private void applyEdit() {
		TreeItem ti = tree.getSelection()[0];
		ti.getText();
		String functionName = ti.getText();
		String functionBody = text.getText();

		IBMJSValidator validator = new IBMJSValidator(functionBody);
		Properties props = new Properties();
		props.setProperty("plusplus", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		props.setProperty("undef", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		if (0 == validator.validate(false)) {// Add validator
			List<String> existNames = FunctionOperator.getNameList();
			String[] existNameAry = new String[existNames.size()];
			existNameAry = existNames.toArray(existNameAry);
			List<String> addedFunctions = FunctionOperator
					.addFunction(functionBody);
			String targetFunction = tree.getSelection()[0].getText();
			for (int i = 0; i < addedFunctions.size(); i++) {
				String added = addedFunctions.get(i);
				if (!added.equalsIgnoreCase(targetFunction) && Util.indexOf(existNameAry, added) != -1) {
					MessagePopup.showErrorMessage(this, 
							MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE),
							MessageUtil.getMsgWithParameter("LDAPJSDialog.function.invalid.exist.message", added) );
				}
				return;
			}
			if (-1 == addedFunctions.indexOf(functionName)) {
				MessagePopup.showErrorMessage(this, 
						MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE),
						MessageUtil.getMsgWithParameter("LDAPJSDialog.noSuchFunction.message", functionName));
				return;
			} else {
				Function f = FunctionOperator.getFunction(functionName);
				ti.setData(f);
				this.editingItem = null;
				this.editingItemNameText = null;
				FunctionOperator.reWrite();
				this.getButton(IDialogConstants.OK_ID).setFocus();
				
			}
		} else {
			MessageDialog.openError(getShell(), MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE), validator
					.getMessage());
			text.setFocus();
		}
		

	}

	private void removeSelectedFunction() {
		if (tree.getSelectionCount() > 0) {
			TreeItem ti = tree.getSelection()[0];
			String functionName = ti.getText();

			if (IDialogConstants.OK_ID==MessagePopup.showQuestionMessage(this, 
					MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE), 
					MessageUtil.getMsgWithParameter("LDAPJSDialog.function.remove.confirm.message", functionName))) {
				FunctionOperator.deleteFunction(functionName);
				FunctionOperator.reWrite();
				ti.dispose();
			} else {
				return;
			}
		}

	}

	private void focusOut() {
		if (editingItemNameText != null) {
			String funcName = editingItemNameText.getText();
			if (!validFunctionName(funcName)) {
				MessagePopup.showErrorMessage(this, 
						MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE), 
						MessageUtil.getMsgWithParameter("LDAPJSDialog.function.invalid.name.message", funcName));
				editItemName(editingItem);
			} else if (FunctionOperator.getFunction(funcName) != null) {
				MessagePopup.showErrorMessage(this, 
						MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE), 
						MessageUtil.getMsgWithParameter("LDAPJSDialog.function.invalid.exist.message", funcName));
				editItemName(editingItem);
			} else {
				editingItem.setText(funcName);
				editingItemNameText.dispose();
				editingItemNameText = null;
				String funcBody = "function " + editingItem.getText()
						+ "(arg){\n\t\n}";
				FunctionOperator.addFunction(funcBody);
				FunctionOperator.reWrite();
				editingItem.setData(new Function(funcName, funcBody));
				setSelectedItem(editingItem);
			}
		}
		// editing = false;

	}

	private boolean validFunctionName(String funcName) {
		if (funcName != null) {
			return funcName.matches("[a-zA-Z_$][0-9a-zA-Z_$]*");
		}
		return false;
	}

}

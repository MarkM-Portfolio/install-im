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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.LCDialog;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.tdipopulate.ldap.ObjectClass;

public class LDAPAttributesDialog extends LCDialog {

	private Tree tree;
	private String selectedAttri;
	private TreeItem firstAttriItem;
	private TreeItem root;
	private boolean selected;
	
	private static Image OBJCLASS_EXPANDED_ICON, OBJCLASS_COLAPSED_ICON, OBJCLASS_ATTRIBUTE_ICON;

	/**
	 * Create the dialog
	 * 
	 * @param parentShell
	 */
	public LDAPAttributesDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.getString("LDAPAttributesDialog.description"));
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		tree = new Tree(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tree.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (event.item instanceof TreeItem) {
					TreeItem ti = (TreeItem) event.item;
					updateOKButton(ti);
				} 
			}
		});
		
		tree.addTreeListener(new TreeListener() {
			public void treeCollapsed(TreeEvent event) {
				if (event.item instanceof TreeItem) {
					TreeItem ti = (TreeItem) event.item;
					ti.setImage(getCollapsedIcon());
				} 
			}

			public void treeExpanded(TreeEvent event) {
				if (event.item instanceof TreeItem) {
					TreeItem ti = (TreeItem) event.item;
					ti.setImage(getExpandedIcon());
				} 
			}			
		});
		
		tree.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				TreeItem[] selection = tree.getSelection();
				if (selection == null || selection.length == 0)
					return;
				switch (e.keyCode) {
				case SWT.ARROW_RIGHT:
					selection[0].setExpanded(true);
					selection[0].setImage(getExpandedIcon());
					tree.setSelection(selection[0]);
					e.doit = false;
					break;
				case SWT.ARROW_LEFT:
					selection[0].setExpanded(false);
					selection[0].setImage(getCollapsedIcon());
					tree.setSelection(selection[0]);
					e.doit = false;
					break;
				default:
					break;
				}
			}
		});
		loadAttributes();
		getShell().setImage(ResourcePool.getWizardTitleIcon());
		return container;
	}

	public int open() {
		return super.open();
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private void loadAttributes() {
		String selectedAttri = getSelectedAttri();
		root = new TreeItem(tree, SWT.NONE);
		root.setText(Messages.getString("LDAPAttributesDialog.rootItemText")); //$NON-NLS-1$

		markUnselectable(root);
		List<ObjectClass> classes = (List<ObjectClass>) DataPool
				.getComplexData(Constants.WIZARD_ID_TDIPOPULATE,
						Constants.TDI_LDAP_OBJECTCLASSES);
		if (classes == null)
			return;
		firstAttriItem = null;
		selected = false;
		updateOKButton(null);
		for (ObjectClass objectClass : classes) {
			TreeItem ti = addTreeItem(root, objectClass.getName());
			markUnselectable(ti);
			List<String> attrbutes = objectClass.getAttrbutes();
			ti.setExpanded(false);
			ti.setImage(getCollapsedIcon());
			for (String attri : attrbutes) {
				TreeItem item = addTreeItem(ti, attri);
				if (!selected && attri != null
						&& attri.equalsIgnoreCase(selectedAttri)) {
					tree.setSelection(item);
					ti.setExpanded(true);
					ti.setImage(getExpandedIcon());
					selected = true;
					Button okButton = getButton(IDialogConstants.OK_ID);
					if (okButton != null)
						okButton.setEnabled(true);
				}
				if (firstAttriItem == null)
					firstAttriItem = item;
			}
			// ti.setExpanded(false);
		}
		root.setExpanded(true);
		root.setImage(getExpandedIcon());
		// selectFirst();
	}

	private void markUnselectable(TreeItem root) {
		root.setData(Constants.BOOL_TRUE);
	}

	// private void selectFirst() {
	// if (firstAttriItem != null && tree.getSelectionCount() == 0) {
	// tree.setSelection(firstAttriItem);
	// }
	// }

	private TreeItem addTreeItem(TreeItem tree, String string) {
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText(string);
		item.setImage(getAttributeIcon());
		return item;
	}

	/**
	 * Create contents of the button bar
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID,
				Messages.getString("button.OK.text"), true);
		okButton.setEnabled(selected);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Messages.getString("button.cancel.text"), false);
	}

	protected void okPressed() {
		this.selectedAttri = tree.getSelection()[0].getText();
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(350, 400);
	}

	public static void main(String[] args) {
		new Display();
		new Shell();
		TestDataOffer.setLDAPAttributeList();
		LDAPAttributesDialog d = new LDAPAttributesDialog(null);
		// d.setSelectedAttri("A1");
		d.open();

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("LDAPAttributesDialog.title")); //$NON-NLS-1$
	}

	public String getSelectedAttri() {
		return selectedAttri;
	}

	public void setSelectedAttri(String selectedAttri) {
		this.selectedAttri = selectedAttri;
	}

	private boolean isUnSelectable(TreeItem ti) {
		return CommonHelper.equals(ti.getData(), Constants.BOOL_TRUE);
	}

	private void updateOKButton(TreeItem ti) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(ti != null && !isUnSelectable(ti));
	}
	
	private Image getCollapsedIcon() {
		if(OBJCLASS_COLAPSED_ICON == null) {
			OBJCLASS_COLAPSED_ICON = ImageDescriptor.createFromFile(LDAPAttributesDialog.class,
					"/icons/tree_collapsed.gif").createImage();
		}
		return OBJCLASS_COLAPSED_ICON;
	}
	
	private Image getExpandedIcon() {
		if(OBJCLASS_EXPANDED_ICON == null) {
			OBJCLASS_EXPANDED_ICON = ImageDescriptor.createFromFile(LDAPAttributesDialog.class,
					"/icons/tree_expanded.gif").createImage();
		}
		return OBJCLASS_EXPANDED_ICON;
	}
	
	private Image getAttributeIcon() {
		if(OBJCLASS_ATTRIBUTE_ICON == null) {
			OBJCLASS_ATTRIBUTE_ICON = ImageDescriptor.createFromFile(LDAPAttributesDialog.class,
					"/icons/tree_item.gif").createImage();
		}
		return OBJCLASS_ATTRIBUTE_ICON;
	}
	
}

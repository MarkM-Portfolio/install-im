/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class InputWASInfoDialog extends Dialog {
	private Text wasLocText;
	private Text dmProfileLocText;
	private Text dmProfileNameText;
	private Text cellNameText;
	private Text nodeNameText;
	private Text hostNameText;
	private TableViewer tv;
	private List nodesList = new ArrayList();

	private String wasLoc;
	private String dmProfileLoc;
	private String dmProfileName;
	private String cellName;
	private String nodeNames;
	private String hostNames;

	private final ILogger log = IMLogger
			.getLogger(com.ibm.connections.install.WasPanel.class);

	public String getWasLoc() {
		return wasLoc;
	}

	public String getDmProfileLoc() {
		return dmProfileLoc;
	}

	public String getDmProfileName() {
		return dmProfileName;
	}

	public String getCellName() {
		return cellName;
	}

	public String getNodeNames() {
		return nodeNames;
	}

	public String getHostNames() {
		return hostNames;
	}

	public List getNodesList() {
		return nodesList;
	}

	public void setNodesList(List nodesList) {
		this.nodesList = nodesList;
	}

	public void setWasLoc(String wasLoc) {
		this.wasLoc = wasLoc;
	}

	public void setDmProfileLoc(String dmProfileLoc) {
		this.dmProfileLoc = dmProfileLoc;
	}

	public void setDmProfileName(String dmProfileName) {
		this.dmProfileName = dmProfileName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public void setNodeNames(String nodeNames) {
		this.nodeNames = nodeNames;
	}

	public void setHostNames(String hostNames) {
		this.hostNames = hostNames;
	}
	
	protected InputWASInfoDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		// FillLayout fillLayout = new FillLayout();
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		container.setLayout(gridLayout);
		container.setBackground(new Color(null, 255, 255, 255));

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		// ScrolledForm form = toolkit.createScrolledForm(parent);

		Section wasInfoSection = toolkit.createSection(container,
				Section.TITLE_BAR);
		wasInfoSection.setText(Messages.INPUT_WAS_INFO_SECTION); //$NON-NLS-1$

		Composite wasInfoContainer = toolkit.createComposite(wasInfoSection);
		GridLayout wasInfoLayout = new GridLayout();
		wasInfoLayout.numColumns = 1;
		wasInfoContainer.setLayout(wasInfoLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		wasInfoContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.verticalIndent = 5;
		inputgridData.widthHint = 248;

		Label wasLocLable = new Label(wasInfoContainer, SWT.NONE);
		wasLocLable.setText(Messages.INPUT_WAS_INFO_WAS_LOC);
		wasLocLable.setLayoutData(inputgridData);
		this.wasLocText = new Text(wasInfoContainer, SWT.BORDER | SWT.SINGLE);
		this.wasLocText.setText(this.wasLoc==null?"":wasLoc);
		this.wasLocText.setLayoutData(inputgridData);
		this.wasLocText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

			}
		});

		Label dmProfileLocLabel = new Label(wasInfoContainer, SWT.NONE);
		dmProfileLocLabel.setText(Messages.INPUT_WAS_INFO_DM_LOC);
		dmProfileLocLabel.setLayoutData(inputgridData);
		this.dmProfileLocText = new Text(wasInfoContainer, SWT.BORDER
				| SWT.SINGLE);
		this.dmProfileLocText.setText(this.dmProfileLoc==null?"":dmProfileLoc);
		this.dmProfileLocText.setLayoutData(inputgridData);
		this.dmProfileLocText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

			}
		});

		Label dmProfileNameLabel = new Label(wasInfoContainer, SWT.NONE);
		dmProfileNameLabel.setText(Messages.INPUT_WAS_INFO_DM_NAME);
		dmProfileNameLabel.setLayoutData(inputgridData);
		this.dmProfileNameText = new Text(wasInfoContainer, SWT.BORDER
				| SWT.SINGLE);
		this.dmProfileNameText.setText(this.dmProfileName==null?"":dmProfileName);
		this.dmProfileNameText.setLayoutData(inputgridData);
		this.dmProfileNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

			}
		});

		Label cellNameLabel = new Label(wasInfoContainer, SWT.NONE);
		cellNameLabel.setText(Messages.INPUT_WAS_INFO_CELL);
		cellNameLabel.setLayoutData(inputgridData);
		this.cellNameText = new Text(wasInfoContainer, SWT.BORDER | SWT.SINGLE);
		this.cellNameText.setText(this.cellName==null?"":cellName);
		this.cellNameText.setLayoutData(inputgridData);
		this.cellNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

			}
		});

		wasInfoSection.setClient(wasInfoContainer);

		/***********************************************/
		Section nodesInfoSection = toolkit.createSection(container,
				Section.TITLE_BAR);
		nodesInfoSection.setText(Messages.INPUT_NODES_INFO_SECTION); //$NON-NLS-1$

		Composite nodesInfoContainer = toolkit
				.createComposite(nodesInfoSection);
		GridLayout nodesInfoLayout = new GridLayout();
		nodesInfoLayout.numColumns = 3;
		nodesInfoContainer.setLayout(nodesInfoLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		nodesInfoContainer.setLayoutData(gd);

		GridData inputgridData2 = new GridData(GridData.BEGINNING);
		//inputgridData2.horizontalSpan = 1;
		inputgridData2.widthHint = 170;

		Label nodeNameLabel = new Label(nodesInfoContainer, SWT.NONE);
		nodeNameLabel.setText(Messages.INPUT_NODES_INFO_NODE_NAME);
		nodeNameLabel.setLayoutData(inputgridData2);
		Label hostNameLabel = new Label(nodesInfoContainer, SWT.NONE);
		hostNameLabel.setText(Messages.INPUT_NODES_INFO_HOST_NAME);
		hostNameLabel.setLayoutData(inputgridData2);
		Label emptyLabel = new Label(nodesInfoContainer, SWT.NONE);
		emptyLabel.setText("");
		emptyLabel.setLayoutData(inputgridData2);

		nodeNameText = new Text(nodesInfoContainer, SWT.BORDER | SWT.SINGLE);
		nodeNameText.setText("");
		this.nodeNameText.setLayoutData(inputgridData2);

		hostNameText = new Text(nodesInfoContainer, SWT.BORDER | SWT.SINGLE);
		this.hostNameText.setText("");
		this.hostNameText.setLayoutData(inputgridData2);
		
		inputgridData2 = new GridData();
		inputgridData2.widthHint = 87;
		Button addButton = new Button(nodesInfoContainer, SWT.PUSH);
		addButton.setText(Messages.INPUT_NODES_INFO_ADD_BUTTON);
		addButton.setLayoutData(inputgridData2);

		Composite treeComposite = new Composite(nodesInfoContainer, SWT.BORDER);
		treeComposite.setLayout(new FillLayout());
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = 350;
		gd.heightHint = 150;
		gd.horizontalSpan = 3;
		treeComposite.setLayoutData(gd);

		tv = new TableViewer(treeComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		Table table = tv.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		layout.addColumnData(new ColumnWeightData(120));
		new TableColumn(table, SWT.NONE).setText(Messages.INPUT_NODES_INFO_NODE_NAME2);
		layout.addColumnData(new ColumnWeightData(120));
		new TableColumn(table, SWT.NONE).setText(Messages.INPUT_NODES_INFO_HOST_NAME2);

		tv.setContentProvider(new TableViewerContentProvider());
		tv.setLabelProvider(new TableViewerLabelProvider());
		if(this.nodeNames != null && this.hostNames != null && this.nodeNames.length()>0 && this.hostNames.length()>0) {
			String nodes[] = this.nodeNames.split(",");
			String hosts[] = this.hostNames.split(",");
			for(int i = 0;i<nodes.length;i++) {
				nodesList.add(new NodeInfo(nodes[i], hosts[i]));
			}
		}
		tv.setInput(nodesList);

		Button removeButton = new Button(nodesInfoContainer, SWT.PUSH);
		removeButton.setText(Messages.INPUT_NODES_INFO_REMOVE_BUTTON);
		GridData gridDataButtonSize = new GridData();
		gridDataButtonSize.widthHint = 87;
		//gridDataButtonSize.heightHint = 30;
		gridDataButtonSize.horizontalSpan = 3;
		removeButton.setLayoutData(gridDataButtonSize);

		addButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				String nodeName = nodeNameText.getText().trim();
				String hostName = hostNameText.getText().trim();
				if (nodeName.length() > 0 && hostName.length() > 0) {
					NodeInfo info = new NodeInfo(nodeName, hostName);
					tv.add(info);
					nodesList.add(info);
					nodeNameText.setText("");
					hostNameText.setText("");
				}
			}
		});

		removeButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection s = (IStructuredSelection) tv
						.getSelection();
				if (!s.isEmpty()) {
					for (Iterator it = s.iterator(); it.hasNext();) {
						NodeInfo info = (NodeInfo) it.next();
						tv.remove(info);
						nodesList.remove(info);
					}
				}
			}
		});

		nodesInfoSection.setClient(nodesInfoContainer);

		return super.createDialogArea(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			wasLoc = wasLocText.getText().trim();
			dmProfileLoc = dmProfileLocText.getText().trim();
			dmProfileName = dmProfileNameText.getText().trim();
			cellName = cellNameText.getText().trim();
			TableItem items[] = tv.getTable().getItems();
			log.info(" items size = " + items.length);
			nodeNames = new String();
			hostNames = new String();
			for (int index = 0; index < items.length; index++) {
				NodeInfo info = (NodeInfo) items[index].getData();
				if (index == 0) {
					nodeNames = info.getNodeName();
					hostNames = info.getHostName();
				} else {
					nodeNames += ("," + info.getNodeName());
					hostNames += ("," + info.getHostName());
				}
			}
		}
		super.buttonPressed(buttonId);
	}
}

class NodeInfo {
	private String nodeName;
	private String hostName;

	public NodeInfo(String nodeName, String hostName) {
		this.nodeName = nodeName;
		this.hostName = hostName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}

class TableViewerContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object element) {
		if (element instanceof List)
			return ((java.util.List) element).toArray();
		else
			return new Object[0];
	}

	public void dispose() {
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}
}

class TableViewerLabelProvider implements ITableLabelProvider {
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object element, int col) {
		NodeInfo info = (NodeInfo) element;
		if (col == 0)
			return info.getNodeName().toString();
		if (col == 1)
			return info.getHostName().toString();
		return null;
	}

	public void addListener(ILabelProviderListener arg0) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}
}

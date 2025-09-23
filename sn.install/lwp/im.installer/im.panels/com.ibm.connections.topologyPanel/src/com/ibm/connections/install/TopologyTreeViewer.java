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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class TopologyTreeViewer {
	private int topologyType;
	private boolean isCombox;
	private String existed_Clusters[] = {};
	private String all_Clusters[] = {};
	private List<TopologyClusterInfo> inputDatas;
	// a default TopologyClusterInfo with current nodes. It is used to compare
	// with the existed clusters, in case the node is changed
	private TopologyClusterInfo defaultDatas;
	private List<TopologyClusterInfo> existedClusterDatas;
	private TreeViewer tv;
	private TopologyPanel depPanel;
	InstallValidator installValidator = new InstallValidator();
	private final ILogger log = IMLogger
			.getLogger(com.ibm.connections.install.TopologyTreeViewer.class);

	public TopologyTreeViewer(TopologyPanel depPanel,
			boolean isCombox) {
		this.isCombox = isCombox;
		this.depPanel = depPanel;
	}

	public void createTreeContent(Composite parent) {
		tv = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION);
		Tree tree = tv.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(false);
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE);
		column.setWidth(100);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.DEPOLOGY_CLUSTER_TABLE_CLUSTER);
		column.setWidth(150);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setWidth(20);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.DEPOLOGY_CLUSTER_TABLE_NODES);
		column.setWidth(150);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.DEPOLOGY_CLUSTER_TABLE_SERVERS);
		column.setWidth(150);

		tv.setColumnProperties(new String[] { "applicatoin", "cluster",
				"nodeSelect", "node", "server" });

		CellEditor[] cellEditor = new CellEditor[5];
		cellEditor[0] = null;
		if (isCombox)
			cellEditor[1] = new TopologyComboBoxCellEditor(tv.getTree(),
					existed_Clusters, SWT.BORDER);
		else
			cellEditor[1] = new TextCellEditor(tv.getTree());
		cellEditor[2] = new CheckboxCellEditor(tv.getTree());
		cellEditor[3] = null;
		cellEditor[4] = new TextCellEditor(tv.getTree());
		tv.setCellEditors(cellEditor);
		tv.setCellModifier(new TopologyTreeCellModifier(this, tv));

		if (isCombox) {
			CCombo combo = (CCombo) cellEditor[1].getControl();
			combo.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
					String inStr = e.text;
					if (inStr.length() > 0) {
						try {
							e.doit = !installValidator
									.containsInvalidChar(inStr)
									&& !installValidator.containsSpace(inStr);
						} catch (Exception ex) {
							showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_WARNING);
						}
					}
				}
			});
		} else {
			Text combo = (Text) cellEditor[1].getControl();
			combo.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
					String inStr = e.text;
					if (inStr.length() > 0) {
						try {
							e.doit = !installValidator
									.containsInvalidChar(inStr)
									&& !installValidator.containsSpace(inStr);
						} catch (Exception ex) {
							showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_WARNING);
						}
					}
				}
			});
		}

		log.debug("isCombox " + isCombox);

		Text text = (Text) cellEditor[4].getControl();
		text.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				String inStr = e.text;
				if (inStr.length() > 0) {
					try {
						e.doit = !installValidator.containsInvalidChar(inStr)
								&& !installValidator.containsSpace(inStr);
					} catch (Exception ex) {
						showErrorMessages(Messages.DEPOLOGY_SERVER_INPUT_WARNING);
					}
				}
			}
		});

		tv.setAutoExpandLevel(2);
		tv.setContentProvider(new TopologyTreeContentProvider());
		tv.setLabelProvider(new TopologyTreeLableProvider(this));
		if (inputDatas == null || inputDatas.size() == 0) {
			List<TopologyClusterInfo> input = DataFactory.createTreeData2();
			tv.setInput(input);
		} else
			tv.setInput(inputDatas);

		updateTreeItems();

		tree.addTreeListener(new TreeListener() {

			public void treeCollapsed(TreeEvent e) {
				TreeItem item = (TreeItem) e.item;
				TopologyClusterInfo tc = (TopologyClusterInfo) item.getData();
				if (tc.getIsFirstOne()) {
					String node = tc.getNodeName() + "...";
					String server = tc.getServerName() + "...";
					tc.setNodeName(node);
					tc.setServerName(server);
				}
				Color forgroundBlackColor = new Color(null, 0, 0, 0);
				item.setForeground(0, forgroundBlackColor);
				item.setForeground(1, forgroundBlackColor);
				item.setForeground(3, forgroundBlackColor);
				item.setForeground(4, forgroundBlackColor);
				tv.update(tc, null);
			}

			public void treeExpanded(TreeEvent e) {
				TreeItem item = (TreeItem) e.item;
				TopologyClusterInfo tc = (TopologyClusterInfo) item.getData();
				if (tc.getIsFirstOne()) {
					String node = tc.getNodeName();
					String server = tc.getServerName();
					if (node.endsWith("..."))
						tc.setNodeName(node.substring(0, node.length() - 3));
					if (server.endsWith("..."))
						tc.setServerName(server.substring(0,
								server.length() - 3));
				}
				Color grayColor = new Color(null, 247, 247, 247);

				if (item.getBackground().equals(grayColor)) {
					TreeItem subItems[] = item.getItems();
					if (subItems.length > 0) {
						for (int j = 0; j < subItems.length; j++) {
							subItems[j].setBackground(grayColor);
						}
					}
				}
				Color forgroundGrayColor = new Color(null, 212, 208, 200);
				if (tc.isNodeSelected() == false) {
					item.setForeground(3, forgroundGrayColor);
					item.setForeground(4, forgroundGrayColor);
				}
				tv.update(tc, null);
			}

		});
	}

	public void update(boolean isResetInputs) {
		try {
			log.debug("update " + inputDatas.size());
			if (isResetInputs)
				tv.setInput(inputDatas);
			updateTreeItems();
			tv.refresh();
			log.debug("finish update");
		} catch (Exception e) {
			log.info("exception");
		}
		// updateTreeItems();
		// tv.refresh();
		// System.out.println("*******************************");
		// printTable();
	}

	public void printTable() {
		for (int i = 0; i < inputDatas.size(); i++) {
			TopologyClusterInfo tc = inputDatas.get(i);
			log.debug(tc.getApplicationName() + "  " + tc.getClusterName()
					+ "  " + tc.getNodeName() + "  " + tc.getServerName()
					+ "  " + tc.isNodeSelected());
			List<TopologyClusterInfo> subTcList = tc.getChildren();
			for (int j = 0; j < subTcList.size(); j++) {
				TopologyClusterInfo subTc = subTcList.get(j);
				log.debug(subTc.getApplicationName() + "  "
						+ subTc.getClusterName() + "  " + subTc.getNodeName()
						+ "  " + subTc.getServerName() + "  "
						+ subTc.isNodeSelected());
			}
		}
	}

	private boolean verifyClusterName(TopologyClusterInfo tc) {
		log.debug("verifyClusterName");
		String clusterName = tc.getClusterName();
		log.debug(clusterName);

		if (clusterName == null || clusterName.trim().length() == 0) {
			showErrorMessages(Messages.bind(
					Messages.DEPOLOGY_CLUSTER_INPUT_WARNING_MSG,
					getApplicationNameText(tc.getApplicationName())));
			return false;
		}
		if (installValidator.containsSpace(clusterName.trim())
				|| installValidator
						.containsInvalidCharForClusterName(clusterName.trim())) {
			showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			return false;
		}
		if (!Arrays.asList(getExisted_Clusters()).contains(clusterName)
				&& Arrays.asList(getAll_Clusters()).contains(clusterName)) {
			showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			return false;
		}

		if (topologyType == TopologyPanel.LARGE_TOPOLOGY) {
			String application = tc.getApplicationName();
			for (int i = 0; i < inputDatas.size(); i++) {
				TopologyClusterInfo tc0 = inputDatas.get(i);
				if (tc0.getClusterName().equalsIgnoreCase(clusterName)
						&& !tc0.getApplicationName().equalsIgnoreCase(
								application)) {
					showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
					return false;
				}
			}

			if (existed_Clusters.length > 0) {
				for (int i = 0; i < existed_Clusters.length; i++) {
					if (clusterName.equalsIgnoreCase(existed_Clusters[i])) {
						showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
						return false;
					}
				}
			}
		}
		log.debug("verifyClusterName complete");
		return true;
	}

	private boolean verifyServerName(TopologyClusterInfo tc) {
		log.debug("verifyServerName start");
		String serverName = tc.getServerName();
		String nodeName = tc.getNodeName();
		String clusterName = tc.getClusterName();
		String application = tc.getApplicationName();
		boolean isSelected = tc.isNodeSelected();

		if (isSelected) {
			if (serverName == null || serverName.trim().length() == 0) {
				log.info("verifyServerName error 1 : serverName is empty");
				showErrorMessages(Messages.DEPOLOGY_SERVER_INPUT_WARNING);
				return false;
			}
			if (installValidator.containsSpace(serverName.trim())
					|| installValidator
							.containsInvalidCharForClusterName(serverName
									.trim())) {
				log.info("verifyServerName error 2 : serverName contains invalid char");
				showErrorMessages(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
				return false;
			}

			boolean existedCluster = false;
			if (existed_Clusters != null && existed_Clusters.length > 0) {
				for (int i = 0; i < existed_Clusters.length; i++) {
					if (clusterName.equalsIgnoreCase(existed_Clusters[i])) {
						existedCluster = true;
						break;
					}
				}
			}

			boolean duplicateServerFlag = false;
			if (!existedCluster && inputDatas != null) {
				outer: for (int i = 0; i < inputDatas.size(); i++) {
					TopologyClusterInfo tc0 = inputDatas.get(i);
					// same cluster, server Name could not be same
					if (tc0.getApplicationName().equalsIgnoreCase(application)) {
						if (tc0.isNodeSelected()
								&& tc0.getServerName().equalsIgnoreCase(
										serverName.trim())
								&& !tc0.getNodeName().equals(nodeName)) {
							log.debug("verifyServerName error 3-a");
							duplicateServerFlag = true;
							break outer;
						}
						List<TopologyClusterInfo> list0 = tc0.getChildren();
						inner: for (int j = 0; j < list0.size(); j++) {
							TopologyClusterInfo subTc0 = list0.get(j);
							if (subTc0.isNodeSelected()
									&& subTc0.getServerName().equalsIgnoreCase(
											serverName.trim())
									&& !subTc0.getNodeName().equals(nodeName)) {
								log.debug("verifyServerName error 3-sub-a");
								duplicateServerFlag = true;
								break outer;
							}
						}
					}
					// diff cluster
					else if (!tc0.getClusterName()
							.equalsIgnoreCase(clusterName)) {
						log.debug("verifyServerName diff cluster 3-b **************");
						if (tc0.isNodeSelected()
								&& tc0.getNodeName().equals(nodeName)
								&& tc0.getServerName().equalsIgnoreCase(
										serverName.trim())) {
							log.debug("verifyServerName error 3-b");
							duplicateServerFlag = true;
							break outer;
						}
						List<TopologyClusterInfo> list0 = tc0.getChildren();
						inner: for (int j = 0; j < list0.size(); j++) {
							TopologyClusterInfo subTc0 = list0.get(j);
							log.debug("verifyServerName diff cluster 3-subb **************");
							if (subTc0.isNodeSelected()
									&& subTc0.getNodeName().equals(nodeName)
									&& subTc0.getServerName().equalsIgnoreCase(
											serverName.trim())) {
								log.debug("verifyServerName error 3-sub-b");
								duplicateServerFlag = true;
								break outer;
							}
						}
					}
				}
			}

			if (!existedCluster && duplicateServerFlag == false
					&& existedClusterDatas != null
					&& existedClusterDatas.size() > 0) {
				outer: for (int i = 0; i < existedClusterDatas.size(); i++) {
					TopologyClusterInfo tc0 = existedClusterDatas.get(i);
					if (!tc0.getClusterName().equalsIgnoreCase(clusterName)) {
						if (tc0.isNodeSelected()
								&& tc0.getNodeName().equals(nodeName)
								&& tc0.getServerName().equalsIgnoreCase(
										serverName.trim())) {
							log.info("verifyServerName error 4-b");
							duplicateServerFlag = true;
							break outer;
						}
						List<TopologyClusterInfo> list0 = tc0.getChildren();
						inner: for (int j = 0; j < list0.size(); j++) {
							TopologyClusterInfo subTc0 = list0.get(j);
							if (subTc0.isNodeSelected()
									&& subTc0.getNodeName().equals(nodeName)
									&& subTc0.getServerName().equalsIgnoreCase(
											serverName.trim())) {
								log.info("verifyServerName error 4-sub-b");
								duplicateServerFlag = true;
								break outer;
							}
						}
					}
				}
			}

			if (duplicateServerFlag) {
				log.info("verifyServerName error 3 : serverName is dulplicated");
				showErrorMessages(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
				return false;
			}

		}
		return true;
	}

	private boolean verifyNodeSelection(TopologyClusterInfo tc) {
		if (tc.isNodeSelected())
			return true;

		List<TopologyClusterInfo> subTcList = tc.getChildren();
		for (int j = 0; j < subTcList.size(); j++) {
			TopologyClusterInfo subTc = subTcList.get(j);
			if (subTc.isNodeSelected())
				return true;
		}
		showErrorMessages(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
		return false;
	}

	public void fireCheckboxChanges(TopologyClusterInfo curTc) {
		log.info("fireCheckboxChanges");
		boolean needUpdate = false;
		boolean select = curTc.isNodeSelected();
		String curFeatureName = curTc.getApplicationName();
		String curClusterName = curTc.getClusterName();
		String curNodeName = curTc.getNodeName();

		outer: for (int i = 0; i < inputDatas.size(); i++) {
			TopologyClusterInfo tc = inputDatas.get(i);
			if (tc.getClusterName().equalsIgnoreCase(curClusterName)
					&& !tc.getApplicationName()
							.equalsIgnoreCase(curFeatureName)) {
				String node = tc.getNodeName();
				if (node.endsWith("...")) {
					if (node.substring(0, node.length() - 3)
							.equals(curNodeName)) {
						tc.setNodeSelected(select);
						needUpdate = true;
						continue outer;
					}
				} else if (node.equals(curNodeName)) {
					tc.setNodeSelected(select);
					needUpdate = true;
					continue outer;
				}

				List<TopologyClusterInfo> subTcList = tc.getChildren();
				for (int j = 0; j < subTcList.size(); j++) {
					TopologyClusterInfo subTc = subTcList.get(j);
					node = subTc.getNodeName();
					if (node.equals(curNodeName)) {
						subTc.setNodeSelected(select);
						needUpdate = true;
						continue outer;
					}
				}
			}
		}
		log.debug("needUpdate = " + needUpdate);
		this.update(false);
		/*
		 * if (needUpdate) this.update(); else tv.update(curTc, null);
		 */
	}

	public void fireServerNameChanges(TopologyClusterInfo curTc) {
		log.debug("fireServerNameChanges");
		boolean needUpdate = false;
		String curFeatureName = curTc.getApplicationName();
		String curClusterName = curTc.getClusterName();
		String curNodeName = curTc.getNodeName();
		String curServerName = curTc.getServerName();

		outer: for (int i = 0; i < inputDatas.size(); i++) {
			TopologyClusterInfo tc = inputDatas.get(i);
			if (tc.getClusterName().equalsIgnoreCase(curClusterName)
					&& !tc.getApplicationName()
							.equalsIgnoreCase(curFeatureName)) {
				String node = tc.getNodeName();
				if (node.endsWith("...")) {
					if (node.substring(0, node.length() - 3)
							.equals(curNodeName)) {
						tc.setServerName(curServerName + "...");
						needUpdate = true;
						continue outer;
					}
				} else if (node.equals(curNodeName)) {
					tc.setServerName(curServerName);
					needUpdate = true;
					continue outer;
				}

				List<TopologyClusterInfo> subTcList = tc.getChildren();
				for (int j = 0; j < subTcList.size(); j++) {
					TopologyClusterInfo subTc = subTcList.get(j);
					node = subTc.getNodeName();
					if (node.equals(curNodeName)) {
						subTc.setServerName(curServerName);
						needUpdate = true;
						continue outer;
					}
				}
			}
		}
		log.debug("needUpdate = " + needUpdate);
		this.update(false);
		/*
		 * if (needUpdate) this.update(); else tv.update(curTc, null);
		 */
	}

	public void mergeTC(TopologyClusterInfo existedTc, TopologyClusterInfo curTc) {
		String curNodeName = curTc.getNodeName();
		TopologyClusterInfo selectTc = getNodeSelection(existedTc, curNodeName);
		curTc.setClusterName(existedTc.getClusterName());
		if (selectTc == null) {
			// new node, not in the existed cluster node
			curTc.setNodeSelected(false);
		} else {
			curTc.setNodeSelected(selectTc.isNodeSelected());
			curTc.setServerName(selectTc.getServerName());
		}

		List<TopologyClusterInfo> subCurTcList = curTc.getChildren();
		for (int i = 0; i < subCurTcList.size(); i++) {
			TopologyClusterInfo subCurTc = subCurTcList.get(i);
			curNodeName = subCurTc.getNodeName();
			selectTc = getNodeSelection(existedTc, curNodeName);
			if (selectTc == null) {
				// new node, not in the existed cluster node
				subCurTc.setNodeSelected(false);
				subCurTc.setClusterName(curTc.getClusterName());
			} else {
				subCurTc.setNodeSelected(selectTc.isNodeSelected());
				subCurTc.setServerName(selectTc.getServerName());
				subCurTc.setClusterName(selectTc.getClusterName());
			}
		}

		// find out any node in existed cluster, but not in default cluster
		List<TopologyClusterInfo> tpList = findDiffNode(existedTc, curTc);
		if (tpList.size() > 0) {
			for (int i = 0; i < tpList.size(); i++) {
				TopologyClusterInfo temp = tpList.get(i);
				temp.setApplicationName(curTc.getApplicationName());
				temp.setIsFirstOne(false);
				subCurTcList.add(temp);
			}
		}
	}

	/*
	 * Check any node exists in tc1, but not in tc2
	 */
	public List<TopologyClusterInfo> findDiffNode(TopologyClusterInfo tc1,
			TopologyClusterInfo tc2) {
		List<TopologyClusterInfo> result = new ArrayList<TopologyClusterInfo>();
		List<String> list2 = new ArrayList<String>();
		list2.add(tc2.getNodeName());
		List<TopologyClusterInfo> subTc2List = tc2.getChildren();
		for (int i = 0; i < subTc2List.size(); i++) {
			TopologyClusterInfo subCurTc2 = subTc2List.get(i);
			list2.add(subCurTc2.getNodeName());
		}

		if (!list2.contains(tc1.getNodeName())) {
			TopologyClusterInfo tp = new TopologyClusterInfo();
			tp.setApplicationName("");
			tp.setClusterName(tc1.getClusterName());
			tp.setNodeSelected(tc1.isNodeSelected());
			tp.setNodeName(tc1.getNodeName());
			tp.setServerName(tc1.getServerName());
			result.add(tp);
		}

		List<TopologyClusterInfo> subTc1List = tc1.getChildren();

		for (int i = 0; i < subTc1List.size(); i++) {
			TopologyClusterInfo subCurTc1 = subTc1List.get(i);
			if (!list2.contains(subCurTc1.getNodeName())) {
				TopologyClusterInfo tp = new TopologyClusterInfo();
				tp.setApplicationName("");
				tp.setClusterName(subCurTc1.getClusterName());
				tp.setNodeSelected(subCurTc1.isNodeSelected());
				tp.setNodeName(subCurTc1.getNodeName());
				tp.setServerName(subCurTc1.getServerName());
				result.add(tp);
			}
		}

		return result;
	}

	public TopologyClusterInfo getNodeSelection(TopologyClusterInfo tc,
			String nodeName) {
		String node = tc.getNodeName();
		if (node.equalsIgnoreCase(nodeName))
			return tc;
		else {
			List<TopologyClusterInfo> subTcList = tc.getChildren();
			for (int i = 0; i < subTcList.size(); i++) {
				TopologyClusterInfo subTc = subTcList.get(i);
				node = subTc.getNodeName();
				if (node.equalsIgnoreCase(nodeName))
					return subTc;
			}
		}
		return null;
	}

	public boolean isNewClusterNameSameAsOthers(String clusterName,
			String currentApplicationName) {
		for (int i = 0; i < inputDatas.size(); i++) {
			TopologyClusterInfo tc = inputDatas.get(i);

			if (tc.getClusterName().equalsIgnoreCase(clusterName)
					&& !tc.getApplicationName().equalsIgnoreCase(
							currentApplicationName)) {
				return true;
			}
		}
		return false;
	}

	public void fireClusterNameChanges(TopologyClusterInfo curTc,
			boolean isExistedCluster) {
		log.debug("fireClusterNameChanges");
		boolean needUpdate = false;
		String curFeatureName = curTc.getApplicationName();
		String curClusterName = curTc.getClusterName();
		String curNodeName = curTc.getNodeName();
		String curServerName = curTc.getServerName();

		if (isExistedCluster) {
			for (int i = 0; i < existedClusterDatas.size(); i++) {
				TopologyClusterInfo existedTc = existedClusterDatas.get(i);
				if (existedTc.getClusterName().equalsIgnoreCase(curClusterName)) {
					mergeTC(existedTc, curTc);
					needUpdate = true;
				}
			}
		} else {
			// the original selection doesn't change the size of the nodes
			if (isNewClusterNameSameAsOthers(curClusterName, curFeatureName)) {
				for (int i = 0; i < inputDatas.size(); i++) {
					TopologyClusterInfo tc = inputDatas.get(i);

					if (tc.getClusterName().equalsIgnoreCase(curClusterName)
							&& !tc.getApplicationName().equalsIgnoreCase(
									curFeatureName)) {
						curTc.setClusterName(tc.getClusterName());
						String node = tc.getNodeName();
						String server = tc.getServerName();
						boolean isSelected = tc.isNodeSelected();

						// even if curTc is collapsed, when change the name,
						// curTc will be expanded automatically
						if (node.endsWith("...")) {
							curTc.setServerName(server.substring(0,
									server.length() - 3));
							curTc.setNodeName(node.substring(0,
									node.length() - 3));
							curTc.setNodeSelected(isSelected);
						} else {
							curTc.setServerName(server);
							curTc.setNodeName(node);
							curTc.setNodeSelected(isSelected);
						}
						needUpdate = true;

						curTc.getChildren().clear();
						List<TopologyClusterInfo> subTcList = tc.getChildren();
						for (int j = 0; j < subTcList.size(); j++) {
							TopologyClusterInfo subTc = subTcList.get(j);
							TopologyClusterInfo temp = new TopologyClusterInfo();
							temp.setApplicationName(curFeatureName);
							temp.setClusterName(subTc.getClusterName());
							temp.setNodeSelected(subTc.isNodeSelected());
							temp.setNodeName(subTc.getNodeName());
							temp.setServerName(subTc.getServerName());
							temp.setIsFirstOne(false);
							curTc.getChildren().add(temp);
						}

					} else if (tc.getApplicationName().equalsIgnoreCase(
							curFeatureName)) {
						List<TopologyClusterInfo> subCurTcList = curTc
								.getChildren();
						for (int j = 0; j < subCurTcList.size(); j++) {
							TopologyClusterInfo subCurTc = subCurTcList.get(j);
							subCurTc.setClusterName(curClusterName);
							needUpdate = true;
						}
					}
				}
			} else if (curTc.getChildren().size() == defaultDatas.getChildren()
					.size()) {
				List<TopologyClusterInfo> subCurTcList = curTc.getChildren();
				for (int j = 0; j < subCurTcList.size(); j++) {
					TopologyClusterInfo subCurTc = subCurTcList.get(j);
					subCurTc.setClusterName(curClusterName);
					needUpdate = true;
				}
			} else {
				// set to default
				String node = defaultDatas.getNodeName();
				String server = defaultDatas.getServerName();
				boolean isSelected = defaultDatas.isNodeSelected();
				curTc.setNodeName(node);
				curTc.setNodeSelected(isSelected);
				curTc.setServerName(curTc.getClusterName() + "_" + server);

				curTc.getChildren().clear();
				List<TopologyClusterInfo> subTcList = defaultDatas
						.getChildren();
				for (int j = 0; j < subTcList.size(); j++) {
					TopologyClusterInfo subTc = subTcList.get(j);
					TopologyClusterInfo temp = new TopologyClusterInfo();
					temp.setApplicationName(curFeatureName);
					temp.setClusterName(curTc.getClusterName());
					temp.setNodeSelected(subTc.isNodeSelected());
					temp.setNodeName(subTc.getNodeName());
					temp.setServerName(curTc.getClusterName() + "_"
							+ subTc.getServerName());
					temp.setIsFirstOne(false);
					curTc.getChildren().add(temp);
				}
				needUpdate = true;
			}
		}

		log.debug("needUpdate = " + needUpdate);
		this.update(false);
		/*
		 * if (needUpdate) this.update(); else tv.update(curTc, null);
		 */
	}

	public boolean isExistedCluster(String clusterName) {
		if (existedClusterDatas == null || existedClusterDatas.size() == 0)
			return false;
		for (int i = 0; i < existedClusterDatas.size(); i++) {
			if (existedClusterDatas.get(i).getClusterName()
					.equalsIgnoreCase(clusterName))
				return true;
		}
		return false;
	}

	public void updateTreeItems() {
		log.debug("updateTreeItems");
		// if alrady find one error, no need to continue others.
		boolean noError = true;
		Color forgroundGrayColor = new Color(null, 212, 208, 200);
		Color forgroundBlackColor = new Color(null, 0, 0, 0);
		Color grayColor = new Color(null, 247, 247, 247);

		TreeItem items[] = tv.getTree().getItems();
		for (int i = 0; i < items.length; i++) {
			TopologyClusterInfo tc = (TopologyClusterInfo) items[i].getData();
			if (i % 2 == 1) {
				items[i].setBackground(grayColor);
				TreeItem subItems[] = items[i].getItems();
				if (subItems.length > 0) {
					for (int j = 0; j < subItems.length; j++) {
						subItems[j].setBackground(grayColor);
					}
				}
			}
			items[i].setForeground(0, forgroundBlackColor);
			items[i].setForeground(1, forgroundBlackColor);
			if (isExistedCluster(tc.getClusterName())) {
				items[i].setForeground(3, forgroundGrayColor);
				items[i].setForeground(4, forgroundGrayColor);
			} else if (!tc.getNodeName().endsWith("...")) {
				if (!tc.isNodeSelected()) {
					items[i].setForeground(3, forgroundGrayColor);
					items[i].setForeground(4, forgroundGrayColor);
				} else {
					items[i].setForeground(3, forgroundBlackColor);
					items[i].setForeground(4, forgroundBlackColor);
				}
			}
			// auto expand, remove "..."
			if (tc.getNodeName().endsWith("...")) {
				String node = tc.getNodeName();
				String server = tc.getServerName();
				tc.setNodeName(node.substring(0, node.length() - 3));
				tc.setServerName(server.substring(0, server.length() - 3));
			}

			if (noError && verifyClusterName(tc)) {
				if (verifyNodeSelection(tc)) {
					if (verifyServerName(tc)) {
						noError = true;
					} else {
						noError = false;
					}
				} else {
					noError = false;
				}
			} else {
				noError = false;
			}

			log.debug("noError = " + noError);

			if (items[i].getExpanded()) {
				TreeItem subItems[] = items[i].getItems();
				for (int j = 0; j < subItems.length; j++) {
					TopologyClusterInfo subtc = (TopologyClusterInfo) subItems[j]
							.getData();
					if (noError && verifyServerName(subtc)) {
						noError = true;
					} else {
						noError = false;
					}
					subItems[j].setForeground(0, forgroundBlackColor);
					subItems[j].setForeground(1, forgroundBlackColor);
					if (isExistedCluster(tc.getClusterName())) {
						subItems[j].setForeground(3, forgroundGrayColor);
						subItems[j].setForeground(4, forgroundGrayColor);
					} else {
						if (!subtc.isNodeSelected()) {
							subItems[j].setForeground(3, forgroundGrayColor);
							subItems[j].setForeground(4, forgroundGrayColor);
						} else {
							subItems[j].setForeground(3, forgroundBlackColor);
							subItems[j].setForeground(4, forgroundBlackColor);
						}
					}
				}
			}
		}

		if (noError) {
			depPanel.setPageComplete(topologyType);
		}
	}

	public String getApplicationNameText(String applicationName) {
		return depPanel.getFeaturesMessageText(applicationName);
	}

	public void showErrorMessages(String msg) {
		depPanel.showErrorMessages(msg);
	}

	public List<TopologyClusterInfo> getInputDatas() {
		return inputDatas;
	}

	public void setInputDatas(List<TopologyClusterInfo> inputDatas) {
	
		for (int i = 0; i < inputDatas.size(); i++) {
			TopologyClusterInfo tc = inputDatas.get(i);
			log.debug("setInputDatas "+tc.getApplicationName() + "  " + tc.getClusterName()
					+ "  " + tc.getNodeName() + "  " + tc.getServerName()
					+ "  " + tc.isNodeSelected());
			for (int j = 0; j < existedClusterDatas.size(); j++) {
				TopologyClusterInfo existedTc = existedClusterDatas.get(j);
				if (existedTc.getClusterName().equalsIgnoreCase(tc.getClusterName())) {
					mergeTC(existedTc, tc);
				}
			}
		}
		
		this.inputDatas = inputDatas;
	}

	public String[] getExisted_Clusters() {
		return existed_Clusters;
	}

	public void setExisted_Clusters(String[] existed_Clusters,
			boolean resetItems) {
		this.existed_Clusters = existed_Clusters;
		if (resetItems) {
			tv.getCellEditors()[1] = new TopologyComboBoxCellEditor(
					tv.getTree(), existed_Clusters, SWT.BORDER);
			CCombo combo = (CCombo) tv.getCellEditors()[1].getControl();
			combo.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
					String inStr = e.text;
					if (inStr.length() > 0) {
						try {
							e.doit = !installValidator
									.containsInvalidChar(inStr)
									&& !installValidator.containsSpace(inStr);
						} catch (Exception ex) {
							showErrorMessages(Messages.DEPOLOGY_CLUSTER_INPUT_WARNING);
						}
					}
				}
			});
		}
	}

	public List<TopologyClusterInfo> getExistedClusterDatas() {
		return existedClusterDatas;
	}

	public void setExistedClusterDatas(
			List<TopologyClusterInfo> existedClusterDatas) {
		this.existedClusterDatas = existedClusterDatas;
	}

	public TopologyClusterInfo getDefaultDatas() {
		return defaultDatas;
	}

	public void setDefaultDatas(TopologyClusterInfo defaultDatas) {
		this.defaultDatas = defaultDatas;
	}

	public boolean isCombox() {
		return isCombox;
	}

	public void setCombox(boolean isCombox) {
		this.isCombox = isCombox;
	}

	public String[] getAll_Clusters() {
		return all_Clusters;
	}

	public void setAll_Clusters(String[] all_Clusters) {
		this.all_Clusters = all_Clusters;
	}

	public int getTopologyType() {
		return topologyType;
	}

	public void setTopologyType(int topologyType) {
		this.topologyType = topologyType;
	}

	public TopologyPanel getDepPanel() {
		return depPanel;
	}

	public void setDepPanel(TopologyPanel depPanel) {
		this.depPanel = depPanel;
	}
}

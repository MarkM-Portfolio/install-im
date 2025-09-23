/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.cic.agent.core.api.IAgent;
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.agent.ui.api.IAgentUI;

public class TopologyPanel extends BaseConfigCustomPanel {
	String className = TopologyPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;

	public static final int NODE_LIST = 0;
	public static final int SERVER_LIST = 1;

	public static final int SMALL_TOPOLOGY = 0;
	public static final int MEDIUM_TOPOLOGY = 1;
	public static final int LARGE_TOPOLOGY = 2;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Section clusterNameSection;
	private Section nodeSelectionSection;
	private TopologyTreeViewer tpTreeMedium = new TopologyTreeViewer(this, true);
	private TopologyTreeViewer tpTreeLarge = new TopologyTreeViewer(this, false);
	private List<TopologyClusterInfo> topoInfoMediumList = new ArrayList<TopologyClusterInfo>();
	private List<TopologyClusterInfo> topoInfoLargeList = new ArrayList<TopologyClusterInfo>();
	private List<TopologyClusterInfo> default_topoInfoMediumList = new ArrayList<TopologyClusterInfo>();
	private List<TopologyClusterInfo> default_topoInfoLargeList = new ArrayList<TopologyClusterInfo>();

	private Combo clusterCombo = null;

	private Button[] nodeCheckButtons = new Button[5];
	private Text[] serverMemberText = new Text[5];
	private Button smallInatallButton, mediumInstallButton, largeInstallButton;

	private Composite nodeSelectContainer;
	private GridData combogridData;

	private ArrayList selectNodeList = new ArrayList();
	private ArrayList originalNodeList = new ArrayList();
	// private ArrayList originalServerList = new ArrayList();
	private ArrayList LCClusterList = new ArrayList();
	private ArrayList allClusterList = new ArrayList();

	private Map existClusterInfoMap = new HashMap();
	private boolean isLoadSmall = false;
	private boolean isLoadMedium = false;
	private boolean isLoadLarge = false;
	private boolean isLoadCluster = false;

	private ArrayList<NodeServerPair> activitiesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> blogsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> communitiesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> dogearSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> homepageSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> profilesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> filesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> forumSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> wikisSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> mobileSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> moderationSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> searchSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> newsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> metricsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ccmSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> rteSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> commonSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> widgetContainerSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> pushNotificationSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ic360SelectNodeServerPairList = new ArrayList<NodeServerPair>();
	//private ArrayList<NodeServerPair> quickResultsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	
	// for large
	private ArrayList<NodeServerPair> activitiesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> blogsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> communitiesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> dogearSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> homepageSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> profilesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> filesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> forumSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> wikisSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> mobileSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> moderationSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> searchSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> newsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> metricsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ccmSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> rteSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> commonSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> widgetContainerSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> pushNotificationSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ic360SelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	//private ArrayList<NodeServerPair> quickResultsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	
	private Button expandMediumButton = null, collapseMediumButton = null,
			resetMediumButton = null;
	private Button expandLargeButton = null, collapseLargeButton = null,
			resetLargeButton = null;

	private ArrayList selectFeaturesList = new ArrayList();
	private boolean isRecordingMode = false;
	Composite parent = null;
	InstallValidator installValidator = new InstallValidator();

	private List<String> existedClusters = null;
	
	public TopologyPanel() {
		super(Messages.DEPOLOGY_PANEL);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		isRecordingMode = agent.isSkipInstall();
		log.info("Deployment Panel :: Entered");
		this.parent = parent;
		/*
		 * isLoadSmall = false; isLoadMedium = false; isLoadLarge = false;
		 * isLoadCluster = false; LCClusterList.clear(); allClusterList.clear();
		 * originalNodeList.clear(); selectNodeList.clear();
		 * selectFeaturesList.clear(); existClusterInfoMap.clear();
		 * 
		 * activitiesSelectNodeServerPairList.clear();
		 * blogsSelectNodeServerPairList.clear();
		 * communitiesSelectNodeServerPairList.clear();
		 * dogearSelectNodeServerPairList.clear();
		 * homepageSelectNodeServerPairList.clear();
		 * profilesSelectNodeServerPairList.clear();
		 * filesSelectNodeServerPairList.clear();
		 * forumSelectNodeServerPairList.clear();
		 * wikisSelectNodeServerPairList.clear();
		 * mobileSelectNodeServerPairList.clear();
		 * moderationSelectNodeServerPairList.clear();
		 * searchSelectNodeServerPairList.clear();
		 * newsSelectNodeServerPairList.clear();
		 * 
		 * // for large activitiesSelectNodeServerPairListLarge.clear();
		 * blogsSelectNodeServerPairListLarge.clear();
		 * communitiesSelectNodeServerPairListLarge.clear();
		 * dogearSelectNodeServerPairListLarge.clear();
		 * homepageSelectNodeServerPairListLarge.clear();
		 * profilesSelectNodeServerPairListLarge.clear();
		 * filesSelectNodeServerPairListLarge.clear();
		 * forumSelectNodeServerPairListLarge.clear();
		 * wikisSelectNodeServerPairListLarge.clear();
		 * mobileSelectNodeServerPairListLarge.clear();
		 * moderationSelectNodeServerPairListLarge.clear();
		 * searchSelectNodeServerPairListLarge.clear();
		 * newsSelectNodeServerPairListLarge.clear();
		 */
		profile = getProfile();
		if (profile != null) {
			wasLoc = profile.getUserData("user.was.install.location");
			profileName = profile.getUserData("user.was.profileName");
		}

		container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();

		gridLayout.verticalSpacing = 10;
		gridLayout.horizontalSpacing = 5;
		container.setLayout(gridLayout);

		IAdaptable adaptable = getInitializationData();
		IAgentUI agentUI = (IAgentUI) adaptable.getAdapter(IAgentUI.class);
		toolkit = agentUI.getFormToolkit();

		form = toolkit.createScrolledForm(container);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);

		// Select Application Server Type
		clusterNameSection = toolkit.createSection(form.getBody(),
				Section.TITLE_BAR);

		// applicationServerSection.setSize(2000, 10);
		clusterNameSection.setText(Messages.DEPOLOGY_PANEL_INFO);

		final Composite applicationServerSelectContainer = toolkit
				.createComposite(clusterNameSection);
		GridLayout applicationServerSelectLayout = new GridLayout();
		// applicationServerSelectLayout.numColumns = 2;
		applicationServerSelectContainer
				.setLayout(applicationServerSelectLayout);
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		applicationServerSelectContainer.setLayoutData(gd);

		GridData textgridData = new GridData(GridData.BEGINNING);
		textgridData.horizontalSpan = 1;
		textgridData.widthHint = 150;
		textgridData.verticalIndent = 10;

		Label topologyDesLabel = new Label(applicationServerSelectContainer,
				SWT.WRAP);
		topologyDesLabel.setText(Messages.DEPOLOGY_SELECTION_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		topologyDesLabel.setLayoutData(gd);

		this.smallInatallButton = new Button(applicationServerSelectContainer,
				SWT.RADIO);
		this.smallInatallButton.setBackground(applicationServerSelectContainer
				.getBackground());
		this.smallInatallButton.setText(Messages.SMALL_DEPOLOGY_SELECTION_INFO);

		this.mediumInstallButton = new Button(applicationServerSelectContainer,
				SWT.RADIO);
		this.mediumInstallButton.setBackground(applicationServerSelectContainer
				.getBackground());
		this.mediumInstallButton.setSelection(false);
		this.mediumInstallButton
				.setText(Messages.MEDIUM_DEPOLOGY_SELECTION_INFO);

		this.largeInstallButton = new Button(applicationServerSelectContainer,
				SWT.RADIO);
		this.largeInstallButton.setBackground(applicationServerSelectContainer
				.getBackground());
		this.largeInstallButton.setText(Messages.LARGE_DEPOLOGY_SELECTION_INFO);

		final Composite applicationServerSelectStackContainer = new Composite(
				form.getBody(), SWT.NONE);
		final StackLayout stackLayout = new StackLayout();
		applicationServerSelectStackContainer.setLayout(stackLayout);

		final Composite smallInatallComposite = new Composite(
				applicationServerSelectStackContainer, SWT.NONE);
		final Composite mediumInstallComposite = new Composite(
				applicationServerSelectStackContainer, SWT.NONE);
		final Composite largeInstallComposite = new Composite(
				applicationServerSelectStackContainer, SWT.NONE);

		// stackLayout.topControl = mediumInstallComposite;
		// applicationServerSelectStackContainer.layout();

		clusterNameSection.setClient(applicationServerSelectContainer);

		mediumInstallButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (mediumInstallButton.getSelection() == true) {
					smallInatallButton.setSelection(false);
					largeInstallButton.setSelection(false);
					stackLayout.topControl = mediumInstallComposite;
					applicationServerSelectStackContainer.layout();
					if (isOnlyModifyAddExistingCCM())
						skipToNextPanel();
					else {
						loadClusterInfo();
						loadNodeForMedium();
					}
				}
			}
		});

		largeInstallButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (largeInstallButton.getSelection() == true) {
					smallInatallButton.setSelection(false);
					mediumInstallButton.setSelection(false);
					stackLayout.topControl = largeInstallComposite;
					applicationServerSelectStackContainer.layout();
					if (isOnlyModifyAddExistingCCM())
						skipToNextPanel();
					else {
						loadClusterInfo();
						loadNodeForLarge();
					}
				}

			}
		});
		
		Label noticeLabel = new Label(applicationServerSelectContainer,
				SWT.WRAP);
		noticeLabel.setText(Messages.warning_message);
		noticeLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.widthHint = 600;
		noticeLabel.setLayoutData(gd);

		// smallInatallComposite
		smallInatallComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		smallInatallComposite.setLayoutData(gd);

		Section smallInstallSection = toolkit.createSection(
				smallInatallComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		smallInstallSection.setLayoutData(gridData);
		smallInstallSection.setText(Messages.DEPOLOGY_INPUT_CLUSTER);

		Composite smallInstallContainer = toolkit
				.createComposite(smallInstallSection);
		GridLayout smallInstallLayout = new GridLayout();
		smallInstallLayout.numColumns = 2;
		smallInstallContainer.setLayout(smallInstallLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		smallInstallContainer.setLayoutData(gd);

		Label clusterDesLabel = new Label(smallInstallContainer, SWT.NONE);
		clusterDesLabel.setText(Messages.DEPOLOGY_INPUT_CLUSTER_INFO_INSTALL);

		if (isNewFeatureAdded()) {
			clusterDesLabel.setText(Messages.DEPOLOGY_INPUT_CLUSTER_INFO);
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		clusterDesLabel.setLayoutData(gd);

		if (isRecordingMode) {
			gd = new GridData();
			gd.widthHint = 600;
			gd.verticalIndent = 10;
			gd.horizontalSpan = 2;
			Label clusterNoteLabel1 = new Label(smallInstallContainer, SWT.WRAP);
			clusterNoteLabel1.setText(Messages.DEPOLOGY_INPUT_CLUSTER_NOTE);
			clusterNoteLabel1.setLayoutData(gd);
			FontData newFontData = clusterNoteLabel1.getFont().getFontData()[0];
			newFontData.setStyle(SWT.BOLD);
			Font newFont = new Font(smallInstallContainer.getDisplay(),
					newFontData);
			clusterNoteLabel1.setFont(newFont);
		}

		new Label(smallInstallContainer, SWT.NONE)
				.setText(Messages.DEPOLOGY_CLUSTER);
		this.clusterCombo = new Combo(smallInstallContainer, SWT.DROP_DOWN);

		this.clusterCombo.setText("ICCluster");
		this.clusterCombo.setLayoutData(textgridData);

		smallInstallSection.setClient(smallInstallContainer);

		nodeSelectionSection = toolkit.createSection(smallInatallComposite,
				Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		nodeSelectionSection.setLayoutData(gridData);
		nodeSelectionSection.setText(Messages.DEPOLOGY_NODE_SELECTION);

		nodeSelectContainer = toolkit.createComposite(nodeSelectionSection);
		GridLayout nodeSelectLayout = new GridLayout();
		nodeSelectLayout.numColumns = 2;
		nodeSelectContainer.setLayout(nodeSelectLayout);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.verticalIndent = 10;
		// gd2.horizontalIndent = 5;
		nodeSelectContainer.setLayoutData(gd2);

		combogridData = new GridData(GridData.BEGINNING);
		combogridData.horizontalSpan = 1;
		combogridData.widthHint = 150;

		Label nodeDesLabel = new Label(nodeSelectContainer, SWT.WRAP);
		nodeDesLabel.setText(Messages.DEPOLOGY_NODE_SELECTION_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nodeDesLabel.setLayoutData(gd);

		// gd = new GridData(GridData.BEGINNING);
		// gd.horizontalAlignment=SWT.BEGINNING;
		// gd.horizontalSpan = 2;
		gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 200;
		gd.horizontalSpan = 2;

		Label NodeSelectionLabel = new Label(nodeSelectContainer, SWT.NONE);
		NodeSelectionLabel.setText(Messages.DEPOLOGY_CLUSTER_TABLE_NODES);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 200;
		NodeSelectionLabel.setLayoutData(gd);

		Label ServerMemberLabel = new Label(nodeSelectContainer, SWT.NONE);
		ServerMemberLabel.setText(Messages.DEPOLOGY_CLUSTER_TABLE_SERVERS);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 200;
		ServerMemberLabel.setLayoutData(gd);

		for (int i = 0; i < nodeCheckButtons.length; i++) {
			this.nodeCheckButtons[i] = new Button(nodeSelectContainer,
					SWT.CHECK);
			this.nodeCheckButtons[i].setEnabled(false);
			this.nodeCheckButtons[i].setVisible(false);
			this.nodeCheckButtons[i].setLayoutData(gd);
			this.nodeCheckButtons[i].setBackground(new Color(null, 255, 255,
					255));
			this.nodeCheckButtons[i].setForeground(new Color(null, 0, 0, 0));

			this.serverMemberText[i] = new Text(nodeSelectContainer, SWT.BORDER
					| SWT.SINGLE);
			this.serverMemberText[i].setText(clusterCombo.getText() + "_server"
					+ (i + 1));
			this.serverMemberText[i].setEnabled(false);
			this.serverMemberText[i].setVisible(false);
			this.serverMemberText[i].setLayoutData(gd);
			this.serverMemberText[i].addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyCompleteForSmall();
				}
			});
		}

		nodeCheckButtons[0].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String nodeName = nodeCheckButtons[0].getText();
				serverMemberText[0].setEnabled(nodeCheckButtons[0]
						.getSelection());
				if (nodeCheckButtons[0].getSelection() == true) {
					if (!selectNodeList.contains(nodeName))
						selectNodeList.add(nodeName);
				} else
					selectNodeList.remove(nodeName);

				if (selectNodeList.size() == 0) {
					setErrorMessage(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
					nextEnabled = false;
					setPageComplete(false);
					return;
				} else {
					verifyCompleteForSmall();
				}
			}

		});

		nodeCheckButtons[1].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String nodeName = nodeCheckButtons[1].getText();
				serverMemberText[1].setEnabled(nodeCheckButtons[1]
						.getSelection());
				if (nodeCheckButtons[1].getSelection() == true) {
					if (!selectNodeList.contains(nodeName))
						selectNodeList.add(nodeName);
				} else
					selectNodeList.remove(nodeName);

				if (selectNodeList.size() == 0) {
					setErrorMessage(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
					nextEnabled = false;
					setPageComplete(false);
					return;
				} else {
					verifyCompleteForSmall();
				}
			}

		});

		nodeCheckButtons[2].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String nodeName = nodeCheckButtons[2].getText();
				serverMemberText[2].setEnabled(nodeCheckButtons[2]
						.getSelection());
				if (nodeCheckButtons[2].getSelection() == true) {
					if (!selectNodeList.contains(nodeName))
						selectNodeList.add(nodeName);
				} else
					selectNodeList.remove(nodeName);

				if (selectNodeList.size() == 0) {
					setErrorMessage(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
					nextEnabled = false;
					setPageComplete(false);
					return;
				} else {
					verifyCompleteForSmall();
				}
			}

		});

		nodeCheckButtons[3].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String nodeName = nodeCheckButtons[3].getText();
				serverMemberText[3].setEnabled(nodeCheckButtons[3]
						.getSelection());
				if (nodeCheckButtons[3].getSelection() == true) {
					if (!selectNodeList.contains(nodeName))
						selectNodeList.add(nodeName);
				} else
					selectNodeList.remove(nodeName);

				if (selectNodeList.size() == 0) {
					setErrorMessage(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
					nextEnabled = false;
					setPageComplete(false);
					return;
				} else {
					verifyCompleteForSmall();
				}
			}

		});

		nodeCheckButtons[4].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String nodeName = nodeCheckButtons[4].getText();
				serverMemberText[4].setEnabled(nodeCheckButtons[4]
						.getSelection());
				if (nodeCheckButtons[4].getSelection() == true) {
					if (!selectNodeList.contains(nodeName))
						selectNodeList.add(nodeName);
				} else
					selectNodeList.remove(nodeName);

				if (selectNodeList.size() == 0) {
					setErrorMessage(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
					nextEnabled = false;
					setPageComplete(false);
					return;
				} else {
					verifyCompleteForSmall();
				}
			}

		});

		nodeSelectionSection.setClient(nodeSelectContainer);

		this.clusterCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				smallClusterComboAction();
				verifyCompleteForSmall();
			}
		});

		smallInatallButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (smallInatallButton.getSelection() == true) { // Cell
					mediumInstallButton.setSelection(false);
					largeInstallButton.setSelection(false);
					stackLayout.topControl = smallInatallComposite;
					applicationServerSelectStackContainer.layout();
					if (isOnlyModifyAddExistingCCM())
						skipToNextPanel();
					else {
						loadClusterInfo();
						addClusterToComboSmall();
						smallClusterComboAction();
						verifyCompleteForSmall();
					}
				}

			}
		});

		// mediumInstallComposite

		mediumInstallComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalIndent = 10;
		mediumInstallComposite.setLayoutData(gd);

		Section mediumInstallSection = toolkit.createSection(
				mediumInstallComposite, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_BOTH);
		mediumInstallSection.setLayoutData(gridData);
		mediumInstallSection.setText(Messages.DEPOLOGY_INPUT_CLUSTER);

		Composite mediumInstallContainer = toolkit
				.createComposite(mediumInstallSection);
		GridLayout mediumInstallLayout = new GridLayout();
		// mediumInstallLayout.numColumns = 5;
		mediumInstallContainer.setLayout(mediumInstallLayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		mediumInstallContainer.setLayoutData(gd);

		clusterDesLabel = new Label(mediumInstallContainer, SWT.WRAP);
		clusterDesLabel.setText(Messages.DEPOLOGY_MEDIUM_INPUT_CLUSTER_INFO);
		gd = new GridData();
		gd.widthHint = 600;
		gd.verticalIndent = 10;
		clusterDesLabel.setLayoutData(gd);

		if (isRecordingMode) {
			Label clusterNoteLabel2 = new Label(mediumInstallContainer,
					SWT.WRAP);
			clusterNoteLabel2.setText(Messages.DEPOLOGY_INPUT_CLUSTER_NOTE);
			clusterNoteLabel2.setLayoutData(gd);
			FontData newFontData = clusterNoteLabel2.getFont().getFontData()[0];
			newFontData.setStyle(SWT.BOLD);
			Font newFont = new Font(mediumInstallContainer.getDisplay(),
					newFontData);
			clusterNoteLabel2.setFont(newFont);
		}

		Composite buttonComposite = new Composite(mediumInstallContainer,
				SWT.NONE);
		buttonComposite.setLayout(new FillLayout());
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		buttonComposite.setLayoutData(gd);

		resetMediumButton = new Button(buttonComposite, SWT.PUSH);
		resetMediumButton.setText(Messages.DEPOLOGY_RESET_BTN);
		resetMediumButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent arg0) {
				log.debug("resetMediumButton clicked");
				resetDefaultTreeData(MEDIUM_TOPOLOGY);
			}
		});

		expandMediumButton = new Button(buttonComposite, SWT.PUSH);
		expandMediumButton.setText(Messages.DEPOLOGY_EXPAND_ALL_BTN);
		expandMediumButton.setVisible(false);
		collapseMediumButton = new Button(buttonComposite, SWT.PUSH);
		collapseMediumButton.setText(Messages.DEPOLOGY_COLLAPSE_ALL_BTN);
		collapseMediumButton.setVisible(false);

		Composite treeComposite = new Composite(mediumInstallContainer,
				SWT.BORDER);
		treeComposite.setLayout(new FillLayout());
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = 650;
		treeComposite.setLayoutData(gd);

		tpTreeMedium.createTreeContent(treeComposite);

		mediumInstallSection.setClient(mediumInstallContainer);

		// largeInatallComposite
		largeInstallComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalIndent = 10;
		largeInstallComposite.setLayoutData(gd);

		Section largeInstallSection = toolkit.createSection(
				largeInstallComposite, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_BOTH);
		largeInstallSection.setLayoutData(gridData);
		largeInstallSection.setText(Messages.DEPOLOGY_INPUT_CLUSTER);

		Composite largeInstallContainer = toolkit
				.createComposite(largeInstallSection);
		GridLayout largeInstallLayout = new GridLayout();
		// largeInstallLayout.numColumns = 5;
		largeInstallContainer.setLayout(largeInstallLayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		largeInstallContainer.setLayoutData(gd);

		clusterDesLabel = new Label(largeInstallContainer, SWT.WRAP);
		clusterDesLabel.setText(Messages.DEPOLOGY_LARGE_INPUT_CLUSTER_INFO);
		gd = new GridData();
		gd.widthHint = 600;
		gd.verticalIndent = 10;
		clusterDesLabel.setLayoutData(gd);

		if (isRecordingMode) {
			Label clusterNoteLabel3 = new Label(largeInstallContainer, SWT.WRAP);
			clusterNoteLabel3.setText(Messages.DEPOLOGY_INPUT_CLUSTER_NOTE);
			clusterNoteLabel3.setLayoutData(gd);
			FontData newFontData = clusterNoteLabel3.getFont().getFontData()[0];
			newFontData.setStyle(SWT.BOLD);
			Font newFont = new Font(largeInstallContainer.getDisplay(),
					newFontData);
			clusterNoteLabel3.setFont(newFont);
		}

		buttonComposite = new Composite(largeInstallContainer, SWT.NONE);
		buttonComposite.setLayout(new FillLayout());
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		buttonComposite.setLayoutData(gd);

		resetLargeButton = new Button(buttonComposite, SWT.PUSH);
		resetLargeButton.setText(Messages.DEPOLOGY_RESET_BTN);
		resetLargeButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent arg0) {
				log.debug("resetLargeButton clicked");
				resetDefaultTreeData(LARGE_TOPOLOGY);
			}
		});

		expandLargeButton = new Button(buttonComposite, SWT.PUSH);
		expandLargeButton.setText(Messages.DEPOLOGY_EXPAND_ALL_BTN);
		expandLargeButton.setVisible(false);
		collapseLargeButton = new Button(buttonComposite, SWT.PUSH);
		collapseLargeButton.setText(Messages.DEPOLOGY_COLLAPSE_ALL_BTN);
		collapseLargeButton.setVisible(false);

		treeComposite = new Composite(largeInstallContainer, SWT.BORDER);
		treeComposite.setLayout(new FillLayout());
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = 650;
		treeComposite.setLayoutData(gd);

		tpTreeLarge.createTreeContent(treeComposite);

		largeInstallSection.setClient(largeInstallContainer);

		nodeSelectionSection = toolkit.createSection(largeInstallComposite,
				Section.NO_TITLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		
		nodeSelectionSection.setLayoutData(gridData);

		form.pack();
		setControl(container);
		// nextEnabled = false;
	}

	private void saveData() {
		profile = getProfile();
		saveWasAdmin();

		String selectCluster = clusterCombo.getText();

		if (selectCluster == null || selectCluster.trim().equals("")) {
			setErrorMessage(Messages.DEPOLOGY_CLUSTER_INPUT_WARNING);
			nextEnabled = false;
			setPageComplete(false);
		}

		String isClusterExist = "false";
		if (LCClusterList.contains(selectCluster)) {
			isClusterExist = "true";
		}

		String firstNodeName = "";
		Collections.sort(selectNodeList);
		if (selectNodeList.size() > 0) {
			firstNodeName = (String) selectNodeList.get(0);
		}

		StringBuffer secondaryNodeNames = new StringBuffer();
		if (selectNodeList.size() > 1) {
			for (int i = 1; i < selectNodeList.size(); i++) {
				secondaryNodeNames.append(selectNodeList.get(i) + ",");
			}
		}

		// generate cluster info to store in config.properties

		log.debug("cluster name in small deplyment : " + selectCluster);
		log.debug("clusterExist in small deplyment : " + isClusterExist);
		log.debug("firstNodeName in small deplyment : " + firstNodeName);
		log.debug("secondaryNodesNames in small deplyment : "
				+ secondaryNodeNames.toString());

		StringBuffer serverNames = new StringBuffer();
		StringBuffer clusterInfo = new StringBuffer();
		profile.setUserData("user.deployment.type", "small");
		
		String ccmServerName = null;
		//showValidationWarningMessageDialog("ServerName: "+ccmServerName);

		for (int feature = ACTIVITIES; feature <= IC360; feature++) {
			if (getIsFeatureSelected(feature)) {
				if (feature == CCM && this.profile.getUserData("user.ccm.existingDeployment").equals("true")){
					profile.setUserData("user.ccm.cluster.key.value", "");
					continue;
				}
				profile.setUserData("user." + getFeatureName(feature)
						+ ".clusterExist", isClusterExist);
				profile.setUserData("user." + getFeatureName(feature)
						+ ".clusterName", selectCluster);
				profile.setUserData("user." + getFeatureName(feature)
						+ ".firstNodeName", firstNodeName);
				profile.setUserData("user." + getFeatureName(feature)
						+ ".secondaryNodesNames", secondaryNodeNames.toString());

				//showValidationWarningMessageDialog("nodelist: "+selectNodeList.size());
				
				if (selectNodeList.size() > 0) {
					clusterInfo.append("[");
					for (int i = 0; i < selectNodeList.size(); i++) {
						inner: for (int j = 0; j < nodeCheckButtons.length; j++) {
							String node = (String) selectNodeList.get(i);
							//showValidationWarningMessageDialog("node: "+node);
							//showValidationWarningMessageDialog("nodebutton: "+nodeCheckButtons[j].getText());
							if (nodeCheckButtons[j].getText().equals(node)) {
								serverNames.append(getFeatureName(feature) +"."+ node +".ServerName="+ serverMemberText[j].getText() +";");
								clusterInfo.append("{\"node\": \""+ node +"\", \"name\": \""+ serverMemberText[j].getText() +"\"},");
								//showValidationWarningMessageDialog("feature: " + getFeatureName(feature) + " ServerName: "+serverMemberText[j].getText());
								if (feature == CCM){
									ccmServerName = serverMemberText[j].getText();
									//showValidationWarningMessageDialog("ccmServerName: "+ccmServerName);
								}
								break inner;
							}
						}
					}
					clusterInfo.append("]");
					profile.setUserData("user."+ getFeatureName(feature) +".serverInfo", serverNames.toString());
					profile.setUserData("user."+ getFeatureName(feature) +".clusterInfo", clusterInfo.toString());
					if (ccmServerName != null){
						//showValidationWarningMessageDialog("writing ccmServerName to profile: "+ccmServerName);
					    profile.setUserData("user."+ getFeatureName(feature) +".serverName", ccmServerName);
					}
					if (feature == CCM){
						profile.setUserData("user.ccm.cluster.key.value", "\""+selectCluster+"\" : " + clusterInfo.toString() + ",");
					}
				}
				serverNames.delete(0, serverNames.length());
				clusterInfo.delete(0, clusterInfo.length());
			}
		}

	}

	private void saveDataMediumLarge(int topologyType) {
		profile = getProfile();

		if (profile != null && topologyType == MEDIUM_TOPOLOGY ? isLoadMedium
				: isLoadLarge) {
			log.debug("saveDataMediumLarge largeInstallButton "
					+ largeInstallButton.getSelection());
			log.debug("saveDataMediumLarge mediumInstallButton "
					+ mediumInstallButton.getSelection());
			log.debug("saveDataMediumLarge topologyType " + topologyType);

			if (largeInstallButton.getSelection() == true
					&& topologyType != LARGE_TOPOLOGY)
				return;
			else if (mediumInstallButton.getSelection() == true
					&& topologyType != MEDIUM_TOPOLOGY)
				return;
			else if (mediumInstallButton.getSelection() == false
					&& largeInstallButton.getSelection() == false)
				return;

			log.info("saveDataMediumLarge");

			saveWasAdmin();
			setTreeInputs(topologyType);

			profile.setUserData("user.deployment.type",
					getTopologyTypeName(topologyType));
			log.info("user.deployment.type : "
					+ getTopologyTypeName(topologyType));

			String firstNodeName = "", isClusterExist = "false";
			StringBuffer secondaryNodeNames = null, serverNames = null, clusterInfo = null, firstCCMServerName = null, secondCCMServerName = null;

			for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
				secondaryNodeNames = new StringBuffer();
				firstCCMServerName = new StringBuffer();
				secondCCMServerName = new StringBuffer();
				serverNames = new StringBuffer();
				clusterInfo = new StringBuffer();
				if (getIsFeatureSelected(featureID)) {
					if (featureID == CCM && this.profile.getUserData("user.ccm.existingDeployment").equals("true")){
						profile.setUserData("user.ccm.cluster.key.value", "");
						continue;
					}
					List<NodeServerPair> pairList = getFeatureNodeServerPairList(
							featureID, topologyType);

					log.info(getFeatureName(featureID)
							+ " : pairList.size() = " + pairList.size());

					int num = 0;
					// find fisrt node
					while (pairList.get(num).isSelected() == false) {
						num++;
					}
					String clusterName = pairList.get(num).getClusterName();
					if (LCClusterList.contains(clusterName))
						isClusterExist = "true";

					firstNodeName = pairList.get(num).getNodeName();

					if (pairList.size() > 1) {
						for (int i = num + 1; i < pairList.size(); i++) {
							if (pairList.get(i).isSelected() == true)
								secondaryNodeNames.append(pairList.get(i)
										.getNodeName() + ",");
						}
					}

					clusterInfo.append("[");
					if (pairList.size() == 1){
						profile.setUserData("user."+ getFeatureName(featureID) + ".secondaryNodeNames.ServerName", "");
					}
					for (int i = 0; i < pairList.size(); i++) {
						NodeServerPair pair = pairList.get(i);
						if (pair.isSelected() == true) {
							serverNames.append(getFeatureName(featureID) +"."+ pair.getNodeName() +".ServerName="+ pair.getServerMemberName() +";");
							log.debug("pair.isSelected() = "+ pair.isSelected());
							log.debug("test node = "+ pair.getNodeName() +"   "+ pair.getServerMemberName());
							clusterInfo.append("{\"node\": \""+ pair.getNodeName() +"\", \"name\": \""+ pair.getServerMemberName() +"\"},");
							
							if (featureID == CCM){
								//showValidationWarningMessageDialog("CCM selected!");
								//showValidationWarningMessageDialog("node name: " + pair.getNodeName());
								//showValidationWarningMessageDialog("server name: " + pair.getServerMemberName());
								if (i == 0){
									//showValidationWarningMessageDialog("i is: "+i);
									firstCCMServerName.append(pair.getServerMemberName());
									profile.setUserData("user."+ getFeatureName(featureID) +".serverName", firstCCMServerName.toString());
									//showValidationWarningMessageDialog("firstCcmServerName: "+firstCCMServerName.toString());
								}
								
								if (i == 1){
									//showValidationWarningMessageDialog("i is: "+i + ", second ccm server name: "+pair.getServerMemberName());
									secondCCMServerName.append(pair.getServerMemberName());
									//profile.setUserData("user."+ getFeatureName(featureID) +".secondServerName", secondCCMServerName.toString());
									profile.setUserData("user."+ getFeatureName(featureID) + ".secondaryNodeNames.ServerName", "user." + getFeatureName(featureID) + "." + secondaryNodeNames + ".ServerName=" + secondCCMServerName.toString());
									//showValidationWarningMessageDialog("firstCcmServerName: "+firstCCMServerName.toString());
								}
								//ccmServerName = serverMemberText[j].getText();
								//showValidationWarningMessageDialog("ccmServerName: "+ccmServerName);
							}
						}
					}
					clusterInfo.append("]");
					profile.setUserData("user."+ getFeatureName(featureID) +".clusterExist", isClusterExist);
					profile.setUserData("user."+ getFeatureName(featureID) +".clusterName", clusterName);
					profile.setUserData("user."+ getFeatureName(featureID) +".firstNodeName", firstNodeName);
					profile.setUserData("user."+ getFeatureName(featureID) +".secondaryNodesNames", secondaryNodeNames.toString());
					profile.setUserData("user."+ getFeatureName(featureID) +".serverInfo", serverNames.toString());
					profile.setUserData("user."+ getFeatureName(featureID) +".clusterInfo", clusterInfo.toString());

					if (featureID == CCM){
						profile.setUserData("user.ccm.cluster.key.value", "\""+clusterName+"\" : " + clusterInfo.toString() + ",");
					}
					
					log.info(getFeatureName(featureID) +": cluster name : "+ clusterName);
					log.info(getFeatureName(featureID) +": clusterExist : "+ isClusterExist);
					log.info(getFeatureName(featureID) +": firstNodeName : "+ firstNodeName);
					log.info(getFeatureName(featureID) +": secondaryNodesNames : "+ secondaryNodeNames.toString());
					log.info(getFeatureName(featureID) +": serverInfo : "+ serverNames.toString());
					log.info(getFeatureName(featureID) +": clusterInfo : "+ clusterInfo.toString());

					serverNames.delete(0, serverNames.length());
					clusterInfo.delete(0, clusterInfo.length());
				}
			}
		}
	}

	private void smallClusterComboAction() {
		String cluster = clusterCombo.getText();

		if (LCClusterList.contains(cluster)) {
			List<NodeServerPair> existPairList = getExistNodeServerPairList(cluster);
			log.debug("smallClusterComboAction existPairList.size = "
					+ existPairList.size());
			selectNodeList.clear();

			boolean isExistNode = false;
			for (int i = 0; i < originalNodeList.size(); i++) {
				String nodeNameTemp = nodeCheckButtons[i].getText();
				log.debug("smallClusterComboAction nodeNameTemp = "
						+ nodeNameTemp);
				inner: for (int j = 0; j < existPairList.size(); j++) {
					NodeServerPair pair = existPairList.get(j);
					if (pair.getNodeName().equals(nodeNameTemp)) {
						nodeCheckButtons[i].setEnabled(false);
						nodeCheckButtons[i].setSelection(true);
						serverMemberText[i].setEnabled(false);
						serverMemberText[i].setText(pair.getServerMemberName());
						selectNodeList.add(nodeNameTemp);
						isExistNode = true;
						break inner;
					}
					log.debug("smallClusterComboAction pair.getNodeName() = "
							+ pair.getNodeName());
				}
				if (!isExistNode) {
					nodeCheckButtons[i].setEnabled(false);
					nodeCheckButtons[i].setSelection(false);
					serverMemberText[i].setEnabled(false);
					serverMemberText[i].setText("");
				}
				isExistNode = false;
			}

		} else {
			selectNodeList.clear();
			for (int i = 0; i < originalNodeList.size(); i++) {
				nodeCheckButtons[i].setEnabled(true);
				nodeCheckButtons[i].setSelection(false);
				nodeCheckButtons[i].setVisible(true);
				nodeCheckButtons[i].setText((String) originalNodeList.get(i));
				serverMemberText[i].setEnabled(false);
				serverMemberText[i].setVisible(true);
				if (serverMemberText[i].getText() != null
						|| serverMemberText[i].getText().trim().length() == 0)
					serverMemberText[i].setText(clusterCombo.getText()
							+ "_server" + (i + 1));
			}
		}

	}

	private void verifyCompleteForSmall() {

		String cluster = clusterCombo.getText();
		if (cluster == null || cluster.trim().length() == 0) {

			setErrorMessage(Messages.DEPOLOGY_CLUSTER_INPUT_WARNING);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
		
		// if it's a "modify" installation, we empty the existedClusters, 
		// that means we allow user to add apps to an existed cluster.
		if(isModify()||isUpdate()) existedClusters.clear();
		if (installValidator.containsSpace(cluster.trim())
				|| installValidator.containsInvalidCharForClusterName(cluster
						.trim()) || existedClusters.contains(cluster)) {
			setErrorMessage(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
		boolean isSelectNode = false, isServerEmpty = true, isServerContainSpace = true;
		Map<String, String> serverMap = new HashMap<String, String>();

		for (int i = 0; i < originalNodeList.size(); i++) {
			if (nodeCheckButtons[i].getSelection() == true) {
				isSelectNode = true;

				String serverMember = serverMemberText[i].getText();

				if (installValidator.containsSpace(serverMember.trim())) {
					isServerContainSpace = false;
				}

				if (serverMember == null || serverMember.trim().length() == 0
						|| serverMap.put(serverMember, serverMember) != null) {
					isServerEmpty = false;
				}
			}
		}

		if (!isSelectNode) {
			setErrorMessage(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}

		if (!isServerContainSpace) {
			setErrorMessage(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}

		if (!isServerEmpty) {
			setErrorMessage(Messages.DEPOLOGY_SERVER_INPUT_WARNING);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}

		/*
		// Need to change behavior?
		if (!LCClusterList.contains(cluster)
				&& allClusterList.contains(cluster)) {
			setErrorMessage(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
		*/

		this.saveData();
		setErrorMessage(null);
		nextEnabled = true;
		setPageComplete(true);
	}
	
	private void skipToNextPanel() {
		setErrorMessage(null);
		nextEnabled = true;
		setPageComplete(true);
	}

	private void addClusterToComboSmall() {

		if (!isLoadSmall) {
			for (int i = 0; i < LCClusterList.size(); i++) {
				this.clusterCombo.add((String) LCClusterList.get(i));
			}

			for (int i = 0; i < originalNodeList.size(); i++) {
				selectNodeList.clear();
				nodeCheckButtons[i].setEnabled(true);
				nodeCheckButtons[i].setSelection(false);
				nodeCheckButtons[i].setVisible(true);
				nodeCheckButtons[i].setText((String) originalNodeList.get(i));
				serverMemberText[i].setEnabled(false);
				serverMemberText[i].setVisible(true);
				serverMemberText[i].setText("server" + (i + 1));
			}

			isLoadSmall = true;
		} else {
			this.clusterCombo.removeAll();
			this.clusterCombo.setText("ICCluster");
			for (int i = 0; i < LCClusterList.size(); i++) {
				this.clusterCombo.add((String) LCClusterList.get(i));
			}

			for (int i = 0; i < originalNodeList.size(); i++) {
				selectNodeList.clear();
				nodeCheckButtons[i].setEnabled(true);
				nodeCheckButtons[i].setSelection(false);
				nodeCheckButtons[i].setVisible(true);
				nodeCheckButtons[i].setText((String) originalNodeList.get(i));
				serverMemberText[i].setEnabled(false);
				serverMemberText[i].setVisible(true);
				serverMemberText[i].setText("server" + (i + 1));
			}
		}
	}

	private void loadNodeForMedium() {

		if (!isLoadMedium || isRecordingMode) {
			log.debug("load cluster for medium ");
			log.debug("setExisted_Clusters ");
			log.info("LCClusterList : " + LCClusterList.toString());

			tpTreeMedium.setExisted_Clusters((String[]) LCClusterList
					.toArray(new String[LCClusterList.size()]), true);
			log.debug("tpTreeMedium existed clusters : "
					+ tpTreeMedium.getExisted_Clusters());

			if (isRecordingMode) {
				activitiesSelectNodeServerPairList.clear();
				blogsSelectNodeServerPairList.clear();
				communitiesSelectNodeServerPairList.clear();
				dogearSelectNodeServerPairList.clear();
				homepageSelectNodeServerPairList.clear();
				profilesSelectNodeServerPairList.clear();
				filesSelectNodeServerPairList.clear();
				forumSelectNodeServerPairList.clear();
				wikisSelectNodeServerPairList.clear();
				mobileSelectNodeServerPairList.clear();
				moderationSelectNodeServerPairList.clear();
				searchSelectNodeServerPairList.clear();
				newsSelectNodeServerPairList.clear();
				metricsSelectNodeServerPairList.clear();
				ccmSelectNodeServerPairList.clear();
				rteSelectNodeServerPairList.clear();
			}

			log.debug("original node list : " + originalNodeList.toString());
			// OS400_Enablement
			//      OS400 does not support Filenet.
			String newCCMSelected;
			if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
				newCCMSelected = null;
			} else {
				newCCMSelected = this.profile.getUserData("user.ccm.existingDeployment");
			}

			for (int i = 0; i < originalNodeList.size(); i++) {

				String nodetemp = (String) originalNodeList.get(i);
				String serverTemp = "server" + (i + 1);

				for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
					// if (getIsFeatureSelected(featureID)) {
					if (featureID == CCM && null != newCCMSelected && newCCMSelected.equals("true")){
						continue;
					}
					getFeatureNodeServerPairList(featureID, MEDIUM_TOPOLOGY)
							.add(new NodeServerPair(
									getFeatureName(featureID),
									getDefaultClusterName(featureID,
											MEDIUM_TOPOLOGY),
									nodetemp,
									getDefaultClusterName(featureID,
											MEDIUM_TOPOLOGY) + "_" + serverTemp,
									i == 0 ? true : false, i == 0 ? true
											: false));
						log.debug("getFeatureNodeServerPairList("
								+ getFeatureName(featureID)
								+ ", MEDIUM_TOPOLOGY) "
								+ getFeatureNodeServerPairList(featureID,
										MEDIUM_TOPOLOGY).size());
					// }
				}

			}
			tpTreeMedium
					.setDefaultDatas(generateDefaultTreeData(originalNodeList));
			tpTreeMedium.setExistedClusterDatas(generateExistedTreeData());
			// tpTreeMedium.setExisted_Clusters((String[])
			// LCClusterList.toArray(new String[LCClusterList.size()]), true);
			tpTreeMedium.setAll_Clusters((String[]) allClusterList
					.toArray(new String[allClusterList.size()]));
			List<TopologyClusterInfo> inputDatas = this.generateTreeData(MEDIUM_TOPOLOGY);
			tpTreeMedium.setInputDatas(inputDatas);
			tpTreeMedium.setTopologyType(MEDIUM_TOPOLOGY);
			if(inputDatas.size() > 0)
				isLoadMedium = true;
			tpTreeMedium.update(true);
			setPageComplete(MEDIUM_TOPOLOGY);
			log.debug("tpTreeMedium isCombox1 " + tpTreeMedium.isCombox());

		} else if (tpTreeMedium.getInputDatas().size() > 0) {
			// in case, the user go back and change the feature panel
			log.debug("loadNodeForMedium 2");
			log.debug("tpTreeMedium isCombox2 " + tpTreeMedium.isCombox());
			tpTreeMedium.setInputDatas(this.generateTreeData(MEDIUM_TOPOLOGY));
			setPageComplete(MEDIUM_TOPOLOGY);
			tpTreeMedium.update(true);

		}

	}

	private void loadNodeForLarge() {
		if (!isLoadLarge || isRecordingMode) {
			log.debug("load cluster for large ");
			log.debug("setExisted_Clusters ");
			log.debug("LCClusterList : " + LCClusterList.toString());

			tpTreeLarge.setExisted_Clusters((String[]) LCClusterList.toArray(new String[LCClusterList.size()]), false);

			if (isRecordingMode) {
				activitiesSelectNodeServerPairListLarge.clear();
				blogsSelectNodeServerPairListLarge.clear();
				communitiesSelectNodeServerPairListLarge.clear();
				dogearSelectNodeServerPairListLarge.clear();
				homepageSelectNodeServerPairListLarge.clear();
				profilesSelectNodeServerPairListLarge.clear();
				filesSelectNodeServerPairListLarge.clear();
				forumSelectNodeServerPairListLarge.clear();
				wikisSelectNodeServerPairListLarge.clear();
				mobileSelectNodeServerPairListLarge.clear();
				moderationSelectNodeServerPairListLarge.clear();
				searchSelectNodeServerPairListLarge.clear();
				newsSelectNodeServerPairListLarge.clear();
				metricsSelectNodeServerPairListLarge.clear();
				ccmSelectNodeServerPairListLarge.clear();
				rteSelectNodeServerPairListLarge.clear();
			}

			log.debug("original node list : " + originalNodeList.toString());
			
			// OS400_Enablement
			//      OS400 does not support Filenet.
			String newCCMSelected;
			if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
				newCCMSelected = null;
			} else {
				newCCMSelected = this.profile.getUserData("user.ccm.existingDeployment");
			}

			for (int i = 0; i < originalNodeList.size(); i++) {

				String nodetemp = (String) originalNodeList.get(i);
				String serverTemp = "server" + (i + 1);

				for (int feature = ACTIVITIES; feature <= IC360; feature++) {
					if (feature == CCM && null != newCCMSelected && newCCMSelected.equals("true")){
						continue;
					}
					getFeatureNodeServerPairList(feature, LARGE_TOPOLOGY).add(
							new NodeServerPair(getFeatureName(feature),
									getDefaultClusterName(feature,
											LARGE_TOPOLOGY), nodetemp,
									getDefaultClusterName(feature,
											LARGE_TOPOLOGY) + "_" + serverTemp,
									i == 0 ? true : false, i == 0 ? true
											: false));
				}

			}
			tpTreeLarge
					.setDefaultDatas(generateDefaultTreeData(originalNodeList));
			tpTreeLarge.setExistedClusterDatas(generateExistedTreeData());
			// tpTreeLarge.setExisted_Clusters((String[])
			// LCClusterList.toArray(new String[LCClusterList.size()]), false);
			tpTreeLarge.setAll_Clusters((String[]) allClusterList.toArray(new String[allClusterList.size()]));
			List<TopologyClusterInfo> inputDatas = this.generateTreeData(LARGE_TOPOLOGY);
			tpTreeLarge.setInputDatas(inputDatas);
			tpTreeLarge.setTopologyType(LARGE_TOPOLOGY);
			tpTreeLarge.update(true);
			log.debug("tpTreeLarge isCombox1 " + tpTreeLarge.isCombox());
			if(inputDatas.size()>0)
				isLoadLarge = true;
			setPageComplete(LARGE_TOPOLOGY);
		} else if (tpTreeLarge.getInputDatas().size() > 0) {
			log.debug("loadNodeForLarge 2");
			log.debug("tpTreeLarge isCombox2 " + tpTreeLarge.isCombox());
			// in case, the user go back and change the feature panel
			tpTreeLarge.setInputDatas(this.generateTreeData(LARGE_TOPOLOGY));
			tpTreeLarge.update(true);
			setPageComplete(LARGE_TOPOLOGY);
		}
	}

	private void loadClusterInfo() {
		profile = getProfile();
		String clusterListStr = profile.getUserData("user.clusterlist");
		String nodeListStr = profile.getUserData("user.nodeslist");
		String clusteFullinfo = profile.getUserData("user.clusterfullinfo");

		if(existedClusters == null){
			existedClusters = new ArrayList<String>();
			String[] aryClusters = clusterListStr.trim().split(",");
			for(int i=0;i<aryClusters.length;i++){
				existedClusters.add(aryClusters[i]);
			}
		}
		
		log.info("loadClusterInfo clusterListStr : " + clusterListStr);
		log.info("loadClusterInfo nodeListStr : " + nodeListStr);
		log.info("loadClusterInfo clusteFullinfo : " + clusteFullinfo);

		if (!isLoadCluster || isRecordingMode) {

			// first load for all cluster including lc cluster and others
			if (allClusterList.size() == 0) {

				if (clusterListStr != null && !clusterListStr.trim().equals("")) {
					String[] clusters = clusterListStr.split(",");
					for (int i = 0; i < clusters.length; i++) {
						if (!allClusterList.contains(clusters[i])) {
							allClusterList.add(clusters[i]);
						}
					}
				}
			}

			log.info("loadClusterInfo all cluster in was : "
					+ allClusterList.toString());
			// for lc cluster

			//String lcclusteStr = this.readClusterInfoProperties("clusterList");
			String lcclusteStr = (isModify()||isUpdate())?clusterListStr:null;
			// String lcclusteStr = "existed_clusterLC";

			// first load for cluster
			if (LCClusterList.size() == 0) {

				if (lcclusteStr != null && !lcclusteStr.trim().equals("")) {
					String[] clusters = lcclusteStr.split(",");
					for (int i = 0; i < clusters.length; i++) {
						if (!LCClusterList.contains(clusters[i])
								&& allClusterList.contains(clusters[i])) {
							LCClusterList.add(clusters[i]);
						}
					}
				}
			}

			log.info("loadClusterInfo lc clusters : "
					+ LCClusterList.toString());

			if (isRecordingMode) {
				originalNodeList.clear();
			}

			// first load for nodes info
			if (originalNodeList.size() == 0) {

				if (nodeListStr != null && !nodeListStr.trim().equals("")) {
					String[] nodes = nodeListStr.split(",");

					for (int i = 0; i < nodes.length; i++) {
						if (!originalNodeList.contains(nodes[i])) {
							this.originalNodeList.add(nodes[i]);
						}
					}
				}
			}

			log.info("loadClusterInfo original nodes in was : "
					+ originalNodeList.toString());

			if(clusteFullinfo != null)
				isLoadCluster = true;

			// first load exist cluster info
			if (clusteFullinfo == null || clusteFullinfo.trim().equals("")) {
				return;
			}

			if (existClusterInfoMap.size() == 0) {
				existClusterInfoMap = this
						.getExistedClusterInfo(clusteFullinfo);
			}

		}
		deleteRemovedClusters();
	}

	private ArrayList getExistNodeServerPairList(String clusterName) {
		log.debug("getExistNodeServerPairList(String)");
		ArrayList nodeList = new ArrayList();
		List<NodeServerPair> nodeinfoList = (ArrayList) existClusterInfoMap
				.get(clusterName);
		log.debug("getExistNodeServerPairList existClusterInfoMap size = "
				+ existClusterInfoMap.size());
		nodeList.addAll(nodeinfoList);
		return nodeList;
	}

	private String parseSelectNodeToStr(ArrayList nodeList) {

		StringBuffer nodeStr = new StringBuffer();
		for (int i = 0; i < nodeList.size(); i++) {
			nodeStr.append(nodeList.get(i));
			nodeStr.append(";");
		}

		return nodeStr.toString();
	}

	private void showSelectNodeDialog(Text nodesLabel, Text serverMembersLabel,
			String clusterName, ArrayList selectNodeServerPairs, String flag) {

	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_TOPOLOGY_PANEL;
	}

	private String wasLoc = "";
	private String profileName = "";

	@Override
	public void setVisible(boolean visible) {

		boolean isFeatureChange = isFeaturesChange();
		log.debug("For deplyment panel features is changed : "
				+ isFeatureChange);
		if (isFeatureChange) {
			createControl(parent);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
		// check if the wasLoc or profile reselect
		profile = getProfile();
		if (profile != null) {

			String newWasLoc = profile.getUserData("user.was.install.location");
			String newProfile = profile.getUserData("user.was.profileName");
			if (newWasLoc != null && newProfile != null) {

				if (!newWasLoc.equals(wasLoc)) {
					createControl(parent);
					return;
				}

				if (!newProfile.equals(profileName)) {
					createControl(parent);
					return;
				}
			}
		}

	}

	private int size = 0;
	private boolean isFeaturesChange() {
		int currentSize = this.getOffering().size();
		if(size == currentSize){
			return false;
		}else{
			size = currentSize;
			return true;
		}
	}

	public class NodeSelectDialog extends Dialog {

		private ArrayList originalNodeList = new ArrayList();
		private ArrayList selectNodeServerPairList = new ArrayList();

		private Text nodeTextLabel = null;
		private Text serverMemberTextLabel = null;

		private Button[] nodeCheckButtonsDialog = null;
		private Text[] serverMemberText = null;
		private Label[] nodeLabelsDialog = null;

		public NodeSelectDialog(Shell parentShell) {
			super(parentShell);
		}

		protected Control createDialogArea(Composite parent) {

			Composite nodeSelectContainer = (Composite) super
					.createDialogArea(parent);

			GridData combogridData = new GridData(GridData.BEGINNING);
			combogridData.horizontalSpan = 1;
			combogridData.widthHint = 100;

			GridLayout nodeSelectLayout = new GridLayout();
			nodeSelectLayout.numColumns = 3;
			nodeSelectContainer.setLayout(nodeSelectLayout);
			GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.verticalIndent = 10;
			// gd2.horizontalIndent = 5;
			nodeSelectContainer.setLayoutData(gd2);

			nodeCheckButtonsDialog = new Button[originalNodeList.size()];
			serverMemberText = new Text[originalNodeList.size()];
			nodeLabelsDialog = new Label[originalNodeList.size()];

			for (int i = 0; i < originalNodeList.size(); i++) {
				nodeCheckButtonsDialog[i] = new Button(nodeSelectContainer,
						SWT.CHECK);
				nodeCheckButtonsDialog[i].setEnabled(true);
				nodeCheckButtonsDialog[i].setVisible(true);

				nodeLabelsDialog[i] = new Label(nodeSelectContainer, SWT.LEFT);
				nodeLabelsDialog[i].setText((String) originalNodeList.get(i));
				nodeLabelsDialog[i].setLayoutData(combogridData);
				nodeLabelsDialog[i].setEnabled(true);
				nodeLabelsDialog[i].setVisible(true);

				serverMemberText[i] = new Text(nodeSelectContainer, SWT.BORDER
						| SWT.SINGLE);
				serverMemberText[i].setText("server" + (i + 1));
				serverMemberText[i].setLayoutData(combogridData);
				serverMemberText[i].setEnabled(false);
				serverMemberText[i].setVisible(true);

				for (int j = 0; j < previousNodesList.size(); j++) {
					String tempNode = (String) previousNodesList.get(j);
					if (tempNode.equals(originalNodeList.get(i))) {
						nodeCheckButtonsDialog[i].setSelection(true);
						serverMemberText[i].setEnabled(true);
					}
				}

				nodeCheckButtonsDialog[i]
						.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								for (int m = 0; m < originalNodeList.size(); m++) {
									if (nodeCheckButtonsDialog[m]
											.getSelection() == true) {
										serverMemberText[m].setEnabled(true);
									} else {
										serverMemberText[m].setEnabled(false);
									}
								}
							}

						});
			}

			return nodeSelectContainer;
		}

		protected void configureShell(Shell newShell) {

			super.configureShell(newShell);
			// Dialog Title
			newShell.setText(Messages.DEPOLOGY_CLUSTER_TABLE_NODES_BTN);

		}

		// protected Point getInitialSize() {
		// return new Point(400, 120);
		// }

		protected void createButtonsForButtonBar(Composite parent) {
			Button okBut = createButton(parent, IDialogConstants.OK_ID,
					IDialogConstants.OK_LABEL, true);
			Button cancelBut = createButton(parent, IDialogConstants.CANCEL_ID,
					IDialogConstants.CANCEL_LABEL, false);
		}

		protected void buttonPressed(int buttonId) {

			super.buttonPressed(buttonId);
		}

		public ArrayList getOriginalNodeList() {
			return originalNodeList;
		}

		public void setOriginalNodeList(ArrayList originalNodeList) {
			this.originalNodeList = originalNodeList;
		}

		public Text getNodeTextLabel() {
			return nodeTextLabel;
		}

		public void setNodeTextLabel(Text nodeTextLabel) {
			this.nodeTextLabel = nodeTextLabel;
		}

		public Text getServerMemberTextLabel() {
			return serverMemberTextLabel;
		}

		public void setServerMemberTextLabel(Text serverMemberTextLabel) {
			this.serverMemberTextLabel = serverMemberTextLabel;
		}

		private ArrayList previousNodesList = new ArrayList();
		private String clusterName;

		public void setInputText(String selectNodesStr) {

			if (selectNodesStr == null || selectNodesStr.trim().equals("")) {
				return;
			}

			StringTokenizer tokenizer = new StringTokenizer(selectNodesStr, ";");
			if (tokenizer != null) {
				while (tokenizer.hasMoreTokens()) {
					String nodeName = tokenizer.nextToken();
					if (nodeName != null && !nodeName.trim().equals("")) {
						if (!previousNodesList.contains(nodeName)) {
							previousNodesList.add(nodeName);
						}
					}
				}

			}
		}

		private void setClusterName(String clusterName) {
			this.clusterName = clusterName;
		}

		public ArrayList getSelectNodeServerPairList() {
			return selectNodeServerPairList;
		}

		public void setSelectNodeServerPairList(
				ArrayList selectNodeServerPairList) {
			this.selectNodeServerPairList = selectNodeServerPairList;
		}

	}

	// TODO copy to GUI
	private List<NodeServerPair> getAllExistNodeServerPairList(String cluster) {
		List<NodeServerPair> results = new ArrayList<NodeServerPair>();
		for (int i = 0; i < LCClusterList.size(); i++) {
			String curCluster = (String) LCClusterList.get(i);
			List<String> features = findFeatures(curCluster);
			List<NodeServerPair> list = getExistNodeServerPairList(curCluster);
			for (int j = 0; j < list.size(); j++) {
				for (int k = 0; k < features.size(); k++) {
					NodeServerPair cur = (NodeServerPair) list.get(j).clone();
					cur.setApplicationName(features.get(k));
					if (cur.getClusterName().equalsIgnoreCase(cluster))
						results.add(cur);
				}
			}
		}
		return results;
	}

	private List<NodeServerPair> getAllExistNodeServerPairList() {
		List<NodeServerPair> results = new ArrayList<NodeServerPair>();
		for (int i = 0; i < LCClusterList.size(); i++) {
			String curCluster = (String) LCClusterList.get(i);
			List<String> features = findFeatures(curCluster);
			List<NodeServerPair> list = getExistNodeServerPairList(curCluster);
			for (int j = 0; j < list.size(); j++) {
				for (int k = 0; k < features.size(); k++) {
					NodeServerPair cur = (NodeServerPair) list.get(j).clone();
					cur.setApplicationName(features.get(k));
					results.add(cur);
				}
			}
		}
		return results;
	}

	private List<NodeServerPair> getExistFeatureNodeServerPairList(
			String feature) {
		List<NodeServerPair> all = getAllExistNodeServerPairList();
		List<NodeServerPair> result = new ArrayList<NodeServerPair>();
		for (NodeServerPair cur : all) {
			if (cur.getApplicationName().equalsIgnoreCase(feature))
				result.add(cur);
		}
		return result;
	}

	/**
	 * in modify-remove, if one feature is to be removed and the removal of that
	 * feature will cause the existing cluster to be removed together,then
	 * remove the cluster from the existing cluster list
	 */
	// TODO check
	private void deleteRemovedClusters() {
		List<String> features = getRemovedFeatures();

		if (features == null || features.size() == 0)
			return;
		List<String> clusters = new ArrayList<String>();
		for (String feature : features) {
			List<NodeServerPair> pair = getExistFeatureNodeServerPairList(feature);
			if (pair == null || pair.size() == 0)
				continue;
			String clustername = pair.get(0).getClusterName();
			if (clustername == null)
				continue;
			if (clusters.contains(clustername))
				continue;
			clusters.add(clustername);
		}
		if (clusters.size() == 0)
			return;
		// 0 for delete the cluster from lcclusterlist, 1 for keep the cluster
		// in lcclusterlist
		List<Integer> clusterFeatureCnt = new ArrayList<Integer>();
		outer: for (int i = 0; i < clusters.size(); i++) {
			String cluster = clusters.get(i);
			List<NodeServerPair> pair = getAllExistNodeServerPairList(cluster);
			for (NodeServerPair cur : pair) {
				if (TextCustomPanelUtils.containsIgnoreCase(features,
						cur.getApplicationName())) {
					continue;
				}
				clusterFeatureCnt.add(1);
				continue outer;
			}
			clusterFeatureCnt.add(0);
		}
		List<String> removeClusters = new ArrayList<String>();
		for (int i = 0; i < clusters.size(); i++) {
			if (clusterFeatureCnt.get(i) == 0)
				removeClusters.add(clusters.get(i));
		}
		for (String cur : removeClusters) {
			if (LCClusterList.contains(cur))
				LCClusterList.remove(cur);
		}
	}

	class ClsuterNodeInfo {

		public String nodeName;
		public String serverName;
	}

	private String readClusterInfoProperties(String infoType) {

		profile = this.getProfile();
		if (profile != null) {
			try {

				String appDir = profile
						.getUserData("user.connections.install.location");
				String clusterInfoPath = appDir + File.separator
						+ "config.properties";
				log.debug("config property path : " + clusterInfoPath);
				File clusterProFile = new File(clusterInfoPath);
				if (!clusterProFile.exists()) {
					return null;
				}

				InputStream insrc = new BufferedInputStream(
						new FileInputStream(clusterProFile));
				Properties props = new Properties();
				props.load(insrc);

				insrc.close();
				ArrayList temClusterList = new ArrayList();
				if (infoType.equals("clusterList")) {
					for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
						String clusterName = props
								.getProperty(getFeatureName(featureID)
										+ ".ClusterName");
						if (clusterName != null
								&& !clusterName.trim().equals("")) {
							if (!temClusterList.contains(clusterName)) {
								temClusterList.add(clusterName);
							}
						}
					}

					StringBuffer temp = new StringBuffer();
					for (int i = 0; i < temClusterList.size(); i++) {
						temp.append(temClusterList.get(i) + ",");
					}
					log.debug(temp.toString());
					return temp.toString();
				} else {

					StringBuffer clusterInfoTemp = new StringBuffer();

					String clusterName = props.getProperty("news.ClusterName");
					String firstNodeInfo = props
							.getProperty("news.FirstNodeName");
					String secondaryNodeInfo = props
							.getProperty("news.SecondaryNodesNames");
					String serverInfo = props.getProperty("news.serverInfo");

					String fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("search.ClusterName");
					firstNodeInfo = props.getProperty("search.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("search.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("profiles.ClusterName");
					firstNodeInfo = props.getProperty("profiles.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("profiles.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("communities.ClusterName");
					firstNodeInfo = props
							.getProperty("communities.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("communities.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("dogear.ClusterName");
					firstNodeInfo = props.getProperty("dogear.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("dogear.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("wikis.ClusterName");
					firstNodeInfo = props.getProperty("wikis.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("wikis.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("files.ClusterName");
					firstNodeInfo = props.getProperty("files.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("files.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("forums.ClusterName");
					firstNodeInfo = props.getProperty("forums.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("forums.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("mobile.ClusterName");
					firstNodeInfo = props.getProperty("mobile.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("mobile.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("moderation.ClusterName");
					firstNodeInfo = props
							.getProperty("moderation.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("moderation.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("activities.ClusterName");
					firstNodeInfo = props
							.getProperty("activities.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("activities.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("blogs.ClusterName");
					firstNodeInfo = props.getProperty("blogs.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("blogs.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("homepage.ClusterName");
					firstNodeInfo = props.getProperty("homepage.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("homepage.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("metrics.ClusterName");
					firstNodeInfo = props.getProperty("metrics.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("metrics.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}
					
					clusterName = props.getProperty("ccm.ClusterName");
					firstNodeInfo = props.getProperty("ccm.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("ccm.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					log.debug(clusterInfoTemp.toString());
					return clusterInfoTemp.toString();

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
				e.printStackTrace();
			}
		}

		return null;
	}

	private Map<String, List<NodeServerPair>> getExistedClusterInfo(
			String clusterfullInfo) {
		log.debug("getExistedClusterInfo");
		Map<String, List<NodeServerPair>> map = new HashMap<String, List<NodeServerPair>>();
		List<NodeServerPair> list = new ArrayList<NodeServerPair>();
		String featureName = null, serverName = null, nodeName = null, clusterName = null;
		boolean isFirstNode = true;
		// cluster30:yanyunNode02#cluster30_server1,yanyunNode03#cluster30_server2
		// ,;test1:yanyunNode02#test1_server1,yanyunNode04#test1_server3,;
		StringTokenizer clusterfullInfoTokenizer = new StringTokenizer(
				clusterfullInfo, ";");
		while (clusterfullInfoTokenizer.hasMoreTokens()) {
			// cluster30:yanyunNode02#cluster30_server1,yanyunNode03#
			// cluster30_server2,
			String clusterinfo = clusterfullInfoTokenizer.nextToken();
			StringTokenizer clusterinfoTokenizer = new StringTokenizer(
					clusterinfo, ":");
			if (clusterinfoTokenizer.hasMoreTokens()) {
				// cluster30
				clusterName = clusterinfoTokenizer.nextToken();
				log.debug("getExistedClusterInfo clusterName : " + clusterName);
				if (clusterinfoTokenizer.hasMoreTokens()) {
					// yanyunNode02#cluster30_server1,yanyunNode03#
					// cluster30_server2,
					String nodeserverFullInfo = clusterinfoTokenizer
							.nextToken();
					StringTokenizer nodeserverFullInfoTokenizer = new StringTokenizer(
							nodeserverFullInfo, ",");
					isFirstNode = true;
					while (nodeserverFullInfoTokenizer.hasMoreTokens()) {
						// yanyunNode02#cluster30_server1
						String nodeserverInfo = nodeserverFullInfoTokenizer
								.nextToken();
						StringTokenizer nodeserverInfoTokenizer = new StringTokenizer(
								nodeserverInfo, "#");
						if (nodeserverInfoTokenizer.hasMoreTokens()) {
							// yanyunNode02
							nodeName = nodeserverInfoTokenizer.nextToken();
							log.debug("getExistedClusterInfo nodeName : "
									+ nodeName);
							if (nodeserverInfoTokenizer.hasMoreTokens()) {
								// cluster30_server1
								serverName = nodeserverInfoTokenizer
										.nextToken();
								log.debug("getExistedClusterInfo serverName : "
										+ serverName);
							}
							featureName = getFeatureName(findFeature(clusterName));
							log.debug("getExistedClusterInfo featureName : "
									+ featureName);
							log.debug("getExistedClusterInfo isFirstNode : "
									+ isFirstNode);
							list.add(new NodeServerPair(featureName,
									clusterName, nodeName, serverName, true,
									isFirstNode));
							isFirstNode = false;
						}
					}
				}
				map.put(clusterName, list);
				list = new ArrayList<NodeServerPair>();
			}
		}

		return map;
	}

	private int findFeature(String clusterName) {
		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
			String tmpClusterName = profile.getUserData("user."
					+ getFeatureName(featureID) + ".clusterName");
			if (tmpClusterName != null && !tmpClusterName.trim().equals("")) {
				if (tmpClusterName.equalsIgnoreCase(clusterName)) {
					return featureID;
				}
			}
		}
		return -1;
	}

	private List<String> findFeatures(String clusterName) {
		List<String> features = new ArrayList<String>();
		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
			String tmpClusterName = profile.getUserData("user."
					+ getFeatureName(featureID) + ".clusterName");
			if (tmpClusterName != null && !tmpClusterName.trim().equals("")) {
				if (tmpClusterName.equalsIgnoreCase(clusterName)) {
					features.add(getFeatureName(featureID));
				}
			}
		}
		return features;
	}

	public void saveWasAdmin() {
		log.debug("saveWasAdmin");
		String id = profile.getUserData("user.was.adminuser.id");
		String pw = profile.getUserData("user.was.adminuser.password");
		if (id == null) {
			id = profile.getUserData("user.news.adminuser.id");
		}
		if (pw == null) {
			pw = profile.getUserData("user.news.adminuser.password");
		}

		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
			profile.setUserData("user." + getFeatureName(featureID)
					+ ".adminuser.id", id);
			profile.setUserData("user." + getFeatureName(featureID)
					+ ".adminuser.password", pw);
		}
	}

	private class NodeServerPair implements Cloneable {
		public String applicationName;
		public String clusterName;
		public String nodeName;
		public String serverMemberName;
		public boolean isSelected;
		public boolean isFirstNode;

		public NodeServerPair(String applicationName, String clusterName,
				String nodeName, String serverMemberName, boolean isSelected,
				boolean isFirstNode) {
			super();
			this.applicationName = applicationName;
			this.clusterName = clusterName;
			this.nodeName = nodeName;
			this.serverMemberName = serverMemberName;
			this.isSelected = isSelected;
			this.isFirstNode = isFirstNode;
		}

		public NodeServerPair(String nodeName, String serverMemberName) {
			super();
			this.nodeName = nodeName;
			this.serverMemberName = serverMemberName;
		}

		@Override
		public NodeServerPair clone() {
			try {
				return (NodeServerPair) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getClusterName() {
			return clusterName;
		}

		public void setClusterName(String clusterName) {
			this.clusterName = clusterName;
		}

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public String getServerMemberName() {
			return serverMemberName;
		}

		public void setServerMemberName(String serverMemberName) {
			this.serverMemberName = serverMemberName;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public boolean isFirstNode() {
			return isFirstNode;
		}

		public void setFirstNode(boolean isFirstNode) {
			this.isFirstNode = isFirstNode;
		}
	}

	/*
	 * @param type 0 is for nodes list, 1 is for servers list
	 */
	private String getFeatureSelectNodeServerList(int feature, int type,
			int topologyType) {
		List<NodeServerPair> list = getFeatureNodeServerPairList(feature,
				topologyType);
		StringBuffer nodeBuf = new StringBuffer();
		StringBuffer serverBuf = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			NodeServerPair pair = list.get(i);
			nodeBuf.append(pair.nodeName);
			nodeBuf.append(";");
			serverBuf.append(pair.serverMemberName);
			serverBuf.append(";");
		}
		if (type == 0)
			return nodeBuf.toString();
		else
			return serverBuf.toString();
	}

	private boolean containsCluster(int feature, String clusterName,
			int topologyType) {
		List<NodeServerPair> list = getFeatureNodeServerPairList(feature,
				topologyType);
		for (int i = 0; i < list.size(); i++) {
			NodeServerPair pair = list.get(i);
			if (pair.clusterName.equalsIgnoreCase(clusterName))
				return true;
		}
		return false;
	}

	public void setTreeInputs(int topologyType) {
		log.debug("topologyType");
		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
			if (getIsFeatureSelected(featureID)) {
				if (featureID == CCM && this.profile.getUserData("user.ccm.existingDeployment").equals("true")){
					continue;
				}
				TopologyClusterInfo tc = findFeatureTreeData(featureID,
						topologyType);
				List<NodeServerPair> pairList = getFeatureNodeServerPairList(
						featureID, topologyType);
				if (pairList.size() > 0)
					pairList.clear();
				// primary node
				NodeServerPair pair = new NodeServerPair(
						tc.getApplicationName(), tc.getClusterName(),
						tc.getNodeName(), tc.getServerName(),
						tc.isNodeSelected(), true);
				pairList.add(pair);
				List<TopologyClusterInfo> subTcList = tc.getChildren();
				if (subTcList.size() > 0) {
					for (int j = 0; j < subTcList.size(); j++) {
						TopologyClusterInfo subTc = subTcList.get(j);
						pair = new NodeServerPair(tc.getApplicationName(),
								tc.getClusterName(), subTc.getNodeName(),
								subTc.getServerName(), subTc.isNodeSelected(),
								false);
						pairList.add(pair);
					}
				}
				log.debug("setTreeInputs pairList size = " + pairList.size());
			}
		}
	}

	public TopologyClusterInfo findFeatureTreeData(int featureID,
			int topologyType) {
		log.debug("findFeatureTreeData " + getFeatureName(featureID));
		List<TopologyClusterInfo> inputDatas = null;
		if (topologyType == MEDIUM_TOPOLOGY) {
			inputDatas = tpTreeMedium.getInputDatas();
		} else {
			inputDatas = tpTreeLarge.getInputDatas();
		}

		for (int i = 0; i < inputDatas.size(); i++) {
			TopologyClusterInfo tc = inputDatas.get(i);
			if (tc.getApplicationName() == getFeatureName(featureID))
				return tc;
		}
		log.debug("Error in findFeatureTreeData(): not find "
				+ getFeatureName(featureID));
		return null;
	}

	public void resetDefaultTreeData(int topologyType) {
		log.debug("resetDefaultTreeData");
		List<TopologyClusterInfo> topoList = null;

		if (topologyType == MEDIUM_TOPOLOGY
				&& default_topoInfoMediumList.size() > 0) {
			topoList = getTopoLogyListClone(default_topoInfoMediumList);
			topoInfoMediumList = topoList;
			log.debug("resetDefaultTreeData 1");
		} else if (topologyType == LARGE_TOPOLOGY
				&& default_topoInfoLargeList.size() > 0) {
			topoList = getTopoLogyListClone(default_topoInfoLargeList);
			topoInfoLargeList = topoList;
			log.debug("resetDefaultTreeData 2");
		} else
			return;

		getTopologyTreeViewer(topologyType).setInputDatas(topoList);
		getTopologyTreeViewer(topologyType).update(true);
		saveDataMediumLarge(topologyType);
	}

	private List<TopologyClusterInfo> getTopoLogyListClone(
			List<TopologyClusterInfo> topoList) {
		List<TopologyClusterInfo> clone = new ArrayList<TopologyClusterInfo>();
		for (int i = 0; i < topoList.size(); i++) {
			TopologyClusterInfo tc = topoList.get(i);
			clone.add(getTopologyClone(tc));
		}
		return clone;
	}

	private TopologyClusterInfo getTopologyClone(TopologyClusterInfo tc) {
		TopologyClusterInfo newTc = new TopologyClusterInfo();
		newTc.setApplicationName(tc.getApplicationName());
		newTc.setClusterName(tc.getClusterName());
		newTc.setNodeSelected(tc.isNodeSelected());
		newTc.setIsFirstOne(tc.getIsFirstOne());
		newTc.setNodeName(tc.getNodeName());
		newTc.setServerName(tc.getServerName());
		List list0 = new ArrayList();
		if (tc.getChildren().size() > 0) {
			List subTcList = tc.getChildren();
			for (int j = 0; j < subTcList.size(); j++) {
				TopologyClusterInfo subTc = (TopologyClusterInfo) subTcList
						.get(j);
				TopologyClusterInfo newSubTc = new TopologyClusterInfo();
				newSubTc.setApplicationName(subTc.getApplicationName());
				newSubTc.setClusterName(subTc.getClusterName());
				newSubTc.setNodeSelected(subTc.isNodeSelected());
				newSubTc.setIsFirstOne(subTc.getIsFirstOne());
				newSubTc.setNodeName(subTc.getNodeName());
				newSubTc.setServerName(subTc.getServerName());
				list0.add(newSubTc);
			}
		}
		newTc.setChildren(list0);
		return newTc;
	}

	public List<TopologyClusterInfo> generateTreeData(int topologyType) {
		log.debug("generateTreeData");
		if (topologyType == MEDIUM_TOPOLOGY && topoInfoMediumList.size() > 0)
			topoInfoMediumList.clear();
		else if (topologyType == LARGE_TOPOLOGY && topoInfoLargeList.size() > 0)
			topoInfoLargeList.clear();
		log.debug("generateTreeData2");
		List<TopologyClusterInfo> topoInfoList = new ArrayList<TopologyClusterInfo>();

		for (int feature = ACTIVITIES; feature <= IC360; feature++) {
			if (getIsFeatureSelected(feature)) {
				if (feature == CCM && this.profile.getUserData("user.ccm.existingDeployment").equals("true")){
					continue;
				}
				List<NodeServerPair> pairList = getFeatureNodeServerPairList(
						feature, topologyType);
				log.debug("feature " + feature + " pariList size = "
						+ pairList.size());
				if (pairList.size() > 0) {
					TopologyClusterInfo t0 = new TopologyClusterInfo();
					t0.setApplicationName(pairList.get(0).getApplicationName());
					t0.setClusterName(pairList.get(0).getClusterName());
					t0.setNodeSelected(pairList.get(0).isSelected());
					t0.setIsFirstOne(true);
					t0.setNodeName(pairList.get(0).getNodeName());
					t0.setServerName(pairList.get(0).getServerMemberName());
					pairList.get(0).setSelected(true);
					log.debug("node0 is " + pairList.get(0).getNodeName());
					List list0 = new ArrayList();
					for (int i = 1; i < pairList.size(); i++) {
						NodeServerPair pair = pairList.get(i);
						TopologyClusterInfo t = new TopologyClusterInfo();
						t.setApplicationName(pair.getApplicationName());
						t.setClusterName(pair.getClusterName());
						t.setNodeSelected(pair.isSelected());
						t.setIsFirstOne(false);
						t.setNodeName(pair.getNodeName());
						t.setServerName(pair.getServerMemberName());
						pair.setSelected(false);
						list0.add(t);
					}
					t0.setChildren(list0);
					topoInfoList.add(t0);
				}
			}
		}
		if (topologyType == MEDIUM_TOPOLOGY) {
			topoInfoMediumList.addAll(topoInfoList);
			if (default_topoInfoMediumList.size() == 0) {
				default_topoInfoMediumList = getTopoLogyListClone(topoInfoMediumList);
				log.debug("default_topoInfoMediumList size "
						+ default_topoInfoMediumList.size());
			}
			return topoInfoMediumList;
		} else {
			topoInfoLargeList.addAll(topoInfoList);
			if (default_topoInfoLargeList.size() == 0)
				default_topoInfoLargeList = getTopoLogyListClone(topoInfoLargeList);
			return topoInfoLargeList;
		}
	}

	public List<TopologyClusterInfo> generateExistedTreeData() {
		log.debug("generateExistedTreeData");
		List<TopologyClusterInfo> topoInfoList = new ArrayList<TopologyClusterInfo>();

		if (existClusterInfoMap.size() > 0) {
			for (Iterator iter = existClusterInfoMap.keySet().iterator(); iter
					.hasNext();) {
				String clusterName = (String) iter.next();
				log.debug("generateExistedTreeData clusterName is "
						+ clusterName);
				List<NodeServerPair> list = (List<NodeServerPair>) existClusterInfoMap
						.get(clusterName);
				log.debug("generateExistedTreeData list size " + list.size());
				if (list.size() > 0) {
					TopologyClusterInfo t0 = new TopologyClusterInfo();
					t0.setApplicationName(list.get(0).getApplicationName());
					t0.setClusterName(clusterName);
					t0.setNodeSelected(true);
					t0.setIsFirstOne(true);
					t0.setNodeName(list.get(0).getNodeName());
					t0.setServerName(list.get(0).getServerMemberName());

					log.debug("generateExistedTreeData t0 ApplicationName = "
							+ list.get(0).getApplicationName());
					log.debug("generateExistedTreeData t0 ClusterName = "
							+ clusterName);
					log.debug("generateExistedTreeData t0 NodeName = "
							+ list.get(0).getNodeName());
					log.debug("generateExistedTreeData t0 ServerName = "
							+ list.get(0).getServerMemberName());

					List list0 = new ArrayList();
					if (list.size() > 1) {
						for (int i = 1; i < list.size(); i++) {
							NodeServerPair pair = list.get(i);
							TopologyClusterInfo subt = new TopologyClusterInfo();
							subt.setApplicationName(pair.getApplicationName());
							subt.setClusterName(clusterName);
							subt.setNodeSelected(true);
							subt.setIsFirstOne(false);
							subt.setNodeName(pair.getNodeName());
							subt.setServerName(pair.getServerMemberName());
							list0.add(subt);
							log.debug("generateExistedTreeData t" + i
									+ " ApplicationName = "
									+ pair.getApplicationName());
							log.debug("generateExistedTreeData t" + i
									+ " ClusterName = " + clusterName);
							log.debug("generateExistedTreeData t" + i
									+ " NodeName = " + pair.getNodeName());
							log.debug("generateExistedTreeData t" + i
									+ " ServerName = "
									+ pair.getServerMemberName());
						}
					}
					t0.setChildren(list0);
					topoInfoList.add(t0);
				}
			}
		}

		log.debug("existClusterInfoMap size is " + existClusterInfoMap.size());
		return topoInfoList;
	}

	public TopologyClusterInfo generateDefaultTreeData(List<String> nodeList) {
		log.debug("generateDefaultTreeData");
		TopologyClusterInfo tp = new TopologyClusterInfo();
		if (nodeList.size() > 0) {
			tp.setApplicationName("");
			tp.setClusterName("");
			tp.setIsFirstOne(true);
			tp.setNodeName(nodeList.get(0));
			tp.setNodeSelected(true);
			tp.setServerName("server0");
			List<TopologyClusterInfo> children = new ArrayList<TopologyClusterInfo>();
			tp.setChildren(children);

			if (nodeList.size() > 1) {
				for (int i = 1; i < nodeList.size(); i++) {
					TopologyClusterInfo subTc = new TopologyClusterInfo();
					subTc.setApplicationName("");
					subTc.setClusterName("");
					subTc.setIsFirstOne(false);
					subTc.setNodeName(nodeList.get(i));
					subTc.setNodeSelected(false);
					subTc.setServerName("server" + (i + 1));
					children.add(subTc);
				}
			}
		}
		return tp;
	}

	public void showErrorMessages(String messages) {
		setErrorMessage(messages);
		nextEnabled = false;
		setPageComplete(false);
	}

	public void setPageComplete(int topologyType) {
		if (topologyType == MEDIUM_TOPOLOGY ? isLoadMedium : isLoadLarge) {
			setErrorMessage(null);
			nextEnabled = true;
			setPageComplete(true);
			saveDataMediumLarge(topologyType);
		}
	}

	private List<NodeServerPair> getFeatureNodeServerPairList(int feature,
			int topologyType) {
		switch (feature) {
		case ACTIVITIES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return activitiesSelectNodeServerPairList;
			else
				return activitiesSelectNodeServerPairListLarge;
		case BLOGS:
			if (topologyType == MEDIUM_TOPOLOGY)
				return blogsSelectNodeServerPairList;
			else
				return blogsSelectNodeServerPairListLarge;
		case COMMUNITIES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return communitiesSelectNodeServerPairList;
			else
				return communitiesSelectNodeServerPairListLarge;
		case DOGEAR:
			if (topologyType == MEDIUM_TOPOLOGY)
				return dogearSelectNodeServerPairList;
			else
				return dogearSelectNodeServerPairListLarge;
		case HOMEPAGE:
			if (topologyType == MEDIUM_TOPOLOGY)
				return homepageSelectNodeServerPairList;
			else
				return homepageSelectNodeServerPairListLarge;
		case PROFILES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return profilesSelectNodeServerPairList;
			else
				return profilesSelectNodeServerPairListLarge;
		case FILES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return filesSelectNodeServerPairList;
			else
				return filesSelectNodeServerPairListLarge;
		case FORUM:
			if (topologyType == MEDIUM_TOPOLOGY)
				return forumSelectNodeServerPairList;
			else
				return forumSelectNodeServerPairListLarge;
		case WIKIS:
			if (topologyType == MEDIUM_TOPOLOGY)
				return wikisSelectNodeServerPairList;
			else
				return wikisSelectNodeServerPairListLarge;
		case MOBILE:
			if (topologyType == MEDIUM_TOPOLOGY)
				return mobileSelectNodeServerPairList;
			else
				return mobileSelectNodeServerPairListLarge;
		case MODERATION:
			if (topologyType == MEDIUM_TOPOLOGY)
				return moderationSelectNodeServerPairList;
			else
				return moderationSelectNodeServerPairListLarge;
		case SEARCH:
			if (topologyType == MEDIUM_TOPOLOGY)
				return searchSelectNodeServerPairList;
			else
				return searchSelectNodeServerPairListLarge;
		case METRICS:
			if (topologyType == MEDIUM_TOPOLOGY)
				return metricsSelectNodeServerPairList;
			else
				return metricsSelectNodeServerPairListLarge;
		case CCM:
			if (topologyType == MEDIUM_TOPOLOGY)
				return ccmSelectNodeServerPairList;
			else
				return ccmSelectNodeServerPairListLarge;
		case RTE:
			if (topologyType == MEDIUM_TOPOLOGY)
				return rteSelectNodeServerPairList;
			else
				return rteSelectNodeServerPairListLarge;
		case COMMON:
			if (topologyType == MEDIUM_TOPOLOGY)
				return commonSelectNodeServerPairList;
			else
				return commonSelectNodeServerPairListLarge;
		case WIDGET_CONTAINER:
			if (topologyType == MEDIUM_TOPOLOGY)
				return widgetContainerSelectNodeServerPairList;
			else
				return widgetContainerSelectNodeServerPairListLarge;
		case PUSH_NOTIFICATION:
			if (topologyType == MEDIUM_TOPOLOGY)
				return pushNotificationSelectNodeServerPairList;
			else
				return pushNotificationSelectNodeServerPairListLarge;
		case IC360:
			if (topologyType == MEDIUM_TOPOLOGY)
				return ic360SelectNodeServerPairList;
			else
				return ic360SelectNodeServerPairListLarge;
		//case QUICK_RESULTS:
		//	if (topologyType == MEDIUM_TOPOLOGY)
		//		return quickResultsSelectNodeServerPairList;
		//	else
		//		return quickResultsSelectNodeServerPairListLarge;
			// NEWS
		default:
			if (topologyType == MEDIUM_TOPOLOGY)
				return newsSelectNodeServerPairList;
			else
				return newsSelectNodeServerPairListLarge;

		}
	}

	private String getDefaultClusterName(int feature, int topologyType) {
		if (topologyType == MEDIUM_TOPOLOGY) {
			switch (feature) {
			case ACTIVITIES:
				return "AppsCluster";
			case BLOGS:
				return "AppsCluster";
			case COMMUNITIES:
				return "InfraCluster";
			case DOGEAR:
				return "AppsCluster";
			case HOMEPAGE:
				return "UtilCluster";
			case PROFILES:
				return "InfraCluster";
			case FILES:
				return "AppsCluster";
			case FORUM:
				return "AppsCluster";
			case WIKIS:
				return "AppsCluster";
			case MOBILE:
				return "AppsCluster";
			case MODERATION:
				return "UtilCluster";
			case SEARCH:
				return "InfraCluster";
			case METRICS:
				return "AppsCluster";
			case CCM:
				return "CCMCluster";
			case RTE:
				return "UtilCluster";
			case COMMON:
				return "InfraCluster";
			case WIDGET_CONTAINER:
				return "InfraCluster";
			case PUSH_NOTIFICATION:
				return "PushCluster";
			case IC360:
				return "IC360Cluster";
			//case QUICK_RESULTS:
			//	return "Cluster2";
				// NEWS
			default:
				return "InfraCluster";
			}
		} else {
			switch (feature) {
			case ACTIVITIES:
				return "ActivitiesCluster";
			case BLOGS:
				return "BlogsCluster";
			case COMMUNITIES:
				return "CommunitiesCluster";
			case DOGEAR:
				return "DogearCluster";
			case HOMEPAGE:
				return "HomepageCluster";
			case PROFILES:
				return "ProfilesCluster";
			case FILES:
				return "FilesCluster";
			case FORUM:
				return "ForumCluster";
			case WIKIS:
				return "WikisCluster";
			case MOBILE:
				return "MobileCluster";
			case MODERATION:
				return "ModerationCluster";
			case SEARCH:
				return "SearchCluster";
			case METRICS:
				return "MetricsCluster";
			case CCM:
				return "CCMCluster";
			case RTE:
				return "RTECluster";
			case COMMON:
				return "CommonCluster";
			case WIDGET_CONTAINER:
				return "WidgetContainerCluster";
			case PUSH_NOTIFICATION:
				return "PushNotificationCluster";
			case IC360:
				return "IC360Cluster";
			//case QUICK_RESULTS:
			//	return "QuickResultsCluster";
				// NEWS
			default:
				return "NewsCluster";
			}
		}
	}

	public String getFeaturesMessageText(String featureName) {
		return this.getFeaturesMessageText(getFeaturesID(featureName));
	}

	private String getTopologyTypeName(int type) {
		switch (type) {
		case SMALL_TOPOLOGY:
			return "small";
		case MEDIUM_TOPOLOGY:
			return "medium";
		default:
			return "large";
		}
	}

	private TopologyTreeViewer getTopologyTreeViewer(int topologyType) {
		if (topologyType == MEDIUM_TOPOLOGY)
			return tpTreeMedium;
		else
			return tpTreeLarge;
	}
}

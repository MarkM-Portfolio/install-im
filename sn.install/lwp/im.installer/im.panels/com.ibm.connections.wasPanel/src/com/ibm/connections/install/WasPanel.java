/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
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
import com.ibm.cic.common.core.api.utils.EncryptionUtils;

/**
 * For more information, refer to: 
 *  http://capilanobuild.swg.usma.ibm.com:9999/help/topic/com.ibm.cic.dev.doc/html/extendingIM/main.html
 *  http://capilanobuild.swg.usma.ibm.com:9999/help/topic/com.ibm.cic.agent.ui.doc.isv/reference/api/agentui/com/ibm/cic/agent/ui/extensions/BaseWizardPanel.html
 *  http://capilanobuild.swg.usma.ibm.com:9999/help/topic/com.ibm.cic.agent.ui.doc.isv/reference/api/agentui/com/ibm/cic/agent/ui/extensions/CustomPanel.html
 *  
 */
public class WasPanel extends BaseConfigCustomPanel {
    
    String className = WasPanel.class.getName();

	// private static final Logger log =
	// Logger.getLogger(com.ibm.connections.install.WasPanel.class);
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private static final String SSC_ENABLED = "user.SSCEnabled";
	private static final String INSTALL_TYPE = "user.installtype";
	private static final String INSTALL_PATH = "user.installlocation";
	private static final String WAS_INSTALL_PATH = "user.was.install.location";
	private static final String TEMP_PATH = "user.nonWin.temp";

	// defaults for UI
	private static final String DEFAULT_WAS_ADMIN_USERID = "wasadmin1";
	private static final String OfferingJob = "user.job";

	private Composite parent = null;
	private String AppProfilename = "";
	private String DMgrProfilename = "";
	private String SNAppProfilename = "";
	private String AppServername = "";
	private String OfferingFoldername = "";
	private String WasDMPort = "";
	private String WasDMSoapPort = "8879";
	// private String nonWinTemp="";

	private static final String DM_HOST = "user.dmhost";
	// private static final String DM_ADMIN_NAME = "user.dmid";
	private Text dmuserid = null; // text field for dm user id
	// private static final String DM_PD = "user.dmpassword";
	private Text dmpassword = null; // text field for dm pwd
	private String dmport = null;

	private static final String HOST_NAME = "user.hostname";
	// private static final String CELL_NAME = "user.cellname";
	// private static final String NODE_NAME = "user.nodename";

	private IProfile profile = null; // profile to save data in
	private Label pwd2_label = null, cell_label2 = null;
	private Label dm_label = null;
	private Composite container = null;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Section applicationServerSection;

	private Button wasDetectButton;
	// private Text wasLocationText = null; // text field for path
	private Text host = null; // text field for path

	private Text wasLocationList = null;
	private Combo dmList = null;
	private Label noticeLabel = null;

	private boolean isDetectWas = true;
	boolean isFixpackInstall;
	boolean isFixpackUninstall;
	boolean isModifyInstall;
	boolean isUninstall;
	private boolean isRecordingMode = false;

	private static final String LC_SCRIPT_Name = "LCInstallScript";
	private static final String MISSING_SCRIPT = "Script_Missing";
	
	private Button validateDectectButton = null;

    
    /**
     * Default constructor
     */
    public WasPanel() {
        super(Messages.WasPanelName); //NON-NLS-1
        super.setDescription(Messages.WAS_INFO_MSG1);
    }

	public void createControl(Composite parent) {
		this.isFixpackInstall = isUpdate();
		this.isFixpackUninstall = isRollback();
		this.isModifyInstall = isModify();
		this.isUninstall = isUninstall();
		log.info("In...waspanel..createControl");
		log.info("isFixpackInstall = " + isFixpackInstall);
		log.info("isFixpackUninstall = " + isFixpackUninstall);
		log.info("isModifyInstall = " + isModifyInstall);
		log.info("isUninstall = " + isUninstall);
		try {
			log.info("*****************************");
			IAgent agent = (IAgent) getInitializationData().getAdapter(
					IAgent.class);
			isRecordingMode = agent.isSkipInstall();

			this.parent = parent;
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
			applicationServerSection = toolkit.createSection(form.getBody(),
					Section.TITLE_BAR);

			// applicationServerSection.setSize(2000, 10);
			applicationServerSection.setText(Messages.WAS_SELECTION);

			final Composite applicationServerSelectContainer = toolkit
					.createComposite(applicationServerSection);
			GridLayout applicationServerSelectLayout = new GridLayout();
			applicationServerSelectLayout.numColumns = 2;
			applicationServerSelectContainer
					.setLayout(applicationServerSelectLayout);
			GridData gd = new GridData();
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			applicationServerSelectContainer.setLayoutData(gd);

			GridData inputgridData = new GridData(GridData.BEGINNING);
			inputgridData.horizontalSpan = 2;
			inputgridData.widthHint = 248;
			GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);
			inputgridDataForLabel.horizontalSpan = 2;
			inputgridDataForLabel.widthHint = 430;

			if (isRecordingMode) {
				final Button b2 = new Button(applicationServerSelectContainer,
						SWT.PUSH);
				GridData gridDataButtonSize = new GridData();
				gridDataButtonSize.widthHint = 200;
				gridDataButtonSize.heightHint = 30;
				b2.setLayoutData(gridDataButtonSize);
				b2.setText(Messages.INPUT_WAS_INFO);
				/*
				 * if (isModifyInstall || isFixpackInstall || isUninstall ||
				 * isFixpackUninstall) b2.setEnabled(false);
				 */
				b2.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
					}

					public void widgetSelected(SelectionEvent event) {
						showInputDialog();
						verifyComplete();
					}
				});
			}
			Label waslocationLabel = new Label(
					applicationServerSelectContainer, SWT.NONE);
			waslocationLabel.setText(Messages.WAS_LOCATION_BROWSER_MSG);
			waslocationLabel.setLayoutData(inputgridDataForLabel);
			this.wasLocationList = new Text(applicationServerSelectContainer,
					SWT.BORDER | SWT.SINGLE);
			if (isModifyInstall || isFixpackInstall || isUninstall
					|| isFixpackUninstall) {
				this.wasLocationList.setEnabled(false);
				profile = getProfile();
				if (profile != null)
					this.wasLocationList.setText(profile
							.getUserData("user.was.install.location"));
			}

			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = 248;
			gd.horizontalSpan = 1;
			gd.verticalSpan = 1;
			wasLocationList.setEditable(false);
			this.wasLocationList.setLayoutData(gd);

			gd = new GridData(GridData.BEGINNING);
			gd.horizontalSpan = 1;

			wasDetectButton = new Button(applicationServerSelectContainer,
					SWT.NONE);
			if (isModifyInstall || isFixpackInstall || isUninstall
					|| isFixpackUninstall) {
				this.wasDetectButton.setEnabled(false);
			}
			wasDetectButton.setLayoutData(gd);
			wasDetectButton.setText(Messages.DB_BTN_Browse);
			if (isRecordingMode)
				wasDetectButton.setEnabled(false);

			wasDetectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// wasLocationList.removeAll();
					// dmList.removeAll();

					DirectoryDialog dialog = new DirectoryDialog(
							applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();

					if (dir != null) {
						// check the was version and profiles
						wasLocationList.setText(dir);
						dmList.removeAll();
						try {
							// check was version
							String wasVersionPath = getAndCopyLCscriptPath("wasVersion.txt");
							log.info("Was version Path : " + wasVersionPath);
							if (wasVersionPath.equals(MISSING_SCRIPT)) {
								setErrorMessage(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
								setPageComplete(false);
								nextEnabled = false;
								return;
							}
							if (!DetectiveProfileAction.isWasLocValid(dir)) {
								setErrorMessage(Messages.DETECT_WAS_VERSION_ERROR);
								setPageComplete(false);
								nextEnabled = false;
								return;
							}
							boolean isValidWasVersion = DetectiveProfileAction
									.isWasVersionValid(dir, wasVersionPath);
							log.info("is was version valid : "
									+ isValidWasVersion);

							if (!isValidWasVersion) {
								setErrorMessage(Messages.WAS_VERSION_NOT_SUPPORT);
								validateDectectButton.setEnabled(false);
								setPageComplete(false);
								nextEnabled = false;
								compareVersion(dir, wasVersionPath);
								return;
							}
							validateDectectButton.setEnabled(true);
							// get dm profiles
							ArrayList profilesList = DetectiveProfileAction
									.getDMProfileNew(dir);
							log.info("profile list : " + profilesList);
							if (profilesList != null) {
								dmList.removeAll();
								for (int i = 0; i < profilesList.size(); i++) {
									String dmProfile = (String) profilesList
											.get(i);
									dmList.add(dmProfile);
								}
								if (profilesList.size() > 0) {
									dmList.select(0);
									updatePort();
									if (verifyPortComplete()) {
										setErrorMessage(null);
									}
								}

							}
							isDetectWas = true;
							showErrorMsg();
						} catch (Exception e1) {
							log.error(e1);
							e1.printStackTrace();
							setErrorMessage(Messages.DETECT_WAS_VERSION_ERROR);
							nextEnabled = false;
							return;
						}
					}
				}
			});

			Label dmSelectLabel = new Label(applicationServerSelectContainer,
					SWT.NONE);
			dmSelectLabel.setText(Messages.WAS_DEPLOY_MANAGER);
			dmSelectLabel.setLayoutData(inputgridDataForLabel);
			this.dmList = new Combo(applicationServerSelectContainer, SWT.LEFT
					| SWT.READ_ONLY);
			if (isModifyInstall || isFixpackInstall || isUninstall
					|| isFixpackUninstall) {
				this.dmList.setEnabled(false);
				profile = getProfile();
				if (profile != null) {
					dmList.removeAll();
					dmList.add(profile.getUserData("user.was.profileName"));
					dmList.select(0);
					updatePort();
				}
			}
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			gd.horizontalSpan = 2;
			gd.verticalSpan = 1;
			this.dmList.setLayoutData(gd);

			this.dmList.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					updatePort();
				}
			});

			this.wasLocationList.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String wasLoc = wasLocationList.getText();
					if (wasLoc == null || wasLoc.trim().equals("")) {
						return;
					}
					dmList.removeAll();
					log.info("Select was location : " + wasLoc);
					if (wasList != null && wasList.size() > 0) {
						Iterator it = wasList.iterator();
						while (it.hasNext()) {
							WASInfo wasInfo = (WASInfo) it.next();

							if (wasInfo.wasLoc.equals(wasLoc)) {
								java.util.List profilesList = wasInfo.profilenames;
								for (Iterator iterator = profilesList
										.iterator(); iterator.hasNext();) {
									String name = (String) iterator.next();
									if (name != null && !name.trim().equals("")) {
										dmList.add(name);
									}

								}
							}

						}
					}
				}
			});

			this.wasLocationList.addFocusListener(new FocusAdapter() {

				public void focusGained(FocusEvent arg0) {
					// showErrorMsg();
				}
			});

			Label hostNameLabel = new Label(applicationServerSelectContainer,
					SWT.NONE);
			hostNameLabel.setText(Messages.WAS_HOST);
			hostNameLabel.setLayoutData(inputgridData);
			this.host = new Text(applicationServerSelectContainer, SWT.BORDER
					| SWT.SINGLE);
			this.host.setLayoutData(inputgridData);
			if (isModifyInstall || isFixpackInstall || isUninstall
					|| isFixpackUninstall) {
				this.host.setEnabled(false);
				profile = getProfile();
				if (profile != null) {
					host.setText(profile.getUserData("user.was.dmHostname"));
				}
			}
			this.host.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete();
				}
			});

			this.host.addFocusListener(new FocusAdapter() {

				public void focusGained(FocusEvent arg0) {
					// showErrorMsg();
				}
			});

			applicationServerSection
					.setClient(applicationServerSelectContainer);

			final Composite applicationServerSelectStackContainer = new Composite(
					form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			applicationServerSelectStackContainer.setLayout(stackLayout);

			final Composite compositeSN = new Composite(
					applicationServerSelectStackContainer, SWT.NONE);

			GridData gridData = new GridData(GridData.BEGINNING);
			gridData.horizontalSpan = 2;

			// Component for SN
			compositeSN.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			compositeSN.setLayoutData(gd);

			Section SNSection = toolkit.createSection(compositeSN,
					Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			SNSection.setLayoutData(gridData);
			SNSection.setText(Messages.WAS_DEPLOY_MANAGER_INFO);

			Composite SNContainer = toolkit.createComposite(SNSection);
			GridLayout SNLayout = new GridLayout();
			SNContainer.setLayout(SNLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			SNContainer.setLayoutData(gd);

			new Label(SNContainer, SWT.NONE).setText(Messages.dmSecurity_info);

			new Label(SNContainer, SWT.NONE)
					.setText(Messages.dmSecurity_username);
			this.dmuserid = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
			this.dmuserid.setLayoutData(inputgridData);
			this.dmuserid.setText("wasadmin");

			if (isModifyInstall || isFixpackInstall || isUninstall
					|| isFixpackUninstall) {
				profile = getProfile();
				if (profile != null) {
					String id = profile.getUserData("user.was.adminuser.id");
					if (id == null)
						id = profile.getUserData("user.news.adminuser.id");
					this.dmuserid.setText(id);
				}
			}

			this.dmuserid.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyUserNameComplete();
				}
			});

			this.dmuserid.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent arg0) {
					// showErrorMsg();
				}
			});

			new Label(SNContainer, SWT.NONE)
					.setText(Messages.dmSecuirty_passwd);
			this.dmpassword = new Text(SNContainer, SWT.BORDER | SWT.SINGLE
					| SWT.PASSWORD);
			this.dmpassword.setLayoutData(inputgridData);
			this.dmpassword.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPasswordComplete();
				}
			});

			this.dmpassword.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent arg0) {
					// showErrorMsg();
				}
			});
			/* hide showing soap port
			new Label(SNContainer, SWT.NONE).setText(Messages.dmSecuirty_port);
			this.dmport = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
			this.dmport.setLayoutData(inputgridData);
			this.dmport.setEnabled(false);
			this.dmport.setText("8879");
			this.dmport.setVisible(false);
			*/
			if (isModifyInstall || isFixpackInstall || isUninstall
					|| isFixpackUninstall) {
				profile = getProfile();
				if (profile != null) {
					String exist_dmport = profile.getUserData("user.was.wasSoapPort");
					log.info("existing dmport: " + exist_dmport);
					if (exist_dmport == null || exist_dmport.length() == 0)
						this.dmport = "";
					else
						this.dmport = exist_dmport;
				}
			}

			validateDectectButton = new Button(SNContainer, SWT.PUSH);
			GridData gridDataButtonSize = new GridData();
			gridDataButtonSize.widthHint = 200;
			gridDataButtonSize.heightHint = 30;
			validateDectectButton.setLayoutData(gridDataButtonSize);
			if (isRecordingMode)
				validateDectectButton.setText(Messages.SKIP_VALIDATION);
			else
				validateDectectButton.setText(Messages.VALIDATE);
			validateDectectButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
				}

				public void widgetSelected(SelectionEvent event) {
					verifyComplete();
				}
			});

			noticeLabel = new Label(SNContainer, SWT.NONE);
			noticeLabel.setText(Messages.WAS_VALIDATION_NOTICE_INFO);

			SNSection.setClient(SNContainer);

			stackLayout.topControl = compositeSN;
			applicationServerSelectStackContainer.layout();
			
			form.pack();
			setControl(container);
			
			setErrorMessage(null);
			nextEnabled = false;
			setPageComplete(false);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void showInputDialog() {
		if (profile == null)
			profile = getProfile();

		InputWASInfoDialog dialog = new InputWASInfoDialog(parent.getShell());
		dialog.setWasLoc(profile.getUserData("user.was.install.location"));
		dialog.setDmProfileLoc(profile.getUserData("user.was.userhome"));
		dialog.setDmProfileName(profile.getUserData("user.was.profileName"));
		dialog.setCellName(profile.getUserData("user.was.dmCellName"));
		dialog.setNodeNames(profile.getUserData("user.nodeslist"));
		dialog.setHostNames(profile.getUserData("user.nodesHostnamelist"));

		dialog.open();
		// TODO check
		profile.setUserData("user.was.install.location", dialog.getWasLoc());
		profile.setUserData("user.was.userhome", dialog.getDmProfileLoc());
		profile.setUserData("user.was.profileName", dialog.getDmProfileName());
		profile.setUserData("user.was.dmCellName", dialog.getCellName());
		profile.setUserData("user.clusterlist", "");
		profile.setUserData("user.nodeslist", dialog.getNodeNames());
		profile.setUserData("user.clusterfullinfo", "");
		profile.setUserData("user.nodesHostnamelist", dialog.getHostNames());

		wasLocationList
				.setText(profile.getUserData("user.was.install.location"));
		dmList.removeAll();
		dmList.add(profile.getUserData("user.was.profileName"));
		dmList.select(0);
		updatePort();
		verifyComplete();
	}

	private void updatePort() {
		int index = dmList.getSelectionIndex();
		String profileName = dmList.getItem(index);
		String profilePath = DetectiveProfileAction.getProfilePath(profileName,
				wasLocationList.getText());
		String tmpPort = DetectiveProfileAction.getProfilePort(profilePath);
		if (!isModifyInstall && !isFixpackInstall && !isUninstall
				&& !isFixpackUninstall)
			this.dmport = tmpPort == null ? "" : tmpPort;
		if (verifyPortComplete())
			setErrorMessage(null);
	}

	// Check the validity of the user input.
	// Save in profile if it's good.

	private void verifyComplete() {

		nextEnabled = false;
		setPageComplete(false);
		profile = getProfile();

		String dmpwd = this.dmpassword == null ? "" : this.dmpassword.getText()
				.trim();
		String dmuserId = this.dmuserid == null ? "" : this.dmuserid.getText()
				.trim();
		String hn = this.host == null ? "" : this.host.getText().trim();
		
		if (!isFixpackInstall && !isModifyInstall && !isUninstall
				&& !isFixpackUninstall) {
			boolean isHostNameValid = this.verifyHostNameComplete();
			if (!isHostNameValid) {
				return;
			}
		}

		boolean isNameValid = this.verifyUserNameComplete();
		if (!isNameValid) {
			return;
		}

		boolean isPawValid = this.verifyPasswordComplete();
		if (!isPawValid) {
			return;
		}

//		if (!isFixpackInstall && !isModifyInstall && !isUninstall
//				&& !isFixpackUninstall) {
//			boolean isPostValid = this.verifyPortComplete();
//			if (!isPostValid) {
//				return;
//			}
//		}

		String wasLoc = null, profilePath = null, wasProfileName = null;
		if (!isFixpackInstall && !isModifyInstall && !isUninstall
				&& !isFixpackUninstall) {
			wasLoc = wasLocationList.getText();
			if (wasLoc == null || wasLoc.trim().equals("")) {
				setErrorMessage(Messages.WAS_SELECTION_WARNING);
				nextEnabled = false;
				this.setPageComplete(false);
				return;
			}

			int wasProLocIndex = dmList.getSelectionIndex();
			if (wasProLocIndex < 0) {
				setErrorMessage(Messages.WAS_PROFILE_SELECTION_WARNING);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}

			wasProfileName = dmList.getItem(wasProLocIndex);

			profilePath = "C:\\PROGRA~1\\IBM\\WebSphere\\AppServer1\\profiles\\Dmgr01";
			try {
				if (isRecordingMode)
					profilePath = profile.getUserData("user.was.userhome");
				else
					profilePath = DetectiveProfileAction.getProfilePath(
							wasProfileName, wasLoc);

				if (profilePath == null) {
					setErrorMessage(Messages.WAS_GET_PROFILE_PATH_WARNING);
					nextEnabled = false;
					setPageComplete(false);
					return;
				}

			} catch (Exception e) {
				setErrorMessage(Messages.WAS_GET_PROFILE_PATH_WARNING);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (dmport == null || dmport.length() == 0) {
				setErrorMessage(Messages.WAS_DETECT_PORT_ERROR);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		} else {
			wasLoc = wasLocationList.getText();
			wasProfileName = dmList.getItem(0);
			profilePath = profile.getUserData("user.was.userhome");
		}
		java.util.List<String> selectFeatures = this.getOffering();

		StringBuffer features = new StringBuffer();
		if (selectFeatures != null) {
			for (int i = 0; i < selectFeatures.size(); i++) {
				features.append(selectFeatures.get(i));
				features.append(",");

			}
		}

		if (isRecordingMode) {
			clusterInfoFull = "";
			dmCellName = profile.getUserData("user.was.dmCellName");
			clustersStr = "";
			nodesStr = profile.getUserData("user.nodeslist");
			hostnameStr = profile.getUserData("user.nodesHostnamelist");
			servernameStr = profile.getUserData("user.nodesServerlist");
			nodeAgentsStr = profile.getUserData("user.nodeAgentList");
			

			nextEnabled = true;
			setPageComplete(true);
			setErrorMessage(null);

		} else {
			WASProgressMonitor dialog = new WASProgressMonitor(hn, dmport,
					dmuserId, dmpwd, wasLoc, profilePath, wasProfileName,
					features.toString());
			
			StringBuffer params = new StringBuffer();
			params.append(hn).append("|")
				.append(dmport).append("|")
				.append(dmuserId).append("|")
				.append(dmpwd).append("|")
				.append(wasLoc).append("|")
				.append(profilePath).append("|")
				.append(wasProfileName).append("|")
				.append(features);
			
			profile.setData("ccmParams", params.toString());
			
			try {
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(parent.getShell());
				pmd.open();
				Shell shell = pmd.getShell();
				shell.setText(Messages.PROGRESS_INFORMATION);
				shell.update();
				pmd.run(true, true, dialog);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String msg = dialog.getErrorMsg();
			log.info("error msg : " + msg);

			if (msg.length() > 0
					&& (msg.equalsIgnoreCase(Messages.WAS_OPEN_FILE_WARNING1) || msg
							.equalsIgnoreCase(Messages.WAS_OPEN_FILE_WARNING2))) {
				nextEnabled = true;
				setPageComplete(true);
				setErrorMessage(null); // in case there had been one;
				setMessage(msg, IMessageProvider.WARNING);
			} else if (msg.length() > 0) {
				setErrorMessage(msg);
				nextEnabled = false;
				setPageComplete(false);
				return;
			} else {
				nextEnabled = true;
				setPageComplete(true);
				setErrorMessage(null); // in case there had been one;
			}

			showValidationSuccessMessageDialog(Messages.VALIDATION_SUCCESSFUL);
			
			validateDectectButton.setText(Messages.VALIDATED);
			
			profile.setUserData("user.clusterlist", clustersStr);
			profile.setUserData("user.nodeslist", nodesStr);
			profile.setUserData("user.clusterfullinfo", clusterInfoFull);
			profile.setUserData("user.nodesHostnamelist", hostnameStr);
			profile.setUserData("user.nodesServerlist", servernameStr);
			profile.setUserData("user.nodeAgentList", nodeAgentsStr);
		}

		this.setDMUserData(wasLoc, profilePath, dmport, wasProfileName,
				dmCellName, hn, dmuserId, dmpwd);

	}

	private void showErrorMsg() {

		if (!isDetectWas) {
			setErrorMessage(Messages.WAS_BTN_DETECT_CONTINUE_WARNING);
			nextEnabled = false;
			setPageComplete(false);
			return;
		} else {
			setErrorMessage(Messages.WAS_BTN_VALIDATE_CONTINUE_WARNING);
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
	}

	private String parseNodeHostNameMapToString(ArrayList target) {
		if (target != null) {
			Iterator it = target.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String content = (String) it.next();
				try {
					result.append(DMValidator.detectNodeHostName(content) + ",");
				} catch (Exception e) {
					log.error(e);
				}
			}
			return result.toString();
		}
		return null;
	}

	private String parseNodeServerMapToString(Map target) {
		if (target != null) {
			Set nodeSet = target.keySet();
			Iterator it = nodeSet.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String node = (String) it.next();
				String serverStr = (String) target.get(node);
				result.append(node + ":" + serverStr + ";");
			}
			return result.toString();
		}
		return null;
	}

	private String parseToString(ArrayList target) {

		if (target != null) {
			Iterator it = target.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String content = (String) it.next();
				result.append(content + ",");
			}
			return result.toString();
		}
		return null;
	}

	private boolean verifyHostNameComplete() {

		nextEnabled = false;
		setPageComplete(false);
		InstallValidator installvalidator = new InstallValidator();

		String host = this.host == null ? "" : this.host.getText().trim();

		try {
			if (!installvalidator.hostNameValidateForWasPanel(host)) {
				setErrorMessage(installvalidator.getMessage());
				nextEnabled = false;
				setPageComplete(false);
				return false;
			}
		} catch (Exception e) {
			log.error(e);
			nextEnabled = false;
			setPageComplete(false);
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	private boolean verifyPortComplete() {

		nextEnabled = false;
		setPageComplete(false);

		// InstallValidator installvalidator = new InstallValidator();

		dmport = this.dmport == null ? "" : this.dmport.trim();

		try {
			if (dmport.length() == 0) {
				setErrorMessage(Messages.WAS_DETECT_PORT_ERROR);
				nextEnabled = false;
				setPageComplete(false);
				return false;
			}
		} catch (Exception e) {
			log.error(e);
			nextEnabled = false;
			setPageComplete(false);
			return false;
		}

		return true;

	}

	private boolean verifyUserNameComplete() {
		String dmuserId = this.dmuserid == null ? "" : this.dmuserid.getText()
				.trim();
		if (dmuserId == null || dmuserId.length() == 0) {
			setErrorMessage(Messages.warning_user_empty);
			setPageComplete(false);
			return false;
		} else {
			setErrorMessage(null);
		}
		return true;
	}

	private boolean verifyPasswordComplete() {
		InstallValidator validator = new InstallValidator();
		String dmuserpw = this.dmpassword == null ? "" : this.dmpassword
				.getText().trim();
		if (dmuserpw == null || dmuserpw.length() == 0) {
			setErrorMessage(Messages.warning_password_empty);
			setPageComplete(false);
			return false;
		} else if (validator.containsSpace(dmuserpw)) {
			setErrorMessage(Messages.warning_password_invalid_chars);
			setPageComplete(false);
			return false;
		} else {
			setErrorMessage(null);
		}
		return true;

	}

	private void setDMUserData(String wasLocation, String wasProfilePath,
			String port, String dmProfileName, String dmCellName,
			String hostname, String adminUser, String password) {

		log.info("Set user data");
		log.info("was location : " + wasLocation);
		log.info("was profile bin path : " + wasProfilePath);
		log.info("DM host : " + hostname);
		log.info("DM soap port : " + port);
		log.info("DM profile name : " + dmProfileName);
		log.info("DM Cell name : " + dmCellName);
		log.info("Admin user name : " + adminUser);
		log.info("Admin password : PASSWORD_REMOVED");

		if (!isModifyInstall && !isFixpackInstall && !isUninstall
				&& !isFixpackUninstall) {
			
			String transferWASPath = transferPath(wasLocation);
			String wasHomeLocation = null;
			if (transferWASPath.contains(":")){
	    		int index = transferWASPath.indexOf(":");
	    		wasHomeLocation = transferWASPath.substring(0,index) + "\\" + transferWASPath.substring(index);
	    		log.info("was location: " + wasHomeLocation);
	    		//showValidationSuccessMessageDialog(wasHomeLocation);
	    	}
			else{
				wasHomeLocation = transferPath(wasLocation);
			}
			
			profile.setUserData("user.was.install.location", transferPath(wasLocation));
			profile.setUserData("user.was.install.location.configproperties", wasHomeLocation);
			profile.setUserData("user.was.install.location.win32format", wasLocation);

			String transferProfilePath = transferPath(wasProfilePath);
			String wasUserHome = null;
			if (transferProfilePath.contains(":")){
	    		int index = transferProfilePath.indexOf(":");
	    		wasUserHome = transferProfilePath.substring(0,index) + "\\" + transferProfilePath.substring(index);
	    		log.info("was profile home: " + wasUserHome);
	    		//showValidationSuccessMessageDialog(wasUserHome);
	    	}
			else{
				wasUserHome = transferPath(wasProfilePath);
			}
			
			profile.setUserData("user.was.userhome.original", wasProfilePath);
			profile.setUserData("user.was.userhome", transferPath(wasProfilePath));
			profile.setUserData("user.was.userhome.configproperties", wasUserHome);
			profile.setUserData("user.was.wasSoapPort", port);
			profile.setUserData("user.was.profileName", dmProfileName);
			profile.setUserData("user.was.dmCellName", dmCellName);
			profile.setUserData("user.was.dmHostname", hostname);
			String domainname = hostname.substring(hostname.indexOf("."));
			profile.setUserData("user.was.domainName", domainname);
		}
		profile.setUserData("user.was.adminuser.id", adminUser);
		profile.setUserData("user.was.adminuser.password",
				EncryptionUtils.encrypt(password));
		profile.setUserData("user.was.adminuser.xor.encrypted.password",
				Util.xor(password));
		
		log.info("user.was.adminuser.xor.encrypted.password : " + Util.xor(password));
		
		for (int featureID = ACTIVITIES; featureID <= NEWS; featureID++) {
			profile.setUserData("user." + getFeatureName(featureID)
					+ ".adminuser.id", adminUser);
			profile.setUserData("user." + getFeatureName(featureID)
					+ ".adminuser.password", EncryptionUtils.encrypt(password));
		}
	}

	@Override
	public void setVisible(boolean visible) {

	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_WAS_PANEL;
	}

	private void loadWas() throws Exception {

		wasList.clear();
		WASInfo wasInfo = null;
		File file = new File("wasLocation.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("--")) {
				String profileName = line.substring(2);
				wasInfo.profilenames.add(profileName);
			} else {
				if (wasInfo != null)
					wasList.add(wasInfo);
				wasInfo = new WASInfo();
				wasInfo.wasLoc = line.trim();
			}
			line = br.readLine();
		}
		if (wasInfo != null)
			wasList.add(wasInfo);
	}

	// private String getPyPath(String pyName) throws Exception{
	// File file = new File(pyName);
	// return file.getAbsolutePath();
	// }

	private void wasDetection(String propertiesFile) {
		String cmd[] = new String[5];
		Process process = null;
		try {
			File file = new File(propertiesFile);
			String propertiesFilePath = file.getAbsolutePath();

			// cmd[0] = getJREHome() + "/jre/bin/java";
			// cmd[0] = "jre_5.0.3.sr8a_20080811b/jre/bin/java";
			cmd[1] = "-cp";
			cmd[2] = "../../LotusConnections/plugins/WasConfigPanel_1.0.0.jar";
			cmd[3] = "com.ibm.connections.install.WasDetectAction";
			cmd[4] = propertiesFilePath;

			process = Runtime.getRuntime().exec(cmd, null, new File("."));
			process.waitFor();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private java.util.List<WASInfo> wasList = new ArrayList<WASInfo>();

	class WASInfo {
		public String wasLoc;
		public java.util.List<String> profilenames = new ArrayList<String>();
	}

	ArrayList nodeList = new ArrayList();
	Map nodeAgentList = new HashMap();
	ArrayList clusterList = new ArrayList();
	Map serverList = new HashMap();
	String clusterInfoFull = "";
	String dmCellName = "";
	String clustersStr = "";
	String nodesStr = "";
	String nodeAgentsStr = "";
	String hostnameStr = "";
	String servernameStr = "";

	class WASProgressMonitor implements IRunnableWithProgress {
		private String hostname;
		private String port;
		private String userid;
		private String password;
		private String profilePath;
		private String wasLoc;
		private String wasProfileName;
		private String features;
		private String checkAppFile = "wkplc_CheckAppSecurity.py";
		private String installFeFile = "lc_GetInstalledFeatures.py";

		StringBuffer sb = new StringBuffer();

		public WASProgressMonitor(String hostname, String port, String userid,
				String password, String wasLoc, String profilePath,
				String wasProfileName, String features) {
			this.hostname = hostname;
			this.port = port;
			this.userid = userid;
			this.password = password;
			this.profilePath = profilePath;
			this.wasLoc = wasLoc;
			this.wasProfileName = wasProfileName;
			this.features = features;
		}

		public String getErrorMsg() {
			return sb.toString();
		}

		public void run(IProgressMonitor monitor) {

			int result = -1;
			monitor.beginTask(Messages.WAS_DETECT_START, 10);

			monitor.setTaskName(Messages.WAS_DETECT_SYSTEM_APP);
			try {
				boolean hasSystemApps = DetectiveProfileAction
						.getSystemAppsCheckResult(wasLoc);
				log.info("hasSystemApps = " + hasSystemApps);
				if (!hasSystemApps) {
					setErrorMessage(Messages.WAS_NO_SYSTEM_APP);
					setPageComplete(false);
					nextEnabled = false;
					return;
				}
			} catch (Exception e) {
				log.error(e);
				sb.append(Messages.WAS_NO_SYSTEM_APP);
				monitor.worked(10);
				monitor.done();
				return;
			}

			monitor.worked(1);

			// Check OAuth provider app availability
			monitor.setTaskName(Messages.WAS_DETECT_OAUTH_PROVIDER_EAR);
			try {
				boolean hasOauthProviderEar = DetectiveProfileAction.getOauthProviderEarCheckResult(wasLoc);
				log.info("hasOauthProviderEar = " + hasOauthProviderEar);
				if (!hasOauthProviderEar) {
					setErrorMessage(Messages.WAS_NO_OAUTH_PROVIDER_EAR);
					setPageComplete(false);
					nextEnabled = false;
					return;
				}
			} catch (Exception e) {
				log.error(e);
				sb.append(Messages.WAS_NO_OAUTH_PROVIDER_EAR);
				monitor.worked(10);
				monitor.done();
				return;
			}

			monitor.worked(1);

			// get ssl certificate
			monitor.setTaskName(Messages.WAS_GET_CERT);

			String sslReturnCode = SSLCertificateGetter.getSSLCertificate(
					profilePath, userid, password);
			log.info("Get ssl certificate return code : " + sslReturnCode);
			if (!sslReturnCode.equals("0")) {
				sb.append(Messages.WAS_GET_SSL_CERTIFICATE_ERROR);
				monitor.worked(9);
				monitor.done();
				return;
			}

			monitor.worked(1);

			// check app security
			if (!isFixpackInstall && !isModifyInstall && !isUninstall
					&& !isFixpackUninstall) {
				String checkApppyPath = "";
				try {
					checkApppyPath = getAndCopyLCscriptPath(checkAppFile);

					if (checkApppyPath.equals(MISSING_SCRIPT)) {
						setErrorMessage(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
						setPageComplete(false);
						nextEnabled = false;
						return;
					}

				} catch (Exception e1) {
					log.error(e1);
					sb.append(Messages.WAS_GET_PY_PATH_ERROR);
					monitor.worked(8);
					monitor.done();
					return;
				}
				monitor.setTaskName(Messages.WAS_CHECK_APP_SECURITY);
				boolean isAppSecurity = WasSecurityValidator
						.validateAppSecurity(profilePath, checkApppyPath);
				log.info("is application security enabled : " + isAppSecurity);
				if (!isAppSecurity) {
					sb.append(Messages.WAS_NO_APP_SECURITY_ERROR);
					monitor.worked(8);
					monitor.done();
					return;
				}
				// Java2 Security check
				monitor.setTaskName(Messages.WAS_CHECK_JAVA2_SECURITY);
				boolean isJava2Sec = WasSecurityValidator.getJava2Security();
				log.info("is java2 security enabled : " + isJava2Sec);
				if (isJava2Sec) {
					sb.append(Messages.WAS_JAVA2_SECURITY_ERROR);
					monitor.worked(8);
					monitor.done();
					return;
				}
				monitor.worked(1);

				// Admin Security check
				monitor.setTaskName(Messages.WAS_CHECK_ADMIN_SECURITY);
				boolean isAdminSec = WasSecurityValidator.getAdminSecurity();
				log.info("is administator security enabled : " + isAdminSec);
				if (!isAdminSec) {
					sb.append(Messages.WAS_NO_ADMIN_SECURITY_ERROR);
					monitor.worked(7);
					monitor.done();
					return;
				}
			}
			monitor.worked(1);

			// check DM info for heap size
			String pyPath = "";
			try {
				pyPath = getAndCopyLCscriptPath("wkplc_GetDMInfo.py");

				if (pyPath.equals(MISSING_SCRIPT)) {
					setErrorMessage(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
					setPageComplete(false);
					nextEnabled = false;
					return;
				}

			} catch (Exception e1) {
				log.error(e1);
				sb.append(Messages.WAS_GET_PY_PATH_ERROR);
				monitor.worked(6);
				monitor.done();
				return;
			}

			monitor.setTaskName(Messages.WAS_GET_DM_INFO);
			String returnCode = "1";
			try {
				returnCode = DMValidator.getDMInfo(profilePath, pyPath, userid,
						password, hostname, port);
				log.info("Get DM info return code : " + returnCode);
			} catch (Exception e) {
				log.error(e);
				returnCode = "1";
			}
			log.info("features : "+ features);
			if (!returnCode.equals("0")) {
				sb.append(Messages.WAS_CONNECT_DM_ERROR);
				monitor.worked(6);
				monitor.done();
				return;
			}
			monitor.worked(3);

			nodeList = (ArrayList) DMValidator.detectNodes();
			
			nodeAgentList = (Map) DMValidator.detectNodeAgents();

			clusterList = (ArrayList) DMValidator.detectClusters();

			try {
				serverList = (Map) DMValidator.detectServers();
			} catch (Exception e) {
				log.error(e);
			}

			try {
				//get web server info and put them into tmp dir
				new WebServerHelper().getWebServerInfo(profilePath, userid, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			clusterInfoFull = DMValidator.getClusterFullInfo();
			dmCellName = DMValidator.detectDMCellName();
			clustersStr = parseToString(clusterList);
			nodesStr = parseToString(nodeList);
			nodeAgentsStr = parseNodeServerMapToString(nodeAgentList);
			hostnameStr = parseNodeHostNameMapToString(nodeList);
			servernameStr = parseNodeServerMapToString(serverList);

			log.info("nodeAgentsStr : "+nodeAgentsStr);
			
			// check nodes num
			boolean valideNodesNumber = DMValidator.validteNodesNumber();

			if (!valideNodesNumber) {

				sb.append(Messages.WAS_NODE_NUM_ERROR);
				monitor.worked(3);
				monitor.done();
				return;
			}

			monitor.worked(1);

			if (!isFixpackInstall && !isModifyInstall && !isUninstall
					&& !isFixpackUninstall) {
				// check install feature
				String checkInstallFePath = "";
				try {
					checkInstallFePath = getAndCopyLCscriptPath(installFeFile);

					if (checkInstallFePath.equals(MISSING_SCRIPT)) {
						setErrorMessage(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
						setPageComplete(false);
						nextEnabled = false;
						return;
					}

				} catch (Exception e1) {
					log.error(e1);
					sb.append(Messages.WAS_GET_PY_PATH_ERROR);
					monitor.worked(2);
					monitor.done();
					return;
				}

				monitor.setTaskName(Messages.WAS_CHECK_INSTALL_FE);
				boolean isvalidFeature = WasInstalledFeaturesValidator
						.validate(profilePath, features.toString(),
								checkInstallFePath, userid, password);
				log.info("is valid feature : " + isvalidFeature);
				if (!isvalidFeature) {
					sb.append(Messages.Existing_Feature_Selected_Error);
					monitor.worked(2);
					monitor.done();
					return;
				}
			}

			// check open file number
			OpenFileNumberCheck openFileNumberCheck = new OpenFileNumberCheck();
			int code = openFileNumberCheck.getOpenFileNumberCheck();
			log.info("OpenFileNumberCheck return code = " + code);
			if (code == OpenFileNumberCheck.LESS_THAN_8192) {
				log.info("OpenFileNumberCheck return LESS_THAN_8192 ");
				sb.append(Messages.WAS_OPEN_FILE_WARNING1);
				monitor.worked(1);
				monitor.done();
				return;
			} else if (code == OpenFileNumberCheck.UNKNOWN_ERROR) {
				log.info("OpenFileNumberCheck return UNKNOWN_ERROR ");
				sb.append(Messages.WAS_OPEN_FILE_WARNING2);
				monitor.worked(1);
				monitor.done();
				return;
			}

			monitor.worked(1);
			monitor.done();
		}
	};

	class WASDetectProgressMonitor implements IRunnableWithProgress {

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

			monitor.beginTask(Messages.WAS_START_DETECT_WAS_LOC,
					monitor.UNKNOWN);
			// monitor.worked(1);
			monitor.setTaskName(Messages.WAS_DETECT_WAS_LOC);
			wasDetection("wasLocation.txt");
			monitor.done();
		}

	}

	private void detectWas() throws Exception {
		WASDetectProgressMonitor dialog = new WASDetectProgressMonitor();
		new ProgressMonitorDialog(parent.getShell()).run(true, true, dialog);
	}

	private String getAndCopyLCscriptPath(String fileName) {
		// application file
		String sysTempPath = System.getProperty("java.io.tmpdir");
		String appDir = sysTempPath + File.separator + LC_SCRIPT_Name;

		// set the application data for future use
		profile = this.getProfile();
		if (profile != null) {
			profile.setUserData("user.lcinstallscript.path", appDir);
		}

		File appFile = new File(appDir);
		if (!appFile.exists()) {
			appFile.mkdirs();
		}
		String toFilePath = appDir + File.separator + fileName;
		log.info(this.className +": toFilePath: " + toFilePath);

		File toFile = new File(toFilePath);
		if (toFile.exists()) {
			toFile.delete();
		}

		// copy script to system temp folder
		InputStream fileInput = DetectiveProfileAction.class.getClassLoader().getResourceAsStream(fileName);
		copyFile(fileInput, toFilePath);
		log.info(this.className +": script file path: " + toFile.getAbsolutePath());
		return toFile.getAbsolutePath();
	}

	private boolean copyFile(InputStream is, String toFile) {
		try {
			// InputStream is = new FileInputStream(fromFile);
			FileOutputStream fos = new FileOutputStream(toFile);
			log.info("Was panel : is == null : " + is == null);
			for (int b = is.read(); b != -1; b = is.read()) {
				fos.write(b);
			}

			is.close();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			log.error(e);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}

	/** compare and display the versions */
	private void compareVersion(String input, String wasVersionPath)
			throws Exception {
		java.util.List<String> versionList = DetectiveProfileAction
				.getWasVersion(input.trim(), wasVersionPath);
		String curVersions = "";
		if (versionList != null && versionList.size() != 0)
			for (String version : versionList)
				curVersions += version;
		@SuppressWarnings("unchecked")
		java.util.List<String> supportVersions = DetectiveProfileAction
				.getSupportVersion(wasVersionPath);
		String supportVer = "";
		if (supportVersions != null && supportVersions.size() != 0)
			for (String cur : supportVersions)
				supportVer += cur;
		log.error("Support versions:" + supportVer + " Current versions:"
				+ curVersions);
	}
}
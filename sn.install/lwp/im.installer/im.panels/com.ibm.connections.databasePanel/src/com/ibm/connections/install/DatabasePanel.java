/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited. 2010, 2021                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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

public class DatabasePanel extends BaseConfigCustomPanel {
	String className = DatabasePanel.class.getName();

	// private static final Logger log =
	// Logger.getLogger(com.ibm.sametime.install.WasPanel.class);
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());
	private IProfile profile = null;

	public static String ACTIVITIES_DB_NAME = "";
	public static String BLOGS_DB_NAME = "";
	public static String COMMUNITIES_DB_NAME = "";
	public static String DOGEAR_DB_NAME = "";
	public static String FILES_DB_NAME = "";
	public static String FORUM_DB_NAME = "";
	public static String HOMEPAGE_DB_NAME = "";
	public static String PEOPLEDB_DB_NAME = "";
	public static String WIKIS_DB_NAME = "";
	public static String METRICS_DB_NAME = "";
	public static String MOBILE_DB_NAME = "";
	public static String PUSHNOTIFICATION_DB_NAME = "";
	public static String CCM_DB_NAME_GCD = "";
	public static String CCM_DB_NAME_OBJECT_STORE = "";
	public static String ICEC_DB_NAME = "";
	public static String IC360_DB_NAME = "";

	private Text hostnameText = null; // text field for path
	private Text portText = null; // text field for path
	private Text jdbcDriverText1 = null; // text field for path
	private Text jdbcDriverText2 = null; // text field for path
	private Button jdbcDriverDirectoryButton1 = null;
	private Button jdbcDriverDirectoryButton2 = null;
	private Composite parent = null;
	private Composite container = null;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Section applicationServerSection;
//	private Label cboServerLabel = null;
	protected Combo cboServer = null;

	private Label ccmFeatureNameLabel1 = null;
	private Label GCDNameLabel1 = null;
	private Label OSNameLabel1 = null;
	private Label ccmFeatureNameLabel2 = null;
	private Label GCDNameLabel2 = null;
	private Label OSNameLabel2 = null;

	private Text activitiesUseridText1 = null;
	private Text activitiesPasswordText1 = null;
	private Text activitiesDBNameText1 = null;
	private Text blogsUseridText1 = null;
	private Text blogsPasswordText1 = null;
	private Text blogsDBNameText1 = null;
	private Text communitiesUseridText1 = null;
	private Text communitiesPasswordText1 = null;
	private Text communitiesDBNameText1 = null;
	private Text dogearUseridText1 = null;
	private Text dogearPasswordText1 = null;
	private Text dogearDBNameText1 = null;
	private Text profilesUseridText1 = null;
	private Text profilesPasswordText1 = null;
	private Text profilesDBNameText1 = null;
	private Text wikisUseridText1 = null;
	private Text wikisPasswordText1 = null;
	private Text wikisDBNameText1 = null;
	private Text filesUseridText1 = null;
	private Text filesPasswordText1 = null;
	private Text filesDBNameText1 = null;
	private Text forumUseridText1 = null;
	private Text forumPasswordText1 = null;
	private Text forumDBNameText1 = null;
	private Text homepageUseridText1 = null;
	private Text homepagePasswordText1 = null;
	private Text homepageDBNameText1 = null;
	private Text metricsUseridText1 = null;
	private Text metricsPasswordText1 = null;
	private Text metricsDBNameText1 = null;
	private Text mobileUseridText1 = null;
	private Text mobilePasswordText1 = null;
	private Text mobileDBNameText1 = null;
	private Text ccmGCDUseridText1 = null;
	private Text ccmGCDPasswordText1 = null;
	private Text ccmGCDDBNameText1 = null;
	private Text ccmOSUseridText1 = null;
	private Text ccmOSPasswordText1 = null;
	private Text ccmOSDBNameText1 = null;
	private Text pushnotificationUseridText1 = null;
	private Text pushnotificationPasswordText1 = null;
	private Text pushnotificationDBNameText1 = null;
	private Text icecUseridText1 = null;
	private Text icecPasswordText1 = null;
	private Text icecDBNameText1 = null;
	private Text ic360UseridText1 = null;
	private Text ic360PasswordText1 = null;
	private Text ic360DBNameText1 = null;

	private Button checkbox1 = null;

	private Text activitiesUseridText2 = null;
	private Text activitiesPasswordText2 = null;
	private Text activitiesDBNameText2 = null;
	private Text activitiesDBHostNameText2 = null;
	private Text activitiesDBPortText2 = null;
	private Text blogsUseridText2 = null;
	private Text blogsPasswordText2 = null;
	private Text blogsDBNameText2 = null;
	private Text blogsDBHostNameText2 = null;
	private Text blogsDBPortText2 = null;
	private Text communitiesUseridText2 = null;
	private Text communitiesPasswordText2 = null;
	private Text communitiesDBNameText2 = null;
	private Text communitiesDBHostNameText2 = null;
	private Text communitiesDBPortText2 = null;
	private Text dogearUseridText2 = null;
	private Text dogearPasswordText2 = null;
	private Text dogearDBNameText2 = null;
	private Text dogearDBHostNameText2 = null;
	private Text dogearDBPortText2 = null;
	private Text profilesUseridText2 = null;
	private Text profilesPasswordText2 = null;
	private Text profilesDBNameText2 = null;
	private Text profilesDBHostNameText2 = null;
	private Text profilesDBPortText2 = null;
	private Text wikisUseridText2 = null;
	private Text wikisPasswordText2 = null;
	private Text wikisDBNameText2 = null;
	private Text wikisDBHostNameText2 = null;
	private Text wikisDBPortText2 = null;
	private Text filesUseridText2 = null;
	private Text filesPasswordText2 = null;
	private Text filesDBNameText2 = null;
	private Text filesDBHostNameText2 = null;
	private Text filesDBPortText2 = null;
	private Text forumUseridText2 = null;
	private Text forumPasswordText2 = null;
	private Text forumDBNameText2 = null;
	private Text forumDBHostNameText2 = null;
	private Text forumDBPortText2 = null;
	private Text homepageUseridText2 = null;
	private Text homepagePasswordText2 = null;
	private Text homepageDBNameText2 = null;
	private Text homepageDBHostNameText2 = null;
	private Text homepageDBPortText2 = null;
	private Text metricsUseridText2 = null;
	private Text metricsPasswordText2 = null;
	private Text metricsDBNameText2 = null;
	private Text metricsDBHostNameText2 = null;
	private Text metricsDBPortText2 = null;
	private Text mobileUseridText2 = null;
	private Text mobilePasswordText2 = null;
	private Text mobileDBNameText2 = null;
	private Text mobileDBHostNameText2 = null;
	private Text mobileDBPortText2 = null;
	private Text ccmGCDUseridText2 = null;
	private Text ccmGCDPasswordText2 = null;
	private Text ccmGCDDBNameText2 = null;
	private Text ccmGCDDBHostNameText2 = null;
	private Text ccmGCDDBPortText2 = null;
	private Text ccmOSUseridText2 = null;
	private Text ccmOSPasswordText2 = null;
	private Text ccmOSDBNameText2 = null;
	private Text ccmOSDBHostNameText2 = null;
	private Text ccmOSDBPortText2 = null;
	private Text pushnotificationUseridText2 = null;
	private Text pushnotificationPasswordText2 = null;
	private Text pushnotificationDBNameText2 = null;
	private Text pushnotificationDBHostNameText2 = null;
	private Text pushnotificationDBPortText2 = null;

	private Text icecUseridText2 = null;
	private Text icecPasswordText2 = null;
	private Text icecDBNameText2 = null;
	private Text icecDBHostNameText2 = null;
	private Text icecDBPortText2 = null;

	private Text ic360UseridText2 = null;
	private Text ic360PasswordText2 = null;
	private Text ic360DBNameText2 = null;
	private Text ic360DBHostNameText2 = null;
	private Text ic360DBPortText2 = null;

	private Button checkbox2 = null;

	private Button sameDBButton = null;
	private Button diffDBButton = null;
	private Button verifyButton1 = null;
	private Button verifyButton2 = null;

	private boolean isFixpackInstall;
	private boolean isModifyInstall;

	private ArrayList<String> selectFeaturesList = new ArrayList<String>();
	private String jdbcDriver;
	private boolean hostNameFlag = false, portFlag = false, databaseNameFlag = false,
			jdbcDriverFlag = false, userNameFlag = false, passwordFlag = false, dbconnection = false;
	private String activitiesHostName, activitiesPort, blogsHostName, blogsPort, communitiesHostName,
			communitiesPort, dogearHostName, dogearPort, profilesHostName, profilesPort,
			wikisHostName, wikisPort, filesHostName, filesPort, forumHostName,
			forumPort, homepageHostName, homepagePort, metricsHostName, metricsPort,
			mobileHostName, mobilePort, ccmGCDHostName, ccmGCDPort,
			ccmOSHostName, ccmOSPort,pushnotificationHostName,pushnotificationPort, activitiesUsername, activitiesPassword, blogsUsername,
			blogsPassword, communitiesUsername, communitiesPassword,
			dogearUsername, dogearPassword, profilesUsername, profilesPassword,
			wikisUsername, wikisPassword, filesUsername, filesPassword,
			forumUsername, forumPassword, homepageUsername, homepagePassword,
			metricsUsername, metricsPassword, mobileUsername, mobilePassword, ccmGCDUsername, ccmGCDPassword,
			ccmOSUsername, ccmOSPassword,pushnotificationUsername,pushnotificationPassword,
			activitiesDatabaseName,blogsDatabaseName,communitiesDatabaseName,dogearDatabaseName,profilesDatabaseName,wikisDatabaseName,
			filesDatabaseName,forumDatabaseName,homepageDatabaseName,metricsDatabaseName,mobileDatabaseName,ccmGCDDatabaseName,ccmOSDatabaseName,
			pushnotificationDatabaseName, icecHostName, icecPort, icecUsername, icecPassword, icecDatabaseName,
			ic360HostName, ic360Port, ic360Username, ic360Password, ic360DatabaseName;

	private boolean isRecordingMode = false;

	public DatabasePanel() {
		super(Messages.DB_DATABASE);
	}

	/**
	 * Hide or show category "CCM" in Database Config Panel
	 * @param visible
	 * @param control
	 */
	private void excludeWidgetFromDisplay(boolean exclude, Control control){
		GridData data = (GridData)control.getLayoutData();
		data.exclude = exclude;
		control.getParent().layout(false);
	}

	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		profile = getProfile();
		isRecordingMode = agent.isSkipInstall();
		log.info("Database Panel :: Entered");
		//check if in update or modifyadd
		this.isFixpackInstall = isUpdate();
		this.isModifyInstall = isModify();
		log.info("isFixpackInstall = " + isFixpackInstall);
		log.info("isModifyInstall = " + isModifyInstall);

		this.parent = parent;
		selectFeaturesList.clear();
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
		applicationServerSection.setText(Messages.DB_SELECTION);

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

		Label sameDBSelectionLabel = new Label(
				applicationServerSelectContainer, SWT.FILL);
		sameDBSelectionLabel.setText(Messages.DB_SELECTION_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sameDBSelectionLabel.setLayoutData(gd);
		this.sameDBButton = new Button(applicationServerSelectContainer,
				SWT.RADIO);
		this.sameDBButton.setBackground(applicationServerSelectContainer
				.getBackground());
		this.sameDBButton.setSelection(true);
		this.sameDBButton.setText(Messages.DB_RADIO_SAME);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sameDBButton.setLayoutData(gd);
		this.diffDBButton = new Button(applicationServerSelectContainer,
				SWT.RADIO);
		this.diffDBButton.setBackground(applicationServerSelectContainer
				.getBackground());
		this.diffDBButton.setText(Messages.DB_RADIO_DIFF);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		diffDBButton.setLayoutData(gd);

		Label noticLabel = new Label(applicationServerSelectContainer, SWT.WRAP);
		noticLabel.setText(Messages.warning_message);
		noticLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.widthHint = 600;
		noticLabel.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 248;

		final Composite dbTypeComposite = new Composite(form.getBody(),
				SWT.NONE);
		GridData dbTypeGridData = new GridData(GridData.BEGINNING);
		dbTypeGridData.horizontalSpan = 2;
		dbTypeComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		dbTypeComposite.setLayoutData(gd);

		Section dbTypeSection = toolkit.createSection(dbTypeComposite,
				Section.TITLE_BAR);
		dbTypeGridData = new GridData(GridData.FILL_HORIZONTAL);
		dbTypeSection.setLayoutData(dbTypeGridData);
		dbTypeSection.setText(Messages.DB_TYPE);

		Composite dbTypeContainer = toolkit.createComposite(dbTypeSection);
		GridLayout dbTypeLayout = new GridLayout();
		dbTypeContainer.setLayout(dbTypeLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		dbTypeContainer.setLayoutData(gd);

//		cboServerLabel = new Label(dbTypeContainer, SWT.FILL);
//		cboServerLabel.setLayoutData(inputgridData);
//		cboServerLabel.setText(Messages.DB_TYPE_INFO);
//		gd.widthHint = 248;
//		gd.horizontalSpan = 2;
//		gd.heightHint = 10;
//		cboServerLabel.setLayoutData(gd);
		cboServer = new Combo(dbTypeContainer, SWT.LEFT | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 248;
//		cboServerLabel.setLayoutData(gd);
		cboServer.add(Messages.DB_TYPE_SELECT);
		cboServer.add(Messages.DB_TYPE_DB2);
		cboServer.add(Messages.DB_TYPE_Oracle);
		cboServer.add(Messages.DB_TYPE_SQL_Server);
		dbTypeSection.setClient(dbTypeContainer);

		final Composite applicationServerSelectStackContainer = new Composite(
				form.getBody(), SWT.NONE);
		final StackLayout stackLayout = new StackLayout();
		applicationServerSelectStackContainer.setLayout(stackLayout);

		final Composite sameDBComposite = new Composite(
				applicationServerSelectStackContainer, SWT.NONE);
		final Composite diffDBComposite = new Composite(
				applicationServerSelectStackContainer, SWT.NONE);

		cboServer.select(0);
		stackLayout.topControl = sameDBComposite;
		applicationServerSelectStackContainer.layout();

		GridData gridData = new GridData(GridData.BEGINNING);
		gridData.horizontalSpan = 2;
		cboServer.setLayoutData(gridData);

		applicationServerSection.setClient(applicationServerSelectContainer);

		//disable db type dropdown for update and modify add
		if (isModifyInstall || isFixpackInstall) {
			if (profile != null) {
				log.info("database type user selected " + profile.getUserData("user.database.type"));
				this.cboServer.select(getSelectedDBIndex(profile.getUserData("user.database.type")));
				this.cboServer.setEnabled(false);
			}
		}

		cboServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String osName = System.getProperty("os.name");
				if(profile == null) profile = getProfile();
					profile.setUserData("user.database.type", getSelectedDBType());
				if (cboServer.getSelectionIndex() == 1) {
					if (sameDBButton.getSelection() == true) {

						portText.setText("50000");
						if (osName.startsWith("Windows")) {
							jdbcDriverText1.setText("C:\\Program Files\\IBM\\SQLLIB\\java");
						} else if (osName.equals("Linux")) {
							jdbcDriverText1.setText("/opt/ibm/db2/V11.1/java");
						} else if (osName.equals("AIX")) {
							jdbcDriverText1.setText("/opt/ibm/db2/V11.1/java");
						}

						if (activitiesPasswordText1 != null) {
							activitiesDBNameText1.setText(DBInfo
									.getProperty("activities.dbName.db2"));
							activitiesUseridText1.setText(DBInfo
									.getProperty("activities.dbUserName.db2"));
						}
						if (blogsPasswordText1 != null) {
							blogsDBNameText1.setText(DBInfo
									.getProperty("blogs.dbName.db2"));
							blogsUseridText1.setText(DBInfo
									.getProperty("blogs.dbUserName.db2"));
						}
						if (communitiesPasswordText1 != null) {
							communitiesDBNameText1.setText(DBInfo
									.getProperty("communities.dbName.db2"));
							communitiesUseridText1.setText(DBInfo
									.getProperty("communities.dbUserName.db2"));
						}
						if (dogearPasswordText1 != null) {
							dogearDBNameText1.setText(DBInfo
									.getProperty("dogear.dbName.db2"));
							dogearUseridText1.setText(DBInfo
									.getProperty("dogear.dbUserName.db2"));
						}
						if (metricsPasswordText1 != null) {
							metricsDBNameText1.setText(DBInfo
									.getProperty("metrics.dbName.db2"));
							metricsUseridText1.setText(DBInfo
									.getProperty("metrics.dbUserName.db2"));
						}
						if (mobilePasswordText1 != null) {
							mobileDBNameText1.setText(DBInfo
									.getProperty("mobile.dbName.db2"));
							mobileUseridText1.setText(DBInfo
									.getProperty("mobile.dbUserName.db2"));
						}
						if (profilesPasswordText1 != null) {
							profilesDBNameText1.setText(DBInfo
									.getProperty("profiles.dbName.db2"));
							profilesUseridText1.setText(DBInfo
									.getProperty("profiles.dbUserName.db2"));
						}
						if (wikisPasswordText1 != null) {
							wikisDBNameText1.setText(DBInfo
									.getProperty("wikis.dbName.db2"));
							wikisUseridText1.setText(DBInfo
									.getProperty("wikis.dbUserName.db2"));
						}
						if (filesPasswordText1 != null) {
							filesDBNameText1.setText(DBInfo
									.getProperty("files.dbName.db2"));
							filesUseridText1.setText(DBInfo
									.getProperty("files.dbUserName.db2"));
						}
						if (forumPasswordText1 != null) {
							forumDBNameText1.setText(DBInfo
									.getProperty("forum.dbName.db2"));
							forumUseridText1.setText(DBInfo
									.getProperty("forum.dbUserName.db2"));
						}
						if (homepagePasswordText1 != null) {
							homepageDBNameText1.setText(DBInfo
									.getProperty("homepage.dbName.db2"));
							homepageUseridText1.setText(DBInfo
									.getProperty("homepage.dbUserName.db2"));
						}
						if (pushnotificationPasswordText1 != null) {
							pushnotificationDBNameText1.setText(DBInfo
									.getProperty("pushnotification.dbName.db2"));
							pushnotificationUseridText1.setText(DBInfo
									.getProperty("pushnotification.dbUserName.db2"));
						}
						if (icecPasswordText1 != null) {
							icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.db2"));
							icecUseridText1.setText(DBInfo.getProperty("icec.dbUserName.db2"));
						}
						if (ic360PasswordText1 != null) {
							ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.db2"));
							ic360UseridText1.setText(DBInfo.getProperty("ic360.dbUserName.db2"));
						}

						if (ccmGCDPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")) {
								ccmFeatureNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText1);
								GCDNameLabel1.setText(Messages.GCD);
								ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.db2.gcd"));
								ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.db2.gcd"));
							}else{
								ccmFeatureNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText1);
							}
						}

						if (ccmOSPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel1);
								ccmOSDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText1);
								ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.db2.object.store"));
								ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.db2.object.store"));
							}else{
								OSNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel1);
								ccmOSDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText1);
							}
						}
					}
					if (diffDBButton.getSelection() == true) {

						if (osName.startsWith("Windows")) {
							jdbcDriverText2.setText("C:\\Program Files\\IBM\\SQLLIB\\java");
						} else if (osName.equals("Linux")) {
							jdbcDriverText2.setText("/opt/ibm/db2/V11.1/java");
						} else if (osName.equals("AIX")) {
							jdbcDriverText2.setText("/opt/ibm/db2/V11.1/java");
						}

						if (activitiesPasswordText2 != null) {
							activitiesDBPortText2.setText("50000");
							activitiesDBNameText2.setText(DBInfo
									.getProperty("activities.dbName.db2"));
							activitiesUseridText2.setText(DBInfo
									.getProperty("activities.dbUserName.db2"));
						}
						if (blogsPasswordText2 != null) {
							blogsDBPortText2.setText("50000");
							blogsDBNameText2.setText(DBInfo
									.getProperty("blogs.dbName.db2"));
							blogsUseridText2.setText(DBInfo
									.getProperty("blogs.dbUserName.db2"));
						}
						if (communitiesPasswordText2 != null) {
							communitiesDBPortText2.setText("50000");
							communitiesDBNameText2.setText(DBInfo
									.getProperty("communities.dbName.db2"));
							communitiesUseridText2.setText(DBInfo
									.getProperty("communities.dbUserName.db2"));
						}
						if (dogearPasswordText2 != null) {
							dogearDBPortText2.setText("50000");
							dogearDBNameText2.setText(DBInfo
									.getProperty("dogear.dbName.db2"));
							dogearUseridText2.setText(DBInfo
									.getProperty("dogear.dbUserName.db2"));
						}
						if (metricsPasswordText2 != null) {
							metricsDBPortText2.setText("50000");
							metricsDBNameText2.setText(DBInfo
									.getProperty("metrics.dbName.db2"));
							metricsUseridText2.setText(DBInfo
									.getProperty("metrics.dbUserName.db2"));
						}
						if (mobilePasswordText2 != null) {
							mobileDBPortText2.setText("50000");
							mobileDBNameText2.setText(DBInfo
									.getProperty("mobile.dbName.db2"));
							mobileUseridText2.setText(DBInfo
									.getProperty("mobile.dbUserName.db2"));
						}
						if (filesPasswordText2 != null) {
							filesDBPortText2.setText("50000");
							filesDBNameText2.setText(DBInfo
									.getProperty("files.dbName.db2"));
							filesUseridText2.setText(DBInfo
									.getProperty("files.dbUserName.db2"));
						}
						if (forumPasswordText2 != null) {
							forumDBPortText2.setText("50000");
							forumDBNameText2.setText(DBInfo
									.getProperty("forum.dbName.db2"));
							forumUseridText2.setText(DBInfo
									.getProperty("forum.dbUserName.db2"));
						}
						if (homepagePasswordText2 != null) {
							homepageDBPortText2.setText("50000");
							homepageDBNameText2.setText(DBInfo
									.getProperty("homepage.dbName.db2"));
							homepageUseridText2.setText(DBInfo
									.getProperty("homepage.dbUserName.db2"));
						}
						if (profilesPasswordText2 != null) {
							profilesDBPortText2.setText("50000");
							profilesDBNameText2.setText(DBInfo
									.getProperty("profiles.dbName.db2"));
							profilesUseridText2.setText(DBInfo
									.getProperty("profiles.dbUserName.db2"));
						}
						if (wikisPasswordText2 != null) {
							wikisDBPortText2.setText("50000");
							wikisDBNameText2.setText(DBInfo
									.getProperty("wikis.dbName.db2"));
							wikisUseridText2.setText(DBInfo
									.getProperty("wikis.dbUserName.db2"));
						}
						if (pushnotificationPasswordText2 != null) {
							pushnotificationDBPortText2.setText("50000");
							pushnotificationDBNameText2.setText(DBInfo
									.getProperty("pushnotification.dbName.db2"));
							pushnotificationUseridText2.setText(DBInfo
									.getProperty("pushnotification.dbUserName.db2"));
						}
						if (icecPasswordText2 != null) {
							icecDBPortText2.setText("50000");
							icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.db2"));
							icecUseridText2.setText(DBInfo.getProperty("icec.dbUserName.db2"));
						}
						if (ic360PasswordText2 != null) {
							ic360DBPortText2.setText("50000");
							ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.db2"));
							ic360UseridText2.setText(DBInfo.getProperty("ic360.dbUserName.db2"));
						}
						if (ccmGCDPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								ccmFeatureNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText2);
								GCDNameLabel2.setText(Messages.GCD);
								ccmGCDDBPortText2.setText("50000");
								ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.db2.gcd"));
								ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.db2.gcd"));
							}else{
								ccmFeatureNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText2);
							}
						}

						if (ccmOSPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel2);
								ccmOSDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText2);
								ccmOSDBPortText2.setText("50000");
								ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.db2.object.store"));
								ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.db2.object.store"));
							}else{
								OSNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel2);
								ccmOSDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText2);
							}
						}
					}

				} else if (cboServer.getSelectionIndex() == 2) {
					if (sameDBButton.getSelection() == true) {

						portText.setText("1521");

						if (osName.startsWith("Windows")) {
							jdbcDriverText1.setText("C:\\oracle\\product\\10.2.0\\db_1\\jdbc\\lib");
						} else if (osName.equals("Linux")) {
							jdbcDriverText1.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						} else if (osName.equals("AIX")) {
							jdbcDriverText1.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						}

						if (activitiesPasswordText1 != null) {
							activitiesDBNameText1.setText(DBInfo
									.getProperty("activities.dbName.oracle"));
							activitiesUseridText1.setText(DBInfo
									.getProperty("activities.dbUserName.oracle"));
						}
						if (blogsPasswordText1 != null) {
							blogsDBNameText1.setText(DBInfo
									.getProperty("blogs.dbName.oracle"));
							blogsUseridText1.setText(DBInfo
									.getProperty("blogs.dbUserName.oracle"));
						}
						if (communitiesPasswordText1 != null) {
							communitiesDBNameText1.setText(DBInfo
									.getProperty("communities.dbName.oracle"));
							communitiesUseridText1.setText(DBInfo
									.getProperty("communities.dbUserName.oracle"));
						}
						if (dogearPasswordText1 != null) {
							dogearDBNameText1.setText(DBInfo
									.getProperty("dogear.dbName.oracle"));
							dogearUseridText1.setText(DBInfo
									.getProperty("dogear.dbUserName.oracle"));
						}
						if (metricsPasswordText1 != null) {
							metricsDBNameText1.setText(DBInfo
									.getProperty("metrics.dbName.oracle"));
							metricsUseridText1.setText(DBInfo
									.getProperty("metrics.dbUserName.oracle"));
						}
						if (mobilePasswordText1 != null) {
							mobileDBNameText1.setText(DBInfo
									.getProperty("mobile.dbName.oracle"));
							mobileUseridText1.setText(DBInfo
									.getProperty("mobile.dbUserName.oracle"));
						}
						if (filesPasswordText1 != null) {
							filesDBNameText1.setText(DBInfo
									.getProperty("files.dbName.oracle"));
							filesUseridText1.setText(DBInfo
									.getProperty("files.dbUserName.oracle"));
						}
						if (forumPasswordText1 != null) {
							forumDBNameText1.setText(DBInfo
									.getProperty("forum.dbName.oracle"));
							forumUseridText1.setText(DBInfo
									.getProperty("forum.dbUserName.oracle"));
						}
						if (homepagePasswordText1 != null) {
							homepageDBNameText1.setText(DBInfo
									.getProperty("homepage.dbName.oracle"));
							homepageUseridText1.setText(DBInfo
									.getProperty("homepage.dbUserName.oracle"));
						}
						if (profilesPasswordText1 != null) {
							profilesDBNameText1.setText(DBInfo
									.getProperty("profiles.dbName.oracle"));
							profilesUseridText1.setText(DBInfo
									.getProperty("profiles.dbUserName.oracle"));
						}
						if (wikisPasswordText1 != null) {
							wikisDBNameText1.setText(DBInfo
									.getProperty("wikis.dbName.oracle"));
							wikisUseridText1.setText(DBInfo
									.getProperty("wikis.dbUserName.oracle"));
						}
						if (pushnotificationPasswordText1 != null) {
							pushnotificationDBNameText1.setText(DBInfo
									.getProperty("pushnotification.dbName.oracle"));
							pushnotificationUseridText1.setText(DBInfo
									.getProperty("pushnotification.dbUserName.oracle"));
						}
						if (icecPasswordText1 != null) {
							icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.oracle"));
							icecUseridText1.setText(DBInfo.getProperty("icec.dbUserName.oracle"));
						}
						if (ic360PasswordText1 != null) {
							ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.oracle"));
							ic360UseridText1.setText(DBInfo.getProperty("ic360.dbUserName.oracle"));
						}
						if (ccmGCDPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")) {
								ccmFeatureNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText1);
								GCDNameLabel1.setText(Messages.GCD);
								ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.oracle.gcd"));
							}else{
								ccmFeatureNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText1);
							}
						}

						if (ccmOSPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel1);
								ccmOSDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText1);
								ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.oracle.object.store"));
							}else{
								OSNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel1);
								ccmOSDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText1);
							}
						}

					}
					if (diffDBButton.getSelection() == true) {

						if (osName.startsWith("Windows")) {
							jdbcDriverText2.setText("C:\\oracle\\product\\10.2.0\\db_1\\jdbc\\lib");
						} else if (osName.equals("Linux")) {
							jdbcDriverText2.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						} else if (osName.equals("AIX")) {
							jdbcDriverText2.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						}

						if (activitiesPasswordText2 != null) {
							activitiesDBPortText2.setText("1521");
							activitiesDBNameText2.setText(DBInfo
									.getProperty("activities.dbName.oracle"));
							activitiesUseridText2.setText(DBInfo
									.getProperty("activities.dbUserName.oracle"));
						}
						if (blogsPasswordText2 != null) {
							blogsDBPortText2.setText("1521");
							blogsDBNameText2.setText(DBInfo
									.getProperty("blogs.dbName.oracle"));
							blogsUseridText2.setText(DBInfo
									.getProperty("blogs.dbUserName.oracle"));
						}
						if (communitiesPasswordText2 != null) {
							communitiesDBPortText2.setText("1521");
							communitiesDBNameText2.setText(DBInfo
									.getProperty("communities.dbName.oracle"));
							communitiesUseridText2.setText(DBInfo
									.getProperty("communities.dbUserName.oracle"));
						}
						if (dogearPasswordText2 != null) {
							dogearDBPortText2.setText("1521");
							dogearDBNameText2.setText(DBInfo
									.getProperty("dogear.dbName.oracle"));
							dogearUseridText2.setText(DBInfo
									.getProperty("dogear.dbUserName.oracle"));
						}
						if (metricsPasswordText2 != null) {
							metricsDBPortText2.setText("1521");
							metricsDBNameText2.setText(DBInfo
									.getProperty("metrics.dbName.oracle"));
							metricsUseridText2.setText(DBInfo
									.getProperty("metrics.dbUserName.oracle"));
						}
						if (mobilePasswordText2 != null) {
							mobileDBPortText2.setText("1521");
							mobileDBNameText2.setText(DBInfo
									.getProperty("mobile.dbName.oracle"));
							mobileUseridText2.setText(DBInfo
									.getProperty("mobile.dbUserName.oracle"));
						}
						if (filesPasswordText2 != null) {
							filesDBPortText2.setText("1521");
							filesDBNameText2.setText(DBInfo
									.getProperty("files.dbName.oracle"));
							filesUseridText2.setText(DBInfo
									.getProperty("files.dbUserName.oracle"));
						}
						if (forumPasswordText2 != null) {
							forumDBPortText2.setText("1521");
							forumDBNameText2.setText(DBInfo
									.getProperty("forum.dbName.oracle"));
							forumUseridText2.setText(DBInfo
									.getProperty("forum.dbUserName.oracle"));
						}
						if (homepagePasswordText2 != null) {
							homepageDBPortText2.setText("1521");
							homepageDBNameText2.setText(DBInfo
									.getProperty("homepage.dbName.oracle"));
							homepageUseridText2.setText(DBInfo
									.getProperty("homepage.dbUserName.oracle"));
						}
						if (profilesPasswordText2 != null) {
							profilesDBPortText2.setText("1521");
							profilesDBNameText2.setText(DBInfo
									.getProperty("profiles.dbName.oracle"));
							profilesUseridText2.setText(DBInfo
									.getProperty("profiles.dbUserName.oracle"));
						}
						if (wikisPasswordText2 != null) {
							wikisDBPortText2.setText("1521");
							wikisDBNameText2.setText(DBInfo
									.getProperty("wikis.dbName.oracle"));
							wikisUseridText2.setText(DBInfo
									.getProperty("wikis.dbUserName.oracle"));
						}
						if (pushnotificationPasswordText2 != null) {
							pushnotificationDBPortText2.setText("1521");
							pushnotificationDBNameText2.setText(DBInfo
									.getProperty("pushnotification.dbName.oracle"));
							pushnotificationUseridText2.setText(DBInfo
									.getProperty("pushnotification.dbUserName.oracle"));
						}
						if (icecPasswordText2 != null) {
							icecDBPortText2.setText("1521");
							icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.oracle"));
							icecUseridText2.setText(DBInfo.getProperty("icec.dbUserName.oracle"));
						}
						if (ic360PasswordText2 != null) {
							ic360DBPortText2.setText("1521");
							ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.oracle"));
							ic360UseridText2.setText(DBInfo.getProperty("ic360.dbUserName.oracle"));
						}
						if (ccmGCDPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								ccmFeatureNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText2);
								GCDNameLabel2.setText(Messages.GCD);
								ccmGCDDBPortText2.setText("1521");
								ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.oracle.gcd"));
							}else{
								ccmFeatureNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText2);
							}
						}

						if (ccmOSPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel2);
								ccmOSDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText2);
								ccmOSDBPortText2.setText("1521");
								ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.oracle.object.store"));
							}else{
								OSNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel2);
								ccmOSDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText2);
							}
					}
					}
				} else if (cboServer.getSelectionIndex() == 3) {
					if (sameDBButton.getSelection() == true) {

						portText.setText("1433");
						jdbcDriverText1.setText("");

						if (activitiesPasswordText1 != null) {
							activitiesDBNameText1.setText(DBInfo
									.getProperty("activities.dbName.sqlserver"));
							activitiesUseridText1.setText(DBInfo
									.getProperty("activities.dbUserName.sqlserver"));
						}
						if (blogsPasswordText1 != null) {
							blogsDBNameText1.setText(DBInfo
									.getProperty("blogs.dbName.sqlserver"));
							blogsUseridText1.setText(DBInfo
									.getProperty("blogs.dbUserName.sqlserver"));
						}
						if (communitiesPasswordText1 != null) {
							communitiesDBNameText1.setText(DBInfo
									.getProperty("communities.dbName.sqlserver"));
							communitiesUseridText1.setText(DBInfo
									.getProperty("communities.dbUserName.sqlserver"));
						}
						if (dogearPasswordText1 != null) {
							dogearDBNameText1.setText(DBInfo
									.getProperty("dogear.dbName.sqlserver"));
							dogearUseridText1.setText(DBInfo
									.getProperty("dogear.dbUserName.sqlserver"));
						}
						if (metricsPasswordText1 != null) {
							metricsDBNameText1.setText(DBInfo
									.getProperty("metrics.dbName.sqlserver"));
							metricsUseridText1.setText(DBInfo
									.getProperty("metrics.dbUserName.sqlserver"));
						}
						if (mobilePasswordText1 != null) {
							mobileDBNameText1.setText(DBInfo
									.getProperty("mobile.dbName.sqlserver"));
							mobileUseridText1.setText(DBInfo
									.getProperty("mobile.dbUserName.sqlserver"));
						}
						if (filesPasswordText1 != null) {
							filesDBNameText1.setText(DBInfo
									.getProperty("files.dbName.sqlserver"));
							filesUseridText1.setText(DBInfo
									.getProperty("files.dbUserName.sqlserver"));
						}
						if (forumPasswordText1 != null) {
							forumDBNameText1.setText(DBInfo
									.getProperty("forum.dbName.sqlserver"));
							forumUseridText1.setText(DBInfo
									.getProperty("forum.dbUserName.sqlserver"));
						}
						if (homepagePasswordText1 != null) {
							homepageDBNameText1.setText(DBInfo
									.getProperty("homepage.dbName.sqlserver"));
							homepageUseridText1.setText(DBInfo
									.getProperty("homepage.dbUserName.sqlserver"));
						}
						if (profilesPasswordText1 != null) {
							profilesDBNameText1.setText(DBInfo
									.getProperty("profiles.dbName.sqlserver"));
							profilesUseridText1.setText(DBInfo
									.getProperty("profiles.dbUserName.sqlserver"));
						}
						if (wikisPasswordText1 != null) {
							wikisDBNameText1.setText(DBInfo
									.getProperty("wikis.dbName.sqlserver"));
							wikisUseridText1.setText(DBInfo
									.getProperty("wikis.dbUserName.sqlserver"));
						}
						if (pushnotificationPasswordText1 != null) {
							pushnotificationDBNameText1.setText(DBInfo
									.getProperty("pushnotification.dbName.sqlserver"));
							pushnotificationUseridText1.setText(DBInfo
									.getProperty("pushnotification.dbUserName.sqlserver"));
						}
						if (icecPasswordText1 != null) {
							icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.sqlserver"));
							icecUseridText1.setText(DBInfo.getProperty("icec.dbUserName.sqlserver"));
						}
						if (ic360PasswordText1 != null) {
							ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.sqlserver"));
							ic360UseridText1.setText(DBInfo.getProperty("ic360.dbUserName.sqlserver"));
						}
						if (ccmGCDPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")) {
								ccmFeatureNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText1);
								GCDNameLabel1.setText(Messages.GCD);
								ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.sqlserver.gcd"));
								ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.gcd"));
							}else{
								ccmFeatureNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText1);
							}
						}

						if (ccmOSPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel1);
								ccmOSDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText1);
								ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.sqlserver.object.store"));
								ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.object.store"));
							}else{
								OSNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel1);
								ccmOSDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText1);
							}
						}
					}
					if (diffDBButton.getSelection() == true) {

						jdbcDriverText2.setText("");

						if (activitiesPasswordText2 != null) {
							activitiesDBPortText2.setText("1433");
							activitiesDBNameText2.setText(DBInfo
									.getProperty("activities.dbName.sqlserver"));
							activitiesUseridText2.setText(DBInfo
									.getProperty("activities.dbUserName.sqlserver"));
						}
						if (blogsPasswordText2 != null) {
							blogsDBPortText2.setText("1433");
							blogsDBNameText2.setText(DBInfo
									.getProperty("blogs.dbName.sqlserver"));
							blogsUseridText2.setText(DBInfo
									.getProperty("blogs.dbUserName.sqlserver"));
						}
						if (communitiesPasswordText2 != null) {
							communitiesDBPortText2.setText("1433");
							communitiesDBNameText2.setText(DBInfo
									.getProperty("communities.dbName.sqlserver"));
							communitiesUseridText2.setText(DBInfo
									.getProperty("communities.dbUserName.sqlserver"));
						}
						if (dogearPasswordText2 != null) {
							dogearDBPortText2.setText("1433");
							dogearDBNameText2.setText(DBInfo
									.getProperty("dogear.dbName.sqlserver"));
							dogearUseridText2.setText(DBInfo
									.getProperty("dogear.dbUserName.sqlserver"));
						}
						if (metricsPasswordText2 != null) {
							metricsDBPortText2.setText("1433");
							metricsDBNameText2.setText(DBInfo
									.getProperty("metrics.dbName.sqlserver"));
							metricsUseridText2.setText(DBInfo
									.getProperty("metrics.dbUserName.sqlserver"));
						}
						if (mobilePasswordText2 != null) {
							mobileDBPortText2.setText("1433");
							mobileDBNameText2.setText(DBInfo
									.getProperty("mobile.dbName.sqlserver"));
							mobileUseridText2.setText(DBInfo
									.getProperty("mobile.dbUserName.sqlserver"));
						}
						if (filesPasswordText2 != null) {
							filesDBPortText2.setText("1433");
							filesDBNameText2.setText(DBInfo
									.getProperty("files.dbName.sqlserver"));
							filesUseridText2.setText(DBInfo
									.getProperty("files.dbUserName.sqlserver"));
						}
						if (forumPasswordText2 != null) {
							forumDBPortText2.setText("1433");
							forumDBNameText2.setText(DBInfo
									.getProperty("forum.dbName.sqlserver"));
							forumUseridText2.setText(DBInfo
									.getProperty("forum.dbUserName.sqlserver"));
						}
						if (homepagePasswordText2 != null) {
							homepageDBPortText2.setText("1433");
							homepageDBNameText2.setText(DBInfo
									.getProperty("homepage.dbName.sqlserver"));
							homepageUseridText2.setText(DBInfo
									.getProperty("homepage.dbUserName.sqlserver"));
						}
						if (profilesPasswordText2 != null) {
							profilesDBPortText2.setText("1433");
							profilesDBNameText2.setText(DBInfo
									.getProperty("profiles.dbName.sqlserver"));
							profilesUseridText2.setText(DBInfo
									.getProperty("profiles.dbUserName.sqlserver"));
						}
						if (wikisPasswordText2 != null) {
							wikisDBPortText2.setText("1433");
							wikisDBNameText2.setText(DBInfo
									.getProperty("wikis.dbName.sqlserver"));
							wikisUseridText2.setText(DBInfo
									.getProperty("wikis.dbUserName.sqlserver"));
						}
						if (pushnotificationPasswordText2 != null) {
							pushnotificationDBPortText2.setText("1433");
							pushnotificationDBNameText2.setText(DBInfo
									.getProperty("pushnotification.dbName.sqlserver"));
							pushnotificationUseridText2.setText(DBInfo
									.getProperty("pushnotification.dbUserName.sqlserver"));
						}
						if (icecPasswordText2 != null) {
							icecDBPortText2.setText("1433");
							icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.sqlserver"));
							icecUseridText2.setText(DBInfo.getProperty("icec.dbUserName.sqlserver"));
						}
						if (ic360PasswordText2 != null) {
							ic360DBPortText2.setText("1433");
							ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.sqlserver"));
							ic360UseridText2.setText(DBInfo.getProperty("ic360.dbUserName.sqlserver"));
						}
						if (ccmGCDPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								ccmFeatureNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText2);
								GCDNameLabel2.setText(Messages.GCD);
								ccmGCDDBPortText2.setText("1433");
								ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.sqlserver.gcd"));
								ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.gcd"));
							}else{
								ccmFeatureNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText2);
							}
						}

						if (ccmOSPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel2);
								ccmOSDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText2);
								ccmOSDBPortText2.setText("1433");
								ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.sqlserver.object.store"));
								ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.object.store"));
							}else{
								OSNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel2);
								ccmOSDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText2);
							}
						}
					}
				}
				verifyComplete();

				if (isOnlyModifyAddExistingCCMAndModeration() || isOnlyModifyAddExistingCCM()) {
					setErrorMessage(null);
					nextEnabled = true;
					setPageComplete(true);
				}
			}
		});

		sameDBButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String osName = System.getProperty("os.name");
				if (sameDBButton.getSelection() == true) { // Cell
					diffDBButton.setSelection(false);

					if (cboServer.getSelectionIndex() == 1) {

						portText.setText("50000");

						if (osName.startsWith("Windows")) {
							jdbcDriverText1.setText("C:\\Program Files\\IBM\\SQLLIB\\java");
						} else if (osName.equals("Linux")) {
							jdbcDriverText1.setText("/opt/ibm/db2/V11.1/java");
						} else if (osName.equals("AIX")) {
							jdbcDriverText1.setText("/opt/ibm/db2/V11.1/java");
						}

						if (activitiesPasswordText1 != null) {
							activitiesDBNameText1.setText(DBInfo
									.getProperty("activities.dbName.db2"));
							activitiesUseridText1.setText(DBInfo
									.getProperty("activities.dbUserName.db2"));
						}
						if (blogsPasswordText1 != null) {
							blogsDBNameText1.setText(DBInfo
									.getProperty("blogs.dbName.db2"));
							blogsUseridText1.setText(DBInfo
									.getProperty("blogs.dbUserName.db2"));
						}
						if (communitiesPasswordText1 != null) {
							communitiesDBNameText1.setText(DBInfo
									.getProperty("communities.dbName.db2"));
							communitiesUseridText1.setText(DBInfo
									.getProperty("communities.dbUserName.db2"));
						}
						if (dogearPasswordText1 != null) {
							dogearDBNameText1.setText(DBInfo
									.getProperty("dogear.dbName.db2"));
							dogearUseridText1.setText(DBInfo
									.getProperty("dogear.dbUserName.db2"));
						}
						if (metricsPasswordText1 != null) {
							metricsDBNameText1.setText(DBInfo
									.getProperty("metrics.dbName.db2"));
							metricsUseridText1.setText(DBInfo
									.getProperty("metrics.dbUserName.db2"));
						}
						if (mobilePasswordText1 != null) {
							mobileDBNameText1.setText(DBInfo
									.getProperty("mobile.dbName.db2"));
							mobileUseridText1.setText(DBInfo
									.getProperty("mobile.dbUserName.db2"));
						}
						if (filesPasswordText1 != null) {
							filesDBNameText1.setText(DBInfo
									.getProperty("files.dbName.db2"));
							filesUseridText1.setText(DBInfo
									.getProperty("files.dbUserName.db2"));
						}
						if (forumPasswordText1 != null) {
							forumDBNameText1.setText(DBInfo
									.getProperty("forum.dbName.db2"));
							forumUseridText1.setText(DBInfo
									.getProperty("forum.dbUserName.db2"));
						}
						if (homepagePasswordText1 != null) {
							homepageDBNameText1.setText(DBInfo
									.getProperty("homepage.dbName.db2"));
							homepageUseridText1.setText(DBInfo
									.getProperty("homepage.dbUserName.db2"));
						}
						if (profilesPasswordText1 != null) {
							profilesDBNameText1.setText(DBInfo
									.getProperty("profiles.dbName.db2"));
							profilesUseridText1.setText(DBInfo
									.getProperty("profiles.dbUserName.db2"));
						}
						if (wikisPasswordText1 != null) {
							wikisDBNameText1.setText(DBInfo
									.getProperty("wikis.dbName.db2"));
							wikisUseridText1.setText(DBInfo
									.getProperty("wikis.dbUserName.db2"));
						}
						if (pushnotificationPasswordText1 != null) {
							pushnotificationDBNameText1.setText(DBInfo
									.getProperty("pushnotification.dbName.db2"));
							pushnotificationUseridText1.setText(DBInfo
									.getProperty("pushnotification.dbUserName.db2"));
						}
						if (icecPasswordText1 != null) {
							icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.db2"));
							icecUseridText1.setText(DBInfo.getProperty("icec.dbUserName.db2"));
						}
						if (ic360PasswordText1 != null) {
							ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.db2"));
							ic360UseridText1.setText(DBInfo.getProperty("ic360.dbUserName.db2"));
						}
						if (ccmGCDPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")) {
								ccmFeatureNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText1);
								GCDNameLabel1.setText(Messages.GCD);
								ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.db2.gcd"));
								ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.db2.gcd"));
							}else{
								ccmFeatureNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText1);
							}
						}

						if (ccmOSPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel1);
								ccmOSDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText1);
								ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.db2.object.store"));
								ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.db2.object.store"));
							}else{
								OSNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel1);
								ccmOSDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText1);
							}
						}
					}

					if (cboServer.getSelectionIndex() == 2) {

						portText.setText("1521");

						if (osName.startsWith("Windows")) {
							jdbcDriverText1.setText("C:\\oracle\\product\\10.2.0\\db_1\\jdbc\\lib");
						} else if (osName.equals("Linux")) {
							jdbcDriverText1.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						} else if (osName.equals("AIX")) {
							jdbcDriverText1.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						}

						if (activitiesPasswordText1 != null) {
							activitiesDBNameText1.setText(DBInfo
									.getProperty("activities.dbName.oracle"));
							activitiesUseridText1.setText(DBInfo
									.getProperty("activities.dbUserName.oracle"));
						}
						if (blogsPasswordText1 != null) {
							blogsDBNameText1.setText(DBInfo
									.getProperty("blogs.dbName.oracle"));
							blogsUseridText1.setText(DBInfo
									.getProperty("blogs.dbUserName.oracle"));
						}
						if (communitiesPasswordText1 != null) {
							communitiesDBNameText1.setText(DBInfo
									.getProperty("communities.dbName.oracle"));
							communitiesUseridText1.setText(DBInfo
									.getProperty("communities.dbUserName.oracle"));
						}
						if (dogearPasswordText1 != null) {
							dogearDBNameText1.setText(DBInfo
									.getProperty("dogear.dbName.oracle"));
							dogearUseridText1.setText(DBInfo
									.getProperty("dogear.dbUserName.oracle"));
						}
						if (metricsPasswordText1 != null) {
							metricsDBNameText1.setText(DBInfo
									.getProperty("metrics.dbName.oracle"));
							metricsUseridText1.setText(DBInfo
									.getProperty("metrics.dbUserName.oracle"));
						}
						if (mobilePasswordText1 != null) {
							mobileDBNameText1.setText(DBInfo
									.getProperty("mobile.dbName.oracle"));
							mobileUseridText1.setText(DBInfo
									.getProperty("mobile.dbUserName.oracle"));
						}
						if (filesPasswordText1 != null) {
							filesDBNameText1.setText(DBInfo
									.getProperty("files.dbName.oracle"));
							filesUseridText1.setText(DBInfo
									.getProperty("files.dbUserName.oracle"));
						}
						if (forumPasswordText1 != null) {
							forumDBNameText1.setText(DBInfo
									.getProperty("forum.dbName.oracle"));
							forumUseridText1.setText(DBInfo
									.getProperty("forum.dbUserName.oracle"));
						}
						if (homepagePasswordText1 != null) {
							homepageDBNameText1.setText(DBInfo
									.getProperty("homepage.dbName.oracle"));
							homepageUseridText1.setText(DBInfo
									.getProperty("homepage.dbUserName.oracle"));
						}
						if (profilesPasswordText1 != null) {
							profilesDBNameText1.setText(DBInfo
									.getProperty("profiles.dbName.oracle"));
							profilesUseridText1.setText(DBInfo
									.getProperty("profiles.dbUserName.oracle"));
						}
						if (wikisPasswordText1 != null) {
							wikisDBNameText1.setText(DBInfo
									.getProperty("wikis.dbName.oracle"));
							wikisUseridText1.setText(DBInfo
									.getProperty("wikis.dbUserName.oracle"));
						}
						if (pushnotificationPasswordText1 != null) {
							pushnotificationDBNameText1.setText(DBInfo
									.getProperty("pushnotification.dbName.oracle"));
							pushnotificationUseridText1.setText(DBInfo
									.getProperty("pushnotification.dbUserName.oracle"));
						}
						if (icecPasswordText1 != null) {
							icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.oracle"));
							icecUseridText1.setText(DBInfo.getProperty("icec.dbUserName.oracle"));
						}
						if (ic360PasswordText1 != null) {
							ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.oracle"));
							ic360UseridText1.setText(DBInfo.getProperty("ic360.dbUserName.oracle"));
						}
						if (ccmGCDPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")) {
								ccmFeatureNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText1);
								GCDNameLabel1.setText(Messages.GCD);
								ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.oracle.gcd"));
							}else{
								ccmFeatureNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText1);
							}
						}

						if (ccmOSPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel1);
								ccmOSDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText1);
								ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.oracle.object.store"));
							}else{
								OSNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel1);
								ccmOSDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText1);
							}
						}
					}

					if (cboServer.getSelectionIndex() == 3) {

						portText.setText("1433");
						jdbcDriverText1.setText("");

						if (activitiesPasswordText1 != null) {
							activitiesDBNameText1.setText(DBInfo
									.getProperty("activities.dbName.sqlserver"));
							activitiesUseridText1.setText(DBInfo
									.getProperty("activities.dbUserName.sqlserver"));
						}
						if (blogsPasswordText1 != null) {
							blogsDBNameText1.setText(DBInfo
									.getProperty("blogs.dbName.sqlserver"));
							blogsUseridText1.setText(DBInfo
									.getProperty("blogs.dbUserName.sqlserver"));
						}
						if (communitiesPasswordText1 != null) {
							communitiesDBNameText1.setText(DBInfo
									.getProperty("communities.dbName.sqlserver"));
							communitiesUseridText1.setText(DBInfo
									.getProperty("communities.dbUserName.sqlserver"));
						}
						if (dogearPasswordText1 != null) {
							dogearDBNameText1.setText(DBInfo
									.getProperty("dogear.dbName.sqlserver"));
							dogearUseridText1.setText(DBInfo
									.getProperty("dogear.dbUserName.sqlserver"));
						}
						if (metricsPasswordText1 != null) {
							metricsDBNameText1.setText(DBInfo
									.getProperty("metrics.dbName.sqlserver"));
							metricsUseridText1.setText(DBInfo
									.getProperty("metrics.dbUserName.sqlserver"));
						}
						if (mobilePasswordText1 != null) {
							mobileDBNameText1.setText(DBInfo
									.getProperty("mobile.dbName.sqlserver"));
							mobileUseridText1.setText(DBInfo
									.getProperty("mobile.dbUserName.sqlserver"));
						}
						if (filesPasswordText1 != null) {
							filesDBNameText1.setText(DBInfo
									.getProperty("files.dbName.sqlserver"));
							filesUseridText1.setText(DBInfo
									.getProperty("files.dbUserName.sqlserver"));
						}
						if (forumPasswordText1 != null) {
							forumDBNameText1.setText(DBInfo
									.getProperty("forum.dbName.sqlserver"));
							forumUseridText1.setText(DBInfo
									.getProperty("forum.dbUserName.sqlserver"));
						}
						if (homepagePasswordText1 != null) {
							homepageDBNameText1.setText(DBInfo
									.getProperty("homepage.dbName.sqlserver"));
							homepageUseridText1.setText(DBInfo
									.getProperty("homepage.dbUserName.sqlserver"));
						}
						if (profilesPasswordText1 != null) {
							profilesDBNameText1.setText(DBInfo
									.getProperty("profiles.dbName.sqlserver"));
							profilesUseridText1.setText(DBInfo
									.getProperty("profiles.dbUserName.sqlserver"));
						}
						if (wikisPasswordText1 != null) {
							wikisDBNameText1.setText(DBInfo
									.getProperty("wikis.dbName.sqlserver"));
							wikisUseridText1.setText(DBInfo
									.getProperty("wikis.dbUserName.sqlserver"));
						}
						if (pushnotificationPasswordText1 != null) {
							pushnotificationDBNameText1.setText(DBInfo
									.getProperty("pushnotification.dbName.sqlserver"));
							pushnotificationUseridText1.setText(DBInfo
									.getProperty("pushnotification.dbUserName.sqlserver"));
						}
						if (icecPasswordText1 != null) {
							icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.sqlserver"));
							icecUseridText1.setText(DBInfo.getProperty("icec.dbUserName.sqlserver"));
						}
						if (ic360PasswordText1 != null) {
							ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.sqlserver"));
							ic360UseridText1.setText(DBInfo.getProperty("ic360.dbUserName.sqlserver"));
						}
						if (ccmGCDPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")) {
								ccmFeatureNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText1);
								GCDNameLabel1.setText(Messages.GCD);
								ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.sqlserver.gcd"));
								ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.gcd"));
							}else{
								ccmFeatureNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel1);
								GCDNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel1);
								ccmGCDDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText1);
								ccmGCDUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText1);
								ccmGCDPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText1);
							}
						}

						if (ccmOSPasswordText1 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel1.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel1);
								ccmOSDBNameText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText1);
								ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.sqlserver.object.store"));
								ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.object.store"));
							}else{
								OSNameLabel1.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel1);
								ccmOSDBNameText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText1);
								ccmOSUseridText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText1);
								ccmOSPasswordText1.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText1);
							}
						}
					}

					stackLayout.topControl = sameDBComposite;
					applicationServerSelectStackContainer.layout();
				}
				verifyComplete();

				if (isOnlyModifyAddExistingCCMAndModeration() || isOnlyModifyAddExistingCCM()) {
					setErrorMessage(null);
					nextEnabled = true;
					setPageComplete(true);
				}
			}
		});

		diffDBButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String osName = System.getProperty("os.name");
				if (diffDBButton.getSelection() == true) { // Cell
					sameDBButton.setSelection(false);

					if (cboServer.getSelectionIndex() == 1) {

						if (osName.startsWith("Windows")) {
							jdbcDriverText2.setText("C:\\Program Files\\IBM\\SQLLIB\\java");
						} else if (osName.equals("Linux")) {
							jdbcDriverText2.setText("/opt/ibm/db2/V11.1/java");
						} else if (osName.equals("AIX")) {
							jdbcDriverText2.setText("/opt/ibm/db2/V11.1/java");
						}

						if (activitiesPasswordText2 != null) {
							activitiesDBPortText2.setText("50000");
							activitiesDBNameText2.setText(DBInfo
									.getProperty("activities.dbName.db2"));
							activitiesUseridText2.setText(DBInfo
									.getProperty("activities.dbUserName.db2"));
						}
						if (blogsPasswordText2 != null) {
							blogsDBPortText2.setText("50000");
							blogsDBNameText2.setText(DBInfo
									.getProperty("blogs.dbName.db2"));
							blogsUseridText2.setText(DBInfo
									.getProperty("blogs.dbUserName.db2"));
						}
						if (communitiesPasswordText2 != null) {
							communitiesDBPortText2.setText("50000");
							communitiesDBNameText2.setText(DBInfo
									.getProperty("communities.dbName.db2"));
							communitiesUseridText2.setText(DBInfo
									.getProperty("communities.dbUserName.db2"));
						}
						if (dogearPasswordText2 != null) {
							dogearDBPortText2.setText("50000");
							dogearDBNameText2.setText(DBInfo
									.getProperty("dogear.dbName.db2"));
							dogearUseridText2.setText(DBInfo
									.getProperty("dogear.dbUserName.db2"));
						}
						if (metricsPasswordText2 != null) {
							metricsDBPortText2.setText("50000");
							metricsDBNameText2.setText(DBInfo
									.getProperty("metrics.dbName.db2"));
							metricsUseridText2.setText(DBInfo
									.getProperty("metrics.dbUserName.db2"));
						}
						if (mobilePasswordText2 != null) {
							mobileDBPortText2.setText("50000");
							mobileDBNameText2.setText(DBInfo
									.getProperty("mobile.dbName.db2"));
							mobileUseridText2.setText(DBInfo
									.getProperty("mobile.dbUserName.db2"));
						}
						if (filesPasswordText2 != null) {
							filesDBPortText2.setText("50000");
							filesDBNameText2.setText(DBInfo
									.getProperty("files.dbName.db2"));
							filesUseridText2.setText(DBInfo
									.getProperty("files.dbUserName.db2"));
						}
						if (forumPasswordText2 != null) {
							forumDBPortText2.setText("50000");
							forumDBNameText2.setText(DBInfo
									.getProperty("forum.dbName.db2"));
							forumUseridText2.setText(DBInfo
									.getProperty("forum.dbUserName.db2"));
						}
						if (homepagePasswordText2 != null) {
							homepageDBNameText2.setText(DBInfo
									.getProperty("homepage.dbName.db2"));
							homepageUseridText2.setText(DBInfo
									.getProperty("homepage.dbUserName.db2"));
						}
						if (profilesPasswordText2 != null) {
							profilesDBPortText2.setText("50000");
							profilesDBNameText2.setText(DBInfo
									.getProperty("profiles.dbName.db2"));
							profilesUseridText2.setText(DBInfo
									.getProperty("profiles.dbUserName.db2"));
						}
						if (wikisPasswordText2 != null) {
							wikisDBPortText2.setText("50000");
							wikisDBNameText2.setText(DBInfo
									.getProperty("wikis.dbName.db2"));
							wikisUseridText2.setText(DBInfo
									.getProperty("wikis.dbUserName.db2"));
						}
						if (pushnotificationPasswordText2 != null) {
							pushnotificationDBPortText2.setText("50000");
							pushnotificationDBNameText2.setText(DBInfo
									.getProperty("pushnotification.dbName.db2"));
							pushnotificationUseridText2.setText(DBInfo
									.getProperty("pushnotification.dbUserName.db2"));
						}
						if (icecPasswordText2 != null) {
							icecDBPortText2.setText("50000");
							icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.db2"));
							icecUseridText2.setText(DBInfo.getProperty("icec.dbUserName.db2"));
						}
						if (ic360PasswordText2 != null) {
							ic360DBPortText2.setText("50000");
							ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.db2"));
							ic360UseridText2.setText(DBInfo.getProperty("ic360.dbUserName.db2"));
						}
						if (ccmGCDPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								ccmFeatureNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText2);
								GCDNameLabel2.setText(Messages.GCD);
								ccmGCDDBPortText2.setText("50000");
								ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.db2.gcd"));
								ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.db2.gcd"));
							}else{
								ccmFeatureNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText2);
							}
						}

						if (ccmOSPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel2);
								ccmOSDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText2);
								ccmOSDBPortText2.setText("50000");
								ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.db2.object.store"));
								ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.db2.object.store"));
							}else{
								OSNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel2);
								ccmOSDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText2);
							}
						}
					}

					if (cboServer.getSelectionIndex() == 2) {

						if (osName.startsWith("Windows")) {
							jdbcDriverText2.setText("C:\\oracle\\product\\10.2.0\\db_1\\jdbc\\lib");
						} else if (osName.equals("Linux")) {
							jdbcDriverText2.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						} else if (osName.equals("AIX")) {
							jdbcDriverText2.setText("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
						}

						if (activitiesPasswordText2 != null) {
							activitiesDBPortText2.setText("1521");
							activitiesDBNameText2.setText(DBInfo
									.getProperty("activities.dbName.oracle"));
							activitiesUseridText2.setText(DBInfo
									.getProperty("activities.dbUserName.oracle"));
						}
						if (blogsPasswordText2 != null) {
							blogsDBPortText2.setText("1521");
							blogsDBNameText2.setText(DBInfo
									.getProperty("blogs.dbName.oracle"));
							blogsUseridText2.setText(DBInfo
									.getProperty("blogs.dbUserName.oracle"));
						}
						if (communitiesPasswordText2 != null) {
							communitiesDBPortText2.setText("1521");
							communitiesDBNameText2.setText(DBInfo
									.getProperty("communities.dbName.oracle"));
							communitiesUseridText2.setText(DBInfo
									.getProperty("communities.dbUserName.oracle"));
						}
						if (dogearPasswordText2 != null) {
							dogearDBPortText2.setText("1521");
							dogearDBNameText2.setText(DBInfo
									.getProperty("dogear.dbName.oracle"));
							dogearUseridText2.setText(DBInfo
									.getProperty("dogear.dbUserName.oracle"));
						}
						if (metricsPasswordText2 != null) {
							metricsDBPortText2.setText("1521");
							metricsDBNameText2.setText(DBInfo
									.getProperty("metrics.dbName.oracle"));
							metricsUseridText2.setText(DBInfo
									.getProperty("metrics.dbUserName.oracle"));
						}
						if (mobilePasswordText2 != null) {
							mobileDBPortText2.setText("1521");
							mobileDBNameText2.setText(DBInfo
									.getProperty("mobile.dbName.oracle"));
							mobileUseridText2.setText(DBInfo
									.getProperty("mobile.dbUserName.oracle"));
						}
						if (filesPasswordText2 != null) {
							filesDBPortText2.setText("1521");
							filesDBNameText2.setText(DBInfo
									.getProperty("files.dbName.oracle"));
							filesUseridText2.setText(DBInfo
									.getProperty("files.dbUserName.oracle"));
						}
						if (forumPasswordText2 != null) {
							forumDBPortText2.setText("1521");
							forumDBNameText2.setText(DBInfo
									.getProperty("forum.dbName.oracle"));
							forumUseridText2.setText(DBInfo
									.getProperty("forum.dbUserName.oracle"));
						}
						if (homepagePasswordText2 != null) {
							homepageDBPortText2.setText("1521");
							homepageDBNameText2.setText(DBInfo
									.getProperty("homepage.dbName.oracle"));
							homepageUseridText2.setText(DBInfo
									.getProperty("homepage.dbUserName.oracle"));
						}
						if (profilesPasswordText2 != null) {
							profilesDBPortText2.setText("1521");
							profilesDBNameText2.setText(DBInfo
									.getProperty("profiles.dbName.oracle"));
							profilesUseridText2.setText(DBInfo
									.getProperty("profiles.dbUserName.oracle"));
						}
						if (wikisPasswordText2 != null) {
							wikisDBPortText2.setText("1521");
							wikisDBNameText2.setText(DBInfo
									.getProperty("wikis.dbName.oracle"));
							wikisUseridText2.setText(DBInfo
									.getProperty("wikis.dbUserName.oracle"));
						}
						if (pushnotificationPasswordText2 != null) {
							pushnotificationDBPortText2.setText("1521");
							pushnotificationDBNameText2.setText(DBInfo
									.getProperty("pushnotification.dbName.oracle"));
							pushnotificationUseridText2.setText(DBInfo
									.getProperty("pushnotification.dbUserName.oracle"));
						}
						if (icecPasswordText2 != null) {
							icecDBPortText2.setText("1521");
							icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.oracle"));
							icecUseridText2.setText(DBInfo.getProperty("icec.dbUserName.oracle"));
						}
						if (ic360PasswordText2 != null) {
							ic360DBPortText2.setText("1521");
							ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.oracle"));
							ic360UseridText2.setText(DBInfo.getProperty("ic360.dbUserName.oracle"));
						}
						if (ccmGCDPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								ccmFeatureNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText2);
								GCDNameLabel2.setText(Messages.GCD);
								ccmGCDDBPortText2.setText("1521");
								ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.oracle.gcd"));
							}else{
								ccmFeatureNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText2);
							}
						}

						if (ccmOSPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel2);
								ccmOSDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText2);
								ccmOSDBPortText2.setText("1521");
								ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.oracle"));
								ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.oracle.object.store"));
							}else{
								OSNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel2);
								ccmOSDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText2);
							}
						}
					}

					if (cboServer.getSelectionIndex() == 3) {

						jdbcDriverText2.setText("");

						if (activitiesPasswordText2 != null) {
							activitiesDBPortText2.setText("1433");
							activitiesDBNameText2.setText(DBInfo
									.getProperty("activities.dbName.sqlserver"));
							activitiesUseridText2.setText(DBInfo
									.getProperty("activities.dbUserName.sqlserver"));
						}
						if (blogsPasswordText2 != null) {
							blogsDBPortText2.setText("1433");
							blogsDBNameText2.setText(DBInfo
									.getProperty("blogs.dbName.sqlserver"));
							blogsUseridText2.setText(DBInfo
									.getProperty("blogs.dbUserName.sqlserver"));
						}
						if (communitiesPasswordText2 != null) {
							communitiesDBPortText2.setText("1433");
							communitiesDBNameText2.setText(DBInfo
									.getProperty("communities.dbName.sqlserver"));
							communitiesUseridText2.setText(DBInfo
									.getProperty("communities.dbUserName.sqlserver"));
						}
						if (dogearPasswordText2 != null) {
							dogearDBPortText2.setText("1433");
							dogearDBNameText2.setText(DBInfo
									.getProperty("dogear.dbName.sqlserver"));
							dogearUseridText2.setText(DBInfo
									.getProperty("dogear.dbUserName.sqlserver"));
						}
						if (metricsPasswordText2 != null) {
							metricsDBPortText2.setText("1433");
							metricsDBNameText2.setText(DBInfo
									.getProperty("metrics.dbName.sqlserver"));
							metricsUseridText2.setText(DBInfo
									.getProperty("metrics.dbUserName.sqlserver"));
						}
						if (mobilePasswordText2 != null) {
							mobileDBPortText2.setText("1433");
							mobileDBNameText2.setText(DBInfo
									.getProperty("mobile.dbName.sqlserver"));
							mobileUseridText2.setText(DBInfo
									.getProperty("mobile.dbUserName.sqlserver"));
						}
						if (filesPasswordText2 != null) {
							filesDBPortText2.setText("1433");
							filesDBNameText2.setText(DBInfo
									.getProperty("files.dbName.sqlserver"));
							filesUseridText2.setText(DBInfo
									.getProperty("files.dbUserName.sqlserver"));
						}
						if (forumPasswordText2 != null) {
							forumDBPortText2.setText("1433");
							forumDBNameText2.setText(DBInfo
									.getProperty("forum.dbName.sqlserver"));
							forumUseridText2.setText(DBInfo
									.getProperty("forum.dbUserName.sqlserver"));
						}
						if (homepagePasswordText2 != null) {
							homepageDBPortText2.setText("1433");
							homepageDBNameText2.setText(DBInfo
									.getProperty("homepage.dbName.sqlserver"));
							homepageUseridText2.setText(DBInfo
									.getProperty("homepage.dbUserName.sqlserver"));
						}
						if (profilesPasswordText2 != null) {
							profilesDBPortText2.setText("1433");
							profilesDBNameText2.setText(DBInfo
									.getProperty("profiles.dbName.sqlserver"));
							profilesUseridText2.setText(DBInfo
									.getProperty("profiles.dbUserName.sqlserver"));
						}
						if (wikisPasswordText2 != null) {
							wikisDBPortText2.setText("1433");
							wikisDBNameText2.setText(DBInfo
									.getProperty("wikis.dbName.sqlserver"));
							wikisUseridText2.setText(DBInfo
									.getProperty("wikis.dbUserName.sqlserver"));
						}
						if (pushnotificationPasswordText2 != null) {
							pushnotificationDBPortText2.setText("1433");
							pushnotificationDBNameText2.setText(DBInfo
									.getProperty("pushnotification.dbName.sqlserver"));
							pushnotificationUseridText2.setText(DBInfo
									.getProperty("pushnotification.dbUserName.sqlserver"));
						}
						if (icecPasswordText2 != null) {
							icecDBPortText2.setText("1433");
							icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.sqlserver"));
							icecUseridText2.setText(DBInfo.getProperty("icec.dbUserName.sqlserver"));
						}
						if (ic360PasswordText2 != null) {
							ic360DBPortText2.setText("1433");
							ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.sqlserver"));
							ic360UseridText2.setText(DBInfo.getProperty("ic360.dbUserName.sqlserver"));
						}
						if (ccmGCDPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								ccmFeatureNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmGCDPasswordText2);
								GCDNameLabel2.setText(Messages.GCD);
								ccmGCDDBPortText2.setText("1433");
								ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.sqlserver.gcd"));
								ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.gcd"));
							}else{
								ccmFeatureNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmFeatureNameLabel2);
								GCDNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, GCDNameLabel2);
								ccmGCDDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBPortText2);
								ccmGCDDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBNameText2);
								ccmGCDDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDDBHostNameText2);
								ccmGCDUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDUseridText2);
								ccmGCDPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmGCDPasswordText2);
							}
						}

						if (ccmOSPasswordText2 != null){
							if (profile.getUserData("user.ccm.existingDeployment").equals("false")){
								OSNameLabel2.setVisible(true);
								excludeWidgetFromDisplay(false, OSNameLabel2);
								ccmOSDBPortText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(true);
								excludeWidgetFromDisplay(false, ccmOSPasswordText2);
								ccmOSDBPortText2.setText("1433");
								ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.sqlserver.object.store"));
								ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.sqlserver.object.store"));
							}else{
								OSNameLabel2.setVisible(false);
								excludeWidgetFromDisplay(true, OSNameLabel2);
								ccmOSDBPortText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBPortText2);
								ccmOSDBNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBNameText2);
								ccmOSDBHostNameText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSDBHostNameText2);
								ccmOSUseridText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSUseridText2);
								ccmOSPasswordText2.setVisible(false);
								excludeWidgetFromDisplay(true, ccmOSPasswordText2);
							}
						}
					}

					stackLayout.topControl = diffDBComposite;
					applicationServerSelectStackContainer.layout();
				}
				verifyComplete();

				if (isOnlyModifyAddExistingCCMAndModeration() || isOnlyModifyAddExistingCCM()) {
					setErrorMessage(null);
					nextEnabled = true;
					setPageComplete(true);
				}
			}
		});

		// sameDBComposite
		sameDBComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		sameDBComposite.setLayoutData(gd);

		Section dbInfoSection = toolkit.createSection(sameDBComposite,
				Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		dbInfoSection.setLayoutData(gridData);
		dbInfoSection.setText(Messages.DB_PROPERTIES);

		Composite dbInfoContainer = toolkit.createComposite(dbInfoSection);
		GridLayout dbInfoLayout = new GridLayout();
		dbInfoLayout.numColumns = 2;
		dbInfoContainer.setLayout(dbInfoLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		dbInfoContainer.setLayoutData(gd);

		new Label(dbInfoContainer, SWT.NONE).setText(Messages.DB_HOST_NAME);
		this.hostnameText = new Text(dbInfoContainer, SWT.BORDER | SWT.SINGLE);

		//provide dbhost default value for update and modify add
		if (isModifyInstall || isFixpackInstall) {
			if (profile != null) {
				log.info("dbHost: "+ profile.getUserData("user."+ Constants.FEATURE_ID_NEWS +".dbHostName"));
				this.hostnameText.setText(profile.getUserData("user."+ Constants.FEATURE_ID_NEWS +".dbHostName"));
				this.hostnameText.setEditable(false);
			}
		} else {
			this.hostnameText.setText("");
		}
		this.hostnameText.setLayoutData(inputgridData);
		this.hostnameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyHostNameComplete(true);
			}
		});

		new Label(dbInfoContainer, SWT.NONE).setText(Messages.DB_PORT);
		this.portText = new Text(dbInfoContainer, SWT.BORDER | SWT.SINGLE);
		this.portText.setLayoutData(inputgridData);
		//provide default dbport value for update and modify add
		if (isModifyInstall || isFixpackInstall) {
			if (profile != null) {
				log.info("dbPort: "+ profile.getUserData("user."+ Constants.FEATURE_ID_NEWS +".dbPort"));
				this.portText.setText(profile.getUserData("user."+ Constants.FEATURE_ID_NEWS +".dbPort"));
				this.portText.setEditable(false);
			}
		}
//		this.portText.setText("50000");
		this.portText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyPortComplete();
			}
		});

		new Label(dbInfoContainer, SWT.NONE).setText(Messages.DB_DRIVER_LIB);
		new Label(dbInfoContainer, SWT.NONE);
		this.jdbcDriverText1 = new Text(dbInfoContainer, SWT.BORDER
				| SWT.SINGLE);

		gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 248;
		this.jdbcDriverText1.setLayoutData(gd);
		//provide default jdbc driver path for update and modify add
		if (isModifyInstall || isFixpackInstall) {
			if (profile != null) {
				log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_NEWS +".jdbcLibraryPath"));
				this.jdbcDriverText1.setText(profile.getUserData("user."+ Constants.FEATURE_ID_NEWS +".jdbcLibraryPath"));
			}
		}
		this.jdbcDriverText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyJDBCDriverPathComplete();
			}
		});

		jdbcDriverDirectoryButton1 = new Button(dbInfoContainer, SWT.NONE);
		jdbcDriverDirectoryButton1.setText(Messages.DB_BTN_Browse);
		jdbcDriverDirectoryButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(
						applicationServerSelectContainer.getShell());
				if (jdbcDriverText1.getText() != null) {
					dialog.setFilterPath(jdbcDriverText1.getText());
				}
				dialog.setMessage(Messages.DB_SELECT_DRIVER_LIB);
				String dir = dialog.open();
				if (dir != null) {
					jdbcDriverText1.setText(dir);
				}
			}
		});

		dbInfoSection.setClient(dbInfoContainer);

		Section featureInputsSection1 = toolkit.createSection(sameDBComposite,
				Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		featureInputsSection1.setLayoutData(gridData);
		featureInputsSection1.setText(Messages.DB_FEATURE_INFO);

		Composite featureInputsContainer = toolkit
				.createComposite(featureInputsSection1);
		GridLayout featureInputsLayout = new GridLayout();
		featureInputsLayout.numColumns = 4;
		featureInputsContainer.setLayout(featureInputsLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		featureInputsContainer.setLayoutData(gd);

		checkbox1 = new Button(featureInputsContainer, SWT.CHECK);
		checkbox1.setBackground(applicationServerSelectContainer
				.getBackground());
		checkbox1.setText(Messages.DB_CHECK_BTN_SAME);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 1;
		gd.horizontalSpan = 4;
		// gd.horizontalIndent = 10;
		gd.widthHint = 400;
		checkbox1.setLayoutData(gd);
		checkbox1.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {

			}

			public void widgetSelected(SelectionEvent event) {
				Object obj = event.widget;
				if (obj == null) {
					return;
				}
				if (obj instanceof Button) {
					Button check = (Button) obj;
					setTheSamePassword_1(check);
				}

			}
		});

		Label featureNameColumnLabel = new Label(featureInputsContainer,
				SWT.NONE);
		featureNameColumnLabel.setText(Messages.DB_TABLE_FEATURE);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		gd.widthHint = 220;
		featureNameColumnLabel.setLayoutData(gd);
		Label DBNameColumnLabel = new Label(featureInputsContainer, SWT.NONE);
		DBNameColumnLabel.setText(Messages.DB_TABLE_DB_NAME);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 150;
		DBNameColumnLabel.setLayoutData(gd);
		Label DBUserIDColumnLabel = new Label(featureInputsContainer, SWT.NONE);
		DBUserIDColumnLabel.setText(Messages.DB_TABLE_USER_ID);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 120;
		DBUserIDColumnLabel.setLayoutData(gd);
		Label DBPasswordColumnLabel = new Label(featureInputsContainer,
				SWT.NONE);
		DBPasswordColumnLabel.setText(Messages.DB_TABLE_PWD);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 120;
		DBPasswordColumnLabel.setLayoutData(gd);

		if (isFeatureSelected("activities")) {
			if (!selectFeaturesList.contains("activities")) {
				selectFeaturesList.add("activities");
			}

			Label activitiesFeatureNameLabel = new Label(
					featureInputsContainer, SWT.NONE);
			activitiesFeatureNameLabel.setText(Messages.ACTIVITIES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			activitiesFeatureNameLabel.setLayoutData(gd);

			activitiesDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			activitiesDBNameText1.setText(DBInfo
					.getProperty("activities.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			activitiesDBNameText1.setLayoutData(gd);

			this.activitiesUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.activitiesUseridText1.setText(DBInfo
					.getProperty("activities.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			activitiesUseridText1.setLayoutData(gd);
			this.activitiesUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.activitiesPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			activitiesPasswordText1.setLayoutData(gd);
			this.activitiesPasswordText1
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							// verifyPasswordComplete();
							Object obj = e.widget;
							if (obj == null) {
								return;
							}
							if (obj instanceof Text) {
								Text text = (Text) obj;
								if (checkbox1.getSelection()) {
									if (blogsPasswordText1 != null) {
										blogsPasswordText1.setText(text
												.getText());
									}
									if (dogearPasswordText1 != null) {
										dogearPasswordText1.setText(text
												.getText());
									}
									if (communitiesPasswordText1 != null) {
										communitiesPasswordText1.setText(text
												.getText());
									}
									if (filesPasswordText1 != null) {
										filesPasswordText1.setText(text
												.getText());
									}
									if (forumPasswordText1 != null) {
										forumPasswordText1.setText(text
												.getText());
									}
									if (homepagePasswordText1 != null) {
										homepagePasswordText1.setText(text
												.getText());
									}
									if (ccmGCDPasswordText1 != null){
										ccmGCDPasswordText1.setText(text.getText());
									}
									if (ccmOSPasswordText1 != null){
										ccmOSPasswordText1.setText(text.getText());
									}
									if (metricsPasswordText1 != null) {
										metricsPasswordText1.setText(text
												.getText());
									}
									if (mobilePasswordText1 != null) {
										mobilePasswordText1.setText(text
												.getText());
									}
									if (profilesPasswordText1 != null) {
										profilesPasswordText1.setText(text
												.getText());
									}
									if (wikisPasswordText1 != null) {
										wikisPasswordText1.setText(text
												.getText());
									}
									if (pushnotificationPasswordText1 != null) {
										pushnotificationPasswordText1.setText(text
												.getText());
									}
									if (icecPasswordText1 != null) {
										icecPasswordText1.setText(text
												.getText());
									}
									if (ic360PasswordText1 != null) {
										ic360PasswordText1.setText(text
												.getText());
									}
								}
							}
						}
					});
		}

		if (isFeatureSelected("blogs")) {

			if (!selectFeaturesList.contains("blogs")) {
				selectFeaturesList.add("blogs");
			}

			Label blogsFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			blogsFeatureNameLabel.setText(Messages.BLOGS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			blogsFeatureNameLabel.setLayoutData(gd);

			blogsDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			blogsDBNameText1.setText(DBInfo.getProperty("blogs.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			blogsDBNameText1.setLayoutData(gd);

			this.blogsUseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.blogsUseridText1.setText(DBInfo
					.getProperty("blogs.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			blogsUseridText1.setLayoutData(gd);
			this.blogsUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.blogsPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			blogsPasswordText1.setLayoutData(gd);
			this.blogsPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (dogearPasswordText1 != null) {
								dogearPasswordText1.setText(text.getText());
							}
							if (communitiesPasswordText1 != null) {
								communitiesPasswordText1.setText(text.getText());
							}
							if (filesPasswordText1 != null) {
								filesPasswordText1.setText(text.getText());
							}
							if (forumPasswordText1 != null) {
								forumPasswordText1.setText(text.getText());
							}
							if (homepagePasswordText1 != null) {
								homepagePasswordText1.setText(text.getText());
							}
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text.getText());
							}
							if (pushnotificationPasswordText1 != null) {
								pushnotificationPasswordText1.setText(text
										.getText());
							}
							if (icecPasswordText1 != null) {
								icecPasswordText1.setText(text
										.getText());
							}
							if (ic360PasswordText1 != null) {
								ic360PasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("dogear")) {

			if (!selectFeaturesList.contains("dogear")) {
				selectFeaturesList.add("dogear");
			}

			Label dogearFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			dogearFeatureNameLabel.setText(Messages.DOGEAR);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			dogearFeatureNameLabel.setLayoutData(gd);

			dogearDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			dogearDBNameText1.setText(DBInfo.getProperty("dogear.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			dogearDBNameText1.setLayoutData(gd);

			this.dogearUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.dogearUseridText1.setText(DBInfo
					.getProperty("dogear.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			dogearUseridText1.setLayoutData(gd);
			this.dogearUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.dogearPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			dogearPasswordText1.setLayoutData(gd);
			this.dogearPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if(communitiesPasswordText1!=null){
								communitiesPasswordText1.setText(text.getText());
							}
							if (filesPasswordText1 != null) {
								filesPasswordText1.setText(text.getText());
							}
							if (forumPasswordText1 != null) {
								forumPasswordText1.setText(text.getText());
							}
							if (homepagePasswordText1 != null) {
								homepagePasswordText1.setText(text.getText());
							}
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text.getText());
							}
							if (pushnotificationPasswordText1 != null) {
								pushnotificationPasswordText1.setText(text
										.getText());
							}
							if (icecPasswordText1 != null) {
								icecPasswordText1.setText(text
										.getText());
							}
							if (ic360PasswordText1 != null) {
								ic360PasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("communities")) {

			if (!selectFeaturesList.contains("communities")) {
				selectFeaturesList.add("communities");
			}

			Label communitiesFeatureNameLabel = new Label(
					featureInputsContainer, SWT.NONE);
			communitiesFeatureNameLabel.setText(Messages.COMMUNITIES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			communitiesFeatureNameLabel.setLayoutData(gd);

			communitiesDBNameText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			communitiesDBNameText1.setText(DBInfo
					.getProperty("communities.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			communitiesDBNameText1.setLayoutData(gd);

			this.communitiesUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.communitiesUseridText1.setText(DBInfo
					.getProperty("communities.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			communitiesUseridText1.setLayoutData(gd);
			this.communitiesUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.communitiesPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			communitiesPasswordText1.setLayoutData(gd);
			this.communitiesPasswordText1
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							// verifyPasswordComplete();
							Object obj = e.widget;
							if (obj == null) {
								return;
							}
							if (obj instanceof Text) {
								Text text = (Text) obj;
								if (checkbox1.getSelection()) {
									if (filesPasswordText1 != null) {
										filesPasswordText1.setText(text
												.getText());
									}
									if (forumPasswordText1 != null) {
										forumPasswordText1.setText(text
												.getText());
									}
									if (homepagePasswordText1 != null) {
										homepagePasswordText1.setText(text
												.getText());
									}
									if (ccmGCDPasswordText1 != null){
										ccmGCDPasswordText1.setText(text.getText());
									}
									if (ccmOSPasswordText1 != null){
										ccmOSPasswordText1.setText(text.getText());
									}
									if (metricsPasswordText1 != null) {
										metricsPasswordText1.setText(text
												.getText());
									}
									if (mobilePasswordText1 != null) {
										mobilePasswordText1.setText(text
												.getText());
									}
									if (profilesPasswordText1 != null) {
										profilesPasswordText1.setText(text
												.getText());
									}
									if (wikisPasswordText1 != null) {
										wikisPasswordText1.setText(text
												.getText());
									}
									if (pushnotificationPasswordText1 != null) {
										pushnotificationPasswordText1.setText(text
												.getText());
									}
									if (icecPasswordText1 != null) {
										icecPasswordText1.setText(text
												.getText());
									}
									if (ic360PasswordText1 != null) {
										ic360PasswordText1.setText(text
												.getText());
									}
								}
							}
						}
					});
		}

		if (isFeatureSelected("files")) {

			if (!selectFeaturesList.contains("files")) {
				selectFeaturesList.add("files");
			}

			Label filesFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			filesFeatureNameLabel.setText(Messages.FILES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			filesFeatureNameLabel.setLayoutData(gd);

			filesDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			filesDBNameText1.setText(DBInfo.getProperty("files.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			filesDBNameText1.setLayoutData(gd);

			this.filesUseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.filesUseridText1.setText(DBInfo
					.getProperty("files.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			filesUseridText1.setLayoutData(gd);
			this.filesUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.filesPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			filesPasswordText1.setLayoutData(gd);
			this.filesPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (forumPasswordText1 != null) {
								forumPasswordText1.setText(text
										.getText());
							}
							if (homepagePasswordText1 != null) {
								homepagePasswordText1.setText(text
										.getText());
							}
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text
										.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text
										.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
							if (pushnotificationPasswordText1 != null) {
								pushnotificationPasswordText1.setText(text
										.getText());
							}
							if (icecPasswordText1 != null) {
								icecPasswordText1.setText(text
										.getText());
							}
							if (ic360PasswordText1 != null) {
								ic360PasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("forums")) {

			if (!selectFeaturesList.contains("forums")) {
				selectFeaturesList.add("forums");
			}

			Label forumFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			forumFeatureNameLabel.setText(Messages.FORUMS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			forumFeatureNameLabel.setLayoutData(gd);

			forumDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			forumDBNameText1.setText(DBInfo.getProperty("forum.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			forumDBNameText1.setLayoutData(gd);

			this.forumUseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.forumUseridText1.setText(DBInfo
					.getProperty("forum.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			forumUseridText1.setLayoutData(gd);
			this.forumUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.forumPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			forumPasswordText1.setLayoutData(gd);
			this.forumPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (homepagePasswordText1 != null) {
								homepagePasswordText1.setText(text
										.getText());
							}
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text
										.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text
										.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
							if (pushnotificationPasswordText1 != null) {
								pushnotificationPasswordText1.setText(text
										.getText());
							}
							if (icecPasswordText1 != null) {
								icecPasswordText1.setText(text
										.getText());
							}
							if (ic360PasswordText1 != null) {
								ic360PasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {

			if (isFeatureSelected("homepage")) {
				if (!selectFeaturesList.contains("homepage")) {
					selectFeaturesList.add("homepage");
				}
			}

			if (isFeatureSelected("search")) {
				if (!selectFeaturesList.contains("search")) {
					selectFeaturesList.add("search");
				}
			}

			if (isFeatureSelected("news")) {
				if (!selectFeaturesList.contains("news")) {
					selectFeaturesList.add("news");
				}
			}

			Label homepageFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			homepageFeatureNameLabel.setText(Messages.HOMEPAGE);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			homepageFeatureNameLabel.setLayoutData(gd);

			homepageDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			homepageDBNameText1.setText(DBInfo
					.getProperty("homepage.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			homepageDBNameText1.setLayoutData(gd);

			this.homepageUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.homepageUseridText1.setText(DBInfo
					.getProperty("homepage.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			homepageUseridText1.setLayoutData(gd);
			this.homepageUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.homepagePasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			homepagePasswordText1.setLayoutData(gd);
			this.homepagePasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text
										.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text
										.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("ccm")) {

			if (!selectFeaturesList.contains("ccm")) {
				selectFeaturesList.add("ccm");
			}

			ccmFeatureNameLabel1 = new Label(featureInputsContainer, SWT.NONE);
			ccmFeatureNameLabel1.setText(Messages.CCM);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 300;
			gd.horizontalSpan = 4;
			ccmFeatureNameLabel1.setLayoutData(gd);

			GCDNameLabel1 = new Label(featureInputsContainer, SWT.NONE);
			GCDNameLabel1.setText(Messages.GCD);
			gd = new GridData(GridData.BEGINNING);
			gd.horizontalIndent = 10;
			gd.widthHint = 160;
			GCDNameLabel1.setLayoutData(gd);

			ccmGCDDBNameText1 = new Text(featureInputsContainer, SWT.BORDER | SWT.SINGLE);
			ccmGCDDBNameText1.setText(DBInfo.getProperty("ccm.dbName.db2.gcd"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			ccmGCDDBNameText1.setLayoutData(gd);

			this.ccmGCDUseridText1 = new Text(featureInputsContainer,	SWT.BORDER | SWT.SINGLE);
			this.ccmGCDUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.db2.gcd"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			ccmGCDUseridText1.setLayoutData(gd);
			this.ccmGCDUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.ccmGCDPasswordText1 = new Text(featureInputsContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			ccmGCDPasswordText1.setLayoutData(gd);
			this.ccmGCDPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
							if (checkbox1.getSelection()) {
								if (ccmOSPasswordText1 != null){
									ccmOSPasswordText1.setText(text.getText());
								}
								if (metricsPasswordText1 != null) {
									metricsPasswordText1.setText(text
											.getText());
								}
								if (mobilePasswordText1 != null) {
									mobilePasswordText1.setText(text
											.getText());
								}
								if (profilesPasswordText1 != null) {
									profilesPasswordText1.setText(text
											.getText());
								}
								if (wikisPasswordText1 != null) {
									wikisPasswordText1.setText(text
											.getText());
								}
							}
						}
					}
			});

			OSNameLabel1 = new Label(featureInputsContainer, SWT.NONE);
			OSNameLabel1.setText(Messages.OBJECTSTORE);
			gd = new GridData(GridData.BEGINNING);
			gd.horizontalIndent = 10;
			gd.widthHint = 160;
			OSNameLabel1.setLayoutData(gd);
			ccmOSDBNameText1 = new Text(featureInputsContainer, SWT.BORDER | SWT.SINGLE);
			ccmOSDBNameText1.setText(DBInfo.getProperty("ccm.dbName.db2.object.store"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			ccmOSDBNameText1.setLayoutData(gd);

			this.ccmOSUseridText1= new Text(featureInputsContainer,	SWT.BORDER | SWT.SINGLE);
			this.ccmOSUseridText1.setText(DBInfo.getProperty("ccm.dbUserName.db2.object.store"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			ccmOSUseridText1.setLayoutData(gd);
			this.ccmOSUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.ccmOSPasswordText1 = new Text(featureInputsContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			ccmOSPasswordText1.setLayoutData(gd);
			this.ccmOSPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
				}
			});
		}

		if (isFeatureSelected("metrics")) {

			if (!selectFeaturesList.contains("metrics")) {
				selectFeaturesList.add("metrics");
			}

			Label metricsFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			metricsFeatureNameLabel.setText(Messages.METRICS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			metricsFeatureNameLabel.setLayoutData(gd);

			metricsDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			metricsDBNameText1
					.setText(DBInfo.getProperty("metrics.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			metricsDBNameText1.setLayoutData(gd);

			this.metricsUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.metricsUseridText1.setText(DBInfo
					.getProperty("metrics.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			metricsUseridText1.setLayoutData(gd);
			this.metricsUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.metricsPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			metricsPasswordText1.setLayoutData(gd);
			this.metricsPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text
										.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("mobile")) {

			if (!selectFeaturesList.contains("mobile")) {
				selectFeaturesList.add("mobile");
			}

			Label mobileFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			mobileFeatureNameLabel.setText(Messages.MOBILE);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			mobileFeatureNameLabel.setLayoutData(gd);

			mobileDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			mobileDBNameText1.setText(DBInfo.getProperty("mobile.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			mobileDBNameText1.setLayoutData(gd);

			this.mobileUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.mobileUseridText1.setText(DBInfo
					.getProperty("mobile.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			mobileUseridText1.setLayoutData(gd);
			this.mobileUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.mobilePasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			mobilePasswordText1.setLayoutData(gd);
			this.mobilePasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("profiles")) {

			if (!selectFeaturesList.contains("profiles")) {
				selectFeaturesList.add("profiles");
			}

			Label profilesFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			profilesFeatureNameLabel.setText(Messages.PROFILES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			profilesFeatureNameLabel.setLayoutData(gd);

			profilesDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			profilesDBNameText1.setText(DBInfo
					.getProperty("profiles.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			profilesDBNameText1.setLayoutData(gd);

			this.profilesUseridText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE);
			this.profilesUseridText1.setText(DBInfo
					.getProperty("profiles.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			profilesUseridText1.setLayoutData(gd);
			this.profilesUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.profilesPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			profilesPasswordText1.setLayoutData(gd);
			this.profilesPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("pushNotification")) {

			if (!selectFeaturesList.contains("pushNotification")) {
				selectFeaturesList.add("pushNotification");
			}

			Label pushnotificationFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			pushnotificationFeatureNameLabel.setText(Messages.PUSH_NOTIFICATION);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 150;
			pushnotificationFeatureNameLabel.setLayoutData(gd);

			pushnotificationDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			pushnotificationDBNameText1.setText(DBInfo.getProperty("pushnotification.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			pushnotificationDBNameText1.setLayoutData(gd);

			this.pushnotificationUseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.pushnotificationUseridText1.setText(DBInfo
					.getProperty("pushnotification.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			pushnotificationUseridText1.setLayoutData(gd);
			this.pushnotificationUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.pushnotificationPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			pushnotificationPasswordText1.setLayoutData(gd);
			this.pushnotificationPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (homepagePasswordText1 != null) {
								homepagePasswordText1.setText(text
										.getText());
							}
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text
										.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text
										.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("icec")) {
			if (!selectFeaturesList.contains("icec")) {
				selectFeaturesList.add("icec");
			}

			Label icecFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			icecFeatureNameLabel.setText(Messages.ICEC);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 220;
			icecFeatureNameLabel.setLayoutData(gd);

			icecDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			icecDBNameText1.setText(DBInfo.getProperty("icec.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			icecDBNameText1.setLayoutData(gd);

			this.icecUseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.icecUseridText1.setText(DBInfo
					.getProperty("icec.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			icecUseridText1.setLayoutData(gd);
			this.icecUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.icecPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			icecPasswordText1.setLayoutData(gd);

			this.icecPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox1.getSelection()) {
							if (homepagePasswordText1 != null) {
								homepagePasswordText1.setText(text
										.getText());
							}
							if (ccmGCDPasswordText1 != null){
								ccmGCDPasswordText1.setText(text.getText());
							}
							if (ccmOSPasswordText1 != null){
								ccmOSPasswordText1.setText(text.getText());
							}
							if (metricsPasswordText1 != null) {
								metricsPasswordText1.setText(text
										.getText());
							}
							if (mobilePasswordText1 != null) {
								mobilePasswordText1.setText(text
										.getText());
							}
							if (profilesPasswordText1 != null) {
								profilesPasswordText1.setText(text
										.getText());
							}
							if (wikisPasswordText1 != null) {
								wikisPasswordText1.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("moderation")) {
			if (!selectFeaturesList.contains("moderation")) {
				selectFeaturesList.add("moderation");
			}
		}
		if (isFeatureSelected("wikis")) {

			if (!selectFeaturesList.contains("wikis")) {
				selectFeaturesList.add("wikis");
			}

			Label wikisFeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			wikisFeatureNameLabel.setText(Messages.WIKIS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			wikisFeatureNameLabel.setLayoutData(gd);

			wikisDBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			wikisDBNameText1.setText(DBInfo.getProperty("wikis.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			wikisDBNameText1.setLayoutData(gd);

			this.wikisUseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.wikisUseridText1.setText(DBInfo
					.getProperty("wikis.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			wikisUseridText1.setLayoutData(gd);
			this.wikisUseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.wikisPasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			wikisPasswordText1.setLayoutData(gd);
			this.wikisPasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
				}
			});
		}

		if (isFeatureSelected("ic360")) {

			if (!selectFeaturesList.contains("ic360")) {
				selectFeaturesList.add("ic360");
			}

			Label ic360FeatureNameLabel = new Label(featureInputsContainer,
					SWT.NONE);
			ic360FeatureNameLabel.setText(Messages.IC360);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			ic360FeatureNameLabel.setLayoutData(gd);

			ic360DBNameText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			ic360DBNameText1.setText(DBInfo.getProperty("ic360.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			ic360DBNameText1.setLayoutData(gd);

			this.ic360UseridText1 = new Text(featureInputsContainer, SWT.BORDER
					| SWT.SINGLE);
			this.ic360UseridText1.setText(DBInfo
					.getProperty("ic360.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			ic360UseridText1.setLayoutData(gd);
			this.ic360UseridText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});
			this.ic360PasswordText1 = new Text(featureInputsContainer,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 120;
			// gd.horizontalIndent = 10;
			ic360PasswordText1.setLayoutData(gd);
			this.ic360PasswordText1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
				}
			});
		}

		GridData gridDataButtonSize = new GridData();
		gridDataButtonSize.widthHint = 200;
		gridDataButtonSize.heightHint = 30;
		gridDataButtonSize.horizontalSpan = 2;

		verifyButton1 = new Button(featureInputsContainer, SWT.PUSH);
		verifyButton1.setLayoutData(gridDataButtonSize);
		if (isRecordingMode)
			verifyButton1.setText(Messages.SKIP_VALIDATION);
		else
			verifyButton1.setText(Messages.VALIDATE);

		verifyButton1.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				verifyDatabaseNameComplete();
				if (databaseNameFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyHostNameComplete(false);
				if (hostNameFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyPortComplete();
				if (portFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyUserNameComplete();
				if (userNameFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyPasswordComplete();
				if (passwordFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyJDBCDriverPathComplete();
				if (jdbcDriverFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				if (!isRecordingMode) {
					dbConnection();
				}

				if ((hostNameFlag == true) & (portFlag == true)
						& (userNameFlag == true) & (passwordFlag == true)) {
					if (!isRecordingMode) {
						if (dbconnection == true)
							showValidationSuccessMessageDialog(Messages.VALIDATION_SUCCESSFUL);
						if(isPageComplete())
							verifyButton1.setText(Messages.VALIDATED);
					} else {
						nextEnabled = true;
						setPageComplete(true);
					}

					setData();
				} else {
					nextEnabled = false;
					setPageComplete(false);
				}
			}

		});

		featureInputsSection1.setClient(featureInputsContainer);

		// diffDBComposite
		diffDBComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		diffDBComposite.setLayoutData(gd);

		Section dbInfoSection2 = toolkit.createSection(diffDBComposite,
				Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		dbInfoSection2.setLayoutData(gridData);
		dbInfoSection2.setText(Messages.DB_PROPERTIES);

		Composite dbInfoContainer2 = toolkit.createComposite(dbInfoSection2);
		GridLayout dbInfoLayout2 = new GridLayout();
		dbInfoLayout2.numColumns = 2;
		dbInfoContainer2.setLayout(dbInfoLayout2);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		dbInfoContainer2.setLayoutData(gd);

		new Label(dbInfoContainer2, SWT.NONE).setText(Messages.DB_DRIVER_LIB);
		new Label(dbInfoContainer2, SWT.NONE);
		this.jdbcDriverText2 = new Text(dbInfoContainer2, SWT.BORDER
				| SWT.SINGLE);

		gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 248;
		this.jdbcDriverText2.setLayoutData(gd);
		this.jdbcDriverText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyJDBCDriverPathComplete();
			}
		});

		jdbcDriverDirectoryButton2 = new Button(dbInfoContainer2, SWT.NONE);
		jdbcDriverDirectoryButton2.setText(Messages.DB_BTN_Browse);
		jdbcDriverDirectoryButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(
						applicationServerSelectContainer.getShell());
				if (jdbcDriverText2.getText() != null) {
					dialog.setFilterPath(jdbcDriverText2.getText());
				}
				dialog.setMessage(Messages.DB_SELECT_DRIVER_LIB);
				String dir = dialog.open();
				if (dir != null) {
					jdbcDriverText2.setText(dir);
				}
			}
		});

		dbInfoSection2.setClient(dbInfoContainer2);

		Section featureInputsSection2 = toolkit.createSection(diffDBComposite,
				Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		featureInputsSection2.setLayoutData(gridData);
		featureInputsSection2.setText(Messages.DB_FEATURE_INFO);

		Composite featureInputsContainer2 = toolkit
				.createComposite(featureInputsSection2);
		GridLayout featureInputsLayout2 = new GridLayout();
		featureInputsLayout2.numColumns = 6;
		featureInputsContainer2.setLayout(featureInputsLayout2);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		featureInputsContainer2.setLayoutData(gd);

		checkbox2 = new Button(featureInputsContainer2, SWT.CHECK);
		checkbox2.setBackground(applicationServerSelectContainer
				.getBackground());
		checkbox2.setText(Messages.DB_CHECK_BTN_SAME);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 1;
		gd.horizontalSpan = 6;
		// gd.horizontalIndent = 20;
		gd.widthHint = 400;
		checkbox2.setLayoutData(gd);
		checkbox2.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {

			}

			public void widgetSelected(SelectionEvent event) {
				Object obj = event.widget;
				if (obj == null) {
					return;
				}
				if (obj instanceof Button) {
					Button check = (Button) obj;
					setTheSamePassword_2(check);
				}

			}
		});

		Label featureNameColumnLabel2 = new Label(featureInputsContainer2,
				SWT.NONE);
		featureNameColumnLabel2.setText(Messages.DB_TABLE_FEATURE);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		gd.widthHint = 80;
		featureNameColumnLabel2.setLayoutData(gd);

		Label DBNameColumnLabel2 = new Label(featureInputsContainer2, SWT.NONE);
		DBNameColumnLabel2.setText(Messages.DB_TABLE_DB_NAME);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 122;
		DBNameColumnLabel2.setLayoutData(gd);

		Label hostNameColumnLabel2 = new Label(featureInputsContainer2,
				SWT.NONE);
		hostNameColumnLabel2.setText(Messages.DB_TABLE_DB_HOST);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		gd.widthHint = 100;
		hostNameColumnLabel2.setLayoutData(gd);

		Label portColumnLabel2 = new Label(featureInputsContainer2, SWT.NONE);
		portColumnLabel2.setText(Messages.HOST_PORT);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		gd.widthHint = 40;
		portColumnLabel2.setLayoutData(gd);

		Label DBUserIDColumnLabel2 = new Label(featureInputsContainer2,
				SWT.NONE);
		DBUserIDColumnLabel2.setText(Messages.DB_TABLE_USER_ID);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 80;
		DBUserIDColumnLabel2.setLayoutData(gd);

		Label DBPasswordColumnLabel2 = new Label(featureInputsContainer2,
				SWT.NONE);
		DBPasswordColumnLabel2.setText(Messages.DB_TABLE_PWD);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 10;
		// gd.horizontalIndent = 10;
		gd.widthHint = 80;
		DBPasswordColumnLabel2.setLayoutData(gd);

		if (isFeatureSelected("activities")) {
			Label activitiesFeatureNameLabel = new Label(
					featureInputsContainer2, SWT.NONE);
			activitiesFeatureNameLabel.setText(Messages.ACTIVITIES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			activitiesFeatureNameLabel.setLayoutData(gd);

			activitiesDBNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			activitiesDBNameText2.setText(DBInfo
					.getProperty("activities.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			activitiesDBNameText2.setLayoutData(gd);

			this.activitiesDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.activitiesDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			activitiesDBHostNameText2.setLayoutData(gd);
			this.activitiesDBHostNameText2
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							verifyHostNameComplete(true);
						}
					});

			this.activitiesDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.activitiesDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			activitiesDBPortText2.setLayoutData(gd);
			this.activitiesDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.activitiesUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.activitiesUseridText2.setText(DBInfo
					.getProperty("activities.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			activitiesUseridText2.setLayoutData(gd);
			this.activitiesUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.activitiesPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			activitiesPasswordText2.setLayoutData(gd);
			this.activitiesPasswordText2
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							// verifyPasswordComplete();
							Object obj = e.widget;
							if (obj == null) {
								return;
							}
							if (obj instanceof Text) {
								Text text = (Text) obj;
								if (checkbox2.getSelection()) {
									if (blogsPasswordText2 != null) {
										blogsPasswordText2.setText(text
												.getText());
									}
									if (dogearPasswordText2 != null) {
										dogearPasswordText2.setText(text
												.getText());
									}
									if (communitiesPasswordText2 != null) {
										communitiesPasswordText2.setText(text
												.getText());
									}
									if (filesPasswordText2 != null) {
										filesPasswordText2.setText(text
												.getText());
									}
									if (forumPasswordText2 != null) {
										forumPasswordText2.setText(text
												.getText());
									}
									if (homepagePasswordText2 != null) {
										homepagePasswordText2.setText(text
												.getText());
									}
									if (ccmGCDPasswordText2 != null){
										ccmGCDPasswordText2.setText(text.getText());
									}
									if (ccmOSPasswordText2 != null){
										ccmOSPasswordText2.setText(text.getText());
									}
									if (metricsPasswordText2 != null) {
										metricsPasswordText2.setText(text
												.getText());
									}
									if (mobilePasswordText2 != null) {
										mobilePasswordText2.setText(text
												.getText());
									}
									if (profilesPasswordText2 != null) {
										profilesPasswordText2.setText(text
												.getText());
									}
									if (wikisPasswordText2 != null) {
										wikisPasswordText2.setText(text
												.getText());
									}
									if (pushnotificationPasswordText2 != null) {
										pushnotificationPasswordText2.setText(text
												.getText());
									}
									if (icecPasswordText2 != null) {
										icecPasswordText2.setText(text
												.getText());
									}
								}
							}
						}
					});
		}

		if (isFeatureSelected("blogs")) {
			Label blogsFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			blogsFeatureNameLabel.setText(Messages.BLOGS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			blogsFeatureNameLabel.setLayoutData(gd);

			blogsDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			blogsDBNameText2.setText(DBInfo.getProperty("blogs.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			blogsDBNameText2.setLayoutData(gd);

			this.blogsDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.blogsDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			blogsDBHostNameText2.setLayoutData(gd);
			this.blogsDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.blogsDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.blogsDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			blogsDBPortText2.setLayoutData(gd);
			this.blogsDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.blogsUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.blogsUseridText2.setText(DBInfo
					.getProperty("blogs.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			blogsUseridText2.setLayoutData(gd);
			this.blogsUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.blogsPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			blogsPasswordText2.setLayoutData(gd);
			this.blogsPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (dogearPasswordText2 != null) {
								dogearPasswordText2.setText(text
										.getText());
							}
							if (communitiesPasswordText2 != null) {
								communitiesPasswordText2.setText(text
										.getText());
							}
							if (filesPasswordText2 != null) {
								filesPasswordText2.setText(text
										.getText());
							}
							if (forumPasswordText2 != null) {
								forumPasswordText2.setText(text
										.getText());
							}
							if (homepagePasswordText2 != null) {
								homepagePasswordText2.setText(text
										.getText());
							}
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("dogear")) {
			Label dogearFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			dogearFeatureNameLabel.setText(Messages.DOGEAR);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			dogearFeatureNameLabel.setLayoutData(gd);

			dogearDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			dogearDBNameText2.setText(DBInfo.getProperty("dogear.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			dogearDBNameText2.setLayoutData(gd);

			this.dogearDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.dogearDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			dogearDBHostNameText2.setLayoutData(gd);
			this.dogearDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.dogearDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.dogearDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			dogearDBPortText2.setLayoutData(gd);
			this.dogearDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.dogearUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.dogearUseridText2.setText(DBInfo
					.getProperty("dogear.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			dogearUseridText2.setLayoutData(gd);
			this.dogearUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.dogearPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			dogearPasswordText2.setLayoutData(gd);
			this.dogearPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (communitiesPasswordText2 != null) {
								communitiesPasswordText2.setText(text
										.getText());
							}
							if (filesPasswordText2 != null) {
								filesPasswordText2.setText(text
										.getText());
							}
							if (forumPasswordText2 != null) {
								forumPasswordText2.setText(text
										.getText());
							}
							if (homepagePasswordText2 != null) {
								homepagePasswordText2.setText(text
										.getText());
							}
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("communities")) {
			Label communitiesFeatureNameLabel = new Label(
					featureInputsContainer2, SWT.NONE);
			communitiesFeatureNameLabel.setText(Messages.COMMUNITIES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 150;
			communitiesFeatureNameLabel.setLayoutData(gd);

			communitiesDBNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			communitiesDBNameText2.setText(DBInfo
					.getProperty("communities.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			communitiesDBNameText2.setLayoutData(gd);

			this.communitiesDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.communitiesDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			communitiesDBHostNameText2.setLayoutData(gd);
			this.communitiesDBHostNameText2
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							verifyHostNameComplete(true);
						}
					});

			this.communitiesDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.communitiesDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			communitiesDBPortText2.setLayoutData(gd);
			this.communitiesDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.communitiesUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.communitiesUseridText2.setText(DBInfo
					.getProperty("communities.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			communitiesUseridText2.setLayoutData(gd);
			this.communitiesUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.communitiesPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			communitiesPasswordText2.setLayoutData(gd);
			this.communitiesPasswordText2
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							// verifyPasswordComplete();
							Object obj = e.widget;
							if (obj == null) {
								return;
							}
							if (obj instanceof Text) {
								Text text = (Text) obj;
								if (checkbox2.getSelection()) {
									if (filesPasswordText2 != null) {
										filesPasswordText2.setText(text
												.getText());
									}
									if (forumPasswordText2 != null) {
										forumPasswordText2.setText(text
												.getText());
									}
									if (homepagePasswordText2 != null) {
										homepagePasswordText2.setText(text
												.getText());
									}
									if (ccmGCDPasswordText2 != null){
										ccmGCDPasswordText2.setText(text.getText());
									}
									if (ccmOSPasswordText2 != null){
										ccmOSPasswordText2.setText(text.getText());
									}
									if (metricsPasswordText2 != null) {
										metricsPasswordText2.setText(text
												.getText());
									}
									if (mobilePasswordText2 != null) {
										mobilePasswordText2.setText(text
												.getText());
									}
									if (profilesPasswordText2 != null) {
										profilesPasswordText2.setText(text
												.getText());
									}
									if (wikisPasswordText2 != null) {
										wikisPasswordText2.setText(text
												.getText());
									}
								}
							}
						}
					});
		}

		if (isFeatureSelected("files")) {
			Label filesFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			filesFeatureNameLabel.setText(Messages.FILES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			filesFeatureNameLabel.setLayoutData(gd);

			filesDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			filesDBNameText2.setText(DBInfo.getProperty("files.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			filesDBNameText2.setLayoutData(gd);

			this.filesDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.filesDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			filesDBHostNameText2.setLayoutData(gd);
			this.filesDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.filesDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.filesDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			filesDBPortText2.setLayoutData(gd);
			this.filesDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.filesUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.filesUseridText2.setText(DBInfo
					.getProperty("files.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			filesUseridText2.setLayoutData(gd);
			this.filesUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.filesPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			filesPasswordText2.setLayoutData(gd);
			this.filesPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (forumPasswordText2 != null) {
								forumPasswordText2.setText(text
										.getText());
							}
							if (homepagePasswordText2 != null) {
								homepagePasswordText2.setText(text
										.getText());
							}
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("forums")) {
			Label forumFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			forumFeatureNameLabel.setText(Messages.FORUMS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			forumFeatureNameLabel.setLayoutData(gd);

			forumDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			forumDBNameText2.setText(DBInfo.getProperty("forum.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			forumDBNameText2.setLayoutData(gd);

			this.forumDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.forumDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			forumDBHostNameText2.setLayoutData(gd);
			this.forumDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.forumDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.forumDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			forumDBPortText2.setLayoutData(gd);
			this.forumDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.forumUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.forumUseridText2.setText(DBInfo
					.getProperty("forum.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			forumUseridText2.setLayoutData(gd);
			this.forumUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.forumPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			forumPasswordText2.setLayoutData(gd);
			this.forumPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (homepagePasswordText2 != null) {
								homepagePasswordText2.setText(text
										.getText());
							}
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			Label homepageFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			homepageFeatureNameLabel.setText(Messages.HOMEPAGE);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			homepageFeatureNameLabel.setLayoutData(gd);

			homepageDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			homepageDBNameText2.setText(DBInfo
					.getProperty("homepage.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			homepageDBNameText2.setLayoutData(gd);

			this.homepageDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.homepageDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			homepageDBHostNameText2.setLayoutData(gd);
			this.homepageDBHostNameText2
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							verifyHostNameComplete(true);
						}
					});

			this.homepageDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.homepageDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			homepageDBPortText2.setLayoutData(gd);
			this.homepageDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.homepageUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.homepageUseridText2.setText(DBInfo
					.getProperty("homepage.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			homepageUseridText2.setLayoutData(gd);
			this.homepageUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.homepagePasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			homepagePasswordText2.setLayoutData(gd);
			this.homepagePasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("ccm")) {
			ccmFeatureNameLabel2 = new Label(featureInputsContainer2,
					SWT.NONE);
			ccmFeatureNameLabel2.setText(Messages.CCM);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 300;
			gd.horizontalSpan = 6;
			ccmFeatureNameLabel2.setLayoutData(gd);

			GCDNameLabel2 = new Label(featureInputsContainer2, SWT.NONE);
			GCDNameLabel2.setText(Messages.GCD);
			gd = new GridData(GridData.BEGINNING);
			gd.horizontalIndent = 10;
			gd.widthHint = 160;
			GCDNameLabel2.setLayoutData(gd);

			ccmGCDDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE);
			ccmGCDDBNameText2.setText(DBInfo.getProperty("ccm.dbName.db2.gcd"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ccmGCDDBNameText2.setLayoutData(gd);

			this.ccmGCDDBHostNameText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE);
			this.ccmGCDDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			ccmGCDDBHostNameText2.setLayoutData(gd);
			this.ccmGCDDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.ccmGCDDBPortText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE);
			this.ccmGCDDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			ccmGCDDBPortText2.setLayoutData(gd);
			this.ccmGCDDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.ccmGCDUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.ccmGCDUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.db2.gcd"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ccmGCDUseridText2.setLayoutData(gd);
			this.ccmGCDUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.ccmGCDPasswordText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ccmGCDPasswordText2.setLayoutData(gd);
			this.ccmGCDPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});

			OSNameLabel2 = new Label(featureInputsContainer2, SWT.NONE);
			OSNameLabel2.setText(Messages.OBJECTSTORE);
			gd = new GridData(GridData.BEGINNING);
			gd.horizontalIndent = 10;
			gd.widthHint = 160;
			OSNameLabel2.setLayoutData(gd);

			ccmOSDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE);
			ccmOSDBNameText2.setText(DBInfo.getProperty("ccm.dbName.db2.object.store"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ccmOSDBNameText2.setLayoutData(gd);

			this.ccmOSDBHostNameText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE);
			this.ccmOSDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			ccmOSDBHostNameText2.setLayoutData(gd);
			this.ccmOSDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.ccmOSDBPortText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE);
			this.ccmOSDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			ccmOSDBPortText2.setLayoutData(gd);
			this.ccmOSDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.ccmOSUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.ccmOSUseridText2.setText(DBInfo.getProperty("ccm.dbUserName.db2.object.store"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ccmOSUseridText2.setLayoutData(gd);
			this.ccmOSUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.ccmOSPasswordText2 = new Text(featureInputsContainer2, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ccmOSPasswordText2.setLayoutData(gd);
			this.ccmOSPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {

				}
			});
		}

		if (isFeatureSelected("metrics")) {
			Label metricsFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			metricsFeatureNameLabel.setText(Messages.METRICS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			metricsFeatureNameLabel.setLayoutData(gd);

			metricsDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			metricsDBNameText2
					.setText(DBInfo.getProperty("metrics.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			metricsDBNameText2.setLayoutData(gd);

			this.metricsDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.metricsDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			metricsDBHostNameText2.setLayoutData(gd);
			this.metricsDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.metricsDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.metricsDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			metricsDBPortText2.setLayoutData(gd);
			this.metricsDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.metricsUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.metricsUseridText2.setText(DBInfo
					.getProperty("metrics.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			metricsUseridText2.setLayoutData(gd);
			this.metricsUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.metricsPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			metricsPasswordText2.setLayoutData(gd);
			this.metricsPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("mobile")) {
			Label mobileFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			mobileFeatureNameLabel.setText(Messages.MOBILE);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			mobileFeatureNameLabel.setLayoutData(gd);

			mobileDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			mobileDBNameText2.setText(DBInfo.getProperty("mobile.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			mobileDBNameText2.setLayoutData(gd);

			this.mobileDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.mobileDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			mobileDBHostNameText2.setLayoutData(gd);
			this.mobileDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.mobileDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.mobileDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			mobileDBPortText2.setLayoutData(gd);
			this.mobileDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.mobileUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.mobileUseridText2.setText(DBInfo
					.getProperty("mobile.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			mobileUseridText2.setLayoutData(gd);
			this.mobileUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.mobilePasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			mobilePasswordText2.setLayoutData(gd);
			this.mobilePasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("profiles")) {
			Label profilesFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			profilesFeatureNameLabel.setText(Messages.PROFILES);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			profilesFeatureNameLabel.setLayoutData(gd);

			profilesDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			profilesDBNameText2.setText(DBInfo
					.getProperty("profiles.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			profilesDBNameText2.setLayoutData(gd);

			this.profilesDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.profilesDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			profilesDBHostNameText2.setLayoutData(gd);
			this.profilesDBHostNameText2
					.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							verifyHostNameComplete(true);
						}
					});

			this.profilesDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.profilesDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			profilesDBPortText2.setLayoutData(gd);
			this.profilesDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.profilesUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.profilesUseridText2.setText(DBInfo
					.getProperty("profiles.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			profilesUseridText2.setLayoutData(gd);
			this.profilesUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.profilesPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			profilesPasswordText2.setLayoutData(gd);
			this.profilesPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("pushNotification")) {
			Label pushnotificationFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			pushnotificationFeatureNameLabel.setText(Messages.PUSH_NOTIFICATION);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 150;
			pushnotificationFeatureNameLabel.setLayoutData(gd);

			pushnotificationDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			pushnotificationDBNameText2.setText(DBInfo.getProperty("pushnotification.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			pushnotificationDBNameText2.setLayoutData(gd);

			this.pushnotificationDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.pushnotificationDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			pushnotificationDBHostNameText2.setLayoutData(gd);
			this.pushnotificationDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.pushnotificationDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.pushnotificationDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			pushnotificationDBPortText2.setLayoutData(gd);
			this.pushnotificationDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.pushnotificationUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.pushnotificationUseridText2.setText(DBInfo
					.getProperty("pushnotification.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			pushnotificationUseridText2.setLayoutData(gd);
			this.pushnotificationUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.pushnotificationPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			pushnotificationPasswordText2.setLayoutData(gd);
			this.pushnotificationPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (homepagePasswordText2 != null) {
								homepagePasswordText2.setText(text
										.getText());
							}
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("icec")) {
			Label icecFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			icecFeatureNameLabel.setText(Messages.ICEC);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 220;
			icecFeatureNameLabel.setLayoutData(gd);

			icecDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			icecDBNameText2.setText(DBInfo.getProperty("icec.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			icecDBNameText2.setLayoutData(gd);

			this.icecDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.icecDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			icecDBHostNameText2.setLayoutData(gd);
			this.icecDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.icecDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.icecDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			icecDBPortText2.setLayoutData(gd);
			this.icecDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.icecUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.icecUseridText2.setText(DBInfo
					.getProperty("icec.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			icecUseridText2.setLayoutData(gd);
			this.icecUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.icecPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			icecPasswordText2.setLayoutData(gd);

			this.icecPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyPasswordComplete();
					Object obj = e.widget;
					if (obj == null) {
						return;
					}
					if (obj instanceof Text) {
						Text text = (Text) obj;
						if (checkbox2.getSelection()) {
							if (homepagePasswordText2 != null) {
								homepagePasswordText2.setText(text
										.getText());
							}
							if (ccmGCDPasswordText2 != null){
								ccmGCDPasswordText2.setText(text.getText());
							}
							if (ccmOSPasswordText2 != null){
								ccmOSPasswordText2.setText(text.getText());
							}
							if (metricsPasswordText2 != null) {
								metricsPasswordText2.setText(text
										.getText());
							}
							if (mobilePasswordText2 != null) {
								mobilePasswordText2.setText(text
										.getText());
							}
							if (profilesPasswordText2 != null) {
								profilesPasswordText2.setText(text
										.getText());
							}
							if (wikisPasswordText2 != null) {
								wikisPasswordText2.setText(text
										.getText());
							}
						}
					}
				}
			});
		}

		if (isFeatureSelected("wikis")) {
			Label wikisFeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			wikisFeatureNameLabel.setText(Messages.WIKIS);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			wikisFeatureNameLabel.setLayoutData(gd);

			wikisDBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			wikisDBNameText2.setText(DBInfo.getProperty("wikis.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			wikisDBNameText2.setLayoutData(gd);

			this.wikisDBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.wikisDBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			wikisDBHostNameText2.setLayoutData(gd);
			this.wikisDBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.wikisDBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.wikisDBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			wikisDBPortText2.setLayoutData(gd);
			this.wikisDBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.wikisUseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.wikisUseridText2.setText(DBInfo
					.getProperty("wikis.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			wikisUseridText2.setLayoutData(gd);
			this.wikisUseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.wikisPasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			wikisPasswordText2.setLayoutData(gd);
			this.wikisPasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
				}
			});
		}

		if (isFeatureSelected("ic360")) {
			Label ic360FeatureNameLabel = new Label(featureInputsContainer2,
					SWT.NONE);
			ic360FeatureNameLabel.setText(Messages.IC360);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			ic360FeatureNameLabel.setLayoutData(gd);

			ic360DBNameText2 = new Text(featureInputsContainer2, SWT.BORDER
					| SWT.SINGLE);
			ic360DBNameText2.setText(DBInfo.getProperty("ic360.dbName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			ic360DBNameText2.setLayoutData(gd);

			this.ic360DBHostNameText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.ic360DBHostNameText2.setText("");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 100;
			// gd.horizontalIndent = 10;
			ic360DBHostNameText2.setLayoutData(gd);
			this.ic360DBHostNameText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyHostNameComplete(true);
				}
			});

			this.ic360DBPortText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.ic360DBPortText2.setText("50000");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 40;
			// gd.horizontalIndent = 10;
			ic360DBPortText2.setLayoutData(gd);
			this.ic360DBPortText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyPortComplete();
				}
			});

			this.ic360UseridText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE);
			this.ic360UseridText2.setText(DBInfo
					.getProperty("ic360.dbUserName.db2"));
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			ic360UseridText2.setLayoutData(gd);
			this.ic360UseridText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// verifyUserNameComplete();
				}
			});

			this.ic360PasswordText2 = new Text(featureInputsContainer2,
					SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 80;
			// gd.horizontalIndent = 10;
			ic360PasswordText2.setLayoutData(gd);
			this.ic360PasswordText2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
				}
			});
		}

		gridDataButtonSize = new GridData();
		gridDataButtonSize.widthHint = 200;
		gridDataButtonSize.heightHint = 30;
		gridDataButtonSize.horizontalSpan = 2;

		verifyButton2 = new Button(featureInputsContainer2, SWT.PUSH);
		verifyButton2.setLayoutData(gridDataButtonSize);
		if (isRecordingMode)
			verifyButton2.setText(Messages.SKIP_VALIDATION);
		else
			verifyButton2.setText(Messages.VALIDATE);
		verifyButton2.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				verifyDatabaseNameComplete();
				if (databaseNameFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyHostNameComplete(false);
				if (hostNameFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyPortComplete();
				if (portFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyUserNameComplete();
				if (userNameFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				verifyPasswordComplete();
				if (passwordFlag == false) {
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				if (!isRecordingMode) {
					verifyJDBCDriverPathComplete();
					if (jdbcDriverFlag == false) {
						nextEnabled = false;
						setPageComplete(false);
						return;
					}

					dbConnection();
				}
				if ((hostNameFlag == true) & (portFlag == true)
						& (userNameFlag == true) & (passwordFlag == true)) {
					if (!isRecordingMode) {
						if (dbconnection == true)
							showValidationSuccessMessageDialog(Messages.VALIDATION_SUCCESSFUL);
						if(isPageComplete())
							verifyButton2.setText(Messages.VALIDATED);
					} else {
						nextEnabled = true;
						setPageComplete(true);
					}

					setData();
				} else {
					nextEnabled = false;
					setPageComplete(false);
				}
			}

		});

		featureInputsSection2.setClient(featureInputsContainer2);

		form.pack();
		setControl(container);
	}

	private void setData() {
		log.info("db setData");
		if (sameDBButton.getSelection() == true) {
			if (activitiesDBNameText1 != null)
				ACTIVITIES_DB_NAME = activitiesDBNameText1.getText().trim();
			if (blogsDBNameText1 != null)
				BLOGS_DB_NAME = blogsDBNameText1.getText().trim();
			if (communitiesDBNameText1 != null)
				COMMUNITIES_DB_NAME = communitiesDBNameText1.getText().trim();
			if (dogearDBNameText1 != null)
				DOGEAR_DB_NAME = dogearDBNameText1.getText().trim();
			if (filesDBNameText1 != null)
				FILES_DB_NAME = filesDBNameText1.getText().trim();
			if (forumDBNameText1 != null)
				FORUM_DB_NAME = forumDBNameText1.getText().trim();
			if (homepageDBNameText1 != null)
				HOMEPAGE_DB_NAME = homepageDBNameText1.getText().trim();
			if (profilesDBNameText1 != null)
				PEOPLEDB_DB_NAME = profilesDBNameText1.getText().trim();
			if (wikisDBNameText1 != null)
				WIKIS_DB_NAME = wikisDBNameText1.getText().trim();
			if (metricsDBNameText1 != null)
				METRICS_DB_NAME = metricsDBNameText1.getText().trim();
			if (mobileDBNameText1 != null)
				MOBILE_DB_NAME = mobileDBNameText1.getText().trim();
			if (pushnotificationDBNameText1 != null)
				PUSHNOTIFICATION_DB_NAME = pushnotificationDBNameText1.getText().trim();
			if (icecDBNameText1 != null)
				ICEC_DB_NAME = icecDBNameText1.getText().trim();
			if (ic360DBNameText1 != null)
				IC360_DB_NAME = ic360DBNameText1.getText().trim();
			if (ccmGCDDBNameText1 != null && ccmGCDDBNameText1.isVisible()){
				CCM_DB_NAME_GCD = ccmGCDDBNameText1.getText().trim();
				CCM_DB_NAME_OBJECT_STORE = ccmOSDBNameText1.getText().trim();
			}
		}
		if (diffDBButton.getSelection() == true) {
			if (activitiesDBNameText2 != null)
				ACTIVITIES_DB_NAME = activitiesDBNameText2.getText().trim();
			if (blogsDBNameText2 != null)
				BLOGS_DB_NAME = blogsDBNameText2.getText().trim();
			if (communitiesDBNameText2 != null)
				COMMUNITIES_DB_NAME = communitiesDBNameText2.getText().trim();
			if (dogearDBNameText2 != null)
				DOGEAR_DB_NAME = dogearDBNameText2.getText().trim();
			if (filesDBNameText2 != null)
				FILES_DB_NAME = filesDBNameText2.getText().trim();
			if (forumDBNameText2 != null)
				FORUM_DB_NAME = forumDBNameText2.getText().trim();
			if (homepageDBNameText2 != null)
				HOMEPAGE_DB_NAME = homepageDBNameText2.getText().trim();
			if (profilesDBNameText2 != null)
				PEOPLEDB_DB_NAME = profilesDBNameText2.getText().trim();
			if (wikisDBNameText2 != null)
				WIKIS_DB_NAME = wikisDBNameText2.getText().trim();
			if (metricsDBNameText2 != null)
				METRICS_DB_NAME = metricsDBNameText2.getText().trim();
			if (mobileDBNameText2 != null)
				MOBILE_DB_NAME = mobileDBNameText2.getText().trim();
			if (pushnotificationDBNameText2 != null)
				PUSHNOTIFICATION_DB_NAME = pushnotificationDBNameText2.getText().trim();
			if (icecDBNameText2 != null)
				ICEC_DB_NAME = icecDBNameText2.getText().trim();
			if (ic360DBNameText2 != null)
				IC360_DB_NAME = ic360DBNameText2.getText().trim();
			if (ccmGCDDBNameText2 != null && ccmGCDDBNameText2.isVisible()){
				CCM_DB_NAME_GCD = ccmGCDDBNameText2.getText().trim();
				CCM_DB_NAME_OBJECT_STORE = ccmOSDBNameText2.getText().trim();
			}
		}

		String dbType = getSelectedDBType();
		String jdbcLibPath = transferPath(jdbcDriver);

		if (isFeatureSelected(Constants.FEATURE_ID_ACTIVITIES)) {
			setDBUserData(Constants.FEATURE_ID_ACTIVITIES, dbType, jdbcLibPath, activitiesHostName, activitiesPort, ACTIVITIES_DB_NAME, activitiesUsername, EncryptionUtils.encrypt(Util.xor(activitiesPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_BLOGS)) {
			setDBUserData(Constants.FEATURE_ID_BLOGS, dbType, jdbcLibPath, blogsHostName, blogsPort, BLOGS_DB_NAME, blogsUsername, EncryptionUtils.encrypt(Util.xor(blogsPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_COMMUNITIES)) {
			setDBUserData(Constants.FEATURE_ID_COMMUNITIES, dbType, jdbcLibPath, communitiesHostName, communitiesPort, COMMUNITIES_DB_NAME, communitiesUsername, EncryptionUtils.encrypt(Util.xor(communitiesPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_BOOKMARKS)) {
			setDBUserData(Constants.FEATURE_ID_BOOKMARKS, dbType, jdbcLibPath, dogearHostName, dogearPort, DOGEAR_DB_NAME, dogearUsername, EncryptionUtils.encrypt(Util.xor(dogearPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_PROFILES)) {
			setDBUserData(Constants.FEATURE_ID_PROFILES, dbType, jdbcLibPath, profilesHostName, profilesPort, PEOPLEDB_DB_NAME, profilesUsername, EncryptionUtils.encrypt(Util.xor(profilesPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_WIKIS)) {
			setDBUserData(Constants.FEATURE_ID_WIKIS, dbType, jdbcLibPath, wikisHostName, wikisPort, WIKIS_DB_NAME, wikisUsername, EncryptionUtils.encrypt(Util.xor(wikisPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_FILES)) {
			setDBUserData(Constants.FEATURE_ID_FILES, dbType, jdbcLibPath, filesHostName, filesPort, FILES_DB_NAME, filesUsername, EncryptionUtils.encrypt(Util.xor(filesPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_FORUMS)) {
			setDBUserData(Constants.FEATURE_ID_FORUMS, dbType, jdbcLibPath, forumHostName, forumPort, FORUM_DB_NAME, forumUsername, EncryptionUtils.encrypt(Util.xor(forumPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_NEWS) || isFeatureSelected(Constants.FEATURE_ID_SEARCH) || isFeatureSelected(Constants.FEATURE_ID_HOMEPAGE)) {
			setDBUserData(Constants.FEATURE_ID_NEWS, dbType, jdbcLibPath, homepageHostName, homepagePort, HOMEPAGE_DB_NAME, homepageUsername, EncryptionUtils.encrypt(Util.xor(homepagePassword)));
			setDBUserData(Constants.FEATURE_ID_SEARCH, dbType, jdbcLibPath, homepageHostName, homepagePort, HOMEPAGE_DB_NAME, homepageUsername, EncryptionUtils.encrypt(Util.xor(homepagePassword)));
			setDBUserData(Constants.FEATURE_ID_HOMEPAGE, dbType, jdbcLibPath, homepageHostName, homepagePort, HOMEPAGE_DB_NAME, homepageUsername, EncryptionUtils.encrypt(Util.xor(homepagePassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_METRICS)) {
			setDBUserData(Constants.FEATURE_ID_METRICS, dbType, jdbcLibPath, metricsHostName, metricsPort, METRICS_DB_NAME, metricsUsername, EncryptionUtils.encrypt(Util.xor(metricsPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_MOBILE)) {
			setDBUserData(Constants.FEATURE_ID_MOBILE, dbType, jdbcLibPath, mobileHostName, mobilePort, MOBILE_DB_NAME, mobileUsername, EncryptionUtils.encrypt(Util.xor(mobilePassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_PUSHNOTIFICATION)) {
			setDBUserData(Constants.FEATURE_ID_PUSHNOTIFICATION, dbType, jdbcLibPath, pushnotificationHostName, pushnotificationPort, PUSHNOTIFICATION_DB_NAME, pushnotificationUsername, EncryptionUtils.encrypt(Util.xor(pushnotificationPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_ICEC)) {
			setDBUserData(Constants.FEATURE_ID_ICEC, dbType, jdbcLibPath, icecHostName, icecPort, ICEC_DB_NAME, icecUsername, EncryptionUtils.encrypt(Util.xor(icecPassword)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_IC360)) {
			setDBUserData(Constants.FEATURE_ID_IC360, dbType, jdbcLibPath, ic360HostName, ic360Port, IC360_DB_NAME, ic360Username, EncryptionUtils.encrypt(Util.xor(ic360Password)));
		}
		if (isFeatureSelected(Constants.FEATURE_ID_CCM)){
			if(this.profile.getUserData("user.ccm.existingDeployment").equals("false")){
				profile.setUserData("user.ccm.jdbcLibraryPath", jdbcLibPath);
				profile.setUserData("user.ccm.dbType", dbType);
				profile.setUserData("user.ccm.gcd.dbHostName", ccmGCDHostName);
				profile.setUserData("user.ccm.gcd.dbPort", ccmGCDPort);
				profile.setUserData("user.ccm.gcd.dbName", CCM_DB_NAME_GCD );
				profile.setUserData("user.ccm.gcd.dbUser", ccmGCDUsername);
				profile.setUserData("user.ccm.gcd.dbUserPassword", EncryptionUtils.encrypt(Util.xor(ccmGCDPassword)));
				profile.setUserData("user.ccm.objstore.dbHostName", ccmOSHostName);
				profile.setUserData("user.ccm.objstore.dbPort", ccmOSPort);
				profile.setUserData("user.ccm.objstore.dbName", CCM_DB_NAME_OBJECT_STORE );
				profile.setUserData("user.ccm.objstore.dbUser", ccmOSUsername);
				profile.setUserData("user.ccm.objstore.dbUserPassword", EncryptionUtils.encrypt(Util.xor(ccmOSPassword)));
			}else{
				profile.setUserData("user.ccm.gcd.dbPort", "None");
				profile.setUserData("user.ccm.objstore.dbPort", "None");
			}
		}
	}

	private void setDBUserData(String featureId, String dbType, String jdbcLibPath, String dbHostName, String dbPort, String dbName, String dbUser, String dbPassword) {
		log.info("Setting database info: "+ featureId);
		log.info("user."+ featureId +".dbType: "+ dbType);
		profile.setUserData("user."+ featureId +".dbType", dbType);
		log.info("user."+ featureId +".jdbcLibraryPath: "+ jdbcLibPath);
		profile.setUserData("user."+ featureId +".jdbcLibraryPath", jdbcLibPath);
		log.info("user."+ featureId +".dbHostName: "+ dbHostName);
		profile.setUserData("user."+ featureId +".dbHostName", dbHostName);
		log.info("user."+ featureId +".dbPort: "+ dbPort);
		profile.setUserData("user."+ featureId +".dbPort", dbPort);
		log.info("user."+ featureId +".dbName: "+ dbName);
		profile.setUserData("user."+ featureId +".dbName", dbName);
		log.info("user."+ featureId +".dbUser: "+ dbUser);
		profile.setUserData("user."+ featureId +".dbUser", dbUser);
		profile.setUserData("user."+ featureId +".dbUserPassword", dbPassword);
		if (dbType == "Oracle")
			profile.setUserData("user."+ featureId +".dbCustomProperties", "'dbCustomProperties' : {'URL': 'jdbc:oracle:thin:@" + dbHostName + ":" + dbPort + "/" + dbName + "',},");
		else
			profile.setUserData("user."+ featureId +".dbCustomProperties", "");
	}

	private void verifyComplete() {

	}

	private void verifyHostNameComplete(boolean isModifyTextEvent) {
		hostNameFlag = false;
		profile = getProfile();
		nextEnabled = false;
		setPageComplete(false);
		InstallValidator installvalidator = new InstallValidator();

		if (isFeatureSelected("activities")) {
			if (sameDBButton.getSelection() == true)
				activitiesHostName = hostnameText.getText().trim();
			else
				activitiesHostName = activitiesDBHostNameText2.getText().trim();

			try {
				if (activitiesHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(activitiesHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.warning("The hostname for Activities is not valid: "+ activitiesHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Activities is valid: " + activitiesHostName);
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDBButton.getSelection() == true)
				blogsHostName = hostnameText.getText().trim();
			else
				blogsHostName = blogsDBHostNameText2.getText().trim();
			try {
				if (blogsHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(blogsHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Blogs is not valid: "+ blogsHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Blogs is valid: " + blogsHostName);
			}
		}

		if (isFeatureSelected("communities")) {
			if (sameDBButton.getSelection() == true)
				communitiesHostName = hostnameText.getText().trim();
			else
				communitiesHostName = communitiesDBHostNameText2.getText().trim();
			try {
				if (communitiesHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(communitiesHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Communities is not valid: "+ communitiesHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Communities is not valid: "+ communitiesHostName);
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDBButton.getSelection() == true)
				dogearHostName = hostnameText.getText().trim();
			else
				dogearHostName = dogearDBHostNameText2.getText().trim();
			try {
				if (dogearHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(dogearHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Dogear is not valid: "+ dogearHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Dogear is valid: " + dogearHostName);
			}
		}

		if (isFeatureSelected("metrics")) {
			if (sameDBButton.getSelection() == true)
				metricsHostName = hostnameText.getText().trim();
			else
				metricsHostName = metricsDBHostNameText2.getText().trim();
			try {
				if (metricsHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(metricsHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Metrics is not valid: "+ metricsHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for metrics is valid: " + metricsHostName);
			}
		}

		if (isFeatureSelected("mobile")) {
			if (sameDBButton.getSelection() == true)
				mobileHostName = hostnameText.getText().trim();
			else
				mobileHostName = mobileDBHostNameText2.getText().trim();
			try {
				if (mobileHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(mobileHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Mobile is not valid: "+ mobileHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for mobile is valid: " + mobileHostName);
			}
		}

		if (isFeatureSelected("profiles")) {
			if (sameDBButton.getSelection() == true)
				profilesHostName = hostnameText.getText().trim();
			else
				profilesHostName = profilesDBHostNameText2.getText().trim();
			try {
				if (profilesHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(profilesHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Profiles is not valid: "+ profilesHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Profiles is valid: " + profilesHostName);
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDBButton.getSelection() == true)
				wikisHostName = hostnameText.getText().trim();
			else
				wikisHostName = wikisDBHostNameText2.getText().trim();
			try {
				if (wikisHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(wikisHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Wikis is not valid: "+ wikisHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Wikis is valid: " + wikisHostName);
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDBButton.getSelection() == true)
				filesHostName = hostnameText.getText().trim();
			else
				filesHostName = filesDBHostNameText2.getText().trim();
			try {
				if (filesHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(filesHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Files is not valid: "+ filesHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Files is valid: " + filesHostName);
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDBButton.getSelection() == true)
				forumHostName = hostnameText.getText().trim();
			else
				forumHostName = forumDBHostNameText2.getText().trim();
			try {
				if (forumHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(forumHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Forum is not valid: "+ forumHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Forum is valid: " + forumHostName);
			}
		}
		if (isFeatureSelected("pushNotification")) {
			if (sameDBButton.getSelection() == true)
				pushnotificationHostName = hostnameText.getText().trim();
			else
				pushnotificationHostName = pushnotificationDBHostNameText2.getText().trim();
			try {
				if (pushnotificationHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(pushnotificationHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for pushnotification is not valid: "+ pushnotificationHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for pushnotification is valid: " + pushnotificationHostName);
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDBButton.getSelection() == true)
				icecHostName = hostnameText.getText().trim();
			else
				icecHostName = icecDBHostNameText2.getText().trim();
			try {
				if (icecHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(icecHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Community Highlights is not valid: "+ icecHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Community Highlights is not valid: "+ icecHostName);
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDBButton.getSelection() == true)
				ic360HostName = hostnameText.getText().trim();
			else
				ic360HostName = ic360DBHostNameText2.getText().trim();
			try {
				if (ic360HostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(ic360HostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for IC360 is not valid: "+ ic360HostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for IC360 is valid: " + ic360HostName);
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search") || isFeatureSelected("news")) {
			if (sameDBButton.getSelection() == true)
				homepageHostName = hostnameText.getText().trim();
			else
				homepageHostName = homepageDBHostNameText2.getText().trim();
			try {
				if (homepageHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(homepageHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for Homepage is not valid: "+ homepageHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			if (!isModifyTextEvent) {
				log.info("The hostname for Homepage is valid: " + homepageHostName);
			}
		}

		if (isFeatureSelected("ccm") && this.profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDBButton.getSelection() == true){
				ccmGCDHostName = hostnameText.getText().trim();
				ccmOSHostName = hostnameText.getText().trim();
			}else{
				ccmGCDHostName = ccmGCDDBHostNameText2.getText().trim();
				ccmOSHostName = ccmOSDBHostNameText2.getText().trim();
			}
			try {
				if (ccmGCDHostName.length() == 0 || ccmOSHostName.length() == 0) {
					setErrorMessage(Messages.warning_host_empty);
					setPageComplete(false);
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(ccmGCDHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for CCM GCD is not valid: "+ ccmGCDHostName);
					}
					return;
				}
				if (!installvalidator.hostNameValidateForWasPanel(ccmOSHostName)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					if (!isModifyTextEvent) {
						log.info("The hostname for CCM Object Store is not valid: "+ ccmOSHostName);
					}
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			log.info("The hostname for CCM GCD is valid: " + ccmGCDHostName);
			log.info("The hostname for CCM Object Store is valid: " + ccmOSHostName);
		}
		hostNameFlag = true;
		if (!isModifyTextEvent) {
			log.info("The hostname validation for all features:  PASS");
		}
		setErrorMessage(null);
	}

	private void verifyPortComplete() {
		portFlag = false;
		profile = getProfile();
		nextEnabled = false;
		setPageComplete(false);

		InstallValidator installvalidator = new InstallValidator();
		if (isFeatureSelected("activities")) {
			if (sameDBButton.getSelection() == true)
				activitiesPort = portText.getText().trim();
			else
				activitiesPort = activitiesDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(activitiesPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Activities is not valid: "
							+ activitiesPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Activities is valid: " + activitiesPort);
		if (isFeatureSelected("blogs")) {
			if (sameDBButton.getSelection() == true)
				blogsPort = portText.getText().trim();
			else
				blogsPort = blogsDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(blogsPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Blogs is not valid: " + blogsPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Blogs is valid: " + blogsPort);
		if (isFeatureSelected("communities")) {
			if (sameDBButton.getSelection() == true)
				communitiesPort = portText.getText().trim();
			else
				communitiesPort = communitiesDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(communitiesPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Communities is not valid: "
							+ communitiesPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Communities is valid: " + communitiesPort);
		if (isFeatureSelected("dogear")) {
			if (sameDBButton.getSelection() == true)
				dogearPort = portText.getText().trim();
			else
				dogearPort = dogearDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(dogearPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Dogear is not valid: " + dogearPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Dogear is valid: " + dogearPort);
		if (isFeatureSelected("metrics")) {
			if (sameDBButton.getSelection() == true)
				metricsPort = portText.getText().trim();
			else
				metricsPort = metricsDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(metricsPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Metrics is not valid: "
							+ metricsPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for metrics is valid: " + metricsPort);

		if (isFeatureSelected("mobile")) {
			if (sameDBButton.getSelection() == true)
				mobilePort = portText.getText().trim();
			else
				mobilePort = mobileDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(mobilePort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Mobile is not valid: " + mobilePort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for mobile is valid: " + mobilePort);

		if (isFeatureSelected("profiles")) {
			if (sameDBButton.getSelection() == true)
				profilesPort = portText.getText().trim();
			else
				profilesPort = profilesDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(profilesPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Profiles is not valid: "
							+ profilesPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Profiles is valid: " + profilesPort);
		if (isFeatureSelected("wikis")) {
			if (sameDBButton.getSelection() == true)
				wikisPort = portText.getText().trim();
			else
				wikisPort = wikisDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(wikisPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Wikis is not valid: " + wikisPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Wikis is valid: " + wikisPort);
		if (isFeatureSelected("files")) {
			if (sameDBButton.getSelection() == true)
				filesPort = portText.getText().trim();
			else
				filesPort = filesDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(filesPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Files is not valid: " + filesPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Files is valid: " + filesPort);
		if (isFeatureSelected("forums")) {
			if (sameDBButton.getSelection() == true)
				forumPort = portText.getText().trim();
			else
				forumPort = forumDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(forumPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Forum is not valid: " + forumPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Forum is valid: " + forumPort);
		if (isFeatureSelected("pushNotification")) {
			if (sameDBButton.getSelection() == true)
				pushnotificationPort = portText.getText().trim();
			else
				pushnotificationPort = pushnotificationDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(pushnotificationPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for pushnotification is not valid: " + pushnotificationPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for pushnotification is valid: " + forumPort);
		if (isFeatureSelected("icec")) {
			if (sameDBButton.getSelection() == true)
				icecPort = portText.getText().trim();
			else
				icecPort = icecDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(icecPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Community Highlights is not valid: " + icecPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("ic360")) {
			if (sameDBButton.getSelection() == true)
				ic360Port = portText.getText().trim();
			else
				ic360Port = ic360DBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(ic360Port)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for ic360 is not valid: " + ic360Port);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for ic360 is valid: " + ic360Port);
		log.info("The port for Community Highlights is valid: " + icecPort);
		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDBButton.getSelection() == true)
				homepagePort = portText.getText().trim();
			else
				homepagePort = homepageDBPortText2.getText().trim();
			try {
				if (!installvalidator.portNumValidate(homepagePort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for Homepage is not valid: "
							+ homepagePort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for Homepage is valid: " + homepagePort);
		if (isFeatureSelected("ccm") && this.profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDBButton.getSelection() == true){
				ccmGCDPort = portText.getText().trim();
				ccmOSPort = portText.getText().trim();
			}else{
				ccmGCDPort = ccmGCDDBPortText2.getText().trim();
				ccmOSPort = ccmOSDBPortText2.getText().trim();
			}
			try {
				if (!installvalidator.portNumValidate(ccmGCDPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for CCM GCD is not valid: " + ccmGCDPort);
					return;
				}
				if (!installvalidator.portNumValidate(ccmOSPort)) {
					setErrorMessage(installvalidator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The port for CCM Object Store is not valid: " + ccmOSPort);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		log.info("The port for CCM GCD is valid: " + ccmGCDPort);
		log.info("The port for CCM Object Store is valid: " + ccmOSPort);
		portFlag = true;
		log.info("The ports validation for all features:  PASS");
		setErrorMessage(null);
	}

	private void verifyJDBCDriverPathComplete() {
		jdbcDriverFlag = false;
		nextEnabled = false;
		setPageComplete(false);
		DbInstallValidator validator = new DbInstallValidator();
		if (sameDBButton.getSelection() == true)
			jdbcDriver = jdbcDriverText1.getText().trim();
		else
			jdbcDriver = jdbcDriverText2.getText().trim();

		if (!isRecordingMode) {
			try {
				if (!validator.JDBCLibraryFolderValidate(jdbcDriver,
						getSelectedDBType())) {
					setErrorMessage(validator.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					log.info("The JDBC Driver path for " + getSelectedDBType()
							+ " is NOT valid: " + jdbcDriver);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		jdbcDriverFlag = true;
		log.info("The JDBC Driver path for " + getSelectedDBType()
				+ " is valid: " + jdbcDriver);
		setErrorMessage(null);
	}

	private void verifyUserNameComplete() {
		userNameFlag = false;
		nextEnabled = false;
		setPageComplete(false);
		InstallValidator installvalidator = new InstallValidator();

		if (isFeatureSelected("activities")) {
			if (sameDBButton.getSelection() == true)
				activitiesUsername = activitiesUseridText1.getText().trim();
			else
				activitiesUsername = activitiesUseridText2.getText().trim();

			if (activitiesUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(activitiesUsername)) {

				setErrorMessage(Messages
						.bind(Messages.DB_USERNAME_CHARS_INVALID,
								Messages.ACTIVITIES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDBButton.getSelection() == true)
				blogsUsername = blogsUseridText1.getText().trim();
			else
				blogsUsername = blogsUseridText2.getText().trim();

			if (blogsUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(blogsUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.BLOGS));
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("communities")) {
			if (sameDBButton.getSelection() == true)
				communitiesUsername = communitiesUseridText1.getText().trim();
			else
				communitiesUsername = communitiesUseridText2.getText().trim();
			if (communitiesUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(communitiesUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID,
						Messages.COMMUNITIES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDBButton.getSelection() == true)
				dogearUsername = dogearUseridText1.getText().trim();
			else
				dogearUsername = dogearUseridText2.getText().trim();
			if (dogearUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(dogearUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.DOGEAR));
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("metrics")) {
			if (sameDBButton.getSelection() == true)
				metricsUsername = metricsUseridText1.getText().trim();
			else
				metricsUsername = metricsUseridText2.getText().trim();

			if (metricsUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(metricsUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.METRICS));
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("mobile")) {
			if (sameDBButton.getSelection() == true)
				mobileUsername = mobileUseridText1.getText().trim();
			else
				mobileUsername = mobileUseridText2.getText().trim();

			if (mobileUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(mobileUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.MOBILE));
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("profiles")) {
			if (sameDBButton.getSelection() == true)
				profilesUsername = profilesUseridText1.getText().trim();
			else
				profilesUsername = profilesUseridText2.getText().trim();

			if (profilesUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(profilesUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.PROFILES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDBButton.getSelection() == true)
				wikisUsername = wikisUseridText1.getText().trim();
			else
				wikisUsername = wikisUseridText2.getText().trim();

			if (wikisUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(wikisUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.WIKIS));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDBButton.getSelection() == true)
				filesUsername = filesUseridText1.getText().trim();
			else
				filesUsername = filesUseridText2.getText().trim();

			if (filesUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(filesUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.FILES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDBButton.getSelection() == true)
				forumUsername = forumUseridText1.getText().trim();
			else
				forumUsername = forumUseridText2.getText().trim();

			if (forumUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(forumUsername)) {

				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.FORUMS));

				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDBButton.getSelection() == true)
				pushnotificationUsername = pushnotificationUseridText1.getText().trim();
			else
				pushnotificationUsername = pushnotificationUseridText2.getText().trim();

			if (pushnotificationUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(pushnotificationUsername)) {

				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.PUSH_NOTIFICATION));

				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDBButton.getSelection() == true)
				icecUsername = icecUseridText1.getText().trim();
			else
				icecUsername = icecUseridText2.getText().trim();

			if (icecUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(icecUsername)) {

				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.ICEC));

				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDBButton.getSelection() == true)
				ic360Username = ic360UseridText1.getText().trim();
			else
				ic360Username = ic360UseridText2.getText().trim();

			if (ic360Username.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(ic360Username)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.IC360));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDBButton.getSelection() == true)
				homepageUsername = homepageUseridText1.getText().trim();
			else
				homepageUsername = homepageUseridText2.getText().trim();

			if (homepageUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(homepageUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.HOMEPAGE));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("ccm") && this.profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDBButton.getSelection() == true){
				ccmGCDUsername = ccmGCDUseridText1.getText().trim();
				ccmOSUsername = ccmOSUseridText1.getText().trim();
			}else{
				ccmGCDUsername = ccmGCDUseridText2.getText().trim();
				ccmOSUsername = ccmOSUseridText2.getText().trim();
			}
			if (ccmGCDUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(ccmGCDUsername)) {
				setErrorMessage(Messages.bind(
						Messages.DB_USERNAME_CHARS_INVALID, Messages.GCD));

				setPageComplete(false);
				return;
			}
			if (ccmOSUsername.length() == 0) {
				setErrorMessage(Messages.warning_user_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(ccmOSUsername)) {
				setErrorMessage(Messages.bind(
							Messages.DB_USERNAME_CHARS_INVALID, Messages.OBJECTSTORE));

				setPageComplete(false);
				return;
			}
		}

		userNameFlag = true;

		// this.profile.setUserData(ADMIN_NAME, cn);
		setErrorMessage(null); // in case there had been one
		if (isPageComplete() == false) {
			setPageComplete(true);

		}
	}

	private void verifyDatabaseNameComplete() {
		databaseNameFlag = false;
		nextEnabled = false;
		setPageComplete(false);

		if (isFeatureSelected("activities")) {
			if (sameDBButton.getSelection() == true)
				activitiesDatabaseName = activitiesDBNameText1.getText().trim();
			else
				activitiesDatabaseName = activitiesDBNameText2.getText().trim();

			if (activitiesDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDBButton.getSelection() == true)
				blogsDatabaseName = blogsDBNameText1.getText().trim();
			else
				blogsDatabaseName = blogsDBNameText2.getText().trim();

			if (blogsDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("communities")) {
			if (sameDBButton.getSelection() == true)
				communitiesDatabaseName = communitiesDBNameText1.getText().trim();
			else
				communitiesDatabaseName = communitiesDBNameText2.getText().trim();
			if (communitiesDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDBButton.getSelection() == true)
				dogearDatabaseName = dogearDBNameText1.getText().trim();
			else
				dogearDatabaseName = dogearDBNameText2.getText().trim();
			if (dogearDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("metrics")) {
			if (sameDBButton.getSelection() == true)
				metricsDatabaseName = metricsDBNameText1.getText().trim();
			else
				metricsDatabaseName = metricsDBNameText2.getText().trim();

			if (metricsDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("mobile")) {
			if (sameDBButton.getSelection() == true)
				mobileDatabaseName = mobileDBNameText1.getText().trim();
			else
				mobileDatabaseName = mobileDBNameText2.getText().trim();

			if (mobileDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("profiles")) {
			if (sameDBButton.getSelection() == true)
				profilesDatabaseName = profilesDBNameText1.getText().trim();
			else
				profilesDatabaseName = profilesDBNameText2.getText().trim();

			if (profilesDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDBButton.getSelection() == true)
				wikisDatabaseName = wikisDBNameText1.getText().trim();
			else
				wikisDatabaseName = wikisDBNameText2.getText().trim();

			if (wikisDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDBButton.getSelection() == true)
				filesDatabaseName = filesDBNameText1.getText().trim();
			else
				filesDatabaseName = filesDBNameText2.getText().trim();

			if (filesDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDBButton.getSelection() == true)
				forumDatabaseName = forumDBNameText1.getText().trim();
			else
				forumDatabaseName = forumDBNameText2.getText().trim();

			if (forumDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDBButton.getSelection() == true)
				pushnotificationDatabaseName = pushnotificationDBNameText1.getText().trim();
			else
				pushnotificationDatabaseName = pushnotificationDBNameText2.getText().trim();

			if (pushnotificationDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDBButton.getSelection() == true)
				icecDatabaseName = icecDBNameText1.getText().trim();
			else
				icecDatabaseName = icecDBNameText2.getText().trim();

			if (icecDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDBButton.getSelection() == true)
				ic360DatabaseName = ic360DBNameText1.getText().trim();
			else
				ic360DatabaseName = ic360DBNameText2.getText().trim();

			if (ic360DatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDBButton.getSelection() == true)
				homepageDatabaseName = homepageDBNameText1.getText().trim();
			else
				homepageDatabaseName = homepageDBNameText2.getText().trim();

			if (homepageDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("ccm") && this.profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDBButton.getSelection() == true){
				ccmGCDDatabaseName = ccmGCDDBNameText1.getText().trim();
				ccmOSDatabaseName = ccmOSDBNameText1.getText().trim();
			}else{
				ccmGCDDatabaseName = ccmGCDDBNameText2.getText().trim();
				ccmOSDatabaseName = ccmOSDBNameText2.getText().trim();
			}
			if (ccmGCDDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
			if (ccmOSDatabaseName.length() == 0) {
				setErrorMessage(Messages.warning_database_name_empty);
				setPageComplete(false);
				return;
			}
		}

		databaseNameFlag = true;

		// this.profile.setUserData(ADMIN_NAME, cn);
		setErrorMessage(null); // in case there had been one
		if (isPageComplete() == false) {
			setPageComplete(true);

		}
	}

	private void verifyPasswordComplete() {
		passwordFlag = false;
		nextEnabled = false;
		setPageComplete(false);
		InstallValidator installvalidator = new InstallValidator();

		if (isFeatureSelected("activities")) {
			if (sameDBButton.getSelection() == true)
				activitiesPassword = activitiesPasswordText1.getText().trim();
			else
				activitiesPassword = activitiesPasswordText2.getText().trim();

			if (activitiesPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(activitiesPassword)||installvalidator.containsSpace(activitiesPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.ACTIVITIES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDBButton.getSelection() == true)
				blogsPassword = blogsPasswordText1.getText().trim();
			else
				blogsPassword = blogsPasswordText2.getText().trim();
			if (blogsPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(blogsPassword)||installvalidator.containsSpace(blogsPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.BLOGS));
				setPageComplete(false);
				return;
			}
		}
		if (isFeatureSelected("communities")) {
			if (sameDBButton.getSelection() == true)
				communitiesPassword = communitiesPasswordText1.getText().trim();
			else
				communitiesPassword = communitiesPasswordText2.getText().trim();

			if (communitiesPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(communitiesPassword)||installvalidator.containsSpace(communitiesPassword)) {

				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.COMMUNITIES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDBButton.getSelection() == true)
				dogearPassword = dogearPasswordText1.getText().trim();
			else
				dogearPassword = dogearPasswordText2.getText().trim();
			if (dogearPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(dogearPassword)||installvalidator.containsSpace(dogearPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.DOGEAR));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("metrics")) {
			if (sameDBButton.getSelection() == true)
				metricsPassword = metricsPasswordText1.getText().trim();
			else
				metricsPassword = metricsPasswordText2.getText().trim();
			if (metricsPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(metricsPassword)||installvalidator.containsSpace(metricsPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.METRICS));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("mobile")) {
			if (sameDBButton.getSelection() == true)
				mobilePassword = mobilePasswordText1.getText().trim();
			else
				mobilePassword = mobilePasswordText2.getText().trim();
			if (mobilePassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(mobilePassword)||installvalidator.containsSpace(mobilePassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.MOBILE));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("profiles")) {
			if (sameDBButton.getSelection() == true)
				profilesPassword = profilesPasswordText1.getText().trim();
			else
				profilesPassword = profilesPasswordText2.getText().trim();
			if (profilesPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(profilesPassword)||installvalidator.containsSpace(profilesPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.PROFILES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDBButton.getSelection() == true)
				wikisPassword = wikisPasswordText1.getText().trim();
			else
				wikisPassword = wikisPasswordText2.getText().trim();
			if (wikisPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(wikisPassword)||installvalidator.containsSpace(wikisPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.WIKIS));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDBButton.getSelection() == true)
				filesPassword = filesPasswordText1.getText().trim();
			else
				filesPassword = filesPasswordText2.getText().trim();

			if (filesPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(filesPassword)||installvalidator.containsSpace(filesPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.FILES));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDBButton.getSelection() == true)
				forumPassword = forumPasswordText1.getText().trim();
			else
				forumPassword = forumPasswordText2.getText().trim();

			if (forumPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(forumPassword)||installvalidator.containsSpace(forumPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.FORUMS));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDBButton.getSelection() == true)
				pushnotificationPassword = pushnotificationPasswordText1.getText().trim();
			else
				pushnotificationPassword = pushnotificationPasswordText2.getText().trim();

			if (pushnotificationPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(pushnotificationPassword)||installvalidator.containsSpace(pushnotificationPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.PUSH_NOTIFICATION));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDBButton.getSelection() == true)
				icecPassword = icecPasswordText1.getText().trim();
			else
				icecPassword = icecPasswordText2.getText().trim();

			if (icecPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(icecPassword)||installvalidator.containsSpace(icecPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.ICEC));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDBButton.getSelection() == true)
				ic360Password = ic360PasswordText1.getText().trim();
			else
				ic360Password = ic360PasswordText2.getText().trim();
			if (ic360Password.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(ic360Password)||installvalidator.containsSpace(ic360Password)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.IC360));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDBButton.getSelection() == true)
				homepagePassword = homepagePasswordText1.getText().trim();
			else
				homepagePassword = homepagePasswordText2.getText().trim();
			if (homepagePassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator
					.containsInvalidPassword(homepagePassword)||installvalidator.containsSpace(homepagePassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.HOMEPAGE));
				setPageComplete(false);
				return;
			}
		}

		if (isFeatureSelected("ccm") && this.profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDBButton.getSelection() == true){
				ccmGCDPassword = ccmGCDPasswordText1.getText().trim();
				ccmOSPassword = ccmOSPasswordText1.getText().trim();
			}else{
				ccmGCDPassword = ccmGCDPasswordText2.getText().trim();
				ccmOSPassword = ccmOSPasswordText2.getText().trim();
			}
			if (ccmGCDPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(ccmGCDPassword)||installvalidator.containsSpace(ccmGCDPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.GCD));
				setPageComplete(false);
				return;
			}
			if (ccmOSPassword.length() == 0) {
				setErrorMessage(Messages.warning_password_empty);
				setPageComplete(false);
				return;
			} else if (installvalidator.containsInvalidPassword(ccmOSPassword)||installvalidator.containsSpace(ccmOSPassword)) {
				setErrorMessage(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.OBJECTSTORE));
				setPageComplete(false);
				return;
			}
		}

		passwordFlag = true;

		// this.profile.setUserData(ADMIN_NAME, cn);
		setErrorMessage(null); // in case there had been one
		if (isPageComplete() == false) {
			setPageComplete(true);

		}
	}

	class DBProgressMonitor implements IRunnableWithProgress {
		private String hostname;
		private String port;
		private String dbtype;
		private boolean sameDBFlag;
		private int dbType;
		private String propertiesFile = "";
		DbInstallValidator dv = new DbInstallValidator();
		StringBuffer sb = new StringBuffer();
		public static final String UI_LINE_SEPARATOR = "\n";

		public DBProgressMonitor(String hostname, String port, String dbtype,
				boolean sameDBFlag, int dbType) {
			this.hostname = hostname;
			this.port = port;
			this.dbtype = dbtype;
			this.sameDBFlag = sameDBFlag;
			this.dbType = dbType;
		}

		public String getErrorMsg() {
			return sb.toString();
		}

		private String getPropertiesFile() {
			if (propertiesFile.equals("")) {
				Random rd = new Random();
				String rdDirPath = System.getProperty("java.io.tmpdir")
						+ File.separator + String.valueOf(rd.nextInt(999999));
				propertiesFile = rdDirPath + File.separator
						+ "dbValidation.txt";
				return propertiesFile;
			}
			return propertiesFile;
		}

		private void checkDB(String dbName, String dbUserName, String dbUserPwd, String dbHostName, String dbPort, String msg, String appName) {
			int result = -1;
			dv.dbConnection(getProfile(), dbtype, dbHostName.trim(), dbPort.trim(), dbName, dbUserName, dbUserPwd, getPropertiesFile(), jdbcDriver, appName);
			result = dv.loadResult();
			if (result != 0) {
				if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW)){
					sb.append(getUnknowDBMessage(getSelectedDBType(), dbName, dbUserName, dbUserPwd, dbHostName, dbPort));
				}else{
					sb.append(Messages.bind(dv.getMessage(), msg) + UI_LINE_SEPARATOR);
				}
			} else {
				log.info(dbName+ " database connection validation: PASS");
			}
		}

		public void run(IProgressMonitor monitor) {
			monitor.beginTask(Messages.DB_START_VALID, 14);
			log.info("**************** Get Into Run **************");

			if (isFeatureSelected("activities")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, ACTIVITIES_DB_NAME));
				checkDB(ACTIVITIES_DB_NAME, activitiesUsername, activitiesPassword,
					sameDBFlag == true ? hostname : activitiesHostName,
					sameDBFlag == true ? port : activitiesPort,
					Messages.ACTIVITIES,
					"activities");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("blogs")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, BLOGS_DB_NAME));
				checkDB(BLOGS_DB_NAME, blogsUsername, blogsPassword,
					sameDBFlag == true ? hostname : blogsHostName,
					sameDBFlag == true ? port : blogsPort,
					Messages.BLOGS,
					"blogs");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("communities")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, COMMUNITIES_DB_NAME));
				checkDB(COMMUNITIES_DB_NAME, communitiesUsername, communitiesPassword,
					sameDBFlag == true ? hostname : communitiesHostName,
					sameDBFlag == true ? port : communitiesPort,
					Messages.COMMUNITIES,
					"communities");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("dogear")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, DOGEAR_DB_NAME));
				checkDB(DOGEAR_DB_NAME, dogearUsername, dogearPassword,
					sameDBFlag == true ? hostname : dogearHostName,
					sameDBFlag == true ? port : dogearPort,
					Messages.DOGEAR,
					"dogear");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("metrics")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, METRICS_DB_NAME));
				checkDB(METRICS_DB_NAME, metricsUsername, metricsPassword,
					sameDBFlag == true ? hostname : metricsHostName,
					sameDBFlag == true ? port : metricsPort,
					Messages.METRICS,
					"metrics");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("mobile")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, MOBILE_DB_NAME));
				checkDB(MOBILE_DB_NAME, mobileUsername, mobilePassword,
					sameDBFlag == true ? hostname : mobileHostName,
					sameDBFlag == true ? port : mobilePort,
					Messages.MOBILE,
					"mobile");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("profiles")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, PEOPLEDB_DB_NAME));
				checkDB(PEOPLEDB_DB_NAME, profilesUsername, profilesPassword,
					sameDBFlag == true ? hostname : profilesHostName,
					sameDBFlag == true ? port : profilesPort,
					Messages.PROFILES,
					"profiles");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("wikis")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, WIKIS_DB_NAME));
				checkDB(WIKIS_DB_NAME, wikisUsername, wikisPassword,
					sameDBFlag == true ? hostname : wikisHostName,
					sameDBFlag == true ? port : wikisPort,
					Messages.WIKIS,
					"wikis");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("files")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, FILES_DB_NAME));
				checkDB(FILES_DB_NAME, filesUsername, filesPassword,
					sameDBFlag == true ? hostname : filesHostName,
					sameDBFlag == true ? port : filesPort,
					Messages.FILES,
					"files");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("forums")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, FORUM_DB_NAME));
				checkDB(FORUM_DB_NAME, forumUsername, forumPassword,
					sameDBFlag == true ? hostname : forumHostName,
					sameDBFlag == true ? port : forumPort,
					Messages.FORUMS,
					"forums");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("pushNotification")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, PUSHNOTIFICATION_DB_NAME));
				checkDB(PUSHNOTIFICATION_DB_NAME, pushnotificationUsername, pushnotificationPassword,
						sameDBFlag == true ? hostname : pushnotificationHostName,
								sameDBFlag == true ? port : pushnotificationPort,
										Messages.PUSH_NOTIFICATION,
						"pushnotification");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("icec")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, ICEC_DB_NAME));
				String icecdb = ICEC_DB_NAME + icecUsername + icecPassword + icecHostName + icecPort;
				checkDB(ICEC_DB_NAME, icecUsername, icecPassword,
					sameDBFlag == true ? hostname : icecHostName,
					sameDBFlag == true ? port : icecPort,
					Messages.ICEC,
					"icec");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("ic360")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, IC360_DB_NAME));
				checkDB(IC360_DB_NAME, ic360Username, ic360Password,
					sameDBFlag == true ? hostname : ic360HostName,
					sameDBFlag == true ? port : ic360Port,
					Messages.IC360,
					"ic360");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("homepage") || isFeatureSelected("search") || isFeatureSelected("news")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, HOMEPAGE_DB_NAME));
				checkDB(HOMEPAGE_DB_NAME, homepageUsername, homepagePassword,
					sameDBFlag == true ? hostname : homepageHostName,
					sameDBFlag == true ? port : homepagePort,
					Messages.HOMEPAGE,
					"homepage");
			}
			monitor.worked(1);
			if (monitor.isCanceled()) return;

			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				monitor.setTaskName(Messages.bind(Messages.DB_VALID, CCM_DB_NAME_GCD));
				checkDB(CCM_DB_NAME_GCD, ccmGCDUsername, ccmGCDPassword,
					sameDBFlag == true ? hostname : ccmGCDHostName,
					sameDBFlag == true ? port : ccmGCDPort,
					Messages.GCD,
					"ccm");

				if (monitor.isCanceled()) return;

				monitor.setTaskName(Messages.bind(Messages.DB_VALID, CCM_DB_NAME_OBJECT_STORE));
				checkDB(CCM_DB_NAME_OBJECT_STORE, ccmOSUsername, ccmOSPassword,
					sameDBFlag == true ? hostname : ccmOSHostName,
					sameDBFlag == true ? port : ccmOSPort,
					Messages.OBJECTSTORE,
					"ccm");
			}
			monitor.worked(2);

			monitor.done();
		}
	};

	private void dbConnection() {
		try {
			if (sameDBButton.getSelection() == true) {
				if (activitiesDBNameText1 != null)
					ACTIVITIES_DB_NAME = activitiesDBNameText1.getText().trim();
				if (blogsDBNameText1 != null)
					BLOGS_DB_NAME = blogsDBNameText1.getText().trim();
				if (communitiesDBNameText1 != null)
					COMMUNITIES_DB_NAME = communitiesDBNameText1.getText().trim();
				if (dogearDBNameText1 != null)
					DOGEAR_DB_NAME = dogearDBNameText1.getText().trim();
				if (metricsDBNameText1 != null)
					METRICS_DB_NAME = metricsDBNameText1.getText().trim();
				if (mobileDBNameText1 != null)
					MOBILE_DB_NAME = mobileDBNameText1.getText().trim();
				if (filesDBNameText1 != null)
					FILES_DB_NAME = filesDBNameText1.getText().trim();
				if (forumDBNameText1 != null)
					FORUM_DB_NAME = forumDBNameText1.getText().trim();
				if (homepageDBNameText1 != null)
					HOMEPAGE_DB_NAME = homepageDBNameText1.getText().trim();
				if (profilesDBNameText1 != null)
					PEOPLEDB_DB_NAME = profilesDBNameText1.getText().trim();
				if (wikisDBNameText1 != null)
					WIKIS_DB_NAME = wikisDBNameText1.getText().trim();
				if (pushnotificationDBNameText1 != null)
					PUSHNOTIFICATION_DB_NAME = pushnotificationDBNameText1.getText().trim();
				if (icecDBNameText1 != null)
					ICEC_DB_NAME = icecDBNameText1.getText().trim();
				if (ic360DBNameText1 != null)
					IC360_DB_NAME = ic360DBNameText1.getText().trim();
				if (ccmGCDDBNameText1 != null && ccmGCDDBNameText1.isVisible()){
					CCM_DB_NAME_GCD = ccmGCDDBNameText1.getText().trim();
					CCM_DB_NAME_OBJECT_STORE = ccmOSDBNameText1.getText().trim();
				}
			}
			if (diffDBButton.getSelection() == true) {
				if (activitiesDBNameText2 != null)
					ACTIVITIES_DB_NAME = activitiesDBNameText2.getText().trim();
				if (blogsDBNameText2 != null)
					BLOGS_DB_NAME = blogsDBNameText2.getText().trim();
				if (communitiesDBNameText2 != null)
					COMMUNITIES_DB_NAME = communitiesDBNameText2.getText()
							.trim();
				if (dogearDBNameText2 != null)
					DOGEAR_DB_NAME = dogearDBNameText2.getText().trim();
				if (metricsDBNameText2 != null)
					METRICS_DB_NAME = metricsDBNameText2.getText().trim();
				if (mobileDBNameText2 != null)
					MOBILE_DB_NAME = mobileDBNameText2.getText().trim();
				if (filesDBNameText2 != null)
					FILES_DB_NAME = filesDBNameText2.getText().trim();
				if (forumDBNameText2 != null)
					FORUM_DB_NAME = forumDBNameText2.getText().trim();
				if (homepageDBNameText2 != null)
					HOMEPAGE_DB_NAME = homepageDBNameText2.getText().trim();
				if (profilesDBNameText2 != null)
					PEOPLEDB_DB_NAME = profilesDBNameText2.getText().trim();
				if (wikisDBNameText2 != null)
					WIKIS_DB_NAME = wikisDBNameText2.getText().trim();
				if (pushnotificationDBNameText2 != null)
					PUSHNOTIFICATION_DB_NAME = pushnotificationDBNameText2.getText().trim();
				if (icecDBNameText2 != null)
					ICEC_DB_NAME = icecDBNameText2.getText().trim();
				if (ic360DBNameText2 != null)
					IC360_DB_NAME = ic360DBNameText2.getText().trim();
				if (ccmGCDDBNameText2 != null && ccmGCDDBNameText2.isVisible()){
					CCM_DB_NAME_GCD = ccmGCDDBNameText2.getText().trim();
					CCM_DB_NAME_OBJECT_STORE = ccmOSDBNameText2.getText().trim();
				}
			}
			DBProgressMonitor dialog = new DBProgressMonitor(hostnameText.getText().trim(), portText.getText().trim(),
					getSelectedDBType(), sameDBButton.getSelection(), cboServer.getSelectionIndex());
			new ProgressMonitorDialog(parent.getShell()).run(true, true, dialog);
			String msg = dialog.getErrorMsg();
			if (msg.length() > 0 && (msg.indexOf(Messages.GCD)!= -1 || msg.indexOf(Messages.OBJECTSTORE)!= -1)) {
				dbconnection = false;
				setErrorMessage(msg + Messages.SET_CORRECT_VALUE_CCM); // in case there had been one
				nextEnabled=false;
				setPageComplete(false);
				log.error("Database Connection Fail: " + msg + "CCM database information must be set correctly otherwise installation would fail.");
			}else if(msg.length() > 0){
				dbconnection = false;
				setErrorMessage(msg); // in case there had been one
				nextEnabled=false;
				setPageComplete(false);
				log.error("Database Connection Fail: " + msg);
			} else {
				dbconnection = true;
				nextEnabled=true;
				setPageComplete(true);
				log.info("All Database connection validation: PASS");
			}
			// else
			// setErrorMessage("no error"); // in case there had been one

		} catch (Exception e) {
			log.error(e);
			// e.printStackTrace();
		}
	}

	private String getSelectedDBType() {
		int type = cboServer.getSelectionIndex();
		switch (type) {
		case 2:
			return DatabaseUtil.DBMS_ORACLE;
		case 3:
			return DatabaseUtil.DBMS_SQLSERVER;
		case 1:
			return DatabaseUtil.DBMS_DB2;
		default:
			return "";
		}
	}

	private int getSelectedDBIndex(String dbType) {
		int index = 0;
		if(DatabaseUtil.DBMS_ORACLE.equals(dbType)){
			index = 2;
		}else if(DatabaseUtil.DBMS_SQLSERVER.equals(dbType)){
			index = 3;
		}else if(DatabaseUtil.DBMS_DB2.equals(dbType)){
			index = 1;
		}
		return index;
	}

	private String getUnknowDBMessage(String dbType, String dbName, String dbUserName, String dbUserPwd, String dbHostName, String dbPort){
		String url = "";
		if(DatabaseUtil.DBMS_ORACLE.equals(dbType)){
			url = "jdbc:oracle:thin:@"+dbHostName+":"+dbPort+":"+dbName;
		}else if(DatabaseUtil.DBMS_SQLSERVER.equals(dbType)){
			url = "ra://"+dbHostName+":"+dbPort+";DatabaseName="+dbName;
		}else {
			url = "jdbc:db2://"+dbHostName+":"+dbPort+"/"+dbName;
		}
		return Messages.bind(Messages.DB_UNKNOW_DATABASE_NEW,new Object[]{ dbName, url, dbUserName, dbUserPwd.length()});
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_DATABASE_PANEL;
	}

	@Override
	public void setVisible(boolean visible) {

		boolean isFeatureChange = isFeaturesChange();
		log.info("For Databse panel features is changed : " + isFeatureChange);
		if (isFeatureChange) {
			createControl(parent);
		}

	}

	private boolean isFeaturesChange() {

		java.util.List<String> selectFeatures = this.getOffering();

		ArrayList newFeatureList = new ArrayList();
		if (selectFeatures != null) {
			for (int i = 0; i < selectFeatures.size(); i++) {
				String fe = selectFeatures.get(i);
				if (!fe.equals("main") && !isFeatureInstalled(fe)
						&& !fe.equals("common")&& !fe.equals("rte")&& !fe.equals("widgetContainer")) {
					newFeatureList.add(fe);
				}
			}
		}

		log.info("Database panel get offering   list: "
				+ newFeatureList.toString());
		log.info("Database panel select feature list: "
				+ selectFeaturesList.toString());

		//disable this checkbox if only one feature added
		log.info("features added size: " + selectFeaturesList.size());
		if ((isModifyInstall || isFixpackInstall) && newFeatureList.size() <= 1) {
			this.checkbox1.setEnabled(false);
			this.checkbox2.setEnabled(false);
		}

		int originalSize = selectFeaturesList.size();
		if (originalSize != newFeatureList.size()) {
			return true;
		}

		for (int i = 0; i < selectFeaturesList.size(); i++) {
			String feature = (String) selectFeaturesList.get(i);
			if (!newFeatureList.contains(feature)) {
				return true;
			}

		}
		return false;

	}

	private void setAllPasswordsToNull(){

		activitiesPassword = null;
		blogsPassword = null;
		communitiesPassword = null;
		dogearPassword = null;
		profilesPassword = null;
		wikisPassword = null;
		filesPassword = null;
		forumPassword = null;
		homepagePassword = null;
		metricsPassword = null;
		mobilePassword = null;
		ccmGCDPassword = null;
		ccmOSPassword = null;
		pushnotificationPassword = null;
		icecPassword = null;
		ic360Password = null;
	}

	private void setTheSamePassword_1(Button check) {
		if (check.getSelection()) {
			if (activitiesPasswordText1 != null) {
				activitiesPasswordText1.setEnabled(false);
			}
			if (blogsPasswordText1 != null) {
				blogsPasswordText1.setEnabled(false);
			}
			if (communitiesPasswordText1 != null) {
				communitiesPasswordText1.setEnabled(false);
			}
			if (dogearPasswordText1 != null) {
				dogearPasswordText1.setEnabled(false);
			}
			if (metricsPasswordText1 != null) {
				metricsPasswordText1.setEnabled(false);
			}
			if (mobilePasswordText1 != null) {
				mobilePasswordText1.setEnabled(false);
			}
			if (profilesPasswordText1 != null) {
				profilesPasswordText1.setEnabled(false);
			}
			if (wikisPasswordText1 != null) {
				wikisPasswordText1.setEnabled(false);
			}
			if (filesPasswordText1 != null) {
				filesPasswordText1.setEnabled(false);
			}
			if (forumPasswordText1 != null) {
				forumPasswordText1.setEnabled(false);
			}
			if (homepagePasswordText1 != null) {
				homepagePasswordText1.setEnabled(false);
			}
			if (pushnotificationPasswordText1 != null) {
				pushnotificationPasswordText1.setEnabled(false);
			}
			if (icecPasswordText1 != null) {
				icecPasswordText1.setEnabled(false);
			}
			if (ic360PasswordText1 != null) {
				ic360PasswordText1.setEnabled(false);
			}
			if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
				ccmGCDPasswordText1.setEnabled(false);
			}
			if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
				ccmOSPasswordText1.setEnabled(false);
			}
			if (isFeatureSelected("activities")) {
				activitiesPasswordText1.setEnabled(true);
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(activitiesPasswordText1
							.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(activitiesPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(activitiesPasswordText1.getText());;
				}
				return;
			}
			if (isFeatureSelected("blogs")) {
				blogsPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(blogsPasswordText1
							.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(blogsPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(blogsPasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(blogsPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(blogsPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(blogsPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(blogsPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(blogsPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(blogsPasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("dogear")) {
				dogearPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(dogearPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(dogearPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1
							.setText(dogearPasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(dogearPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1
							.setText(dogearPasswordText1.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(dogearPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(dogearPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(dogearPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(dogearPasswordText1.getText());;
				}

				return;
			}

			if (isFeatureSelected("communities")) {
				communitiesPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(communitiesPasswordText1
							.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(communitiesPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(communitiesPasswordText1.getText());;
				}

				return;
			}

			if (isFeatureSelected("files")) {
				filesPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(filesPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(filesPasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(filesPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(filesPasswordText1
							.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(filesPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(filesPasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(filesPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(filesPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(filesPasswordText1.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(filesPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(filesPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(filesPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(filesPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(filesPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(filesPasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("forums")) {
				forumPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(forumPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(forumPasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(forumPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(forumPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(forumPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(forumPasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(forumPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(forumPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(forumPasswordText1.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(forumPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(forumPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(forumPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(forumPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(forumPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(forumPasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("homepage") || isFeatureSelected("news")
					|| isFeatureSelected("search")) {
				homepagePasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(homepagePasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(homepagePasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1
							.setText(homepagePasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(homepagePasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(homepagePasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(homepagePasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(homepagePasswordText1
							.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1
							.setText(homepagePasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(homepagePasswordText1
							.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(homepagePasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(homepagePasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(homepagePasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(homepagePasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(homepagePasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(homepagePasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				ccmGCDPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(ccmGCDPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(ccmGCDPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(ccmGCDPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(ccmGCDPasswordText1
							.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(ccmGCDPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(ccmGCDPasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("metrics")) {
				metricsPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(metricsPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(metricsPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(metricsPasswordText1
							.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(metricsPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(metricsPasswordText1
							.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(metricsPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(metricsPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(metricsPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(metricsPasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("mobile")) {
				mobilePasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(mobilePasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(mobilePasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1
							.setText(mobilePasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(mobilePasswordText1.getText());
				}

				if (profilesPasswordText1 != null) {
					profilesPasswordText1
							.setText(mobilePasswordText1.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(mobilePasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(mobilePasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(mobilePasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(mobilePasswordText1.getText());;
				}

				return;
			}
			if (isFeatureSelected("profiles")) {
				profilesPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(profilesPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(profilesPasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1
							.setText(profilesPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(profilesPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(profilesPasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(profilesPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(profilesPasswordText1
							.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(dogearPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(dogearPasswordText1.getText());
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText(profilesPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(profilesPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(profilesPasswordText1.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(profilesPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(profilesPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(profilesPasswordText1.getText());;
				}
				return;
			}
			if (isFeatureSelected("wikis")) {
				wikisPasswordText1.setEnabled(true);
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText(wikisPasswordText1
							.getText());
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText(wikisPasswordText1
							.getText());
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText(wikisPasswordText1.getText());
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText(wikisPasswordText1.getText());
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText(wikisPasswordText1
							.getText());
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText(wikisPasswordText1
							.getText());
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText(wikisPasswordText1.getText());
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText(wikisPasswordText1.getText());
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText(wikisPasswordText1.getText());;
				}

				return;
			}
		} else {
			if (activitiesPasswordText1 != null) {
				activitiesPasswordText1.setEnabled(true);
			}
			if (blogsPasswordText1 != null) {
				blogsPasswordText1.setEnabled(true);
			}
			if (dogearPasswordText1 != null) {
				dogearPasswordText1.setEnabled(true);
			}
			if (communitiesPasswordText1 != null) {
				communitiesPasswordText1.setEnabled(true);
			}
			if (filesPasswordText1 != null) {
				filesPasswordText1.setEnabled(true);
			}
			if (forumPasswordText1 != null) {
				forumPasswordText1.setEnabled(true);
			}
			if (homepagePasswordText1 != null) {
				homepagePasswordText1.setEnabled(true);
			}
			if (metricsPasswordText1 != null) {
				metricsPasswordText1.setEnabled(true);
			}
			if (mobilePasswordText1 != null) {
				mobilePasswordText1.setEnabled(true);
			}
			if (profilesPasswordText1 != null) {
				profilesPasswordText1.setEnabled(true);
			}
			if (wikisPasswordText1 != null) {
				wikisPasswordText1.setEnabled(true);
			}
			if (pushnotificationPasswordText1 != null) {
				pushnotificationPasswordText1.setEnabled(true);
			}
			if (icecPasswordText1 != null) {
				icecPasswordText1.setEnabled(true);
			}
			if (ic360PasswordText1 != null) {
				ic360PasswordText1.setEnabled(true);
			}
			if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
				ccmGCDPasswordText1.setEnabled(true);
			}
			if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
				ccmOSPasswordText1.setEnabled(true);
			}

			//set all passwords to null
			setAllPasswordsToNull();

			if (isFeatureSelected("activities")) {
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}

				return;
			}
			if (isFeatureSelected("blogs")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}

				return;
			}
			if (isFeatureSelected("dogear")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("communities")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("files")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("forums")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("homepage") || isFeatureSelected("news")
					|| isFeatureSelected("search")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("metrics")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("mobile")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}

			if (isFeatureSelected("profiles")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (wikisPasswordText1 != null) {
					wikisPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ic360PasswordText1 != null) {
					ic360PasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
			if (isFeatureSelected("wikis")) {
				if (activitiesPasswordText1 != null) {
					activitiesPasswordText1.setText("");
				}
				if (blogsPasswordText1 != null) {
					blogsPasswordText1.setText("");
				}
				if (dogearPasswordText1 != null) {
					dogearPasswordText1.setText("");
				}
				if (communitiesPasswordText1 != null) {
					communitiesPasswordText1.setText("");
				}
				if (filesPasswordText1 != null) {
					filesPasswordText1.setText("");
				}
				if (forumPasswordText1 != null) {
					forumPasswordText1.setText("");
				}
				if (homepagePasswordText1 != null) {
					homepagePasswordText1.setText("");
				}
				if (metricsPasswordText1 != null) {
					metricsPasswordText1.setText("");
				}
				if (mobilePasswordText1 != null) {
					mobilePasswordText1.setText("");
				}
				if (profilesPasswordText1 != null) {
					profilesPasswordText1.setText("");
				}
				if (pushnotificationPasswordText1 != null) {
					pushnotificationPasswordText1.setText("");
				}
				if (icecPasswordText1 != null) {
					icecPasswordText1.setText("");
				}
				if (ccmGCDPasswordText1 != null && ccmGCDPasswordText1.isVisible()){
					ccmGCDPasswordText1.setText("");
				}
				if (ccmOSPasswordText1 != null && ccmOSPasswordText1.isVisible()){
					ccmOSPasswordText1.setText("");;
				}
				return;
			}
		}
	}

	private void setTheSamePassword_2(Button check) {
		if (check.getSelection()) {
			if (activitiesPasswordText2 != null) {
				activitiesPasswordText2.setEnabled(false);
			}
			if (blogsPasswordText2 != null) {
				blogsPasswordText2.setEnabled(false);
			}
			if (dogearPasswordText2 != null) {
				dogearPasswordText2.setEnabled(false);
			}
			if (communitiesPasswordText2 != null) {
				communitiesPasswordText2.setEnabled(false);
			}
			if (filesPasswordText2 != null) {
				filesPasswordText2.setEnabled(false);
			}
			if (forumPasswordText2 != null) {
				forumPasswordText2.setEnabled(false);
			}
			if (homepagePasswordText2 != null) {
				homepagePasswordText2.setEnabled(false);
			}
			if (metricsPasswordText2 != null) {
				metricsPasswordText2.setEnabled(false);
			}
			if (mobilePasswordText2 != null) {
				mobilePasswordText2.setEnabled(false);
			}
			if (profilesPasswordText2 != null) {
				profilesPasswordText2.setEnabled(false);
			}
			if (wikisPasswordText2 != null) {
				wikisPasswordText2.setEnabled(false);
			}
			if (pushnotificationPasswordText2 != null) {
				pushnotificationPasswordText2.setEnabled(false);
			}
			if (icecPasswordText2 != null) {
				icecPasswordText2.setEnabled(false);
			}
			if (ic360PasswordText2 != null) {
				ic360PasswordText2.setEnabled(false);
			}
			if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
				ccmGCDPasswordText2.setEnabled(false);
			}
			if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
				ccmOSPasswordText2.setEnabled(false);
			}
			if (isFeatureSelected("activities")) {
				activitiesPasswordText2.setEnabled(true);
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(activitiesPasswordText2
							.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(activitiesPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(activitiesPasswordText2.getText());
				}
				return;
			}
			if (isFeatureSelected("blogs")) {
				blogsPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(blogsPasswordText2
							.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(blogsPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(blogsPasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(blogsPasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(blogsPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(blogsPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(blogsPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(blogsPasswordText2.getText());
				}

				return;
			}
			if (isFeatureSelected("communities")) {
				communitiesPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(communitiesPasswordText2
							.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(communitiesPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(communitiesPasswordText2.getText());
				}

				return;
			}
			if (isFeatureSelected("dogear")) {
				dogearPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(dogearPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(dogearPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2
							.setText(dogearPasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(dogearPasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2
							.setText(dogearPasswordText2.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(dogearPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(dogearPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(dogearPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(dogearPasswordText2.getText());
				}

				return;
			}
			if (isFeatureSelected("files")) {
				filesPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(filesPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(filesPasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(filesPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(filesPasswordText2
							.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(filesPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(filesPasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(filesPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(filesPasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(filesPasswordText2.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(filesPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(filesPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(filesPasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(filesPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(filesPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(filesPasswordText2.getText());
				}
				return;
			}
			if (isFeatureSelected("forums")) {
				forumPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(forumPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(forumPasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(forumPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(forumPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(forumPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(forumPasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(forumPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(forumPasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(forumPasswordText2.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(forumPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(forumPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(forumPasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(forumPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(forumPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(forumPasswordText2.getText());
				}

				return;
			}
			if (isFeatureSelected("homepage") || isFeatureSelected("news")
					|| isFeatureSelected("search")) {
				homepagePasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(homepagePasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(homepagePasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(homepagePasswordText2
							.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2
							.setText(homepagePasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(homepagePasswordText2
							.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(homepagePasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(homepagePasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(homepagePasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(homepagePasswordText2.getText());
				}

				return;
			}
			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				ccmGCDPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(ccmGCDPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(ccmGCDPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(ccmGCDPasswordText2.getText());
				}
				return;
			}
			if (isFeatureSelected("metrics")) {
				metricsPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(metricsPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(metricsPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(metricsPasswordText2.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(metricsPasswordText2
							.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(metricsPasswordText2
							.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(metricsPasswordText2
							.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(metricsPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(metricsPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(metricsPasswordText2.getText());
				}

				return;
			}
			if (isFeatureSelected("moblie")) {
				mobilePasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(mobilePasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(mobilePasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2
							.setText(mobilePasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2
							.setText(mobilePasswordText2.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(mobilePasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(mobilePasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(mobilePasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(mobilePasswordText2.getText());
				}

				return;
			}

			if (isFeatureSelected("profiles")) {
				profilesPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(profilesPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(profilesPasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2
							.setText(profilesPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(profilesPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(profilesPasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(profilesPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(profilesPasswordText2
							.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(profilesPasswordText2
							.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2
							.setText(profilesPasswordText2.getText());
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText(profilesPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(profilesPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(profilesPasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(profilesPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(profilesPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(profilesPasswordText2.getText());
				}
				return;
			}
			if (isFeatureSelected("wikis")) {
				wikisPasswordText2.setEnabled(true);
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText(wikisPasswordText2
							.getText());
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText(wikisPasswordText2
							.getText());
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText(wikisPasswordText2.getText());
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText(wikisPasswordText2.getText());
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText(wikisPasswordText2
							.getText());
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText(wikisPasswordText2.getText());
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText(wikisPasswordText2.getText());
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText(wikisPasswordText2.getText());
				}
				return;
			}
		} else {
			if (activitiesPasswordText2 != null) {
				activitiesPasswordText2.setEnabled(true);
			}
			if (blogsPasswordText2 != null) {
				blogsPasswordText2.setEnabled(true);
			}
			if (communitiesPasswordText2 != null) {
				communitiesPasswordText2.setEnabled(true);
			}
			if (dogearPasswordText2 != null) {
				dogearPasswordText2.setEnabled(true);
			}
			if (metricsPasswordText2 != null) {
				metricsPasswordText2.setEnabled(true);
			}
			if (mobilePasswordText2 != null) {
				mobilePasswordText2.setEnabled(true);
			}
			if (profilesPasswordText2 != null) {
				profilesPasswordText2.setEnabled(true);
			}
			if (wikisPasswordText2 != null) {
				wikisPasswordText2.setEnabled(true);
			}
			if (filesPasswordText2 != null) {
				filesPasswordText2.setEnabled(true);
			}
			if (forumPasswordText2 != null) {
				forumPasswordText2.setEnabled(true);
			}
			if (homepagePasswordText2 != null) {
				homepagePasswordText2.setEnabled(true);
			}
			if (pushnotificationPasswordText2 != null) {
				pushnotificationPasswordText2.setEnabled(true);
			}
			if (icecPasswordText2 != null) {
				icecPasswordText2.setEnabled(true);
			}
			if (ic360PasswordText2 != null) {
				ic360PasswordText2.setEnabled(true);
			}
			if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
				ccmGCDPasswordText2.setEnabled(true);
			}
			if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
				ccmOSPasswordText2.setEnabled(true);
			}
			if (isFeatureSelected("activities")) {
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("blogs")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("communities")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("dogear")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("files")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("forums")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("homepage") || isFeatureSelected("news")
					|| isFeatureSelected("search")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("metrics")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
			if (isFeatureSelected("mobile")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}

				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}

			if (isFeatureSelected("profiles")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (wikisPasswordText2 != null) {
					wikisPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}

				return;
			}
			if (isFeatureSelected("wikis")) {
				if (activitiesPasswordText2 != null) {
					activitiesPasswordText2.setText("");
				}
				if (blogsPasswordText2 != null) {
					blogsPasswordText2.setText("");
				}
				if (dogearPasswordText2 != null) {
					dogearPasswordText2.setText("");
				}
				if (communitiesPasswordText2 != null) {
					communitiesPasswordText2.setText("");
				}
				if (filesPasswordText2 != null) {
					filesPasswordText2.setText("");
				}
				if (forumPasswordText2 != null) {
					forumPasswordText2.setText("");
				}
				if (homepagePasswordText2 != null) {
					homepagePasswordText2.setText("");
				}
				if (metricsPasswordText2 != null) {
					metricsPasswordText2.setText("");
				}
				if (mobilePasswordText2 != null) {
					mobilePasswordText2.setText("");
				}
				if (profilesPasswordText2 != null) {
					profilesPasswordText2.setText("");
				}
				if (pushnotificationPasswordText2 != null) {
					pushnotificationPasswordText2.setText("");
				}
				if (icecPasswordText2 != null) {
					icecPasswordText2.setText("");
				}
				if (ic360PasswordText2 != null) {
					ic360PasswordText2.setText("");
				}
				if (ccmGCDPasswordText2 != null && ccmGCDPasswordText2.isVisible()){
					ccmGCDPasswordText2.setText("");
				}
				if (ccmOSPasswordText2 != null && ccmOSPasswordText2.isVisible()){
					ccmOSPasswordText2.setText("");;
				}
				return;
			}
		}
	}
}

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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
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

public class ContentStorePanel extends BaseConfigCustomPanel {
	String className = ContentStorePanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section sharedContentStoreSection = null;
	private Section localContentStoreSection = null;

	/*
	private Button samePathButton = null;
	private Button diffPathButton = null;
	*/

	private Text sharedContentlocationText = null;
	private Button sharedContentlocationButton = null;
	private Text localContentlocationText = null;
	private Button localContentlocationButton = null;
	private Button verifyButton = null;
	
	private String sharedContentStorePath;
	private String localContentStorePath;
	
	private boolean isRecordingMode = false;
	private boolean isCreate = false;
	/*
	private Text oaStatisticLocationText = null;
	private Button oaStatisticlocationButton = null;
	private Text oaContentLocationText = null;
	private Button oaContentlocationButton = null;
	private Text blogsUploadLocationText = null;
	private Button blogsUploadlocationButton = null;
	private Text communitiesStatisticLocationText = null;
	private Button communitiesStatisticlocationButton = null;
	private Text dogearFavoriteLocationText = null;
	private Button dogearFavoritelocationButton = null;
	private Text filesUploadLocationText = null;
	private Button filesUploadlocationButton = null;
	private Text forumUploadLocationText = null;
	private Button forumUploadlocationButton = null;
	private Text profilesStatisticLocationText = null;
	private Button profilesStatisticlocationButton = null;
	private Text profilesCacheLocationText = null;
	private Button profilesCacheLocationButton = null;
	private Text searchIndexLocationText = null;
	private Button searchIndexlocationButton = null;
	private Text searchDictionaryLocationText = null;
	private Button searchDictionaryLocationButton = null;
	private Text wikisUploadLocationText = null;
	private Button wikisUploadlocationButton = null;
	*/

	public ContentStorePanel() {
		super(Messages.CONTENT_STORE);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData().getAdapter(IAgent.class);
		isRecordingMode = agent.isSkipInstall();
		profile = getProfile();
		
		log.info("Content Store Panel :: Entered");
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

		//Select Application Server Type
		sharedContentStoreSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);

		//applicationServerSection.setSize(2000, 10);
		sharedContentStoreSection.setText(Messages.SHARED_CONTENT_STORE);

		final Composite sharedContentStoreContainer = toolkit.createComposite(sharedContentStoreSection);
		GridLayout sharedContentStoreLayout = new GridLayout();
		sharedContentStoreLayout.numColumns = 2;
		sharedContentStoreContainer.setLayout(sharedContentStoreLayout);
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		sharedContentStoreContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		//inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 100;

		/*
		this.samePathButton = new Button(applicationServerSelectContainer, SWT.RADIO);
		this.samePathButton.setBackground(applicationServerSelectContainer.getBackground());
		this.samePathButton.setSelection(true);
		this.samePathButton.setText("Specify a data directory for all features");
		this.diffPathButton = new Button(applicationServerSelectContainer, SWT.RADIO);
		this.diffPathButton.setBackground(applicationServerSelectContainer.getBackground());
		this.diffPathButton.setText("Specify a data directory for each feature");
		*/

		/*
		final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
		final StackLayout stackLayout = new StackLayout();
		applicationServerSelectStackContainer.setLayout(stackLayout);

		
		final Composite samePathComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
		final Composite diffPathComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
		

		stackLayout.topControl = samePathComposite;
		applicationServerSelectStackContainer.layout();

		applicationServerSection.setClient(applicationServerSelectContainer);

		samePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (samePathButton.getSelection() == true) { //Cell
					diffPathButton.setSelection(false);
					stackLayout.topControl = samePathComposite;
					applicationServerSelectStackContainer.layout();
				}
				verifyComplete();
			}
		});

		diffPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (diffPathButton.getSelection() == true) { //Cell
					samePathButton.setSelection(false);
					stackLayout.topControl = diffPathComposite;
					applicationServerSelectStackContainer.layout();
				}
				verifyComplete();
			}
		});
		
		//samePathButton
		samePathComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		samePathComposite.setLayoutData(gd);

		Section samePathSection = toolkit.createSection(samePathComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		samePathSection.setLayoutData(gridData);
		samePathSection.setText("Same Data Directory");

		Composite samePathContainer = toolkit.createComposite(samePathSection);
		GridLayout samePathLayout = new GridLayout();
		samePathLayout.numColumns = 2;
		samePathContainer.setLayout(samePathLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		samePathContainer.setLayoutData(gd);
		*/

		Label sharedContentlocationLabel = new Label(sharedContentStoreContainer, SWT.WRAP);
		sharedContentlocationLabel.setText(Messages.SHARED_CONTENT_STORE_LOC);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint=630;
		sharedContentlocationLabel.setLayoutData(gd);
		sharedContentlocationLabel.pack();
		this.sharedContentlocationText = new Text(sharedContentStoreContainer, SWT.BORDER | SWT.SINGLE);
		
		String osName = System.getProperty("os.name");
		try{
		if (osName.startsWith("Windows")) {
			//this.sharedContentlocationText.setText(profile.getInstallLocation()+"\\data\\shared");
		} else if (osName.equals("Linux")) {
			//this.sharedContentlocationText.setText(profile.getInstallLocation()+"/data/shared");
		} else if (osName.equals("AIX")) {
			//this.sharedContentlocationText.setText(profile.getInstallLocation()+"/data/shared");
		}
		}
		catch(Exception e){}
		gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 248;
		sharedContentlocationText.setLayoutData(gd);
		this.sharedContentlocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setChanged();
			}
		});
		sharedContentlocationButton = new Button(sharedContentStoreContainer, SWT.NONE);
		sharedContentlocationButton.setText("...");
		sharedContentlocationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(sharedContentStoreContainer.getShell());
				
				if(sharedContentlocationText.getText() != null){
					dialog.setFilterPath(sharedContentlocationText.getText());
				}
				
				dialog.setText(Messages.SHARED_CONTENT_STORE_LOC);
				String dir = dialog.open();
				if(dir != null){
					sharedContentlocationText.setText(dir);
				}
				setChanged();
			}
		});

		sharedContentStoreSection.setClient(sharedContentStoreContainer);

		/*
		final Composite localContentStoreStackContainer = new Composite(form.getBody(), SWT.NONE);
		final StackLayout stackLayout = new StackLayout();
		localContentStoreStackContainer.setLayout(stackLayout);
		*/

		final Composite compositeSN = new Composite(form.getBody(), SWT.NONE);

		GridData gridData = new GridData(GridData.BEGINNING);
		gridData.horizontalSpan = 2;

		//Component for SN        
		compositeSN.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		compositeSN.setLayoutData(gd);

		localContentStoreSection = toolkit.createSection(compositeSN, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		localContentStoreSection.setLayoutData(gridData);
		localContentStoreSection.setText(Messages.LOCAL_CONTENT_STORE);

		Composite localContentStoreContainer = toolkit.createComposite(localContentStoreSection);
		GridLayout localContentStoreLayout = new GridLayout();
		localContentStoreLayout.numColumns = 2;
		localContentStoreContainer.setLayout(localContentStoreLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		localContentStoreContainer.setLayoutData(gd);

		Label localContentlocationLabel = new Label(localContentStoreContainer, SWT.WRAP);
		localContentlocationLabel.setText(Messages.LOCAL_CONTENT_STORE_LOC);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint=630;
		localContentlocationLabel.pack();
		localContentlocationLabel.setLayoutData(gd);

		this.localContentlocationText = new Text(localContentStoreContainer, SWT.BORDER | SWT.SINGLE);
		try{
		if (osName.startsWith("Windows")) {
			//this.localContentlocationText.setText(profile.getInstallLocation()+"\\data\\local");
		} else if (osName.equals("Linux")) {
			//this.localContentlocationText.setText(profile.getInstallLocation()+"/data/local");
		} else if (osName.equals("AIX")) {
			//this.localContentlocationText.setText(profile.getInstallLocation()+"/data/local");
		}
		}
		catch(Exception e){}
		gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 248;
		localContentlocationText.setLayoutData(gd);
		this.localContentlocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setChanged();
			}
		});
		localContentlocationButton = new Button(localContentStoreContainer, SWT.NONE);
		localContentlocationButton.setText("...");
		localContentlocationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(compositeSN.getShell());
				if(localContentlocationText.getText() != null){
					dialog.setFilterPath(localContentlocationText.getText());
				}
				dialog.setText(Messages.LOCAL_CONTENT_STORE_LOC);
				String dir = dialog.open();
				if(dir != null){
					localContentlocationText.setText(dir);
				}
				setChanged();
			}
		});
		
		GridData gridDataButtonSize = new GridData();
		gridDataButtonSize.widthHint = 200;
		gridDataButtonSize.heightHint = 30;
		gridDataButtonSize.horizontalSpan = 2;
		
		verifyButton = new Button(localContentStoreContainer, SWT.PUSH);
		verifyButton.setLayoutData(gridDataButtonSize);
		if(isRecordingMode)
			verifyButton.setText(Messages.SKIP_VALIDATION);
		else
			verifyButton.setText(Messages.VALIDATE);
		verifyButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				verifyContentStoreLocation();
			}

		});

		localContentStoreSection.setClient(localContentStoreContainer);

		/*
		//diffPathComposite
		diffPathComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		diffPathComposite.setLayoutData(gd);

		if (isFeatureSelected("activities")) {
			Section activitiesSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			activitiesSection.setLayoutData(gridData);
			activitiesSection.setText("Activities Content Store");

			Composite activitiesContainer = toolkit.createComposite(activitiesSection);
			GridLayout activitesLayout = new GridLayout();
			activitesLayout.numColumns = 2;
			activitiesContainer.setLayout(activitesLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			activitiesContainer.setLayoutData(gd);

			Label aoStatisticDescriptionLabel = new Label(activitiesContainer, SWT.WRAP);
			aoStatisticDescriptionLabel
					.setText("Specify the location to store sets of statistics collected by Activities in files.  The files are stored in a comma separated values (CSV) text file.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			aoStatisticDescriptionLabel.setLayoutData(gd);

			this.oaStatisticLocationText = new Text(activitiesContainer, SWT.BORDER | SWT.SINGLE);
			this.oaStatisticLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\activities\\statistic");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			oaStatisticLocationText.setLayoutData(gd);
			this.oaStatisticLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.oaStatisticlocationButton = new Button(activitiesContainer, SWT.NONE);
			this.oaStatisticlocationButton.setText("...");
			this.oaStatisticlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			Label aoContentDescriptionLabel = new Label(activitiesContainer, SWT.WRAP);
			aoContentDescriptionLabel
					.setText("Specify the file location of the network or operating system file system for the content store.  For non-clustered environments specify a local directory.  For clustered environments, use a share that is accessible through the network.");
			gd = new GridData();
			gd.verticalIndent = 10;
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			aoContentDescriptionLabel.setLayoutData(gd);

			this.oaContentLocationText = new Text(activitiesContainer, SWT.BORDER | SWT.SINGLE);
			this.oaContentLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\activities\\content");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			oaContentLocationText.setLayoutData(gd);
			this.oaContentLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});

			this.oaContentlocationButton = new Button(activitiesContainer, SWT.NONE);
			this.oaContentlocationButton.setText("...");
			this.oaContentlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			activitiesSection.setClient(activitiesContainer);
		}

		if (isFeatureSelected("blogs")) {
			Section blogsSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			blogsSection.setLayoutData(gridData);
			blogsSection.setText("Blogs Content Store");

			Composite blogsContainer = toolkit.createComposite(blogsSection);
			GridLayout blogsLayout = new GridLayout();
			blogsLayout.numColumns = 2;
			blogsContainer.setLayout(blogsLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			blogsContainer.setLayoutData(gd);

			Label blogsUploadDescriptionLabel = new Label(blogsContainer, SWT.WRAP);
			blogsUploadDescriptionLabel
					.setText("Select a storage location for file uploads.  File uploads add files such as images or presentations to Blogs.  For non-clustered environments specify a local directory.  For clustered environments, use a share that is accessible through the network.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			blogsUploadDescriptionLabel.setLayoutData(gd);

			this.blogsUploadLocationText = new Text(blogsContainer, SWT.BORDER | SWT.SINGLE);
			this.blogsUploadLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\blogs\\upload");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			blogsUploadLocationText.setLayoutData(gd);
			this.blogsUploadLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.blogsUploadlocationButton = new Button(blogsContainer, SWT.NONE);
			this.blogsUploadlocationButton.setText("...");
			this.blogsUploadlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			blogsSection.setClient(blogsContainer);
		}

		if (isFeatureSelected("communities")) {
			Section communitiesSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			communitiesSection.setLayoutData(gridData);
			communitiesSection.setText("Communities Content Store");

			Composite communitiesContainer = toolkit.createComposite(communitiesSection);
			GridLayout communitiesLayout = new GridLayout();
			communitiesLayout.numColumns = 2;
			communitiesContainer.setLayout(communitiesLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			communitiesContainer.setLayoutData(gd);

			Label communitiesStatisticDescriptionLabel = new Label(communitiesContainer, SWT.WRAP);
			communitiesStatisticDescriptionLabel
					.setText("Specify the location to store sets of statistics collected by Communities in files.  The files are stored in a comma separated values (CSV) text file.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			communitiesStatisticDescriptionLabel.setLayoutData(gd);

			this.communitiesStatisticLocationText = new Text(communitiesContainer, SWT.BORDER | SWT.SINGLE);
			this.communitiesStatisticLocationText
					.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\communities\\statistic");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			communitiesStatisticLocationText.setLayoutData(gd);
			this.communitiesStatisticLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.communitiesStatisticlocationButton = new Button(communitiesContainer, SWT.NONE);
			this.communitiesStatisticlocationButton.setText("...");
			this.communitiesStatisticlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			communitiesSection.setClient(communitiesContainer);
		}

		if (isFeatureSelected("dogear")) {
			Section dogearSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			dogearSection.setLayoutData(gridData);
			dogearSection.setText("Dogear Content Store");

			Composite dogearContainer = toolkit.createComposite(dogearSection);
			GridLayout dogearLayout = new GridLayout();
			dogearLayout.numColumns = 2;
			dogearContainer.setLayout(dogearLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			dogearContainer.setLayoutData(gd);

			Label dogearFavoriteDescriptionLabel = new Label(dogearContainer, SWT.WRAP);
			dogearFavoriteDescriptionLabel
					.setText("Specify the location to store favicons for Dogear. Favicons (short for \"favorite icons\") are images displayed with a bookmark. Users see at a glance the favicon used to represent Web sites and differentiate intranet and Internet locations.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			dogearFavoriteDescriptionLabel.setLayoutData(gd);

			this.dogearFavoriteLocationText = new Text(dogearContainer, SWT.BORDER | SWT.SINGLE);
			this.dogearFavoriteLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\dogear\\favorite");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			dogearFavoriteLocationText.setLayoutData(gd);
			this.dogearFavoriteLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.dogearFavoritelocationButton = new Button(dogearContainer, SWT.NONE);
			this.dogearFavoritelocationButton.setText("...");
			this.dogearFavoritelocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			dogearSection.setClient(dogearContainer);
		}

		if (isFeatureSelected("files")) {
			Section filesSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			filesSection.setLayoutData(gridData);
			filesSection.setText("Files Content Store");

			Composite filesContainer = toolkit.createComposite(filesSection);
			GridLayout filesLayout = new GridLayout();
			filesLayout.numColumns = 2;
			filesContainer.setLayout(filesLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			filesContainer.setLayoutData(gd);

			Label filesUploadDescriptionLabel = new Label(filesContainer, SWT.WRAP);
			filesUploadDescriptionLabel
					.setText("Select a storage location for file uploads.  File uploads add files such as images or presentations to Files.  For non-clustered environments specify a local directory.  For clustered environments, use a share that is accessible through the network.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			filesUploadDescriptionLabel.setLayoutData(gd);

			this.filesUploadLocationText = new Text(filesContainer, SWT.BORDER | SWT.SINGLE);
			this.filesUploadLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\files\\upload");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			filesUploadLocationText.setLayoutData(gd);
			this.filesUploadLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.filesUploadlocationButton = new Button(filesContainer, SWT.NONE);
			this.filesUploadlocationButton.setText("...");
			this.filesUploadlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			filesSection.setClient(filesContainer);
		}

		if (isFeatureSelected("forums")) {
			Section forumSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			forumSection.setLayoutData(gridData);
			forumSection.setText("Forum Content Store");

			Composite forumContainer = toolkit.createComposite(forumSection);
			GridLayout forumLayout = new GridLayout();
			forumLayout.numColumns = 2;
			forumContainer.setLayout(forumLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			forumContainer.setLayoutData(gd);

			Label forumUploadDescriptionLabel = new Label(forumContainer, SWT.WRAP);
			forumUploadDescriptionLabel
					.setText("Select a storage location for file uploads.  File uploads add forum such as images or presentations to Forum.  For non-clustered environments specify a local directory.  For clustered environments, use a share that is accessible through the network.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			forumUploadDescriptionLabel.setLayoutData(gd);

			this.forumUploadLocationText = new Text(forumContainer, SWT.BORDER | SWT.SINGLE);
			this.forumUploadLocationText.setText("C:\\Program Forum\\IBM\\LotusConnections\\Data\\forum\\upload");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			forumUploadLocationText.setLayoutData(gd);
			this.forumUploadLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.forumUploadlocationButton = new Button(forumContainer, SWT.NONE);
			this.forumUploadlocationButton.setText("...");
			this.forumUploadlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			forumSection.setClient(forumContainer);
		}

		if (isFeatureSelected("profiles")) {
			Section profilesSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			profilesSection.setLayoutData(gridData);
			profilesSection.setText("Profiles Cache Store");

			Composite profilesContainer = toolkit.createComposite(profilesSection);
			GridLayout profilesLayout = new GridLayout();
			profilesLayout.numColumns = 2;
			profilesContainer.setLayout(profilesLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			profilesContainer.setLayoutData(gd);

			Label profilesStatisticDescriptionLabel = new Label(profilesContainer, SWT.WRAP);
			profilesStatisticDescriptionLabel
					.setText("Specify the location to store sets of statistics collected by Profiles in files.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			profilesStatisticDescriptionLabel.setLayoutData(gd);

			this.profilesStatisticLocationText = new Text(profilesContainer, SWT.BORDER | SWT.SINGLE);
			this.profilesStatisticLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\profiles\\statistic");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			profilesStatisticLocationText.setLayoutData(gd);
			this.profilesStatisticLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.profilesStatisticlocationButton = new Button(profilesContainer, SWT.NONE);
			this.profilesStatisticlocationButton.setText("...");
			this.profilesStatisticlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			Label profilesCacheDescriptionLabel = new Label(profilesContainer, SWT.WRAP);
			profilesCacheDescriptionLabel.setText("Specify the location to store sets of cache collected by Profiles in files.");
			gd = new GridData();
			gd.verticalIndent = 10;
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			profilesCacheDescriptionLabel.setLayoutData(gd);

			this.profilesCacheLocationText = new Text(profilesContainer, SWT.BORDER | SWT.SINGLE);
			this.profilesCacheLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\profiles\\cache");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			profilesCacheLocationText.setLayoutData(gd);
			this.profilesCacheLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});

			this.profilesCacheLocationButton = new Button(profilesContainer, SWT.NONE);
			this.profilesCacheLocationButton.setText("...");
			this.profilesCacheLocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			profilesSection.setClient(profilesContainer);
		}

		if (isFeatureSelected("search")) {
			Section searchSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			searchSection.setLayoutData(gridData);
			searchSection.setText("Search Dictionary Store");

			Composite searchContainer = toolkit.createComposite(searchSection);
			GridLayout searchLayout = new GridLayout();
			searchLayout.numColumns = 2;
			searchContainer.setLayout(searchLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			searchContainer.setLayoutData(gd);

			Label searchIndexDescriptionLabel = new Label(searchContainer, SWT.WRAP);
			searchIndexDescriptionLabel
					.setText("Specify a local directory or network share to store index files.  Search uses index files for full-text searches across all installed Lotus Connections services.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			searchIndexDescriptionLabel.setLayoutData(gd);

			this.searchIndexLocationText = new Text(searchContainer, SWT.BORDER | SWT.SINGLE);
			this.searchIndexLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\search\\index");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			searchIndexLocationText.setLayoutData(gd);
			this.searchIndexLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.searchIndexlocationButton = new Button(searchContainer, SWT.NONE);
			this.searchIndexlocationButton.setText("...");
			this.searchIndexlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			Label searchDictionaryDescriptionLabel = new Label(searchContainer, SWT.WRAP);
			searchDictionaryDescriptionLabel
					.setText("Specify a local directory or network share to store dictionary files.  Search uses these dictionaries for LanguageWare features.");
			gd = new GridData();
			gd.verticalIndent = 10;
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			searchDictionaryDescriptionLabel.setLayoutData(gd);

			this.searchDictionaryLocationText = new Text(searchContainer, SWT.BORDER | SWT.SINGLE);
			this.searchDictionaryLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\search\\dictionary");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			searchDictionaryLocationText.setLayoutData(gd);
			this.searchDictionaryLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});

			this.searchDictionaryLocationButton = new Button(searchContainer, SWT.NONE);
			this.searchDictionaryLocationButton.setText("...");
			this.searchDictionaryLocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			searchSection.setClient(searchContainer);
		}

		if (isFeatureSelected("wikis")) {
			Section wikisSection = toolkit.createSection(diffPathComposite, Section.TITLE_BAR);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			wikisSection.setLayoutData(gridData);
			wikisSection.setText("Wikis Content Store");

			Composite wikisContainer = toolkit.createComposite(wikisSection);
			GridLayout wikisLayout = new GridLayout();
			wikisLayout.numColumns = 2;
			wikisContainer.setLayout(wikisLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			wikisContainer.setLayoutData(gd);

			Label wikisUploadDescriptionLabel = new Label(wikisContainer, SWT.WRAP);
			wikisUploadDescriptionLabel
					.setText("Select a storage location for file uploads.  File uploads add files such as images or presentations to Wikis.  For non-clustered environments specify a local directory.  For clustered environments, use a share that is accessible through the network.");
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 400;
			wikisUploadDescriptionLabel.setLayoutData(gd);

			this.wikisUploadLocationText = new Text(wikisContainer, SWT.BORDER | SWT.SINGLE);
			this.wikisUploadLocationText.setText("C:\\Program Files\\IBM\\LotusConnections\\Data\\wikis\\upload");
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 248;
			wikisUploadLocationText.setLayoutData(gd);
			this.wikisUploadLocationText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					verifyComplete();
				}
			});
			this.wikisUploadlocationButton = new Button(wikisContainer, SWT.NONE);
			this.wikisUploadlocationButton.setText("...");
			this.wikisUploadlocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(applicationServerSelectContainer.getShell());
					dialog.setText(Messages.WAS_LOCATION_BROWSER_MSG);
					String dir = dialog.open();
				}
			});

			wikisSection.setClient(wikisContainer);
		}
		*/
		form.pack();
		setControl(container);
	}
	
	private void setChanged() {
		nextEnabled = false;
		setPageComplete(false);
		setErrorMessage(Messages.VALIDATE_BTN_WARNING);
	}

	private void verifyContentStoreLocation() {
		nextEnabled = false;
		setPageComplete(false);
		InstallValidator iv = new InstallValidator();
		sharedContentStorePath = sharedContentlocationText.getText().trim();
		localContentStorePath = localContentlocationText.getText().trim();
		log.info("local content store path: " + localContentStorePath);
		log.info("shared content store path: " + sharedContentStorePath);
		if (!isRecordingMode) {
			try {
				boolean localResult = iv.validatePath(localContentStorePath);
				if (!localResult) {
					log.error("local content store path is not valid: "
							+ iv.getMessage());
					setErrorMessage(iv.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
				boolean sharedResult = iv.validatePath(sharedContentStorePath);
				if (!sharedResult) {
					log.error("shared content store path is not valid: "
							+ iv.getMessage());
					setErrorMessage(iv.getMessage());
					nextEnabled = false;
					setPageComplete(false);
					return;
				}
			} catch (Exception e) {
				log.error(e);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
			showValidationSuccessMessageDialog(Messages.VALIDATION_SUCCESSFUL);
			verifyButton.setText(Messages.VALIDATED);
			isCreate = true;
		}
		
		setErrorMessage(null);
		nextEnabled = true;
		setPageComplete(true);
		log.info("local and shared content store path is valid");
		//profile = getProfile();
		
		if(localContentStorePath.endsWith("/") || localContentStorePath.endsWith("\\")){
			StringBuilder tempLocalPath = new StringBuilder(localContentStorePath); 
			tempLocalPath.deleteCharAt(tempLocalPath.length()-1);
			localContentStorePath = tempLocalPath.toString();
		}
		
		if(sharedContentStorePath.endsWith("/") || sharedContentStorePath.endsWith("\\")){
			StringBuilder tempLocalPath = new StringBuilder(sharedContentStorePath); 
			tempLocalPath.deleteCharAt(tempLocalPath.length()-1);
			sharedContentStorePath = tempLocalPath.toString();
		}
		
		this.profile.setUserData("user.contentStore.local.path", transferWinPath(localContentStorePath));
		
		String transfersharedContentStorePath = convertPathToForwardSlash(sharedContentStorePath);
		String sharedContentStorePath_ccm = sharedContentStorePath;
		String ConStorePath = null;
		if (transfersharedContentStorePath.contains(":")){
    		int index = transfersharedContentStorePath.indexOf(":");
    		ConStorePath = transfersharedContentStorePath.substring(0,index) + "\\" + transfersharedContentStorePath.substring(index);
    		log.info("shared content store path: " + ConStorePath);
    		sharedContentStorePath_ccm = convertPathToForwardSlash(sharedContentStorePath);
    		sharedContentStorePath_ccm = sharedContentStorePath_ccm + "/ccm";
    		//showValidationSuccessMessageDialog(ConStorePath);
    	}
		else{
			ConStorePath = convertPathToForwardSlash(sharedContentStorePath);
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				sharedContentStorePath_ccm = sharedContentStorePath_ccm + "\\ccm";
			}
			else {
				sharedContentStorePath_ccm = sharedContentStorePath_ccm + "/ccm";
			}
		}
		this.profile.setUserData("user.contentStore.shared.path", transferWinPath(sharedContentStorePath));
		this.profile.setUserData("user.contentStore.shared.path.configproperties", sharedContentStorePath_ccm);
		this.profile.setUserData("user.messageStore.shared.path", transferWinConStorePath(transferWinPath(escapeDollarSign(sharedContentStorePath))));
		this.profile.setUserData("user.connections.install.location", transferPath(profile.getInstallLocation()));
		this.profile.setUserData("user.connections.install.location.win32format", profile.getInstallLocation());
		
		String ceInstallDir = transferPath(profile.getInstallLocation()) + "/FileNet/ContentEngine";
		String ceclientInstallDir = transferPath(profile.getInstallLocation()) + "/FileNet/CEClient";
		String fncsInstallDir = transferPath(profile.getInstallLocation()) + "/FNCS";
		
		//showValidationSuccessMessageDialog(ceInstallDir);
		//showValidationSuccessMessageDialog(ceclientInstallDir);
		//showValidationSuccessMessageDialog(fncsInstallDir);
		
		this.profile.setUserData("user.ce.install.location", ceInstallDir);
		this.profile.setUserData("user.ceclient.install.location", ceclientInstallDir);
		this.profile.setUserData("user.fncs.install.location", fncsInstallDir);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_CONTENT_STORE_PANEL;
	}
	@Override
	public void setVisible(boolean visible) {
		
		if(!isCreate){
			createControl(parent);
		}
		
//		createControl(parent);
	}
}

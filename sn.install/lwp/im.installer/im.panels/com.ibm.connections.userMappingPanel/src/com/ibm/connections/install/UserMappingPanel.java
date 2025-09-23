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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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

public class UserMappingPanel extends BaseConfigCustomPanel {

	String className = UserMappingPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section applicationServerSection = null;
	
	private Button addOnlyUserButton = null;
	private Button doLaterButton = null;
	
	private Composite addOnlyUserComposite;
	private Composite doLaterButtonComposite;
	
	private Text administrativeUsersText = null;
	private Text globalModeraterUsersText = null;

	private boolean isRecordingMode = false;
	
	private GridData gd = new GridData();
	
	public UserMappingPanel() {
		super(Messages.USER_MAPPING);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		profile = getProfile();
		isRecordingMode = agent.isSkipInstall();
		log.info("User Mapping Panel :: Entered");
		
		try {
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
			//************************************************************
			
			//Select Application Server Type
//			applicationServerSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR|SWT.WRAP);
			applicationServerSection = toolkit.createSection(form.getBody(), Section.NO_TITLE);

			//applicationServerSection.setSize(2000, 10);
//			applicationServerSection.setText(Messages.USER_MAPPING);

			final Composite applicationServerSelectContainer = toolkit.createComposite(applicationServerSection);
			GridLayout applicationServerSelectLayout = new GridLayout();
			//applicationServerSelectLayout.numColumns = 2;
			applicationServerSelectContainer.setLayout(applicationServerSelectLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			applicationServerSelectContainer.setLayoutData(gd);

			//applicationServerSection.setClient(applicationServerSelectContainer);
			
//			this.addOnlyUserButton = new Button(applicationServerSelectContainer, SWT.RADIO);
//			this.addOnlyUserButton.setBackground(applicationServerSelectContainer.getBackground());
//			this.addOnlyUserButton.setSelection(true);
//			this.addOnlyUserButton.setText(Messages.USER_MAPPING_BUTTON_ADD_USER);
//
//			this.doLaterButton = new Button(applicationServerSelectContainer, SWT.RADIO);
//			this.doLaterButton.setBackground(applicationServerSelectContainer.getBackground());
//			this.doLaterButton.setSelection(false);
//			this.doLaterButton.setText(Messages.USER_MAPPING_BUTTON_DO_LATER);
	
			final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			applicationServerSelectStackContainer.setLayout(stackLayout);

			addOnlyUserComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
			doLaterButtonComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

			stackLayout.topControl = doLaterButtonComposite;
			applicationServerSelectStackContainer.layout();

			applicationServerSection.setClient(applicationServerSelectContainer);
			
//			addOnlyUserButton.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					if (addOnlyUserButton.getSelection() == true) {
//						addOnlyUserButton.setSelection(true);
//						doLaterButton.setSelection(false);
//						stackLayout.topControl = addOnlyUserComposite;
//						applicationServerSelectStackContainer.layout();
//						verifyCompleteNone();
//					}
//				}
//			});
//			
//			doLaterButton.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					if (doLaterButton.getSelection() == true) {
//						addOnlyUserButton.setSelection(false);
//						doLaterButton.setSelection(true);
//						stackLayout.topControl = doLaterButtonComposite;
//						applicationServerSelectStackContainer.layout();
//						verifyCompleteNone();
//					}
//				}
//			});
			
			//final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
			//final StackLayout stackLayout = new StackLayout();
			//applicationServerSelectStackContainer.setLayout(stackLayout);

			//final Composite setCognosLaterComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

			stackLayout.topControl = addOnlyUserComposite;
			applicationServerSelectStackContainer.layout();
			
			addOnlyUserComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			addOnlyUserComposite.setLayoutData(gd);
			createRoleMappingSection(addOnlyUserComposite);
			
			form.pack();
			setControl(container);
			nextEnabled = false;
			setPageComplete(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createRoleMappingSection(Composite setCognosLaterComposite) {

		//1.Create administrative users section
		Section administrativeUsersSection = toolkit.createSection(setCognosLaterComposite,Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		administrativeUsersSection.setLayoutData(gridData);
		administrativeUsersSection.setText(Messages.USER_MAPPING_ADMINISTRATIVE_USERS_NAME);
		Composite administrativeUsersContainer = toolkit.createComposite(administrativeUsersSection);
		GridLayout userLayout = new GridLayout();
		administrativeUsersContainer.setLayout(userLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		administrativeUsersContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		// inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 350;
		GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);
		// inputgridDataForLabel.horizontalSpan = 2;
		inputgridDataForLabel.widthHint = 150;
		
		new Label(administrativeUsersContainer, SWT.WRAP).setText(Messages.USER_MAPPING_ADMINISTRATIVE_USERS_DESC);
		administrativeUsersText = new Text(administrativeUsersContainer, SWT.BORDER | SWT.SINGLE);
		this.administrativeUsersText.setLayoutData(inputgridData);
		this.administrativeUsersText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				profile.setUserData("user.user.mapping.administrative.users", administrativeUsersText.getText());
				setRoleMappingForModify();
			}
		});
		administrativeUsersSection.setClient(administrativeUsersContainer);

		//2.Create global moderater users
		Section globalModeraterUsersSection = toolkit.createSection(setCognosLaterComposite,Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		globalModeraterUsersSection.setLayoutData(gridData);
		globalModeraterUsersSection.setText(Messages.USER_MAPPING_GLOBAL_MODERATOR_USERS_NAME);
		Composite globalModeraterUsersContainer = toolkit.createComposite(globalModeraterUsersSection);
		userLayout = new GridLayout();
		globalModeraterUsersContainer.setLayout(userLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		globalModeraterUsersContainer.setLayoutData(gd);
		
		inputgridData = new GridData(GridData.BEGINNING);
		// inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 350;
		inputgridDataForLabel = new GridData(GridData.BEGINNING);
		// inputgridDataForLabel.horizontalSpan = 2;
		inputgridDataForLabel.widthHint = 150;
		
		new Label(globalModeraterUsersContainer, SWT.WRAP).setText(Messages.USER_MAPPING_GLOBAL_MODERATOR_USERS_DESC);
		globalModeraterUsersText = new Text(globalModeraterUsersContainer, SWT.BORDER | SWT.SINGLE);
		this.globalModeraterUsersText.setLayoutData(inputgridData);
		this.globalModeraterUsersText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				profile.setUserData("user.user.mapping.global.moderater.users", globalModeraterUsersText.getText());
				setRoleMappingForModify();
			}
		});
		globalModeraterUsersSection.setClient(globalModeraterUsersContainer);
		
	}

	private void setRoleMappingForModify(){
		
		if(isModify()){
			String admin = profile.getUserData("user.user.mapping.administrative.users");
			String glbModerater = profile.getUserData("user.user.mapping.global.moderater.users");
			
			StringBuffer sb = new StringBuffer();
			if(admin != null && !"".equals(admin))
				sb.append("\"admin\": \"").append(admin).append("\",");
			if(admin != null && !"".equals(admin))
				sb.append("\"global-moderator\": \"").append(glbModerater).append("\",");
			
			profile.setUserData("user.profiles.role.mapping", sb.toString());
			profile.setUserData("user.activities.role.mapping", sb.toString());
			profile.setUserData("user.blogs.role.mapping", sb.toString());
			profile.setUserData("user.communities.role.mapping", sb.toString());
			profile.setUserData("user.ccm.role.mapping", sb.toString());
			profile.setUserData("user.dogear.role.mapping", sb.toString());
			profile.setUserData("user.forums.role.mapping", sb.toString());
			profile.setUserData("user.metrics.role.mapping", sb.toString());
			profile.setUserData("user.wikis.role.mapping", sb.toString());
			profile.setUserData("user.mobile.role.mapping", sb.toString());
			profile.setUserData("user.moderation.role.mapping", sb.toString());
		}
	}
	
	private void setDataForNextForbidden() {
		//setErrorMessage(null);
		nextEnabled = false;
		setPageComplete(false);
	}
	
	private void verifyCompleteNone() {
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_USER_MAPPING_PANEL;
	}

	private boolean isCreate = false;
	
	private int flag = 0;

	@Override
	public void setVisible(boolean visible) {
		log.info("UserMappingPanel - visible:" + visible + " isCreate:" + isCreate + " flag:"+flag);
		if (!isCreate) {
			createControl(parent);
			isCreate = true;
			return;
		}
		if(flag ++ >= 1 && visible)
			this.verifyCompleteNone();
	}
}

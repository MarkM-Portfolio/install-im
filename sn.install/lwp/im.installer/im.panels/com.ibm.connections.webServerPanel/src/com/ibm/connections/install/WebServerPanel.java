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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.cic.agent.core.api.IAgent;
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.agent.ui.api.IAgentUI;

public class WebServerPanel extends BaseConfigCustomPanel {

	String className = WebServerPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section applicationServerSection = null;

	private Button doNowButton = null;
	private Button doLaterButton = null;
	
	private Combo doNowCombo = null;
	
	private Composite doNowButtonComposite;
	private Composite doLaterButtonComposite;
	private List<WebServer> webServers;
	
	private boolean isRecordingMode = false;
	GridData gd = new GridData();
	
	public WebServerPanel() {
		super(Messages.WEB_SERVER);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		profile = getProfile();
		isRecordingMode = agent.isSkipInstall();
		log.info("Web Server Panel :: Entered");
		
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
			applicationServerSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR|SWT.WRAP);
			//applicationServerSection = toolkit.createSection(form.getBody(), Section.NO_TITLE);

			//applicationServerSection.setSize(2000, 10);
			applicationServerSection.setText(Messages.WEB_SERVER_SELECTION);

			final Composite applicationServerSelectContainer = toolkit.createComposite(applicationServerSection);
			GridLayout applicationServerSelectLayout = new GridLayout();
			//applicationServerSelectLayout.numColumns = 2;
			applicationServerSelectContainer.setLayout(applicationServerSelectLayout);
//			GridData gd = new GridData();
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			applicationServerSelectContainer.setLayoutData(gd);

			//web server title
			/*Label desLabel = new Label(applicationServerSelectContainer, SWT.WRAP);
			desLabel.setText(Messages.WEB_SERVER_TITLE);
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint=630;
			desLabel.setLayoutData(gd);*/
			
			this.doLaterButton = new Button(applicationServerSelectContainer, SWT.RADIO);
			this.doLaterButton.setBackground(applicationServerSelectContainer.getBackground());
			this.doLaterButton.setSelection(true);
			this.doLaterButton.setText(Messages.WEB_SERVER_DO_LATER);
			
			this.doNowButton = new Button(applicationServerSelectContainer, SWT.RADIO);
			this.doNowButton.setBackground(applicationServerSelectContainer.getBackground());
			this.doNowButton.setSelection(false);
			this.doNowButton.setText(Messages.WEB_SERVER_DO_NOW);
			
			final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			applicationServerSelectStackContainer.setLayout(stackLayout);

			doNowButtonComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
			doLaterButtonComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

			stackLayout.topControl = doLaterButtonComposite;
			applicationServerSelectStackContainer.layout();

			applicationServerSection.setClient(applicationServerSelectContainer);
			
			doNowButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (doNowButton.getSelection() == true) {
						doNowButton.setSelection(true);
						doLaterButton.setSelection(false);
						stackLayout.topControl = doNowButtonComposite;
						applicationServerSelectStackContainer.layout();
						
						log.info("web server selection index : " + doNowCombo.getSelectionIndex());
						
						if(webServers == null){
							webServers = new WebServerHelper().parseIhsTxt();
							for(int i=0;i<webServers.size();i++){
								WebServer webServer = webServers.get(i);
								doNowCombo.add(webServer.getName()+","+webServer.getNode());
							}
						}
						
						checkWebServerExists();
						
						if(doNowCombo.getSelectionIndex() >=0 )
							setDataForNext();
						else
							setDataForNextForbidden();
						
					}
				}
			});
			
			doLaterButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (doLaterButton.getSelection() == true) {
						doNowButton.setSelection(false);
						doLaterButton.setSelection(true);
						stackLayout.topControl = doLaterButtonComposite;
						applicationServerSelectStackContainer.layout();
						
						if(profile == null) profile = getProfile();

						if(profile.getUserData("user.web.server.name") != null && !"".equals(profile.getUserData("user.web.server.name")))
							profile.setUserData("user.web.server.name", "");
						if(profile.getUserData("user.web.server.node") != null && !"".equals(profile.getUserData("user.web.server.node")))
							profile.setUserData("user.web.server.node", "");
					
						setDataForNext();
					}
				}
			});
			
			//enableDoNowButtonComposite
			doNowButtonComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			doNowButtonComposite.setLayoutData(gd);
			createDoNowSettingSection(doNowButtonComposite);
			//enableDoLaterButtonComposite
			doLaterButtonComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			doLaterButtonComposite.setLayoutData(gd);
			createDoLaterSettingSection(doLaterButtonComposite);
			
			form.pack();
			setControl(container);
			nextEnabled = false;
			setPageComplete(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createDoLaterSettingSection(Composite doLaterButtonComposite) {
		Section doLaterSection = toolkit.createSection(doLaterButtonComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		doLaterSection.setLayoutData(gridData);
		doLaterSection.setText(Messages.WEB_SERVER_INFOMATION_TITLE);
		
		Composite doLaterContainer = toolkit.createComposite(doLaterSection);
		GridLayout doNowLayout = new GridLayout();
		doNowLayout.numColumns = 2;
		doLaterContainer.setLayout(doNowLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		doLaterContainer.setLayoutData(gd);

		Label webServerDesLabel = new Label(doLaterContainer, SWT.WRAP);
		webServerDesLabel.setText(Messages.WEB_SERVER_INFOMATION);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint=650;
		webServerDesLabel.setLayoutData(gd);
		webServerDesLabel.pack();;
		
		doLaterSection.setClient(doLaterContainer);
	}

	public void createDoNowSettingSection(Composite doNowButtonComposite) {
		
		Section doNowSection = toolkit.createSection(doNowButtonComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		doNowSection.setLayoutData(gridData);
		doNowSection.setText(Messages.WEB_SERVER);

		Composite doNowContainer = toolkit.createComposite(doNowSection);
		GridLayout doNowLayout = new GridLayout();
		doNowLayout.numColumns = 2;
		doNowContainer.setLayout(doNowLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		doNowContainer.setLayoutData(gd);

		Label webServerDesLabel = new Label(doNowContainer, SWT.NONE);
		webServerDesLabel.setText(Messages.WEB_SERVER_EXISTED_SELECTION);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		webServerDesLabel.setLayoutData(gd);
		
		new Label(doNowContainer, SWT.NONE).setText(Messages.WEB_SERVER_NAME);
		this.doNowCombo = new Combo(doNowContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData textgridData = new GridData(GridData.BEGINNING);
		textgridData.horizontalSpan = 1;
		textgridData.widthHint = 300;
		textgridData.verticalIndent = 10;
		//this.doNowCombo.setText("LCCluster");
		//this.doNowCombo.add("webserver1,win2001Node01");
		//this.doNowCombo.add("webserver2,win2001Node02");
		//this.doNowCombo.add("webserver3,win2001Node03");
		
		this.doNowCombo.setLayoutData(textgridData);
		
		doNowSection.setClient(doNowContainer);

		doNowCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setUpWebServerInfo();
			}
		});
		
	}
	
	private void checkWebServerExists(){
		if(this.doNowCombo.getItemCount() <= 0)
			setErrorMessage(Messages.NO_WEB_SERVER_DETECTED);
	}
	
	private void setUpWebServerInfo() {
		String webServerItem = doNowCombo.getText();
		if(webServerItem != null && webServerItem.contains(",")){
			String[] webServerItemArray = webServerItem.split(",");
			profile.setUserData("user.web.server.name", webServerItemArray[0]);
			profile.setUserData("user.web.server.node", webServerItemArray[1]);
			setDataForNext();
		}
	}
	
	private void setDataForNextForbidden() {
		//setErrorMessage(null);
		nextEnabled = false;
		setPageComplete(false);
	}
	
	private void setDataForNext() {
		setErrorMessage(null);
		nextEnabled = true;
		setPageComplete(true);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_WEB_SERVER_PANEL;
	}

	private boolean isCreate = false;
	
	private int flag = 0;
	@Override
	public void setVisible(boolean visible) {
		log.info("WebServerPanel - visible:" + visible + " isCreate:" + isCreate + " flag:"+flag);
		if (!isCreate) {
			createControl(parent);
			isCreate = true;
		}
		if(flag ++ >= 1 && visible)
			this.setDataForNext();
		// createControl(parent);
	}
}

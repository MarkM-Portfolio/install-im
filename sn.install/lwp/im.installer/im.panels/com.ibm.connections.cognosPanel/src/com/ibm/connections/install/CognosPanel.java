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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import com.ibm.cic.agent.core.api.IAgent;
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.agent.ui.api.IAgentUI;

public class CognosPanel extends BaseConfigCustomPanel {
	String className = CognosPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section applicationServerSection = null;
	private Section cognosBIServerInformationSection = null;

	private Text hostnameText = null;
	private Text hostnameText2 = null;
	private Combo nodesList = null;
	private Combo serverList = null;

	private Button setCognosLaterButton = null;
	
	private Map<String, String> nodeHostnameMap = new HashMap<String, String>();
	private Map<String, List<String>> nodeServernameMap = new HashMap<String, List<String>>();
	private InstallValidator iv = new InstallValidator();

	private Combo cboServer = null;

	private Text cognosUseridText = null;
	private Text cognosPasswordText = null;
	private Text cognosContextRootText = null;
	private String cognosPort = null;
	private Button loadButton = null;
	private Button verifyButton = null;

	private boolean isRecordingMode = false;

	public CognosPanel() {
		super(Messages.COGNOS_CONFIGURATION);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		isRecordingMode = agent.isSkipInstall();
		log.info("Cognos Configuration Panel :: Entered");
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

			//applicationServerSection.setSize(2000, 10);
			applicationServerSection.setText(Messages.COGNOS_SET_TITLE);

			final Composite applicationServerSelectContainer = toolkit.createComposite(applicationServerSection);
			GridLayout applicationServerSelectLayout = new GridLayout();
			//applicationServerSelectLayout.numColumns = 2;
			applicationServerSelectContainer.setLayout(applicationServerSelectLayout);
			GridData gd = new GridData();
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			applicationServerSelectContainer.setLayoutData(gd);
			/*
			Label desLabel = new Label(applicationServerSelectContainer, SWT.WRAP);
			desLabel.setText(Messages.COGNOS_SET_TITLE);
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint=630;
			desLabel.setLayoutData(gd);
			*/
			
			this.setCognosLaterButton = new Button(applicationServerSelectContainer, SWT.CHECK);
			this.setCognosLaterButton.setBackground(applicationServerSelectContainer.getBackground());
			this.setCognosLaterButton.setText(Messages.COGNOS_SET_LATER);

			final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			applicationServerSelectStackContainer.setLayout(stackLayout);

			final Composite setCognosLaterComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

			stackLayout.topControl = setCognosLaterComposite;
			applicationServerSelectStackContainer.layout();

			applicationServerSection.setClient(applicationServerSelectContainer);
			
			setCognosLaterButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (setCognosLaterButton.getSelection() == true) {
						stackLayout.topControl = setCognosLaterComposite;
						applicationServerSelectStackContainer.layout();
					}
					setDataForLaterCognosConfig();
				}
			});
			
			//enableNotificationComposite
			setCognosLaterComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			setCognosLaterComposite.setLayoutData(gd);
			createCognosSettingSection(setCognosLaterComposite);
			
			form.pack();
			setControl(container);
			nextEnabled = false;
			setPageComplete(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createCognosSettingSection(Composite composite) {
		Section SNSection = toolkit.createSection(composite,
				Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		SNSection.setLayoutData(gridData);
		SNSection.setText(Messages.COGNOS_INFORMATION);
  
		Composite SNContainer = toolkit.createComposite(SNSection);
		GridLayout SNLayout = new GridLayout();
		SNContainer.setLayout(SNLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		SNContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		// inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 148;
		GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);
		// inputgridDataForLabel.horizontalSpan = 2;
		inputgridDataForLabel.widthHint = 150;
		
		Label cgInfoLabel = new Label(SNContainer, SWT.WRAP);
		cgInfoLabel.setText(Messages.COGNOS_DO_LATER_INFO);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint=630;
		cgInfoLabel.setLayoutData(gd);
		cgInfoLabel.pack();

		SNSection.setClient(SNContainer);
	}


	private void setDataForLaterCognosConfig() {
		setErrorMessage(null);
		nextEnabled = true;
		setPageComplete(true);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_COGNOS_PANEL;
	}

	private boolean isCreate = false;

	@Override
	public void setVisible(boolean visible) {

		if (!isCreate) {
			createControl(parent);
			isCreate = true;
		}

		// createControl(parent);
	}
}

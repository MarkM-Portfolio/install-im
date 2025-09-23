/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
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

public class FilesViewerPanel extends BaseConfigCustomPanel {
	String className = FilesViewerPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section applicationServerSection = null;
	private Section filesViewerBIServerInformationSection = null;

	private Text hostnameText = null;
	private Text hostnameText2 = null;
	private Combo nodesList = null;
	private Combo serverList = null;

	private Button setFilesViewerLaterButton = null;
	
	private Map<String, String> nodeHostnameMap = new HashMap<String, String>();
	private Map<String, List<String>> nodeServernameMap = new HashMap<String, List<String>>();
	private InstallValidator iv = new InstallValidator();

	private Combo cboServer = null;

	private Text filesViewerUseridText = null;
	private Text filesViewerPasswordText = null;
	private Text filesViewerContextRootText = null;
	private String filesViewerPort = null;
	private Button loadButton = null;
	private Button verifyButton = null;

	private boolean isRecordingMode = false;

	public FilesViewerPanel() {
		super(Messages.FileViewerPanelName);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		isRecordingMode = agent.isSkipInstall();
		log.info("Files Viewer Configuration Panel :: Entered");
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
			applicationServerSection.setText(Messages.FILE_VIEWER_SET_TITLE);

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
			desLabel.setText(Messages.FILE_VIEWER_SET_TITLE);
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint=630;
			desLabel.setLayoutData(gd);
			*/
			
			this.setFilesViewerLaterButton = new Button(applicationServerSelectContainer, SWT.CHECK);
			this.setFilesViewerLaterButton.setBackground(applicationServerSelectContainer.getBackground());
			this.setFilesViewerLaterButton.setText(Messages.COGNOS_SET_LATER);

			final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			applicationServerSelectStackContainer.setLayout(stackLayout);

			final Composite setFilesViewerLaterComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

			stackLayout.topControl = setFilesViewerLaterComposite;
			applicationServerSelectStackContainer.layout();

			applicationServerSection.setClient(applicationServerSelectContainer);
			
			setFilesViewerLaterButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (setFilesViewerLaterButton.getSelection() == true) {
						stackLayout.topControl = setFilesViewerLaterComposite;
						applicationServerSelectStackContainer.layout();
					}
					setDataForLaterFilesViewerConfig();
				}
			});
			
			//enableNotificationComposite
			setFilesViewerLaterComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			setFilesViewerLaterComposite.setLayoutData(gd);
			createFilesViewerSettingSection(setFilesViewerLaterComposite);
			
			form.pack();
			setControl(container);
			nextEnabled = false;
			setPageComplete(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createFilesViewerSettingSection(Composite composite) {
		Section SNSection = toolkit.createSection(composite,
				Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		SNSection.setLayoutData(gridData);
		SNSection.setText(Messages.FILE_VIEWER_INFORMATION);
  
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
		
		Label fvInfoLabel = new Label(SNContainer, SWT.WRAP);
		fvInfoLabel.setText(Messages.FILE_VIEWER_DO_LATER_INFO);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint=630;
		fvInfoLabel.setLayoutData(gd);
		fvInfoLabel.pack();
		
		SNSection.setClient(SNContainer);
	}


	private void setDataForLaterFilesViewerConfig() {
		setErrorMessage(null);
		nextEnabled = true;
		setPageComplete(true);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_FILES_VIEWER_PANEL;
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

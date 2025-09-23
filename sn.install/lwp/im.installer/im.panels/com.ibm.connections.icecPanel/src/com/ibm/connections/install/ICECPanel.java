/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2018                                    */
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

public class ICECPanel extends BaseConfigCustomPanel {

	String className = ICECPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section icecSection = null;
	
	private Composite icecComposite;

	private boolean isRecordingMode = false;
	
	private GridData gd = new GridData();
	
	public ICECPanel() {
		super(Messages.ICEC);
	}

	@Override
	public void createControl(Composite parent) {
		IAgent agent = (IAgent) getInitializationData()
				.getAdapter(IAgent.class);
		profile = getProfile();
		isRecordingMode = agent.isSkipInstall();
		log.info("ICEC Panel :: Entered");
		
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

			icecSection = toolkit.createSection(form.getBody(), Section.NO_TITLE);

			final Composite icecContainer = toolkit.createComposite(icecSection);
			GridLayout icecLayout = new GridLayout();
			icecContainer.setLayout(icecLayout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			//gd.horizontalIndent = 5;
			icecContainer.setLayoutData(gd);
	
			final Composite icecStackContainer = new Composite(form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			icecStackContainer.setLayout(stackLayout);

			icecComposite = new Composite(icecStackContainer, SWT.NONE);

			icecStackContainer.layout();

			icecSection.setClient(icecContainer);

			stackLayout.topControl = icecComposite;
			icecStackContainer.layout();
			
			icecComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			icecComposite.setLayoutData(gd);
			createICECSection(icecComposite);
			
			form.pack();
			setControl(container);
			nextEnabled = true;
			setPageComplete(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createICECSection(Composite seticecComposite) {
		Section icecWarningSection = toolkit.createSection(seticecComposite,Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		icecWarningSection.setLayoutData(gridData);
//		icecWarningSection.setText(Messages.ICEC_WARNING_MSG);
		
		Composite icecContainer = toolkit.createComposite(icecWarningSection);
		GridLayout userLayout = new GridLayout();
		icecContainer.setLayout(userLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		icecContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 350;
		GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);
		inputgridDataForLabel.widthHint = 150;
		icecWarningSection.setClient(icecContainer);
		
		Label icecInfoLabel = new Label(icecContainer, SWT.WRAP);
		icecInfoLabel.setText(Messages.ICEC_WARNING_MSG);
		gd = new GridData();
		gd.horizontalSpan = 10;
		gd.widthHint=630;
		icecInfoLabel.setLayoutData(gd);
		icecInfoLabel.pack();
	}
	
	private void verifyCompleteNone() {
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_ICEC_INFO_PANEL;
	}

	private boolean isCreate = false;
	
	private int flag = 0;

	@Override
	public void setVisible(boolean visible) {
		log.info("ICECPanel - visible:" + visible + " isCreate:" + isCreate + " flag:"+flag);
		if (!isCreate) {
			createControl(parent);
			isCreate = true;
			return;
		}
		if(flag ++ >= 1 && visible)
			this.verifyCompleteNone();
	}
}

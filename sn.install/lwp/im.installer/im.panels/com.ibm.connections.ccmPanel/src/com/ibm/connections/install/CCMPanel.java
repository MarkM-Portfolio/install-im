/* ***************************************************************** */
/*                                                                   */
/* IBM                                                               */
/*                                                                   */
/* Licensed Material                                                 */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
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
public class CCMPanel extends BaseConfigCustomPanel {
	public static final String className = CCMPanel.class.getName();
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;
	private Composite parent = null;

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private Section applicationServerSection = null;

	private Text urlText = null;
	private Text urlHttpsText = null;

	private Button newDeploymentButton = null;
	private Button existingDeploymentButton = null;

	private Text ccmUseridText = null;
	private Text ccmPasswordText = null;
	private Text ccmAnonymousUser = null;
	private Text ccmAnonymousPassword = null;
	private Text fnInstallersPathText = null;
	private Button browseFnInstallersPathBtn = null;
	private Button verifyButtonExisting = null;
	private Button verifyButtonNew = null;
	private Label desLabel = null;
	private Label promptLabel = null;
	private Label existingValidateLabel = null;
	private Label installersLocationLabel = null;

	private boolean isRecordingMode = false;
	
	//all declared cpe installers
	private Properties fnInstallers = null;
	private Text baseCPEText;
	private Text CPEFixpackText;
	private Text CPEFixpackClientText;
	private Text baseICNText;
	private Text ICNFixpackText;
	
	//From Line 136 to 163,modify the value "5.2.1.5-P8CPE-*-FP005.*" to "5.2.1.7-P8CPE-*-FP007.*"
	//and "5.2.1.5-P8CPE-CLIENT-*-FP005.*" to "5.2.1.7-P8CPE-CLIENT-*-FP007.*"
	//make the 6.0 CR1 installation package to support FNCE5.2.1.7
	//Please refer to LC RTC 193304.
	//Base version is required for FNCE and ICN, FNCE Client allows direct installation without a base.
	protected final static String CE_AIX_INSTALLER = "5.2.1-P8CPE-AIX.BIN";
	protected final static String CE_FP_AIX_INSTALLER = "5.2.1.7-P8CPE-AIX-FP007.BIN";
	protected final static String FNCS_AIX_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3-AIX.bin";
	protected final static String FNCS_FP_AIX_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-AIX.bin";
	protected final static String CE_CLIENT_AIX_INSTALLER = "5.2.1.7-P8CPE-CLIENT-AIX-FP007.BIN";
	//protected final static String CE_CLIENT_FP_AIX_INSTALLER = "5.2.1.2-P8CPE-CLIENT-AIX-FP002.BIN";
	
	protected final static String CE_LINUX_INSTALLER = "5.2.1-P8CPE-LINUX.BIN";
	protected final static String CE_FP_LINUX_INSTALLER = "5.2.1.7-P8CPE-LINUX-FP007.BIN";
	protected final static String FNCS_LINUX_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3-LINUX.bin";
	protected final static String FNCS_FP_LINUX_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-LINUX.bin";
	protected final static String CE_CLIENT_LINUX_INSTALLER = "5.2.1.7-P8CPE-CLIENT-LINUX-FP007.BIN";
	//protected final static String CE_CLIENT_FP_LINUX_INSTALLER = "5.2.1.2-P8CPE-CLIENT-LINUX-FP002.BIN";
	
	protected final static String CE_ZLINUX_INSTALLER = "5.2.1-P8CPE-ZLINUX.BIN";
	protected final static String CE_FP_ZLINUX_INSTALLER = "5.2.1.7-P8CPE-ZLINUX-FP007.BIN";
	protected final static String FNCS_ZLINUX_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3-zLINUX.bin";
	protected final static String FNCS_FP_ZLINUX_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-zLINUX.bin";
	protected final static String CE_CLIENT_ZLINUX_INSTALLER = "5.2.1.7-P8CPE-CLIENT-ZLINUX-FP007.BIN";
	//protected final static String CE_CLIENT_FP_ZLINUX_INSTALLER = "5.2.1.2-P8CPE-CLIENT-ZLINUX-FP002.BIN";
	
	protected final static String CE_WINDOWS_INSTALLER = "5.2.1-P8CPE-WIN.EXE";
	protected final static String CE_FP_WINDOWS_INSTALLER = "5.2.1.7-P8CPE-WIN-FP007.EXE";
	protected final static String FNCS_WINDOWS_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3-WIN.exe";
	protected final static String FNCS_FP_WINDOWS_INSTALLER = "IBM_CONTENT_NAVIGATOR-2.0.3.8-FP008-WIN.exe";
	protected final static String CE_CLIENT_WINDOWS_INSTALLER = "5.2.1.7-P8CPE-CLIENT-WIN-FP007.EXE";
	//protected final static String CE_CLIENT_FP_WINDOWS_INSTALLER = "5.2.1.2-P8CPE-CLIENT-WIN-FP002.EXE";
    
	public final static String TINS_LINUXSETUP_RHEL7 = "https://www-01.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.installation.nd.doc/ae/tins_linuxsetup_rhel7.html";
	
    /**
     * Default constructor
     */
    public CCMPanel() {
        super(Messages.CCMPanelName); //NON-NLS-1
		fnInstallers = new Properties();
		setFNInstallerProps(fnInstallers);
    }

	protected static void setFNInstallerProps(Properties props) {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch"); 
		os = os.toLowerCase();
		arch = arch.toLowerCase();
		if (os.indexOf("win") != -1) {
			props.setProperty("ccm.ce.installer", CE_WINDOWS_INSTALLER);
			props.setProperty("ccm.ce.fp.installer", CE_FP_WINDOWS_INSTALLER);
			props.setProperty("ccm.fncs.installer", FNCS_WINDOWS_INSTALLER);
			props.setProperty("ccm.fncs.fp.installer", FNCS_FP_WINDOWS_INSTALLER);
			props.setProperty("ccm.ceclient.installer", CE_CLIENT_WINDOWS_INSTALLER);
			//props.setProperty("ccm.ceclient.fp.installer", CE_CLIENT_FP_WINDOWS_INSTALLER);
		} else if (os.indexOf("aix") != -1) {
			props.setProperty("ccm.ce.installer", CE_AIX_INSTALLER);
			props.setProperty("ccm.ce.fp.installer", CE_FP_AIX_INSTALLER);
			props.setProperty("ccm.fncs.installer", FNCS_AIX_INSTALLER);
			props.setProperty("ccm.fncs.fp.installer", FNCS_FP_AIX_INSTALLER);
			props.setProperty("ccm.ceclient.installer", CE_CLIENT_AIX_INSTALLER);
			//props.setProperty("ccm.ceclient.fp.installer", CE_CLIENT_FP_AIX_INSTALLER);
		} else if(arch.indexOf("s390") != -1) {
			props.setProperty("ccm.ce.installer", CE_ZLINUX_INSTALLER);
			props.setProperty("ccm.ce.fp.installer", CE_FP_ZLINUX_INSTALLER);
			props.setProperty("ccm.fncs.installer", FNCS_ZLINUX_INSTALLER);
			props.setProperty("ccm.fncs.fp.installer", FNCS_FP_ZLINUX_INSTALLER);
			props.setProperty("ccm.ceclient.installer", CE_CLIENT_ZLINUX_INSTALLER);
			//props.setProperty("ccm.ceclient.fp.installer", CE_CLIENT_FP_ZLINUX_INSTALLER);
		} else {
			props.setProperty("ccm.ce.installer", CE_LINUX_INSTALLER);
			props.setProperty("ccm.ce.fp.installer", CE_FP_LINUX_INSTALLER);
			props.setProperty("ccm.fncs.installer", FNCS_LINUX_INSTALLER);
			props.setProperty("ccm.fncs.fp.installer", FNCS_FP_LINUX_INSTALLER);
			props.setProperty("ccm.ceclient.installer", CE_CLIENT_LINUX_INSTALLER);
			//props.setProperty("ccm.ceclient.fp.installer", CE_CLIENT_FP_LINUX_INSTALLER);
		}
	}
	
	@Override
	public void createControl(Composite parent) {
		log.info(CCMPanel.className +": Creating panel");
		IAgent agent = (IAgent) getInitializationData().getAdapter(IAgent.class);
		isRecordingMode = agent.isSkipInstall();
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

			// Description section
			Section ccmTitleSection = toolkit.createSection(form.getBody(),
					Section.TITLE_BAR | SWT.WRAP);
			ccmTitleSection.setText(Messages.CCM);
			final Composite ccmTitleContainer = toolkit
					.createComposite(ccmTitleSection);
			GridLayout applicationServerSelectLayout = new GridLayout();
			// applicationServerSelectLayout.numColumns = 2;
			ccmTitleContainer.setLayout(applicationServerSelectLayout);
			GridData gd = new GridData();
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			ccmTitleContainer.setLayoutData(gd);

			desLabel = new Label(ccmTitleContainer, SWT.WRAP);
			desLabel.setText(Messages.CCM_DES);
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 630;
			desLabel.setLayoutData(gd);
			ccmTitleContainer.setLayoutData(gd);

			ccmTitleSection.setClient(ccmTitleContainer);
			
			// Select Application Server Type
			applicationServerSection = toolkit.createSection(form.getBody(),
					Section.TITLE_BAR | SWT.WRAP);

			// applicationServerSection.setSize(2000, 10);
			applicationServerSection.setText(Messages.CCM_OPTION_DES);

			final Composite applicationServerSelectContainer = toolkit
					.createComposite(applicationServerSection);
			applicationServerSelectContainer
					.setLayout(applicationServerSelectLayout);
			gd = new GridData();
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			applicationServerSelectContainer.setLayoutData(gd);

			Label promptPart1 = new Label(applicationServerSelectContainer, SWT.NONE);
			promptPart1.setText(Messages.CCM_PROMPT_PART_1);
			
			Composite descContainer = toolkit.createComposite(applicationServerSelectContainer);
			descContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			descContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			Label promptPart2 = new Label(descContainer, SWT.NONE);
			promptPart2.setText(Messages.CCM_PROMPT_PART_2);
			
			Hyperlink link = toolkit.createHyperlink(descContainer, Messages.CCM_PROMPT_DOCUMENTATION,SWT.NONE);
		
			Label promptPart3 = new Label(applicationServerSelectContainer, SWT.NONE);
			promptPart3.setText(Messages.CCM_PROMPT_PART_3);
			
			Label promptPart4 = new Label(applicationServerSelectContainer, SWT.NONE);
			promptPart4.setText(Messages.CCM_PROMPT_PART_4);
			promptPart4.setLayoutData(new GridData(630, 30));
			
			link.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					if (java.awt.Desktop.isDesktopSupported()) {
						try {
							java.net.URI uri = java.net.URI.create(Messages.CCM_DOCUMENTATION_URL);
							java.awt.Desktop dp = java.awt.Desktop.getDesktop();
							if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
								dp.browse(uri);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			
			this.newDeploymentButton = new Button(
					applicationServerSelectContainer, SWT.RADIO);
			this.newDeploymentButton
					.setBackground(applicationServerSelectContainer
							.getBackground());
			this.newDeploymentButton.setSelection(false);
			this.newDeploymentButton
					.setText(Messages.CCM_OPTION_NEW_DEPLOYMENT);

			this.existingDeploymentButton = new Button(
					applicationServerSelectContainer, SWT.RADIO);
			this.existingDeploymentButton
					.setBackground(applicationServerSelectContainer
							.getBackground());
			this.existingDeploymentButton
					.setText(Messages.CCM_OPTION_EXISTING_DEPLOYMENT);

			final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
			final StackLayout stackLayout = new StackLayout();
			applicationServerSelectStackContainer.setLayout(stackLayout);

			final Composite newDeploymentComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
			final Composite existingDeploymentComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

			//stackLayout.topControl = existingDeploymentButton;
			applicationServerSelectStackContainer.layout();

			applicationServerSection.setClient(applicationServerSelectContainer);

			newDeploymentButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (newDeploymentButton.getSelection() == true) {
						if(profile == null)profile = getProfile();
						profile.setUserData("user.ccm.install.now", "true");
						existingDeploymentButton.setSelection(false);
						stackLayout.topControl = newDeploymentComposite;
						applicationServerSelectStackContainer.layout();
					}
				}
			});

			existingDeploymentButton
					.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (existingDeploymentButton.getSelection() == true) {
								if(profile == null)profile = getProfile();
								profile.setUserData("user.ccm.install.now", "false");
								newDeploymentButton.setSelection(false);
								stackLayout.topControl = existingDeploymentComposite;
								applicationServerSelectStackContainer.layout();
							}
						}
					});

			// newDeploymentComposite
			newDeploymentComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			newDeploymentComposite.setLayoutData(gd);
			createNewDeploymentSection(newDeploymentComposite);

			// existingDeploymentComposite
			existingDeploymentComposite.setLayout(new GridLayout());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			existingDeploymentComposite.setLayoutData(gd);
			createExistingDeploymentSection(existingDeploymentComposite);

			form.pack();
			setControl(container);
			nextEnabled = false;
			setPageComplete(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createNewDeploymentSection(final Composite composite) {
		profile = getProfile();
		
		//Anonymous User Account Settings:
				
		Section AnonymousSection = toolkit.createSection(composite, Section.TITLE_BAR);
		GridData anonymous_gd = new GridData(GridData.FILL_HORIZONTAL);
		AnonymousSection.setLayoutData(anonymous_gd);
		AnonymousSection.setText(Messages.CCM_ANONYMOUS_DES);
				
		final Composite AnonymousContainer = toolkit.createComposite(AnonymousSection);
		GridLayout anonymousLayout = new GridLayout();
		AnonymousContainer.setLayout(anonymousLayout);
		AnonymousContainer.setLayoutData(anonymous_gd);
		
		GridData anonymous_gridata = new GridData(GridData.BEGINNING);
		anonymous_gridata.widthHint = 248;
		GridData anonymous_gridata_label = new GridData(GridData.BEGINNING);
		anonymous_gridata_label.widthHint = 430;
		
		GridData anonymous_des_gd = new GridData();
		anonymous_des_gd.horizontalSpan = 2;
		anonymous_des_gd.widthHint = 630;
		
		
		Label anonymous_des = new Label(AnonymousContainer, SWT.WRAP);
		anonymous_des.setText(Messages.CCM_ANONYMOUS_DES_LABEL);
		anonymous_des.setLayoutData(anonymous_des_gd);
		AnonymousContainer.setLayoutData(anonymous_des_gd);
		
		new Label(AnonymousContainer, SWT.NONE).setText(Messages.CCM_ANONYMOUS_USER_LABEL);

		this.ccmAnonymousUser = new Text(AnonymousContainer, SWT.BORDER | SWT.SINGLE);
		this.ccmAnonymousUser.setLayoutData(anonymous_gridata);
		this.ccmAnonymousUser.setText("");
	
		new Label(AnonymousContainer, SWT.NONE)
				.setText(Messages.CCM_ANONYMOUS_PASSWORD_LABEL);
		this.ccmAnonymousPassword = new Text(AnonymousContainer, SWT.BORDER
				| SWT.SINGLE | SWT.PASSWORD);
		this.ccmAnonymousPassword.setLayoutData(anonymous_gridata);
		
		AnonymousSection.setClient(AnonymousContainer);

		Section SNSection = toolkit.createSection(composite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		SNSection.setLayoutData(gridData);
		SNSection.setText(Messages.CCM_NEW_DEPLOYMENT_INSTALLERS_LOCATION);

		final Composite SNContainer = toolkit.createComposite(SNSection);
		GridLayout SNLayout = new GridLayout();
		SNLayout.numColumns = 2;
		SNContainer.setLayout(SNLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		SNContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 248;
		GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);
		inputgridDataForLabel.widthHint = 430;

		installersLocationLabel = new Label(SNContainer, SWT.NONE);
		//installersLocationLabel.setText(Messages.bind(Messages.CCM_NEW_DEPLOYMENT_NOTE, setExamples(fnInstallers)));
		installersLocationLabel.setText(Messages.CCM_NEW_DEPLOYMENT_NOTE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.widthHint = 248;
		installersLocationLabel.setLayoutData(gd);

		fnInstallersPathText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		GridData fnInstallersPathGd = new GridData(GridData.BEGINNING);
		fnInstallersPathGd.widthHint = 370;
		fnInstallersPathText.setLayoutData(fnInstallersPathGd);
		fnInstallersPathText.setText("");

		//fnInstallersPathText.setEditable(false);
		SNContainer.setLayoutData(gd);
			
		browseFnInstallersPathBtn = new Button(SNContainer, SWT.NONE);
		browseFnInstallersPathBtn.setText(Messages.DB_BTN_Browse);
		browseFnInstallersPathBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog( SNContainer.getShell() );
				if (fnInstallersPathText.getText() != null) {
					dialog.setFilterPath(fnInstallersPathText.getText());
				}
				dialog.setMessage(Messages.CCM_NEW_DEPLOYMENT_INSTALLERS_LOCATION);
				String dir = dialog.open();
				if (dir != null) {
					fnInstallersPathText.setText(dir);
				}
			}
		});
		
		//add modification note for ccm installers
		Label modifyNoteLabel = new Label(SNContainer, SWT.WRAP);
		modifyNoteLabel.setText(Messages.CCM_NEW_DEPLOYMENT_MODIFY_NOTE);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint = 630;
		modifyNoteLabel.setLayoutData(gd);
		
		//add restore defaults button 
		Button restoreButton  = new Button(SNContainer, SWT.NONE); 
		restoreButton.setText(Messages.DEPOLOGY_RESET_BTN);
		restoreButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// make all the labels below restore button to default values
				baseCPEText.setText(fnInstallers.getProperty("ccm.ce.installer"));
				CPEFixpackText.setText(fnInstallers.getProperty("ccm.ce.fp.installer"));
				CPEFixpackClientText.setText(fnInstallers.getProperty("ccm.ceclient.installer"));
				baseICNText.setText(fnInstallers.getProperty("ccm.fncs.installer"));
				ICNFixpackText.setText(fnInstallers.getProperty("ccm.fncs.fp.installer"));
			}
		});
		
		// common config for CPE
		GridData cpeLabelGd = new GridData(GridData.FILL_HORIZONTAL);
		cpeLabelGd.horizontalSpan = 2;
		cpeLabelGd.widthHint = 300;
		GridData cpeTextGd = new GridData(GridData.BEGINNING);
		cpeTextGd.widthHint = 370;
		
		//1. add base CPE
		Label baseCPELabel = new Label(SNContainer, SWT.NONE);
		baseCPELabel.setText(Messages.CCM_BASE_CPE);
		baseCPELabel.setLayoutData(cpeLabelGd);

		baseCPEText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		baseCPEText.setLayoutData(cpeTextGd);
		baseCPEText.setText(fnInstallers.getProperty("ccm.ce.installer"));
		baseCPEText.setEditable(false);
		
		//2. CPE fixpack
		Label CPEFixpackLabel = new Label(SNContainer, SWT.NONE);
		CPEFixpackLabel.setText(Messages.CCM_CPE_FIXPACK);
		CPEFixpackLabel.setLayoutData(cpeLabelGd);

		CPEFixpackText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		CPEFixpackText.setLayoutData(cpeTextGd);
		CPEFixpackText.setText(fnInstallers.getProperty("ccm.ce.fp.installer"));
		
		//3.CPE fixpack client
		Label CPEFixpackClientLabel = new Label(SNContainer, SWT.NONE);
		CPEFixpackClientLabel.setText(Messages.CCM_CPE_FIXPACK_CLIENT);
		CPEFixpackClientLabel.setLayoutData(cpeLabelGd);

		CPEFixpackClientText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		CPEFixpackClientText.setLayoutData(cpeTextGd);
		CPEFixpackClientText.setText(fnInstallers.getProperty("ccm.ceclient.installer"));
		
		//4. base ICN 
		Label baseICNLabel = new Label(SNContainer, SWT.NONE);
		baseICNLabel.setText(Messages.CCM_BASE_ICN);
		baseICNLabel.setLayoutData(cpeLabelGd);

		baseICNText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		baseICNText.setLayoutData(cpeTextGd);
		baseICNText.setText(fnInstallers.getProperty("ccm.fncs.installer"));
		baseICNText.setEditable(false);
		
		//5.ICN fixpack
		Label ICNFixpackLabel = new Label(SNContainer, SWT.NONE);
		ICNFixpackLabel.setText(Messages.CCM_ICN_FIXPACK);
		ICNFixpackLabel.setLayoutData(cpeLabelGd);

		ICNFixpackText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		ICNFixpackText.setLayoutData(cpeTextGd);
		ICNFixpackText.setText(fnInstallers.getProperty("ccm.fncs.fp.installer"));

		// add bottom label
		Label noteLabel = new Label(SNContainer, SWT.WRAP);
		noteLabel.setText(Messages.CCM_NEW_DEPLOYMENT_POST_INSTALL_NOTE);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint = 630;
		noteLabel.setLayoutData(gd);

		GridData gridDataButtonSize = new GridData();
		gridDataButtonSize.widthHint = 200;
		gridDataButtonSize.heightHint = 30;

		verifyButtonNew = new Button(SNContainer, SWT.PUSH);
		verifyButtonNew.setLayoutData(gridDataButtonSize);
		if (isRecordingMode)
			verifyButtonNew.setText(Messages.SKIP_VALIDATION);
		else
			verifyButtonNew.setText(Messages.VALIDATE);

		verifyButtonNew.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				if (!isRecordingMode) {
					verifyNewDeployment();
				} else {
					nextEnabled = true;
					setPageComplete(true);
					log.info(CCMPanel.className +": skipping validation");
					
					String installersLocationPath = fnInstallersPathText.getText();
					String anonymous_user = ccmAnonymousUser.getText();
					String anonymous_pass = ccmAnonymousPassword.getText();	
					installersLocationPath = installersLocationPath.replaceAll("\\\\", "/");
					
					log.info(CCMPanel.className +": FileNet installers location = "+ installersLocationPath);

					profile = getProfile();
					
					profile.setUserData("user.ccm.existingDeployment", "false");
					profile.setUserData("user.ccm.installers.path", installersLocationPath);
					profile.setUserData("user.ccm.anonymous.user", anonymous_user == null ? "" : anonymous_user.trim());
					profile.setUserData("user.ccm.anonymous.password", anonymous_pass == null ? "" : EncryptionUtils.encrypt(Util.xor(anonymous_pass.trim())));
					
					profile.setUserData("user.ccm.ce.installer", baseCPEText.getText());
					profile.setUserData("user.ccm.ce.fp.installer", CPEFixpackText.getText());
					profile.setUserData("user.ccm.ceclient.installer", CPEFixpackClientText.getText());
					profile.setUserData("user.ccm.fncs.installer", baseICNText.getText());
					profile.setUserData("user.ccm.fncs.fp.installer", ICNFixpackText.getText());
				}
			}
		});

		SNSection.setClient(SNContainer);
	}
	
	protected static String setExamples(Properties props) {
		StringBuffer buff = new StringBuffer();
		buff.append("\n");
		buff.append(props.getProperty("ccm.ce.installer"));
		buff.append("; ");
		buff.append(props.getProperty("ccm.ce.fp.installer"));		
		buff.append("; ");
		buff.append(props.getProperty("ccm.fncs.installer"));
		buff.append(";\n");
		buff.append(props.getProperty("ccm.fncs.fp.installer"));		
		buff.append("; ");
		buff.append(props.getProperty("ccm.ceclient.installer"));
		//buff.append("; ");
		//buff.append(props.getProperty("ccm.ceclient.fp.installer"));
		
		return buff.toString();
	}
	
	class InstallerFilter implements FilenameFilter {
		private String installerName;
		
		public InstallerFilter(String installerName) {
			this.installerName = installerName;
		}
		public boolean accept(File dir, String name) {
			return name.equals(installerName) ? true : false;
		}
	}
	
	private boolean isAllFNInstallersExist(String pathToSearch, Properties fnInstallers, StringBuffer errMsg) {
		boolean installersExist = true;
		if (pathToSearch == null || pathToSearch.equals("")) {
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE);
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE_FP);
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_FNCS);
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_FNCS_FP);
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE_CLIENT);
			//log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE_CLIENT_FP);
			errMsg.append(Messages.FAIL_TO_FIND_CE+" ");
			errMsg.append("\n"+Messages.FAIL_TO_FIND_CE_FP+" ");
			errMsg.append("\n"+Messages.FAIL_TO_FIND_FNCS+" ");
			errMsg.append("\n"+Messages.FAIL_TO_FIND_FNCS_FP+" ");
			errMsg.append("\n"+Messages.FAIL_TO_FIND_CE_CLIENT);
			//errMsg.append("\n"+Messages.FAIL_TO_FIND_CE_CLIENT_FP);
			return false;
		}
		File dir = new File(pathToSearch.trim());
		File[] files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ce.installer")));
		if (files == null || files.length == 0) {
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE);
			errMsg.append(Messages.FAIL_TO_FIND_CE+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ce.fp.installer")));
		if (files == null || files.length == 0) {
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE_FP);
			errMsg.append((installersExist ? "" : "\n") + Messages.FAIL_TO_FIND_CE_FP+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.fncs.installer")));
		if (files == null || files.length == 0) {
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_FNCS);
			errMsg.append((installersExist ? "" : "\n") + Messages.FAIL_TO_FIND_FNCS+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.fncs.fp.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_FNCS_FP);
			errMsg.append((installersExist ? "" : "\n") + Messages.FAIL_TO_FIND_FNCS_FP+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ceclient.installer")));
		if (files == null || files.length == 0) {
			log.error(CCMPanel.className +": "+ Messages.FAIL_TO_FIND_CE_CLIENT);
			errMsg.append((installersExist ? "" : "\n") + Messages.FAIL_TO_FIND_CE_CLIENT+" ");
			installersExist = false;
		}
		/*
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ceclient.fp.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_CE_CLIENT_FP);
			errMsg.append((installersExist ? "" : "\n") + Messages.FAIL_TO_FIND_CE_CLIENT_FP+" ");
			installersExist = false;
		}*/
		return installersExist;
	}
			
	private void verifyNewDeployment() {
		
		String params = profile.getData("ccmParams");
		String[] arrParams = params.split("\\|");
		
		log.info("arrParams size : "+ arrParams.length );
		
		WASProgressMonitor dialog = new WASProgressMonitor(arrParams[0], arrParams[1],
				arrParams[2], arrParams[3], arrParams[4], arrParams[5], arrParams[6],
				arrParams[7]);
		try {
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(parent.getShell());
			pmd.open();
			Shell shell = pmd.getShell();
			shell.setText(Messages.PROGRESS_INFORMATION);
			shell.update();
			pmd.run(true, true, dialog);
		} catch (Exception e) {
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
		
		//verify Node Agents
		String firstNodeName = profile.getUserData("user.ccm.firstNodeName");
		String secondNodeName = profile.getUserData("user.ccm.secondaryNodesNames");
		//String nodeAgents = profile.getUserData("user.nodeAgentList");
		
		log.info("CCMPanel verifyNodeAgent - firstNodeName: " + firstNodeName + " secondNodeName: "+secondNodeName);
		log.info("CCMPanel verifyNodeAgent - nodeAgents : " + nodeAgentsStr);
		boolean started = isNodeAgentsStarted(nodeAgentsStr);
		log.info("CCMPanel verifyNodeAgent - started : " + started);
		
		if (!started) {
			setErrorMessage(Messages.bind(Messages.WAS_NODEAGENT_UNSTARTED_ERROR, unstartedNodesAgents.toString()));
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
		
		String anonymous_user = ccmAnonymousUser.getText();
		String anonymous_pass = ccmAnonymousPassword.getText();	
		
		// Check if IATEMPDIR is set; use it if set, else use system temp folder
		String tmpdirPath = System.getenv("IATEMPDIR");
		if (tmpdirPath == null) {
			tmpdirPath = System.getProperty("java.io.tmpdir");
		}
		
		// Skip anonymous validation if no values given
		if (anonymous_pass != null && !anonymous_pass.trim().equals("") && !verifyUserNameComplete(anonymous_user)){
			this.profile.setUserData("user.ccm.existingDeployment", "false");
			return;
		}
		if (anonymous_user != null && !anonymous_user.trim().equals("") && !verifyPasswordComplete(anonymous_pass)){
			this.profile.setUserData("user.ccm.existingDeployment", "false");
			return;
		}
		
		StringBuffer errMsg = new StringBuffer();
		boolean isFailedVerification = false;
		if (profile != null) {
			String ccmAdmin = profile.getUserData("user.ccm.adminuser.id");
			if (ccmAdmin == null) ccmAdmin = "";
			if (anonymous_user != null && !anonymous_user.trim().equals("") && anonymous_user.trim().equals(ccmAdmin)) {
				log.error(CCMPanel.className +": "+ Messages.CCM_ADMIN_CANNOT_BE_ANON_USER);
				setErrorMessage(Messages.CCM_ADMIN_CANNOT_BE_ANON_USER);
				nextEnabled = false;
				setPageComplete(false);
				return;
			}
		}
		
		String thePath = fnInstallersPathText.getText();
		log.info(CCMPanel.className +": Searching for installers in: "+ thePath);
		
		if (thePath != null && !thePath.equals("")) {
			// Check for override properties file
			File fnOverrideFile = new File(thePath, CCMConstants.FN_INSTALLERS_OVERRIDE_PROPS);
			if (fnOverrideFile.exists()) {
				Properties fnOverrideProps = new Properties();
				InputStream input = null;
				try {
					input = new FileInputStream(fnOverrideFile);
					fnOverrideProps.load(input);
					if (fnOverrideProps.getProperty("ccm.ce.installer") != null && !fnOverrideProps.getProperty("ccm.ce.installer").trim().equals("")) {
						fnInstallers.setProperty("ccm.ce.installer", fnOverrideProps.getProperty("ccm.ce.installer"));
					}
					if (fnOverrideProps.getProperty("ccm.ce.fp.installer") != null && !fnOverrideProps.getProperty("ccm.ce.fp.installer").trim().equals("")) {
						fnInstallers.setProperty("ccm.ce.fp.installer", fnOverrideProps.getProperty("ccm.ce.fp.installer"));
					}
					if (fnOverrideProps.getProperty("ccm.fncs.installer") != null && !fnOverrideProps.getProperty("ccm.fncs.installer").trim().equals("")) {
						fnInstallers.setProperty("ccm.fncs.installer", fnOverrideProps.getProperty("ccm.fncs.installer"));
					}
					if (fnOverrideProps.getProperty("ccm.fncs.fp.installer") != null && !fnOverrideProps.getProperty("ccm.fncs.fp.installer").trim().equals("")) {
						fnInstallers.setProperty("ccm.fncs.fp.installer", fnOverrideProps.getProperty("ccm.fncs.fp.installer"));
					}
					if (fnOverrideProps.getProperty("ccm.ceclient.installer") != null && ! fnOverrideProps.getProperty("ccm.ceclient.installer").trim().equals("")) {
						fnInstallers.setProperty("ccm.ceclient.installer", fnOverrideProps.getProperty("ccm.ceclient.installer"));
					}
					/*
					if (fnOverrideProps.getProperty("ccm.ceclient.fp.installer") != null && ! fnOverrideProps.getProperty("ccm.ceclient.fp.installer").trim().equals("")) {
						fnInstallers.setProperty("ccm.ceclient.fp.installer", fnOverrideProps.getProperty("ccm.ceclient.fp.installer"));
					}
					*/
					// Update panel text to show files from override properties file
					installersLocationLabel.setText(Messages.bind(Messages.CCM_NEW_DEPLOYMENT_NOTE, setExamples(fnInstallers)));
				} catch (IOException ex) {
					log.error(CCMPanel.className +": "+ ex);
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							log.error(CCMPanel.className +": "+ e);
						}
					}
				}
			}
		}
		
		//get the user inputted cpe installers
		Properties inputtedInstallers = new Properties();
		inputtedInstallers.put("ccm.ce.installer", baseCPEText.getText());
		inputtedInstallers.put("ccm.ce.fp.installer", CPEFixpackText.getText());
		inputtedInstallers.put("ccm.ceclient.installer", CPEFixpackClientText.getText());
		inputtedInstallers.put("ccm.fncs.installer", baseICNText.getText());
		inputtedInstallers.put("ccm.fncs.fp.installer", ICNFixpackText.getText());
		
		isFailedVerification = !isAllFNInstallersExist(thePath, inputtedInstallers, errMsg);
		
		File tmpdir = new File(tmpdirPath);
		try {
			long usableSpace = tmpdir.getUsableSpace();
			if (usableSpace < 6442450944L) { // Need 6GB temp disk space
				log.warning(CCMPanel.className +": "+ Messages.INSUFFICIENT_TMP_DISK_SPACE);
				errMsg.append("\n"+Messages.INSUFFICIENT_TMP_DISK_SPACE);
				isFailedVerification = true;
			}
		} catch (SecurityException e) {
			log.error(CCMPanel.className +": "+ Messages.UNABLE_TO_ACCESS_TMP_DIR);
			errMsg.append("\n"+Messages.UNABLE_TO_ACCESS_TMP_DIR);
			isFailedVerification = true;
		}
		
		if (isFailedVerification) {
			setErrorMessage(errMsg.toString());
			nextEnabled = false;
			setPageComplete(false);
			return;
		}
		
		showValidationSuccessMessageDialog(Messages.VALIDATION_SUCCESSFUL);

		verifyButtonNew.setText(Messages.VALIDATED);
		
		log.info(CCMPanel.className +": user.ccm.installers.path:"+ thePath.trim().replaceAll("\\\\", "/"));
		log.info(CCMPanel.className +": user.ccm.anonymous.user"+ anonymous_user);
					
		setErrorMessage(null);
		profile = getProfile();
		this.profile.setUserData("user.ccm.existingDeployment", "false");
		this.profile.setUserData("user.ccm.installers.original.path", thePath.trim());
		this.profile.setUserData("user.ccm.installers.path", thePath.trim().replaceAll("\\\\", "/"));
		this.profile.setUserData("user.ccm.anonymous.user", anonymous_user == null ? "" : anonymous_user.trim());
		this.profile.setUserData("user.ccm.anonymous.password", anonymous_pass == null ? "" : EncryptionUtils.encrypt(Util.xor(anonymous_pass.trim())));
		
		this.profile.setUserData("user.ccm.ce.installer", inputtedInstallers.getProperty("ccm.ce.installer"));
		this.profile.setUserData("user.ccm.ce.fp.installer", inputtedInstallers.getProperty("ccm.ce.fp.installer"));
		this.profile.setUserData("user.ccm.ceclient.installer", inputtedInstallers.getProperty("ccm.ceclient.installer"));
		this.profile.setUserData("user.ccm.fncs.installer", inputtedInstallers.getProperty("ccm.fncs.installer"));
		this.profile.setUserData("user.ccm.fncs.fp.installer", inputtedInstallers.getProperty("ccm.fncs.fp.installer"));
		
		nextEnabled = true;
		setPageComplete(true);
		log.info(CCMPanel.className +": validation successful");
	}

	private Map nodeAgentList = new HashMap();
	private String nodeAgentsStr = "";

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
			monitor.beginTask(Messages.WAS_DETECT_NODE_AGENTS, 10);

			monitor.worked(1);

			monitor.worked(1);

			monitor.worked(1);

			monitor.worked(1);
			
			//check Redhat 7 if supports 32bit app.
			Linux32AppSupportCheck check = new Linux32AppSupportCheck();
			if(!check.linux32AppSupport(getAndCopyLCscriptPath("check.linux.32"))){
				log.error(Messages.CCM_32BIT_RUNTIME_SUPPORT_FAIL);
				sb.append(Messages.CCM_32BIT_RUNTIME_SUPPORT_FAIL).append(" ").append(TINS_LINUXSETUP_RHEL7);
				monitor.worked(6);
				monitor.done();
				return;
			}
			
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

			monitor.setTaskName(Messages.WAS_DETECT_NODE_AGENTS);
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

			nodeAgentList = (Map) DMValidator.detectNodeAgents();

			nodeAgentsStr = parseNodeServerMapToString(nodeAgentList);

			log.info("CCMPanel nodeAgentsStr:: " + nodeAgentsStr);
			
			monitor.worked(1);
			
			monitor.done();
		}
	};

	private static final String LC_SCRIPT_Name = "LCInstallScript";
	private static final String MISSING_SCRIPT = "Script_Missing";
	
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
	
	private StringBuffer unstartedNodesAgents = new StringBuffer();
	
	private boolean isNodeAgentsStarted(String nodeAgents){
		boolean started = true;
		unstartedNodesAgents = new StringBuffer();
		if(nodeAgents == null) return false;
		String[] nodeAgentsPairs = clearEmptyValues(nodeAgents.split(";"));
		for(String s: nodeAgentsPairs){
			String[] pair = s.split(":");
			if(!Boolean.valueOf(pair[1])){
				unstartedNodesAgents.append("["+pair[0]+"] ");
				started = false;
			}
		}
		return started;
	}
	
	private String[] clearEmptyValues(String[] array){
		if(array == null) return null;
		if(array.length == 0 ) return array;
		ArrayList<String> list = new ArrayList<String>();
		for(String s:array){
			if(s != null && !"".equals(s))
				list.add(s);
		}
		return list.toArray(new String[0]);
	}
	

	private void createExistingDeploymentSection(Composite composite) {
		Section SNSection = toolkit.createSection(composite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		SNSection.setLayoutData(gridData);
		SNSection.setText(Messages.CCM_EXISTING_DEPLOYMENT_Credential);

		Composite SNContainer = toolkit.createComposite(SNSection);
		GridLayout SNLayout = new GridLayout();
		SNContainer.setLayout(SNLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		SNContainer.setLayoutData(gd);

		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 248;
		GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);
		inputgridDataForLabel.widthHint = 430;

		new Label(SNContainer, SWT.NONE)
				.setText(Messages.CCM_EXISTING_DEPLOYMENT_USER_ID);

		this.ccmUseridText = new Text(SNContainer, SWT.BORDER | SWT.SINGLE);
		this.ccmUseridText.setLayoutData(inputgridData);
		this.ccmUseridText.setText("");

		this.ccmUseridText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyUserNameComplete(ccmUseridText.getText());
			}
		});

		new Label(SNContainer, SWT.NONE)
				.setText(Messages.CCM_EXISTING_DEPLOYMENT_PASSW0RD);
		this.ccmPasswordText = new Text(SNContainer, SWT.BORDER
				| SWT.SINGLE | SWT.PASSWORD);
		this.ccmPasswordText.setLayoutData(inputgridData);
		this.ccmPasswordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyPasswordComplete(ccmPasswordText.getText());
			}
		});

		SNSection.setClient(SNContainer);

		final Composite compositeSN = new Composite(composite, SWT.NONE);

		gridData = new GridData(GridData.BEGINNING);
		gridData.horizontalSpan = 2;

		// Component for SN
		compositeSN.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.verticalIndent = 10;
		compositeSN.setLayoutData(gd);

		Section ccmURLSection = toolkit.createSection(composite,
				Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		ccmURLSection.setLayoutData(gridData);
		ccmURLSection.setText(Messages.CCM_EXISTING_DEPLOYMENT);

		Composite ccmURLContainer = toolkit
				.createComposite(ccmURLSection);
		GridLayout nodeSelectLayout = new GridLayout();
		// nodeSelectLayout.numColumns = 2;
		ccmURLContainer.setLayout(nodeSelectLayout);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		// gd2.verticalIndent = 10;
		// gd2.horizontalIndent = 5;
		ccmURLContainer.setLayoutData(gd2);

		Label urlLabel = new Label(ccmURLContainer, SWT.NONE);
		urlLabel.setText(Messages.CCM_EXISTING_DEPLOYMENT_URL);
		inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 248;
		urlLabel.setLayoutData(gd);

		urlText = new Text(ccmURLContainer, SWT.BORDER | SWT.SINGLE);
		urlText.setLayoutData(inputgridData);
		urlText.setEnabled(true);
		urlText.setVisible(true);
		urlText.setText("http://");

		Label exampleLabel = new Label(ccmURLContainer, SWT.NONE);
		exampleLabel.setText(Messages.CCM_EXISTING_DEPLOYMENT_EXAMPLE);
		exampleLabel.setEnabled(false);
		inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 248;
		exampleLabel.setLayoutData(gd);

		Label urlHttpsLabel = new Label(ccmURLContainer, SWT.NONE);
		urlHttpsLabel.setText(Messages.CCM_EXISTING_DEPLOYMENT_URL_HTTPS);
		inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 248;
		urlHttpsLabel.setLayoutData(gd);

		urlHttpsText = new Text(ccmURLContainer, SWT.BORDER | SWT.SINGLE);
		urlHttpsText.setLayoutData(inputgridData);
		urlHttpsText.setEnabled(true);
		urlHttpsText.setVisible(true);
		urlHttpsText.setText("https://");

		Label exampleHttpsLabel = new Label(ccmURLContainer, SWT.NONE);
		exampleHttpsLabel.setText(Messages.CCM_EXISTING_DEPLOYMENT_HTTPS_EXAMPLE);
		exampleHttpsLabel.setEnabled(false);
		inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.widthHint = 248;
		exampleHttpsLabel.setLayoutData(gd);

		GridData gridDataButtonSize = new GridData();
		gridDataButtonSize.widthHint = 200;
		gridDataButtonSize.heightHint = 30;

		verifyButtonExisting = new Button(ccmURLContainer, SWT.PUSH);
		verifyButtonExisting.setLayoutData(gridDataButtonSize);
		if (isRecordingMode)
			verifyButtonExisting.setText(Messages.SKIP_VALIDATION);
		else
			verifyButtonExisting.setText(Messages.VALIDATE);

		verifyButtonExisting.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent event) {
				if (!isRecordingMode) {
					verifyExistingDeployment();
				} else {
					nextEnabled = true;
					setPageComplete(true);
					log.info(CCMPanel.className +": skipping validation");
					
					String userName = ccmUseridText.getText();
					String password = ccmPasswordText.getText();
					String url = urlText.getText();
					String urlHttps = urlHttpsText.getText();
					
					log.info(CCMPanel.className +": CCM userName = " + userName);
					log.info(CCMPanel.className +": CCM url = " + url);
					log.info(CCMPanel.className +": CCM url (https) = " + urlHttps);
					
					StringBuffer filenetAdmin = new StringBuffer();
					filenetAdmin.append("\"filenetAdmin\": \"").append(userName.trim());
					filenetAdmin.append("\",");
					
					StringBuffer filenetPwd = new StringBuffer();
					filenetPwd.append("\"filenetAdminPassword\": \"").append(EncryptionUtils.encrypt(password.trim()));
					filenetPwd.append("\",");

					profile = getProfile();
					
					profile.setUserData("user.ccm.existingDeployment", "true");
					profile.setUserData("user.ccm.userName", filenetAdmin.toString());
					profile.setUserData("user.ccm.password", filenetPwd.toString());					
					profile.setUserData("user.ccm.url", removeDM(url.trim()));
					profile.setUserData("user.ccm.url.https", removeDM(urlHttps.trim()));
				}
			}
		});

		existingValidateLabel = new Label(ccmURLContainer, SWT.NONE|SWT.WRAP);
		existingValidateLabel.setText(Messages.CCM_EXISTING_DEPLOYMENT_VALIDATE_DES);
		existingValidateLabel.setEnabled(false);
		//inputgridData = new GridData(GridData.BEGINNING);
		gd = new GridData();
		gd.widthHint = 600;
		existingValidateLabel.setLayoutData(gd);

		ccmURLSection.setClient(ccmURLContainer);
	}

	private void verifyExistingDeployment() {
		String userName = ccmUseridText.getText();
		String password = ccmPasswordText.getText();
		String url = urlText.getText();
		String urlHttps = urlHttpsText.getText();
		profile = getProfile();
		
		if (!verifyUserNameComplete(userName)){
			this.profile.setUserData("user.ccm.existingDeployment", "true");
			return;
		}
		if (!verifyPasswordComplete(password)){
			this.profile.setUserData("user.ccm.existingDeployment", "true");
			return;
		}
		// Enabling next button per 
		// https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/86359
		nextEnabled = true;
		setPageComplete(true);
		
		StringBuffer filenetAdmin = new StringBuffer();
		filenetAdmin.append("\"filenetAdmin\": \"").append(userName.trim());
		filenetAdmin.append("\",");
		
		StringBuffer filenetPwd = new StringBuffer();
		filenetPwd.append("\"filenetAdminPassword\": \"").append(password.trim());
		filenetPwd.append("\",");
		
		if (!verifyHTTPURLComplete(url, userName, password)){
			this.profile.setUserData("user.ccm.existingDeployment", "true");
			this.profile.setUserData("user.ccm.userName", filenetAdmin.toString());
			this.profile.setUserData("user.ccm.password", filenetPwd.toString());
			this.profile.setUserData("user.ccm.url", removeDM(removeLastSlash(url.trim())));
			this.profile.setUserData("user.ccm.url.https", removeDM(removeLastSlash(urlHttps.trim())));
			return;
		}
		if (!verifyHTTPSURLComplete(urlHttps, userName, password)){
			this.profile.setUserData("user.ccm.existingDeployment", "true");
			this.profile.setUserData("user.ccm.userName", filenetAdmin.toString());
			this.profile.setUserData("user.ccm.password", filenetPwd.toString());
			this.profile.setUserData("user.ccm.url", removeDM(removeLastSlash(url.trim())));
			this.profile.setUserData("user.ccm.url.https", removeDM(removeLastSlash(urlHttps.trim())));
			return;
		}
		
		this.profile.setUserData("user.ccm.existingDeployment", "true");
		this.profile.setUserData("user.ccm.userName", filenetAdmin.toString());
		this.profile.setUserData("user.ccm.password", filenetPwd.toString());
		this.profile.setUserData("user.ccm.url", removeDM(removeLastSlash(url.trim())));
		this.profile.setUserData("user.ccm.url.https", removeDM(removeLastSlash(urlHttps.trim())));
		
		if(isOnlyModifyAddExistingCCM())
			showValidationSuccessMessageDialog(Messages.CCM_EXISTING_DEPLOYMENT_SKIP_TOPOLOGY_DATABASE_PANEL);
		else if(isOnlyModifyAddExistingCCMAndModeration()) {
			showValidationSuccessMessageDialog(Messages.CCM_EXISTING_DEPLOYMENT_SKIP_DATABASE_PANEL);
		} else {
			showValidationSuccessMessageDialog(Messages.VALIDATION_SUCCESSFUL);
			verifyButtonExisting.setText(Messages.VALIDATED);
		}
		
		setErrorMessage(null);
		setMessage("", IMessageProvider.NONE);
		nextEnabled = true;
		setPageComplete(true);
		log.info(CCMPanel.className +": validation successful");
	}

	private String removeLastSlash(String url) {
		String realURL = null;
		if (url.lastIndexOf("/") == url.length() - 1){
			realURL = url.substring(0, url.lastIndexOf("/"));
		}else{
			realURL = url;
		}
		log.info(CCMPanel.className +": After removed last slash the CCM URL is " + realURL);
		return realURL;
	}
	
	private String removeDM(String url) {
		String realURL = null;
		if (url.lastIndexOf("/dm") == url.length() - 3){
			realURL = url.substring(0, url.lastIndexOf("/dm"));
		}else{
			realURL = url;
		}
		log.info(CCMPanel.className +": After removed last slash the CCM URL is " + realURL);
		return realURL;
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_CCM_PANEL;
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

	private boolean verifyUserNameComplete(String userid) {
		if (userid == null || userid.length() == 0) {
			setErrorMessage(Messages.warning_user_empty);
			setPageComplete(false);
			return false;
		} else {
			setErrorMessage(null);
		}
		return true;
	}

	private boolean verifyPasswordComplete(String dmuserpw) {
		if (dmuserpw == null || dmuserpw.length() == 0) {
			setErrorMessage(Messages.warning_password_empty);
			setPageComplete(false);
			return false;
		} else {
			setErrorMessage(null);
		}
		return true;
	}
	
	private boolean verifyHTTPSURLComplete(String url, String username, String password){
		HttpURLConnection connection = null;
		try {
			String version = null;
			if (url == null || !url.startsWith("http")) {
				log.error(CCMPanel.className +": Invalid CCM url: " + url);
				setMessage(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_CONNECTION_INVALID_HTTPS_URL_MSG, url), IMessageProvider.WARNING);
				return false;
			}
			username = (username == null ? "" : username.trim());
			password = (password == null ? "" : password.trim());
			
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
					}
				}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			connection = (HttpsURLConnection) new URL(url.trim()).openConnection();
			
			String userPass = username+":"+password;
			byte[] encodedBytes = Base64.encodeBase64(userPass.getBytes());
			connection.setRequestProperty("Authorization", "Basic "+new String(encodedBytes));
			connection.setRequestProperty("Accept-Language", "en-US,en;");
			connection.connect();
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error(CCMPanel.className +": Failed to connect to CCM server: " + url);
				log.error(CCMPanel.className +": HTTP response code: " + connection.getResponseCode());
				
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
					setMessage(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_UNAUTHORIZED_ERROR_MSG, IMessageProvider.WARNING);
				}
				if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					setMessage(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_FORBIDDEN_ERROR_MSG, username), IMessageProvider.WARNING);
				} else {
					setMessage(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTPS_RESPONSE_ERROR_MSG, responseCode), IMessageProvider.WARNING);
				}
				return false;
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains("Product version")) {
						version = (inputLine.replaceAll("<tr><td>Product version</td><td>v", "")).replaceAll("</tr>", "");
					}
				}
				in.close();

				if (!isVersionOk(version, "2.0.0.0")) {
					log.error(CCMPanel.className +": Collaboration Services version 2.0.0 required to install and use CCM.");
					setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_VERSION_ERROR_MSG, IMessageProvider.WARNING);
					return false;
				}

				return true;
			}
		} catch (MalformedURLException e) {
			log.error(CCMPanel.className +": Failed to connect to CCM server - malformed URL: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		} catch (IOException e) {
			log.error(CCMPanel.className +": Failed to connect to CCM server - IO exception: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		} catch (GeneralSecurityException e) {
			log.error(CCMPanel.className +": Failed to connect to CCM server - General security exception: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		} catch (IllegalArgumentException e){
			log.error(CCMPanel.className +": Failed to connect to CCM server - Illegal argument: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		}
	}

	private boolean verifyHTTPURLComplete(String url, String username, String password) {
		HttpURLConnection connection = null;
		try {
			String version = null;
			if (url == null || !url.startsWith("http")) {
				log.error(CCMPanel.className +": Invalid CCM url: " + url);
				setMessage(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_CONNECTION_INVALID_HTTP_URL_MSG, url), IMessageProvider.WARNING);
				return false;
			}
			username = (username == null ? "" : username.trim());
			password = (password == null ? "" : password.trim());
			connection = (HttpURLConnection) new URL(url.trim()).openConnection();
			
			String userPass = username+":"+password;
			byte[] encodedBytes = Base64.encodeBase64(userPass.getBytes());
			connection.setRequestProperty("Authorization", "Basic "+new String(encodedBytes));
			connection.setRequestProperty("Accept-Language", "en-US,en;");
			connection.connect();
		
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error(CCMPanel.className +": Failed to connect to CCM server: " + url);
				log.error(CCMPanel.className +": HTTP response code: " + connection.getResponseCode());
				
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
					setMessage(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_UNAUTHORIZED_ERROR_MSG, IMessageProvider.WARNING);
				}
				if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					setMessage(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_FORBIDDEN_ERROR_MSG, username), IMessageProvider.WARNING);
				} else {
					setMessage(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_RESPONSE_ERROR_MSG, responseCode), IMessageProvider.WARNING);
				}
				return false;
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains("Product version")) {
						version = (inputLine.replaceAll("<tr><td>Product version</td><td>", "")).replaceAll("</tr>", "");
						log.info(CCMPanel.className +": Product Version = " + version);
					}
				}
				in.close();

				if (!isVersionOk(version, "2.0.0.0")) {
					log.error(CCMPanel.className +": Collaboration Services version 2.0.0 required to install and use CCM.");
					setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_VERSION_ERROR_MSG, IMessageProvider.WARNING);
					return false;
				}

				return true;
			}
		} catch (MalformedURLException e) {
			log.error(CCMPanel.className +": Failed to connect to CCM server - malformed URL: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		} catch (IOException e) {
			log.error(CCMPanel.className +": Failed to connect to CCM server - IO exception: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		} catch (IllegalArgumentException e){
			log.error(CCMPanel.className +": Failed to connect to CCM server - Illegal argument: " + url);
			log.error(CCMPanel.className +": "+ e.getLocalizedMessage());
			setMessage(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG, IMessageProvider.WARNING);
			return false;
		}
	}

	boolean isVersionOk(String version, String versionReq) {
		// versionReq needs to be in full a.b.c.d format
		if (version == null || version.equals("")){
			return false;
		}
		boolean isOk = true;
		String[] vInfo = version.split(".");
		String[] vInfoReq = versionReq.split(".");
		for (int x = 0; x < vInfo.length; x++) {
			if (Integer.parseInt(vInfo[x]) < Integer.parseInt(vInfoReq[x])) {
				log.debug(CCMPanel.className +": vInfo[x] = " + vInfo[x]);
				isOk = false;
				break;
			}
		}
		return isOk;
	}
}
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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.agent.ui.api.IAgentUI;

public class NotificationPanel extends BaseConfigCustomPanel {
	String className = NotificationPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	private Composite container = null;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Section applicationServerSection = null;
	private Composite parent = null;

	private Button mailDisableButton = null;
	private Button enableNotificationButton = null;
	private Button enableReplyToButton = null;
	private Button javaMailButton1 = null;
	private Button dnsMXButton1 = null;
	private Button javaMailButton2 = null;
	private Button dnsMXButton2 = null;

	private Text smtpHostNameTextJavaMail1 = null;
	private Text smtpUserIdTextJavaMail1 = null;
	private Text smtpUserPwTextJavaMail1 = null;
	private Button enableSSLButtonJavaMail1 = null;
	private Text portTextJavaMail1 = null;
	private Button enableAuthButtonJavaMail1 = null;
	
	private Text smtpHostNameTextJavaMail2 = null;
	private Text smtpUserIdTextJavaMail2 = null;
	private Text smtpUserPwTextJavaMail2 = null;
	private Button enableSSLButtonJavaMail2 = null;
	private Text portTextJavaMail2 = null;
	private Button enableAuthButtonJavaMail2 = null;
	
	private Text smtpDomaiNameTextDNS1 = null;
	private Text smtpUserIdTextDNS1 = null;
	private Text smtpUserPwTextDNS1 = null;
	private Button enableSSLButtonDNS1 = null;
	private Text portTextDNS1 = null;
	private Button enableAuthButtonDNS1 = null;
	private Button enableServerInfoButtonDNS1 = null;
	
	private Text smtpDomaiNameTextDNS2 = null;
	private Text smtpUserIdTextDNS2 = null;
	private Text smtpUserPwTextDNS2 = null;
	private Button enableSSLButtonDNS2 = null;
	private Text portTextDNS2 = null;
	private Button enableAuthButtonDNS2 = null;
	private Button enableServerInfoButtonDNS2 = null;
	
	private Text dnsServerHostText1 = null;
	private Text dnsServerPortText1 = null;
	private Text dnsServerHostText2 = null;
	private Text dnsServerPortText2 = null;
	
	private Text domainNameText1 = null;
	private Text localPartText1 = null;
	private Text mailFileServerText1 = null;
	private Text mailFileUserIDText1 = null;
	private Text mailFilePWText1 = null;
	private Button emailAddressNoneButton1 = null;
	private Button emailAddressPrefixButton1 = null;
	private Button emailAddressSuffixButton1 = null;
	
	private Text domainNameText2 = null;
	private Text localPartText2 = null;
	private Text mailFileServerText2 = null;
	private Text mailFileUserIDText2 = null;
	private Text mailFilePWText2 = null;
	private Button emailAddressNoneButton2 = null;
	private Button emailAddressPrefixButton2 = null;
	private Button emailAddressSuffixButton2 = null;
	
	InstallValidator installValidator = new InstallValidator();
	GridData inputgridData = new GridData(GridData.BEGINNING);
	GridData inputgridDataForLabel = new GridData(GridData.BEGINNING);

	public NotificationPanel() {
		super(Messages.Notification_TITLE);
	}

	@Override
	public void createControl(Composite parent) {
		this.parent = parent;
		log.info("Notification Panel :: Entered");
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
		applicationServerSection = toolkit.createSection(form.getBody(), Section.NO_TITLE);

		//applicationServerSection.setSize(2000, 10);
		applicationServerSection.setText("");

		final Composite applicationServerSelectContainer = toolkit.createComposite(applicationServerSection);
		GridLayout applicationServerSelectLayout = new GridLayout();
		//applicationServerSelectLayout.numColumns = 2;
		applicationServerSelectContainer.setLayout(applicationServerSelectLayout);
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		applicationServerSelectContainer.setLayoutData(gd);

		inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 270;
		
		inputgridDataForLabel.horizontalSpan = 2;
		//inputgridDataForLabel.widthHint = 420;

		Label desLabel = new Label(applicationServerSelectContainer, SWT.WRAP);
		desLabel.setText(Messages.Notification_DES);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint=630;
		desLabel.setLayoutData(gd);

		this.enableNotificationButton = new Button(applicationServerSelectContainer, SWT.RADIO);
		this.enableNotificationButton.setBackground(applicationServerSelectContainer.getBackground());
		this.enableNotificationButton.setSelection(true);
		this.enableNotificationButton.setText(Messages.Notification_ENABLE_NOTIFICATION_ONLY);
		
		this.enableReplyToButton = new Button(applicationServerSelectContainer, SWT.RADIO);
		this.enableReplyToButton.setBackground(applicationServerSelectContainer.getBackground());
		this.enableReplyToButton.setSelection(false);
		this.enableReplyToButton.setText(Messages.Notification_ENABLE_NOTIFICATION_AND_REPLYTO);
		
		this.mailDisableButton = new Button(applicationServerSelectContainer, SWT.RADIO);
		this.mailDisableButton.setBackground(applicationServerSelectContainer.getBackground());
		this.mailDisableButton.setText(Messages.Notification_NOT_ENABLED);

		final Composite applicationServerSelectStackContainer = new Composite(form.getBody(), SWT.NONE);
		final StackLayout stackLayout = new StackLayout();
		applicationServerSelectStackContainer.setLayout(stackLayout);

		final Composite enableNotificationComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
		final Composite enableReplyToComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);
		final Composite maiDisableComposite = new Composite(applicationServerSelectStackContainer, SWT.NONE);

		stackLayout.topControl = enableNotificationComposite;
		applicationServerSelectStackContainer.layout();

		applicationServerSection.setClient(applicationServerSelectContainer);

		enableNotificationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (enableNotificationButton.getSelection() == true) {
					enableReplyToButton.setSelection(false);
					mailDisableButton.setSelection(false);
					stackLayout.topControl = enableNotificationComposite;
					applicationServerSelectStackContainer.layout();
				}

				verify();

			}
		});

		enableReplyToButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (enableReplyToButton.getSelection() == true) {
					enableNotificationButton.setSelection(false);
					mailDisableButton.setSelection(false);
					stackLayout.topControl = enableReplyToComposite;
					applicationServerSelectStackContainer.layout();
				}
				
				verify();
					
			}
		});

		mailDisableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (mailDisableButton.getSelection() == true) {
					enableReplyToButton.setSelection(false);
					enableNotificationButton.setSelection(false);
					stackLayout.topControl = maiDisableComposite;
					applicationServerSelectStackContainer.layout();
				}
				verify();
			}
		});
		
		//enableNotificationComposite
		enableNotificationComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		enableNotificationComposite.setLayoutData(gd);
		createNotificationSection1(enableNotificationComposite);
		
		//enableReplyToComposite
		enableReplyToComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		enableReplyToComposite.setLayoutData(gd);
		createNotificationSection2(enableReplyToComposite);

		form.pack();
		setControl(container);
		nextEnabled = false;
		setPageComplete(false);
		
	}
	
	private void createNotificationSection1(Composite composite) {
		Section enableNotificationSection = toolkit.createSection(composite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		enableNotificationSection.setLayoutData(gridData);
		enableNotificationSection.setText(Messages.Notification_TITLE);

		Composite enableNotificationContainer = toolkit.createComposite(enableNotificationSection);
		GridLayout enableNotificationLayout = new GridLayout();
		enableNotificationLayout.numColumns = 2;
		enableNotificationContainer.setLayout(enableNotificationLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = 248;
		enableNotificationContainer.setLayoutData(gd);

		// ***************************************************
		
		Label chooseInfoLabel = new Label(enableNotificationContainer, SWT.WRAP);  
		chooseInfoLabel.setText(Messages.Notification_CHOOSE_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		chooseInfoLabel.setLayoutData(gd);
		
		this.javaMailButton1 = new Button(enableNotificationContainer, SWT.RADIO);
		this.javaMailButton1.setBackground(enableNotificationContainer.getBackground());
		this.javaMailButton1.setSelection(true);
		this.javaMailButton1.setText(Messages.Notification_WAS_JAVA_MAIL);
		this.javaMailButton1.setLayoutData(gd);
		this.dnsMXButton1 = new Button(enableNotificationContainer, SWT.RADIO);
		this.dnsMXButton1.setBackground(enableNotificationContainer.getBackground());
		this.dnsMXButton1.setText(Messages.Notification_DNS_INFO);
		this.dnsMXButton1.setLayoutData(gd);
		
		final Composite mailSelectStackContainer = new Composite(enableNotificationContainer, SWT.NONE);
		final StackLayout javaMailstackLayout = new StackLayout();
		mailSelectStackContainer.setLayout(javaMailstackLayout);

		final Composite javaMailComposite = new Composite(mailSelectStackContainer, SWT.NONE);
		final Composite dnsMXComposite = new Composite(mailSelectStackContainer, SWT.NONE);
		
		javaMailstackLayout.topControl = javaMailComposite;
		mailSelectStackContainer.layout();

		enableNotificationSection.setClient(enableNotificationContainer);
		
		javaMailButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (javaMailButton1.getSelection() == true) {
					dnsMXButton1.setSelection(false);
					javaMailstackLayout.topControl = javaMailComposite;
					mailSelectStackContainer.layout();
				}
				verify();
			}
		});

		dnsMXButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (dnsMXButton1.getSelection() == true) {
					javaMailButton1.setSelection(false);
					javaMailstackLayout.topControl = dnsMXComposite;
					mailSelectStackContainer.layout();
				}
				verify();
			}
		});
		
		//javaMailComposite
		javaMailComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		javaMailComposite.setLayoutData(gd);

		createJavaMailSection1(javaMailComposite);
		
		//dnsMXComposite
		dnsMXComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		dnsMXComposite.setLayoutData(gd);

		createDNSSection1(dnsMXComposite);
	}
	
	private void createNotificationSection2(Composite composite) {
		Section enableNotificationSection = toolkit.createSection(composite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		enableNotificationSection.setLayoutData(gridData);
		enableNotificationSection.setText(Messages.Notification_TITLE);

		Composite enableNotificationContainer = toolkit.createComposite(enableNotificationSection);
		GridLayout enableNotificationLayout = new GridLayout();
		enableNotificationLayout.numColumns = 2;
		enableNotificationContainer.setLayout(enableNotificationLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = 248;
		enableNotificationContainer.setLayoutData(gd);

		// ***************************************************
		
		Label chooseInfoLabel = new Label(enableNotificationContainer, SWT.WRAP);  
		chooseInfoLabel.setText(Messages.Notification_CHOOSE_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		chooseInfoLabel.setLayoutData(gd);
		
		this.javaMailButton2 = new Button(enableNotificationContainer, SWT.RADIO);
		this.javaMailButton2.setBackground(enableNotificationContainer.getBackground());
		this.javaMailButton2.setSelection(true);
		this.javaMailButton2.setText(Messages.Notification_WAS_JAVA_MAIL);
		this.javaMailButton2.setLayoutData(gd);
		this.dnsMXButton2 = new Button(enableNotificationContainer, SWT.RADIO);
		this.dnsMXButton2.setBackground(enableNotificationContainer.getBackground());
		this.dnsMXButton2.setText(Messages.Notification_DNS_INFO);
		this.dnsMXButton2.setLayoutData(gd);
		
		final Composite mailSelectStackContainer = new Composite(enableNotificationContainer, SWT.NONE);
		final StackLayout javaMailstackLayout = new StackLayout();
		mailSelectStackContainer.setLayout(javaMailstackLayout);

		final Composite javaMailComposite = new Composite(mailSelectStackContainer, SWT.NONE);
		final Composite dnsMXComposite = new Composite(mailSelectStackContainer, SWT.NONE);
		
		javaMailstackLayout.topControl = javaMailComposite;
		mailSelectStackContainer.layout();

		enableNotificationSection.setClient(enableNotificationContainer);
		
		javaMailButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (javaMailButton2.getSelection() == true) {
					dnsMXButton2.setSelection(false);
					javaMailstackLayout.topControl = javaMailComposite;
					mailSelectStackContainer.layout();
				}
				verify();
			}
		});

		dnsMXButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (dnsMXButton2.getSelection() == true) {
					javaMailButton2.setSelection(false);
					javaMailstackLayout.topControl = dnsMXComposite;
					mailSelectStackContainer.layout();
				}
				verify();
			}
		});
		
		//javaMailComposite
		javaMailComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		javaMailComposite.setLayoutData(gd);

		createJavaMailSection2(javaMailComposite);
		createReplyToSection1(javaMailComposite);
		
		//dnsMXComposite
		dnsMXComposite.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		dnsMXComposite.setLayoutData(gd);

		createDNSSection2(dnsMXComposite);
		createReplyToSection2(dnsMXComposite);
	}
	
	private void createDNSSection1(Composite dnsMXComposite) {
		Section dnsMXSection = toolkit.createSection(dnsMXComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		dnsMXSection.setLayoutData(gridData);
		dnsMXSection.setText(Messages.Notification_DNS_MX);

		Composite dnsMXContainer = toolkit.createComposite(dnsMXSection);
		GridLayout dnsMXLayout = new GridLayout();
		dnsMXLayout.numColumns = 2;
		dnsMXContainer.setLayout(dnsMXLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		dnsMXContainer.setLayoutData(gd);
		
		Label dnsMXDesLabel = new Label(dnsMXContainer, SWT.WRAP);
		dnsMXDesLabel
				.setText(Messages.Notification_DNS_MSG);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		dnsMXDesLabel.setLayoutData(gd);

		Label smtpHostNameLabel = new Label(dnsMXContainer, SWT.NONE);
		smtpHostNameLabel.setText(Messages.Notification_DNS_DOMAIN_NAME);
		smtpHostNameLabel.setLayoutData(inputgridData);
		this.smtpDomaiNameTextDNS1 = new Text(dnsMXContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpDomaiNameTextDNS1.setLayoutData(inputgridData); 
		this.smtpDomaiNameTextDNS1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.enableServerInfoButtonDNS1 = new Button(dnsMXContainer, SWT.CHECK);
		this.enableServerInfoButtonDNS1.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableServerInfoButtonDNS1.setLayoutData(gd);
		
		enableServerInfoButtonDNS1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableServerInfoButtonDNS1.getSelection() == true){
					dnsServerHostText1.setEnabled(true);
					dnsServerPortText1.setEnabled(true);
				}
				else{
					dnsServerHostText1.setEnabled(false);
					dnsServerPortText1.setEnabled(false);
					dnsServerHostText1.setText("");
					dnsServerPortText1.setText("");
				}
				verify();
			}
		});
		
		Label dnsServerCheckLabel = new Label(dnsMXContainer, SWT.LEFT);
		dnsServerCheckLabel.setText(Messages.Notification_SMTP_SPECIFY_DNS_SERVER_INFO);

		Label dnsServerLabel = new Label(dnsMXContainer, SWT.NONE);
		dnsServerLabel.setText(Messages.Notification_DNS_SERVER);
		dnsServerLabel.setLayoutData(inputgridDataForLabel);
		this.dnsServerHostText1 = new Text(dnsMXContainer, SWT.BORDER | SWT.SINGLE);
		this.dnsServerHostText1.setLayoutData(inputgridData);
		dnsServerHostText1.setEnabled(false);
		this.dnsServerHostText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label dnsPortLabel = new Label(dnsMXContainer, SWT.NONE);
		dnsPortLabel.setText(Messages.Notification_DNS_PORT);
		dnsPortLabel.setLayoutData(inputgridDataForLabel);
		this.dnsServerPortText1 = new Text(dnsMXContainer, SWT.BORDER | SWT.SINGLE);
		this.dnsServerPortText1.setText("53");
		dnsServerPortText1.setEnabled(false);
		this.dnsServerPortText1.setLayoutData(inputgridData);
		this.dnsServerPortText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});	

		dnsMXSection.setClient(dnsMXContainer);
		
		// for smtp server info in dns section
		Section dnsSMTPSection = toolkit.createSection(dnsMXComposite, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		dnsSMTPSection.setLayoutData(gridData);
		dnsSMTPSection.setText(Messages.Notification_JAVA_MAIL_TITLE);

		Composite dnsSMTPContainer = toolkit.createComposite(dnsSMTPSection);
		GridLayout dnsSMTPMXLayout = new GridLayout();
		dnsSMTPMXLayout.numColumns = 2;
		dnsSMTPContainer.setLayout(dnsSMTPMXLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		dnsSMTPContainer.setLayoutData(gd);
		
		Label portLabel = new Label(dnsSMTPContainer, SWT.NONE);
		portLabel.setText(Messages.Notification_SMTP_PORT_INFO);
		portLabel.setLayoutData(inputgridDataForLabel);
		this.portTextDNS1 = new Text(dnsSMTPContainer, SWT.BORDER | SWT.SINGLE);
		portTextDNS1.setText("25");
		this.portTextDNS1.setLayoutData(inputgridData);
		this.portTextDNS1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.enableSSLButtonDNS1 = new Button(dnsSMTPContainer, SWT.CHECK);
		this.enableSSLButtonDNS1.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableSSLButtonDNS1.setLayoutData(gd);
		new Label(dnsSMTPContainer, SWT.LEFT).setText(Messages.Notification_SMTP_ENCRYPT);
		
		enableSSLButtonDNS1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableSSLButtonDNS1.getSelection() == true)
					portTextDNS1.setText("465");
				else
					portTextDNS1.setText("25");
				
				verify();
			}
		});		
		
		this.enableAuthButtonDNS1 = new Button(dnsSMTPContainer, SWT.CHECK);
		this.enableAuthButtonDNS1.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableAuthButtonDNS1.setLayoutData(gd);
		
		Label dnsMXDes2Label = new Label(dnsSMTPContainer, SWT.LEFT);
		dnsMXDes2Label.setText(Messages.Notification_SMTP_AUTH_INFO);
		
		enableAuthButtonDNS1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (enableAuthButtonDNS1.getSelection() == true){
					smtpUserIdTextDNS1.setEnabled(true);
					smtpUserPwTextDNS1.setEnabled(true);
				}
				else{
					smtpUserIdTextDNS1.setEnabled(false);
					smtpUserPwTextDNS1.setEnabled(false);
					smtpUserIdTextDNS1.setText("");
					smtpUserPwTextDNS1.setText("");
				}
				verify();
			}
		});

		Label smtpUserIdLabel = new Label(dnsSMTPContainer, SWT.NONE);
		smtpUserIdLabel.setText(Messages.Notification_SMTP_USER_ID);
		smtpUserIdLabel.setLayoutData(inputgridData);
		this.smtpUserIdTextDNS1 = new Text(dnsSMTPContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpUserIdTextDNS1.setLayoutData(inputgridData);
		smtpUserIdTextDNS1.setEnabled(false);
		this.smtpUserIdTextDNS1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		Label smtpUserPwLabel = new Label(dnsSMTPContainer, SWT.NONE);
		smtpUserPwLabel.setText(Messages.Notification_SMTP_PASSWORD);
		smtpUserPwLabel.setLayoutData(inputgridData);
		this.smtpUserPwTextDNS1 = new Text(dnsSMTPContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		this.smtpUserPwTextDNS1.setLayoutData(inputgridData);
		smtpUserPwTextDNS1.setEnabled(false);
		this.smtpUserPwTextDNS1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		dnsSMTPSection.setClient(dnsSMTPContainer);
	}
	
	private void createDNSSection2(Composite dnsMXComposite) {
		Section dnsMXSection = toolkit.createSection(dnsMXComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		dnsMXSection.setLayoutData(gridData);
		dnsMXSection.setText(Messages.Notification_DNS_MX);

		Composite dnsMXContainer = toolkit.createComposite(dnsMXSection);
		GridLayout dnsMXLayout = new GridLayout();
		dnsMXLayout.numColumns = 2;
		dnsMXContainer.setLayout(dnsMXLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		dnsMXContainer.setLayoutData(gd);
		
		Label dnsMXDesLabel = new Label(dnsMXContainer, SWT.WRAP);
		dnsMXDesLabel
				.setText(Messages.Notification_DNS_MSG);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		dnsMXDesLabel.setLayoutData(gd);

		Label smtpHostNameLabel = new Label(dnsMXContainer, SWT.NONE);
		smtpHostNameLabel.setText(Messages.Notification_DNS_DOMAIN_NAME);
		smtpHostNameLabel.setLayoutData(inputgridData);
		this.smtpDomaiNameTextDNS2 = new Text(dnsMXContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpDomaiNameTextDNS2.setLayoutData(inputgridData);
		this.smtpDomaiNameTextDNS2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		
		this.enableServerInfoButtonDNS2 = new Button(dnsMXContainer, SWT.CHECK);
		this.enableServerInfoButtonDNS2.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableServerInfoButtonDNS2.setLayoutData(gd);
		
		enableServerInfoButtonDNS2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableServerInfoButtonDNS2.getSelection() == true){
					dnsServerHostText2.setEnabled(true);
					dnsServerPortText2.setEnabled(true);
				}
				else{
					dnsServerHostText2.setEnabled(false);
					dnsServerPortText2.setEnabled(false);
					dnsServerHostText2.setText("");
					dnsServerPortText2.setText("");
				}
				verify();
			}
		});
		
		Label dnsServerCheckLabel = new Label(dnsMXContainer, SWT.LEFT);
		dnsServerCheckLabel.setText(Messages.Notification_SMTP_SPECIFY_DNS_SERVER_INFO);

		Label dnsServerLabel = new Label(dnsMXContainer, SWT.NONE);
		dnsServerLabel.setText(Messages.Notification_DNS_SERVER);
		dnsServerLabel.setLayoutData(inputgridDataForLabel);
		this.dnsServerHostText2 = new Text(dnsMXContainer, SWT.BORDER | SWT.SINGLE);
		this.dnsServerHostText2.setLayoutData(inputgridData);
		dnsServerHostText2.setEnabled(false);
		this.dnsServerHostText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		Label dnsPortLabel = new Label(dnsMXContainer, SWT.NONE);
		dnsPortLabel.setText(Messages.Notification_DNS_PORT);
		dnsPortLabel.setLayoutData(inputgridDataForLabel);
		this.dnsServerPortText2 = new Text(dnsMXContainer, SWT.BORDER | SWT.SINGLE);
		this.dnsServerPortText2.setText("53");
		dnsServerPortText2.setEnabled(false);
		this.dnsServerPortText2.setLayoutData(inputgridData);
		this.dnsServerPortText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		dnsMXSection.setClient(dnsMXContainer);
		
		// for smtp server info in dns section
		Section dnsSMTPSection = toolkit.createSection(dnsMXComposite, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		dnsSMTPSection.setLayoutData(gridData);
		dnsSMTPSection.setText(Messages.Notification_JAVA_MAIL_TITLE);

		Composite dnsSMTPContainer = toolkit.createComposite(dnsSMTPSection);
		GridLayout dnsSMTPMXLayout = new GridLayout();
		dnsSMTPMXLayout.numColumns = 2;
		dnsSMTPContainer.setLayout(dnsSMTPMXLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		dnsSMTPContainer.setLayoutData(gd);
		
		Label portLabel = new Label(dnsSMTPContainer, SWT.NONE);
		portLabel.setText(Messages.Notification_SMTP_PORT_INFO);
		portLabel.setLayoutData(inputgridDataForLabel);
		this.portTextDNS2 = new Text(dnsSMTPContainer, SWT.BORDER | SWT.SINGLE);
		portTextDNS2.setText("25");
		this.portTextDNS2.setLayoutData(inputgridData);
		this.portTextDNS2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.enableSSLButtonDNS2 = new Button(dnsSMTPContainer, SWT.CHECK);
		this.enableSSLButtonDNS2.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableSSLButtonDNS2.setLayoutData(gd);
		new Label(dnsSMTPContainer, SWT.LEFT).setText(Messages.Notification_SMTP_ENCRYPT);
		
		enableSSLButtonDNS2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableSSLButtonDNS2.getSelection() == true)
					portTextDNS2.setText("465");
				else
					portTextDNS2.setText("25");
				
				verify();
			}
		});	
		
		this.enableAuthButtonDNS2 = new Button(dnsSMTPContainer, SWT.CHECK);
		this.enableAuthButtonDNS2.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableAuthButtonDNS2.setLayoutData(gd);
		
		Label dnsMXDes2Label = new Label(dnsSMTPContainer, SWT.LEFT);
		dnsMXDes2Label.setText(Messages.Notification_SMTP_AUTH_INFO);
		
		enableAuthButtonDNS2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (enableAuthButtonDNS2.getSelection() == true){
					smtpUserIdTextDNS2.setEnabled(true);
					smtpUserPwTextDNS2.setEnabled(true);
				}
				else{
					smtpUserIdTextDNS2.setEnabled(false);
					smtpUserPwTextDNS2.setEnabled(false);
					smtpUserIdTextDNS2.setText("");
					smtpUserPwTextDNS2.setText("");
				}
				verify();
			}
		});

		Label smtpUserIdLabel = new Label(dnsSMTPContainer, SWT.NONE);
		smtpUserIdLabel.setText(Messages.Notification_SMTP_USER_ID);
		smtpUserIdLabel.setLayoutData(inputgridData);
		this.smtpUserIdTextDNS2 = new Text(dnsSMTPContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpUserIdTextDNS2.setLayoutData(inputgridData);
		smtpUserIdTextDNS2.setEnabled(false);
		this.smtpUserIdTextDNS2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		Label smtpUserPwLabel = new Label(dnsSMTPContainer, SWT.NONE);
		smtpUserPwLabel.setText(Messages.Notification_SMTP_PASSWORD);
		smtpUserPwLabel.setLayoutData(inputgridData);
		this.smtpUserPwTextDNS2 = new Text(dnsSMTPContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		this.smtpUserPwTextDNS2.setLayoutData(inputgridData);
		smtpUserPwTextDNS2.setEnabled(false);
		this.smtpUserPwTextDNS2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		dnsSMTPSection.setClient(dnsSMTPContainer);
	}

	
	private void createJavaMailSection1(Composite javaMailComposite) {
		Section javaMailSection = toolkit.createSection(javaMailComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		javaMailSection.setLayoutData(gridData);
		javaMailSection.setText(Messages.Notification_JAVA_MAIL_TITLE);

		Composite javaMailContainer = toolkit.createComposite(javaMailSection);
		GridLayout javaMailLayout = new GridLayout();
		javaMailLayout.numColumns = 2;
		javaMailContainer.setLayout(javaMailLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = 248;
		javaMailContainer.setLayoutData(gd);

		Label javamailDesLabel = new Label(javaMailContainer, SWT.NONE);
		javamailDesLabel
				.setText(Messages.Notification_JAVA_MAIL_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		javamailDesLabel.setLayoutData(gd);

		Label smtpHostNameLabel = new Label(javaMailContainer, SWT.NONE);
		smtpHostNameLabel.setText(Messages.Notification_SMTP_HOST_NAME);
		smtpHostNameLabel.setLayoutData(inputgridDataForLabel);
		this.smtpHostNameTextJavaMail1 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpHostNameTextJavaMail1.setText("example.com");
		this.smtpHostNameTextJavaMail1.setLayoutData(inputgridData);
		this.smtpHostNameTextJavaMail1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label portLabel = new Label(javaMailContainer, SWT.NONE);
		portLabel.setText(Messages.Notification_SMTP_PORT_INFO);
		portLabel.setLayoutData(inputgridDataForLabel);
		this.portTextJavaMail1 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE);
		portTextJavaMail1.setText("25");
		this.portTextJavaMail1.setLayoutData(inputgridData);
		this.portTextJavaMail1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.enableSSLButtonJavaMail1 = new Button(javaMailContainer, SWT.CHECK);
		this.enableSSLButtonJavaMail1.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableSSLButtonJavaMail1.setLayoutData(gd);
		new Label(javaMailContainer, SWT.LEFT).setText(Messages.Notification_SMTP_ENCRYPT);

		enableSSLButtonJavaMail1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableSSLButtonJavaMail1.getSelection() == true)
					portTextJavaMail1.setText("465");
				else
					portTextJavaMail1.setText("25");
				
				verify();
			}
		});
				
		this.enableAuthButtonJavaMail1 = new Button(javaMailContainer, SWT.CHECK);
		this.enableAuthButtonJavaMail1.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableAuthButtonJavaMail1.setLayoutData(gd);
		
		Label javamailDes2Label = new Label(javaMailContainer, SWT.LEFT);
		javamailDes2Label.setText(Messages.Notification_SMTP_AUTH_INFO);
		
		enableAuthButtonJavaMail1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableAuthButtonJavaMail1.getSelection() == true){
					smtpUserIdTextJavaMail1.setEnabled(true);
					smtpUserPwTextJavaMail1.setEnabled(true);
				}
				else{
					smtpUserIdTextJavaMail1.setEnabled(false);
					smtpUserIdTextJavaMail1.setText("");
					smtpUserPwTextJavaMail1.setEnabled(false);
					smtpUserPwTextJavaMail1.setText("");
				}

				verify();
			}
		});
				
		Label smtpUserIdLabel = new Label(javaMailContainer, SWT.NONE);
		smtpUserIdLabel.setText(Messages.Notification_SMTP_USER_ID);
		smtpUserIdLabel.setLayoutData(inputgridData);
		this.smtpUserIdTextJavaMail1 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpUserIdTextJavaMail1.setLayoutData(inputgridData);
		smtpUserIdTextJavaMail1.setEnabled(false);
		this.smtpUserIdTextJavaMail1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		Label smtpUserPwLabel = new Label(javaMailContainer, SWT.NONE);
		smtpUserPwLabel.setText(Messages.Notification_SMTP_PASSWORD);
		smtpUserPwLabel.setLayoutData(inputgridData);
		this.smtpUserPwTextJavaMail1 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		this.smtpUserPwTextJavaMail1.setLayoutData(inputgridData);
		smtpUserPwTextJavaMail1.setEnabled(false);
		this.smtpUserPwTextJavaMail1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		javaMailSection.setClient(javaMailContainer);
	}
	
	private void createJavaMailSection2(Composite javaMailComposite) {
		Section javaMailSection = toolkit.createSection(javaMailComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		javaMailSection.setLayoutData(gridData);
		javaMailSection.setText(Messages.Notification_JAVA_MAIL_TITLE);

		Composite javaMailContainer = toolkit.createComposite(javaMailSection);
		GridLayout javaMailLayout = new GridLayout();
		javaMailLayout.numColumns = 2;
		javaMailContainer.setLayout(javaMailLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = 248;
		javaMailContainer.setLayoutData(gd);

		Label javamailDesLabel = new Label(javaMailContainer, SWT.NONE);
		javamailDesLabel
				.setText(Messages.Notification_JAVA_MAIL_INFO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		javamailDesLabel.setLayoutData(gd);

		Label smtpHostNameLabel = new Label(javaMailContainer, SWT.NONE);
		smtpHostNameLabel.setText(Messages.Notification_SMTP_HOST_NAME);
		smtpHostNameLabel.setLayoutData(inputgridDataForLabel);
		this.smtpHostNameTextJavaMail2 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpHostNameTextJavaMail2.setText("example.com");
		this.smtpHostNameTextJavaMail2.setLayoutData(inputgridData);
		this.smtpHostNameTextJavaMail2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label portLabel = new Label(javaMailContainer, SWT.NONE);
		portLabel.setText(Messages.Notification_SMTP_PORT_INFO);
		portLabel.setLayoutData(inputgridDataForLabel);
		this.portTextJavaMail2 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE);
		portTextJavaMail2.setText("25");
		this.portTextJavaMail2.setLayoutData(inputgridData);
		this.portTextJavaMail2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.enableSSLButtonJavaMail2 = new Button(javaMailContainer, SWT.CHECK);
		this.enableSSLButtonJavaMail2.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableSSLButtonJavaMail2.setLayoutData(gd);
		new Label(javaMailContainer, SWT.LEFT).setText(Messages.Notification_SMTP_ENCRYPT);

		enableSSLButtonJavaMail2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableSSLButtonJavaMail2.getSelection() == true)
					portTextJavaMail2.setText("465");
				else
					portTextJavaMail2.setText("25");
				
				verify();
			}
		});
			
		this.enableAuthButtonJavaMail2 = new Button(javaMailContainer, SWT.CHECK);
		this.enableAuthButtonJavaMail2.setSelection(false);
		gd = new GridData(GridData.BEGINNING);
		this.enableAuthButtonJavaMail2.setLayoutData(gd);
		
		Label javamailDes2Label = new Label(javaMailContainer, SWT.LEFT);
		javamailDes2Label.setText(Messages.Notification_SMTP_AUTH_INFO);
		
		enableAuthButtonJavaMail2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (enableAuthButtonJavaMail2.getSelection() == true){
					smtpUserIdTextJavaMail2.setEnabled(true);
					smtpUserPwTextJavaMail2.setEnabled(true);
				}
				else{
					smtpUserIdTextJavaMail2.setEnabled(false);
					smtpUserIdTextJavaMail2.setText("");
					smtpUserPwTextJavaMail2.setEnabled(false);
					smtpUserPwTextJavaMail2.setText("");
				}

				verify();
			}
		});
		
		
		Label smtpUserIdLabel = new Label(javaMailContainer, SWT.NONE);
		smtpUserIdLabel.setText(Messages.Notification_SMTP_USER_ID);
		smtpUserIdLabel.setLayoutData(inputgridData);
		this.smtpUserIdTextJavaMail2 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE);
		this.smtpUserIdTextJavaMail2.setLayoutData(inputgridData);
		smtpUserIdTextJavaMail2.setEnabled(false);
		this.smtpUserIdTextJavaMail2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		Label smtpUserPwLabel = new Label(javaMailContainer, SWT.NONE);
		smtpUserPwLabel.setText(Messages.Notification_SMTP_PASSWORD);
		smtpUserPwLabel.setLayoutData(inputgridData);
		this.smtpUserPwTextJavaMail2 = new Text(javaMailContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		this.smtpUserPwTextJavaMail2.setLayoutData(inputgridData);
		smtpUserPwTextJavaMail2.setEnabled(false);
		this.smtpUserPwTextJavaMail2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		javaMailSection.setClient(javaMailContainer);
	}
	
	private void createReplyToSection1(Composite dnsMXComposite) {
		Section enableReplyToSection = toolkit.createSection(dnsMXComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		enableReplyToSection.setLayoutData(gridData);
		enableReplyToSection.setText(Messages.Notification_REPLYTO_TITLE);

		Composite enableReplyToContainer = toolkit.createComposite(enableReplyToSection);
		GridLayout enableReplyToLayout = new GridLayout();
		//enableReplyToLayout.numColumns = 2;
		enableReplyToContainer.setLayout(enableReplyToLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		enableReplyToContainer.setLayoutData(gd);
		
		enableReplyToSection.setClient(enableReplyToContainer);
		
		Section emailAddressSection = toolkit.createSection(enableReplyToContainer, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		emailAddressSection.setLayoutData(gridData);
		gridData.horizontalSpan = 2;
		emailAddressSection.setText(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_TITLE);

		Composite emailAddressContainer = toolkit.createComposite(emailAddressSection);
		GridLayout emailAddressLayout = new GridLayout();
		emailAddressLayout.numColumns = 2;
		emailAddressContainer.setLayout(emailAddressLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		emailAddressContainer.setLayoutData(gd);
		
		Label emailAddressDesLabel = new Label(emailAddressContainer, SWT.WRAP);
		emailAddressDesLabel.setText(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_DES);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		emailAddressDesLabel.setLayoutData(gd);
		
		Label domainNameLabel = new Label(emailAddressContainer, SWT.WRAP);
		domainNameLabel.setText(Messages.Notificaton_REPLYTO_DOMAIN_NAME);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		domainNameLabel.setLayoutData(gd);
		
		this.domainNameText1 = new Text(emailAddressContainer, SWT.BORDER | SWT.SINGLE);
		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 270;
		this.domainNameText1.setLayoutData(inputgridData);
		this.domainNameText1.setEnabled(true);
		this.domainNameText1.setText("example.com");
		this.domainNameText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.emailAddressNoneButton1 = new Button(emailAddressContainer, SWT.RADIO);
		this.emailAddressNoneButton1.setBackground(emailAddressContainer.getBackground());
		this.emailAddressNoneButton1.setText(Messages.Notification_REPLYTO_NONE);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		this.emailAddressNoneButton1.setLayoutData(gd);
		this.emailAddressNoneButton1.setSelection(true);
		emailAddressNoneButton1.setBounds(0, 25, 430, 25);
		
		this.emailAddressPrefixButton1 = new Button(emailAddressContainer, SWT.RADIO);
		this.emailAddressPrefixButton1.setBackground(emailAddressContainer.getBackground());
		this.emailAddressPrefixButton1.setText(Messages.Notification_REPLYTO_Prefix);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		emailAddressPrefixButton1.setBounds(0, 50, 430, 25);
		this.emailAddressPrefixButton1.setLayoutData(gd);
		
		this.emailAddressSuffixButton1 = new Button(emailAddressContainer, SWT.RADIO);
		this.emailAddressSuffixButton1.setBackground(emailAddressContainer.getBackground());
		this.emailAddressSuffixButton1.setText(Messages.Notification_REPLYTO_Suffix);
		this.emailAddressSuffixButton1.setSelection(false);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		emailAddressSuffixButton1.setBounds(0, 75, 430, 25);
		this.emailAddressSuffixButton1.setLayoutData(gd);
		
		final Label localPartLabel = new Label(emailAddressContainer, SWT.WRAP);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		
		this.localPartText1 = new Text(emailAddressContainer, SWT.BORDER | SWT.SINGLE);
		this.localPartText1.setLayoutData(inputgridData);
		this.localPartText1.setEnabled(false);
		
		localPartLabel.setLayoutData(gd);
		String domainName = this.domainNameText1.getText();
		if(domainName.trim().length() == 0)
			domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
		if(emailAddressNoneButton1.getSelection() == true)
			localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
		else if(emailAddressPrefixButton1.getSelection() == true)
			localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText1.getText().trim(), domainName));
		else
			localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText1.getText().trim(), domainName));
		
		
		emailAddressNoneButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emailAddressNoneButton1.getSelection() == true) {
					emailAddressPrefixButton1.setSelection(false);
					emailAddressSuffixButton1.setSelection(false);
					localPartText1.setText("");
					localPartText1.setEnabled(false);
					String domainName = domainNameText1.getText();
					if(domainName.trim().length() == 0)
						domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
					
				}
			}
		});
		
		emailAddressPrefixButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emailAddressPrefixButton1.getSelection() == true) {
					emailAddressNoneButton1.setSelection(false);
					emailAddressSuffixButton1.setSelection(false);
					localPartText1.setText("prefix_");
					localPartText1.setEnabled(true);
					String domainName = domainNameText1.getText();
					if(domainName.trim().length() == 0)
						domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText1.getText(), domainName));
				}
				
			}
		});
		
		emailAddressSuffixButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emailAddressSuffixButton1.getSelection() == true) {
					emailAddressNoneButton1.setSelection(false);
					emailAddressPrefixButton1.setSelection(false);
					localPartText1.setText("_suffix");
					localPartText1.setEnabled(true);
					String domainName = domainNameText1.getText();
					if(domainName.trim().length() == 0)
						domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText1.getText(), domainName));
				}
				
			}
		});
		
		this.domainNameText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String domainName = domainNameText1.getText();
				if(domainName.trim().length() == 0)
					domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
				if(emailAddressNoneButton1.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
				else if(emailAddressPrefixButton1.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText1.getText().trim(), domainName));
				else
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText1.getText().trim(), domainName));
				verify();
			}
		});
		
		this.localPartText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String domainName = domainNameText1.getText();
				if(domainName.trim().length() == 0)
					domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
				if(emailAddressNoneButton1.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
				else if(emailAddressPrefixButton1.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText1.getText().trim(), domainName));
				else
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText1.getText().trim(), domainName));
				verify();
			}
		});
		
		localPartText1.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				String inStr = e.text;
				if (inStr.length() > 0) {
					try {
						if(localPartText1.getText().length() < 28)
							e.doit = true;
						else
							e.doit = false;
					} catch (Exception ex) {
						if(emailAddressSuffixButton1.getSelection())
							showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING);
						else
							showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING);
					}
				}
			}
		});
		
		emailAddressSection.setClient(emailAddressContainer);
		
		Section mailFileSection = toolkit.createSection(enableReplyToContainer, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		mailFileSection.setLayoutData(gridData);
		mailFileSection.setText(Messages.Notification_REPLYTO_MAIL_FILE_TITLE);

		Composite mailFileContainer = toolkit.createComposite(mailFileSection);
		GridLayout mailFileLayout = new GridLayout();
		mailFileLayout.numColumns = 2;
		mailFileContainer.setLayout(mailFileLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		mailFileContainer.setLayoutData(gd);
		
		Label mailFileDesLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFileDesLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_DES);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFileDesLabel.setLayoutData(gd);
		
		Label mailFileServerLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFileServerLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_SERVER);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFileServerLabel.setLayoutData(gd);
		
		this.mailFileServerText1 = new Text(mailFileContainer, SWT.BORDER | SWT.SINGLE);
		this.mailFileServerText1.setLayoutData(inputgridData);
		this.mailFileServerText1.setEnabled(true);
		this.mailFileServerText1.setText("example.com");
		this.mailFileServerText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label mailFileUserIDLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFileUserIDLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_USER_ID);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFileUserIDLabel.setLayoutData(gd);
		
		this.mailFileUserIDText1 = new Text(mailFileContainer, SWT.BORDER | SWT.SINGLE);
		this.mailFileUserIDText1.setLayoutData(inputgridData);
		this.mailFileUserIDText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label mailFilePWLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFilePWLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_PW);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFilePWLabel.setLayoutData(gd);

		this.mailFilePWText1 = new Text(mailFileContainer, SWT.BORDER | SWT.SINGLE| SWT.PASSWORD);
		this.mailFilePWText1.setLayoutData(inputgridData);
		this.mailFilePWText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		mailFileSection.setClient(mailFileContainer);
	}
	
	private void createReplyToSection2(Composite dnsMXComposite) {
		Section enableReplyToSection = toolkit.createSection(dnsMXComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		enableReplyToSection.setLayoutData(gridData);
		enableReplyToSection.setText(Messages.Notification_REPLYTO_TITLE);

		Composite enableReplyToContainer = toolkit.createComposite(enableReplyToSection);
		GridLayout enableReplyToLayout = new GridLayout();
		//enableReplyToLayout.numColumns = 2;
		enableReplyToContainer.setLayout(enableReplyToLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		enableReplyToContainer.setLayoutData(gd);
		
		enableReplyToSection.setClient(enableReplyToContainer);
		
		Section emailAddressSection = toolkit.createSection(enableReplyToContainer, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		emailAddressSection.setLayoutData(gridData);
		gridData.horizontalSpan = 2;
		emailAddressSection.setText(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_TITLE);

		Composite emailAddressContainer = toolkit.createComposite(emailAddressSection);
		GridLayout emailAddressLayout = new GridLayout();
		emailAddressLayout.numColumns = 2;
		emailAddressContainer.setLayout(emailAddressLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		emailAddressContainer.setLayoutData(gd);
		
		Label emailAddressDesLabel = new Label(emailAddressContainer, SWT.WRAP);
		emailAddressDesLabel.setText(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_DES);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		emailAddressDesLabel.setLayoutData(gd);
		
		Label domainNameLabel = new Label(emailAddressContainer, SWT.WRAP);
		domainNameLabel.setText(Messages.Notificaton_REPLYTO_DOMAIN_NAME);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		domainNameLabel.setLayoutData(gd);
		
		this.domainNameText2 = new Text(emailAddressContainer, SWT.BORDER | SWT.SINGLE);
		GridData inputgridData = new GridData(GridData.BEGINNING);
		inputgridData.horizontalSpan = 2;
		inputgridData.widthHint = 270;
		this.domainNameText2.setLayoutData(inputgridData);
		this.domainNameText2.setEnabled(true);
		this.domainNameText2.setText("example.com");
		this.domainNameText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		this.emailAddressNoneButton2 = new Button(emailAddressContainer, SWT.RADIO);
		this.emailAddressNoneButton2.setBackground(emailAddressContainer.getBackground());
		this.emailAddressNoneButton2.setText(Messages.Notification_REPLYTO_NONE);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		this.emailAddressNoneButton2.setLayoutData(gd);
		this.emailAddressNoneButton2.setSelection(true);
		emailAddressNoneButton2.setBounds(0, 25, 430, 25);
		
		this.emailAddressPrefixButton2 = new Button(emailAddressContainer, SWT.RADIO);
		this.emailAddressPrefixButton2.setBackground(emailAddressContainer.getBackground());
		this.emailAddressPrefixButton2.setText(Messages.Notification_REPLYTO_Prefix);
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		emailAddressPrefixButton2.setBounds(0, 50, 430, 25);
		this.emailAddressPrefixButton2.setLayoutData(gd);
		
		this.emailAddressSuffixButton2 = new Button(emailAddressContainer, SWT.RADIO);
		this.emailAddressSuffixButton2.setBackground(emailAddressContainer.getBackground());
		this.emailAddressSuffixButton2.setText(Messages.Notification_REPLYTO_Suffix);
		this.emailAddressSuffixButton2.setSelection(false);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		emailAddressSuffixButton2.setBounds(0, 75, 430, 25);
		this.emailAddressSuffixButton2.setLayoutData(gd);
		
		final Label localPartLabel = new Label(emailAddressContainer, SWT.WRAP);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		
		this.localPartText2 = new Text(emailAddressContainer, SWT.BORDER | SWT.SINGLE);
		this.localPartText2.setLayoutData(inputgridData);
		this.localPartText2.setEnabled(false);
		
		localPartLabel.setLayoutData(gd);
		String domainName = this.domainNameText2.getText();
		if(domainName.trim().length() == 0)
			domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
		if(emailAddressNoneButton2.getSelection() == true)
			localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
		else if(emailAddressPrefixButton2.getSelection() == true)
			localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText2.getText().trim(), domainName));
		else
			localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText2.getText().trim(), domainName));
		
		
		emailAddressNoneButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emailAddressNoneButton2.getSelection() == true) {
					emailAddressPrefixButton2.setSelection(false);
					emailAddressSuffixButton2.setSelection(false);
					localPartText2.setText("");
					localPartText2.setEnabled(false);
					String domainName = domainNameText2.getText();
					if(domainName.trim().length() == 0)
						domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
					
				}
			}
		});
		
		emailAddressPrefixButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emailAddressPrefixButton2.getSelection() == true) {
					emailAddressNoneButton2.setSelection(false);
					emailAddressSuffixButton2.setSelection(false);
					localPartText2.setText("prefix_");
					localPartText2.setEnabled(true);
					String domainName = domainNameText2.getText();
					if(domainName.trim().length() == 0)
						domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText2.getText(), domainName));
				}
				
			}
		});
		
		emailAddressSuffixButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emailAddressSuffixButton2.getSelection() == true) {
					emailAddressNoneButton2.setSelection(false);
					emailAddressPrefixButton2.setSelection(false);
					localPartText2.setText("_suffix");
					localPartText2.setEnabled(true);
					String domainName = domainNameText2.getText();
					if(domainName.trim().length() == 0)
						domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText2.getText(), domainName));
				}
				
			}
		});

		this.domainNameText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String domainName = domainNameText2.getText();
				if(domainName.trim().length() == 0)
					domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
				if(emailAddressNoneButton2.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
				else if(emailAddressPrefixButton2.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText2.getText().trim(), domainName));
				else
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText2.getText().trim(), domainName));
				verify();
			}
		});
		
		this.localPartText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String domainName = domainNameText2.getText();
				if(domainName.trim().length() == 0)
					domainName = Messages.Notification_REPLYTO_EMPTY_DOMAIN_NAME;
				if(emailAddressNoneButton2.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_NONE_LABEL, domainName));
				else if(emailAddressPrefixButton2.getSelection() == true)
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Prefix_LABEL, localPartText2.getText().trim(), domainName));
				else
					localPartLabel.setText(Messages.bind(Messages.Notification_REPLYTO_Suffix_LABEL, localPartText2.getText().trim(), domainName));
				verify();
			}
		});
		
		localPartText2.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				String inStr = e.text;
				if (inStr.length() > 0) {
					try {
						if(localPartText2.getText().length() < 28)
							e.doit = true;
						else
							e.doit = false;
					} catch (Exception ex) {
						if(emailAddressSuffixButton2.getSelection())
							showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING);
						else
							showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING);
					}
				}
			}
		});
		
		emailAddressSection.setClient(emailAddressContainer);
		
		Section mailFileSection = toolkit.createSection(enableReplyToContainer, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		mailFileSection.setLayoutData(gridData);
		mailFileSection.setText(Messages.Notification_REPLYTO_MAIL_FILE_TITLE);

		Composite mailFileContainer = toolkit.createComposite(mailFileSection);
		GridLayout mailFileLayout = new GridLayout();
		mailFileLayout.numColumns = 2;
		mailFileContainer.setLayout(mailFileLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		mailFileContainer.setLayoutData(gd);
		
		Label mailFileDesLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFileDesLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_DES);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFileDesLabel.setLayoutData(gd);
		
		Label mailFileServerLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFileServerLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_SERVER);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFileServerLabel.setLayoutData(gd);
		
		this.mailFileServerText2 = new Text(mailFileContainer, SWT.BORDER | SWT.SINGLE);
		this.mailFileServerText2.setLayoutData(inputgridData);
		this.mailFileServerText2.setEnabled(true);
		this.mailFileServerText2.setText("example.com");
		this.mailFileServerText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label mailFileUserIDLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFileUserIDLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_USER_ID);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFileUserIDLabel.setLayoutData(gd);
		
		this.mailFileUserIDText2 = new Text(mailFileContainer, SWT.BORDER | SWT.SINGLE);
		this.mailFileUserIDText2.setLayoutData(inputgridData);
		this.mailFileUserIDText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		Label mailFilePWLabel = new Label(mailFileContainer, SWT.WRAP);
		mailFilePWLabel.setText(Messages.Notification_REPLYTO_MAIL_FILE_PW);
		gd = new GridData();
		gd.horizontalSpan = 2;
		//gd.widthHint=630;
		mailFilePWLabel.setLayoutData(gd);

		this.mailFilePWText2 = new Text(mailFileContainer, SWT.BORDER | SWT.SINGLE| SWT.PASSWORD);
		this.mailFilePWText2.setLayoutData(inputgridData);
		this.mailFilePWText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		mailFileSection.setClient(mailFileContainer);
	}

	private void verifyCompleteNone() {
		setUserDataNone();
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
	}
	
	private boolean verifyCompeleteReplyTo1() {
		String userId = mailFileUserIDText1.getText().trim();
		String pwd = mailFilePWText1.getText().trim();
		String hostName = mailFileServerText1.getText().trim(); 
		String domain = domainNameText1.getText().trim();
		boolean isNone = emailAddressNoneButton1.getSelection();
		boolean isPrefix = emailAddressPrefixButton1.getSelection();
		boolean isSuffix = emailAddressSuffixButton1.getSelection();
		
		
		boolean isValidDomainName = verifyHostNameComplete(domain);
		if (!isValidDomainName) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_DOMAIN_NAME_WARNING);
			return false;
		}
		
		if(isPrefix || isSuffix) {
			String localPart = localPartText1.getText().trim();
			if(localPart.trim().length() <= 0) {
				if(isPrefix)
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_PREFIX_INPUT_WARNING);
				else
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_SUFFIX_INPUT_WARNING);
				return false;
			}
			
			try {
				boolean isValidEmail = false;
				if(isPrefix)
					isValidEmail= installValidator.validateEmailAddress1(localPart+"aaa");
				else
					isValidEmail= installValidator.validateEmailAddress1("aaa"+localPart);
				if(!isValidEmail) {
					if(isPrefix)
						showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING);
					else
						showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING);
					return false;
				}
			} catch (Exception ex) {
				if(emailAddressPrefixButton1.getSelection())
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_PREFIX_INPUT_WARNING);
				else if(emailAddressSuffixButton1.getSelection())
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_SUFFIX_INPUT_WARNING);
				return false;
			}
		}
		
		boolean isHostNameValid = verifyHostNameComplete(hostName);
		if (!isHostNameValid) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_SERVER_WARNING);
			return false;
		}
		
		boolean isNameValid = this.verifyUserNameComplete(userId);
		if (!isNameValid) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_USER_ID_WARNING);
			return false;
		}
		
		boolean isPawValid = this.verifyPasswordComplete(pwd);
		if (!isPawValid) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_PW_WARNING);
			return false;
		}
		
		String type = null;
		if(isNone)
			type = "none";
		else if(isPrefix)
			type = "prefix";
		else
			type = "suffix";
		
		String localPart = "";
		if(isPrefix || isSuffix)
			localPart = localPartText1.getText().trim();
		
		this.setUserDataReplyTo(domain, type, localPart, hostName, userId, pwd);
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
		return true;
	}
	
	private boolean verifyCompeleteReplyTo2() {
		String userId = mailFileUserIDText2.getText().trim();
		String pwd = mailFilePWText2.getText().trim();
		String hostName = mailFileServerText2.getText().trim(); 
		String domain = domainNameText2.getText().trim();
		boolean isNone = emailAddressNoneButton2.getSelection();
		boolean isPrefix = emailAddressPrefixButton2.getSelection();
		boolean isSuffix = emailAddressSuffixButton2.getSelection();
		
		
		boolean isValidDomainName = verifyHostNameComplete(domain);
		if (!isValidDomainName) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_DOMAIN_NAME_WARNING);
			return false;
		}
		
		if(isPrefix || isSuffix) {
			String localPart = localPartText2.getText().trim();
			if(localPart.trim().length() <= 0) {
				if(isPrefix)
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_PREFIX_INPUT_WARNING);
				else
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_SUFFIX_INPUT_WARNING);
				return false;
			}
			
			try {
				boolean isValidEmail = false;
				if(isPrefix)
					isValidEmail= installValidator.validateEmailAddress1(localPart+"aaa");
				else
					isValidEmail= installValidator.validateEmailAddress1("aaa"+localPart);
				if(!isValidEmail) {
					if(isPrefix)
						showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING);
					else
						showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING);
					return false;
				}
			} catch (Exception ex) {
				if(emailAddressPrefixButton2.getSelection())
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_PREFIX_INPUT_WARNING);
				else if(emailAddressSuffixButton2.getSelection())
					showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_SUFFIX_INPUT_WARNING);
				return false;
			}
		}
		
		boolean isHostNameValid = verifyHostNameComplete(hostName);
		if (!isHostNameValid) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_SERVER_WARNING);
			return false;
		}
		
		boolean isNameValid = this.verifyUserNameComplete(userId);
		if (!isNameValid) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_USER_ID_WARNING);
			return false;
		}
		
		boolean isPawValid = this.verifyPasswordComplete(pwd);
		if (!isPawValid) {
			showErrorMessages(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_PW_WARNING);
			return false;
		}
		
		String type = null;
		if(isNone)
			type = "none";
		else if(isPrefix)
			type = "prefix";
		else
			type = "suffix";
		
		String localPart = "";
		if(isPrefix || isSuffix)
			localPart = localPartText2.getText().trim();
		
		this.setUserDataReplyTo(domain, type, localPart, hostName, userId, pwd);
		
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
		return true;
	}
	
	private boolean verifyCompleteJavaMail1() {
		
		boolean isSSL = enableSSLButtonJavaMail1.getSelection();
		String userId = smtpUserIdTextJavaMail1.getText().trim();
		String pwd = smtpUserPwTextJavaMail1.getText().trim();
		String hostName = smtpHostNameTextJavaMail1.getText().trim();
		boolean isAuth = enableAuthButtonJavaMail1.getSelection();
		
		boolean isHostNameValid = verifyHostNameComplete(hostName);
		if (!isHostNameValid) {
			showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_HOSTNAME_INPUT_WARNING);
			return false;
		}
		
		if(isAuth){
				boolean isNameValid = this.verifyUserNameComplete(userId);
			if (!isNameValid) {
				showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_USERID_INPUT_WARNING);
				return false;
			}
			
			boolean isPawValid = this.verifyPasswordComplete(pwd);
			if (!isPawValid) {
				showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PW_INPUT_WARNING);
				return false;
			}
		}
		
		String port = portTextJavaMail1.getText();
		
		boolean isPortValid = this.verifyPortComplete(port);
		if (!isPortValid) {
			showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PORT_INPUT_WARNING);
			return false;
		}
		
		
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
		
		this.setUserDataJavaMail(hostName, port, userId, pwd, isSSL);
		return true;
	}
	
private boolean verifyCompleteJavaMail2() {
		
		boolean isSSL = enableSSLButtonJavaMail2.getSelection();
		String userId = smtpUserIdTextJavaMail2.getText().trim();
		String pwd = smtpUserPwTextJavaMail2.getText().trim();
		String hostName = smtpHostNameTextJavaMail2.getText().trim();
		boolean isAuth = enableAuthButtonJavaMail2.getSelection();
		
		boolean isHostNameValid = verifyHostNameComplete(hostName);
		if (!isHostNameValid) {
			showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_HOSTNAME_INPUT_WARNING);
			return false;
		}
		
		if(isAuth){
				boolean isNameValid = this.verifyUserNameComplete(userId);
			if (!isNameValid) {
				showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_USERID_INPUT_WARNING);
				return false;
			}
			
			boolean isPawValid = this.verifyPasswordComplete(pwd);
			if (!isPawValid) {
				showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PW_INPUT_WARNING);
				return false;
			}
		}
		
		String port = portTextJavaMail2.getText();
		
		boolean isPortValid = this.verifyPortComplete(port);
		if (!isPortValid) {
			showErrorMessages(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PORT_INPUT_WARNING);
			return false;
		}
		
		
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
		
		this.setUserDataJavaMail(hostName, port, userId, pwd, isSSL);
		return true;
	}
	
	private boolean verifyCompleteDNS1() {
		
		boolean isSSL = enableSSLButtonDNS1.getSelection();
		String userId = smtpUserIdTextDNS1.getText().trim();
		String pwd = smtpUserPwTextDNS1.getText().trim();
		boolean isAuth = enableAuthButtonDNS1.getSelection();
		boolean isDNSServerinfo = enableServerInfoButtonDNS1.getSelection();
		String dnsHost = dnsServerHostText1.getText().trim();
		String dnsPort = dnsServerPortText1.getText().trim();
		
		String domain = smtpDomaiNameTextDNS1.getText();
		boolean isDomainNameValid = verifyHostNameComplete(domain);
		if (!isDomainNameValid) {
			return false;
		}
		
		if(isDNSServerinfo){
			
			boolean isHostNameValid = verifyHostNameComplete(dnsHost);
			if (!isHostNameValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_HOSTNAME_INPUT_WARNING);
				return false;
			}
			
			boolean isdnsPortValid = verifyPortComplete(dnsPort);
			if (!isdnsPortValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_PORT_INPUT_WARNING);
				return false;
			}
		}
		
		if(isAuth){
			boolean isNameValid = this.verifyUserNameComplete(userId);
			if (!isNameValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_SMTP_USERID_INPUT_WARNING);
				return false;
			}
			
			boolean isPawValid = this.verifyPasswordComplete(pwd);
			if (!isPawValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_SMTP_PW_INPUT_WARNING);
				return false;
			}
		}
		
		String sslPort = portTextDNS1.getText();
		
		boolean isPortValid = this.verifyPortComplete(sslPort);
		if (!isPortValid) {
			showErrorMessages(Messages.Notificaton_DNS_INVALID_SMTP_PORT_INPUT_WARNING);
			return false;
		}
		
		
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
		
		this.setUserDataDNS(dnsHost, dnsPort, domain, sslPort, userId, pwd, isSSL);
		return true;
	}
	
	private boolean verifyCompleteDNS2() {
		
		boolean isSSL = enableSSLButtonDNS2.getSelection();
		String userId = smtpUserIdTextDNS2.getText().trim();
		String pwd = smtpUserPwTextDNS2.getText().trim();
		boolean isAuth = enableAuthButtonDNS2.getSelection();
		boolean isDNSServerinfo = enableServerInfoButtonDNS2.getSelection();
		String dnsHost = dnsServerHostText2.getText().trim();
		String dnsPort = dnsServerPortText2.getText().trim();
		
		String domain = smtpDomaiNameTextDNS2.getText();
		boolean isDomainNameValid = verifyHostNameComplete(domain);
		if (!isDomainNameValid) {
			return false;
		}
		
		if(isDNSServerinfo){
			
			boolean isHostNameValid = verifyHostNameComplete(dnsHost);
			if (!isHostNameValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_HOSTNAME_INPUT_WARNING);
				return false;
			}
			
			boolean isdnsPortValid = verifyPortComplete(dnsPort);
			if (!isdnsPortValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_PORT_INPUT_WARNING);
				return false;
			}
		}
		
		if(isAuth){
			boolean isNameValid = this.verifyUserNameComplete(userId);
			if (!isNameValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_SMTP_USERID_INPUT_WARNING);
				return false;
			}
			
			boolean isPawValid = this.verifyPasswordComplete(pwd);
			if (!isPawValid) {
				showErrorMessages(Messages.Notificaton_DNS_INVALID_SMTP_PW_INPUT_WARNING);
				return false;
			}
		}
		
		String sslPort = portTextDNS2.getText();
		
		boolean isPortValid = this.verifyPortComplete(sslPort);
		if (!isPortValid) {
			showErrorMessages(Messages.Notificaton_DNS_INVALID_SMTP_PORT_INPUT_WARNING);
			return false;
		}
		
		
		setErrorMessage(null); // in case there had been one;
		nextEnabled = true;
		setPageComplete(true);
		
		this.setUserDataDNS(dnsHost, dnsPort, domain, sslPort, userId, pwd, isSSL);
		return true;
	}
	
	private boolean verifyHostNameComplete(String hostName) {
		
		nextEnabled = false;
		setPageComplete(false);
		InstallValidator installvalidator = new InstallValidator();
		
		try {
			if (!installvalidator.hostNameValidate(hostName)) {
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
		
		return true;
	}
	
    private boolean verifyPortComplete(String dmport) {
		
		nextEnabled = false;
		setPageComplete(false);

		InstallValidator installvalidator = new InstallValidator();
		
		try {
			if (!installvalidator.portNumValidate(dmport)) {
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
		
		return true;

	}

	private boolean verifyUserNameComplete(String dmuserId) {

		if (dmuserId == null || dmuserId.length() == 0) {
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
	
	private void setUserDataNone(){
		profile = getProfile();
		profile.setUserData("user.notification.enabled", "");
		profile.setUserData("user.notification.useJavaMailProvider", "");
		profile.setUserData("user.notification.host", "");
		
		profile.setUserData("user.notification.port", "");
		profile.setUserData("user.notification.user", "");
		profile.setUserData("user.notification.password", "");
		
		profile.setUserData("user.notification.port.key.value", "");
		profile.setUserData("user.notification.user.key.value", "");
		profile.setUserData("user.notification.password.key.value", "");
		profile.setUserData("user.notification.protocol.key.value", "");
		
		profile.setUserData("user.notification.ssl.enabled", "");
		profile.setUserData("user.notification.domain", "");
		
		//for dns url
		profile.setUserData("user.notification.dnshost", "");
		profile.setUserData("user.notification.dnsport", "");
		profile.setUserData("user.notification.dns.url.key.value", "");
		
		profile.setUserData("user.notification.enabled.key.value", "\"enableMailNotification\": \"false\",");
		
		setuserDataDisableReplyTo();
	}
	
	private void setuserDataDisableReplyTo() {
		profile = getProfile();
		profile.setUserData("user.notification.replyto.enabled", "false");
		profile.setUserData("user.notification.replyto.domain", "");
		profile.setUserData("user.notification.replyto.prefix", "");
		profile.setUserData("user.notification.replyto.suffix", "");
		//profile.setUserData("user.notification.replyto.localPart.type", "");
		//profile.setUserData("user.notification.replyto.localPart", "");
		profile.setUserData("user.notification.replyto.mailFile.hostName", "");
		profile.setUserData("user.notification.replyto.mailFile.userID", "");
		profile.setUserData("user.notification.replyto.mailFile.password", "");
		
		profile.setUserData("user.notification.replyto.info", "");
		
	}
	
	private void setUserDataReplyTo(String domain,String type,String localPart,String hostName,String userId, String pwd){
		profile = getProfile();
		profile.setUserData("user.notification.replyto.enabled", "true");
		profile.setUserData("user.notification.replyto.domain", domain.toLowerCase());
		if("prefix".equals(type)){
			profile.setUserData("user.notification.replyto.prefix", localPart);
		}else if("suffix".equals(type)){
			profile.setUserData("user.notification.replyto.suffix", localPart);
		}
		//profile.setUserData("user.notification.replyto.localPart.type", type);
		//profile.setUserData("user.notification.replyto.localPart", localPart);
		profile.setUserData("user.notification.replyto.mailFile.hostName", hostName.toLowerCase());
		profile.setUserData("user.notification.replyto.mailFile.userID", userId);
		profile.setUserData("user.notification.replyto.mailFile.password", pwd);
		
		StringBuffer replyto = new StringBuffer();
		replyto.append("\"replyto\":{").append("\n");
		replyto.append("\"domain\": \"").append(profile.getUserData("user.notification.replyto.domain")).append("\",").append("\n");
		replyto.append("\"suffix\": \"").append(profile.getUserData("user.notification.replyto.suffix")==null?"":profile.getUserData("user.notification.replyto.suffix")).append("\",").append("\n");
		replyto.append("\"prefix\": \"").append(profile.getUserData("user.notification.replyto.prefix")==null?"":profile.getUserData("user.notification.replyto.prefix")).append("\",").append("\n");
		replyto.append("\"store\":{").append("\n");
		replyto.append("\"mailStoreHost\": \"").append(profile.getUserData("user.notification.replyto.mailFile.hostName")).append("\",").append("\n");
		replyto.append("\"mailStoreUser\": \"").append(profile.getUserData("user.notification.replyto.mailFile.userID")).append("\",").append("\n");
		replyto.append("\"mailStorePassword\": \"").append(profile.getUserData("user.notification.replyto.mailFile.password")).append("\",").append("\n");
		replyto.append("\"mailStoreProtocol\": \"imap\"}").append("\n");
		replyto.append(" },").append("\n");
		
		profile.setUserData("user.notification.replyto.info", replyto.toString());
		
	}
	
	private void setUserDataJavaMail(String host,String port,String userId,String pwd,boolean isSSL){
//		"\"port\":\""+port+"\""
		profile = getProfile();
		profile.setUserData("user.notification.enabled", "true");
		profile.setUserData("user.notification.useJavaMailProvider", "true");
		profile.setUserData("user.notification.host", host.toLowerCase());
		
		profile.setUserData("user.notification.port", port);
		profile.setUserData("user.notification.user", userId);
		profile.setUserData("user.notification.password", pwd);
		
		profile.setUserData("user.notification.port.key.value", "\"port\": "+port+",");
		profile.setUserData("user.notification.user.key.value", "\"user\":\""+userId+"\",");
		profile.setUserData("user.notification.password.key.value", "\"password\":\""+pwd+"\",");
		profile.setUserData("user.notification.protocol.key.value", "\"protocol\":\""+( isSSL ? "smtps": "smtp" ) +"\",");
		
		profile.setUserData("user.notification.ssl.enabled", isSSL+"");
		profile.setUserData("user.notification.domain", "");
		
		//for dns url
		profile.setUserData("user.notification.dnshost", "");
		profile.setUserData("user.notification.dnsport", "");
		profile.setUserData("user.notification.dns.url.key.value", "");
		
		profile.setUserData("user.notification.enabled.key.value", "");
		
	}
	
	private void setUserDataDNS(String dnsHost,String dnsPort,String domain,String port,String userId,String pwd, boolean isSSL){
		
		profile = getProfile();
		profile.setUserData("user.notification.enabled", "true");
		profile.setUserData("user.notification.useJavaMailProvider", "false");
		profile.setUserData("user.notification.host", "");
		
		profile.setUserData("user.notification.port", port);
		profile.setUserData("user.notification.user", userId);
		profile.setUserData("user.notification.password", pwd);
		
		profile.setUserData("user.notification.port.key.value", "");
		profile.setUserData("user.notification.user.key.value", "");
		profile.setUserData("user.notification.password.key.value", "");
		profile.setUserData("user.notification.protocol.key.value", "");
		
		profile.setUserData("user.notification.ssl.enabled", isSSL+"");
		profile.setUserData("user.notification.domain", domain.toLowerCase());
		
		// for dns url
		//dns://dns.server.mycompany.com:53/mycompany.com
		dnsHost = dnsHost.toLowerCase();
		profile.setUserData("user.notification.dnshost", dnsHost);
		profile.setUserData("user.notification.dnsport", dnsPort);
		String dnsUrl = "dns://"+dnsHost+":"+dnsPort+"/"+dnsHost.substring(dnsHost.lastIndexOf('.', dnsHost.lastIndexOf('.')-1) + 1);
		//"notification": { "dnsURL": "dns://dns.server.mycompany.com:53/mycompany.com" },
		profile.setUserData("user.notification.dns.url.key.value", "\"notification\": { \"dnsURL\": \""+ dnsUrl +"\" },");
		
		profile.setUserData("user.notification.enabled.key.value", "");
		
	}
	
	
	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_NOTIFICATION_PANEL;
	}
	private boolean isCreate = false;
	@Override
	public void setVisible(boolean visible) {
		
		if(!isCreate){
			createControl(parent);
			isCreate = true;
		}
	}
	
	public void showErrorMessages(String messages) {
		setErrorMessage(messages);
		nextEnabled = false;
		setPageComplete(false);
	}
	
	private void verify() {
		if(enableNotificationButton != null && enableNotificationButton.getSelection() == true) {
			if(javaMailButton1 != null && javaMailButton1.getSelection())
				verifyCompleteJavaMail1();
			else if(dnsMXButton1 != null && dnsMXButton1.getSelection())
				verifyCompleteDNS1();
			setuserDataDisableReplyTo();
		} else if(enableReplyToButton != null && enableReplyToButton.getSelection() == true) {
			if(javaMailButton2 != null && javaMailButton2.getSelection()) {
				boolean flag = verifyCompleteJavaMail2();
				if(flag && enableReplyToButton != null && enableReplyToButton.getSelection())
					verifyCompeleteReplyTo1();
			}else if(dnsMXButton2 != null && dnsMXButton2.getSelection()) {
				boolean flag = verifyCompleteDNS2();
				if(flag && enableReplyToButton != null && enableReplyToButton.getSelection())
					verifyCompeleteReplyTo2();
			}
		} else if(mailDisableButton != null && mailDisableButton.getSelection() == true) {
			verifyCompleteNone();
		}
	}
}

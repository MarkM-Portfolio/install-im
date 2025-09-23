/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.update.ui.wizardPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.action.DMValidator;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;

public class WASAuthInfoWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(WASAuthInfoWizardPage.class);
	
	private Composite container;
	private FormToolkit toolkit;
	private ScrolledForm form;
	private Text userText;
	private Text pwdText;

	public WASAuthInfoWizardPage(){
		super(Constants.WIZARD_PAGE_WAS_AUTH_INFO);
		setTitle(Messages.getString("label.wizard.was.authentication.title"));
	}

	@Override
	public void onShow(Composite parent) {
		container = CommonHelper.createScrollableControl(Composite.class,
				parent, SWT.BORDER, SWT.V_SCROLL);
//		container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 10;
		gridLayout.horizontalSpacing = 5;
		container.setLayout(gridLayout);
	
		toolkit = new FormToolkit(container.getDisplay());
		form = toolkit.createScrolledForm(container);
		gridLayout = new GridLayout();
		form.getBody().setLayout(gridLayout);
		
		Section wasAuthSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		wasAuthSection.setText(Messages.getString("label.specify.was.authentication"));
		final Composite wasAuthSectionComposite = toolkit.createComposite(wasAuthSection);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		wasAuthSectionComposite.setLayout(gridLayout);
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		wasAuthSectionComposite.setLayoutData(gd);
		
		container.setBackground(wasAuthSectionComposite.getBackground());
		
		Label wasAuthSectionCompositeLabel = new Label(wasAuthSectionComposite, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		wasAuthSectionCompositeLabel.setLayoutData(gd);
		wasAuthSectionCompositeLabel.setText(Messages.getString("desc.was.info.collector"));
		
		Label userLabel = new Label(wasAuthSectionComposite, SWT.FILL);
		userLabel.setText(Messages.getString("label.was.info.admin"));
		userLabel.setVisible(true);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		userLabel.setLayoutData(gd);
		userText = new Text(wasAuthSectionComposite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		gd.widthHint = 170;
		userText.setLayoutData(gd);
		if(UpdateWizardInputs.getWASUserid(getWizard()) != null){
			userText.setText(UpdateWizardInputs.getWASUserid(getWizard()));
		}
		userText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent arg0) {
				UpdateWizardInputs.setWASUserid(getWizard(), userText.getText().trim());
			}
			
		});
		
		Label pwdLabel = new Label(wasAuthSectionComposite, SWT.FILL);
		pwdLabel.setText(Messages.getString("label.was.info.password"));
		pwdLabel.setVisible(true);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2000;
		pwdLabel.setLayoutData(gd);
		pwdText = new Text(wasAuthSectionComposite, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		gd.widthHint = 170;
		pwdText.setLayoutData(gd);
		if(UpdateWizardInputs.getWASPassword(getWizard())!= null){
			pwdText.setText(UpdateWizardInputs.getWASPassword(getWizard()));
		}
		pwdText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent arg0) {
				UpdateWizardInputs.setWASPassword(getWizard(), pwdText.getText().trim());
			}
			
		});
		
		/*Button validateButton = new Button(wasAuthSectionComposite, SWT.PUSH);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.heightHint = 27;
		gd.widthHint = 70;
		validateButton.setLayoutData(gd);
		validateButton.setText("Validate");
//		Font font = new Font(shell.getDisplay(), "Courier", 6, SWT.BOLD);
//		validateButton.setFont(font);
		validateButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				
				
			}
			
		});*/
		
		
		wasAuthSection.setClient(wasAuthSectionComposite);
		
		
		form.pack();
		setControl(container);
		
	}
	
	public boolean isInputValid() {
		if("".equals(userText.getText().trim())){
			showInputCheckWarningMsg(Messages.getString("validator.empty_string.message", Messages.getString("label.was.info.admin")));
			userText.setFocus();
			return false;
		}
		if("".equals(pwdText.getText().trim())){
			showInputCheckWarningMsg(Messages.getString("validator.empty_string.message", Messages.getString("label.was.info.password")));
			pwdText.setFocus();
			return false;
		}
		String dmProfilePath = LCUtil.getDMProfilePath(System.getProperty(com.ibm.lconn.common.LCUtil.LC_HOME) + File.separator + LCUtil.PRODUCTID_LC_CONFIGENGINE);
//		String sslReturnCode = SSLCertificateGetter.getSSLCertificate(dmProfilePath, userText.getText().trim(), pwdText.getText().trim());
		String result = "";
		try {
			String toFilePath = CommonHelper.copyAndGetLCScriptPath("wasAuthValidate.py", DMValidator.class, dmProfilePath + "/bin");
			result = DMValidator.getDMInfo(dmProfilePath, toFilePath, userText.getText().trim(), pwdText.getText().trim());
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
			result = "2";
		}
		if(!Constants.RETURN_CODE_0.equals(result)){
			showInputCheckWarningMsg(Messages.getString("was.connect.dm.error"));
			logger.log(Level.SEVERE, "Result ErrorCode: " + result);
			return false;
		}
		
		showValidationSuccessMessageDialog(Messages.getString("label.validation.success"));
		setErrorMessage(null);
		setPageComplete(true);
		System.setProperty("was_username", userText.getText().trim());
		System.setProperty("was_password", pwdText.getText().trim());
		return true;
	}
	
	// no use
	private void setChanged() {
		setPageComplete(false);
		setErrorMessage("Please click the validate button to continue.");
	}

}


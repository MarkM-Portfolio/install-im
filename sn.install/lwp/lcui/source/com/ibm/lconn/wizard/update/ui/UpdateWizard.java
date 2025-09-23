/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.update.ui;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.common.ui.SystemEnv;
import com.ibm.lconn.wizard.update.ui.wizardPage.Pages;
import com.ibm.lconn.wizard.update.ui.wizardPage.WelcomeWizardPage;

public class UpdateWizard extends Wizard {
	protected UpdateWizardDialog parentDialog;
	protected SystemEnv env;
	protected Object execResult;
	private List<Listener> pageRenderListener;
	public static final Logger logger = LogUtil.getLogger(UpdateWizard.class);
//	private static final String DIALOG_SETTING_FILE = "DBWizard" + Constants.FS
//			+ "settings.xml";

	public UpdateWizard() {
		setWindowTitle(Messages.getString("updateWizard.window.title"));
		setNeedsProgressMonitor(true);
//		loadImage();
	}

	public void addPages() {		
		addPage(Pages.getPage(Pages.WELCOME_PAGE));
		addPage(Pages.getPage(Pages.ACTION_SELECT));
		addPage(Pages.getPage(Pages.FIX_INFO_SELECT));
		addPage(Pages.getPage(Pages.WAS_AUTH_INFO));
		addPage(Pages.getPage(Pages.SUMMARY_PAGE));
		addPage(Pages.getPage(Pages.EXECUTION_PAGE));
		addPage(Pages.getPage(Pages.FINISH_PAGE));
		
		loadIcon();
	//	loadSettings();
	}

	public boolean performFinish() {
	//	saveSettings();
		getShell().dispose();
		return true;
	}

	private void loadIcon() {
		try {
			Image iconImage = ResourcePool.getImage(WelcomeWizardPage.class,
					"/icons/icon.GIF");

			getShell().setImage(iconImage);
		} catch (Exception e) {
//			logger.log(Level.SEVERE, "dbconfig.severe.load.icon.fail", e);
		}
	}

	private void loadImage() {
		try {
			ImageDescriptor logoImage = ImageDescriptor.createFromFile(
					UpdateWizard.class, "/icons/LogoImage.JPG");
			setDefaultPageImageDescriptor(logoImage);

		} catch (Exception e) {
//			logger.log(Level.SEVERE, "dbconfig.severe.load.image.fail", e);
		}
	}

	/*private void loadSettings() {
		try {
			this.getDialogSettings().load(DIALOG_SETTING_FILE);
		} catch (IOException e) {
//			logger.log(Level.SEVERE, Constants.WARN_DB_WIZARD_SETTING_MISSING, e);
		}
	}

	private void saveSettings() {
		try {
			// refreshSettings();
			this.getDialogSettings().save(DIALOG_SETTING_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IDialogSettings getDialogSettings() {
		IDialogSettings settings = super.getDialogSettings();
		if (settings == null) {
			settings = new DialogSettings(DIALOG_SETTING_FILE);
			try {
				settings.load(DIALOG_SETTING_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.setDialogSettings(settings);

		}
		return settings;
	}*/

	public boolean canFinish() {
		return CommonHelper.equals(Pages.getCurrentPage(this), Pages
				.getPage(Pages.FINISH_PAGE));
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer);
	}

	public UpdateWizardDialog getParentDialog() {
		return parentDialog;
	}

	public void setParentDialog(UpdateWizardDialog parentDialog) {
		this.parentDialog = parentDialog;
	}

	public SystemEnv getEnv() {
		if (env == null) {
			env = new SystemEnv();
		}
		return env;
	}

	public IWizardPage getNextPage() {
		if (getPageCount() == 0) {
			return null;
		}
		return getContainer().getCurrentPage().getNextPage();
	}

	public void setExecResult(Object obj) {
		this.execResult = obj;
	}

	public Object getExecResult() {
		return this.execResult;
	}

	public void firePageRenderAction(IWizardPage nextPage) {
		if (pageRenderListener != null) {
			Iterator<Listener> iterator = pageRenderListener.iterator();
			while (iterator.hasNext()) {
				Event event = new Event();
				event.data = nextPage;
				iterator.next().handleEvent(event);
			}
		}
	}
}

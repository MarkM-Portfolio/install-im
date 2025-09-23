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

package com.ibm.lconn.wizard.update.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.ibm.lconn.common.LCUtil;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.updateEvent;
import com.ibm.websphere.update.ioservices.IOService;
import com.ibm.websphere.update.ioservices.IOServicesException;
import com.ibm.websphere.update.ioservices.Notifier;
import com.ibm.websphere.update.ptf.EFixBatchUpdater;
import com.ibm.websphere.update.ptf.EFixImage;


/**
 * Class: EFixUninstall.java Abstract: Uninstalls eFixes Component Name: WAS.ptf Release: ASV50X History 1.2, 1/29/04 01-Nov-2002 Initial Version 16-Jan-2003 Updated to reset the product following the update.
 */

public class EFixUninstallAction {

	private Vector<updateEvent> updates = new Vector<updateEvent>();;
	private Vector updateImages = new Vector();;
	private String errorLogName;
	private Vector<String> uninstallOrder = new Vector<String>();;
	private Vector errors = new Vector();
	
	public boolean checkUninstallPrereq(List<EFixComponent> efixComponents){
		
		WPProduct wasp = new WPProduct(System.getProperty(LCUtil.LC_HOME));
		
		String productDirName = wasp.getProductDirName();
		String versionDirName = wasp.getVersionDirName();
		String historyDirName = WPHistory.determineHistoryDirName(versionDirName);
		WPHistory wph = new WPHistory(productDirName, versionDirName, historyDirName);
		
		Vector<String> imagesForUninstall = new Vector<String>();
		int componentsSize = efixComponents.size();
		for(int i=0; i<componentsSize; i++){
			imagesForUninstall.add(efixComponents.get(i).getIdStr());
		}
		
		EFixBatchUpdater ebu = new EFixBatchUpdater(wasp, wph, new NotifierImpl(),new IOService());
		ebu.testUninstallPrerequisites(imagesForUninstall, uninstallOrder, errors);
	
//		uninstall(uninstallOrder);
		
		return true;
	}
	
	public void uninstall(Vector<String> eFixIds) throws InterruptedException {
	
			WPProduct wasp = new WPProduct(System.getProperty(LCUtil.LC_HOME));
			
			String productDirName = wasp.getProductDirName();
			String versionDirName = wasp.getVersionDirName();
			String historyDirName = WPHistory.determineHistoryDirName(versionDirName);
			WPHistory wph = new WPHistory(productDirName, versionDirName, historyDirName);
			
			EFixBatchUpdater ebu = new EFixBatchUpdater(wasp, wph, new NotifierImpl(),new IOService());			

//					EFixBatchUpdater ebuer = new EFixBatchUpdater(wasp, wph, new IOService());

			uninstall(ebu, eFixIds);
		
	}

	public boolean uninstall(EFixBatchUpdater ebu, Vector<String> eFixIds)
		throws InterruptedException {

		boolean status = false;

		ebu.prepareEFixes(eFixIds
				);

		Vector updateEvents = ebu.uninstall(eFixIds);

		if (updateEvents.size() > 0) {
			if (ebu.didFail(updateEvents)) {
//				setUninstallFailed(true);
				setUpdates(updateEvents);
				
				updateEvent updateError = ebu.selectFailingEvent(updateEvents);
				String errorLogName = updateError.getLogName();
				setErrorLogName(errorLogName);
				
				status = false;
				throw new InterruptedException("failed");
			} else {
				setUpdates(updateEvents);
				status = true;
			}
		} else {
			status = false;
		}

		return status;
	}

	public Vector<updateEvent> getUpdates() {
		return updates;
	}

	public void setUpdates(Vector<updateEvent> updates) {
		this.updates = updates;
	}

	public Vector getUpdateImages() {
		return updateImages;
	}

	public void setUpdateImages(Vector updateImages) {
		this.updateImages = updateImages;
	}

	public String getErrorLogName() {
		return errorLogName;
	}

	public void setErrorLogName(String errorLogName) {
		this.errorLogName = errorLogName;
	}

	public Vector<String> getUninstallOrder() {
		return uninstallOrder;
	}

	public void setUninstallOrder(Vector<String> uninstallOrder) {
		this.uninstallOrder = uninstallOrder;
	}

	public Vector getErrors() {
		return errors;
	}

	public void setErrors(Vector errors) {
		this.errors = errors;
	}
	
	class NotifierImpl implements Notifier {

		public void setTaskCount(int taskCount) {
		}

		// Answer the count of tasks to be performed.

		public int getTaskCount() {
			return 0;
		}

		// Answer the current task number.  This is
		// reset to 0 when the task count is set.

		public int getTaskNumber() {
			return 0;
		}

		// Push a banner.

		public void pushBanner(String banner) {
		}

		// Pop (and return) the last banner.

		public String popBanner() {
			return null;
		}

		// Collate the banners on the banner stack.
		public String collateBanners() {
			return null;
		}

		// Replace the current banner (the tail of the banner stack).

		public String replaceBanner(String banner) {
			return null;
		}

		// Indicate that a task has been started.
		// Answer the current notification text.

		public String beginTask() {
			return null;
		}

		// Indicate that another task was completed.
		// Answer the current notification text.

		public String endTask() {
			return null;
		}

		// Tell if processing was cancelled.

		public boolean wasCancelled() {
			return false;
		}

	}
	
	public static void main(String[] args) {
		System.setProperty(LCUtil.LC_HOME, "C:/Program Files/IBM/IBM-Connections");
		List<EFixComponent> efixComponents = new ArrayList<EFixComponent>();
		try {
			efixComponents = FixInfoDetect.getInstalledFixesInfo(System.getProperty(LCUtil.LC_HOME));
		} catch (IOServicesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new EFixUninstallAction().checkUninstallPrereq(efixComponents);
		
	}

}

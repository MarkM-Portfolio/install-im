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
import com.ibm.websphere.update.ioservices.Notifier;
import com.ibm.websphere.update.ptf.EFixBatchUpdater;
import com.ibm.websphere.update.ptf.EFixImage;

public class EFixInstallAction {
	
	private Vector<updateEvent> updates = new Vector();;
	private Vector updateImages = new Vector();;
	private String errorLogName;
	private Vector<EFixImage> installOrder = new Vector();;
	private Vector errors = new Vector();
	private Vector supersededInfo = new Vector();
	
	private static WPProduct wasp;
	private static WPHistory wash;
	
	static {
		wasp = new WPProduct(System.getProperty(LCUtil.LC_HOME));
		
		String productDirName = wasp.getProductDirName();
		String versionDirName = wasp.getVersionDirName();
		String historyDirName = WPHistory.determineHistoryDirName(versionDirName);
		wash = new WPHistory(productDirName, versionDirName, historyDirName);
	}
	
	public void checkInstallPrereqs(List<EFixComponent> efixComponents){
	
		Vector<EFixImage> imagesForInstall = new Vector<EFixImage>();
		int componentsSize = efixComponents.size();
		for(int i=0; i<componentsSize; i++){
			imagesForInstall.add(efixComponents.get(i).getEFixImage());
		}
		
		EFixBatchUpdater ebu = new EFixBatchUpdater(wasp, wash, new NotifierImpl(),new IOService());
		ebu.testInstallPrerequisites(imagesForInstall, installOrder, errors, supersededInfo);	
	}
	
	public List<EFixComponent> filterWithSuperseding(List<EFixComponent> efixComponents){
		List<EFixComponent> components = new ArrayList<EFixComponent>();
		
		if(installOrder != null && efixComponents != null 
				&& installOrder.size() != 0 && efixComponents.size()!=0 
				){
			if(installOrder.size() == efixComponents.size()){
				components = efixComponents;
			}else{
				Vector<String> efixIds = new Vector<String>();
				for(int i=0; i<installOrder.size(); i++){
					efixIds.add(installOrder.get(i).getEFixId());
				}
				
				for(int i=0; i<efixComponents.size(); i++){
					if(efixIds.contains(efixComponents.get(i).getAparNum())){
						components.add(efixComponents.get(i));
					}
				}
			}	
		}
			
		return components;
	}
	
	public void install (Vector<EFixImage> eFixImages) throws InterruptedException {
			
			EFixBatchUpdater ebu = new EFixBatchUpdater(wasp, wash, new NotifierImpl(),new IOService());			

//					EFixBatchUpdater ebuer = new EFixBatchUpdater(wasp, wph, new IOService());

			install(ebu, eFixImages);

	}
	
	public boolean install(EFixBatchUpdater ebu, Vector<EFixImage> eFixImages) throws InterruptedException {

		boolean status = false;

		ebu.prepareImages(eFixImages);
		setUpdateImages(eFixImages);
		Vector<updateEvent> updateEvents = ebu.install(eFixImages);
				
		if (updateEvents.size() > 0) {			
			if (ebu.wasCancelled(updateEvents)) {
//				setCancelledByUser(true);
				setUpdates(updateEvents);
				status = false;
				throw new InterruptedException("cancelled");

			} else if (ebu.didFail(updateEvents)) {
//				setInstallFailed(true);
				setUpdates(updateEvents);

				updateEvent updateError = ebu.selectFailingEvent(updateEvents);
				setErrorLogName(updateError.getLogName());	
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

	public Vector<EFixImage> getInstallOrder() {
		return installOrder;
	}

	public void setInstallOrder(Vector<EFixImage> installOrder) {
		this.installOrder = installOrder;
	}

	public Vector getErrors() {
		return errors;
	}

	public void setErrors(Vector errors) {
		this.errors = errors;
	}

	public Vector getSupersededInfo() {
		return supersededInfo;
	}

	public void setSupersededInfo(Vector supersededInfo) {
		this.supersededInfo = supersededInfo;
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
	
}

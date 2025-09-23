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

package com.ibm.websphere.update.actions;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.lconn.wizard.update.data.EFixDataModel;
import com.ibm.lconn.wizard.update.data.UpdateComponent;
import com.ibm.lconn.wizard.update.data.UpdateListingData;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.efixApplied;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.history.xml.efixPrereq;
import com.ibm.websphere.product.history.xml.platformPrereq;
import com.ibm.websphere.product.history.xml.productPrereq;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.update.ioservices.IOServicesException;
import com.ibm.websphere.update.ioservices.StandardIOServiceFactory;
import com.ibm.websphere.update.msg.InstallerMessages;
import com.ibm.websphere.update.ptf.EFixImage;
import com.ibm.websphere.update.ptf.ImageRepository;

public class EFixProcessor {

	EFixDataModel efdm;
	AbstractTableModel dm;
	String versionDirName;
	String reposDirName;
	int installableCount = 0 ;
	int offset = 0 ;
	Vector efixIdContainer;
	Vector cExceptions = new Vector();
	ArrayList efixContainer = new ArrayList();
	ArrayList filteredComponents = new ArrayList();
	ImageRepository efir;
	WPProduct wasp;
	WPHistory wash;
	private boolean modelReady = true;

	public EFixProcessor(){
		
	}
	
	public EFixProcessor(EFixDataModel efdm, String versionDirName) {
		this.efdm = efdm;
		this.versionDirName = versionDirName;
	}
	
	public EFixProcessor(EFixDataModel efdm, String reposDirName, String versionDirName) {
		this.efdm = efdm;
		this.reposDirName = reposDirName;
		this.versionDirName = versionDirName;
	}

	public AbstractTableModel process() {
		try {
			wasp = new WPProduct();

			String productDirName = wasp.getProductDirName();
			String versionDirName = wasp.getVersionDirName();
			String historyDirName = WPHistory.determineHistoryDirName(versionDirName);

			wash = new WPHistory(productDirName, versionDirName, historyDirName);

			efir = new ImageRepository(new StandardIOServiceFactory(), wasp.getDTDDirName(), reposDirName, ImageRepository.EFIX_IMAGES);

			efir.prepare();

			efixIdContainer = efir.getEFixIds();
			Hashtable efixTable = efir.getEFixImages();

			for (int a = 0; a < efixIdContainer.size(); a++) {
				String efixId = (String) efixIdContainer.elementAt(a);
				efixContainer.add(efixTable.get(efixId));
			}

			if (efixTable.isEmpty()) {
				modelReady = false;
			} else {

				modelReady = true;

				int numEfixes = efixContainer.size();

				EFixImage efi;
				efix eFix;
				for (int i = 0; i < numEfixes; i++) {
					efi = (EFixImage) efixContainer.get(i);

					efi.prepareDriver();
					efi.prepareComponents();
					efixDriver efixDriverObj = efi.getEFixDriver();

					EFixComponent efc = new EFixComponent();
					packageEfixComponents(efc, wasp, wash, efixDriverObj, efi, i);

					if (!efc.getInstallState().equals("installed")) {
						++installableCount;
						filteredComponents.add(efc);
					}
				}

				int dmSize = installableCount;

				//efdm.initializeData(dmSize);
				refreshListing(efdm, filteredComponents, dmSize);

//				if (installableCount > 0)
//					installableComponents = true;
			}
		} catch (IOException ioe) {
			modelReady = false;
			cExceptions.add(ioe);
		} catch (IOServicesException iose) {
			modelReady = false;
			cExceptions.add(iose);
		} catch (BaseHandlerException fe) {
			modelReady = false;
			cExceptions.add(fe);
		} catch (Throwable th){
			modelReady = false;				
		} finally {								
//			if (!modelReady) {
//				dm = new EFixDefaultDataModel("installer");						
//				setConsumedExceptions(cExceptions);
//				
//				if(efir.hasFaultyJars()){
//					setUnpreparedJars(efir.getFaultyJars());
//				}
//			}

		}

//		if (modelReady && installableComponents) {
//			dm = efdm;
//		} else if (modelReady && !installableComponents) {
//			dm = new EFixDefaultDataModel("installer");
//		}

		return dm;
	}
	
	

	public void refreshListing(EFixDataModel efdm, ArrayList filteredComponents, int dmSize) {

		Boolean defState = new Boolean("false");
		for (int j = 0; j < dmSize; j++) {
			EFixComponent efc = (EFixComponent) filteredComponents.get(j);
			UpdateListingData uld = new UpdateListingData(defState, efc);
			efdm.setEfixData(uld);
		}

	}

	public void packageEfixComponents(EFixComponent efc, WPProduct wasp, WPHistory wash, efixDriver eFix, EFixImage efi, int rowID) {

		String efixID = eFix.getId();
		int status = 0;

		if (wash.efixAppliedPresent(efixID)) {
			status = processEfixStatus(efixID, wasp, wash, efi);
			if (status == 0) {

				efc.setEFixImage(efi);
				efc.setId(rowID);
				efc.setIdStr(eFix.getId());
				efc.setInstallDate(eFix.getBuildDate());
				efc.setInstallState("installed");
				efc.setInstallDescShort(eFix.getShortDescription());
				efc.setInstallDescLong(eFix.getLongDescription());
				efc.setAparNum(eFix.getAPARNumber());
				efc.setBuildVersion(eFix.getBuildVersion());
				efc.setPmrNum("");
				efc.setPrereqs(constructPrereqDisplay(eFix));
			} else {
				efc.setEFixImage(efi);
				efc.setId(rowID);
				efc.setIdStr(eFix.getId());
				efc.setInstallDate(eFix.getBuildDate());
				efc.setInstallState("partially_installed");
				efc.setInstallDescShort(eFix.getShortDescription());
				efc.setInstallDescLong(eFix.getLongDescription());
				efc.setAparNum(eFix.getAPARNumber());
				efc.setBuildVersion(eFix.getBuildVersion());
				efc.setPmrNum("");
				efc.setPrereqs(constructPrereqDisplay(eFix));
			}
		} else {
			efc.setEFixImage(efi);
			efc.setId(rowID);
			efc.setIdStr(eFix.getId());
			efc.setInstallDate(eFix.getBuildDate());
			efc.setInstallState("not_installed");
			efc.setInstallDescShort(eFix.getShortDescription());
			efc.setInstallDescLong(eFix.getLongDescription());
			efc.setAparNum(eFix.getAPARNumber());
			efc.setBuildVersion(eFix.getBuildVersion());
			efc.setPmrNum("");
			efc.setPrereqs(constructPrereqDisplay(eFix));
		}
	}
	
	public void packageEfixComponents(
			WPProduct wasp,
			WPHistory wash,
			efixDriver eFix,
			UpdateComponent uc,
			int rowID) {

			EFixComponent efc = (EFixComponent) uc;
			String efixId = eFix.getId();
			efixApplied eA = wash.getEFixAppliedById(efixId);
			if (eA != null) {
				int numCompApplied = eA.getComponentAppliedCount();
				int numCompTotal = eFix.getComponentUpdateCount();

				if (numCompApplied < numCompTotal) {
					efc.setId(rowID);
					efc.setIdStr(eFix.getId());
					efc.setInstallDate(eFix.getBuildDate());

					efc.setInstallState("partially_installed");
					efc.setInstallDescShort(eFix.getShortDescription());
					efc.setInstallDescLong(eFix.getLongDescription());
					efc.setAparNum(eFix.getAPARNumber());
					efc.setBuildVersion(eFix.getBuildVersion());
					efc.setPmrNum("");
					efc.setPrereqs(getEfixPrereq(eFix));
				} else {
					efc.setId(rowID);
					efc.setIdStr(eFix.getId());
					efc.setInstallDate(eFix.getBuildDate());
					efc.setBuildVersion(eFix.getBuildVersion());
					efc.setInstallState("installed");
					efc.setInstallDescShort(eFix.getShortDescription());
					efc.setInstallDescLong(eFix.getLongDescription());
					efc.setAparNum(eFix.getAPARNumber());
					efc.setPmrNum("");
					efc.setPrereqs(getEfixPrereq(eFix));
				}

			} else {
				//efixApplied file not found (shouldn't occur)
				efc.setId(rowID);
				efc.setIdStr(eFix.getId());
				efc.setInstallDate(eFix.getBuildDate());

				efc.setInstallState("partially_installed");
				efc.setInstallDescShort(eFix.getShortDescription());
				efc.setInstallDescLong(eFix.getLongDescription());
				efc.setAparNum(eFix.getAPARNumber());
				efc.setBuildVersion(eFix.getBuildVersion());
				efc.setPmrNum("");
				efc.setPrereqs(getEfixPrereq(eFix));
			}
		}

	public String getEfixPrereq(efixDriver eFix) {
		int count = eFix.getEFixPrereqCount();
		StringBuffer resultBuffer = new StringBuffer();

		String negativeEfix = InstallerMessages.getString("label.negative.efix");
		String efixSeparator = InstallerMessages.getString("label.efix.separator");

		for (int i = 0; i < count; i++) {
			efixPrereq nextPrereq = eFix.getEFixPrereq(i);

			if (i != 0)
				resultBuffer.append(efixSeparator);
			if (nextPrereq.getIsNegativeAsBoolean())
				resultBuffer.append(negativeEfix);

			resultBuffer.append(nextPrereq.getEFixId());
		}

		return resultBuffer.toString();
	}

	public String getProductPrereq(efixDriver eFix) {

		int prereqCount = eFix.getProductPrereqCount();
		StringBuffer resultBuffer = new StringBuffer();

		String efixSeparator = InstallerMessages.getString("label.efix.separator");

		for (int prereqNo = 0; prereqNo < prereqCount; prereqNo++) {

			productPrereq nextPrereq = eFix.getProductPrereq(prereqNo);
			if (prereqNo != 0)
				resultBuffer.append(efixSeparator);

			resultBuffer.append(nextPrereq.getProductId());
		}

		return resultBuffer.toString();

	}

	public String getPlatformPrereq(efixDriver eFix) {

		int prereqCount = eFix.getPlatformPrereqCount();

		StringBuffer resultBuffer = new StringBuffer();

		for (int prereqNo = 0; prereqNo < prereqCount; prereqNo++) {
			platformPrereq nextPrereq = eFix.getPlatformPrereq(prereqNo);

			if (prereqNo != 0)
				resultBuffer.append(" ");

			resultBuffer.append(nextPrereq.getOSPlatform());
			resultBuffer.append(nextPrereq.getArchitecture());
			resultBuffer.append(nextPrereq.getOSVersion());
		}

		return resultBuffer.toString();
	}

	public String constructPrereqDisplay(efixDriver eFix) {
		return LCUtil.constructPrereqDisplay(eFix);
//		String productPrereqLabel = "Product prerequisite: {0}";
//		String platformPrereqLabel = "Platform prerequsite: {0}";
//		String efixPrereqLabel = "EFix prerequsite: {0}";
//		String efixPrereq = getEfixPrereq(eFix);
//		String productPrereq = getProductPrereqWithVersion(eFix);
//		String platformPrereq = getPlatformPrereq(eFix);
//		if("***".equals(efixPrereq)) efixPrereq = "";
//		if("***".equals(productPrereq)) productPrereq = "";
//		if("***".equals(platformPrereq)) platformPrereq = "";
//		StringBuffer sb = new StringBuffer();
//		if(!"".equals(efixPrereq)){
//			String efixPrereqDetail = MessageFormat.format(efixPrereqLabel, new Object[]{efixPrereq});
//			sb.append(efixPrereqDetail+"\n");
//		}
//		if(!"".equals(productPrereq)){
//			String productPrereqDetail = MessageFormat.format(productPrereqLabel, new Object[]{productPrereq});
//			sb.append(productPrereqDetail+"\n");
//		}
//		if(!"".equals(platformPrereq)){
//			String platformPrereqDetail = MessageFormat.format(platformPrereqLabel, new Object[]{platformPrereq});
//			sb.append(platformPrereqDetail+"\n");
//		}
//
//		return sb.toString();
	}

	private String getProductPrereqWithVersion(efixDriver eFix) {
		int prereqCount = eFix.getProductPrereqCount();
		StringBuffer resultBuffer = new StringBuffer();

		String efixSeparator = InstallerMessages.getString("label.efix.separator");
		String productPreqWithVersion = "{0}({1})";

		for (int prereqNo = 0; prereqNo < prereqCount; prereqNo++) {

			productPrereq nextPrereq = eFix.getProductPrereq(prereqNo);
			if (prereqNo != 0)
				resultBuffer.append(efixSeparator);

			resultBuffer.append(MessageFormat.format(productPreqWithVersion, new Object[]{nextPrereq.getProductId(), nextPrereq.getBuildVersion()}));
		}

		return resultBuffer.toString();
	}

	public int processEfixStatus(String efixID, WPProduct wasp, WPHistory wash, EFixImage efi) {
		Vector compNames = null;
		int compFound = 0;
		int status = 0;

		compNames = efi.getComponentNames();
		int compCount = efi.getComponentCount();
		for (int i = 0; i < compCount; i++) {
			String compName = (String) compNames.elementAt(i);
			if (wash.efixComponentAppliedPresent(efixID, compName)) {
				compFound++;
			}
		}

		if (compFound == compCount) {
			status = 0;
		} else {
			status = 1;
		}

		return status;
	}
} 
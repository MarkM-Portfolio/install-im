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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ibm.lconn.common.LCUtil;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.update.actions.EFixProcessor;
import com.ibm.websphere.update.efix.prereq.EFixPrereqChecker;
import com.ibm.websphere.update.ioservices.IOServicesException;
import com.ibm.websphere.update.ioservices.StandardIOServiceFactory;
import com.ibm.websphere.update.ptf.EFixImage;
import com.ibm.websphere.update.ptf.ImageRepository;

public class FixInfoDetect {
	private static boolean modleReady;
	private static Vector<String> errors = new Vector();
	private static Vector<String> supersededInfo = new Vector();
	private static WPProduct wasp;
	private static WPHistory wash;
	
	static {
		wasp = new WPProduct(System.getProperty(LCUtil.LC_HOME));
		
		String productDirName = wasp.getProductDirName();
		String versionDirName = wasp.getVersionDirName();
		String historyDirName = WPHistory.determineHistoryDirName(versionDirName);
		wash = new WPHistory(productDirName, versionDirName, historyDirName);	
//		System.out.print("##########: " + System.getProperty(LCUtil.LC_HOME));
	}
	
	public static List<EFixComponent> getNotInstalledFixesInfo(String fixDir) throws IOServicesException{
		List<EFixComponent> components = new ArrayList<EFixComponent>();
		List<EFixComponent> eFixComponents = getFixesInfo(fixDir);
		for(EFixComponent eFixComponent : eFixComponents){
			if("not_installed".equals(eFixComponent.getInstallState())){
				components.add(eFixComponent);
			}
		}
		
//		components = filterWithSuperseding(components);
//		components = filterWithVersion(components);
		
		return components;
	}
	
	public static List<EFixComponent> getInstalledFixesInfo(String fixDir) throws IOServicesException{
		List<EFixComponent> installedEFixComponents = new ArrayList<EFixComponent>();
		List<EFixComponent> eFixComponents = getFixesInfo(fixDir);
		for(EFixComponent eFixComponent : eFixComponents){
			if("installed".equals(eFixComponent.getInstallState()) || "partially_installed".equals(eFixComponent.getInstallState())){
				installedEFixComponents.add(eFixComponent);
			}
		}
		return installedEFixComponents;
	}
	
	public static List<EFixComponent> getFixesInfo(String fixDir) throws IOServicesException{
		
		List<EFixComponent> efixComponents = new Vector<EFixComponent>();
		
		ImageRepository efir = new ImageRepository(new StandardIOServiceFactory(), wasp.getDTDDirName(), fixDir, ImageRepository.EFIX_IMAGES);
		
		try {
			efir.prepare();
		} catch (IOServicesException e) {
			throw e;
		}
		
		Vector efixIdContainer = efir.getEFixIds();
		EFixImage efixImage = null;
		EFixProcessor efixProcessor = new EFixProcessor();
		for(int i=0; i< efixIdContainer.size(); i++){
			efixImage = (EFixImage)efir.getEFixImages().get(efixIdContainer.elementAt(i));
			try {
				efixImage.prepareDriver();
				efixImage.prepareComponents();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (BaseHandlerException e) {
				e.printStackTrace();
			}
			EFixComponent efixComponent = new EFixComponent();
			efixProcessor.packageEfixComponents(efixComponent, wasp, wash, efixImage.getEFixDriver(), efixImage, i);
			efixComponents.add(efixComponent);
		}
		
		return efixComponents;
	}
	
	public static List<EFixComponent> getInstalledFixesInfo() throws IOServicesException{
		List<EFixComponent> efixComponents = new Vector<EFixComponent>();
		
		if (wasp.numExceptions() > 0) {

//			JOptionPane.showMessageDialog(
//				null,
//				InstallerMessages.getString(
//					"label.product.directory.error"));

		} else {
			Iterator efixContainer = wash.getEFixDrivers();
			ArrayList<String> efixInstallDate = wash.getInstalledTime();
			int numEfixes = wash.getEFixDriverCount();
			if ((efixContainer != null) && (numEfixes > 0)) {
				EFixProcessor efixProcessor = new EFixProcessor();
				efixDriver eFix;
				String efixInstalledTime;
				int i = 0;
				while (efixContainer.hasNext()) {
					eFix = (efixDriver) efixContainer.next();
					efixInstalledTime = (String)efixInstallDate.get(i);
					EFixComponent efc = new EFixComponent();
					efixProcessor.packageEfixComponents(wasp, wash, eFix, efc, i);
					efixComponents.add(efc);
					efc.setInstalledTime(efixInstalledTime);
					++i;
				}
			} else {
				modleReady = false;
			}
		}
		
		return efixComponents;
	}
	
	public static List<EFixComponent> filterWithSuperseding(List<EFixComponent> efixComponents){
		List<EFixComponent> components = new ArrayList<EFixComponent>();
		
		Vector<efixDriver> efixDrivers = new Vector<efixDriver>();
		for(int i=0; i<efixComponents.size(); i++){
			efixDrivers.add(efixComponents.get(i).getEFixImage().getEFixDriver());
		}
		
		EFixPrereqChecker checker =  new EFixPrereqChecker(wasp, wash);
		efixDrivers = checker.removeEFixByInternalSuperseding(efixDrivers, supersededInfo);
		
		Vector<String> efixIds = new Vector<String>();
		for(int i=0; i<efixDrivers.size(); i++){
			efixIds.add(efixDrivers.get(i).getAPARNumber());
		}
		
		for(int i=0; i<efixComponents.size(); i++){
			if(efixIds.contains(efixComponents.get(i).getAparNum())){
				components.add(efixComponents.get(i));
			}
		}
			
		return components;
	}
	
	public static List<EFixComponent> filterWithVersion(List<EFixComponent> efixComponents){
		List<EFixComponent> components = new ArrayList<EFixComponent>();
		
		Vector<efixDriver> efixDrivers = new Vector<efixDriver>();
		for(int i=0; i<efixComponents.size(); i++){
			efixDrivers.add(efixComponents.get(i).getEFixImage().getEFixDriver());
		}
		
		Vector<efixDriver> efixDriversWithCorrectVersion = new Vector<efixDriver>();
		EFixPrereqChecker checker =  new EFixPrereqChecker(wasp, wash);
		checker.testProductPrereqs(efixDrivers, efixDriversWithCorrectVersion, errors);
		
		Vector<String> efixIds = new Vector<String>();
		for(int i=0; i<efixDriversWithCorrectVersion.size(); i++){
			efixIds.add(efixDriversWithCorrectVersion.get(i).getAPARNumber());
		}
		
		for(int i=0; i<efixComponents.size(); i++){
			if(efixIds.contains(efixComponents.get(i).getAparNum())){
				components.add(efixComponents.get(i));
			}
		}
		
		return components;
	}
	
	public static Vector<String> getErrors() {
		return errors;
	}

	public static void setErrors(Vector<String> errors) {
		FixInfoDetect.errors = errors;
	}

	public static Vector<String> getSupersededInfo() {
		return supersededInfo;
	}

	public static void setSupersededInfo(Vector<String> supersededInfo) {
		FixInfoDetect.supersededInfo = supersededInfo;
	}

	public static void main(String[] args) throws IOServicesException{
		System.out.println(getInstalledFixesInfo("C:/Program Files/IBM/IBM-Connections"));
	}

}

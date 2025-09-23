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

/* @copyright module */

package com.ibm.websphere.update.harness;

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.WPProduct;
import java.io.*;

/**
 * Class: UpdateHarnessController.java Abstract: Delegates the correct strategy
 * based on the product input. Component Name: WAS.ptf Release: ASV50X History
 * 1.4, 3/20/03 01-Feb-2003 Initial Version
 */

public class UpdateHarnessController {

	// ********************************************************
	// Debugging Utilities
	// ********************************************************
	public static final String debugPropertyName = "com.ibm.websphere.update.harness.debug";
	// ********************************************************
	// Debugging Utilities
	// ********************************************************
	public static final String debugTrueValue = "true";
	// ********************************************************
	// Debugging Utilities
	// ********************************************************
	public static final String debugFalseValue = "false";

	// Debugging support ...
	protected static boolean debug;

	static {
		String debugValue = System.getProperty(debugPropertyName);

		debug = ((debugValue != null) && debugValue.equals(debugTrueValue));
	}

	/**
	 * @return the debug
	 * @uml.property name="debug"
	 */
	public static boolean isDebug() {
		return debug;
	}

	public static void debug(String arg) {
		if (!debug)
			return;

		System.out.println(arg);
	}

	public static void debug(String arg1, String arg2) {
		if (!debug)
			return;

		System.out.print(arg1);
		System.out.println(arg2);
	}

	// ********************************************************
	// Program Versioning
	// ********************************************************
	public static final String pgmVersion = "1.4";
	// ********************************************************
	// Program Versioning
	// ********************************************************
	public static final String pgmUpdate = "3/20/03";

	// ********************************************************
	// Instance State
	// ********************************************************
	private String productDir;

	private String ihsVerifyToken = "conf" + File.separator + "httpd.conf";
	private String wasVerifyToken = "config";
	private String wasLiteVerifyToken = "java";
	private String wasPluginVerifyToken = "bin";
	private String wasPluginLibVerifyToken = "lib";

	private UpdateHarnessManager whm = null;

	public UpdateHarnessController(String productDir) {
		this.productDir = productDir;
	}

	// ********************************************************
	// Method Definition
	// ********************************************************

	public boolean performStrategy() {

		debug("Initialized UpdateHarnessManager object");

		String currentProduct = getProductDir();
		debug("Current product : " + currentProduct);

		debug("Testing product type...");
		if (isLCActivities()) {
			debug("Detected IBM Connections Activities");
			whm = new LCActivitiesUpdateHarnessManager(currentProduct);
		} else if (isLCBlogs()) {
			debug("Detected IBM Connections Blogs");
			whm = new LCBlogUpdateHarnessManager(currentProduct);
		} else if (isLCCommunities()) {
			debug("Detected IBM Connections Communities");
			whm = new LCCommunitiesUpdateHarnessManager(currentProduct);
		} else if (isLCDogear()) {
			debug("Detected IBM Connections Dogear");
			whm = new LCDogearUpdateHarnessManager(currentProduct);
		} else if (isLCProfiles()) {
			debug("Detected IBM Connections Profiles");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCHomepage()) {
			debug("Detected IBM Connections Homepage");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCWikis()) {
			debug("Detected IBM Connections Wikis");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCFiles()) {
			debug("Detected IBM Connections Files");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCSearch()) {
			debug("Detected IBM Connections Search");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCMobile()) {
			debug("Detected IBM Connections Mobile");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCNews()) {
			debug("Detected IBM Connections News");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCForum()) {
			debug("Detected IBM Connections Forum");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCModeration()){
			debug("Detected IBM Connections Moderation");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isLCMetrics()){
			debug("Detected IBM Connections Metrics");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if(isLCCCM()){
			debug("Detected IBM Connections CCM");
			whm = new LCProfilesUpdateHarnessManager(currentProduct);
		} else if (isWASLite()) {
			debug("Detected WAS-Lite product");
			whm = new WASUpdateHarnessManager(currentProduct,
					UpdateProductType.WAS_LITE);
		} else if (isIHS()) {
			debug("Detected IHS product");
			whm = new WASUpdateHarnessManager(currentProduct,
					UpdateProductType.IHS);
		} else if (isWASPlugin()) {
			debug("Detected WAS-Plugin product");
			whm = new WASUpdateHarnessManager(currentProduct,
					UpdateProductType.WAS_PLUGIN);
		} else if (isWPCP()) {
			debug("Detected WPCP product");
			whm = new WASUpdateHarnessManager(currentProduct,
					UpdateProductType.WPCP);
		} else if (isWCS()) {
			debug("Detected WCS product");
			whm = new WCSUpdateHarnessManager(currentProduct);
		} else if (isTMS()) {
			debug("Detected TMS product");
			whm = new TMSUpdateHarnessManager(currentProduct);
		} else if (isWSE()) {
			debug("Detected WSE product");
			whm = new WSEUpdateHarnessManager(currentProduct);
		} else if (isWEMP()) {
			debug("Detected WEMP product");
			whm = new WEMPUpdateHarnessManager(currentProduct);
		} else if (isPDM()) {
			debug("Detected PDM product");
			whm = new PDMUpdateHarnessManager(currentProduct);
		} else if (isWCM()) {
			debug("Detected WCM product");
			whm = new WCMUpdateHarnessManager(currentProduct);
		} else if (isDOC()) {
			debug("Detected DOC product");
			whm = new DOCUpdateHarnessManager(currentProduct);
		} else if (isTS()) {
			debug("Detected TS product");
			whm = new TSUpdateHarnessManager(currentProduct);
		} else if (isLRNWPS()) {
			debug("Detected LRNWPS product");
			whm = new LRNWPSUpdateHarnessManager(currentProduct);
		} else if (isLRNSRVR()) {
			debug("Detected LRNSRVR product");
			whm = new LRNSRVRUpdateHarnessManager(currentProduct);
		} else if (isIHOTFIX()) {
			debug("Detected IHOTFIX product");
			whm = new IHOTFIXUpdateHarnessManager(currentProduct);
		} else if (isWBSE()) {
			debug("Detected WBSE product");
			whm = new WBSEUpdateHarnessManager(currentProduct);
		} else if (isPZNStandalone()) {
			debug("Detected PZN Standalone");
			whm = new WPPZNUpdateHarnessManager(currentProduct);
		} 

		if (whm == null) {
			debug("Could not detect a suitable product to harness update strategy");
			debug("Returning false");
			return false;
		}

		return whm.harness();

	}

	private boolean isLCCCM() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_CCM) != null);
		} else {
			return false;
		}
	}

	private boolean isIHS() {
		return (new File(this.productDir + File.separator + ihsVerifyToken)
				.exists());
	}

	private boolean isWASLite() {
		return (new File(this.productDir + File.separator + wasLiteVerifyToken)
				.exists())
				&& !(new File(this.productDir + File.separator + wasVerifyToken)
						.exists());
	}

	private boolean isWPCP() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("WPCP") != null);
		} else {
			return false;
		}
	}

	private boolean isWCS() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("WCS") != null);
		} else {
			return false;
		}
	}

	private boolean isTMS() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("TMS") != null);
		} else {
			return false;
		}
	}

	private boolean isWBSE() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("WBSE") != null);
		} else {
			return false;
		}
	}

	private boolean isWSE() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("WSE") != null);
		} else {
			return false;
		}
	}

	private boolean isWEMP() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("WEMP") != null);
		} else {
			return false;
		}
	}

	private boolean isPDM() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("PDM") != null);
		} else {
			return false;
		}
	}

	private boolean isWCM() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("WCM") != null);
		} else {
			return false;
		}
	}

	private boolean isDOC() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("DOC") != null);
		} else {
			return false;
		}
	}

	private boolean isTS() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("TS") != null);
		} else {
			return false;
		}
	}

	private boolean isLRNWPS() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("LRNWPS") != null);
		} else {
			return false;
		}
	}

	private boolean isLRNSRVR() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("LRNSRVR") != null);
		} else {
			return false;
		}
	}

	private boolean isIHOTFIX() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("ISeries Hotfix") != null);
		} else {
			return false;
		}
	}

	private boolean isPZNStandalone() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById("PZN") != null);
		} else {
			return false;
		}
	}

	private boolean isLCActivities() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_ACTIVITIES) != null);
		} else {
			return false;
		}
	}

	private boolean isLCBlogs() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_BLOGS) != null);
		} else {
			return false;
		}
	}

	private boolean isLCCommunities() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_COMMUNITIES) != null);
		} else {
			return false;
		}
	}

	private boolean isLCDogear() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_DOGEAR) != null);
		} else {
			return false;
		}
	}

	private boolean isLCProfiles() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_PROFILES) != null);
		} else {
			return false;
		}
	}

	private boolean isLCHomepage() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_HOMEPAGE) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCWikis() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_WIKIS) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCFiles() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_FILES) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCSearch() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_SEARCH) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCMobile() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_MOBILE) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCNews() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_NEWS) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCForum() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_FORUM) != null);
		} else {
			return false;
		}
	}
	private boolean isLCModeration() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_MODERATION) != null);
		} else {
			return false;
		}
	}
	
	private boolean isLCMetrics() {
		WPProduct wp = new WPProduct(this.productDir);
		if (wp.numExceptions() <= 0) {
			return (wp.getProductById(LCUtil.PRODUCTID_LC_METRICS) != null);
		} else {
			return false;
		}
	}

	private boolean isWASPlugin() {
		debug("Checking to see if " + this.productDir + " is a WAS Plugin");
		String[] pluginFiles = new File(this.productDir + File.separator
				+ wasPluginVerifyToken).list();
		int numTokens = 0;

		if (pluginFiles != null) {

			debug("Need to find the following files to denote WAS Plugin:");
			debug("   -- mod_app_server_http_eapi");
			debug("   -- mod_ibm_app_server_http");
			debug("   -- ns41_http");

			for (int i = 0; i < pluginFiles.length; i++) {
				debug("File " + pluginFiles[i] + " retrieved");
				if (pluginFiles[i].startsWith("mod_app_server_http_eapi")) {
					debug("Located " + pluginFiles[i]);
					numTokens++;
				}

				if (pluginFiles[i].startsWith("mod_ibm_app_server_http")) {
					debug("Located " + pluginFiles[i]);
					numTokens++;
				}

				if (pluginFiles[i].startsWith("ns41_http")) {
					debug("Located " + pluginFiles[i]);
					numTokens++;
				}
			}
		}

		debug("Found a total of " + numTokens + " out of the required 3");
		if (numTokens == 3) {
			return true;
		}

		return false;
	}

	public boolean cleanUp() {
		return ((UpdateHarnessManager) whm).cleanUp();
	}

	/**
	 * @param productDir
	 *            the productDir to set
	 * @uml.property name="productDir"
	 */
	public void setProductDir(String productDir) {
		this.productDir = productDir;
	}

	/**
	 * @return the productDir
	 * @uml.property name="productDir"
	 */
	public String getProductDir() {
		return productDir;
	}

	/*
	 * public static void main(String args[]) { UpdateHarnessController uhc =
	 * new UpdateHarnessController("C:\\TestHarness"); uhc.performStrategy(); }
	 */

}
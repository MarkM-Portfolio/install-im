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

import java.io.File;

/**
 * Class: UpdateHarnessController.java Abstract: Delegates the correct strategy based on the product input. History 1.9, 4/5/05 19-Sep-2003 Initial Version
 */

public class WPUpdateHarnessController {

	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugPropertyName = "com.ibm.websphere.update.harness.debug" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugTrueValue = "true" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugFalseValue = "false" ;

	// Debugging support ...
	protected static boolean debug;

	static {
		String debugValue = System.getProperty(debugPropertyName);

		debug = ((debugValue != null) && debugValue.equals(debugTrueValue));
	}

	/**
	 * @return  the debug
	 * @uml.property  name="debug"
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


	//********************************************************
	//  Program Versioning
	//********************************************************	
	public static final String pgmVersion = "1.9" ;
	//********************************************************
	//  Program Versioning
	//********************************************************	
	public static final String pgmUpdate = "4/5/05" ;

	//********************************************************
	//  Instance State
	//********************************************************
	private String productDir;

	private String wpMPVerifyToken      = "version" + File.separator + "MP.product";
	private String wpExpressVerifyToken = "version" + File.separator + "EXPRESS.product";
	private String wpISCVerifyToken     = "version" + File.separator + "ISC.product";
	private String wpToolkitVerifyToken = "version" + File.separator + "TOOLKIT.product";
        private String wpcpVerifyToken      = "version" + File.separator + "wpcp.component";
	private String wpcpSoloVerifyToken  = "version" + File.separator + "wpcpsolo.component";
        private String wpPZNVerifyToken     = "version" + File.separator + "PZN.product";
        private String wpWBCRVerifyToken    = "version" + File.separator + "WBCR.product";
        private String wpWCSVerifyToken     = "version" + File.separator + "WCS.product";
        private String wpTMSVerifyToken     = "version" + File.separator + "TMS.product";
        private String wpWSEVerifyToken     = "version" + File.separator + "WSE.product";
        private String wpWEMPVerifyToken    = "version" + File.separator + "WEMP.product";
        private String wpPDMVerifyToken     = "version" + File.separator + "PDM.product";
        private String wpWCMVerifyToken     = "version" + File.separator + "WCM.product";
        private String wpDOCVerifyToken     = "version" + File.separator + "DOC.product";
        private String wpLRNWPSVerifyToken     = "version" + File.separator + "LRNWPS.product";
        private String wpLRNSRVRVerifyToken     = "version" + File.separator + "LRNSRVR.product";
        private String wpTSVerifyToken      = "version" + File.separator + "TS.product";
        private String wpIHOTFIXVerifyToken    = "version" + File.separator + "IHOTIFX.product";
        private String wpWBSEVerifyToken    = "version" + File.separator + "WBSE.product";
        
        // IBM Connections
        private String LCActivitiesVerifyToken    = "version" + File.separator + "activities.product";
        private String LCBlogVerifyToken    = "version" + File.separator + "blogs.product";
        private String LCCommunitiesVerifyToken    = "version" + File.separator + "communities.product";
        private String LCDogearVerifyToken    = "version" + File.separator + "dogear.product";
        private String LCProfilesVerifyToken    = "version" + File.separator + "profiles.product";
        private String LCHomepageVerifyToken    = "version" + File.separator + "homepage.product";
        private String LCWikisVerifyToken    = "version" + File.separator + "wikis.product";
        private String LCFilesVerifyToken    = "version" + File.separator + "files.product";
        private String LCSearchVerifyToken    = "version" + File.separator + "search.product";
        private String LCNewsVerifyToken    = "version" + File.separator + "news.product";
        private String LCMobileVerifyToken    = "version" + File.separator + "mobile.product";
        private String LCForumVerifyToken    = "version" + File.separator + "forum.product";
        private String LCModerationVerifyToken = "version" + File.separator + "moderation.product";
        private String LCMetricsVerifyToken = "version" + File.separator + "metrics.product";
        private String LCCCMVerifyToken = "version" + File.separator + "ccm.product";



	private UpdateHarnessManager whm = null;

	public WPUpdateHarnessController(String productDir) {
		super();
      this.productDir = productDir;
	}

	//********************************************************
	//  Method Definition
	//********************************************************

	public boolean performStrategy() {

		debug("Initialized WPUpdateHarnessManager object");

		String currentProduct = getProductDir();
		debug("Current product : " + currentProduct);

		debug("Testing product type...");
      // Note ordering here is important.  The checks below "assume" the previous checks failed.  This simplifies the checks
      if (isWPCPStandalone()) {
         debug("Detected WPCP Standalone product");
         whm = new WPCPUpdateHarnessManager(currentProduct );
		} else if (isMP() ) {
			debug("Detected WP MultiPlatform product");
			whm = new WPMPUpdateHarnessManager(currentProduct );
		} else if (isExpress()) {
			debug("Detected WP Express product");
			whm = new WPExpressUpdateHarnessManager(currentProduct );
		} else if (isISC()) {
            debug("Detected ISC product");
            whm = new WPISCUpdateHarnessManager(currentProduct );
        } else if (isToolkit()) {
            debug("Detected WP Toolkit product");
            whm = new WPISCUpdateHarnessManager(currentProduct );
        } else if (isPznStandalone()) {
            debug("Detected PZN Standalone product");
            whm = new WPPZNUpdateHarnessManager(currentProduct );
        } else if (isWBCR()) {
            debug("Detected WBCR product");
            whm = new WBCRUpdateHarnessManager(currentProduct );
        } else if (isWCS()) {
            debug("Detected WCS product");
            whm = new WCSUpdateHarnessManager(currentProduct );
        } else if (isTMS()) {
           debug("Detected TMS product");
           whm = new TMSUpdateHarnessManager(currentProduct );
        } else if (isWSE()) {
           debug("Detected WSE product");
           whm = new WSEUpdateHarnessManager(currentProduct );
        } else if (isWEMP()) {
           debug("Detected WEMP product");
           whm = new WEMPUpdateHarnessManager(currentProduct );
        } else if (isPDM()) {
           debug("Detected PDM product");
           whm = new PDMUpdateHarnessManager(currentProduct );
        } else if (isWCM()) {
           debug("Detected WCM product");
           whm = new WCMUpdateHarnessManager(currentProduct );
        } else if (isDOC()) {
           debug("Detected DOC product");
           whm = new DOCUpdateHarnessManager(currentProduct );
        } else if (isTS()) {
           debug("Detected TS product");
           whm = new TSUpdateHarnessManager(currentProduct );
        } else if (isLRNWPS()) {
           debug("Detected LRNWPS product");
           whm = new LRNWPSUpdateHarnessManager(currentProduct );
        } else if (isLRNSRVR()) {
           debug("Detected LRNSRVR product");
           whm = new LRNSRVRUpdateHarnessManager(currentProduct );
        } else if (isIHOTFIX()) {
           debug("Detected IHOTFIX product");
           whm = new IHOTFIXUpdateHarnessManager(currentProduct );
        } else if (isWBSE()) {
          debug("Detected WBSE product");
          whm = new WBSEUpdateHarnessManager(currentProduct );
        } else if (isLCActivities()) {
            debug("Detected IBM Connections Activities product");
            whm = new WBSEUpdateHarnessManager(currentProduct );
        } else if (isLCBlogs()) {
            debug("Detected IBM Connections Blogs product");
            whm = new WBSEUpdateHarnessManager(currentProduct );
        } else if (isLCCommunities()) {
            debug("Detected IBM Connections Communities product");
            whm = new WBSEUpdateHarnessManager(currentProduct );
        } else if (isLCDogear()) {
            debug("Detected IBM Connections Dogear product");
            whm = new WBSEUpdateHarnessManager(currentProduct );
        } else if (isLCProfiles()) {
            debug("Detected IBM Connections Profiles product");
            whm = new WBSEUpdateHarnessManager(currentProduct );
        }else if (isLCHomepage()) {
            debug("Detected IBM Connections Homepage");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCWikis()) {
            debug("Detected IBM Connections Wikis");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCFiles()) {
            debug("Detected IBM Connections Files");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCMobile()) {
            debug("Detected IBM Connections Mobile");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCSearch()) {
            debug("Detected IBM Connections Search");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCNews()) {
            debug("Detected IBM Connections News");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCForum()) {
            debug("Detected IBM Connections Forum");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCModeration()) {
        	debug("Detected IBM Connections Moderation");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCMetrics()) {
        	debug("Detected IBM Connections Metrics");
            whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }else if (isLCCCM()) {
        	debug("Detected IBM Connections CCM");
        	whm = new LCProfilesUpdateHarnessManager(currentProduct);
        }


		if (whm == null) {
			debug("Could not detect a suitable product to harness update strategy");
			debug("Returning false");
			return false;
		}

		return whm.harness();

	}

	private boolean isExpress() {
      // Assume MP check failed. Otherwise we may report an Express product when in fact we are MP
		return (new File(this.productDir + File.separator + wpExpressVerifyToken).exists());
	}


	private boolean isMP() {
      // Assumes WPCPStandalone check failed. Other wise we may report MP, when its really WPCP SA
		return (new File(this.productDir + File.separator + wpMPVerifyToken).exists());
	}

	private boolean isWPCPStandalone() {
      // We see is the solo component is installed,but not the wpcp core.
		return (new File(this.productDir + File.separator + wpcpSoloVerifyToken).exists()) &&
           !(new File(this.productDir + File.separator + wpcpVerifyToken).exists());
	}

        private boolean isISC() {
            // assumes MP, EXPRESS and WPCPStandalone have all failed...
            return (new File(this.productDir + File.separator + wpISCVerifyToken).exists());
        }

        private boolean isToolkit() {
            // assumes MP, EXPRESS, ISC and WPCPStandalone have all failed...
            return (new File(this.productDir + File.separator + wpToolkitVerifyToken).exists());
        }
        
        private boolean isPznStandalone() {
            // assumes MP, EXPRESS, ISC and WPCPStandalone have all failed...
            return (new File(this.productDir + File.separator + wpPZNVerifyToken).exists());
        }

        private boolean isWBCR() {
            // assumes MP, EXPRESS, ISC, PznStandalone and WPCPStandalone have all failed...
            return (new File(this.productDir + File.separator + wpWBCRVerifyToken).exists());
        }

        private boolean isWCS() {
            // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
            return (new File(this.productDir + File.separator + wpWCSVerifyToken).exists());
        }

        private boolean isTMS() {
           // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
           return (new File(this.productDir + File.separator + wpTMSVerifyToken).exists());
        }

        private boolean isWSE() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpWSEVerifyToken).exists());
        }

        private boolean isWEMP() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpWEMPVerifyToken).exists());
        }

        private boolean isPDM() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpPDMVerifyToken).exists());
        }

        private boolean isWCM() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpWCMVerifyToken).exists());
        }
        
        private boolean isDOC() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpDOCVerifyToken).exists());
        }
        
        private boolean isTS() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpTSVerifyToken).exists());
        }

        private boolean isLRNWPS() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpLRNWPSVerifyToken).exists());
        }
        
        private boolean isLRNSRVR() {
         // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
         return (new File(this.productDir + File.separator + wpLRNSRVRVerifyToken).exists());
        }

        private boolean isIHOTFIX() {
        // assumes MP, EXPRESS, ISC, PznStandalone, WBCR and WPCPStandalone have all failed...
        return (new File(this.productDir + File.separator + wpIHOTFIXVerifyToken).exists());
        }

        private boolean isWBSE() {
            return (new File(this.productDir + File.separator + wpWBSEVerifyToken).exists());
        }
        

        // IBM Connections
        private boolean isLCActivities() {
          return (new File(this.productDir + File.separator + LCActivitiesVerifyToken).exists());
        }

        private boolean isLCBlogs() {
            return (new File(this.productDir + File.separator + LCBlogVerifyToken).exists());
        }
        private boolean isLCCommunities() {
            return (new File(this.productDir + File.separator + LCCommunitiesVerifyToken).exists());
        }
        private boolean isLCDogear() {
            return (new File(this.productDir + File.separator + LCDogearVerifyToken).exists());
        }
        private boolean isLCProfiles() {
            return (new File(this.productDir + File.separator + LCProfilesVerifyToken).exists());
        }
        private boolean isLCHomepage() {
            return (new File(this.productDir + File.separator + LCHomepageVerifyToken).exists());
        }
        private boolean isLCWikis() {
            return (new File(this.productDir + File.separator + LCWikisVerifyToken).exists());
        }
        private boolean isLCFiles() {
            return (new File(this.productDir + File.separator + LCFilesVerifyToken).exists());
        }
        private boolean isLCNews() {
            return (new File(this.productDir + File.separator + LCNewsVerifyToken).exists());
        }
        private boolean isLCSearch() {
            return (new File(this.productDir + File.separator + LCSearchVerifyToken).exists());
        }
        private boolean isLCMobile() {
            return (new File(this.productDir + File.separator + LCMobileVerifyToken).exists());
        }
        private boolean isLCForum() {
            return (new File(this.productDir + File.separator + LCForumVerifyToken).exists());
        }
        private boolean isLCModeration() {
            return (new File(this.productDir + File.separator + LCModerationVerifyToken).exists());
        }
        private boolean isLCMetrics() {
        	return (new File(this.productDir + File.separator + LCMetricsVerifyToken).exists());
        }
        private boolean isLCCCM() {
        	return (new File(this.productDir + File.separator + LCCCMVerifyToken).exists());
        }


	public boolean cleanUp() {
		return ((UpdateHarnessManager) whm).cleanUp();
	}

	/**
	 * @param productDir  the productDir to set
	 * @uml.property  name="productDir"
	 */
	public void setProductDir(String productDir) {
		this.productDir = productDir;
	}

	/**
	 * @return  the productDir
	 * @uml.property  name="productDir"
	 */
	public String getProductDir() {
		return productDir;
	}

}
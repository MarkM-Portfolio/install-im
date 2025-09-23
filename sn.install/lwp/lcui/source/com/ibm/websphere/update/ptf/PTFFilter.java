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
package com.ibm.websphere.update.ptf;

/*
 * PTF Filtering Operations
 *
 * History 1.4, 3/11/05
 *
 * 17-Dec-2002 Initial Version
 *
 * 03-Mar-2003 Support '+' syntax for AND product prereqs.
 *             Defect LIDB2704.4.3
 */

/*
 * Filtering Notes:
 *
 * PTFImage -> ptfDriver
 *    ptfDriver { productUpdate }
 *    productUpdate { id, name, version, date, level }
 *    platformPrereq { architecture, os-platform, os-version }

 * WASProduct { product }
 *    product { id, name, version, date, level }
 *
 * 'satisfiesProduct' uses the productUpdate.id and product.id.
 * 'satisifesPlatform' uses the platformPrereq values, and system properties.
 * 
 * 'isCurrent', 'isFuture', and 'isLaterThan' use
 * productUpdate.version and product.version.
 *
 * The syntax of a version is "#.#.#".  The value for the WAS release 5
 * initial install is "5.0.0"; the value for the first PTF is "5.0.1".
 *
 * Comparison is by the first digit, then by the second digit, then by
 * the third.
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.product.*;
import com.ibm.websphere.update.efix.prereq.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ptf.prereq.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class PTFFilter {
	// Program versioning ...

	public static final String pgmVersion = "1.4" ;
	// Program versioning ...

	public static final String pgmUpdate = "3/11/05" ;

    // Debugging support ...

    public static final String debugPropertyName = "com.ibm.websphere.update.ptf.debug" ;
    // Debugging support ...

    public static final String debugTrueValue = "true" ;
    // Debugging support ...

    public static final String debugFalseValue = "false" ;

    protected static boolean debug;

    static {
        String debugValue = System.getProperty(debugPropertyName);

        debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
    }

    /**
	 * @return
	 * @uml.property  name="isDebug"
	 */
    public static boolean isDebug()
    {
        return debug;
    }

    public static void debug(String arg)
    {
        if ( !debug )
            return;

        System.out.println(arg);
    }

    public static void debug(String arg1, String arg2)
    {
        if ( !debug )
            return;

        System.out.print(arg1);
        System.out.println(arg2);
    }


	private ImageRepository imageRepository;
	private List productBreeds;
	private static int max_ptf = 0;
	private boolean prepareIOClean = true ;
	private boolean prepareWellFormedXML = true ;
	private boolean state = false ;

	// Instantor ...
	//
	// public PTFFilter(WASProduct, WASHistory);
	//
	// public getWASProduct();
	// public getWASHistory();

	public PTFFilter(WPProduct wpsProduct, WPHistory wpsHistory) {
		this.wpsProduct = wpsProduct;
		this.wpsHistory = wpsHistory;
	}

	protected WPProduct wpsProduct;
	protected WPHistory wpsHistory;

	public WPProduct getWPProduct() {
		return wpsProduct;
	}

	public WPHistory getWPHistory() {
		return wpsHistory;
	}

	/**
	 * @param imageRepository  the imageRepository to set
	 * @uml.property  name="imageRepository"
	 */
	public void setImageRepository(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	/**
	 * @return  the imageRepository
	 * @uml.property  name="imageRepository"
	 */
	public ImageRepository getImageRepository() {
		return imageRepository;
	}

	/**
	 * @param productBreeds  the productBreeds to set
	 * @uml.property  name="productBreeds"
	 */
	public void setProductBreeds(List productBreeds) {
		this.productBreeds = productBreeds;
	}

	/**
	 * @return  the productBreeds
	 * @uml.property  name="productBreeds"
	 */
	public List getProductBreeds() {
		return productBreeds;
	}

	public Vector preparePTFImages() throws IOServicesException {
		Vector prepareExceptions = new Vector();
		ImageRepository imageRepository = getImageRepository();
		Vector fixPackIds, fixPackContainer = new Vector(), PTFImages = new Vector(), badlyPreparedJars = new Vector();

		imageRepository.prepare();

		fixPackIds = imageRepository.getPTFIds();
		Hashtable ptfTable = imageRepository.getPTFImages();

		for (int a = 0; a < fixPackIds.size(); a++) {
			String fixPackId = (String) fixPackIds.elementAt(a);
			fixPackContainer.add(ptfTable.get(fixPackId));
		}

		PTFImage ptfImg = null;
		int numPTF = fixPackContainer.size();
		for (int i = 0; i < numPTF; i++) {
			try {
				ptfImg = (PTFImage) fixPackContainer.elementAt(i);
				ptfImg.prepareDriver();
				ptfImg.prepareComponents();
				ptfImg.prepareExtendedComponentImages(wpsProduct, "external.mq");

            /* Need to do component prereq checking here.  Need to remove any component-updates that do not apply. */
            Vector errors = new Vector();
            PTFPrereqChecker checker =
                new PTFPrereqChecker( wpsProduct, wpsHistory );
            if ( checker.testPTFInstallation( ptfImg.getPTFDriver(), errors ) ) {
               PTFImages.add(ptfImg);
            } else {
               //   Nothing left to install. prereqs not met
            }

			} catch (IOException ioe) {
				prepareIOClean = false;
				prepareExceptions.add(ioe);
				if (!badlyPreparedJars.contains(ptfImg.getPTFId()))
					badlyPreparedJars.add(ptfImg.getPTFId());
			} catch (BaseHandlerException bhe) {
				prepareExceptions.add(bhe);
				prepareWellFormedXML = false;
				if (!badlyPreparedJars.contains(ptfImg.getPTFId()))
					badlyPreparedJars.add(ptfImg.getPTFId());
			}
		}

		setUnpreparedJars(badlyPreparedJars);
		setPrepareExceptions(prepareExceptions);

		return PTFImages;

	}

	public boolean containsWellFormedXML(){
		return prepareWellFormedXML;		
	}
	
	public boolean containsCleanIOScan(){
		return prepareIOClean;	
	}

	private Vector prepareExceptions;
	/**
	 * @param prepareExceptions  the prepareExceptions to set
	 * @uml.property  name="prepareExceptions"
	 */
	public void setPrepareExceptions(Vector pExceptions) {
		this.prepareExceptions = pExceptions;
	}

	/**
	 * @return  the prepareExceptions
	 * @uml.property  name="prepareExceptions"
	 */
	public Vector getPrepareExceptions() {
		return prepareExceptions;
	}

	private Vector unPreparedJars = new Vector();
	public void setUnpreparedJars(Vector unPreparedJars) {
		this.unPreparedJars = unPreparedJars;
	}

	public Vector getUnpreparedJars() {
		return unPreparedJars;
	}

	public boolean hasUnpreparedJars() {
		return unPreparedJars.size() > 0;
	}

	public int getVersionAsNum(String version) {
		StringTokenizer st = new StringTokenizer(version, ".");
		StringBuffer num = new StringBuffer();

		while (st.hasMoreTokens()) {
			num.append(st.nextToken());
		}

		return new Integer(num.toString()).intValue();
	}

	// Preparation:
	//
	// public PTFImage getCurrentPTF(ImageRepository);
	// public Vector getFuturePTFs(ImageRepository);
	// public PTFImage getLatestPTF(ImageRepository);
	//
	// public boolean satisfiesPlatform(PTFImage);
	// public boolean satisfiesProduct(PTFImage);
	// public boolean isCurrent(PTFImage);
	// public boolean isFuture(PTFImage);
	// public boolean isLaterThan(PTFImage, PTFImage);
	//   isLaterThan fetches the max out of the future images

	public PTFImage getCurrentPTF(Vector allPTFImages) {
		PTFImage currentPTF = null;

		int numPTFs = allPTFImages.size();
		for (int i = 0; i < numPTFs; i++) {
			PTFImage ptfImg = (PTFImage) allPTFImages.elementAt(i);

			if (isCurrentPTF(ptfImg, getWPProduct())) {
				currentPTF = ptfImg;
				return currentPTF;
			}

		}

		return currentPTF;
	}

	private boolean isCurrentPTF(PTFImage ptfImg, WPProduct wasp) {
		boolean result = false;

		List productBreeds = getProductBreeds();

		ptfDriver driver = ptfImg.getPTFDriver();
		int productUpdateNum = driver.getProductUpdateCount();

		for (int j = 0; j < productUpdateNum; j++) {
			productUpdate prodUpdate = driver.getProductUpdate(j);

			String productName = prodUpdate.getProductId();
			if (productBreeds.contains(productName)) {
				String ptfBuildVersion = prodUpdate.getBuildVersion();
				int ptfBuildVersionNum = getVersionAsNum(ptfBuildVersion);

				product contextProduct = getWPProduct().getProductById(productName);
				if (contextProduct != null) {
					String currentBuildVersion = contextProduct.getVersion();

					int currentBuildVersionNum = getVersionAsNum(currentBuildVersion);
					if (ptfBuildVersionNum == currentBuildVersionNum) {
						result = true;
					}
				}

			}

		}

		return result;

	}

	public Vector getFuturePTFs(Vector allPTFImages) {
		Vector futurePTFs = new Vector();

		int numPTFs = allPTFImages.size();
		for (int i = 0; i < numPTFs; i++) {
			PTFImage ptfImg = (PTFImage) allPTFImages.elementAt(i);

			if (isFuturePTF(ptfImg, getWPProduct())) {
				futurePTFs.add(ptfImg);
			}

		}

		return futurePTFs;

	}

	private boolean isFuturePTF(PTFImage ptfImg, WPProduct wasp) {
		boolean result = false;

		List productBreeds = getProductBreeds();
        
        debug( "PTFFilter::isFuturePTF productBreeds == " + productBreeds.size() );
        Object[] breeds = productBreeds.toArray();
        if ( null == breeds ){
            debug( "PTFFilter::isFuturePTF breeds is NULL" );
        } else if ( null != breeds && 0 == breeds.length ) {
            debug( "PTFFilter::isFuturePTF breeds.length  == 0" );
        } else {
            for ( int i=0; i < breeds.length; i++ ) {
                debug( "PTFFilter::isFuturePTF breeds[" + i + "] == " + breeds[i].toString() );
            }
        }
        

		ptfDriver driver = ptfImg.getPTFDriver();
		int productUpdateNum = driver.getProductUpdateCount();
        debug( "PTFFilter::isFuturePTF productUpdateNum == " + productUpdateNum );

		for (int j = 0; j < productUpdateNum; j++) {
			productUpdate prodUpdate = driver.getProductUpdate(j);

			String productName = prodUpdate.getProductId();
            debug( "PTFFilter::isFuturePTF j == " + j );
            debug( "PTFFilter::isFuturePTF.productName == " + productName );
			if (productBreeds.contains(productName)) {
				String ptfBuildVersion = prodUpdate.getBuildVersion();
                debug( "PTFFilter::isFuturePTF.ptfBuildVersion == " + ptfBuildVersion );
				int ptfBuildVersionNum = getVersionAsNum(ptfBuildVersion);
                debug( "PTFFilter::isFuturePTF.ptfBuildVersionNum == " + ptfBuildVersionNum );

				product contextProduct = getWPProduct().getProductById(productName);
				if (contextProduct != null) {
					String currentBuildVersion = contextProduct.getVersion();
                    debug( "PTFFilter::isFuturePTF.currentBuildVersion == " + currentBuildVersion );

					int currentBuildVersionNum = getVersionAsNum(currentBuildVersion);
                    debug( "PTFFilter::isFuturePTF.currentBuildVersionNum == " + currentBuildVersionNum );
                    debug( "ptfBuildVersionNum >= currentBuildVersionNum  ? == " + (ptfBuildVersionNum >= currentBuildVersionNum) );
					if (ptfBuildVersionNum >= currentBuildVersionNum) {
                        result = true;
					}
				}

			}

		}

		return result;
	}

	public PTFImage getLatestPTF(Vector allPTFImages) {
		PTFImage latestPTF = null;
		int numPTFs = allPTFImages.size();

		if (getMaxPTF() != 0)
			setMaxPTF(0);

		for (int i = 0; i < numPTFs; i++) {
			PTFImage ptfImg = (PTFImage) allPTFImages.elementAt(i);

			if (isLatestPTF(ptfImg, getWPProduct())) {
				latestPTF = ptfImg;
			}

		}

		return latestPTF;
	}

	private boolean isLatestPTF(PTFImage ptfImg, WPProduct wasp) {
		boolean result = false;

		List productBreeds = getProductBreeds();

		ptfDriver driver = ptfImg.getPTFDriver();
		int productUpdateNum = driver.getProductUpdateCount();

		for (int j = 0; j < productUpdateNum; j++) {
			productUpdate prodUpdate = driver.getProductUpdate(j);

			String productName = prodUpdate.getProductId();
			if (productBreeds.contains(productName)) {
				String ptfBuildVersion = prodUpdate.getBuildVersion();
				int ptfBuildVersionNum = getVersionAsNum(ptfBuildVersion);

				product contextProduct = getWPProduct().getProductById(productName);
				if (contextProduct != null) {
					String currentBuildVersion = contextProduct.getVersion();

					int currentBuildVersionNum = getVersionAsNum(currentBuildVersion);

					if (ptfBuildVersionNum > currentBuildVersionNum) {
						if (ptfBuildVersionNum > getMaxPTF()) {
							setMaxPTF(ptfBuildVersionNum);
							result = true;
						}
					}
				}

			}

		}

		return result;
	}

	private int getMaxPTF() {
		return max_ptf;
	}

	private void setMaxPTF(int new_max_ptf) {
		this.max_ptf = new_max_ptf;
	}

        public static final boolean isDebug = ImageRepository.isDebug;
    
        public boolean satisfiesPlatform(PTFImage candidateImage) {
            String systemArch = System.getProperty("os.arch");
            String systemPlatform = System.getProperty("os.name");
            String systemVersion = System.getProperty("os.version");

            if (isDebug) {
                System.out.println("Testing platform prereqs:");
                System.out.println("  System Architecture: " + systemArch);
                System.out.println("  System Platform: " + systemPlatform);
                System.out.println("  System Version: " + systemVersion);
            }

            ptfDriver driver = candidateImage.getPTFDriver();
            int platformPrereqNum = driver.getPlatformPrereqCount();
            /*
            if (platformPrereqNum == 0) {
               if (isDebug)
                   System.out.println("Satisfied Platform Prereq - None specified");
               return true;
            }
            */
            for (int prereqNo = 0; prereqNo < platformPrereqNum; prereqNo++) {
                boolean architecturePass = false;
                boolean platformPass = false;
                boolean versionPass = false;

                platformPrereq platformPreq = driver.getPlatformPrereq(prereqNo);

                String arch = platformPreq.getArchitecture();
                String platform = platformPreq.getOSPlatform();
                String version = platformPreq.getOSVersion();

                if (isDebug) {
                    System.out.println("Testing platform prereq: " + prereqNo);
                    System.out.println("  Architecture: " + arch);
                    System.out.println("  Platform: " + platform);
                    System.out.println("  Version: " + version);
                }

                if (arch.equals("") || arch.equals("*")) {
                    if (isDebug)
                        System.out.println("Null architecture.");
                    architecturePass = true;
                } else if ((arch.length() > 1) && (arch.indexOf("*") >= 0)) {
                    int archSubstringIndx = arch.indexOf("*");
                    String archSubstring = arch.substring(0, archSubstringIndx);
                    if (isDebug)
                        System.out.println("Effective Architecture: " + archSubstring);
                    architecturePass = systemArch.startsWith(archSubstring);
                } else {
                    architecturePass = arch.equals(systemArch);
                }

                if (isDebug) {
                    if (architecturePass)
                        System.out.println("Architecture matches; passing");
                    else
                        System.out.println("Architecture does not matches");
                }

                if (platform.equals("") || platform.equals("*")) {
                    if (isDebug)
                        System.out.println("Null platform.");
                    platformPass = true;
                } else if ((platform.length() > 1) && (platform.indexOf("*") >= 0)) {
                    int platformSubstringIndx = platform.indexOf("*");
                    String platformSubstring = platform.substring(0, platformSubstringIndx);
                    if (isDebug)
                        System.out.println("Effective Platform: " + platformSubstring);
                    platformPass = systemPlatform.startsWith(platformSubstring);
                } else {
                    platformPass = platform.equals(systemPlatform);
                }

                if (isDebug) {
                    if (platformPass)
                        System.out.println("Platform matches; passing");
                    else
                        System.out.println("Platform does not matches");
                }

                if (version.equals("") || version.equals("*")) {
                    if (isDebug)
                        System.out.println("Null version.");
                    versionPass = true;

                } else if (version.length() > 1 && version.indexOf("*") >= 0) {
                    int versionSubstringIndx = version.indexOf("*");
                    String versionSubstring = version.substring(0, versionSubstringIndx);
                    if (isDebug)
                        System.out.println("Effective Version: " + versionSubstring);
                    versionPass = systemVersion.startsWith(versionSubstring);
                } else {
                    versionPass = version.equals(systemVersion);
                }

                if (isDebug) {
                    if (versionPass)
                        System.out.println("Version matches; passing");
                    else
                        System.out.println("Version does not matches");
                }

                if (architecturePass && platformPass && versionPass) {
                    if (isDebug)
                        System.out.println("Satisfied Platform Prereq");
                    return true;
                }
            }

            if (isDebug)
                System.out.println("Failed All Platform Prereqs");
            return false;
        }

    // Changed the prereq test to split out 'and' prereqs from the
    // rest.
    //
    // The new interpretation of the product prereqs is:
    //
    // a,   b       means 'a' or 'b'
    // +a,  b       means 'a' and 'b'
    // +a,  b, c    means 'a' and ( 'b' or 'c' )
    // +a, +b, c    means 'a' and 'b' and 'c'
    // +a, +b, c, d means 'a' and 'b' and ( 'c' or 'd' )
    //
    // The 'a, b' case occurs with a usual PTF, for example,
    //
    // PTF 5.0.1 can be applied to
    // product 5.0.0 or 5.0.1.
    //
    // The '+a, b, c, d' case occurs with PME, where
    // the prereqs are:
    //
    //    BASE 5.0.1 and ( PME 5.0.0 or PME 5.0.1 )
    //

    public static final String PRODUCT_AND_PREFIX = "+";

    protected boolean isAndProductPrereq(String productId)
    {
        return ( (productId != null) && productId.startsWith(PRODUCT_AND_PREFIX) );
    }

    protected String getKernelProductId(String productId)
    {
        if ( !isAndProductPrereq(productId) )
            return productId;
        else
            return productId.substring( PRODUCT_AND_PREFIX.length() );
    }

    protected void splitProductPrereqs(ptfDriver driver, Vector andPrereqs, Vector orPrereqs)
    {
        int prereqCount = driver.getProductPrereqCount();

        for ( int prereqNo = 0; prereqNo < prereqCount; prereqNo++ ) {
            productPrereq nextPrereq = driver.getProductPrereq(prereqNo);

            if ( isAndProductPrereq( nextPrereq.getProductId() ) )
                andPrereqs.addElement(nextPrereq);
            else
                orPrereqs.addElement(nextPrereq);
        }
    }

    public boolean satisfiesProduct(PTFImage candidateImage)
    {
        ptfDriver driver = candidateImage.getPTFDriver();

        if ( isDebug )
            debug("Testing product prereqs: ", driver.getId());

        int numPrereqs = driver.getProductPrereqCount();

        if ( numPrereqs == 0 ) {
            debug("No product prereqs; answering false.");
            return false;
        }

        Vector andPrereqs = new Vector();
        Vector orPrereqs = new Vector();

        splitProductPrereqs(driver, andPrereqs, orPrereqs);

        List productBreeds = getProductBreeds();
        HashMap productMap = getProductMap();

        if ( !testAndProductPrereqs(productBreeds, productMap, andPrereqs) ||
             !testOrProductPrereqs(productBreeds, productMap, orPrereqs) ) {
            debug("Product prereqs were not satisfied.");
            return false;

        } else {
            debug("Product prereqs were satisfied.");
            return true;
        }
    }

    protected boolean testAndProductPrereqs(List productBreeds, HashMap productMap,
                                            Vector prereqs)
    {
        debug("Testing AND product prereqs");

        int prereqCount = prereqs.size();
        if ( prereqCount == 0 ) {
            debug("No AND product prereqs; answering true");
            return true;
        }


        boolean foundFailure = false;

        for ( int prereqNo = 0; !foundFailure && (prereqNo < prereqCount); prereqNo++ ) {
            debug("Testing AND prereq [ " + prereqNo + " ]");

            productPrereq nextPrereq = (productPrereq) prereqs.elementAt(prereqNo);

            foundFailure = !satisfiesPrereq(nextPrereq, productBreeds, productMap);
        }
        
        return !foundFailure;
    }

    protected boolean testOrProductPrereqs(List productBreeds, HashMap productMap,
                                           Vector prereqs)
    {
        debug("Testing OR product prereqs");

        int prereqCount = prereqs.size();

        if ( prereqCount == 0 ) {
            debug("No OR product prereqs; answering true");
            return true;
        }

        boolean foundMatch = false;

        for ( int prereqNo = 0; !foundMatch && (prereqNo < prereqCount); prereqNo++ ) {
            debug("Testing OR prereq [ " + prereqNo + " ]");

            productPrereq nextPrereq = (productPrereq) prereqs.elementAt(prereqNo);

            foundMatch = satisfiesPrereq(nextPrereq, productBreeds, productMap);
        }
        
        return foundMatch;
    }

    protected boolean satisfiesPrereq(productPrereq prereq,
                                      List productBreeds, HashMap productMap)
    {
        String prereqId = prereq.getProductId();
        String kernelId = getKernelProductId(prereqId);

        if ( isDebug )
            debug("Testing for product: " + prereqId + ": " + kernelId);

        if ( !productBreeds.contains(kernelId) ) {
            debug("Not a current product breed; answering false.");
            return false;
        }

        product breedProduct = (product) productMap.get(kernelId);

        if ( breedProduct == null ) {
            debug("Not a current product; answering false.");
            return false;
        }

        int prereqNum = getVersionAsNum( prereq.getBuildVersion() );
        int breedNum  = getVersionAsNum( breedProduct.getVersion() );

        if ( isDebug ) {
            debug("Prereq Build Version : " + prereqNum);
            debug("Product Build Version: " + breedNum);
        }

        if ( breedNum == prereqNum ) {
            debug("Prereq is satisfied.");
            return true;

        } else {
            debug("Prereq is not satisfied.");
            return false;
        }
    }

    protected HashMap getProductMap()
    {
        HashMap products = new HashMap();

        Iterator useProducts = getWPProduct().getProducts();

        product
            baseProduct = null,
            ndProduct = null;

        String
            baseProductId = null,
            ndProductId = null;

        while ( useProducts.hasNext() ) {
            product nextProduct = (product) useProducts.next();

            String nextProductId = nextProduct.getId();

            if ( nextProductId.equalsIgnoreCase(WPProduct.PRODUCTID_BASE) ) {
                baseProduct = nextProduct;
                baseProductId = nextProductId;
            } else if ( nextProductId.equalsIgnoreCase(WPProduct.PRODUCTID_ND) ) {
                ndProduct = nextProduct;
                ndProductId = nextProductId;
            } else {
                products.put(nextProductId, nextProduct);
            }
        }

        // Defect 156580: Mask out the ND product when BASE is installed.

        if ( baseProduct == null ) {
            if ( ndProduct != null )
                products.put(ndProductId, ndProduct);
        } else {
            products.put(baseProductId, baseProduct);
        }

        return products;
    }

	public boolean isCurrent(PTFImage candidateImage) {
		return isCurrentPTF(candidateImage, getWPProduct());
	}

	public boolean isFuture(PTFImage candidateImage) {
		return isFuturePTF(candidateImage, getWPProduct());
	}

	//answer if the second image is later than the first
	public boolean isLaterThan(PTFImage firstImage, PTFImage secondImage) {
		boolean isLaterThan = false;

		if (getMaxPTF() != 0)
			setMaxPTF(0);

		Vector images = new Vector();
		images.add(firstImage);
		images.add(secondImage);

		for (int i = 0; i < images.size(); i++) {
			if (isLatestPTF((PTFImage) images.elementAt(i), getWPProduct())) {
				isLaterThan = true;
			} else {
				isLaterThan = false;
			}
		}

		return isLaterThan;

	}
}

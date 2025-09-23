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
package com.ibm.websphere.update.ptf.prereq;

/*
 * EFix Prerequisite Checking
 *
 * History 1.5, 9/14/06
 *
 * 17-Sep-2002 Initial Version
 *
 * 18-Sep-2002 Added handling of coreqs to uninstall tests.
 *
 * 20-Oct-2002 Added wildcards to platform testing.
 *
 * ??-Jan-2003 Relocated from the efix package.
 *
 * 13-Jan-2003 Mask out the ND product when BASE is installed.
 *             Defect 156580.
 *
 * 16-Jan-2003 Allow add updates, but only if the component is absent.
 *             Defect 156574
 *
 * 28-Feb-2003 Support '+' syntax for AND product prereqs.
 *             Defect LIDB2704.4.3
 *
 * 04-Mar-2003 Force efixes to be uninstalled in the same order
 *             as which they were installed.
 */

// Checking:
//    efixDriver -> { productPrereq }, { platformPrereq }, { efixPrereq }
//    componentUpdate -> { componentPrereq : componentVersion }, isRequired
//      componentVersion -> componentName, specVersion, buildVersion, buildDate
//    productPrereq -> productId, buildVersion, buildDate, buildLevel
//    platformPrereq -> architecture, osPlatform, osVersion
//    efixPrereq -> efixId, isNegative, installIndex
//
// efixApplied ->
//    { componentApplied }
//
// See the nlsprops file for the error messages.
//
// When installing a collection of efixes:
//   If there are product prereqs, one must be satisfied.
//
//   If there are platform prereqs, one must be satisfied.
//
//   If there are efix prereqs:
//     Corequisite efixes are installed together;
//     Corequisite efixes are ordered by their install order.
//     An efix is not installed until all of its prerequisite
//     efixes is installed.
//
//   If there required component updates, each of these
//   components must be installed.
//
//   For each component update to be installed, if that
//   update as component prerequisites, these prerequisites
//   must be satisified.
//
//   An efix may not be installed if a negative coreq of that efix
//   is installed.
//
//   An efix may not be installed if an installed efix lists that efix
//   as a negative prereq.
//
// When removing a collection of efixes:
//   Must order the efixes such that:
//     Corequisite efixes are removed together;
//     Corequisite efixes are ordered by the reverse of their
//     install order.
//
//   An efix is not removed until all of its dependent efixes
//   are first removed.
//
//   An efix cannot be removed without the backup information
//   for a component update of that efix.
//

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.component.ComponentHandler;
import com.ibm.websphere.product.xml.component.ComponentWriter;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.EFixHandler;
import com.ibm.websphere.product.xml.efix.EFixWriter;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.product.xml.efix.ptf;
import com.ibm.websphere.product.xml.product.*;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class EFixPrereqChecker
{

    // Program versioning ...
   public static final String pgmVersion = "1.5" ;
    // Program versioning ...
   public static final String pgmUpdate = "9/14/06" ;

    public static final String debugPropertyName = "com.ibm.websphere.update.efix.prereq.debug" ;
    public static final String debugTrueValue = "true" ;
    public static final String debugFalseValue = "false" ;

    // Debugging support ...

    protected static boolean debug;

    static {
        String debugValue = System.getProperty(debugPropertyName);

        debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
    }

    /**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
    public static boolean isDebug()
    {
        return debug;
    }

    public static void debug(Object arg)
    {
        if ( !debug )
            return;

        System.out.println(arg);
    }

    public static void debug(Object arg1, Object arg2)
    {
        if ( !debug )
            return;

        System.out.print(arg1);
        System.out.println(arg2);
    }

    // Instantor ...

    public EFixPrereqChecker(WPProduct wpsProduct, WPHistory wpsHistory)
    {
        this.wpsProduct = wpsProduct;
        this.wpsHistory = wpsHistory;
    }

    protected WPProduct wpsProduct;
    protected WPHistory wpsHistory;
    
    protected WPProduct getWPProduct()
    {
        return wpsProduct;
    }

    protected WPHistory getWPHistory()
    {
        return wpsHistory;
    }

    public boolean testEFixInstallation(Vector efixes,
                                        Vector installOrder,
                                        Vector errors)
    {
        boolean
            satisifiedProductPrereqs    = testProductPrereqs(efixes, errors),
            satisfiedPlatformPrereqs    = testPlatformPrereqs(efixes, errors),
            satisfiedNegativePrereqs    = testNegativePrereqs(efixes, errors),
            satisfiedEFixPrereqs        = testEFixPrereqs(efixes, installOrder, errors),
            satisfiedRequiredComponents = testRequiredComponents(efixes, errors),
            satisfiedComponentPrereqs   = testComponentPrereqs(efixes, errors);

        if ( isDebug() )
            displayInstallErrors(efixes, installOrder, errors);

        return
            satisifiedProductPrereqs    &&
            satisfiedPlatformPrereqs    &&
            satisfiedNegativePrereqs    &&
            satisfiedEFixPrereqs        &&
            satisfiedRequiredComponents &&
            satisfiedComponentPrereqs;
    }

    protected void displayInstallErrors(Vector efixes, Vector installOrder, Vector errors)
    {
        System.out.println("Install Prerequisite Report: ");
        System.out.println();

        System.out.println("EFixes: " );

        int numFixes = efixes.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            System.out.println("  EFix: " + nextFix.getId());
        }

        System.out.println();

        System.out.println("EFixes (install order): " );

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) installOrder.elementAt(fixNo);

            System.out.println("  EFix: " + nextFix.getId());
        }

        System.out.println();

        System.out.println("Install prerequisite disatisfaction: " );

        int numErrors = errors.size();

        for ( int errorNo = 0; errorNo < numErrors; errorNo++ ) {
            String nextError = (String) errors.elementAt(errorNo);

            System.out.println("[" + errorNo + "]: " + nextError);
        }

        System.out.println();
    }

    public boolean testProductPrereqs(Vector efixes, Vector errors)
    {
        boolean result = true;

        Vector localErrors = new Vector();

        int numFixes = efixes.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            if ( !testProductPrereqs(nextFix, localErrors) )
                result = false;
        }

        if ( !result ) {
            errors.add( getString("product.prereq.failure") );

            Iterator useProducts = getWPProduct().getProducts();

            while ( useProducts.hasNext() ) {
                product nextProduct = (product) useProducts.next();

                String productText = getString("product.setting",
                                               nextProduct.getName(),
                                               nextProduct.getId());
                errors.add(productText);
            }

            transferErrors(localErrors, errors);
        }

        return result;
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
        return ( (productId != null) &&
                 productId.startsWith(PRODUCT_AND_PREFIX) );
    }

    protected String getKernelProductId(String productId)
    {
        if ( !isAndProductPrereq(productId) )
            return productId;

        return productId.substring( PRODUCT_AND_PREFIX.length() );
    }

    protected void splitProductPrereqs(efixDriver fix, Vector andPrereqs, Vector orPrereqs)
    {
        int prereqCount = fix.getProductPrereqCount();

        for ( int prereqNo = 0; prereqNo < prereqCount; prereqNo++ ) {
            productPrereq nextPrereq = fix.getProductPrereq(prereqNo);

            if ( isAndProductPrereq( nextPrereq.getProductId() ) )
                andPrereqs.addElement(nextPrereq);
            else
                orPrereqs.addElement(nextPrereq);
        }
    }

    protected boolean testAndProductPrereqs(HashMap productMap, Vector prereqs)
    {
        debug("Testing AND product prereqs");

        int prereqCount = prereqs.size();

        boolean foundFailure = false;

        for ( int prereqNo = 0; !foundFailure && (prereqNo < prereqCount); prereqNo++ ) {
            debug("Testing AND prereq [ " + prereqNo + " ]");

            productPrereq nextPrereq = (productPrereq) prereqs.elementAt(prereqNo);

            foundFailure = !productPrereqIsSatisfied(nextPrereq, productMap);
        }
        
        return !foundFailure;
    }

    protected boolean testOrProductPrereqs(HashMap productMap, Vector prereqs)
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

            foundMatch = !productPrereqIsSatisfied(nextPrereq, productMap);
        }
        
        return foundMatch;
    }

    protected boolean testProductPrereqs(efixDriver fix, Vector errors)
    {
        debug("Testing product prereqs: ", fix.getId());

        int prereqCount = fix.getProductPrereqCount();
        if ( prereqCount == 0 ) {
            debug("No prereqs; answering true.");
            return true;
        }

        HashMap productMap = getProductMap();

        Vector andPrereqs = new Vector();
        Vector orPrereqs = new Vector();

        splitProductPrereqs(fix, andPrereqs, orPrereqs);

        if ( !testAndProductPrereqs(productMap, andPrereqs) ||
             !testOrProductPrereqs(productMap, orPrereqs) ) {
            debug("Product prereqs were not satisfied.");

            errors.add( getString("efix.fails.product", fix.getId()) );
            return false;

        } else {
            debug("Product prereqs were satisfied.");

            return true;
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

    protected boolean productPrereqIsSatisfied(productPrereq prereq, HashMap productMap)
    {
        String productId = prereq.getProductId();
        String kernelId  = getKernelProductId(productId);

        debug("Testing for product: " + productId + ": " + kernelId);

        product matchingProduct = (product) productMap.get(kernelId);

        if ( matchingProduct == null ) {
            debug("That product was not found.");
            return false;
        }

        boolean result = true;

        String prereqVersion = prereq.getBuildVersion();

        if ( (prereqVersion.length() != 0) && !prereqVersion.equals("*") ) {
            if ( !prereqVersion.equals(matchingProduct.getVersion()) ) {
                if ( isDebug() ) {
                    debug("Expecting build version [ " + prereqVersion +
                          " ], got [ " + matchingProduct.getVersion() + " ]");
                }

                result = false;
            }
        } else {
            debug("Pass on product version");
        }

        String prereqDate = prereq.getBuildDate();

        if ( (prereqDate.length() != 0) && !prereqDate.equals("*") ) {
            if ( !prereqDate.equals(matchingProduct.getBuildInfo().getDate()) ) {
                if ( isDebug() ) {
                    debug("Expecting build date [ " + prereqDate +
                          " ], got [ " + matchingProduct.getBuildInfo().getDate() + " ]");
                }

                result = false;
            }
        } else {
            debug("Pass on product build date");
        }

        String prereqLevel = prereq.getBuildLevel();

        if ( (prereqLevel.length() != 0) && !prereqLevel.equals("*") ) {
            if ( !prereqLevel.equals(matchingProduct.getBuildInfo().getLevel()) ) {
                if ( isDebug() ) {
                    debug("Expecting build level [ " + prereqLevel +
                          " ], got [ " + matchingProduct.getBuildInfo().getLevel() + " ]");
                }

                result = false;
            }
        } else {
            debug("Pass on product build date");
        }

        if ( isDebug() ) {
            if ( result )
                debug("Product matches");
            else
                debug("Product does not match");
        }

        return result;
    }

    public boolean testPlatformPrereqs(Vector efixes, Vector errors)
    {
        boolean result = true;
        Vector localErrors = new Vector();

        String architecture = System.getProperty("os.arch");
        String osPlatform = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");

        int numFixes = efixes.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            if ( !testPlatformPrereqs(nextFix,
                                      architecture, osPlatform, osVersion,
                                      localErrors) ) {
                result = false;
            }
        }

        if ( !result ) {
            errors.add( getString("platform.prereq.failure") );

            String[] platformArgs = new String[] {
                architecture,
                osPlatform,
                osVersion
            };

            errors.add( getString("platform.setting", platformArgs) );

            transferErrors(localErrors, errors);
        }

        return result;
    }

    protected boolean testPlatformPrereqs(efixDriver fix,
                                          String architecture,
                                          String osPlatform,
                                          String osVersion,
                                          Vector errors)
    {
        int prereqCount = fix.getPlatformPrereqCount();
        if ( prereqCount == 0 )
            return true;

        boolean foundMatch = false;

        for ( int prereqNo = 0; !foundMatch && (prereqNo < prereqCount); prereqNo++ ) {
            platformPrereq nextPrereq = fix.getPlatformPrereq(prereqNo);
            foundMatch = platformPrereqIsSatisfied(nextPrereq,
                                                   architecture, osPlatform, osVersion);
        }

        if ( !foundMatch )
            errors.add( getString("efix.fails.platform", fix.getId()) );

        return foundMatch;
    }

    public static final String platformWildcard = "*";

    protected boolean platformPrereqIsSatisfied(platformPrereq prereq,
                                                String architecture,
                                                String osPlatform,
                                                String osVersion)
    {
        String requiredArchitecture = prereq.getArchitecture();

        if ( (requiredArchitecture != null) && (requiredArchitecture.length() > 0) ) {

            if ( requiredArchitecture.endsWith(platformWildcard) ) {
                int matchLen = requiredArchitecture.length() - platformWildcard.length();

                if ( !architecture.regionMatches(0, requiredArchitecture, 0, matchLen) )
                    return false;

            } else if ( !architecture.equals(requiredArchitecture) ) {
                return false;
            }
        }

        String requiredOSPlatform = prereq.getOSPlatform();

        if ( (requiredOSPlatform != null) && (requiredOSPlatform.length() > 0) ) {

            if ( requiredOSPlatform.endsWith(platformWildcard) ) {
                int matchLen = requiredOSPlatform.length() - platformWildcard.length();

                if ( !osPlatform.regionMatches(0, requiredOSPlatform, 0, matchLen) )
                    return false;

            } else if ( !osPlatform.equals(requiredOSPlatform) ) {
                return false;
            }
        }

        String requiredOSVersion = prereq.getOSVersion();

        if ( (requiredOSVersion != null) && (requiredOSVersion.length() > 0) ) {

            if ( requiredOSVersion.endsWith(platformWildcard) ) {
                int matchLen = requiredOSVersion.length() - platformWildcard.length();

                if ( !osVersion.regionMatches(0, requiredOSVersion, 0, matchLen) )
                    return false;

            } else if ( !osVersion.equals(requiredOSVersion) ) {
                return false;
            }
        }

        return true;
    }

    public boolean testNegativePrereqs(Vector efixes, Vector errors)
    {
        HashMap efixMap = new HashMap();

        int numFixes = efixes.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            efixMap.put(nextFix.getId(), nextFix);
        }

        Vector localErrors = new Vector();
        boolean result = true;

        // Look for conflicts between the efixes and each other

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            int numPrereqs = nextFix.getEFixPrereqCount();

            for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                efixPrereq nextPrereq = nextFix.getEFixPrereq(prereqNo);

                if ( nextPrereq.getIsNegativeAsBoolean() ) {
                    String negativeId = nextPrereq.getEFixId();

                    if ( efixMap.get(negativeId) != null ) {
                        localErrors.add( getString("efix.install.fails.negative.prereq.concurrent",
                                                   nextFix.getId(), negativeId) );

                        result = false;
                    }
                }
            }
        }

        WPProduct useProduct = getWPProduct();

        // Look for conflicts between the efixes and installed efixes

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            int numPrereqs = nextFix.getEFixPrereqCount();

            for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                efixPrereq nextPrereq = nextFix.getEFixPrereq(prereqNo);

                if ( nextPrereq.getIsNegativeAsBoolean() ) {
                    String negativeId = nextPrereq.getEFixId();

                    if ( useProduct.efixPresent(negativeId) ) {
                        localErrors.add( getString("efix.install.fails.negative.prereq.about.to.install",
                                                   nextFix.getId(), negativeId) );
                        
                        result = false;
                    }
                }
            }
        }

        WASHistory useHistory = getWPHistory();

        // Look for conflicts between the installed efixes and the efixes

        Iterator efixNames = useProduct.getEFixNames();

        while ( efixNames.hasNext() ) {
            String nextName = (String) efixNames.next();

            efixDriver nextDriver = useHistory.getEFixDriverByFilename(nextName);

            int numPrereqs;
            String nextId;

            if ( nextDriver == null ) {
                debug(" >> Warning: Could not load driver for: ", nextName);
                numPrereqs = 0;
                nextId = null;
            } else {
                numPrereqs = nextDriver.getEFixPrereqCount();
                nextId = nextDriver.getId();
            }

            for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                efixPrereq nextPrereq = nextDriver.getEFixPrereq(prereqNo);

                if ( nextPrereq.getIsNegativeAsBoolean() ) {
                    String negativeId = nextPrereq.getEFixId();

                    if ( efixMap.get(negativeId) != null ) {
                        localErrors.add( getString("efix.install.fails.negative.prereq.installed",
                                                   nextId, negativeId) );
                        
                        result = false;
                    }
                }
            }
        }

        if ( !result ) {
            errors.add( getString("efix.install.negative.prereq.failure") );
            transferErrors(localErrors, errors);
        }

        return result;
    }

    public boolean testEFixPrereqs(Vector efixes, Vector installOrder, Vector errors)
    {
        FactorSorter sorter = new FactorSorter();

        Vector externalPrereqs = sorter.buildGraph(efixes);

        boolean result = testExternalPrereqs(externalPrereqs, errors);

        Vector factorOrder = sorter.sort();
        Vector driverOrder = sorter.expandFactors(factorOrder);

        installOrder.addAll(driverOrder);

        return result;
    }

    protected boolean testExternalPrereqs(Vector externalPrereqs, Vector errors)
    {
        Vector localErrors = new Vector();
        boolean result = true;

        WPProduct useProduct = getWPProduct();

        int numPrereqs = externalPrereqs.size();

        for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
            String[] prereqInfo = (String[]) externalPrereqs.elementAt(prereqNo);

            String
                requestor = prereqInfo[0],
                prereq = prereqInfo[1];

            if ( !useProduct.efixPresent(prereq) ) {
                localErrors.addElement( getString("efix.install.fails.prereq",
                                                  new String[] { requestor, prereq }) );
                
                result = false;
            }
        }

        if ( !result ) {
            errors.add( getString("efix.install.prereq.failure") );
            transferErrors(localErrors, errors);
        }

        return result;
    }
        
    public boolean testRequiredComponents(Vector efixes, Vector errors)
    {
        boolean result = true;

        Vector localErrors = new Vector();

        int numFixes = efixes.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            if ( !testRequiredComponents(nextFix, localErrors) )
                result = false;
        }

        if ( !result ) {
            errors.add( getString("efix.component.failure") );
            transferErrors(localErrors, errors);
        }

        return result;
    }

    protected boolean testRequiredComponents(efixDriver fix, Vector errors)
    {
        boolean result = true;

        int numUpdates = fix.getComponentUpdateCount();

        for ( int updateNo = 0; updateNo < numUpdates; updateNo++ ) {
            componentUpdate nextUpdate = fix.getComponentUpdate(updateNo);

            if ( !testRequiredComponent(fix, nextUpdate, errors) )
                result = false;
        }

        return result;
    }

    protected boolean testRequiredComponent(efixDriver fix, componentUpdate update,
                                            Vector errors)
    {
        if ( !update.getIsRequiredAsBoolean() )
            return true;

        String componentName = update.getComponentName();

        boolean isPresent = getWPProduct().componentPresent(componentName);
        boolean isAdd = (update.getUpdateTypeAsEnum() == enumUpdateType.ADD_UPDATE_TYPE);

        // Could use 'isPresent == !isAdd', but that is confusing.

        if ( (isPresent && !isAdd) || // Allow if present, and not an add.
             (!isPresent && isAdd) )  // Allof if absent, and an add.
            return true;

        errors.add( getString("component.fails.efix", fix.getId(), componentName) );

        return false;
    }

    public boolean testComponentPrereqs(Vector efixes, Vector errors)
    {
        debug("Testing efix component prereqs.");

        boolean result = true;

        Vector localErrors = new Vector();

        int numFixes = efixes.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            efixDriver nextFix = (efixDriver) efixes.elementAt(fixNo);

            if ( !testComponentPrereqs(nextFix, localErrors) )
                result = false;
        }

        if ( !result ) {
            errors.add( getString("efix.component.prereq.failure") );
            transferErrors(localErrors, errors);
        }

        debug("Testing efix component prereqs: Done");

        return result;
    }

    protected boolean testComponentPrereqs(efixDriver fix, Vector errors)
    {
       debug("Testing efix (efix) component prereqs: ", fix.getId());

       boolean result = true;

       int numUpdates = fix.getComponentUpdateCount();

       debug("Total component updates: " + fix.getComponentUpdateCount() );
       List badComps = new ArrayList(numUpdates);
       Vector prereqErrors = new Vector();

       for ( int updateNo = 0; updateNo < numUpdates; updateNo++ ) {
           componentUpdate nextUpdate = fix.getComponentUpdate(updateNo);

           if ( !testComponentPrereqs(fix, nextUpdate, prereqErrors) ) {
              if ( nextUpdate.getIsRequiredAsBoolean() ) {
                 debug("Component prereq for required component update (" + updateNo + ") " + nextUpdate.getComponentName() + 
                      " not satisfied." );
                 result = false;
              } else {
                 debug("Skipping component update (" + updateNo + ") " + nextUpdate.getComponentName() + 
                      " with primary content " + nextUpdate.getPrimaryContent() + 
                      " component-prereq not matched" );
                 badComps.add( new Integer(updateNo) );
              }
              //result = false;
           }
       }

       if (result) {
          // Still OK.
          // remove the badComps from set of component updates.
          debug("Removing component updates that unsatisfy component-prereq Checking" );
          // Remove in reverse order so we don't mess up the index numbers.
          for ( int i=badComps.size()-1; i>=0; i--) {
             fix.removeComponentUpdate( ((Integer)badComps.get( i ) ).intValue() );
          }
          if ( fix.getComponentUpdateCount() <= 0 ) result = false;
       }

       if ( isDebug() )
           debug("Testing efix component prereqs: " + fix.getId() + ": Done, returning  " + result);

       if (!result) {
          errors.addAll( prereqErrors );
       }

       return result;
    }
    private static final String WAS_LEVEL_PREREQ = "was-level";

    protected boolean testComponentPrereqs(efixDriver fix, componentUpdate update,
                                           Vector errors)
    {

        boolean result = true;
        int customCount = update.getCustomPropertyCount();
        for ( int i=0; i<customCount; i++ ) {
            customProperty prop = update.getCustomProperty( i );
            if ( prop.getPropertyName().equals( WAS_LEVEL_PREREQ ) ) {
                if ( isDebug() ) {
                   debug("Testing PTF component was-level prereq: " + prop.getPropertyValue() );
                }
                WASProduct wasp = new WASProduct( WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ) );
                product wasprod = wasp.getProductById( "BASE" );
                if (wasprod != null) {
                    result = wasprod.getVersion().startsWith( prop.getPropertyValue() );
                } else {
                    result = false;
                }
                if (!result) {
                    errors.addElement( getString("efix.fails.component.prereq.prodlevel",
                                                 new Object[] { fix.getId(),
                                                    wasprod.getName(),
                                                    prop.getPropertyValue(),
                                                    wasprod.getVersion(),
                                                 }) );
                }
            }
        }
        if (result) { 
            int numPrereqs = update.getComponentPrereqCount();

            if ( numPrereqs == 0 )
                return true;


            for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                componentVersion nextPrereq = update.getComponentPrereq(prereqNo);

                if ( isDebug() ) {
                    debug("Testing efix component prereqs: " + fix.getId(),
                          " [ " + prereqNo + " ]" );
                }

                if ( !componentPrereqIsSatisfied(nextPrereq) ) {
                    result = false;

                    errors.addElement( getString("efix.fails.component.prereq",
                                                 new Object[] { fix.getId(),
                                                                nextPrereq.getComponentName(),
                                                                nextPrereq.getSpecVersion(),
                                                                nextPrereq.getBuildVersion(),
                                                                nextPrereq.getBuildDate() }) );
                }

            }
        }


        return result;
    }

    protected boolean componentPrereqIsSatisfied(componentVersion prereq)
    {
        WPProduct useProduct = getWPProduct();

        String componentName = prereq.getComponentName();

        debug("Prereq against component: ", componentName);

        if ( !useProduct.componentPresent(componentName) ) {
            debug("That component is not installed.");
            return false;
        }

        boolean result = true;

        component installedComponent = useProduct.getComponentByName(componentName);

        String prereqSpecVersion = prereq.getSpecVersion();

        if ( prereqSpecVersion.equals("*") ) {
            debug("Pass on spec version");

        } else if ( !installedComponent.getSpecVersion().equals(prereqSpecVersion) ) {
            if ( isDebug() ) {
                debug("Expecting spec version [ " + prereqSpecVersion + " ]",
                      ", got [ " + installedComponent.getSpecVersion() + " ]");
            }

            result = false;
        }

        String prereqBuildVersion = prereq.getBuildVersion();

        if ( prereqBuildVersion.equals("*") ) {
            debug("Pass on build version");

        } else if ( !installedComponent.getBuildVersion().equals(prereqBuildVersion) ) {
            if ( isDebug() ) {
                debug("Expecting build version [ " + prereqBuildVersion + " ]",
                      ", got [ " + installedComponent.getBuildVersion() + " ]");
            }

            result = false;
        }

        String prereqBuildDate = prereq.getBuildDate();

        if ( prereqBuildDate.equals("*") ) {
            debug("Pass on build date");

        } else if ( !installedComponent.getBuildDate().equals(prereqBuildDate) ) {
            if ( isDebug() ) {
                debug("Expecting build date [ " + prereqBuildDate + " ]",
                      ", got [ " + installedComponent.getBuildDate() + " ]");
            }

            result = false;
        }

        if ( isDebug() ) {
            if ( result )
                debug("Component prereq is OK");
            else
                debug("Component prereq fails");
        }

        return result;
    }

    public boolean testEFixUninstallation(Vector efixIds,
                                          Vector uninstallOrder,
                                          Vector errors)
    {
        debug("Testing EFix Uninstall Prerequisites.");

        HashMap uninstallMap = getIdMap(efixIds);

        boolean
            satisfiesInstallBlocking = testEFixBlockingUninstall(efixIds, uninstallMap, errors),
            satisfiedEFixPrereqs = testEFixPrereqsUninstall(efixIds, uninstallMap, errors),
            satisfiedEFixCoreqs = testEFixCoreqsUninstall(efixIds, uninstallMap,
                                                          satisfiedEFixPrereqs, errors),
            backupsAreAvailable = testBackupAvailability(efixIds, errors);

        computeEFixUninstallOrder(efixIds, uninstallOrder);

        if ( isDebug() )
            displayUninstallErrors(efixIds, uninstallOrder, errors);

        debug("Testing EFix Uninstall Prerequisites: Complete");

        return
            satisfiesInstallBlocking &&
            satisfiedEFixPrereqs &&
            backupsAreAvailable;
    }

    protected void displayUninstallErrors(Vector efixIds, Vector uninstallOrder, Vector errors)
    {
        System.out.println("Uninstall Prerequisite Report: ");
        System.out.println();

        System.out.println("EFixes: " );

        int numIds = efixIds.size();

        for ( int idNo = 0; idNo < numIds; idNo++ ) {
            String nextId = (String) efixIds.elementAt(idNo);
            System.out.println("  EFix: " + nextId);
        }

        System.out.println();

        System.out.println("EFixes (uninstall order): " );

        for ( int idNo = 0; idNo < numIds; idNo++ ) {
            String nextId = (String) uninstallOrder.elementAt(idNo);
            System.out.println("  EFix: " + nextId);
        }

        System.out.println();

        System.out.println("Uninstall prerequisite disatisfaction: " );

        int numErrors = errors.size();

        for ( int errorNo = 0; errorNo < numErrors; errorNo++ ) {
            String nextError = (String) errors.elementAt(errorNo);
            System.out.println("[" + errorNo + "]: " + nextError);
        }

        System.out.println();
    }

    protected boolean testEFixBlockingUninstall(Vector efixIds, HashMap uninstallMap, Vector errors)
    {
        debug("Testing uninstall blocking.");

        HashMap orderMapping = getInstallOrderMapping();

        boolean noMinYet = true;
        int minOffset = -1;

        int numIds = efixIds.size();

        for ( int idNo = 0; idNo < numIds; idNo++ ) {
            String nextId = (String) efixIds.elementAt(idNo);

            Integer nextOffset = (Integer) orderMapping.get(nextId);

            if ( nextOffset == null ) {
                debug("Strange: No install found during blocking test: ", nextId);
            } else {
                int nextOffsetValue = nextOffset.intValue();

                if ( noMinYet || (nextOffsetValue < minOffset) ) {
                    noMinYet = false;
                    minOffset = nextOffsetValue;
                }

                orderMapping.remove(nextId);
            }
        }

        if ( noMinYet ) {
            debug("Strange: Found no installs during blocking test.");
            return true;
        }

        boolean noBlockingFixes = true;

        Iterator remainingFixIds = orderMapping.keySet().iterator();

        while ( remainingFixIds.hasNext() ) {
            String nextId = (String) remainingFixIds.next();
            Integer nextOffset = (Integer) orderMapping.get(nextId);

            debug("Scanning remaining: ", nextId);
            debug("  Offset: ", nextOffset);

            if ( nextOffset.intValue() > minOffset ) {
                debug("  >> Blocking error");

                noBlockingFixes = false;
                errors.addElement( getString("efix.uninstall.fails.blocking", new Object[] { nextId }) );

                // com/ibm/websphere/update/efix/prereq/EFixPrereqNLS.nlsprops
                //
                // efix.uninstall.fails.blocking=
                //    The fix {0} is not selected to be uninstalled, but was installed
                //    after a fix which is selected to be installed.
            } else {
                debug("  >> No blocking error");
            }

        }

        debug("Testing uninstall blocking: ", (noBlockingFixes ? "OK" : "Blocked") );

        return noBlockingFixes;
    }

    protected HashMap getInstallOrderMapping()
    {
        eventHistory useHistory = getWPHistory().getHistory();

        Vector fixIdsInOrder = new Vector();
        HashMap installMap = new HashMap();

        int numEvents = useHistory.getUpdateEventCount();
        debug("Scanning for fixes in order: " + numEvents);

        for ( int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            updateEvent nextEvent = useHistory.getUpdateEvent(eventNo);

            String nextEventId = nextEvent.getId();
            String nextEventType = nextEvent.getEventType();
            String nextEventAction = nextEvent.getUpdateAction();

            debug("Event id: ", nextEventId);
            debug("  Type  : ", nextEventType);
            debug("  Action: ", nextEventAction);

            if ( nextEvent.getEventTypeAsEnum() == enumEventType.EFIX_EVENT_TYPE ) {
                enumUpdateAction useAction = nextEvent.getUpdateActionAsEnum();

                updateEvent priorEvent = (updateEvent) installMap.get(nextEventId);

                if ( (useAction == enumUpdateAction.INSTALL_UPDATE_ACTION) ||
                     (useAction == enumUpdateAction.SELECTIVE_INSTALL_UPDATE_ACTION) ) {

                    if ( priorEvent == null ) {
                        debug("  ==> Installation: mapping.");

                        fixIdsInOrder.add(nextEventId);
                        installMap.put(nextEventId, nextEvent);

                    } else {
                        debug("  ==> Redundant installation: skipping.");
                    }

                } else {
                    if ( priorEvent == null ) {
                        debug("  ==> Strange: Uninstall without install: skipping.");

                    } else {
                        debug("  ==> Uninstall: Removing mapped installation.");

                        fixIdsInOrder.remove(nextEventId);
                        installMap.remove(nextEventId);
                    }
                }
            }
        }

        HashMap orderMap = new HashMap();

        int numFixIds = fixIdsInOrder.size();
        for ( int idNo = 0; idNo < numFixIds; idNo++ ) {
            String nextId = (String) fixIdsInOrder.elementAt(idNo);

            orderMap.put(nextId, new Integer(idNo));
        }

        return orderMap;
    }

    public boolean testEFixPrereqsUninstall(Vector efixIds, HashMap uninstallMap, Vector errors)
    {
        boolean result = true;
        Vector localErrors = new Vector();

        WASHistory useHistory = getWPHistory();
        Iterator installedNames = useHistory.getEFixDriverNames();

        WPProduct useProduct = getWPProduct();

        while ( installedNames.hasNext() ) {
            String nextName = (String) installedNames.next();

            efixDriver nextDriver = useHistory.getEFixDriverByFilename(nextName);

            int numPrereqs;
            String nextId;

            if ( nextDriver == null ) {
                debug(" >> Warning: Could not load driver for: ", nextName);
                numPrereqs = 0;
                nextId = null;
            } else {
                numPrereqs = nextDriver.getEFixPrereqCount();
                nextId = nextDriver.getId();
            }

            if ( (nextId != null) && !uninstallMap.containsKey(nextId) ) {
                // This efix is not scheduled to be uninstalled;
                // none of it's prereq's may be scheduled to be uninstalled.

                for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                    efixPrereq nextPrereq = nextDriver.getEFixPrereq(prereqNo);

                    if ( !nextPrereq.getIsNegativeAsBoolean() ) {
                        String prereqId = nextPrereq.getEFixId();

                        if ( uninstallMap.containsKey(prereqId) ) {
                            localErrors.addElement( getString("efix.uninstall.fails.prereq",
                                                              prereqId, nextId) );
                            result = false;
                        }
                    }
                }
            }
        }

        if ( !result ) {
            errors.add( getString("efix.uninstall.prereq.failure") );
            transferErrors(localErrors, errors);
        }

        return result;
    }

    public boolean testEFixCoreqsUninstall(Vector efixIds, HashMap uninstallMap,
                                           boolean alreadyHaveAPrereqError, Vector errors)
    {
        boolean result = true;
        Vector localErrors = new Vector();

        // Look for conflicts between the installed efixes and the efixes

        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        Iterator efixNames = useProduct.getEFixNames();

        while ( efixNames.hasNext() ) {
            String nextName = (String) efixNames.next();
            efixDriver nextDriver = useHistory.getEFixDriverByFilename(nextName);

            int numPrereqs;
            String nextId;

            if ( nextDriver == null ) {
                debug(" >> Warning: Could not load driver for: ", nextName);
                numPrereqs = 0;
                nextId = null;
            } else {
                numPrereqs = nextDriver.getEFixPrereqCount();
                nextId = nextDriver.getId();
            }

            // Looking for efixes which will remain installed but which
            // have prereqs that are scheduled to be uninstalled.

            if ( (nextId != null) && !uninstallMap.containsKey(nextId) ) {

                for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                    efixPrereq nextPrereq = nextDriver.getEFixPrereq(prereqNo);

                    // Don't care about negative prereqs!

                    if ( !nextPrereq.getIsNegativeAsBoolean() ) {
                        String prereqId = nextPrereq.getEFixId();

                        if ( uninstallMap.containsKey(prereqId) ) {
                            localErrors.addElement( getString("efix.uninstall.fails.prereq",
                                                              prereqId, nextId) );
                            result = false;
                        }
                    }
                }
            }
        }

        if ( !result ) {
            if ( !alreadyHaveAPrereqError )
                errors.add( getString("efix.uninstall.prereq.failure") );

            transferErrors(localErrors, errors);
        }

        return result;
    }

    public void computeEFixUninstallOrder(Vector efixIds, Vector uninstallOrder)
    {
        final HashMap indexMap = getInstallMap();

        Comparator idComparator = new Comparator() {
            public int compare(Object id1, Object id2) {
                Integer index1 = (Integer) indexMap.get((String) id1);
                Integer index2 = (Integer) indexMap.get((String) id2);

                return ( ((index1 == null) ? 0 : index1.intValue()) -
                         ((index2 == null) ? 0 : index2.intValue()) );
            }

            public boolean equals(Object id1, Object id2) {
                Integer index1 = (Integer) indexMap.get((String) id1);
                Integer index2 = (Integer) indexMap.get((String) id2);

                return ( ((index1 == null) ? 0 : index1.intValue()) ==
                         ((index2 == null) ? 0 : index2.intValue()) );
            }
        };

        Object[] ids = efixIds.toArray();

        Arrays.sort(ids, idComparator);

        for ( int idNo = 0; idNo < ids.length; idNo++ )
            uninstallOrder.addElement( ids[idNo] );
    }

    protected HashMap getIdMap(Vector ids)
    {
        HashMap map = new HashMap();

        int numIds = ids.size();

        for ( int idNo = 0; idNo < numIds; idNo++ ) {
            String nextId = (String) ids.elementAt(idNo);

            map.put(nextId, nextId);
        }

        return map;
    }

    protected HashMap getInstallMap()
    {
        HashMap indexMap = new HashMap();

        eventHistory installHistory = getWPHistory().getHistory();

        int numEvents = installHistory.getUpdateEventCount();

        for ( int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            updateEvent nextEvent = installHistory.getUpdateEvent(eventNo);

            if ( nextEvent.getEventTypeAsEnum() == enumEventType.EFIX_EVENT_TYPE ) {
                String efixId = nextEvent.getId();

                enumUpdateAction action = nextEvent.getUpdateActionAsEnum();

                if ( (action == enumUpdateAction.INSTALL_UPDATE_ACTION) ||
                     (action == enumUpdateAction.SELECTIVE_INSTALL_UPDATE_ACTION) ) {
                    indexMap.put(efixId, new Integer(eventNo));
                } else {
                    indexMap.remove(efixId);
                }
            }
        }

        return indexMap;
    }

    public boolean testBackupAvailability(Vector efixIds, Vector errors)
    {
        boolean result = true;

        Vector localErrors = new Vector();

        int numFixes = efixIds.size();

        for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
            String nextId = (String) efixIds.elementAt(fixNo);

            if ( !testBackupAvailability(nextId, localErrors) )
                result = false;
        }
        
        if ( !result ) {
            errors.add( getString("efix.uninstall.undo.failure") );
            transferErrors(localErrors, errors);
        }
            
        return result;
    }

    protected boolean testBackupAvailability(String efixId, Vector errors)
    {
        efixApplied anApplied = getWPHistory().getEFixAppliedById(efixId);

        if ( anApplied == null )
            return false;

        boolean result = true;

        int numCompApplieds = anApplied.getComponentAppliedCount();

        for ( int compNo = 0; compNo < numCompApplieds; compNo++ ) {
            componentApplied nextApplied = anApplied.getComponentApplied(compNo);

            if ( !testBackupAvailability(anApplied, nextApplied, errors) )
                result = false;
        }

        return result;
    }

    protected boolean testBackupAvailability(efixApplied parentApplied,
                                             componentApplied childApplied,
                                             Vector errors)
    {
        String backupName = childApplied.getBackupName();

        File backupFile = new File(backupName);

        if ( !backupFile.exists() || backupFile.isDirectory() ) {
            errors.add( getString("efix.uninstall.fails.undo",
                                  new String[] { parentApplied.getEFixId(),
                                                 childApplied.getComponentName(),
                                                 backupName }) );
            return false;
        } else {
            return true;
        }
    }

    protected void transferErrors(Vector source, Vector destination)
    {
        int numElements = source.size();

        for ( int elementNo = 0; elementNo < numElements; elementNo++ ) {
            Object nextElement = source.elementAt(elementNo);
            destination.add(nextElement);
        }
    }

    // Messaging ...
    //
    // static String getString(String);
    // static String getString(String, String)
    // static String getString(String, String, String)
    // static String getString(String, Object[]);

    // Answer the NLS text for the specified message id.

    protected static String getString(String msgCode)
    {
        return EFixPrereqException.getString(msgCode);
    }

    // Answer the NLS text for the specified message id, substituting
    // in the argument to the message text.

    protected static String getString(String msgCode, String arg)
    {
        return EFixPrereqException.getString(msgCode, arg);
    }

    protected static String getString(String msgCode, String arg1, String arg2)
    {
        return EFixPrereqException.getString(msgCode, arg1, arg2);
    }

    protected static String getString(String msgCode, Object[] args)
    {
        return EFixPrereqException.getString(msgCode, args);
    }
}

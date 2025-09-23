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
 * History 1.3, 8/6/04
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

import com.ibm.websphere.product.WASProduct;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.update.efix.prereq.CommonPrereqChecker;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.text.MessageFormat;
import java.util.*;

/**
 *  
 */
public class PTFPrereqChecker {

   // Program versioning ...
   public static final String pgmVersion = "1.3" ;
   // Program versioning ...
   public static final String pgmUpdate = "8/6/04" ;

   public static final String debugPropertyName = "com.ibm.websphere.update.ptf.prereq.debug" ;
   public static final String debugTrueValue = "true" ;
   public static final String debugFalseValue = "false" ;

    private Vector andCoreqList = new Vector();    // this holds all the ids in the AND coreq
    private Vector orCoreqList = new Vector();     // this holds all the ids in the OR coreq that exist on the machine

   // Debugging support ...

   protected static boolean debug;

   static {
      String debugValue = System.getProperty(debugPropertyName);

      debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
      
      //debug = true;
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

   public PTFPrereqChecker(WPProduct wpsProduct, WPHistory wpsHistory)
   {
      this.wpsProduct = wpsProduct;
      this.wpsHistory = wpsHistory;
   }

   protected WPProduct wpsProduct;
   protected WPHistory wpsHistory;

   protected WPProduct getWPProduct() {
      return wpsProduct;
   }

   protected WPHistory getWPHistory() {
      return wpsHistory;
   }

   public boolean testPTFInstallation(ptfDriver ptf,
                                      Vector errors) {
      boolean
//        satisfiedComponentPrereqs   = testComponentPrereqs(ptf, errors),
        satisifiedProductPrereqs    = testProductPrereqs(ptf, errors),
        satisifiedProductCoreqs    = testProductCoreqs(ptf, errors),
        satisfiedProductUpdate = testProductUpdate(ptf, errors);

      if ( isDebug() )
         displayInstallErrors(ptf, errors);

      return
            satisifiedProductCoreqs     &&
            satisifiedProductPrereqs    &&
            satisfiedProductUpdate;
//            satisfiedComponentPrereqs;

   }

    protected void displayInstallErrors( ptfDriver ptf, Vector errors) {
       System.out.println("Install Prerequisite Report: ");
       System.out.println();

       System.out.println("PTF: " + ptf.getId());

       /*
       //System.out.println("PTFs: " );
       int numFixes = ptfs.size();

       for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
          ptfDriver nextFix = (ptfDriver) ptfs.elementAt(fixNo);

          System.out.println("  PTF: " + nextFix.getId());
       }
       */
       System.out.println();
       /*
       System.out.println("EFixes (install order): " );

       for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
           efixDriver nextFix = (efixDriver) installOrder.elementAt(fixNo);

           System.out.println("  EFix: " + nextFix.getId());
       }

       System.out.println();
       */
       System.out.println("Install prerequisite disatisfaction: " );

       int numErrors = errors.size();

       for ( int errorNo = 0; errorNo < numErrors; errorNo++ ) {
          String nextError = (String) errors.elementAt(errorNo);

          System.out.println("[" + errorNo + "]: " + nextError);
       }

       System.out.println();
    }


   public boolean testComponentPrereqs(Vector ptfs, Vector errors) {
      debug("Testing ptf component prereqs.");

      boolean result = true;

      Vector localErrors = new Vector();

      int numFixes = ptfs.size();

      for ( int fixNo = 0; fixNo < numFixes; fixNo++ ) {
         ptfDriver nextFix = (ptfDriver) ptfs.elementAt(fixNo);

         if ( !testComponentPrereqs(nextFix, localErrors) )
            result = false;
      }

      if ( !result ) {
         errors.add( getString("ptf.component.prereq.failure") );
         transferErrors(localErrors, errors);
      }

      debug("Testing efix component prereqs: Done");

      return result;
   }
// Hank
   public boolean testComponentPrereqs(ptfDriver ptf, Vector errors) {
      debug("Testing PTF component prereqs: ", ptf.getId());

      boolean result = true;

      int numUpdates = ptf.getComponentUpdateCount();

      if ( numUpdates == 0 ) {
            debug("No component prereqs; answering false.");
            debug("Invalid ptfDriver, component-update is required for fixpack "+ptf.getId());
            errors.add(getString("element.required", "component-update", ptf.getId()));
            return false;
      }
      debug("Total component updates: " + ptf.getComponentUpdateCount() );
      List badComps = new ArrayList(numUpdates);
      Vector prereqErrors = new Vector();

      for ( int updateNo = 0; updateNo < numUpdates; updateNo++ ) {
         componentUpdate nextUpdate = ptf.getComponentUpdate(updateNo);

         if ( !testComponentPrereqs(ptf, nextUpdate, prereqErrors) ) {
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
            ptf.removeComponentUpdate( ((Integer)badComps.get( i ) ).intValue() );
         }
         if ( ptf.getComponentUpdateCount() <= 0 ) result = false;
      }

      if ( isDebug() )
         debug("Testing PTF component prereqs: " + ptf.getId() + ": Done, returning  " + result);

      if (!result) {
         errors.addAll( prereqErrors );
      }

      return result;
   }

   private static final String WAS_LEVEL_PREREQ = "was-level";

   protected boolean testComponentPrereqs(ptfDriver ptf, componentUpdate update,
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
                   errors.addElement( getString("ptf.fails.component.prereq.prodlevel",
                                                new Object[] { ptf.getId(),
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
                 debug("Testing PTF component prereqs: " + ptf.getId(),
                       " [ " + prereqNo + " ]" );
              }

              if ( !componentPrereqIsSatisfied(nextPrereq) ) {
                 result = false;

                 errors.addElement( getString("ptf.fails.component.prereq",
                                              new Object[] { ptf.getId(),
                                                 nextPrereq.getComponentName(),
                                                 nextPrereq.getSpecVersion(),
                                                 nextPrereq.getBuildVersion(),
                                                 nextPrereq.getBuildDate()}) );
              }


           }
       }

       return result;
   }

   protected boolean componentPrereqIsSatisfied(componentVersion prereq)
   {
      WPProduct useProduct = getWPProduct();

      String componentName = prereq.getComponentName();

      debug("Testing preReqs for component: ", componentName);

      if ( !useProduct.componentPresent(componentName) ) {
         debug("Component " + componentName + " is not installed.");
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

    protected void splitProductPrereqs(ptfDriver ptf, Vector orPrereqs, Vector andCoreqs, Vector orCoreqs)
    {
        int prereqCount = ptf.getProductPrereqCount();
        int coreqCount = ptf.getProductCoreqCount();

        // product-prereq is always in OR condition
        for ( int prereqNo = 0; prereqNo < prereqCount; prereqNo++ )
        {
        productPrereq nextPrereq = ptf.getProductPrereq(prereqNo);
        orPrereqs.addElement(nextPrereq);
        }
        // product-coreq is either AND or OR
        for ( int coreqNo = 0; coreqNo < coreqCount; coreqNo++ ) {
        productCoreq nextCoreq = ptf.getProductCoreq(coreqNo);

        if ( isAndProductPrereq( nextCoreq.getProductId() ) )
        andCoreqs.addElement(nextCoreq);
        else
        orCoreqs.addElement(nextCoreq);
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

            // foundMatch = !productPrereqIsSatisfied(nextPrereq, productMap);
            foundMatch = productPrereqIsSatisfied(nextPrereq, productMap);
        }

        return foundMatch;
    }

    protected boolean testProductPrereqs(ptfDriver ptf, Vector errors)
    {
        int prereqCount = ptf.getProductPrereqCount();
        if ( prereqCount == 0 ) {
            debug("No product prereqs; answering false.");
            debug("Invalid ptfDriver, product-prereq is required for fixpack "+ptf.getId());
            errors.add(getString("element.required", "product-prereq", ptf.getId()));
            return false;
        }
        if(prereqCount > 0)
        {
            boolean bNoError = true;
            for(int i=0; i<prereqCount; i++)
            {
                productPrereq pp = ptf.getProductPrereq(i);
                if(!CommonPrereqChecker.checkProductid(pp.getProductId()))
                    if(bNoError) bNoError = false;
                if(!CommonPrereqChecker.checkBuildversion(pp.getBuildVersion()))
                    if(bNoError) bNoError = false;
                if(!CommonPrereqChecker.checkBuildlevel(pp.getBuildLevel()))
                    if(bNoError) bNoError = false;
            }
            if (!bNoError) {
                errors.add(ptf.getId() + " - " + getString("product.prereq.failure"));
                debug("Invalid product-prereq, fixpack::"+ptf.getId());
                return bNoError;
            }
        }

        HashMap productMap = getProductMap();

        Vector andCoreqs = new Vector();
        Vector orCoreqs = new Vector();
        Vector orPrereqs = new Vector();

        splitProductPrereqs(ptf, orPrereqs, andCoreqs, orCoreqs);

        if (testOrProductPrereqs(productMap, orPrereqs) &&
                testProductCoreqs(productMap, andCoreqs, orCoreqs) )
        {
            debug("Product prereqs were satisfied.");
            buildAndCoreqList(andCoreqs);
            buildOrCoreqList(productMap, orCoreqs);
            return true;

        } else {
            debug("Product prereqs were not satisfied.");
            System.out.println("Product prereqs were not satisfied.");

            errors.add( getString("efix.fails.product", ptf.getId()) );
            return false;
        }

    }

    private void buildAndCoreqList(Vector andCoreqs)
    {
        int count = andCoreqs.size();
        if(count > 0)
        {
           for(int i=0; i<count; i++)
           {
               //System.out.println("AND: "+(((productPrereq)andCoreqs.get(i)).getProductId().toLowerCase()).substring( PRODUCT_AND_PREFIX.length() ));
               andCoreqList.add((((productPrereq)andCoreqs.get(i)).getProductId().toLowerCase()).substring( PRODUCT_AND_PREFIX.length() ));
           }
        }
    }

    public Vector getAndCoreqs()
    {
        return andCoreqList;
    }

    public Vector getOrCoreqs()
    {
        return orCoreqList;
    }

    private void buildOrCoreqList(HashMap productMap, Vector orCoreqs)
    {
        int count = orCoreqs.size();
        if(count > 0)
        {
            for(int i=0; i < count; i++)
            {
                String productId = ((productPrereq)orCoreqs.get(i)).getProductId();
                //System.out.println("OR :" + productId.toLowerCase());
                String kernelId  = getKernelProductId(productId);


                product matchingProduct = (product) productMap.get(kernelId);

                if ( matchingProduct != null )
                    orCoreqList.add(productId.toLowerCase());
            }
        }
    }

    // called by testProductPrereqs, this is for testing AND and OR
    protected boolean testProductCoreqs(HashMap productMap, Vector andCoreqs, Vector orCoreqs)
    {
        return (testAndProductPrereqs(productMap, andCoreqs) && testOrProductPrereqs(productMap, orCoreqs));
    }

    // called by testPTFInstallation, this is for validating the attributes
    protected boolean testProductCoreqs(ptfDriver ptf, Vector errors)
    {
        int coreqCount = ptf.getProductCoreqCount();
        if ( coreqCount == 0 ) {
            debug("No product coreqs; not mandatory; answering true.");
            debug("ptfDriver, product-coreq not exist, not required for ptf "+ptf.getId());
            return true;
        }
        if(coreqCount > 0)
        {
            boolean bNoError = true;
            for(int i=0; i<coreqCount; i++)
            {
                productCoreq pp = ptf.getProductCoreq(i);
                if(!CommonPrereqChecker.checkProductid(pp.getProductId()))
                    if(bNoError) bNoError = false;
                if(!CommonPrereqChecker.checkBuildversion(pp.getBuildVersion()))
                    if(bNoError) bNoError = false;
                if(!CommonPrereqChecker.checkBuildlevel(pp.getBuildLevel()))
                    if(bNoError) bNoError = false;
            }
            if (!bNoError) {
                errors.add(ptf.getId() + " - " + getString("efix.coreq.failure"));
                debug("Invalid product-coreq, efix::"+ptf.getId());
                return bNoError;
            }
        }

        debug("Product prereqs were satisfied.");

        return true;
    }

    private boolean testProductUpdate(ptfDriver ptf, Vector errors)
    {
        boolean bNoError = true;
        int updateCount = ptf.getProductUpdateCount();
        if ( updateCount == 0 ) {
            debug("No product updates; answering false.");
            debug("Invalid ptfDriver, at least one product-update is required for fixpack "+ptf.getId());
            errors.add(getString("element.required", "product-update", ptf.getId()));
            return false;
        }
        if(updateCount > 0)
        {
            for(int i=0; i<updateCount; i++)
            {
                productUpdate pp = ptf.getProductUpdate(i);
                String pid = pp.getProductId();
                if(null == pid || 0 == pid.trim().length())
                {
                    if(bNoError) bNoError = false;
                    errors.add(MessageFormat.format("{0} - product-update {1}: {2}",
                                new Object[]{ptf.getId(), new Integer(i + 1), getString("attribute.value.empty", "product-id")}));
                    debug("Invalid product-update, fixpack::"+ptf.getId());
                }
            }
        }
        return bNoError;
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

            if ( nextProductId.equalsIgnoreCase(WASProduct.PRODUCTID_BASE) ) {
                baseProduct = nextProduct;
                baseProductId = nextProductId;
            } else if ( nextProductId.equalsIgnoreCase(WASProduct.PRODUCTID_ND) ) {
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

        boolean result = true;

        debug("Testing preReqs for product: " + productId + ": " + kernelId);

        if ( productId.equals("*") || (productId.trim().length() == 0) ) {
            System.out.println(getString("attribute.value.not.allowed", productId, "<product-id>"));
            result = false;
        }

        product matchingProduct = (product) productMap.get(kernelId);

        if ( matchingProduct == null ) {
            debug("Product " + productId + ": " + kernelId + " was not found.");
            return false;
        }

        String prereqVersion = prereq.getBuildVersion();

        if ( (prereqVersion.length() == 0))
        {
            System.out.println(getString("attribute.value.empty", "<product-version>"));
            result = false;
        }
        else if((prereqVersion.length() != 0) && !prereqVersion.equals("*") ) {
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

        if ( (prereqLevel.length() == 0))
        {
            System.out.println(getString("attribute.value.empty", "<product-level>"));
            result = false;
        }
        else if ( (prereqLevel.length() != 0) && !prereqLevel.equals("*") ) {
            if ( !prereqLevel.equals(matchingProduct.getBuildInfo().getLevel()) ) {
                if ( isDebug() ) {
                    debug("Expecting build level [ " + prereqLevel +
                          " ], got [ " + matchingProduct.getBuildInfo().getLevel() + " ]");
                }

                result = false;
            }
        } else {
            debug("Pass on product build level");
        }

        if ( isDebug() ) {
            if ( result )
                debug("Product matches");
            else
                debug("Product does not match");
        }

        return result;
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

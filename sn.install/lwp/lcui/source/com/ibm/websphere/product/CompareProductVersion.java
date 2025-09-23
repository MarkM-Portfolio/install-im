/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2007, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/*
 * 5724-B88
 * CMVC Location: /wps/fix/src/com/ibm/websphere/product/CompareProductVersion.java, wps.base.fix, wps6.fix
 * adapted from ui/wp/code/wp.migration.core/src/com/ibm/wps/migration/core/util/PortalVersion.java
 * Version:       1.1
 * Last Modified: 02/07/07
 * Revision / History
 *-----------------------------------------------------------------------------
 * CMVC ID    Date       Who      Description
 *----------------------------------------------------------------------------
 *******************************************************************************/
package com.ibm.websphere.product;


import java.lang.Integer;

/**
 *  
 */
public class CompareProductVersion {

    private String versionMajor;

    private String versionMinor;

    private String versionMaintenance;

    private String versionUpdate;

    private String[] versionComponents;

    public static final int LESS_THAN = 0;
    public static final int LESS_THAN_OR_EQUAL = 1;
    public static final int EQUAL = 2;
    public static final int GREATER_THAN_OR_EQUAL = 3;
    public static final int GREATER_THAN = 4;
    public static final int NOT_EQUAL = 5;

    public static final int NUMBER_OF_COMPONENTS_IN_VERSION = 4;;

    /**
	 * @param versionMajor  the versionMajor to set
	 * @uml.property  name="versionMajor"
	 */
    public void setVersionMajor(String versionMajor) {
        this.versionMajor = versionMajor;
    }

    public String getMajorionMinor() {
        return versionMajor;
    } 

    /**
	 * @param versionMinor  the versionMinor to set
	 * @uml.property  name="versionMinor"
	 */
    public void setVersionMinor(String versionMinor) {
        this.versionMinor = versionMinor;
    }

    /**
	 * @return  the versionMinor
	 * @uml.property  name="versionMinor"
	 */
    public String getVersionMinor() {
        return versionMinor;
    }

    /**
	 * @param versionMaintenance  the versionMaintenance to set
	 * @uml.property  name="versionMaintenance"
	 */
    public void setVersionMaintenance(String versionMaintenance) {
        this.versionMaintenance = versionMaintenance;
    }

    /**
	 * @return  the versionMaintenance
	 * @uml.property  name="versionMaintenance"
	 */
    public String getVersionMaintenance() {
        return versionMaintenance;
    }

    /**
	 * @param versionUpdate  the versionUpdate to set
	 * @uml.property  name="versionUpdate"
	 */
    public void setVersionUpdate(String versionUpdate) {
        this.versionUpdate = versionUpdate;
    }

    /**
	 * @return  the versionUpdate
	 * @uml.property  name="versionUpdate"
	 */
    public String getVersionUpdate() {
        return versionUpdate;
    }

    /**
	 * @return  the versionComponents
	 * @uml.property  name="versionComponents"
	 */
    public String[] getVersionComponents() {
        return versionComponents;
    }



    public  CompareProductVersion(String version) {

        // Split on the dots ('.')
        versionComponents = getVersionComponents(version);
   
        // Store the values of the version components
        setVersionMajor(versionComponents[0]);
        setVersionMinor(versionComponents[1]);
        setVersionMaintenance(versionComponents[2]);
        setVersionUpdate(versionComponents[3]);

    }

    private boolean compareValue(int comparisonType, int value1, int value2) {

        if (comparisonType == CompareProductVersion.LESS_THAN) {
            return value1 < value2;
        }
        if (comparisonType == CompareProductVersion.LESS_THAN_OR_EQUAL) {
            return value1 <= value2;           
        }
        if (comparisonType == CompareProductVersion.EQUAL) {
            return value1 == value2;      
        }
        if (comparisonType == CompareProductVersion.GREATER_THAN_OR_EQUAL) {
            return value1 >= value2;
        }
        if (comparisonType == CompareProductVersion.GREATER_THAN) {
            return value1 > value2;
        }
        if (comparisonType ==  CompareProductVersion.NOT_EQUAL) {
            return value1 != value2;
        }
        else {
            throw new UnsupportedOperationException("Invalid comparison specified");
        }
    }

    public boolean isGreaterThan(String productVersionToCompareTo) {

        return !isLessThan(productVersionToCompareTo) && isNotEqualTo(productVersionToCompareTo);
    } 

    public boolean isLessThanOrEqualTo(String productVersionToCompareTo) {

        return isLessThan(productVersionToCompareTo) || isEqualTo(productVersionToCompareTo);
    } 

    public boolean isGreaterThanOrEqualTo(String productVersionToCompareTo) {

        return isGreaterThan(productVersionToCompareTo) || isEqualTo(productVersionToCompareTo);
    } 

    /*
     *Function Name:isLessThan
     *
     *Parameters:
     *
     *Description:
     *
     *Returns:
     *
    */     
    public boolean isLessThan(String productVersionToCompareTo) {

        boolean isLessThan = false;
        boolean exit = false;

        String[] productVersionToCompareToComponents = getVersionComponents(productVersionToCompareTo);

        // If the versions are not equal test to see if productVersionToCompareTo is less
        // than the internal version of this object
        if (isNotEqualTo(productVersionToCompareTo)) {
            // Test to see if we run into a component scanning from left to right that
            // is lesser in value.
            for (int i = 0 ; i < versionComponents.length && isLessThan == false && exit != true; ++i) {
    
                int valueA = Integer.parseInt(versionComponents[i]);
                int valueB = Integer.parseInt(productVersionToCompareToComponents[i]);

                if (compareValue(LESS_THAN,valueA,valueB)) {
                    isLessThan = true;
                }
                else if (compareValue(GREATER_THAN,valueA,valueB)) {
                    exit = true;
                }
            }
        }


        return isLessThan;
    }    



    /*
     *Function Name:isEqualTo
     *
     *Parameters:
     *
     *Description:
     *
     *Returns:
     *
    */     
    public boolean isEqualTo(String productVersionToCompareTo) {

        boolean isEqual = true;

        String[] productVersionToCompareToComponents = getVersionComponents(productVersionToCompareTo);

        for (int i = 0 ; i < versionComponents.length && isEqual == true ; ++i) {

            int valueA = Integer.parseInt(versionComponents[i]);
            int valueB = Integer.parseInt(productVersionToCompareToComponents[i]);

            if (!compareValue(EQUAL,valueA,valueB)) {
                isEqual = false;
            }

        }

        return isEqual;
    }

    /*
     *Function Name:isNotEqualTo
     *
     *Parameters:
     *
     *Description:
     *
     *Returns:
     *
    */     
    public boolean isNotEqualTo(String productVersionToCompareTo) {

       return !isEqualTo(productVersionToCompareTo);
    }



    String[] getVersionComponents(String version) {
   

        // Split on the dots ('.')
        String[] versionComponents = version.split("\\.");
        
        //System.out.println("***debug:getVersionComponents*** version = " + version);
        //System.out.println("***debug:getVersionComponents*** versionComponents.length = " + versionComponents.length);

        // Count the number of components if not 4 then assume the version is incorrectly formatted
        if (versionComponents.length != NUMBER_OF_COMPONENTS_IN_VERSION) {
            throw new IllegalArgumentException("The specified version is not in the correct format.  Use Major, Minor, Maintenance, Update format. (i.e 6.0.1.2)");
        }

        return versionComponents;
    } 
}

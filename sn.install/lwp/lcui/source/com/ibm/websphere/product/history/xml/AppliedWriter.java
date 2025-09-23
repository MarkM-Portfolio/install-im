/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.history.xml;

/*
 * EFix Driver (et al) Writer
 *
 * History 1.2, 9/26/03
 *
 * 12-Sep-2002 Initial Version
 *
 * 15-Oct-2002 Defect 150556:
 *
 *             Updated to close platform prereq elements
 *             immediately following the attributes.
 *
 *             Otherwise, the parser interprets the
 *             prereq as having non-empty text, causing
 *             a parse error.
 *
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class AppliedWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public AppliedWriter()
    {
        super();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        Iterator roots = rootElements.iterator();
        if ( !roots.hasNext() )
            return null;

        Object firstRootElement = roots.next();

        if ( firstRootElement instanceof efixDriver )
            return getDefaultEFixDriverDocTypeString();
        else if ( firstRootElement instanceof efixApplied )
            return getDefaultEFixAppliedDocTypeString();
        else if ( firstRootElement instanceof ptfDriver )
            return getDefaultPTFDriverDocTypeString();
        else if ( firstRootElement instanceof ptfApplied )
            return getDefaultPTFAppliedDocTypeString();
        else
            return null;
    }

    public String getDefaultEFixDriverDocTypeString()
    {
        return "<!DOCTYPE efix-driver SYSTEM \"applied.dtd\">";
    }

    public String getDefaultPTFDriverDocTypeString()
    {
        return "<!DOCTYPE ptf-driver SYSTEM \"applied.dtd\">";
    }

    public String getDefaultEFixAppliedDocTypeString()
    {
        return "<!DOCTYPE efix-applied SYSTEM \"applied.dtd\">";
    }

    public String getDefaultPTFAppliedDocTypeString()
    {
        return "<!DOCTYPE ptf-applied SYSTEM \"applied.dtd\">";
    }

    public void emitEFixDriver(efixDriver efixDriver)
    {
        beginDocument();

        printIndent();
        beginElementOpening(AppliedHandler.EFIX_DRIVER_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.ID_FIELD_TAG, efixDriver.getId());
        emitAttributeOnLine(AppliedHandler.SHORT_DESCRIPTION_FIELD_TAG, efixDriver.getShortDescription());
        emitAttributeOnLine(AppliedHandler.LONG_DESCRIPTION_FIELD_TAG, efixDriver.getLongDescription());
        emitAttributeOnLine(AppliedHandler.IS_TRIAL_FIELD_TAG, efixDriver.getIsTrial());
        emitAttributeOnLine(AppliedHandler.EXPIRATION_DATE_FIELD_TAG, efixDriver.getExpirationDate());
        emitAttributeOnLine(AppliedHandler.BUILD_VERSION_FIELD_TAG, efixDriver.getBuildVersion());

        printIndent();
        emitAttribute(AppliedHandler.BUILD_DATE_FIELD_TAG, efixDriver.getBuildDate());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numApars = efixDriver.getAparInfoCount();
        for (int aparNo = 0; aparNo < numApars; aparNo++ ) {
            aparInfo nextApar = efixDriver.getAparInfo(aparNo);
            emitAparInfo(nextApar);
        }

        int numComps = efixDriver.getComponentUpdateCount();
        for (int compNo = 0; compNo < numComps; compNo++ ) {
            componentUpdate nextComponent = efixDriver.getComponentUpdate(compNo);
            emitComponentUpdate(nextComponent);
        }

        int numPrereqs = efixDriver.getPlatformPrereqCount();
        for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
            platformPrereq nextPrereq = efixDriver.getPlatformPrereq(prereqNo);
            emitPlatformPrereq(nextPrereq);
        }

        numPrereqs = efixDriver.getProductPrereqCount();
        for ( int prereqNo = 0;  prereqNo < numPrereqs; prereqNo++ ) {
            productPrereq nextPrereq = efixDriver.getProductPrereq(prereqNo);
            emitProductPrereq(nextPrereq);
        }

        numPrereqs = efixDriver.getProductCoreqCount();
        for ( int prereqNo = 0;  prereqNo < numPrereqs; prereqNo++ ) {
            productCoreq nextCoreq = efixDriver.getProductCoreq(prereqNo);
            emitProductCoreq(nextCoreq);
        }

        numPrereqs = efixDriver.getEFixPrereqCount();
        for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
            efixPrereq nextPrereq = efixDriver.getEFixPrereq(prereqNo);
            emitEFixPrereq(nextPrereq);
        }

        int numConfigs    = efixDriver.getConfigTaskCount();
        for ( int configNo = 0; configNo < numConfigs; configNo++ ) {
            configTask nextConfig = efixDriver.getConfigTask( configNo );
            emitConfigTask(nextConfig);
        }

        int numProperties = efixDriver.getCustomPropertyCount();
        for ( int propertyNo = 0; propertyNo < numProperties; propertyNo++ ) {
            customProperty nextProperty = efixDriver.getCustomProperty(propertyNo);
            emitCustomProperty(nextProperty);
        }

        indentOut();

        printIndent();
        emitElementClosure(AppliedHandler.EFIX_DRIVER_ELEMENT_NAME);
        println();
    }

    public void emitEFixApplied(efixApplied efixApplied)
    {
        beginDocument();

        printIndent();
        beginElementOpening(AppliedHandler.EFIX_APPLIED_ELEMENT_NAME);
        println();

        indentIn();

        printIndent();
        emitAttribute(AppliedHandler.EFIX_ID_FIELD_TAG, efixApplied.getEFixId());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numApplieds = efixApplied.getComponentAppliedCount();
        for (int appliedNo = 0; appliedNo < numApplieds; appliedNo++ ) {
            componentApplied nextApplied = efixApplied.getComponentApplied(appliedNo);
            emitComponentApplied(nextApplied);
        }

        numApplieds = efixApplied.getConfigAppliedCount();
        for (int appliedNo = 0; appliedNo < numApplieds; appliedNo++ ) {
            configApplied nextApplied = efixApplied.getConfigApplied(appliedNo);
            emitConfigApplied(nextApplied);
        }

        indentOut();

        printIndent();
        emitElementClosure(AppliedHandler.EFIX_APPLIED_ELEMENT_NAME);
        println();
    }

    public void emitPTFDriver(ptfDriver ptfDriver)
    {
        beginDocument();

        printIndent();
        beginElementOpening(AppliedHandler.PTF_DRIVER_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.ID_FIELD_TAG, ptfDriver.getId());
        emitAttributeOnLine(AppliedHandler.SHORT_DESCRIPTION_FIELD_TAG, ptfDriver.getShortDescription());
        emitAttributeOnLine(AppliedHandler.LONG_DESCRIPTION_FIELD_TAG, ptfDriver.getLongDescription());
        emitAttributeOnLine(AppliedHandler.BUILD_VERSION_FIELD_TAG, ptfDriver.getBuildVersion());

        printIndent();
        emitAttribute(AppliedHandler.BUILD_DATE_FIELD_TAG, ptfDriver.getBuildDate());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numComps = ptfDriver.getComponentUpdateCount();
        for (int compNo = 0; compNo < numComps; compNo++ ) {
            componentUpdate nextComponent = ptfDriver.getComponentUpdate(compNo);
            emitComponentUpdate(nextComponent);
        }

        int numUpdates = ptfDriver.getProductUpdateCount();
        for (int updateNo = 0; updateNo < numUpdates; updateNo++ ) {
            productUpdate nextUpdate = ptfDriver.getProductUpdate(updateNo);
            emitProductUpdate(nextUpdate);
        }

        int numIncluded = ptfDriver.getIncludedEFixCount();
        for (int efixNo = 0; efixNo < numIncluded; efixNo++ ) {
            includedEFix nextEFix = ptfDriver.getIncludedEFix(efixNo);
            emitIncludedEFix(nextEFix);
        }

        int numPrereqs = ptfDriver.getPlatformPrereqCount();
        for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
            platformPrereq nextPrereq = ptfDriver.getPlatformPrereq(prereqNo);
            emitPlatformPrereq(nextPrereq);
        }

        numPrereqs = ptfDriver.getProductPrereqCount();
        for ( int prereqNo = 0;  prereqNo < numPrereqs; prereqNo++ ) {
            productPrereq nextPrereq = ptfDriver.getProductPrereq(prereqNo);
            emitProductPrereq(nextPrereq);
        }

        numPrereqs = ptfDriver.getProductCoreqCount();
        for ( int prereqNo = 0;  prereqNo < numPrereqs; prereqNo++ ) {
            productCoreq nextCoreq = ptfDriver.getProductCoreq(prereqNo);
            emitProductCoreq(nextCoreq);
        }

        int numConfigs    = ptfDriver.getConfigTaskCount();
        for ( int configNo = 0; configNo < numConfigs; configNo++ ) {
            configTask nextConfig = ptfDriver.getConfigTask( configNo );
            emitConfigTask(nextConfig);
        }


        int numProperties = ptfDriver.getCustomPropertyCount();
        for ( int propertyNo = 0; propertyNo < numProperties; propertyNo++ ) {
            customProperty nextProperty = ptfDriver.getCustomProperty(propertyNo);
            emitCustomProperty(nextProperty);
        }

        indentOut();

        printIndent();
        emitElementClosure(AppliedHandler.PTF_DRIVER_ELEMENT_NAME);
        println();
    }

    public void emitPTFApplied(ptfApplied ptfApplied)
    {
        beginDocument();

        printIndent();
        beginElementOpening(AppliedHandler.PTF_APPLIED_ELEMENT_NAME);
        println();

        indentIn();

        printIndent();
        emitAttribute(AppliedHandler.PTF_ID_FIELD_TAG, ptfApplied.getPTFId());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numApplieds = ptfApplied.getComponentAppliedCount();
        for (int appliedNo = 0; appliedNo < numApplieds; appliedNo++ ) {
            componentApplied nextApplied = ptfApplied.getComponentApplied(appliedNo);
            emitComponentApplied(nextApplied);
        }

        numApplieds = ptfApplied.getConfigAppliedCount();
        for (int appliedNo = 0; appliedNo < numApplieds; appliedNo++ ) {
            configApplied nextApplied = ptfApplied.getConfigApplied(appliedNo);
            emitConfigApplied(nextApplied);
        }

        indentOut();

        printIndent();
        emitElementClosure(AppliedHandler.PTF_APPLIED_ELEMENT_NAME);
        println();
    }

    public void emitAparInfo(aparInfo apar)
    {
        printIndent();
        beginElementOpening(AppliedHandler.APAR_INFO_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.APAR_NUMBER_FIELD_TAG, apar.getNumber());
        emitAttributeOnLine(AppliedHandler.APAR_DATE_FIELD_TAG, apar.getDate());
        emitAttributeOnLine(AppliedHandler.SHORT_DESCRIPTION_FIELD_TAG, apar.getShortDescription());

        printIndent();
        emitAttribute(AppliedHandler.LONG_DESCRIPTION_FIELD_TAG, apar.getLongDescription());

        int numProperties = apar.getCustomPropertyCount();

        if ( numProperties == 0 ) {
            endElementOpening(CLOSE_WHOLLY);
            println();

        } else {
            endElementOpening(CLOSE_PARTIALLY);
            println();

            indentIn();

            for ( int propertyNo = 0; propertyNo < numProperties; propertyNo++ ) {
                customProperty nextProperty = apar.getCustomProperty(propertyNo);
                emitCustomProperty(nextProperty);
            }

            indentOut();

            printIndent();
            emitElementClosure(AppliedHandler.COMPONENT_UPDATE_ELEMENT_NAME);
            println();
        }
    }

    public void emitComponentUpdate(componentUpdate componentUpdate)
    {
        printIndent();
        beginElementOpening(AppliedHandler.COMPONENT_UPDATE_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.COMPONENT_NAME_FIELD_TAG, componentUpdate.getComponentName());
        emitAttributeOnLine(AppliedHandler.UPDATE_TYPE_FIELD_TAG, componentUpdate.getUpdateType());
        emitAttributeOnLine(AppliedHandler.IS_REQUIRED_FIELD_TAG, componentUpdate.getIsRequired());
        emitAttributeOnLine(AppliedHandler.IS_OPTIONAL_FIELD_TAG, componentUpdate.getIsOptional());
        emitAttributeOnLine(AppliedHandler.IS_RECOMMENDED_FIELD_TAG, componentUpdate.getIsRecommended());
        emitAttributeOnLine(AppliedHandler.IS_EXTERNAL_FIELD_TAG, componentUpdate.getIsExternal());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_FILE_FIELD_TAG, componentUpdate.getRootPropertyFile());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_NAME_FIELD_TAG, componentUpdate.getRootPropertyName());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_VALUE_FIELD_TAG, componentUpdate.getRootPropertyValue());
        emitAttributeOnLine(AppliedHandler.IS_CUSTOM_FIELD_TAG, componentUpdate.getIsCustom());

        printIndent();
        emitAttribute(AppliedHandler.PRIMARY_CONTENT_FIELD_TAG, componentUpdate.getPrimaryContent());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numPrereqs = componentUpdate.getComponentPrereqCount();
        for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
            componentVersion nextPrereq = componentUpdate.getComponentPrereq(prereqNo);
            emitComponentPrereq(nextPrereq);
        }

        componentVersion finalVersion = componentUpdate.getFinalVersion();
        if ( finalVersion != null )
            emitFinalVersion(finalVersion);

        int numProperties = componentUpdate.getCustomPropertyCount();
        for ( int propertyNo = 0; propertyNo < numProperties; propertyNo++ ) {
            customProperty nextProperty = componentUpdate.getCustomProperty(propertyNo);
            emitCustomProperty(nextProperty);
        }

        indentOut();

        printIndent();
        emitElementClosure(AppliedHandler.COMPONENT_UPDATE_ELEMENT_NAME);
        println();
    }

    public void emitPlatformPrereq(platformPrereq platformPrereq)
    {
        printIndent();
        beginElementOpening(AppliedHandler.PLATFORM_PREREQ_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.ARCHITECTURE_FIELD_TAG, platformPrereq.getArchitecture());
        emitAttributeOnLine(AppliedHandler.OS_PLATFORM_FIELD_TAG, platformPrereq.getOSPlatform());

        printIndent();
        emitAttribute(AppliedHandler.OS_VERSION_FIELD_TAG, platformPrereq.getOSVersion());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitProductPrereq(productPrereq productPrereq)
    {
        printIndent();
        beginElementOpening(AppliedHandler.PRODUCT_PREREQ_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.PRODUCT_ID_FIELD_TAG, productPrereq.getProductId());
        emitAttributeOnLine(AppliedHandler.BUILD_VERSION_FIELD_TAG, productPrereq.getBuildVersion());
        emitAttributeOnLine(AppliedHandler.BUILD_DATE_FIELD_TAG, productPrereq.getBuildDate());

        printIndent();
        emitAttribute(AppliedHandler.BUILD_LEVEL_FIELD_TAG, productPrereq.getBuildLevel());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitProductCoreq(productCoreq productCoreq)
    {
        printIndent();
        beginElementOpening(AppliedHandler.PRODUCT_COREQ_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.PRODUCT_ID_FIELD_TAG, productCoreq.getProductId());
        emitAttributeOnLine(AppliedHandler.BUILD_VERSION_FIELD_TAG, productCoreq.getBuildVersion());
        emitAttributeOnLine(AppliedHandler.BUILD_DATE_FIELD_TAG, productCoreq.getBuildDate());

        printIndent();
        emitAttribute(AppliedHandler.BUILD_LEVEL_FIELD_TAG, productCoreq.getBuildLevel());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitEFixPrereq(efixPrereq efixPrereq)
    {
        printIndent();
        beginElementOpening(AppliedHandler.EFIX_PREREQ_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.EFIX_ID_FIELD_TAG, efixPrereq.getEFixId());
        emitAttributeOnLine(AppliedHandler.IS_NEGATIVE_FIELD_TAG, efixPrereq.getIsNegative());

        printIndent();
        emitAttribute(AppliedHandler.INSTALL_INDEX_FIELD_TAG, efixPrereq.getInstallIndex());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitConfigTask(configTask configtask)
    {
        printIndent();
        beginElementOpening(AppliedHandler.CONFIG_TASK_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.CONFIG_NAME_FIELD_TAG, configtask.getConfigurationTaskName() );
        emitAttributeOnLine(AppliedHandler.CONFIG_REQUIRED_FIELD_TAG, configtask.isConfigurationRequired() );

        emitAttributeOnLine(AppliedHandler.UNCONFIG_NAME_FIELD_TAG, configtask.getUnconfigurationTaskName() );

        printIndent();
        emitAttribute(AppliedHandler.UNCONFIG_REQUIRED_FIELD_TAG, configtask.isUnconfigurationRequired() );
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitCustomProperty(customProperty customProperty)
    {
        printIndent();
        beginElementOpening(AppliedHandler.CUSTOM_PROPERTY_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.PROPERTY_NAME_FIELD_TAG, customProperty.getPropertyName());
        emitAttributeOnLine(AppliedHandler.PROPERTY_TYPE_FIELD_TAG, customProperty.getPropertyType());

        printIndent();
        emitAttribute(AppliedHandler.PROPERTY_VALUE_FIELD_TAG, customProperty.getPropertyValue());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitProductUpdate(productUpdate productUpdate)
    {
        printIndent();
        beginElementOpening(AppliedHandler.PRODUCT_UPDATE_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.PRODUCT_ID_FIELD_TAG, productUpdate.getProductId());
        emitAttributeOnLine(AppliedHandler.PRODUCT_NAME_FIELD_TAG, productUpdate.getProductName());
        emitAttributeOnLine(AppliedHandler.BUILD_VERSION_FIELD_TAG, productUpdate.getBuildVersion());
        emitAttributeOnLine(AppliedHandler.BUILD_DATE_FIELD_TAG, productUpdate.getBuildDate());

        printIndent();
        emitAttribute(AppliedHandler.BUILD_LEVEL_FIELD_TAG, productUpdate.getBuildLevel());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitIncludedEFix(includedEFix includedEFix)
    {
        printIndent();
        beginElementOpening(AppliedHandler.INCLUDED_EFIX_ELEMENT_NAME);
        emitAttribute(AppliedHandler.EFIX_ID_FIELD_TAG, includedEFix.getEFixId());
        endElementOpening(CLOSE_WHOLLY);
        println();
    }

    public void emitComponentPrereq(componentVersion componentPrereq)
    {
        emitComponentVersion(AppliedHandler.COMPONENT_PREREQ_ELEMENT_NAME, componentPrereq);
    }

    public void emitInitialVersion(componentVersion initialVersion)
    {
        emitComponentVersion(AppliedHandler.INITIAL_VERSION_ELEMENT_NAME, initialVersion);
    }

    public void emitFinalVersion(componentVersion finalVersion)
    {
        emitComponentVersion(AppliedHandler.FINAL_VERSION_ELEMENT_NAME, finalVersion);
    }

    public void emitComponentVersion(String elementName,
                                     componentVersion componentVersion)
    {
        printIndent();
        beginElementOpening(elementName);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.COMPONENT_NAME_FIELD_TAG, componentVersion.getComponentName());
        emitAttributeOnLine(AppliedHandler.SPEC_VERSION_FIELD_TAG, componentVersion.getSpecVersion());
        emitAttributeOnLine(AppliedHandler.BUILD_VERSION_FIELD_TAG, componentVersion.getBuildVersion());

        printIndent();
        emitAttribute(AppliedHandler.BUILD_DATE_FIELD_TAG, componentVersion.getBuildDate());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void emitComponentApplied(componentApplied componentApplied)
    {
        printIndent();
        beginElementOpening(AppliedHandler.COMPONENT_APPLIED_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(AppliedHandler.COMPONENT_NAME_FIELD_TAG, componentApplied.getComponentName());
        emitAttributeOnLine(AppliedHandler.SELECTIVE_UPDATE_FIELD_TAG, componentApplied.getSelectiveUpdate());
        emitAttributeOnLine(AppliedHandler.UPDATE_TYPE_FIELD_TAG, componentApplied.getUpdateType());
        emitAttributeOnLine(AppliedHandler.IS_REQUIRED_FIELD_TAG, componentApplied.getIsRequired());
        emitAttributeOnLine(AppliedHandler.IS_OPTIONAL_FIELD_TAG, componentApplied.getIsOptional());
        emitAttributeOnLine(AppliedHandler.IS_EXTERNAL_FIELD_TAG, componentApplied.getIsExternal());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_FILE_FIELD_TAG, componentApplied.getRootPropertyFile());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_NAME_FIELD_TAG, componentApplied.getRootPropertyName());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_VALUE_FIELD_TAG, componentApplied.getRootPropertyValue());
        emitAttributeOnLine(AppliedHandler.IS_CUSTOM_FIELD_TAG, componentApplied.getIsCustom());
        emitAttributeOnLine(AppliedHandler.LOG_NAME_FIELD_TAG, componentApplied.getLogName());
        emitAttributeOnLine(AppliedHandler.BACKUP_NAME_FIELD_TAG, componentApplied.getBackupName());

        printIndent();
        emitAttribute(AppliedHandler.TIME_STAMP_FIELD_TAG, componentApplied.getTimeStamp());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        componentVersion initialVersion = componentApplied.getInitialVersion();

        if ( initialVersion != null )
            emitInitialVersion(initialVersion);

        componentVersion finalVersion = componentApplied.getFinalVersion();

        if ( finalVersion != null )
            emitFinalVersion(finalVersion);

        indentOut();

        printIndent();
        emitElementClosure(AppliedHandler.COMPONENT_APPLIED_ELEMENT_NAME);
        println();
    }

    public void emitConfigApplied(configApplied configapplied)
    {
        printIndent();
        beginElementOpening(AppliedHandler.CONFIG_APPLIED_ELEMENT_NAME);
        println();

        indentIn();
/*
        emitAttributeOnLine(AppliedHandler.COMPONENT_NAME_FIELD_TAG, componentApplied.getComponentName());
        emitAttributeOnLine(AppliedHandler.UPDATE_TYPE_FIELD_TAG, componentApplied.getUpdateType());
        emitAttributeOnLine(AppliedHandler.IS_REQUIRED_FIELD_TAG, componentApplied.getIsRequired());
        emitAttributeOnLine(AppliedHandler.IS_OPTIONAL_FIELD_TAG, componentApplied.getIsOptional());
        emitAttributeOnLine(AppliedHandler.IS_EXTERNAL_FIELD_TAG, componentApplied.getIsExternal());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_FILE_FIELD_TAG, componentApplied.getRootPropertyFile());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_NAME_FIELD_TAG, componentApplied.getRootPropertyName());
        emitAttributeOnLine(AppliedHandler.ROOT_PROPERTY_VALUE_FIELD_TAG, componentApplied.getRootPropertyValue());
        emitAttributeOnLine(AppliedHandler.IS_CUSTOM_FIELD_TAG, componentApplied.getIsCustom());
        emitAttributeOnLine(AppliedHandler.LOG_NAME_FIELD_TAG, componentApplied.getLogName());
        emitAttributeOnLine(AppliedHandler.BACKUP_NAME_FIELD_TAG, componentApplied.getBackupName());

        printIndent();
        emitAttribute(AppliedHandler.TIME_STAMP_FIELD_TAG, componentApplied.getTimeStamp());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        componentVersion initialVersion = componentApplied.getInitialVersion();

        if ( initialVersion != null )
            emitInitialVersion(initialVersion);

        componentVersion finalVersion = componentApplied.getFinalVersion();

        if ( finalVersion != null )
            emitFinalVersion(finalVersion);

*/
        emitAttributeOnLine(AppliedHandler.CONFIG_NAME_FIELD_TAG, configapplied.getConfigName() );
        emitAttributeOnLine(AppliedHandler.CONFIG_CONFIGURED_FIELD_TAG, configapplied.isConfigured() );

        printIndent();
        emitAttribute(AppliedHandler.CONFIG_CONFIG_ACTIVE_FIELD_TAG, configapplied.isConfigurationActive() );
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void baseEmit(List rootElements)
    {
        Object root = rootElements.iterator().next();

        if ( root instanceof efixDriver )
            emitEFixDriver((efixDriver) root);
        else if ( root instanceof efixApplied )
            emitEFixApplied((efixApplied) root);
        else if ( root instanceof ptfDriver )
            emitPTFDriver((ptfDriver) root);
        else if ( root instanceof ptfApplied )
            emitPTFApplied((ptfApplied) root);
    }
}

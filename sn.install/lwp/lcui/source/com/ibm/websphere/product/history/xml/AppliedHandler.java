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
 * Applied Handler
 *
 * History 1.2, 9/26/03
 *
 * 12-Sep-2002 Initial Version
 */

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class AppliedHandler extends BaseHandler
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // File links ...

    public static final String EFIX_DRIVER_FILE_EXTENSION = ".efixDriver" ;
    // File links ...

    public static final String EFIX_APPLIED_FILE_EXTENSION = ".efixApplied" ;
    // File links ...

    public static final String PTF_DRIVER_FILE_EXTENSION = ".ptfDriver" ;
    // File links ...

    public static final String PTF_APPLIED_FILE_EXTENSION = ".ptfApplied" ;

    public static final String
        APPLIED_PACKAGE             = "com.ibm.websphere.product.history.xml";

    // File links ...

    public static boolean acceptsEFixDriverFileName(String filename)
    {
        return ( filename.endsWith(EFIX_DRIVER_FILE_EXTENSION) );
    }

    public static String getStandardEFixDriverFileName(String efixId)
    {
        return efixId + EFIX_DRIVER_FILE_EXTENSION;
    }

    public static boolean acceptsEFixAppliedFileName(String filename)
    {
        return ( filename.endsWith(EFIX_APPLIED_FILE_EXTENSION) );
    }

    public static String getStandardEFixAppliedFileName(String efixId)
    {
        return efixId + EFIX_APPLIED_FILE_EXTENSION;
    }

    public static boolean acceptsPTFDriverFileName(String filename)
    {
        return ( filename.endsWith(PTF_DRIVER_FILE_EXTENSION) );
    }

    public static String getStandardPTFDriverFileName(String ptfId)
    {
        return ptfId + PTF_DRIVER_FILE_EXTENSION;
    }

    public static boolean acceptsPTFAppliedFileName(String filename)
    {
        return ( filename.endsWith(PTF_APPLIED_FILE_EXTENSION) );
    }

    public static String getStandardPTFAppliedFileName(String ptfId)
    {
        return ptfId + PTF_APPLIED_FILE_EXTENSION;
    }

    // Instantor ...

    public AppliedHandler()
    {
        super();
    }

    // Class and element links ...

    public static final String EFIX_DRIVER_CLASS_NAME = "efixDriver" ;
    // Class and element links ...

    public static final String EFIX_DRIVER_ELEMENT_NAME = "efix-driver" ;
    // Class and element links ...

    public static final String APAR_INFO_CLASS_NAME = "aparInfo" ;
    // Class and element links ...

    public static final String APAR_INFO_ELEMENT_NAME = "apar-info" ;
    // Class and element links ...

    public static final String COMPONENT_UPDATE_CLASS_NAME = "componentUpdate" ;
    // Class and element links ...

    public static final String COMPONENT_UPDATE_ELEMENT_NAME = "component-update" ;
    // Class and element links ...

    public static final String EFIX_PREREQ_CLASS_NAME = "efixPrereq" ;
    // Class and element links ...

    public static final String EFIX_PREREQ_ELEMENT_NAME = "efix-prereq" ;
    // Class and element links ...

    public static final String PLATFORM_PREREQ_CLASS_NAME = "platformPrereq" ;
    // Class and element links ...

    public static final String PLATFORM_PREREQ_ELEMENT_NAME = "platform-prereq" ;
    // Class and element links ...

    public static final String PRODUCT_PREREQ_CLASS_NAME = "productPrereq" ;
    // Class and element links ...

    public static final String PRODUCT_PREREQ_ELEMENT_NAME = "product-prereq" ;
    // Class and element links ...

    public static final String PRODUCT_COREQ_CLASS_NAME = "productCoreq" ;
    // Class and element links ...

    public static final String PRODUCT_COREQ_ELEMENT_NAME = "product-coreq" ;
    // Class and element links ...

    public static final String COMPONENT_PREREQ_CLASS_NAME = "componentVersion" ;
    // Class and element links ...

    public static final String COMPONENT_PREREQ_ELEMENT_NAME = "component-prereq" ;
    // Class and element links ...

    public static final String CUSTOM_PROPERTY_CLASS_NAME = "customProperty" ;
    // Class and element links ...

    public static final String CUSTOM_PROPERTY_ELEMENT_NAME = "custom-property" ;
    // Class and element links ...

    public static final String FINAL_VERSION_CLASS_NAME = "componentVersion" ;
    // Class and element links ...

    public static final String FINAL_VERSION_ELEMENT_NAME = "final-version" ;
    // Class and element links ...

    public static final String CONFIG_TASK_CLASS_NAME = "configTask" ;
    // Class and element links ...

    public static final String CONFIG_TASK_ELEMENT_NAME = "config-task" ;
    // Class and element links ...

    public static final String EFIX_APPLIED_CLASS_NAME = "efixApplied" ;
    // Class and element links ...

    public static final String EFIX_APPLIED_ELEMENT_NAME = "efix-applied" ;
    // Class and element links ...

    public static final String COMPONENT_APPLIED_CLASS_NAME = "componentApplied" ;
    // Class and element links ...

    public static final String COMPONENT_APPLIED_ELEMENT_NAME = "component-applied" ;
    // Class and element links ...

    public static final String CONFIG_APPLIED_CLASS_NAME = "configApplied" ;
    // Class and element links ...

    public static final String CONFIG_APPLIED_ELEMENT_NAME = "config-applied" ;
    // Class and element links ...

    public static final String INITIAL_VERSION_CLASS_NAME = "componentVersion" ;
    // Class and element links ...

    public static final String INITIAL_VERSION_ELEMENT_NAME = "initial-version" ;
    // FINAL_VERSION_CLASS_NAME       = "componentVersion",
 // FINAL_VERSION_ELEMENT_NAME     = "final-version",
 // Class and element links ...

    public static final String PTF_DRIVER_CLASS_NAME = "ptfDriver" ;
    // Class and element links ...

    public static final String PTF_DRIVER_ELEMENT_NAME = "ptf-driver" ;
    // COMPONENT_UPDATE_CLASS_NAME    = "componentUpdate",
 // COMPONENT_UPDATE_ELEMENT_NAME  = "component-update"
 // Class and element links ...

    public static final String PRODUCT_UPDATE_CLASS_NAME = "productUpdate" ;
    // Class and element links ...

    public static final String PRODUCT_UPDATE_ELEMENT_NAME = "product-update" ;
    // PLATFORM_PREREQ_CLASS_NAME     = "platformPrereq",
 // PLATFORM_PREREQ_ELEMENT_NAME   = "platform-prereq",
 // PRODUCT_PREREQ_CLASS_NAME      = "productPrereq",
 // PRODUCT_PREREQ_ELEMENT_NAME    = "product-prereq",
 // Class and element links ...

    public static final String INCLUDED_EFIX_CLASS_NAME = "includedEFix" ;
    // Class and element links ...

    public static final String INCLUDED_EFIX_ELEMENT_NAME = "included-efix" ;
    // CUSTOM_PROPERTY_CLASS_NAME     = "customProperty",
 // CUSTOM_PROPERTY_ELEMENT_NAME   = "custom-property",
 // FINAL_VERSION_CLASS_NAME       = "componentVersion",
 // FINAL_VERSION_ELEMENT_NAME     = "final-version",
 // COMPONENT_PREREQ_CLASS_NAME    = "componentVersion",
 // COMPONENT_PREREQ_ELEMENT_NAME  = "component-prereq",
 // Class and element links ...

    public static final String PTF_APPLIED_CLASS_NAME = "ptfApplied" ;
    // Class and element links ...

    public static final String PTF_APPLIED_ELEMENT_NAME = "ptf-applied" ;

            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String ID_FIELD_TAG = "id" ;
            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String SHORT_DESCRIPTION_FIELD_TAG = "short-description" ;
            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String LONG_DESCRIPTION_FIELD_TAG = "long-description" ;
            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String IS_TRIAL_FIELD_TAG = "is-trial" ;
            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String EXPIRATION_DATE_FIELD_TAG = "expiration-date" ;
            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String BUILD_VERSION_FIELD_TAG = "build-version" ;
            // COMPONENT_APPLIED_CLASS_NAME   = "componentApplied",
            // COMPONENT_APPLIED_ELEMENT_NAME = "component-applied",

            // INITIAL_VERSION_CLASS_NAME     = "componentVersion",
            // INITIAL_VERSION_ELEMENT_NAME   = "initial-version",

            // FINAL_VERSION_CLASS_NAME       = "componentVersion",
            // FINAL_VERSION_ELEMENT_NAME     = "final-version";

    // efix tags ...

    public static final String BUILD_DATE_FIELD_TAG = "build-date" ;

    // id                (String)  [required] The id of the update.
    // short-description (String)  [required] A short description of the update.
    // long-description  (String)  [optional] An optional longer description of the update.
    // is-trial          (boolean) [required] If true, this is a trial fix.
    // expiration-date   (Date)    [optional] An optional expiration date for this fix.
    // build-version     (String)  [required] The build version of the update.
    // build-date        (Date)    [required] The build date of the update.

    public static final String APAR_NUMBER_FIELD_TAG = "number" ;
    // id                (String)  [required] The id of the update.
    // short-description (String)  [required] A short description of the update.
    // long-description  (String)  [optional] An optional longer description of the update.
    // is-trial          (boolean) [required] If true, this is a trial fix.
    // expiration-date   (Date)    [optional] An optional expiration date for this fix.
    // build-version     (String)  [required] The build version of the update.
    // build-date        (Date)    [required] The build date of the update.

    public static final String APAR_DATE_FIELD_TAG = "date" ;
        // SHORT_DESCRIPTION_FIELD_TAG = "short-description",
        // LONG_DESCRIPTION_FIELD_TAG  = "long-description";

    // number            (String)  [required] The APAR number of this update.
    // date              (Date)    [required] The date of the APAR.
    // short-description (String)  [required] A short description of the APAR.
    // long-description  (String)  [optional] An optional longer description of the APAR.

    // platformPrereq tags ...

    public static final String ARCHITECTURE_FIELD_TAG = "architecture" ;
        // SHORT_DESCRIPTION_FIELD_TAG = "short-description",
        // LONG_DESCRIPTION_FIELD_TAG  = "long-description";

    // number            (String)  [required] The APAR number of this update.
    // date              (Date)    [required] The date of the APAR.
    // short-description (String)  [required] A short description of the APAR.
    // long-description  (String)  [optional] An optional longer description of the APAR.

    // platformPrereq tags ...

    public static final String OS_PLATFORM_FIELD_TAG = "os-platform" ;
        // SHORT_DESCRIPTION_FIELD_TAG = "short-description",
        // LONG_DESCRIPTION_FIELD_TAG  = "long-description";

    // number            (String)  [required] The APAR number of this update.
    // date              (Date)    [required] The date of the APAR.
    // short-description (String)  [required] A short description of the APAR.
    // long-description  (String)  [optional] An optional longer description of the APAR.

    // platformPrereq tags ...

    public static final String OS_VERSION_FIELD_TAG = "os-version" ;

    // architecture (String) [required] (The name of the prerequisite architecture.)
    // os-platform  (String) [required] (The name of the operating system.)
    // os-version   (String) [required] (The version of the operating system.)

    // productPrereq tags ...

    public static final String PRODUCT_ID_FIELD_TAG = "product-id" ;
    // BUILD_VERSION_FIELD_TAG = "build-version",
 // BUILD_DATE_FIELD_TAG    = "build-date",
 // architecture (String) [required] (The name of the prerequisite architecture.)
    // os-platform  (String) [required] (The name of the operating system.)
    // os-version   (String) [required] (The version of the operating system.)

    // productPrereq tags ...

    public static final String BUILD_LEVEL_FIELD_TAG = "build-level" ;
    
    // product-id    (String) [required] (The id of the prerequisite product.)
    // build-version (String) [optional] (The prerequisite product build version.)
    // build-date    (date)   [optional] (The prerequisite product build date.)
    // build-level   (string) [optional] (The prerequisite product build level.)

    // efixPrereq tags

    // Field tags ...

    public static final String EFIX_ID_FIELD_TAG = "efix-id" ;
    // product-id    (String) [required] (The id of the prerequisite product.)
    // build-version (String) [optional] (The prerequisite product build version.)
    // build-date    (date)   [optional] (The prerequisite product build date.)
    // build-level   (string) [optional] (The prerequisite product build level.)

    // efixPrereq tags

    // Field tags ...

    public static final String IS_NEGATIVE_FIELD_TAG = "is-negative" ;
    // product-id    (String) [required] (The id of the prerequisite product.)
    // build-version (String) [optional] (The prerequisite product build version.)
    // build-date    (date)   [optional] (The prerequisite product build date.)
    // build-level   (string) [optional] (The prerequisite product build level.)

    // efixPrereq tags

    // Field tags ...

    public static final String INSTALL_INDEX_FIELD_TAG = "install-index" ;

    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String COMPONENT_NAME_FIELD_TAG = "component-name" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...
    public static final String SELECTIVE_UPDATE_FIELD_TAG = "selective-update";
    
    public static final String UPDATE_TYPE_FIELD_TAG = "update-type" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String IS_REQUIRED_FIELD_TAG = "is-required" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String IS_OPTIONAL_FIELD_TAG = "is-optional" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String IS_RECOMMENDED_FIELD_TAG = "is-recommended" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String IS_EXTERNAL_FIELD_TAG = "is-external" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String ROOT_PROPERTY_FILE_FIELD_TAG = "root-property-file" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String ROOT_PROPERTY_NAME_FIELD_TAG = "root-property-name" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String ROOT_PROPERTY_VALUE_FIELD_TAG = "root-property-value" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String IS_CUSTOM_FIELD_TAG = "is-custom" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String PRIMARY_CONTENT_FIELD_TAG = "primary-content" ;
    // efix-id       (String)  [required] The ID of a prerequisite which is required
    //                                    before installing this efix.
    // is-negative   (boolean) [required] Changes the meaning of this prerequisite:
    //                                    the id is of an efix which, if installed,
    //                                    blocks the installation of this efix.
    // install-index (String)  [optional] An ordinal value which is used to
    //                                    order corequisite efixes.

    // componentUpdate tags ...

    // Field tags ...

    public static final String FINAL_VERSION_FIELD_TAG = "final-version" ;

    // "component-name"      (String)  [required] The name of a component to update.
    // "update-type"         (enum)    [required] The type of the update.
    // "is-required"         (boolean) [required] Specifies that the component must be
    //                                            available if the update is to be installed.
    // "is-optional"         (boolean) [required] If the component update is optional, even
    //                                            if the component is installed.
    // "is-recommended"      (boolean) [required] If the component update is recommended, if
    //                                            optional and the component is installed.
    // "is-external"         (boolean) [required] Specifies that the component is external
    //                                            to the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the
    //                                            external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the
    //                                            external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"           (boolean) [required] Specifies that a custom executable has
    //                                            been supplied (instead of an update jar).
    // "primary-content"     (String)  [required] The name of the content file, either the
    //                                            name of an update jar file, or the name
    //                                            of a custom executable.
    // "final-version"       (componentVersion)
    //                                 [optional] The new component version for an add or
    //                                            replace type update.

    // Custom property tags ...

    public static final String PROPERTY_NAME_FIELD_TAG = "property-name" ;
    // "component-name"      (String)  [required] The name of a component to update.
    // "update-type"         (enum)    [required] The type of the update.
    // "is-required"         (boolean) [required] Specifies that the component must be
    //                                            available if the update is to be installed.
    // "is-optional"         (boolean) [required] If the component update is optional, even
    //                                            if the component is installed.
    // "is-recommended"      (boolean) [required] If the component update is recommended, if
    //                                            optional and the component is installed.
    // "is-external"         (boolean) [required] Specifies that the component is external
    //                                            to the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the
    //                                            external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the
    //                                            external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"           (boolean) [required] Specifies that a custom executable has
    //                                            been supplied (instead of an update jar).
    // "primary-content"     (String)  [required] The name of the content file, either the
    //                                            name of an update jar file, or the name
    //                                            of a custom executable.
    // "final-version"       (componentVersion)
    //                                 [optional] The new component version for an add or
    //                                            replace type update.

    // Custom property tags ...

    public static final String PROPERTY_TYPE_FIELD_TAG = "property-type" ;
    // "component-name"      (String)  [required] The name of a component to update.
    // "update-type"         (enum)    [required] The type of the update.
    // "is-required"         (boolean) [required] Specifies that the component must be
    //                                            available if the update is to be installed.
    // "is-optional"         (boolean) [required] If the component update is optional, even
    //                                            if the component is installed.
    // "is-recommended"      (boolean) [required] If the component update is recommended, if
    //                                            optional and the component is installed.
    // "is-external"         (boolean) [required] Specifies that the component is external
    //                                            to the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the
    //                                            external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the
    //                                            external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"           (boolean) [required] Specifies that a custom executable has
    //                                            been supplied (instead of an update jar).
    // "primary-content"     (String)  [required] The name of the content file, either the
    //                                            name of an update jar file, or the name
    //                                            of a custom executable.
    // "final-version"       (componentVersion)
    //                                 [optional] The new component version for an add or
    //                                            replace type update.

    // Custom property tags ...

    public static final String PROPERTY_VALUE_FIELD_TAG = "property-value" ;

    // property-name   (string) [required] (The properties name.)
    // property-type   (string) [optional] (The properties type.)
    // property-value  (string) [optional] (The properties value.)

    // ptf tags ...

    // All Duplicates of efix tags

    //    public static final String
        //        ID_FIELD_TAG                = "id",
        //        SHORT_DESCRIPTION_FIELD_TAG = "short-description",
        //        LONG_DESCRIPTION_FIELD_TAG  = "long-description",
        //        BUILD_VERSION_FIELD_TAG     = "build-version",
        //        BUILD_DATE_FIELD_TAG        = "build-date";

    // id                (String)  [required] The id of the update.
    // short-description (String)  [required] A short description of the update.
    // long-description  (String)  [optional] An optional longer description of the update.
    // build-version     (String)  [required] The build version of the update.
    // build-date        (Date)    [required] The build date of the update.

    // includedEFix tags ...

    // Duplicated from efixPrereq

    //    public static final String
    //        EFIX_ID_FIELD_TAG = "efix-id";

    // efix-id     (String) [required] (The id of an efix fixed by a PTF.)

    // productUpdate tags ...

    public static final String
        //        PRODUCT_ID_FIELD_TAG    = "product-id",
        PRODUCT_NAME_FIELD_TAG  = "product-name";
        //        BUILD_VERSION_FIELD_TAG = "build-version",
        //        BUILD_DATE_FIELD_TAG    = "build-date",
        //        BUILD_LEVEL_FIELD_TAG   = "build-level";

    // product-id    (String) [required] (The id of the replacement product file.)
    // product-name  (String) [required] (The replacement product name.)
    // build-version (String) [required] (The replacement build version.)
    // build-date    (date)   [required] (The replacement build date.)
    // build-level   (string) [required] (The replacement build level.)

    // componentVersion tags ...

    public static final String
        //        COMPONENT_NAME_FIELD_TAG = "component-name",
        SPEC_VERSION_FIELD_TAG   = "spec-version";
        //        BUILD_VERSION_FIELD_TAG  = "build-version",
        //        BUILD_DATE_FIELD_TAG     = "build-date";

    // component-name (String) [required] (The component's name.)
    // spec-version   (String) [required] (The specification version of a component.)
    // build-version  (String) [required] (The build version of a component.)
    // build-date     (date)   [required] (The build date of a component.)

    // efixApplied  tags ...

    //    public static final String
    //        EFIX_ID_FIELD_TAG      = "efix-id";

    // "efix-id"      (String)    [required] The id of the efix which was applied.

    // ptfApplied tags ...

    public static final String
        PTF_ID_FIELD_TAG      = "ptf-id";

    //      COMPONENT_NAME_FIELD_TAG      = "component-name",
 //      UPDATE_TYPE_FIELD_TAG         = "update-type",
 //      IS_REQUIRED_FIELD_TAG         = "is-required",
 //      IS_OPTIONAL_FIELD_TAG         = "is-optional",
 //      IS_EXTERNAL_FIELD_TAG         = "is-external",
 //      ROOT_PROPERTY_FILE_FIELD_TAG  = "root-property-file",
 //      ROOT_PROPERTY_NAME_FIELD_TAG  = "root-property-name",
 //      ROOT_PROPERTY_VALUE_FIELD_TAG = "root-property-value",
 //      IS_CUSTOM_FIELD_TAG           = "is-custom",
 // "ptf-id"       (String)    [required] The id of the ptf which was applied.

    // componentApplied tags ...

    public static final String LOG_NAME_FIELD_TAG = "log-name" ;
    // "ptf-id"       (String)    [required] The id of the ptf which was applied.

    // componentApplied tags ...

    public static final String BACKUP_NAME_FIELD_TAG = "backup-name" ;
    // "ptf-id"       (String)    [required] The id of the ptf which was applied.

    // componentApplied tags ...

    public static final String TIME_STAMP_FIELD_TAG = "time-stamp" ;
    // "ptf-id"       (String)    [required] The id of the ptf which was applied.

    // componentApplied tags ...

    public static final String INITIAL_VERSION_FIELD_TAG = "initial-version" ;
        //      FINAL_VERSION_FIELD_TAG       = "final-version";

    // "component-name" (String)    [required] The name of the component which was updated.
    // "update-type"    (enum)      [required] The type of the update.
    // "is-required"    (boolean)   [required] If the component update is required.
    // "is-optional"    (boolean)   [required] If the component update is optional, even
    //                                         if the component is installed.
    // "is-external"    (boolean)      [required] If the component lives outside of the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"      (boolean)   [required] If the component update is a custom update.
    // "log-name"       (URL)       [optional] The location of the log of the update.
    // "backup-name"    (URL)       [optional] The location of the backup of the update.
    // "time-stamp"     (TimeStamp) [required] The timestamp of the update.
    // "initial-version"       (componentVersion)
    //                                 [required] The initial version of the component.
    // "final-version"       (componentVersion)
    //                                 [required] The final version of the component.


    // Field tags ...
    public static final String CONFIG_NAME_FIELD_TAG = "config-name" ;
        //      FINAL_VERSION_FIELD_TAG       = "final-version";

    // "component-name" (String)    [required] The name of the component which was updated.
    // "update-type"    (enum)      [required] The type of the update.
    // "is-required"    (boolean)   [required] If the component update is required.
    // "is-optional"    (boolean)   [required] If the component update is optional, even
    //                                         if the component is installed.
    // "is-external"    (boolean)      [required] If the component lives outside of the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"      (boolean)   [required] If the component update is a custom update.
    // "log-name"       (URL)       [optional] The location of the log of the update.
    // "backup-name"    (URL)       [optional] The location of the backup of the update.
    // "time-stamp"     (TimeStamp) [required] The timestamp of the update.
    // "initial-version"       (componentVersion)
    //                                 [required] The initial version of the component.
    // "final-version"       (componentVersion)
    //                                 [required] The final version of the component.


    // Field tags ...
    public static final String CONFIG_REQUIRED_FIELD_TAG = "config-required" ;
        //      FINAL_VERSION_FIELD_TAG       = "final-version";

    // "component-name" (String)    [required] The name of the component which was updated.
    // "update-type"    (enum)      [required] The type of the update.
    // "is-required"    (boolean)   [required] If the component update is required.
    // "is-optional"    (boolean)   [required] If the component update is optional, even
    //                                         if the component is installed.
    // "is-external"    (boolean)      [required] If the component lives outside of the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"      (boolean)   [required] If the component update is a custom update.
    // "log-name"       (URL)       [optional] The location of the log of the update.
    // "backup-name"    (URL)       [optional] The location of the backup of the update.
    // "time-stamp"     (TimeStamp) [required] The timestamp of the update.
    // "initial-version"       (componentVersion)
    //                                 [required] The initial version of the component.
    // "final-version"       (componentVersion)
    //                                 [required] The final version of the component.


    // Field tags ...
    public static final String UNCONFIG_NAME_FIELD_TAG = "unconfig-name" ;
        //      FINAL_VERSION_FIELD_TAG       = "final-version";

    // "component-name" (String)    [required] The name of the component which was updated.
    // "update-type"    (enum)      [required] The type of the update.
    // "is-required"    (boolean)   [required] If the component update is required.
    // "is-optional"    (boolean)   [required] If the component update is optional, even
    //                                         if the component is installed.
    // "is-external"    (boolean)      [required] If the component lives outside of the install root.
    // "root-property-file"  (anyURL)  [optional] The properties file containing the external root.
    // "root-property-name"  (String)  [optional] The name of the property containing the external root.
    // "root-property-value" (String)  [optional] The external root value.
    // "is-custom"      (boolean)   [required] If the component update is a custom update.
    // "log-name"       (URL)       [optional] The location of the log of the update.
    // "backup-name"    (URL)       [optional] The location of the backup of the update.
    // "time-stamp"     (TimeStamp) [required] The timestamp of the update.
    // "initial-version"       (componentVersion)
    //                                 [required] The initial version of the component.
    // "final-version"       (componentVersion)
    //                                 [required] The final version of the component.


    // Field tags ...
    public static final String UNCONFIG_REQUIRED_FIELD_TAG = "unconfig-required" ;

    // "config-name"         (String)  [required] The name of a configuration taske.
    // "config-required"     (boolean)            Specified if the config task must be run as part of the eFix
    // "unconfig-name"       (String)  [required] The name of a unconfiguration task.  To be run prior to uninstalling the iFix
    // "unconfig-required"   (boolean)            Specified if the unconfig task must be run prior to uninstalling the iFix.

    // Field tags ...
    public static final String CONFIG_CONFIGURED_FIELD_TAG = "configured" ;
    // "config-name"         (String)  [required] The name of a configuration taske.
    // "config-required"     (boolean)            Specified if the config task must be run as part of the eFix
    // "unconfig-name"       (String)  [required] The name of a unconfiguration task.  To be run prior to uninstalling the iFix
    // "unconfig-required"   (boolean)            Specified if the unconfig task must be run prior to uninstalling the iFix.

    // Field tags ...
    public static final String CONFIG_CONFIG_ACTIVE_FIELD_TAG = "configuration-active" ;

    // "configured"          (boolean)            True is the configurastion task has been run
    // "configuration-active (boolean)            True if the config is currently active.  Depending on what the unconfig 
    //                                             does, this is an additional flag that could mark an unconfig, while leaving the
    //                                             configured flag to true.  This may be necessary for a partially unconfig items, 
    //                                             it wasn't possible to restor config to original state.

    // Factory operations ...

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        Object element = null; // Default case: This is an error.

        if ( parentElement == null ) {
            if ( elementName.equals(EFIX_DRIVER_ELEMENT_NAME) ) {
                efixDriver typedElement = new efixDriver();

                typedElement.setId( getAttribute(attributes, ID_FIELD_TAG, elementName, null) );
                typedElement.setShortDescription( getAttribute(attributes, SHORT_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setLongDescription( getAttribute(attributes, LONG_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setIsTrial( getAttribute(attributes, IS_TRIAL_FIELD_TAG, elementName, "false") );
                typedElement.setExpirationDate( getAttribute(attributes, EXPIRATION_DATE_FIELD_TAG, elementName, "false") );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );

                element = typedElement;

            } else if ( elementName.equals(EFIX_APPLIED_ELEMENT_NAME) ) {
                efixApplied typedElement = new efixApplied();

                typedElement.setEFixId( getAttribute(attributes, EFIX_ID_FIELD_TAG, elementName, null) );

                element = typedElement;

            } else if ( elementName.equals(PTF_DRIVER_ELEMENT_NAME) ) {
                ptfDriver typedElement = new ptfDriver();

                typedElement.setId( getAttribute(attributes, ID_FIELD_TAG, elementName, null) );
                typedElement.setShortDescription( getAttribute(attributes, SHORT_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setLongDescription( getAttribute(attributes, LONG_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );

                element = typedElement;

            } else if ( elementName.equals(PTF_APPLIED_ELEMENT_NAME) ) {
                ptfApplied typedElement = new ptfApplied();

                typedElement.setPTFId( getAttribute(attributes, PTF_ID_FIELD_TAG, elementName, null) );

                element = typedElement;
            }

        } else if ( parentElement instanceof efixDriver ) {
            efixDriver typedParentElement = (efixDriver) parentElement;

            // APAR_INFO_ELEMENT_NAME
            // COMPONENT_UPDATE_ELEMENT_NAME
            // EFIX_PREREQ_ELEMENT_NAME
            // PLATFORM_PREREQ_ELEMENT_NAME
            // PRODUCT_PREREQ_ELEMENT_NAME
            // PRODUCT_COREQ_ELMENT_NAME
            // CONFIG_TASK_ELEMENT_NAME
            // CUSTOM_PROPERTY_ELEMENT_NAME

            if ( elementName.equals(APAR_INFO_ELEMENT_NAME) ) {
                aparInfo typedElement = new aparInfo();

                typedElement.setNumber( getAttribute(attributes, APAR_NUMBER_FIELD_TAG, elementName, null) );
                typedElement.setDate( getAttribute(attributes, APAR_DATE_FIELD_TAG, elementName, null) );
                typedElement.setShortDescription( getAttribute(attributes, SHORT_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setLongDescription( getAttribute(attributes, LONG_DESCRIPTION_FIELD_TAG, elementName, "") );

                typedParentElement.addAparInfo(typedElement);

                element = typedElement;

            } else if ( elementName.equals(EFIX_PREREQ_ELEMENT_NAME) ) {
                efixPrereq typedElement = new efixPrereq();

                typedElement.setEFixId( getAttribute(attributes, EFIX_ID_FIELD_TAG, elementName, null) );
                typedElement.setIsNegative( getAttribute(attributes, IS_NEGATIVE_FIELD_TAG, elementName, "false") );
                typedElement.setInstallIndex( getAttribute(attributes, INSTALL_INDEX_FIELD_TAG, elementName, "") );

                typedParentElement.addEFixPrereq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(PLATFORM_PREREQ_ELEMENT_NAME) ) {
                platformPrereq typedElement = new platformPrereq();

                typedElement.setArchitecture( getAttribute(attributes, ARCHITECTURE_FIELD_TAG, elementName, null) );
                typedElement.setOSPlatform( getAttribute(attributes, OS_PLATFORM_FIELD_TAG, elementName, null) );
                typedElement.setOSVersion( getAttribute(attributes, OS_VERSION_FIELD_TAG, elementName, null) );

                typedParentElement.addPlatformPrereq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(PRODUCT_PREREQ_ELEMENT_NAME) ) {
                productPrereq typedElement = new productPrereq();

                typedElement.setProductId( getAttribute(attributes, PRODUCT_ID_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
                typedElement.setBuildLevel( getAttribute(attributes, BUILD_LEVEL_FIELD_TAG, elementName, null) );

                typedParentElement.addProductPrereq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(PRODUCT_COREQ_ELEMENT_NAME) ) {
                productCoreq typedElement = new productCoreq();

                typedElement.setProductId( getAttribute(attributes, PRODUCT_ID_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
                typedElement.setBuildLevel( getAttribute(attributes, BUILD_LEVEL_FIELD_TAG, elementName, null) );

                typedParentElement.addProductCoreq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(CONFIG_TASK_ELEMENT_NAME) ) {
                configTask typedElement = new configTask();

                typedElement.setConfigurationTaskName( getAttribute(attributes, CONFIG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setUnconfigurationTaskName( getAttribute(attributes, UNCONFIG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setConfigurationRequired( getAttribute(attributes, CONFIG_REQUIRED_FIELD_TAG, elementName, "false") );
                typedElement.setUnconfigurationRequired( getAttribute(attributes, UNCONFIG_REQUIRED_FIELD_TAG, elementName, "false") );

                typedParentElement.addConfgiTask(typedElement);

                element = typedElement;

            } else if ( elementName.equals(COMPONENT_UPDATE_ELEMENT_NAME) ) {
                componentUpdate typedElement = new componentUpdate();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setUpdateType( getAttribute(attributes, UPDATE_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setIsRequired( getAttribute(attributes, IS_REQUIRED_FIELD_TAG, elementName, "true") );
                typedElement.setIsOptional( getAttribute(attributes, IS_OPTIONAL_FIELD_TAG, elementName, "false") );
                typedElement.setIsRecommended( getAttribute(attributes, IS_RECOMMENDED_FIELD_TAG, elementName, "false") );
                typedElement.setIsExternal( getAttribute(attributes, IS_EXTERNAL_FIELD_TAG, elementName, "false") );
                typedElement.setRootPropertyFile( getAttribute(attributes, ROOT_PROPERTY_FILE_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyName( getAttribute(attributes, ROOT_PROPERTY_NAME_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyValue( getAttribute(attributes, ROOT_PROPERTY_VALUE_FIELD_TAG, elementName, "") );
                typedElement.setIsCustom( getAttribute(attributes, IS_CUSTOM_FIELD_TAG, elementName, "false") );
                typedElement.setPrimaryContent( getAttribute(attributes, PRIMARY_CONTENT_FIELD_TAG, elementName, null) );

                typedParentElement.addComponentUpdate(typedElement);

                element = typedElement;

            } else if ( elementName.equals(CUSTOM_PROPERTY_ELEMENT_NAME) ) {
                customProperty typedElement = new customProperty();

                typedElement.setPropertyName( getAttribute(attributes, PROPERTY_NAME_FIELD_TAG, elementName, null) );
                typedElement.setPropertyType( getAttribute(attributes, PROPERTY_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setPropertyValue( getAttribute(attributes, PROPERTY_VALUE_FIELD_TAG, elementName, null) );

                typedParentElement.addCustomProperty(typedElement);

                element = typedElement;
            }

        } else if ( parentElement instanceof ptfDriver ) {
            ptfDriver typedParentElement = (ptfDriver) parentElement;

            // INCLUDED_EFIX_ELEMENT_NAME
            // PRODUCT_UPDATE_ELEMENT_NAME
            // COMPONENT_UPDATE_ELEMENT_NAME
            // PLATFORM_PREREQ_ELEMENT_NAME
            // PRODUCT_PREREQ_ELEMENT_NAME
            // PRODUCT_COREQ_ELEMENT_NAME
            // CUSTOM_PROPERTY_ELEMENT_NAME

            if ( elementName.equals(INCLUDED_EFIX_ELEMENT_NAME) ) {
                includedEFix typedElement = new includedEFix();

                typedElement.setEFixId( getAttribute(attributes, EFIX_ID_FIELD_TAG, elementName, null) );

                typedParentElement.addIncludedEFix(typedElement);

                element = typedElement;

            } else if ( elementName.equals(PRODUCT_UPDATE_ELEMENT_NAME) ) {
                productUpdate typedElement = new productUpdate();

                typedElement.setProductId( getAttribute(attributes, PRODUCT_ID_FIELD_TAG, elementName, "") );
                typedElement.setProductName( getAttribute(attributes, PRODUCT_NAME_FIELD_TAG, elementName, "") );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, "") );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, "") );
                typedElement.setBuildLevel( getAttribute(attributes, BUILD_LEVEL_FIELD_TAG, elementName, "") );

                typedParentElement.addProductUpdate(typedElement);

                element = typedElement;

            } else if ( elementName.equals(COMPONENT_UPDATE_ELEMENT_NAME) ) {
                componentUpdate typedElement = new componentUpdate();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setUpdateType( getAttribute(attributes, UPDATE_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setIsRequired( getAttribute(attributes, IS_REQUIRED_FIELD_TAG, elementName, "false") );
                typedElement.setIsOptional( getAttribute(attributes, IS_OPTIONAL_FIELD_TAG, elementName, "false") );
                typedElement.setIsRecommended( getAttribute(attributes, IS_RECOMMENDED_FIELD_TAG, elementName, "false") );
                typedElement.setIsExternal( getAttribute(attributes, IS_EXTERNAL_FIELD_TAG, elementName, "false") );
                typedElement.setRootPropertyFile( getAttribute(attributes, ROOT_PROPERTY_FILE_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyName( getAttribute(attributes, ROOT_PROPERTY_NAME_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyValue( getAttribute(attributes, ROOT_PROPERTY_VALUE_FIELD_TAG, elementName, "") );
                typedElement.setIsCustom( getAttribute(attributes, IS_CUSTOM_FIELD_TAG, elementName, "false") );
                typedElement.setPrimaryContent( getAttribute(attributes, PRIMARY_CONTENT_FIELD_TAG, elementName, null) );

                typedParentElement.addComponentUpdate(typedElement);

                element = typedElement;

            } else if ( elementName.equals(PLATFORM_PREREQ_ELEMENT_NAME) ) {
                platformPrereq typedElement = new platformPrereq();

                typedElement.setArchitecture( getAttribute(attributes, ARCHITECTURE_FIELD_TAG, elementName, null) );
                typedElement.setOSPlatform( getAttribute(attributes, OS_PLATFORM_FIELD_TAG, elementName, "") );
                typedElement.setOSVersion( getAttribute(attributes, OS_VERSION_FIELD_TAG, elementName, "") );

                typedParentElement.addPlatformPrereq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(PRODUCT_PREREQ_ELEMENT_NAME) ) {
                productPrereq typedElement = new productPrereq();

                typedElement.setProductId( getAttribute(attributes, PRODUCT_ID_FIELD_TAG, elementName, "") );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, "") );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, "") );
                typedElement.setBuildLevel( getAttribute(attributes, BUILD_LEVEL_FIELD_TAG, elementName, "") );

                typedParentElement.addProductPrereq(typedElement);

                element = typedElement;

            }  else if ( elementName.equals(PRODUCT_COREQ_ELEMENT_NAME) ) {
                productCoreq typedElement = new productCoreq();

                typedElement.setProductId( getAttribute(attributes, PRODUCT_ID_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
                typedElement.setBuildLevel( getAttribute(attributes, BUILD_LEVEL_FIELD_TAG, elementName, null) );

                typedParentElement.addProductCoreq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(CONFIG_TASK_ELEMENT_NAME) ) {
                configTask typedElement = new configTask();

                typedElement.setConfigurationTaskName( getAttribute(attributes, CONFIG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setUnconfigurationTaskName( getAttribute(attributes, UNCONFIG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setConfigurationRequired( getAttribute(attributes, CONFIG_REQUIRED_FIELD_TAG, elementName, "false") );
                typedElement.setUnconfigurationRequired( getAttribute(attributes, UNCONFIG_REQUIRED_FIELD_TAG, elementName, "false") );

                typedParentElement.addConfgiTask(typedElement);

                element = typedElement;

            } else if ( elementName.equals(CUSTOM_PROPERTY_ELEMENT_NAME) ) {
                customProperty typedElement = new customProperty();

                typedElement.setPropertyName( getAttribute(attributes, PROPERTY_NAME_FIELD_TAG, elementName, null) );
                typedElement.setPropertyType( getAttribute(attributes, PROPERTY_TYPE_FIELD_TAG, elementName, "") );
                typedElement.setPropertyValue( getAttribute(attributes, PROPERTY_VALUE_FIELD_TAG, elementName, "") );

                typedParentElement.addCustomProperty(typedElement);

                element = typedElement;
            }

        } else if ( parentElement instanceof componentUpdate ) {
            componentUpdate typedParentElement = (componentUpdate) parentElement;

            // COMPONENT_PREREQ_ELEMENT_NAME
            // FINAL_VERSION_ELEMENT_NAME
            // CUSTOM_PROPERTY_ELEMENT_NAME

            if ( elementName.equals(COMPONENT_PREREQ_ELEMENT_NAME) ) {
                componentVersion typedElement = new componentVersion();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setSpecVersion( getAttribute(attributes, SPEC_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
                
                typedParentElement.addComponentPrereq(typedElement);

                element = typedElement;

            } else if ( elementName.equals(FINAL_VERSION_ELEMENT_NAME) ) {
                componentVersion typedElement = new componentVersion();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setSpecVersion( getAttribute(attributes, SPEC_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
                
                typedParentElement.setFinalVersion(typedElement);

                element = typedElement;

            } else if ( elementName.equals(CUSTOM_PROPERTY_ELEMENT_NAME) ) {
                customProperty typedElement = new customProperty();

                typedElement.setPropertyName( getAttribute(attributes, PROPERTY_NAME_FIELD_TAG, elementName, null) );
                typedElement.setPropertyType( getAttribute(attributes, PROPERTY_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setPropertyValue( getAttribute(attributes, PROPERTY_VALUE_FIELD_TAG, elementName, null) );

                typedParentElement.addCustomProperty(typedElement);

                element = typedElement;
            }

        } else if ( parentElement instanceof efixApplied ) {
            efixApplied typedParentElement = (efixApplied) parentElement;
            // COMPONENT_APPLIED_ELEMENT_NAME

            if ( elementName.equals(COMPONENT_APPLIED_ELEMENT_NAME) ) {
                componentApplied typedElement = new componentApplied();
                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setUpdateType( getAttribute(attributes, UPDATE_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setIsRequired( getAttribute(attributes, IS_REQUIRED_FIELD_TAG, elementName, "true") );
                typedElement.setIsOptional( getAttribute(attributes, IS_OPTIONAL_FIELD_TAG, elementName, "true") );
                typedElement.setIsExternal( getAttribute(attributes, IS_EXTERNAL_FIELD_TAG, elementName, "false") );
                typedElement.setRootPropertyFile( getAttribute(attributes, ROOT_PROPERTY_FILE_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyName( getAttribute(attributes, ROOT_PROPERTY_NAME_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyValue( getAttribute(attributes, ROOT_PROPERTY_VALUE_FIELD_TAG, elementName, "") );
                typedElement.setIsCustom( getAttribute(attributes, IS_CUSTOM_FIELD_TAG, elementName, "false") );
                typedElement.setLogName( getAttribute(attributes, LOG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setBackupName( getAttribute(attributes, BACKUP_NAME_FIELD_TAG, elementName, null) );
                typedElement.setTimeStamp( getAttribute(attributes, TIME_STAMP_FIELD_TAG, elementName, null) );

                typedParentElement.addComponentApplied(typedElement);

                element = typedElement;
            } else if ( elementName.equals(CONFIG_APPLIED_ELEMENT_NAME) ) {
                configApplied typedElement = new configApplied();

                typedElement.setConfigName( getAttribute(attributes, CONFIG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setConfigured( getAttribute(attributes, CONFIG_CONFIGURED_FIELD_TAG, elementName, "false") );
                typedElement.setConfigurationActive( getAttribute(attributes, CONFIG_CONFIG_ACTIVE_FIELD_TAG, elementName, "false") );

                typedParentElement.addConfigApplied(typedElement);

                element = typedElement;
            }

        } else if ( parentElement instanceof ptfApplied ) {
            ptfApplied typedParentElement = (ptfApplied) parentElement;

            // COMPONENT_APPLIED_ELEMENT_NAME

            if ( elementName.equals(COMPONENT_APPLIED_ELEMENT_NAME) ) {
                componentApplied typedElement = new componentApplied();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setSelectiveUpdate( getAttribute(attributes, SELECTIVE_UPDATE_FIELD_TAG, elementName, "false"));
                typedElement.setUpdateType( getAttribute(attributes, UPDATE_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setIsRequired( getAttribute(attributes, IS_REQUIRED_FIELD_TAG, elementName, null) );
                typedElement.setIsOptional( getAttribute(attributes, IS_OPTIONAL_FIELD_TAG, elementName, null) );
                typedElement.setIsExternal( getAttribute(attributes, IS_EXTERNAL_FIELD_TAG, elementName, null) );
                typedElement.setRootPropertyFile( getAttribute(attributes, ROOT_PROPERTY_FILE_FIELD_TAG, elementName, null) );
                typedElement.setRootPropertyName( getAttribute(attributes, ROOT_PROPERTY_NAME_FIELD_TAG, elementName, null) );
                typedElement.setRootPropertyValue( getAttribute(attributes, ROOT_PROPERTY_VALUE_FIELD_TAG, elementName, null) );
                typedElement.setIsCustom( getAttribute(attributes, IS_CUSTOM_FIELD_TAG, elementName, null) );
                typedElement.setLogName( getAttribute(attributes, LOG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setBackupName( getAttribute(attributes, BACKUP_NAME_FIELD_TAG, elementName, null) );
                typedElement.setTimeStamp( getAttribute(attributes, TIME_STAMP_FIELD_TAG, elementName, null) );

                typedParentElement.addComponentApplied(typedElement);

                element = typedElement;
            } else if ( elementName.equals(CONFIG_APPLIED_ELEMENT_NAME) ) {
                configApplied typedElement = new configApplied();

                typedElement.setConfigName( getAttribute(attributes, CONFIG_NAME_FIELD_TAG, elementName, null) );
                typedElement.setConfigured( getAttribute(attributes, CONFIG_CONFIGURED_FIELD_TAG, elementName, "false") );
                typedElement.setConfigurationActive( getAttribute(attributes, CONFIG_CONFIG_ACTIVE_FIELD_TAG, elementName, "false") );

                typedParentElement.addConfigApplied(typedElement);

                element = typedElement;
            }

        } else if ( parentElement instanceof componentApplied ) {
            componentApplied typedParentElement = (componentApplied) parentElement;

            // INITIAL_VERSION_ELEMENT_NAME
            // FINAL_VERSION_ELEMENT_NAME

            if ( elementName.equals(FINAL_VERSION_ELEMENT_NAME) ) {
                componentVersion typedElement = new componentVersion();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setSpecVersion( getAttribute(attributes, SPEC_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
                
                typedParentElement.setFinalVersion(typedElement);

                element = typedElement;

            } else if ( elementName.equals(INITIAL_VERSION_ELEMENT_NAME) ) {
                componentVersion typedElement = new componentVersion();

                typedElement.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
                typedElement.setSpecVersion( getAttribute(attributes, SPEC_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );

                typedParentElement.setInitialVersion(typedElement);

                element = typedElement;
            }
        }

        if ( element == null )
            throw newInvalidElementException(parentElementName, elementName);
        else
            return element;
    }
}

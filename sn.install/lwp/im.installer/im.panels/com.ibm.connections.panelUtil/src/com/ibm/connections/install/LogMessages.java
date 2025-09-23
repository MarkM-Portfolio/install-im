/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;


import org.eclipse.osgi.util.NLS;



public class LogMessages extends NLS {
	private static final String BUNDLE_NAME
    = "com.ibm.connections.install.Logmessages"; //$NON-NLS-1$

static {
    // initialize resource bundles
    NLS.initializeMessages(BUNDLE_NAME, LogMessages.class);
}

private LogMessages() {
    // Do not instantiate
}

public static String info_st_install_ssc_login_success;
public static String info_st_install_ssc_deployment_list_count;
public static String info_st_install_ssc_check_log;
public static String info_st_install_ssc_deployment_plan_selected;
public static String info_st_install_ssc_deployment_details_count;

public static String info_st_install_ssc_property_from_ssc;
public static String info_st_install_ssc_property_im;
public static String info_st_install_was_dm_property_for_sn;
public static String info_st_install_ssc_property_is_anonymous_bind;
public static String info_st_install_ssc_property_anonymous_bind_true;
public static String info_st_install_ssc_property_from_offering;
public static String info_st_install_ssc_property_soap_port;
public static String info_st_install_was_ssc_enabled;
public static String info_st_install_im_job;
public static String info_st_install_im_property;
public static String info_st_install_db_hostname;

public static String info_st_install_db_check_log;
public static String info_st_install_ldap_check_log;

public static String err_st_install_ssc_login_failure;
public static String err_st_install_ssc_deployment_list_empty;
public static String err_st_install_ssc_deployment_list_exception;
public static String err_st_install_ssc_deployment_details_empty;
public static String err_st_install_ssc_deployment_details_exception;
public static String err_st_install_ldap_bind_dn_password_required;


}

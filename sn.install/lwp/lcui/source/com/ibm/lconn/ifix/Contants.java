/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2015, 2021                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
public class Contants {
	
	public static final String USER_INSTALL_ROOT = "user.install.root";
	public static final String WAS_HOME = "was.home";
	public static final String WAS_CELL = "was.cell";
	public static final String ClEAN_TEMP = "clean.work.temp";
	
    public static final String CONNECTIONS_CONFIG_FOLDER = "LotusConnections-config";
    public static final String PARTIALAPP_DELETE_PROPS = "ibm-partialapp-delete.props";

    public static final String FULLAPP_BACKUP_FILENAME_SUFFIX = "-fullapp-backup.ear";
    public static final String PARTIALAPP_BACKUP_FILENAME_SUFFIX = "-partialapp-backup.zip";
    public static final String PARTIALAPP_UPDATE_FILENAME_SUFFIX = "-partialapp-update.zip";
    public static final String PARTIALAPP_DELETE_FILENAME_SUFFIX = "-partialapp-delete.zip";

    public static final String EAR_FOLDER = "EAR";
    public static final String PROVISION_FOLDER = "provision.web";
    
    // APP_NAME - this is the component name used in the iFix structure
    // APP_EAR - the WebSphere EAR folder name
    // APP_EAR_BUILD_NAME - the filename of the ear as it comes from the build output

    public static final String ACTIVITIES_NAME = "activities";
    public static final String ACTIVITIES_EAR = "Activities.ear";
    public static final String ACTIVITIES_EAR_BUILD_NAME = "oa.ear";
    
    public static final String BLOGS_NAME = "blogs";
    public static final String BLOGS_EAR = "Blogs.ear";
    public static final String BLOGS_EAR_BUILD_NAME = "blogs.ear";
    
    public static final String COMMUNITIES_NAME = "communities";
    public static final String COMMUNITIES_EAR = "Communities.ear";
    public static final String COMMUNITIES_EAR_BUILD_NAME = "communities.ear";
    
    public static final String DOGEAR_NAME = "dogear";
    public static final String DOGEAR_EAR = "Dogear.ear";
    public static final String DOGEAR_EAR_BUILD_NAME = "dogear.ear";

    public static final String PROFILES_NAME = "profiles";
    public static final String PROFILES_EAR = "Profiles.ear";
    public static final String PROFILES_EAR_BUILD_NAME = "profiles.ear";

    public static final String FORUMS_NAME = "forum";
    public static final String FORUMS_EAR = "Forums.ear";
    public static final String FORUMS_EAR_BUILD_NAME = "forums.ear";

    public static final String FILES_NAME = "files";
    public static final String FILES_EAR = "Files.ear";
    public static final String FILES_EAR_BUILD_NAME = "files.ear";

    public static final String WIKIS_NAME = "wikis";
    public static final String WIKIS_EAR = "Wikis.ear";
    public static final String WIKIS_EAR_BUILD_NAME = "wikis.ear";

    public static final String HOMEPAGE_NAME = "homepage";
    public static final String HOMEPAGE_EAR = "Homepage.ear";
    public static final String HOMEPAGE_EAR_BUILD_NAME = "dboard.ear";

    public static final String SEARCH_NAME = "search";
    public static final String SEARCH_EAR = "Search.ear";
    public static final String SEARCH_EAR_BUILD_NAME = "search.ear";

    public static final String NEWS_NAME = "news";
    public static final String NEWS_EAR = "News.ear";
    public static final String NEWS_EAR_BUILD_NAME = "news.ear";

    public static final String METRICS_NAME = "metrics";
    public static final String METRICS_EAR = "Metrics.ear";
    public static final String METRICS_EAR_BUILD_NAME = "metrics.ear";
	
	public static final String METRICSUI_NAME = "metricsui";
    public static final String METRICSUI_EAR = "MetricsUI.ear";
    public static final String METRICSUI_EAR_BUILD_NAME = "metrics.ui.ear";
	
	public static final String METRICSEVENTCAPTURE_NAME = "metricseventcapture";
    public static final String METRICSEVENTCAPTURE_EAR = "MetricsEventCapture.ear";
    public static final String METRICSEVENTCAPTURE_EAR_BUILD_NAME = "metrics.eventcapture.ear";

    public static final String MODERATION_NAME = "moderation";
    public static final String MODERATION_EAR = "Moderation.ear";
    public static final String MODERATION_EAR_BUILD_NAME = "moderation.ear";

    public static final String MOBILE_NAME = "mobile";
    public static final String MOBILE_EAR = "Mobile.ear";
    public static final String MOBILE_EAR_BUILD_NAME = "mobile.ear";

    public static final String CCM_NAME = "ccm";
    public static final String CCM_EAR = "Extensions.ear";
    public static final String CCM_EAR_BUILD_NAME = "extensions.ear";
 
    public static final String MOBILEADMIN_NAME = "mobileadmin";
    public static final String MOBILEADMIN_EAR = "Mobile Administration.ear";
    public static final String MOBILEADMIN_EAR_BUILD_NAME = "mobile.admin.ear";

    public static final String PUSHNOTIFICATION_NAME = "notification";
    public static final String PUSHNOTIFICATION_EAR = "PushNotification.ear";
    public static final String PUSHNOTIFICATION_EAR_BUILD_NAME = "lconn.pushnotification.ear";

    public static final String COMMON_NAME = "common";
    public static final String COMMON_EAR = "Common.ear";
    public static final String COMMON_EAR_BUILD_NAME = "connections.common.ear";
    
    public static final String PROXY_NAME = "proxy";
    public static final String PROXY_EAR = "ConnectionsProxy.ear";
    public static final String PROXY_EAR_BUILD_NAME = "connections.proxy.ear";
    
    public static final String CONTAINER_NAME = "container";
    public static final String CONTAINER_EAR = "WidgetContainer.ear";
    public static final String CONTAINER_EAR_BUILD_NAME = "widget.container.ear";
    
    public static final String HELP_NAME = "help";
    public static final String HELP_EAR = "Help.ear";
    public static final String HELP_EAR_BUILD_NAME = "cnxhelp.ear";
    
    public static final String RTE_NAME = "rte";
    public static final String RTE_EAR = "RichTextEditors.ear";
    public static final String RTE_EAR_BUILD_NAME = "rte.ear";
    
    public static final String URLPREVIEW_NAME = "urlpreview";
    public static final String URLPREVIEW_EAR = "URLPreview.ear";   
    public static final String URLPREVIEW_EAR_BUILD_NAME = "oembed.ear";   
	
    public static final String INVITE_NAME = "invite";
    public static final String INVITE_EAR = "Invite.ear";   
    public static final String INVITE_EAR_BUILD_NAME = "invite.ear";   
    
    public static final String SIDEBAR_NAME = "sidebar";
    public static final String SIDEBAR_EAR = "Sidebar.ear";   
    public static final String SIDEBAR_EAR_BUILD_NAME = "social.sidebar.ear";   
    
    public static final String TOUCHPOINT_NAME = "touchpoint";
    public static final String TOUCHPOINT_EAR = "Touchpoint.ear";       
    public static final String TOUCHPOINT_EAR_BUILD_NAME = "touchpoint.ear";       
    
    public static final String SHAREPOINT_NAME = "sharepoint";
    public static final String SHAREPOINT_EAR = "Sharepoint.ear";    
    public static final String SHAREPOINT_EAR_BUILD_NAME = "sharepoint.widget.ear";    
    
    public static final String IC360_CORE_NAME = "ic360_core";
    public static final String IC360_CORE_EAR = "IC360_core.ear";    
    public static final String IC360_CORE_EAR_BUILD_NAME = "ic360.core.app.ear";    
    
    public static final String IC360_MOD_TEMPLATE_NAME = "ic360_mod_template";
    public static final String IC360_MOD_TEMPLATE_EAR = "IC360_mod_template.ear";    
    public static final String IC360_MOD_TEMPLATE_EAR_BUILD_NAME = "ic360.mod.template.ear";    
    
    public static final String NAVIGATOR_NAME = "navigator";
    public static final String NAVIGATOR_EAR = "navigator.ear";
    public static final String NAVIGATOR_EAR_BUILD_NAME = "navigator.ear";
    
    public static final String FILENETENGINE_NAME = "filenetengine";
    public static final String FILENETENGINE_EAR = "FileNetEngine.ear";   
    public static final String FILENETENGINE_EAR_BUILD_NAME = "filenetengine.ear";   
	
	public static final String ICEC_NAME = "icec";
    public static final String ICEC_EAR = "ICEC.ear";   
    public static final String ICEC_EAR_BUILD_NAME = "icec.ear";   
    
    public static final String VARIABLES_FILE = "variables.xml";
    public static final String WEBRESOURCES = "webresources";

	//********************************************************
	//  Method Definitions
	//********************************************************
    public static String loadWASVariable(String variableName, File variables){
    	//
		Configuration config = null;
		String variableValue = null ;
		try {
			config = XMLHelper.loadConfigFile(variables);
			if (config != null) {
				System.out.println("variables.xml file [" + variables.getAbsolutePath() + "] can be loaded.");
			} else {
				System.out.println("variables.xml file can not be loaded.");
			}
	        int i = 0;
	        while (config != null) {
	            String symbolicName = config.getString("entries(" + i + ")[@symbolicName]");

	            if (symbolicName == null)
	                break;       
	           if(variableName.equals(symbolicName))
	           {
	        	   variableValue = config.getString("entries(" + i + ")[@value]");
	        	   System.out.println("symbolicName=" + symbolicName);
	        	   System.out.println("value=" + variableValue);      
		        break;
	           }
	            i++;
	        }	
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return variableValue;
    }


}

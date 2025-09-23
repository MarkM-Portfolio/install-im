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
import java.util.ArrayList;
import java.util.Vector;

import com.ibm.websphere.update.delta.Logger;


public class Component {
	ZipUtil zipUtil = new ZipUtil();	
	String componentName = null;
	String appName = null;
	String appEARName = null;
	// appEARFileNameFromBuild - this is used when replacing the full ear and you need to know the .ear file name in the iFix.
	String appEARFileNameFromBuild = null;

	String deleteEARFileList = null;
	String provisionFileList = null;
	
	String srcFolderPath = null;
	
	File srcFolder = null;
	File earFolder = null;
	File bundleName = null;
    File provisionFolder = null;
    File oldProvisionFolder = null;
	String updatedEARFileList = null;
	File newProvisionFolder = null;
	File updateZip = null;
	File earFile = null;
	boolean isInstalled = false;
	protected Logger logStream;
	File backupZip = null;
	File deleteZip = null;
	File backupEAR = null;
	Vector<Component> subComponents = new Vector<Component>();
	boolean isFullEarUpdate = false;

	public Component(){
    	
    }

    public Logger getLogStream() {
		return logStream;
	}

	public void setLogStream(Logger logStream) {
		this.logStream = logStream;
		zipUtil.setLogStream(logStream);
	}
    
	public String getAppName() {
		if(appName != null)
			appName = appName.toLowerCase();
		return appName;
	}

	public String getAppEARName() {
		return appEARName;
	}

	public String getappEARFileNameFromBuild() {
		return appEARFileNameFromBuild;
	}

	public String getAppEARNameNoExtension() {

		String name = appEARName;
		// Dynamically set the "export" name as the EAR name minus ".ear".  This is the actual name
		// of the application when looked up in WAS.
		if (name != null) {
			name = name.substring(0, (name.indexOf(".")));
		}

		return name;
	}

    public void setOldProvisionFolder(File oldProvisionFolder) {
		this.oldProvisionFolder = oldProvisionFolder;
	}

	public void setNewProvisionFolder(File newProvisionFolder) {
		this.newProvisionFolder = newProvisionFolder;
	}	
    
    public File getOldProvisionFolder() {
		return oldProvisionFolder;
	}
	public File getNewProvisionFolder() {
		return newProvisionFolder;
	}

	public File getUpdateZip() {
		return updateZip;
	}

	public void setUpdateZip(File updateZip) {
		this.updateZip = updateZip;
	}

	public File getBackupZip() {
		return backupZip;
	}

	public void setBackupZip(File backupZip) {
		this.backupZip = backupZip;
	}

	public File getBackupEAR() {
		return backupEAR;
	}

	public void setBackupEAR(File backupEAR) {
		this.backupEAR = backupEAR;
	}

	public File getDeleteZip() {
		return deleteZip;
	}

	public void setDeleteZip(File deleteZip) {
		this.deleteZip = deleteZip;
	}

	public File getEarFile() {
		return earFile;
	}

	public void setEarFile(File earFile) {
		this.earFile = earFile;
	}

	public Vector<Component> getSubComponents() {
		return subComponents;
	}

	private String regexp =".*/WEB-INF/eclipse/plugins/.*\\.jar";
	
	public void setName (String name){
		this.componentName = name;
	}
	
	public void setSrcFolder (String srcFolderPath){
		this.srcFolderPath = srcFolderPath;
	}
	
	public Component(String name, String srcFolderPath,  Logger logStream){
		this.componentName = name;
		this.appName = name;
		this.srcFolderPath = srcFolderPath;
		srcFolder = new File(srcFolderPath);
		this.setLogStream(logStream);
	}
	
	public Component(String name, String appName, String srcFolderPath ,  Logger logStream){
		this.componentName = name;
		this.appName = appName;
		this.srcFolderPath = srcFolderPath;
		srcFolder = new File(srcFolderPath);
		this.setLogStream(logStream);
	}
	
	protected void configureEARName(){
		logStream.Both("Component.configureEARName:  appName=" + this.appName + ", componentName="+this.componentName );

    	if(componentName.equalsIgnoreCase(Contants.NEWS_NAME) || 
    			componentName.equalsIgnoreCase(Contants.COMMON_NAME) ||
    			componentName.equalsIgnoreCase(Contants.CONTAINER_NAME) ||
    			componentName.equalsIgnoreCase(Contants.PROXY_NAME) ||
    			componentName.equalsIgnoreCase(Contants.HELP_NAME) ||
    			componentName.equalsIgnoreCase(Contants.RTE_NAME) ||
    			componentName.equalsIgnoreCase(Contants.URLPREVIEW_NAME) ||
    			componentName.equalsIgnoreCase(Contants.INVITE_NAME) ||
    			componentName.equalsIgnoreCase(Contants.SIDEBAR_NAME) ||
    			componentName.equalsIgnoreCase(Contants.TOUCHPOINT_NAME) ||				
				componentName.equalsIgnoreCase(Contants.ICEC_NAME) ||
				componentName.equalsIgnoreCase(Contants.SHAREPOINT_NAME) ||
				componentName.equalsIgnoreCase(Contants.IC360_CORE_NAME) ||
				componentName.equalsIgnoreCase(Contants.IC360_MOD_TEMPLATE_NAME))
				{
    		
    		if(this.appName.equalsIgnoreCase(Contants.NEWS_NAME)){
               this.appEARName = Contants.NEWS_EAR;
               this.appEARFileNameFromBuild = Contants.NEWS_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.COMMON_NAME)){
    			this.appEARName = Contants.COMMON_EAR;
				this.appEARFileNameFromBuild = Contants.COMMON_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.PROXY_NAME)){
    			this.appEARName = Contants.PROXY_EAR;
    			this.appEARFileNameFromBuild = Contants.PROXY_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.HELP_NAME)){
    			this.appEARName = Contants.HELP_EAR;
    			this.appEARFileNameFromBuild = Contants.HELP_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.CONTAINER_NAME)){
    			this.appEARName = Contants.CONTAINER_EAR;
    			this.appEARFileNameFromBuild = Contants.CONTAINER_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.RTE_NAME)){
    			this.appEARName = Contants.RTE_EAR;
    			this.appEARFileNameFromBuild = Contants.RTE_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.URLPREVIEW_NAME)){
    			this.appEARName = Contants.URLPREVIEW_EAR;
    			this.appEARFileNameFromBuild = Contants.URLPREVIEW_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.INVITE_NAME)){
    			this.appEARName = Contants.INVITE_EAR;
    			this.appEARFileNameFromBuild = Contants.INVITE_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.SIDEBAR_NAME)){
    			this.appEARName = Contants.SIDEBAR_EAR;
    			this.appEARFileNameFromBuild = Contants.SIDEBAR_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.TOUCHPOINT_NAME)){
    			this.appEARName = Contants.TOUCHPOINT_EAR;
    			this.appEARFileNameFromBuild = Contants.TOUCHPOINT_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.ICEC_NAME)){
    			this.appEARName = Contants.ICEC_EAR;
    			this.appEARFileNameFromBuild = Contants.ICEC_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.SHAREPOINT_NAME)){
	    		this.appEARName = Contants.SHAREPOINT_EAR;
	    		this.appEARFileNameFromBuild = Contants.SHAREPOINT_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.IC360_CORE_NAME)){
    			this.appEARName = Contants.IC360_CORE_EAR;
    			this.appEARFileNameFromBuild = Contants.IC360_CORE_EAR_BUILD_NAME;
    		}else if(this.appName.equalsIgnoreCase(Contants.IC360_MOD_TEMPLATE_NAME)){
    			this.appEARName = Contants.IC360_MOD_TEMPLATE_EAR;
    			this.appEARFileNameFromBuild = Contants.IC360_MOD_TEMPLATE_EAR_BUILD_NAME; 	
			}else{
    			this.appName = Contants.NEWS_NAME;
    			this.appEARName = Contants.NEWS_EAR;
    			this.appEARFileNameFromBuild = Contants.NEWS_EAR_BUILD_NAME;
    		}
    		
    	}else if(componentName.equalsIgnoreCase(Contants.MOBILE_NAME) || componentName.equalsIgnoreCase(Contants.MOBILEADMIN_NAME)){
    		if(this.appName.equalsIgnoreCase(Contants.MOBILE_NAME)){
                this.appEARName = Contants.MOBILE_EAR;
                this.appEARFileNameFromBuild = Contants.MOBILE_EAR_BUILD_NAME;
    		}else if (this.appName.equalsIgnoreCase(Contants.MOBILEADMIN_NAME)){
    			this.appEARName = Contants.MOBILEADMIN_EAR;
    			this.appEARFileNameFromBuild = Contants.MOBILEADMIN_EAR_BUILD_NAME;
    		}else{
				logStream.Both("Unexpected appName of " + this.appName + " for component " + componentName + ", defaulting appName to " + Contants.MOBILE_NAME);
    			this.appName = Contants.MOBILE_NAME;
    			this.appEARName = Contants.MOBILE_EAR;
    			this.appEARFileNameFromBuild = Contants.MOBILE_EAR_BUILD_NAME;
    		}
   		
    	}else if(componentName.equalsIgnoreCase(Contants.METRICS_NAME) || componentName.equalsIgnoreCase(Contants.METRICSUI_NAME) || componentName.equalsIgnoreCase(Contants.METRICSEVENTCAPTURE_NAME) ){
    		if(this.appName.equalsIgnoreCase(Contants.METRICS_NAME)){
                this.appEARName = Contants.METRICS_EAR;
                this.appEARFileNameFromBuild = Contants.METRICS_EAR_BUILD_NAME;
    		}else if (this.appName.equalsIgnoreCase(Contants.METRICSUI_NAME)){
    			this.appEARName = Contants.METRICSUI_EAR;
    			this.appEARFileNameFromBuild = Contants.METRICSUI_EAR_BUILD_NAME;
    		}else if (this.appName.equalsIgnoreCase(Contants.METRICSEVENTCAPTURE_NAME)){
    			this.appEARName = Contants.METRICSEVENTCAPTURE_EAR;
    			this.appEARFileNameFromBuild = Contants.METRICSEVENTCAPTURE_EAR_BUILD_NAME;
    		}else{
				logStream.Both("Unexpected appName of " + this.appName + " for component " + componentName + ", defaulting appName to " + Contants.METRICS_NAME);
    			this.appName = Contants.METRICS_NAME;
    			this.appEARName = Contants.METRICS_EAR;
    			this.appEARFileNameFromBuild = Contants.METRICS_EAR_BUILD_NAME;
    		}
   		
    	}else if(componentName.equalsIgnoreCase(Contants.FILES_NAME) || componentName.equalsIgnoreCase(Contants.PUSHNOTIFICATION_NAME)){
    		if(this.appName.equalsIgnoreCase(Contants.FILES_NAME)){
                this.appEARName = Contants.FILES_EAR;
                this.appEARFileNameFromBuild = Contants.FILES_EAR_BUILD_NAME;
    		}else if (this.appName.equalsIgnoreCase(Contants.PUSHNOTIFICATION_NAME)){
    			this.appEARName = Contants.PUSHNOTIFICATION_EAR;
    			this.appEARFileNameFromBuild = Contants.PUSHNOTIFICATION_EAR_BUILD_NAME;
    		}else{
				logStream.Both("Unexpected appName of " + this.appName + " for component " + componentName + ", defaulting appName to " + Contants.FILES_NAME);
    			this.appName = Contants.FILES_NAME;
    			this.appEARName = Contants.FILES_EAR;
    			this.appEARFileNameFromBuild = Contants.FILES_EAR_BUILD_NAME;
    		}
    	}else if(componentName.equalsIgnoreCase(Contants.ACTIVITIES_NAME)){
    		this.appName = Contants.ACTIVITIES_NAME;
    		this.appEARName = Contants.ACTIVITIES_EAR;
    		this.appEARFileNameFromBuild = Contants.ACTIVITIES_EAR_BUILD_NAME;
    	}    	
    	else if(componentName.equalsIgnoreCase(Contants.CCM_NAME) ||componentName.equalsIgnoreCase(Contants.FILENETENGINE_NAME) || componentName.equalsIgnoreCase(Contants.NAVIGATOR_NAME)){
    		if(this.appName.equalsIgnoreCase(Contants.FILENETENGINE_NAME)){
                this.appEARName = Contants.FILENETENGINE_EAR;
                this.appEARFileNameFromBuild = Contants.FILENETENGINE_EAR_BUILD_NAME;
    		}else if (this.appName.equalsIgnoreCase(Contants.NAVIGATOR_NAME)){
    			this.appEARName = Contants.NAVIGATOR_EAR;
    			this.appEARFileNameFromBuild = Contants.NAVIGATOR_EAR_BUILD_NAME;
    		}else{
        		this.appName = Contants.CCM_NAME;
        		this.appEARName = Contants.CCM_EAR;
        		this.appEARFileNameFromBuild = Contants.CCM_EAR_BUILD_NAME;
    		}    		
    	}else if(componentName.equalsIgnoreCase(Contants.BLOGS_NAME)){
    		this.appName = Contants.BLOGS_NAME;
    		this.appEARName = Contants.BLOGS_EAR;
    		this.appEARFileNameFromBuild = Contants.BLOGS_EAR_BUILD_NAME;    		
    	}else if(componentName.equalsIgnoreCase(Contants.COMMUNITIES_NAME)){
    		this.appName = Contants.COMMUNITIES_NAME;
    		this.appEARName = Contants.COMMUNITIES_EAR;
    		this.appEARFileNameFromBuild = Contants.COMMUNITIES_EAR_BUILD_NAME;
    	}else if(componentName.equalsIgnoreCase(Contants.DOGEAR_NAME)){
    		this.appName = Contants.DOGEAR_NAME;
    		this.appEARName = Contants.DOGEAR_EAR;
    		this.appEARFileNameFromBuild = Contants.DOGEAR_EAR_BUILD_NAME;
    	}else if(componentName.equalsIgnoreCase(Contants.PROFILES_NAME)){
    		this.appName = Contants.PROFILES_NAME;
    		this.appEARName = Contants.PROFILES_EAR;
    		this.appEARFileNameFromBuild = Contants.PROFILES_EAR_BUILD_NAME;
    	}else if(componentName.equalsIgnoreCase(Contants.FORUMS_NAME)){
    		this.appName = Contants.FORUMS_NAME;
    		this.appEARName = Contants.FORUMS_EAR;
    		this.appEARFileNameFromBuild = Contants.FORUMS_EAR_BUILD_NAME;
    	}else if(componentName.equalsIgnoreCase(Contants.WIKIS_NAME)){
    		this.appName = Contants.WIKIS_NAME;
    		this.appEARName = Contants.WIKIS_EAR;
    		this.appEARFileNameFromBuild = Contants.WIKIS_EAR_BUILD_NAME;    		
    	}else if(componentName.equalsIgnoreCase(Contants.HOMEPAGE_NAME)){
    		this.appName = Contants.HOMEPAGE_NAME;
    		this.appEARName = Contants.HOMEPAGE_EAR;
    		this.appEARFileNameFromBuild = Contants.HOMEPAGE_EAR_BUILD_NAME;    		
    	}else if(componentName.equalsIgnoreCase(Contants.SEARCH_NAME)){
    		this.appName = Contants.SEARCH_NAME;
    		this.appEARName = Contants.SEARCH_EAR;
    		this.appEARFileNameFromBuild = Contants.SEARCH_EAR_BUILD_NAME;
		}else if(this.appName.equalsIgnoreCase(Contants.IC360_CORE_NAME)){
			this.appName = Contants.IC360_CORE_NAME;
			this.appEARName = Contants.IC360_CORE_EAR; 
			this.appEARFileNameFromBuild = Contants.IC360_CORE_EAR_BUILD_NAME; 
		}else if(this.appName.equalsIgnoreCase(Contants.IC360_MOD_TEMPLATE_NAME)){
			this.appName = Contants.IC360_MOD_TEMPLATE_NAME;
			this.appEARName = Contants.IC360_MOD_TEMPLATE_EAR;
			this.appEARFileNameFromBuild = Contants.IC360_MOD_TEMPLATE_EAR_BUILD_NAME;
		}else if(this.appName.equalsIgnoreCase(Contants.SHAREPOINT_NAME)){
			this.appName = Contants.SHAREPOINT_NAME;
			this.appEARName = Contants.SHAREPOINT_EAR;
			this.appEARFileNameFromBuild = Contants.SHAREPOINT_EAR_BUILD_NAME;
		}else if(componentName.equalsIgnoreCase(Contants.MODERATION_NAME)){
    		this.appName = Contants.MODERATION_NAME;
    		this.appEARName = Contants.MODERATION_EAR;
    		this.appEARFileNameFromBuild = Contants.MODERATION_EAR_BUILD_NAME;    		
    	}else{
    		logStream.Both("error occurs when configureEARName(): "  +  componentName);
    	}

    	logStream.Both("appName=" + appName );
    	logStream.Both("appEARName=" + appEARName );
    	logStream.Both("appEARFileNameFromBuild=" + appEARFileNameFromBuild );
	}
	public void processFromMetadata(){
		configureEARName();
		backupZip = new File (srcFolder, this.appName + Contants.PARTIALAPP_BACKUP_FILENAME_SUFFIX);
		deleteZip = new File (srcFolder, this.appName + Contants.PARTIALAPP_DELETE_FILENAME_SUFFIX);
		backupEAR = new File (srcFolder, this.appName + Contants.FULLAPP_BACKUP_FILENAME_SUFFIX);
		
		oldProvisionFolder = new File(srcFolder, Contants.PROVISION_FOLDER + File.separator +"old");
		newProvisionFolder = new File(srcFolder, Contants.PROVISION_FOLDER + File.separator +"new");
		
	}
	public void processFromPackage(){
							
		earFolder = new File(srcFolderPath + File.separator + Contants.EAR_FOLDER);
		provisionFolder = new File(srcFolderPath + File.separator + Contants.PROVISION_FOLDER);

		
		if(provisionFolder.exists()){
			
			provisionFileList = FileUtil.getAllFilesRelPath(provisionFolder);
			logStream.Both("provisionFileList=" + provisionFileList);
			
		}else{
			logStream.Both("the directory does NOT exist, path is " + provisionFolder.getAbsolutePath());
		}	
		
		if (earFolder.exists()){
			boolean hasSubComponent = false;
    		configureEARName();
			//need to handle subComponent ***
	    	if(componentName.equalsIgnoreCase(Contants.NEWS_NAME)){
	    		File news_news = new File(earFolder, Contants.NEWS_NAME);
	    		File news_common = new File(earFolder, Contants.COMMON_NAME);
	    		File news_container = new File(earFolder, Contants.CONTAINER_NAME);
	    		File news_proxy = new File(earFolder, Contants.PROXY_NAME);
	    		File news_help = new File(earFolder, Contants.HELP_NAME);
                File news_icec = new File(earFolder, Contants.ICEC_NAME);
	    		File news_rte = new File(earFolder, Contants.RTE_NAME);
	    		File news_urlpreview = new File(earFolder, Contants.URLPREVIEW_NAME);
	    		File news_invite = new File(earFolder, Contants.INVITE_NAME);
	    		File news_sidebar = new File(earFolder, Contants.SIDEBAR_NAME);
	    		File news_touchpoint = new File(earFolder, Contants.TOUCHPOINT_NAME);
	    		File news_sharepoint = new File(earFolder, Contants.SHAREPOINT_NAME);				
	    		File news_ic360_core = new File(earFolder, Contants.IC360_CORE_NAME);				
	    		File news_ic360_mod_template = new File(earFolder, Contants.IC360_MOD_TEMPLATE_NAME);	

	    		if(news_news.exists()){
	    			SubComponent newsComponent = new SubComponent(componentName, Contants.NEWS_NAME, news_news, this.logStream);
	    			subComponents.add(newsComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_common.exists()){
	    			SubComponent commonComponent = new SubComponent(componentName, Contants.COMMON_NAME, news_common,this.logStream);
	    			subComponents.add(commonComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_container.exists()){
	    			SubComponent containerComponent = new SubComponent(componentName, Contants.CONTAINER_NAME, news_container, this.logStream);
	    			subComponents.add(containerComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_proxy.exists()){
	    			SubComponent proxyComponent = new SubComponent(componentName, Contants.PROXY_NAME, news_proxy, this.logStream);
	    			subComponents.add(proxyComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_help.exists()){
	    			SubComponent helpComponent = new SubComponent(componentName, Contants.HELP_NAME, news_help, this.logStream);
	    			subComponents.add(helpComponent);
	    			hasSubComponent = true;
	    		}
				if(news_icec.exists()){
	    			SubComponent icecComponent = new SubComponent(componentName, Contants.ICEC_NAME, news_icec, this.logStream);
	    			subComponents.add(icecComponent);
	    			hasSubComponent = true;
	    		}
	    		if(news_rte.exists()){
	    			SubComponent rteComponent = new SubComponent(componentName, Contants.RTE_NAME, news_rte, this.logStream);
	    			subComponents.add(rteComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_urlpreview.exists()){
	    			SubComponent urlComponent = new SubComponent(componentName, Contants.URLPREVIEW_NAME, news_urlpreview, this.logStream);
	    			subComponents.add(urlComponent);
	    			hasSubComponent = true;
	    		}	

	    		if(news_invite.exists()){
	    			SubComponent inviteComponent = new SubComponent(componentName, Contants.INVITE_NAME, news_invite, this.logStream);
	    			subComponents.add(inviteComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_sidebar.exists()){
	    			SubComponent sidebarComponent = new SubComponent(componentName, Contants.SIDEBAR_NAME, news_sidebar, this.logStream);
	    			subComponents.add(sidebarComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_touchpoint.exists()){
	    			SubComponent touchpointComponent = new SubComponent(componentName, Contants.TOUCHPOINT_NAME, news_touchpoint, this.logStream);
	    			subComponents.add(touchpointComponent);
	    			hasSubComponent = true;
	    		}
	    		
	    		if(news_sharepoint.exists()){
	    			SubComponent sharepointComponent = new SubComponent(componentName, Contants.SHAREPOINT_NAME, news_sharepoint, this.logStream);
	    			subComponents.add(sharepointComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_ic360_core.exists()){
	    			SubComponent ic360_coreComponent = new SubComponent(componentName, Contants.IC360_CORE_NAME, news_ic360_core, this.logStream);
	    			subComponents.add(ic360_coreComponent);
	    			hasSubComponent = true;
	    		}

	    		if(news_ic360_mod_template.exists()){
	    			SubComponent ic360_mod_templateComponent = new SubComponent(componentName, Contants.IC360_MOD_TEMPLATE_NAME, news_ic360_mod_template, this.logStream);
	    			subComponents.add(ic360_mod_templateComponent);
	    			hasSubComponent = true;
	    		}
	    	}else if (componentName.equalsIgnoreCase(Contants.MOBILE_NAME)){
	    		File mobile_mobile = new File(earFolder, Contants.MOBILE_NAME);
	    		File mobile_mobileadmin = new File(earFolder, Contants.MOBILEADMIN_NAME);

	    		if(mobile_mobile.exists()){
	    			SubComponent mobileComponent = new SubComponent(componentName, Contants.MOBILE_NAME, mobile_mobile, this.logStream);
	    			subComponents.add(mobileComponent);
	    			hasSubComponent = true;
	    		}

	    		if(mobile_mobileadmin.exists()){
	    			SubComponent mobileadminComponent = new SubComponent(componentName, Contants.MOBILEADMIN_NAME, mobile_mobileadmin, this.logStream);
	    			subComponents.add(mobileadminComponent);
	    			hasSubComponent = true;
	    		}

	    		
	    	}else if (componentName.equalsIgnoreCase(Contants.METRICS_NAME)){
	    		File metrics_metrics = new File(earFolder, Contants.METRICS_NAME);
	    		File metrics_metricsui = new File(earFolder, Contants.METRICSUI_NAME);
				File metrics_metricseventcapture = new File(earFolder, Contants.METRICSEVENTCAPTURE_NAME);

	    		if(metrics_metrics.exists()){
	    			SubComponent metricsComponent = new SubComponent(componentName, Contants.METRICS_NAME, metrics_metrics, this.logStream);
	    			subComponents.add(metricsComponent);
	    			hasSubComponent = true;
	    		}

	    		if(metrics_metricsui.exists()){
	    			SubComponent metricsuiComponent = new SubComponent(componentName, Contants.METRICSUI_NAME, metrics_metricsui, this.logStream);
	    			subComponents.add(metricsuiComponent);
	    			hasSubComponent = true;
	    		}
				if(metrics_metricseventcapture.exists()){
	    			SubComponent metricseventcaptureComponent = new SubComponent(componentName, Contants.METRICSEVENTCAPTURE_NAME, metrics_metricseventcapture, this.logStream);
	    			subComponents.add(metricseventcaptureComponent);
	    			hasSubComponent = true;
	    		}

	    		
	    	}else if (componentName.equalsIgnoreCase(Contants.FILES_NAME)){
	    		File files_files = new File(earFolder, Contants.FILES_NAME);
	    		File files_notification = new File(earFolder, Contants.PUSHNOTIFICATION_NAME);

	    		if(files_files.exists()){
	    			SubComponent filesComponent = new SubComponent(componentName, Contants.FILES_NAME, files_files, this.logStream);
	    			subComponents.add(filesComponent);
	    			hasSubComponent = true;
	    		}

	    		if(files_notification.exists()){
	    			SubComponent notificationComponent = new SubComponent(componentName, Contants.PUSHNOTIFICATION_NAME, files_notification, this.logStream);
	    			subComponents.add(notificationComponent);
	    			hasSubComponent = true;
	    		}

	    	}else if (componentName.equalsIgnoreCase(Contants.CCM_NAME)){
	    		File ccm_ccm = new File(earFolder, Contants.CCM_NAME);
	    		File ccm_filenet = new File(earFolder, Contants.FILENETENGINE_NAME);
	    		File ccm_navigator = new File(earFolder, Contants.NAVIGATOR_NAME);

	    		if(ccm_ccm.exists()){
	    			SubComponent ccmComponent = new SubComponent(componentName, Contants.CCM_NAME, ccm_ccm, this.logStream);
	    			subComponents.add(ccmComponent);
	    			hasSubComponent = true;
	    		}

	    		if(ccm_filenet.exists()){
	    			SubComponent filenetComponent = new SubComponent(componentName, Contants.FILENETENGINE_NAME, ccm_filenet, this.logStream);
	    			subComponents.add(filenetComponent);
	    			hasSubComponent = true;
	    		}
	    		if(ccm_navigator.exists()){
	    			SubComponent navigatorComponent = new SubComponent(componentName, Contants.NAVIGATOR_NAME, ccm_navigator, this.logStream);
	    			subComponents.add(navigatorComponent);
	    			hasSubComponent = true;
	    		}
			}else{
	    		//other component does NOT contain sub-Component.
	    	}
	    	
			if(!hasSubComponent){
				logStream.Both("hasSubComponent= false");	
	    		// normal fix: one ifix is only used to update one application
				updatedEARFileList = FileUtil.getAllFilesRelPath(earFolder);				
				logStream.Both(componentName + "-updatedEARFileList=" + updatedEARFileList);				
				createUpdatePackage(earFolder);
		
			}else{
				logStream.Both("the directory does NOT exist, path is " + earFolder.getAbsolutePath());
			}
		}
	}
    protected void createUpdatePackage(File newearFolder){
		logStream.Both("Starting createUpdatePackage for " + newearFolder);
    	
		String zipDirectory = newearFolder.getAbsolutePath();
		String updateZipName = this.appName + Contants.PARTIALAPP_UPDATE_FILENAME_SUFFIX;
		String deleteZipName = this.appName + Contants.PARTIALAPP_DELETE_FILENAME_SUFFIX;
		String includesList = ".*/ibm-partialapp-delete\\.props"; 
		String excludesList = Contants.PARTIALAPP_DELETE_PROPS;
		
		// First check to see if we are doing a full EAR replacement.
		// If the ear file exists, then set earFile and skip updateZip processing.
		// We always assume the ear file will be lowercase, since this is how it comes out of the build.
		earFile = new File(newearFolder + File.separator + appEARFileNameFromBuild);
		logStream.Both("Checking to see if doing full ear replacement, earFile=" + earFile);
		if(earFile != null && earFile.exists()) {
			logStream.Both("Full ear file detected " + earFile);
			this.isFullEarUpdate = true;
		} else {
			zipUtil.doZip(zipDirectory, updateZipName, null, excludesList, false, false);
			
			if(this.updatedEARFileList.indexOf("ibm-partialapp-delete.props")>0) {
			 zipUtil.doZip(zipDirectory, deleteZipName, includesList, null, false, true);
			}else{
			 logStream.Both("No ibm-partialapp-delete.props exist");
			}

			updateZip = new File(newearFolder.getParent() + File.separator + updateZipName);
			if(updateZip != null && updateZip.exists()){
				logStream.Both("updateZip exist, path is " + updateZip.getAbsolutePath());
			}else{
				logStream.Both("updateZip NOT exist, path is " + updateZip.getAbsolutePath());
			}	

			deleteZip = new File(newearFolder.getParent() + File.separator + deleteZipName);
			if (deleteZip !=null && deleteZip.exists()){
				logStream.Both("deleteZip exist, path is " + deleteZip.getAbsolutePath());
				ArrayList<String> array = FileUtil.getDeleteFilesPattern(deleteZip);
				
				for(int i=0; i< array.size(); i++){
					if(i == 0){
					  deleteEARFileList = array.get(i);
					}else{
					  deleteEARFileList += "," + array.get(i);
					}
				}		
			}else{
				logStream.Both("no need to delete files, deleteZip NOT exist");
			}
		}
    }
	
	public void changeTimeStamp(String extractDirPath, String earContainerPath){
		File extractDir = new File(extractDirPath);
		File newearFolder = new File(earContainerPath);
		
		if(this.earFolder !=null && earFolder.exists()){
			File[] fileList = earFolder.listFiles();
    		ArrayList<File> bundleFiles = new ArrayList<File>();
			File file;
			for(int j=0; j< fileList.length; j++){
               file = fileList[j];
               if(file.isDirectory()){
            	   findFileByRegexp(file, regexp, bundleFiles, newearFolder); 
               }else{
        		String relPath = file.getAbsolutePath().substring(this.earFolder.getAbsolutePath().length()+1);
        		logStream.Both("relParentPath= " + relPath);
        		File newFile = new File(newearFolder, relPath);           	   
            	try {
            		if(file.exists())
            		  logStream.Both("Copy File: " + file.getAbsolutePath() + " To: " + newFile.getAbsolutePath());
					FileUtil.copyFile(file, newFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logStream.Both("ERROR: " + e.getMessage());
					e.printStackTrace();
				}
               }
			}
						
			File bundleFile;
			for(int i=0; i< bundleFiles.size(); i++){
				bundleFile = bundleFiles.get(i);
				logStream.Both("bundleFile=" + bundleFile.getAbsolutePath());
				changeDirectoryName(bundleFile,extractDir,newearFolder );
			}
						
			updatedEARFileList = FileUtil.getAllFilesRelPath(newearFolder);
			logStream.Both("new updatedEARFileList=" + updatedEARFileList);
			createUpdatePackage(newearFolder);
		}
	}
	private void changeDirectoryName(File bundleFile, File extractDir, File newearFolder){

		String parentPath = bundleFile.getParent();
		String relParentPath = parentPath.substring(this.earFolder.getAbsolutePath().length()+1);
		logStream.Both("relParentPath= " + relParentPath);
		File pluginDir = new File(extractDir, relParentPath);
		String bundleName = "";
		
		if(bundleFile.getName().indexOf("_")>0){
		  bundleName = bundleFile.getName().substring(0, bundleFile.getName().indexOf("_"));
		}else if (bundleFile.getName().indexOf("-")>0){
			bundleName= bundleFile.getName().substring(0, bundleFile.getName().indexOf("-"));
		}else{
			bundleName = bundleFile.getName().substring(0, bundleFile.getName().lastIndexOf("."));
		}
		
		logStream.Both("bundleName =" + bundleName);
		if(pluginDir !=null && pluginDir.exists()){
			File[] list = pluginDir.listFiles();
			File temp, temp1;
			for(int i=0; i< list.length; i++ ){
				temp = list[i];
				if(temp!=null && temp.getName().contains(bundleName)){
					logStream.Both("bundle file Name= " + bundleFile.getName());
					logStream.Both("Rename to bundleName= " + temp.getName());					
					try {
						temp1 = new File(newearFolder, relParentPath + File.separator + temp.getName());
						FileUtil.copyDirectory(bundleFile, temp1);
						logStream.Both("Copy bundle Dir: " + bundleFile.getAbsolutePath() + " To: " + temp1.getAbsolutePath());	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	private void findFileByRegexp(File dir, String regexp, ArrayList<File> bundleFiles, File newearFolder){
		
		if(dir !=null && dir.isDirectory()){
			String path = dir.getAbsolutePath();
			String parent = dir.getParentFile().getName();
			path = path.replace(File.separatorChar, '/');
			boolean a = parent.equalsIgnoreCase("plugins");
			boolean b = ZipUtil.match(regexp, path);
			
			if(a && b){
				bundleFiles.add(dir);
				return ;
			}else{
				File[] fileList = dir.listFiles();
				File file;
				for(int j=0; j< fileList.length; j++){
		            file = fileList[j];
		            if(file.isDirectory()){
		         	   findFileByRegexp(file, regexp, bundleFiles, newearFolder); 
		            }else{
		        		String relPath = file.getAbsolutePath().substring(this.earFolder.getAbsolutePath().length()+1);
		        		logStream.Both("relParentPath= " + relPath);
		        		File newFile = new File(newearFolder, relPath);           	   
		            	try {
		            		if(file.exists())
		            		  logStream.Both("Copy File: " + file.getAbsolutePath() + " To: " + newFile.getAbsolutePath());
							FileUtil.copyFile(file, newFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logStream.Both("ERROR: " + e.getMessage());
							e.printStackTrace();
						}		            	
		            }
				}							
			}
		}
	}
	public File getProvisionFolder() {
		return provisionFolder;
	}

	public String getUpdatedEARFileList() {
		return updatedEARFileList;
	}

	public String getDeleteEARFileList() {
		return deleteEARFileList;
	}

	public String getProvisionFileList() {
		return provisionFileList;
	}
	public void setProvisionFileList(String provisionFileList) {
		this.provisionFileList = provisionFileList;
	}
	public boolean webResourceUpdate (){	
		return true;
	}

	public boolean isFullEarUpdate() {
		return isFullEarUpdate;
	}
}

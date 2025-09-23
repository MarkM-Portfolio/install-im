package com.ibm.connections.install;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */


import org.eclipse.swt.widgets.Composite;

import com.ibm.cic.agent.core.api.IAgentJob;
import com.ibm.cic.agent.core.api.IProfile;
//import com.ibm.cic.common.logging.Logger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.ILogger;
import org.eclipse.osgi.util.NLS;

public class findIMJobConsole extends BaseConfigConsolePanel{
	String className=findIMJob.class.getName();
	private IProfile profile = null; // profile to save data in
	private IAgentJob job = null;
	private static final String OfferingJob = "user.job";
    private static final String FEATURE_ID = "findIMJob";
	
    private final ILogger log=IMLogger.getLogger(com.ibm.connections.install.findIMJob.class);
	//private static final Logger log = Logger.getLogger(com.ibm.connections.install.findIMJob.class);



	public findIMJobConsole() {
		
		super("");
		log.info(className);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldSkip() {
		job=this.findJobAndOfferingId();
		profile=this.getProfile();
		if (profile != null){
			if (job.isUpdate()) {
				this.profile.setUserData(OfferingJob,"UPDATE");
				this.profile.setUserData("user.activities.new.add", isFeatureNewAdded("activities")?"true":"false");
				this.profile.setUserData("user.blogs.new.add", isFeatureNewAdded("blogs")?"true":"false");
				this.profile.setUserData("user.communities.new.add", isFeatureNewAdded("communities")?"true":"false");
				this.profile.setUserData("user.ccm.new.add", isFeatureNewAdded("ccm")?"true":"false");
				this.profile.setUserData("user.dogear.new.add", isFeatureNewAdded("dogear")?"true":"false");
				this.profile.setUserData("user.profiles.new.add", isFeatureNewAdded("profiles")?"true":"false");
				this.profile.setUserData("user.wikis.new.add", isFeatureNewAdded("wikis")?"true":"false");
				this.profile.setUserData("user.files.new.add", isFeatureNewAdded("files")?"true":"false");
				this.profile.setUserData("user.forums.new.add", isFeatureNewAdded("forums")?"true":"false");
				this.profile.setUserData("user.mobile.new.add", isFeatureNewAdded("mobile")?"true":"false");
				this.profile.setUserData("user.metrics.new.add", isFeatureNewAdded("metrics")?"true":"false");
				this.profile.setUserData("user.moderation.new.add", isFeatureNewAdded("moderation")?"true":"false");
			} else if(job.isInstall()) {
				this.profile.setUserData(OfferingJob,"INSTALL");
			} else if(job.isModify()) {
			    if(isNewFeatureAdded())
					this.profile.setUserData(OfferingJob,"MODIFY_ADD");
				else
					this.profile.setUserData(OfferingJob,"MODIFY_REMOVE");
			} else if(job.isUninstall())
				this.profile.setUserData(OfferingJob,"UNINSTALL");
			else if(job.isRollback())
				this.profile.setUserData(OfferingJob,"ROLLBACK");

			
			//log.info(NLS.bind(LogMessages.info_st_install_im_job, this.profile.getUserData(OfferingJob)));
		}
		
		return true;
	}

	
	@Override
	public String getFeatureId() {
		// TODO Auto-generated method stub
		return FEATURE_ID;
	}
	

}

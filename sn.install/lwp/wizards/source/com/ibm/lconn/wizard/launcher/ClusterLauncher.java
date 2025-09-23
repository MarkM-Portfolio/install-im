/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.launcher;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.cluster.ui.ClusterWizard;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.interfaces.PageController;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.LCWizardDialog;
import com.ibm.lconn.wizard.common.ui.ResourcePool;

public class ClusterLauncher {
	public static void main(String[] args) {
		TestDataOffer.setLocale();
		if(args!=null && args.length>0 && "-silent".equals(args[0])){
			if(args.length==1){
				ConsoleInstallerParser.printUsage(Constants.WIZARD_ID_TDIPOPULATE);
				System.exit(Constants.EXIT_STATUS_PARAMETER_ERROR);
			}
			String[] arg = new String[args.length-1];
			for (int i = 0; i < arg.length; i++) {
				arg[i] = args[i+1];
			}
			TDIConsoleLauncher.main(arg);
		}
		
		new Shell(ResourcePool.getDisplay());
		ClusterWizard wizard = new ClusterWizard("cluster");
		setWizard(wizard);
		LCWizardDialog dialog = new LCWizardDialog(null, wizard);
		wizard.setParentDialog(dialog);
		dialog.setInitialSize(800, 600);
		dialog.open();
	}

//	/**
//	 * Set the controllers of the wizard here. 
//	 * {@link PageController}
//	 * 
//	 * @param wizard
//	 */
	private static void setWizard(ClusterWizard wizard) {
//		wizard.setForcePreviousAndNextButtons(false);
		PageController pageController = wizard.getData().getPageController();
		if(pageController!=null)
			wizard.setPageController(pageController);
	}
}

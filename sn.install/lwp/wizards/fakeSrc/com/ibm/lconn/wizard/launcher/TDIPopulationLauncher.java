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

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.interfaces.PageController;
import com.ibm.lconn.wizard.common.interfaces.ValueListener;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.LCWizardDialog;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.tdipopulate.ui.TDIPopulationWizard;

public class TDIPopulationLauncher {
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
		TDIPopulationWizard wizard = new TDIPopulationWizard(Constants.WIZARD_ID_TDIPOPULATE);
		setWizard(wizard);
		LCWizardDialog dialog = new LCWizardDialog(null, wizard);
		wizard.setParentDialog(dialog);
		dialog.setInitialSize(800, 600);
		dialog.open();
	}

	/**
	 * Set the controllers of the wizard here. 
	 * {@link PageController}
	 * 
	 * @param wizard
	 */
	private static void setWizard(TDIPopulationWizard wizard) {
		DataPool.addValueListener(Constants.WIZARD_ID_TDIPOPULATE, new ValueListener(){
			public String valueChanged(String input, String oldValue,
					String newValue) {
				if(CommonHelper.equals(Constants.INPUT_TDI_OPTIONAL_TASK, input)){
					String wizardId = Constants.WIZARD_ID_TDIPOPULATE;
					boolean markManagerCheck = Constants.OPTION_MARKMANGER_YES.equals(DataPool.getValue(wizardId , Constants.INPUT_TDI_MARK_MANGER_CHECK));
					if(markManagerCheck){
						newValue = Util.addSelection(newValue, Constants.LDAP_OPTIONAL_TASK_MARK_MANAGER);
						return newValue;
					}
				}else if(CommonHelper.equals(Constants.INPUT_TDI_MARK_MANGER_CHECK, input)){
					String wizardId = Constants.WIZARD_ID_TDIPOPULATE;
					boolean markManagerCheck = Constants.OPTION_MARKMANGER_YES.equals(newValue);
					if(markManagerCheck){
						String value = DataPool.getValue(wizardId, Constants.INPUT_TDI_OPTIONAL_TASK);
						if(-1==Util.indexOf(value, Constants.LDAP_OPTIONAL_TASK_MARK_MANAGER)){
							value = Util.addSelection(value, Constants.LDAP_OPTIONAL_TASK_MARK_MANAGER);
							DataPool.setValue(wizardId, Constants.INPUT_TDI_OPTIONAL_TASK, value);
						}
					}
				}
				return newValue;
			}});
		wizard.setForcePreviousAndNextButtons(false);
		PageController pageController = wizard.getData().getPageController();
		if(pageController!=null)
			wizard.setPageController(pageController);
	}
}

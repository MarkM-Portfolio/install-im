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
package com.ibm.lconn.wizard.common.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.WelcomeWizardPage;

public class CommonHelper {
	public static final Logger logger = LogUtil.getLogger(CommonHelper.class);
	public static Listener scrollListener;
	private static final String EMPTY_STRING = "";
	private static SystemEnv env = new SystemEnv();

	public static boolean validatorEnabled() {
		return CommonHelper.equals(MessageUtil.getSetting("global.enableValidator"), Constants.BOOL_TRUE);
	}

	public static boolean isConsoleMode() {
		return Constants.LAUNCH_MODE_CONSOLE.equals(DataPool.getValue(Constants.WIZARD_ID_TDIPOPULATE, Constants.WIZARD_LAUNCH_MODE));
	}

	public static void setConsoleMode(boolean enable) {
		String launchModeConsole = (enable ? Constants.LAUNCH_MODE_CONSOLE : "");
		DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE, Constants.WIZARD_LAUNCH_MODE, launchModeConsole);
	}

	public static void setVariable(IWizard wizard, String section, String vName, String vValue) {
		if (wizard == null || getDialogSettings(wizard) == null) {
			env.setVariable(section, vName, vValue);
			return;
		}
		IDialogSettings dialogSettings = getDialogSettings(wizard);
		IDialogSettings section2 = dialogSettings.getSection(section);
		if (section2 == null)
			section2 = dialogSettings.addNewSection(section);
		section2.put(vName, vValue);

	}

	public static void setObject(String section, String vName, Object object) {

		env.setVariableObject(section, vName, object);
	}

	public static <T extends Control> T createScrollableControl(Class<T> clazz, Composite parent, int style, int scrollStyle) {
		try {
			final ScrolledComposite sc = new ScrolledComposite(parent, scrollStyle);
			final T control = clazz.getConstructor(Composite.class, int.class).newInstance(sc, style);
			sc.setLayoutData(new GridData(GridData.FILL_BOTH));
			FillLayout layout = new FillLayout();
			sc.setLayout(layout);
			sc.setExpandVertical(true);
			sc.setExpandHorizontal(true);
			sc.setContent(control);

			sc.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					Rectangle rectangle = sc.getClientArea();
					sc.setMinSize(control.computeSize(rectangle.width, SWT.DEFAULT));
				}
			});

			scrollListener = new Listener() {
				public void handleEvent(Event e) {
					Control child = (Control) e.widget;
					Rectangle bounds = child.getBounds();
					Rectangle area = sc.getClientArea();
					Point origin = sc.getOrigin();
					if (origin.x > bounds.x)
						origin.x = Math.max(0, bounds.x);
					if (origin.y > bounds.y)
						origin.y = Math.max(0, bounds.y);
					if (origin.x + area.width < bounds.x + bounds.width)
						origin.x = Math.max(0, bounds.x + bounds.width - area.width);
					if (origin.y + area.height < bounds.y + bounds.height)
						origin.y = Math.max(0, bounds.y + bounds.height - area.height);
					sc.setOrigin(origin);
				}
			};

			return control;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T extends Control> T createHVScrollableControl(Class<T> clazz, Composite parent, int style, int scrollStyle) {
		try {
			final ScrolledComposite sc = new ScrolledComposite(parent, scrollStyle);
			final T control = clazz.getConstructor(Composite.class, int.class).newInstance(sc, style);
			sc.setLayoutData(new GridData(GridData.FILL_BOTH));
			FillLayout layout = new FillLayout();
			sc.setLayout(layout);
			sc.setExpandVertical(true);
			sc.setExpandHorizontal(true);
			sc.setContent(control);

			sc.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					// Rectangle rectangle = sc.getClientArea();
					sc.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				}
			});
			return control;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getObject(String section, String vName) {

		return env.getVariableObject(section, vName);
	}

	public static void setColumn(Composite container, int numColumns) {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = numColumns;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		container.setLayout(gridLayout);
	}

	private static IDialogSettings getDialogSettings(IWizard wizard) {
		IDialogSettings dialogSettings = wizard.getDialogSettings();
		return dialogSettings;
	}

	public static boolean isStyle(int style, int SWT_STYLE) {
		return (style & SWT_STYLE) != 0;
	}

	public static String getVariable(IWizard wizard, String section, String vName) {
		if (wizard == null || getDialogSettings(wizard) == null)
			return env.getVariable(section, vName);
		IDialogSettings dialogSettings = getDialogSettings(wizard);
		IDialogSettings section2 = dialogSettings.getSection(section);
		if (section2 == null)
			section2 = dialogSettings.addNewSection(section);
		String vValue = section2.get(vName);
		if (vValue == null)
			return EMPTY_STRING;
		return vValue;
	}

	public static Composite createEmptyPanel(Composite container, GridData gridData) {
		Composite con = new Composite(container, SWT.NONE);
		con.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		con.setLayout(gridLayout);
		return con;
	}

	public static Composite createEmptyPanel(Composite container, GridData gridData, int numColumn) {
		Composite con = new Composite(container, SWT.NONE);
		con.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.numColumns = numColumn;
		con.setLayout(gridLayout);
		return con;
	}

	public static String getPlatformType() {
		String os = System.getProperty("os.name");
		os = os.toLowerCase();
		if (os.indexOf("win") != -1)
			return Constants.OS_WINDOWS;
		if (os.indexOf("aix") != -1)
			return Constants.OS_AIX;
		if (os.indexOf("redhat") != -1)
			return Constants.OS_LINUX_REDHAT;
		if (os.indexOf("suse") != -1)
			return Constants.OS_LINUX_SUSE;
		return Constants.OS_LINUX_REDHAT;
	}
	public static String getPlatformTypeForJRE() {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch"); 
		os = os.toLowerCase();
		arch = arch.toLowerCase();
		if (os.indexOf("win") != -1)
			return Constants.OS_WINDOWS;
		if (os.indexOf("aix") != -1)
			return Constants.OS_AIX;
		if (os.indexOf("redhat") != -1)
			return Constants.OS_LINUX_REDHAT;
		if (os.indexOf("suse") != -1)
			return Constants.OS_LINUX_SUSE;
		if(arch.indexOf("s390") != -1) {
			return Constants.OS_ZLINUX_S390;
		}
		return Constants.OS_LINUX_REDHAT;
	}

	public static String getPlatformShortType() {
		String os = System.getProperty("os.name");
		os = os.toLowerCase();
		if (os.indexOf("win") != -1)
			return Constants.OS_WINDOWS;
		if (os.indexOf("aix") != -1)
			return Constants.OS_LINUX;
		if (os.indexOf("redhat") != -1)
			return Constants.OS_LINUX;
		if (os.indexOf("suse") != -1)
			return Constants.OS_LINUX;
		return Constants.OS_LINUX;
	}

	public static boolean isTrue(String isRemoteDatabase) {
		return Constants.BOOL_TRUE.equalsIgnoreCase(isRemoteDatabase);
	}

	public static String getBoolString(boolean bool) {
		return (bool ? Constants.BOOL_TRUE : Constants.BOOL_FALSE);
	}

	public static boolean isEmpty(String str) {
		return str == null || EMPTY_STRING.equalsIgnoreCase(str);
	}

	public static void setWidgetID(Widget widget, String id) {
		widget.setData(Constants.ID_UI, id);
	}

	public static String getWidgetID(Widget widget) {
		if (widget == null)
			return null;
		Object data = widget.getData(Constants.ID_UI);
		return (data == null ? null : (String) data);
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null)
			return false;
		return o1.equals(o2);
	}

	public static String getFeatureDatabase(String dbType, String feature) {
		return Constants.featureDBMapping.get(dbType).get(feature);
	}

	public static void resetButtonText() {
		Constants.BUTTON_TEXT_FINISH = Messages.getString("button.finish.text");
		Constants.BUTTON_TEXT_NEXT = Messages.getString("button.next.text");
		Constants.BUTTON_TEXT_BACK = Messages.getString("button.prev.text");
		Constants.BUTTON_TEXT_CANCEL = Messages.getString("button.cancel.text");
		Constants.BUTTON_TEXT_OK = Messages.getString("button.OK.text");
		Constants.BUTTON_TEXT_YES = Messages.getString("button.YES.text");
		Constants.BUTTON_TEXT_NO = Messages.getString("button.NO.text");
		Constants.BUTTON_TEXT_CONFIG = Messages.getString("button.config.text");
	}

	private static Shell logDialog;
	private static Text logDialogText;
	
	public static boolean openLog(String logPath) {
		logger.info("open log");
	
		logDialog = new Shell(Display.getCurrent(), SWT.MIN | SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE);
		Image iconImage = ResourcePool.getImage(WelcomeWizardPage.class, "/icons/icon.GIF");

		logDialog.setImage(iconImage);
		logDialog.setText(logPath);
		logDialog.setLayout(new FillLayout());

		logDialogText = new Text(logDialog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		final Text text = logDialogText;
		text.setEditable(false);
		if (CommonHelper.getTextBackgroundColor() != null)
			text.setBackground(getTextBackgroundColor());

		logger.info("file="+logPath);
		File log = new File(logPath);
		if (!log.canRead()) {
			logger.info("log can not be read.");
			return false; // return false;
		}
		BufferedReader logReader = null;
		try {
			InputStreamReader read = new InputStreamReader (new FileInputStream(log),"UTF-8");
			logReader =new BufferedReader(read);
		} catch (FileNotFoundException e) {
			logger.info(e.toString());
		} catch (UnsupportedEncodingException e) {
			logger.info(e.toString());
		}
		if (logReader == null)
			return false; // return false;

		String line = null;
		try {
			while ((line = logReader.readLine()) != null) {
				text.append(line + Constants.CRLF);
			}
		} catch (IOException e) {
			logger.info("e="+e.toString());
			return false;// return false;
		}
		text.setSelection(0);

		logDialog.setSize(800, 600);
		logDialog.addListener(SWT.Close, new Listener(){

			public void handleEvent(Event arg0) {
				logDialog = null;				
			}
			
		});
		logDialog.open();
		return true;
	}
	
	public static void logDialogAppend(String line) {
		if (logDialog != null ) {
			logDialogText.append(Constants.CRLF);
			logDialogText.append(line);
		}
	}

	/*
	 * public static boolean launch(String ext, String filePath) { try { Program
	 * txtProgram = Program.findProgram(ext); if (txtProgram == null) return
	 * false; return txtProgram.execute(filePath); } catch (Exception e) {
	 * return false; } }
	 */

	public static boolean openHTML(String url) {
		Shell dialog = new Shell(SWT.MIN | SWT.MODELESS | SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE);
		Image iconImage = ResourcePool.getImage(WelcomeWizardPage.class, "/icons/icon.GIF");

		dialog.setImage(iconImage);
		dialog.setText(url);
		dialog.setLayout(new FillLayout());

		Browser browser = null;
		try {
			browser = new Browser(dialog, SWT.NONE);
		} catch (SWTError e) {
			/*
			 * The Browser widget throws an SWTError if it fails to instantiate
			 * properly. Application code should catch this SWTError and disable
			 * any feature requiring the Browser widget. Platform requirements
			 * for the SWT Browser widget are available from the SWT FAQ
			 * website.
			 */
		}
		if (browser == null)
			return false;

		browser.setUrl(url);

		dialog.open();
		return true;
	}

	public static Color parseColor(String bgColor) {
		if (CommonHelper.equals(bgColor, Constants.COLOR_WHITE)) {
			return ResourcePool.getColor(SWT.COLOR_WHITE);
		}
		return null;
	}

	public static int removeStyle(int style, int... needRemove) {
		for (int i = 0; i < needRemove.length; i++) {
			style &= ~needRemove[i];
		}
		return style;
	}

	private static Color textBackgroundColor = null;

	public static Color getTextBackgroundColor() {
		return textBackgroundColor;
	}

	public static void setTextBackgroundColor(Color color) {
		textBackgroundColor = color;
	}
	public static String getSqlFileName(List<String> command, String dbType){
		StringBuilder sb = new StringBuilder();
		int indexStart = 0;
		int indexEnd = 0;
		for(String cmd : command){
			sb.append(cmd).append(" ");
		}
		
		if(sb.toString().indexOf("-classpath") == -1){
			if(Constants.DB_DB2.equals(dbType)){
				indexStart = sb.toString().lastIndexOf(File.separator + Constants.DB_DB2 + File.separator) + Constants.DB_DB2.length() + 2;
			}
			if(Constants.DB_ORACLE.equals(dbType)){
				indexStart = sb.toString().lastIndexOf(File.separator + Constants.DB_ORACLE + File.separator) + Constants.DB_ORACLE.length() + 2;
			}
			if(Constants.DB_SQLSERVER.equals(dbType)){
				indexStart = sb.toString().lastIndexOf(File.separator + Constants.DB_SQLSERVER + File.separator) + Constants.DB_SQLSERVER.length() + 2;
			}
			indexEnd = sb.toString().lastIndexOf(".sql");
		}else{
			indexStart = sb.toString().lastIndexOf("migrate.jar");
			indexEnd = sb.toString().lastIndexOf("migrate.jar") + "migrate".length();
		}
		String resultStr = sb.toString().substring(indexStart,indexEnd);
		if( (sb.toString().indexOf("communities") != -1 && sb.toString().indexOf("forum") != -1) ||
				(sb.toString().indexOf("migrate.jar") != -1 && sb.toString().indexOf("forum") != -1)){
			resultStr = "forum_" + resultStr;
		}
		if(sb.toString().indexOf("news.migrate.jar") != -1){
			if(sb.toString().indexOf("NewsMigration") != -1){
				resultStr = "news_" + resultStr;
			}
			if(sb.toString().indexOf("EmailDigestMigration") != -1){
				resultStr = "news_email_" + resultStr;
			}
		}
		return resultStr;
	}
	public static String uCase(String str){
		String initial = str.substring(0,1);
		return initial.toUpperCase() + str.substring(1);
	}
	public static String getCommandStr(List<String> command, DBWizard wizard){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < command.size(); i++){
			String cmd = command.get(i);
			sb.append(cmd).append(" ");
			if (cmd.equals("-dbpassword")){
				sb.append("******").append(" ");
				i++;
			}
			if (cmd.equals("-dbpassword_source")){
				sb.append("******").append(" ");
				i++;
			}
		}
		sb.deleteCharAt(sb.length()-1);
		String filepath_tmp = "";
		if(sb.toString().indexOf("connections") !=-1 && sb.toString().indexOf(".sql") !=-1){
			filepath_tmp = sb.toString().substring(sb.toString().indexOf("connections"),sb.toString().lastIndexOf(".sql")+".sql".length());
		}
		final String filepath = filepath_tmp;
		final String textStrCon = sb.toString();
		String textStr = sb.toString();
		if(Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(wizard)) 
				&& ((Constants.OPERATION_TYPE_CREATE_DB.equals(DBWizardInputs.getActionId(wizard))
				&& textStr.indexOf("-classpath") == -1)
				||
				(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(wizard)) && DBWizardInputs.getFeatures(wizard).contains(Constants.FEATURE_HOMEPAGE))
				)){
			if (-1 != textStr.indexOf("password=")){
				textStr = textStr.replace(textStr.substring(textStr.indexOf("password=")),"password=\"******\"");
			}
		}
		if(Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(wizard)) 
				&& ((Constants.OPERATION_TYPE_CREATE_DB.equals(DBWizardInputs.getActionId(wizard)) 
				&& textStr.indexOf("-classpath") == -1)
				|| 
				(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(wizard))
					&& textStr.indexOf("calendar")!=-1 && textStr.indexOf("createDb")!=-1	))){
			textStr = textStr.substring(0,textStr.lastIndexOf(".sql") + ".sql".length()) + " ******";
		}
		if(textStr.indexOf("-classpath") != -1){
			String tmpStr = textStr.substring(textStr.indexOf("-classpath") + "-classpath".length() + 1, textStr.indexOf("migrate.jar") + "migrate.jar".length());
			textStr = textStr.replace(tmpStr, "\"" + tmpStr + "\"");
//			if(textStr.indexOf("-src") == -1){
//				if(textStr.indexOf("-dbpassword") != -1){
//				textStr = textStr.replace(textStr.substring(textStr.indexOf("-dbpassword") + "-dbpassword".length() + 1),"-dbpassword ******");	
//				}
//			}else{
//				if(textStr.indexOf("-dbpassword") != -1){
//				textStr = textStr.replace(textStr.substring(textStr.indexOf("-dbpassword") + "-dbpassword".length() + 1,textStr.indexOf("-src") - 1),"******");
//				}
//			}
//			if(textStr.indexOf("-srcdbpassword") != -1){
//				textStr = textStr.replace(textStr.substring(textStr.indexOf("-srcdbpassword")),"-srcdbpassword ******");	
//			}
//			if (textStr.indexOf("-dbpassword_source") != -1){
//				textStr = textStr.replace(textStr.substring(textStr.indexOf("-dbpassword_source") + "-dbpassword_source".length() + 1),"-dbpassword_source ******");	
//			}
			if(textStr.indexOf("MigrateCommunityTheme_2_5_To_3_0") != -1){
				if(!Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(wizard))){
				tmpStr = textStr.substring(textStr.indexOf("-source"), textStr.indexOf("-target") - 1);
				textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
				tmpStr = textStr.substring(textStr.indexOf("-target"));
				textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
				}else{
					tmpStr = textStr.substring(textStr.indexOf("-source"), textStr.indexOf("-SID") - 1);
					textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
					tmpStr = textStr.substring(textStr.indexOf("-target"), textStr.lastIndexOf("-SID") - 1);
					textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
				}
			}
		}
		textStr = textStr.replace("\\\\", "\\");
		return textStr;
	}
}

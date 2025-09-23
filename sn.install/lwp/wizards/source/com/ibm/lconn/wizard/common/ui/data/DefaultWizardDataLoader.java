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
package com.ibm.lconn.wizard.common.ui.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.wizard.cluster.ui.ClusterPageController;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Entry;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.interfaces.PageController;
import com.ibm.lconn.wizard.common.interfaces.TDIPopulatePageController;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.LCWizardPage;
import com.ibm.lconn.wizard.common.ui.ext.LCCheck;
import com.ibm.lconn.wizard.common.ui.ext.LCDirBrowser;
import com.ibm.lconn.wizard.common.ui.ext.LCDropdown;
import com.ibm.lconn.wizard.common.ui.ext.LCFileBrowser;
import com.ibm.lconn.wizard.common.ui.ext.LCGroup;
import com.ibm.lconn.wizard.common.ui.ext.LCLabel;
import com.ibm.lconn.wizard.common.ui.ext.LCTable;
import com.ibm.lconn.wizard.common.ui.ext.LCText;
import com.ibm.lconn.wizard.common.ui.ext.LCWizardInput;
import com.ibm.lconn.wizard.tdipopulate.backend.AttributeMapping;

/**
 * @author Jun Jing Zhang
 * 
 */
public class DefaultWizardDataLoader {

	private static final String TAG_CONTROLLER = "controller";
	private static final String TAG_EXT_FILTER = "extFilter";
	private static final String TAG_VALIATORS = "validators";
	private static final String TAG_ROW_DATAS = "rowDatas";
	private static final String TAG_ROW_COUNT = "rowCount";
	private static final String TAG_ID = "id";
	private static final String TAG_PARAMETERS = "parameters";
	private static final String TAG_WIZARD_PAGE_TITLE = "WIZARD_PAGE.title";
	private static final String TAG_WIZARD_PAGE_DESCRIPTION = "WIZARD_PAGE.description";
	private static final String TAG_TYPE = "type";
	private static final String TAG_CLASS = "class";
	private static final String TAG_OPTIONS = "options";
	private static final String TAG_DIALOGMSG = "DIALOGMSG";
	private static final String TAG_BUTTON = "BUTTON";
	public static final String ERROR_NO_CLASS_FOUND = "NoClassFound";
	private static final String TAG_PAGES = "pages";
	private static final String TAG_VALUES = "values";
	private static final String TAG_DEFAULT_VALUE = "DEFAULTVALUE";
	private static final String TAG_COLUMNS = "columns";
	private static final String TAG_STYLE = "style";
	private static final String TAG_COLOR_BACKGROUND = "color.bg";
	private static Logger logger = LogUtil
			.getLogger(DefaultWizardDataLoader.class);

	public static void initWizardPage(WizardPageData page) {
		String id = page.getId();
		String wizardId = page.getWizardId();
		boolean isWelcome = CommonHelper.equals(wizardId + "."
				+ Constants.WIZARD_PAGE_COMMON_WELCOME, id);
		String pageKey = page.getWizardId() + MessageUtil.DOT + id;
		String pageName = MessageUtil.getSetting(pageKey, TAG_ID);
		String className = MessageUtil.getSetting(pageKey, TAG_CLASS);
		String parameters = MessageUtil.getSetting(pageKey, TAG_PARAMETERS);

		String title = MessageUtil.getMsg(TAG_WIZARD_PAGE_TITLE, id);
		String desc = MessageUtil.getMsg(TAG_WIZARD_PAGE_DESCRIPTION, id);

		page.setClassName(className);
		page.setName(pageName);
		page.setInputsId(Util.delimStr(parameters));
		page.setTitle(title);
		page.setDesc(desc);
		DataPool.setWizardPage(wizardId, id, page);

		if (isWelcome)
			initWelcome(page);

		loadValidator(page);
	}

	private static void loadValidator(WizardPageData page) {
		String wizardId = page.getWizardId();
		// WizardData wizardData = DataPool.getWizard(wizardId);
		String id = page.getId();
		String validatorStr = MessageUtil.getSetting(wizardId, id,
				TAG_VALIATORS);
		if (CommonHelper.isEmpty(validatorStr))
			return;
		String[] validatorStrs = Util.delimStr(validatorStr);
		for (String validator : validatorStrs) {
			String vClass = MessageUtil.getSetting(wizardId, id, validator,
					TAG_CLASS);
			String vParas = MessageUtil.getSetting(wizardId, id, validator,
					TAG_PARAMETERS);

			try {
				Validator valid = (Validator) loadObject(vClass,
						(Object[]) Util.delimStr(vParas));
				valid.setParameters(Util.delimStr(vParas));
				page.addValidator(valid);
			} catch (Exception e) {
				LogUtil.getLogger(DefaultWizardDataLoader.class).log(
						Level.SEVERE,
						ErrorMsg.getString(Constants.ERROR_VALIDATOR_LOAD_FAIL
								+ ".msg", vClass, vParas));
			}
		}

	}

	private static String evalClass(String classId) {
		String vClassDefine = MessageUtil.getSetting(TAG_CLASS, classId);
		if (!CommonHelper.isEmpty(vClassDefine))
			classId = vClassDefine;
		return classId;
	}

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(String className,
			Class<T> t, Class... para) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException {
		Class<T> loadClass = (Class<T>) ClassLoader.getSystemClassLoader()
				.loadClass(className);
		Constructor<T> constructor = loadClass.getConstructor(para);
		return constructor;
	}

	public static void initWizardPageInput(WizardPageInputData inputWidget) {
		String id = inputWidget.getId();
		String type = MessageUtil.getSetting(id, TAG_TYPE);
		String label = MessageUtil.getMsg(MessageUtil.TAG_LABEL, id);
		String defaultValue = getDefaultValue(id);
		String wizardId = inputWidget.getWizardId();
		String className = MessageUtil.getSetting(id, TAG_CLASS);
		String style = MessageUtil.getSetting(id, TAG_STYLE);
		String name = MessageUtil.getMsg(MessageUtil.TAG_NAME, id);
		String tooltip = MessageUtil.getMsg(MessageUtil.TAG_TOOLTIP, id);
		String bgColor = MessageUtil.getSetting(id, TAG_COLOR_BACKGROUND);
		
		inputWidget.setLabel(label);
		inputWidget.setDefaultValue(defaultValue);
		inputWidget.setType(type);
		inputWidget.setClassName(className);
		inputWidget.setStyle(parseStyle(style));
		inputWidget.setBgColor(bgColor);
		inputWidget.setName(name);
		inputWidget.setTooltip(tooltip);
		if (inputWidget.isGroup()) {
			String options = MessageUtil.getSetting(id, TAG_OPTIONS);
			String defaultSelected = getDefaultValue(id);
			inputWidget.setOptions(Util.delimStr(options));
			inputWidget.setDefaultValue(defaultSelected);
		} else if (inputWidget.isDirBrowser()) {
			String dirButtonTxt = MessageUtil.getMsg(MessageUtil.TAG_LABEL, id,
					TAG_BUTTON);
			String dirDialogMsg = MessageUtil.getMsg(MessageUtil.TAG_LABEL, id,
					TAG_DIALOGMSG);
			inputWidget.setValue(Constants.WIDGET_PROP_DIR_BROWSER_DIALOG_MSG,
					dirDialogMsg);
			inputWidget.setValue(Constants.WIDGET_PROP_DIR_BROWSER_BUTTON_TEXT,
					dirButtonTxt);
			if (inputWidget.isFileBrowser()) {
				String extFilter = MessageUtil.getSetting(id, TAG_EXT_FILTER);
				inputWidget.setValue(
						Constants.WIDGET_PROP_FILE_BROWSER_EXT_FILTER,
						extFilter);
			}
		}
		DataPool.setWizardPageInput(wizardId, id, inputWidget);
	}

	private static int parseStyle(String styleStr) {
		int style = SWT.NONE;
		if (styleStr != null) {
			if (styleStr.indexOf(Constants.STYLE_READ_ONLY) != -1) {
				style |= SWT.READ_ONLY;
			}
		}
		return style;
	}

	public static void initWizard(WizardData wizardData) {
		String id = wizardData.getId();
		String pages = MessageUtil.getSetting(id, TAG_PAGES);
		wizardData.setPages(Util.delimStr(pages));
		DataPool.setWizard(id, wizardData);
		if (CommonHelper.equals(MessageUtil
				.getSetting("global.enableValidator"), Constants.BOOL_TRUE)) {
			PageController controller = null;
			if(Constants.WIZARD_ID_TDIPOPULATE.equals(id)){
				controller = new TDIPopulatePageController(id);
			}
			if(Constants.WIZARD_ID_CLUSTER.equals(id)){
				controller = new ClusterPageController(id);
			}
			
			wizardData.setPageController(controller);
		}

	}

	private static void initWelcome(WizardPageData data) {
		String id = data.getId();
		String welImage = getDefaultValue(id, Constants.WELCOME_IMAGE);
		String infocenter = getDefaultValue(id, Constants.WELCOME_INFOCENTER);
		String info = getDefaultValue(id, Constants.WELCOME_INTRO);
		String licence = getDefaultValue(id, Constants.WELCOME_LICENCE);

		data.setValue(Constants.WELCOME_IMAGE, welImage);
		data.setValue(Constants.WELCOME_INFOCENTER, infocenter);
		data.setValue(Constants.WELCOME_INTRO, info);
		data.setValue(Constants.WELCOME_LICENCE, licence);
		data.setClassName(Constants.WELCOME_CLASS);
		data.setName(id);
	}

	private static String getDefaultValue(String... keys) {
		return getDefaultValue(MessageUtil.getKeyAll(keys));
	}

	public static void initWizardDropDown(WizardDropDownData wizardDropDown) {
		String id = wizardDropDown.getId();
		String valuesStr = MessageUtil.getSetting(id, TAG_VALUES);
		String defaultValue = getDefaultValue(id);
		String[] values = Util.delimStr(valuesStr);
		String[] labels = new String[values.length];
		for (int i = 0; i < labels.length; i++) {
			String label = getDefaultValue(values[i]);
			if (CommonHelper.isEmpty(label)) {
				label = values[i];
			} else {
				labels[i] = label;
			}
		}
		wizardDropDown.setValues(values);
		wizardDropDown.setLabels(labels);
		wizardDropDown.setSelectedValue(defaultValue);
	}

	public static void initWizardTableExtra(WizardTableData wizardTableData) {
		String id = wizardTableData.getId();
		String columnStr = MessageUtil.getSetting(id, TAG_COLUMNS);
		String[] columns = Util.delimStr(columnStr);
		wizardTableData.setColumns(columns);

		for (int i = 0; i < columns.length; i++) {
			String col = columns[i];

			String colPrefix = id + "." + col;
			colPrefix = col;

			// The columnId.rowDatas will override the columnId.0, columnId.1
			String colData = getDefaultValue(colPrefix + "." + TAG_ROW_DATAS);
			int rowCount = -1;
			if (!CommonHelper.isEmpty(colData)) {
				String[] colDatas = Util.delimStr(colData);
				for (int j = 0; j < colDatas.length; j++) {
					String fieldId = col + "." + j;
					wizardTableData.setValue(fieldId, colDatas[j]);

				}
				rowCount = colDatas.length;

			} else {
				String rowCountStr = MessageUtil.getSetting(id, TAG_ROW_COUNT);
				rowCount = Integer.valueOf(rowCountStr);
				for (int j = 0; j < rowCount; j++) {
					String fieldId = col + "." + j;
					String value = getDefaultValue(fieldId);
					wizardTableData.setValue(fieldId, value);
				}
			}
			wizardTableData.setRowCount(rowCount);
		}

	}

	private static String getDefaultValue(String inputId) {
		String value = Constants.TEXT_EMPTY_STRING;
		String fieldValueInMsg = MessageUtil.getMsg(TAG_DEFAULT_VALUE, inputId);
		String fieldValueInSetting = MessageUtil.getSetting(TAG_DEFAULT_VALUE,
				inputId);
		if (!CommonHelper.isEmpty(fieldValueInMsg)) {
			value = fieldValueInMsg;
		} else if (!CommonHelper.isEmpty(fieldValueInSetting)) {
			value = fieldValueInSetting;
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public static Composite createWidget(Composite parent, String wizardId,
			String[] inputsId) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (inputsId != null) {
			for (String inputId : inputsId) {
				createSingleWidget(wizardId, container, inputId);
			}
		}

		return container;
	}

	public static Properties collectMappingData() {
		WizardTableData mappingTableData = (WizardTableData) DataPool
				.getWizardPageInput(Constants.WIZARD_ID_TDIPOPULATE,
						"INPUT_TDI_LDAP_MAPPING_TABLE");
		String[] rowNames = mappingTableData.getColumnValues(0);
		Properties mappingData = mappingTableData.getUserInput(rowNames, 1);
		return mappingData;
	}

	public static Properties collectValidationData() {
		WizardTableData mappingTableData = (WizardTableData) DataPool
				.getWizardPageInput(Constants.WIZARD_ID_TDIPOPULATE,
						"INPUT_TDI_LDAP_MAPPING_TABLE");
		String[] rowNames = mappingTableData.getColumnValues(0);
		Properties mappingData = mappingTableData.getUserInput(rowNames, 2);
		return mappingData;
	}

	@SuppressWarnings("unchecked")
	public static Properties loadMappingData() {
		List<AttributeMapping> tableInput = (List<AttributeMapping>) DataPool
				.getComplexData(Constants.WIZARD_ID_TDIPOPULATE,
						Constants.INPUT_TDI_MAPPING_TABLE);
		Properties props = new Properties();
		if(tableInput==null) return props;
		for (AttributeMapping mapping : tableInput) {
			props.setProperty(mapping.getDbField(), mapping.getAttribute());
		}
		return props;
	}

	public static LCWizardPage createWizardPage(WizardPageData pageData) {
		String className = pageData.getClassName();
		if (!CommonHelper.isEmpty(className)) {
			return (LCWizardPage) loadObject(className, pageData);
		}
		return new LCWizardPage(pageData);
	}

	private static void createSingleWidget(String wizardId,
			Composite container, String inputId) {
		LCWizardInput createdInput = null;
		WizardPageInputData input = DataPool.getWizardPageInput(wizardId,
				inputId);
		String className = input.getClassName();
		if (!CommonHelper.isEmpty(className)) {
			createdInput = (LCWizardInput) loadObject(className, container, input);

		} else if(input.isLabel()){
			createdInput = new LCLabel(container, input);
		}else if (input.isText()) {
			if (input.isPassword())
				createdInput = new LCText(container, input, SWT.PASSWORD);
			else
				createdInput = new LCText(container, input);
		} else if (input.isTextArea()) {
			createdInput = new LCText(container, input, SWT.WRAP | SWT.V_SCROLL
					| SWT.READ_ONLY);
		} else if (input.isGroup()) {
			int style = SWT.RADIO;
			if (input.isCheckGroup()) {
				style = SWT.CHECK;
			}
			createdInput = new LCGroup(container, input, style);
			String controller = MessageUtil.getSetting(inputId, TAG_CONTROLLER);
			LCGroupController loadObject = (LCGroupController) loadObject(controller, new Object[]{});
			if(loadObject!=null) ((LCGroup)createdInput).setController(loadObject);
		} else if (input.isCheck()) {
			createdInput = new LCCheck(container, input);
		} else if (input.isDirBrowser()) {
			if (input.isFileBrowser()) {
				createdInput = new LCFileBrowser(container, input);
			} else
				createdInput = new LCDirBrowser(container, input);
		} else if (input.isTable()) {
			createdInput = new LCTable(container, (WizardTableData) input);
		} else if (input.isDropDown()) {
			createdInput = new LCDropdown(container, input, input.getStyle());
		}
		DataPool.setWizardInputWidget(wizardId, inputId, createdInput);
	}

	@SuppressWarnings("unchecked")
	private static Object loadObject(String className, Object... parameters) {
		if(CommonHelper.isEmpty(className))return null;
		try {
			if (evalClass(className) != null)
				className = evalClass(className);
			Class loadClass = ClassLoader.getSystemClassLoader().loadClass(
					className);
			Constructor[] constructors = loadClass.getConstructors();
			for (Constructor constructor : constructors) {
				Class[] parameterTypes = constructor.getParameterTypes();
				if (sameClassTypes(parameterTypes, parameters)) {
					return constructor.newInstance(parameters);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.log(DefaultWizardDataLoader.class, Level.SEVERE, ErrorMsg
					.getString(ERROR_NO_CLASS_FOUND, className), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static boolean sameClassTypes(Class[] parameterTypes,
			Object... parameters) {
		if (parameterTypes.length == parameters.length) {
			for (int i = 0; i < parameterTypes.length; i++) {
				if (parameters[i].getClass() != parameterTypes[i]
						&& !isSuperClass(parameterTypes[i], parameters[i]
								.getClass())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static boolean isSuperClass(Class superClass, Class cl) {
		if (superClass == Object.class)
			return false;
		if (superClass == cl)
			return true;
		return isSuperClass(superClass, cl.getSuperclass());
	}

	public static void addInput(String wizardId, String id) {
		WizardPageInputData inputWidget = null;
		String type = MessageUtil.getSetting(id, TAG_TYPE);
		String label = MessageUtil.getMsg(MessageUtil.TAG_LABEL, id);
		String defaultValue = getDefaultValue(id);
		String className = MessageUtil.getSetting(id, TAG_CLASS);

		if (CommonHelper.equals(type, Constants.WIDGET_TYPE_TABLE)) {
			inputWidget = new WizardTableData(wizardId, id);
		} else {
			inputWidget = new WizardPageInputData(wizardId, id);
		}
		inputWidget.setLabel(label);
		inputWidget.setDefaultValue(defaultValue);
		inputWidget.setType(type);
		inputWidget.setClassName(className);
		if (inputWidget.isGroup()) {
			String options = MessageUtil.getSetting(id, TAG_OPTIONS);
			String defaultSelected = getDefaultValue(id);
			inputWidget.setOptions(Util.delimStr(options));
			inputWidget.setDefaultValue(defaultSelected);
		} else if (inputWidget.isDirBrowser()) {
			String dirButtonTxt = MessageUtil.getMsg(MessageUtil.TAG_LABEL, id,
					TAG_BUTTON);
			String dirDialogMsg = MessageUtil.getMsg(MessageUtil.TAG_LABEL, id,
					TAG_DIALOGMSG);
			inputWidget.setValue(Constants.WIDGET_PROP_DIR_BROWSER_DIALOG_MSG,
					dirDialogMsg);
			inputWidget.setValue(Constants.WIDGET_PROP_DIR_BROWSER_BUTTON_TEXT,
					dirButtonTxt);
		} else if (inputWidget.isTable()) {
			initWizardTableExtra((WizardTableData) inputWidget);
		}
		DataPool.setWizardPageInput(wizardId, id, inputWidget);
	}

	public static void collectPageInput(LCWizardPage page) {
		WizardPageData data = page.getData();
		String[] inputsId = data.getInputsId();
		String wizardId = data.getWizardId();
		if (inputsId != null) {
			for (String inputId : inputsId) {
				LCWizardInput input = DataPool
						.getWizardInputWidget(wizardId, inputId);
				if (input == null)
					continue;
				String value = input.getValue();
				WizardPageInputData inputData = input.getData();
				if (inputData.isRadio()) {
					value = evalValue(value);
				} else if (inputData.isCheckGroup()) {
					String[] values = Util.delimStr(value);
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < values.length; i++) {
						String evalValue = evalValue(values[i]);
						if (i == 0)
							sb.append(evalValue);
						else
							sb.append("," + evalValue);
					}
					value = sb.toString();
				}
				DataPool.setValueWithDirtyFlag(wizardId, inputId, value);
			}
		}
	}

	public static String evalValue(String value) {
		String evalValue = MessageUtil.getSetting("VALUE", value);
		if (!CommonHelper.isEmpty(evalValue))
			value = evalValue;
		return value;
	}

	public static List<Entry<String, String>> collectTaskInput(String wizard,
			String taskName) {
		String keysStr = MessageUtil.getSetting("execution", taskName,
				"parameterKeys");
		String[] keys = Util.delimStr(keysStr);
		List<Entry<String, String>> resultPros = new ArrayList<Entry<String, String>>();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String valueKey = getMapKey(key);
			String value = DataPool.getValue(wizard, valueKey);
			logger.log(Level.FINEST, "collectTaskInput<<"+ key+":[" + valueKey + "=" + value + "]");
			Entry<String, String> entry = new Entry<String, String>(key, value);
			resultPros.add(entry);
		}
		return resultPros;
	}

	public static String getMapKey(String key) {
		return MessageUtil.getSetting("KEYMAPPING", key);
	}

	public static Properties list2Properties(List<Entry<String, String>> enties) {
		Properties props = new Properties();
		Iterator<Entry<String, String>> iterator = enties.iterator();
		while (iterator.hasNext()) {
			Entry<String, String> next = iterator.next();
			props.put(next.getKey(), next.getValue());
		}
		return props;
	}

	public static void saveSession(String wizard, String taskName) {
		saveUserinput(wizard, taskName);
		if (CommonHelper.equals(wizard, Constants.WIZARD_ID_TDIPOPULATE)) {
			try {
				saveTDIMappingData();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private static void saveUserinput(String wizard, String taskName) {
		List<Entry<String, String>> collectTaskInput = collectTaskInput(wizard,
				taskName);
		String setting = MessageUtil.getSetting("execution", taskName,
				"parameterSaveExclude");
		String filePath = MessageUtil.getSetting("execution", taskName,
				"parameterFiles");
		File file = new File(filePath);
		file.getParentFile().mkdirs();
//		System.out.println(file.getAbsolutePath());
		String[] excludedProps = Util.delimStr(setting);
		for (String prop : excludedProps) {
			for (int i = 0; i < collectTaskInput.size(); i++) {
				Entry<String, String> entry = collectTaskInput.get(i);
				if (CommonHelper.equals(entry.getKey(), prop)) {
					collectTaskInput.remove(i);
					break;
				}
			}
		}
		try {
			list2Properties(collectTaskInput).store(
					new FileOutputStream(filePath), null);
		} catch (Exception e) {
			LogUtil.getLogger(DefaultWizardDataLoader.class).log(Level.WARNING,
					ErrorMsg.getString("LastSessionSaveFail", filePath), e);
		}
	}

	public static boolean hasLastSession(String wizard, String taskName) {
		String filePath = MessageUtil.getSetting("execution", taskName,
				"parameterFiles");
		File f = new File(filePath);
		return f.exists() && f.isFile();
	}

	public static void loadLastSession(String wizard, String taskName) {
		String filePath = MessageUtil.getSetting("execution", taskName,
				"parameterFiles");
		try {
			loadProperties(wizard, taskName, filePath);
		} catch (Exception e) {
			logger.log(Level.WARNING, ErrorMsg.getString(
					Constants.WARN_LOAD_SESSION_FAIL, filePath), e);
		}
	}

	public static void loadProperties(String wizard, String taskName,
			String filePath) throws FileNotFoundException, IOException {
		String keysStr = MessageUtil.getSetting("execution", taskName,
				"parameterKeys");
		DataPool.resetAll(wizard);
		if (CommonHelper.equals(wizard, Constants.WIZARD_ID_TDIPOPULATE)) {
			DataPool.setValue(wizard, Constants.INPUT_TDI_ACTION_TYPE,
					Constants.TDI_START_FROM_LAST_SESSION);
			loadTDIMappingData();
		}
		Properties sessionProps = new Properties();
		sessionProps.load(new FileInputStream(filePath));
		String[] keys = Util.delimStr(keysStr);
		logger.log(Level.INFO, "Loading last session... \n");
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String val = (String) sessionProps.get(key);
			if (CommonHelper.isEmpty(val))
				continue;
			String valueKey = getMapKey(key);
			logger.log(Level.INFO, valueKey + "=" + val);
			if (!CommonHelper.isEmpty(val)) {
				DataPool.setValue(wizard, valueKey, val);
			}
		}
		DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_OS_TYPE, CommonHelper.getPlatformType());
	}

	public static void loadNewSession(String wizardId) {
		DataPool.resetAll(wizardId);
	}

	@SuppressWarnings("unchecked")
	private static void saveTDIMappingData() {
		List<AttributeMapping> elements = (List<AttributeMapping>) DataPool
				.getComplexData(Constants.WIZARD_ID_TDIPOPULATE,
						Constants.INPUT_TDI_MAPPING_TABLE);
		Properties mappings = new Properties();
		Iterator<AttributeMapping> iterator = elements.iterator();
		while (iterator.hasNext()) {
			AttributeMapping next = iterator.next();
			String attribute = next.getAttribute();
			if (attribute == null)
				attribute = Constants.TEXT_EMPTY_STRING;
			mappings.setProperty(next.getDbField(), attribute);
		}
		try {
			mappings.store(new FileOutputStream(
					Constants.TDI_LAST_SESSION_MAPPING), "");
		} catch (Exception e) {
			logger.log(Level.WARNING, ErrorMsg
					.getString(Constants.WARN_SAVE_SESSION_FAIL), e);
		}
	}

	private static void loadTDIMappingData() {
		Properties mappings = new Properties();
		Properties vRules = new Properties();
		try {
			mappings.load(new FileInputStream(
					Constants.TDI_LAST_SESSION_MAPPING));
//			vRules.load(new FileInputStream(
//					Constants.TDI_LAST_SESSION_VALIDATION_RULE));
		} catch (IOException e) {
			logger.log(Level.WARNING, ErrorMsg
					.getString(Constants.WARN_LOAD_SESSION_FAIL), e);
			return;
		}
		List<AttributeMapping> m = new ArrayList<AttributeMapping>();
		Enumeration<Object> e = mappings.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String vRule = vRules.getProperty(key);
			String map = mappings.getProperty(key, "null");

			m.add(new AttributeMapping(key, map, vRule));
		}
		if(m.size()==0) return;
		Collections.sort(m);
		DataPool.setComplexData(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_TDI_MAPPING_TABLE, m);
	}

	public static Map<String, String> loadTDIEnv(String tdiHome) {
		Map<String, String> env = new HashMap<String, String>();
		env.put("TDIPATH", tdiHome);
		env.put("TDI_CS_HOST", "localhost");
		env.put("TDI_CS_PORT", "1527");
		return env;
	}

	public static String loadConsolePageSequence(String wizardId) {
		return MessageUtil.getSetting(wizardId, "console", "pages");
	}
}

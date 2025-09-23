/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;

public class ResourcePool {
	private static final int MISSING_IMAGE_SIZE = 10;
	private static Display display;
	private static Image iconImage;
	private static ImageDescriptor logoImage;
	private static Image sideImage;
	private static HashMap<Object, Color> colorMap = new HashMap<Object, Color>();

	public static Font getBoldFont(Control control) {
		Font initialFont = control.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
		fontData[i].setStyle(SWT.BOLD);
		}
		Font newFont = new Font(Display.getCurrent(), fontData);
		return newFont;
	}
	
	public static Display getDisplay() {
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	public static Image getImage(InputStream is) {
		Display display = getDisplay();
		ImageData data = new ImageData(is);
		if (data.transparentPixel > 0)
			return new Image(display, data, data.getTransparencyMask());
		return new Image(display, data);
	}

	public static Image getWizardSideImage() {
		if (sideImage == null)
			sideImage = ImageDescriptor.createFromFile(ResourcePool.class,
					"/icons/LeftSideImage.JPG").createImage();
		return sideImage;
	}

	public static Image getWizardTitleIcon() {
		if (iconImage == null)
			iconImage = ImageDescriptor.createFromFile(ResourcePool.class,
					"/icons/icon.GIF").createImage();
		return iconImage;
	}

	public static ImageDescriptor getWizardLogoIcon() {
		if (logoImage == null)
			logoImage = ImageDescriptor.createFromFile(ResourcePool.class,
					"/icons/LogoImage.JPG");
		return logoImage;
	}

	@SuppressWarnings("unchecked")
	public static Image getImage(Class clazz, String path) {
		return getImage(Util.getInputStream(clazz, path));
	}

	public static Image getImage(String path) {
		File file = new File(path);
		try {
			return getImage(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			LogUtil.log(ResourcePool.class, Level.WARNING, ErrorMsg.getString(
					Constants.ERROR_FILE_NOT_EXIST, path), e);
			return getMissingImage();
		}
	}

	private static Image getMissingImage() {
		Image image = new Image(getDisplay(), MISSING_IMAGE_SIZE,
				MISSING_IMAGE_SIZE);
		GC gc = new GC(image);
		gc.setBackground(getColor(SWT.COLOR_RED));
		gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
		gc.dispose();
		return image;
	}

	public static Color getColor(int colorRed) {
		Color color = colorMap.get(colorRed);
		if(color==null){
			color = getDisplay().getSystemColor(colorRed);
			colorMap.put(colorRed, color);
		}
		return color;
	}

	public static Shell getActiveShell() {
		Shell activeShell = getDisplay().getActiveShell();
		if(activeShell==null) return new Shell(getDisplay());
		if(activeShell.getImage()==null)
			activeShell.setImage(getWizardTitleIcon());
		return activeShell;
	}
}

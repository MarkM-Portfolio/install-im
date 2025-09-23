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

package com.ibm.lconn.wizard.cluster.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.validator.AbstractValidator;
import com.ibm.misc.BASE64Decoder;
import com.ibm.misc.BASE64Encoder;


/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class FileValidator extends AbstractValidator {
	private static final Logger logger = LogUtil.getLogger(FileValidator.class);
	public static final String SIGNATURE_FILE = ".lock";
	private String mPath;
	private String mCellName;
	private String mFeature;
	private String mClusterTask;
	public FileValidator(String path, String cellName, String feature, String clusterTask) {
		this.mPath = path;
		this.mCellName = cellName;
		this.mFeature = feature;
		this.mClusterTask = clusterTask;
	}

	public int validate() {
		String path = eval(mPath);

		boolean isPrimaryNode = ClusterConstant.OPTION_clusterTaskCreate.equals(eval(mClusterTask));
		if(path == null || "".equals(path.trim())) {
			logError("empty_string", new Object[] {getLable(mPath)});
			return 1;
		}
		// check whether paht is valid folder and user can write to that folder
		File folder = new File(path);
		if(!folder.exists() || !folder.isDirectory()) {
			logError("dir_not_exist");
			return 2;
		} else if (!folder.canWrite()) {
			logError("cannot_write_to_folder", new Object[] {path});
			return 3;
		}
		
		if(isPrimaryNode) {
			// check whether the signature file	
			OutputStream os = null;
			try {
				File signatureFile = new File (folder, SIGNATURE_FILE);
				if(signatureFile.exists() && signatureFile.isFile()) {
					BASE64Decoder decoder = new BASE64Decoder();
					byte[] b = decoder.decodeBuffer(new FileInputStream(signatureFile));
					boolean valid = validateSignature(new String(b, "utf8"));
					if(!valid) {
						logError("shared_folder_used", new Object[] {path});
						return 7;
					}
				}

				BASE64Encoder encoder = new BASE64Encoder();
				os = new FileOutputStream(new File(folder, SIGNATURE_FILE));
				encoder.encode(getSignature().getBytes("utf8"), os);
			} catch (Exception e) {
				logError("cannot_write_to_folder", new Object[] {path});
				return 4;
			} finally {
				if(os != null) {
					try {
						os.close();
					} catch(IOException e) {
						// ignore
					}
				}
			}
		} else {
			try {
				File sigFile = new File(folder, SIGNATURE_FILE);
				if(!sigFile.exists()) {
					logError("invalid_shared_folder", new Object[] {path});
					return 5;
				}
				
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] b = decoder.decodeBuffer(new FileInputStream(sigFile));
				boolean valid = validateSignature(new String(b, "utf8"));
				if(!valid) {
					logError("invalid_shared_folder", new Object[] {path});
					return 6;
				}
			} catch (Exception e) {
				logError("invalid_shared_folder", new Object[] {path});
				return 7;
			}
			
		}

		return 0;
	}
	
	private String getSignature() {
		String cellName = eval(mCellName).trim();
		String feature = eval(mFeature).trim();
		
		logger.log(Level.INFO, "cluster.info.current_feature_for_shared_folder", feature);
		return cellName + ":" + feature ;
	}
	
	private boolean validateSignature(String signature) {
		String cellName = eval(mCellName).trim();
		String feature = eval(mFeature).trim();
		
		String[] sigs = signature.split(":");
		if(!sigs[0].equals(cellName)) {
			return false;
		}
		if(!sigs[1].equals(feature)) {
			return false;
		}
		
		return true;
	}

}

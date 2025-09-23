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

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.migration;

import java.io.File;
import java.util.Properties;

import com.ibm.lconn.common.feature.LCInfo;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class MigrationPrepare{

	private static final String[] FEATURE_ALL = LCInfo.FEATURE_ALL;

	public static Properties buildEnv(){
		Properties props = new Properties();
		String baseDir = MigrationLauncher.MIGRATION_BASE;
		for (int i = 0; i < FEATURE_ALL.length; i++) {
			String feature = FEATURE_ALL[i];
			File f = new File(baseDir, "source/config/"+feature );
			String val = f.isDirectory()?"true":"false";
			props.setProperty(feature+".source.exist", val);
			
			f = new File(baseDir, "source/data/"+feature);
			val = f.isDirectory()?"true":"false";
			props.setProperty(feature+".source.data.exist", val);
		}
		return props;
	}
	
	public static void main(String[] args) {
		buildEnv().list(System.out);
	}
}

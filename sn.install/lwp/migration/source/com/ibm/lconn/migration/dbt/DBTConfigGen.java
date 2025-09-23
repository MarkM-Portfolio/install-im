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
package com.ibm.lconn.migration.dbt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Properties;

import com.ibm.lconn.common.feature.LCInfo;
import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.util.DatabaseUtil;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.Spliter;
import com.ibm.lconn.common.util.Util;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class DBTConfigGen extends LogOperator{
	@Override
	public boolean execute(String para) {
		String feature, targetPath, source, dest;
		
		Spliter sp = new Spliter(para, ">>");
		feature = sp.getHeader();
		targetPath = sp.takeTail().getHeader();
		source = sp.takeTail().getHeader();
		dest = sp.takeTail().getSource();
		
		
		feature = resolve(feature);
		source = resolve(source);
		dest = resolve(dest);
		
		try {
			String sourceRoleString = getRoleString(feature, source, "source");
			String destRoleString = getRoleString(feature, dest, "target");
			File f = new File(targetPath);
			f.getParentFile().mkdirs();
			storeDBT(sourceRoleString, destRoleString, new PrintStream(new FileOutputStream(targetPath)));
			
			if(LCInfo.FEATURE_PROFILES.equals(feature)){
				String exportVersion = getMacroProperties().getProperty("migration.export.version");
				String[] specialVersion = {"2.0.0", "2.0.0beta1", "2.0.0beta2"};
				if(-1!=Util.indexOf(specialVersion, exportVersion)){
					sourceRoleString = getRoleString(feature, source, "source", "SNCORE");
					destRoleString = getRoleString(feature, dest, "target", "SNCORE");
					f = new File(targetPath);
					f.getParentFile().mkdirs();
					storeDBT(sourceRoleString, destRoleString, new PrintStream(new FileOutputStream(addBeforeExt(targetPath, ".", "_SNCORE"))));
				}
			}
			
			log("Generate DBT config file for {0} successfully. ", feature);
		} catch (IOException e) {
			log("Failed to generate DBT config file for {0}. ", feature);
			e.printStackTrace();
		}
		return true;
	}

	private String addBeforeExt(String targetPath, String delim, String symbol) {
		int index = targetPath.lastIndexOf(delim);
		if(index==-1) return targetPath+symbol;
		return targetPath.substring(0, index) + symbol + targetPath.substring(index);
	}

	private void storeDBT(String sourceRoleString, String destRoleString, PrintStream output) {
		output.println("<dbTransfer xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		output.println("\t"+sourceRoleString);
		output.println("\t"+destRoleString);
		output.println("</dbTransfer>");
		output.flush();
	}

	private String getRoleString(String feature, String source, String role)
			throws IOException {
		String dbSchema = getSchema(feature);
		return getRoleString(feature, source, role, dbSchema);
	}

	private String getRoleString(String feature, String source, String role,
			String dbSchema) throws IOException {
		Properties sourceProps = ObjectUtil.loadProperties(source);
		String dbType = eval(sourceProps, feature, "DbType");
		String dbName = eval(sourceProps, feature, "DbName");
		String dbPort = eval(sourceProps, feature, "DataBaseServerPort");
		String dbHost = eval(sourceProps, feature, "DataBaseServerName");
		
		String dbUser = eval(sourceProps, feature, "DbAppUser");
		String dbDriver = getDbDriver(dbType);
		String dbTypeForDbt = getDbTypeForDBT(dbType);
		String dbUrl = DatabaseUtil.getDBUrl(dbType, dbHost, dbPort, dbName);
		String templete = "<database role=\"{0}\" driver=\"{1}\" url=\"{2}\" userId=\"{3}\" schema=\"{4}\" dbType=\"{5}\"/>";
		String roleString = MessageFormat.format(templete, role, dbDriver, dbUrl, dbUser, dbSchema, dbTypeForDbt);
		return roleString;
	}

	private String getDbTypeForDBT(String dbType) {
		if(DatabaseUtil.DBMS_SQLSERVER.equals(dbType)) return "sqlserver2005";
		return dbType;
	}

	private String getDbDriver(String dbType) {
		return DatabaseUtil.getJDBCDriver(dbType);
	}
	
	private String getSchema(String feature){
		if(LCInfo.FEATURE_ACTIVITIES.equals(feature))
			return "ACTIVITIES";
		if(LCInfo.FEATURE_BLOGS.equals(feature))
			return "BLOGS";
		if(LCInfo.FEATURE_COMMUNITIES.equals(feature))
			return "SNCOMM";
		if(LCInfo.FEATURE_DOGEAR.equals(feature))
			return "DOGEAR";
		if(LCInfo.FEATURE_PROFILES.equals(feature))
			return "EMPINST";
		if(LCInfo.FEATURE_HOMEPAGE.equals(feature))
			return "HOMEPAGE";
		return "";
	}

	private String eval(Properties sourceProps, String feature, String key) {
		String re = feature+"."+key;
		re = sourceProps.getProperty(re);
		return re;
	}
	
	public static void main(String[] args) {
		DBTConfigGen gen = new DBTConfigGen();
		gen.execute("activities>>AAA.xml>>migrationData/source/config/activities/activities.properties>>migrationData/dest/config/activities/activities.properties");
	}

}

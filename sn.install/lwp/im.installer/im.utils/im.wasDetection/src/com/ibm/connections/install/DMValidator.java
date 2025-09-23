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

package com.ibm.connections.install;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DMValidator {
	
	public static final String HEAP_SIZE_ERROR="heap_size_error";
	public static final String NODE_NUM_ERROR="node_num_error";
	
	
	protected static JavaPropertyGetter propertyGetter;
	
//	static {
//		String tempDir = System.getProperty("tempDir");
//		File file = new File(tempDir + "/dminfo.properties");
////		file = new File("src/dminfo.properties");
//		deleteFile(file);
//		
//		String profilePath = System.getProperty("profilePath");
//		String cdLocation = System.getProperty("cdLocation");
//		String dmUserid = System.getProperty("dm.userid");
//		String dmPassword = System.getProperty("dm.password");
//		
//		String command = profilePath + "/bin/wsadmin -conntype SOAP -lang jython -username " + dmUserid + " -password " + dmPassword + " -f " + cdLocation + "/ConfigEngine/config/was/wkplc_GetDMInfo.py " +  tempDir + "/dminfo.properties";
//		ExternalCommandAction eca = new ExternalCommandAction();
//		eca.setCommand(command);
//		eca.execute();
//		
//		propertyGetter = new JavaPropertyGetter(file);
//	}
	
	public static String getDMInfo(String profilePath,String pyPath,String dmUserid,String dmPassword) throws Exception{
		
		try{
			String tempDir = profilePath + "/bin";
			File file = new File(tempDir + "/dminfo.properties");
	//		file = new File("src/dminfo.properties");
			deleteFile(file);
			
	//		String profilePath = System.getProperty("profilePath");
	//		String cdLocation = System.getProperty("cdLocation");
	//		String dmUserid = System.getProperty("dm.userid");
	//		String dmPassword = System.getProperty("dm.password");
			//String pypath = "C:\\SN_Build\\SN_INSTALL_SRC\\latest_team_LC3_0_sn_install_2\\socnet.02\\sn.install\\lwp\\build\\im.framework\\im.launch\\setup\\disk\\IM\\windows\\wkplc_GetDMInfo.py";
//			String pyPathStr = pyPath + "/wkplc_GetDMInfo.py";
			File pyfile = new File(pyPath);
			if(!pyfile.exists()){
				return "2";
			}
			
			String command = profilePath + "/bin/wsadmin.bat -conntype SOAP -lang jython -username " + dmUserid + " -password " + dmPassword + " -f " +  pyPath + " " + tempDir + "/dminfo.properties";
			ExternalCommandAction eca = new ExternalCommandAction();
			eca.setWorkingDirectory(profilePath);
			eca.setCommand(command);
			eca.execute();
			
			if(!file.exists()){
				return "3";
			}

			propertyGetter = new JavaPropertyGetter(file);
			return eca.getReturnCode();
		}catch(Exception e){
			throw e;
		}
	}
	
	public static boolean validateHeapsize(){
		boolean result = false;
		
		
		String dmHeapSizeTooLow = propertyGetter.getProperty("dm.heapSizeTooLow");
		
		if("true".equals(dmHeapSizeTooLow)){
			result = true;
		}
		
		return result;
	}
	
	public static boolean validteNodesNumber(){
		List list = detectNodes();
		if(list.size()>0){
			return true;
		}
		return false;
	}
	
	
	
	public static List detectNodes(){
		ArrayList list = new ArrayList();
		String dmnodeStr = detectDMNode();
		String nodesStr = propertyGetter.getProperty("dm.nodes");
		String[] nodes = nodesStr.split(",");
		for(int i=0;i<nodes.length;i++){
			if(!dmnodeStr.equals(nodes[i])){
				list.add(nodes[i]);
			}
		}
		return list;
	}
	
	public static String detectDMNode(){
		
		return propertyGetter.getProperty("dm.nodename");
	}
	
	public static String detectDMCellName(){
		
		return propertyGetter.getProperty("dm.cellanme");
	}
	
	public static List detectClusters(){
		ArrayList list = new ArrayList();
		String clustersStr = propertyGetter.getProperty("dm.clusterlist");
		Map clustersMap = FieldMappingParser.parseSemicolon(clustersStr);
		Set clustersSet = clustersMap.keySet();
		Iterator it = clustersSet.iterator();
		while(it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
	
	public static boolean deleteFile(File file){
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
	public static void main(String[] args) {
//		System.out.println(detectClusters());
//		System.out.println(detectDMNode());
//		System.out.println(detectNodes());

	}
}

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class WebServerHelper {

	private static final ILogger log = IMLogger
			.getLogger(com.ibm.connections.install.WebServerHelper.class);

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	
	private static String ihsFilePath = TMP_DIR + File.separator + "ihs.txt";
	private static File ihsFile = new File(ihsFilePath);

	private static String webServerPyFilePath = TMP_DIR + File.separator + "webServer.py";
	
	private static String commandPath = TMP_DIR + File.separator + "command.txt";
//	private static File webServerPyFile = new File(webServerPyFilePath);

	/**
	 * 
	 * @param profilePath  Dmgr path
	 * @param dmUserid
	 * @param dmPassword
	 * @return
	 * @throws Exception
	 */
	public String getWebServerInfo(String profilePath,String dmUserid, String dmPassword) throws Exception {

		try {
			
			if(!ihsFile.exists())
				ihsFile.createNewFile();
			
			//InputStream fileInput = WebServerHelper.class.getResourceAsStream("webserver.py");
			
			//WebServerHelper.MESSAGE += " fileInput.available()::"+fileInput.available();
			
			//copyFile(fileInput, webServerPyFilePath);
			
			PrintWriter printWriter = new PrintWriter(new File(webServerPyFilePath));
			printWriter.println("f = open(sys.argv[0],'w')");
			printWriter.println("f.write(AdminTask.listServers('[-serverType WEB_SERVER]'))");
			printWriter.println("f.close()");
			printWriter.close();
			
			String command = "";
			String commands[] = null;
			
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				commands = new String[10];
				commands[0] = "\""+profilePath + "\\bin\\wsadmin.bat\"";
				commands[1] = "-lang";
				commands[2] = "jython";
				commands[3] = "-user";
				commands[4] = dmUserid;
				commands[5] = "-password";
				commands[6] = dmPassword;
				commands[7] = "-f";
				commands[8] = webServerPyFilePath;
				commands[9] = ihsFilePath;
				
//				command = "\"C:\\Program Files (x86)\\IBM\\WebSphere\\AppServer\\profiles\\Dmgr01\\bin\\wsadmin.bat\" -lang jython -user wasadmin -password passw0rd -f C:\\Users\\IBM_ADMIN\\Desktop\\test.py C:\\Users\\IBM_ADMIN\\AppData\\Local\\Temp\\ihs.txt";
//				command = "\"C:\\Program Files (x86)\\IBM\\WebSphere\\AppServer\\profiles\\Dmgr01\\bin\\wsadmin.bat\" -lang jython -user wasadmin -password passw0rd -f C:\\Users\\IBM_ADMIN\\Desktop\\test.py C:\\Users\\IBM_ADMIN\\AppData\\Local\\Temp\\ihs.txt";

			} else if (osName.toLowerCase().startsWith("os/400")) {
				// OS400_Enablement
				commands = new String[10];
				commands[0] = profilePath + "/bin/wsadmin";
				commands[1] = "-lang";
				commands[2] = "jython";
				commands[3] = "-user";
				commands[4] = dmUserid;
				commands[5] = "-password";
				commands[6] = dmPassword;
				commands[7] = "-f";
				commands[8] = webServerPyFilePath;
				commands[9] = ihsFilePath;

			} else {
				commands = new String[10];
				commands[0] = profilePath + "/bin/wsadmin.sh";
				commands[1] = "-lang";
				commands[2] = "jython";
				commands[3] = "-user";
				commands[4] = dmUserid;
				commands[5] = "-password";
				commands[6] = dmPassword;
				commands[7] = "-f";
				commands[8] = webServerPyFilePath;
				commands[9] = ihsFilePath;
			}
			
			for(int i=0;i<commands.length;i++){
				command += commands[i]+" ";
			}
			//Runtime.getRuntime().exec(command);
			command = command.replace('\\', '/');
			printWriter = new PrintWriter(new File(commandPath));
			printWriter.println(command);
			printWriter.close();
			
			ExternalCommandAction eca = new ExternalCommandAction();
			eca.setWorkingDirectory(profilePath);
			eca.setCommand(command);
			eca.execute();
			
			return "success";
		} catch (Exception e) {
			throw e;
		}
	}
	
	private boolean copyFile(InputStream is, String toFile) {
		try {
			// InputStream is = new FileInputStream(fromFile);
			FileOutputStream fos = new FileOutputStream(toFile);
			log.info("Web Server panel : is == null : " + is == null);
			for (int b = is.read(); b != -1; b = is.read()) {
				fos.write(b);
			}
			is.close();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			log.error(e);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}
	
	public List<WebServer> parseIhsTxt(){
		List<WebServer> webServers = new ArrayList<WebServer>();
		try {
			FileInputStream fis = new FileInputStream(ihsFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			WebServer ws = null;
			while((line = br.readLine())!= null){
				line = line.trim();
				if(!"".equals(line)){
					webServers.add(parseIhsTxtForOneLine(line));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return webServers;
	}
	
	private  WebServer parseIhsTxtForOneLine(String line) {
		WebServer ws = new WebServer();
		ws.setName(line.substring(0,line.indexOf('(')));
		ws.setNode(line.substring(line.indexOf("nodes")+6, line.indexOf("servers")-1));
		return ws;
	}
	
	public static void main(String[] args) {
	/*
		String profilePath = "C:\\Program Files (x86)\\IBM\\WebSphere\\AppServer\\profiles\\Dmgr01";
		String pyPath = "C:\\Users\\IBM_AD~1\\AppData\\Local\\Temp\\ihs.txt";
		String dmUserid = "wasadmin";
		String dmPassword = "passw0rd";
		String hostname = "adminib-k0u2l8g.cn.ibm.com";
		String port = "9043";
	*/
		String profilePath = "C:\\Program Files (x86)\\IBM\\WebSphere\\AppServer\\profiles\\Dmgr01";
		String dmUserid = "wasadmin";
		String dmPassword = "passw0rd";
		
		try {
			//helper.parseIhsTxt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

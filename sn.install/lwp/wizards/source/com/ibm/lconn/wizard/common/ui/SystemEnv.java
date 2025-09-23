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
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


public class SystemEnv {
	private Hashtable<String, Hashtable<String, Object>> scopeHash;
	private Hashtable<String, Object> defaultScope;
	public void setVariable(String scope, String vName, String vValue){
		getScope(scope).put(vName, vValue);
	}
	
	public void setVariableObject(String scope, String vName, Object vValue){
		getScope(scope).put(vName, vValue);
	}
	
	public String getVariable(String scope, String vName){
		try{
			return (String) getScope(scope).get(vName);
		}catch (Exception e) {
			return "";
		}		
	}
	
	public Object getVariableObject(String scope, String vName){
		try{
			return getScope(scope).get(vName);
		}catch (Exception e) {
			return null;
		}		
	}
	
	private void printScope(PrintStream pw, String scope){
		String scopeName = getScopeName(scope);
		pw.println("========== " + scopeName + " ==============");
		Hashtable<String, Object> current = getScope(scope);
		
		Enumeration<String> keys = current.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = (String)current.get(key);
			pw.println(key+"="+value);
		}
		pw.println("===========================================");
	}
	
	public void printScope(PrintStream pw, Set<String> scopes){
		for (Iterator<String> iter = scopes.iterator(); iter.hasNext();) {
			String scope = iter.next();
			printScope(pw, scope);
		}
	}
	
	private String getScopeName(String scope) {
		if(scope==null) return "Default Scope";
		else return scope;
	}
	
	public void printScopes(PrintStream pw, String scopes){
		Set<String> scopeSet = new HashSet<String>();
		String[] scopesArry = scopes.split(" ");
		for (int i = 0; i < scopesArry.length; i++) {
			scopeSet.add(scopesArry[i]);
		}
		printScope(pw, scopeSet);
	}
	
	public void printScope(String scope){
		printScope(System.out, scope);
	}
	
	public void printScopes(String scopes){
		printScopes(System.out, scopes);
	}

	public void printAll(){
		this.printScope(null);
		if(this.scopeHash!=null) this.printScope(System.out, this.scopeHash.keySet());
	}
	
	private Hashtable<String, Object> getScope(String scope){
		if(scope==null) return initDefault();
		else return initScope(scope);
	}
	
	private Hashtable<String, Object> initScope(String scope) {
		if(scopeHash==null) scopeHash = new Hashtable<String, Hashtable<String,Object>>();
		Hashtable<String, Object> scopeHashtable = scopeHash.get(scope);
		if(scopeHashtable==null){
			scopeHashtable = new Hashtable<String, Object>();
			scopeHash.put(scope, scopeHashtable);
		}
		return scopeHashtable;
	}
	
	private Hashtable<String,Object> initDefault() {
		if(defaultScope==null)
			defaultScope = new Hashtable<String, Object>();
		return defaultScope;
	}

	public void updateProperties(Properties pro, String scope) {
		Enumeration<Object> keys = pro.keys();
		Hashtable<String, Object> scopeEnv = getScope(scope);
		while(keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			String value = (String)scopeEnv.get(key);
			if(value!=null)
				pro.setProperty(key, value);
		}
	}
	
}

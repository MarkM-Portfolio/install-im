 // Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 12/6/2004 10:03:59 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LdapCheckTask.java

package com.ibm.wps.localize;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.Task;

// Referenced classes of package com.ibm.wps.configwizard.helper:
//			  LdapCheck

public class LdapMailAttributeTask extends Task
{

	public LdapMailAttributeTask()
	{
		ldapURL = "";
		objectDn = "";
		failOnError = true;
		ldapUser = "";
		ldapPassword = "";
		ldapSslEnabled = false;
		p = new Properties();
		retval = 0;
		emailattribute = "";
		localdomain = "";		
	}

	public void execute()
	{
		System.out.println("###############################################################");
		System.out.println("ldapURL        : " + ldapURL);
		System.out.println("ldapUser       : " + ldapUser);
		System.out.println("ldapPassword   : *");
		System.out.println("ldapSslEnabled : " + ldapSslEnabled);
		if(null != objectDn && "" != objectDn.trim())
		{
			System.out.println("objectDn       : " + objectDn);
			System.out.println("###############################################################");
		}
		if(null == ldapPassword || ldapPassword.equals(""))
			if(failOnError)
				System.out.println(new Exception("Password cannot be empty or null"));
			else
				System.out.println("WARNING: Password is empty or null");
		String objectDnArray[] = {
			objectDn
		};
		retval = LdapMailAttribute.check(ldapURL, ldapUser, ldapPassword, objectDnArray, ldapSslEnabled);

		if(0 != retval)
		{
			System.out.println("ERROR: " + retval);
			switch(retval)
			{
			case 4: // '\004'
				System.out.println("Invalid or insufficient authorization privileges.");
				break;

			case 1: // '\001'
				System.out.println("Could not connect to LDAP directory at specified host, port and SSL state.");
				break;

			case 2: // '\002'
				System.out.println("Specified entity does not exist in directory.");
				break;
			}
			if(failOnError)
				System.out.println(new Exception("RETURN_CODE_FOR_THIS_TASK: " + retval));
		}
		System.out.println("###############################################################");
		emailattribute = LdapMailAttribute.getMailAttribute(ldapUser);
		if (emailattribute != null || emailattribute.length() > 0){
			int atindex = emailattribute.indexOf("@");
			int mailindex = emailattribute.indexOf(":");
			int test2 = emailattribute.length();
			String loco = emailattribute.substring(atindex+1,emailattribute.length());
			String mail = emailattribute.substring(mailindex+2,emailattribute.length());
			project.setProperty("adminemail",mail);
			project.setProperty("localdomain",loco);
		}
	}

	public String replaceAllInstancesOf(String bigString, String toReplace, String with)
	{
		return join(split(bigString, toReplace), with);
	}

	public List split(String string, String delimiter)
	{
		List result = new ArrayList();
		int index = string.indexOf(delimiter);
		String tempString = string;
		for(; index != -1; index = tempString.indexOf(delimiter))
		{
			result.add(tempString.substring(0, index));
			tempString = tempString.substring(index + delimiter.length());
		}

		result.add(tempString);
		return result;
	}

	public String join(List list, String delimiter)
	{
		StringBuffer result = new StringBuffer();
		int count = 0;
		for(Iterator i = list.iterator(); i.hasNext();)
		{
			Object element = i.next();
			if(count > 0)
				result.append(delimiter);
			result.append(element.toString());
			count++;
		}

		return result.toString();
	}

	public void setProperties(String filePathPlusFileName, String hostNameKey, String portKey, String userKey, String password, String objectDnKey, String sslKey)
	{
		try
		{
			java.io.InputStream in = new FileInputStream(filePathPlusFileName);
			p = new Properties();
			p.load(in);
			if(!portKey.equalsIgnoreCase("null") || !hostNameKey.equalsIgnoreCase("null"))
				setLdapURL(p.getProperty(hostNameKey) + ":" + p.getProperty(portKey));
			if(!userKey.equals("null"))
				setLdapUser(p.getProperty(userKey));
			setLdapPassword(password);
			if(!objectDnKey.equals("null"))
				setObjectDn(p.getProperty(objectDnKey));
			if(p.getProperty(sslKey).equalsIgnoreCase("false"))
				setLdapSslEnabled(false);
			else
				setLdapSslEnabled(true);
		}
		catch(UnsupportedEncodingException e)
		{
			System.out.println("ERROR: Unsupported encoding for file: " + filePathPlusFileName + "\n" + e);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("ERROR: FILE NOT FOUND: " + filePathPlusFileName + "\n" + e);
		}
		catch(IOException e)
		{
			System.out.println("ERROR: FILE NOT FOUND: " + filePathPlusFileName + "\n" + e);
		}
	}

	public static void main(String args[])
	{
		LdapMailAttribute lct = new LdapMailAttribute();
		//String args0 = lct.replaceAllInstancesOf(args[0], "!@SPACE_HERE@!", " ");
		boolean hasSpaceInPath = false;
		String args2 = args[2];
		String args3 = args[3];
		for(int i = 3; i < args.length - 6; i++)
		{
			System.out.println("Space found in path to parent properties file... appending " + args2 + " and " + args[i]);
			args2 = args2 + " " + args[i];
		}

		if(args.length < 9)
		{
			System.out.println(args[1]);
		//	System.out.println(args0);
			return;
		}
		//lct.setProperties(args2, args[args.length - 6], args[args.length - 5], args[args.length - 4], args[args.length - 3], args[args.length - 2], args[args.length - 1]);
		//if(lct.ldapURL.equals("") || lct.ldapURL == null || lct.ldapUser.equals("") || lct.ldapUser == null || lct.ldapPassword.equals("") || lct.ldapPassword == null)
		{
			System.out.println(args[1]);
		//	System.out.println(args0);
			return;
		}
		//System.out.println(args[1]);
		//lct.setFailOnError(true);
		//lct.execute();
		//System.out.println("RETURN_CODE_FOR_THIS_TASK: " + lct.retval);
		//if(lct.retval != 0)
		//	System.exit(lct.retval);
	}

	public void setLdapURL(String ldapURL)
	{
		if(ldapURL.equalsIgnoreCase("null"))
		{
			return;
		} else
		{
			this.ldapURL = ldapURL.trim();
			return;
		}
	}

	public void setLdapUser(String ldapUser)
	{
		if(ldapUser.equalsIgnoreCase("null"))
		{
			return;
		} else
		{
			this.ldapUser = ldapUser.trim();
			return;
		}
	}

	public void setLdapPassword(String ldapPassword)
	{
		if(ldapPassword.equalsIgnoreCase("null"))
		{
			return;
		} else
		{
			this.ldapPassword = ldapPassword.trim();
			return;
		}
	}

	public void setObjectDn(String objectDn)
	{
		if(objectDn.equalsIgnoreCase("null"))
		{
			return;
		} else
		{
			this.objectDn = objectDn.trim();
			return;
		}
	}

	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	public void setLdapSslEnabled(boolean ldapSslEnabled)
	{
		this.ldapSslEnabled = ldapSslEnabled;
	}

	protected String ldapURL;
	protected String objectDn;
	protected boolean failOnError;
	protected String ldapUser;
	protected String ldapPassword;
	protected boolean ldapSslEnabled;
	protected Properties p;
	public int retval;
	public String emailattribute;
	public String email;
	public String localdomain;	
	public static final int RC_SUCCESS = 0;
	public static final int RC_NO_CONNECTION = 1;
	public static final int RC_OBJECT_NOT_FOUND = 2;
	public static final int RC_INVALID_NAME = 3;
	public static final int RC_INVALID_AUTH = 4;
}
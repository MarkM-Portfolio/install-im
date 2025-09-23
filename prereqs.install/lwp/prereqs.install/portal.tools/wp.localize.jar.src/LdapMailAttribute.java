// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 12/6/2004 9:54:43 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LdapCheck.java

package com.ibm.wps.localize;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.*;
import javax.naming.directory.InitialDirContext;

class LdapMailAttribute
{
    private static InitialDirContext _dircontext;
	LdapMailAttribute()
	{
	}

	public static int check(String ldapURL, String ldapUser, String ldapPassword, String objectDn[], boolean LDAPsslEnabled)
	{
		//InitialDirContext dircontext;
		try
		{
			Properties properties = new Properties();
			Hashtable env = new Hashtable(11);
			env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
			env.put("java.naming.factory.url.pkgs", "com.ibm.jndi");
			env.put("java.naming.provider.url", "ldap://" + ldapURL);
			if(ldapUser != null)
				env.put("java.naming.security.principal", ldapUser);
			if(ldapPassword != null)
				env.put("java.naming.security.credentials", ldapPassword);
			if(LDAPsslEnabled)
				env.put("java.naming.security.protocol", "ssl");
			_dircontext = new InitialDirContext(env);

		}
		catch(InvalidNameException e1)
		{
			System.err.println(e1);
			return 3;
		}
		catch(NameNotFoundException e2)
		{
			System.err.println(e2);
			return 4;
		}
		catch(AuthenticationException ae)
		{
			System.err.println(ae);
			return 4;
		}
		catch(Exception e3)
		{
			System.err.println(e3);
			return 1;
		}
		try
		{
			for(int i = 0; i < objectDn.length; i++)
			{
				String dn = objectDn[i];
				if(null != dn && "" != dn.trim())
					System.err.println("Checking for '" + dn + "'");
				javax.naming.directory.Attributes attributes = _dircontext.getAttributes(dn);
				
				if(attributes == null)
					return 2;
			}

		}
		catch(InvalidNameException e1)
		{
			System.err.println(e1);
			return 3;
		}
		catch(NameNotFoundException e2)
		{
			System.err.println(e2);
			return 2;
		}
		catch(AuthenticationException e3)
		{
			System.err.println(e3);
			return 4;
		}
		catch(Exception e)
		{
			System.err.println(e);
			return 2;
		}
		return 0;
	}
	public static String getMailAttribute(String ldapUser)
	{
		javax.naming.directory.Attributes mail = null;
		String emailaddress = ""; 
		try {
			mail = _dircontext.getAttributes(ldapUser);
	        emailaddress = mail.get("mail").toString();
		} catch (NamingException e) {
			System.out.println("LDAP does not have the mail attribute defined");
		}
		return emailaddress;

		

	}	

	public static final int RC_SUCCESS = 0;
	public static final int RC_NO_CONNECTION = 1;
	public static final int RC_OBJECT_NOT_FOUND = 2;
	public static final int RC_INVALID_NAME = 3;
	public static final int RC_INVALID_AUTH = 4;
}
/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.delta;

import java.io.*;
import java.util.*;

/*
 * interface ISystemFile
 *
 * @author: Kim Hackett
 * History 1.2, 9/26/03
 *
 */

/**
 *  
 */
interface ISystemFile{
   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;

   public java.io.File getFile();

   /**
 * @return
 * @uml.property  name="permissions"
 */
public String getPermissions();

   /**
 * @return
 * @uml.property  name="group"
 */
public String getGroup();

   /**
 * @return
 * @uml.property  name="owner"
 */
public String getOwner();

   /**
 * @param chmodValue
 * @return
 * @uml.property  name="permissions"
 */
public int setPermissions(String chmodValue);

   /**
 * @param groupValue
 * @return
 * @uml.property  name="group"
 */
public int setGroup(String groupValue);

   /**
 * @param ownerValue
 * @return
 * @uml.property  name="owner"
 */
public int setOwner(String ownerValue);

   public int setWritable();

   public boolean isWritable();

} // end interface

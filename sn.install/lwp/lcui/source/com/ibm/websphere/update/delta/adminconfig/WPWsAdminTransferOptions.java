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

package com.ibm.websphere.update.delta.adminconfig;

import com.ibm.websphere.management.filetransfer.client.FileTransferOptions;

/**
 * File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminTransferOptions.java, wps.base.fix, wps6.fix History 1.3, 2/25/04
 */
class WPWsAdminTransferOptions implements FileTransferOptions {
    public final static String pgmVersion = "1.3" ;
    public final static String pgmUpdate = "2/25/04" ;

    private boolean remove     = true;
    private boolean compress   = true;
    private boolean secure     = false;
    private boolean overwrite  = true;

    /**
	 * @param overwrite  the overwrite to set
	 * @uml.property  name="overwrite"
	 */
    public void setOverwrite(boolean flag) { overwrite = flag; }

    /**
	 * @param secure  the secure to set
	 * @uml.property  name="secure"
	 */
    public void setSecure(boolean flag)  { secure = flag; }

    /**
	 * @param compress  the compress to set
	 * @uml.property  name="compress"
	 */
    public void setCompress(boolean flag) { compress = flag; }

    public void setDeleteSourceOnCompletion(boolean flag) { remove = flag; }

    /**
	 * @return  the overwrite
	 * @uml.property  name="overwrite"
	 */
    public boolean isOverwrite()  { return overwrite; }

    /**
	 * @return  the secure
	 * @uml.property  name="secure"
	 */
    public boolean isSecure() { return secure; }

    /**
	 * @return  the compress
	 * @uml.property  name="compress"
	 */
    public boolean isCompress()  { return compress; }

    public boolean isDeleteSourceOnCompletion() { return remove; }
}


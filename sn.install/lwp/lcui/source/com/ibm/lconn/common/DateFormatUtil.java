/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.common;

import java.util.Date;
import com.ibm.icu.text.DateFormat;

public class DateFormatUtil {
	
	public static String format(String date){
		return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(date));
	}

}

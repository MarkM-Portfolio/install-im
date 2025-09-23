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
package com.ibm.lconn.wizard.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class LogFormatter extends Formatter {
	private static final DateFormat df = new SimpleDateFormat(
			"[MM/dd/yy HH:mm:ss.SSS z] ");

	private static final String CRLF = System.getProperty("line.separator");

	@Override
	public String format(LogRecord record) {
		StringBuffer msg = new StringBuffer();
		msg.append(df.format(new Date(record.getMillis())));
		msg.append(record.getSourceClassName());
		msg.append(" ");
		msg.append(record.getSourceMethodName());
		msg.append(CRLF);
		msg.append("[" + record.getLevel() + "] ");
		msg.append(formatMessage(record));
		msg.append(CRLF);
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			Throwable t = record.getThrown();
			t.printStackTrace(pw);
			msg.append(sw.toString());
			msg.append(CRLF);
		}
		return msg.toString();
	}
}

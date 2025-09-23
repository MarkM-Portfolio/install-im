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
package com.ibm.websphere.update.ioservices.standard;


/*
 * Standard Notifier
 *
 * History 1.1, 9/6/02
 *
 * 30-Jul-2002 Initial Version
 */

import com.ibm.websphere.update.ioservices.Notifier;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class StandardNotifier implements Notifier
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/6/02" ;

    // Instantor ...

    public StandardNotifier(PrintStream output)
    {
        this.output = output;

        this.taskCount = 0;
        this.taskNumber = 0;

        this.bannerStack = new Vector();
    }

    // Set the count of tasks to be performed.

    protected int taskCount;

    /**
	 * @param taskCount  the taskCount to set
	 * @uml.property  name="taskCount"
	 */
    public void setTaskCount(int taskCount)
    {
        this.taskNumber = 0;
        this.taskCount = taskCount;
    }

    // Answer the count of tasks to be performed.

    /**
	 * @return  the taskCount
	 * @uml.property  name="taskCount"
	 */
    public int getTaskCount()
    {
        return taskCount;
    }

    // Answer the current task number.  This is
    // reset to 0 when the task count is set.

    protected int taskNumber;

    /**
	 * @return  the taskNumber
	 * @uml.property  name="taskNumber"
	 */
    public int getTaskNumber()
    {
        return taskNumber;
    }

    protected int incrementTaskNumber()
    {
        return ( ++taskNumber );
    }

    // Allow a stack of banners to be stored.

    protected Vector bannerStack;

    /**
	 * @return  the bannerStack
	 * @uml.property  name="bannerStack"
	 */
    protected Vector getBannerStack()
    {
        return bannerStack;
    }

    // Add a banner to the stack.

    public void pushBanner(String banner)
    {
        getBannerStack().addElement(banner);
    }

    // Remove the last banner from the stack.
    // Answer null if the stack is empty.

    public String popBanner()
    {
        Vector useBannerStack = getBannerStack();
        int bannerLength = useBannerStack.size();

        if ( bannerLength == 0 ) {
            return null;

        } else {
            String result = (String) useBannerStack.elementAt(bannerLength - 1);
            useBannerStack.removeElementAt(bannerLength - 1);
            return result;
        }
    }

    // Collate the banners on the stack.

    public String collateBanners()
    {
        Vector useBannerStack = getBannerStack();
        int numBanners = useBannerStack.size();

        StringBuffer collation = new StringBuffer();

        for ( int bannerNo = 0; bannerNo < numBanners; bannerNo++ ) {
            String nextBanner = (String) useBannerStack.elementAt(bannerNo);
            collation.append(nextBanner);
        }

        return collation.toString();
    }

    // Replace the last banner with another.  Answer the replaced banner.

    public String replaceBanner(String banner)
    {
        String oldBanner = popBanner();
        pushBanner(banner);
        return oldBanner;
    }

    public String beginTask()
    {
        incrementTaskNumber();

        return performTaskNotification(BEGIN_TASK);
    }

    public String endTask()
    {
        return performTaskNotification(END_TASK);
    }

    // Tell if processing has been cancelled.

    public boolean wasCancelled()
    {
        return false;
    }

    // Output support ...

    protected PrintStream output;

    /**
	 * @return  the output
	 * @uml.property  name="output"
	 */
    protected PrintStream getOutput()
    {
        return output;
    }

    protected void println(String text)
    {
        getOutput().println(text);
    }

    public static final boolean BEGIN_TASK = false ;
    public static final boolean END_TASK = true ;

    protected String performTaskNotification(boolean taskStatus)
    {
        StringBuffer outputBuffer = new StringBuffer();

        outputBuffer.append("Task ");
        outputBuffer.append(Integer.toString(getTaskNumber()));
        outputBuffer.append(" out of ");
        outputBuffer.append(Integer.toString(getTaskCount()));
        outputBuffer.append("; ");

        outputBuffer.append( (taskStatus == BEGIN_TASK) ? "Begin " : "End " );
        outputBuffer.append( collateBanners() );

        String notificationText = outputBuffer.toString();

        println(notificationText);

        return notificationText;
    }
}



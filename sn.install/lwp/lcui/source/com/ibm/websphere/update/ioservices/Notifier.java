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
package com.ibm.websphere.update.ioservices;

/*
 * Notifier
 *
 * History 1.1, 9/6/02
 *
 * 09-Jul-2002 Initial Version
 */

/**
 *  
 */
public interface Notifier
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/6/02" ;

    // Set the count of tasks to be performed.

    /**
	 * @param taskCount
	 * @uml.property  name="taskCount"
	 */
    public void setTaskCount(int taskCount);

    // Answer the count of tasks to be performed.

    /**
	 * @return
	 * @uml.property  name="taskCount"
	 */
    public int getTaskCount();

    // Answer the current task number.  This is
    // reset to 0 when the task count is set.

    public int getTaskNumber();

    // Push a banner.

    public void pushBanner(String banner);

    // Pop (and return) the last banner.

    public String popBanner();

    // Collate the banners on the banner stack.
    public String collateBanners();

    // Replace the current banner (the tail of the banner stack).

    public String replaceBanner(String banner);

    // Indicate that a task has been started.
    // Answer the current notification text.

    public String beginTask();

    // Indicate that another task was completed.
    // Answer the current notification text.

    public String endTask();

    // Tell if processing was cancelled.

    public boolean wasCancelled();
}

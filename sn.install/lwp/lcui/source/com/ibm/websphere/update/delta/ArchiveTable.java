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

/*
 * Created on Jul 12, 2005
 *
 */
package com.ibm.websphere.update.delta;

import java.util.HashMap;

/**
 * Represents an archive table.
 * 
 * @author cooka
 *
 */
public class ArchiveTable extends HashMap {
    
    /**
     * <code>SEPARATOR_STR</code> the string indicating a nested archive
     */
    public static final String SEPARATOR_STR = "*";
    
    public ArchiveTable()
    {
        super();
    }
    
    /**
     * Tracks the entries in an archive.
     * 
     * @return an iterator across the archive entries
     */
    public ArchiveTableIterator tableIterator()
    {
        return new ArchiveTableIterator( this );
    }
}

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

package com.ibm.websphere.update.delta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator to facilitate the traversal of the 
 * archive table.
 * 
 * @author cooka
 *
 */
public class ArchiveTableIterator implements Iterator {
    
    /**
     * the table of <code>archives</code> and their contents
     */
    private HashMap archives;

    /**
     * <code>keyArray</code> array of keys 
     */
    private Object [] keyArray;

    /**
     * Tracks the current <code>position</code> of the iterator 
     */
    private int position;

    /**
     * Constructs a new ArchiveTableIterator from the
     * contents of the specified table.
     * 
     * @param table
     */
    public ArchiveTableIterator( HashMap table )
    {
        archives = new HashMap();

        populateArchiveList( table );

        keyArray = this.archives.keySet().toArray();
        position = -1;
    }

    /**
     * Creates a new table sorted by the archive
     * containing the file. 
     * 
     * @param tbl
     */
    private void populateArchiveList( HashMap tbl ) 
    {
        Iterator keys = tbl.keySet().iterator();

        while ( keys.hasNext() )
        {
            String key = (String) keys.next();

            //System.out.println( "key = " + key );
            
            /*
             * Entry is in the root archive.
             *
             */
            
            if ( key.indexOf( ArchiveTable.SEPARATOR_STR ) < 0 )
            {
                putNullEntry( key );
            }
            /*
             * Entry is nested one or more archives deep.
             */
            else {
                int index = key.lastIndexOf( ArchiveTable.SEPARATOR_STR );
                String newKey = key.substring( 0, index );
                String keyValue = key.substring( index + 1 );


                putEntry( newKey, keyValue );
            }
        }
    }

    /**
     * Puts the key into the table with no
     * entries associated.
     * 
     * @param key
     */
    private void putNullEntry( String key ) 
    {
        this.archives.put( key, null );
    }

    /**
     * Puts the value into the table, adding to
     * the list stored at that key (creating it 
     * if it doesn't exist)
     * 
     * @param key
     * @param value
     */
    private void putEntry( String key, Object value ) 
    {
        List values = (List) this.archives.get( key );

        if ( values == null )
        {
            values = new ArrayList();
        }

        values.add( value );

        this.archives.put( key, values );
    }

    /**
     * prevent no-arg instantiation
     */ 
    private ArchiveTableIterator()
    {
    }

    /**
     * Returns the list of associated entries for the current key.
     * 
     * @return the list of entries in the associate archive
     */ 
    public List getCurrentList() 
    {
        return ( (List) this.archives.get( this.keyArray [ position ] ) );
    }


    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext()
    {
        if ( this.position < ( this.keyArray.length - 1 ) )
        {
            //System.out.println( "Returning true..." );
            
            return true;
        }
        else
        {
            //System.out.println( "Returning false...pos=" + position + " length = " + (this.keyArray.length - 1 ) );
            return false;
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next()
    {
        if ( ( position + 1 ) >= this.keyArray.length )
            throw new NoSuchElementException( "No more elements in iterator." );
        
        return ( this.keyArray[ ++position ] );
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
    
     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove()
    {
        throw new UnsupportedOperationException( "Remove operation is not supported!" );
    }
}

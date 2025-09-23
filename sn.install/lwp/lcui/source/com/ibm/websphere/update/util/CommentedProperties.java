/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
 */

package com.ibm.websphere.update.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * The <code>CommentedProperties</code> class represents a persistent set of properties. The <code>CommentedProperties</code> can be saved to a stream or loaded from a stream. Each key and its corresponding value in the property list is a string. <p> This class models the <code>java.util.Properties</code> but has a few distinct  difference.   <ol> <li>The <code>load</code> method preserve all comments and blank lines in the stream. <li>The order of the properties within the stream is preserved.  <li>The <code>CommentedProperties</code> class does not inherit from Hashtable.  It uses a HashMap internally.  This code does <b>NOT</b> synchronize the data, so it is not  Thread-safe. </ol> <p> <a name="encoding"></a> When saving properties to a stream or loading them from a stream, the ISO 8859-1 character encoding is used. For characters that cannot be directly represented in this encoding, <a href="http://java.sun.com/docs/books/jls/html/3.doc.html#100850">Unicode escapes</a> are used; however, only a single 'u' character is allowed in an escape sequence. The native2ascii tool can be used to convert property files to and from other character encodings.
 * @see  <a href="../../../tooldocs/solaris/native2ascii.html">native2ascii tool for Solaris</a>
 * @see  <a href="../../../tooldocs/windows/native2ascii.html">native2ascii tool for Windows</a>
 * @author   Steven Pritko
 * @version  1.2, 1/29/04
 * @since    PUI 1.1
 */
public class CommentedProperties implements Serializable {

   private static final long serialVersionUID = 1578276490876354862L;

   public static final String AFTER_LAST_KEY   = "**(THE_LAST_KEY)**";
   public static final String BEFORE_FIRST_KEY = "**(THE_FIRST_KEY)**";


   private static final char[] HEX_DIGITS = {
      '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
   };

   private static final String WHITE_SPACE_CHARS = " \t\r\n\f";


   protected HashMap   theProperties = new HashMap();

   private   Entry     firstEntry = null;
   private   Entry     lastEntry  = null;

   /**
    * Creates an empty property list with no default values.
    */
   public CommentedProperties() {
   }


   /**
    * Calls the <tt>Hashtable</tt> method <code>put</code>. Provided for
    * parallelism with the <tt>getProperty</tt> method. Enforces use of
    * strings for property keys and values. The value returned is the
    * result of the <tt>Hashtable</tt> call to <code>put</code>.
    *
    * @param key the key to be placed into this property list.
    * @param value the value corresponding to <tt>key</tt>.
    * @return     the previous value of the specified key in this property
    *             list, or <code>null</code> if it did not have one.
    * @see #getProperty
    * @since    1.2
    */
   public String setProperty(String key, String value) {
      return setProperty( key, value, AFTER_LAST_KEY );
   }


   public String setProperty(String key, String value, List comments ) {
      String oldValue = setProperty( key, value, AFTER_LAST_KEY );
      setComments( key, comments );
      return oldValue;
   }

   public String setProperty(String key, String value, String afterKey, List comments ) {
      String oldValue = setProperty( key, value, afterKey );
      setComments( key, comments );
      return oldValue;
   }


   public String setProperty( String key, String value, String afterKey ) {
      String oldValue = null;
      Entry theEntry = (Entry)theProperties.get( key );
      if (theEntry != null) {
         oldValue = theEntry.value;
         theEntry.value = value;
      } else {
         Entry newEntry = new Entry( key, value );
         if ( afterKey == BEFORE_FIRST_KEY ) {
            prepend( newEntry );
         } else if ( afterKey == AFTER_LAST_KEY ) {
            append( newEntry );
         } else if ( theProperties.containsKey( afterKey ) ) {
            theEntry = (Entry)theProperties.get( afterKey );
            if ( theEntry == lastEntry ) {
               append( newEntry );
            } else {
               newEntry.next = theEntry.next;
               newEntry.previous = theEntry;
               theEntry.next.previous = newEntry;
               theEntry.next = newEntry;
            }

         } else {
            append( newEntry );
         }
         theProperties.put( key, newEntry );

      }
      return oldValue;
   }

   public String getProperty( String key ) {
      String value = null;
      Entry theEntry = (Entry)theProperties.get( key );
      if (theEntry != null) {
         value = theEntry.value;
      }
      return value;
   }

   public void setComments( String key, List comments ) {
      Entry theEntry = (Entry)theProperties.get( key );
      if (theEntry != null) {
         theEntry.comments = new ArrayList( comments.size() );
         theEntry.comments.addAll( comments );
      }
   }

   public void addComment( String key, String commentLine ) {
      Entry theEntry = (Entry)theProperties.get( key );
      if (theEntry != null) {
         if ( theEntry.comments == null ) {
            theEntry.comments = new ArrayList();
         }  
         if ( !commentLine.trim().startsWith( "#" ) ) {
            commentLine = "# " + commentLine; 
         }
         theEntry.comments.add( commentLine );
      }
   }

   public List getComments( String key ) {
      Entry theEntry = (Entry)theProperties.get( key );
      List comments = new ArrayList(0);
      if (theEntry != null) {
         if ( theEntry.comments != null ) {
            comments = new ArrayList( theEntry.comments.size() );
            comments.addAll( theEntry.comments );
         }
      }
      return comments;
   }

   public Iterator propertyNames() {
      return new CommentedPropertiesIterator( true );
   }

   public Iterator properties() {
      return new CommentedPropertiesIterator( false );
   }


   public void clear() {
      theProperties.clear();
      firstEntry = lastEntry = null; // Don't forget to empty the LinkedList
   }

   public int size() {
      return theProperties.size();
   }

   public boolean isEmpty() {
      return theProperties.isEmpty();
   }

   public Iterator iterator() {
      return propertyNames();
   }

   public boolean containsProperty( String key ) {
      return theProperties.containsKey( key );
   }

   public String removeProperty( String key ) {

      String oldValue = null;
      Entry theEntry = (Entry)theProperties.get( key );
      if (theEntry != null) {
         oldValue = theEntry.value;
         theProperties.remove( key );
         //Now update linked list pointers
         if ( theEntry == firstEntry ) {
            firstEntry = theEntry.next;
            if (firstEntry != null) {
               firstEntry.previous = null;
            }
         } else {
            theEntry.previous.next = theEntry.next;
         }

         if ( theEntry == lastEntry ) {
            lastEntry = theEntry.previous;
            if (lastEntry != null) {
               lastEntry.next = null;
            }
         } else {
            theEntry.next.previous = theEntry.previous;
         }
      }
      return oldValue;
   }

   public void load(InputStream inStream) throws IOException {
      if (inStream == null) {
         throw new IOException( "Unable to read from <null> stream." );
      }
      

      BufferedReader in = new BufferedReader(new InputStreamReader(inStream, "8859_1"));
      String lineRead;
      Entry currentEntry = new Entry();
      boolean lineContinued = false;
      String line = "";
      while ( (lineRead = in.readLine() ) != null ) {
         if ( lineContinued ) {
            line += lineRead;
            lineContinued = false;
         } else {
            line = lineRead;
         }

         int totalSize = line.length();
         int idx = skipWhiteSpace( line, 0 );
         if ( idx >= totalSize ) {
            currentEntry.addCommentLine( line );
            continue;
         }
         char thisChar = line.charAt( idx );
         if ( thisChar == '#' || thisChar == '!' ) {
            currentEntry.addCommentLine( line );
         } else {
            int keyStart = idx;
            int endIndex = skipEndWhiteSpace( line );
            lineContinued = isContinueLine( line, endIndex );
            if ( lineContinued ) {
               line = line.substring( 0, endIndex );
            } else {
               int keyEnd = keyStart;
               boolean endNotFound = true;

               while ( endNotFound && keyEnd <= endIndex ) {
                  thisChar = line.charAt(keyEnd);
                  if ( thisChar == '=' || thisChar == ':' ) {
                     if ( line.charAt( keyEnd - 1 ) != '\\' ) {
                        endNotFound = false;
                     } else {
                        keyEnd++;
                     }
                  } else {
                     keyEnd++;
                  }
               }
               if (endNotFound) {
                  // this is a bad line. Ignore.....
                  //System.out.println( "Unable to find key separator in line " + line );
                  continue;
               }
               String theKey = line.substring( keyStart, keyEnd );
               idx = keyEnd;  // Keep key/value separator index for later.
               keyEnd = skipEndWhiteSpace( theKey );
               currentEntry.key = theKey.substring( 0, keyEnd+1 );

               idx = skipWhiteSpace( line, idx+1 );
               // System.out.println("idx is " + idx);
               // System.out.println("endIndex is " + endIndex);
               if (idx > endIndex  ) {
                  // currentEntry.value = "   ";
               } else
               {
                 currentEntry.value = resolveLoadValue( line.substring( idx, endIndex + 1 ) );
               }
               
               append( currentEntry );
               theProperties.put( currentEntry.key, currentEntry );
               currentEntry = new Entry();
            }

         }

      }
      if ( currentEntry.comments != null ) {
         append( currentEntry ); // Make sure we get anything after last key/value 
      }
   }

   public void save( OutputStream out )  {
      try {
         store(out, null );
      } catch (IOException e) {
      }
   }
      
   public  void save( OutputStream out, String header )  {
      try {
         store(out, header);
      } catch (IOException e) {
      }
   }

   public void store(OutputStream out ) throws IOException {
      store( out, null );
   }

   public void store(OutputStream out, String header) throws IOException {
      //store( new OutputStreamWriter(out, "8859_1" ), header );
      PrintStream writer = new PrintStream( out );
      if (header != null) {
         writer.println( "#" + header );
         writer.println( "#" + new Date().toString() );
      }

      Entry theEntry = firstEntry;
      while (theEntry != null) {
         Iterator iter = theEntry.commentLines();
         while (iter.hasNext()) {
            writer.println( (String)iter.next() );
         }
         // TODO: process key/value for any additional encoding.
         if ( theEntry.key != null ) {
            writer.println( resolveSave( theEntry ) );
         }
         theEntry = theEntry.next;
      }
      writer.flush();

   }

   public void store( Writer out ) throws IOException {
      store( out, null );
   }

   public void store( Writer out, String header) throws IOException {
      PrintWriter writer = new PrintWriter( out );
      if (header != null) {
         writer.println( "#" + header );
         writer.println( "#" + new Date().toString() );
      }

      Entry theEntry = firstEntry;
      while (theEntry != null) {
         Iterator iter = theEntry.commentLines();
         while (iter.hasNext()) {
            writer.println( (String)iter.next() );
         }
         // TODO: process key/value for any additional encoding.
         if ( theEntry.key != null ) {
            writer.println( resolveSave( theEntry ) );
         }
         theEntry = theEntry.next;
      }
      writer.flush();
   }

   public void list() {
      list( false );
   }

   public void list( boolean withComments ) {
      list( System.out, withComments );
   }

   public void list(PrintStream out) {
      list( out, false );
   }

   public void list(PrintStream out, boolean withComments) {

      out.println("-- listing properties " + (withComments ? "( with comments )" : "" ) + " --");
      Entry theEntry = firstEntry;
      while (theEntry != null) {
         if ( withComments && theEntry.comments != null ) {
            Iterator iter = theEntry.commentLines();
            while (iter.hasNext()) {
               out.println( iter.next() );
            }
         }
         if (theEntry.key != null) {
            String val = theEntry.value;
            if (val.length() > 40) {
               val = val.substring(0, 37) + "...";
            }
            out.println( theEntry.key + "=" + val);
         }
         theEntry = theEntry.next;
      }
   }

   public void list(PrintWriter out ) {
      list( out, false );
   }

   public void list(PrintWriter out, boolean withComments ) {
      out.println("-- listing properties --");
      Entry theEntry = firstEntry;
      while (theEntry != null) {
         if ( withComments && theEntry.comments != null ) {
            Iterator iter = theEntry.commentLines();
            while (iter.hasNext()) {
               out.println( iter.next() );
            }
         }
         String val = theEntry.value;
         if (val.length() > 40) {
            val = val.substring(0, 37) + "...";
         }
         out.println( theEntry.key + "=" + val);
      }
      out.flush();
   }

   private String resolveLoadValue( String value  ) {
      StringBuffer  buffer = new StringBuffer( value.length() );
      if (value != null ) {
         for ( int i=0; i< value.length(); i++ ) {
            char thisChar = value.charAt( i );
            switch ( thisChar ) {
               case '\\':
                  thisChar = value.charAt( ++i );
                  switch (thisChar) {
                     case 'u':
                        int uniValue=0;
                        for (int uniIdx=0; uniIdx<4; uniIdx++) {
                           char uniChar = Character.toUpperCase( value.charAt( ++i ) );
                           if ( uniChar >= '0' && uniChar <= '9' ) {
                              uniValue = (uniValue << 4) + ( uniChar - '0');
                           } else if ( uniChar >= 'A' && uniChar <= 'F' ) {
                              uniValue = (uniValue << 4) + ( 10 + ( uniChar - 'A') );
                           } else {
                              throw new IllegalArgumentException( "Malformed \\uxxxx encoding." );
                           }
                        }
                        //System.out.println( "Processing unicoide char " + uniValue );
                        buffer.append((char)uniValue);
                        break;
                     case '\\':
                        buffer.append( '\\' );
                        break;
                     case 'r':
                        buffer.append( '\r' );
                        break;
                     case 'n':
                        buffer.append( '\n' );
                        break;
                     case 't':
                        buffer.append( '\t' );
                        break;
                     case 'f':
                        buffer.append( '\f' );
                        break;
                     default:
                        buffer.append( thisChar );  // any other char and its nothign special, just throw away the \
                        break;
                  }
                  break;
               default:
                  buffer.append( thisChar );
                  break;
            };
         }
      }
      return buffer.toString();
   }

   private String resolveSave( Entry theEntry ) {
      StringBuffer  buffer = new StringBuffer( );
      buffer.append( theEntry.key );
      buffer.append( "=" );
      String value = theEntry.value;
      if (value != null ) {
         for ( int i=0; i< value.length(); i++ ) {
            char thisChar = value.charAt( i );
            switch ( thisChar ) {
               case '\\':
                  buffer.append( "\\\\" );
                  break;
               case '\t':
                  buffer.append( "\\t" );
                  break;
               case '\r':
                  buffer.append( "\\r" );
                  break;
               case '\n':
                  buffer.append( "\\n" );
                  break;
               case '\f':
                  buffer.append( "\\f" );
                  break;
               default:
                  if ((thisChar < 0x0020) || (thisChar > 0x007e)) {
                     buffer.append("\\u");
                     buffer.append( HEX_DIGITS[ ((thisChar >> 12) & 0xf) ] );
                     buffer.append( HEX_DIGITS[ ((thisChar >>  8) & 0xf) ] );
                     buffer.append( HEX_DIGITS[ ((thisChar >>  4) & 0xf) ] );
                     buffer.append( HEX_DIGITS[ ( thisChar        & 0xf) ] );
                     //System.out.println( "Formatting as unicode : " + (int)thisChar );
                  } else {
                     buffer.append( thisChar );
                  }
                  break;
            }
         }
      }
      return buffer.toString();
   }

   private int skipWhiteSpace( String line, int startIndex ) {
      int end = line.length();
      while ( startIndex < end && WHITE_SPACE_CHARS.indexOf( line.charAt( startIndex ) ) != -1 ) startIndex++;
      return startIndex;
   }

   private int skipEndWhiteSpace( String line ) {
      return skipEndWhiteSpace( line, line.length() - 1 );
   }

   private int skipEndWhiteSpace( String line, int endIndex ) {
      while ( endIndex > 0  && WHITE_SPACE_CHARS.indexOf( line.charAt( endIndex ) ) != -1 ) endIndex--;
      return endIndex;
   }

   private boolean isContinueLine( String line, int endIndex ) {
      int count = 0;
      boolean continueLine = false;
      if ( line.charAt( endIndex ) == '\\' ) {
         while ( endIndex > 0 && line.charAt( endIndex-- ) == '\\' ) count++;
         continueLine = ((count % 2) == 1);  // if odd number, its a continue line
      }
      return continueLine;
   }

   private void prepend( Entry newEntry ) {
      if ( firstEntry != null ) {
         firstEntry.previous = newEntry;
      }
      newEntry.next = firstEntry;
      firstEntry = newEntry;

      if (lastEntry == null) {
         lastEntry = newEntry;
      }
   }

   private void append( Entry newEntry ) {
      if ( lastEntry != null ) {
         lastEntry.next = newEntry;
      }
      newEntry.previous = lastEntry;
      lastEntry = newEntry;

      if (firstEntry == null) {
         firstEntry = newEntry;
      }
   }


   private final static Iterator EMPTY_ITERATOR = new Iterator() {
         public boolean hasNext() {
            return false;
         }
         public Object next() {
            throw new NoSuchElementException( "There are no more elements." );
         }
         public void remove() {
            throw new UnsupportedOperationException( "Cannot remove elements via this Iterator." );
         }
   };


   /**
 *  
 */
private class Entry {

      String key      = null;
      String value    = null;
      List   comments = null;
      Entry  next     = null;
      Entry  previous = null;

      Entry() {}

      Entry( String k, String v ) {
         key = k;
         value = v;
      }

      void addCommentLine( String line ) {
         if (comments == null) {
            comments = new ArrayList();
         }
         comments.add( line );
      }

      Iterator commentLines() {
         return (comments == null ? EMPTY_ITERATOR : comments.iterator() );
      }

   }

   /**
 *  
 */
private class CommentedPropertiesIterator implements Iterator {

      boolean returnName = false;
      Entry   nextEntry;
                                            
      CommentedPropertiesIterator( boolean returnNames ) {
         this.returnName = returnNames;
         nextEntry = firstEntry;
      }


      public boolean hasNext() {
         return nextEntry != null;
      }

      public Object next() {
         String theValue = null;
         if (nextEntry == null) {
            throw new NoSuchElementException( "There are no more elements." );
            // throw Exception;
         } else {
            theValue = (returnName ? nextEntry.key : nextEntry.value );
            nextEntry = nextEntry.next;
         }
         return theValue;
      }

      public void remove() {
         throw new UnsupportedOperationException( "Cannot remove elements in a CommentedPropties file via an Iterator." );
      }
   }


}

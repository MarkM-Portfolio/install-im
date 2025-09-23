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
 * Created on Jul 5, 2005
 *
 */
package com.ibm.websphere.update.delta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Utility methods for dealing with jar files.
 * 
 * @author cooka
 *
 */
public class JarFileUtils {

    /**
     * Usage:
     * <p><code>java JarWriteTest &lt;jarfile&gt; &lt;entry-name&gt; &lt;file-location&gt;</code></p> 
     * 	
     * @param args
     */
    public static void main( String [] args )
    {
        if ( args == null )
            return;
        
        File jarfile = new File( args[0] );
        File toCompress = new File( args[2] );
        String entryName = args[1];
        JarFile jFile = null;
        
        if ( !toCompress.exists() )
        {
            System.out.println( "File \"" + args[2] + "\" does not exist!" );
            return;
        }
        
        //convert backslashes to forward slashes for entry name
        entryName = entryName.replace('\\','/');
        
        try 
        {
            System.out.println( updateArchive(jarfile, entryName, toCompress) );
        }
        catch ( IOException ioe ) 
        {
            System.err.println( "Error creating jar file: " + ioe );
        }
        
        
    }
    
    public static void inflateNestedEntry( File archive, String key, File tempFile ) throws IOException
    {
        int index = key.indexOf( "*" );
        ArrayList filesToDelete = null;
        String tempkey = key;
        File tempArchive = archive;

        while ( ( index = tempkey.indexOf( "*" ) ) >= 0)
        {
            
            File temp = File.createTempFile( "extractedJar", "jar", tempFile.getParentFile() );
            String entry = tempkey.substring( 0, index );
                
            JarFileUtils.inflateEntry( tempArchive, entry, temp );

            if ( filesToDelete == null )
            {
                filesToDelete = new ArrayList();
            }
                
            filesToDelete.add( temp );

            tempkey = tempkey.substring( index + 1 );
            tempArchive = temp;
        }
        
        inflateEntry( tempArchive, tempkey, tempFile );

        if ( filesToDelete != null)
        {
            Iterator i = filesToDelete.iterator();

            while ( i.hasNext() )
            {
                File f = (File) i.next();
                f.delete();
            }
        }
    }
    
    /**
     * Inflates the specified entry.
     * 
     * @param jFile the jarfile
     * @param entryName the entry to inflate
     * @param tempFile the location for the inflated entry 
     * @throws IOException 
     * @throws FileNotFoundException
     */
    public static void inflateEntry( File archive, String entryName, File tempFile ) throws IOException, FileNotFoundException
    {
        //System.out.println( "JarFileUtils.inflateEntry -> entry p1=" + archive + "; p2=" + entryName + "; p3=" + tempFile );
        
        try {
        
        if ( tempFile != null && !tempFile.exists() ) 
        {
            System.out.println( "The temp file wasn't found!! " );
            return;
        }
             
        FileOutputStream fos = new FileOutputStream( tempFile );
        ZipFile archiveFile = new ZipFile( archive );
        ZipEntry entry = scanEntry( archiveFile, entryName );
        InputStream is = null;
        long total = 0;
        
        //System.out.println( "Inflating entry: " + entryName );
        
        if ( entry != null )
        {
            is = archiveFile.getInputStream( entry );
            total = transferBytes( is, fos );
            is.close();
        }
        
        //System.out.println( "Size of compressed file: " + entry.getCompressedSize() );
        //System.out.println( "Size of inflated file: " + total );
        
        fos.close();
        archiveFile.close();

        } catch (Exception ex ) {
            System.out.println( "An error occurred inflating " + entryName + " : " + ex );
        }
        
    }
    
    /**
     * Scans to the specified entry.
     * 
     * @param zis
     * @param entryName
     * @return TODO
     */
    public static ZipEntry scanEntry( ZipFile zip, String entryName ) throws IOException
    {
        ZipEntry entry = null;
        Enumeration entries = zip.entries();
        boolean found = false;
        
        //System.out.println("Looking for entry \"" + entryName + "\"..." );
        
        while ( entries.hasMoreElements() )
        {
            entry = (ZipEntry) entries.nextElement();
            //System.out.println("Looking at entry \"" + entry.getName() + "\"..." );
            
            if ( entry.getName().equalsIgnoreCase( entryName ) )
            {
                found = true;
                //System.out.println( "Found \"" + entry.getName() + "\" ! " );
                break;
            }
        }
        
        if ( found )
            return entry;
        else
            return null;
    }

    /**
     * Update an entry in the specified jarfile.
     * 
     * @param archive the jarfile
     * @param entryName the entry name to update (usually the path)
     * @param updatedFile the file to compress and store at the specified entry 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static boolean updateArchive(File archive, String entryName, File updatedFile) throws IOException, FileNotFoundException 
    {
        ZipFile archiveFile = new ZipFile( archive );
        File tempFile = createTempFile( archive );
                
        ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( tempFile ) );
        FileInputStream fis = new FileInputStream( updatedFile );
        ZipEntry zEntry = new ZipEntry( entryName );
        
        System.out.println( "Read archive: " + archive.getAbsolutePath() );
        System.out.println( "Write archive: " + tempFile.getAbsolutePath() );
        
        System.out.println( "Size of updated file: " + updatedFile.length() );
                
        /* *************************
         * write our new entry first
         * *************************/
        //System.out.println( "Writing Entry: " + zEntry.getName() );
        
        zos.putNextEntry( zEntry );
        long total = transferBytes( fis, zos );
        
        System.out.println( "Size of compressed file: " + total );
        
        /* ******************************
         * clone the rest of the entries
         * ******************************/
        
        zEntry = null;
        Enumeration entries = archiveFile.entries();
        
        while ( entries.hasMoreElements() )
        {
            zEntry = (ZipEntry) entries.nextElement();
            
            if ( !zEntry.getName().equalsIgnoreCase( entryName ) )
            {
                //System.out.println( "Writing Entry: " + zEntry.getName() );
                
                ZipEntry newEntry = new ZipEntry( zEntry.getName() );
                zos.putNextEntry( newEntry );
                
                InputStream in = archiveFile.getInputStream( zEntry );
                transferBytes( in, zos );
                
                in.close();
                zos.flush();
            }
        }
        
        zos.flush();
        zos.close();
        fis.close();
        //jis.close();
        
        //inJar.close();
        archiveFile.close();
        
        return replaceFile( archive, tempFile );
    }
        
    /**
     * @param jFile
     * @param inJar
     * @param jos
     * @throws IOException
     * @throws FileNotFoundException
     */
   /* private static void cloneManifest(File temp, JarFile inJar, JarOutputStream jos) throws IOException, FileNotFoundException {
        ZipEntry manEntry = new ZipEntry( JarFile.MANIFEST_NAME );
        //File temp = File.createTempFile( "jarManifest", "mf", tempFile );
        inflateEntry( inJar, JarFile.MANIFEST_NAME, temp );
        FileInputStream fis2 = new FileInputStream( temp );
        
        jos.putNextEntry( manEntry );
        transferBytes( fis2, jos );
        
        fis2.close();
    }*/
    
    private static File createTempFile(File jFile) throws IOException
    {
        File temp = null;
        temp = File.createTempFile("jarUpdated", "jar", jFile.getParentFile());
        //temp = new File( "temp.jar" );
        return temp;
    }
    
    private static boolean replaceFile(File oldFile, File newFile)
    {
        String oldFileName = oldFile.getAbsolutePath();
        System.out.println("Replacing " + oldFile.getAbsolutePath() + " with " + newFile.getAbsolutePath() );
        oldFile.delete();
        return newFile.renameTo(new File(oldFileName));
    }
    
    /**
     * Transfers bytes read from the {@link java.io.InputStream} to the
     * specified {@link java.io.OutputStream}.
     * 
     * @param is the InputStream to read from
     * @param os the OutputStream to write to
     * @throws IOException
     */
    private static long transferBytes( InputStream is, OutputStream os ) throws IOException
    {
        byte barray [] = new byte [1024];
        int bytes, total = 0;
        
        while ( (bytes = is.read( barray ) ) > 0 )
        {
            os.write( barray, 0, bytes );
            total += bytes;
        }

        return total;
    }
    
}

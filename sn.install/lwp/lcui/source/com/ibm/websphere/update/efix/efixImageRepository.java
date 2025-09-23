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
package com.ibm.websphere.update.efix;


/*
 * eFix Install Image Repository
 *
 * This packages up a collection of efix install images
 * which are present in a bound directory.
 *
 * History 1.1, 9/6/02
 *
 * 09-Jul-2002 Initial Version
 */

/**
 * <p>Expected efix directory contents.  Any numberf of efix jars may be
 * present, as follows:</p>
 *
 * <pre>
 *     efixes\
 *
 *     efixes\efixPackage1.jar  // with 'efix1' & 'efix2'
 *     efixes\efixPackage2.jar  // with 'efix3'
 * </pre>
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.update.*;
import com.ibm.websphere.update.ioservices.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class efixImageRepository
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/6/02" ;

    // Instantor ... aka Constructor

    public efixImageRepository(IOService ioService,
			       String dtdDirName,
			       String repositoryDirName)
    {
        this.ioService = ioService;

        this.dtdDirName        = dtdDirName;
        this.repositoryDirName = repositoryDirName;

	this.didPrepare = false;

        this.jarFiles   = null;

        this.efixIds    = null;
        this.efixImages = null;
    }

    // IO access ...

    // Answer the IOService which is bound into the receiver.

    protected IOService ioService; // External helper for complex IO tasks.

    public IOService getIOService()
    {
        return ioService;
    }

    // IO access helper ... answer the names of the files beneath the
    // named parent directory.

    protected Vector getChildNames(String parentName)
        throws IOServicesException
    {
        return getIOService().getChildNames(parentName);
        // 'getChildNames' throws IOServicesException
    }

    // IO access helper ... answer the (partial) names of the entries
    // within the target named jar.  Entries having the specified prefix
    // are matched; the token following the specified prefix is nabbed
    // and added to the results vector.

    protected Vector getChildEntryNames(String jarName, String prefix)
        throws IOException
    {
        return getIOService().getChildEntryNames(jarName, prefix);
        // 'getChildEntryNames' throws IOException
    }

    // DTD Directory access ...

    protected String dtdDirName;  // Location of "efix.dtd", among other DTDs.

    /**
	 * @return  the dtdDirName
	 * @uml.property  name="dtdDirName"
	 */
    public String getDtdDirName()
    {
        return dtdDirName;
    }

    // Dir access ...

    // Answer the directory name on which the receiver is bound.

    protected String repositoryDirName;  // Directory containing efix image jars.

    /**
	 * @return  the repositoryDirName
	 * @uml.property  name="repositoryDirName"
	 */
    public String getRepositoryDirName()
    {
        return repositoryDirName;
    }

    // Container preparation ...

    protected boolean didPrepare;  // Tells if the contents have been prepared.

    public boolean didPrepare()
    {
	return didPrepare;
    }

    public void prepare()
	throws IOException, IOServicesException
    {
	if ( didPrepare )
	    return;

	didPrepare = true;

	setJarFiles();   // throws IOServicesException
        setEFixImages(); // throws IOException, IOServicesException
    }

    // Jar access ...

    // Answer the jars in the bound directory (as File objects).

    protected Vector jarFiles;  // The noted jar files: Vector<File>

    /**
	 * @return  the jarFiles
	 * @uml.property  name="jarFiles"
	 */
    public Vector getJarFiles()
    {
        return jarFiles;
    }

    // Compute and return the list of the names of the jars in the
    // bound directory.

    protected void setJarFiles()
        throws IOServicesException
    {
        jarFiles = getChildNames(getRepositoryDirName());
        // 'getChildNames' throws IOServicesException
    }

    // Image access ...

    protected Vector    efixIds;     // The ids of the resulting efix images: Vector<String>
    protected Hashtable efixImages;  // Container for efix images: Map<String, efixImage>

    public Vector getEFixIds()
    {
        return efixIds;
    }

    // Answer the table of child images computed from the child
    // jars.  Note that each child jar may have one or more
    // packaged efix image.

    public Hashtable getEFixImages()
    {
        return efixImages;
    }

    // Answer the efix image having the specified id.  Answer
    // null if no match is found.

    // Note that the answered efix image will not initially be
    // prepared: 'prepareEFix' and 'prepareComponents' must
    // be invoked on the efix image before full access is
    // available to the image.

    public efixImage getEFixImage(String efixId)
    {
        return (efixImage) getEFixImages().get(efixId);
    }

    // Child image computation ...

    // Compute the mapping of efixId --> efixImage,
    // and compute the listing of efix ids.

    protected void setEFixImages()
        throws IOException
    {
        Vector imageList = readImageList(); // throws IOException

        efixIds = new Vector();
        efixImages = new Hashtable();

        int numImages = imageList.size();

        for ( int imageNo = 0; imageNo < numImages; imageNo++ ) {
            efixImage nextImage = (efixImage) imageList.elementAt(imageNo);
            String nextEFixId = nextImage.getEFixId();

            efixIds.add(nextEFixId);
            efixImages.put(nextEFixId, nextImage);
        }
    }

    // Create and return the list of efix images which are
    // stored in the bound directory.

    protected Vector readImageList()
        throws IOException
    {
        Vector imageList = new Vector();

        Vector useJarFiles = getJarFiles(); // Vector<File>
        int numJars = useJarFiles.size();

        for ( int jarNo = 0; jarNo < numJars; jarNo++ ){
            File nextJarFile = (File) useJarFiles.elementAt(jarNo);
            String nextJarName =  nextJarFile.getAbsolutePath();

            readImages(nextJarName, imageList); // throws IOException
        }

        return imageList;
    }

    // Create efix images for each of the efixes packed in the
    // named jar.  Add these images to the supplied storage.

    protected void readImages(String jarName, Vector imageList)
        throws IOException
    {
        Vector efixIds = readEFixIds(jarName); // throws IOException

        int numIds = efixIds.size();

        for ( int idNo = 0; idNo < numIds; idNo++ ) {
            String nextId = (String) efixIds.elementAt(idNo);

            efixImage nextImage = new efixImage(getIOService(),
                                                getDtdDirName(),
                                                jarName,
                                                nextId);

            imageList.add(nextImage);
        }
    }

    // Note the ids of the efixes which are stored in the named jar.

    // efix image jars may have multiple efixes; each is
    // stored as a subdirectory of <EFIXES_DIR>.

    public static final String EFIXES_DIR = "efixes";

    protected Vector readEFixIds(String jarName)
        throws IOException
    {
        return getChildEntryNames(jarName, efixImage.EFIXES_DIR);
	       // throws IOException
    }
}

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
package com.ibm.websphere.update.ptf;


/*
 * UpdateImage Repository
 *
 * This packages up a collection of efix and ptf install images
 * which are present in a bound directory.
 *
 * History 1.2, 2/20/03
 *
 * 09-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
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
 *
 *     ptfs\ptfPackage1.jar     // with 'ptf1' & 'ptf2'
 *     ptfs\ptfPackage2.jar     // with 'ptf3'
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
public class ImageRepository {
	// Program versioning ...

	public static final String pgmVersion = "1.2" ;
	// Program versioning ...

	public static final String pgmUpdate = "2/20/03" ;

	public static final boolean isDebug;

	public static final String debugPropertyName = "com.ibm.websphere.update.ptf.debug" ;
	public static final String debugPropertyTrueValue = "true" ;

	static {
		String debugPropertyValue = System.getProperty(debugPropertyName);

		isDebug = (debugPropertyValue != null) && debugPropertyValue.equalsIgnoreCase(debugPropertyTrueValue);
	}

	public static void debug(Object arg) {
		if (isDebug)
			System.out.println(arg);
	}

	public static void debug(Object arg1, Object arg2) {
		if (isDebug) {
			System.out.print(arg1);
			System.out.println(arg2);
		}
	}

	public static final String PTF_IMAGES = "ptf";
	public static final String EFIX_IMAGES = "efix";

	// Instantor ... aka Constructor

	public ImageRepository(IOServiceFactory ioServiceFactory, String dtdDirName, String repositoryDirName, String imageType) {
		this.ioService = ioServiceFactory.createIOService();

		this.dtdDirName = dtdDirName;
		this.repositoryDirName = repositoryDirName;

		this.didPrepare = false;

		this.jarFiles = null;

		this.efixIds = null;
		this.efixImages = null;

		this.ptfIds = null;
		this.ptfImages = null;

		this.imageType = imageType;
		this.faultyJars = new Vector();

		if (isDebug) {
			debug("Image Repository:");
			debug("DTD Directory : ", dtdDirName);
			debug("Repository Dir: ", repositoryDirName);
		}

	}

	// IO access ...

	// Answer the IOService which is bound into the receiver.

	protected IOService ioService; // External helper for complex IO tasks.

	public IOService getIOService() {
		return ioService;
	}

	// IO access helper ... answer the names of the files beneath the
	// named parent directory.

	protected Vector getChildNames(String parentName) throws IOServicesException {
		return getIOService().getChildNames(parentName);
		// 'getChildNames' throws IOServicesException
	}

	// IO access helper ... answer the (partial) names of the entries
	// within the target named jar.  Entries having the specified prefix
	// are matched; the token following the specified prefix is nabbed
	// and added to the results vector.

	protected Vector getChildEntryNames(String jarName, String prefix) throws IOException {
		return getIOService().getChildEntryNames(jarName, prefix);
		// 'getChildEntryNames' throws IOException
	}

	// DTD Directory access ...

	protected String dtdDirName; // Location of "applied.dtd", among other DTDs.

	/**
	 * @return  the dtdDirName
	 * @uml.property  name="dtdDirName"
	 */
	public String getDtdDirName() {
		return dtdDirName;
	}

	// Dir access ...

	// Answer the directory name on which the receiver is bound.

	protected String repositoryDirName; // Directory containing efix image jars.

	/**
	 * @return  the repositoryDirName
	 * @uml.property  name="repositoryDirName"
	 */
	public String getRepositoryDirName() {
		return repositoryDirName;
	}

	protected String imageType = "";

	/**
	 * @return  the imageType
	 * @uml.property  name="imageType"
	 */
	public String getImageType() {
		return imageType;
	}

	// Container preparation ...

	protected boolean didPrepare; // Tells if the contents have been prepared.

	public boolean didPrepare() {
		return didPrepare;
	}

	/**
	 * Note: IOServicesException only denotes that the imageRepository input
	 * either does not exist, or is not a directory.
	 * 
	 * All other jar stream exceptions were refactored to be benign, and not fatal.
	 * This way, there wont be 1 single point of failure, in case there are other 
	 * jars in the repository that are clean.
	 * 
	 */

	public void prepare() throws IOServicesException {
		if (didPrepare)
			return;

		didPrepare = true;

		if (isDebug)
			debug("Image Repository: prepare ...");

		setJarFiles(); // throws IOServicesException

		if (getImageType().equals(PTF_IMAGES)) {
			setPTFImages(); // throws IOException, IOServicesException
		} else if (getImageType().equals(EFIX_IMAGES)) {
			setEFixImages(); // throws IOException, IOServicesException
		} else {
			setPTFImages(); // throws IOException, IOServicesException
			setEFixImages(); // throws IOException, IOServicesException
		}

		if (isDebug)
			debug("Image Repository: prepare ... complete");
	}

	// Jar access ...

	// Answer the jars in the bound directory (as File objects).

	protected Vector jarFiles; // The noted jar files: Vector<File>

	/**
	 * @return  the jarFiles
	 * @uml.property  name="jarFiles"
	 */
	public Vector getJarFiles() {
		return jarFiles;
	}

	// Compute and return the list of the names of the jars in the
	// bound directory.

	protected void setJarFiles() throws IOServicesException {

		if (isDebug)
			debug("Image Repository: setJarFiles ...");

		jarFiles = getChildNames(getRepositoryDirName());
		// 'getChildNames' throws IOServicesException

      /*
      if (isDebug) {
			debug("Image Repository: JarFiles :");

         for (int i=0;i<jarFiles.size(); i++) {
            debug("\t" + jarFiles.elementAt(i) );
         }
      }

      */
		if (isDebug)
			debug("Image Repository: setJarFiles ... complete");

	}

	// Image access ...

	protected Vector efixIds; // The ids of the resulting efix images: Vector<String>
	protected Hashtable efixImages; // Container for efix images: Map<String, efixImage>

	public Vector getEFixIds() {
		return efixIds;
	}

	// Answer the table of child images computed from the child
	// jars.  Note that each child jar may have one or more
	// packaged efix image.

	public Hashtable getEFixImages() {
		return efixImages;
	}

	// Answer the efix image having the specified id.  Answer
	// null if no match is found.

	// Note that the answered efix image will not initially be
	// prepared: 'prepareEFix' and 'prepareComponents' must
	// be invoked on the efix image before full access is
	// available to the image.

	public EFixImage getEFixImage(String efixId) {
		return (EFixImage) getEFixImages().get(efixId);
	}

	// Child image computation ...

	// Compute the mapping of efixId --> EFixImage,
	// and compute the listing of efix ids.

	protected void setEFixImages() {

		if (isDebug)
			debug("Image Repository: setEFixImages ...");

		Vector imageList = readEFixImageList(); // throws IOException
      /*
      if (isDebug) {
			debug("Image Repository: image List :");
         for (int i =0; i<imageList.size(); i++) {
            EFixImage fix =(EFixImage)imageList.elementAt( i ) ;
            debug("\t Id:" + fix.getEFixId() + "  Jar:" + fix.getJarName() );
         }

      }
      */
		efixIds = new Vector();
		efixImages = new Hashtable();

		int numImages = imageList.size();

		for (int imageNo = 0; imageNo < numImages; imageNo++) {
			EFixImage nextImage = (EFixImage) imageList.elementAt(imageNo);
			String nextEFixId = nextImage.getEFixId();

			if (isDebug)
				debug("Image Repository: setEFixImages ... efix ID: ", nextEFixId);

			efixIds.add(nextEFixId);
			efixImages.put(nextEFixId, nextImage);
		}

		if (isDebug)
			debug("Image Repository: setEFixImages ... complete");
	}

	// Create and return the list of efix images which are
	// stored in the bound directory.

	protected Vector readEFixImageList() {
		Vector imageList = new Vector();

		Vector useJarFiles = getJarFiles(); // Vector<File>
		int numJars = useJarFiles.size();

		for (int jarNo = 0; jarNo < numJars; jarNo++) {
			File nextJarFile = (File) useJarFiles.elementAt(jarNo);
			String nextJarName = nextJarFile.getAbsolutePath();

			readEFixImages(nextJarName, imageList); // throws IOException
		}

		return imageList;
	}

	// Create efix images for each of the efixes packed in the
	// named jar.  Add these images to the supplied storage.

	protected void readEFixImages(String jarName, Vector imageList) {
		try {
			Vector efixIds = readEFixIds(jarName); // throws IOException

			int numIds = efixIds.size();

			for (int idNo = 0; idNo < numIds; idNo++) {
				String nextId = (String) efixIds.elementAt(idNo);

				EFixImage nextImage = new EFixImage(getIOService(), getDtdDirName(), jarName, nextId);

				imageList.add(nextImage);
			}

		} catch (IOException ioe) {
         ioe.printStackTrace();
			faultyJars.add(jarName);
		}
	}

	// Note the ids of the efixes which are stored in the named jar.

	// efix image jars may have multiple efixes; each is
	// stored as a subdirectory of <EFIXES_DIR>.

	public static final String EFIXES_DIR = "efixes";

	protected Vector readEFixIds(String jarName) throws IOException {
		return getChildEntryNames(jarName, EFixImage.EFIXES_DIR);
		// throws IOException
	}

	// PTF Image access ...

	protected Vector ptfIds; // The ids of the resulting ptf images: Vector<String>
	protected Hashtable ptfImages; // Container for ptf images: Map<String, ptfImage>

	public Vector getPTFIds() {
		return ptfIds;
	}

	// Answer the table of child images computed from the child
	// jars.  Note that each child jar may have one or more
	// packaged ptf image.

	public Hashtable getPTFImages() {
		return ptfImages;
	}

	// Answer the ptf image having the specified id.  Answer
	// null if no match is found.

	// Note that the answered ptf image will not initially be
	// prepared: 'preparePTF' and 'prepareComponents' must
	// be invoked on the ptf image before full access is
	// available to the image.

	public PTFImage getPTFImage(String ptfId) {
		return (PTFImage) getPTFImages().get(ptfId);
	}

	// Child image computation ...

	// Compute the mapping of ptfId --> PTFImage,
	// and compute the listing of ptf ids.

	protected void setPTFImages() {
		if (isDebug)
			debug("Image Repository: setPTFImages ...");

		Vector imageList = readPTFImageList(); // throws IOException

		ptfIds = new Vector();
		ptfImages = new Hashtable();

		int numImages = imageList.size();

		for (int imageNo = 0; imageNo < numImages; imageNo++) {
			PTFImage nextImage = (PTFImage) imageList.elementAt(imageNo);
			String nextPTFId = nextImage.getPTFId();

			if (isDebug)
				debug("Image Repository: setPTFImages ... PTF Id: ", nextPTFId);

			ptfIds.add(nextPTFId);
			ptfImages.put(nextPTFId, nextImage);
		}

		if (isDebug)
			debug("Image Repository: setPTFImages ... complete");
	}

	// Create and return the list of ptf images which are
	// stored in the bound directory.

	protected Vector readPTFImageList() {
		Vector imageList = new Vector();

		Vector useJarFiles = getJarFiles(); // Vector<File>
		int numJars = useJarFiles.size();

		for (int jarNo = 0; jarNo < numJars; jarNo++) {
			File nextJarFile = (File) useJarFiles.elementAt(jarNo);
			String nextJarName = nextJarFile.getAbsolutePath();

			readPTFImages(nextJarName, imageList); // throws IOException
		}

		return imageList;
	}

	// Create ptf images for each of the ptfs packed in the
	// named jar.  Add these images to the supplied storage.

	protected void readPTFImages(String jarName, Vector imageList) {
		try {
			Vector ptfIds = readPTFIds(jarName); // throws IOException

			int numIds = ptfIds.size();

			for (int idNo = 0; idNo < numIds; idNo++) {
				String nextId = (String) ptfIds.elementAt(idNo);

				PTFImage nextImage = new PTFImage(getIOService(), getDtdDirName(), jarName, nextId);

				imageList.add(nextImage);
			}
		} catch (IOException ioe) {
			faultyJars.add(jarName);
		}
	}

	// Note the ids of the ptfs which are stored in the named jar.

	// ptf image jars may have multiple ptfs; each is
	// stored as a subdirectory of <PTFS_DIR>.

	public static final String PTFS_DIR = "ptfs";

	protected Vector readPTFIds(String jarName) throws IOException {
		return getChildEntryNames(jarName, PTFImage.PTFS_DIR);
		// throws IOException
	}

	protected Vector faultyJars;

	/**
	 * @return  the faultyJars
	 * @uml.property  name="faultyJars"
	 */
	public Vector getFaultyJars() {
		return faultyJars;
	}

	public boolean hasFaultyJars() {
		return faultyJars.size() > 0;
	}
}

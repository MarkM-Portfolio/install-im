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

package com.ibm.websphere.update.util.was;

import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.FileReader;
import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

// <d63896>
/**
 *  
 */
public class WASNodeType extends DefaultHandler {

   boolean standaloneCell = true;
   boolean cellChecked = false;
   String  cellName;

   public WASNodeType () {
      super();
      String  managed = WPConfig.getProperty( WPConstants.PROP_WAS_MANAGED_NODE );
      if ( managed != null ) {
         cellChecked = true;
         standaloneCell = managed.equals( "0" );
      }
   }

   /**
 * @return  the standaloneCell
 * @uml.property  name="standaloneCell"
 */
public static boolean isStandaloneCell() {
      WASNodeType checker = new WASNodeType();
      return checker.isStandaloneNode();
   }

   public boolean isManagedNode() {
      return !isStandaloneNode();
   }

   public boolean isStandaloneNode() {
      if (cellChecked) return standaloneCell;
      try {
         XMLReader xr = XMLReaderFactory.createXMLReader();
         xr.setContentHandler(this);
         xr.setErrorHandler(this);

         String wasHome = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ).replace( '\\', '/');
         cellName = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );

         // Construct path to the cell.xml file for this cell.
         String cellFile = wasHome + "/config/cells/" + this.cellName + "/cell.xml";

         FileReader r = new FileReader( cellFile );
         xr.parse(new InputSource(r));  // Parse the XMLfile, on return all parsing is done.
         cellChecked = true;
         WPConfig.setProperty( WPConstants.PROP_WAS_MANAGED_NODE, standaloneCell ? "0" : "1" );
      } catch ( Exception e) {
      }
      return standaloneCell;
   }

   ////////////////////////////////////////////////////////////////////
   // Event handlers.
   ////////////////////////////////////////////////////////////////////
   public void startElement (String uri, String name, String qName, Attributes atts) {
      if ( qName.equals("topology.cell:Cell" ) ) {
         String cellType = atts.getValue( "cellType" );
         String cellName = atts.getValue( "name" );
         if (this.cellName.equals( cellName ) ) {
            standaloneCell = "STANDALONE".equals( cellType );
         }
      }
   }

}
// </d63896>


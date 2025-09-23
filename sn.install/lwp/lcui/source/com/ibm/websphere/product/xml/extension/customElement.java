/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.xml.extension;

/*
 * Extension Custom Element Bean
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;
import org.xml.sax.*;

/**
 *  
 */
public class customElement extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public customElement(String elementName, Attributes attributes)
    {
        super();

        this.elementName = elementName;
        this.attributes = attributes;
        this.customElements = new ArrayList();
        this.elementText = null;
    }

    protected String elementName;

    /**
	 * @return  the elementName
	 * @uml.property  name="elementName"
	 */
    public String getElementName()
    {
        return elementName;
    }

    protected Attributes attributes;

    /**
	 * @return  the attributes
	 * @uml.property  name="attributes"
	 */
    public Attributes getAttributes()
    {
        return attributes;
    }

    protected ArrayList customElements;
    
    /**
	 * @return  the customElements
	 * @uml.property  name="customElements"
	 */
    public List getCustomElements()
    {
        return customElements;
    }

    public int getCustomElementCount()
    {
        return customElements.size();
    }

    public customElement getCustomElement(int index)
    {
        return (customElement) customElements.get(index);
    }

    public customElement addCustomElement(String elementName, Attributes attributes)
    {
        customElement newElement = new customElement(elementName, attributes);

        addCustomElement( newElement);

        return newElement;
    }

    public void addCustomElement(customElement customElement)
    {
        customElements.add(customElement);
    }

    public void removeCustomElement(int index)
    {
        customElements.remove(index);
    }

    protected String elementText;

    /**
	 * @return  the elementText
	 * @uml.property  name="elementText"
	 */
    public String getElementText()
    {
        return elementText;
    }

    /**
	 * @param elementText  the elementText to set
	 * @uml.property  name="elementText"
	 */
    public void setElementText(String elementText)
    {
        this.elementText = elementText;
    }
}

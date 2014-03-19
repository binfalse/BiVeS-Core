/**
 * 
 */
package de.unirostock.sems.bives.ds.rdf;

import org.jdom2.Attribute;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class RDFDescription representing an rdf:Description element.
 *
 * @author Martin Scharm
 */
public class RDFDescription
{
	
	/** The node rooting the RDF description subtree. */
	private DocumentNode node; 
	
	/**
	 * Instantiates a new RDF object.
	 *
	 * @param node the document node rooting the RDF block
	 */
	public RDFDescription (DocumentNode node)
	{
		this.node = node;
	}
	
	/**
	 * Gets the node.
	 *
	 * @return the node rooting the RDF block
	 */
	public DocumentNode getNode ()
	{
		return node;
	}
	
	/**
	 * Gets the about id.
	 *
	 * @return the about
	 */
	public String getAbout ()
	{
		String about = node.getAttributeValue ("about");
		if (about != null && about.length () > 0 && about.charAt (0) == '#')
			return about.substring (1);
		return about;
	}
	
	/**
	 * Sets the about attribute.
	 *
	 * @param id the new about
	 */
	public void setAbout (String id)
	{
		Attribute attr = node.getAttribute ("about");
		if (attr != null)
		{
			attr.setValue (id);
			return;
		}
		else
		{
			attr = new Attribute ("about", id, node.getNameSpace ());
			node.setAttribute (attr);
		}
	}
	
}

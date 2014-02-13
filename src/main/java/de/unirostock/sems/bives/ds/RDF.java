/**
 * 
 */
package de.unirostock.sems.bives.ds;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class RDF representing an RDF subtree.
 *
 * @author Martin Scharm
 */
public class RDF
{
	
	/** The node rooting the RDF subtree. */
	private DocumentNode node; 
	
	/**
	 * Instantiates a new RDF object.
	 *
	 * @param node the document node rooting the RDF block
	 */
	public RDF (DocumentNode node)
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
}

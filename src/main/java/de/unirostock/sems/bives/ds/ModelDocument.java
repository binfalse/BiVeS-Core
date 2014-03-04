/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.net.URI;

import de.unirostock.sems.xmlutils.ds.TreeDocument;


/**
 * The Class ModelDocument representing a document encoding for a model.
 *
 * @author Martin Scharm
 */
public abstract class ModelDocument
{
	
	/** The document storing this model. */
	protected TreeDocument	doc;
	
	/**
	 * Instantiates a new model document.
	 *
	 * @param doc the corresponding XML document
	 */
	public ModelDocument (TreeDocument doc)
	{
		this.doc = doc;
	}
	
	
	/**
	 * Gets the base URI. (used to resolve relative paths e.g. for imports)
	 * 
	 * @return the base URI
	 */
	public URI getBaseUri ()
	{
		return doc.getBaseUri ();
	}
	
	
	/**
	 * Gets the tree document.
	 * 
	 * @return the tree document
	 */
	public TreeDocument getTreeDocument ()
	{
		return doc;
	}
}

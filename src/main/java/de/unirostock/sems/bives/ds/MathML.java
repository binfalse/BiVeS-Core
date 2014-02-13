/**
 * 
 */
package de.unirostock.sems.bives.ds;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class MathML to host MathML trees.
 *
 * @author Martin Scharm
 */
public class MathML
{
	
	/** The node rooting the MathML subtree. */
	private DocumentNode math;
	
	/**
	 * Instantiates a new MathML object.
	 *
	 * @param math the node rooting the MathML subtree
	 */
	public MathML (DocumentNode math)
	{
		this.math = math;
	}
	
	/**
	 * Gets the document node that roots the MathML subtree.
	 *
	 * @return the document node
	 */
	public DocumentNode getDocumentNode ()
	{
		return math;
	}
}

/**
 * 
 */
package de.unirostock.sems.bives.ds.rdf;

import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class RDF representing an RDF subtree.
 *
 * @author Martin Scharm
 */
public class RDF
{
	
	/** The node rooting the RDF subtree. */
	private DocumentNode node; 
	
	private List<RDFDescription> descriptions;
	
	/**
	 * Instantiates a new RDF object.
	 *
	 * @param node the document node rooting the RDF block
	 */
	public RDF (DocumentNode node)
	{
		this.node = node;
		descriptions = new ArrayList<RDFDescription> ();
		
		List<TreeNode> kids= node.getChildrenWithTag ("Description");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;
			descriptions.add (new RDFDescription ((DocumentNode) kid));
		}
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
	 * Gets the descriptions.
	 *
	 * @return the descriptions
	 */
	public List<RDFDescription> getDescriptions ()
	{
		return descriptions;
	}
}

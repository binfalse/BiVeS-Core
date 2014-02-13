/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;



/**
 * The Class Xhtml representing a sequence of XHTML subtrees.
 * 
 * <p>
 * e.g. the following code can be represented in a sequence of two XHTML nodes
 * in this object:
 * </p>
 * 
 * <pre>
 * &lt;notes&gt;
 * 	&lt;p&gt;
 * 		some text
 * 	&lt;/p&gt;
 * 	&lt;ul&gt;
 * 		&lt;li&gt;
 * 			list item
 * 		&lt;/li&gt;
 * 	&lt;/ul&gt;
 * &lt;/notes&gt;
 * </pre>
 * 
 * <p>
 * Of course, you can also create two Xhtml objects, each storing one node.
 * Whatever you prefer.
 * </p>
 * 
 * @author Martin Scharm
 */
public class Xhtml
{
	
	/** The nodes. */
	private List<TreeNode>	nodes;
	
	
	/**
	 * Instantiates a new Xhtml object.
	 */
	public Xhtml ()
	{
		nodes = new ArrayList<TreeNode> ();
	}
	
	
	/**
	 * Adds an XHTML subtree.
	 * 
	 * @param node
	 *          the node that roots the subtree
	 */
	public void addXhtml (TreeNode node)
	{
		nodes.add (node);
	}
	
	
	/**
	 * Prints the sequence of subtrees, all in one string.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuilder ret = new StringBuilder ();
		for (TreeNode node : nodes)
			if (node.getType () == TreeNode.DOC_NODE)
				ret.append (DocumentTools.printPrettySubDoc ((DocumentNode) node));
			else if (node.getType () == TreeNode.TEXT_NODE)
				ret.append (((TextNode) node).getText ());
		return ret.toString ();
	}
	
}

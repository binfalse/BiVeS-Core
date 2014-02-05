/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.util.Vector;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;


/**
 * @author martin
 *
 */
public class Xhtml
{
	// html is beneath
	private Vector<TreeNode> nodes;
	
	public Xhtml ()
	{
		nodes = new Vector<TreeNode> ();
	}
	
	public void addXhtml (TreeNode node)
	{
		nodes.add (node);
	}
	
	public String toString ()
	{
		String ret = "";
		for (TreeNode node : nodes)
		{
			if (node.getType () == TreeNode.DOC_NODE)
				ret += DocumentTools.printPrettySubDoc ((DocumentNode) node);
			else if (node.getType () == TreeNode.TEXT_NODE)
				ret += ((TextNode) node).getText ();
		}
		return ret;
	}
	
}

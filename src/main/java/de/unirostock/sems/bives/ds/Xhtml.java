/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
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
	
	/** The node. */
	private DocumentNode	node;
	
	
	/**
	 * Instantiates a new Xhtml object.
	 */
	public Xhtml ()
	{
		node = null;
	}
	
	
	/**
	 * Adds an XHTML subtree.
	 * 
	 * @param node
	 *          the node that roots the subtree
	 */
	public void setXhtml (DocumentNode node)
	{
		this.node = node;
	}
	
	
	/**
	 * Prints the sequence of subtrees, all in one string.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		if (node == null)
			return "";
		return DocumentTools.printPrettySubDoc (node);
	}


	public void reportModification (SimpleConnectionManager conMgmt,
		Xhtml a, Xhtml b, MarkupElement me)
	{
		if (a.node.getModification () == 0 && b.node.getModification () == 0)
			return;
		

		// if the nodes are simply moved..
		if (a.node.getModification () == TreeNode.SWAPPEDKID && b.node.getModification () == TreeNode.SWAPPEDKID)
			return;
		
		String valA = a.toString ();
		String valB = b.toString ();
		
		if (valA.equals (valB))
			return;
		
		me.addValue (MarkupDocument.supplemental (
			MarkupDocument.delete ("previous notes: <pre>"+valA+"</pre>") + " "+MarkupDocument.rightArrow ()+" " + MarkupDocument.insert ("new notes: <pre>"+valB+"</pre>")));
	}


	public void reportInsert (MarkupElement me)
	{
		me.addValue (MarkupDocument.supplemental (MarkupDocument.insert ("inserted notes: <pre>"+toString ()+"</pre>")));
	}


	public void reportDelete (MarkupElement me)
	{
		me.addValue (MarkupDocument.supplemental (MarkupDocument.delete ("deleted notes: <pre>"+toString ()+"</pre>")));
	}
	
}

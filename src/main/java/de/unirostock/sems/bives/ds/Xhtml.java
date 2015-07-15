/**
 * 
 */
package de.unirostock.sems.bives.ds;

import org.jdom2.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.BivesTools;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;



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


	/**
	 * Report a modification between to Xhtml objects.
	 *
	 * @param conMgmt the connection
	 * @param a the original version
	 * @param b the modified version
	 * @param me the markup element
	 */
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
		
		// rerun bives
		try
		{
			// parse trees
			TreeDocument tdA = new TreeDocument (XmlTools.readDocument (valA), null);
			TreeDocument tdB = new TreeDocument (XmlTools.readDocument (valB), null);
			
			// rerun bives
			RegularDiff differ = new RegularDiff (tdA, tdB);
			differ.mapTrees ();
			Patch p = differ.getPatch ();
			

			
			Element deletes = p.getDeletes ();
			for (Element el : deletes.getChildren ())
			{
				
				if (el.getName ().equals ("node"))
				{
					BivesTools.markDeleted ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
				}
				else if (el.getName ().equals ("attribute"))
				{
					BivesTools.markUpdated ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
				}
				else
				{
					BivesTools.markDeleted (((TextNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath"))).getParent ());
				}
			}

			Element inserts = p.getInserts ();
			for (Element el : inserts.getChildren ())
			{
				
				if (el.getName ().equals ("node"))
				{
					BivesTools.markInserted ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
				}
				else if (el.getName ().equals ("attribute"))
				{
					BivesTools.markUpdated ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
				}
				else
				{
					BivesTools.markInserted (((TextNode) tdB.getNodeByPath (el.getAttributeValue ("newPath"))).getParent ());
				}
			}
			
			Element moves = p.getMoves ();
			for (Element el : moves.getChildren ())
			{
				if (el.getName ().equals ("node"))
				{
					BivesTools.markMoved ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
					BivesTools.markMoved ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
				}
				else
				{
					BivesTools.markMoved (((TextNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath"))).getParent ());
					BivesTools.markMoved (((TextNode) tdB.getNodeByPath (el.getAttributeValue ("newPath"))).getParent ());
				}
			}
			
			Element updates = p.getUpdates ();
			for (Element el : updates.getChildren ())
			{
				if (el.getName ().equals ("attribute"))
				{
					BivesTools.markUpdated ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
					BivesTools.markUpdated ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
				}
				else
				{
					BivesTools.markUpdated ((TextNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
					BivesTools.markUpdated ((TextNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
				}
			}
			

			me.addValue ("modified notes: <pre>"
				+ XmlTools.prettyPrintDocument (DocumentTools.getDoc (tdA)) + "</pre> to <pre>"
				+ XmlTools.prettyPrintDocument (DocumentTools.getDoc (tdB))+"</pre>");
			
			return;
			
		}
		catch (Exception e)
		{
			LOGGER.error (e, "was not able to rerun bives for the text nodes");
		}
		
		me.addValue (MarkupDocument.supplemental (
			MarkupDocument.delete ("previous notes: <pre>"+valA+"</pre>") + " "+MarkupDocument.rightArrow ()+" " + MarkupDocument.insert ("new notes: <pre>"+valB+"</pre>")));
	}


	/**
	 * Report this object as inserted.
	 *
	 * @param me the MarkupElement
	 */
	public void reportInsert (MarkupElement me)
	{
		me.addValue (MarkupDocument.supplemental (MarkupDocument.insert ("inserted notes: <pre>"+toString ()+"</pre>")));
	}


	/**
	 * Report this object as deleted.
	 *
	 * @param me the MarkupElement
	 */
	public void reportDelete (MarkupElement me)
	{
		me.addValue (MarkupDocument.supplemental (MarkupDocument.delete ("deleted notes: <pre>"+toString ()+"</pre>")));
	}
	
}

/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * The Class BivesTools providing some utils for document comparison.
 * 
 * @author Martin Scharm
 */
public class BivesTools
{
	
	/**
	 * Generate some MathML difference report for Markup Documents .
	 * 
	 * @param a
	 *          the node rooting the MathML in document A
	 * @param b
	 *          the node rooting the MathML in document B
	 * @param markupElement
	 *          the markup element
	 */
	public static void genMathMarkupStats (DocumentNode a, DocumentNode b,
		MarkupElement markupElement)
	{
		if (a == null && b == null)
			return;
		
		try
		{
			if (a == null)
			{
				markupElement.addValue ("inserted math: "
					+ MarkupDocument.math (MarkupDocument.insert (DocumentTools.transformMathML (b)), false));
			}
			else if (b == null)
			{
				markupElement.addValue ("deleted math: "
					+ MarkupDocument.math (MarkupDocument.delete (DocumentTools.transformMathML (a)), true));
			}
			else if (a.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
			{
				// we now highlight changes in mathml -> we need to convert the content mathml to presentation mathml, rerun comparision and attach classes to changed subtrees..
				
				// convert both
				String mathA = DocumentTools.transformMathML (a);
				String mathB = DocumentTools.transformMathML (b);
				
				// parse trees
				TreeDocument tdA = new TreeDocument (XmlTools.readDocument (mathA), null);
				TreeDocument tdB = new TreeDocument (XmlTools.readDocument (mathB), null);
				
				// rerun bives
				RegularDiff differ = new RegularDiff (tdA, tdB);
				differ.mapTrees ();
				Patch p = differ.getPatch ();
				
				Element deletes = p.getDeletes ();
				for (Element el : deletes.getChildren ())
				{
					
					if (el.getName ().equals ("node"))
					{
						deleteMath ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
					}
					else if (el.getName ().equals ("attribute"))
					{
						updateMath ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
					}
					else
					{
						deleteMath (((TextNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath"))).getParent ());
					}
				}

				Element inserts = p.getInserts ();
				for (Element el : inserts.getChildren ())
				{
					
					if (el.getName ().equals ("node"))
					{
						insertMath ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
					}
					else if (el.getName ().equals ("attribute"))
					{
						updateMath ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
					}
					else
					{
						insertMath (((TextNode) tdB.getNodeByPath (el.getAttributeValue ("newPath"))).getParent ());
					}
				}
				
				Element moves = p.getMoves ();
				for (Element el : moves.getChildren ())
				{
					if (el.getName ().equals ("node"))
					{
						moveMath ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
						moveMath ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
					}
					else
					{
						moveMath (((TextNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath"))).getParent ());
						moveMath (((TextNode) tdB.getNodeByPath (el.getAttributeValue ("newPath"))).getParent ());
					}
				}
				
				Element updates = p.getUpdates ();
				for (Element el : updates.getChildren ())
				{
					if (el.getName ().equals ("attribute"))
					{
						updateMath ((DocumentNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
						updateMath ((DocumentNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
					}
					else
					{
						updateMath ((TextNode) tdA.getNodeByPath (el.getAttributeValue ("oldPath")));
						updateMath ((TextNode) tdB.getNodeByPath (el.getAttributeValue ("newPath")));
					}
				}
				
				
				markupElement.addValue ("modified math: "
					+ MarkupDocument.math (XmlTools.prettyPrintDocument (DocumentTools.getDoc (tdA)), true) + " to "
					+ MarkupDocument.math (XmlTools.prettyPrintDocument (DocumentTools.getDoc (tdB)), false));
			}
		}
		catch (Exception e)
		{
			LOGGER.error (e, "error generating math");
			markupElement.addValue ("error generating math: " + e.getMessage ());
		}
	}
	
	private static void deleteMath (DocumentNode dn)
	{
		dn.setAttribute ("class", Typesetting.CSS_CLASS_DELETE);
	}
	
	private static void insertMath (DocumentNode dn)
	{
		dn.setAttribute ("class", Typesetting.CSS_CLASS_INSERT);
	}
	
	private static void moveMath (DocumentNode dn)
	{
		// do not overwrite class -> delete/insert has higher priority
		if (dn.getAttribute ("class") == null)
			dn.setAttribute ("class", Typesetting.CSS_CLASS_MOVE);
	}
	
	private static void updateMath (TextNode dn)
	{
		// do not overwrite class -> delete/insert has higher priority
		if (dn.getParent ().getAttribute ("class") == null)
			dn.getParent ().setAttribute ("class", Typesetting.CSS_CLASS_UPDATE);
	}
	
	private static void updateMath (DocumentNode dn)
	{
		// do not overwrite class -> delete/insert has higher priority
		if (dn.getAttribute ("class") == null)
			dn.setAttribute ("class", Typesetting.CSS_CLASS_UPDATE);
	}
	
	
	/**
	 * Generate some Attribute difference report for Markup Documents.
	 * 
	 * @param a
	 *          the nodes carrying the attributes in document A
	 * @param b
	 *          the nodes carrying the attributes in document B
	 * @param markupElement
	 *          the markup element
	 */
	public static void genAttributeMarkupStats (DocumentNode a, DocumentNode b,
		MarkupElement markupElement)
	{
		if (a == null || b == null)
			return;
		
		Set<String> allAttr = new HashSet<String> ();
		allAttr.addAll (a.getAttributes ());
		allAttr.addAll (b.getAttributes ());
		for (String attr : allAttr)
		{
			String aA = a.getAttributeValue (attr), bA = b.getAttributeValue (attr);
			if (aA == null)
				markupElement.addValue ("Attribute " + MarkupDocument.attribute (attr)
					+ " was inserted: " + MarkupDocument.insert (bA));
			else if (bA == null)
				markupElement.addValue ("Attribute " + MarkupDocument.attribute (attr)
					+ " was deleted: " + MarkupDocument.delete (aA));
			else if (!aA.equals (bA))
				markupElement.addValue ("Attribute " + MarkupDocument.attribute (attr)
					+ " has changed: " + MarkupDocument.delete (aA) + " "
					+ MarkupDocument.rightArrow () + " " + MarkupDocument.insert (bA));
		}
	}
}

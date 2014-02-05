/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.util.HashSet;
import java.util.Set;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;



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
					+ MarkupDocument.insert (DocumentTools.transformMathML (b)));
			}
			else if (b == null)
			{
				markupElement.addValue ("deleted math: "
					+ MarkupDocument.delete (DocumentTools.transformMathML (a)));
			}
			else if (a.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
			{
				markupElement.addValue ("modified math: "
					+ MarkupDocument.delete (DocumentTools.transformMathML (a)) + " to "
					+ MarkupDocument.insert (DocumentTools.transformMathML (b)));
			}
		}
		catch (Exception e)
		{
			LOGGER.error ("error generating math", e);
			markupElement.addValue ("error generating math: " + e.getMessage ());
		}
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
			String aA = a.getAttribute (attr), bA = b.getAttribute (attr);
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

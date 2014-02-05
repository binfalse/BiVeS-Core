/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.util.HashSet;
import java.util.Set;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;


/**
 * @author martin
 *
 */
public class BivesTools
{

  /**
   * Gen math html stats.
   *
   * @param a the a
   * @param b the b
   * @param markupElement the markup element
   * @param markupDocument the markup document
   */
  public static void genMathHtmlStats (DocumentNode a, DocumentNode b, MarkupElement markupElement, MarkupDocument markupDocument)
  {
  	if (a == null && b == null)
  		return;

  	try
  	{
			if (a == null)
			{
				markupElement.addValue ("inserted math: " + markupDocument.insert (DocumentTools.transformMathML (b)));
			}
			else if (b == null)
			{
				markupElement.addValue ("deleted math: " + markupDocument.delete (DocumentTools.transformMathML (a)));
			}
			else if (a.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
			{
				markupElement.addValue ("modified math: " + markupDocument.delete (DocumentTools.transformMathML (a)) + " to " + markupDocument.insert (DocumentTools.transformMathML (b)));
			}
  	}
  	catch (Exception e)
  	{
  		LOGGER.error ("error generating math", e);
  		markupElement.addValue ("error generating math: " + e.getMessage ());
  	}
  }
  
  /**
   * Gen attribute html stats.
   *
   * @param a the a
   * @param b the b
   * @param markupElement the markup element
   * @param markupDocument the markup document
   */
  public static void genAttributeHtmlStats (DocumentNode a, DocumentNode b, MarkupElement markupElement, MarkupDocument markupDocument)
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
				markupElement.addValue ("Attribute "+ markupDocument.attribute (attr) + " was inserted: "+markupDocument.insert (bA));
			else if (bA == null)
				markupElement.addValue ("Attribute "+ markupDocument.attribute (attr) + " was deleted: "+markupDocument.delete (aA));
			else if (!aA.equals (bA))
				markupElement.addValue ("Attribute "+ markupDocument.attribute (attr) + " has changed: "+markupDocument.delete (aA) +" "+markupDocument.rightArrow ()+" "+ markupDocument.insert (bA));
		}
  }
}

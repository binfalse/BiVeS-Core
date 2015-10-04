/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import org.jdom2.Element;

import de.unirostock.sems.comodi.Change;
import de.unirostock.sems.comodi.ChangeFactory;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public abstract class DiffAnnotator
{
	/**
	 * @param node
	 * @param diffNode
	 * @param changes
	 * @return the created change entity
	 */
	public abstract Change annotateDeletion (TreeNode node, Element diffNode, ChangeFactory changeFac);
	public abstract Change annotateInsertion (TreeNode node, Element diffNode, ChangeFactory changeFac);
	public abstract Change annotateMove (TreeNode nodeA, TreeNode nodeB, Element diffNode, ChangeFactory changeFac, boolean permutation);
	public abstract Change annotateUpdateAttribute (TreeNode nodeA, TreeNode nodeB, String attributeName, Element diffNode, ChangeFactory changeFac);
	public abstract Change annotateUpdateText (TextNode nodeA, TextNode nodeB, Element diffNode, ChangeFactory changeFac);
}

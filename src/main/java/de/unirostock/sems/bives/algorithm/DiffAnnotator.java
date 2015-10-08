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
 * The abstract class DiffAnnotator is the central item in the patch annotation infrastructure.
 * 
 * @author Martin Scharm
 *
 */
public abstract class DiffAnnotator
{
	/**
	 * Annotate a deletion.
	 * @param node this node or something in this node was deleted
	 * @param diffNode the node encoding for the difference
	 * @param changeFac the change factory to produce changes
	 * @return the created change entity, for further processing
	 */
	public abstract Change annotateDeletion (TreeNode node, Element diffNode, ChangeFactory changeFac);

	/**
	 * Annotate an insertion.
	 * @param node this node or something in this node was inserted
	 * @param diffNode the node encoding for the difference
	 * @param changeFac the change factory to produce changes
	 * @return the created change entity, for further processing
	 */
	public abstract Change annotateInsertion (TreeNode node, Element diffNode, ChangeFactory changeFac);

	/**
	 * Annotate a move.
	 * @param nodeA this node was moved
	 * @param nodeB `nodeA` was moved to this place
	 * @param diffNode the node encoding for the difference
	 * @param changeFac the change factory to produce changes
	 * @param permutation is that just a permutation of nodes under the same parent?
	 * @return the created change entity, for further processing
	 */
	public abstract Change annotateMove (TreeNode nodeA, TreeNode nodeB, Element diffNode, ChangeFactory changeFac, boolean permutation);
	

	/**
	 * Annotate an update of an attribute value.
	 * @param nodeA the attribute in this node was updated
	 * @param nodeB this node contains the final attribute value
	 * @param diffNode the node encoding for the difference
	 * @param changeFac the change factory to produce changes
	 * @param attributeName the attribute with this name was updated
	 * @return the created change entity, for further processing
	 */
	public abstract Change annotateUpdateAttribute (TreeNode nodeA, TreeNode nodeB, String attributeName, Element diffNode, ChangeFactory changeFac);

	/**
	 * Annotate an update of a text node.
	 * @param nodeA the original text node
	 * @param nodeB the modified text node
	 * @param diffNode the node encoding for the difference
	 * @param changeFac the change factory to produce changes
	 * @return the created change entity, for further processing
	 */
	public abstract Change annotateUpdateText (TextNode nodeA, TextNode nodeB, Element diffNode, ChangeFactory changeFac);
}

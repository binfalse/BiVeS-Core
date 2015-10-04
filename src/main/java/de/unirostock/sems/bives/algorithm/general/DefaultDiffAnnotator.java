/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import org.jdom2.Element;

import de.unirostock.sems.bives.algorithm.DiffAnnotator;
import de.unirostock.sems.comodi.Change;
import de.unirostock.sems.comodi.ChangeFactory;
import de.unirostock.sems.comodi.branches.ComodiChangeType;
import de.unirostock.sems.comodi.branches.ComodiEntity;
import de.unirostock.sems.comodi.branches.ComodiTarget;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class DefaultDiffAnnotator
	extends DiffAnnotator
{
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateDeletion(de.unirostock.sems.xmlutils.ds.TreeNode, org.jdom2.Element, de.unirostock.sems.comodi.ChangeBundle)
	 */
	@Override
	public Change annotateDeletion (TreeNode node, Element diffNode,
		ChangeFactory changeFac)
	{
		Change change = changeFac.createChange (diffNode)
			.changeType (ComodiChangeType.getDeletion ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.changeType (ComodiChangeType.getTriggered ());
		
		if (diffNode.getName ().equals ("attribute"))
			change.appliedTo (ComodiEntity.getAttribute ());
		else if (diffNode.getName ().equals ("node"))
			change.appliedTo (ComodiEntity.getNode ());
		else if (diffNode.getName ().equals ("text"))
			change.appliedTo (ComodiEntity.getText ());
		
		return change;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateInsertion(de.unirostock.sems.xmlutils.ds.TreeNode, org.jdom2.Element, de.unirostock.sems.comodi.ChangeBundle)
	 */
	@Override
	public Change annotateInsertion (TreeNode node, Element diffNode,
		ChangeFactory changeFac)
	{
		Change change = changeFac.createChange (diffNode)
			.changeType (ComodiChangeType.getInsertion ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.changeType (ComodiChangeType.getTriggered ());
		
		if (diffNode.getName ().equals ("attribute"))
			change.appliedTo (ComodiEntity.getAttribute ());
		else if (diffNode.getName ().equals ("node"))
			change.appliedTo (ComodiEntity.getNode ());
		else if (diffNode.getName ().equals ("text"))
			change.appliedTo (ComodiEntity.getText ());
		
		return change;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateMove(de.unirostock.sems.xmlutils.ds.TreeNode, de.unirostock.sems.xmlutils.ds.TreeNode, org.jdom2.Element, de.unirostock.sems.comodi.ChangeBundle)
	 */
	@Override
	public Change annotateMove (TreeNode nodeA, TreeNode nodeB, Element diffNode,
		ChangeFactory changeFac, boolean permutation)
	{
		Change change = changeFac.createChange (diffNode)
			.changeType (ComodiChangeType.getMove ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.changeType (ComodiChangeType.getTriggered ());
		
		if (diffNode.getName ().equals ("node"))
			change.appliedTo (ComodiEntity.getNode ());
		else if (diffNode.getName ().equals ("text"))
			change.appliedTo (ComodiEntity.getText ());
		else if (diffNode.getName ().equals ("attribute"))
			change.appliedTo (ComodiEntity.getAttribute ());
		
		if (permutation)
			change.affected (ComodiTarget.getPermutationOfEntities ());
		
		return change;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateUpdateAttibute(de.unirostock.sems.xmlutils.ds.TreeNode, de.unirostock.sems.xmlutils.ds.TreeNode, java.lang.String, org.jdom2.Element, de.unirostock.sems.comodi.ChangeBundle)
	 */
	@Override
	public Change annotateUpdateAttribute (TreeNode nodeA, TreeNode nodeB,
		String attributeName, Element diffNode, ChangeFactory changeFac)
	{
		Change change = changeFac.createChange (diffNode)
			.appliedTo (ComodiEntity.getAttribute ())
			.changeType (ComodiChangeType.getAttributeValue ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.changeType (ComodiChangeType.getTriggered ());
		
		return change;
	}


	@Override
	public Change annotateUpdateText (TextNode nodeA, TextNode nodeB,
		Element diffNode, ChangeFactory changeFac)
	{
		Change change = changeFac.createChange (diffNode)
			.changeType (ComodiChangeType.getDeletion ())
			.appliedTo (ComodiEntity.getText ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.changeType (ComodiChangeType.getTriggered ());
		
		return change;
	}
	
}

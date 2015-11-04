/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import org.jdom2.Element;

import de.unirostock.sems.bives.algorithm.DiffAnnotator;
import de.unirostock.sems.comodi.Change;
import de.unirostock.sems.comodi.ChangeFactory;
import de.unirostock.sems.comodi.branches.ComodiChangeType;
import de.unirostock.sems.comodi.branches.ComodiTarget;
import de.unirostock.sems.comodi.branches.ComodiXmlEntity;
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
			.hasChangeType (ComodiChangeType.getDeletion ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.wasTriggeredBy (diffNode.getAttributeValue ("triggeredBy"));
		
		if (diffNode.getName ().equals ("attribute"))
			change.appliesTo (ComodiXmlEntity.getAttribute ());
		else if (diffNode.getName ().equals ("node"))
			change.appliesTo (ComodiXmlEntity.getNode ());
		else if (diffNode.getName ().equals ("text"))
			change.appliesTo (ComodiXmlEntity.getText ());
		
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
			.hasChangeType (ComodiChangeType.getInsertion ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.wasTriggeredBy (diffNode.getAttributeValue ("triggeredBy"));
		
		if (diffNode.getName ().equals ("attribute"))
			change.appliesTo (ComodiXmlEntity.getAttribute ());
		else if (diffNode.getName ().equals ("node"))
			change.appliesTo (ComodiXmlEntity.getNode ());
		else if (diffNode.getName ().equals ("text"))
			change.appliesTo (ComodiXmlEntity.getText ());
		
		return change;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateMove(de.unirostock.sems.xmlutils.ds.TreeNode, de.unirostock.sems.xmlutils.ds.TreeNode, org.jdom2.Element, de.unirostock.sems.comodi.ChangeFactory, boolean)
	 */
	@Override
	public Change annotateMove (TreeNode nodeA, TreeNode nodeB, Element diffNode,
		ChangeFactory changeFac, boolean permutation)
	{
		Change change = changeFac.createChange (diffNode);
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.wasTriggeredBy (diffNode.getAttributeValue ("triggeredBy"));
		
		if (diffNode.getName ().equals ("node"))
			change.appliesTo (ComodiXmlEntity.getNode ());
		else if (diffNode.getName ().equals ("text"))
			change.appliesTo (ComodiXmlEntity.getText ());
		else if (diffNode.getName ().equals ("attribute"))
			change.appliesTo (ComodiXmlEntity.getAttribute ());
		
		if (permutation)
			change.hasChangeType (ComodiChangeType.getPermutationOfEntities ());
		else
			change.hasChangeType (ComodiChangeType.getMove ());
		
		return change;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateUpdateAttribute(de.unirostock.sems.xmlutils.ds.TreeNode, de.unirostock.sems.xmlutils.ds.TreeNode, java.lang.String, org.jdom2.Element, de.unirostock.sems.comodi.ChangeFactory)
	 */
	@Override
	public Change annotateUpdateAttribute (TreeNode nodeA, TreeNode nodeB,
		String attributeName, Element diffNode, ChangeFactory changeFac)
	{
		Change change = changeFac.createChange (diffNode)
			.appliesTo (ComodiXmlEntity.getAttribute ())
			.hasChangeType (ComodiChangeType.getAttributeValue ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.wasTriggeredBy (diffNode.getAttributeValue ("triggeredBy"));
		
		return change;
	}


	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.DiffAnnotator#annotateUpdateText(de.unirostock.sems.xmlutils.ds.TextNode, de.unirostock.sems.xmlutils.ds.TextNode, org.jdom2.Element, de.unirostock.sems.comodi.ChangeFactory)
	 */
	@Override
	public Change annotateUpdateText (TextNode nodeA, TextNode nodeB,
		Element diffNode, ChangeFactory changeFac)
	{
		Change change = changeFac.createChange (diffNode)
			.hasChangeType (ComodiChangeType.getUpdate ())
			.appliesTo (ComodiXmlEntity.getText ());
		
		if (diffNode.getAttribute ("triggeredBy") != null)
			change.wasTriggeredBy (diffNode.getAttributeValue ("triggeredBy"));
		
		return change;
	}
	
}

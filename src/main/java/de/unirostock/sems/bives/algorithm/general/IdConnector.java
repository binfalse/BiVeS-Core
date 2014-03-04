/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.util.Set;

import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.NodeConnection;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;



/**
 * Connector to connect nodes with same id. If the ids aren't unique in both
 * documents it'll do exactly nothing.
 * 
 * @author Martin Scharm
 * 
 */
public class IdConnector
	extends Connector
{
	
	private boolean	requireSameLabel;
	
	
	/**
	 * Instantiates a new XyDiffConnector. In this setting we'll run an ID mapper
	 * before we do our work.
	 * 
	 * @param docA
	 *          the original document
	 * @param docB
	 *          the modified document
	 * @param requireSameLabel
	 *          if true, both id-tags need to have the same label
	 */
	public IdConnector (TreeDocument docA, TreeDocument docB,
		boolean requireSameLabel)
	{
		super (docA, docB);
		this.requireSameLabel = requireSameLabel;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		// we can only map by ids if they are unique...
		if (!docA.uniqueIds () || !docB.uniqueIds ())
			return;
		
		Set<String> ids = docA.getOccurringIds ();
		
		for (String id : ids)
		{
			TreeNode nB = docB.getNodeById (id);
			if (nB == null)
				continue;
			
			TreeNode nA = docA.getNodeById (id);
			
			if (!requireSameLabel)
			{
				conMgmt.addConnection (new NodeConnection (nA, nB));
			}
			else if (nB.getType () == TreeNode.DOC_NODE
				&& nA.getType () == TreeNode.DOC_NODE
				&& ((DocumentNode) nB).getTagName ().equals (
					((DocumentNode) nA).getTagName ()))
			{
				conMgmt.addConnection (new NodeConnection (nA, nB));
			}
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.algorithm.Connector#init()
	 */
	@Override
	protected void init () throws BivesConnectionException
	{
		// nothing to do
	}
	
}

/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.util.List;
import java.util.Set;

import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.NodeConnection;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;



/**
 * Class to connect all nodes with the same label in two trees. As result a node
 * in tree A with label <em>a</em> will be connected to all nodes in tree B
 * which are also labeled with <em>a</em>.
 * 
 * <pre>
 * tree A:    a a b
 * connect:   |X| |
 * tree B:    a a b
 * </pre>
 * 
 * This class is currently deprecated because we don't support the mapping of
 * multiple node. Each node must not have more than one connection.
 * There are cases where this mapper will work like a charm, but it will most
 * likely fail..
 * 
 * @author Martin Scharm
 * 
 * 
 */
@Deprecated
public class LabelConnector
	extends Connector
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		Set<String> tags = docA.getOccurringTags ();
		
		for (String tag : tags)
		{
			List<DocumentNode> nB = docB.getNodesByTag (tag);
			if (nB == null)
				continue;
			
			List<DocumentNode> nA = docA.getNodesByTag (tag);
			for (TreeNode b : nB)
			{
				for (TreeNode a : nA)
				{
					conMgmt.addConnection (new NodeConnection (a, b));
				}
			}
		}
	}
	
}

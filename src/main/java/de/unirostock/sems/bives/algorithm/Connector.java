/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class Connector, intended to find node-correspondences between two trees.
 *
 * @author Martin Scharm
 */
public abstract class Connector
{
	
	/** The connection manager, holding node-mappings. */
	protected SimpleConnectionManager conMgmt;
	
	/** The original document. */
	protected TreeDocument docA;
	/** The modified document. */
	protected TreeDocument docB;
	
	/**
	 * Instantiates a new connector.
	 *
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public Connector (TreeDocument docA, TreeDocument docB)
	{
		this.docA = docA;
		this.docB = docB;
		conMgmt = new SimpleConnectionManager (docA, docB);
	}
	
	/**
	 * Gets the original document.
	 *
	 * @return the original document
	 */
	public TreeDocument getDocA ()
	{
		return docA;
	}
	
	/**
	 * Gets the modified document.
	 *
	 * @return the modified document
	 */
	public TreeDocument getDocB ()
	{
		return docB;
	}
	
	
	/**
	 * Initializes the extension.
	 *
	 * @throws BivesConnectionException the bives connection exception
	 */
	protected abstract void init () throws BivesConnectionException;
	
	/**
	 * Inherit to search for connections.
	 *
	 * @throws BivesConnectionException the bives connection exception
	 */
	protected abstract void connect () throws BivesConnectionException;
	
	
	/**
	 * Find connections between nodes of both versions of your document.
	 *
	 * @throws BivesConnectionException the bives connection exception
	 */
	public final void findConnections () throws BivesConnectionException
	{
		init ();
		connect ();

		docA.getRoot ().resetModifications ();
		docA.getRoot ().evaluate (conMgmt);
		
		docB.getRoot ().resetModifications ();
		docB.getRoot ().evaluate (conMgmt);
	}
	
	/**
	 * Gets the connections.
	 *
	 * @return the connections
	 */
	public final SimpleConnectionManager getConnections ()
	{
		return conMgmt;
	}
	
	
	/**
	 * Assign two nodes to each other, as long as they don't have a connection.
	 *
	 * @param a the node from the original tree
	 * @param b the node from the modified tree
	 * @return true, if successfully connected
	 * @throws BivesConnectionException the bives connection exception
	 */
	protected boolean nodeAssign (TreeNode a, TreeNode b) throws BivesConnectionException
	{
		LOGGER.debug ("Matching old: ", a.getXPath (), " with new: ", b.getXPath ());
		if (conMgmt.getConnectionForNode (a) != null || conMgmt.getConnectionForNode (b) != null)
		{
			LOGGER.debug ("already assigned");
			return true;
		}
		
		if (a.getType () != b.getType ())
			return false;
		
		if ((a.getType () == TreeNode.DOC_NODE && ((DocumentNode) b).getTagName ().equals (((DocumentNode) a).getTagName ())) || a.getType () == TreeNode.TEXT_NODE)
		{
			conMgmt.addConnection (new NodeConnection (a, b));
			return true;
		}
		return false;
		// statsCantMatchDifferentOwnHash
	}
}

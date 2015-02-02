/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.ds.TreeDocument;


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
}

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
	
	/** The documents in versions a and b. */
	protected TreeDocument docA, docB;
	
	/**
	 * Initializes the connector.
	 *
	 * @param docA the original document
	 * @param docB the modified document
	 * @throws BivesConnectionException 
	 */
	public final void init (TreeDocument docA, TreeDocument docB) throws BivesConnectionException
	{
		this.docA = docA;
		this.docB = docB;
		conMgmt = new SimpleConnectionManager (docA, docB);
	}
	
	
	/**
	 * Initializes the extension.
	 */
	protected abstract void init () throws BivesConnectionException;
	
	/**
	 * Inherit to search for connections.
	 */
	protected abstract void connect () throws BivesConnectionException;
	
	
	/**
	 * Find connections between nodes of both versions of your document.
	 *
	 * @throws BivesConnectionException the bives connection exception
	 */
	public final void findConnections () throws BivesConnectionException
	{
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

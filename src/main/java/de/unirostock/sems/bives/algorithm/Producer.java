/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.xmlutils.ds.TreeDocument;


/**
 * The Class Producer produces different kind of output. General workflow:
 * 
 * <pre>
 * Producer p = new WhatEverProducer ();
 * p.init (connections, docA, docB);
 * String output = p.produce ();
 * </pre>
 *
 * @author Martin Scharm
 */
public abstract class Producer
{
	
	/** The connection manager. */
	protected SimpleConnectionManager conMgmt;
	
	/** The original version. */
	protected TreeDocument docA;
	
	/** The modified version. */
	protected TreeDocument docB;
	
	/**
	 * Initializes the producer.
	 *
	 * @param conMgmt the connection manager
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public void init (SimpleConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		this.conMgmt = conMgmt;
		this.docA = docA;
		this.docB = docB;
	}
	
	/**
	 * Produce the output.
	 *
	 * @return the ourput
	 */
	public abstract String produce ();
}

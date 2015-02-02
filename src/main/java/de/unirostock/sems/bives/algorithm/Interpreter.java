/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.xmlutils.ds.TreeDocument;


/**
 * @author Martin Scharm
 *
 */
public abstract class Interpreter
{
	
	/** The connection manager. */
	protected SimpleConnectionManager conMgmt;
	
	/** The original document. */
	protected TreeDocument docA;
	
	/** The modified document. */
	protected TreeDocument docB;
	
	/**
	 * Instantiates a new interpreter.
	 *
	 * @param conMgmt the connection manager
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public Interpreter (SimpleConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		this.conMgmt = conMgmt;
		this.docA = docA;
		this.docB = docB;
	}
	
	/**
	 * Interprete the connections!
	 */
	public abstract void interprete ();
}

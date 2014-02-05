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
	protected SimpleConnectionManager conMgmt;
	protected TreeDocument docA;
	protected TreeDocument docB;
	
	public Interpreter (SimpleConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		this.conMgmt = conMgmt;
		this.docA = docA;
		this.docB = docB;
	}
	
	public abstract void interprete ();
}

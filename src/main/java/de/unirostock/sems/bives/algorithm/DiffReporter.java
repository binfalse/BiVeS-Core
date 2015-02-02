package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.markup.MarkupElement;


/**
 * @author Martin Scharm
 *
 */
public interface DiffReporter
{
	
	/**
	 * Report as a modification.
	 *
	 * @param conMgmt the connection manager
	 * @param docA the original document
	 * @param docB the modified document
	 * @return the markup element
	 */
	public MarkupElement reportModification (SimpleConnectionManager conMgmt, DiffReporter docA, DiffReporter docB);
	
	/**
	 * Report an insert.
	 *
	 * @return the markup element
	 */
	public MarkupElement reportInsert ();
	
	/**
	 * Report a delete.
	 *
	 * @return the markup element
	 */
	public MarkupElement reportDelete ();
	
}

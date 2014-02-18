/**
 * 
 */
package de.unirostock.sems.bives.ds;


/**
 * @author Martin Scharm
 *
 */
public interface GraphEntity
{
	
	/** The flag for UNMODIFIED entities. */
	public static final int UNMODIFIED = 0;
	
	/** The flag for INSERTed entities. */
	public static final int INSERT = 1;
	
	/** The flag for DELETEd entities. */
	public static final int DELETE = -1;
	
	/** The flag for MODIFIED entities. */
	public static final int MODIFIED = 2;
	
	
}

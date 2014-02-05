/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesLogicalException signaling logical errors.
 * 
 * @author Martin Scharm
 */
public class BivesLogicalException
	extends BivesException
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= -97204163091159494L;
	
	
	/**
	 * Instantiates a new bives logical exception.
	 * 
	 * @param msg
	 *          the message
	 */
	public BivesLogicalException (String msg)
	{
		super (msg);
	}
}

/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesNetworkingException signaling network errors.
 * 
 * @author Martin Scharm
 */
public class BivesNetworkingException
	extends BivesException
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= -7167108667606707987L;
	
	
	/**
	 * Instantiates a new bives networking exception.
	 * 
	 * @param msg
	 *          the message
	 */
	public BivesNetworkingException (String msg)
	{
		super (msg);
	}
}

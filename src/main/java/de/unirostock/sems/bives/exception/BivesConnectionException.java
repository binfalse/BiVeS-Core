/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesConnectionException signaling node-connection problems.
 * 
 * @author Martin Scharm
 */
public class BivesConnectionException
	extends BivesException
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= -3026781977946111686L;
	
	
	/**
	 * Instantiates a new bives connection exception.
	 * 
	 * @param msg
	 *          the message
	 */
	public BivesConnectionException (String msg)
	{
		super (msg);
	}
	
}

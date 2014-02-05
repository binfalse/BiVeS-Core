/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesException representing problems that occurred in BiVeS.
 * 
 * @author Martin Scharm
 */
public class BivesException
	extends Exception
{
	
	private static final long	serialVersionUID	= 1977304380044136155L;
	
	
	/**
	 * Instantiates a new BiVeS exception. Exceptions need to have a message..
	 * 
	 * @param message
	 *          the message
	 */
	public BivesException (String message)
	{
		super (message);
	}
}

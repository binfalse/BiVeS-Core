/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesUnsupportedException for not (yet) supported operations.
 * 
 * @author Martin Scharm
 */
public class BivesUnsupportedException
	extends BivesException
{
	
	private static final long	serialVersionUID	= 9053231268460584393L;
	
	
	/**
	 * Instantiates a new unsupported exception.
	 * 
	 * @param msg
	 *          the message
	 */
	public BivesUnsupportedException (String msg)
	{
		super (msg);
	}
}

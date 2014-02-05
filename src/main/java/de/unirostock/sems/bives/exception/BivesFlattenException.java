/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesFlattenException signaling problems while flattening a
 * document.
 * 
 * @author Martin Scharm
 */
public class BivesFlattenException
	extends BivesException
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= -2888059827506993888L;
	
	
	/**
	 * Instantiates a new bives flatten exception.
	 * 
	 * @param msg
	 *          the message
	 */
	public BivesFlattenException (String msg)
	{
		super (msg);
	}
}

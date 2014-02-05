/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesDocumentConsistencyException signaling inconsistencies.
 * 
 * @author Martin Scharm
 */
public class BivesDocumentConsistencyException
	extends BivesException
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= -2832452681114954532L;
	
	
	/**
	 * Instantiates a new bives document consistency exception.
	 * 
	 * @param message
	 *          the message
	 */
	public BivesDocumentConsistencyException (String message)
	{
		super (message);
	}
	
}

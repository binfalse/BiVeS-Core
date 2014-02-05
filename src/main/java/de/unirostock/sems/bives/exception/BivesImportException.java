/**
 * 
 */
package de.unirostock.sems.bives.exception;

/**
 * The Class BivesImportException signaling errors while importing documents.
 * 
 * @author Martin Scharm
 */
public class BivesImportException
	extends BivesException
{
	
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= 1944282636128412419L;
	
	
	/**
	 * Instantiates a new bives import exception.
	 * 
	 * @param file
	 *          the file
	 * @param e
	 *          the thrown exception
	 */
	public BivesImportException (String file, Exception e)
	{
		super ("Exception during import of " + file + ": ["
			+ e.getClass ().getName () + ": " + e.getMessage () + "]");
	}
}

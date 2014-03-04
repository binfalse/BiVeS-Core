/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.ModelDocument;
import de.unirostock.sems.xmlutils.ds.TreeDocument;



/**
 * The abstract class ModelValidator will serve as parent for ML validators. This might also be a good way to parse documents.
 * 
 * @author Martin Scharm
 */
public abstract class ModelValidator
{
	
	/**
	 * Validate a document.
	 * 
	 * @param d
	 *          the document
	 * @return true, if document is a valid model
	 */
	public abstract boolean validate (TreeDocument d);
	
	
	/**
	 * Validate a document represented as a string.
	 * 
	 * @param d
	 *          the textual representation of the document
	 * @return true, if submitted string is a valid model
	 */
	public abstract boolean validate (String d);
	
	
	/**
	 * Gets the parsed document if it was valid, of null otherwise. Since we need
	 * to create a document anyways, here you can get it for free ;-)
	 * 
	 * @return the document
	 */
	public abstract ModelDocument getDocument ();
	
	
	/**
	 * Gets the error the was thrown in case of an invalid document.
	 * 
	 * @return the error
	 */
	public abstract Exception getError ();
}

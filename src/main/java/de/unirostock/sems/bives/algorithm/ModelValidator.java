/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.xmlutils.ds.TreeDocument;


/**
 * The abstract class ModelValidator will serve as parent for ML validators.
 *
 * @author Martin Scharm
 */
public abstract class ModelValidator
{
	
	/**
	 * Validate a document.
	 *
	 * @param d the document
	 * @return true, if document is valid
	 * @throws Exception the exception
	 */
	public abstract boolean validate (TreeDocument d) throws Exception;
	
	/**
	 * Validate a document represented as a string.
	 *
	 * @param d the textual representation of the document
	 * @return true, if valid
	 * @throws Exception the exception
	 */
	public abstract boolean validate (String d) throws Exception;
}

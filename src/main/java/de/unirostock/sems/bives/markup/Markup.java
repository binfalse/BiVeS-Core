/**
 * 
 */
package de.unirostock.sems.bives.markup;

/**
 * The Interface Markup for objects who are able to report their contents to a
 * markup document.
 * 
 * @author Martin Scharm
 */
public interface Markup
{
	
	/**
	 * Produce some markup presenting the contents of this object.
	 * 
	 * @return the string to include in the report
	 */
	public String markup ();
	
}

/**
 * 
 */
package de.unirostock.sems.bives.markup;

/**
 * The Class Typesetting to convert markup documents to certain formats.
 * 
 * @author Martin Scharm
 */
public abstract class Typesetting
{
	
	/** represents the system specific new line character. */
	public static final String	NL_TXT		= System.getProperty ("line.separator");
	
	/** represents a new line in XHTML files. */
	public static final String	NL_XHTML	= "<br/>";
	
	/** represents a new line in HTML files. */
	public static final String	NL_HTML		= "<br>";
	
	
	/**
	 * convert a markup document using a format-specific make-up.
	 * 
	 * @param doc
	 *          the markup document
	 * @return the format specific representation
	 */
	public abstract String typeset (MarkupDocument doc);
}

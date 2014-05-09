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
	public static final String CSS_CLASS_INSERT = "bives-insert";
	public static final String CSS_CLASS_DELETE = "bives-delete";
	public static final String CSS_CLASS_MOVE = "bives-move";
	public static final String CSS_CLASS_UPDATE = "bives-update";
	public static final String CSS_CLASS_ATTRIBUTE = "bives-attr";
	public static final String CSS_CLASS_MATH = "bives-math";
	public static final String CSS_CLASS_MATH_ORIGINAL = "bives-math-original";
	public static final String CSS_CLASS_MATH_MODIFIED = "bives-math-modified";
	public static final String CSS_CLASS_TABLE_LEFT_COLUMN = "bives-table-left";
	public static final String CSS_CLASS_TABLE_RIGHT_COLUMN = "bives-table-right";
	// plus various classes ala `bives-table-SECTIONNAME`,
	// e.g. `bives-table-Species` to address the table containing changes in the species.
	
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

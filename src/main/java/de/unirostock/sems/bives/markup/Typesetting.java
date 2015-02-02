/**
 * 
 */
package de.unirostock.sems.bives.markup;

// TODO: Auto-generated Javadoc
/**
 * The Class Typesetting to convert markup documents to certain formats.
 * 
 * @author Martin Scharm
 */
public abstract class Typesetting
{
	
	/** The Constant CSS_CLASS_INSERT. */
	public static final String CSS_CLASS_INSERT = "bives-insert";
	
	/** The Constant CSS_CLASS_DELETE. */
	public static final String CSS_CLASS_DELETE = "bives-delete";
	
	/** The Constant CSS_CLASS_MOVE. */
	public static final String CSS_CLASS_MOVE = "bives-move";
	
	/** The Constant CSS_CLASS_UPDATE. */
	public static final String CSS_CLASS_UPDATE = "bives-update";
	
	/** The Constant CSS_CLASS_ATTRIBUTE. */
	public static final String CSS_CLASS_ATTRIBUTE = "bives-attr";
	
	/** The Constant CSS_CLASS_MATH. */
	public static final String CSS_CLASS_MATH = "bives-math";
	
	/** The Constant CSS_CLASS_MATH_ORIGINAL. */
	public static final String CSS_CLASS_MATH_ORIGINAL = "bives-math-original";
	
	/** The Constant CSS_CLASS_MATH_MODIFIED. */
	public static final String CSS_CLASS_MATH_MODIFIED = "bives-math-modified";
	
	/** The Constant CSS_CLASS_TABLE_LEFT_COLUMN. */
	public static final String CSS_CLASS_TABLE_LEFT_COLUMN = "bives-table-left";
	
	/** The Constant CSS_CLASS_TABLE_RIGHT_COLUMN. */
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

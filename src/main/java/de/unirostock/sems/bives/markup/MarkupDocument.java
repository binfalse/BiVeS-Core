/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * The Class MarkupDocument representing a generic document that can be encoded in different formats using Typesetters.
 *
 * @see de.unirostock.sems.bives.markup.Typesetting
 * @author Martin Scharm
 */
public class MarkupDocument
{
	
	/** The headline. */
	private String headline;
	
	/** The sections. */
	private List<MarkupSection> sections;
	
	/** The header. */
	private List<String> header;
	
	/**
	 * Instantiates a new markup document.
	 *
	 * @param headline the headline
	 */
	public MarkupDocument (String headline)
	{
		this.headline = headline;
		sections = new ArrayList<MarkupSection> ();
		header = new ArrayList<String> ();
	}
	
	/**
	 * Adds a header.
	 *
	 * @param header the header to add
	 */
	public void addHeader (String header)
	{
		this.header.add (header);
	}
	
	/**
	 * Adds a section.
	 *
	 * @param section the section
	 */
	public void addSection (MarkupSection section)
	{
		sections.add (section);
	}
	
	/**
	 * Highlights a string, e.g. a special word or phrase.
	 *
	 * @param s the string to highlight
	 * @return the highlighted string
	 */
	public static final String highlight (String s)
	{
		return "{{highlight}}" + s + "}}highlight{{";
	}
	
	/**
	 * Highlights an insert.
	 *
	 * @param s the string to highlight
	 * @return the highlighted string
	 */
	public static final String insert (String s)
	{
		return "{{insert}}" + s + "}}insert{{";
	}
	
	/**
	 * Highlights a delete.
	 *
	 * @param s the string to highlight
	 * @return the highlighted string
	 */
	public static final String delete (String s)
	{
		return "{{delete}}" + s + "}}delete{{";
	}
	
	/**
	 * Highlights supplementary information.
	 *
	 * @param s the string to highlight
	 * @return the highlighted string
	 */
	public static final String supplemental (String s)
	{
		return "{{supp}}" + s + "}}supp{{";
	}
	
	/**
	 * Highlights an attribute.
	 *
	 * @param s the string to highlight
	 * @return the highlighted string
	 */
	public static final String attribute (String s)
	{
		return "{{attribute}}" + s + "}}attribute{{";
	}
	
	/**
	 * Container for unchanged math.
	 *
	 * @param s the string representing math
	 * @return the container containing the math
	 */
	public static final String math (String s)
	{
		return "{{math}}" + s + "}}math{{";
	}
	
	/**
	 * Container for modified math.
	 *
	 * @param s the string representing math
	 * @param original the is that the original document?
	 * @return the container containing the math
	 */
	public static final String math (String s, boolean original)
	{
		if (original)
			return "{{mathD}}" + s + "}}mathD{{";
		return "{{mathI}}" + s + "}}mathI{{";
	}
	
	/**
	 * Produces a right arrow. (e.g. for chemical reactions)
	 *
	 * @return the right arrow
	 */
	public static final String rightArrow ()
	{
		return "{{rightArrow}}";
	}
	
	/**
	 * Produces a multiply symbol. (e.g. * )
	 *
	 * @return the multiply symbol
	 */
	public static final String multiply ()
	{
		return "{{multiplication}}";
	}
	
	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public List<String> getHeader ()
	{
		return header;
	}
	
	/**
	 * Gets the sections.
	 *
	 * @return the sections
	 */
	public List<MarkupSection> getSections ()
	{
		return sections;
	}
	
	/**
	 * Gets the headline.
	 *
	 * @return the headline
	 */
	public String getHeadline ()
	{
		return headline;
	}
	
	/**
	 * Replaces highlights.
	 *
	 * @param s the string containing highlights
	 * @param pre the opening, e.g. &lt;em&gt;
	 * @param post the closing, e.g. &lt;/em&gt;
	 * @return the final string
	 */
	public static final String replaceHighlights (String s, String pre, String post)
	{
		return replace (s, "{{highlight}}", "}}highlight{{", pre, post);
	}
	
	/**
	 * Replaces inserts.
	 *
	 * @param s the string containing inserts
	 * @param pre the opening, e.g. &lt;ins&gt;
	 * @param post the closing, e.g. &lt;/ins&gt;
	 * @return the final string
	 */
	public static final String replaceInserts (String s, String pre, String post)
	{
		return replace (s, "{{insert}}", "}}insert{{", pre, post);
	}
	
	/**
	 * Replaces deletes.
	 *
	 * @param s the string containing deletes
	 * @param pre the opening, e.g. &lt;del&gt;
	 * @param post the closing, e.g. &lt;/del&gt;
	 * @return the final string
	 */
	public static final String replaceDeletes (String s, String pre, String post)
	{
		return replace (s, "{{delete}}", "}}delete{{", pre, post);
	}
	
	/**
	 * Replaces supplementary information.
	 *
	 * @param s the string containing supp info
	 * @param pre the opening, e.g. &lt;supp&gt;
	 * @param post the closing, e.g. &lt;/supp&gt;
	 * @return the final string
	 */
	public static final String replaceSupplementals (String s, String pre, String post)
	{
		return replace (s, "{{supp}}", "}}supp{{", pre, post);
	}
	
	/**
	 * Replaces attributes.
	 *
	 * @param s the string containing attributes
	 * @param pre the opening, e.g. &lt;attr&gt;
	 * @param post the closing, e.g. &lt;/attr&gt;
	 * @return the final string
	 */
	public static final String replaceAttributes (String s, String pre, String post)
	{
		return replace (s, "{{attribute}}", "}}attribute{{", pre, post);
	}
	
	/**
	 * Replaces modified math.
	 *
	 * @param s the string containing modified math
	 * @param pre the opening, e.g. &lt;inserted math&gt;
	 * @param post the closing, e.g. &lt;/inserted math&gt;
	 * @return the final string
	 */
	public static final String replaceModifiedMaths (String s, String pre, String post)
	{
		return replace (s, "{{mathI}}", "}}mathI{{", pre, post);
	}
	
	/**
	 * Replaces original math.
	 *
	 * @param s the string containing original math
	 * @param pre the opening, e.g. &lt;deleted math&gt;
	 * @param post the closing, e.g. &lt;/deleted math&gt;
	 * @return the final string
	 */
	public static final String replaceOriginalMaths (String s, String pre, String post)
	{
		return replace (s, "{{mathD}}", "}}mathD{{", pre, post);
	}
	
	/**
	 * Replaces all math stuff, doesn't matter if inserted or deleted.
	 *
	 * @param s the string containing inserted or deleted math
	 * @param pre the opening, e.g. &lt;my math tag&gt;
	 * @param post the closing, e.g. &lt;/my math tag&gt;
	 * @return the final string
	 */
	public static final String replaceAllMaths (String s, String pre, String post)
	{
		return replace (replace (replace (s, "{{mathI}}", "}}mathI{{", pre, post), "{{mathD}}", "}}mathD{{", pre, post), "{{math}}", "}}math{{", pre, post);
	}
	
	/**
	 * Replaces math that hasn't changed.
	 *
	 * @param s the string containing math
	 * @param pre the opening, e.g. &lt;unchanged math&gt;
	 * @param post the closing, e.g. &lt;/unchanged math&gt;
	 * @return the final string
	 */
	public static final String replaceUnchangedMaths (String s, String pre, String post)
	{
		return replace (s, "{{math}}", "}}math{{", pre, post);
	}
	
	/**
	 * Replaces multiplication.
	 *
	 * @param s the string containing multiplications
	 * @param replacement the replacement, e.g. "*"
	 * @return the final string
	 */
	public static final String replaceMultiplication (String s, String replacement)
	{
		if (replacement == null)
			replacement = "";
		return s.replaceAll (Pattern.quote ("{{multiplication}}"), replacement);
	}
	
	/**
	 * Replaces right arrow.
	 *
	 * @param s the string containing arrows
	 * @param replacement the replacement, e.g. "-&gt;"
	 * @return the final string
	 */
	public static final String replaceRightArrow (String s, String replacement)
	{
		if (replacement == null)
			replacement = "";
		return s.replaceAll (Pattern.quote ("{{rightArrow}}"), replacement);
	}
	
	/**
	 * The internal replacer.
	 *
	 * @param s the string of interest
	 * @param pre the opening
	 * @param post the closing
	 * @param rpre the opening replacement
	 * @param rpost the closing replacement
	 * @return the final string
	 */
	private static final String replace (String s, String pre, String post, String rpre, String rpost)
	{
		if (rpre == null)
			rpre = "";
		if (rpost == null)
			rpost = "";
		
		String ret = s.replaceAll (Pattern.quote (pre), rpre);
		ret = ret.replaceAll (Pattern.quote (post), rpost);
		
		return ret;
		
	}
}

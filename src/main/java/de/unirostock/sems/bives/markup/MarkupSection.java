/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class MarkupSection representing a section in a MarkupDocument.
 *
 * @author Martin Scharm
 */
public class MarkupSection
{
	
	/** The header. */
	private String header;
	
	/** The list of elements. */
	private List<MarkupElement> values;
	
	/**
	 * Instantiates a new markup section.
	 *
	 * @param header the header
	 */
	public MarkupSection (String header)
	{
		this.header = header;
		values = new ArrayList<MarkupElement> ();
	}
	
	/**
	 * Adds an element to this section.
	 *
	 * @param element the element
	 */
	public void addValue (MarkupElement element)
	{
		if (element != null)
		values.add (element);
	}
	
	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public String getHeader ()
	{
		return header;
	}
	
	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public List<MarkupElement> getValues ()
	{
		return values;
	}
	
	/**
	 * Sets the header.
	 *
	 * @param header the new header
	 */
	public void setHeader (String header)
	{
		this.header = header;
	}
}

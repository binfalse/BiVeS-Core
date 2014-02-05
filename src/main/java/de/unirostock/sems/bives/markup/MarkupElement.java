/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class MarkupElement representing an element in a MarkupDocument.
 *
 * @author Martin Scharm
 */
public class MarkupElement
{
	
	/** The header. */
	private String header;
	
	/** The list of entries in this element. */
	private List<String> values;
	
	/** The list of sub-elements. */
	private List<MarkupElement> subElements;
	
	/**
	 * Instantiates a new markup element.
	 *
	 * @param header the header
	 */
	public MarkupElement (String header)
	{
		this.header = header;
		values = new ArrayList<String> ();
		subElements = new ArrayList<MarkupElement> ();
	}
	
	/**
	 * Adds an entry to this element.
	 *
	 * @param value the entry
	 */
	public void addValue (String value)
	{
		if (value != null)
		values.add (value);
	}
	
	/**
	 * Adds a sub-element.
	 *
	 * @param element the element
	 */
	public void addSubElements (MarkupElement element)
	{
		if (element != null)
		subElements.add (element);
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
	 * Gets the entries stored in this element.
	 *
	 * @return the entries
	 */
	public List<String> getValues ()
	{
		return values;
	}
	
	/**
	 * Gets the sub-elements.
	 *
	 * @return the sub-elements
	 */
	public List<MarkupElement> getSubElements ()
	{
		return subElements;
	}
}

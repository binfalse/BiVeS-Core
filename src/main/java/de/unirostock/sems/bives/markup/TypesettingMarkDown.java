/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.List;



/**
 * The Class TypesettingMarkDown to typeset reports in MarkDown format.
 * 
 * @author Martin Scharm
 */
public class TypesettingMarkDown
	extends Typesetting
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.markup.Markup#typeset(de.unirostock.sems.bives.
	 * markup.MarkupDocument)
	 */
	@Override
	public String typeset (MarkupDocument doc)
	{
		String s = doc.getHeadline () + NL_TXT + "===================" + NL_TXT
			+ NL_TXT;
		
		List<String> headers = doc.getHeader ();
		for (String head : headers)
			s += "* " + head + NL_TXT;
		s += NL_TXT;
		
		List<MarkupSection> sections = doc.getSections ();
		for (MarkupSection sec : sections)
			s += typesetSection (sec);
		
		s = MarkupDocument.replaceHighlights (s, "*", "*");
		s = MarkupDocument.replaceInserts (s, "<span class='" + CSS_CLASS_INSERT + "'>",
			"</span>");
		s = MarkupDocument.replaceDeletes (s, "<span class='" + CSS_CLASS_DELETE + "'>",
			"</span>");
		s = MarkupDocument.replaceAttributes (s, "<span class='" + CSS_CLASS_ATTRIBUTE + "'>",
			"</span>");
		s = MarkupDocument.replaceAllMaths (s, "<span class='" + CSS_CLASS_MATH + "'>",
			"</span>");
		s = MarkupDocument.replaceRightArrow (s, "->");
		s = MarkupDocument.replaceMultiplication (s, "*");
		
		return s;
	}
	
	
	/**
	 * Typeset a section.
	 * 
	 * @param section
	 *          the section
	 * @return the section in MarkDown
	 */
	private String typesetSection (MarkupSection section)
	{
		String s = NL_TXT + section.getHeader () + NL_TXT + "-------------------"
			+ NL_TXT + NL_TXT;
		
		List<MarkupElement> elements = section.getValues ();
		for (MarkupElement e : elements)
			s += typesetElement (e);
		
		return s + NL_TXT + NL_TXT;
	}
	
	
	/**
	 * Typeset an element.
	 * 
	 * @param element
	 *          the element
	 * @return the element in MarkDown
	 */
	private String typesetElement (MarkupElement element)
	{
		String s = "* **" + element.getHeader () + "**" + NL_TXT;
		
		List<String> values = element.getValues ();
		for (String v : values)
			s += "    * " + v + NL_TXT;
		
		List<MarkupElement> subElements = element.getSubElements ();
		for (MarkupElement e : subElements)
			s += "    * " + typesetSubElement (e) + NL_TXT;
		
		return s;
	}
	
	
	/**
	 * Typeset a sub-element.
	 * 
	 * @param element
	 *          the element
	 * @return the sub-element in MarkDown
	 */
	private String typesetSubElement (MarkupElement element)
	{
		String s = "**" + element.getHeader () + "**" + NL_TXT;
		
		List<String> values = element.getValues ();
		for (String v : values)
			s += "        * " + v + "";
		
		return s;
	}
	
}

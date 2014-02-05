/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.List;



/**
 * The Class TypesettingHTML to typeset reports in HTML format.
 * 
 * @author Martin Scharm
 */
public class TypesettingHTML
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
		String s = "<h1>" + doc.getHeadline () + "</h1>";
		String sub = "";
		
		List<String> headers = doc.getHeader ();
		for (String head : headers)
			sub += "<li>" + head + "</li>";
		if (sub.length () > 0)
			s += "<ul>" + sub + "</ul>";
		
		List<MarkupSection> sections = doc.getSections ();
		for (MarkupSection sec : sections)
			s += typesetSection (sec);
		
		s = MarkupDocument.replaceHighlights (s, "<strong>", "</strong>");
		s = MarkupDocument.replaceInserts (s, "<span class='bives-insert'>",
			"</span>");
		s = MarkupDocument.replaceDeletes (s, "<span class='bives-delete'>",
			"</span>");
		s = MarkupDocument.replaceAttributes (s, "<span class='bives-attr'>",
			"</span>");
		s = MarkupDocument.replaceRightArrow (s, "&rarr;");
		s = MarkupDocument.replaceMultiplication (s, "&middot;");
		
		return s;
	}
	
	
	/**
	 * Typeset a section.
	 * 
	 * @param section
	 *          the section
	 * @return the section in HTML
	 */
	private String typesetSection (MarkupSection section)
	{
		String s = "<h2>" + section.getHeader () + "</h2>";
		String sub = "";
		
		List<MarkupElement> elements = section.getValues ();
		for (MarkupElement e : elements)
			sub += typesetElement (e);
		
		if (sub.length () > 0)
			return s + "<table class='bives-table-"
				+ section.getHeader ().replaceAll ("[^a-zA-Z0-9]", "_") + "'>" + sub
				+ "</table>";
		return "";
	}
	
	
	/**
	 * Typeset an element.
	 * 
	 * @param element
	 *          the element
	 * @return the element in HTML
	 */
	private String typesetElement (MarkupElement element)
	{
		String s = "<tr><td class='bives-table-left'>" + element.getHeader ()
			+ "</td><td class='bives-table-right'>";
		String sub = "";
		
		List<String> values = element.getValues ();
		for (String v : values)
			sub += "<li>" + v + "</li>";
		
		List<MarkupElement> subElements = element.getSubElements ();
		for (MarkupElement e : subElements)
			sub += "<li>" + typesetSubElement (e) + "</li>";
		
		if (sub.length () > 0)
			s += "<ul>" + sub + "</ul>";
		
		return s + "</td></tr>";
	}
	
	
	/**
	 * Typeset a sub-element.
	 * 
	 * @param element
	 *          the element
	 * @return the sub-element in HTML
	 */
	private String typesetSubElement (MarkupElement element)
	{
		String s = "<strong>" + element.getHeader () + "</strong>";
		String sub = "";
		
		List<String> values = element.getValues ();
		for (String v : values)
			sub += "<li>" + v + "</li>";
		
		if (sub.length () > 0)
			s += "<ul>" + sub + "</ul>";
		
		return s;
	}
	
}

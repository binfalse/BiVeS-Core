package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;


/**
 * @author Martin Scharm
 *
 */
public interface DiffReporter
{
	/*public static final String CLASS_DELETED = "deleted";
	public static final String CLASS_INSERTED = "inserted";
	public static final String CLASS_ATTRIBUTE = "attr";*/
	
	/*public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB);
	public String reportInsert ();
	public String reportDelete ();*/
	
	public MarkupElement reportModification (SimpleConnectionManager conMgmt, DiffReporter docA, DiffReporter docB);
	public MarkupElement reportInsert ();
	public MarkupElement reportDelete ();
	
}

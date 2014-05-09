/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.BivesTools;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;

/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class TestMaths
{
	private static final File	MathOne	= new File ("test/mathml-1.xml");
	private static final File	MathTwo	= new File ("test/mathml-2.xml");
	
	private static final File	StuartMathOne	= new File ("test/stuarts-math-v1.xml");
	private static final File	StuartMathTwo	= new File ("test/stuarts-math-v2.xml");
	

	@Test
	public void testMathDiff1 () throws XmlDocumentParseException, IOException, JDOMException, BivesConnectionException
	{
		TreeDocument td1 = new TreeDocument (XmlTools.readDocument (MathOne), MathOne.toURI ());
		TreeDocument td2 = new TreeDocument (XmlTools.readDocument (MathTwo), MathTwo.toURI ());
		
		RegularDiff differ = new RegularDiff (td1, td2);
		differ.mapTrees ();
		
		Patch p = differ.getPatch ();
		assertEquals ("expected to see 3 deletes", 3, p.getNumDeletes ());
		assertEquals ("expected to see 1 move", 1, p.getNumMoves ());
		assertEquals ("expected to see 0 inserts or updates", 0, p.getNumInserts () + p.getNumUpdates ());
		
		
		// try to highlight changes
		MarkupElement me = new MarkupElement ("some element");
		BivesTools.genMathMarkupStats (td1.getRoot (), td2.getRoot (), me);
		
		//for (String el : me.getValues ())
		//	System.out.println (">>> \t " + el);
	}

	@Test
	public void testMathDiffStuart () throws XmlDocumentParseException, IOException, JDOMException, BivesConnectionException
	{
		TreeDocument td1 = new TreeDocument (XmlTools.readDocument (StuartMathOne), MathOne.toURI ());
		TreeDocument td2 = new TreeDocument (XmlTools.readDocument (StuartMathTwo), MathTwo.toURI ());
		
		RegularDiff differ = new RegularDiff (td1, td2);
		differ.mapTrees ();
		
		Patch p = differ.getPatch ();
		/*assertEquals ("expected to see 3 deletes", 3, p.getNumDeletes ());
		assertEquals ("expected to see 1 move", 1, p.getNumMoves ());
		assertEquals ("expected to see 0 inserts or updates", 0, p.getNumInserts () + p.getNumUpdates ());*/
		
		
		// try to highlight changes
		MarkupElement me = new MarkupElement ("some element");
		BivesTools.genMathMarkupStats (td1.getRoot (), td2.getRoot (), me);
		
		//for (String el : me.getValues ())
		//	System.out.println (">>> \t " + el);
	}
	
}

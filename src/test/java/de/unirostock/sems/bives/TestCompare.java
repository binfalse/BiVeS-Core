/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class TestCompare
{
	
	private static final File		SIMPLE_DOC	= new File ("test/simple.xml");

	private static TreeDocument simpleFile;

	/**
	 * Read files.
	 */
	@BeforeClass
	public static void readFiles ()
	{
		if (SIMPLE_DOC.canRead ())
		{
			try
			{
				simpleFile = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), SIMPLE_DOC.toURI ());
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot read ", SIMPLE_DOC, " -> skipping tests");
			}
		}
		else
		{
			LOGGER.error ("cannot read ", SIMPLE_DOC, " -> skipping tests");
		}
	}
	
	
	/**
	 * Test annotation stuff
	 */
	@Test
	public void testAnnotation ()
	{
		try
		{
			TreeDocument supp1 = new TreeDocument (XmlTools.readDocument (new File ("test/annotation-1")), null);
			TreeDocument supp2 = new TreeDocument (XmlTools.readDocument (new File ("test/annotation-2")), null);
			/*TreeDocument supp1 = new TreeDocument (XmlTools.readDocument (new File ("/home/martin/unisonSyncPrivate/education/stuff/diffonto/BIOMD0000000056/2008-03-28")), null);
			TreeDocument supp2 = new TreeDocument (XmlTools.readDocument (new File ("/home/martin/unisonSyncPrivate/education/stuff/diffonto/BIOMD0000000056/2008-08-21")), null);*/
			
		// ok, let's ask bives for its opinion on that
			Diff diff = new RegularDiff (supp1, supp2);
			//LOGGER.setMinLevel (LOGGER.DEBUG);
			//LOGGER.setLogFile (new File ("/tmp/bives/annotationlog-again"));
			//LOGGER.setLogToFile (true);
			diff.mapTrees ();
			//LOGGER.setMinLevel (LOGGER.WARN);
			//LOGGER.setLogToFile (false);
			Patch patch = diff.getPatch ();
			TestPatching.checkPatch (patch);
			//System.out.println (XmlTools.prettyPrintDocument (patch.getDocument ()));
			assertEquals ("expected 2 moves", 2, patch.getNumMoves ());
			//assertEquals ("expected 0 deletes|inserts|moves", 2, patch.getNumMoves ());
			//assertEquals ("expected 0 deletes|inserts|moves", 0, patch.getNumDeletes () + patch.getNumInserts ());*/
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected error comparing annotations: " + e);
		}
		
	}
	
	
	/**
	 * Test fun stuff
	 */
	@Test
	public void testBloedsinn ()
	{
		try
		{
			TreeDocument supp1 = new TreeDocument (XmlTools.readDocument (new File ("test/bloedsinn1")), null);
			TreeDocument supp2 = new TreeDocument (XmlTools.readDocument (new File ("test/bloedsinn2")), null);
			
		// ok, let's ask bives for its opinion on that
			Diff diff = new RegularDiff (supp1, supp2);
			diff.mapTrees ();
			Patch patch = diff.getPatch ();
			TestPatching.checkPatch (patch);
			//System.out.println (XmlTools.prettyPrintDocument (patch.getDocument ()));
			assertEquals ("expected 1 update", 1, patch.getNumUpdates ());
			assertEquals ("expected 0 deletes|inserts|moves", 2, patch.getNumMoves ());
			assertEquals ("expected 0 deletes|inserts|moves", 0, patch.getNumDeletes () + patch.getNumInserts ());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected error comparing bloedsinn: " + e);
		}
	}
	
	
	/**
	 * Test mod compare liebal stuff.
	 */
	@Test
	public void testModCompareLiebal ()
	{
		try
		{
			TreeDocument supp1 = new TreeDocument (XmlTools.readDocument (new File ("test/liebal-2012/BSA-laczsynth-2012-11-10")), null);
			TreeDocument supp2 = new TreeDocument (XmlTools.readDocument (new File ("test/liebal-2012/BSA-laczsynth-2012-11-11")), null);
			
			
		// ok, let's ask bives for its opinion on that
			Diff diff = new RegularDiff (supp1, supp2);
			diff.mapTrees ();
			Patch patch = diff.getPatch ();
			TestPatching.checkPatch (patch);
			
			assertEquals ("expected 3 inserts", 3, patch.getNumInserts ());
			assertEquals ("expected 0 deletes|updates|moves", 0, patch.getNumDeletes () + patch.getNumUpdates () + patch.getNumMoves ());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected error comparing liebal models: " + e);
		}
	}
	
	
	/**
	 * Test mod compare supp paper.
	 */
	@Test
	public void testModCompareSuppPaper ()
	{
		try
		{
			TreeDocument supp1 = new TreeDocument (XmlTools.readDocument (new File ("test/paper-supp-1.xml")), null);
			TreeDocument supp2 = new TreeDocument (XmlTools.readDocument (new File ("test/paper-supp-2.xml")), null);
			assertFalse ("copy and original do not differ!?", supp1.equals (supp2));
			
		// ok, let's ask bives for its opinion on that
			Diff diff = new RegularDiff (supp1, supp2);
			diff.mapTrees ();
			Patch patch = diff.getPatch ();
			TestPatching.checkPatch (patch);
			
			assertEquals ("expected 5 deletes", 5, patch.getNumDeletes ());
			assertEquals ("expected 8 inserts", 8, patch.getNumInserts ());
			// TODO
			if (0 != patch.getNumMoves ())
			{
				LOGGER.warn (diff.getDiff ());
				LOGGER.warn ("I don't want a move here!");
				/*assertEquals ("I don't want a move here!", 0, 
					patch.getNumMoves ());*/
			}
			assertEquals ("expected 0 updates", 0, patch.getNumUpdates ());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected error comparing paper supplementals: " + e);
		}
	}
	
	/**
	 * Test mod compare.
	 */
	@Test
	public void testModCompare ()
	{
		try
		{
			// first: create a copy
			TreeDocument test = new TreeDocument (XmlTools.readDocument (DocumentTools.printSubDoc (simpleFile.getRoot ())), null);
			// make sure there is no diff
			assertTrue ("copy and original differ!?", test.equals (simpleFile));
			
			// ok, let's ask bives for its opinion on that
			Diff diff = new RegularDiff (simpleFile, test);
			diff.mapTrees ();
			Patch patch = diff.getPatch ();
			TestPatching.checkPatch (patch);
			assertEquals ("did not expect any operations in patch", 0,
				patch.getNumDeletes () + 
				patch.getNumInserts () + 
				patch.getNumMoves () + 
				patch.getNumUpdates ());
			
			// cool. just modify the document and check again
			// here i just attach a copy of root's first node to root.
			test.getRoot ().addChild (((DocumentNode)test.getRoot ().getChildren ().get (0)).extract ());
			
			// just a litte successful-check
			assertFalse ("now id's shouldn't be unique", test.uniqueIds ());
			
			// ok, check doc equality
			assertFalse ("documents should differ", test.equals (simpleFile));
			
			// and bives?
			diff = new RegularDiff (simpleFile, test);
			diff.mapTrees ();
			patch = diff.getPatch ();
			TestPatching.checkPatch (patch);
			
			// diff?
			assertTrue ("expected at least some operations in diff", 0 < 
				patch.getNumInserts () + 
				patch.getNumMoves ());
			assertEquals ("did not expected a delete or update", 0, 
				patch.getNumDeletes () + 
				patch.getNumUpdates ());
			
			// TODO
			if (0 != patch.getNumMoves ())
			{
				LOGGER.warn (diff.getDiff ());
				LOGGER.warn ("I don't want a move here!");
				/*assertEquals ("I don't want a move here!", 0, 
					patch.getNumMoves ());*/
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ("unexpected error reading exported document" + e);
		}
	}
}

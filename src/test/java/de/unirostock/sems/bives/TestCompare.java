/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.*;

import java.io.File;

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

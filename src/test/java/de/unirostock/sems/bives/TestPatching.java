package de.unirostock.sems.bives;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.NodeConnection;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class TestPatching.
 */
@RunWith(JUnit4.class)
public class TestPatching
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
	 * Test patch producer1.
	 */
	@Test
	public void testPatchProducer1 ()
	{
		// match all
		TreeDocument doc1 = new TreeDocument (simpleFile);
		TreeDocument doc2 = new TreeDocument (simpleFile);

		
		SimpleConnectionManager conMgmt = new SimpleConnectionManager (doc1, doc2);
		
		try
		{
			// connect subtrees
			List<TreeNode> nodes = new ArrayList<TreeNode> ();
			nodes.add (doc1.getRoot ());

			conMgmt.addConnection (new NodeConnection (doc1.getRoot (), doc2.getRoot ()));
			
			while (!nodes.isEmpty ())
			{
				TreeNode tn = nodes.remove (0);
				if (tn.getType () != TreeNode.DOC_NODE)
					continue;
				DocumentNode node = (DocumentNode) tn;
				for (TreeNode n : node.getChildren ())
				{
					nodes.add (n);
					conMgmt.addConnection (new NodeConnection (doc1.getNodeByPath (n.getXPath ()), doc2.getNodeByPath (n.getXPath ())));
				}
			}
		}
		catch (BivesConnectionException e)
		{
			LOGGER.error (e, "couldn't add connections");
			fail ("couldn't add connections " + e);
		}
		
		doc1.resetAllModifications ();
		doc1.getRoot ().evaluate (conMgmt);
		doc2.resetAllModifications ();
		doc2.getRoot ().evaluate (conMgmt);
		
		PatchProducer prod = new PatchProducer ();
		prod.init (conMgmt, doc1, doc2);
		String patch = prod.produce ();
		assertFalse ("XML patch shouldn't be empty", patch.isEmpty ());
		
		Patch p = prod.getPatch ();
		assertEquals ("expected 0 operations", 0, p.getNumUpdates () + p.getNumMoves () + p.getNumInserts () + p.getNumDeletes ());
		checkPatch (p);
	}

	/**
	 * Test patch producer2.
	 */
	@Test
	public void testPatchProducer2 ()
	{
		// match subtree
		TreeDocument doc1 = new TreeDocument (simpleFile);
		TreeDocument doc2 = new TreeDocument (simpleFile);
		DocumentNode dn1 = doc1.getNodeById ("messageone");

		
		SimpleConnectionManager conMgmt = new SimpleConnectionManager (doc1, doc2);
		
		try
		{
			// connect subtrees
			List<TreeNode> nodes = new ArrayList<TreeNode> ();
			nodes.add (dn1);

			conMgmt.addConnection (new NodeConnection (doc1.getNodeByPath (dn1.getXPath ()), doc2.getNodeByPath (dn1.getXPath ())));
			
			while (!nodes.isEmpty ())
			{
				TreeNode tn = nodes.remove (0);
				if (tn.getType () != TreeNode.DOC_NODE)
					continue;
				DocumentNode node = (DocumentNode) tn;
				for (TreeNode n : node.getChildren ())
				{
					nodes.add (n);
					conMgmt.addConnection (new NodeConnection (doc1.getNodeByPath (n.getXPath ()), doc2.getNodeByPath (n.getXPath ())));
				}
			}
		}
		catch (BivesConnectionException e)
		{
			LOGGER.error (e, "couldn't add connections");
			fail ("couldn't add connections " + e);
		}
		
		doc1.resetAllModifications ();
		doc1.getRoot ().evaluate (conMgmt);
		doc2.resetAllModifications ();
		doc2.getRoot ().evaluate (conMgmt);
		
		PatchProducer prod = new PatchProducer ();
		prod.init (conMgmt, doc1, doc2);
		String patch = prod.produce ();
		assertFalse ("XML patch shouldn't be empty", patch.isEmpty ());
		
		Patch p = prod.getPatch ();
		assertEquals ("expected 0 updates", 0, p.getNumUpdates ());
		assertEquals ("expected 1 moves", 1, p.getNumMoves ());
		assertEquals ("expected 10 inserts", 10, p.getNumInserts ());
		assertEquals ("expected 10 deletes", 10, p.getNumDeletes ());
		checkPatch (p);
	}

	/**
	 * Test patch producer3.
	 */
	@Test
	public void testPatchProducer3 ()
	{
		// match subtrees crossed
		TreeDocument doc1 = new TreeDocument (simpleFile);
		TreeDocument doc2 = new TreeDocument (simpleFile);
		DocumentNode dn1 = doc1.getNodeById ("messageone");
		DocumentNode dn2 = doc2.getNodeById ("messagetwo");

		
		SimpleConnectionManager conMgmt = new SimpleConnectionManager (doc1, doc2);
		
		try
		{
			// connect subtrees
			List<TreeNode> nodes = new ArrayList<TreeNode> ();
			nodes.add (dn1);

			conMgmt.addConnection (new NodeConnection (doc1.getNodeByPath (dn1.getXPath ()), doc2.getNodeByPath (dn2.getXPath ())));
			
			while (!nodes.isEmpty ())
			{
				TreeNode tn = nodes.remove (0);
				if (tn.getType () != TreeNode.DOC_NODE)
					continue;
				DocumentNode node = (DocumentNode) tn;
				for (TreeNode n : node.getChildren ())
				{
					nodes.add (n);
					conMgmt.addConnection (new NodeConnection (doc1.getNodeByPath (n.getXPath ()), doc2.getNodeByPath (n.getXPath ().replace ("message[1]", "message[2]"))));
				}
			}
		}
		catch (BivesConnectionException e)
		{
			LOGGER.error (e, "couldn't add connections");
			fail ("couldn't add connections " + e);
		}
		
		doc1.resetAllModifications ();
		doc1.getRoot ().evaluate (conMgmt);
		doc2.resetAllModifications ();
		doc2.getRoot ().evaluate (conMgmt);
		
		PatchProducer prod = new PatchProducer ();
		prod.init (conMgmt, doc1, doc2);
		String patch = prod.produce ();
		assertFalse ("XML patch shouldn't be empty", patch.isEmpty ());
		
		Patch p = prod.getPatch ();
		assertEquals ("expected 4 updates", 4, p.getNumUpdates ());
		assertEquals ("expected 1 moves", 1, p.getNumMoves ());
		assertEquals ("expected 11 inserts", 11, p.getNumInserts ());
		assertEquals ("expected 11 deletes", 11, p.getNumDeletes ());
		checkPatch (p);
	}
	
	
	/**
	 * Test patch.
	 */
	@Test
	public void testPatch ()
	{
		TreeDocument doc = new TreeDocument (simpleFile);
		
		try
		{
			Patch patch = new Patch (true);
			String docStr = XmlTools.prettyPrintDocument (patch.getDocument ());
			assertFalse ("XML patch shouldn't be empty", docStr.isEmpty ());
			
			patch.deleteSubtree (doc.getRoot (), -1);
			//System.out.println (XmlTools.prettyPrintDocument (patch.getDocument ()));
			assertEquals ("not exactly all nodes deleted?", doc.getNumNodes () + 4 /*attr*/, patch.getNumDeletes ());
			assertEquals ("there shouldn't be a non-delete", 0, patch.getNumInserts () + patch.getNumMoves () + patch.getNumUpdates ());

			checkPatch (patch);
			patch = new Patch (true);
			
			DocumentNode dn1 = doc.getNodeById ("messageone");
			DocumentNode dn2 = doc.getNodeById ("messagetwo");

			patch.deleteNode (dn1.getChildren ().get (0), -1);
			assertEquals ("more than one delte?", 1, patch.getNumDeletes ());
			assertEquals ("there shouldn't be a non-delete", 0, patch.getNumInserts () + patch.getNumMoves () + patch.getNumUpdates ());
			
			
			patch.deleteNode (dn1, -1);
			assertEquals ("not exactly three more deltes?", 4, patch.getNumDeletes ());
			assertEquals ("there shouldn't be a non-delete", 0, patch.getNumInserts () + patch.getNumMoves () + patch.getNumUpdates ());
			

			patch.insertNode (dn2.getChildren ().get (0), -1);
			assertEquals ("more than one insert?", 1, patch.getNumInserts ());
			assertEquals ("not exactly four deltes?", 4, patch.getNumDeletes ());
			assertEquals ("there shouldn't be a non-(insert|delete)", 0, patch.getNumMoves () + patch.getNumUpdates ());
			
			checkPatch (patch);
			patch = new Patch (true);

			patch.insertSubtree (doc.getRoot (), -1);
			assertEquals ("not exactly all nodes inserted?", doc.getNumNodes () + 4 /*attr*/, patch.getNumInserts ());
			assertEquals ("there shouldn't be a non-insert", 0, patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
			
			
			checkPatch (patch);
			patch = new Patch (true);
			

			patch.insertNode (dn2.getChildren ().get (0), -1);
			assertEquals ("more than one insert?", 1, patch.getNumInserts ());
			assertEquals ("there shouldn't be a non-insert", 0, patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
			
			
			patch.insertNode (dn2, -1);
			assertEquals ("more than three more inserts?", 4, patch.getNumInserts ());
			assertEquals ("there shouldn't be a non-insert", 0, patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
			checkPatch (patch);
			docStr = XmlTools.prettyPrintDocument (patch.getDocument ());
			assertFalse ("XML patch shouldn't be empty", docStr.isEmpty ());
			checkPatch (patch);
		}
		catch (Exception e)
		{
			LOGGER.error (e, "unexpected error creating a patch");
		}
	}

	/**
	 * Check patch.
	 *
	 * @param patch the patch
	 */
	public static void checkPatch (Patch patch)
	{
		assertNotNull ("patch shouldn't be null", patch);
		assertTrue ("#inserts in patch needs to be >= 0", 0 <= patch.getNumInserts ());
		assertTrue ("#deletes in patch needs to be >= 0", 0 <= patch.getNumDeletes ());
		assertTrue ("#moves in patch needs to be >= 0", 0 <= patch.getNumMoves ());
		assertTrue ("#updates in patch needs to be >= 0", 0 <= patch.getNumUpdates ());
		
	}
}

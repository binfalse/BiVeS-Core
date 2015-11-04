package de.unirostock.sems.bives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.jdom2.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.bives.algorithm.NodeConnection;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class TestPatching.
 * 
 * TODO: test no annotations
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
	 * Test annotations with comodi.
	 */
	@Test
	public void testPatchAnnotations ()
	{
		try
		{
			TreeDocument supp1 = new TreeDocument (XmlTools.readDocument (new File ("test/annotation-1")), null);
			TreeDocument supp2 = new TreeDocument (XmlTools.readDocument (new File ("test/annotation-2")), null);
			

			Diff diff = new RegularDiff (supp1, supp2);
			diff.mapTrees ();
			Patch patch = diff.getPatch ();
//			System.out.println (diff.getDiff (false));
//			GeneralTools.stringToFile (diff.getDiff (false), new File ("/tmp/bives.debug1"));
//			GeneralTools.stringToFile (patch.getAnnotationDocumentXml (), new File ("/tmp/bives.debug2"));
			TestPatching.checkPatch (patch);

			Document patchDoc = patch.getDocument (false);
			TreeDocument annotationsDoc = new TreeDocument (XmlTools.readDocument (patch.getAnnotationDocumentXml ()), null);
			
			
			
			TreeDocument myPathDoc = new TreeDocument (patchDoc, null);
			
			StmtIterator stmtIt = patch.getAnnotations ().getAnnotaions ().listStatements ();
			int i = 0;
			Map<String,String> map = new HashMap<String,String> ();
			while (stmtIt.hasNext ())
			{
//				System.out.println (stmtIt.next ().getSubject ());
				Statement stmt = stmtIt.next ();
				if (stmt.toString ().contains ("http://purl.org/net/comodi#Deletion"))
				{
					i++;
					String subj = stmt.getSubject ().toString ();
					String subjId = subj.substring ("file://bives-differences.patch#".length ());
					
					// System.out.println (subj);
					assertFalse ("two subjects annotated with a deletion", map.containsKey (subj));
					map.put (subj, subj);
					
					assertTrue ("xpath if annotated with delete", myPathDoc.getNodeById (subjId).getXPath ().contains ("delete"));
				}
			}

			assertEquals ("num deletes and del annotations expected to be equal", patch.getDeletes ().getChildren ().size (), i);
			
			// num http://purl.org/net/comodi#Insertion = num children of insert
			assertEquals ("expected as much inserts as insert annotations", patch.getNumInserts (), countAnnotationUrls (annotationsDoc.getRoot (), "http://purl.org/net/comodi#Insertion"));
			assertTrue ("expected at least one insert", patch.getNumInserts () != 0);
			assertEquals ("expected as much deletes as delete annotations", patch.getNumDeletes (), countAnnotationUrls (annotationsDoc.getRoot (), "http://purl.org/net/comodi#Deletion"));
			assertTrue ("expected at least one delete", patch.getNumDeletes () != 0);
			assertEquals ("expected as much moves as move annotations", patch.getNumMoves (), countAnnotationUrls (annotationsDoc.getRoot (), "http://purl.org/net/comodi#Move") + countAnnotationUrls (annotationsDoc.getRoot (), "http://purl.org/net/comodi#PermutationOfEntities"));
			assertTrue ("expected at least one move", patch.getNumMoves () != 0);
			assertEquals ("expected as much updates as update annotations", patch.getNumUpdates (), countAnnotationUrls (annotationsDoc.getRoot (), "http://purl.org/net/comodi#Update"));
			assertTrue ("expected at least one update", patch.getNumUpdates () != 0);
			
			// num http://purl.org/net/comodi#Attribute = num nodes with label attribute
			// etc
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected error creating patch with annotations: " + e);
		}
	}
	
	private int countAnnotationUrls (DocumentNode dn, String url)
	{
		int i = 0;
		if (dn.getAttribute ("resource") != null && dn.getAttributeValue ("resource").endsWith (url))
			i++;
		
		for (TreeNode tn : dn.getChildren ())
			if (tn.getType () == TreeNode.DOC_NODE)
				i += countAnnotationUrls (((DocumentNode) tn), url);
		
		return i;
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
		assertTrue ("XML patch should contain bives version", patch.contains ("BiVeS compiled with"));
		
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

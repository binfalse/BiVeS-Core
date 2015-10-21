/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.ds.rdf.RDF;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class RdfTests
{

	@Test
	public void testRdfStatements ()
	{
		try
		{
			TreeDocument anno1 = new TreeDocument (XmlTools.readDocument (new File ("test/annotation-1")), null);
			List<DocumentNode> nodes = anno1.getNodesByTag ("RDF");
			for (DocumentNode node : nodes)
			{
				RDF rdf = new RDF (node);
				assertTrue ("expected different number of rdf statements", 0 < rdf.getDescriptions ().size ());
				
				
				/*
				String xml = XmlTools.prettyPrintDocument (DocumentTools.getSubDoc (rdf.getNode ()));
				System.out.println (xml);
				
				Model model = ModelFactory.createDefaultModel();
				model.read(new ByteArrayInputStream(xml.getBytes ()), "test/annotation-1", "RDF/XML");
				StmtIterator stmts = model.listStatements ();
				while (stmts.hasNext ())
				{
					Statement stmt = stmts.next ();
					System.out.println (stmt);
				}
				*/
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("failed to test for rdf statements");
		}
		
	}
}

/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

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
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("failed to test for rdf statements");
		}
		
	}
}

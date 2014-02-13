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
import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.crn.CRNCompartment;
import de.unirostock.sems.bives.ds.crn.CRNReaction;
import de.unirostock.sems.bives.ds.crn.CRNSubstance;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.XmlTools;

/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class TestGraphs
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
	
	public CRNCompartment addCompartment (DocumentNode node, String label, CRN crn, boolean a)
	{
		CRNCompartment compartment = null;
		if (a)
			compartment = new CRNCompartment (crn, label, null, node, null);
		else
			compartment = new CRNCompartment (crn, null, label, null, node);
		
		crn.setCompartment (node, compartment);
		return compartment;
	}
	
	public CRNSubstance addSubstrate (DocumentNode node, String label, CRN crn, CRNCompartment compartment, boolean a)
	{
		CRNSubstance subst = null;
		if (a)
			subst = new CRNSubstance (crn, label, null, node, null, compartment, null);
		else
			subst = new CRNSubstance (crn, null, label, null, node, null, compartment);
		
		crn.setSubstance (node, subst);
		return subst;
	}
	
	public CRNReaction addReaction (DocumentNode node, String label, CRN crn, CRNCompartment compartment, boolean a)
	{
		// NOTE: reversible will always be true
		CRNReaction react = null;
		if (a)
			react = new CRNReaction (crn, label, null, node, null, compartment, null, true);
		else
			react = new CRNReaction (crn, null, label, null, node, null, compartment, true);
		
		crn.setReaction (node, react);
		return react;
	}
	
	
	@Test
	public void testCrn ()
	{
		// test the chemical reaction network stuff
		CRN crn = new CRN ();
		
		// create a dummy node
		DocumentNode dummy = simpleFile.getRoot ();

		// create a deleted compartment
		CRNCompartment compartment1 = addCompartment (dummy, "compartment in A", crn, true);
		
		// create deleted species
		CRNSubstance substrate1 = addSubstrate (dummy, "substrate in A", crn, compartment1, true);
		
		// create deleted reaction
		CRNReaction reaction1 = addReaction (dummy, "reaction in A", crn, compartment1, true);
		reaction1.addInputA (substrate1, null);
		reaction1.addOutputA (substrate1, null);
		
		assertEquals ("cannot retrieve compartment", crn.getCompartment (dummy), compartment1);
		assertEquals ("cannot retrieve substrate", crn.getSubstance (dummy),substrate1 );
		assertEquals ("cannot retrieve reaction", crn.getReaction (dummy), reaction1);

		assertNotNull ("expected to get a node", compartment1.getA ());
		assertNull ("didn't expect to get a node", compartment1.getB ());
		
		assertNotNull ("expected to get a node", substrate1.getA ());
		assertNull ("didn't expect to get a node", substrate1.getB ());
		
		assertNotNull ("expected to get a node", reaction1.getA ());
		assertNull ("didn't expect to get a node", reaction1.getB ());

		assertEquals ("unexpected label in compartment", "compartment in A", compartment1.getLabel ());
		assertEquals ("unexpected label in substrate", "substrate in A", substrate1.getLabel ());
		assertEquals ("unexpected label in reaction", "reaction in A", reaction1.getLabel ());
		
		
		
		
		fail ("todo");
		// test different compartments, what happens with reaction etc.
		// get substrate by node
		// what happens if inputA and outputB in a reaction?
		// create a modified compartment
		// create an inserted compartment
		// create an unchanged compartment
		
		// test single
		crn.setSingleDocument ();
		
		
	}
	
	@Test
	public void testHrn ()
	{
		// test the hierarchy network stuff
		HierarchyNetwork hn = new HierarchyNetwork ();
		
		fail ("todo");
		// test single
		hn.setSingleDocument ();
	}
}

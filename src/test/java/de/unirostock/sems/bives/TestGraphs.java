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
import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkComponent;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkVariable;
import de.unirostock.sems.bives.ds.ontology.SBOTerm;
import de.unirostock.sems.bives.ds.rn.ReactionNetwork;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkCompartment;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkReaction;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkSubstance;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkSubstanceRef;
import de.unirostock.sems.bives.exception.BivesUnsupportedException;
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
	 * Adds the compartment.
	 *
	 * @param node the node
	 * @param label the label
	 * @param crn the crn
	 * @param a the a
	 * @return the reaction network compartment
	 */
	public ReactionNetworkCompartment addCompartment (DocumentNode node, String label, ReactionNetwork crn, boolean a)
	{
		ReactionNetworkCompartment compartment = null;
		if (a)
			compartment = new ReactionNetworkCompartment (crn, label, null, node, null);
		else
			compartment = new ReactionNetworkCompartment (crn, null, label, null, node);
		
		crn.setCompartment (node, compartment);
		return compartment;
	}
	
	/**
	 * Adds the substrate.
	 *
	 * @param node the node
	 * @param label the label
	 * @param crn the crn
	 * @param compartment the compartment
	 * @param a the a
	 * @return the reaction network substance
	 */
	public ReactionNetworkSubstance addSubstrate (DocumentNode node, String label, ReactionNetwork crn, ReactionNetworkCompartment compartment, boolean a)
	{
		ReactionNetworkSubstance subst = null;
		if (a)
			subst = new ReactionNetworkSubstance (crn, label, null, node, null, compartment, null);
		else
			subst = new ReactionNetworkSubstance (crn, null, label, null, node, null, compartment);
		
		crn.setSubstance (node, subst);
		return subst;
	}
	
	/**
	 * Adds the reaction.
	 *
	 * @param node the node
	 * @param label the label
	 * @param crn the crn
	 * @param compartment the compartment
	 * @param a the a
	 * @return the reaction network reaction
	 */
	public ReactionNetworkReaction addReaction (DocumentNode node, String label, ReactionNetwork crn, ReactionNetworkCompartment compartment, boolean a)
	{
		// NOTE: reversible will always be true
		ReactionNetworkReaction react = null;
		if (a)
			react = new ReactionNetworkReaction (crn, label, null, node, null, compartment, null, true);
		else
			react = new ReactionNetworkReaction (crn, null, label, null, node, null, compartment, true);
		
		crn.setReaction (node, react);
		return react;
	}
	
	
	/**
	 * Test crn.
	 */
	@Test
	public void testCrn ()
	{
		// test the chemical reaction network stuff
		ReactionNetwork crn = new ReactionNetwork ();
		
		// create a dummy node
		DocumentNode dummy = simpleFile.getRoot ();
		DocumentNode dummy2 = simpleFile.getNodeById ("messageone");

		ReactionNetworkCompartment compartment1 = addCompartment (dummy, "compartment in A", crn, true);
		ReactionNetworkSubstance substrate1 = addSubstrate (dummy, "substrate in A", crn, compartment1, true);
		ReactionNetworkReaction reaction1 = addReaction (dummy, "reaction in A", crn, compartment1, true);

		ReactionNetworkCompartment compartment2 = addCompartment (dummy2, "compartment in B", crn, false);
		ReactionNetworkSubstance substrate2 = addSubstrate (dummy2, "substrate in B", crn, compartment2, false);
		//CRNReaction reaction2 = addReaction (dummy2, "reaction in B", crn, compartment2, true);
		
		
		
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
		
		// what happens if nothing's changed?
		compartment1.setLabelB ("compartment in A");
		compartment1.setDocB (dummy);
		
		substrate1.setCompartmentB (compartment1);
		substrate1.setDocB (dummy);
		substrate1.setLabelB ("substrate in A");
		
		reaction1.setCompartmentB (compartment1);
		reaction1.setLabelB ("reaction in A");
		reaction1.setDocB (dummy);
		reaction1.addInputB (substrate1, null);
		reaction1.addOutputB (substrate1, null);

		assertEquals ("unexpected label in compartment", "compartment in A", compartment1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, compartment1.getModification ());

		assertEquals ("unexpected label in substrate", "substrate in A", substrate1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, substrate1.getModification ());
		assertEquals ("unexpected compartment in substrate", compartment1, substrate1.getCompartment ());

		assertEquals ("unexpected label in reaction", "reaction in A", reaction1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, reaction1.getModification ());
		assertEquals ("unexpected compartment in reaction", compartment1, reaction1.getCompartment ());
		
		
		// what happens if we change something?
		compartment1.setLabelB ("compartment in B");
		compartment1.setDocB (dummy);
		
		substrate1.setCompartmentB (compartment1);
		substrate1.setDocB (dummy);
		substrate1.setLabelB ("substrate in B");
		
		reaction1.setCompartmentB (compartment1);
		reaction1.setLabelB ("reaction in N");
		reaction1.setDocB (dummy);
		reaction1.addInputB (substrate1, null);
		reaction1.addOutputB (substrate1, null);


		assertFalse ("unexpected label in compartment", "compartment in A".equals (compartment1.getLabel ()));
		assertEquals ("expected no modification", GraphEntity.MODIFIED, compartment1.getModification ());

		assertFalse ("unexpected label in substrate", "substrate in A".equals (substrate1.getLabel ()));
		assertEquals ("expected no modification", GraphEntity.MODIFIED, substrate1.getModification ());
		assertEquals ("unexpected compartment in substrate", compartment1, substrate1.getCompartment ());

		assertFalse ("unexpected label in reaction", "reaction in A".equals (reaction1.getLabel ()));
		assertEquals ("expected no modification", GraphEntity.MODIFIED, reaction1.getModification ());
		assertEquals ("unexpected compartment in reaction", compartment1, reaction1.getCompartment ());
		

		// change compartment		
		substrate1.setCompartmentB (compartment2);
		substrate1.setDocB (dummy);
		substrate1.setLabelB ("substrate in B");
		
		reaction1.setCompartmentB (compartment2);
		reaction1.setLabelB ("reaction in N");
		reaction1.setDocB (dummy);
		reaction1.addInputB (substrate1, null);
		reaction1.addOutputB (substrate1, null);


		assertFalse ("unexpected label in substrate", "substrate in A".equals (substrate1.getLabel ()));
		assertEquals ("expected no modification", GraphEntity.MODIFIED, substrate1.getModification ());
		assertNull ("unexpected compartment in substrate", substrate1.getCompartment ());

		assertFalse ("unexpected label in reaction", "reaction in A".equals (reaction1.getLabel ()));
		assertEquals ("expected no modification", GraphEntity.MODIFIED, reaction1.getModification ());
		assertNull ("unexpected compartment in reaction", reaction1.getCompartment ());

		
		// reset?
		compartment1.setLabelB ("compartment in A");
		compartment1.setDocB (dummy);
		
		substrate1.setCompartmentB (compartment1);
		substrate1.setDocB (dummy);
		substrate1.setLabelB ("substrate in A");
		
		reaction1.setCompartmentB (compartment1);
		reaction1.setLabelB ("reaction in A");
		reaction1.setDocB (dummy);
		reaction1.addInputB (substrate1, null);
		reaction1.addOutputB (substrate1, null);

		assertEquals ("unexpected label in compartment", "compartment in A", compartment1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, compartment1.getModification ());

		assertEquals ("unexpected label in substrate", "substrate in A", substrate1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, substrate1.getModification ());
		assertEquals ("unexpected compartment in substrate", compartment1, substrate1.getCompartment ());

		assertEquals ("unexpected label in reaction", "reaction in A", reaction1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, reaction1.getModification ());
		assertEquals ("unexpected compartment in reaction", compartment1, reaction1.getCompartment ());
		
		
		
		// different i/o?
		reaction1.addInputB (substrate2, null);
		reaction1.addOutputA (substrate2, null);
		assertEquals ("unexpected label in compartment", "compartment in A", compartment1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, compartment1.getModification ());

		assertEquals ("unexpected label in substrate", "substrate in A", substrate1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.UNMODIFIED, substrate1.getModification ());
		assertEquals ("unexpected compartment in substrate", compartment1, substrate1.getCompartment ());

		assertEquals ("unexpected label in reaction", "reaction in A", reaction1.getLabel ());
		assertEquals ("expected no modification", GraphEntity.MODIFIED, reaction1.getModification ());
		assertEquals ("unexpected compartment in reaction", compartment1, reaction1.getCompartment ());

		// retest components retrieving
		assertEquals ("cannot retrieve compartment", crn.getCompartment (dummy), compartment1);
		assertEquals ("cannot retrieve substrate", crn.getSubstance (dummy),substrate1 );
		assertEquals ("cannot retrieve reaction", crn.getReaction (dummy), reaction1);
		

		int tmp = 0;
		for (ReactionNetworkSubstanceRef ref : reaction1.getInputs ())
			tmp += ref.getModification ();
		assertEquals ("input of reaction doesn't report an insert", GraphEntity.UNMODIFIED + GraphEntity.INSERT, tmp);
		
		tmp = 0;
		for (ReactionNetworkSubstanceRef ref : reaction1.getOutputs ())
			tmp += ref.getModification ();
		assertEquals ("input of reaction doesn't report an insert", GraphEntity.UNMODIFIED + GraphEntity.DELETE, tmp);
		
		
		
		// test modifiers
		try
		{
			reaction1.addModA (substrate1, null);
			reaction1.addModB (substrate1, SBOTerm.createStimulator ());
			reaction1.addModB (substrate2, null);
			reaction1.addModA (substrate2, null);
			assertEquals ("unexpected number of modifiers", 3, reaction1.getModifiers ().size ());
		}
		catch (BivesUnsupportedException e)
		{
			LOGGER.error (e, "this error wasn't expected!?");
			fail ("this error wasn't expected!?");
		}
		
	}
	
	/**
	 * Test hrn.
	 */
	@Test
	public void testHrn ()
	{
		// test the hierarchy network stuff
		HierarchyNetwork hn = new HierarchyNetwork ();
		

		
		// create a dummy node
		DocumentNode dummy = simpleFile.getRoot ();
		DocumentNode dummy2 = simpleFile.getNodeById ("messageone");
		
		HierarchyNetworkComponent component1 = new HierarchyNetworkComponent (hn, "component A",null, dummy, null);
		HierarchyNetworkComponent component2 = new HierarchyNetworkComponent (hn, null, "component B", null, dummy2);
		
		HierarchyNetworkVariable var1 = new HierarchyNetworkVariable (hn, "var A", null, dummy, null, component1, null);
		HierarchyNetworkVariable var2 = new HierarchyNetworkVariable (hn, null, "var B", null, dummy2, null, component2);
		
		
		
		// test component belonging and modification
		var1.setComponentA (component1);
		assertEquals ("expected to see a deletion", GraphEntity.DELETE, var1.getModification ());
		
		var1.setComponentB (component1);
		var1.setDocB (dummy2);
		var1.setLabelB ("var A");
		assertEquals ("expected to get a component", component1, var1.getComponent ());
		assertEquals ("unexpected modification", GraphEntity.UNMODIFIED, var1.getModification ());
		
		var1.setComponentB (component2);
		var1.setDocB (dummy2);
		var1.setLabelB ("label b");
		assertNull ("didn't expect to get a component (different components)", var1.getComponent ());
		assertEquals ("expected to see a modification", GraphEntity.MODIFIED, var1.getModification ());

		var1.setComponentA (null);
		var1.setDocA (null);
		var1.setLabelA (null);
		assertEquals ("expected to see an insertion", GraphEntity.INSERT, var1.getModification ());
		
		
		// component modifications
		assertEquals ("expected to see a deletion in component", GraphEntity.DELETE, component1.getModification ());

		component1.setDocB (dummy2);
		component1.setLabelB ("component A");
		assertEquals ("unexpected modification in component", GraphEntity.UNMODIFIED, component1.getModification ());

		component1.setDocB (dummy2);
		component1.setLabelB ("component b");
		assertEquals ("unexpected modification in component", GraphEntity.MODIFIED, component1.getModification ());

		component1.setDocA (null);
		component1.setLabelA (null);
		assertEquals ("expected to see an insertion", GraphEntity.INSERT, component1.getModification ());
		
		component1.setDocA (dummy2);
		component1.setLabelA ("component b");
		assertEquals ("unexpected modification in component", GraphEntity.UNMODIFIED, component1.getModification ());
		
		
		// variable changes
		component1.addVariable (var1);
		component1.addVariable (var2);
		assertEquals ("unexpected modification in component", GraphEntity.UNMODIFIED, component1.getModification ());
		
		component1.addChildA (component2);
		assertEquals ("unexpected modification in component (UPDATE WIKI!)", GraphEntity.UNMODIFIED, component1.getModification ());
		
		component2.addChildA (component1);
		assertEquals ("expected modification after hierarchy change", GraphEntity.MODIFIED, component1.getModification ());

	}
}

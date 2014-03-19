/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.ds.ontology.SBOTerm;

/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class TestSBO
{
	@Test
	public void testStatics ()
	{
		assertEquals ("inhibitor != inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier (SBOTerm.createInhibitor ().getSBOTerm ()));
		assertEquals ("stimulator != stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier (SBOTerm.createStimulator ().getSBOTerm ()));
		
		
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000013"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000021"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000459"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000460"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000461"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000462"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000535"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000534"));
		assertEquals ("expected to find a stimulator", SBOTerm.MOD_STIMULATOR, SBOTerm.resolveModifier ("SBO:0000533"));


		assertEquals ("expected to find an inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier ("SBO:0000020"));
		assertEquals ("expected to find an inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier ("SBO:0000206"));
		assertEquals ("expected to find an inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier ("SBO:0000207"));
		assertEquals ("expected to find an inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier ("SBO:0000536"));
		assertEquals ("expected to find an inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier ("SBO:0000537"));
		assertEquals ("expected to find an inhibitor", SBOTerm.MOD_INHIBITOR, SBOTerm.resolveModifier ("SBO:0000597"));
		
		
	}
}

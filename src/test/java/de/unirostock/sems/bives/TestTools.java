/**
 * 
 */
package de.unirostock.sems.bives;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.tools.BivesTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestTools
{
	
	/**
	 * Test version.
	 */
	@Test
	public void testVersion ()
	{
		String version = BivesTools.getBivesVersion ();
		
		assertTrue ("core module wasn't compiled into the bives?",
			version.contains ("Core"));
	}
}

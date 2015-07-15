/**
 * 
 */
package de.unirostock.sems.bives;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author Martin Scharm
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ TestCompare.class, TestGraphs.class, TestMaths.class, TestPatching.class, TestSBO.class })
public class BivesCoreTests
{
	
}

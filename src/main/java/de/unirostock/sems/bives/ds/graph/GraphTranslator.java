/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;


/**
 * The Class GraphTranslator, abstract parent to convert the internal graph representation to (un)common graph formats.
 * All methods might return null if not available in certain cases.
 *
 * @author Martin Scharm
 */
public abstract class GraphTranslator
{
	
	/**
	 * Translate a chemical reaction network.
	 * Might return null if not available.
	 *
	 * @param crn the internal chemical reaction network
	 * @return the graph format
	 * @throws Exception the exception
	 */
	public abstract Object translate (CRN crn) throws Exception;
	
	/**
	 * Translate a hierarchy network.
	 * Might return null if not available.
	 *
	 * @param hn the internal hierarchy network
	 * @return the graph format
	 * @throws Exception the exception
	 */
	public abstract Object translate (HierarchyNetwork hn) throws Exception;
}

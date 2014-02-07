/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;


/**
 * The Class GraphTranslator, abstract parent to convert the internal graph representation to (un)common graph fromats.
 *
 * @author Martin Scharm
 */
public abstract class GraphTranslator
{
	
	/**
	 * Translate a chemical reaction network.
	 *
	 * @param crn the internal chemical reaction network
	 * @return the graph format
	 * @throws Exception the exception
	 */
	public abstract Object translate (CRN crn) throws Exception;
	
	/**
	 * Translate a hierarchy network.
	 *
	 * @param hn the internal hierarchy network
	 * @return the graph format
	 * @throws Exception the exception
	 */
	public abstract Object translate (HierarchyNetwork hn) throws Exception;
}

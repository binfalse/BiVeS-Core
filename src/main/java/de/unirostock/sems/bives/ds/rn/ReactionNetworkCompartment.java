/**
 * 
 */
package de.unirostock.sems.bives.ds.rn;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class ReactionNetworkCompartment representing a compartment in a reaction network.
 *
 * @author Martin Scharm
 */
public class ReactionNetworkCompartment
extends ReactionNetworkEntity
{

	/**
	 * Instantiates a new compartment.
	 *
	 * @param rn the reaction network
	 * @param labelA the label of that compartment in the original document
	 * @param labelB the label of that compartment in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public ReactionNetworkCompartment (ReactionNetwork rn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, ReactionNetworkCompartment outsideA, ReactionNetworkCompartment outsideB)
	{
		super ("c" + rn.getNextCompartmentID (), labelA, labelB, docA, docB, outsideA, outsideB);
	}
	
}

/**
 * 
 */
package de.unirostock.sems.bives.ds.rn;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class ReactionNetworkSubstance representing a substance in a reaction network.
 *
 * @author Martin Scharm
 */
public class ReactionNetworkSubstance
extends ReactionNetworkEntity
{
	/** The compartments hosting this substance in the original/modified version. */
	private ReactionNetworkCompartment compartmentA, compartmentB;

	/**
	 * Instantiates a new substance in a reaction network.
	 *
	 * @param rn the reaction network
	 * @param labelA the label of that compartment in the original document
	 * @param labelB the label of that compartment in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 * @param compartmentA the compartment a
	 * @param compartmentB the compartment b
	 */
	public ReactionNetworkSubstance (ReactionNetwork rn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, ReactionNetworkCompartment compartmentA, ReactionNetworkCompartment compartmentB)
	{
		super ("s" + rn.getNextSubstanceID (), labelA, labelB, docA, docB, compartmentA, compartmentB);
		this.compartmentA = compartmentA;
		this.compartmentB = compartmentB;
		singleDoc = false;
	}
	
	/**
	 * Sets the compartment in original version.
	 *
	 * @param compartment the compartment in original version
	 */
	public void setCompartmentA (ReactionNetworkCompartment compartment)
	{
		this.compartmentA = compartment;
	}
	
	/**
	 * Sets the compartment in modified version.
	 *
	 * @param compartment the compartment in modified version
	 */
	public void setCompartmentB (ReactionNetworkCompartment compartment)
	{
		this.compartmentB = compartment;
	}
	
	/**
	 * Gets the compartment. Will return:
	 * <ul>
	 * <li>the original compartment, if it's the same compartment as in the modified version</li>
	 * <li>null, otherwise</li>
	 * </ul>
	 *
	 * @return the compartment
	 */
	public ReactionNetworkCompartment getCompartment ()
	{
		if (compartmentA == compartmentB)
			return compartmentB;
		if(compartmentB != null) 
			return compartmentB;
		if(compartmentA != null)
			return compartmentA;
		return null;
	}
	
	/**
	 * Gets the SBOTerm describing this substance.
	 *
	 * @return the SBOTerm
	 */
	public String getSBO ()
	{
		if(docA == null && docB == null) return "";
		String a = null, b = null;
		if(docA != null) a = docA.getAttributeValue ("sboTerm");
		if(docB != null) b = docB.getAttributeValue ("sboTerm");
		if (a == null || b == null || !a.equals (b))
			return "";
		return a;
	}
}

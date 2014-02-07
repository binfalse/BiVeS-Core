/**
 * 
 */
package de.unirostock.sems.bives.ds.crn;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class CRNSubstance representing a substance in a chemical reaction network.
 *
 * @author Martin Scharm
 */
public class CRNSubstance
extends CRNEntity
{
	/** The compartments hosting this substance in the original/modified version. */
	private CRNCompartment compartmentA, compartmentB;

	/**
	 * Instantiates a new substance in a chemical reaction network.
	 *
	 * @param crn the chemical reaction network
	 * @param labelA the label of that compartment in the original document
	 * @param labelB the label of that compartment in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 * @param compartmentA the compartment a
	 * @param compartmentB the compartment b
	 */
	public CRNSubstance (CRN crn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, CRNCompartment compartmentA, CRNCompartment compartmentB)
	{
		super ("s" + crn.getNextSubstanceID (), labelA, labelB, docA, docB);
		this.compartmentA = compartmentA;
		this.compartmentB = compartmentB;
		singleDoc = false;
	}
	
	/**
	 * Sets the compartment in original version.
	 *
	 * @param compartment the compartment in original version
	 */
	public void setCompartmentA (CRNCompartment compartment)
	{
		this.compartmentA = compartment;
	}
	
	/**
	 * Sets the compartment in modified version.
	 *
	 * @param compartment the compartment in modified version
	 */
	public void setCompartmentB (CRNCompartment compartment)
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
	public CRNCompartment getCompartment ()
	{
		if (compartmentA == compartmentB)
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
		String a = docA.getAttribute ("sboTerm");
		String b = docA.getAttribute ("sboTerm");
		if (a == null || b == null || !a.equals (b))
			return "";
		return a;
	}
}

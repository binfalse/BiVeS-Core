/**
 * 
 */
package de.unirostock.sems.bives.ds.crn;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class CRNCompartment representing a compartment in a chemical reaction network.
 *
 * @author Martin Scharm
 */
public class CRNCompartment
extends CRNEntity
{

	/**
	 * Instantiates a new compartment.
	 *
	 * @param crn the chemical reaction network
	 * @param labelA the label of that compartment in the original document
	 * @param labelB the label of that compartment in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public CRNCompartment (CRN crn, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		super ("c" + crn.getNextCompartmentID (), labelA, labelB, docA, docB);
	}
	
}

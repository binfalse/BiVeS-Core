/**
 * 
 */
package de.unirostock.sems.bives.ds.crn;

import de.unirostock.sems.bives.ds.SBOTerm;


/**
 * The Class CRNSubstanceRef representing a reference to a CRNSubstance.
 *
 * @author Martin Scharm
 */
public class CRNSubstanceRef
{
	
	/** The substance. */
	private CRNSubstance subst;
	
	/** The reference existent in versions A/B. */
	private boolean refA, refB;
	
	/** The optional modification terms in versions A/B. */
	public SBOTerm modTermA, modTermB;
	
	/** The single doc flag if in single-doc-operation-mode. */
	protected boolean singleDoc;
	
	/**
	 * Instantiates a new substance reference.
	 *
	 * @param subst the substance
	 * @param flagA the existence flag for the original version
	 * @param flagB the existence flag for the modified version
	 * @param modTermA the modification type in the original version
	 * @param modTermB the modification type in the original version
	 */
	public CRNSubstanceRef (CRNSubstance subst, boolean flagA, boolean flagB, SBOTerm modTermA, SBOTerm modTermB)
	{
		this.subst = subst;
		this.refA = flagA;
		this.refB = flagB;
		this.modTermA = modTermA;
		this.modTermB = modTermB;
		this.singleDoc = false;
	}
	
	
	/**
	 * Gets the referenced substance.
	 *
	 * @return the substance
	 */
	public CRNSubstance getSubstance ()
	{
		return subst;
	}
	
	/**
	 * Sets the existence flag for the original version.
	 *
	 * @param flagA the flag for doc A
	 */
	public void setFlagA (boolean flagA)
	{
		this.refA = flagA;
	}
	
	/**
	 * Sets the existence flag for the modified version.
	 *
	 * @param flagB the flag for doc B
	 */
	public void setFlagB (boolean flagB)
	{
		this.refB = flagB;
	}
	
	/**
	 * Gets the SBOTerm.
	 *
	 * @return the SBOTerm
	 */
	public String getSBO ()
	{
		if (modTermA == null && modTermB == null)
			return "";
		if (modTermA == null)
			return modTermB.getSBOTerm ();
		return modTermA.getSBOTerm ();
	}
	
	/**
	 * Gets the SBOTerm as defined in the original version.
	 *
	 * @return the SBOTerm
	 */
	public String getSBOA ()
	{
		if (modTermA == null)
			return "";
		return modTermA.getSBOTerm ();
	}
	
	/**
	 * Gets the SBOTerm as defined in the modified version.
	 *
	 * @return the SBOTerm
	 */
	public String getSBOB ()
	{
		if (modTermB == null)
			return "";
		return modTermB.getSBOTerm ();
	}
	
	/**
	 * Gets the modifier term.
	 *
	 * @return the modifier term
	 */
	public String getModTerm ()
	{
		if (modTermA == null && modTermB == null)
			return SBOTerm.MOD_UNKNOWN;
		if (modTermA == null)
			return modTermB.resolvModifier ();
		return modTermA.resolvModifier ();
	}
	
	/**
	 * Gets the modification term in version A.
	 *
	 * @return the modification term
	 */
	public String getModTermA ()
	{
		if (modTermA == null)
			return SBOTerm.MOD_UNKNOWN;
		return modTermA.resolvModifier ();
	}
	
	/**
	 * Gets the modification term in version B.
	 *
	 * @return the modification term
	 */
	public String getModTermB ()
	{
		if (modTermB == null)
			return SBOTerm.MOD_UNKNOWN;
		return modTermB.resolvModifier ();
	}
	
	/**
	 * Gets the modification.
	 *
	 * @param singleDoc the single doc
	 * @return the modification
	 */
	public int getModification ()
	{
		if (singleDoc)
			return CRN.UNMODIFIED;
		
		if (refA && refB)
		{
			if (modTermA == null && modTermB == null)
				return CRN.UNMODIFIED;
			if (modTermA != null && modTermB != null && modTermA.resolvModifier ().equals (modTermB.resolvModifier ()))
					return CRN.UNMODIFIED;
			return CRN.MODIFIED;
		}
		if (refA)
			return CRN.DELETE;
		return CRN.INSERT;
	}

	/**
	 * Sets the single document flag.
	 */
	public void setSingleDocument ()
	{
		singleDoc = true;
	}
	
}
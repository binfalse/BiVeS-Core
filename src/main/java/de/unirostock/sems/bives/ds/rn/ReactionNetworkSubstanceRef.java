/**
 * 
 */
package de.unirostock.sems.bives.ds.rn;

import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.bives.ds.ontology.SBOTerm;
import de.unirostock.sems.bives.exception.BivesUnsupportedException;


/**
 * The Class ReactionNetworkSubstanceRef representing a reference to a {@link de.unirostock.sems.bives.ds.rn.ReactionNetworkSubstance}.
 *
 * @author Martin Scharm
 */
public class ReactionNetworkSubstanceRef
implements GraphEntity
{
	
	/** The substance. */
	public ReactionNetworkSubstance subst;
	
	/** The reference existent in versions A/B. */
	private boolean refA, refB;
	
	/** The optional modification terms in original doc. */
	public SBOTerm modTermA;
	
	/** The optional modification terms in modified doc. */
	public SBOTerm modTermB;
	
	/** The single doc flag if in single-doc-operation-mode. */
	protected boolean singleDoc;
	
	/** The xPath to the DocumentNode */
	protected String xPath;
	
	/**
	 * Instantiates a new substance reference.
	 *
	 * @param subst the substance
	 * @param flagA the existence flag for the original version
	 * @param flagB the existence flag for the modified version
	 * @param modTermA the modification type in the original version
	 * @param modTermB the modification type in the original version
	 * @throws BivesUnsupportedException 
	 */
	public ReactionNetworkSubstanceRef (ReactionNetworkSubstance subst, boolean flagA, boolean flagB, SBOTerm modTermA, SBOTerm modTermB, String xPath)
		throws BivesUnsupportedException
	{
		this.subst = subst;
		this.refA = flagA;
		this.refB = flagB;
		this.modTermA = modTermA;
		this.modTermB = modTermB;
		this.singleDoc = false;
		this.xPath = xPath;
		// modifier terms have to be the same. otherwise this edge differs -> create two edges!
		if (refA && refB && !SBOTerm.sameModifier (modTermA, modTermB))
			throw new BivesUnsupportedException ("modifiers differ");
	}
	
	
	/**
	 * Sets the SBOTerm as defined in the original version.
	 *
	 * @param modTermA the SBOTerm of version A
	 * @throws BivesUnsupportedException indicating that this edge has two different modification properties.
	 */
	public void setSboA (SBOTerm modTermA) throws BivesUnsupportedException
	{
		this.modTermA = modTermA;
		if (refA && refB && !SBOTerm.sameModifier (modTermA, modTermB))
			throw new BivesUnsupportedException ("modifiers differ");
	}
	
	
	/**
	 * Sets the SBOTerm as defined in the modified version.
	 *
	 * @param modTermB the SBOTerm of version B
	 * @throws BivesUnsupportedException indicating that this edge has two different modification properties.
	 */
	public void setSboB (SBOTerm modTermB) throws BivesUnsupportedException
	{
		this.modTermB = modTermB;
		if (refA && refB && !SBOTerm.sameModifier (modTermA, modTermB))
			throw new BivesUnsupportedException ("modifiers differ");
	}
	
	
	/**
	 * Gets the referenced substance.
	 *
	 * @return the substance
	 */
	public ReactionNetworkSubstance getSubstance ()
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
	 * Gets the modifier term.
	 *
	 * @return the modifier term
	 */
	public String getModTerm ()
	{
		if (modTermA == null && modTermB == null)
			return SBOTerm.MOD_UNKNOWN;
		if (modTermA == null)
			return modTermB.resolveModifier ();
		return modTermA.resolveModifier ();
	}
	
	/**
	 * Gets the modification.
	 *
	 * @return the modification
	 */
	public int getModification ()
	{
		if (singleDoc)
			return UNMODIFIED;
		
		if (refA && refB)
		{
			if (modTermA == null && modTermB == null)
				return UNMODIFIED;
			if (modTermA != null && modTermB != null && modTermA.resolveModifier ().equals (modTermB.resolveModifier ()))
					return UNMODIFIED;
			return MODIFIED;
		}
		if (refA)
			return DELETE;
		return INSERT;
	}
	
	/**
	 * Gets the modification.
	 *
	 * @return the modification
	 */
	public String getXPath() {
		return xPath;
	}

	/**
	 * Sets the single document flag.
	 */
	public void setSingleDocument ()
	{
		singleDoc = true;
	}
	
}
/**
 * 
 */
package de.unirostock.sems.bives.ds.crn;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.ds.ontology.SBOTerm;
import de.unirostock.sems.bives.exception.BivesUnsupportedException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;


// TODO: Auto-generated Javadoc
/**
 * The Class CRNReaction representing a reaction in a chemical reaction network.
 *
 * @author Martin Scharm
 */
public class CRNReaction
extends CRNEntity
{
	
	/** The reversible. */
	private boolean reversible;
	
	/** The compartment b. */
	private CRNCompartment compartmentA, compartmentB;

	/** The in. */
	private HashMap<CRNSubstance, CRNSubstanceRef> in;
	
	/** The out. */
	private HashMap<CRNSubstance, CRNSubstanceRef> out;
	
	/** The mod. */
	private Vector<CRNSubstanceRef> mod;
	//private HashMap<CRNSubstance, CRNSubstanceRef> mod;
	
	/**
	 * Instantiates a new cRN reaction.
	 *
	 * @param crn the chemical reaction network
	 * @param labelA the label of that reaction in the original document
	 * @param labelB the label of that reaction in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 * @param compartmentA the compartment a
	 * @param compartmentB the compartment b
	 * @param reversible the reversible flag
	 */
	public CRNReaction (CRN crn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, CRNCompartment compartmentA, CRNCompartment compartmentB, boolean reversible)
	{
		super ("r" + crn.getNextReactionID (), labelA, labelB, docA, docB);
		in = new HashMap<CRNSubstance, CRNSubstanceRef> ();
		out = new HashMap<CRNSubstance, CRNSubstanceRef> ();
		mod = new Vector<CRNSubstanceRef> ();
		this.compartmentA = compartmentA;
		this.compartmentB = compartmentB;
		singleDoc = false;
		this.reversible = reversible;
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
	 * <li>the compartment all substances involved in this reactions are from, if all substances originate from the same compartment</li>
	 * <li>null, otherwise</li>
	 * </ul>
	 *
	 * @return the compartment
	 */
	public CRNCompartment getCompartment ()
	{
		if (compartmentA != null && compartmentA == compartmentB)
				return compartmentA;
		
		boolean sameCompartment = true;
		CRNCompartment compartment = null;
		
		if (sameCompartment)
			for (CRNSubstance sub : in.keySet ())
			{
				if (compartment == null)
					compartment = sub.getCompartment ();
				else
				{
					if (compartment != sub.getCompartment ())
					{
						sameCompartment = false;
					}
				}
			}
		
		if (sameCompartment)
			for (CRNSubstance sub : out.keySet ())
			{
				if (compartment == null)
					compartment = sub.getCompartment ();
				else
				{
					if (compartment != sub.getCompartment ())
					{
						sameCompartment = false;
					}
				}
			}
		
		if (sameCompartment)
			for (CRNSubstanceRef sub : mod)
			{
				if (compartment == null)
					compartment = sub.subst.getCompartment ();
				else
				{
					if (compartment != sub.subst.getCompartment ())
					{
						sameCompartment = false;
					}
				}
			}
		
		if (sameCompartment)
			return compartment;
		return null;
	}
	
	
	/**
	 * Add a reactant of this reaction in its original version.
	 *
	 * @param subst the substance
	 * @param sbo the SBOTerm describing the interaction
	 */
	public void addInputA (CRNSubstance subst, SBOTerm sbo)
	{
		CRNSubstanceRef r = in.get (subst);
		if (r == null)
			try{
				in.put (subst, new CRNSubstanceRef (subst, true, false, sbo, null));
			}catch (BivesUnsupportedException e){}
		else
		{
			r.setFlagA (true);
		}
	}
	
	/**
	 * Add a product of this reaction in its original version.
	 *
	 * @param subst the substance
	 * @param sbo the SBOTerm describing the interaction
	 */
	public void addOutputA (CRNSubstance subst, SBOTerm sbo)
	{
		CRNSubstanceRef r = out.get (subst);
		if (r == null)
			try{
				out.put (subst, new CRNSubstanceRef (subst, true, false, sbo, null));
			}catch (BivesUnsupportedException e){}
		else
		{
			r.setFlagA (true);
		}
	}
	
	/**
	 * Adds the modifier of this reaction in its original version.
	 *
	 * @param subst the substance
	 * @param sbo the SBOTerm describing the modification
	 * @throws BivesUnsupportedException if one edges contains two types of modifications
	 */
	public void addModA (CRNSubstance subst, SBOTerm sbo) throws BivesUnsupportedException
	{
		for (CRNSubstanceRef sub : mod)
			if (sub.subst == subst)
			{
				if (SBOTerm.sameModifier (sub.modTermB, sbo))
				{
					sub.setFlagA (true);
					sub.modTermA = sbo;
					return;
				}
			}
		
		mod.add (new CRNSubstanceRef (subst, true, false, sbo, null));
	}
	
	/**
	 * Add a reactant of this reaction in its modified version.
	 *
	 * @param subst the substance
	 * @param sbo the SBOTerm describing the interaction
	 */
	public void addInputB (CRNSubstance subst, SBOTerm sbo)
	{
		CRNSubstanceRef r = in.get (subst);
		if (r == null)
			try{
				in.put (subst, new CRNSubstanceRef (subst, false, true, null, sbo));
			}catch (BivesUnsupportedException e){}
		else
		{
			r.setFlagB (true);
		}
	}
	
	/**
	 * Add a product of this reaction in its modified version.
	 *
	 * @param subst the substance
	 * @param sbo the SBOTerm describing the interaction
	 */
	public void addOutputB (CRNSubstance subst, SBOTerm sbo)
	{
		CRNSubstanceRef r = out.get (subst);
		if (r == null)
			try{
				out.put (subst, new CRNSubstanceRef (subst, false, true, null, sbo));
			}catch (BivesUnsupportedException e){}
		else
		{
			r.setFlagB (true);
		}
	}
	
	/**
	 * Add a modifier of this reaction in its modified version.
	 *
	 * @param subst the substance
	 * @param sbo the SBOTerm describing the modification
	 * @throws BivesUnsupportedException if one edges contains two types of modifications
	 */
	public void addModB (CRNSubstance subst, SBOTerm sbo) throws BivesUnsupportedException
	{
		for (CRNSubstanceRef sub : mod)
			if (sub.subst == subst)
			{
				if (SBOTerm.sameModifier (sub.modTermA, sbo))
				{
					sub.setFlagB (true);
					sub.modTermB = sbo;
					return;
				}
			}
		
		mod.add (new CRNSubstanceRef (subst, false, true, null, sbo));
	}
	
	/**
	 * Gets the reactants.
	 *
	 * @return the reactants
	 */
	public Collection<CRNSubstanceRef> getInputs ()
	{
		return in.values ();
	}
	
	/**
	 * Gets the products.
	 *
	 * @return the products
	 */
	public Collection<CRNSubstanceRef> getOutputs ()
	{
		return out.values ();
	}
	
	/**
	 * Gets the modifiers.
	 *
	 * @return the modifiers
	 */
	public Collection<CRNSubstanceRef> getModifiers ()
	{
		return mod;
	}
	
	/**
	 * Gets the sbo.
	 *
	 * @return the sbo
	 */
	public String getSBO ()
	{
		String a = docA.getAttributeValue ("sboTerm");
		String b = docA.getAttributeValue ("sboTerm");
		if (a == null || b == null || !a.equals (b))
			return "";
		return a;
	}
	
	/**
	 * Checks if is reversible.
	 *
	 * @return true, if is reversible
	 */
	public boolean isReversible ()
	{
		return reversible;
	}

	/**
	 * Sets the single document flag.
	 */
	public void setSingleDocument ()
	{
		singleDoc = true;
		
		for (CRNSubstanceRef subst : in.values ())
			subst.setSingleDocument ();
		for (CRNSubstanceRef subst : out.values ())
			subst.setSingleDocument ();
		for (CRNSubstanceRef subst : mod)
			subst.setSingleDocument ();
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.ds.crn.CRNEntity#getModification()
	 */
	public int getModification ()
	{
		int i = super.getModification ();
		if (i != UNMODIFIED)
			return i;
		if (changes (in.values ()) || changes (out.values ()) || changes (mod))
			return MODIFIED;
		return UNMODIFIED;
	}
	
	/**
	 * Are there changes in a list of IO?
	 *
	 * @param substances the substances
	 * @return true, if something in this list has changed
	 */
	private boolean changes (Collection<CRNSubstanceRef> substances)
	{
		for (CRNSubstanceRef substance : substances)
			if (substance.getModification () != UNMODIFIED)
				return true;
		return false;
	}
}

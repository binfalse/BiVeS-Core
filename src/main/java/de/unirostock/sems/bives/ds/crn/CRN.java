/**
 * 
 */
package de.unirostock.sems.bives.ds.crn;

import java.util.Collection;
import java.util.HashMap;

import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class CRN representing a chemical reaction network.
 * 
 * <p>
 * A CRN contains compartments, which may contain species and reactions.
 * There may be directed connections between species and reactions representing relationships like reactants, products, modifiers etc.
 * <br>
 * see also <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/ChemicalReactionNetwork">ChemicalReactionNetwork</a>
 * </p>
 * 
 *
 * @author Martin Scharm
 */
public class CRN
{
	
	/** The reaction id: incremented with every reation to assign ids to reactions. */
	private int reactionID;
	
	/** The substance id: incremented with every substance to assign ids to substance. */
	private int substanceID;
	
	/** The compartment id: incremented with every compartment to assign ids to compartments. */
	private int compartmentID;
	
	/** map for reactions. */
	private HashMap<TreeNode, CRNReaction> crnR;
	
	/** map for substances. */
	private HashMap<TreeNode, CRNSubstance> crnS;
	
	/** map for compartments. */
	private HashMap<TreeNode, CRNCompartment> crnC;
	
	/**
	 * Instantiates a new chemical reaction network.
	 */
	public CRN ()
	{
		reactionID = 0;
		substanceID = 0;
		compartmentID = 0;
		crnR = new HashMap<TreeNode, CRNReaction> ();
		crnS = new HashMap<TreeNode, CRNSubstance> ();
		crnC = new HashMap<TreeNode, CRNCompartment> ();
	}
	
	/**
	 * Gets the substances.
	 *
	 * @return the substances
	 */
	public Collection<CRNSubstance> getSubstances ()
	{
		return crnS.values ();
	}
	
	/**
	 * Gets the reactions.
	 *
	 * @return the reactions
	 */
	public Collection<CRNReaction> getReactions ()
	{
		return crnR.values ();
	}
	
	/**
	 * Gets the compartments.
	 *
	 * @return the compartments
	 */
	public Collection<CRNCompartment> getCompartments ()
	{
		return crnC.values ();
	}
	
	/**
	 * Gets the next substance id.
	 *
	 * @return the next substance id
	 */
	public int getNextSubstanceID ()
	{
		return ++substanceID;
	}
	
	/**
	 * Gets the next compartment id.
	 *
	 * @return the next compartment id
	 */
	public int getNextCompartmentID ()
	{
		return ++compartmentID;
	}
	
	/**
	 * Gets the next reaction id.
	 *
	 * @return the next reaction id
	 */
	public int getNextReactionID ()
	{
		return ++reactionID;
	}
	

	
	/**
	 * Adds a reaction.
	 *
	 * @param node the node
	 * @param react the react
	 */
	public void setReaction (TreeNode node, CRNReaction react)
	{
		crnR.put (node, react);
	}
	
	/**
	 * Adds a substance.
	 *
	 * @param node the node
	 * @param subst the subst
	 */
	public void setSubstance (TreeNode node, CRNSubstance subst)
	{
		crnS.put (node, subst);
	}
	
	/**
	 * Adds a compartment.
	 *
	 * @param node the node
	 * @param compartment the compartment
	 */
	public void setCompartment (TreeNode node, CRNCompartment compartment)
	{
		crnC.put (node, compartment);
	}
	
	/**
	 * Gets a substance.
	 *
	 * @param node the node
	 * @return the substance
	 */
	public CRNSubstance getSubstance (TreeNode node)
	{
		return crnS.get (node);
	}
	
	/**
	 * Gets a reaction.
	 *
	 * @param node the node
	 * @return the reaction
	 */
	public CRNReaction getReaction (TreeNode node)
	{
		return crnR.get (node);
	}
	
	/**
	 * Gets a compartment.
	 *
	 * @param node the node
	 * @return the compartment
	 */
	public CRNCompartment getCompartment (TreeNode node)
	{
		return crnC.get (node);
	}

	/**
	 * Sets the single document flag for non-comparison graphs.
	 */
	public void setSingleDocument ()
	{
		for (CRNReaction r : crnR.values ())
			r.setSingleDocument ();
		for (CRNSubstance s : crnS.values ())
			s.setSingleDocument ();
		for (CRNCompartment c : crnC.values ())
			c.setSingleDocument ();
	}
	
	/**
	 * Convert a modification to a string representation.
	 *
	 * @param modification the modification
	 * @return the textual representation
	 */
	public static String modToString (int modification)
	{
		switch (modification)
		{
			case GraphEntity.INSERT:
				return "inserted";
			case GraphEntity.DELETE:
				return "deleted";
			case GraphEntity.MODIFIED:
				return "modified";
		}
		return "unmodified";
	}
}

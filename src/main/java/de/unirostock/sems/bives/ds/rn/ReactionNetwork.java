/**
 * 
 */
package de.unirostock.sems.bives.ds.rn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class ReactionNetwork representing a reaction network.
 * 
 * <p>
 * A ReactionNetwork contains compartments, which may contain species and reactions.
 * There may be directed connections between species and reactions representing relationships like reactants, products, modifiers etc.
 * <br>
 * see also <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/ReactionNetwork">ReactionNetwork</a>
 * </p>
 * 
 *
 * @author Martin Scharm
 */
public class ReactionNetwork
{
	
	/** The reaction id: incremented with every reation to assign ids to reactions. */
	private int reactionID;
	
	/** The substance id: incremented with every substance to assign ids to substance. */
	private int substanceID;
	
	/** The compartment id: incremented with every compartment to assign ids to compartments. */
	private int compartmentID;
	
	/** map for reactions. */
	private HashMap<TreeNode, ReactionNetworkReaction> rnR;
	
	/** map for substances. */
	private HashMap<TreeNode, ReactionNetworkSubstance> rnS;
	
	/** map for compartments. */
	private HashMap<TreeNode, ReactionNetworkCompartment> rnC;
	
	/**
	 * Instantiates a new reaction network.
	 */
	public ReactionNetwork ()
	{
		reactionID = 0;
		substanceID = 0;
		compartmentID = 0;
		rnR = new HashMap<TreeNode, ReactionNetworkReaction> ();
		rnS = new HashMap<TreeNode, ReactionNetworkSubstance> ();
		rnC = new HashMap<TreeNode, ReactionNetworkCompartment> ();
	}
	
	/**
	 * Gets the substances.
	 *
	 * @return the substances
	 */
	public Collection<ReactionNetworkSubstance> getSubstances ()
	{
		Set<ReactionNetworkSubstance> s = new HashSet<ReactionNetworkSubstance> ();
		s.addAll (rnS.values ());
		return s;
	}
	
	/**
	 * Gets the reactions.
	 *
	 * @return the reactions
	 */
	public Collection<ReactionNetworkReaction> getReactions ()
	{
		Set<ReactionNetworkReaction> s = new HashSet<ReactionNetworkReaction> ();
		s.addAll (rnR.values ());
		return s;
	}
	
	/**
	 * Gets the compartments.
	 *
	 * @return the compartments
	 */
	public Collection<ReactionNetworkCompartment> getCompartments ()
	{
		Set<ReactionNetworkCompartment> s = new HashSet<ReactionNetworkCompartment> ();
		s.addAll (rnC.values ());
		return s;
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
	public void setReaction (TreeNode node, ReactionNetworkReaction react)
	{
		rnR.put (node, react);
	}
	
	/**
	 * Adds a substance.
	 *
	 * @param node the node
	 * @param subst the subst
	 */
	public void setSubstance (TreeNode node, ReactionNetworkSubstance subst)
	{
		rnS.put (node, subst);
	}
	
	/**
	 * Adds a compartment.
	 *
	 * @param node the node
	 * @param compartment the compartment
	 */
	public void setCompartment (TreeNode node, ReactionNetworkCompartment compartment)
	{
		rnC.put (node, compartment);
	}
	
	/**
	 * Gets a substance.
	 *
	 * @param node the node
	 * @return the substance
	 */
	public ReactionNetworkSubstance getSubstance (TreeNode node)
	{
		return rnS.get (node);
	}
	
	/**
	 * Gets a reaction.
	 *
	 * @param node the node
	 * @return the reaction
	 */
	public ReactionNetworkReaction getReaction (TreeNode node)
	{
		return rnR.get (node);
	}
	
	/**
	 * Gets a compartment.
	 *
	 * @param node the node
	 * @return the compartment
	 */
	public ReactionNetworkCompartment getCompartment (TreeNode node)
	{
		return rnC.get (node);
	}

	/**
	 * Sets the single document flag for non-comparison graphs.
	 */
	public void setSingleDocument ()
	{
		for (ReactionNetworkReaction r : rnR.values ())
			r.setSingleDocument ();
		for (ReactionNetworkSubstance s : rnS.values ())
			s.setSingleDocument ();
		for (ReactionNetworkCompartment c : rnC.values ())
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

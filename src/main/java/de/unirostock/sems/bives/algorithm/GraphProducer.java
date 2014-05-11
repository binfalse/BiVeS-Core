/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.bives.ds.rn.ReactionNetwork;


/**
 * The Class GraphProducer to produce graphs from models.
 *
 * @author Martin Scharm
 */
public abstract class GraphProducer
{
	
	/** The single flag. */
	protected boolean single;
	
	/** The reaction network. */
	protected ReactionNetwork rn;
	
	/** The hierarchy network. */
	protected HierarchyNetwork hn;

	/** The flag for a produced reaction network. */
	private boolean producedRn;
	
	/** The flag for a produced hierarchy network. */
	private boolean producedHn;

	/**
	 * Instantiates a new graph producer.
	 *
	 * @param single the single flag
	 */
	public GraphProducer (boolean single)
	{
		this.single = single;

		producedRn = false;
		producedHn = false;
	}
	
	/**
	 * Gets the chemical reaction network graph. Calling it for the first time takes a bit longer to compute the network ;-)
	 * Might return null if not available.
	 *
	 * @return the produced chemical reaction network
	 * @deprecated As of 1.3.3 replaced by {@link #getReactionNetwork()}
	 */
	@Deprecated
	public ReactionNetwork getCRN ()
	{
		return getReactionNetwork ();
	}
	
	/**
	 * Gets the reaction network graph. Calling it for the first time takes a bit longer to compute the network ;-)
	 * Might return null if not available.
	 *
	 * @return the produced reaction network
	 */
	public ReactionNetwork getReactionNetwork ()
	{
		if (!producedRn)
		{
			rn = new ReactionNetwork ();
			produceReactionNetwork ();
			producedRn = true;
		}
		return rn;
	}
	
	/**
	 * Gets the hierarchy network graph. Calling it for the first time takes a bit longer to compute the network ;-)
	 * Might return null if not available.
	 *
	 * @return the hierarchy network
	 */
	public HierarchyNetwork getHierarchy ()
	{
		if (!producedHn)
		{
			hn = new HierarchyNetwork ();
			produceHierarchyGraph ();
			producedHn = true;
		}
		return hn;
	}
	
	/**
	 * Produce the reaction network.
	 */
	protected abstract void produceReactionNetwork ();
	
	/**
	 * Produce hierarchy graph.
	 */
	protected abstract void produceHierarchyGraph ();
}

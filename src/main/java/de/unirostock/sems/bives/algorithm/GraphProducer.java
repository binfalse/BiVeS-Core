/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;


/**
 * The Class GraphProducer to produce graphs from models.
 *
 * @author Martin Scharm
 */
public abstract class GraphProducer
{
	
	/** The single flag. */
	protected boolean single;
	
	/** The chemical reaction network. */
	protected CRN crn;
	
	/** The hierarchy network. */
	protected HierarchyNetwork hn;

	/** The flag for a produced chemical reaction network. */
	private boolean producedCrn;
	
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

		producedCrn = false;
		producedHn = false;
	}
	
	/**
	 * Gets the chemical reaction network graph. Calling it for the first time takes a bit longer to compute the network ;-)
	 * Might return null if not available.
	 *
	 * @return the produced chemical reaction network
	 */
	public CRN getCRN ()
	{
		if (!producedCrn)
		{
			crn = new CRN ();
			produceCRN ();
			producedCrn = true;
		}
		return crn;
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
	 * Produce the chemical reaction network.
	 */
	protected abstract void produceCRN ();
	
	/**
	 * Produce hierarchy graph.
	 */
	protected abstract void produceHierarchyGraph ();
}

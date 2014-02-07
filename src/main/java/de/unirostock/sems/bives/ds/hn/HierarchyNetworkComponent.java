/**
 * 
 */
package de.unirostock.sems.bives.ds.hn;


import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class HierarchyNetworkComponent representing a component of a HierarchyNetwork.
 *
 * @author Martin Scharm
 */
public class HierarchyNetworkComponent
extends HierarchyNetworkEntity
{
	
	/** The parent components as defined in the original/modified version. */
	private HierarchyNetworkComponent parentA, parentB;
	
	/** The sub-components as defined in the original/modified version. */
	private List<HierarchyNetworkComponent> kidsA, kidsB;
	
	/** The list of variables in this component. */
	private List<HierarchyNetworkVariable> vars;

	/**
	 * Instantiates a new hierarchy network component.
	 *
	 * @param hn the hierarchy network
	 * @param labelA the label as defined in the original document
	 * @param labelB the label as defined in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public HierarchyNetworkComponent (HierarchyNetwork hn, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		super ("c" + hn.getNextComponentID(), labelA, labelB, docA, docB);
		kidsA = new ArrayList<HierarchyNetworkComponent> ();
		kidsB = new ArrayList<HierarchyNetworkComponent> ();
		vars = new ArrayList<HierarchyNetworkVariable> ();
	}
	
	/**
	 * Gets the variables hosted in this component.
	 *
	 * @return the variables
	 */
	public List<HierarchyNetworkVariable> getVariables ()
	{
		return vars;
	}
	
	/**
	 * Adds a variable.
	 *
	 * @param var the variable 
	 */
	public void addVariable (HierarchyNetworkVariable var)
	{
		if (vars.contains (var))
			return;
		vars.add (var);
	}
	
	/**
	 * Adds a sub-component (below in hierarchy) as it is defined in the original version.
	 *
	 * @param component the sub-component
	 */
	public void addChildA (HierarchyNetworkComponent component)
	{
		this.kidsA.add (component);
	}
	
	/**
	 * Adds a sub-component (below in hierarchy) as it is defined in the modified version.
	 *
	 * @param component the sub-component
	 */
	public void addChildB (HierarchyNetworkComponent component)
	{
		this.kidsB.add (component);
	}
	
	/**
	 * Sets the parent component (above in hierarchy) as it is defined in the original version.
	 *
	 * @param component the new parent component
	 */
	public void setParentA (HierarchyNetworkComponent component)
	{
		this.parentA = component;
	}
	
	/**
	 * Sets the parent component (above in hierarchy) as it is defined in the modified version.
	 *
	 * @param component the new parent component
	 */
	public void setParentB (HierarchyNetworkComponent component)
	{
		this.parentB = component;
	}
	
	/**
	 * Gets the parent component as it is defined in the original version.
	 *
	 * @return the parent component
	 */
	public HierarchyNetworkComponent getParentA ()
	{
		return parentA;
	}
	
	/**
	 * Gets the parent component as it is defined in the modified version.
	 *
	 * @return the parent component
	 */
	public HierarchyNetworkComponent getParentB ()
	{
		return parentB;
	}
	
}

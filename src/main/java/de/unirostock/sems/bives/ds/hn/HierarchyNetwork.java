/**
 * 
 */
package de.unirostock.sems.bives.ds.hn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class HierarchyNetwork representing a graphical hierarchy network, intended to visualize CellML hierarchical dependencies.
 * 
 * <p>
 * A HierarchyNetwork contains components which may contain variables. Moreover, there may be directed connections from parent components to
 * child components representing the hierarchy among components.
 * In addition, variables can be connected visualizing the flow of information between them.
 * <br>
 * see also <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/HierarchyNetwork">HierarchyNetwork</a>
 * </p>
 * 
 * 
 *
 * @author Martin Scharm
 */
public class HierarchyNetwork
{
	/** The latest component id. */
	private int componentID;
	
	/** The latest variable id. */
	private int variableID;
	
	/** The component mapper. */
	private HashMap<TreeNode, HierarchyNetworkComponent> hnC;
	
	/** The variable mapper. */
	private HashMap<TreeNode, HierarchyNetworkVariable> hnV;
	
	/**
	 * Instantiates a new hierarchy network.
	 */
	public HierarchyNetwork ()
	{
		componentID = 0;
		variableID = 0;
		hnC = new HashMap<TreeNode, HierarchyNetworkComponent> ();
		hnV = new HashMap<TreeNode, HierarchyNetworkVariable> ();
	}
	
	/**
	 * Gets the components.
	 *
	 * @return the components
	 */
	public Collection<HierarchyNetworkComponent> getComponents ()
	{
		Set<HierarchyNetworkComponent> s = new HashSet<HierarchyNetworkComponent> ();
		s.addAll (hnC.values ());
		return s;
	}
	
	/**
	 * Gets the variables.
	 *
	 * @return the variables
	 */
	public Collection<HierarchyNetworkVariable> getVariables ()
	{
		Set<HierarchyNetworkVariable> s = new HashSet<HierarchyNetworkVariable> ();
		s.addAll (hnV.values ());
		return s;
	}
	
	/**
	 * Gets the next component id.
	 *
	 * @return the next component id
	 */
	public int getNextComponentID ()
	{
		return ++componentID;
	}
	
	/**
	 * Gets the next variable id.
	 *
	 * @return the next variable id
	 */
	public int getNextVariableID ()
	{
		return ++variableID;
	}
	

	
	/**
	 * Adds a new component to the hierarchy.
	 *
	 * @param node the node in the document tree
	 * @param comp the network component
	 */
	public void setComponent (TreeNode node, HierarchyNetworkComponent comp)
	{
		hnC.put (node, comp);
	}
	
	/**
	 * Adds a new variable.
	 *
	 * @param node the node in the document tree
	 * @param var the variable
	 */
	public void setVariable (TreeNode node, HierarchyNetworkVariable var)
	{
		hnV.put (node, var);
	}
	
	/**
	 * Gets a component.
	 *
	 * @param node the node from the document tree
	 * @return the corresponding component
	 */
	public HierarchyNetworkComponent getComponent (TreeNode node)
	{
		return hnC.get (node);
	}
	
	/**
	 * Gets a variable.
	 *
	 * @param node the node from the document tree
	 * @return the corresponding variable
	 */
	public HierarchyNetworkVariable getVariable (TreeNode node)
	{
		return hnV.get (node);
	}

	/**
	 * Sets the single document flag for non-comparison graphs.
	 */
	public void setSingleDocument ()
	{
		for (HierarchyNetworkComponent c : hnC.values ())
			c.setSingleDocument ();
		for (HierarchyNetworkVariable v : hnV.values ())
			v.setSingleDocument ();
	}
	
	
}

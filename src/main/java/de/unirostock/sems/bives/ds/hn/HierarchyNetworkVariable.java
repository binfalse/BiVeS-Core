/**
 * 
 */
package de.unirostock.sems.bives.ds.hn;

import java.util.HashMap;

import de.unirostock.sems.xmlutils.ds.DocumentNode;


/**
 * The Class HierarchyNetworkVariable representing a variable of a HierarchyNetwork.
 *
 * @author Martin Scharm
 */
public class HierarchyNetworkVariable
extends HierarchyNetworkEntity
{
	
	/** The components containing this variable, as defined in the original and modified version, respectively. */
	private HierarchyNetworkComponent componentA, componentB;
	
	/** The connections to other variables. */
	private HashMap<HierarchyNetworkVariable, VarConnection> connections;
	
	/**
	 * The Class VarConnection representing a connection of two variables.
	 */
	public class VarConnection
	{
		
		/** The existence flags for original/modified document. */
		public boolean a, b;
		
		/**
		 * Instantiates a new connection to another variable.
		 *
		 * @param a if true, existent in original document
		 * @param b if true, existent in modified document
		 */
		public VarConnection (boolean a, boolean b)
		{
			this.a = a;
			this.b = b;
		}
		
		/**
		 * Gets the modification.
		 *
		 * @return the modification
		 */
		public int getModification ()
		{
			return (a?b?UNMODIFIED:DELETE:b?INSERT:UNMODIFIED);
		}
	}

	/**
	 * Instantiates a new hierarchy network variable.
	 *
	 * @param hn the hierarchy network
	 * @param labelA the label as defined in the original document
	 * @param labelB the label as defined in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 * @param componentA the component in the original document
	 * @param componentB the component in the modified document
	 */
	public HierarchyNetworkVariable (HierarchyNetwork hn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, HierarchyNetworkComponent componentA, HierarchyNetworkComponent componentB)
	{
		super ("v" + hn.getNextVariableID(), labelA, labelB, docA, docB);
		this.componentA = componentA;
		this.componentB = componentB;
		connections = new HashMap<HierarchyNetworkVariable, VarConnection> ();
	}
	
	/**
	 * Gets the connections of this variable to other variables of the hierarchy network.
	 *
	 * @return the connections
	 */
	public HashMap<HierarchyNetworkVariable, VarConnection> getConnections ()
	{
		return connections;
	}
	
	/**
	 * Adds a connection as defined in the original document.
	 *
	 * @param var the variable to connect
	 */
	public void addConnectionA (HierarchyNetworkVariable var)
	{
		VarConnection v = connections.get (var);
		if (v == null)
			connections.put (var, new VarConnection (true, false));
		else
			v.a = true;
	}
	
	/**
	 * Adds a connection as defined in the modified document.
	 *
	 * @param var the variable to connect
	 */
	public void addConnectionB (HierarchyNetworkVariable var)
	{
		VarConnection v = connections.get (var);
		if (v == null)
			connections.put (var, new VarConnection (false, true));
		else
			v.b = true;
	}
	
	/**
	 * Sets the component hosting this variable as defined in the original document.
	 *
	 * @param component the host in the original version
	 */
	public void setComponentA (HierarchyNetworkComponent component)
	{
		this.componentA = component;
	}
	
	/**
	 * Sets the component hosting this variable as defined in the modified document.
	 *
	 * @param component the host in the modified version
	 */
	public void setComponentB (HierarchyNetworkComponent component)
	{
		this.componentB = component;
	}
	
	/**
	 * Gets the component. Will return:
	 * <ul>
	 * <li>the original component, if it's the same component as in the modified version</li>
	 * <li>null, otherwise</li>
	 * </ul>
	 *
	 * @return the component
	 */
	public HierarchyNetworkComponent getComponent ()
	{
		if (componentA == componentB)
			return componentA;
		return null;
	}
	
	/**
	 * Gets the modification of this entity.
	 *
	 * @return the modification
	 */
	public int getModification ()
	{
		int i = super.getModification ();
		if (i != UNMODIFIED)
			return i;
		
		if (componentA != componentB)
			return MODIFIED;
		return UNMODIFIED;
	}
	
}

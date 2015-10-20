/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import de.unirostock.sems.xmlutils.comparison.Connection;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class NodeConnection implements  Connection
{
	
	/** The first node in this connection. */
	private TreeNode a;
	
	/** The second node in this connection. */
	private TreeNode b;
	
	/** The weight of this connection. */
	private double weight;
	
	/** The annotations - a list of predicates and objects describing this connection. */
	private List<Map.Entry<Property, RDFNode>> annotations;
	
	/**
	 * Instantiates a new connection, implicitly setting the weight to 1.
	 *
	 * @param a the node of tree 1
	 * @param b the node of tree 2
	 */
	public NodeConnection (TreeNode a, TreeNode b)
	{
		this.a = a;
		this.b = b;
		weight = 1;
		annotations = new ArrayList<Map.Entry<Property, RDFNode>> ();
	}
	
	
	/**
	 * Instantiates a new connection as a copy of toCopy.
	 *
	 * @param toCopy the connection to copy
	 */
	public NodeConnection (NodeConnection toCopy)
	{
		this.a = toCopy.a;
		this.b = toCopy.b;
		weight = toCopy.weight;
		annotations = new ArrayList<Map.Entry<Property, RDFNode>> ();
	}
	
	/**
	 * Instantiates a new connection, explicitly defining a weight.
	 *
	 * @param a the node of tree 1
	 * @param b the node of tree 2
	 * @param weight the weight of this connection
	 */
	public NodeConnection (TreeNode a, TreeNode b, double weight)
	{
		this.a = a;
		this.b = b;
		this.weight = weight;
		annotations = new ArrayList<Map.Entry<Property, RDFNode>> ();
	}
	
	/**
	 * Gets the annotations of this mapping. The annotation list is a list of predicates and objects.
	 *
	 * @return the annotations
	 */
	public List<Map.Entry<Property, RDFNode>> getAnnotations ()
	{
		return annotations;
	}
	
	/**
	 * Adds an annotation describing this node connection.
	 *
	 * @param predicate the predicate that relates to an object
	 * @param object the actual object describing this element
	 */
	public void addAnnotation (Property predicate, RDFNode object)
	{
		annotations.add (new AbstractMap.SimpleEntry<>(predicate, object));
	}
	
	/**
	 * Gets the corresponding node in tree a.
	 *
	 * @return the node in tree a
	 */
	public TreeNode getTreeA ()
	{
		return a;
	}
	
	/**
	 * Gets the corresponding node in tree b.
	 *
	 * @return the node in tree b
	 */
	public TreeNode getTreeB ()
	{
		return b;
	}
	
	public void setWeight (double u)
	{
		weight = u;
	}
	
	public void addWeight (double u)
	{
		weight += u;
	}
	
	public void scaleWeight (double u)
	{
		weight *= u;
	}
	
	public double getWeight ()
	{
		return weight;
	}
	
	public String toString ()
	{
		return "[" + a.getXPath () + " => " + b.getXPath () + " (" + weight + ")]";
	}
	
	public TreeNode getPartnerOf (TreeNode node)
	{
		if (a == node)
			return b;
		if (b == node)
			return a;
		return null;
	}
}

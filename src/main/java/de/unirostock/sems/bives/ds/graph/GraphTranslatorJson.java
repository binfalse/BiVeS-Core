/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.crn.CRNCompartment;
import de.unirostock.sems.bives.ds.crn.CRNReaction;
import de.unirostock.sems.bives.ds.crn.CRNSubstance;
import de.unirostock.sems.bives.ds.crn.CRNSubstanceRef;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkComponent;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkVariable;
import de.unirostock.sems.bives.ds.ontology.SBOTerm;



/**
 * The Class GraphTranslatorJson to translate internal graph structures into
 * JSON format.
 * (e.g. to pass the graph to CytoscapeJS)
 * 
 * The resulting graph will look like:
 * 
 * <pre>
 * {
 * 	"elements": {
 * 		"edges": [
 * 			{
 * 				"classes": "bives-ioedge",
 * 				"data": {
 * 					"source": "s2",
 * 					"target": "r3"
 * 				}
 * 			},
 * 			{
 * 				"classes": "bives-stimulator bives-deleted",
 * 				"data": {
 * 					"source": "s4",
 * 					"target": "r3"
 * 				}
 * 			},
 * 			[...]
 * 		],
 * 		"nodes": [
 * 			{
 * 				"classes": "compartment",
 * 				"data": {
 * 					"id": "c1",
 * 					"name": "compartment"
 * 				}
 * 			},
 * 			{
 * 				"classes": "species",
 * 				"data": {
 * 					"id": "s2",
 * 					"name": "sigb",
 * 					"parent": "c1"
 * 				}
 * 			},
 * 			{
 * 				"classes": "reaction bives-modified",
 * 				"data": {
 * 					"id": "r3",
 * 					"name": "sigb degr",
 * 					"parent": "c1"
 * 				}
 * 			},
 * 			[...]
 * 		]
 * 	}
 * }
 * </pre>
 * 
 * for more information see <a href=
 * "https://sems.uni-rostock.de/trac/bives-core/wiki/JsonGraphFormatDescription"
 * >JsonGraphFormatDescription</a>
 * 
 * @author Martin Scharm
 */
public class GraphTranslatorJson
	extends GraphTranslator
{
	
	/** The nodes. */
	private JSONArray		nodes;
	
	/** The edges. */
	private JSONArray		edges;
	
	/** The graph. */
	private JSONObject	graph;
	
	
	/**
	 * Adds a node to the JSON graph.
	 * 
	 * @param parent
	 *          the id of the parent node
	 * @param id
	 *          the id of the new node
	 * @param name
	 *          the name of the new node
	 * @param version
	 *          the version flag: [-1,0,1,2]
	 * @param species
	 *          if true : species; otherwise : reaction
	 */
	@SuppressWarnings("unchecked")
	private void addNode (String parent, String id, String name, int version,
		boolean species)
	{
		JSONObject node = new JSONObject ();
		
		String classes = species ? "species" : "reaction";
		switch (version)
		{
			case 1:
				classes += " bives-inserted";
				break;
			case 2:
				classes += " bives-modified";
				break;
			case -1:
				classes += " bives-deleted";
				break;
		}
		node.put ("classes", classes);
		
		JSONObject data = new JSONObject ();
		data.put ("id", id);
		data.put ("name", name);
		if (parent != null)
			data.put ("parent", parent);
		
		node.put ("data", data);
		
		nodes.add (node);
	}
	
	
	/**
	 * Adds an edge from <code>from</code> to <code>to</code> to the JSON graph.
	 * 
	 * @param from
	 *          the id of the source node
	 * @param to
	 *          the id of the target node
	 * @param version
	 *          the version flag: [-1,0,1,2]
	 * @param modification
	 *          the modification property (SBOTerm.*)
	 */
	@SuppressWarnings("unchecked")
	private void addEdge (String from, String to, int version, String modification)
	{
		
		JSONObject edge = new JSONObject ();
		
		String classes = "";
		
		if (modification != null)
		{
			if (modification.equals (SBOTerm.MOD_INHIBITOR))
				classes = "bives-inhibitor";
			else if (modification.equals (SBOTerm.MOD_STIMULATOR))
				classes = "bives-stimulator";
			else if (modification.equals (SBOTerm.MOD_UNKNOWN))
				classes = "bives-unkwnmod";
			else
				classes = "bives-ioedge";
		}
		else
			classes = "bives-ioedge";
		
		switch (version)
		{
			case 1:
				classes += " bives-inserted";
				break;
			case 2:
				classes += " bives-modified";
				break;
			case -1:
				classes += " bives-deleted";
				break;
		}
		edge.put ("classes", classes);
		
		JSONObject data = new JSONObject ();
		data.put ("source", from);
		data.put ("target", to);
		
		edge.put ("data", data);
		
		edges.add (edge);
	}
	
	
	/**
	 * Creates a compartment.
	 *
	 * @param id the id of that compartment
	 * @param name the name of the compartment
	 * @param version the version flag: [-1,0,1,2]
	 */
	@SuppressWarnings("unchecked")
	private void createCompartment (String id, String name, int version)
	{
		JSONObject node = new JSONObject ();
		
		String classes = "compartment";
		switch (version)
		{
			case 1:
				classes += " bives-inserted";
				break;
			case 2:
				classes += " bives-modified";
				break;
			case -1:
				classes += " bives-deleted";
				break;
		}
		node.put ("classes", classes);
		
		JSONObject data = new JSONObject ();
		data.put ("id", id);
		data.put ("name", name);
		
		node.put ("data", data);
		
		nodes.add (node);
	}
	
	
	/**
	 * Start a new graph. Initializes the resources.
	 */
	@SuppressWarnings("unchecked")
	private void startNewGraph ()
	{
		graph = new JSONObject ();
		nodes = new JSONArray ();
		edges = new JSONArray ();
		
		JSONObject elements = new JSONObject ();
		elements.put ("nodes", nodes);
		elements.put ("edges", edges);
		
		graph.put ("elements", elements);
	}
	
	
	/**
	 * Gets the JSON object.
	 * 
	 * @return the JSON object representing the graph
	 */
	public JSONObject getJsonGraph ()
	{
		return graph;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock
	 * .sems.bives.ds.graph.CRN)
	 */
	@Override
	public String translate (CRN crn)
	{
		startNewGraph ();
		for (CRNCompartment c : crn.getCompartments ())
		{
			createCompartment (c.getId (), c.getLabel (), c.getModification ());
		}
		
		for (CRNSubstance s : crn.getSubstances ())
		{
			CRNCompartment compartment = s.getCompartment ();
			if (compartment != null)
				addNode (compartment.getId (), s.getId (), s.getLabel (),
					s.getModification (), true);
			else
				addNode (null, s.getId (), s.getLabel (), s.getModification (), true);
		}
		
		for (CRNReaction r : crn.getReactions ())
		{
			CRNCompartment compartment = r.getCompartment ();
			if (compartment != null)
				addNode (compartment.getId (), r.getId (), r.getLabel (),
					r.getModification (), false);
			else
				addNode (null, r.getId (), r.getLabel (), r.getModification (), false);
			
			for (CRNSubstanceRef s : r.getInputs ())
				addEdge (s.getSubstance ().getId (), r.getId (), s.getModification (),
					SBOTerm.MOD_NONE);
			
			for (CRNSubstanceRef s : r.getOutputs ())
				addEdge (r.getId (), s.getSubstance ().getId (), s.getModification (),
					SBOTerm.MOD_NONE);
			
			for (CRNSubstanceRef s : r.getModifiers ())
			{
				if (s.getModification () == CRN.MODIFIED)
				{
					addEdge (s.getSubstance ().getId (), r.getId (), CRN.DELETE,
						s.getModTermA ());
					addEdge (s.getSubstance ().getId (), r.getId (), CRN.INSERT,
						s.getModTermB ());
				}
				else
					addEdge (s.getSubstance ().getId (), r.getId (),
						s.getModification (), s.getModTerm ());
			}
		}
		
		return graph.toJSONString ();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock
	 * .sems.bives.ds.hn.HierarchyNetwork)
	 */
	@Override
	public String translate (HierarchyNetwork hn)
	{
		startNewGraph ();
		
		Collection<HierarchyNetworkComponent> components = hn.getComponents ();
		for (HierarchyNetworkComponent c : components)
		{
			createCompartment (c.getId (), c.getLabel (), c.getModification ());
			
			List<HierarchyNetworkVariable> vars = c.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				addNode (c.getId (), var.getId (), var.getLabel (),
					var.getModification (), false);
				
				HashMap<HierarchyNetworkVariable, HierarchyNetworkVariable.VarConnection> cons = var
					.getConnections ();
				
				for (HierarchyNetworkVariable con : cons.keySet ())
				{
					addEdge (con.getId (), var.getId (), cons.get (con)
						.getModification (), SBOTerm.MOD_NONE);
				}
			}
			
			HierarchyNetworkComponent parA = c.getParentA (), parB = c.getParentB ();
			if (parA != null || parB != null)
			{
				if (parA == parB)
				{
					// connect w/o mod
					addEdge (parA.getId (), c.getId (), CRN.UNMODIFIED, SBOTerm.MOD_NONE);
				}
				else
				{
					if (parA != null)
					{
						// connect delete
						addEdge (parA.getId (), c.getId (), CRN.DELETE, SBOTerm.MOD_NONE);
					}
					if (parB != null)
					{
						// connect insert
						addEdge (parA.getId (), c.getId (), CRN.INSERT, SBOTerm.MOD_NONE);
					}
				}
			}
			
		}
		
		return graph.toJSONString ();
	}
}

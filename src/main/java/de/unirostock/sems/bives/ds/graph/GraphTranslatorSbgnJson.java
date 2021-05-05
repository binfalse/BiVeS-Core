/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkComponent;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkVariable;
import de.unirostock.sems.bives.ds.ontology.SBOTerm;
import de.unirostock.sems.bives.ds.rn.ReactionNetwork;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkCompartment;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkReaction;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkSubstance;
import de.unirostock.sems.bives.ds.rn.ReactionNetworkSubstanceRef;
import de.unirostock.sems.xmlutils.ds.DocumentNode;

/**
 * The class GraphTranslatorSbgnJson translates the internal graph structure into a JSON
 * which is SBGN PD sufficient. The needed form looks like:
 * {"nodes":
 * 	[
		{"bivesChange":"...","compartment":"...","id":"...","label":"...","sboTerm":"..."},
		...
	],
	"links":
	[
		{"bivesChange":"...","source":"...","sboTerm":"...","target":"..."},
		...
	]
 * }
	
	The elements order is not important

 * @author Tom
 *
 */

public class GraphTranslatorSbgnJson
	extends GraphTranslator
{
	/** The nodes. */
	private JSONArray	nodes;
	
	/** The edges. */
	private JSONArray	edges;
	
	/** The graph. */
	private JSONObject	graph;
	
	
	//retrieve doc Path based on change for comodi connection

	private String getPath(DocumentNode docNodeA, DocumentNode docNodeB, int mod) {
		if(mod == 1 || mod == 2 || mod == 0) return docNodeB.getXPath();
		else return docNodeA.getXPath();
		//return "";
	}
	//add node
	@SuppressWarnings("unchecked")
	private void addNode (String id, String label, String compartment, int diffClass, String sbo, String path)
	{
		JSONObject node = new JSONObject();
		
		node.put("id", id);
		node.put("label", label);
		node.put("compartment", compartment);
		
		node.put("sboTerm", sbo);
		
		node.put("path", path);
		
		String diff;
		
		switch (diffClass) {
        case 0:  diff = "nothing";
                 break;
        case -1:  diff = "delete";
                 break;
        case 2:  diff = "update";
                 break;
        case 3:  diff = "move";
                 break;
        case 1:  diff = "insert";
                 break;
        default: diff = "diffClass"+diffClass;
                 break;
		}
		
		node.put("bivesChange", diff);
		
		nodes.add(node);
	}
	
	//add compartment
	@SuppressWarnings("unchecked")
	private void addCompartment (String id, String compartment, String label, int diffClass, String sbo, String path)
	{
		JSONObject node = new JSONObject();
		
		node.put("id", id);
		node.put("label", label);
		node.put("compartment", compartment);
	
		node.put("sboTerm", sbo);
		
		node.put("path", path);
		
		String diff;
		
		//match change types
		switch (diffClass) {
        case 0:  diff = "nothing";
                 break;
        case -1:  diff = "delete";
                 break;
        case 2:  diff = "update";
                 break;
        case 3:  diff = "move";
                 break;
        case 1:  diff = "insert";
                 break;
        default: diff = "";
        		 break;
		}
		
		node.put("bivesChange", diff);
		
		nodes.add(node);
	}
	
	//add edge
	@SuppressWarnings("unchecked")
	private void addEdge (String source, String target, String sbgnClass, int diffClass, String path)
	{
		JSONObject edge = new JSONObject();
		
		edge.put("source", source);
		edge.put("target", target);
		
		edge.put("sboTerm", sbgnClass);
		
		edge.put("path", path);
		
		String diff;

		//match change types
		switch (diffClass) {
        case 0:  diff = "nothing";
                 break;
        case -1:  diff = "delete";
                 break;
        case 2:  diff = "update";
                 break;
        case 3:  diff = "move";
                 break;
        case 1:  diff = "insert";
                 break;
        default: diff = "bivesChange"+diffClass;
                 break;
		}
		
		edge.put("bivesChange", diff);
		edges.add(edge);
	}
	
	/**
	 * Start a new graph. Initializes the resources.
	 */
	@SuppressWarnings("unchecked")
	private void startNewGraph()
	{
		graph = new JSONObject();
		nodes = new JSONArray();
		edges = new JSONArray();
		
		graph.put("nodes", nodes);
		graph.put("links", edges);
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
	
	
	public String translate (ReactionNetwork rn)
	{
		if (rn == null)
			return null;
		
		startNewGraph ();
		
		//add compartments
		for (ReactionNetworkCompartment c : rn.getCompartments ()){
			String path = getPath(c.getA(), c.getB(), c.getModification());
			addCompartment(c.getId(), "null", c.getLabel(), c.getModification(), "SBO:0000290", path);
		}
		
		//add species 
		for (ReactionNetworkSubstance s : rn.getSubstances ()){
			String path = getPath(s.getA(), s.getB(), s.getModification());
			ReactionNetworkCompartment compartment = s.getCompartment ();
			String label = ""+s.getLabel();
			if(!label.equals("EmptySet") && !label.equals("Empty Set") && !label.equals("emptyset") && !label.equals("empty set")){	
				if(compartment != null && !compartment.equals("null") ) {
					addNode(s.getId(), s.getLabel(), compartment.getId(), s.getModification(), s.getSBO(), path);
				}
				else {
					addNode(s.getId(), s.getLabel(), null, s.getModification(), s.getSBO(), path);
				}
			}
		}
		
		//variable for sourceSink Id's
		int sourceSink = 0;

		//loop over reactions
		for (ReactionNetworkReaction r : rn.getReactions ()){
			ReactionNetworkCompartment compartment = r.getCompartment ();
			Collection<ReactionNetworkSubstanceRef> inputs = r.getInputs();
			Collection<ReactionNetworkSubstanceRef> outputs = r.getOutputs();
			Collection<ReactionNetworkSubstanceRef> modifiers = r.getModifiers();
			
			String rPath = getPath(r.getA(), r.getB(), r.getModification());
			
			//add process Node
			String processId = r.getId();
			String compartmentId = null;
			//check if the process assigned to a compartment
			if (compartment != null) compartmentId = compartment.getId();
			if(r.getSBO() == null || r.getSBO().equals("")){
				addNode(processId, null, compartmentId, r.getModification(), "SBO:0000205", rPath);
			} else 	addNode(processId, null, compartmentId, r.getModification(), r.getSBO(), rPath);
			
				//check if its a creation or deletion
				
				//input is not empty	
				if(!inputs.isEmpty()) {
					for(ReactionNetworkSubstanceRef s : inputs){
						
						//String path = getPath(s.getA(), s.getB(), s.getModification());
						String label = "" + s.getSubstance().getLabel();
						//String path = getPath(s.getA(), s.getB(), s.getModification());
						if(!label.matches("(?i)^empty[ ,_,\\',^]?set")){
							addEdge(s.getSubstance().getId(), processId, "SBO:0000015", s.getModification(), s.getXPath());
						} else {
							//Empty set is target species in SBML
							addNode("EmptySet" + sourceSink, null, compartmentId, s.getModification(), "SBO:0000291", rPath);
							addEdge("EmptySet" + sourceSink, processId, "SBO:0000015", s.getModification(), s.getXPath());
							sourceSink++;
						}
					}
				} else { //input is empty. it is a creation	
					//add SourceSink node
					addNode("EmptySet" + sourceSink, null, compartmentId, r.getModification(), "SBO:0000291", rPath);
					//add edge between SourceSink and process node
					addEdge("EmptySet" + sourceSink, processId, "SBO:0000015", r.getModification(), rPath);
					sourceSink++;
				}
				
				//output is not empty
				if(!outputs.isEmpty()){
					for(ReactionNetworkSubstanceRef s : outputs){
						String label = ""+s.getSubstance().getLabel();
						if(!label.matches("(?i)^empty[ ,_,\\',^]?set")){
							addEdge(processId, s.getSubstance().getId(), "SBO:0000393", s.getModification(), s.getXPath());
						} else { 
							//Empty set is target species in SBML 
								addNode("EmptySet" + sourceSink, null, compartmentId, s.getModification(), "SBO:0000291", s.getXPath());
								addEdge(processId, "EmptySet" + sourceSink, "SBO:0000393", s.getModification(), s.getXPath());
								sourceSink++;
						}						
					}
				
				//output is empty. it is a deletion
				} else {
					//add SourceSink node
					addNode("EmptySet" + sourceSink, null, compartmentId, r.getModification(), "SBO:0000291", rPath);
					//add edge between SourceSink and process node
					addEdge(processId, "EmptySet" + sourceSink, "SBO:0000393", r.getModification(), rPath);
					sourceSink++;
				}
				
				//add modifiers
				if(modifiers != null){
					for (ReactionNetworkSubstanceRef s : modifiers){
						addEdge(s.getSubstance().getId(), processId, s.getSBO(), s.getModification(), s.getXPath());
					}
				}

		}
		return graph.toJSONString();
	}
	
	/*
	 * translation to a hierarchy network necessary but not needed for SBML
	 * @see de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock.sems.bives.ds.hn.HierarchyNetwork)
	 */
	public String translate (HierarchyNetwork hn)
	{
		return null;
	}
}


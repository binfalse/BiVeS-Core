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

/**
 * The class GraphTranslatorSbgnJson translates the internal graph structure into a JSON
 * which is SBGN PD sufficient. The needed form looks like:
 * 
 *  
 * For more exact information, have a look at the Masterthesis
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
	
	@SuppressWarnings("unchecked")
	private void addNode (String id, String label, String compartment, int diffClass, String sbo)
	{
		JSONObject node = new JSONObject();
		
		node.put("id", id);
		node.put("label", label);
		node.put("compartment", compartment);
		
		node.put("class", sbo);
		
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
		
		node.put("bivesClass", diff);
		
		nodes.add(node);
	}
	
	@SuppressWarnings("unchecked")
	private void addCompartment (String id, String compartment, String label, int diffClass, String sbo)
	{
		JSONObject node = new JSONObject();
		
		node.put("id", id);
		node.put("label", label);
		node.put("compartment", compartment);
	
		node.put("class", sbo);
		
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
		
		node.put("bivesClass", diff);
		
		nodes.add(node);
	}
	
	@SuppressWarnings("unchecked")
	private void addEdge (String source, String target, String sbgnClass, int diffClass)
	{
		JSONObject edge = new JSONObject();
		
		edge.put("source", source);
		edge.put("target", target);
		
		edge.put("class", sbgnClass);
		
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
        default: diff = "bivesClass"+diffClass;
                 break;
		}
		
		edge.put("bivesClass", diff);
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
			addCompartment(c.getId(), "null", c.getLabel(), c.getModification(), "SBO:0000290");
		}
		
		//add species 
		for (ReactionNetworkSubstance s : rn.getSubstances ()){
			ReactionNetworkCompartment compartment = s.getCompartment ();
			
			if(s.getLabel() != "EmptySet"){
				
				System.out.println(compartment);
				if(compartment != null && !compartment.equals("null") ) {
					addNode(s.getId(), s.getLabel(), compartment.getId(), s.getModification(), s.getSBO());
				}
				else { 
					addNode(s.getId(), s.getLabel(), null, s.getModification(), s.getSBO());
				}
			}
		}
		
		//variable for sourceSink Id's
		int sourceSink = 0;
		for (ReactionNetworkReaction r : rn.getReactions ()){
			ReactionNetworkCompartment compartment = r.getCompartment ();
			Collection<ReactionNetworkSubstanceRef> inputs = r.getInputs();
			Collection<ReactionNetworkSubstanceRef> outputs = r.getOutputs();
			Collection<ReactionNetworkSubstanceRef> modifiers = r.getModifiers();
			
			
			//add process Node
			String processId = r.getId();
			String compartmentId = null;
			//check if the process assigned to a compartment
			if (compartment != null){
				compartmentId = compartment.getId();
				if(r.getSBO() == null || r.getSBO() == ""){
					addNode(processId, null, compartmentId, r.getModification(), "SBO:0000205");
				} else 	addNode(processId, null, compartmentId, r.getModification(), r.getSBO());
			} else {
				if(r.getSBO() == null || r.getSBO() == ""){
					addNode(processId, null, compartmentId, r.getModification(), "SBO:0000205");
				} else 	addNode(processId, null, compartmentId, r.getModification(), r.getSBO());				
			}
				//check if its a creation or deletion
				
				if(!inputs.isEmpty()) { //creation
					for(ReactionNetworkSubstanceRef s : inputs){
						
						if(s.getSubstance().getLabel() != "EmptySet"){
							addEdge(s.getSubstance().getId(), processId, "SBO:0000015", r.getModification());
						} else {
							//Empty set is target species in SBML
							addNode("EmptySet" + sourceSink, null, compartmentId, r.getModification(), "SBO:0000291");
							addEdge("EmptySet" + sourceSink, processId, "SBO:0000015", r.getModification());
							sourceSink++;
						}
					}
					
				} else { //input is empty. it is a creation
					LOGGER.info("should add an empty set");
					//add SourceSink node
					addNode("EmptySet" + sourceSink, null, compartmentId, r.getModification(), "SBO:0000291");
					//add edge between SourceSink and process node
					addEdge("EmptySet" + sourceSink, processId, "SBO:0000015", r.getModification());
					sourceSink++;
				}
				
				if(!outputs.isEmpty()){
					for(ReactionNetworkSubstanceRef s : outputs){

						if(s.getSubstance().getLabel() != "EmptySet"){
							addEdge(processId, s.getSubstance().getId(), "SBO:0000393", r.getModification());
						} else { 
							//Empty set is target species in SBML 
								addNode("EmptySet" + sourceSink, null, compartmentId, r.getModification(), "SBO:0000291");
								addEdge(processId, "EmptySet" + sourceSink, "SBO:0000393", r.getModification());
								sourceSink++;
						}						
					}
				} else {
					//empty output
					//add SourceSink node
					addNode("EmptySet" + sourceSink, null, compartmentId, r.getModification(), "SBO:0000291");
					//add edge between SourceSink and process node
					addEdge(processId, "EmptySet" + sourceSink, "SBO:0000393", r.getModification());
					sourceSink++;
				}
				
				if(modifiers != null){
					for (ReactionNetworkSubstanceRef s : r.getModifiers()){
						addEdge(s.getSubstance().getId(), processId, s.getModTerm(), s.getModification());
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


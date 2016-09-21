/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		System.out.println("diff "+diffClass);
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
	
	private void addModifier (String id, String compartment, String label, int diffClass){
		
	}
	
	@SuppressWarnings("unchecked")
	private void addEdge (String source, String target, String sbgnClass, int diffClass)
	{
		JSONObject edge = new JSONObject();
		
		edge.put("source", source);
		edge.put("target", target);
		
		edge.put("class", sbgnClass);
		edge.put("diffClass", diffClass);
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
			
			if(!s.getLabel().equals("EmptySet")){
				
				if(!compartment.equals("null")) {
					addNode(s.getId(), s.getLabel(), compartment.getId(), s.getModification(), s.getSBO());
				}
				else { 
					addNode(s.getId(), null, null, s.getModification(), s.getSBO());
				}
			} else {
				if(!compartment.equals("null")) {
					addNode(s.getId(), null, compartment.getId(), s.getModification(), s.getSBO());
				} else {
					addNode(s.getId(), null, null, s.getModification(), s.getSBO());
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
			
			//check if the process assigned to a compartment
			if (compartment != null){
				System.out.println("202 sbo "+r.getSBO());
				System.out.println(r.getSBO() == null);
				System.out.println(r.getSBO() == "");
				System.out.println(r.getSBO().equals(""));
				if(r.getSBO() == null){
					addNode(processId, null, compartment.getId(), r.getModification(), "SBO:0000205");
				} else 	addNode(processId, null, compartment.getId(), r.getModification(), "SBO:0000205");
			} else {
				if(r.getSBO() == null){
					addNode(processId, null, null, r.getModification(), "SBO:0000205");
				} else 	addNode(processId, null, null, r.getModification(), "SBO:0000205");				
			}
				//check if its a creation or deletion
				
				System.out.println(inputs);
				if(!inputs.isEmpty()) { //creation
					for(ReactionNetworkSubstanceRef s : inputs){
						System.out.println("215 label "+s.getSubstance().getLabel());
						if(!s.getSubstance().getLabel().equals("EmptySet")){
							addEdge(s.getSubstance().getId(), processId, "SBO:0000015", r.getModification());

							
						} else {
							System.out.println("221 test");
							
							if(sourceSink > 0){
								addNode("EmptySet" + sourceSink, null, compartment.getId(), r.getModification(), "SBO:0000291");
								addEdge("EmptySet" + sourceSink, processId, "SBO:0000015", r.getModification());
								sourceSink++;
							} else {
								addEdge(s.getSubstance().getId(), processId, "SBO:0000015", r.getModification());
								sourceSink++;
							}
						}
					}
					
				} else {
					System.out.println("should add an empty set");
					//add SourceSink node
					addNode("EmptySet" + sourceSink, null, compartment.getId(), r.getModification(), "SBO:0000291");
					
					//add edge between SourceSink and process node
					addEdge("EmptySet" + sourceSink, processId, "SBO:0000015", r.getModification());
					
					sourceSink++;
				
				}
				System.out.println(outputs);
				if(!outputs.isEmpty()){
					for(ReactionNetworkSubstanceRef s : outputs){
						System.out.println("248 label " + s.getSubstance().getLabel());

						if(!s.getSubstance().getLabel().equals("EmptySet")){
							addEdge(processId, s.getSubstance().getId(), "SBO:0000393", r.getModification());
						} else {
							System.out.println("253 test test");
							if(sourceSink > 1){
								addNode("EmptySet" + sourceSink, null, compartment.getId(), r.getModification(), "SBO:0000291");
								addEdge(processId, s.getSubstance().getId(), "SBO:0000393", r.getModification());
								sourceSink++;
							} else {
								addEdge(processId, s.getSubstance().getId(), "SBO:0000393", r.getModification());
								sourceSink++;
							}
							
							
							
							
						}						
					}
				} else {
					System.out.println("269 should add an empty set");
					//add SourceSink node
					addNode("sourceSink" + sourceSink, null, compartment.getId(), r.getModification(), "SBO:0000291");
					
					//add edge between SourceSink and process node
					addEdge(processId, "sourceSink" + sourceSink, "SBO:0000393", r.getModification());
					
					sourceSink++;
				}
				
				if(!modifiers.equals(null)){
					for (ReactionNetworkSubstanceRef s : r.getModifiers()){
						addEdge(s.getSubstance().getId(), processId, s.getModTerm(), r.getModification());
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


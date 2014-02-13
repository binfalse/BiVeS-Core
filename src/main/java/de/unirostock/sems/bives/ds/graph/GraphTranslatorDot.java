/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.crn.CRNCompartment;
import de.unirostock.sems.bives.ds.crn.CRNReaction;
import de.unirostock.sems.bives.ds.crn.CRNSubstance;
import de.unirostock.sems.bives.ds.crn.CRNSubstanceRef;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkComponent;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkVariable;
import de.unirostock.sems.bives.ds.ontology.SBOTerm;
import de.unirostock.sems.bives.markup.Typesetting;



/**
 * The Class GraphTranslatorDot to translate internal graph structures into
 * DOT format.
 * (e.g. to create images using GraphViz)
 * 
 * The resulting graph will look like:
 * 
 * <pre>
 * digraph BiVeSexport {
 * 	graph [overlap=false];
 * 	edge [len=1.3];
 * 	node [fontsize=11];
 * 	subgraph clusterc1 {
 * 		label = "compartment";
 * 		color=lightgrey;
 * 		s2[label="sigb",shape=circle];
 * 		s1[label="lacz",shape=circle];
 * 		s4[label="x",shape=circle];
 * 		s3[label="IPTG",shape=circle];
 * 		r3[label="sigb degr",color=yellow,shape=diamond];
 * 		r2[label="lacz degr",shape=diamond];
 * 		r1[label="sigb syn",color=yellow,shape=diamond];
 * 		r4[label="x syn",color=yellow,shape=diamond];
 * 		r5[label="lacz syn",color=yellow,shape=diamond];
 * 	}
 * 	s2-&gt;r3;
 * 	s4-&gt;r3[color=red,style=dashed,arrowType=normal];
 * 	s4-&gt;r3[color=blue,style=dashed,arrowType=odot];
 * 	s1-&gt;r2;
 * 	r1-&gt;s2;
 * 	s3-&gt;r1[color=red,style=dashed,arrowType=normal];
 * 	s3-&gt;r1[color=blue,style=dashed,arrowType=odot];
 * 	r4-&gt;s4;
 * 	s2-&gt;r4[color=red,style=dashed,arrowType=normal];
 * 	s2-&gt;r4[color=blue,style=dashed,arrowType=odot];
 * 	r5-&gt;s1;
 * 	s2-&gt;r5[color=red,style=dashed,arrowType=normal];
 * 	s2-&gt;r5[color=blue,style=dashed,arrowType=odot];
 * 	label="Diff Graph created by BiVeS";
 * }
 * </pre>
 * 
 * for more information see <a href=
 * "https://sems.uni-rostock.de/trac/bives-core/wiki/DotFormatDescription"
 * >DotFormatDescription</a>
 * 
 * 
 * @author Martin Scharm
 */
public class GraphTranslatorDot
	extends GraphTranslator
{
	
	/** The string containing the dot graph. */
	private String	dotStr;
	
	
	/**
	 * Instantiates a new graph translator for DOT conversion.
	 */
	public GraphTranslatorDot ()
	{
		dotStr = "";
	}
	
	
	/**
	 * Gets the dot postamble.
	 * 
	 * @return the dot postamble
	 */
	private static String getDotPostamble ()
	{
		return "\tlabel=\"Diff Graph created by BiVeS\";" + Typesetting.NL_TXT
			+ "}";
	}
	
	
	/**
	 * Gets the dot preamble.
	 * 
	 * @return the dot preamble
	 */
	private static String getDotPreamble ()
	{
		return "##Command to produce the pic: `neato -Tpng thisfile > thisfile.png`"
			+ Typesetting.NL_TXT
			+ Typesetting.NL_TXT
			+ "digraph BiVeSexport {"
			+ Typesetting.NL_TXT
			+ "\tgraph [overlap=false];"
			+ Typesetting.NL_TXT
			+ "\tedge [len=1.3];"
			+ Typesetting.NL_TXT
			+ "\tnode [fontsize=11];"
			+ Typesetting.NL_TXT;
	}
	
	/**
	 * Adds a node to the dot gaph.
	 * 
	 * @param id
	 *          the id of the node
	 * @param name
	 *          the label of the node
	 * @param version
	 *          the version flag: [-1,0,1,2]
	 * @param species
	 *          the species
	 * @return the string
	 */
	private static String addNode (String id, String name, int version,
		boolean species)
	{
		String ret = id + "[label=\"" + name + "\"";
		switch (version)
		{
			case 1:
				ret += ",color=blue";
				break;
			case 2:
				ret += ",color=yellow";
				break;
			case -1:
				ret += ",color=red";
				break;
		}
		
		if (species)
			ret += ",shape=circle";
		else
			ret += ",shape=diamond";
		
		ret += "];" + Typesetting.NL_TXT;
		return "\t" + ret;
	}
	
	
	/**
	 * Adds the edge.
	 * 
	 * @param from
	 *          the from
	 * @param to
	 *          the to
	 * @param version
	 *          the version flag: [-1,0,1,2]
	 * @param modification
	 *          the modification property (SBOTerm.*)
	 * @return the string
	 */
	private static String addEdge (String from, String to, int version,
		String modification)
	{
		String ret = from + "->" + to;
		String sub = null;
		switch (version)
		{
			case 1:
				sub = "color=blue";
				break;
			case 2:
				sub = "color=yellow";
				break;
			case -1:
				sub = "color=red";
				break;
		}
		String sub2 = null;
		if (modification != null)
		{
			if (modification.equals (SBOTerm.MOD_INHIBITOR))
				sub2 = "style=dashed,arrowType=tee";
			else if (modification.equals (SBOTerm.MOD_STIMULATOR))
				sub2 = "style=dashed,arrowType=normal";
			else if (modification.equals (SBOTerm.MOD_UNKNOWN))
				sub2 = "style=dashed,arrowType=odot";
		}
		
		if (sub != null && sub2 != null)
			ret += "[" + sub + "," + sub2 + "]";
		else if (sub != null)
			ret += "[" + sub + "]";
		else if (sub2 != null)
			ret += "[" + sub2 + "]";
		
		ret += ";" + Typesetting.NL_TXT;
		return "\t" + ret;
	}
	
	
	/**
	 * Creates a compartment.
	 * 
	 * @param compartment
	 *          the compartment as found in the chemical reaction network
	 * @param nodeList
	 *          the nodes of that compartment
	 * @return the string
	 */
	private String createCompartment (CRNCompartment compartment,
		List<String> nodeList)
	{
		String ret = "\tsubgraph cluster" + compartment.getId () + " {"
			+ Typesetting.NL_TXT;
		ret += "\t\tlabel = \"" + compartment.getLabel () + "\";"
			+ Typesetting.NL_TXT;
		ret += "\t\tcolor=lightgrey;" + Typesetting.NL_TXT;
		for (String n : nodeList)
			ret += "\t" + n;
		ret += "\t}" + Typesetting.NL_TXT;
		return ret;
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
		dotStr = getDotPreamble ();
		
		List<String> edges = new ArrayList<String> ();
		HashMap<CRNCompartment, List<String>> compartments = new HashMap<CRNCompartment, List<String>> ();
		for (CRNCompartment c : crn.getCompartments ())
		{
			compartments.put (c, new ArrayList<String> ());
		}
		
		for (CRNSubstance s : crn.getSubstances ())
		{
			CRNCompartment compartment = s.getCompartment ();
			if (compartment != null)
				compartments.get (compartment).add (
					addNode (s.getId (), s.getLabel (), s.getModification (), true));
			else
				dotStr += addNode (s.getId (), s.getLabel (), s.getModification (),
					true);
		}
		
		for (CRNReaction r : crn.getReactions ())
		{
			CRNCompartment compartment = r.getCompartment ();
			if (compartment != null)
				compartments.get (compartment).add (
					addNode (r.getId (), r.getLabel (), r.getModification (), false));
			else
				dotStr += addNode (r.getId (), r.getLabel (), r.getModification (),
					false);
			
			for (CRNSubstanceRef s : r.getInputs ())
				edges.add (addEdge (s.getSubstance ().getId (), r.getId (),
					s.getModification (), SBOTerm.MOD_NONE));
			
			for (CRNSubstanceRef s : r.getOutputs ())
				edges.add (addEdge (r.getId (), s.getSubstance ().getId (),
					s.getModification (), SBOTerm.MOD_NONE));
			
			for (CRNSubstanceRef s : r.getModifiers ())
			{
				if (s.getModification () == CRN.MODIFIED)
				{
					edges.add (addEdge (s.getSubstance ().getId (), r.getId (),
						CRN.DELETE, s.getModTermA ()));
					edges.add (addEdge (s.getSubstance ().getId (), r.getId (),
						CRN.INSERT, s.getModTermB ()));
				}
				else
					edges.add (addEdge (s.getSubstance ().getId (), r.getId (),
						s.getModification (), s.getModTerm ()));
			}
		}
		
		for (CRNCompartment compartment : compartments.keySet ())
		{
			dotStr += createCompartment (compartment, compartments.get (compartment));
		}
		
		for (String e : edges)
			dotStr += e;
		
		dotStr += getDotPostamble ();
		
		return dotStr;
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
		dotStr = getDotPreamble ();
		Collection<HierarchyNetworkComponent> components = hn.getComponents ();
		for (HierarchyNetworkComponent c : components)
		{
			dotStr += "\tsubgraph cluster" + c.getId () + " {" + Typesetting.NL_TXT;
			dotStr += "\t\tlabel = \"" + c.getLabel () + "\";" + Typesetting.NL_TXT;
			dotStr += "\t\tcolor=lightgrey;" + Typesetting.NL_TXT;
			
			List<HierarchyNetworkVariable> vars = c.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				dotStr += "\t"
					+ addNode (var.getId (), var.getLabel (), var.getModification (),
						true);
			}
			dotStr += "\t}" + Typesetting.NL_TXT;
		}
		
		for (HierarchyNetworkComponent comp : components)
		{
			HierarchyNetworkComponent parA = comp.getParentA (), parB = comp
				.getParentB ();
			if (parA != null || parB != null)
			{
				if (parA == parB)
				{
					// connect w/o mod
					dotStr += addEdge ("cluster" + parA.getId (),
						"cluster" + comp.getId (), CRN.UNMODIFIED, SBOTerm.MOD_NONE);
				}
				else
				{
					if (parA != null)
					{
						// connect delete
						dotStr += addEdge ("cluster" + parA.getId (),
							"cluster" + comp.getId (), CRN.DELETE, SBOTerm.MOD_NONE);
					}
					if (parB != null)
					{
						// connect insert
						dotStr += addEdge ("cluster" + parA.getId (),
							"cluster" + comp.getId (), CRN.INSERT, SBOTerm.MOD_NONE);
					}
				}
			}
			
			List<HierarchyNetworkVariable> vars = comp.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				HashMap<HierarchyNetworkVariable, HierarchyNetworkVariable.VarConnection> cons = var
					.getConnections ();
				
				for (HierarchyNetworkVariable con : cons.keySet ())
				{
					LOGGER.info ("connecting var: ", var.getId (), " -> ",
						con.getId ());
					dotStr += addEdge (con.getId (), var.getId (), cons.get (con)
						.getModification (), SBOTerm.MOD_NONE);
				}
			}
		}
		
		dotStr += getDotPostamble ();
		return dotStr;
	}
}

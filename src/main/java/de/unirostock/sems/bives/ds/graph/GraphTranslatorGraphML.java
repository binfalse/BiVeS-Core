/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.bives.ds.crn.CRNCompartment;
import de.unirostock.sems.bives.ds.crn.CRNReaction;
import de.unirostock.sems.bives.ds.crn.CRNSubstance;
import de.unirostock.sems.bives.ds.crn.CRNSubstanceRef;
import de.unirostock.sems.bives.ds.hn.HierarchyNetwork;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkComponent;
import de.unirostock.sems.bives.ds.hn.HierarchyNetworkVariable;
import de.unirostock.sems.bives.ds.ontology.SBOTerm;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * The Class GraphTranslatorGraphML to translate internal graph structures into
 * GraphML.
 * 
 * The resulting graph will look like:
 * 
 * <pre>
 * &lt;graphml&gt;
 * 	&lt;key attr.name="name" attr.type="string" for="node" id="name"/&gt;
 * 	&lt;key attr.name="node set" attr.type="string" for="node" id="ns"&gt;
 * 			&lt;default&gt;species&lt;/default&gt;
 * 	&lt;/key&gt;
 * 	&lt;key attr.name="version" attr.type="int" for="all" id="vers"&gt;
 * 			&lt;default&gt;0&lt;/default&gt;
 * 	&lt;/key&gt;
 * 	&lt;key attr.name="modifier" attr.type="string" for="edge" id="mod"&gt;
 * 			&lt;default&gt;none&lt;/default&gt;
 * 	&lt;/key&gt;
 * 	&lt;graph edgedefault="directed" id="G"&gt;
 * 		&lt;node id="c1"&gt;
 * 			&lt;data key="vers"&gt;0&lt;/data&gt;
 * 			&lt;data key="name"&gt;compartment&lt;/data&gt;
 * 			&lt;graph edgedefault="directed" id="G1"&gt;
 * 				&lt;node id="s2"&gt;
 * 					&lt;data key="ns"&gt;species&lt;/data&gt;
 * 					&lt;data key="vers"&gt;0&lt;/data&gt;
 * 					&lt;data key="name"&gt;sigb&lt;/data&gt;
 * 				&lt;/node&gt;
 * 				&lt;node id="s1"&gt;
 * 					&lt;data key="ns"&gt;species&lt;/data&gt;
 * 					&lt;data key="vers"&gt;0&lt;/data&gt;
 * 					&lt;data key="name"&gt;lacz&lt;/data&gt;
 * 				&lt;/node&gt;
 * 				&lt;node id="r3"&gt;
 * 					&lt;data key="ns"&gt;reaction&lt;/data&gt;
 * 					&lt;data key="vers"&gt;2&lt;/data&gt;
 * 					&lt;data key="name"&gt;sigb degr&lt;/data&gt;
 * 				&lt;/node&gt;
 * 				&lt;!-- more nodes --&gt;
 * 			&lt;/graph&gt;
 * 		&lt;/node&gt;
 * 		&lt;edge source="s2" target="r3"&gt;
 * 			&lt;data key="mod"&gt;none&lt;/data&gt;
 * 			&lt;data key="vers"&gt;0&lt;/data&gt;
 * 		&lt;/edge&gt;
 * 		&lt;edge source="s4" target="r3"&gt;
 * 			&lt;data key="mod"&gt;stimulator&lt;/data&gt;
 * 			&lt;data key="vers"&gt;-1&lt;/data&gt;
 * 		&lt;/edge&gt;
 * 		&lt;!-- more edges --&gt;
 * 	&lt;/graph&gt;
 * &lt;/graphml&gt;
 * </pre>
 * 
 * for more information see <a href=
 * "https://sems.uni-rostock.de/trac/bives-core/wiki/GraphmlFormatDescription"
 * >GraphmlFormatDescription</a>
 * 
 * @author Martin Scharm
 */
public class GraphTranslatorGraphML
	extends GraphTranslator
{
	
	/** The root node of the graph document. */
	private Element		graphRoot;
	
	/** The graph document. */
	private Document	graphDocument;
	
	/** The graph id. */
	private int				graphid;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock
	 * .sems.bives.ds.graph.HierarchyNetwork)
	 */
	@Override
	public String translate (HierarchyNetwork hn)
	{
		if (hn == null)
			return null;
		
		/*graphDocument = DocumentBuilderFactory.newInstance ().newDocumentBuilder ()
			.newDocument ();*/
		Element graphML = new Element ("graphml");
		graphRoot = addGraphMLPreamble (graphML);
		graphDocument = new Document (graphML);
		graphid = 1;
		
		Collection<HierarchyNetworkComponent> components = hn.getComponents ();
		for (HierarchyNetworkComponent comp : components)
		{
			LOGGER.info ("creating comp: ", comp.getId ());
			Element node = createGraphMLNode (graphRoot, comp.getId (), null,
				comp.getLabel (), comp.getModification () + "");
			Element subtree = createGraphRoot (true);
			node.addContent (subtree);
			
			List<HierarchyNetworkVariable> vars = comp.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				LOGGER.info ("creating var: ", var.getId ());
				//Element vNode = 
				createGraphMLNode (subtree, var.getId (), null,
					var.getLabel (), var.getModification () + "");
				//subtree.addContent (vNode);
			}
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
					createEdge (graphRoot, parA.getId (), comp.getId (), null, null);
				}
				else
				{
					if (parA != null)
					{
						// connect delete
						createEdge (graphRoot, parA.getId (), comp.getId (), GraphEntity.DELETE
							+ "", null);
					}
					if (parB != null)
					{
						// connect insert
						createEdge (graphRoot, parB.getId (), comp.getId (), GraphEntity.INSERT
							+ "", null);
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
					createEdge (graphRoot, con.getId (), var.getId (), "" + cons.get (con)
						.getModification (), null);
				}
			}
		}
		
		return XmlTools.prettyPrintDocument (graphDocument);
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
		if (crn == null)
			return null;
		
		/*graphDocument = DocumentBuilderFactory.newInstance ().newDocumentBuilder ()
			.newDocument ();*/

		Element graphML = new Element ("graphml");
		
		graphRoot = addGraphMLPreamble (graphML);
		graphDocument = new Document (graphML);
		HashMap<CRNCompartment, Element> compartments = new HashMap<CRNCompartment, Element> ();
		graphid = 1;
		
		for (CRNCompartment c : crn.getCompartments ())
		{
			Element node = createGraphMLNode (graphRoot, c.getId (), null,
				c.getLabel (), c.getModification () + "");
			Element compartment = createGraphRoot (true);
			node.addContent (compartment);
			compartments.put (c, compartment);
		}
		
		for (CRNSubstance s : crn.getSubstances ())
		{
			CRNCompartment compartment = s.getCompartment ();
			if (compartment != null)
				createGraphMLNode (compartments.get (compartment), s.getId (),
					"species", s.getLabel (), s.getModification () + "");
			else
				createGraphMLNode (graphRoot, s.getId (), "species", s.getLabel (),
					s.getModification () + "");
		}
		
		for (CRNReaction r : crn.getReactions ())
		{
			CRNCompartment compartment = r.getCompartment ();
			if (compartment != null)
				createGraphMLNode (compartments.get (compartment), r.getId (),
					"reaction", r.getLabel (), r.getModification () + "");
			else
				createGraphMLNode (graphRoot, r.getId (), "reaction", r.getLabel (),
					r.getModification () + "");
			
			for (CRNSubstanceRef s : r.getInputs ())
				createEdge (graphRoot, s.getSubstance ().getId (), r.getId (),
					s.getModification () + "", SBOTerm.MOD_NONE);
			
			for (CRNSubstanceRef s : r.getOutputs ())
				createEdge (graphRoot, r.getId (), s.getSubstance ().getId (),
					s.getModification () + "", SBOTerm.MOD_NONE);
			
			for (CRNSubstanceRef s : r.getModifiers ())
			{
				/*if (s.getModification () == CRN.MODIFIED)
				{
					createEdge (graphRoot, s.getSubstance ().getId (), r.getId (),
						CRN.DELETE + "", s.getModTermA ());
					createEdge (graphRoot, s.getSubstance ().getId (), r.getId (),
						CRN.INSERT + "", s.getModTermB ());
				}
				else*/
					createEdge (graphRoot, s.getSubstance ().getId (), r.getId (),
						s.getModification () + "", s.getModTerm ());
			}
		}
		
		return XmlTools.prettyPrintDocument (graphDocument);
	}
	
	/**
	 * Gets the graphml document.
	 *
	 * @return the graphml document
	 */
	public Document getGraphmlDocument ()
	{
		return graphDocument;
	}
	
	
	/**
	 * Produces the preamble of the graph.
	 * 
	 * <p>
	 * Creates some nodes in the document defining the graph and its properties.
	 * Creates also the <graph> node to insert the graph itself. This graph-node
	 * will be returned afterwards. Example of a typical preamble:
	 * </p>
	 * 
	 * <pre>
	 * &lt;graphml&gt;
	 * 	&lt;key attr.name="name" attr.type="string" for="node" id="name"/&gt;
	 * 	&lt;key attr.name="node set" attr.type="string" for="node" id="ns"&gt;
	 * 			&lt;default&gt;species&lt;/default&gt;
	 * 	&lt;/key&gt;
	 * 	&lt;key attr.name="version" attr.type="int" for="all" id="vers"&gt;
	 * 			&lt;default&gt;0&lt;/default&gt;
	 * 	&lt;/key&gt;
	 * 	&lt;key attr.name="modifier" attr.type="string" for="edge" id="mod"&gt;
	 * 			&lt;default&gt;none&lt;/default&gt;
	 * 	&lt;/key&gt;
	 * 	&lt;graph edgedefault="directed" id="G"&gt;
	 * 		&lt;!-- this node will be returned --&gt;
	 * 	&lt;/graph&gt;
	 * &lt;/graphml&gt;
	 * </pre>
	 * 
	 * @param root
	 *          the document
	 * @return the graph node (root for the graph)
	 */
	private Element addGraphMLPreamble (Element graphML)
	{
		
		// key for node name
		Element keyEl = new Element ("key");
		keyEl.setAttribute ("id", "name");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "name");
		keyEl.setAttribute ("attr.type", "string");
		graphML.addContent (keyEl);
		
		// key for node set (e.g. species or reaction)
		keyEl = new Element ("key");
		keyEl.setAttribute ("id", "ns");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "node set");
		keyEl.setAttribute ("attr.type", "string");
		Element defEl = new Element ("default");
		defEl.setText ("species");
		keyEl.addContent (defEl);
		graphML.addContent (keyEl);
		
		// key for version flag
		keyEl = new Element ("key");
		keyEl.setAttribute ("id", "vers");
		keyEl.setAttribute ("for", "all");
		keyEl.setAttribute ("attr.name", "version");
		keyEl.setAttribute ("attr.type", "int");
		defEl = new Element ("default");
		defEl.setText ("0");
		keyEl.addContent (defEl);
		graphML.addContent (keyEl);
		
		// key for modifier flag
		keyEl = new Element ("key");
		keyEl.setAttribute ("id", "mod");
		keyEl.setAttribute ("for", "edge");
		keyEl.setAttribute ("attr.name", "modifier");
		keyEl.setAttribute ("attr.type", "string");
		defEl = new Element ("default");
		defEl.setText (SBOTerm.MOD_NONE);
		keyEl.addContent (defEl);
		graphML.addContent (keyEl);
		
		// <graph>
		keyEl = createGraphRoot (true);
		graphML.addContent (keyEl);
		
		return keyEl;
	}
	
	
	/**
	 * Creates the root of a graph.
	 * 
	 * @param directed
	 *          the directed flag, if true this (sub)graph will be directed
	 * @return the root node
	 */
	private Element createGraphRoot (boolean directed)
	{
		Element keyEl = new Element ("graph");
		keyEl.setAttribute ("id", "G" + graphid++);
		if (directed)
			keyEl.setAttribute ("edgedefault", "directed");
		else
			keyEl.setAttribute ("edgedefault", "undirected");
		return keyEl;
	}
	
	
	/**
	 * Inserts a new node to the graph.
	 * This node will automatically appended a node to the {@code parent}s
	 * children.
	 * 
	 * @param parent
	 *          the parent node
	 * @param id
	 *          the ID of the node
	 * @param ns
	 *          the node set
	 * @param name
	 *          the name of the node
	 * @param version
	 *          the version flag: [-1,0,1,2]
	 * @return the new node
	 */
	private Element createGraphMLNode (Element parent, String id, String ns,
		String name, String version)
	{
		LOGGER.debug ("create gml node: ", id, " mod: ", version);
		Element element = new Element ("node");
		
		element.setAttribute ("id", id);
		
		if (ns != null)
		{
			Element nsElement = new Element ("data");
			nsElement.setAttribute ("key", "ns");
			nsElement.setText (ns);
			element.addContent (nsElement);
		}
		
		if (version != null)
		{
			Element srcElement = new Element ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.setText (version);
			element.addContent (srcElement);
		}
		
		Element nameElement = new Element ("data");
		nameElement.setAttribute ("key", "name");
		nameElement.setText (name);
		element.addContent (nameElement);
		
		if (parent != null)
			parent.addContent (element);
		return element;
	}
	
	
	/**
	 * Inserts a new edge to the graph.
	 * This edge gets automatically appended to the {@code parent}s children.
	 * 
	 * @param parent
	 *          the parent
	 * @param source
	 *          the source
	 * @param target
	 *          the target
	 * @param version
	 *          the version flag: [-1,0,1,2]
	 * @param modifier
	 *          the modifier property (SBOTerm.*)
	 */
	private void createEdge (Element parent, String source, String target,
		String version, String modifier)
	{
		LOGGER.debug ("create gml edge: ", source, " -> ", target, " mod: ",
			version);
		Element element = new Element ("edge");
		
		element.setAttribute ("source", source);
		element.setAttribute ("target", target);
		
		if (modifier != null)
		{
			Element nsElement = new Element ("data");
			nsElement.setAttribute ("key", "mod");
			nsElement.setText (modifier);
			element.addContent (nsElement);
		}
		
		if (version != null)
		{
			Element srcElement = new Element ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.setText (version);
			element.addContent (srcElement);
		}
		
		parent.addContent (element);
	}
}

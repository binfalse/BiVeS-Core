/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * The Class Diff, the central object if it comes to the comparison of XML
 * documents. All methods might return null if not available in a certain case.
 * 
 * @author Martin Scharm
 */
public abstract class Diff
{
	
	/** The tree corresponding to the former version. */
	protected TreeDocument						treeA;
	/** The tree corresponding to the later version. */
	protected TreeDocument						treeB;
	
	/** The connections. */
	protected SimpleConnectionManager	connections;
	
	/** The patch producer to produce a patch from the computed mapping. */
	private PatchProducer							patchProducer;
	
	/** The patch. */
	private Patch											patch;
	
	/** The xml patch. */
	private String										xmlPatch;
	
	
	/**
	 * Instantiates a new diff object in order to compare two documents stored in
	 * files fileA and fileB.
	 *
	 * @param fileA the file containing the former version
	 * @param fileB the file containing the later version
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public Diff (File fileA, File fileB) throws XmlDocumentParseException, IOException, JDOMException
	{
		treeA = new TreeDocument (XmlTools.readDocument (fileA),
			fileA.toURI ());
		treeB = new TreeDocument (XmlTools.readDocument (fileB),
			fileB.toURI ());
	}
	
	
	/**
	 * Instantiates a new diff object in order to compare two documents stored in
	 * strings docA and docB.
	 *
	 * @param docA the former version
	 * @param docB the later version
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public Diff (String docA, String docB) throws XmlDocumentParseException, IOException, JDOMException
	{
		treeA = new TreeDocument (XmlTools.readDocument (docA), null);
		treeB = new TreeDocument (XmlTools.readDocument (docB), null);
	}
	
	
	/**
	 * Instantiates a new diff object in order to compare two tree documents.
	 * 
	 * @param treeA
	 *          the former version of the tree
	 * @param treeB
	 *          the later version of the tree
	 */
	public Diff (TreeDocument treeA, TreeDocument treeB)
	{
		this.treeA = treeA;
		this.treeB = treeB;
	}
	
	
	/**
	 * Gets the diff as a string, encoded in XML.
	 * 
	 * @return the diff
	 */
	public String getDiff ()
	{
		if (xmlPatch == null)
			producePatch ();
		return xmlPatch;
	}
	
	
	/**
	 * Gets the patch object.
	 * 
	 * @return the patch
	 */
	public Patch getPatch ()
	{
		if (patch == null)
			producePatch ();
		return patch;
	}
	
	
	/**
	 * Produce the patch (if it wasn't so far).
	 */
	private void producePatch ()
	{
		if (patchProducer == null)
		{
			patchProducer = new PatchProducer ();
			patchProducer.init (connections, treeA, treeB);
			xmlPatch = patchProducer.produce ();
			patch = patchProducer.getPatch ();
		}
		
	}
	
	
	/**
	 * Map both trees.
	 * 
	 * @return true, if successful
	 * @throws Exception
	 *           the exception
	 */
	public abstract boolean mapTrees () throws Exception;
	
	
	/**
	 * Returns the graph of the chemical reaction network providing an own graph
	 * translator.
	 * Might return null if not available.
	 * 
	 * @param gt
	 *          the graph translator
	 * @return the chemical reaction network or null if not available
	 * @throws Exception
	 *           the exception
	 * @deprecated As of 1.3.3 replaced by {@link #getReactionsGraph(de.unirostock.sems.bives.ds.graph.GraphTranslator)}
	 */
	@Deprecated
	public Object getCRNGraph (GraphTranslator gt) throws Exception
	{
		return getReactionsGraph (gt);
	}
	
	
	/**
	 * Returns the graph of the reaction network providing an own graph
	 * translator.
	 * Might return null if not available.
	 * 
	 * @param gt
	 *          the graph translator
	 * @return the reaction network or null if not available
	 * @throws Exception
	 *           the exception
	 */
	public abstract Object getReactionsGraph (GraphTranslator gt) throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph providing an own graph translator.
	 * Might return null if not available.
	 * 
	 * @param gt
	 *          the graph translator
	 * @return the hierarchy graph or null if not available
	 * @throws Exception
	 *           the exception
	 */
	public abstract Object getHierarchyGraph (GraphTranslator gt)
		throws Exception;
	
	
	/**
	 * Returns the graph of the chemical reaction network encoded in GraphML.
	 * Might return null if not available.
	 * 
	 * @return the chemical reaction network or null if not available
	 * @deprecated As of 1.3.3 replaced by {@link #getReactionsGraphML()}
	 */
	@Deprecated
	public String getCRNGraphML () throws Exception
	{
		return getReactionsGraphML ();
	}
	
	
	/**
	 * Returns the graph of the reaction network encoded in GraphML.
	 * Might return null if not available.
	 * 
	 * @return the reaction network or null if not available
	 */
	public abstract String getReactionsGraphML () throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph encoded in GraphML.
	 * Might return null if not available.
	 * 
	 * @return the hierarchy graph or null if not available
	 */
	public abstract String getHierarchyGraphML () throws Exception;
	
	
	/**
	 * Returns the graph of the chemical reaction network encoded in DOT language.
	 * Might return null if not available.
	 * 
	 * @return the chemical reaction network or null if not available
	 * @deprecated As of 1.3.3 replaced by {@link #getReactionsDotGraph()}
	 */
	@Deprecated
	public String getCRNDotGraph () throws Exception
	{
		return getReactionsDotGraph ();
	}
	
	
	/**
	 * Returns the graph of the reaction network encoded in DOT language.
	 * Might return null if not available.
	 * 
	 * @return the reaction network or null if not available
	 */
	public abstract String getReactionsDotGraph () throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph encoded in DOT language.
	 * Might return null if not available.
	 * 
	 * @return the hierarchy graph or null if not available
	 */
	public abstract String getHierarchyDotGraph () throws Exception;
	
	
	/**
	 * Returns the graph of the chemical reaction network encoded in JSON.
	 * Might return null if not available.
	 * 
	 * @return the chemical reaction network or null if not available
	 * @deprecated As of 1.3.3 replaced by {@link #getReactionsJsonGraph()}
	 */
	@Deprecated
	public  String getCRNJsonGraph () throws Exception
	{
		return getReactionsJsonGraph ();
	}
	
	
	/**
	 * Returns the graph of the reaction network encoded in JSON.
	 * Might return null if not available.
	 * 
	 * @return the reaction network or null if not available
	 */
	public abstract String getReactionsJsonGraph () throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph encoded in JSON.
	 * Might return null if not available.
	 * 
	 * @return the hierarchy graph or null if not available
	 */
	public abstract String getHierarchyJsonGraph () throws Exception;
	
	
	/**
	 * Returns the report providing an on markup processor.
	 * Might return null if not available.
	 * 
	 * @param ts
	 *          the ts
	 * @return the report or null if not available
	 */
	public abstract String getReport (Typesetting ts) throws Exception;
	
	
	/**
	 * Returns the report encoded in HTML.
	 * Might return null if not available.
	 * 
	 * @return the hTML report or null if not available
	 */
	public abstract String getHTMLReport () throws Exception;
	
	
	/**
	 * Returns the report encoded MarkDown.
	 * Might return null if not available.
	 * 
	 * @return the mark down report or null if not available
	 */
	public abstract String getMarkDownReport () throws Exception;
	
	
	/**
	 * Returns the report encoded in ReStructured text.
	 * Might return null if not available.
	 * 
	 * @return the ReStructured text report or null if not available
	 */
	public abstract String getReStructuredTextReport () throws Exception;
	
}

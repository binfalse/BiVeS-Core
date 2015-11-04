/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

import de.unirostock.sems.bives.algorithm.DiffAnnotator;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
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
	/**
	 * Are mappings of nodes with different ids allowed?
	 * 
	 * At the first glimpse that might sound stupid, but ids are generally arbitrary and say nothing valuable about the entities. they just need to be unique.. Look at the following two species:
	 * <pre>
	 * &lt;species id="s1" name="glucose" initial_concentration="0.1" compartment="cell" initialAmount="1" hasOnlySubstanceUnits="true" boundaryCondition="true" /&gt;
	 * &lt;species id="species1" name="glucose" initial_concentration="0.1" compartment="cell" initialAmount="1" hasOnlySubstanceUnits="true" boundaryCondition="true" /&gt;
	 * </pre>
	 * The id attribute might just be assigned by two different tools, everything else is the same. Should we map these entities?
	 * By default this is <code>true</code>.
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.DocumentNode#getAttributeDistance(DocumentNode, boolean, boolean, boolean)
	 */
	public static final boolean ALLOW_DIFFERENT_IDS = true;
	
	/**
	 * Do we care about names?
	 * Should we treat names differently? They often provide more information than other attributes.
	 * @see de.unirostock.sems.xmlutils.ds.DocumentNode#getAttributeDistance(DocumentNode, boolean, boolean, boolean)
	 */
	public static final boolean CARE_ABOUT_NAMES = true;
	
	/**
	 * Should we handle names very strictly?
	 * Go for this option if you're sure that you're names are very similar. 
	 * @see de.unirostock.sems.xmlutils.ds.DocumentNode#getAttributeDistance(DocumentNode, boolean, boolean, boolean)
	 */
	public static final boolean STRICTER_NAMES = false;
	
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
	
	/** The XML patch. */
	private String										xmlPatch;
	
	/** The XML patch including annotations. */
	private String										xmlPatchInclAnnotations;
	
	/** The annotation infrastructure to store knowledge about the changes. */
	private DiffAnnotator diffAnnotator;
	
	
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
	 * Instantiates a new diff object in order to compare two documents stored in
	 * files fileA and fileB.
	 *
	 * @param fileA the file containing the former version
	 * @param fileB the file containing the later version
	 * @param diffAnnotator the annotator for identified changes
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public Diff (File fileA, File fileB, DiffAnnotator diffAnnotator) throws XmlDocumentParseException, IOException, JDOMException
	{
		treeA = new TreeDocument (XmlTools.readDocument (fileA),
			fileA.toURI ());
		treeB = new TreeDocument (XmlTools.readDocument (fileB),
			fileB.toURI ());
		this.diffAnnotator = diffAnnotator;
	}
	
	
	/**
	 * Instantiates a new diff object in order to compare two documents stored in
	 * strings docA and docB.
	 *
	 * @param docA the former version
	 * @param docB the later version
	 * @param diffAnnotator the annotator for identified changes
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public Diff (String docA, String docB, DiffAnnotator diffAnnotator) throws XmlDocumentParseException, IOException, JDOMException
	{
		treeA = new TreeDocument (XmlTools.readDocument (docA), null);
		treeB = new TreeDocument (XmlTools.readDocument (docB), null);
		this.diffAnnotator = diffAnnotator;
	}
	
	
	/**
	 * Instantiates a new diff object in order to compare two tree documents.
	 *
	 * @param treeA the former version of the tree
	 * @param treeB the later version of the tree
	 * @param diffAnnotator the annotator for identified changes
	 */
	public Diff (TreeDocument treeA, TreeDocument treeB, DiffAnnotator diffAnnotator)
	{
		this.treeA = treeA;
		this.treeB = treeB;
		this.diffAnnotator = diffAnnotator;
	}
	
	
	/**
	 * Gets the diff as a string, encoded in XML.
	 * 
	 * @return the diff
	 */
	public String getDiff ()
	{
		return getDiff (true);
	}
	
	
	/**
	 * Gets the differences encoded in an XML string
	 *
	 * @param inclAnnotations include annotations in the XML patch
	 * @return the diff
	 */
	public String getDiff (boolean inclAnnotations)
	{
		if (patch == null)
			producePatch ();
		
		if (inclAnnotations && xmlPatchInclAnnotations == null)
			xmlPatchInclAnnotations = XmlTools.prettyPrintDocument (patch.getDocument (true));
		if (!inclAnnotations && xmlPatch == null)
			xmlPatch = XmlTools.prettyPrintDocument (patch.getDocument (false));
		
		return inclAnnotations ? xmlPatchInclAnnotations : xmlPatch;
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
	 * Produce the patch (if it's not there yet).
	 */
	private void producePatch ()
	{
		if (patchProducer == null)
		{
			patchProducer = new PatchProducer ();
			patchProducer.init (connections, treeA, treeB, diffAnnotator);
			xmlPatch = null;
			xmlPatchInclAnnotations = patchProducer.produce (true);
			patch = patchProducer.getPatch ();
		}
		
	}
	
	
	/**
	 * Map both trees.
	 * 
	 * This method let's you decide whether
	 * <ul>
	 * <li>mapped entities may have different ids,</li>
	 * <li>we specially treat name attributes,</li>
	 * <li>we handle names very strictly.</li>
	 * </ul>
	 * For default values see {@link de.unirostock.sems.bives.api.Diff#ALLOW_DIFFERENT_IDS}, {@link de.unirostock.sems.bives.api.Diff#CARE_ABOUT_NAMES}, and {@link de.unirostock.sems.bives.api.Diff#STRICTER_NAMES}.
	 *
	 * @param allowDifferentIds are mapped entities allowed to have different ids?
	 * @param careAboutNames should we care about names?
	 * @param stricterNames should we handle names very strictly?
	 * @return true, if successful mapped
	 * @throws Exception the exception
	 */
	public abstract boolean mapTrees (boolean allowDifferentIds, boolean careAboutNames, boolean stricterNames) throws Exception;
	
	
	/**
	 * Map both trees.
	 * 
	 * This method by default allows different ids, better cares about names (that means, different names are worse than different concentrations) but doesn't handle the names too strict. Compare {@link de.unirostock.sems.xmlutils.ds.DocumentNode#getAttributeDistance(DocumentNode, boolean, boolean, boolean)}.
	 * 
	 * @return true, if successful
	 * @throws Exception
	 *           the exception
	 */
	public boolean mapTrees () throws Exception
	{
		return mapTrees (ALLOW_DIFFERENT_IDS, CARE_ABOUT_NAMES, STRICTER_NAMES);
	}
	
	
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
	 * @throws Exception the exception
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
	 * @throws Exception the exception
	 */
	public abstract String getReactionsGraphML () throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph encoded in GraphML.
	 * Might return null if not available.
	 *
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHierarchyGraphML () throws Exception;
	
	
	/**
	 * Returns the graph of the chemical reaction network encoded in DOT language.
	 * Might return null if not available.
	 *
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
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
	 * @throws Exception the exception
	 */
	public abstract String getReactionsDotGraph () throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph encoded in DOT language.
	 * Might return null if not available.
	 * 
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHierarchyDotGraph () throws Exception;
	
	
	/**
	 * Returns the graph of the chemical reaction network encoded in JSON.
	 * Might return null if not available.
	 * 
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
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
	 * @throws Exception the exception
	 */
	public abstract String getReactionsJsonGraph () throws Exception;
	
	
	/**
	 * Returns the component's hierarchy graph encoded in JSON.
	 * Might return null if not available.
	 * 
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHierarchyJsonGraph () throws Exception;
	
	
	/**
	 * Returns the report providing an on markup processor.
	 * Might return null if not available.
	 * 
	 * @param ts
	 *          the ts
	 * @return the report or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getReport (Typesetting ts) throws Exception;
	
	
	/**
	 * Returns the report encoded in HTML.
	 * Might return null if not available.
	 * 
	 * @return the hTML report or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHTMLReport () throws Exception;
	
	
	/**
	 * Returns the report encoded MarkDown.
	 * Might return null if not available.
	 * 
	 * @return the mark down report or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getMarkDownReport () throws Exception;
	
	
	/**
	 * Returns the report encoded in ReStructured text.
	 * Might return null if not available.
	 * 
	 * @return the ReStructured text report or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getReStructuredTextReport () throws Exception;
	
}

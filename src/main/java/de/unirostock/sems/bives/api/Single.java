/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class Single, providing an API to extract some information about single XML files. All methods might return null if not available in a certain case.
 *
 * @author Martin Scharm
 */
public abstract class Single
{
	
	/** The tree. */
	protected TreeDocument tree;

	/**
	 * Instantiates a new single object reading the document from a file.
	 *
	 * @param file the file containing the document
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public Single (File file) throws XmlDocumentParseException, IOException, JDOMException
	{
		tree = new TreeDocument (XmlTools.readDocument (file), file.toURI ());
	}

	/**
	 * Instantiates a new single object reading the document from a string.
	 *
	 * @param str the string containing the document
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public Single (String str) throws XmlDocumentParseException, IOException, JDOMException
	{
		tree = new TreeDocument (XmlTools.readDocument (str), null);
	}

	/**
	 * Instantiates a new single object.
	 *
	 * @param tree the tree
	 */
	public Single (TreeDocument tree)
	{
		this.tree = tree;
	}
	
	/**
	 * Flatten the document.
	 *
	 * @return the flattened document
	 * @throws Exception the exception
	 */
	public abstract String flatten () throws Exception;
	
	/**
	 * Returns the graph of the chemical reaction network providing an own graph translator.
	 * Might return null if not available.
	 * 
	 * @param gt the graph translator
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
	 * @deprecated As of 1.3.3 replaced by {@link #getReactionsGraph(de.unirostock.sems.bives.ds.graph.GraphTranslator)}
	 */
	@Deprecated
	public Object getCRNGraph (GraphTranslator gt) throws Exception
	{
		return getReactionsGraph (gt);
	}
	
	/**
	 * Returns the graph of the reaction network providing an own graph translator.
	 * Might return null if not available.
	 * 
	 * @param gt the graph translator
	 * @return the reaction network or null if not available
	 * @throws Exception the exception
	 */
	public abstract Object getReactionsGraph (GraphTranslator gt) throws Exception;
	
	/**
	 * Returns the component's hierarchy graph providing an own graph translator.
	 * Might return null if not available.
	 *
	 * @param gt the graph translator
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract Object getHierarchyGraph (GraphTranslator gt) throws Exception;

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
	public String getCRNJsonGraph () throws Exception
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
	
}

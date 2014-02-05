/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class Single, providing an API to extract some information about single XML files.
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
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Single (File file) throws ParserConfigurationException, XmlDocumentParseException, FileNotFoundException, SAXException, IOException
	{
		tree = new TreeDocument (XmlTools.readDocument (file), file.toURI ());
	}

	/**
	 * Instantiates a new single object reading the document from a string.
	 *
	 * @param str the string containing the document
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Single (String str) throws ParserConfigurationException, XmlDocumentParseException, FileNotFoundException, SAXException, IOException
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
	 * Returns the graph of the chemical reaction network providing an own graph translator.
	 *
	 * @param gt the graph translator
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
	 */
	public abstract Object getCRNGraph (GraphTranslator gt) throws Exception;
	
	/**
	 * Returns the component's hierarchy graph providing an own graph translator.
	 *
	 * @param gt the graph translator
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract Object getHierarchyGraph (GraphTranslator gt) throws Exception;

	/**
	 * Returns the graph of the chemical reaction network encoded in GraphML.
	 *
	 * @return the chemical reaction network or null if not available
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public abstract String getCRNGraphML () throws ParserConfigurationException;

	/**
	 * Returns the component's hierarchy graph encoded in GraphML.
	 *
	 * @return the hierarchy graph or null if not available
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public abstract String getHierarchyGraphML () throws ParserConfigurationException;

	/**
	 * Returns the graph of the chemical reaction network encoded in DOT language.
	 *
	 * @return the chemical reaction network or null if not available
	 */
	public abstract String getCRNDotGraph ();

	/**
	 * Returns the component's hierarchy graph encoded in DOT language.
	 *
	 * @return the hierarchy graph or null if not available
	 */
	public abstract String getHierarchyDotGraph ();

	/**
	 * Returns the graph of the chemical reaction network encoded in JSON.
	 *
	 * @return the chemical reaction network or null if not available
	 */
	public abstract String getCRNJsonGraph ();

	/**
	 * Returns the component's hierarchy graph encoded in JSON.
	 *
	 * @return the hierarchy graph or null if not available
	 */
	public abstract String getHierarchyJsonGraph ();
	
}

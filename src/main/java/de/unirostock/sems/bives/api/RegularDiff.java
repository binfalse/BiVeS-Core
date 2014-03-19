/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;



/**
 * The Class RegularDiff to compare two regular XML documents.
 * 
 * @author Martin Scharm
 */
public class RegularDiff
	extends Diff
{
	
	/**
	 * Instantiates a new regular diff object in order to compare two documents
	 * stored in files fileA and fileB.
	 *
	 * @param fileA the file containing the former version
	 * @param fileB the file containing the later version
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public RegularDiff (File fileA, File fileB) throws XmlDocumentParseException, IOException, JDOMException
	{
		super (fileA, fileB);
	}
	
	
	/**
	 * Instantiates a new regular diff object in order to compare two documents
	 * stored in strings docA and docB.
	 *
	 * @param docA the former version
	 * @param docB the later version
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public RegularDiff (String docA, String docB) throws XmlDocumentParseException, IOException, JDOMException
	{
		super (docA, docB);
	}
	
	
	/**
	 * Instantiates a new regular diff object in order to compare two tree
	 * documents.
	 * 
	 * @param treeA
	 *          the former version of the tree
	 * @param treeB
	 *          the later version of the tree
	 */
	public RegularDiff (TreeDocument treeA, TreeDocument treeB)
	{
		super (treeA, treeB);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#mapTrees()
	 */
	@Override
	public boolean mapTrees () throws BivesConnectionException
	{
		XyDiffConnector con = new XyDiffConnector (treeA, treeB);
		con.findConnections ();
		connections = con.getConnections ();
		
		treeA.getRoot ().resetModifications ();
		treeA.getRoot ().evaluate (connections);
		
		treeB.getRoot ().resetModifications ();
		treeB.getRoot ().evaluate (connections);
		
		return true;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.api.Diff#getCRNGraph(de.unirostock.sems.bives.
	 * ds.graph.GraphTranslator)
	 */
	@Override
	public Object getCRNGraph (GraphTranslator gt) throws Exception
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getGraphML()
	 */
	@Override
	public String getCRNGraphML ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getDotGraph()
	 */
	@Override
	public String getCRNDotGraph ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getHTMLReport()
	 */
	@Override
	public String getHTMLReport ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getMarkDownReport()
	 */
	@Override
	public String getMarkDownReport ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getReStructuredTextReport()
	 */
	@Override
	public String getReStructuredTextReport ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.api.Diff#getHierarchyGraph(de.unirostock.sems.
	 * bives.ds.graph.GraphTranslator)
	 */
	@Override
	public String getHierarchyGraph (GraphTranslator gt)
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getHierarchyGraphML()
	 */
	@Override
	public String getHierarchyGraphML ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getHierarchyDotGraph()
	 */
	@Override
	public String getHierarchyDotGraph ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getCRNJsonGraph()
	 */
	@Override
	public String getCRNJsonGraph ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.bives.api.Diff#getHierarchyJsonGraph()
	 */
	@Override
	public String getHierarchyJsonGraph ()
	{
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.bives.api.Diff#getReport(de.unirostock.sems.bives.markup
	 * .Typesetting)
	 */
	@Override
	public String getReport (Typesetting ts)
	{
		return null;
	}
	
}

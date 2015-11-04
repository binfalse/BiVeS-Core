/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.DiffAnnotator;
import de.unirostock.sems.bives.algorithm.NodeConnection;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.algorithm.general.DefaultDiffAnnotator;
import de.unirostock.sems.bives.tools.BivesTools;
import de.unirostock.sems.comodi.Change;
import de.unirostock.sems.comodi.ChangeFactory;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class Patch storing all operations necessary to transfer one document into another.
 *
 * @author Martin Scharm
 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta">delta documentation</a>
 */
public class Patch
{
	
	/** The default file name of the path. This name will be used in the annotations to refer to elements in the path file. */
	public static URI PATCH_FILE_NAME = URI.create ("file://bives-differences.patch");
	
	/** The actual current patch file name. */
	private URI patchFileName;
	
	/** The change annotation factory. */
	private ChangeFactory changeAnnotationFactory;
	
	/** The annotator for differences. */
	private DiffAnnotator diffAnnotator;
	
	/** The latest used id in this document. */
	private int id;
	
	/** The XML document that will contain all operations. */
	private Document xmlDoc;
	
	/** The nodes rooting insert operations. */
	private Element insert;
	/** The nodes rooting delete operations. */
	private Element delete;
	/** The nodes rooting update operations. */
	private Element update;
	/** The nodes rooting move operations. */
	private Element move;
	//, copy, glue;
	
	/** The fullDiff flag indicating whether this diff is a full diff. */
	private boolean fullDiff;
	
	
	/**
	 * Instantiates a new patch specifying the file name of the resulting patch and an annotator to annotate the differences.
	 * This will result in a patch of type fullDiff.
	 * 
	 * @param patchFileName the file name of the final patch
	 * @param diffAnnotator the annotator for detected differences
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (URI patchFileName, DiffAnnotator diffAnnotator)
	{
		fullDiff = true;
		init (patchFileName, diffAnnotator);
	}
	
	/**
	 * Instantiates a new patch specifying. an annotator to annotate the differences.
	 * This constructor assumes that the final patch will be called {@value #PATCH_FILE_NAME}.
	 * This will result in a patch of type fullDiff.
	 *
	 * @param diffAnnotator the annotator for detected differences
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (DiffAnnotator diffAnnotator)
	{
		fullDiff = true;
		init (PATCH_FILE_NAME, diffAnnotator);
	}
	
	/**
	 * Instantiates a new patch specifying the fullDiff flag, the file name of the resulting patch, and an annotator to annotate the differences.
	 * If the fullDiff flag is set to false only a partially delta will be generated.
	 * Thus, the delta will briefly describe the modifications, but cannot be used to transform one version of a document into the other.
	 * Set it to true to obtain a full delta.
	 *
	 * @param fullDiff the fullDiff flag
	 * @param patchFileName the file name of the final patch
	 * @param diffAnnotator the annotator for detected differences
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (boolean fullDiff, URI patchFileName, DiffAnnotator diffAnnotator)
	{
		this.fullDiff = fullDiff;
		init (patchFileName, diffAnnotator);
	}
	
	/**
	 * Instantiates a new patch specifying the fullDiff flag and an annotator to annotate the differences.
	 * If the fullDiff flag is set to false only a partial delta will be generated.
	 * Thus, the delta will briefly describe the modifications, but cannot be used to transform one version of a document into the other.
	 * Set it to true to obtain a full delta.
	 * This constructor assumes that the final patch will be called {@value #PATCH_FILE_NAME}.
	 *
	 * @param fullDiff the fullDiff flag
	 * @param diffAnnotator the annotator for detected differences
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (boolean fullDiff, DiffAnnotator diffAnnotator)
	{
		this.fullDiff = fullDiff;
		init (PATCH_FILE_NAME, diffAnnotator);
	}
	
	
	/**
	 * Instantiates a new patch specifying the file name of the resulting patch.
	 * This constructor assumes that the final patch will be called {@value #PATCH_FILE_NAME}.
	 * To annotate the differences a new instance of the {@link de.unirostock.sems.bives.algorithm.general.DefaultDiffAnnotator DefaultDiffAnnotator} will be created.
	 * This will result in a patch of type fullDiff.
	 * 
	 * @param patchFileName the file name of the final patch
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (URI patchFileName)
	{
		fullDiff = true;
		init (patchFileName, new DefaultDiffAnnotator ());
	}
	
	/**
	 * Instantiates a new patch.
	 * This constructor assumes that the final patch will be called {@value #PATCH_FILE_NAME}.
	 * To annotate the differences a new instance of the {@link de.unirostock.sems.bives.algorithm.general.DefaultDiffAnnotator DefaultDiffAnnotator} will be created.
	 * This will result in a patch of type fullDiff.
	 * 
	 * 
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 * 
	 */
	public Patch ()
	{
		fullDiff = true;
		init (PATCH_FILE_NAME, new DefaultDiffAnnotator ());
	}
	
	/**
	 * Instantiates a new patch specifying the fullDiff flag and the file name of the resulting patch.
	 * If the fullDiff flag is set to false only a partial delta will be generated.
	 * Thus, the delta will briefly describe the modifications, but cannot be used to transform one version of a document into the other.
	 * Set it to true to obtain a full delta.
	 * To annotate the differences a new instance of the {@link de.unirostock.sems.bives.algorithm.general.DefaultDiffAnnotator DefaultDiffAnnotator} will be created.
	 * 
	 *
	 * @param fullDiff the fullDiff flag
	 * @param patchFileName the file name of the final patch
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (boolean fullDiff, URI patchFileName)
	{
		this.fullDiff = fullDiff;
		init (patchFileName, new DefaultDiffAnnotator ());
	}
	
	/**
	 * Instantiates a new patch specifying the fullDiff flag.
	 * If the fullDiff flag is set to false only a partial delta will be generated.
	 * Thus, the delta will briefly describe the modifications, but cannot be used to transform one version of a document into the other.
	 * Set it to true to obtain a full delta.
	 * This constructor assumes that the final patch will be called {@value #PATCH_FILE_NAME}.
	 * To annotate the differences a new instance of the {@link de.unirostock.sems.bives.algorithm.general.DefaultDiffAnnotator DefaultDiffAnnotator} will be created.
	 * 
	 * 
	 * @param fullDiff the fullDiff flag
	 * @see <a href="https://sems.uni-rostock.de/trac/bives-core/wiki/BivesDelta#fulldiff">fullDiff documentation</a>
	 */
	public Patch (boolean fullDiff)
	{
		this.fullDiff = fullDiff;
		init (PATCH_FILE_NAME, new DefaultDiffAnnotator ());
	}
	
	/**
	 * Gets the number of stored move operations.
	 *
	 * @return the number of moves
	 */
	public int getNumMoves ()
	{
		return move.getChildren ().size ();
	}
	
	/**
	 * Gets the number of stored update operations.
	 *
	 * @return the number of updates
	 */
	public int getNumUpdates ()
	{
		return update.getChildren ().size ();
	}
	
	/**
	 * Gets the number of stored delete operations.
	 *
	 * @return the number of deletes
	 */
	public int getNumDeletes ()
	{
		return delete.getChildren ().size ();
	}
	
	/**
	 * Gets the number of stored insert operations.
	 *
	 * @return the number of inserts
	 */
	public int getNumInserts ()
	{
		return insert.getChildren ().size ();
	}
	
	/**
	 * Gets the deletes.
	 *
	 * @return the deletes
	 */
	public Element getDeletes ()
	{
		return delete;
	}
	
	/**
	 * Gets the inserts.
	 *
	 * @return the inserts
	 */
	public Element getInserts ()
	{
		return insert;
	}
	
	/**
	 * Gets the updates.
	 *
	 * @return the updates
	 */
	public Element getUpdates ()
	{
		return update;
	}
	
	/**
	 * Gets the moves.
	 *
	 * @return the moves
	 */
	public Element getMoves ()
	{
		return move;
	}
	
	/**
	 * Gets the number of node changes.
	 * That is the number of differences in this patch that affect XML nodes.
	 *
	 * @return the number of node changes
	 */
	public int getNumNodeChanges ()
	{
		return insert.getChildren ("node").size ()
			+ delete.getChildren ("node").size ()
			+ update.getChildren ("node").size ()
			+ move.getChildren ("node").size ();
	}

	
	/**
	 * Gets the number of text changes.
	 * That is the number of differences in this patch that affect XML text nodes.
	 *
	 * @return the number of text changes
	 */
	public int getNumTextChanges ()
	{
		return insert.getChildren ("text").size ()
			+ delete.getChildren ("text").size ()
			+ update.getChildren ("text").size ()
			+ move.getChildren ("text").size ();
	}

	
	/**
	 * Gets the number of attribute changes.
	 * That is the number of differences in this patch that affect XML attributes.
	 *
	 * @return the number of attribute changes
	 */
	public int getNumAttributeChanges ()
	{
		return insert.getChildren ("attribute").size ()
			+ delete.getChildren ("attribute").size ()
			+ update.getChildren ("attribute").size ()
			+ move.getChildren ("attribute").size ();
	}
	
	/**
	 * Gets the document containing all changes.
	 * If <code>inclAnnotations</code> is set to true the patch will have an embedded RDF subtree with annotations of the differences.
	 *
	 * @param inclAnnotations should the annotations be embedded into the patch
	 * @return the document
	 */
	public Document getDocument (boolean inclAnnotations)
	{
		// add root element <bives type="fullDiff">
		Element rootElement = new Element ("bives");
		xmlDoc = new Document (rootElement);
		
		rootElement.setAttribute ("type", "fullDiff");// TODO: implement shortDiff
		
		rootElement.addContent (new Comment (BivesTools.getBivesVersion ()));
		
		rootElement.addContent (update.clone ());
		rootElement.addContent (delete.clone ());
		rootElement.addContent (insert.clone ());
		rootElement.addContent (move.clone ());
		
		if (inclAnnotations && changeAnnotationFactory.getNumStatements () > 0)
		{
			String xml = changeAnnotationFactory.getRdfXml ();
			try
			{
				rootElement.addContent (XmlTools.readDocument (xml.replaceAll (patchFileName.toString (), "")).getRootElement ().detach ());
			}
			catch (Exception e)
			{
				LOGGER.error (e, "wasn't able to read rdf-annotations subtree to add it to the diff");
			}
		}
		
		return xmlDoc;
	}
	
	
	/**
	 * Gets the number of annotations for the differences in this patch.
	 *
	 * @return the number annotations
	 */
	public int getNumAnnotations ()
	{
		return changeAnnotationFactory.getNumStatements ();
	}
	
	/**
	 * Gets the document containing all changes.
	 *
	 * @return the document
	 */
	public Document getDocument ()
	{
		return getDocument (true);
	}
	
	/**
	 * Gets the annotation document encoded in XML.
	 *
	 * @return the annotation document
	 */
	public String getAnnotationDocumentXml ()
	{
		return changeAnnotationFactory.getRdfXml ();
	}
	
	/**
	 * Gets the actual annotations.
	 *
	 * @return the annotations
	 */
	public ChangeFactory getAnnotations ()
	{
		return changeAnnotationFactory;
	}
	
	/**
	 * Initializes the patch.
	 * Creates the the nodes which will root the different kinds of operations.
	 * 
	 * @param patchFileName the file name of the final patch
	 * @param diffAnnotator the annotator to be used to annotate the differences
	 */
	private void init (URI patchFileName, DiffAnnotator diffAnnotator)
	{
		this.patchFileName = patchFileName;
		this.diffAnnotator = diffAnnotator;
		LOGGER.info ("initializing patch w/ fullDiff = ", fullDiff);
		id = 0;
		
		// create nodes for inserts/updates/moves tec
		update = new Element("update");
		delete = new Element("delete");
		insert = new Element("insert");
		move = new Element("move");
		
		changeAnnotationFactory = new ChangeFactory (this.patchFileName);
		
		LOGGER.info ("initialized patch");
	}
	
	/**
	 * Creates an attribute element. (not an attribute, but a whole node defining an operation on an attribute)
	 *
	 * @param nodeId the node id
	 * @param oldPath the old path of the node hosting this attribute, set null to omit
	 * @param newPath the new path of the node hosting this attribute, set null to omit
	 * @param name the name of the attribute
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 * @return the created node
	 */
	private Element createAttributeElement (int nodeId, String oldPath, String newPath, String name, String oldValue, String newValue, int chainId)
	{
		LOGGER.info ("create attribute element for ", oldPath, " -> ", newPath);
		Element attribute = new Element("attribute");

		attribute.setAttribute ("name", name);
		attribute.setAttribute ("id", nodeId + "");
		
		if (chainId > 0)
			attribute.setAttribute ("triggeredBy", chainId + "");
		
		if (oldValue != null)
			attribute.setAttribute ("oldValue", oldValue);
		
		if (newValue != null)
			attribute.setAttribute ("newValue", newValue);

		if (oldPath != null)
			attribute.setAttribute ("oldPath", oldPath);
		
		if (newPath != null)
			attribute.setAttribute ("newPath", newPath);
		
		return attribute;
	}
	
	/**
	 * Creates a node element: A node in the XML delta defining an operation on a node.
	 *
	 * @param nodeId the node id
	 * @param oldParent the XPath to the old parent, set null to omit
	 * @param newParent the XPath to the new parent, set null to omit
	 * @param oldPath the old path to the node, set null to omit
	 * @param newPath the new path to the node, set null to omit
	 * @param oldChildNo the old child no, set < 1 to omit
	 * @param newChildNo the new child no, set < 1 to omit
	 * @param oldTag the old tag
	 * @param newTag the new tag
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 * @return the element
	 */
	private Element createNodeElement (int nodeId, String oldParent, String newParent, String oldPath, String newPath, int oldChildNo, int newChildNo, String oldTag, String newTag, int chainId)
	{
		LOGGER.info ("create node element for ", oldPath, " -> ", newPath);
		Element node = new Element("node");

		node.setAttribute ("id", "" + nodeId);
		
		if (chainId > 0)
			node.setAttribute ("triggeredBy", chainId + "");

		if (oldParent != null)
			node.setAttribute ("oldParent", oldParent);

		if (newParent != null)
			node.setAttribute ("newParent", newParent);

		if (oldChildNo > 0)
			node.setAttribute ("oldChildNo", "" + oldChildNo);
		
		if (newChildNo > 0)
			node.setAttribute ("newChildNo", newChildNo + "");
		
		if (oldPath != null)
			node.setAttribute ("oldPath", oldPath);
		
		if (newPath != null)
			node.setAttribute ("newPath", newPath);
		
		if (oldTag != null)
			node.setAttribute ("oldTag", oldTag);
		
		if (newTag != null)
			node.setAttribute ("newTag", newTag);
		
		return node;
	}
	
	/**
	 * Creates a text element.
	 *
	 * @param nodeId the node id
	 * @param oldParent the XPath to the old parent, set null to omit
	 * @param newParent the XPath to the new parent, set null to omit
	 * @param oldPath the old path to the node, set null to omit
	 * @param newPath the new path to the node, set null to omit
	 * @param oldChildNo the old child no, set < 1 to omit
	 * @param newChildNo the new child no, set < 1 to omit
	 * @param oldText the old text content
	 * @param newText the new text content
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 * @return the element
	 */
	private Element createTextElement (int nodeId, String oldParent, String newParent, String oldPath, String newPath, int oldChildNo, int newChildNo, String oldText, String newText, int chainId)
	{
		LOGGER.info ("create text element for ", oldPath, " -> ", newPath);
		Element node = new Element("text");
		
		node.setAttribute ("id", nodeId + "");
		
		if (chainId > 0)
			node.setAttribute ("triggeredBy", chainId + "");

		if (oldParent != null)
			node.setAttribute ("oldParent", oldParent);

		if (newParent != null)
			node.setAttribute ("newParent", newParent);

		if (oldChildNo > 0)
			node.setAttribute ("oldChildNo", "" + oldChildNo);
		
		if (newChildNo > 0)
			node.setAttribute ("newChildNo", "" + newChildNo);
		
		if (oldPath != null)
			node.setAttribute ("oldPath", oldPath);
		
		if (newPath != null)
			node.setAttribute ("newPath", newPath);

		if (fullDiff)
		{
			if (oldText != null)
				node.setAttribute ("oldText", oldText);
			
			if (newText != null)
				node.setAttribute ("newText", newText);
		}
		
		return node;
		
	}

	/**
	 * Mark a whole subtree as deleted.
	 *
	 * @param toDelete the node rooting the subtree to delete
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	public void deleteSubtree (TreeNode toDelete, int chainId)
	{
		switch (toDelete.getType ())
		{
			case TreeNode.DOC_NODE:
			{
				DocumentNode dnode = (DocumentNode) toDelete;
				int parentId = deleteNode (dnode, chainId);
				for (TreeNode tn : dnode.getChildren ())
					deleteSubtree (tn, parentId);
				break;
			}
			case TreeNode.TEXT_NODE:
				deleteNode ((TextNode) toDelete, chainId);
				break;
			default:
			{
				LOGGER.error ("unsupported tree node type for deletion...");
				throw new UnsupportedOperationException ("unsupported tree node type...");
			}
		}
	}

	/**
	 * Delete a single node.
	 *
	 * @param toDelete the node to delete
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	public void deleteNode (TreeNode toDelete, int chainId)
	{
		switch (toDelete.getType ())
		{
			case TreeNode.DOC_NODE:
				deleteNode ((DocumentNode) toDelete, chainId);
				break;
			case TreeNode.TEXT_NODE:
				deleteNode ((TextNode) toDelete, chainId);
				break;
			default:
			{
				LOGGER.error ("unsupported tree node type for deletion...");
				throw new UnsupportedOperationException ("unsupported tree node type...");
			}
		}
	}
	
	/**
	 * Delete a single document node.
	 *
	 * @param toDelete the node to delete
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 * @return the id of the delete-operation-node in the XML delta
	 */
	private int deleteNode (DocumentNode toDelete, int chainId)
	{
		LOGGER.info ("deleting node ", toDelete.getXPath ());
		int nodeId = ++id;
		
		Element diffElement = createNodeElement (nodeId, getParentXpath (toDelete), null, toDelete.getXPath (), null, getChildNo (toDelete), -1, toDelete.getTagName (), null, chainId);
		delete.addContent (diffElement);
		diffAnnotator.annotateDeletion (toDelete, diffElement, changeAnnotationFactory);
		
		if (!fullDiff)
			return nodeId;
		LOGGER.info ("checking attributes for full diff");
		Set<String> attr = toDelete.getAttributes ();
		for (String a: attr)
			deleteAttribute (toDelete, a, nodeId);
		return nodeId;
	}

	/**
	 * Delete an attribute.
	 *
	 * @param node the node carrying the attribute
	 * @param attribute the attribute to delete
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	private void deleteAttribute (DocumentNode node, String attribute, int chainId)
	{
		LOGGER.info ("deleting attribute ", attribute, " of ", node.getXPath ());
		
		Element diffElement = createAttributeElement (++id, node.getXPath (), null, attribute, node.getAttributeValue (attribute), null, chainId);
		delete.addContent (diffElement);
		diffAnnotator.annotateDeletion (node, diffElement, changeAnnotationFactory);
	}
	
	/**
	 * Delete a single text node.
	 *
	 * @param toDelete the node to delete
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	private void deleteNode (TextNode toDelete, int chainId)
	{
		LOGGER.info ("deleting text of ", toDelete.getXPath ());
		
		Element diffElement = createTextElement (++id, getParentXpath (toDelete), null, toDelete.getXPath (), null, getChildNo (toDelete), -1, toDelete.getText (), null, chainId); 
		delete.addContent (diffElement);
		diffAnnotator.annotateDeletion (toDelete, diffElement, changeAnnotationFactory);
	}
	
	/**
	 * Mark a whole subtree as inserted.
	 *
	 * @param toInsert the node rooting the subtree to insert
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	public void insertSubtree (TreeNode toInsert, int chainId)
	{
		switch (toInsert.getType ())
		{
			case TreeNode.DOC_NODE:
				DocumentNode dnode = (DocumentNode) toInsert;
				int parentId = insertNode (dnode, chainId);
				for (TreeNode tn : dnode.getChildren ())
					insertSubtree (tn, parentId);
				break;
			case TreeNode.TEXT_NODE:
				insertNode ((TextNode) toInsert, chainId);
				break;
			default:
			{
				LOGGER.error ("unsupported tree node type for insertion...");
				throw new UnsupportedOperationException ("unsupported tree node type...");
			}
		}
	}
	
	/**
	 * Insert a single node.
	 *
	 * @param toInsert the node to insert
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	public void insertNode (TreeNode toInsert, int chainId)
	{
		switch (toInsert.getType ())
		{
			case TreeNode.DOC_NODE:
				insertNode ((DocumentNode) toInsert, chainId);
				break;
			case TreeNode.TEXT_NODE:
				insertNode ((TextNode) toInsert, chainId);
				break;
			default:
			{
				LOGGER.error ("unsupported tree node type for insertion...");
				throw new UnsupportedOperationException ("unsupported tree node type...");
			}
		}
	}
	
	/**
	 * Insert a single node.
	 *
	 * @param toInsert the document node to insert
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 * @return the id of the insert-operation-node in the XML delta
	 */
	public int insertNode (DocumentNode toInsert, int chainId)
	{
		LOGGER.info ("inserting node ", toInsert.getXPath ());
		int nodeId = ++id;
		
		Element diffElement = createNodeElement (nodeId, null, getParentXpath (toInsert), null, toInsert.getXPath (), -1, getChildNo (toInsert), null, toInsert.getTagName (), chainId);
		insert.addContent (diffElement);
		diffAnnotator.annotateInsertion (toInsert, diffElement, changeAnnotationFactory);
		
		if (!fullDiff)
			return nodeId;
		LOGGER.info ("checking attributes for full diff");
		Set<String> attr = toInsert.getAttributes ();
		for (String a: attr)
			insertAttribute (toInsert, a, nodeId);
		return nodeId;
	}
	
	/**
	 * Insert an attribute.
	 *
	 * @param node the node carrying the attribute to insert
	 * @param attribute the attribute to insert
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	private void insertAttribute (DocumentNode node, String attribute, int chainId)
	{
		LOGGER.info ("inserting attribute ", attribute, " of ", node.getXPath ());
		
		Element diffElement = createAttributeElement (++id, null, node.getXPath (), attribute, null, node.getAttributeValue (attribute), chainId);
		insert.addContent (diffElement);
		diffAnnotator.annotateInsertion (node, diffElement, changeAnnotationFactory);
	}
	
	/**
	 * Insert a single node.
	 *
	 * @param toInsert the text node to insert
	 * @param chainId the chain id: id of the trigger, if this modification was triggered, or <1 otherwise
	 */
	private void insertNode (TextNode toInsert, int chainId)
	{
		LOGGER.info ("inserting text of ", toInsert.getXPath ());
		
		Element diffElement = createTextElement (++id, null, getParentXpath (toInsert), null, toInsert.getXPath (), -1, getChildNo (toInsert), null, toInsert.getText (), chainId);
		insert.addContent (diffElement);
		diffAnnotator.annotateInsertion (toInsert, diffElement, changeAnnotationFactory);
	}
	
	/**
	 * Update a node.
	 *
	 * @param c the connection between the old version of the node and the new version of it.
	 * @param conMgmt the connection manager
	 */
	public void updateNode (NodeConnection c, SimpleConnectionManager conMgmt)
	{
		TreeNode a = c.getTreeA ();
		TreeNode b = c.getTreeB ();
		LOGGER.info ("updating node ", a.getXPath (), " to become ", b.getXPath ());
		
		if (a.getType () != b.getType ())
		{
			LOGGER.error ("node types differ, not supported");
			throw new UnsupportedOperationException ("cannot update nodes of different type...");
		}
		
		if (((a.getModification () | b.getModification ()) & (TreeNode.GLUED | TreeNode.COPIED)) != 0)
			throw new UnsupportedOperationException ("copy & glue not supported yet...");
		
		boolean moveThem = (a.getModification () & (TreeNode.MOVED | TreeNode.SWAPPEDKID)) != 0;
		if (moveThem && LOGGER.isInfoEnabled ())
			LOGGER.info ("will move them: par: ", conMgmt.parentsConnected (c), " chNoA: ", getChildNo (a), " chNoB: ", getChildNo (b));
		
		Change change = null;
		
		// text node
		if (a.getType () == TreeNode.TEXT_NODE)
		{
			if ((a.getModification () & TreeNode.MODIFIED) != 0)
			{
				LOGGER.info ("text differs");
				Element e = createTextElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), ((TextNode) a).getText (), ((TextNode) b).getText (), -1);
				change = diffAnnotator.annotateUpdateText ((TextNode) a, (TextNode) b, e, changeAnnotationFactory);
				
				if (moveThem)
				{
					move.addContent (e);
					change = diffAnnotator.annotateMove (a, b, e, changeAnnotationFactory, conMgmt.parentsConnected (c));
				}
				else
				{
					update.addContent (e);
				}
			}
			else if (moveThem)
			{
				LOGGER.info ("equal text");
				Element diffElement = createTextElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null, -1);
				move.addContent (diffElement);
				change = diffAnnotator.annotateMove (a, b, diffElement, changeAnnotationFactory, conMgmt.parentsConnected (c));
			}
			if (change != null)
			{
				for (Map.Entry<Property, RDFNode> annotation : c.getAnnotations ())
					change.addAnnotation (annotation.getKey (), annotation.getValue ());
			}
			return;
		}

		// xml node
		
		DocumentNode dA = (DocumentNode) a;
		DocumentNode dB = (DocumentNode) b;
		
		if ((a.getModification () & TreeNode.MODIFIED) == 0)
		{
			// unmodified -> just move
			if (moveThem)
			{
				LOGGER.info ("nodes unmodified");
				
				Element diffElement = createNodeElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null, -1);
				move.addContent (diffElement);
				change = diffAnnotator.annotateMove (a, b, diffElement, changeAnnotationFactory, conMgmt.parentsConnected (c));
			}
		}
		else
		{
			// matching label? -> update more extreme than move...
			if (!dA.getTagName ().equals (dB.getTagName ()))
			{
				LOGGER.info ("label of nodes differ -> updating");
				
				Element diffElement = createNodeElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), dA.getTagName (), dB.getTagName (), -1);
				update.addContent (diffElement);
			}
			else if (moveThem)
			{
				LOGGER.info ("label of nodes do not differ -> moving");
				
				Element diffElement = createNodeElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null, -1);
				move.addContent (diffElement);
				change = diffAnnotator.annotateMove (a, b, diffElement, changeAnnotationFactory, conMgmt.parentsConnected (c));
			}
			
			if (fullDiff)
			{
				// arguments
				LOGGER.info ("checking attributes for full diff");

				Set<String> allAttr = new HashSet<String> ();
				allAttr.addAll (dA.getAttributes ());
				allAttr.addAll (dB.getAttributes ());
				for (String attr : allAttr)
				{
					String aA = dA.getAttributeValue (attr), bA = dB.getAttributeValue (attr);
					if (aA == null)
						insertAttribute (dB, attr, -1);
					else if (bA == null)
						deleteAttribute (dA, attr, -1);
					else if (!aA.equals (bA))
					{
						Element diffElement = createAttributeElement (++id, a.getXPath (), b.getXPath (), attr, aA, bA, -1);
						update.addContent (diffElement);
						change = diffAnnotator.annotateUpdateAttribute (a, b, attr, diffElement, changeAnnotationFactory);
					}
				}
			}
		}
		if (change != null)
		{
			for (Map.Entry<Property, RDFNode> annotation : c.getAnnotations ())
				change.addAnnotation (annotation.getKey (), annotation.getValue ());
		}
	}
	
	/**
	 * Gets the child number of a node.
	 *
	 * @param n the node of interest
	 * @return the child no
	 */
	private int getChildNo (TreeNode n)
	{
		return n.isRoot () ? -1 : n.getParent ().getNoOfChild (n);
	}
	
	/**
	 * Gets the XPath of the node's parent.
	 *
	 * @param n the node of interest
	 * @return the parent XPath
	 */
	private String getParentXpath (TreeNode n)
	{
		return n.isRoot () ? "" : n.getParent ().getXPath ();
	}
}

/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.xmlutils.comparison.Connection;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class Patch storing all operations necessary to transfer one document into another.
 *
 * @author Martin Scharm
 */
public class Patch
{
	
	/** The latest used id in this document. */
	private int id;
	
	/** The XML document that will contain all operations. */
	private Document xmlDoc;
	
	/** The nodes rooting subtrees for different kind of operations. */
	private Element insert, delete, update, move;//, copy, glue;
	
	/** The fullDiff flag indication whether this diff is a full diff. */
	private boolean fullDiff;
	
	/**
	 * Instantiates a new patch.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public Patch () throws ParserConfigurationException
	{
		fullDiff = true;
		init ();
	}
	
	/**
	 * Instantiates a new patch. If the fullDiff flag is set to false only a partially diff will be generated.
	 * This diff briefly describes the modifications, but cannot be used to transform one version of a document into another.
	 *
	 * @param fullDiff the fullDiff flag
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public Patch (boolean fullDiff) throws ParserConfigurationException
	{
		this.fullDiff = fullDiff;
		init ();
	}
	
	/**
	 * Gets the number of stored move operations.
	 *
	 * @return the number moves
	 */
	public int getNumMoves ()
	{
		return move.getChildNodes ().getLength ();
	}
	
	/**
	 * Gets the number of stored update operations.
	 *
	 * @return the number updates
	 */
	public int getNumUpdates ()
	{
		return update.getChildNodes ().getLength ();
	}
	
	/**
	 * Gets the number of stored delete operations.
	 *
	 * @return the number deletes
	 */
	public int getNumDeletes ()
	{
		return delete.getChildNodes ().getLength ();
	}
	
	/**
	 * Gets the number of stored insert operations.
	 *
	 * @return the number inserts
	 */
	public int getNumInserts ()
	{
		return insert.getChildNodes ().getLength ();
	}
	
	/**
	 * Gets the document containing all changes.
	 *
	 * @return the document
	 */
	public Document getDocument ()
	{
		return xmlDoc;
	}
	
	/**
	 * Initializes the patch. Creates the XML document and the nodes which will root the different kinds of operations.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	private void init () throws ParserConfigurationException
	{
		LOGGER.info ("initializing patch w/ fullDiff = ", fullDiff);
		id = 0;
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		xmlDoc = docBuilder.newDocument();
		
		// add root element <bives type="fullDiff">
		Element rootElement = xmlDoc.createElement("bives");
		Attr attr = xmlDoc.createAttribute("type");
		attr.setValue("fullDiff"); // TODO: implement shortDiff
		rootElement.setAttributeNode(attr);
		xmlDoc.appendChild (rootElement);
		
		// create nodes for inserts/updates/moves tec
		update = xmlDoc.createElement("update");
		rootElement.appendChild (update);
		
		
		delete = xmlDoc.createElement("delete");
		rootElement.appendChild (delete);
		
		
		insert = xmlDoc.createElement("insert");
		rootElement.appendChild (insert);
		
		
		move = xmlDoc.createElement("move");
		rootElement.appendChild (move);
		
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
		Element attribute = xmlDoc.createElement("attribute");

		Attr attr = xmlDoc.createAttribute("name");
		attr.setValue (name);
		attribute.setAttributeNode(attr);
		
		attr = xmlDoc.createAttribute("id");
		attr.setValue (nodeId + "");
		attribute.setAttributeNode(attr);
		
		if (chainId > 0)
		{
			attr = xmlDoc.createAttribute("triggeredBy");
			attr.setValue (chainId + "");
			attribute.setAttributeNode(attr);
		}
		
		if (oldValue != null)
		{
			attr = xmlDoc.createAttribute("oldValue");
			attr.setValue (oldValue);
			attribute.setAttributeNode(attr);
		}
		
		if (newValue != null)
		{
			attr = xmlDoc.createAttribute("newValue");
			attr.setValue (newValue);
			attribute.setAttributeNode(attr);
		}

		if (oldPath != null)
		{
			attr = xmlDoc.createAttribute("oldPath");
			attr.setValue (oldPath);
			attribute.setAttributeNode(attr);
		}
		
		if (newPath != null)
		{
			attr = xmlDoc.createAttribute("newPath");
			attr.setValue (newPath);
			attribute.setAttributeNode(attr);
		}
		
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
		Element node = xmlDoc.createElement("node");
		
		Attr attr = xmlDoc.createAttribute("id");
		attr.setValue (nodeId + "");
		node.setAttributeNode(attr);
		
		if (chainId > 0)
		{
			attr = xmlDoc.createAttribute("triggeredBy");
			attr.setValue (chainId + "");
			node.setAttributeNode(attr);
		}

		if (oldParent != null)
		{
			attr = xmlDoc.createAttribute("oldParent");
			attr.setValue (oldParent);
			node.setAttributeNode(attr);
		}

		if (newParent != null)
		{
			attr = xmlDoc.createAttribute("newParent");
			attr.setValue (newParent);
			node.setAttributeNode(attr);
		}

		if (oldChildNo > 0)
		{
			attr = xmlDoc.createAttribute("oldChildNo");
			attr.setValue ("" + oldChildNo);
			node.setAttributeNode(attr);
		}
		
		if (newChildNo > 0)
		{
			attr = xmlDoc.createAttribute("newChildNo");
			attr.setValue ("" + newChildNo);
			node.setAttributeNode(attr);
		}
		
		if (oldPath != null)
		{
			attr = xmlDoc.createAttribute("oldPath");
			attr.setValue (oldPath);
			node.setAttributeNode(attr);
		}
		
		if (newPath != null)
		{
			attr = xmlDoc.createAttribute("newPath");
			attr.setValue (newPath);
			node.setAttributeNode(attr);
		}
		
		if (oldTag != null)
		{
			attr = xmlDoc.createAttribute("oldTag");
			attr.setValue (oldTag);
			node.setAttributeNode(attr);
		}
		
		if (newTag != null)
		{
			attr = xmlDoc.createAttribute("newTag");
			attr.setValue (newTag);
			node.setAttributeNode(attr);
		}
		
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
		Element node = xmlDoc.createElement("text");
		
		Attr attr = xmlDoc.createAttribute("id");
		attr.setValue (nodeId + "");
		node.setAttributeNode(attr);
		
		if (chainId > 0)
		{
			attr = xmlDoc.createAttribute("triggeredBy");
			attr.setValue (chainId + "");
			node.setAttributeNode(attr);
		}

		if (oldParent != null)
		{
			attr = xmlDoc.createAttribute("oldParent");
			attr.setValue (oldParent);
			node.setAttributeNode(attr);
		}

		if (newParent != null)
		{
			attr = xmlDoc.createAttribute("newParent");
			attr.setValue (newParent);
			node.setAttributeNode(attr);
		}

		if (oldChildNo > 0)
		{
			attr = xmlDoc.createAttribute("oldChildNo");
			attr.setValue ("" + oldChildNo);
			node.setAttributeNode(attr);
		}
		
		if (newChildNo > 0)
		{
			attr = xmlDoc.createAttribute("newChildNo");
			attr.setValue ("" + newChildNo);
			node.setAttributeNode(attr);
		}
		
		if (oldPath != null)
		{
			attr = xmlDoc.createAttribute("oldPath");
			attr.setValue (oldPath);
			node.setAttributeNode(attr);
		}
		
		if (newPath != null)
		{
			attr = xmlDoc.createAttribute("newPath");
			attr.setValue (newPath);
			node.setAttributeNode(attr);
		}

		if (fullDiff)
		{
			if (oldText != null)
			{
				Element old = xmlDoc.createElement("oldText");
				old.setTextContent (oldText);
				node.appendChild (old);
			}
			
			if (newText != null)
			{
				Element neu = xmlDoc.createElement("newText");
				neu.setTextContent (newText);
				node.appendChild (neu);
			}
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
		delete.appendChild (createNodeElement (nodeId, getParentXpath (toDelete), null, toDelete.getXPath (), null, getChildNo (toDelete), -1, toDelete.getTagName (), null, chainId));
		
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
		delete.appendChild (createAttributeElement (++id, node.getXPath (), null, attribute, node.getAttribute (attribute), null, chainId));
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
		delete.appendChild (createTextElement (++id, getParentXpath (toDelete), null, toDelete.getXPath (), null, getChildNo (toDelete), -1, toDelete.getText (), null, chainId));
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
		insert.appendChild (createNodeElement (nodeId, null, getParentXpath (toInsert), null, toInsert.getXPath (), -1, getChildNo (toInsert), null, toInsert.getTagName (), chainId));
		
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
		insert.appendChild (createAttributeElement (++id, null, node.getXPath (), attribute, null, node.getAttribute (attribute), chainId));
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
		insert.appendChild (createTextElement (++id, null, getParentXpath (toInsert), null, toInsert.getXPath (), -1, getChildNo (toInsert), null, toInsert.getText (), chainId));
	}
	
	/**
	 * Update a node.
	 *
	 * @param c the connection between the old version of the node and the new version of it.
	 * @param conMgmt the connection manager
	 */
	public void updateNode (Connection c, SimpleConnectionManager conMgmt)
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
		
		// text node
		if (a.getType () == TreeNode.TEXT_NODE)
		{
			if ((a.getModification () & TreeNode.MODIFIED) != 0)
			{
				LOGGER.info ("text differs");
				Element e = createTextElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), ((TextNode) a).getText (), ((TextNode) b).getText (), -1);
				
				if (moveThem)
					move.appendChild (e);
				else
					update.appendChild (e);
			}
			else if (moveThem)
			{
				LOGGER.info ("equal text");
				move.appendChild (createTextElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null, -1));
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
				move.appendChild (createNodeElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null, -1));
			}
		}
		else
		{
			// matching label? -> update more extreme than move...
			if (!dA.getTagName ().equals (dB.getTagName ()))
			{
				LOGGER.info ("label of nodes differ -> updating");
				update.appendChild (createNodeElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), dA.getTagName (), dB.getTagName (), -1));
			}
			else if (moveThem)
			{
				LOGGER.info ("label of nodes do not differ -> moving");
				move.appendChild (createNodeElement (++id, getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null, -1));
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
					String aA = dA.getAttribute (attr), bA = dB.getAttribute (attr);
					if (aA == null)
						insertAttribute (dB, attr, -1);
					else if (bA == null)
						deleteAttribute (dA, attr, -1);
					else if (!aA.equals (bA))
						update.appendChild (createAttributeElement (++id, a.getXPath (), b.getXPath (), attr, aA, bA, -1));
				}
			}
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

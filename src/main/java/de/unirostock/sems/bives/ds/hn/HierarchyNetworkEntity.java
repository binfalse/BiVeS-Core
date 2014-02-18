/**
 * 
 */
package de.unirostock.sems.bives.ds.hn;

import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.bives.ds.crn.CRN;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class HierarchyNetworkEntity.
 *
 * @author Martin Scharm
 */
public abstract class HierarchyNetworkEntity
implements GraphEntity
{
	
	/** The id. */
	private String id;
	
	/** The label in the original and modified document. */
	private String labelA, labelB;
	
	/** The the original and modified document. */
	private DocumentNode docA, docB;
	
	/** The single document flag. */
	private boolean singleDoc;

	/**
	 * Instantiates a new hierarchy network entity.
	 *
	 * @param id the id of this entity
	 * @param labelA the label a
	 * @param labelB the label b
	 * @param docA the doc a
	 * @param docB the doc b
	 */
	public HierarchyNetworkEntity (String id, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		this.id = id;
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		this.singleDoc = false;
	}
	
	/**
	 * Sets the document node from the original document.
	 *
	 * @param docA the original document node
	 */
	public void setDocA (DocumentNode docA)
	{
		this.docA = docA;
	}
	
	/**
	 * Sets the label from the original document.
	 * 
	 * @param labelA the original label
	 */
	public void setLabelA (String labelA)
	{
		this.labelA = labelA;
	}
	
	/**
	 * Sets the document node from the modified document.
	 *
	 * @param docB the modified document node
	 */
	public void setDocB (DocumentNode docB)
	{
		this.docB = docB;
	}
	
	/**
	 * Sets the label from the modified document.
	 *
	 * @param labelB the modified label
	 */
	public void setLabelB (String labelB)
	{
		this.labelB = labelB;
	}
	
	/**
	 * Gets the original document node.
	 *
	 * @return the original document node
	 */
	public DocumentNode getA ()
	{
		return docA;
	}
	
	/**
	 * Gets the modified document node.
	 *
	 * @return the modified document node
	 */
	public DocumentNode getB ()
	{
		return docB;
	}
	
	/**
	 * Gets the id of this entity.
	 *
	 * @return the id
	 */
	public String getId ()
	{
		return "c" + id;
	}
	
	/**
	 * Gets the label of this entity.
	 *
	 * @return the label
	 */
	public String getLabel ()
	{
		if (labelA == null)
			return labelB;
		if (labelB == null)
			return labelA;
		if (labelA.equals (labelB))
			return labelA;
		return labelA + " -> " + labelB;
	}
	
	/**
	 * Gets the modification of this entity.
	 *
	 * @return the modification
	 */
	public int getModification ()
	{
		if (singleDoc)
			return UNMODIFIED;
		
		if (labelA == null)
			return INSERT;
		if (labelB == null)
			return DELETE;
		
		if (!labelA.equals (labelB)
			|| docA.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED)
			|| docB.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED))
			return MODIFIED;
		return UNMODIFIED;
	}

	/**
	 * Sets the single document flag.
	 */
	public void setSingleDocument ()
	{
		singleDoc = true;
	}
	
}

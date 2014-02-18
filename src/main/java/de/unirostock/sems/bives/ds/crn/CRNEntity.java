/**
 * 
 */
package de.unirostock.sems.bives.ds.crn;

import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class CRNEntity representing an entity in a chemical reaction network.
 *
 * @author Martin Scharm
 */
public abstract class CRNEntity
implements GraphEntity
{
	/** The id of this entity. */
	protected String id;
	
	/** The labels in docs A and B. */
	protected String labelA, labelB;
	
	/** The documents A and B. */
	protected DocumentNode docA, docB;
	
	/** The single doc flag if in single-doc-operation-mode. */
	protected boolean singleDoc;

	/**
	 * Instantiates a new entity.
	 *
	 * @param entityId the entity id
	 * @param labelA the label of that entity in the original document
	 * @param labelB the label of that entity in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public CRNEntity (String entityId, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		this.id = entityId;
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		singleDoc = false;
	}
	
	/**
	 * Sets the original document node.
	 *
	 * @param docA the original document node
	 */
	public void setDocA (DocumentNode docA)
	{
		this.docA = docA;
	}
	
	/**
	 * Sets the original label.
	 *
	 * @param labelA the label in the original document
	 */
	public void setLabelA (String labelA)
	{
		this.labelA = labelA;
	}
	
	/**
	 * Sets the modified document node.
	 *
	 * @param docB the modified document node
	 */
	public void setDocB (DocumentNode docB)
	{
		this.docB = docB;
	}
	
	/**
	 * Sets the modified label.
	 *
	 * @param labelB the label in the modified document
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
	 * Gets the id of the entity.
	 *
	 * @return the id
	 */
	public String getId ()
	{
		return id;
	}
	
	/**
	 * Gets the label.
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

/**
 * 
 */
package de.unirostock.sems.bives.ds.rn;

import de.unirostock.sems.bives.ds.GraphEntity;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class ReactionNetworkEntity representing an entity in a reaction network.
 *
 * @author Martin Scharm
 */
public abstract class ReactionNetworkEntity
implements GraphEntity
{
	/** The id of this entity. */
	protected String id;

	/** The labels in original doc. */
	protected String labelA;
	/** The labels in modified doc. */
	protected String labelB;

	/** The original document. */
	protected DocumentNode docA;
	/** The modified document. */
	protected DocumentNode docB;
	
	/** The single doc flag if in single-doc-operation-mode. */
	protected boolean singleDoc;

	/** Id of the containing compartment in original document **/
	protected ReactionNetworkCompartment outsideA;
	
	/** Id of the containing compartment in original document **/
	protected ReactionNetworkCompartment outsideB;

	/**
	 * Instantiates a new entity.
	 *
	 * @param entityId the entity id
	 * @param labelA the label of that entity in the original document
	 * @param labelB the label of that entity in the modified document
	 * @param docA the original document
	 * @param docB the modified document
	 * @param compartmentB 
	 * @param compartmentA 
	 */
	public ReactionNetworkEntity (String entityId, String labelA, String labelB, DocumentNode docA, DocumentNode docB, ReactionNetworkCompartment compartmentA, ReactionNetworkCompartment compartmentB)
	{
		this.id = entityId;
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		singleDoc = false;
		this.outsideA = compartmentA;
		this.outsideB = compartmentB;
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
	
	public void setOutsideA(ReactionNetworkCompartment compartmentA) {
		this.outsideA = compartmentA;
		
	}
	
	public void setOutsideB(ReactionNetworkCompartment compartmentB) {
		this.outsideB = compartmentB;
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
	
	public String getOutsideCompartment() {
		if(outsideA == outsideB) {
			if(outsideA == null) return null;
			return outsideB.getId();
		}
		if(outsideA != null && outsideB != null)
			return outsideB.getId();
		if(outsideA == null && outsideB != null)
			return outsideB.getId();
		if(outsideA != null)
			return outsideA.getId();
		return null;
	}

	/**
	 * Sets the single document flag.
	 */
	public void setSingleDocument ()
	{
		singleDoc = true;
	}
	
}

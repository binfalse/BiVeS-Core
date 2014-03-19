/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class PatchProducer producing patches..
 *
 * @author Martin Scharm
 */
public class PatchProducer
	extends Producer
{
	
	/** The patch. */
	private Patch patch;
	
	/** The full-diff-flag. if false we'll produce shorter diffs. somewhen in the future.. ;-) */
	private boolean fullDiff;
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.Producer#init(de.unirostock.sems.bives.algorithm.SimpleConnectionManager, de.unirostock.sems.xmlutils.ds.TreeDocument, de.unirostock.sems.xmlutils.ds.TreeDocument)
	 */
	public void init (SimpleConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		super.init (conMgmt, docA, docB);
		fullDiff = true;
		LOGGER.info ("creating patch producer: ");// + conMgmt + " " + docA + " " + docB);
	}
	
	/**
	 * Gets the patch.
	 *
	 * @return the patch
	 */
	public Patch getPatch ()
	{
		return patch;
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Producer#produce()
	 */
	@Override
	public String produce ()
	{
		LOGGER.info ("producing patch");
		
		patch = new Patch (fullDiff);
		
		// examine original document
		producePatchA (docA.getRoot ());

		// examine modified version
		producePatchB (docB.getRoot ());
		
		LOGGER.info ("patch finished, producing xml output");
		
		return XmlTools.prettyPrintDocument (patch.getDocument ());
	}
	
	/**
	 * Produce patch parts of the original document, recursively.
	 *
	 * @param node the node rooting the current subtree of interest
	 */
	private void producePatchA (TreeNode node)
	{
		if ((node.hasModification (TreeNode.SUBTREEUNMAPPED)))
			patch.deleteSubtree (node, -1);
		else
		{
			if ((node.hasModification (TreeNode.UNMAPPED)))
				patch.deleteNode (node, -1);
			else
			{
				if ((node.hasModification (TreeNode.GLUED | TreeNode.COPIED)))
				{
					LOGGER.error ("detected multiple connections of a single node, but copy & glue not supported yet...");
					// TODO: support copy & glue
					throw new UnsupportedOperationException ("copy & glue not supported yet...");
				}
				else
				{
					patch.updateNode (conMgmt.getConnectionForNode (node), conMgmt);
				}
			}
			
			if (node.getType () == TreeNode.DOC_NODE)
			{
				DocumentNode dnode = (DocumentNode) node;
				for (TreeNode n : dnode.getChildren ())
					producePatchA (n);
			}
		}
	}
	
	/**
	 * Produce patch parts of the modified document, recursively.
	 *
	 * @param node the node rooting the current subtree of interest
	 */
	private void producePatchB (TreeNode node)
	{
		if ((node.hasModification (TreeNode.SUBTREEUNMAPPED)))
			patch.insertSubtree (node, -1);
		else
		{
			if ((node.hasModification (TreeNode.UNMAPPED)))
				patch.insertNode (node, -1);
			else
			{
				if ((node.getModification () & (TreeNode.GLUED | TreeNode.COPIED)) != 0)
				{
					LOGGER.error ("detected multiple connections of a single node, but copy & glue not supported yet...");
					// TODO: support copy & glue
					throw new UnsupportedOperationException ("copy & glue not supported yet...");
				}
				// else part covered before
			}
			
			if (node.getType () == TreeNode.DOC_NODE)
			{
				DocumentNode dnode = (DocumentNode) node;
				for (TreeNode n : dnode.getChildren ())
					producePatchB (n);
			}
		}
	}
	
}

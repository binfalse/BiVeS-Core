/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.NodeConnection;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.xmlutils.comparison.Connection;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.NodeDistance;
import de.unirostock.sems.xmlutils.ds.NodeDistanceComparator;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.ds.TreeNodeComparatorBySubtreeSize;


/**
 * 
 * The Class XyDiffConnector to map nodes as described in Cobena2002.
 *
 * @author Martin Scharm
 */
public class XyDiffConnector
	extends Connector
{
	
	/** The level we definitely walk up (at least). */
	private final int MIN_CANDIDATEPARENT_LEVEL = 6;
	
	/** The preprocessor. */
	private Connector preprocessor;

	/**
	 * Instantiates a new XyDiffConnector. In this setting we'll run an ID mapper before we do our work.
	 * @param docA the original document
	 * @param docB the modified document
	 */
	public XyDiffConnector (TreeDocument docA, TreeDocument docB)
	{
		super (docA, docB);
	}
	
	/**
	 * Instantiates a new XyDiffConnector. Here we'll use `preprocessor` to find some connections before we start.
	 *
	 * @param preprocessor the connector to initiate the connections
	 */
	public XyDiffConnector (Connector preprocessor)
	{
		super (preprocessor.getDocA (), preprocessor.getDocB ());
		this.preprocessor = preprocessor;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.algorithm.Connector#init(de.unirostock.sems.xmlutils.ds.TreeDocument, de.unirostock.sems.xmlutils.ds.TreeDocument)
	 */
	@Override
	protected void init () throws BivesConnectionException
	{
		// connections not yet initialized?
		if (preprocessor == null)
		{
			// then we'll use an id-connector by default...
			IdConnector id = new IdConnector (docA, docB, true);
			id.findConnections ();
	
			conMgmt = id.getConnections ();
		}
		else
		{
			// otherwise let the preprocessor do its work
			//preprocessor.init (docA, docB);
			preprocessor.findConnections ();
	
			conMgmt = preprocessor.getConnections ();
		}
	}
	

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		LOGGER.info ("starting XY Diff");
		boolean debug = LOGGER.isDebugEnabled();
		if (debug)
		{
			LOGGER.debug ("pre xy diff run");
			LOGGER.debug (conMgmt.toString ());
		}
		
		// document roots always match...
		if (conMgmt.getConnectionOfNodes (docA.getRoot (), docB.getRoot ()) == null)
			conMgmt.addConnection (new NodeConnection (docA.getRoot (), docB.getRoot ()));
		

		// doing full bottom up
		LOGGER.info ("doing full bottom up");
		fullBottomUp (docB.getRoot ());

		if (debug)
			LOGGER.debug (conMgmt.toString ());
		
		// doing top-down
		LOGGER.info ("doing top down");
		topdownMatch (docA.getRoot (), docB.getRoot ());

		if (debug)
			LOGGER.debug (conMgmt.toString ());

		// optimize the mapping
		LOGGER.info ("doing optimizations");
		optimize (docA.getRoot ());

		if (debug)
		{
			LOGGER.debug ("post xy diff run");
			LOGGER.debug (conMgmt.toString ());

			LOGGER.debug ("unmatched in A:");
			List<TreeNode> unmatched = conMgmt.getUnmatched (docA.getRoot (), new ArrayList<TreeNode> ());
			for (TreeNode u : unmatched)
				LOGGER.debug (u.getXPath ());
	
			LOGGER.debug ("unmatched in B:");
			unmatched = conMgmt.getUnmatched (docB.getRoot (), new ArrayList<TreeNode> ());
			for (TreeNode u : unmatched)
				LOGGER.debug (u.getXPath ());
		}
		LOGGER.info ("finished XY Diff");
	}
	
	/**
	 * Full-bottomUp step.
	 *
	 * @param nodeB the node in tree B
	 * @return the tree node
	 * @throws BivesConnectionException the bives connection exception
	 */
	private TreeNode fullBottomUp (TreeNode nodeB) throws BivesConnectionException
	{
		HashMap<TreeNode, Double> weightByCandidate = new HashMap<TreeNode, Double> ();
		
		// Apply to children
		if (nodeB.getType () == TreeNode.DOC_NODE)
		{
			List<TreeNode> children = ((DocumentNode) nodeB).getChildren ();
			for (TreeNode child : children)
			{
				TreeNode childMatch = fullBottomUp (child);
				if (childMatch != null)
				{
					// get the parent of this node
					TreeNode v0childParent = conMgmt.getConnectionForNode (childMatch).getTreeA ().getParent ();
					if (v0childParent != null)
					{
						if (weightByCandidate.get (v0childParent) == null)
							weightByCandidate.put (v0childParent, child.getWeight ());
						else
							weightByCandidate.put (v0childParent, weightByCandidate.get (v0childParent) + child.getWeight ());
					}
				}
			}
		}
		
		// Do self
		if (conMgmt.getConnectionForNode (nodeB) != null)
		{
			TreeNode match = conMgmt.getConnectionForNode (nodeB).getTreeA ();
			LOGGER.debug ("v1 node ", nodeB.getXPath (), " already has a match, returning ", match.getXPath ());
			return match;
		}
		if (weightByCandidate.size () < 1)
		{
			TreeNode match = null;
			if (conMgmt.getConnectionForNode (nodeB) != null)
			{
				match = conMgmt.getConnectionForNode (nodeB).getTreeA ();
				LOGGER.debug ("v1 node ", nodeB.getXPath (), " has no matched children, returning ", match.getXPath ());
			}
			else
				LOGGER.debug ("v1 node ", nodeB.getXPath (), " has no matched children, returning ", match);
			return match;
		}
		
		// Find parent corresponding to largest part of children
		LOGGER.debug ("v0 parents of v0 nodes matching v1 children of v1 node ", nodeB.getXPath (), " are:");
		double max=-1.0;
		TreeNode bestMatch=null;
		for (TreeNode node : weightByCandidate.keySet ())
		{
			LOGGER.debug ("v0 node ", node.getXPath (), " with total weight among children of ", weightByCandidate.get (node));
			if (weightByCandidate.get (node) > max)
			{
				bestMatch = node;
				max = weightByCandidate.get (node);
			}
		}
		if (bestMatch == null)
			return null;
		
		LOGGER.debug ("best parent is v0 node ", bestMatch.getXPath (), " with total weight among children of ", max);
		nodeAssign (bestMatch, nodeB);
		return bestMatch;
	}
	
	/**
	 * Top-Down step.
	 *
	 * @param rootA the root a
	 * @param rootB the root b
	 * @throws BivesConnectionException the bives connection exception
	 */
	private void topdownMatch (TreeNode rootA, TreeNode rootB) throws BivesConnectionException
	{
		PriorityQueue<TreeNode> toMatch = new PriorityQueue<TreeNode> (100, new TreeNodeComparatorBySubtreeSize (true));
		
		toMatch.add (rootB);
		while (toMatch.size () > 0)
		{
			TreeNode nodeID = toMatch.poll ();
			
			String v1hash = nodeID.getSubTreeHash ();
			LOGGER.debug ("Trying new node ", nodeID.getXPath (), ", hash=", v1hash);
			
			TreeNode matcher = null;
			
		  // consistency check: has it already been done ???
			
			if (conMgmt.getConnectionForNode (nodeID) != null)
			{
				LOGGER.debug ("skipping Full Subtree check because subtree node is already assigned.");
			}
			else
			{
				// Document roots *always* match
				// a 'renameRoot' operation will be added later if necessary
				if (nodeID == rootB)
				{
					conMgmt.addConnection (new NodeConnection (rootA, rootB));
				}
				else
				{
					matcher = getBestCandidate(nodeID, v1hash);
				}
			}
			
			if (matcher != null)
			{
				recursiveAssign (matcher, nodeID);
			}
			// If not found, children will have to be investigated
			else
			{
				// put children in the vector so they'll be taken care of later
				LOGGER.debug ("Subtree rooted at ", nodeID.getXPath (), " not fully matched, programming children");
				if (nodeID.getType () == TreeNode.DOC_NODE)
				{
					List<TreeNode> children = ((DocumentNode) nodeID).getChildren ();
					for (TreeNode child : children)
					{
						toMatch.add (child);
					}
				}
			}
			// Next node to investigate
		}
	}
	
	/**
	 * Optimization step.
	 *
	 * @param nodeA the node a
	 * @throws BivesConnectionException the bives connection exception
	 */
	private void optimize (DocumentNode nodeA) throws BivesConnectionException
	{
		// If node is matched, we can try to do some work
		Connection c = conMgmt.getConnectionForNode (nodeA);
		if (c != null)
		{
			TreeNode tnb = c.getPartnerOf (nodeA);
			if (tnb.getType () != TreeNode.DOC_NODE)
				return;
			DocumentNode nodeB = (DocumentNode) tnb;
			
			// Get Free nodes in v0
			HashMap<String, ArrayList<TreeNode>> kidsMapA = new HashMap<String, ArrayList<TreeNode>> ();
			List<TreeNode> kidsA = nodeA.getChildren ();
			for (TreeNode node : kidsA)
			{
				if (conMgmt.getConnectionForNode (node) != null)
					continue;
				String tag = node.getTagName ();
				if (kidsMapA.get (tag) == null)
					kidsMapA.put (tag, new ArrayList<TreeNode> ());
				kidsMapA.get (tag).add (node);
			}
			
			// Look for similar nodes in v1
			HashMap<String, List<TreeNode>> kidsMapB = new HashMap<String, List<TreeNode>> ();
			List<TreeNode> kidsB = nodeB.getChildren ();
			for (TreeNode node : kidsB)
			{
				if (conMgmt.getConnectionForNode (node) != null)
					continue;
				String tag = node.getTagName ();
				if (kidsMapB.get (tag) == null)
					kidsMapB.put (tag, new ArrayList<TreeNode> ());
				kidsMapB.get (tag).add (node);
			}

			// Now match unique children
			for (String tag : kidsMapA.keySet ())
			{
				optimize (kidsMapA.get (tag), kidsMapB.get (tag));
			}
			
			/*std::map<std::string, int>::iterator i ;
			for(i=v0freeChildren.begin(); i!=v0freeChildren.end(); i++) {
				if ((i->second>0)&&(v1freeChildren.find(i->first)!=v1freeChildren.end())) {
					int v1ID = v1freeChildren[i->first];
					if (v1ID>0) {
						vddprintf(("matching v0(%d) with v1(%d)\n", i->second, v1ID));
						nodeAssign(i->second, v1ID);
						}
					}
				}

			// End-if - Assigned(v0nodeID)
			}*/
		} //endif
		
		// Apply recursivly on children
		List<TreeNode> children = nodeA.getChildren ();
		for (TreeNode child : children)
		{
			if (child.getType () == TreeNode.DOC_NODE)
				optimize ((DocumentNode) child);
		}
	}// end optimize
	
	/**
	 * Optimization step.
	 *
	 * @param nodesA the nodes a
	 * @param nodesB the nodes b
	 * @throws BivesConnectionException the bives connection exception
	 */
	private void optimize (List<TreeNode> nodesA, List<TreeNode> nodesB) throws BivesConnectionException
	{
		// try to find mappings of children w/ same tag name and same parents
		if (nodesA == null || nodesB == null || nodesA.size () == 0 || nodesB.size () == 0)
			return;
		
		boolean textNodes = nodesA.get (0).getType () == TreeNode.TEXT_NODE;
		
		if (nodesA.size () == 1 && nodesB.size () == 1)
		{
			// lets match both if they are not too different
			TreeNode nodeA = nodesA.get (0), nodeB = nodesB.get (0);
			if (!textNodes)
			{
				DocumentNode dnodeA = (DocumentNode) nodeA, dnodeB = (DocumentNode) nodeB;
				if (dnodeA.getAttributeDistance (dnodeB) < .9)
				{
					LOGGER.debug ("connect unambiguos nodes during optimization: ", nodeA.getXPath (), " --> ", nodeB.getXPath ());
					conMgmt.addConnection (new NodeConnection (nodeA, nodeB));
				}
			}
			else if (textNodes)
			{
				TextNode tnodeA = (TextNode) nodeA, tnodeB = (TextNode) nodeB;
				if (tnodeA.getTextDistance (tnodeB) < .5)
				{
					LOGGER.debug ("connect unambiguos nodes during optimization: ", nodeA.getXPath (), " --> ", nodeB.getXPath ());
					conMgmt.addConnection (new NodeConnection (nodeA, nodeB));
				}
			}
			return;
		}
		
		// calculate distances between nodes
		List<NodeDistance> distances = new ArrayList<NodeDistance> ();
		for (TreeNode nodeA : nodesA)
			for (TreeNode nodeB : nodesB)
			{
				if (nodeA.getType () == TreeNode.TEXT_NODE)
				{
					TextNode tnodeA = (TextNode) nodeA, tnodeB = (TextNode) nodeB;
					distances.add (new NodeDistance (nodeA, nodeB, tnodeA.getTextDistance (tnodeB)));
				}
				else
					distances.add (new NodeDistance (nodeA, nodeB, ((DocumentNode) nodeA).getAttributeDistance ((DocumentNode) nodeB)));
			}
		// sort by distance
		Collections.sort (distances, new NodeDistanceComparator (false));
		
		// greedy connect nodes
		for (NodeDistance comp : distances)
		{
			// stop at too different nodes
			if ((textNodes &&  comp.distance > 0.5) || (!textNodes && comp.distance > 0.9))
				break;
			TreeNode na = comp.nodeA, nb = comp.nodeB;
			if (conMgmt.getConnectionForNode (na) == null && conMgmt.getConnectionForNode (nb) == null)
				conMgmt.addConnection (new NodeConnection (na, nb));
		}
	}
	
	/**
	 * Assign two nodes to each other, as long as they don't have a connection.
	 *
	 * @param a the node from the original tree
	 * @param b the node from the modified tree
	 * @return true, if successfully connected
	 * @throws BivesConnectionException the bives connection exception
	 */
	private boolean nodeAssign (TreeNode a, TreeNode b) throws BivesConnectionException
	{
		LOGGER.debug ("Matching old: ", a.getXPath (), " with new: ", b.getXPath ());
		if (conMgmt.getConnectionForNode (a) != null || conMgmt.getConnectionForNode (b) != null)
		{
			LOGGER.debug ("already assigned");
			return true;
		}
		
		if (a.getType () != b.getType ())
			return false;
		
		if ((a.getType () == TreeNode.DOC_NODE && ((DocumentNode) b).getTagName ().equals (((DocumentNode) a).getTagName ())) || a.getType () == TreeNode.TEXT_NODE)
		{
			conMgmt.addConnection (new NodeConnection (a, b));
			return true;
		}
		return false;
		// statsCantMatchDifferentOwnHash
	}
	
//From a number of old nodes that have the exact same signature, one has to choose which one
//will be considered 'matching' the new node
//Basically, the best is the old node somehow related to new node: parents are matching for example
//If none has this property, and if hash_matching is *meaningfull* ( text length > ??? ) we may consider returning any matching node
//Maybe on a second level parents ?
	/**
 * Gets the best candidate.
 *
 * @param v1nodeID the v1node id
 * @param selfkey the selfkey
 * @return the best candidate
 * @throws BivesConnectionException the bives connection exception
 */
private TreeNode getBestCandidate (TreeNode v1nodeID, String selfkey) throws BivesConnectionException
	{

		// nodeRange.first==nodeRange.second) return 0;

		// first pass : finds a node which parent matches v1node parent (usefull because documents roots always match or parent may be matched thanks to its unique label)
		int candidateRelativeLevel = 1 ;
		TreeNode v1nodeRelative = v1nodeID ;

	  /* The relative weight correspond to the ratio of the weight of the subtree over the weight of the entire document */
		double relativeWeight = v1nodeID.getWeight () / docB.getRoot ().getWeight ();
		int maxLevelPath = MIN_CANDIDATEPARENT_LEVEL + (int) (5.0*Math.log((double)docB.getNumNodes ())*relativeWeight) ;

		/* Try to attach subtree to existing match among ancesters
		 * up to maximum level of ancester, depending on subtree weight
		 */
		
		LOGGER.debug ("maxLevel=", maxLevelPath);
		
		while ( candidateRelativeLevel <= maxLevelPath )
		{
			LOGGER.debug ("    pass parentLevel=", candidateRelativeLevel);
			
			v1nodeRelative = v1nodeRelative.getParent ();
			if (v1nodeRelative == null)
			{
				LOGGER.debug ("but node doesn't not have ancesters up to this level\n");
				return null;
			}
			LOGGER.debug ("    pass v1nodeRelative=", v1nodeRelative.getXPath ());
			
			if (conMgmt.getConnectionForNode (v1nodeRelative) == null)
			{
				LOGGER.debug ("but v1 relative at this level has no match");
			}
			else
			{
				/* For the lower levels, use precomputed index tables to acces candidates given the parent */
				
				if (false && candidateRelativeLevel<=MIN_CANDIDATEPARENT_LEVEL)
				{
					// TODO: no idea...
				}
				/* For higher levels, try every candidate and this if its ancestor is a match for us */
				else
				{
					List<TreeNode> theList = docA.getNodesByHash (selfkey);
					if (theList == null || theList.size () < 1)
					{
						LOGGER.debug ("  no candidates for hash");
						return null;
					}
					LOGGER.debug ("  num candidates: ", theList.size ());
					if (theList.size () > 50)
						LOGGER.warn ("it seems that there are too many candidates (", theList.size (), ") for a match of ", v1nodeID.getXPath (), " (", selfkey, ")");
					
					final String xPath = v1nodeID.getXPath ();
					class CandidateResult implements Comparable<CandidateResult>
					{
						TreeNode candidate;
						int level;
						int dist;
						public CandidateResult (TreeNode candidate, int level)
						{
							this.candidate = candidate;
							this.level = level;
							this.dist = -1;
						}
						public int getDist ()
						{
							if (dist == -1)
								dist = GeneralTools.computeLevenshteinDistance (xPath, candidate.getXPath ());
							return dist;
						}
						@Override
						public int compareTo (CandidateResult cr)
						{
							if (level < cr.level)
								return -1;
							if (level > cr.level)
								return 1;
							
							if (getDist () < cr.getDist ())
								return -1;
							
							if (getDist () > cr.getDist ())
								return 1;
							
							return 0;
						}
					}
					List<CandidateResult> candidates = new ArrayList<CandidateResult> ();
					
					//for (int i = 0; i < theList.size (); i++)
					for (TreeNode candidate : theList)
					{
						//TreeNode candidate = theList.get (i);
						if (conMgmt.getConnectionForNode (candidate) == null)
						{// Node still not assigned
							LOGGER.debug ("(", candidate.getXPath (), ")");
							TreeNode candidateRelative = candidate;
							for (int j = 0; j < candidateRelativeLevel; j++)
							{
								candidateRelative = candidateRelative.getParent ();
								if (candidateRelative == null)
									break;
							}
							// if relative is ok at required level, test matching
							if (candidateRelative != null)
							{
								if (conMgmt.getConnectionOfNodes (candidateRelative, v1nodeRelative) != null)
								{
									LOGGER.debug (" adding candidate because some relatives ( level= ", candidateRelativeLevel, " ) are matching");
									//return candidate;
									candidates.add (new CandidateResult (candidate, candidateRelativeLevel));
								}
							}
						}
					} //try next candidate
					
					if (candidates.size () > 0)
					{
						// sort
						Collections.sort (candidates);
						// get min
						CandidateResult candidate = candidates.get (0);
						LOGGER.debug (" took candidate: ", candidate.candidate.getXPath ());
						/*if (candidates.size () == 2)
						{
							LOGGER.debug ("    all candidates");
							for (CandidateResult c : candidates)
							{
								LOGGER.debug ("    candidate: " + c.level + "/" + c.dist + " -> " + c.candidate.getXPath ());
							}
						}*/
			
						if (candidate.level > 1)
						{
							LOGGER.debug ("    level>1 so forcing parents matching in the hierarchie");
							forceParentsAssign(candidate.candidate, v1nodeID, candidateRelativeLevel );
						}
						
						return candidate.candidate;
					}
					
				}//end MIN(Precomputed)<relativelevel<MAX
				
			} //end ancestor is matched
			candidateRelativeLevel++;
		} // endwhile: next level
		return null;
	}
	
	/**
	 * Recursively assign subtrees to each other.
	 *
	 * @param v0nodeID the node rooting the subtree in the original document
	 * @param v1nodeID the node rooting the subtree in the modified document
	 * @throws BivesConnectionException the bives connection exception
	 */
	private void recursiveAssign (TreeNode v0nodeID, TreeNode v1nodeID) throws BivesConnectionException
	{
		if (v0nodeID == null || v1nodeID == null)
		{
			LOGGER.debug ("recursiveAssign::bad arguments (", v0nodeID, ", ", v0nodeID, ")");
			return;
		}
		
		nodeAssign (v0nodeID, v1nodeID);

		if (v0nodeID.getType () == TreeNode.DOC_NODE && v1nodeID.getType () == TreeNode.DOC_NODE)
		{
			List<TreeNode> v0children = ((DocumentNode) v0nodeID).getChildren ();
			List<TreeNode> v1children = ((DocumentNode) v1nodeID).getChildren ();
			if (v0children.size () != v1children.size ())
				LOGGER.debug ("recursiveAssign::diff # children: ", v0children.size (), " -vs- ", v1children.size ());
			for (int i = 0; i < v0children.size (); i++)
				recursiveAssign(v0children.get (i), v1children.get (i));
		}
	}
	
	/**
	 * Force the connections of parents of two nodes. Recursively for #level levels
	 *
	 * @param v0nodeID the node in the original document
	 * @param v1nodeID the node in the modified document
	 * @param level the number of levels to climb
	 * @throws BivesConnectionException the bives connection exception
	 */
	private void forceParentsAssign (TreeNode v0nodeID, TreeNode v1nodeID, int level) throws BivesConnectionException
	{
		if (v0nodeID == null ||  v1nodeID == null)
		{
			LOGGER.debug ("forceParentsAssign::bad arguments");
			return;
		}
		TreeNode v0ascendant = v0nodeID ;
		TreeNode v1ascendant = v1nodeID ;
		
		for (int i = 0; i < level - 1; i++)
		{
			v0ascendant = v0ascendant.getParent ();
			v1ascendant = v1ascendant.getParent ();
			if (v0ascendant==null||v1ascendant==null)
				return;
			
			if (conMgmt.getConnectionForNode (v0ascendant) != null)
			{
				LOGGER.debug ("forceParentsAssign stopped at level ", i, " because v0 ascendant is already assigned");
				return;
			}
			if (conMgmt.getConnectionForNode (v1ascendant) != null)
			{
				LOGGER.debug ("forceParentsAssign stopped at level ", i, " because v1 ascendant is already assigned");
				return;
			}
			
			if (!nodeAssign( v0ascendant, v1ascendant))
			{
				LOGGER.debug ("forceParentsAssign stopped because relatives (", v0ascendant.getXPath (), ", ", v1ascendant.getXPath (), ") do not have the same label");
				return;
			}
			
			/*
			if (v0ascendant.getTagName ().equals (v1ascendant.getTagName ()))
			{
				nodeAssign( v0ascendant, v1ascendant );
			}
			else
			{
				debug ("forceParentsAssign stopped because relatives ("+v0ascendant.getXPath ()+", "+v1ascendant.getXPath ()+") do not have the same label");
				return;
			}*/
		}
	}
}

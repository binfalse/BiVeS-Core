/**
 * 
 */
package de.unirostock.sems.bives.ds.ontology;



/**
 * The Class SBOTerm representing a link into the Systems Biology Ontology.
 *
 * @author Martin Scharm
 */
public class SBOTerm
{
	
	/** The string defining a STIMULATOR. */
	public static final String MOD_STIMULATOR = "stimulator";
	
	/** The string defining an UNKNOWN modifier. */
	public static final String MOD_UNKNOWN = "unknown";
	
	/** The string defining an INHIBITOR. */
	public static final String MOD_INHIBITOR = "inhibitor";
	
	/** The string defining no modification at all. */
	public static final String MOD_NONE = "none";
	
	/** The SBO term. */
	private String SBOTerm;
	
	/**
	 * Instantiates a new SBO term.
	 *
	 * @param SBOTerm the SBO term of the form SBO:[0-9]+
	 */
	public SBOTerm (String SBOTerm)
	{
		this.SBOTerm = SBOTerm;
	}
	
	/**
	 * Gets the SBO term stored in here.
	 *
	 * @return the SBO term
	 */
	public String getSBOTerm ()
	{
		return SBOTerm;
	}
	
	/**
	 * Creates a dummy stimulator.
	 *
	 * @return the SBO term describing a stimulator
	 */
	public static SBOTerm createStimulator ()
	{
		return new SBOTerm ("SBO:0000459");
	}
	
	/**
	 * Creates a dummy inhibitor.
	 *
	 * @return the SBO term describing an inhibitor
	 */
	public static SBOTerm createInhibitor ()
	{
		return new SBOTerm ("SBO:0000020");
	}
	
	/**
	 * Resolve a modifier.
	 *
	 * @param mod the SBO term of the form SBO:[0-9]+
	 * @return the textual equivalent
	 */
	public static String resolveModifier (String mod)
	{
		try
		{
			// TODO: resolve the stuff dynamically from db...
			switch (Integer.parseInt (mod.substring (4)))
			{
				case 459: // stimulator
				case 13: // catalyst
				case 460: // enzymatic catalyst (is a)
				case 461: // essential activator (is a)
				case 535: // binding activator (is a)
				case 534: // catalytic activator (is a)
				case 533: // specific activator (is a)
				case 462: // non-essential activator (is a)
				case 21: // potentiator (is a)
					return MOD_STIMULATOR;
				case 20: // inhibitor (is a)
				case 206: // competitive inhibitor (is a)
				case 207: // non-competitive inhibitor (is a)
				case 537: // complete inhibitor (is a)
				case 536: // partial inhibitor (is a)
				case 597: // silencer
					return MOD_INHIBITOR;
			}
		}
		catch (NumberFormatException e)
		{
			
		}
		return MOD_UNKNOWN;
	}
	
	/**
	 * Resolve this modifier.
	 *
	 * @return the textual equivalent
	 */
	public String resolveModifier ()
	{
		if (SBOTerm == null || !SBOTerm.startsWith ("SBO:"))
			return MOD_UNKNOWN;
		return resolveModifier (SBOTerm);
	}
	
	
	/**
	 * Check if modifiers are equal (in terms of chemical reaction networks).
	 * They are equal if one of the following is true:
	 * 
	 * <ul>
	 * <li>
	 * both terms are null
	 * </li>
	 * <li>
	 * both terms encode for an inhibitor (no matter which kind)
	 * </li>
	 * <li>
	 * both terms encode for a stimulator (no matter which kind)
	 * </li>
	 * </ul>
	 *
	 * @param modTermA the mod term a
	 * @param modTermB the mod term b
	 * @return true, if successful
	 */
	public static boolean sameModifier (SBOTerm modTermA, SBOTerm modTermB)
	{
		if (modTermA != null || modTermB != null)
		{
			if (modTermA == null || modTermB == null)
				return false;
			if (!modTermA.resolveModifier ().equals (modTermB.resolveModifier ()))
				return false;
		}
		return true;
	}
}

/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import java.io.File;
import java.net.URL;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.FileRetriever;
import de.unirostock.sems.bives.ds.ModelDocument;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * The abstract class ModelValidator will serve as parent for ML validators. This might also be a good way to parse documents.
 * 
 * @author Martin Scharm
 */
public abstract class ModelValidator
{
	/** The error. */
	protected Exception error;
	
	/**
	 * Validate a document.
	 * 
	 * @param d
	 *          the document
	 * @return true, if document is a valid model
	 */
	public abstract boolean validate (TreeDocument d);
	
	
	/**
	 * Validate a document represented as a file.
	 * 
	 * @param d
	 *          the file storing a document
	 * @return true, if submitted string is a valid model
	 */
	public abstract boolean validate (File d);
	
	
	/**
	 * Validate a document represented as a string.
	 * 
	 * @param d
	 *          the textual representation of a document
	 * @return true, if submitted string is a valid model
	 */
	public abstract boolean validate (String d);
	
	
	/**
	 * Gets the parsed document if it was valid, of null otherwise. Since we need
	 * to create a document anyways, here you can get it for free ;-)
	 * 
	 * @return the document
	 */
	public abstract ModelDocument getDocument ();
	
	
	/**
	 * Gets the error the was thrown in case of an invalid document.
	 * 
	 * @return the error
	 */
	public Exception getError ()
	{
		return error;
	}
	
	
	/**
	 * Validate a document downladable from a web server.
	 * 
	 * @param url
	 *          the URL to the webserver
	 * @return true, if submitted string is a valid model
	 */
	public boolean validate (URL url)
	{
		try
		{
			File tmp = File.createTempFile ("Bives", "download");
			tmp.deleteOnExit ();
			FileRetriever.getFile (url.toURI (), tmp);
			return validate (new TreeDocument (XmlTools.readDocument (tmp), url.toURI ()));
		}
		catch (Exception e)
		{
			error = e;
			LOGGER.error (e, "error retrieving file from ", url);
			return false;
		}
	}
}

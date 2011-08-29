package gov.nasa.pds.imaging.generation.context;

import gov.nasa.pds.imaging.generation.TemplateException;

/**
 * Interface for the PDS Context to be used for extracting values
 * for the Velocity Templates.
 * 
 * @author jpadams
 *
 */
public interface PDSContext {
	public String getContext();
	public Object get(String key) throws TemplateException;
	public String getUnits(String key);
	public void setInputPath(String str);
	public void setConfigPath(String path);
	public void setMappings() throws Exception;
}

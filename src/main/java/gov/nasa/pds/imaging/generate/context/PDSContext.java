package gov.nasa.pds.imaging.generate.context;

import gov.nasa.pds.imaging.generate.TemplateException;

/**
 * Interface for the PDS Context to be used for extracting values for the
 * Velocity Templates.
 * 
 * @author jpadams
 * 
 */
public interface PDSContext {
    public Object get(String key) throws TemplateException;

    public String getContext();

    public String getUnits(String key);

    public void setConfigPath(String path);

    public void setInputPath(String str);

    public void setMappings() throws Exception;
}

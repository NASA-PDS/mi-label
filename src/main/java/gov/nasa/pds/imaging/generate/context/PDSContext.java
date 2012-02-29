package gov.nasa.pds.imaging.generate.context;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;

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

    //public void setConfigPath(String path);

    //public void setInputPath(String str);
    
    public void setParameters(PDSObject pdsObject, String confPath);

    public void setMappings() throws Exception;
}

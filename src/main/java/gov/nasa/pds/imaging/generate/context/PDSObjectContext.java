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
public interface PDSObjectContext extends PDSContext {
    public Object get(String key) throws TemplateException;

    public String getUnits(String key);
    
    public void setParameters(PDSObject pdsObject);

    public void setMappings() throws Exception;
}
